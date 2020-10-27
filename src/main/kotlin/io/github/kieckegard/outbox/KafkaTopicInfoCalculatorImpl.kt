package io.github.kieckegard.outbox

import org.springframework.stereotype.Component
import java.lang.RuntimeException

@Component
class KafkaTopicInfoCalculatorImpl(
        val topicInfoByEventType: Map<Class<*>, DomainEventTopic>
) : KafkaTopicInfoCalculator {

    override fun calculate(event: DomainEvent): KafkaTopicInfoCalculator.KafkaTopicInfo {

        val result = this.topicInfoByEventType[event::class.java]
                ?: throw RuntimeException("")

        return KafkaTopicInfoCalculator.KafkaTopicInfo(
                result.topicName,
                result.partition
        );
    }
}