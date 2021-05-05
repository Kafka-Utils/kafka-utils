package br.com.kafkautils.kafka.consumergroups.service

import br.com.kafkautils.kafka.clients.AdminClientFactory
import br.com.kafkautils.kafka.cluster.model.Cluster
import br.com.kafkautils.kafka.consumergroups.model.ConsumerGroup
import br.com.kafkautils.kafka.consumergroups.model.ConsumerGroupTopic
import br.com.kafkautils.kafka.consumergroups.model.ConsumerGroupTopicPartitionOffset
import br.com.kafkautils.utils.FutureUtils
import javax.inject.Singleton
import reactor.core.publisher.Flux

@Singleton
open class ConsumerGroupService(
    private val adminClientFactory: AdminClientFactory,
    private val futureUtils: FutureUtils
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
        return futureUtils.toMono(result.partitionsToOffsetAndMetadata()).flatMapIterable { map ->
            map.entries.groupBy {
                it.key.topic()
            }.entries.map { entry ->
                ConsumerGroupTopic(
                    groupId = groupId,
                    topic = entry.key,
                    partitionsOffsets = entry.value.map {
                        ConsumerGroupTopicPartitionOffset(
                            partition = it.key.partition(),
                            offset = it.value.offset(),
                            leaderEpoch = it.value.leaderEpoch().orElse(null)
                        )
                    }.toSet()
                )
            }
        }
    }
}