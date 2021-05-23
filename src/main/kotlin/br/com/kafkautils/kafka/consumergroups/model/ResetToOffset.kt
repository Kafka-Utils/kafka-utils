package br.com.kafkautils.kafka.consumergroups.model

data class ResetToOffset(
    override val topic: String,
    override val partition: Int,
    val offset: Long
) : TopicToResetOffset
