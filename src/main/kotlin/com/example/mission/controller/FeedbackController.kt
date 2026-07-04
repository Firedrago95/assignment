package com.example.mission.controller

import com.example.mission.dto.FeedbackRequest
import com.example.mission.service.FeedbackService
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/feedbacks")
class FeedbackController(
    private val feedbackService: FeedbackService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createFeedback(
        @AuthenticationPrincipal userId: String,
        @RequestBody request: FeedbackRequest
    ) {
        feedbackService.createFeedback(userId.toLong(), request)
    }
}
