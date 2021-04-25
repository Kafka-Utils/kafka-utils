package br.com.kafkautils.security

import br.com.kafkautils.security.user.repository.UserRepository
import br.com.kafkautils.security.user.service.PasswordEncoderService
import io.micronaut.http.HttpRequest
import io.micronaut.security.authentication.AuthenticationException
import io.micronaut.security.authentication.AuthenticationFailed
import io.micronaut.security.authentication.AuthenticationProvider
import io.micronaut.security.authentication.AuthenticationRequest
import io.micronaut.security.authentication.AuthenticationResponse
import io.micronaut.security.authentication.UserDetails
import org.reactivestreams.Publisher
import reactor.core.publisher.Mono
import javax.inject.Singleton

@Singleton
class AuthenticationProviderUserPassword(
    private val userRepository: UserRepository,
    private val passwordEncoderService: PasswordEncoderService
) : AuthenticationProvider {

    override fun authenticate(
        httpRequest: HttpRequest<*>?,
        authenticationRequest: AuthenticationRequest<*, *>?
    ): Publisher<AuthenticationResponse> {
        return userRepository.findByUsername(authenticationRequest!!.identity.toString()).flatMap {
            if (passwordEncoderService.check(authenticationRequest.secret.toString(), it.password)) {
                Mono.just(UserDetails(it.username, listOf(it.role.name), mapOf("id" to it.id)))
            } else {
                Mono.error(AuthenticationException(AuthenticationFailed()))
            }
        }
    }
}
