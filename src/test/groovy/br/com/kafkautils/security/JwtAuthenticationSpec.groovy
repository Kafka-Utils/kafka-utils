package br.com.kafkautils.security

import br.com.kafkautils.IntegrationSpec
import br.com.kafkautils.security.user.controller.dto.UserDto
import com.nimbusds.jwt.JWTParser
import com.nimbusds.jwt.SignedJWT
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.security.authentication.UsernamePasswordCredentials
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken
import io.micronaut.test.extensions.spock.annotation.MicronautTest

import javax.inject.Inject

@MicronautTest
class JwtAuthenticationSpec extends IntegrationSpec {

	@Inject
	@Client('/api')
	RxHttpClient client

	def "Verify JWT authentication works"() {
		when: 'Accessing a secured URL without authenticating'
		client.toBlocking().exchange(HttpRequest.GET('/current-user',))

		then: 'returns unauthorized'
		HttpClientResponseException e = thrown(HttpClientResponseException)
		e.status == HttpStatus.UNAUTHORIZED

		when: 'Login endpoint is called with valid credentials'
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials('admin', 'admin')
		HttpRequest request = HttpRequest.POST('/login', creds)
		HttpResponse<BearerAccessRefreshToken> rsp = client.toBlocking().exchange(request, BearerAccessRefreshToken)

		then: 'the endpoint can be accessed'
		noExceptionThrown()
		rsp.status == HttpStatus.OK
		rsp.body().username == 'admin'
		rsp.body().accessToken
		JWTParser.parse(rsp.body().accessToken) instanceof SignedJWT

		when:
		String accessToken = rsp.body().accessToken
		HttpRequest requestWithAuthorization = HttpRequest.GET('/current-user').header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
		HttpResponse<UserDto> response = client.toBlocking().exchange(requestWithAuthorization, UserDto)

		then:
		noExceptionThrown()
		response.status == HttpStatus.OK
		response.body().username == 'admin'
	}

	def "Try login with incorrect password"() {
		when:
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials('admin', 'abcd')
		HttpRequest request = HttpRequest.POST('/login', creds)
		client.toBlocking().exchange(request, BearerAccessRefreshToken)

		then:
		HttpClientResponseException exception = thrown()
		exception.response.status() == HttpStatus.UNAUTHORIZED
	}

	def "Try login with nonexistent user"() {
		when:
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials('abcd', 'abcd')
		HttpRequest request = HttpRequest.POST('/login', creds)
		client.toBlocking().exchange(request, BearerAccessRefreshToken)

		then:
		HttpClientResponseException exception = thrown()
		exception.response.status() == HttpStatus.UNAUTHORIZED
	}
}
