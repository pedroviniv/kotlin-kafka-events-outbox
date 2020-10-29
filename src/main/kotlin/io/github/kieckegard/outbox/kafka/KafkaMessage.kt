package io.github.kieckegard.outbox.kafka

class KafkaMessage(
        val id: String,
        val aggregateId: String,
        val payload: String,
        val type: String
)