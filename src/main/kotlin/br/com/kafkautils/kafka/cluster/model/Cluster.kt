package br.com.kafkautils.kafka.cluster.model

import io.micronaut.data.annotation.DateCreated
import io.micronaut.data.annotation.DateUpdated
import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.TypeDef
import io.micronaut.data.model.DataType
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class Cluster(
    @field: Id
    @field: GeneratedValue
    var id: Int? = null,
    @field: DateCreated
    var dateCreated: LocalDateTime?,
    @field: DateUpdated
    var dateUpdated: LocalDateTime?,
    @field: NotBlank
    @field: Size(max = 255)
    val name: String,
    @field: Size(min = 1)
    @field: TypeDef(type = DataType.JSON)
    val brokers: Set<String>
)