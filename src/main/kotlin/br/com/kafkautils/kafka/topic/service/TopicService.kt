package br.com.kafkautils.kafka.topic.service

import br.com.kafkautils.kafka.clients.AdminClientFactory
import br.com.kafkautils.kafka.cluster.model.Cluster
import br.com.kafkautils.kafka.topic.model.Topic
import br.com.kafkautils.utils.FutureUtils
import javax.inject.Singleton
import org.apache.kafka.clients.admin.ListTopicsOptions
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Singleton
class TopicService(
    private val adminClientFactory: AdminClientFactory,
    private val futureUtils: FutureUtils
) {

    fun list(listInternal: Boolean, cluster: Cluster): Flux<Topic> {
        val adminClient = adminClientFactory.build(cluster)
        val options = ListTopicsOptions()
        options.listInternal(listInternal)
        val future = adminClient.listTopics(options)
        return futureUtils.toMono(future.listings()).flatMapIterable { result ->
            result.map { topicInfo ->
                Topic(topicInfo.name(), topicInfo.isInternal)
            }
        }
    }
}