package br.com.kafkautils.security.user.model

import spock.lang.Specification

import javax.validation.ConstraintViolation
import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory

class UserSpec extends Specification {

	private Validator validator

	void setup() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory()
		validator = factory.getValidator()
	}

	void "test username values"() {
		given:
		User user = new User(
				1,
				null,
				null,
				username,
				'admin',
				'',
				Role.ADMIN,
				true
		)
		when:
		Set<ConstraintViolation> errors = validator.validateProperty(user, 'username')
		then:
		errors.empty == valid
		where:
		username | valid
		''       | false
		'a'      | false
		'aa'     | false
		'_aaa'   | false
		'@as'    | false
		'aaa'    | true
		'a_a_a'  | true
		'a.a.a'  | true
	}

	void "test password values"() {
		given:
		User user = new User(
				1,
				null,
				null,
				'admin',
				password,
				'',
				Role.ADMIN,
				true
		)
		when:
		Set<ConstraintViolation> errors = validator.validateProperty(user, 'password')
		then:
		errors.empty == valid
		where:
		password      | valid
		''            | false
		'a'           | false
		'aa'          | false
		'123456'      | true
		'123456abcfe' | true
	}
}
