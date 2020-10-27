package io.github.kieckegard.outbox.kafka

/**
 * special annotation used to bind a specific Topic
 * to the annotated DomainEvent.
 *
 * currently has two properties:
 *
 * 1. topicName: specifies the name of the topic. Can be a template
 * as well and following variables are allowed:
 * <ul>
 *     <li>$CLASS_NAME = that retrieves the name of the class annotated</li>
 * </ul>
 *
 * 2. partitionNumber: specifies the number of partition of this topic.
 * if isn't specified, ${kafka.defaultPartitionNumber} will be used
 * and if this isn't specified, 1 will be used.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class KafkaTopicInfo(
        val topicName: String = "",
        val partitionNumber: Int = -1
)