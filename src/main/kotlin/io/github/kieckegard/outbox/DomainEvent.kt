package io.github.kieckegard.outbox

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "t_domain_events")
open class DomainEvent (
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        open var id: Long?,
        @Column(name = "emitted_at")
        open var emittedAt: LocalDateTime?,
        @Column(name = "handled_at")
        open var handledAt: LocalDateTime?,
        open var payload: String,
        open var aggregateId : String,
        open var type: String
) {

    @PrePersist
    fun prePersist() {
        this.emittedAt = LocalDateTime.now();
    }

    fun handle() {
        this.handledAt = LocalDateTime.now();
    }
}