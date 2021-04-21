package br.com.kafkautils.security.user

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

@Introspected
data class UpdateUserPasswordCommand(
    @field: NotBlank
    @field: Size(min = 6, max = 100)
    val password: String
)
