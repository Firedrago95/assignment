package com.example.mission.dto

import com.example.mission.domain.chat.ChatStatus
import java.time.ZonedDateTime

data class ThreadResponseDto(
    val threadId: Long,
    val createdAt: ZonedDateTime,
    val chats: List<ChatResponseDto>
)

data class ChatResponseDto(
    val chatId: Long,
    val role: String,
    val content: String,
    val status: ChatStatus?,
    val createdAt: ZonedDateTime
)

data class ChatHistoryCursorResponse(
    val threads: List<ThreadResponseDto>,
    val nextCursor: Long?,
    val hasNext: Boolean
)
