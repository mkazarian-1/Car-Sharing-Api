package org.example.carsharingapi.payment.impl;

import com.stripe.model.checkout.Session;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.example.carsharingapi.dto.payment.CreatePaymentRequestDto;
import org.example.carsharingapi.dto.payment.PaymentDto;
import org.example.carsharingapi.mapper.PaymentMapper;
import org.example.carsharingapi.model.Payment;
import org.example.carsharingapi.model.Rental;
import org.example.carsharingapi.model.constants.PaymentConstants;
import org.example.carsharingapi.model.enums.PaymentStatus;
import org.example.carsharingapi.model.enums.PaymentType;
import org.example.carsharingapi.payment.PaymentService;
import org.example.carsharingapi.payment.StripeService;
import org.example.carsharingapi.repository.PaymentRepository;
import org.example.carsharingapi.repository.RentalRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
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
        return (userId == null)
                ? paymentRepository.findAll(pageable).map(paymentMapper::toDto)
                : paymentRepository.findAllByRentalUserId(userId, pageable)
                .map(paymentMapper::toDto);
    }

    @Override
    public PaymentDto createPaymentSession(CreatePaymentRequestDto request,
                                           Long userId,
                                           UriComponentsBuilder uriBuilder) {
        Rental rental = rentalRepository.findByIdAndUserId(request.getRentalId(), userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Rental not found for the given ID and user"));

        if (paymentRepository.existsByRental(rental)) {
            throw new IllegalArgumentException("Payment session for this rental already exist");
        }

        BigDecimal amountToPay = calculateAmountToPay(rental, request.getPaymentType());

        String successUrl = buildSuccessUrl(uriBuilder);
        String cancelUrl = buildCancelUrl(uriBuilder);

        Session session = stripeService.createStripeSession(amountToPay, successUrl, cancelUrl);

        Payment payment = createPaymentRecord(rental,
                request.getPaymentType(),
                amountToPay,
                session);

        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    @Override
    public boolean handleSuccess(String sessionId) {
        Payment payment = paymentRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Payment not found fot the given session ID"));

        if (stripeService.isPaymentSuccessful(sessionId)) {
            payment.setStatus(PaymentStatus.PAID);
            paymentRepository.save(payment);
            return true;
        }
        return false;
    }

    private String buildSuccessUrl(UriComponentsBuilder uriBuilder) {
        return uriBuilder.cloneBuilder()
                .path(successUrl)
                .queryParam("sessionId", "{CHECKOUT_SESSION_ID}")
                .build().toUriString();
    }

    private String buildCancelUrl(UriComponentsBuilder uriBuilder) {
        return uriBuilder.cloneBuilder()
                .path(cancelUrl)
                .queryParam("sessionId", "{CHECKOUT_SESSION_ID}")
                .build().toUriString();
    }

    private Payment createPaymentRecord(Rental rental,
                                        PaymentType paymentType,
                                        BigDecimal amountToPay,
                                        Session session) {
        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.PENDING);
        payment.setType(paymentType);
        payment.setRental(rental);
        payment.setAmountToPay(amountToPay);
        payment.setSessionId(session.getId());
        payment.setSessionUrl(session.getUrl());
        return payment;
    }

    private BigDecimal calculateAmountToPay(Rental rental,
                                            PaymentType paymentType) {
        BigDecimal dailyFee = rental.getCar().getDailyFee();
        BigDecimal rentCost = dailyFee.multiply(
                BigDecimal.valueOf(
                        daysBetween(rental.getRentalDate(), rental.getReturnDate())));

        if (paymentType == PaymentType.PAYMENT) {
            return rentCost;
        }

        long overdueDays = Math.max(0, daysBetween(rental.getReturnDate(),
                rental.getActualReturnDate()));
        BigDecimal fine = dailyFee.multiply(BigDecimal.valueOf(overdueDays))
                .multiply(PaymentConstants.FINE_MULTIPLIER);

        return rentCost.add(fine);
    }

    private long daysBetween(LocalDate start, LocalDate end) {
        return (end == null || start == null) ? 0 : ChronoUnit.DAYS.between(start, end);
    }
}
