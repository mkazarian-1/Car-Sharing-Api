package org.example.carsharingapi.controller;

import lombok.RequiredArgsConstructor;
import org.example.carsharingapi.dto.user.UpdateUserInfoDto;
import org.example.carsharingapi.dto.user.UpdateUserRoleDto;
import org.example.carsharingapi.dto.user.UserDto;
import org.example.carsharingapi.mapper.UserMapper;
import org.example.carsharingapi.model.User;
import org.example.carsharingapi.security.util.UserUtil;
import org.example.carsharingapi.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserMapper userMapper;
    private final UserService userService;

    @GetMapping("/me")
    public UserDto getCurrentUserInfo() {
        User user = UserUtil.getAuthenticatedUser();
        return userMapper.toDto(user);
    }

    @PreAuthorize("hasAuthority('MANAGER')")
    @PutMapping("/{id}/role")
    public UserDto updateUserRole(@PathVariable Long id,
                           @RequestBody UpdateUserRoleDto updateUserRoleDto) {
        return userService.updateUserRole(id, updateUserRoleDto);
    }

    @PutMapping("/me")
    public UserDto updateUserInfo(@RequestBody UpdateUserInfoDto updateUserInfoDto) {
        User user = UserUtil.getAuthenticatedUser();
        return userService.updateUserInfo(user, updateUserInfoDto);
    }
}
