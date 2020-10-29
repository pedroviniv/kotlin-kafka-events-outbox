package io.github.kieckegard.outbox.kafka

import io.github.kieckegard.outbox.DomainEvent
import org.springframework.stereotype.Component
import java.lang.RuntimeException

@Component
class KafkaTopicInfoCalculatorImpl(
        val scanned: KafkaScannedDomainEventTopics
) : KafkaTopicInfoCalculator {

    override fun calculate(event: DomainEvent): KafkaTopicInfoCalculator.KafkaTopicInfo {

        val type = Class.forName(event.type)

        val result = this.scanned.map[type]
                ?: throw RuntimeException("")

        return KafkaTopicInfoCalculator.KafkaTopicInfo(
                result.topicName,
                result.partition
        );
    }
}