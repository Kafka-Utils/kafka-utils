package br.com.kafkautils.security.user

import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

@MappedEntity
data class UserData(
    @field: Id
    @field: GeneratedValue
    var id: Int? = null,
    @field: NotBlank
    @field: Size(min = 3, max = 100)
    @field: Pattern(regexp = "[a-z][\\w_.]{2,}")
    var username: String,
    @field: NotBlank
    @field: Size(min = 6, max = 100)
    var password: String,
    @field: NotBlank
    var name: String,
    var role: Role,
    var active: Boolean
)
