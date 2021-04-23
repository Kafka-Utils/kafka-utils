package br.com.kafkautils.security.user

import br.com.kafkautils.IntegrationSpec
import br.com.kafkautils.http.handler.ResponseError
import br.com.kafkautils.http.handler.ValidationErrorList
import io.micronaut.core.type.Argument
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
class UserDataControllerIntegrationSpec extends IntegrationSpec {

	@Inject
	@Client('/api/user')
	private HttpClient client

	@Inject
	private UserService userService

	void "Add"() {
		given:
		NewUserCommand user = new NewUserCommand(
				'user',
				'123456',
				'User',
				Role.EDITOR,
				true
		)
		MutableHttpRequest request = HttpRequest.POST('/', user)
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
		NewUserCommand user = new NewUserCommand(
				'admin',
				'123456',
				'admin 2',
				Role.EDITOR,
				true
		)
		MutableHttpRequest request = HttpRequest.POST('/', user)
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
				active  : true
		]
		MutableHttpRequest request = HttpRequest.POST('/', user)
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
		MutableHttpRequest request = HttpRequest.GET("/1")
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
		UpdateUserCommand user = new UpdateUserCommand(
				'User Up',
				Role.VIEWER,
				false
		)
		MutableHttpRequest request = HttpRequest.PUT('/2', user)
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
		UserData userBefore = userService.get(2).blockOptional().get()
		UpdateUserPasswordCommand user = new UpdateUserPasswordCommand(
				'newPass'
		)
		MutableHttpRequest request = HttpRequest.PUT('/2/password', user)
		when:
		HttpResponse result = client.toBlocking().exchange(request)
		UserData userAfter = userService.get(2).blockOptional().get()
		then:
		result.status() == HttpStatus.OK
		userBefore.password != userAfter.password
	}

	void "List"() {
		given:
		MutableHttpRequest request = HttpRequest.GET("/")
		when:
		List<UserDto> result = client.toBlocking().retrieve(request, Argument.listOf(UserDto))
		then:
		result.size() == 2
		result*.username.toSet() == ['user', 'admin'].toSet()
	}

}
