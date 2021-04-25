package br.com.kafkautils.kafka.cluster.controller.command

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Introspected
data class ClusterCommand(
    @field: NotBlank
    val name: String,
    @field: Size(min = 1)
    val brokers: Set<String>
)
