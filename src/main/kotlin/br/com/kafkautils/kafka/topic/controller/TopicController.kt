package br.com.kafkautils.kafka.topic.controller

import br.com.kafkautils.http.DefaultErrorResponses
import br.com.kafkautils.kafka.cluster.service.CusterService
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
import javax.validation.Valid
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller("/cluster")
@Secured(SecurityRule.IS_AUTHENTICATED)
class TopicController(
    private val topicService: TopicService,
    private val custerService: CusterService
) {

    @DefaultErrorResponses
    @Get("/{clusterId}/topic")
    fun list(@PathVariable clusterId: Int): Flux<Topic> {
        return custerService.get(clusterId).flatMapMany {
            topicService.list(it)
        }
    }

    @DefaultErrorResponses
    @Get("/{clusterId}/topic/{topic}")
    fun get(@PathVariable clusterId: Int, @PathVariable topic: String): Mono<TopicDescription> {
        return custerService.get(clusterId).flatMap {
            topicService.get(topic, it)
        }
    }

    @DefaultErrorResponses
    @Secured("EDITOR")
    @Post("/{clusterId}/topic")
    fun add(@PathVariable clusterId: Int, @Body @Valid config: NewTopicConfig): Mono<Void> {
        return custerService.get(clusterId).flatMap {
            topicService.add(config, it)
        }
    }

    @DefaultErrorResponses
    @Secured("EDITOR")
    @Patch("/{clusterId}/topic/partitions")
    fun addPartition(@PathVariable clusterId: Int, @Body @Valid partition: Set<NewPartition>): Mono<Void> {
        return custerService.get(clusterId).flatMap {
            topicService.addPartition(partition, it)
        }
    }

    @DefaultErrorResponses
    @Secured("EDITOR")
    @Put("/{clusterId}/topic/{topic}")
    fun edit(@PathVariable clusterId: Int, @PathVariable topic: String, @Body @Valid config: TopicConfig): Mono<Void> {
        return custerService.get(clusterId).flatMap {
            val updateTopicConfig = UpdateTopicConfig(topic, config)
            topicService.edit(updateTopicConfig, it)
        }
    }

    @DefaultErrorResponses
    @Secured("EDITOR")
    @Delete("/{clusterId}/topic/{topic}")
    fun delete(@PathVariable clusterId: Int, @PathVariable topic: String): Mono<Void> {
        return custerService.get(clusterId).flatMap {
            topicService.delete(topic, it)
        }
    }
}