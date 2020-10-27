package io.github.kieckegard.outbox

import org.springframework.stereotype.Component

@Component
class PersistentDomainEventPublisher(val domainEventRepository: DomainEventRepository) : DomainEventPublisher {

    override fun publish(event: DomainEvent) {
        this.domainEventRepository.save(event);
    }
}