package br.com.kafkautils.security.user.repository

import br.com.kafkautils.security.user.model.User
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.r2dbc.repository.ReactorCrudRepository
import reactor.core.publisher.Mono
import javax.validation.constraints.NotNull

@R2dbcRepository(dialect = Dialect.MYSQL)
interface UserRepository: ReactorCrudRepository<User, Int> {
    fun findByUsername(@NotNull username: String): Mono<User>
    fun existsByUsername(@NotNull username: String): Mono<Boolean>
}