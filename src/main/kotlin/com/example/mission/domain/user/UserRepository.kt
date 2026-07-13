package com.example.mission.domain.user

import org.springframework.data.jpa.repository.JpaRepository
import java.time.ZonedDateTime

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
    fun countByCreatedAtAfter(time: ZonedDateTime): Long
}
