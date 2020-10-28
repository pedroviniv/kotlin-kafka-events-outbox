package io.github.kieckegard.outbox.kafka

import io.github.kieckegard.outbox.*
import io.github.kieckegard.outbox.repository.jpa.DomainEventRepository
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaDomainEventHandler(
        eventRepository: DomainEventRepository,
        val kafkaTemplate: KafkaTemplate<String, String>,
        val kafkaTopicInfoCalculator: KafkaTopicInfoCalculator) : PersistentDomainEventHandler(eventRepository) {

    override fun doHandle(event: DomainEvent) {
        val topicInfo = this.kafkaTopicInfoCalculator.calculate(event)
        this.kafkaTemplate.send(topicInfo.topicName, event.aggregateId, event.payload)
    }

}