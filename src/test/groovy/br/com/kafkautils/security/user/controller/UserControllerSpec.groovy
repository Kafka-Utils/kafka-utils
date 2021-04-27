package br.com.kafkautils.security.user.controller

import br.com.kafkautils.IntegrationSpec
import br.com.kafkautils.http.handler.ResponseError
import br.com.kafkautils.http.handler.ValidationErrorList
import br.com.kafkautils.security.mock.MockAccessTokenProvider
import br.com.kafkautils.security.user.controller.dto.NewUserDto
import br.com.kafkautils.security.user.controller.dto.UpdateUserDto
import br.com.kafkautils.security.user.controller.dto.UpdateUserPasswordDto
import br.com.kafkautils.security.user.controller.dto.UserDto
import br.com.kafkautils.security.user.model.Role
import br.com.kafkautils.security.user.model.User
import br.com.kafkautils.security.user.service.UserService
import io.micronaut.core.type.Argument
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MutableHttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.spock.annotation.MicronautTest

import javax.inject.Inject

@MicronautTest(transactional = false)
class UserControllerSpec extends IntegrationSpec {

	@Inject
	@Client('/api/user')
	private HttpClient client

	@Inject
	private UserService userService

	@Inject
	MockAccessTokenProvider accessTokenProvider

	void "Add"() {
		given:
		NewUserDto user = new NewUserDto(
				'user',
				'123456',
				'User',
				Role.EDITOR,
				true
		)
		String accessToken = accessTokenProvider.adminAccessToken
		MutableHttpRequest request = HttpRequest.POST('/', user)
				.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
		when:
		UserDto result = client.toBlocking().retrieve(request, UserDto)
		then:
		result
		result.id
		result.username == user.username
		result.name == user.name
		result.role == user.role
		result.active == user.active
	}

	void "try add duplicate username"() {
		given:
		NewUserDto user = new NewUserDto(
				'admin',
				'123456',
				'admin 2',
				Role.EDITOR,
				true
		)
		String accessToken = accessTokenProvider.adminAccessToken
		MutableHttpRequest request = HttpRequest.POST('/', user)
				.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
		when:
		client.toBlocking().retrieve(request, UserDto)
		then:
		HttpClientResponseException exception = thrown()
		exception.response.status() == HttpStatus.CONFLICT
		exception.response.getBody(ResponseError).get().message == "Username ${user.username} is already in use!"
	}

	void "try add user with invalid body"() {
		given:
		Map user = [
				username: '1',
				password: '1',
				name    : '1',
				role    : Role.ADMIN,
				active  : true,
		]
		String accessToken = accessTokenProvider.adminAccessToken
		MutableHttpRequest request = HttpRequest.POST('/', user)
				.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
		when:
		client.toBlocking().retrieve(request, UserDto)
		then:
		HttpClientResponseException exception = thrown()
		exception.response.status() == HttpStatus.BAD_REQUEST
		exception.response.getBody(ValidationErrorList).get().message
		exception.response.getBody(ValidationErrorList).get().errors
	}

	void "Get"() {
		given:
		String accessToken = accessTokenProvider.adminAccessToken
		MutableHttpRequest request = HttpRequest.GET('/1')
				.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
		when:
		UserDto result = client.toBlocking().retrieve(request, UserDto)
		then:
		result.id == 1
		result.username == 'admin'
		result.name == 'Admin'
		result.role == Role.ADMIN
		result.active
	}

	void "Update"() {
		given:
		UpdateUserDto user = new UpdateUserDto(
				'User Up',
				Role.VIEWER,
				false
		)
		String accessToken = accessTokenProvider.adminAccessToken
		MutableHttpRequest request = HttpRequest.PUT('/2', user)
				.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
		when:
		UserDto result = client.toBlocking().retrieve(request, UserDto)
		then:
		result
		result.id
		result.name == user.name
		result.role == user.role
		result.active == user.active
	}

	void "Update password"() {
		given:
		User userBefore = userService.getByUsername('user').blockOptional().get()
		UpdateUserPasswordDto user = new UpdateUserPasswordDto(
				'newPass'
		)
		String accessToken = accessTokenProvider.adminAccessToken
		MutableHttpRequest request = HttpRequest.PUT("/${userBefore.id}/password", user)
				.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
		when:
		HttpResponse result = client.toBlocking().exchange(request)
		User userAfter = userService.getByUsername('user').blockOptional().get()
		then:
		result.status() == HttpStatus.OK
		userBefore.password != userAfter.password
	}

	void "List"() {
		given:
		String accessToken = accessTokenProvider.adminAccessToken
		MutableHttpRequest request = HttpRequest.GET('/')
				.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
		when:
		List<UserDto> result = client.toBlocking().retrieve(request, Argument.listOf(UserDto))
		then:
		result.size() == 4
		result*.username.toSet() == ['admin', 'editor', 'viewer', 'user'].toSet()
	}

	void "try add with role editor"() {
		given:
		NewUserDto user = new NewUserDto(
				'user2',
				'123456',
				'user 2',
				Role.EDITOR,
				true
		)
		String accessToken = accessTokenProvider.editorAccessToken
		MutableHttpRequest request = HttpRequest.POST('/', user)
				.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
		when:
		client.toBlocking().retrieve(request, UserDto)
		then:
		HttpClientResponseException exception = thrown()
		exception.response.status() == HttpStatus.FORBIDDEN
	}

	void "try get with role editor"() {
		given:
		String accessToken = accessTokenProvider.editorAccessToken
		MutableHttpRequest request = HttpRequest.GET('/1')
				.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
		when:
		client.toBlocking().retrieve(request, UserDto)
		then:
		HttpClientResponseException exception = thrown()
		exception.response.status() == HttpStatus.FORBIDDEN
	}

	void "try update with role editor"() {
		given:
		UpdateUserDto user = new UpdateUserDto(
				'User Up',
				Role.VIEWER,
				false
		)
		String accessToken = accessTokenProvider.editorAccessToken
		MutableHttpRequest request = HttpRequest.PUT('/2', user)
				.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
		when:
		client.toBlocking().retrieve(request, UserDto)
		then:
		HttpClientResponseException exception = thrown()
		exception.response.status() == HttpStatus.FORBIDDEN
	}

	void "try update password with role editor"() {
		given:
		UpdateUserPasswordDto user = new UpdateUserPasswordDto(
				'newPass'
		)
		String accessToken = accessTokenProvider.editorAccessToken
		MutableHttpRequest request = HttpRequest.PUT('/2/password', user)
				.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
		when:
		client.toBlocking().exchange(request)
		then:
		HttpClientResponseException exception = thrown()
		exception.response.status() == HttpStatus.FORBIDDEN
	}

	void "try list with role editor"() {
		given:
		String accessToken = accessTokenProvider.editorAccessToken
		MutableHttpRequest request = HttpRequest.GET('/')
				.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
		when:
		client.toBlocking().retrieve(request, Argument.listOf(UserDto))
		then:
		HttpClientResponseException exception = thrown()
		exception.response.status() == HttpStatus.FORBIDDEN
	}

}
