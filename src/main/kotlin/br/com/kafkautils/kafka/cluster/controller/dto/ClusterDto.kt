package br.com.kafkautils.kafka.cluster.controller.dto

import io.micronaut.core.annotation.Introspected
import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Introspected
@Schema(name = "Cluster")
data class ClusterDto(
    val id: Int,
    @field: NotBlank
    val name: String,
    @field: Size(min = 1)
    val brokers: Set<String>,
    val requestTimeoutMs: Long
)
