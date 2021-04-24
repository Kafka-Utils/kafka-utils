package br.com.kafkautils.security.user.controller

import br.com.kafkautils.security.user.model.UserData
import br.com.kafkautils.security.user.controller.command.NewUserCommand
import br.com.kafkautils.security.user.controller.command.UpdateUserCommand
import br.com.kafkautils.security.user.controller.dto.UserDto
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "jsr330", unmappedTargetPolicy = ReportingPolicy.IGNORE)
abstract class UserMapper {
    abstract fun toDto(user: UserData): UserDto
    abstract fun toDomain(command: NewUserCommand): UserData
    fun updateFromCommand(command: UpdateUserCommand, user: UserData): UserData {
        return user.copy(
            name = command.name,
            role =  command.role,
            active =  command.active
        )
    }
}