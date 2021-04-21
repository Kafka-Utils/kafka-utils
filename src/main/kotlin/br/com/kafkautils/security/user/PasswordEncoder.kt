package br.com.kafkautils.security.user

import io.micronaut.context.annotation.Property
import javax.inject.Singleton
import com.password4j.Password

@Singleton
class PasswordEncoder(
    @Property(name = "password-encoder.slat") private val salt: String
) {

    fun encode(password: String): String {
        val hash = Password.hash(password).addSalt(salt).withBCrypt()
        return hash.result
    }

    fun check(password: String, hash: String): Boolean {
        return Password.check(password, hash).addSalt(salt).withBCrypt()
    }

}