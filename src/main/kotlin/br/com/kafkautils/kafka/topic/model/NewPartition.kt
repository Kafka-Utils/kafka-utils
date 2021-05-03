package br.com.kafkautils.kafka.topic.model

import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank

data class NewPartition(
    @field: NotBlank val topic: String,
    @field: Min(1) val numPartitions: Int
)
