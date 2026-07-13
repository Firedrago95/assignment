package com.example.mission.domain.chat

import io.lettuce.core.dynamic.annotation.Param
import org.springframework.data.jpa.repository.JpaRepository

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import java.time.ZonedDateTime

interface ThreadRepository : JpaRepository<Thread, Long> {
    // Member: cursor desc
    fun findByUserIdAndIdLessThanOrderByIdDesc(userId: Long, id: Long, pageable: Pageable): List<Thread>
    fun findByUserIdOrderByIdDesc(userId: Long, pageable: Pageable): List<Thread>

    // Admin: cursor desc
    fun findByIdLessThanOrderByIdDesc(id: Long, pageable: Pageable): List<Thread>
    fun findAllByOrderByIdDesc(pageable: Pageable): List<Thread>

    // Member: cursor asc
    fun findByUserIdAndIdGreaterThanOrderByIdAsc(userId: Long, id: Long, pageable: Pageable): List<Thread>
    fun findByUserIdOrderByIdAsc(userId: Long, pageable: Pageable): List<Thread>

    // Admin: cursor asc
    fun findByIdGreaterThanOrderByIdAsc(id: Long, pageable: Pageable): List<Thread>
    fun findAllByOrderByIdAsc(pageable: Pageable): List<Thread>

    fun countByCreatedAtAfter(time: java.time.ZonedDateTime): Long

    @Query("SELECT t FROM Thread t JOIN FETCH t.user WHERE t.createdAt >= :time ORDER BY t.id DESC")
    fun findAllWithUserByCreatedAtAfter(@Param("time") time: ZonedDateTime): List<Thread>
}
