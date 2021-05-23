package br.com.kafkautils.kafka.consumergroups.model

import java.time.ZonedDateTime

data class ResetToTime(
    override val topic: String,
    override val partition: Int,
    val time: ZonedDateTime
) : TopicToResetOffset
