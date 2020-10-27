package io.github.kieckegard.outbox

interface DomainEventHandler {

    fun handle(event: DomainEvent)
}