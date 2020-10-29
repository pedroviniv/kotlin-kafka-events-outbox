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

# property to configure the kafka broker address
# address of your kafka broker
kafka.broker.address=localhost:9092
```

It's worth to mention that a DomainEvent has a aggregateId property because, inside a topic, all events
with the same aggregateId will be delivered to the same partition so kafka can guarantee the event
ordering.

Also, you don't need to explicitly create the topics you defined in the code inside Kafka. This POC
scans all the annotated DomainEvent specializations and creates the corresponding topics.

## running

to run this application you will need kafka+zookeeper and postgresql instances running.
`YOU DON'T` need to create the topics defined in the code. (this scans all the @KafkaTopicInfo, creating
the corresponding topics)

I recommend running kafka + zookeeper using this docker-compose file: https://github.com/obsidiandynamics/kafdrop/tree/master/docker-compose/kafka-kafdrop
because it starts a kafdrop instance so you can inspect the messages sents, the created topics, partitions, etc.

Also, when this application requests kafka, it first gets metadata about the broker cluster and, in the docker-compose above,
the host of the broker cluster is `kafka`. so you have to map inside your OS hosts file the `kafka` hostname to `127.0.0.1`.
OR you could run this application with docker with a link to the name `kafka` :)


