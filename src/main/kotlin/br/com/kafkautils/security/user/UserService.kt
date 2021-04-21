package br.com.kafkautils.security.user

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Singleton
@Transactional
open class UserService(
    private val passwordEncoder: PasswordEncoder,
    private val userRepository: UserRepository
) {

    open fun add(@Valid user: UserData): Mono<UserData> {
        return userRepository.existsByUsername(user.username).flatMap { existsUser ->
            if (existsUser) {
                throw RuntimeException()
            }
            val userToSave = user.copy(
                password = passwordEncoder.encode(user.password)
            )
            userRepository.save(userToSave)
        }
    }

    open fun update(@Valid user: UserData): Mono<UserData> {
        return userRepository.update(user)
    }

    open fun updatePassword(id: Int, newPassword: String): Mono<Void> {
        return userRepository.findById(id).flatMap { user ->
            val userToSave = user.copy(
                password = passwordEncoder.encode(user.password)
            )
            userRepository.save(userToSave).then()
        }
    }

    open fun get(id: Int): Mono<UserData> {
        return userRepository.findById(id)
    }

    open fun list(): Flux<UserData> {
        return userRepository.findAll()
    }
}
