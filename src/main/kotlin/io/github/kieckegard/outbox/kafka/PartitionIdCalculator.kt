package io.github.kieckegard.outbox.kafka

import io.github.kieckegard.outbox.DomainEvent

interface PartitionIdCalculator {

    fun calculate(event: DomainEvent, partitionsNumber: Int): Int
}