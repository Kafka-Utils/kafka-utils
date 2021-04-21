package br.com.kafkautils.security.user

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

@Introspected
data class UpdateUserCommand(
    @field: NotBlank
    val name: String,
    val role: Role,
    val active: Boolean
)
