package br.com.kafkautils.security.user

import br.com.kafkautils.security.user.repository.PasswordEncoderService
import spock.lang.Specification

class PasswordEncoderServiceTest extends Specification {

	void "test encoder and check"() {
		given:
		String password = '123'
		PasswordEncoderService passwordEncoder = new PasswordEncoderService('$2b$10$SwKT/LWOb86Crn6OCzheru')
		when:
		String hash = passwordEncoder.encode(password)
		then:
		passwordEncoder.check(password, hash)
	}

}
