package br.com.kafkautils.kafka.cluster.controller

import br.com.kafkautils.http.DefaultErrorResponses
import br.com.kafkautils.kafka.cluster.controller.dto.ClusterCommandDto
import br.com.kafkautils.kafka.cluster.controller.dto.ClusterDto
import br.com.kafkautils.kafka.cluster.service.ClusterService
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Put
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.swagger.v3.oas.annotations.tags.Tag
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.validation.Valid

@Controller("/cluster")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "cluster")
open class ClusterController(
    private val clusterService: ClusterService,
    private val clusterMapper: ClusterMapper
) {

    @Get("/")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @DefaultErrorResponses
    open fun list(): Flux<ClusterDto> {
        return clusterService.list().map {
            clusterMapper.toDto(it)
        }
    }

    @Get("/{id}")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @DefaultErrorResponses
    open fun get(@PathVariable id: Int): Mono<ClusterDto> {
        return clusterService.get(id).map {
            clusterMapper.toDto(it)
        }
    }

    @Secured("EDITOR", "ADMIN")
    @Post("/")
    @DefaultErrorResponses
    open fun add(@Valid @Body commandDto: ClusterCommandDto): Mono<ClusterDto> {
        val cluster = clusterMapper.toDomain(commandDto)
        return clusterService.add(cluster).map {
            clusterMapper.toDto(it)
        }
    }

    @Secured("EDITOR", "ADMIN")
    @Put("/{id}")
    @DefaultErrorResponses
    open fun update(@PathVariable id: Int, @Valid @Body commandDto: ClusterCommandDto): Mono<ClusterDto> {
        return clusterService.get(id).flatMap { cluster ->
            val userToUpdate = clusterMapper.updateFromCommand(commandDto, cluster)
            clusterService.update(userToUpdate).map {
                clusterMapper.toDto(it)
            }
        }
    }
}
