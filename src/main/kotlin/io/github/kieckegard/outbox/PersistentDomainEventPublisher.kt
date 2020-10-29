package io.github.kieckegard.outbox

import io.github.kieckegard.outbox.storage.DomainEventStorage
import io.github.kieckegard.outbox.storage.jpa.JpaDomainEventRepository
import org.springframework.stereotype.Component

@Component
class PersistentDomainEventPublisher(val domainEventStorage: DomainEventStorage) : DomainEventPublisher {

    override fun publish(event: DomainEvent) {
        this.domainEventStorage.persist(event);
    }
}