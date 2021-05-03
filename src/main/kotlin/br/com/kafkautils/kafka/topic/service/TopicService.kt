package br.com.kafkautils.kafka.topic.service

import br.com.kafkautils.kafka.clients.AdminClientFactory
import br.com.kafkautils.kafka.cluster.model.Cluster
import br.com.kafkautils.kafka.topic.model.NewPartition
import br.com.kafkautils.kafka.topic.model.NewTopicConfig
import br.com.kafkautils.kafka.topic.model.PartitionInfo
import br.com.kafkautils.kafka.topic.model.Topic
import br.com.kafkautils.kafka.topic.model.TopicConfig
import br.com.kafkautils.kafka.topic.model.TopicDescription
import br.com.kafkautils.kafka.topic.model.UpdateTopicConfig
import br.com.kafkautils.utils.FutureUtils
import org.apache.kafka.clients.admin.AlterConfigOp
import org.apache.kafka.clients.admin.ConfigEntry
import org.apache.kafka.clients.admin.ListTopicsOptions
import org.apache.kafka.clients.admin.NewPartitions
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.common.config.ConfigResource
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.inject.Singleton
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@Singleton
open class TopicService(
    private val adminClientFactory: AdminClientFactory,
    private val futureUtils: FutureUtils
) {

    open fun list(cluster: Cluster): Flux<Topic> {
        val adminClient = adminClientFactory.build(cluster)
        val options = ListTopicsOptions()
        options.listInternal(true)
        val future = adminClient.listTopics(options)
        return futureUtils.toMono(future.listings()).flatMapIterable { result ->
            result.map { topicInfo ->
                Topic(topicInfo.name(), topicInfo.isInternal)
            }
        }
    }

    open fun get(@NotBlank topic: String, cluster: Cluster): Mono<TopicDescription> {
        val adminClient = adminClientFactory.build(cluster)
        val resource = ConfigResource(ConfigResource.Type.TOPIC, topic)

        val futureDescribeConfigs = adminClient.describeConfigs(listOf(resource))
        val describeConfigsMono = futureUtils.toMono(futureDescribeConfigs.all()).map {
            val config = it.getValue(resource)
            TopicConfig(config)
        }

        val futureDescribeTopics = adminClient.describeTopics(listOf(topic))
        val describeTopicsMono = futureUtils.toMono(futureDescribeTopics.all())

        return describeConfigsMono.flatMap { topicConfig ->
            describeTopicsMono.map { descriptions ->
                val description = descriptions.getValue(topic)
                TopicDescription(
                    name = topic,
                    internal = description.isInternal,
                    partitions = description.partitions().map {
                        PartitionInfo(
                            partition = it.partition(),
                            replicas = it.replicas().size
                        )
                    }.toSet(),
                    topicConfig = topicConfig
                )
            }
        }
    }

    open fun add(@Valid topicConfig: NewTopicConfig, cluster: Cluster): Mono<Void> {
        val adminClient = adminClientFactory.build(cluster)
        val newTopic = NewTopic(topicConfig.name, topicConfig.numPartitions, topicConfig.replicationFactor)
        newTopic.configs(topicConfig.configMap())
        val future = adminClient.createTopics(listOf(newTopic))
        return futureUtils.toMono(future.all())
    }

    open fun addPartition(@Valid partitions: Set<NewPartition>, cluster: Cluster): Mono<Void> {
        val adminClient = adminClientFactory.build(cluster)
        val newPartitions = partitions.associate {
            it.topic to NewPartitions.increaseTo(it.numPartitions)
        }
        val future = adminClient.createPartitions(newPartitions)
        return futureUtils.toMono(future.all())
    }

    open fun edit(@Valid topicConfig: UpdateTopicConfig, cluster: Cluster): Mono<Void> {
        val adminClient = adminClientFactory.build(cluster)
        val configs = topicConfig.configMap().entries.map {
            val configEntry = ConfigEntry(it.key, it.value)
            AlterConfigOp(configEntry, AlterConfigOp.OpType.SET)
        }
        val resource = ConfigResource(ConfigResource.Type.TOPIC, topicConfig.name)
        val updateConfig = mapOf(resource to configs)
        val future = adminClient.incrementalAlterConfigs(updateConfig)
        return futureUtils.toMono(future.all())
    }

    open fun delete(@NotBlank topic: String, cluster: Cluster): Mono<Void> {
        val adminClient = adminClientFactory.build(cluster)
        val future = adminClient.deleteTopics(listOf(topic))
        return futureUtils.toMono(future.all())
    }
}
