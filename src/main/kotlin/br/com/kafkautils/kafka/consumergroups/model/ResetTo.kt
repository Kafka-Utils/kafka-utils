package br.com.kafkautils.kafka.consumergroups.model

import javax.validation.constraints.Pattern

data class ResetTo(
    override val topic: String,
    override val partition: Int,
    @field: Pattern(regexp = "earliest|latest") val to: String
) : TopicToResetOffset
