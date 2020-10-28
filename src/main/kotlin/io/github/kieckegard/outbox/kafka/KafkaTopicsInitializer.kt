package io.github.kieckegard.outbox.kafka

import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaAdmin
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

/**
 * component that executes on startup, injects the events annotated with EventInfo,
 * and registers corresponding topics in KAFKA
 */
@Configuration
class KafkaTopicsInitializer(
        val kafkaSettings: KafkaSettings,
        val scanned: KafkaScannedDomainEventTopics
) {

    fun toNewTopic(domainEventTopic: KafkaDomainEventTopic): NewTopic {
        return NewTopic(
                domainEventTopic.topicName,
                domainEventTopic.partition,
                1
        )
    }

    fun getAllTopics(): List<NewTopic> {

        return this.scanned.map
                .values
                .map { this.toNewTopic(it) }
                .toList()
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    fun kafkaAdmin(): KafkaAdmin {
        val kafkaAdminProps = HashMap<String, Any>()
        kafkaAdminProps[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = this.kafkaSettings.bootstrapServer
        return KafkaAdmin(kafkaAdminProps)
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    fun producerFactory(): ProducerFactory<String, String> {
        val producerProps = HashMap<String, Any>()
        producerProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = this.kafkaSettings.bootstrapServer
        producerProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class
        producerProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class

        return DefaultKafkaProducerFactory(producerProps)
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    fun kafkaTemplate(producerFactory: ProducerFactory<String, String>): KafkaTemplate<String, String> {
        return KafkaTemplate(producerFactory)
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