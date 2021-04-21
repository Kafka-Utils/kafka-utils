package br.com.kafkautils.security.user

import spock.lang.Specification

class PasswordEncoderTest extends Specification {

	void "test encoder and check"() {
		given:
		String password = '123'
		PasswordEncoder passwordEncoder = new PasswordEncoder('$2b$10$SwKT/LWOb86Crn6OCzheru')
		when:
		String hash = passwordEncoder.encode(password)
		then:
		passwordEncoder.check(password, hash)
	}

}
