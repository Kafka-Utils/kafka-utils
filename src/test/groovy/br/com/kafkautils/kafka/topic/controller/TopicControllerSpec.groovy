package br.com.kafkautils.kafka.topic.controller

import br.com.kafkautils.KafkaIntegrationSpec
import br.com.kafkautils.kafka.cluster.model.Cluster
import br.com.kafkautils.kafka.cluster.service.ClusterService
import br.com.kafkautils.kafka.topic.model.NewPartition
import br.com.kafkautils.kafka.topic.model.NewTopicConfig
import br.com.kafkautils.kafka.topic.model.Topic
import br.com.kafkautils.kafka.topic.model.TopicDescription
import br.com.kafkautils.security.mock.MockAccessTokenProvider
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.core.type.Argument
import io.micronaut.http.*
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import spock.lang.Shared
import spock.lang.Stepwise

import javax.inject.Inject

@Stepwise
@MicronautTest(transactional = false)
class TopicControllerSpec extends KafkaIntegrationSpec {

	private static final String CONFLUENT_SUPPORT_METRICS = '__confluent.support.metrics'

	@Inject
	@Client('/api/cluster')
	private HttpClient client

	@Inject
	private MockAccessTokenProvider accessTokenProvider

	@Inject
	@Shared
	private ClusterService clusterService

	@Shared
	private Cluster cluster

	@Inject
	private ObjectMapper objectMapper

	String topicName =  'topic.test'

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
				topicName,
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

	void "get new topic test"() {
		given:
		String accessToken = accessTokenProvider.editorAccessToken
		MutableHttpRequest request = HttpRequest.GET("/${cluster.id}/topic/$topicName")
				.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
		when:
		TopicDescription topicDescription = client.toBlocking().retrieve(request, TopicDescription)
		then:
		topicDescription
		topicDescription.name == topicName
		topicDescription.partitions.size() == 1
		!topicDescription.internal
		topicDescription.topicConfig.cleanupPolicy == 'delete'
	}

	void "update topic configuration"() {
		given:
		String accessToken = accessTokenProvider.editorAccessToken
		MutableHttpRequest request = HttpRequest.GET("/${cluster.id}/topic/$topicName")
				.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
		when:
		TopicDescription topicDescription = client.toBlocking().retrieve(request, TopicDescription)
		then:
		topicDescription
		when:
		Map topicConfig = objectMapper.convertValue(topicDescription.topicConfig, Map)
		topicConfig.cleanupPolicy = 'compact'
		MutableHttpRequest requestUpdate = HttpRequest.PUT("/${cluster.id}/topic/$topicName", topicConfig)
				.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
		client.toBlocking().exchange(requestUpdate)
		request = HttpRequest.GET("/${cluster.id}/topic/$topicName")
				.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
		topicDescription = client.toBlocking().retrieve(request, TopicDescription)
		then:
		topicDescription.topicConfig.cleanupPolicy == 'compact'
	}

	void "add partition in topic"() {
		given:
		NewPartition newPartition = new NewPartition(topicName, 2)
		String accessToken = accessTokenProvider.editorAccessToken
		MutableHttpRequest request = HttpRequest.PATCH("/${cluster.id}/topic/partitions", newPartition)
				.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
		when:
		client.toBlocking().exchange(request)
		request = HttpRequest.GET("/${cluster.id}/topic/$topicName")
				.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
		TopicDescription topicDescription = client.toBlocking().retrieve(request, TopicDescription)
		then:
		topicDescription.name == topicName
		topicDescription.partitions.size() == 2
	}

	void "list topics"() {
		given:
		NewTopicConfig newTopic = new NewTopicConfig(
				"$topicName-2",
				1,
				1 as short
		)
		String accessToken = accessTokenProvider.editorAccessToken
		MutableHttpRequest request = HttpRequest.POST("/${cluster.id}/topic", newTopic)
				.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
		when:
		client.toBlocking().exchange(request)
		request = HttpRequest.GET("/${cluster.id}/topic")
				.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
		List<Topic> topics = client.toBlocking().retrieve(request, Argument.listOf(Topic))
		then:
		topics.size() == 3
		topics*.name.toSet() == ["$topicName-2", topicName, CONFLUENT_SUPPORT_METRICS].toSet()
		topics*.internal.toSet() == [false].toSet()
	}

	void "delete topic"() {
		given:
		String accessToken = accessTokenProvider.editorAccessToken
		MutableHttpRequest request = HttpRequest.DELETE("/${cluster.id}/topic/$topicName-2")
				.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
		when:
		client.toBlocking().exchange(request)
		request = HttpRequest.GET("/${cluster.id}/topic")
				.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
		List<Topic> topics = client.toBlocking().retrieve(request, Argument.listOf(Topic))
		then:
		topics.size() == 2
		topics*.name.toSet() == [topicName, CONFLUENT_SUPPORT_METRICS].toSet()
		topics*.internal.toSet() == [false].toSet()
	}
}
