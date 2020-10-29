package io.github.kieckegard.outbox.kafka

class KafkaDomainEventTopic(
        val topicName: String,
        val partition: Int
)