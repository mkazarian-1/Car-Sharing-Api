package org.example.carsharingapi.payment;

import org.example.carsharingapi.dto.payment.CreatePaymentRequestDto;
import org.example.carsharingapi.dto.payment.PaymentDto;
import org.example.carsharingapi.dto.payment.PaymentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.util.UriComponentsBuilder;

public interface PaymentService {
    Page<PaymentDto> getAllPaymentsByUser(Long userId, Pageable pageable);

    PaymentResponseDto createPaymentSession(CreatePaymentRequestDto request, Long userId, UriComponentsBuilder uriBuilder);

    boolean handleSuccess(String sessionId);
}
