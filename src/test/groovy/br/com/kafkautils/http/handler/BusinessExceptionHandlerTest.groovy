package br.com.kafkautils.http.handler

import br.com.kafkautils.exceptions.BusinessException
import br.com.kafkautils.exceptions.ConflictException
import br.com.kafkautils.exceptions.ValidationException
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import spock.lang.Specification

class BusinessExceptionHandlerTest extends Specification {

	BusinessExceptionHandler businessExceptionHandler

	void setup() {
		businessExceptionHandler = new BusinessExceptionHandler()
	}

	void "test handle"() {
		HttpRequest request = Mock(HttpRequest)
		when:
		HttpResponse result = businessExceptionHandler.handle(request, exception)

		then:
		result.code() == code
		result.body().message == message
		where:
		exception                           | code | message
		new ConflictException('Conflict')   | 409  | 'Conflict'
		new ValidationException('Invalid')  | 422  | 'Invalid'
		new BusinessException('Ops!', null) | 500  | 'Ops!'
	}
}
