package br.com.kafkautils.security.user

import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.r2dbc.repository.ReactorCrudRepository
import reactor.core.publisher.Mono
import java.util.*
import javax.validation.constraints.NotNull

@R2dbcRepository(dialect = Dialect.POSTGRES)
interface UserRepository: ReactorCrudRepository<UserData, UUID> {
    fun findByUsername(@NotNull username: String): Mono<UserData>
}