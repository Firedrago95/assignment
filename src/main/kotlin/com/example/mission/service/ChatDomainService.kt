package com.example.mission.service

import com.example.mission.domain.chat.*
import com.example.mission.domain.chat.Thread
import com.example.mission.domain.user.UserRepository
import com.example.mission.global.exception.BusinessException
import com.example.mission.global.exception.ErrorCode
import com.example.mission.dto.AiMessageDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.data.redis.core.StringRedisTemplate
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.concurrent.TimeUnit

import com.example.mission.domain.feedback.FeedbackRepository

data class CachedChatDto(
    val threadId: Long,
    val role: String,
    val content: String
)

@Service
@Transactional
class ChatDomainService(
    private val userRepository: UserRepository,
    private val threadRepository: ThreadRepository,
    private val chatRepository: ChatRepository,
    private val feedbackRepository: FeedbackRepository,
    private val redisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper
) {
    fun prepareChats(userId: Long, content: String): Pair<UserChat, AiChat> {
        val user = userRepository.findById(userId)
            .orElseThrow { BusinessException(ErrorCode.USER_NOT_FOUND) }

        val lockKey = "lock:prepare_chat:$userId"
        val isLocked = redisTemplate.opsForValue().setIfAbsent(lockKey, "LOCKED", 3, TimeUnit.SECONDS)

        if (isLocked == false) {
            throw BusinessException(ErrorCode.TOO_MANY_REQUESTS)
        }

        try {
            val redisKey = "active_chat:user:$userId"
            val firstChatJson = redisTemplate.opsForList().index(redisKey, 0)
            
            var thread: Thread? = null
            if (firstChatJson != null) {
                val cachedChat = objectMapper.readValue(firstChatJson, CachedChatDto::class.java)
                thread = threadRepository.getReferenceById(cachedChat.threadId)
            } else {
                thread = threadRepository.save(Thread(user = user))
            }

            val userChat = chatRepository.save(UserChat(thread = thread, content = content))
            val aiChat = chatRepository.save(AiChat(thread = thread, content = "", status = ChatStatus.PENDING, parentChat = userChat))

            val cachedUserChat = CachedChatDto(threadId = thread.id, role = "user", content = userChat.content)
            redisTemplate.opsForList().rightPush(redisKey, objectMapper.writeValueAsString(cachedUserChat))
            redisTemplate.expire(redisKey, 30, TimeUnit.MINUTES)

            return Pair(userChat, aiChat)
        } finally {
            redisTemplate.delete(lockKey)
        }
    }

    fun updateAiChatSuccess(aiChatId: Long, fullContent: String) {
        val chat = chatRepository.findById(aiChatId).orElseThrow() as AiChat
        chat.content = fullContent
        chat.status = ChatStatus.COMPLETED

        val redisKey = "active_chat:user:${chat.thread.user.id}"
        val cachedAiChat = CachedChatDto(threadId = chat.thread.id, role = "assistant", content = fullContent)
        redisTemplate.opsForList().rightPush(redisKey, objectMapper.writeValueAsString(cachedAiChat))
        redisTemplate.expire(redisKey, 30, TimeUnit.MINUTES)
    }

    fun updateAiChatFailed(aiChatId: Long) {
        val chat = chatRepository.findById(aiChatId).orElseThrow() as AiChat
        chat.status = ChatStatus.FAILED
    }

    fun updateAiChatPartial(aiChatId: Long, partialContent: String) {
        val chat = chatRepository.findById(aiChatId).orElseThrow() as AiChat
        chat.content = partialContent
        chat.status = ChatStatus.PARTIAL
    }

    @Transactional(readOnly = true)
    fun getChatHistory(userId: Long): List<AiMessageDto> {
        val redisKey = "active_chat:user:$userId"
        val cachedJsons = redisTemplate.opsForList().range(redisKey, 0, -1) ?: emptyList()
        
        return cachedJsons.map { json ->
            val cached = objectMapper.readValue(json, CachedChatDto::class.java)
            AiMessageDto(role = cached.role, content = cached.content)
        }
    }

    @Transactional
    fun deleteThread(userId: Long, threadId: Long) {
        val thread = threadRepository.findById(threadId)
            .orElseThrow { BusinessException(ErrorCode.THREAD_NOT_FOUND) }
        if (thread.user.id != userId) {
            throw BusinessException(ErrorCode.THREAD_ACCESS_DENIED)
        }
        feedbackRepository.deleteAllByThreadId(thread.id)
        chatRepository.deleteAllByThreadId(thread.id)
        threadRepository.delete(thread)
    }
}
