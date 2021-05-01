package br.com.kafkautils.kafka.topic.model

import java.math.BigDecimal
import org.apache.kafka.clients.admin.Config

data class TopicConfig(
    val cleanupPolicy: String, //cleanup.policy
    val compressionType: String, //compression.type
    val deleteRetentionMs: Long, //delete.retention.ms
    val fileDeleteDelayMs: Long, //file.delete.delay.ms
    val flushMessages: Long, //flush.messages
    val flushMs: Long, //flush.ms
    val followerReplicationThrottledReplicas: String, //follower.replication.throttled.replicas
    val indexIntervalBytes: Int, //index.interval.bytes
    val leaderReplicationThrottledReplicas: String, //leader.replication.throttled.replicas
    val maxCompactionLagMs: Long, //max.compaction.lag.ms
    val maxMessageBytes: Int, //max.message.bytes
    val messageFormatVersion: String, //message.format.version
    val messageTimestampDifferenceMaxMs: Long, //message.timestamp.difference.max.ms
    val messageTimestampType: String, //message.timestamp.type
    val minCleanableDirtyRatio: BigDecimal, //min.cleanable.dirty.ratio
    val minCompactionLagMs: Long, //min.compaction.lag.ms
    val minInsyncReplicas: Int, //min.insync.replicas
    val preallocate: Boolean, //preallocate
    val retentionBytes: Long, //retention.bytes
    val retentionMs: Long, //retention.ms
    val segmentBytes: Int, //segment.bytes
    val segmentIndexBytes: Int, //segment.index.bytes
    val segmentJitterMs: Long, //segment.jitter.ms
    val segmentMs: Long, //segment.ms
    val uncleanLeaderElectionEnable: Boolean, //unclean.leader.election.enable
    val messageDownconversionEnable: Boolean, //message.downconversion.enable
) {

    constructor(config: Config) : this(
        config.get("cleanup.policy").value(),
        config.get("compression.type").value(),
        config.get("delete.retention.ms").value().toLong(),
        config.get("file.delete.delay.ms").value().toLong(),
        config.get("flush.messages").value().toLong(),
        config.get("flush.ms").value().toLong(),
        config.get("follower.replication.throttled.replicas").value(),
        config.get("index.interval.bytes").value().toInt(),
        config.get("leader.replication.throttled.replicas").value(),
        config.get("max.compaction.lag.ms").value().toLong(),
        config.get("max.message.bytes").value().toInt(),
        config.get("message.format.version").value(),
        config.get("message.timestamp.difference.max.ms").value().toLong(),
        config.get("message.timestamp.type").value(),
        config.get("min.cleanable.dirty.ratio").value().toBigDecimal(),
        config.get("min.compaction.lag.ms").value().toLong(),
        config.get("min.insync.replicas").value().toInt(),
        config.get("preallocate").value().toBoolean(),
        config.get("retention.bytes").value().toLong(),
        config.get("retention.ms").value().toLong(),
        config.get("segment.bytes").value().toInt(),
        config.get("segment.index.bytes").value().toInt(),
        config.get("segment.jitter.ms").value().toLong(),
        config.get("segment.ms").value().toLong(),
        config.get("unclean.leader.election.enable").value().toBoolean(),
        config.get("message.downconversion.enable").value().toBoolean(),
    )
}