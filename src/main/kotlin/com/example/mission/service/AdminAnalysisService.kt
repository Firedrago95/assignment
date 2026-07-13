package com.example.mission.service

import com.example.mission.domain.chat.ChatRepository
import com.example.mission.domain.chat.ThreadRepository
import com.example.mission.domain.user.LoginHistoryRepository
import com.example.mission.domain.user.UserRepository
import com.example.mission.dto.ActivitySummaryResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Service
class AdminAnalysisService(
    private val userRepository: UserRepository,
    private val loginHistoryRepository: LoginHistoryRepository,
    private val threadRepository: ThreadRepository,
    private val chatRepository: ChatRepository
) {
    @Transactional(readOnly = true)
    fun getActivitySummary(): ActivitySummaryResponse {
        val yesterday = ZonedDateTime.now().minusHours(24)
        
        val signupCount = userRepository.countByCreatedAtAfter(yesterday)
        val loginCount = loginHistoryRepository.countByLoginAtAfter(yesterday)
        val threadCount = threadRepository.countByCreatedAtAfter(yesterday)

        return ActivitySummaryResponse(
            signupCount = signupCount,
            loginCount = loginCount,
            threadCount = threadCount
        )
    }

    @Transactional(readOnly = true)
    fun generateReportCsv(): String {
        val yesterday = ZonedDateTime.now().minusHours(24)
        val threads = threadRepository.findAllWithUserByCreatedAtAfter(yesterday)
        
        if (threads.isEmpty()) {
            return "Thread ID,User Email,User Name,Created At,Chat Count\n"
        }

        val threadIds = threads.map { it.id }
        val chatCounts = chatRepository.countChatsByThreadIds(threadIds)
            .associate { it.threadId to it.chatCount }

        val sb = StringBuilder()
        sb.append("Thread ID,User Email,User Name,Created At,Chat Count\n")

        for (thread in threads) {
            val count = chatCounts[thread.id] ?: 0L
            sb.append("${thread.id},${thread.user.email},${thread.user.name},${thread.createdAt},${count}\n")
        }

        return sb.toString()
    }
}
