package io.github.kieckegard.outbox.kafka

import javax.persistence.*

@Entity
@Table(name = "t_aggregate_id_partition")
class AggregateIdPartition(
        @Column(name = "aggregate_id")
        @EmbeddedId
        val id: AggregateIdPartitionId,
        val partition: Int
)