package org.example.carsharingapi.security;

import org.example.carsharingapi.dto.user.UserLoginRequestDto;
import org.example.carsharingapi.dto.user.UserLoginResponseDto;
import org.example.carsharingapi.dto.user.UserRegistrationRequestDto;
import org.example.carsharingapi.dto.user.UserRegistrationResponseDto;

public interface UserAuthenticationService {
    UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto);

    UserLoginResponseDto login(UserLoginRequestDto loginRequestDto);
}
