package com.example.mission.dto

data class FeedbackRequest(
    val chatId: Long,
    val isPositive: Boolean
)
