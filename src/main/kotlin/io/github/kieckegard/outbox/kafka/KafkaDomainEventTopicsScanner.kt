package io.github.kieckegard.outbox.kafka

import io.github.kieckegard.outbox.ClassScanner
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

class KafkaScannedDomainEventTopics(val map: Map<Class<*>, KafkaDomainEventTopic>)

@Configuration
class Provider(val scanner: KafkaDomainEventTopicsScanner) {

    @Bean
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    fun provideScanned(): KafkaScannedDomainEventTopics {
        return KafkaScannedDomainEventTopics(this.scanner.scan())
    }
}

@Component
class KafkaDomainEventTopicsScanner(
        val scanner: ClassScanner,
        @Value("\${kafka.defaultPartitionNumber}")
        val defaultPartitionNumber: Int,
        @Value("\${kafka.defaultTopicNameTemplate}")
        val defaultTopicNameTemplate: String
) {

    fun isATemplate(text: String): Boolean {
        return text.contains("\$")
    }

    fun compile(template: String, clazz: Class<*>): String {
        return template.replace("\$CLASS_NAME", clazz.name)
    }

    /**
     * gets the topic name
     */
    fun getTopicName(annotation: KafkaTopicInfo, clazz: Class<*>): String {
        if (annotation.topicName.isEmpty()) {

            if (this.defaultTopicNameTemplate == null) {
                return clazz.name
            } else if (this.isATemplate(this.defaultTopicNameTemplate)) {
                return this.compile(this.defaultTopicNameTemplate, clazz)
            }
        }

        if (this.isATemplate(annotation.topicName)) {
            return this.compile(annotation.topicName, clazz)
        }

        return annotation.topicName
    }

    fun getPartitionNumber(annotation: KafkaTopicInfo): Int {

        if (annotation.partitionNumber < 1) {
            return this.defaultPartitionNumber
        }

        return annotation.partitionNumber
    }

    fun mapToValue(clazz: Class<*>): KafkaDomainEventTopic {
        val annotation = clazz.getAnnotation(KafkaTopicInfo::class.java)

        val partitionNumber = this.getPartitionNumber(annotation)
        val topicName = this.getTopicName(annotation, clazz)

        return KafkaDomainEventTopic(
                topicName,
                partitionNumber,
                clazz
        )
    }

    fun scan(): Map<Class<*>, KafkaDomainEventTopic> {
        val candidates = this.scanner.getClassesAnnotatedWith(KafkaTopicInfo::class.java)
        val asMap = candidates
                .map { it to mapToValue(it) }
                .toMap()

        return asMap
    }
}