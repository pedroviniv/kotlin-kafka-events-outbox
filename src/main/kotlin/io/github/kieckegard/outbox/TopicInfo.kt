package io.github.kieckegard.outbox

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class TopicInfo(
        val topicName: String = "",
        val partitionNumber: Int = -1
)