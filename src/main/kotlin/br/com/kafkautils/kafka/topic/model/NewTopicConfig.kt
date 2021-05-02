package br.com.kafkautils.kafka.topic.model

import java.math.BigDecimal
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import org.apache.kafka.clients.admin.Config

data class NewTopicConfig(
    @field: NotBlank val name: String,
    @field: NotNull val numPartitions: Int,
    @field: NotNull val replicationFactor: Short,
    val cleanupPolicy: String? = null, //cleanup.policy
    val compressionType: String? = null, //compression.type
    val deleteRetentionMs: Long? = null, //delete.retention.ms
    val fileDeleteDelayMs: Long? = null, //file.delete.delay.ms
    val flushMessages: Long? = null, //flush.messages
    val flushMs: Long? = null, //flush.ms
    val followerReplicationThrottledReplicas: String? = null, //follower.replication.throttled.replicas
    val indexIntervalBytes: Int? = null, //index.interval.bytes
    val leaderReplicationThrottledReplicas: String? = null, //leader.replication.throttled.replicas
    val maxCompactionLagMs: Long? = null, //max.compaction.lag.ms
    val maxMessageBytes: Int? = null, //max.message.bytes
    val messageFormatVersion: String? = null, //message.format.version
    val messageTimestampDifferenceMaxMs: Long? = null, //message.timestamp.difference.max.ms
    val messageTimestampType: String? = null, //message.timestamp.type
    val minCleanableDirtyRatio: BigDecimal? = null, //min.cleanable.dirty.ratio
    val minCompactionLagMs: Long? = null, //min.compaction.lag.ms
    val minInsyncReplicas: Int? = null, //min.insync.replicas
    val preallocate: Boolean? = null, //preallocate
    val retentionBytes: Long? = null, //retention.bytes
    val retentionMs: Long? = null, //retention.ms
    val segmentBytes: Int? = null, //segment.bytes
    val segmentIndexBytes: Int? = null, //segment.index.bytes
    val segmentJitterMs: Long? = null, //segment.jitter.ms
    val segmentMs: Long? = null, //segment.ms
    val uncleanLeaderElectionEnable: Boolean? = null, //unclean.leader.election.enable
    val messageDownconversionEnable: Boolean? = null, //message.downconversion.enable
) {

    constructor(
        name: String,
        numPartitions: Int,
        replicationFactor: Short
    ) : this(name, numPartitions, replicationFactor, null)

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