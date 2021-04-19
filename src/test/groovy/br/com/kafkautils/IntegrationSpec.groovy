package br.com.kafkautils

import io.micronaut.test.support.TestPropertyProvider
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import spock.lang.Shared
import spock.lang.Specification

class IntegrationSpec extends Specification implements TestPropertyProvider {

	@Shared
	private PostgreSQLContainer postgreSQLContainer

	void cleanupSpec() {
		postgreSQLContainer?.stop()
	}

	@Override
	Map<String, String> getProperties() {
		postgreSQLContainer = new PostgreSQLContainer(DockerImageName.parse("postgres:13"))
				.withDatabaseName('kafka_utils')
				.withUsername('postgres')
				.withPassword('postgres')
		postgreSQLContainer.start()
		Map<String, String> properties = [
				'database.host'    : postgreSQLContainer.containerIpAddress,
				'database.port'    : postgreSQLContainer.getMappedPort(5432),
				'database.username': postgreSQLContainer.username,
				'database.password': postgreSQLContainer.password,

		]
		return properties
	}

}
