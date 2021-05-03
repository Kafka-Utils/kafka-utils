package br.com.kafkautils.kafka.topic.model

import javax.validation.constraints.NotBlank

data class UpdateTopicConfig(
    @field: NotBlank val name: String,
    val config: TopicConfig
) {

    fun configMap(): Map<String, String> {
        return mapOf(
            "cleanup.policy" to config.cleanupPolicy,
            "compression.type" to config.compressionType,
            "delete.retention.ms" to config.deleteRetentionMs.toString(),
            "file.delete.delay.ms" to config.fileDeleteDelayMs.toString(),
            "flush.messages" to config.flushMessages.toString(),
            "flush.ms" to config.flushMs.toString(),
            "follower.replication.throttled.replicas" to config.followerReplicationThrottledReplicas,
            "index.interval.bytes" to config.indexIntervalBytes.toString(),
            "leader.replication.throttled.replicas" to config.leaderReplicationThrottledReplicas,
            "max.compaction.lag.ms" to config.maxCompactionLagMs.toString(),
            "max.message.bytes" to config.maxMessageBytes.toString(),
            "message.format.version" to config.messageFormatVersion,
            "message.timestamp.difference.max.ms" to config.messageTimestampDifferenceMaxMs.toString(),
            "message.timestamp.type" to config.messageTimestampType,
            "min.cleanable.dirty.ratio" to config.minCleanableDirtyRatio.toString(),
            "min.compaction.lag.ms" to config.minCompactionLagMs.toString(),
            "min.insync.replicas" to config.minInsyncReplicas.toString(),
            "preallocate" to config.preallocate.toString(),
            "retention.bytes" to config.retentionBytes.toString(),
            "retention.ms" to config.retentionMs.toString(),
            "segment.bytes" to config.segmentBytes.toString(),
            "segment.index.bytes" to config.segmentIndexBytes.toString(),
            "segment.jitter.ms" to config.segmentJitterMs.toString(),
            "segment.ms" to config.segmentMs.toString(),
            "unclean.leader.election.enable" to config.uncleanLeaderElectionEnable.toString(),
            "message.downconversion.enable" to config.messageDownconversionEnable.toString(),
        )
    }
}
