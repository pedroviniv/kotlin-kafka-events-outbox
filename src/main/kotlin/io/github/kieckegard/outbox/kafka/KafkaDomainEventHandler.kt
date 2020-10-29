package io.github.kieckegard.outbox.kafka

import io.github.kieckegard.outbox.*
import io.github.kieckegard.outbox.storage.DomainEventStorage
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaDomainEventHandler(
        eventStorage: DomainEventStorage,
        val kafkaTemplate: KafkaTemplate<String, String>,
        val kafkaTopicInfoCalculator: KafkaTopicInfoCalculator) : PersistentDomainEventHandler(eventStorage) {

    override fun doHandle(event: DomainEvent) {
        val topicInfo = this.kafkaTopicInfoCalculator.calculate(event)
        this.kafkaTemplate.send(topicInfo.topicName, event.aggregateId, event.payload)
    }

}