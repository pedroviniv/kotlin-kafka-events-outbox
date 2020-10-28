package io.github.kieckegard.outbox

import io.github.kieckegard.outbox.repository.jpa.DomainEventRepository
import org.springframework.transaction.annotation.Transactional

abstract class PersistentDomainEventHandler(private val eventRepository: DomainEventRepository)
    : DomainEventHandler {

    abstract fun doHandle(event: DomainEvent);

    @Transactional
    override fun handle(event: DomainEvent) {
        try {
            this.doHandle(event)
            event.handle()
            this.eventRepository.save(event)
        } catch (ex: Throwable) {
            /**
             * If some error occurs while "do handling", the event will not be
             * updated to "handled".
             */
            println("Some error occurred while handling the event ${event.id}. err: ${ex.message}")
            ex.printStackTrace()
        }
    }
}