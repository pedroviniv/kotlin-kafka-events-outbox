package io.github.kieckegard.outbox

interface DomainEventPublisher {

    fun publish(event: DomainEvent)
}