package io.github.kieckegard.outbox.repository.jpa

import io.github.kieckegard.outbox.DomainEvent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface DomainEventRepository : JpaRepository<DomainEvent, String> {

    @Query("SELECT d FROM DomainEvent d WHERE d.handledAt IS NULL")
    fun findAllByHandledAtIsNull(limit: Int): List<DomainEvent>
}