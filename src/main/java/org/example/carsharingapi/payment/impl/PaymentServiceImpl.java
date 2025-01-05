package org.example.carsharingapi.payment.impl;

import com.stripe.model.checkout.Session;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.carsharingapi.dto.payment.CreatePaymentRequestDto;
import org.example.carsharingapi.dto.payment.PaymentDto;
import org.example.carsharingapi.dto.payment.PaymentResponseDto;
import org.example.carsharingapi.mapper.PaymentMapper;
import org.example.carsharingapi.model.Payment;
import org.example.carsharingapi.model.Rental;
import org.example.carsharingapi.model.User;
import org.example.carsharingapi.model.constants.PaymentConstants;
import org.example.carsharingapi.model.enums.PaymentStatus;
import org.example.carsharingapi.model.enums.PaymentType;
import org.example.carsharingapi.repository.PaymentRepository;
import org.example.carsharingapi.repository.RentalRepository;
import org.example.carsharingapi.payment.PaymentService;
import org.example.carsharingapi.payment.StripeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.sql.SQLOutput;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Value("${app.payment.success-url}")
    private String successUrl;

    @Value("${app.payment.cancel-url}")
    private String cancelUrl;

    private final RentalRepository rentalRepository;
    private final PaymentRepository paymentRepository;
    private final StripeService stripeService;
    private final PaymentMapper paymentMapper;

    @Override
    public Page<PaymentDto> getAllPaymentsByUser(Long userId, Pageable pageable) {
        if (userId == null) {
            return paymentRepository.findAll(pageable).map(paymentMapper::toDto);
        }
        return paymentRepository.findAllByRentalUserId(userId, pageable).map(paymentMapper::toDto);
    }

    @Override
    public PaymentResponseDto createPaymentSession(CreatePaymentRequestDto request, Long userId, UriComponentsBuilder uriBuilder) {
        Rental rental = rentalRepository.findByIdAndUserId(request.getRentalId(), userId)
                .orElseThrow(() -> new IllegalArgumentException("Rental not found"));

        BigDecimal amountToPay = calculateAmountToPay(rental, request.getPaymentType());

        String successUrl = uriBuilder.cloneBuilder()
                .path("/payments/success")
                .queryParam("sessionId", "{CHECKOUT_SESSION_ID}")
                .build().toUriString();

        String cancelUrl = uriBuilder.cloneBuilder()
                .path("/payments/cancel")
                .queryParam("sessionId", "{CHECKOUT_SESSION_ID}")
                .build().toUriString();

        Session session = stripeService.createStripeSession(amountToPay, successUrl, cancelUrl);

        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.PENDING);
        payment.setType(request.getPaymentType());
        payment.setRental(rental);
        payment.setSessionId(session.getId());
        payment.setSessionUrl(session.getUrl());
        payment.setAmountToPay(amountToPay);

        return paymentMapper.toResponseDto(paymentRepository.save(payment));
    }

    private BigDecimal calculateAmountToPay(Rental rental, PaymentType paymentType) {
        long rentDays = ChronoUnit.DAYS.between(rental.getRentalDate(), rental.getReturnDate());
        BigDecimal dailyFee = rental.getCar().getDailyFee();
        BigDecimal payForRent = dailyFee.multiply(BigDecimal.valueOf(rentDays));

        if(paymentType == PaymentType.PAYMENT){
            return payForRent;
        }

        long overdueDays = (rental.getActualReturnDate() == null)? 0 : ChronoUnit.DAYS.between(
                rental.getReturnDate(),
                rental.getActualReturnDate());
        if(overdueDays<=0) {
            overdueDays = 0;
        }

        return payForRent.add(dailyFee.multiply(BigDecimal
                        .valueOf(overdueDays)).multiply(PaymentConstants.FINE_MULTIPLIER));
    }

    public boolean handleSuccess(String sessionId) {
        Payment payment = paymentRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found"));

        if (stripeService.isPaymentSuccessful(sessionId)) {
            payment.setStatus(PaymentStatus.PAID);
            paymentRepository.save(payment);
            return true;
        }
        return false;
    }
}
