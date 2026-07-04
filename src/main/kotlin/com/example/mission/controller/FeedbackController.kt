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
        @AuthenticationPrincipal userId: Long,
        @RequestBody request: FeedbackRequest
    ) {
        feedbackService.createFeedback(userId, request)
    }

    @GetMapping
    fun getFeedbacks(
        @AuthenticationPrincipal userId: Long,
        @RequestParam(required = false) isPositive: Boolean?,
        @org.springframework.data.web.PageableDefault(sort = ["createdAt"], direction = org.springframework.data.domain.Sort.Direction.DESC) pageable: org.springframework.data.domain.Pageable
    ): org.springframework.data.domain.Page<com.example.mission.dto.FeedbackResponseDto> {
        return feedbackService.getFeedbacks(userId, isPositive, pageable)
    }

    @PatchMapping("/{feedbackId}/resolve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun resolveFeedback(
        @AuthenticationPrincipal userId: Long,
        @PathVariable feedbackId: Long
    ) {
        feedbackService.resolveFeedback(userId, feedbackId)
    }
}
