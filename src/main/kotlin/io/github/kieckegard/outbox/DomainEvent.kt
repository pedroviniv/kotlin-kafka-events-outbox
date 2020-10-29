package io.github.kieckegard.outbox

import java.time.LocalDateTime

/**
 * primary constructor that which type
 * is retrieved automatically in runtime
 */
open class DomainEvent (
        var id: Long?,
        val emittedAt: LocalDateTime? = LocalDateTime.now(),
        var handledAt: LocalDateTime?,
        val payload: String,
        val aggregateId : String
) {

    /**
     * secondary constructor that takes a type
     *
     * it is used to create a full customizable DomainEvent
     */
    constructor(id: Long?,
                emittedAt: LocalDateTime? = LocalDateTime.now(),
                handledAt: LocalDateTime?,
                payload: String,
                aggregateId : String,
                type: String) : this(id, emittedAt, handledAt, payload, aggregateId){
        this.type = type
    }

    var type: String = this::class.java.name;

    fun handle() {
        this.handledAt = LocalDateTime.now();
    }
}