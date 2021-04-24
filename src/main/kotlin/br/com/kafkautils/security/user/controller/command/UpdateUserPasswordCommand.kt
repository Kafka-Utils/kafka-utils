package br.com.kafkautils.security.user.controller.command

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Introspected
data class UpdateUserPasswordCommand(
    @field: NotBlank
    @field: Size(min = 6, max = 100)
    val password: String
)
