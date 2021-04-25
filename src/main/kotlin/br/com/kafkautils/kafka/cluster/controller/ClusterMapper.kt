package br.com.kafkautils.kafka.cluster.controller

import br.com.kafkautils.kafka.cluster.controller.command.ClusterCommand
import br.com.kafkautils.kafka.cluster.controller.dto.ClusterDto
import br.com.kafkautils.kafka.cluster.model.Cluster
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "jsr330", unmappedTargetPolicy = ReportingPolicy.IGNORE)
abstract class ClusterMapper {
    abstract fun toDto(cluster: Cluster): ClusterDto
    abstract fun toDomain(command: ClusterCommand): Cluster
    fun updateFromCommand(command: ClusterCommand, user: Cluster): Cluster {
        return user.copy(
            name = command.name,
            brokers = command.brokers
        )
    }
}
