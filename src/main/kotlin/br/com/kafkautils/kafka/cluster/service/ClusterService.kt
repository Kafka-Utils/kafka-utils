package br.com.kafkautils.kafka.cluster.service

import br.com.kafkautils.exceptions.ConflictException
import br.com.kafkautils.i18n.Messages
import br.com.kafkautils.kafka.cluster.model.Cluster
import br.com.kafkautils.kafka.cluster.repository.CusterRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Singleton
@Transactional
open class ClusterService(
    private val custerRepository: CusterRepository,
    private val messages: Messages
) {

    open fun add(@Valid cluster: Cluster): Mono<Cluster> {
        return custerRepository.existsByName(cluster.name).flatMap { existsCluster ->
            if (existsCluster) {
                val msg = messages.getMessage("name.already.in.use", mapOf("name" to cluster.name))
                Mono.error(ConflictException(msg))
            } else {
                custerRepository.save(cluster)
            }
        }
    }

    open fun update(@Valid cluster: Cluster): Mono<Cluster> {
        return custerRepository.update(cluster)
    }

    open fun get(id: Int): Mono<Cluster> {
        return custerRepository.findById(id)
    }

    open fun list(): Flux<Cluster> {
        return custerRepository.findAll()
    }
}
