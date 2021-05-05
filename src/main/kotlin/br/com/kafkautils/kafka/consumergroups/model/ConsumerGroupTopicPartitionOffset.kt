package br.com.kafkautils.kafka.consumergroups.model

data class ConsumerGroupTopicPartitionOffset(
    val partition: Int,
    val offset: Long,
    val leaderEpoch: Int?
)
