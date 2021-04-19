package br.com.kafkautils.security.user

import io.micronaut.http.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*
import javax.validation.Valid

@Controller("/user")
//@Secured("ADMIN") TODO KU-9
open class UserController(private val userRepository: UserRepository) {

    @Get("/")
    open fun list(): Flux<UserData> {
        return userRepository.findAll()
    }

    @Get("/{id}")
    open fun get(@PathVariable id: UUID): Mono<UserData> {
        return userRepository.findById(id)
    }

    @Post("/")
    open fun add(@Valid @Body userDataMono: Mono<UserData>): Mono<UserData> {
        return userDataMono.flatMap {
            userRepository.save(it)
        }
    }

    @Put("/{id}")
    open fun update(@PathVariable id: UUID, @Valid @Body userDataMono: Mono<UserData>): Mono<UserData> {
        return userDataMono.flatMap {
            it.id = id
            userRepository.update(it)
        }
    }
}