package br.com.kafkautils.security.user

import io.micronaut.core.annotation.Introspected
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*
import javax.validation.constraints.NotBlank

@Introspected
@Schema(name = "User")
data class UserDto(
    val id: UUID,
    @field: NotBlank
    val username: String,
    @field: NotBlank
    val name: String,
    val role: Role,
    val active: Boolean
)
