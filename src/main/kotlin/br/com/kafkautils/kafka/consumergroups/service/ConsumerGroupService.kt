package br.com.kafkautils.kafka.consumergroups.service

import br.com.kafkautils.kafka.clients.AdminClientFactory
import br.com.kafkautils.kafka.cluster.model.Cluster
import br.com.kafkautils.kafka.consumer.service.ConsumerService
import br.com.kafkautils.kafka.consumergroups.model.ConsumerGroup
import br.com.kafkautils.kafka.consumergroups.model.ConsumerGroupTopic
import br.com.kafkautils.kafka.consumergroups.model.ConsumerGroupTopicPartitionOffset
import br.com.kafkautils.kafka.consumergroups.model.TopicsToResetOffset
import br.com.kafkautils.kafka.consumergroups.model.ToOffset
import br.com.kafkautils.kafka.consumergroups.model.ToTime
import br.com.kafkautils.utils.FutureUtils
import javax.inject.Singleton
import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.common.TopicPartition
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

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

    open fun resetOffsetToOffset(cluster: Cluster, topicsToResetOffset: TopicsToResetOffset<ToOffset>): Mono<Void> {
        val client = adminClientFactory.build(cluster)
        val map = topicsToResetOffset.topicsAndOffsets.associate {
            val topicPartition = TopicPartition(it.topic, it.partition)
            val offsetAndMetadata = OffsetAndMetadata(it.offset)
            topicPartition to offsetAndMetadata
        }
        val result = client.alterConsumerGroupOffsets(topicsToResetOffset.groupId, map)
        return futureUtils.toMono(result.all())
    }

    open fun resetOffsetShift(cluster: Cluster, topicsToResetOffset: TopicsToResetOffset<ToOffset>): Mono<Void> {
        val client = adminClientFactory.build(cluster)
        val result = client.listConsumerGroupOffsets(topicsToResetOffset.groupId)
        return futureUtils.toMono(result.partitionsToOffsetAndMetadata()).flatMap { offsetByTopicPartition ->
            val map = topicsToResetOffset.topicsAndOffsets.associate {
                val topicPartition = TopicPartition(it.topic, it.partition)
                val currrentOffset = offsetByTopicPartition.getValue(topicPartition)
                val offsetAndMetadata = OffsetAndMetadata(currrentOffset.offset() + it.offset)
                topicPartition to offsetAndMetadata
            }
            val alterConsumerGroupOffsetsResult = client.alterConsumerGroupOffsets(topicsToResetOffset.groupId, map)
            futureUtils.toMono(alterConsumerGroupOffsetsResult.all())
        }
    }

    open fun resetOffsetToTime(cluster: Cluster, topicsToResetOffset: TopicsToResetOffset<ToTime>): Mono<Void> {
        val client = adminClientFactory.build(cluster)
        val partitions = topicsToResetOffset.topicsAndOffsets.associate {
            TopicPartition(it.topic, it.partition) to it.time
        }
        return consumerService.listOffsetsOffTopicAtTime(cluster, partitions).flatMap { offsetByTopicPartition ->
            val map = topicsToResetOffset.topicsAndOffsets.map {
                val topicPartition = TopicPartition(it.topic, it.partition)
                val offset = offsetByTopicPartition[topicPartition]
                val offsetAndMetadata = if (offset == null) null else OffsetAndMetadata(offset.offset())
                topicPartition to offsetAndMetadata
            }.filter { it.second != null }.toMap()
            val result = client.alterConsumerGroupOffsets(topicsToResetOffset.groupId, map)
            futureUtils.toMono(result.all())
        }
    }
}