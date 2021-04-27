package br.com.kafkautils.kafka.cluster.controller

import br.com.kafkautils.kafka.cluster.controller.dto.ClusterCommandDto
import br.com.kafkautils.kafka.cluster.model.Cluster
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "jsr330", unmappedTargetPolicy = ReportingPolicy.IGNORE)
abstract class ClusterMapper {
    abstract fun toDto(cluster: Cluster): ClusterCommandDto
    abstract fun toDomain(commandDto: ClusterCommandDto): Cluster
    fun updateFromCommand(commandDto: ClusterCommandDto, user: Cluster): Cluster {
        return user.copy(
            name = commandDto.name,
            brokers = commandDto.brokers
        )
    }
}
