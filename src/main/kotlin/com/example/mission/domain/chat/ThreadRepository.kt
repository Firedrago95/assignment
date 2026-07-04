package com.example.mission.domain.chat

import org.springframework.data.jpa.repository.JpaRepository

interface ThreadRepository : JpaRepository<Thread, Long> {
    fun findTopByUserIdOrderByUpdatedAtDesc(userId: Long): Thread?
}
