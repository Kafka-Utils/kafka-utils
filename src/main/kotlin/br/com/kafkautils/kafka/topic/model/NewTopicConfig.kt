package br.com.kafkautils.kafka.topic.model

import java.math.BigDecimal
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import org.apache.kafka.clients.admin.Config

data class NewTopicConfig(
    @field: NotBlank val name: String,
    @field: NotNull val numPartitions: Int,
    @field: NotNull val replicationFactor: Short,
    val cleanupPolicy: String?, //cleanup.policy
    val compressionType: String?, //compression.type
    val deleteRetentionMs: Long?, //delete.retention.ms
    val fileDeleteDelayMs: Long?, //file.delete.delay.ms
    val flushMessages: Long?, //flush.messages
    val flushMs: Long?, //flush.ms
    val followerReplicationThrottledReplicas: String?, //follower.replication.throttled.replicas
    val indexIntervalBytes: Int?, //index.interval.bytes
    val leaderReplicationThrottledReplicas: String?, //leader.replication.throttled.replicas
    val maxCompactionLagMs: Long?, //max.compaction.lag.ms
    val maxMessageBytes: Int?, //max.message.bytes
    val messageFormatVersion: String?, //message.format.version
    val messageTimestampDifferenceMaxMs: Long?, //message.timestamp.difference.max.ms
    val messageTimestampType: String?, //message.timestamp.type
    val minCleanableDirtyRatio: BigDecimal?, //min.cleanable.dirty.ratio
    val minCompactionLagMs: Long?, //min.compaction.lag.ms
    val minInsyncReplicas: Int?, //min.insync.replicas
    val preallocate: Boolean?, //preallocate
    val retentionBytes: Long?, //retention.bytes
    val retentionMs: Long?, //retention.ms
    val segmentBytes: Int?, //segment.bytes
    val segmentIndexBytes: Int?, //segment.index.bytes
    val segmentJitterMs: Long?, //segment.jitter.ms
    val segmentMs: Long?, //segment.ms
    val uncleanLeaderElectionEnable: Boolean?, //unclean.leader.election.enable
    val messageDownconversionEnable: Boolean?, //message.downconversion.enable
) {

    fun configMap(): Map<String, String> {
        return mapOf(
            "cleanup.policy" to cleanupPolicy,
            "compression.type" to compressionType,
            "delete.retention.ms" to deleteRetentionMs,
            "file.delete.delay.ms" to fileDeleteDelayMs,
            "flush.messages" to flushMessages,
            "flush.ms" to flushMs,
            "follower.replication.throttled.replicas" to followerReplicationThrottledReplicas,
            "index.interval.bytes" to indexIntervalBytes,
            "leader.replication.throttled.replicas" to leaderReplicationThrottledReplicas,
            "max.compaction.lag.ms" to maxCompactionLagMs,
            "max.message.bytes" to maxMessageBytes,
            "message.format.version" to messageFormatVersion,
            "message.timestamp.difference.max.ms" to messageTimestampDifferenceMaxMs,
            "message.timestamp.type" to messageTimestampType,
            "min.cleanable.dirty.ratio" to minCleanableDirtyRatio,
            "min.compaction.lag.ms" to minCompactionLagMs,
            "min.insync.replicas" to minInsyncReplicas,
            "preallocate" to preallocate,
            "retention.bytes" to retentionBytes,
            "retention.ms" to retentionMs,
            "segment.bytes" to segmentBytes,
            "segment.index.bytes" to segmentIndexBytes,
            "segment.jitter.ms" to segmentJitterMs,
            "segment.ms" to segmentMs,
            "unclean.leader.election.enable" to uncleanLeaderElectionEnable,
            "message.downconversion.enable" to messageDownconversionEnable,
        ).entries.mapNotNull {
            if (it.value != null) it.key to it.value.toString() else null
        }.toMap()
    }
}