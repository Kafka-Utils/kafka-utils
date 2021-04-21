package br.com.kafkautils.security.user

import br.com.kafkautils.IntegrationSpec
import io.micronaut.http.HttpRequest
import io.micronaut.http.MutableHttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.spock.annotation.MicronautTest

import javax.inject.Inject

@MicronautTest
class UserDataControllerIntegrationSpec extends IntegrationSpec {

	@Inject
	@Client('/api/user')
	private HttpClient client

	void "Add"() {
		given:
		UserData user = new UserData(
				null,
				'user',
				'123456',
				'User',
				Role.EDITOR,
				true
		)
		MutableHttpRequest request = HttpRequest.POST('/', user)
		when:
		UserData result = client.toBlocking().retrieve(request, UserData)
		then:
		noExceptionThrown()
		result
		result.id
	}

	void "Get"() {
		given:
		MutableHttpRequest request = HttpRequest.GET("/1")
		when:
		UserData result = client.toBlocking().retrieve(request, UserData)
		then:
		noExceptionThrown()
		result
	}

	void "Update"() {
	}

	void "List"() {
	}

}
