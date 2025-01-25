package org.example.carsharingapi.service.impl;


import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import jakarta.persistence.EntityNotFoundException;
import org.example.carsharingapi.dto.user.UpdateUserInfoDto;
import org.example.carsharingapi.dto.user.UpdateUserRoleDto;
import org.example.carsharingapi.dto.user.UserDto;
import org.example.carsharingapi.mapper.UserMapper;
import org.example.carsharingapi.mapper.impl.UserMapperImpl;
import org.example.carsharingapi.model.User;
import org.example.carsharingapi.model.enums.UserRole;
import org.example.carsharingapi.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Spy
    private UserMapper userMapper = new UserMapperImpl();

    @Test
    @DisplayName("Should update user role successfully")
    void testUpdateUserRole() {
        // given
        Long userId = 1L;
        UpdateUserRoleDto updateUserRoleDto = new UpdateUserRoleDto();
        updateUserRoleDto.setRoles(new HashSet<>(Set.of(UserRole.CUSTOMER)));

        User user = new User();
        user.setId(userId);
        user.setRoles(new HashSet<>(Set.of(UserRole.MANAGER)));

        UserDto expected = new UserDto();
        expected.setId(userId);
        expected.setRoles(new HashSet<>(Set.of(UserRole.CUSTOMER)));

        when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        // when
        UserDto actual = userService.updateUserRole(userId, updateUserRoleDto);

        // then
        assertEquals(expected.getRoles(), actual.getRoles());
        verify(userRepository).findUserById(userId);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when user is not found")
    void testUpdateUserRoleUserNotFound() {
        // given
        Long userId = 1L;
        UpdateUserRoleDto updateUserRoleDto = new UpdateUserRoleDto();
        updateUserRoleDto.setRoles(Set.of());

        when(userRepository.findUserById(userId)).thenReturn(Optional.empty());

        // when & then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            userService.updateUserRole(userId, updateUserRoleDto);
        });

        assertEquals("Can't find user by id: " + userId, exception.getMessage());
        verify(userRepository).findUserById(userId);
    }

    @Test
    @DisplayName("Should update user info successfully")
    void testUpdateUserInfo() {
        // given
        User user = new User();
        user.setId(1L);
        user.setFirstName("Old First Name");
        user.setFirstName("Old Second Name");

        UpdateUserInfoDto updateUserInfoDto = new UpdateUserInfoDto();
        updateUserInfoDto.setFirstName("New First Name");
        updateUserInfoDto.setSecondName("New Second Name");

        when(userRepository.save(any(User.class))).thenReturn(user);
        // when
        UserDto actual = userService.updateUserInfo(user, updateUserInfoDto);

        // then
        assertEquals("New First Name", actual.getFirstName());
        assertEquals("New Second Name", actual.getSecondName());
        verify(userRepository).save(user);
    }
}
