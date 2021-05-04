package br.com.kafkautils.security.user.controller.dto

import br.com.kafkautils.security.user.model.Role
import io.micronaut.core.annotation.Introspected
import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

@Introspected
@Schema(name = "NewUser")
data class NewUserDto(
    @field: NotBlank
    @field: Size(min = 3, max = 100)
    @field: Pattern(regexp = "[a-z][\\w_.]{2,}")
    val username: String,
    @field: NotBlank
    @field: Size(min = 6, max = 100)
    val password: String,
    @field: NotBlank
    val name: String,
    val role: Role,
    val active: Boolean
)
