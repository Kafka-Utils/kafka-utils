package br.com.kafkautils.kafka.consumergroups.model

data class ConsumerGroupTopicPartitionOffset(
    val partition: Int,
    val offset: Long,
    val lastOffset: Long,
    val lag: Long,
    val leaderEpoch: Int?
)
