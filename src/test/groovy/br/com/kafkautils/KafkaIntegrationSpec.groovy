package br.com.kafkautils

import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName
import spock.lang.Shared

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

class KafkaIntegrationSpec extends IntegrationSpec {

	@Shared
	KafkaContainer kafka

	@Override
	void cleanupSpec() {
		kafka?.stop()
	}

	@Override
	Map<String, String> getProperties() {
		ExecutorService executorService = Executors.newFixedThreadPool(2)
		try {
			String kafkaImage = System.getenv('KAFKA_CONTAINER_IMAGE') ?: 'confluentinc/cp-kafka:5.4.3'
			kafka = new KafkaContainer(DockerImageName.parse(kafkaImage)).withTmpFs([
					'/var/lib/zookeeper/data': 'rw',
					'/etc/zookeeper/secrets': 'rw',
					'/var/lib/zookeeper/log': 'rw',
					'/var/lib/kafka/data': 'rw',
					'/etc/kafka/secrets': 'rw',
			]) as KafkaContainer
			Future future = executorService.submit {
				kafka.start()
			}
			Map<String, String> properties = executorService.submit({
				return super.properties
			} as Callable<Map<String, String>>).get()
			future.get()
			return properties
		} finally {
			executorService.shutdown()
		}
	}

}
