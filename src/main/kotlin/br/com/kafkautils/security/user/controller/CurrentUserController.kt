package br.com.kafkautils.security.user.controller

import br.com.kafkautils.http.DefaultErrorResponses
import br.com.kafkautils.security.user.controller.dto.UpdateUserPasswordDto
import br.com.kafkautils.security.user.controller.dto.UserDto
import br.com.kafkautils.security.user.service.UserService
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Put
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.micronaut.security.utils.SecurityService
import reactor.core.publisher.Mono
import javax.validation.Valid

@Controller("/current-user")
@Secured(SecurityRule.IS_AUTHENTICATED)
open class CurrentUserController(
    private val userService: UserService,
    private val userMapper: UserMapper,
    private val securityService: SecurityService
) {

    @Get("/")
    @DefaultErrorResponses
    open fun get(): Mono<UserDto> {
        val id = getCurrentUserId()
        return userService.get(id).map {
            userMapper.toDto(it)
        }
    }

    @Put("/password")
    @DefaultErrorResponses
    open fun updatePassword(
        @Valid @Body dto: UpdateUserPasswordDto
    ): Mono<Void> {
        val id = getCurrentUserId()
        return userService.updatePassword(id, dto.password)
    }

    private fun getCurrentUserId(): Int {
        return securityService.authentication.get().attributes["id"].toString().toInt()
    }
}
