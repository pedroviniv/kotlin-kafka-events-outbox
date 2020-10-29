# kotlin-kafka-events-outbox
A POC that sends events to KAFKA (or other brokers) using at-least-once semantics. the main reason behind this POC was enable the dev to send events to a broker without having to worry about implementation specific stuff and consistency problems (you send an event inside a transaction block, the event will be delivered)

## usage

first you extend the class `DomainEvent`, creating your own domain events.
And annotate your specialization with `@KafkaTopicInfo`. This annotation has only one
paramater: the topicName.

ex:

```kotlin
@KafkaTopicInfo(topicName = "PRODUCTS")
class ProductCreatedDomainEvent(aggregateId: String, payload: String) : DomainEvent(...)
```

In the example above all `ProductCreatedDomainEvent` emitted will be sent to the kafka Topic "PRODUCTS".

Besides that you should explicitly configure a few things inside the `application.properties`.

```properties
# once you publish an event, the event will be stored in postgresql
# and a scheduler keeps fetching an amount of the unhandled events periodically
# this property tells how many events must be fetched to be processed periodically
events.handler.worker.batchSize=5

# period in milliseconds between scheduler cycles
# in this case, each 5000 milliseconds (5 seconds) the scheduler
# will fetch 5 events and proccess it.
events.handler.worker.fixedDelayInMs=5000

# timeout to do an operation to kafkaAdminClient
# mainly used to see if the topics found in the annotation @KafkaTopicInfo
# already exists in the Kafka broker
kafka.operationTimeout=20

# configuration of each topic :)
# here you can configure the number of partitions that each of your topics has
# ex: since we annotated our event with "@KafkaTopicInfo(topicName = "PRODUCTS")
# a topic called "PRODUCTS" will be created, so, here we configure the number of partitions
kafka.topics.PRODUCTS.partitionNumber=3
```

It's worth to mention that a DomainEvent has a aggregateId property because, inside a topic, all events
with the same aggregateId will be delivered to the same partition so kafka can guarantee the event
ordering.
