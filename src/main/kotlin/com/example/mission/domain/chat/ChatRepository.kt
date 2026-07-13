package com.example.mission.domain.chat

import org.springframework.data.jpa.repository.JpaRepository

interface ChatRepository : JpaRepository<Chat, Long> {
    fun findTop20ByThreadIdAndIdNotOrderByCreatedAtDesc(threadId: Long, excludeChatId: Long): List<Chat>
    fun findByThreadIdInOrderByCreatedAtAsc(threadIds: List<Long>): List<Chat>
    fun deleteAllByThreadId(threadId: Long)

    @org.springframework.data.jpa.repository.Query("SELECT c.thread.id as threadId, COUNT(c) as chatCount FROM Chat c WHERE c.thread.id IN :threadIds GROUP BY c.thread.id")
    fun countChatsByThreadIds(@org.springframework.data.repository.query.Param("threadIds") threadIds: List<Long>): List<ThreadChatCount>
}

interface ThreadChatCount {
    val threadId: Long
    val chatCount: Long
}
