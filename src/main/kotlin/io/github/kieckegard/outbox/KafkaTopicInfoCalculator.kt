package io.github.kieckegard.outbox

/**
 * calculates|retrieves info about the domainEvent
 * corresponding Topic.
 */
interface KafkaTopicInfoCalculator {

    /**
     * info about the kafka topic
     */
    class KafkaTopicInfo(
            /**
             * topic name
             */
            val topicName: String,
            /**
             * number of partitions
             */
            val partitionsNumber: Int
    )

    fun calculate(event: DomainEvent): KafkaTopicInfo
}