package br.com.kafkautils.security.user.repository

import com.password4j.Password
import io.micronaut.context.annotation.Property
import javax.inject.Singleton

@Singleton
class PasswordEncoderService(
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
