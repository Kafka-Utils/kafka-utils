package br.com.kafkautils.security.user.model

import io.micronaut.data.annotation.*
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

@MappedEntity
data class User(
    @field: Id
    @field: GeneratedValue
    var id: Int? = null,
    @field: DateCreated
    var dateCreated: LocalDateTime?,
    @field: DateUpdated
    var dateUpdated: LocalDateTime?,
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
