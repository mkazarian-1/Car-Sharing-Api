package org.example.carsharingapi.mapper;

import org.example.carsharingapi.config.MapperConfig;
import org.example.carsharingapi.dto.user.UserRegistrationRequestDto;
import org.example.carsharingapi.dto.user.UserResponseDto;
import org.example.carsharingapi.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User toUser(UserRegistrationRequestDto requestDto);

    UserResponseDto toDto(User user);
}
