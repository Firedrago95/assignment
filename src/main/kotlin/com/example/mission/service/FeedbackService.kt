package com.example.mission.service

import com.example.mission.domain.chat.AiChat
import com.example.mission.domain.chat.ChatRepository
import com.example.mission.domain.chat.ChatStatus
import com.example.mission.domain.feedback.Feedback
import com.example.mission.domain.feedback.FeedbackRepository
import com.example.mission.domain.user.Role
import com.example.mission.domain.user.UserRepository
import com.example.mission.dto.FeedbackRequest
import com.example.mission.global.exception.BusinessException
import com.example.mission.global.exception.ErrorCode
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import com.example.mission.domain.chat.Chat
import com.example.mission.domain.user.User

@Service
@Transactional
class FeedbackService(
    private val feedbackRepository: FeedbackRepository,
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository
) {
    fun createFeedback(userId: Long, request: FeedbackRequest) {
        val user = userRepository.findById(userId).orElseThrow { BusinessException(ErrorCode.USER_NOT_FOUND) }
        val chat = chatRepository.findById(request.chatId).orElseThrow { BusinessException(ErrorCode.CHAT_NOT_FOUND) }

        validateFeedbackEligibility(user, chat, userId)

        try {
            feedbackRepository.saveAndFlush(Feedback(user = user, aiChat = chat as AiChat, isPositive = request.isPositive))
        } catch (e: DataIntegrityViolationException) {
            throw BusinessException(ErrorCode.FEEDBACK_ALREADY_EXISTS)
        }
    }

    private fun validateFeedbackEligibility(user: User, chat: Chat, userId: Long) {
        if (user.role != Role.ADMIN && chat.thread.user.id != userId) {
            throw BusinessException(ErrorCode.FEEDBACK_ACCESS_DENIED)
        }

        if (chat !is AiChat) {
            throw BusinessException(ErrorCode.INVALID_CHAT_STATUS)
        }

        if (chat.status == ChatStatus.PENDING || chat.status == ChatStatus.FAILED) {
            throw BusinessException(ErrorCode.INVALID_CHAT_STATUS)
        }
    }

    @Transactional(readOnly = true)
    fun getFeedbacks(userId: Long, isPositive: Boolean?, pageable: org.springframework.data.domain.Pageable): org.springframework.data.domain.Page<com.example.mission.dto.FeedbackResponseDto> {
        val user = userRepository.findById(userId).orElseThrow { BusinessException(ErrorCode.USER_NOT_FOUND) }
        val feedbacks = if (user.role == Role.ADMIN) {
            if (isPositive != null) feedbackRepository.findByIsPositive(isPositive, pageable)
            else feedbackRepository.findAll(pageable)
        } else {
            if (isPositive != null) feedbackRepository.findByUserIdAndIsPositive(userId, isPositive, pageable)
            else feedbackRepository.findByUserId(userId, pageable)
        }
        return feedbacks.map { 
            com.example.mission.dto.FeedbackResponseDto(it.id, it.user.id, it.aiChat.id, it.isPositive, it.status, it.createdAt!!)
        }
    }

    fun resolveFeedback(userId: Long, feedbackId: Long) {
        val user = userRepository.findById(userId).orElseThrow { BusinessException(ErrorCode.USER_NOT_FOUND) }
        if (user.role != Role.ADMIN) {
            throw BusinessException(ErrorCode.FEEDBACK_ACCESS_DENIED)
        }
        val feedback = feedbackRepository.findById(feedbackId).orElseThrow { BusinessException(ErrorCode.FEEDBACK_NOT_FOUND) }
        feedback.status = com.example.mission.domain.feedback.FeedbackStatus.RESOLVED
    }
}
