package com.example.mission.domain.user

import org.springframework.data.jpa.repository.JpaRepository
import java.time.ZonedDateTime

interface LoginHistoryRepository : JpaRepository<LoginHistory, Long> {
    fun countByLoginAtAfter(time: ZonedDateTime): Long
}
