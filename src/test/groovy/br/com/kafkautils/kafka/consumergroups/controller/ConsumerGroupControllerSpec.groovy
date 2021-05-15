package br.com.kafkautils.kafka.consumergroups.controller

import br.com.kafkautils.KafkaIntegrationSpec
import br.com.kafkautils.kafka.cluster.model.Cluster
import br.com.kafkautils.kafka.cluster.service.ClusterService
import br.com.kafkautils.kafka.consumer.service.ConsumerService
import br.com.kafkautils.kafka.consumergroups.model.ConsumerGroup
import br.com.kafkautils.kafka.consumergroups.model.ConsumerGroupTopic
import br.com.kafkautils.kafka.consumergroups.model.ToOffset
import br.com.kafkautils.kafka.consumergroups.model.TopicsToResetOffset
import br.com.kafkautils.kafka.consumergroups.service.ConsumerGroupService
import br.com.kafkautils.kafka.topic.model.NewTopicConfig
import br.com.kafkautils.kafka.topic.service.TopicService
import br.com.kafkautils.security.mock.MockAccessTokenProvider
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.core.type.Argument
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MutableHttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import spock.lang.Shared

import javax.inject.Inject
import java.time.Duration

@MicronautTest(transactional = false)
class ConsumerGroupControllerSpec extends KafkaIntegrationSpec {
	@Inject
	@Client('/api/cluster')
	private HttpClient client

	@Inject
	private MockAccessTokenProvider accessTokenProvider

	@Inject
	@Shared
	private ClusterService clusterService

	@Inject
	@Shared
	private TopicService topicService

	@Shared
	private Cluster cluster

	@Inject
	private ObjectMapper objectMapper

	@Shared
	@Inject
	private ConsumerService consumerService

	@Shared
	@Inject
	ConsumerGroupService consumerGroupService

	@Shared
	String topicName = 'topic.test'

	@Shared
	String consumerGroup1 = 'consumerGroup1'

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

		topicService.add(
				new NewTopicConfig(topicName, 2, 1 as Short),
				cluster
		).block()

		Properties producerProperties = new Properties()
		producerProperties[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafka.bootstrapServers
		producerProperties[ProducerConfig.CLIENT_ID_CONFIG] = 'client1'
		producerProperties[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer.name
		producerProperties[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer.name
		KafkaProducer<String, String> producer = new KafkaProducer<String, String>(producerProperties)

		(1..10).forEach {
			producer.send(new ProducerRecord<String, String>(topicName, it.toString(), it.toString()))
		}

		Consumer<String, String> consumer = consumerService.buildSimpleConsumer(cluster, consumerGroup1)
		consumer.subscribe([topicName])
		int count = 0
		while (count < 10) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1))
			if (records.count() > 0) {
				consumer.commitSync()
			}
			count += records.count()
		}

		(11..15).forEach {
			producer.send(new ProducerRecord<String, String>(topicName, it.toString(), it.toString()))
		}
		while (count < 15) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1))
			count += records.count()
		}
		producer.close()
		consumer.close()
	}

	def "List"() {
		given:
		String accessToken = accessTokenProvider.editorAccessToken
		MutableHttpRequest request = HttpRequest.GET("/${cluster.id}/consumer-group")
				.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
		when:
		List<ConsumerGroup> groups = client.toBlocking().retrieve(request, Argument.listOf(ConsumerGroup))
		then:
		groups.size() == 1
		groups*.groupId.toSet() == [consumerGroup1].toSet()
		groups*.simple.toSet() == [false].toSet()
		groups*.state.toSet() == [''].toSet()
	}

	def "Details"() {
		given:
		String accessToken = accessTokenProvider.editorAccessToken
		MutableHttpRequest request = HttpRequest.GET("/${cluster.id}/consumer-group/$consumerGroup1")
				.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
		when:
		List<ConsumerGroupTopic> consumerGroupTopics = client.toBlocking().retrieve(request, Argument.listOf(ConsumerGroupTopic))
		ConsumerGroupTopic consumerGroupTopic = consumerGroupTopics[0]
		then:
		consumerGroupTopics.size() == 1
		consumerGroupTopic.groupId == consumerGroup1
		consumerGroupTopic.topic == topicName
		consumerGroupTopic.partitionsOffsets.size() == 2
		consumerGroupTopic.partitionsOffsets*.offset.toSet() == [6L, 4L].toSet()
		consumerGroupTopic.partitionsOffsets*.lastOffset.toSet() == [8L, 7L].toSet()
		consumerGroupTopic.partitionsOffsets*.lag.toSet() == [3L, 2L].toSet()
	}

	def "reset using offset"() {
		given:
		TopicsToResetOffset topicsToResetOffset = new TopicsToResetOffset<ToOffset>(
				consumerGroup1,
				[
				        new ToOffset(topicName, 0, 0),
				        new ToOffset(topicName, 1, 0),
				].toSet()
		)
		String accessToken = accessTokenProvider.editorAccessToken
		MutableHttpRequest request = HttpRequest.PUT("/${cluster.id}/consumer-group/$consumerGroup1/offset/offsets", topicsToResetOffset)
				.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
		when:
		HttpResponse response = client.toBlocking().exchange(request)
		List<ConsumerGroupTopic> consumerGroupTopics = consumerGroupService.details(consumerGroup1, cluster).collectList().block()
		ConsumerGroupTopic consumerGroupTopic = consumerGroupTopics[0]
		then:
		response.status() == HttpStatus.OK
		consumerGroupTopics.size() == 1
		consumerGroupTopic.groupId == consumerGroup1
		consumerGroupTopic.topic == topicName
		consumerGroupTopic.partitionsOffsets.size() == 2
		consumerGroupTopic.partitionsOffsets*.offset.toSet() == [0L, 0L].toSet()
		consumerGroupTopic.partitionsOffsets*.lastOffset.toSet() == [8L, 7L].toSet()
		consumerGroupTopic.partitionsOffsets*.lag.toSet() == [8L, 7L].toSet()
	}

	def "reset using shift"() {
		setup:
		TopicsToResetOffset topicsToResetOffset = new TopicsToResetOffset<ToOffset>(
				consumerGroup1,
				[
						new ToOffset(topicName, 0, 6),
						new ToOffset(topicName, 1, 4),
				].toSet()
		)
		consumerGroupService.resetOffsetToOffset(cluster, topicsToResetOffset).block()
		topicsToResetOffset = new TopicsToResetOffset<ToOffset>(
				consumerGroup1,
				[
				        new ToOffset(topicName, 0, -2),
				        new ToOffset(topicName, 1, 2),
				].toSet()
		)
		String accessToken = accessTokenProvider.editorAccessToken
		MutableHttpRequest request = HttpRequest.PUT("/${cluster.id}/consumer-group/$consumerGroup1/offset/shift", topicsToResetOffset)
				.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
		when:
		HttpResponse response = client.toBlocking().exchange(request)
		List<ConsumerGroupTopic> consumerGroupTopics = consumerGroupService.details(consumerGroup1, cluster).collectList().block()
		ConsumerGroupTopic consumerGroupTopic = consumerGroupTopics[0]
		then:
		response.status() == HttpStatus.OK
		consumerGroupTopics.size() == 1
		consumerGroupTopic.groupId == consumerGroup1
		consumerGroupTopic.topic == topicName
		consumerGroupTopic.partitionsOffsets.size() == 2
		consumerGroupTopic.partitionsOffsets*.offset.toSet() == [4L, 6L].toSet()
		consumerGroupTopic.partitionsOffsets*.lastOffset.toSet() == [8L, 7L].toSet()
		consumerGroupTopic.partitionsOffsets*.lag.toSet() == [2L, 3L].toSet()
	}
}
