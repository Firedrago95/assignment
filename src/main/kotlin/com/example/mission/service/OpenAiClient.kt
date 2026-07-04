package com.example.mission.service

import com.example.mission.dto.AiMessageDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux

@Component
class OpenAiClient(
    private val webClientBuilder: WebClient.Builder,
    @Value("\${openai.api-key:dummy-api-key}") private val apiKey: String,
    @Value("\${openai.url:https://api.openai.com/v1/chat/completions}") private val openAiUrl: String
) : AiClient {

    override fun askStreaming(messages: List<AiMessageDto>, model: String): Flux<String> {
        val requestBody = mapOf(
            "model" to model,
            "messages" to messages,
            "stream" to true
        )

        return webClientBuilder.build()
            .post()
            .uri(openAiUrl)
            .header("Authorization", "Bearer $apiKey")
            .bodyValue(requestBody)
            .retrieve()
            .bodyToFlux(String::class.java)
            .timeout(java.time.Duration.ofSeconds(60))
            // 실제로는 JSON에서 delta.content 부분만 추출하는 map 로직이 추가됩니다.
    }
}
