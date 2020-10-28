package io.github.kieckegard.outbox.kafka

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class KafkaSettings(
        @Value("\${kafka.broker.address}")
        val bootstrapServer: String
)