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

@RestController
@RequestMapping("/api/v1/chats")
class ChatController(
    private val chatAppService: ChatAppService
) {

    @PostMapping(produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun createChat(
        @AuthenticationPrincipal userId: String,
        @RequestBody request: ChatRequest
    ): Flux<String> {
        return chatAppService.createChat(userId.toLong(), request)
    }
}
