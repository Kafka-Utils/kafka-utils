package br.com.kafkautils.kafka.consumergroups.model

data class ConsumerGroupTopic(
    val groupId: String,
    val topic: String,
    val partitionsOffsets: Set<ConsumerGroupTopicPartitionOffset>
)
