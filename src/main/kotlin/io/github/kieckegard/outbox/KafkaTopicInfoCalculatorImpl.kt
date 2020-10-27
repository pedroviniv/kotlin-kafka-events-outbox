package io.github.kieckegard.outbox

import org.springframework.stereotype.Component

@Component
class KafkaTopicInfoCalculatorImpl : KafkaTopicInfoCalculator {
    override fun calculate(event: DomainEvent): KafkaTopicInfoCalculator.KafkaTopicInfo {
        val topicName = "topic-${event.type}"
        return KafkaTopicInfoCalculator.KafkaTopicInfo(topicName, 6)
    }
}