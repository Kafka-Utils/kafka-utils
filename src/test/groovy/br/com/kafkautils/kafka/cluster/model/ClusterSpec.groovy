package br.com.kafkautils.kafka.cluster.model

import spock.lang.Specification

import javax.validation.ConstraintViolation
import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory

class ClusterSpec extends Specification {

	private Validator validator

	void setup() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory()
		validator = factory.getValidator()
	}

	void "test name values"() {
		given:
		Cluster cluster = new Cluster(
				null,
				null,
				null,
				name,
				[].<String> toSet(),
				5000
		)
		when:
		Set<ConstraintViolation> errors = validator.validateProperty(cluster, 'name')
		then:
		errors.empty == valid
		where:
		name  | valid
		''    | false
		'   ' | false
		'a'   | true
	}

	void "test brokers values"() {
		given:
		Cluster cluster = new Cluster(
				null,
				null,
				null,
				'a',
				brokers.<String> toSet(),
				5000
		)
		when:
		Set<ConstraintViolation> errors = validator.validateProperty(cluster, 'brokers')
		then:
		errors.empty == valid
		where:
		brokers       | valid
		[]            | false
		['']          | false
		[' ']         | false
		[null]        | false
		['localhost'] | true
	}

}
