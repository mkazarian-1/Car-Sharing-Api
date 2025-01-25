package org.example.carsharingapi.controller;

import org.example.carsharingapi.dto.rental.CreateRequestRentalDto;
import org.example.carsharingapi.dto.rental.RentalDto;
import org.example.carsharingapi.dto.rental.UpdateRequestRentalDto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.carsharingapi.util.CustomPageImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class RentalControllerTest {

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
    @Sql(scripts = "classpath:rentals/add-rentals-with-user-car.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:rentals/delete-rentals-users-cars.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails(value = "Lanot@gmail.com-MANAGER")
    @DisplayName("Should return paginated rentals for a user when requested by a manager")
    void getByUserIdAndActivity_WithManagerRole_Success() throws Exception {
        // Given
        long userId = 3L;
        boolean isActive = true;

        // When
        MvcResult mvcResult = mockMvc
                .perform(get("/rentals")
                        .param("user_id", Long.toString(userId))
                        .param("is_active", String.valueOf(isActive)))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        CustomPageImpl<RentalDto> actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructParametricType(CustomPageImpl.class, RentalDto.class)
        );
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(2, actual.getContent().size());
    }

    @Test
    @Sql(scripts = "classpath:rentals/add-rentals-with-user-car.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:rentals/delete-rentals-users-cars.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails(value = "Lanot@gmail.com-CUSTOMER")
    @DisplayName("Should return rentals for the authenticated user")
    void getByUserIdAndActivity_WithUserRole_Success() throws Exception {
        // When
        MvcResult mvcResult = mockMvc
                .perform(get("/rentals")
                        .param("is_active", "false"))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        CustomPageImpl<RentalDto> actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructParametricType(CustomPageImpl.class, RentalDto.class)
        );
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(1, actual.getContent().size());
    }

    @Test
    @Sql(scripts = "classpath:rentals/add-rentals-with-user-car.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:rentals/delete-rentals-users-cars.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails(value = "Lanot@gmail.com-MANAGER")
    @DisplayName("Should return rental by ID for a manager")
    void getById_WithManagerRole_Success() throws Exception {
        // Given
        Long rentalId = 1L;

        // When
        MvcResult mvcResult = mockMvc
                .perform(get("/rentals/" + rentalId))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        RentalDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), RentalDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(rentalId, actual.getId());
    }

    @Test
    @Sql(scripts = "classpath:rentals/add-rentals-with-user-car.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:rentals/delete-rentals-users-cars.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails(value = "Lanot@gmail.com-CUSTOMER")
    @DisplayName("""
            Should return status 404 as customer don't have rental with current ID
            """)
    void getById_WithCustomerRole_Exception() throws Exception {
        // Given
        long rentalId = 2L;

        // When
        mockMvc.perform(get("/rentals/" + rentalId))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @Sql(scripts = "classpath:rentals/add-rentals-with-user-car-without-id.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:rentals/delete-rentals-users-cars.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails(value = "Lanot@gmail.com-MANAGER")
    @DisplayName("Should create a new rental for authenticated user")
    void addRental_WithValidRequest_Success() throws Exception {
        // Given
        CreateRequestRentalDto requestDto = new CreateRequestRentalDto();
        requestDto.setCarId(1L);
        requestDto.setRentalDate(LocalDate.now().plusDays(1));
        requestDto.setReturnDate(LocalDate.now().plusDays(5));

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult mvcResult = mockMvc
                .perform(post("/rentals")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        // Then
        RentalDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), RentalDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        Assertions.assertEquals(requestDto.getCarId(), actual.getCar().getId());
    }

    @Test
    @Sql(scripts = "classpath:rentals/add-rentals-with-user-car.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:rentals/delete-rentals-users-cars.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails(value = "Lanot@gmail.com-MANAGER")
    @DisplayName("Should update actual return date for a rental")
    void returnRental_WithValidRequest_Success() throws Exception {
        // Given
        Long rentalId = 1L;
        UpdateRequestRentalDto requestDto = new UpdateRequestRentalDto();
        requestDto.setActualReturnDate(LocalDate.parse("2025-08-01"));

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult mvcResult = mockMvc
                .perform(post("/rentals/return/" + rentalId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        RentalDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), RentalDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(requestDto.getActualReturnDate(), actual.getActualReturnDate());
    }
}