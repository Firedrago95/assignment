package com.example.mission.service

import com.example.mission.domain.chat.AiChat
import com.example.mission.domain.chat.ChatRepository
import com.example.mission.domain.chat.Thread
import com.example.mission.domain.chat.ThreadRepository
import com.example.mission.domain.user.Role
import com.example.mission.domain.user.UserRepository
import com.example.mission.dto.ChatHistoryCursorResponse
import com.example.mission.dto.ChatResponseDto
import com.example.mission.dto.ThreadResponseDto
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ChatQueryService(
    private val threadRepository: ThreadRepository,
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository
) {
    fun getChatHistoryPaginated(userId: Long, cursor: Long?, limit: Int, sortDirection: String): ChatHistoryCursorResponse {
        val isAdmin = userRepository.findById(userId).orElseThrow().role == Role.ADMIN
        val threads = fetchThreads(isAdmin, userId, cursor, PageRequest.of(0, limit + 1), sortDirection.uppercase())

        val hasNext = threads.size > limit
        val resultThreads = if (hasNext) threads.dropLast(1) else threads
        if (resultThreads.isEmpty()) return ChatHistoryCursorResponse(emptyList(), null, false)

        return ChatHistoryCursorResponse(buildThreadDtos(resultThreads), resultThreads.last().id, hasNext)
    }

    private fun fetchThreads(isAdmin: Boolean, userId: Long, cursor: Long?, pageable: PageRequest, sortDirection: String): List<Thread> {
        val isAsc = sortDirection == "ASC"
        return when {
            isAdmin && cursor != null -> if (isAsc) threadRepository.findByIdGreaterThanOrderByIdAsc(cursor, pageable) else threadRepository.findByIdLessThanOrderByIdDesc(cursor, pageable)
            isAdmin -> if (isAsc) threadRepository.findAllByOrderByIdAsc(pageable) else threadRepository.findAllByOrderByIdDesc(pageable)
            cursor != null -> if (isAsc) threadRepository.findByUserIdAndIdGreaterThanOrderByIdAsc(userId, cursor, pageable) else threadRepository.findByUserIdAndIdLessThanOrderByIdDesc(userId, cursor, pageable)
            else -> if (isAsc) threadRepository.findByUserIdOrderByIdAsc(userId, pageable) else threadRepository.findByUserIdOrderByIdDesc(userId, pageable)
        }
    }

    private fun buildThreadDtos(threads: List<Thread>): List<ThreadResponseDto> {
        val chatMap = chatRepository.findByThreadIdInOrderByCreatedAtAsc(threads.map { it.id }).groupBy { it.thread.id }
        return threads.map { thread ->
            ThreadResponseDto(
                threadId = thread.id,
                createdAt = thread.createdAt!!,
                chats = (chatMap[thread.id] ?: emptyList()).map { chat ->
                    ChatResponseDto(
                        chatId = chat.id, role = if (chat is AiChat) "assistant" else "user",
                        content = chat.content, status = if (chat is AiChat) chat.status else null,
                        createdAt = chat.createdAt!!
                    )
                }
            )
        }
    }
}
