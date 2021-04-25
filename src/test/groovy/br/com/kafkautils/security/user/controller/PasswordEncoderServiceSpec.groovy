package br.com.kafkautils.security.user.controller

import br.com.kafkautils.security.user.service.PasswordEncoderService
import spock.lang.Specification

class PasswordEncoderServiceSpec extends Specification {

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
