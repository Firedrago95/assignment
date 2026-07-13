package com.example.mission.domain.chat

import com.example.mission.domain.user.User
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.ZonedDateTime

@Entity
@Table(
    name = "threads",
    indexes = [
        Index(name = "idx_user_id_id", columnList = "user_id, id DESC"),
        Index(name = "idx_user_id_id_asc", columnList = "user_id, id ASC"),
        Index(name = "idx_thread_created_at", columnList = "created_at DESC")
    ]
)
@EntityListeners(AuditingEntityListener::class)
class Thread(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    var createdAt: ZonedDateTime? = null

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    var updatedAt: ZonedDateTime? = null
}
