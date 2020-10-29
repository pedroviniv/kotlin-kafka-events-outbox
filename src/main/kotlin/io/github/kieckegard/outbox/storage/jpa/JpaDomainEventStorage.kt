package io.github.kieckegard.outbox.storage.jpa

import io.github.kieckegard.outbox.DomainEvent
import io.github.kieckegard.outbox.storage.DomainEventStorage
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import kotlin.streams.toList

@Repository
class JpaDomainEventStorage(
        val jpaRepository: JpaDomainEventRepository,
        @PersistenceContext
        var entityManager: EntityManager
) : DomainEventStorage {

    @Transactional
    override fun persist(domainEvent: DomainEvent) {

        val entity = DomainEventEntity(null, domainEvent.emittedAt, domainEvent.handledAt, domainEvent.payload, domainEvent.aggregateId, domainEvent.type)
        this.jpaRepository.save(entity)
    }

    @Transactional
    override fun update(domainEvent: DomainEvent) {
        val entity = DomainEventEntity(domainEvent.id, domainEvent.emittedAt, domainEvent.handledAt, domainEvent.payload, domainEvent.aggregateId, domainEvent.type)
        this.jpaRepository.save(entity)
    }

    fun toDomain(entity: DomainEventEntity): DomainEvent {
        return DomainEvent(entity.id, entity.emittedAt, entity.handledAt, entity.payload, entity.aggregateId, entity.type)
    }

    override fun getUnhandledEvents(limit: Int): List<DomainEvent> {

        val entities = this.entityManager.createQuery("SELECT e FROM DomainEventEntity e WHERE e.handledAt IS NULL", DomainEventEntity::class.java)
                .setMaxResults(limit)
                .resultList

        return entities.stream()
                .map(this::toDomain)
                .toList()

    }
}