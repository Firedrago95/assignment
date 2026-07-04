package com.example.mission.service

import com.example.mission.dto.AiMessageDto
import reactor.core.publisher.Flux

interface AiClient {
    fun askStreaming(messages: List<AiMessageDto>): Flux<String>
}
