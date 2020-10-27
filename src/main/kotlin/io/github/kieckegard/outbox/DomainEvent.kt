package io.github.kieckegard.outbox

import java.time.LocalDateTime
import javax.persistence.*

open class DomainEvent (
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
) {

    @PrePersist
    fun prePersist() {
        this.emittedAt = LocalDateTime.now();
    }

    fun handle() {
        this.handledAt = LocalDateTime.now();
    }
}