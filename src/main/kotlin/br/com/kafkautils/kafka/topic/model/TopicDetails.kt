package br.com.kafkautils.kafka.topic.model

data class TopicDetails(
    val name: String,
    val internal: Boolean,
    val partitions: Set<PartitionInfo>,
    val topicConfig: TopicConfig
)
