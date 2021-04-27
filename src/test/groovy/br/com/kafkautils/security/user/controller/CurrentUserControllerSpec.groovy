package br.com.kafkautils.security.user.controller

import br.com.kafkautils.IntegrationSpec
import br.com.kafkautils.security.mock.MockAccessTokenProvider
import br.com.kafkautils.security.user.controller.dto.UpdateUserPasswordDto
import br.com.kafkautils.security.user.model.User
import br.com.kafkautils.security.user.service.UserService
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MutableHttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.spock.annotation.MicronautTest

import javax.inject.Inject

@MicronautTest(transactional = false)
class CurrentUserControllerSpec extends IntegrationSpec {

	@Inject
	@Client('/api/current-user')
	private HttpClient client

	@Inject
	private UserService userService

	@Inject
	MockAccessTokenProvider accessTokenProvider

	void "Update password"() {
		given:
		User userBefore = userService.getByUsername('viewer').blockOptional().get()
		UpdateUserPasswordDto user = new UpdateUserPasswordDto(
				'newPass'
		)
		String accessToken = accessTokenProvider.viewerAccessToken
		MutableHttpRequest request = HttpRequest.PUT('/password', user)
				.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
		when:
		HttpResponse result = client.toBlocking().exchange(request)
		User userAfter = userService.getByUsername('viewer').blockOptional().get()
		then:
		result.status() == HttpStatus.OK
		userBefore.password != userAfter.password
	}
}
