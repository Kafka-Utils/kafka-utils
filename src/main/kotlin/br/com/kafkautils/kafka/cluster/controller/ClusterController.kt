package br.com.kafkautils.kafka.cluster.controller

import br.com.kafkautils.http.DefaultErrorResponses
import br.com.kafkautils.kafka.cluster.controller.command.ClusterCommand
import br.com.kafkautils.kafka.cluster.controller.dto.ClusterDto
import br.com.kafkautils.kafka.cluster.service.CusterService
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Put
import io.micronaut.security.annotation.Secured
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.validation.Valid

@Controller("/cluster")
@Secured("EDITOR")
open class ClusterController(
    private val custerService: CusterService,
    private val clusterMapper: ClusterMapper
) {

    @Get("/")
    @DefaultErrorResponses
    open fun list(): Flux<ClusterDto> {
        return custerService.list().map {
            clusterMapper.toDto(it)
        }
    }

    @Get("/{id}")
    @DefaultErrorResponses
    open fun get(@PathVariable id: Int): Mono<ClusterDto> {
        return custerService.get(id).map {
            clusterMapper.toDto(it)
        }
    }

    @Post("/")
    @DefaultErrorResponses
    open fun add(@Valid @Body command: ClusterCommand): Mono<ClusterDto> {
        val cluster = clusterMapper.toDomain(command)
        return custerService.add(cluster).map {
            clusterMapper.toDto(it)
        }
    }

    @Put("/{id}")
    @DefaultErrorResponses
    open fun update(@PathVariable id: Int, @Valid @Body command: ClusterCommand): Mono<ClusterDto> {
        return custerService.get(id).flatMap { cluster ->
            val userToUpdate = clusterMapper.updateFromCommand(command, cluster)
            custerService.update(userToUpdate).map {
                clusterMapper.toDto(it)
            }
        }
    }

}
