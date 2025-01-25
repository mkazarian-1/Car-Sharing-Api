package org.example.carsharingapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.example.carsharingapi.dto.car.CarDto;
import org.example.carsharingapi.dto.car.CreateRequestCarDto;
import org.example.carsharingapi.dto.car.UpdateRequestCarDto;
import org.example.carsharingapi.model.enums.CarType;
import org.example.carsharingapi.util.CarTestUtil;
import org.example.carsharingapi.util.CustomPageImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CarControllerTest {
    private static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }


    @Test
    @DisplayName("""
            Should return empty with pageable without authorization
            """)
    void getAllCar_Without_Success() throws Exception {
        //When
        MvcResult mvcResult = mockMvc
                .perform(
                        get("/cars")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andReturn();

        //Then
        CustomPageImpl<CarDto> actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructParametricType(CustomPageImpl.class, CarDto.class)
        );

        Assertions.assertEquals(0, actual.getContent().size());
    }

    @Test
    @DisplayName("""
            Should return all cars with pageable without authorization
            """)
    @Sql(scripts = "classpath:cars/add-cars.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:cars/delete-cars.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getAllCar_WithCorrectData_Success() throws Exception {
        //Given
        List<CarDto> expected = CarTestUtil.getCarDtoList();

        //When
        MvcResult mvcResult = mockMvc
                .perform(
                        get("/cars")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andReturn();

        //Then
        CustomPageImpl<CarDto> actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructParametricType(CustomPageImpl.class, CarDto.class)
        );

        Assertions.assertEquals(expected.size(), actual.getContent().size());

        for (int i = 0; i < actual.getContent().size(); i++) {
            assertTrue(EqualsBuilder.reflectionEquals(expected.get(i),
                    actual.getContent().get(i)));
        }
    }

    @Test
    @DisplayName("""
            Should return carDto without authorization
            """)
    @Sql(scripts = "classpath:cars/add-cars.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:cars/delete-cars.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getCarById_WithCorrectData_Success() throws Exception {
        CarDto expected = CarTestUtil.getCarDtoList().get(1);

        //When
        MvcResult mvcResult = mockMvc
                .perform(
                        get("/cars/2")
                )
                .andExpect(status().isOk())
                .andReturn();

        //Then
        CarDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                CarDto.class);

        assertTrue(EqualsBuilder.reflectionEquals(expected,
                actual));
    }

    @Test
    @DisplayName("""
            Should create and return new carDto
            """)
    @Sql(scripts = "classpath:cars/add-cars-without-id.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:cars/delete-cars.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "admin", authorities = {"MANAGER"})
    void addCar_CorrectData_Success() throws Exception {
        CreateRequestCarDto requestCarDto = new CreateRequestCarDto();
        requestCarDto.setBrand("Tesla 1");
        requestCarDto.setModel("Model S 1");
        requestCarDto.setType(CarType.SUV);
        requestCarDto.setInventory(0);
        requestCarDto.setDailyFee(BigDecimal.valueOf(120));

        CarDto expected = new CarDto();
        expected.setBrand("Tesla 1");
        expected.setModel("Model S 1");
        expected.setType(CarType.SUV);
        expected.setInventory(0);
        expected.setDailyFee(BigDecimal.valueOf(120));

        String jsonRequest = objectMapper.writeValueAsString(requestCarDto);

        //When
        MvcResult mvcResult = mockMvc
                .perform(
                        post("/cars")
                                .content(jsonRequest)
                                .contentType(MediaType
                                        .APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        //Then
        CarDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), CarDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        assertTrue(EqualsBuilder.reflectionEquals(expected,
                actual, List.of("id")));
    }

    @Test
    @DisplayName("""
            Should update return carDto with update data
            """)
    @Sql(scripts = "classpath:cars/add-cars.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:cars/delete-cars.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "admin", authorities = {"MANAGER"})
    void updateCar_CorrectData_Success() throws Exception {
        UpdateRequestCarDto requestCarDto = new UpdateRequestCarDto();
        requestCarDto.setInventory(10);
        requestCarDto.setDailyFee(BigDecimal.valueOf(120));

        CarDto expected = CarTestUtil.getCarDtoList().get(1);
        expected.setInventory(10);
        expected.setDailyFee(BigDecimal.valueOf(120));

        String jsonRequest = objectMapper.writeValueAsString(requestCarDto);

        //When
        MvcResult mvcResult = mockMvc
                .perform(
                        patch("/cars/2")
                                .content(jsonRequest)
                                .contentType(MediaType
                                        .APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        CarDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), CarDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        assertTrue(EqualsBuilder.reflectionEquals(expected,
                actual, List.of("id")));
    }

    @Test
    @DisplayName("""
            Should delete car without authorization
            """)
    @Sql(scripts = "classpath:cars/add-cars.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:cars/delete-cars.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "admin", authorities = {"MANAGER"})
    void deleteCar_CorrectData_Success() throws Exception {
        mockMvc.perform(
                delete("/cars/1")
        ).andExpect(status().isNoContent()).andReturn();
    }
}