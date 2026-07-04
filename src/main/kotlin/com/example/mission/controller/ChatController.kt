package com.example.mission.controller

import com.example.mission.dto.ChatRequest
import com.example.mission.service.ChatAppService
import org.springframework.http.MediaType
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.http.HttpStatus
import com.example.mission.dto.ChatHistoryCursorResponse
import com.example.mission.service.ChatQueryService
import com.example.mission.service.ChatDomainService

@RestController
@RequestMapping("/api/v1/chats")
class ChatController(
    private val chatAppService: ChatAppService,
    private val chatQueryService: ChatQueryService,
    private val chatDomainService: ChatDomainService
) {

    @GetMapping
    fun getChatHistory(
        @AuthenticationPrincipal userId: String,
        @RequestParam(required = false) cursor: Long?,
        @RequestParam(defaultValue = "10") limit: Int
    ): ChatHistoryCursorResponse {
        return chatQueryService.getChatHistoryPaginated(userId.toLong(), cursor, limit)
    }

    @PostMapping(produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun createChat(
        @AuthenticationPrincipal userId: String,
        @RequestBody request: ChatRequest
    ): Flux<String> {
        return chatAppService.createChat(userId.toLong(), request)
    }

    @DeleteMapping("/threads/{threadId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteThread(
        @AuthenticationPrincipal userId: String,
        @PathVariable threadId: Long
    ) {
        chatDomainService.deleteThread(userId.toLong(), threadId)
    }
}
