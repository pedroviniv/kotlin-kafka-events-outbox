package io.github.kieckegard.outbox.demo.events

import io.github.kieckegard.outbox.DomainEvent
import io.github.kieckegard.outbox.kafka.KafkaTopicInfo

@KafkaTopicInfo(topicName = "topic-PRODUCTS")
class UpdatedProductDomainEvent(payload: String, aggregateId: String)
    : DomainEvent(null, null, null, payload, aggregateId) {
}