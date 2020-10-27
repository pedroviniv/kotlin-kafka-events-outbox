package io.github.kieckegard.outbox

import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class AggregateIdPartitionId(
        @Column(name = "aggregate")
        val aggregate: String,
        @Column(name = "aggregate_id")
        val aggregateId: String
)