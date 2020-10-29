package io.github.kieckegard.outbox.storage.jpa

import io.github.kieckegard.outbox.DomainEvent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface JpaDomainEventRepository : JpaRepository<DomainEventEntity, Long>