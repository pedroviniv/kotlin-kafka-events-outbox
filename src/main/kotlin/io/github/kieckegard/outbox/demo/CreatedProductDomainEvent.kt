package io.github.kieckegard.outbox.demo

import io.github.kieckegard.outbox.DomainEvent
import io.github.kieckegard.outbox.kafka.KafkaTopicInfo

@KafkaTopicInfo(topicName = "topic-name-\$CLASS_NAME")
class CreatedProductDomainEvent(payload: String, aggregateId: String)
    : DomainEvent(null, null, null, payload, aggregateId, "CreatedProductDomainEvent") {
}