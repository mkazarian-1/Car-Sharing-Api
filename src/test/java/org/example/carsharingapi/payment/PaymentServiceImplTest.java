package org.example.carsharingapi.payment;

import com.stripe.model.checkout.Session;
import jakarta.persistence.EntityNotFoundException;
import org.example.carsharingapi.dto.payment.CreatePaymentRequestDto;
import org.example.carsharingapi.dto.payment.PaymentDto;
import org.example.carsharingapi.mapper.PaymentMapper;
import org.example.carsharingapi.mapper.impl.CarMapperImpl;
import org.example.carsharingapi.mapper.impl.PaymentMapperImpl;
import org.example.carsharingapi.mapper.impl.RentalMapperImpl;
import org.example.carsharingapi.model.Car;
import org.example.carsharingapi.model.Payment;
import org.example.carsharingapi.model.Rental;
import org.example.carsharingapi.model.User;
import org.example.carsharingapi.model.enums.PaymentStatus;
import org.example.carsharingapi.model.enums.PaymentType;
import org.example.carsharingapi.payment.impl.PaymentServiceImpl;
import org.example.carsharingapi.repository.PaymentRepository;
import org.example.carsharingapi.repository.RentalRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.util.UriComponentsBuilder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private StripeService stripeService;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Spy
    private PaymentMapper paymentMapper = new PaymentMapperImpl(new RentalMapperImpl(new CarMapperImpl()));

    @Test
    @DisplayName("Get all payments by user - success")
    void testGetAllPaymentsByUser_Success() {
        // given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Payment> paymentPage = new PageImpl<>(List.of(createPayment()));
        when(paymentRepository.findAllByRentalUserId(userId, pageable)).thenReturn(paymentPage);

        // when
        Page<PaymentDto> result = paymentService.getAllPaymentsByUser(userId, pageable);

        // then
        assertEquals(1, result.getContent().size());
        assertEquals("PENDING", result.getContent().get(0).getStatus().name());
        verify(paymentRepository).findAllByRentalUserId(userId, pageable);
    }

    @Test
    @DisplayName("Create payment session - success")
    void testCreatePaymentSession_Success() {
        // given
        Long userId = 1L;
        CreatePaymentRequestDto request = new CreatePaymentRequestDto();
        request.setRentalId(1L);
        request.setPaymentType(PaymentType.PAYMENT);

        Rental rental = createRental();
        when(rentalRepository.findByIdAndUserId(1L, userId)).thenReturn(Optional.of(rental));
        when(paymentRepository.existsByRental(rental)).thenReturn(false);
        when(paymentRepository.save(any())).thenReturn(createPayment());

        Session sessionMock = createSessionMock();
        when(stripeService.createStripeSession(any(BigDecimal.class), anyString(), anyString()))
                .thenReturn(sessionMock);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();

        // when
        PaymentDto result = paymentService.createPaymentSession(request, userId, uriBuilder);

        // then
        assertNotNull(result);
        assertEquals("https://example.com/session", result.getSessionUrl());
        verify(rentalRepository).findByIdAndUserId(1L, userId);
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    @DisplayName("Create payment session - rental not found")
    void testCreatePaymentSessionRental_NotFound() {
        // given
        Long userId = 1L;
        CreatePaymentRequestDto request = new CreatePaymentRequestDto();
        request.setRentalId(1L);
        request.setPaymentType(PaymentType.PAYMENT);

        when(rentalRepository.findByIdAndUserId(1L, userId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () ->
                paymentService.createPaymentSession(request, userId, UriComponentsBuilder.newInstance()));
    }

    @Test
    @DisplayName("Handle success - payment successful")
    void testHandleSuccessPayment_Successful() {
        // given
        String sessionId = "session_123";
        Payment payment = createPayment();
        when(paymentRepository.findBySessionId(sessionId)).thenReturn(Optional.of(payment));
        when(stripeService.isPaymentSuccessful(sessionId)).thenReturn(true);

        // when
        boolean result = paymentService.handleSuccess(sessionId);

        // then
        assertTrue(result);
        assertEquals(PaymentStatus.PAID, payment.getStatus());
        verify(paymentRepository).save(payment);
    }

    @Test
    @DisplayName("Handle success - payment not successful")
    void testHandleSuccessPayment_NotSuccessful() {
        // given
        String sessionId = "session_123";
        Payment payment = createPayment();
        when(paymentRepository.findBySessionId(sessionId)).thenReturn(Optional.of(payment));
        when(stripeService.isPaymentSuccessful(sessionId)).thenReturn(false);

        // when
        boolean result = paymentService.handleSuccess(sessionId);

        // then
        assertFalse(result);
        assertEquals(PaymentStatus.PENDING, payment.getStatus());
        verify(paymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Handle success - payment not found")
    void testHandleSuccessPayment_NotFound() {
        // given
        String sessionId = "session_123";
        when(paymentRepository.findBySessionId(sessionId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> paymentService.handleSuccess(sessionId));
    }

    private Rental createRental() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("Old First Name");
        user.setFirstName("Old Second Name");

        Car car = new Car();
        car.setBrand("Toyota");
        car.setDailyFee(BigDecimal.valueOf(50));

        Rental rental = new Rental();
        rental.setId(1L);
        rental.setUser(user);
        rental.setRentalDate(LocalDate.now());
        rental.setReturnDate(LocalDate.now().plusDays(5));
        rental.setCar(car);
        return rental;
    }

    private Payment createPayment() {
        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.PENDING);
        payment.setType(PaymentType.PAYMENT);
        payment.setAmountToPay(BigDecimal.valueOf(250));
        payment.setSessionId("session_123");
        payment.setSessionUrl("https://example.com/session");
        return payment;
    }

    private Session createSessionMock() {
        Session session = mock(Session.class);
        when(session.getId()).thenReturn("session_123");
        when(session.getUrl()).thenReturn("https://example.com/session");
        return session;
    }
}
