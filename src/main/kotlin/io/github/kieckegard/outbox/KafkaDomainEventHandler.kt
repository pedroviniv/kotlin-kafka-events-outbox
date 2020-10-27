package io.github.kieckegard.outbox

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaDomainEventHandler(
        eventRepository: DomainEventRepository,
        val kafkaTemplate: KafkaTemplate<String, String>,
        val partitionIdCalculator: PartitionIdCalculator,
        val kafkaTopicInfoCalculator: KafkaTopicInfoCalculator) : PersistentDomainEventHandler(eventRepository) {

    override fun doHandle(event: DomainEvent) {
        val topicInfo = this.kafkaTopicInfoCalculator.calculate(event)
        val partition = this.partitionIdCalculator.calculate(event, topicInfo.partitionsNumber)
        this.kafkaTemplate.send(topicInfo.topicName, partition, event.aggregateId, event.payload)
    }

}