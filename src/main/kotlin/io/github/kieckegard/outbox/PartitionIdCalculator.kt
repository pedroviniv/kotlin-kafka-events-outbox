package io.github.kieckegard.outbox

interface PartitionIdCalculator {

    fun calculate(event: DomainEvent, partitionsNumber: Int): Int
}