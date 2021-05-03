package br.com.kafkautils.kafka.clients

import br.com.kafkautils.kafka.cluster.model.Cluster
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig
import javax.inject.Singleton

@Singleton
class AdminClientFactory {

    fun build(cluster: Cluster): AdminClient {
        val conf: MutableMap<String, Any> = HashMap()
        conf[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = cluster.brokers.joinToString(",")
        conf[AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG] = cluster.requestTimeoutMs.toString()
        return AdminClient.create(conf)
    }
}
