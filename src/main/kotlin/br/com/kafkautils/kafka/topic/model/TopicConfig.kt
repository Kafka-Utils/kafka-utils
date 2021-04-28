package br.com.kafkautils.kafka.topic.model

import java.math.BigDecimal

data class TopicConfig(
    val name: String,
    val cleanupPolicy: String?,
    val compressionType: String?,
    val deleteRetentionMs: Long?,
    val fileDeleteDelayMs: Long?,
    val flushMessages: Long?,
    val flushMs: Long?,
    val followerReplicationThrottledReplicas: String?,
    val indexIntervalBytes: Int?,
    val leaderReplicationThrottledReplicas: String?,
    val maxCompactionLagMs: Long?,
    val maxMessageBytes: Int?,
    val messageFormatVersion: String?,
    val messageTimestampDifferenceMaxMs: Long?,
    val messageTimestampType: String?,
    val minCleanableDirtyRatio: BigDecimal?,
    val minCompactionLagMs: Long?, // min.compaction.lag.ms

)