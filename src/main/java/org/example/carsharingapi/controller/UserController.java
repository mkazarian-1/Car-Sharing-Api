package org.example.carsharingapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

@Tag(name = "Users", description = "Endpoints for managing users")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserMapper userMapper;
    private final UserService userService;

    @Operation(summary = "Get current user information",
            description = "Retrieves the details of the currently authenticated user.")
    @GetMapping("/me")
    public UserDto getCurrentUserInfo() {
        User user = UserUtil.getAuthenticatedUser();
        return userMapper.toDto(user);
    }

    @Operation(summary = "Update user role",
            description = "Updates the role of a specific user. Necessary role: MANAGER.")
    @PreAuthorize("hasAuthority('MANAGER')")
    @PutMapping("/{id}/role")
    public UserDto updateUserRole(@PathVariable Long id,
                                  @RequestBody @Valid UpdateUserRoleDto updateUserRoleDto) {
        return userService.updateUserRole(id, updateUserRoleDto);
    }

    @Operation(summary = "Update current user information",
            description = "Updates the personal information of the currently authenticated user.")
    @PutMapping("/me")
    public UserDto updateUserInfo(@RequestBody @Valid UpdateUserInfoDto updateUserInfoDto) {
        User user = UserUtil.getAuthenticatedUser();
        return userService.updateUserInfo(user, updateUserInfoDto);
    }
}
