package br.com.kafkautils

import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName
import spock.lang.Shared

class KafkaIntegrationSpec extends IntegrationSpec {

	@Shared
	KafkaContainer kafka

	void cleanupSpec() {
		kafka?.stop()
	}

	@Override
	Map<String, String> getProperties() {
		String kafkaImage = System.getenv("KAFKA_CONTAINER_IMAGE") ?: "confluentinc/cp-kafka:5.4.3"
		kafka = new KafkaContainer(DockerImageName.parse(kafkaImage))
		kafka.start()
		return super.properties
	}

}
