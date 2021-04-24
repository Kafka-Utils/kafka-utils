package br.com.kafkautils.http.handler

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import spock.lang.Specification

import javax.validation.ConstraintViolation
import javax.validation.ConstraintViolationException
import javax.validation.Path

class CustomConstraintExceptionHandlerTest extends Specification {

	CustomConstraintExceptionHandler customConstraintExceptionHandler

	void setup() {
		customConstraintExceptionHandler = new CustomConstraintExceptionHandler()
	}

	void "test handle with empty constrains violations"() {
		HttpRequest request = Mock(HttpRequest)
		ConstraintViolationException exception = new ConstraintViolationException('Error', to([]))
		when:
		HttpResponse<ValidationErrorList> result = customConstraintExceptionHandler.handle(request, exception)

		then:
		result.code() == 400
		result.body().message == 'Error'
		result.body().errors == []
	}

	void "test handle with constrains violations"() {
		HttpRequest request = Mock(HttpRequest)
		ConstraintViolation violation = Mock(ConstraintViolation)
		violation.message >> 'Invalid field'
		Path propertyPath = Mock(Path)
		propertyPath.toString() >> 'error'
		Node node = Mock(Node)
		node.toString() >> 'field'
		List<Node> nodes = [node]
		propertyPath.iterator() >> nodes.iterator()
		violation.propertyPath >> propertyPath
		violation.invalidValue >> ''

		ConstraintViolationException exception = new ConstraintViolationException('Invalid field', to([violation]))
		when:
		HttpResponse<ValidationErrorList> result = customConstraintExceptionHandler.handle(request, exception)

		then:
		result.code() == 400
		result.body().message == 'Invalid field'
		result.body().errors == [new ValidationError(
				'Invalid field', 'error', 'field', ''
		)]
	}

	private Set<? extends ConstraintViolation<?>> to(List list) {
		return list as Set<? extends ConstraintViolation<?>>
	}
}
