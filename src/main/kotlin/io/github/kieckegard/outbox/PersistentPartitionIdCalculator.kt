package io.github.kieckegard.outbox

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.random.Random

@Component
class PersistentPartitionIdCalculator(
        val aggregateIdPartitionRepository: AggregateIdPartitionRepository) : PartitionIdCalculator {

    @Transactional
    override fun calculate(event: DomainEvent, partitionsNumber: Int): Int {
        // inclusive | exclusive
        val partitionNumber = Random.nextInt(1, partitionsNumber + 1)
        val id = AggregateIdPartitionId(event.type, event.aggregateId)

        val result = this.aggregateIdPartitionRepository.findById(id)
        if (result.isEmpty) {

            val aggregateIdPartition = AggregateIdPartition(id, partitionNumber)
            this.aggregateIdPartitionRepository.save(aggregateIdPartition)
            return partitionNumber
        }

        val existent = result.get()
        return existent.partition
    }
}