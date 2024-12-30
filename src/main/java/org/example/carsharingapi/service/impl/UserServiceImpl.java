package org.example.carsharingapi.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.carsharingapi.dto.user.UpdateUserInfoDto;
import org.example.carsharingapi.dto.user.UpdateUserRoleDto;
import org.example.carsharingapi.dto.user.UserDto;
import org.example.carsharingapi.mapper.UserMapper;
import org.example.carsharingapi.model.User;
import org.example.carsharingapi.repository.UserRepository;
import org.example.carsharingapi.service.UserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    public UserDto updateUserRole(Long id, UpdateUserRoleDto updateUserRoleDto) {
        User user = userRepository.findUserById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find user by id: " + id));
        userMapper.updateRole(user, updateUserRoleDto);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserDto updateUserInfo(User user, UpdateUserInfoDto updateUserInfoDto) {
        userMapper.updateInfo(user, updateUserInfoDto);
        return userMapper.toDto(userRepository.save(user));
    }
}
