package com.example.mission.dto

import com.example.mission.domain.feedback.FeedbackStatus
import java.time.ZonedDateTime

data class FeedbackRequest(
    val chatId: Long,
    val isPositive: Boolean
)

data class FeedbackResponseDto(
    val id: Long,
    val userId: Long,
    val chatId: Long,
    val isPositive: Boolean,
    val status: FeedbackStatus,
    val createdAt: ZonedDateTime
)
