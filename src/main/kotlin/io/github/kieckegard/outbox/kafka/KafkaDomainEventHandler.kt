package io.github.kieckegard.outbox.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.kieckegard.outbox.*
import io.github.kieckegard.outbox.storage.DomainEventStorage
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaDomainEventHandler(
        eventStorage: DomainEventStorage,
        val kafkaTemplate: KafkaTemplate<String, String>,
        val kafkaTopicInfoCalculator: KafkaTopicInfoCalculator,
        val objectMapper: ObjectMapper) : PersistentDomainEventHandler(eventStorage) {

    override fun doHandle(event: DomainEvent) {
        val topicInfo = this.kafkaTopicInfoCalculator.calculate(event)

        val id = event.id.toString()

        val message = KafkaMessage(id, event.aggregateId, event.payload, event.type)
        val jsonMessage = this.objectMapper.writeValueAsString(message)

        this.kafkaTemplate.send(topicInfo.topicName, event.aggregateId, jsonMessage)
    }

}