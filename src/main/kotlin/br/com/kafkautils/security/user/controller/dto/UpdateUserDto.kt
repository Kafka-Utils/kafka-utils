package br.com.kafkautils.security.user.controller.dto

import br.com.kafkautils.security.user.model.Role
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
data class UpdateUserDto(
    @field: NotBlank
    val name: String,
    val role: Role,
    val active: Boolean
)
