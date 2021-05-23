package br.com.kafkautils.kafka.consumer.service

import br.com.kafkautils.kafka.cluster.model.Cluster
import io.micronaut.scheduling.TaskExecutors
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.consumer.OffsetAndTimestamp
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import java.time.ZonedDateTime
import java.util.Properties
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class ConsumerService(
    @Named(TaskExecutors.IO) executorService: ExecutorService
) {

    private val scheduler: Scheduler = Schedulers.fromExecutor(executorService)
    private val consumerCache = ConcurrentHashMap<Int, Consumer<String, String>>()

    fun listOffsetsOffTopic(cluster: Cluster, topics: Set<TopicPartition>): Mono<Map<TopicPartition, Long>> {
        return Mono.fromCallable {
            val consumer = getSimpleConsumer(cluster)
            consumer.endOffsets(topics)
        }.subscribeOn(scheduler)
    }

    fun listOffsetsOffTopicAtTime(cluster: Cluster, topics: Map<TopicPartition, ZonedDateTime>): Mono<Map<TopicPartition, OffsetAndTimestamp>> {
        return Mono.fromCallable {
            val consumer = getSimpleConsumer(cluster)
            val partitionTimes = topics.mapValues { it.value.toInstant().toEpochMilli() }
            consumer.offsetsForTimes(partitionTimes)
        }.subscribeOn(scheduler)
    }

    fun buildSimpleConsumer(cluster: Cluster, groupId: String): Consumer<String, String> {
        val consumerProperties = Properties()
        consumerProperties[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = cluster.brokers()
        consumerProperties[ConsumerConfig.CLIENT_ID_CONFIG] = "$groupId-${UUID.randomUUID()}"
        consumerProperties[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        consumerProperties[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        consumerProperties[ConsumerConfig.GROUP_ID_CONFIG] = groupId
        consumerProperties[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = "false"
        consumerProperties[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        return KafkaConsumer(consumerProperties)
    }

    private fun getSimpleConsumer(cluster: Cluster): Consumer<String, String> {
        return consumerCache.getOrPut(cluster.id) {
            buildSimpleConsumer(cluster, "kafka-utils")
        }
    }
}
