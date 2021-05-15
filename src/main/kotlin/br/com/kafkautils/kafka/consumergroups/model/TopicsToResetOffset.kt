package br.com.kafkautils.kafka.consumergroups.model

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class TopicsToResetOffset<out T : TopicToResetOffset>(
    @field: NotBlank
    val groupId: String,
    @field: Size(min = 1)
    val topicsAndOffsets: Set<T>
)
