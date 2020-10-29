package io.github.kieckegard.outbox

import io.github.kieckegard.outbox.storage.DomainEventStorage
import io.github.kieckegard.outbox.storage.jpa.JpaDomainEventRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class DomainEventHandlerWorker(
        val domainEventHandler: DomainEventHandler,
        val eventStorage: DomainEventStorage,
        @Value("\${events.handler.worker.batchSize}") val batchSize: Int
) {

    @Scheduled(fixedDelayString = "\${events.handler.worker.fixedDelayInMs}")
    fun run() {

        /**
         * TODO:
         * 1. communicate with lock service
         * 2. acquire a lock
         * 2. get domain events in process from a in-memory database
         * 3. get the batch size number of events to process (ignoring the events in process retrieved from the lock)
         * 4. update the domain events in process with the batch size ones retrieved from db
         * 5. release lock
         * 6. process
         *
         *
         * THERE ARE GC stop the world pauses, the lock could timeout, etc.
         * BUT there is no problem by sending the event multiple time,
         * because our consumers should be idempotent.
         */

        this.eventStorage.getUnhandledEvents(this.batchSize)
                .forEach {
                    println("handling event ${it.id} of type ${it.type}, with aggregate id ${it.aggregateId}")
                    this.domainEventHandler.handle(it)
                }
    }

}