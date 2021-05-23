package br.com.kafkautils.kafka.consumergroups.controller

import br.com.kafkautils.http.DefaultErrorResponses
import br.com.kafkautils.kafka.cluster.service.ClusterService
import br.com.kafkautils.kafka.consumergroups.model.ConsumerGroup
import br.com.kafkautils.kafka.consumergroups.model.ConsumerGroupTopic
import br.com.kafkautils.kafka.consumergroups.model.ResetTo
import br.com.kafkautils.kafka.consumergroups.model.ResetToOffset
import br.com.kafkautils.kafka.consumergroups.model.ResetToTime
import br.com.kafkautils.kafka.consumergroups.model.TopicsToResetOffset
import br.com.kafkautils.kafka.consumergroups.service.ConsumerGroupService
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Put
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller("/cluster")
@Secured(SecurityRule.IS_AUTHENTICATED)
open class ConsumerGroupController(
    private val clusterService: ClusterService,
    private val consumerGroupService: ConsumerGroupService
) {

    @DefaultErrorResponses
    @Get("/{clusterId}/consumer-group")
    open fun list(@PathVariable clusterId: Int): Flux<ConsumerGroup> {
        return clusterService.get(clusterId).flatMapMany {
            consumerGroupService.list(it)
        }
    }

    @DefaultErrorResponses
    @Get("/{clusterId}/consumer-group/{groupId}")
    open fun details(@PathVariable clusterId: Int, @PathVariable groupId: String): Flux<ConsumerGroupTopic> {
        return clusterService.get(clusterId).flatMapMany {
            consumerGroupService.details(groupId, it)
        }
    }

    @DefaultErrorResponses
    @Put("/{clusterId}/consumer-group/{groupId}/offset/offsets")
    open fun resetToOffset(@PathVariable clusterId: Int, @PathVariable groupId: String, @Body topicsResetToResetOffset: TopicsToResetOffset<ResetToOffset>): Mono<Void> {
        return clusterService.get(clusterId).flatMap {
            consumerGroupService.resetOffsetToOffset(it, topicsResetToResetOffset)
        }
    }

    @DefaultErrorResponses
    @Put("/{clusterId}/consumer-group/{groupId}/offset/shift")
    open fun resetOffsetToOffset(@PathVariable clusterId: Int, @PathVariable groupId: String, @Body topicsResetToResetOffset: TopicsToResetOffset<ResetToOffset>): Mono<Void> {
        return clusterService.get(clusterId).flatMap {
            consumerGroupService.resetOffsetShift(it, topicsResetToResetOffset)
        }
    }

    @DefaultErrorResponses
    @Put("/{clusterId}/consumer-group/{groupId}/offset/time")
    open fun resetOffsetToTime(@PathVariable clusterId: Int, @PathVariable groupId: String, @Body topicsResetToResetOffset: TopicsToResetOffset<ResetToTime>): Mono<Void> {
        return clusterService.get(clusterId).flatMap {
            consumerGroupService.resetOffsetToTime(it, topicsResetToResetOffset)
        }
    }

    @DefaultErrorResponses
    @Put("/{clusterId}/consumer-group/{groupId}/offset")
    open fun resetOffsetTo(@PathVariable clusterId: Int, @PathVariable groupId: String, @Body topicsResetToResetOffset: TopicsToResetOffset<ResetTo>): Mono<Void> {
        return clusterService.get(clusterId).flatMap {
            consumerGroupService.resetOffsetTo(it, topicsResetToResetOffset)
        }
    }
}
