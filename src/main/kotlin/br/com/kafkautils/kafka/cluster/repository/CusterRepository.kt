package br.com.kafkautils.kafka.cluster.repository

import br.com.kafkautils.kafka.cluster.model.Cluster
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.r2dbc.repository.ReactorCrudRepository
import reactor.core.publisher.Mono

@R2dbcRepository(dialect = Dialect.MYSQL)
interface CusterRepository : ReactorCrudRepository<Cluster, Int> {
    fun existsByName(name: String): Mono<Boolean>
}
