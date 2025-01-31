package org.example.carsharingapi.payment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stripe.exception.ApiException;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import org.example.carsharingapi.payment.impl.StripeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StripeServiceImplTest {
    private static final String STRIPE_SECRET_KEY = "test_secret_key";

    private StripeService stripeService;

    @Mock
    private Session sessionMock;

    @BeforeEach
    void setUp() {
        stripeService = new StripeServiceImpl(STRIPE_SECRET_KEY);
    }

    @Test
    @DisplayName("Should return true if payment is successful")
    void testIsPaymentSuccessful_PaymentSuccessful() throws StripeException {
        // given
        String sessionId = "test_session_id";

        try (MockedStatic<Session> ignored = mockStatic(Session.class)) {
            when(Session.retrieve(sessionId)).thenReturn(sessionMock);
            when(sessionMock.getPaymentStatus()).thenReturn("paid");
            // when
            boolean result = stripeService.isPaymentSuccessful(sessionId);

            // then
            assertTrue(result);
            verify(sessionMock).getPaymentStatus();
        }
    }

    @Test
    @DisplayName("Should return false if payment is not successful")
    void testIsPaymentSuccessful_PaymentNotSuccessful() throws StripeException {
        String sessionId = "test_session_id";

        try (MockedStatic<Session> ignored = mockStatic(Session.class)) {
            when(Session.retrieve(sessionId)).thenReturn(sessionMock);
            when(sessionMock.getPaymentStatus()).thenReturn("unpaid");

            // when
            boolean result = stripeService.isPaymentSuccessful(sessionId);

            // then
            assertFalse(result);
            verify(sessionMock).getPaymentStatus();
        }
    }

    @Test
    @DisplayName("""
            Should throw RuntimeException
            when StripeException occurs in isPaymentSuccessful
            """)
    void testIsPaymentSuccessful_ApiException() throws StripeException {
        // given
        String sessionId = "test_session_id";

        try (MockedStatic<Session> ignored = mockStatic(Session.class)) {
            when(Session.retrieve(sessionId)).thenThrow(new ApiException(
                    "Stripe API error", null, null, 0, null
            ));

            // when
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> stripeService.isPaymentSuccessful(sessionId));

            // then
            assertEquals("Failed to verify payment", exception.getMessage());
        }
    }

    @Test
    @DisplayName("Should create a Stripe session successfully")
    void testCreateStripeSession_Success() throws StripeException {
        // given
        BigDecimal amountToPay = BigDecimal.valueOf(100.00);
        String successUrl = "https://success.url";
        String cancelUrl = "https://cancel.url";

        Session sessionMock = mock(Session.class);
        try (MockedStatic<Session> ignored = mockStatic(Session.class)) {
            when(Session.create(any(SessionCreateParams.class))).thenReturn(sessionMock);

            // when
            Session result = stripeService
                    .createStripeSession(amountToPay, successUrl, cancelUrl);

            // then
            assertNotNull(result);
        }
    }

    @Test
    @DisplayName("""
            Should throw RuntimeException
            when StripeException occurs in createStripeSession
            """)
    void testCreateStripeSession_StripeException() throws StripeException {
        // given
        BigDecimal amountToPay = BigDecimal.valueOf(100.00);
        String successUrl = "https://success.url";
        String cancelUrl = "https://cancel.url";

        try (MockedStatic<Session> ignored = mockStatic(Session.class)) {
            when(Session.create(any(SessionCreateParams.class)))
                    .thenThrow(new ApiException(
                            "Stripe API error", null, null, 0, null
                    ));

            // when
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> stripeService.createStripeSession(amountToPay, successUrl, cancelUrl));

            // then
            assertEquals("Stripe session creation failed", exception.getMessage());
        }
    }
}
