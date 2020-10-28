package io.github.kieckegard.outbox

import io.github.kieckegard.outbox.repository.jpa.DomainEventRepository
import org.springframework.stereotype.Component

@Component
class PersistentDomainEventPublisher(val domainEventRepository: DomainEventRepository) : DomainEventPublisher {

    override fun publish(event: DomainEvent) {
        this.domainEventRepository.save(event);
    }
}