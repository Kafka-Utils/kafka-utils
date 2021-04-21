package br.com.kafkautils.security.user

import io.micronaut.http.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.validation.Valid

@Controller("/user")
//@Secured("ADMIN") TODO KU-9
open class UserController(
    private val userService: UserService,
    private val userMapper: UserMapper
) {

    @Get("/")
    open fun list(): Flux<UserDto> {
        return userService.list().map {
            userMapper.toDto(it)
        }
    }

    @Get("/{id}")
    open fun get(@PathVariable id: Int): Mono<UserDto> {
        return userService.get(id).map {
            userMapper.toDto(it)
        }
    }

    @Post("/")
    open fun add(@Valid @Body command: NewUserCommand): Mono<UserDto> {
        val user = userMapper.toDomain(command)
        return userService.add(user).map {
            userMapper.toDto(it)
        }
    }

    @Put("/{id}")
    open fun update(@PathVariable id: Int, @Valid @Body command: UpdateUserCommand): Mono<UserDto> {
        return userService.get(id).flatMap { user ->
            val userToUpdate = userMapper.updateFromCommand(command, user)
            userService.update(userToUpdate).map {
                userMapper.toDto(it)
            }
        }
    }

    @Put("/{id}/password")
    open fun updatePassword(
        @PathVariable id: Int,
        @Valid @Body command: UpdateUserPasswordCommand
    ): Mono<Void> {
        return userService.updatePassword(id, command.password)
    }

}