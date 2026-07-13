package com.example.mission.domain.user

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.ZonedDateTime

@Entity
@Table(
    name = "login_histories",
    indexes = [
        Index(name = "idx_login_histories_login_at", columnList = "login_at")
    ]
)
@EntityListeners(AuditingEntityListener::class)
class LoginHistory(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @CreatedDate
    @Column(name = "login_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    var loginAt: ZonedDateTime? = null
}
