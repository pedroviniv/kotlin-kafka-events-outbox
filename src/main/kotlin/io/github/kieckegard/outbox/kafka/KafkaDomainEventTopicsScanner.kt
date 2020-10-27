package io.github.kieckegard.outbox.kafka

import io.github.kieckegard.outbox.ClassScanner
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Configuration
@Component
class KafkaDomainEventTopicsScanner(
        val scanner: ClassScanner,
        @Value("\${kafka.defaultPartitionNumber}")
        val defaultPartitionNumber: Int,
        @Value("\${kafka.defaultTopicNameTemplate}")
        val defaultTopicNameTemplate: String
) {

    @Bean
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    fun provideDomainEventTopics(): Map<Class<*>, KafkaDomainEventTopic> {
        return this.scan()
    }

    fun mapToValue(clazz: Class<*>): KafkaDomainEventTopic {
        val annotation = clazz.getAnnotation(KafkaTopicInfo::class.java)

        var partitionNumber = this.defaultPartitionNumber
        if (annotation.partitionNumber > 0) {
            partitionNumber = annotation.partitionNumber
        }

        var topicName = annotation.topicName
        if (annotation.topicName.isEmpty()) {

            // using default topic naming behaviour
            if (this.defaultTopicNameTemplate == null) {
                topicName = clazz.name
            } else if (this.defaultTopicNameTemplate.contains("\$")) { // has variables
                topicName = this.defaultTopicNameTemplate
                        .replace("\$CLASS_NAME", clazz.name)
            }
        }
        // using template defined topic name
        else if (annotation.topicName
                .contains("\$")) { // has variables
            topicName = annotation.topicName
                    .replace("\$CLASS_NAME", clazz.name)
        }

        return KafkaDomainEventTopic(
                topicName,
                partitionNumber,
                clazz
        )
    }

    fun scan(): Map<Class<*>, KafkaDomainEventTopic> {
        val candidates = this.scanner.getClassesAnnotatedWith(KafkaTopicInfo::class.java)
        return candidates
                .map { it to mapToValue(it) }
                .toMap()
    }
}