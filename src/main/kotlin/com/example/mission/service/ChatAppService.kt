package com.example.mission.service

import com.example.mission.domain.chat.UserChat
import com.example.mission.dto.AiMessageDto
import com.example.mission.dto.ChatRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.SignalType
import reactor.core.scheduler.Schedulers

@Service
class ChatAppService(
    private val chatDomainService: ChatDomainService,
    private val aiClient: AiClient
) {
    private val log = LoggerFactory.getLogger(ChatAppService::class.java)

    fun createChat(userId: Long, request: ChatRequest): Flux<String> {
        return Mono.fromCallable {
            val (userChat, aiChat) = chatDomainService.prepareChats(userId, request.content)
            val aiMessages = chatDomainService.getChatHistory(userId)
            Pair(aiChat.id, aiMessages)
        }
        .subscribeOn(Schedulers.boundedElastic())
        .flatMapMany { (aiChatId, aiMessages) ->
            val responseBuilder = StringBuilder()

            aiClient.askStreaming(aiMessages, request.model)
                .doOnNext { chunk -> responseBuilder.append(chunk) }
                .publishOn(Schedulers.boundedElastic())
                .doFinally { signalType ->
                    Mono.fromRunnable<Void> {
                        val finalContent = responseBuilder.toString()
                        if (signalType == SignalType.ON_COMPLETE) {
                            chatDomainService.updateAiChatSuccess(aiChatId, finalContent)
                        } else if (signalType == SignalType.CANCEL) {
                            chatDomainService.updateAiChatPartial(aiChatId, finalContent)
                        } else {
                            chatDomainService.updateAiChatFailed(aiChatId)
                        }
                    }
                    .subscribeOn(Schedulers.boundedElastic())
                    .subscribe({}, { error -> log.error("chat status update failed", error) })
                }
        }
    }
}
