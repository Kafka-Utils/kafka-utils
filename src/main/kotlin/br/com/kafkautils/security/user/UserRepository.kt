package br.com.kafkautils.security.user

import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.r2dbc.repository.ReactorCrudRepository
import reactor.core.publisher.Mono
import java.util.*
import javax.validation.constraints.NotNull

@R2dbcRepository(dialect = Dialect.MYSQL)
interface UserRepository: ReactorCrudRepository<UserData, Int> {
    fun findByUsername(@NotNull username: String): Mono<UserData>
    fun existsByUsername(@NotNull username: String): Mono<Boolean>
}