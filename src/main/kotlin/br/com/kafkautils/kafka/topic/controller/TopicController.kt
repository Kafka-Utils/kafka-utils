package br.com.kafkautils.kafka.topic.controller

import br.com.kafkautils.http.DefaultErrorResponses
import br.com.kafkautils.kafka.cluster.service.ClusterService
import br.com.kafkautils.kafka.topic.model.NewPartition
import br.com.kafkautils.kafka.topic.model.NewTopicConfig
import br.com.kafkautils.kafka.topic.model.Topic
import br.com.kafkautils.kafka.topic.model.TopicConfig
import br.com.kafkautils.kafka.topic.model.TopicDescription
import br.com.kafkautils.kafka.topic.model.UpdateTopicConfig
import br.com.kafkautils.kafka.topic.service.TopicService
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Patch
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Put
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import javax.validation.Valid

@Controller("/cluster")
@Secured(SecurityRule.IS_AUTHENTICATED)
open class TopicController(
    private val topicService: TopicService,
    private val clusterService: ClusterService
) {

    @DefaultErrorResponses
    @Get("/{clusterId}/topic")
    open fun list(@PathVariable clusterId: Int): Flux<Topic> {
        return clusterService.get(clusterId).flatMapMany {
            topicService.list(it)
        }
    }

    @DefaultErrorResponses
    @Get("/{clusterId}/topic/{topic}")
    open fun get(@PathVariable clusterId: Int, @PathVariable topic: String): Mono<TopicDescription> {
        return clusterService.get(clusterId).flatMap {
            topicService.get(URLDecoder.decode(topic, StandardCharsets.UTF_8), it)
        }
    }

    @DefaultErrorResponses
    @Secured("EDITOR")
    @Post("/{clusterId}/topic")
    open fun add(@PathVariable clusterId: Int, @Body @Valid config: NewTopicConfig): Mono<Void> {
        return clusterService.get(clusterId).flatMap {
            topicService.add(config, it)
        }
    }

    @DefaultErrorResponses
    @Secured("EDITOR")
    @Patch("/{clusterId}/topic/partitions")
    open fun addPartition(@PathVariable clusterId: Int, @Body @Valid partition: Set<NewPartition>): Mono<Void> {
        return clusterService.get(clusterId).flatMap {
            val decodePartition = partition.map { newPartition ->
                newPartition.copy(topic = URLDecoder.decode(newPartition.topic, StandardCharsets.UTF_8))
            }.toSet()
            topicService.addPartition(decodePartition, it)
        }
    }

    @DefaultErrorResponses
    @Secured("EDITOR")
    @Put("/{clusterId}/topic/{topic}")
    open fun edit(@PathVariable clusterId: Int, @PathVariable topic: String, @Body @Valid config: TopicConfig): Mono<Void> {
        return clusterService.get(clusterId).flatMap {
            val updateTopicConfig = UpdateTopicConfig(URLDecoder.decode(topic, StandardCharsets.UTF_8), config)
            topicService.edit(updateTopicConfig, it)
        }
    }

    @DefaultErrorResponses
    @Secured("EDITOR")
    @Delete("/{clusterId}/topic/{topic}")
    open fun delete(@PathVariable clusterId: Int, @PathVariable topic: String): Mono<Void> {
        return clusterService.get(clusterId).flatMap {
            topicService.delete(URLDecoder.decode(topic, StandardCharsets.UTF_8), it)
        }
    }
}
