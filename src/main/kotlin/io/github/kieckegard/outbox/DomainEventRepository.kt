package io.github.kieckegard.outbox

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface DomainEventRepository : JpaRepository<DomainEvent, String> {

    @Query("SELECT d FROM DomainEvent d LIMIT :limit")
    fun findAllByHandledAtIsNull(limit: Int): List<DomainEvent>
}