package br.com.kafkautils.security.user.controller

import br.com.kafkautils.security.user.controller.command.NewUserCommand
import br.com.kafkautils.security.user.controller.command.UpdateUserCommand
import br.com.kafkautils.security.user.controller.dto.UserDto
import br.com.kafkautils.security.user.model.User
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "jsr330", unmappedTargetPolicy = ReportingPolicy.IGNORE)
abstract class UserMapper {
    abstract fun toDto(user: User): UserDto
    abstract fun toDomain(command: NewUserCommand): User
    fun updateFromCommand(command: UpdateUserCommand, user: User): User {
        return user.copy(
            name = command.name,
            role = command.role,
            active = command.active
        )
    }
}
