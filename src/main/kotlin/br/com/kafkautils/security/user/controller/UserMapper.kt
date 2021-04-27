package br.com.kafkautils.security.user.controller

import br.com.kafkautils.security.user.controller.dto.NewUserDto
import br.com.kafkautils.security.user.controller.dto.UpdateUserDto
import br.com.kafkautils.security.user.controller.dto.UserDto
import br.com.kafkautils.security.user.model.User
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "jsr330", unmappedTargetPolicy = ReportingPolicy.IGNORE)
abstract class UserMapper {
    abstract fun toDto(user: User): UserDto
    abstract fun toDomain(dto: NewUserDto): User
    fun updateFromCommand(dto: UpdateUserDto, user: User): User {
        return user.copy(
            name = dto.name,
            role = dto.role,
            active = dto.active
        )
    }
}
