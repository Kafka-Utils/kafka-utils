package br.com.kafkautils.kafka.topic.controller

import br.com.kafkautils.KafkaIntegrationSpec
import br.com.kafkautils.kafka.cluster.model.Cluster
import br.com.kafkautils.kafka.cluster.service.ClusterService
import br.com.kafkautils.kafka.topic.model.NewTopicConfig
import br.com.kafkautils.security.mock.MockAccessTokenProvider
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MutableHttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import spock.lang.Shared
import spock.lang.Stepwise

import javax.inject.Inject

@Stepwise
@MicronautTest(transactional = false)
class TopicControllerSpec extends KafkaIntegrationSpec {

	@Inject
	@Client('/api/cluster')
	private HttpClient client

	@Inject
	private MockAccessTokenProvider accessTokenProvider

	@Inject
	@Shared
	ClusterService clusterService

	@Shared
	Cluster cluster

	void setupSpec() {
		cluster = new Cluster(
				null,
				null,
				null,
				'Test',
				[kafka.bootstrapServers].toSet(),
				5000
		)
		cluster = clusterService.add(cluster).block()
	}

	void "add new topic test"() {
		given:
		NewTopicConfig newTopic = new NewTopicConfig(
				'topic.test',
				1,
				1 as short
		)
		String accessToken = accessTokenProvider.editorAccessToken
		MutableHttpRequest request = HttpRequest.POST("/${cluster.id}/topic", newTopic)
				.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
		when:
		HttpResponse httpRequest = client.toBlocking().exchange(request)
		then:
		httpRequest.status() == HttpStatus.OK
	}
}
