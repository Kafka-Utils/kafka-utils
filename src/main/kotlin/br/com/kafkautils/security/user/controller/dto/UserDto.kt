package br.com.kafkautils.security.user.controller.dto

import br.com.kafkautils.security.user.model.Role
import io.micronaut.core.annotation.Introspected
import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotBlank

@Introspected
@Schema(name = "User")
data class UserDto(
    val id: Int,
    @field: NotBlank
    val username: String,
    @field: NotBlank
    val name: String,
    val role: Role,
    val active: Boolean
)
