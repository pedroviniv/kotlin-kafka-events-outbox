package io.github.kieckegard.outbox.kafka

import org.apache.kafka.clients.admin.*
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.KafkaException
import org.apache.kafka.common.KafkaFuture
import org.apache.kafka.common.errors.TimeoutException
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
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.function.Consumer
import java.util.stream.Collectors
import kotlin.collections.Collection
import kotlin.collections.HashMap
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.forEach
import kotlin.collections.map
import kotlin.collections.set
import kotlin.collections.toList


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
                .distinctBy { it.topicName }
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
        producerProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
        producerProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name

        return DefaultKafkaProducerFactory(producerProps)
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    fun kafkaTemplate(producerFactory: ProducerFactory<String, String>): KafkaTemplate<String, String> {
        return KafkaTemplate(producerFactory)
    }

    private fun addTopicsIfNeeded(adminClient: AdminClient, topics: Collection<NewTopic>) {
        if (topics.isNotEmpty()) {

            val topicsInfo = adminClient.describeTopics(topics.map { it.name() })
            val existingTopicsByName = topicsInfo
                    .values()

            val topicsToAdd = ArrayList<NewTopic>()

            for (it in topics) {
                val correspondingTopicFuture =
                        existingTopicsByName[it.name()]

                if (correspondingTopicFuture == null) {
                    topicsToAdd.add(it)
                    continue
                }

                try {
                    correspondingTopicFuture
                            .get(this.kafkaSettings.operationTimeout, TimeUnit.SECONDS)
                } catch (e: ExecutionException) {
                    topicsToAdd.add(it)
                }
            }

            // TODO: check if partition has changed

            if (topicsToAdd.isNotEmpty()) {
                adminClient.createTopics(topicsToAdd)
                        .all()
                        .get()
            }
        }
    }
    @Bean
    fun runner(kafkaAdmin: KafkaAdmin): ApplicationRunner {

        return ApplicationRunner {
            val kafka = AdminClient.create(kafkaAdmin.config)

            this.addTopicsIfNeeded(kafka, this.getAllTopics())
        }
    }

}