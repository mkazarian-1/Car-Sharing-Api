package org.example.carsharingapi.mapper;

import org.example.carsharingapi.config.MapperConfig;
import org.example.carsharingapi.dto.user.UpdateUserInfoDto;
import org.example.carsharingapi.dto.user.UpdateUserRoleDto;
import org.example.carsharingapi.dto.user.UserDto;
import org.example.carsharingapi.dto.user.UserRegistrationRequestDto;
import org.example.carsharingapi.dto.user.UserRegistrationResponseDto;
import org.example.carsharingapi.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User toUser(UserRegistrationRequestDto requestDto);

    UserRegistrationResponseDto toRegistrationResponseDto(User user);

    UserDto toDto(User user);

    void updateRole(@MappingTarget User user, UpdateUserRoleDto updateUserRoleDto);

    void updateInfo(@MappingTarget User user, UpdateUserInfoDto updateUserInfoDto);
}
