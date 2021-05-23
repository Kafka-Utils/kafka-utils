package br.com.kafkautils.kafka.consumergroups.model

data class ConsumerGroup(
    val groupId: String,
    val simple: Boolean,
    val state: String
)
