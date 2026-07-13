package com.example.mission.domain.chat

import org.springframework.data.jpa.repository.JpaRepository

import org.springframework.data.domain.Pageable

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
}
