package br.com.kafkautils.kafka.consumergroups.service

import br.com.kafkautils.kafka.clients.AdminClientFactory
import br.com.kafkautils.kafka.cluster.model.Cluster
import br.com.kafkautils.kafka.consumer.service.ConsumerService
import br.com.kafkautils.kafka.consumergroups.model.ConsumerGroup
import br.com.kafkautils.kafka.consumergroups.model.ConsumerGroupTopic
import br.com.kafkautils.kafka.consumergroups.model.ConsumerGroupTopicPartitionOffset
import br.com.kafkautils.kafka.consumergroups.model.ResetTo
import br.com.kafkautils.kafka.consumergroups.model.ResetToOffset
import br.com.kafkautils.kafka.consumergroups.model.ResetToTime
import br.com.kafkautils.kafka.consumergroups.model.TopicsToResetOffset
import br.com.kafkautils.utils.FutureUtils
import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.common.TopicPartition
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.inject.Singleton

@Singleton
open class ConsumerGroupService(
    private val adminClientFactory: AdminClientFactory,
    private val futureUtils: FutureUtils,
    private val consumerService: ConsumerService
) {
    open fun list(cluster: Cluster): Flux<ConsumerGroup> {
        val client = adminClientFactory.build(cluster)
        val result = client.listConsumerGroups()
        return futureUtils.toMono(result.all()).flatMapIterable { groups ->
            groups.map { group ->
                ConsumerGroup(
                    groupId = group.groupId(),
                    simple = group.isSimpleConsumerGroup,
                    state = group.state().map { it.name }.orElse("")
                )
            }
        }
    }

    open fun details(groupId: String, cluster: Cluster): Flux<ConsumerGroupTopic> {
        val client = adminClientFactory.build(cluster)
        val result = client.listConsumerGroupOffsets(groupId)
        return futureUtils.toMono(result.partitionsToOffsetAndMetadata()).flatMapMany { map ->
            val partitions = map.keys
            consumerService.listOffsetsOffTopic(cluster, partitions).flatMapIterable { offsetsOffTopics ->
                map.entries.groupBy {
                    it.key.topic()
                }.entries.map { entry ->
                    ConsumerGroupTopic(
                        groupId = groupId,
                        topic = entry.key,
                        partitionsOffsets = entry.value.map {
                            val offset = offsetsOffTopics[it.key] ?: 0
                            ConsumerGroupTopicPartitionOffset(
                                partition = it.key.partition(),
                                offset = it.value.offset(),
                                leaderEpoch = it.value.leaderEpoch().orElse(null),
                                lastOffset = offset,
                                lag = offset - it.value.offset()
                            )
                        }.toSet()
                    )
                }
            }
        }
    }

    open fun resetOffsetToOffset(
        cluster: Cluster,
        topicsResetToResetOffset: TopicsToResetOffset<ResetToOffset>
    ): Mono<Void> {
        val client = adminClientFactory.build(cluster)
        val map = topicsResetToResetOffset.topicsAndOffsets.associate {
            val topicPartition = TopicPartition(it.topic, it.partition)
            val offsetAndMetadata = OffsetAndMetadata(it.offset)
            topicPartition to offsetAndMetadata
        }
        val result = client.alterConsumerGroupOffsets(topicsResetToResetOffset.groupId, map)
        return futureUtils.toMono(result.all())
    }

    open fun resetOffsetShift(
        cluster: Cluster,
        topicsResetToResetOffset: TopicsToResetOffset<ResetToOffset>
    ): Mono<Void> {
        val client = adminClientFactory.build(cluster)
        val result = client.listConsumerGroupOffsets(topicsResetToResetOffset.groupId)
        return futureUtils.toMono(result.partitionsToOffsetAndMetadata()).flatMap { offsetByTopicPartition ->
            val map = topicsResetToResetOffset.topicsAndOffsets.associate {
                val topicPartition = TopicPartition(it.topic, it.partition)
                val currrentOffset = offsetByTopicPartition.getValue(topicPartition)
                val offsetAndMetadata = OffsetAndMetadata(currrentOffset.offset() + it.offset)
                topicPartition to offsetAndMetadata
            }
            val alterConsumerGroupOffsetsResult =
                client.alterConsumerGroupOffsets(topicsResetToResetOffset.groupId, map)
            futureUtils.toMono(alterConsumerGroupOffsetsResult.all())
        }
    }

    open fun resetOffsetToTime(
        cluster: Cluster,
        topicsResetToResetOffset: TopicsToResetOffset<ResetToTime>
    ): Mono<Void> {
        val client = adminClientFactory.build(cluster)
        val partitions = topicsResetToResetOffset.topicsAndOffsets.associate {
            TopicPartition(it.topic, it.partition) to it.time
        }
        return consumerService.listOffsetsOffTopicAtTime(cluster, partitions).flatMap { offsetByTopicPartition ->
            val map = topicsResetToResetOffset.topicsAndOffsets.map {
                val topicPartition = TopicPartition(it.topic, it.partition)
                val offset = offsetByTopicPartition[topicPartition]
                val offsetAndMetadata = if (offset == null) null else OffsetAndMetadata(offset.offset())
                topicPartition to offsetAndMetadata
            }.filter { it.second != null }.toMap()
            val result = client.alterConsumerGroupOffsets(topicsResetToResetOffset.groupId, map)
            futureUtils.toMono(result.all())
        }
    }

    open fun resetOffsetTo(cluster: Cluster, topicsResetToResetOffset: TopicsToResetOffset<ResetTo>): Mono<Void> {
        val client = adminClientFactory.build(cluster)
        val byOffset = topicsResetToResetOffset.topicsAndOffsets.groupBy { it.to }
        var earliestMono: Mono<Void>? = null
        if (byOffset.containsKey("earliest")) {
            val toEarliest = byOffset.getValue("earliest")
            val map = toEarliest.associate {
                val topicPartition = TopicPartition(it.topic, it.partition)
                val offsetAndMetadata = OffsetAndMetadata(0L)
                topicPartition to offsetAndMetadata
            }
            val result = client.alterConsumerGroupOffsets(topicsResetToResetOffset.groupId, map)
            earliestMono = futureUtils.toMono(result.all())
        }
        var latestMono: Mono<Void>? = null
        if (byOffset.containsKey("latest")) {
            val partitions = byOffset.getValue("latest").map {
                TopicPartition(it.topic, it.partition)
            }.toSet()
            latestMono = consumerService.listOffsetsOffTopic(cluster, partitions).flatMap { offsetByPartition ->
                val map = offsetByPartition.entries.associate {
                    it.key to OffsetAndMetadata(it.value)
                }
                val result = client.alterConsumerGroupOffsets(topicsResetToResetOffset.groupId, map)
                futureUtils.toMono(result.all())
            }
        }
        if (latestMono != null && earliestMono != null) {
            return latestMono.flatMap { earliestMono }
        }
        return latestMono ?: earliestMono ?: Mono.empty()
    }
}
