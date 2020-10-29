package io.github.kieckegard.outbox.storage

import io.github.kieckegard.outbox.DomainEvent

interface DomainEventStorage {

    fun persist(domainEvent: DomainEvent)
    fun update(domainEvent: DomainEvent)
    fun getUnhandledEvents(limit: Int): List<DomainEvent>
}