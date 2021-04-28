package br.com.kafkautils.kafka.topic.controller

import br.com.kafkautils.kafka.cluster.service.CusterService
import br.com.kafkautils.kafka.topic.model.Topic
import br.com.kafkautils.kafka.topic.service.TopicService
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import reactor.core.publisher.Flux

@Controller("/cluster")
@Secured(SecurityRule.IS_AUTHENTICATED)
class TopicController(
    private val topicService: TopicService,
    private val custerService: CusterService
) {

    @Get("/{clusterId}/topic{?listInternal}")
    fun list(@PathVariable clusterId: Int, listInternal: Boolean?): Flux<Topic> {
        return custerService.get(clusterId).flatMapMany {
            topicService.list(listInternal ?: false, it)
        }
    }
}