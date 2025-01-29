package org.example.carsharingapi.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import org.example.carsharingapi.dto.payment.CreatePaymentRequestDto;
import org.example.carsharingapi.dto.payment.PaymentDto;
import org.example.carsharingapi.model.enums.PaymentType;
import org.example.carsharingapi.payment.StripeService;
import org.example.carsharingapi.util.CustomPageImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @TestConfiguration
    static class TestAppConfig {
        @Bean
        @Primary
        public StripeService stripeService() {
            StripeService stripeService = Mockito.mock(StripeService.class);

            Session mockSession = new Session();
            mockSession.setId("mock-session-id");
            mockSession.setUrl("https://mock-session-url.com");

            Mockito.when(stripeService.createStripeSession(any(),
                    any(), any())).thenReturn(mockSession);
            return stripeService;
        }
    }

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
    @Sql(scripts = "classpath:payments/add-payment-with-rentals-user-car.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:payments/delete-payment-rentals-users-cars.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails(value = "Lanot@gmail.com-MANAGER")
    @DisplayName("Should return paginated payments for a specified user")
    void getPayments_WithManagerRole_Success() throws Exception {
        // Given
        long userId = 2L;

        // When
        MvcResult mvcResult = mockMvc
                .perform(get("/payments")
                        .param("user_id", Long.toString(userId)))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        CustomPageImpl<PaymentDto> actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                objectMapper.getTypeFactory()
                        .constructParametricType(CustomPageImpl.class, PaymentDto.class)
        );
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(1, actual.getContent().size());
        Assertions.assertEquals(2, actual.getContent().getFirst().getId());
    }

    @Test
    @Sql(scripts = "classpath:payments/add-payment-with-rentals-user-car.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:payments/delete-payment-rentals-users-cars.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails(value = "Lanot@gmail.com-CUSTOMER")
    @DisplayName("Should return payments for the authenticated user")
    void getPayments_WithCustomerRole_Success() throws Exception {
        // When
        MvcResult mvcResult = mockMvc
                .perform(get("/payments"))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        CustomPageImpl<PaymentDto> actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                objectMapper.getTypeFactory()
                        .constructParametricType(CustomPageImpl.class, PaymentDto.class)
        );
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(1, actual.getContent().size());
        Assertions.assertEquals(1, actual.getContent().getFirst().getId());
    }

    @Test
    @Sql(scripts = "classpath:payments/add-payment-with-rentals-user-car-without-id.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:payments/delete-payment-rentals-users-cars.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails(value = "Lanot@gmail.com-CUSTOMER")
    @DisplayName("Should create a new payment session")
    void createPaymentSession_WithValidRequest_Success() throws Exception {
        // Given
        CreatePaymentRequestDto requestDto = new CreatePaymentRequestDto();
        requestDto.setPaymentType(PaymentType.PAYMENT);
        requestDto.setRentalId(3L);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult mvcResult = mockMvc
                .perform(
                        post("/payments")
                                .content(jsonRequest)
                                .contentType(MediaType
                                        .APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        // Then
        PaymentDto actual = objectMapper
                .readValue(mvcResult
                        .getResponse().getContentAsString(), PaymentDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        Assertions.assertNotNull(actual.getSessionUrl());
        Assertions.assertNotNull(actual.getSessionId());
        Assertions.assertEquals(BigDecimal.valueOf(6100), actual.getAmountToPay());
    }
}
