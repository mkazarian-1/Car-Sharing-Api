package org.example.carsharingapi.security.impl;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.example.carsharingapi.dto.user.UserLoginRequestDto;
import org.example.carsharingapi.dto.user.UserLoginResponseDto;
import org.example.carsharingapi.dto.user.UserRegistrationRequestDto;
import org.example.carsharingapi.dto.user.UserRegistrationResponseDto;
import org.example.carsharingapi.exeption.RegistrationException;
import org.example.carsharingapi.mapper.UserMapper;
import org.example.carsharingapi.model.User;
import org.example.carsharingapi.model.enums.UserRole;
import org.example.carsharingapi.repository.UserRepository;
import org.example.carsharingapi.security.UserAuthenticationService;
import org.example.carsharingapi.security.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAuthenticationServiceImpl implements UserAuthenticationService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    public UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException("User with this email already exist");
        }

        User user = userMapper.toUser(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setRoles(Set.of(UserRole.CUSTOMER));

        user = userRepository.save(user);

        return userMapper.toRegistrationResponseDto(user);
    }

    @Override
    public UserLoginResponseDto login(UserLoginRequestDto loginRequestDto) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getEmail(), loginRequestDto.getPassword())
        );

        UserLoginResponseDto userLoginResponseDto = new UserLoginResponseDto();
        userLoginResponseDto.setToken(jwtUtil.generateToken(authentication.getName()));
        return userLoginResponseDto;
    }
}
