package io.github.kieckegard.outbox

import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaAdmin

/**
 * component that executes on startup, injects the events annotated with EventInfo,
 * and registers corresponding topics in KAFKA
 */
@Configuration
class KafkaTopicsInitializer(
        val domainEventTopics: Map<Class<*>, DomainEventTopic>
) {

    fun toNewTopic(domainEventTopic: DomainEventTopic): NewTopic {
        return NewTopic(
                domainEventTopic.topicName,
                domainEventTopic.partition,
                1
        )
    }

    fun getAllTopics(): List<NewTopic> {
        return this.domainEventTopics
                .values
                .map { this.toNewTopic(it) }
                .toList()
    }

    @Bean
    fun runner(kafkaAdmin: KafkaAdmin): ApplicationRunner {

        // TODO: check if some topic already exists to ignore it's creation

        return ApplicationRunner {
            val kafka = AdminClient.create(kafkaAdmin.config)

            kafka.createTopics(this.getAllTopics())
                    .all()
                    .get()
        }
    }

}