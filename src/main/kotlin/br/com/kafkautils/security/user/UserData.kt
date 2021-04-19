package br.com.kafkautils.security.user

import io.micronaut.data.annotation.AutoPopulated
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

@MappedEntity
data class UserData(
    @field: Id
    @field: AutoPopulated
    var id: UUID? = null,
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
