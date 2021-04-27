package br.com.kafkautils.security.user.controller

import br.com.kafkautils.http.DefaultErrorResponses
import br.com.kafkautils.security.user.controller.dto.NewUserDto
import br.com.kafkautils.security.user.controller.dto.UpdateUserDto
import br.com.kafkautils.security.user.controller.dto.UpdateUserPasswordDto
import br.com.kafkautils.security.user.controller.dto.UserDto
import br.com.kafkautils.security.user.service.UserService
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

@Controller("/user")
@Secured("ADMIN")
open class UserController(
    private val userService: UserService,
    private val userMapper: UserMapper
) {

    @Get("/")
    @DefaultErrorResponses
    open fun list(): Flux<UserDto> {
        return userService.list().map {
            userMapper.toDto(it)
        }
    }

    @Get("/{id}")
    @DefaultErrorResponses
    open fun get(@PathVariable id: Int): Mono<UserDto> {
        return userService.get(id).map {
            userMapper.toDto(it)
        }
    }

    @Post("/")
    @DefaultErrorResponses
    open fun add(@Valid @Body dto: NewUserDto): Mono<UserDto> {
        val user = userMapper.toDomain(dto)
        return userService.add(user).map {
            userMapper.toDto(it)
        }
    }

    @Put("/{id}")
    @DefaultErrorResponses
    open fun update(@PathVariable id: Int, @Valid @Body dto: UpdateUserDto): Mono<UserDto> {
        return userService.get(id).flatMap { user ->
            val userToUpdate = userMapper.updateFromCommand(dto, user)
            userService.update(userToUpdate).map {
                userMapper.toDto(it)
            }
        }
    }

    @Put("/{id}/password")
    @DefaultErrorResponses
    open fun updatePassword(
        @PathVariable id: Int,
        @Valid @Body dto: UpdateUserPasswordDto
    ): Mono<Void> {
        return userService.updatePassword(id, dto.password)
    }
}
