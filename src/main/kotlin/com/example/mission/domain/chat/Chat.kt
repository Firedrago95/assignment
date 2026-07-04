package com.example.mission.domain.chat

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.ZonedDateTime

@Entity
@Table(
    name = "chats",
    indexes = [
        Index(name = "idx_thread_created", columnList = "thread_id, created_at ASC")
    ]
)
@EntityListeners(AuditingEntityListener::class)
class Chat(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thread_id", nullable = false)
    val thread: Thread,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: ChatRole,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ChatStatus = ChatStatus.COMPLETED,

    @Column(nullable = false, columnDefinition = "TEXT")
    var content: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    var createdAt: ZonedDateTime? = null
}
