package com.example.mission.service

import com.example.mission.domain.chat.*
import com.example.mission.domain.chat.Thread
import com.example.mission.domain.user.UserRepository
import com.example.mission.global.exception.BusinessException
import com.example.mission.global.exception.ErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Service
@Transactional
class ChatDomainService(
    private val userRepository: UserRepository,
    private val threadRepository: ThreadRepository,
    private val chatRepository: ChatRepository
) {
    fun prepareChats(userId: Long, content: String): Pair<UserChat, AiChat> {
        val user = userRepository.findById(userId)
            .orElseThrow { BusinessException(ErrorCode.USER_NOT_FOUND) }

        val now = ZonedDateTime.now()
        
        var thread = threadRepository.findTopByUserIdOrderByUpdatedAtDesc(userId)
        if (thread == null || thread.isExpired(now)) {
            thread = threadRepository.save(Thread(user = user))
        } else {
            thread.updatedAt = now
        }

        val userChat = chatRepository.save(UserChat(thread = thread, content = content))
        val aiChat = chatRepository.save(AiChat(thread = thread, content = "", status = ChatStatus.PENDING, parentChat = userChat))

        return Pair(userChat, aiChat)
    }

    fun updateAiChatSuccess(aiChatId: Long, fullContent: String) {
        val chat = chatRepository.findById(aiChatId).orElseThrow() as AiChat
        chat.content = fullContent
        chat.status = ChatStatus.COMPLETED
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
    fun getChatHistory(threadId: Long, excludeChatId: Long): List<Chat> {
        return chatRepository.findTop20ByThreadIdAndIdNotOrderByCreatedAtDesc(threadId, excludeChatId).reversed()
    }
}
