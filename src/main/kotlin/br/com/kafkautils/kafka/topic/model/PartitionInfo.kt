package br.com.kafkautils.kafka.topic.model

data class PartitionInfo(
    val partition: Int,
    val replicas: Int
)
