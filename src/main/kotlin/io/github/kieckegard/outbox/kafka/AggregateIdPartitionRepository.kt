package io.github.kieckegard.outbox.kafka

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AggregateIdPartitionRepository : JpaRepository<AggregateIdPartition, AggregateIdPartitionId> {
}