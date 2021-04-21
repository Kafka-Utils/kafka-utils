package br.com.kafkautils.security.user

import org.mapstruct.Mapper
import org.mapstruct.MappingTarget
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "jsr330", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface UserMapper {
    fun toDto(user: UserData): UserDto
    fun updateFromCommand(command: UpdateUserCommand, @MappingTarget user: UserData): UserData
    fun toDomain(command: NewUserCommand): UserData
}