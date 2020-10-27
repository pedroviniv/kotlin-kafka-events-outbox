package io.github.kieckegard.outbox

class DomainEventTopic(
        val topicName: String,
        val partition: Int,
        val target: Class<*>
)