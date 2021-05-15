package br.com.kafkautils.kafka.consumergroups.model

interface TopicToResetOffset {
    val topic: String
    val partition: Int
}