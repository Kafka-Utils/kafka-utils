package br.com.kafkautils.kafka.cluster.controller

import br.com.kafkautils.IntegrationSpec
import br.com.kafkautils.kafka.cluster.controller.dto.ClusterCommandDto
import br.com.kafkautils.kafka.cluster.controller.dto.ClusterDto
import br.com.kafkautils.security.mock.MockAccessTokenProvider
import io.micronaut.core.type.Argument
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.MutableHttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import spock.lang.Stepwise

import javax.inject.Inject

@Stepwise
@MicronautTest
class ClusterControllerSpec extends IntegrationSpec {

	@Inject
	@Client('/api/cluster')
	private HttpClient client

	@Inject
	private MockAccessTokenProvider accessTokenProvider

	void "Add"() {
		ClusterCommandDto dto = new ClusterCommandDto(
				'cluster',
				['localhost'].toSet(),
				5000
		)
		String accessToken = accessTokenProvider.editorAccessToken
		MutableHttpRequest request = HttpRequest.POST('/', dto)
				.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
		when:
		ClusterDto result = client.toBlocking().retrieve(request, ClusterDto)
		then:
		result.name == dto.name
		result.brokers == dto.brokers
	}

	void "Update"() {
		ClusterCommandDto dto = new ClusterCommandDto(
				'cluster a',
				['localhost:19092', 'localhost:9092'].toSet(),
				10000
		)
		String accessToken = accessTokenProvider.editorAccessToken
		MutableHttpRequest request = HttpRequest.PUT('/1', dto)
				.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
		when:
		ClusterDto result = client.toBlocking().retrieve(request, ClusterDto)
		then:
		result.name == dto.name
		result.brokers == dto.brokers
	}

	void "List"() {
		String accessToken = accessTokenProvider.editorAccessToken
		MutableHttpRequest request = HttpRequest.GET('/')
				.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
		when:
		List<ClusterDto> result = client.toBlocking().retrieve(request, Argument.listOf(ClusterDto))
		then:
		result*.name == ['cluster a']
		result*.brokers.flatten().toSet() == ['localhost:19092', 'localhost:9092'].toSet()
	}

	void "Get"() {
		String accessToken = accessTokenProvider.editorAccessToken
		MutableHttpRequest request = HttpRequest.GET('/1')
				.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
		when:
		ClusterDto result = client.toBlocking().retrieve(request, ClusterDto)
		then:
		result.name == 'cluster a'
		result.brokers == ['localhost:19092', 'localhost:9092'].toSet()
	}

	void "try add with viwer"() {
		ClusterCommandDto dto = new ClusterCommandDto(
				'cluster',
				['localhost'].toSet(),
				5000
		)
		String accessToken = accessTokenProvider.viewerAccessToken
		MutableHttpRequest request = HttpRequest.POST('/', dto)
				.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
		when:
		client.toBlocking().exchange(request)
		then:
		HttpClientResponseException exception = thrown()
		exception.response.status() == HttpStatus.FORBIDDEN
	}

	void "try update with viwer"() {
		ClusterCommandDto dto = new ClusterCommandDto(
				'cluster a',
				['localhost:19092', 'localhost:9092'].toSet(),
				10000
		)
		String accessToken = accessTokenProvider.viewerAccessToken
		MutableHttpRequest request = HttpRequest.PUT('/1', dto)
				.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
		when:
		client.toBlocking().exchange(request)
		then:
		HttpClientResponseException exception = thrown()
		exception.response.status() == HttpStatus.FORBIDDEN
	}

	void "try list with viwer"() {
		String accessToken = accessTokenProvider.viewerAccessToken
		MutableHttpRequest request = HttpRequest.GET('/')
				.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
		when:
		client.toBlocking().exchange(request)
		then:
		notThrown(HttpClientResponseException)
	}

	void "try get with viwer"() {
		String accessToken = accessTokenProvider.viewerAccessToken
		MutableHttpRequest request = HttpRequest.GET('/1')
				.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
		when:
		client.toBlocking().exchange(request)
		then:
		notThrown(HttpClientResponseException)
	}
}
