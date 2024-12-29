package org.example.carsharingapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.carsharingapi.dto.user.UserLoginRequestDto;
import org.example.carsharingapi.dto.user.UserLoginResponseDto;
import org.example.carsharingapi.dto.user.UserRegistrationRequestDto;
import org.example.carsharingapi.dto.user.UserResponseDto;
import org.example.carsharingapi.exeption.RegistrationException;
import org.example.carsharingapi.security.UserAuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

//@Tag(name = "User Authentication",
//        description = "Endpoints for managing user authentication and registration")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {
    private final UserAuthenticationService userAuthenticationService;

    //    @Operation(summary = "Register a new user",
    //            description = """
    //                    Registers a new user in the system.
    //                    The provided email must be unique.
    //                    Returns the newly registered user's details.
    //                    Throws RegistrationException if the email already exists.
    //                    \nNecessary role: None
    //                    """)
    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto request)
            throws RegistrationException {
        return userAuthenticationService.register(request);
    }

    //    @Operation(summary = "User authorization",
    //            description = """
    //                    Authorizes the user in the system.
    //                    Returns JWT token for 10 minutes.
    //                    In case of incorrect email or password, a 401 status is returned
    //                    \nNecessary role: None
    //                    """)
    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto loginRequestDto) {
        return userAuthenticationService.login(loginRequestDto);
    }
}
