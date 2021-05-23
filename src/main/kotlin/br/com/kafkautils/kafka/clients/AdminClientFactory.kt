package br.com.kafkautils.kafka.clients

import br.com.kafkautils.kafka.cluster.model.Cluster
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Singleton

@Singleton
class AdminClientFactory {

    private val clientCache = ConcurrentHashMap<Int, AdminClient>()

    fun build(cluster: Cluster): AdminClient {
        return clientCache.getOrPut(cluster.id) {
            val conf: MutableMap<String, Any> = HashMap()
            conf[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = cluster.brokers()
            conf[AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG] = cluster.requestTimeoutMs.toString()
            AdminClient.create(conf)
        }
    }
}
