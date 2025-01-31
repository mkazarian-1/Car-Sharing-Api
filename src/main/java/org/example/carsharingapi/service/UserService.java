package org.example.carsharingapi.service;

import org.example.carsharingapi.dto.user.UpdateUserInfoDto;
import org.example.carsharingapi.dto.user.UpdateUserRoleDto;
import org.example.carsharingapi.dto.user.UserDto;
import org.example.carsharingapi.model.User;

public interface UserService {
    UserDto updateUserRole(Long id, UpdateUserRoleDto updateUserRoleDto);

    UserDto updateUserInfo(User user, UpdateUserInfoDto updateUserInfoDto);
}
