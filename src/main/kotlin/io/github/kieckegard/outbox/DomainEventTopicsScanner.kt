package io.github.kieckegard.outbox

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.stereotype.Component

@Configuration
@Component
class DomainEventTopicsScanner(
        @Qualifier("scanner_LOCAL")
        val scanner: ClassPathScanningCandidateComponentProvider,
        @Value("\${kafka.defaultPartitionNumber}")
        val defaultPartitionNumber: Int,
        @Value("\${kafka.defaultTopicNameTemplate}")
        val defaultTopicNameTemplate: String
) {

    @Bean
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    fun provideDomainEventTopics(): Map<Class<*>, DomainEventTopic> {
        return this.scan()
    }

    @Bean("scanner_LOCAL")
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    fun provideScanner(): ClassPathScanningCandidateComponentProvider {
        val scanner = ClassPathScanningCandidateComponentProvider(true)
        scanner.addIncludeFilter(AnnotationTypeFilter(TopicInfo::class.java))
        return scanner
    }

    fun mapToKey(definition: BeanDefinition): Class<*> {
        return Class.forName(definition.beanClassName)
    }

    fun mapToValue(definition: BeanDefinition): DomainEventTopic {
        val clazz = this.mapToKey(definition)
        val annotation = clazz.getAnnotation(TopicInfo::class.java)

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

        return DomainEventTopic(
                topicName,
                partitionNumber,
                clazz
        )
    }

    fun scan(): Map<Class<*>, DomainEventTopic> {
        val candidates = this.scanner.findCandidateComponents("io.github.kieckegard.outbox")
        return candidates
                .map { mapToKey(it) to mapToValue(it) }
                .toMap()
    }
}