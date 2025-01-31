package org.example.carsharingapi.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import org.example.carsharingapi.dto.user.UpdateUserInfoDto;
import org.example.carsharingapi.dto.user.UpdateUserRoleDto;
import org.example.carsharingapi.dto.user.UserDto;
import org.example.carsharingapi.model.enums.UserRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {
    private static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Should return current user info")
    @WithUserDetails(value = "Lanot@gmail.com")
    @Sql(scripts = "classpath:users/add-user.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:users/delete-users.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getCurrentUserInfo_Success() throws Exception {
        // When
        MvcResult mvcResult = mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        UserDto actual = objectMapper
                .readValue(mvcResult.getResponse().getContentAsString(), UserDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals("Lanot@gmail.com", actual.getEmail());
    }

    @Test
    @DisplayName("Should update user role")
    @WithUserDetails(value = "Lanot@gmail.com-MANAGER")
    @Sql(scripts = "classpath:users/add-user.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:users/delete-users.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateUserRole_Success() throws Exception {
        UpdateUserRoleDto requestDto = new UpdateUserRoleDto();
        requestDto.setRoles(Set.of(UserRole.CUSTOMER,UserRole.MANAGER));
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult mvcResult = mockMvc.perform(put("/users/3/role")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        UserDto actual = objectMapper
                .readValue(mvcResult.getResponse().getContentAsString(), UserDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(
                Set.of(UserRole.CUSTOMER,UserRole.MANAGER), actual.getRoles());
    }

    @Test
    @DisplayName("Should update current user info")
    @Sql(scripts = "classpath:users/add-user.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:users/delete-users.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails(value = "Lanot@gmail.com")
    void updateUserInfo_Success() throws Exception {
        UpdateUserInfoDto requestDto = new UpdateUserInfoDto();
        requestDto.setFirstName("John");
        requestDto.setSecondName("Doe");
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult mvcResult = mockMvc.perform(put("/users/me")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        UserDto actual = objectMapper
                .readValue(mvcResult.getResponse().getContentAsString(), UserDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals("John", actual.getFirstName());
        Assertions.assertEquals("Doe", actual.getSecondName());
    }

    @Test
    @DisplayName("Should return 403 for unauthorized access to update role")
    @WithUserDetails(value = "Lanot@gmail.com")
    @Sql(scripts = "classpath:users/add-user.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:users/delete-users.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateUserRole_Unauthorized() throws Exception {
        UpdateUserRoleDto requestDto = new UpdateUserRoleDto();
        requestDto.setRoles(Set.of(UserRole.CUSTOMER,UserRole.MANAGER));
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When / Then
        mockMvc.perform(put("/users/1/role")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return 404 for non-existent user when updating role")
    @WithUserDetails(value = "Lanot@gmail.com-MANAGER")
    @Sql(scripts = "classpath:users/add-user.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:users/delete-users.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateUserRole_NotFound() throws Exception {
        UpdateUserRoleDto requestDto = new UpdateUserRoleDto();
        requestDto.setRoles(Set.of(UserRole.CUSTOMER,UserRole.MANAGER));
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When / Then
        mockMvc.perform(put("/users/999/role")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 400 for invalid input when updating current user info")
    @WithUserDetails(value = "Lanot@gmail.com")
    @Sql(scripts = "classpath:users/add-user.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:users/delete-users.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateUserInfo_BadRequest() throws Exception {
        UpdateUserInfoDto requestDto = new UpdateUserInfoDto();
        requestDto.setSecondName("");
        requestDto.setFirstName("");
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When / Then
        mockMvc.perform(put("/users/me")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}

