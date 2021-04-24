package br.com.kafkautils.security.user.service

import br.com.kafkautils.exceptions.ConflictException
import br.com.kafkautils.i18n.Messages
import br.com.kafkautils.security.user.repository.PasswordEncoderService
import br.com.kafkautils.security.user.model.UserData
import br.com.kafkautils.security.user.repository.UserRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Singleton
@Transactional
open class UserService(
    private val passwordEncoderService: PasswordEncoderService,
    private val userRepository: UserRepository,
    private val messages: Messages
) {

    open fun add(@Valid user: UserData): Mono<UserData> {
        return userRepository.existsByUsername(user.username).flatMap { existsUser ->
            if (existsUser) {
                val msg = messages.getMessage("username.already.in.use", mapOf("username" to user.username))
                Mono.error(ConflictException(msg))
            } else {
                val userToSave = user.copy(
                    password = passwordEncoderService.encode(user.password)
                )
                userRepository.save(userToSave)
            }
        }
    }

    open fun update(@Valid user: UserData): Mono<UserData> {
        return userRepository.update(user)
    }

    open fun updatePassword(id: Int, newPassword: String): Mono<Void> {
        return userRepository.findById(id).flatMap { user ->
            val userToSave = user.copy(
                password = passwordEncoderService.encode(user.password)
            )
            userRepository.update(userToSave).then()
        }
    }

    open fun get(id: Int): Mono<UserData> {
        return userRepository.findById(id)
    }

    open fun list(): Flux<UserData> {
        return userRepository.findAll()
    }
}
