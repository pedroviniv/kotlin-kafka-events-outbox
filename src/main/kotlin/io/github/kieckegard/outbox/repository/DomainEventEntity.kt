package io.github.kieckegard.outbox.repository

import java.time.LocalDateTime
import javax.persistence.*

class DomainEventEntity (
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        val id: Long?,
        var emittedAt: LocalDateTime,
        @Column(name = "handled_at")
        var handledAt: LocalDateTime?,
        val payload: String,
        val aggregateId : String,
        val type: String
)