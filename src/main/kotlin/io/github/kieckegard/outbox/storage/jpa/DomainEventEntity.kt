package io.github.kieckegard.outbox.storage.jpa

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "t_domain_events")
class DomainEventEntity (
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        val id: Long?,
        @Column(name = "emitted_at")
        var emittedAt: LocalDateTime?,
        @Column(name = "handled_at")
        var handledAt: LocalDateTime?,
        val payload: String,
        val aggregateId : String,
        val type: String
)