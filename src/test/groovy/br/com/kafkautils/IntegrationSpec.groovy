package br.com.kafkautils

import io.micronaut.test.support.TestPropertyProvider
import org.testcontainers.containers.MySQLContainer
import spock.lang.Shared
import spock.lang.Specification

class IntegrationSpec extends Specification implements TestPropertyProvider {

	@Shared
	private MySQLContainer container

	void cleanupSpec() {
		container?.stop()
	}

	@Override
	Map<String, String> getProperties() {
		container = new MySQLContainer()
				.withDatabaseName('kafka_utils')
				.withUsername('mysql')
				.withPassword('mysql')
				.withTmpFs(['/var/lib/mysql': 'rw']) as MySQLContainer
		container.start()
		Map<String, String> properties = [
				'dbcommons.host'    : container.containerIpAddress,
				'dbcommons.port'    : container.getMappedPort(3306).toString(),
				'dbcommons.username': 'mysql',
				'dbcommons.password': 'mysql',

		]
		return properties
	}

}
