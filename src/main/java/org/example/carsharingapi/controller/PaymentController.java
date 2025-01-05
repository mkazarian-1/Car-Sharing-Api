package org.example.carsharingapi.controller;

import lombok.RequiredArgsConstructor;
import org.example.carsharingapi.dto.payment.CreatePaymentRequestDto;
import org.example.carsharingapi.dto.payment.PaymentDto;
import org.example.carsharingapi.dto.payment.PaymentResponseDto;
import org.example.carsharingapi.model.User;
import org.example.carsharingapi.model.enums.UserRole;
import org.example.carsharingapi.payment.PaymentService;
import org.example.carsharingapi.security.util.UserUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping()
    public Page<PaymentDto> getPayments(@RequestParam(name = "user_id", required = false,
            defaultValue = "#{null}") Long userId, Pageable pageable){
        User user = UserUtil.getAuthenticatedUser();
        if(user.getRoles().contains(UserRole.MANAGER)){
            return paymentService.getAllPaymentsByUser(userId, pageable);
        }
        return paymentService.getAllPaymentsByUser(user.getId(), pageable);
    }

    @PostMapping
    public PaymentResponseDto createPaymentSession(//TODO - add validation
                                                   @RequestBody CreatePaymentRequestDto request,
                                                   UriComponentsBuilder uriBuilder) {
        User user = UserUtil.getAuthenticatedUser();
        return paymentService.createPaymentSession(request, user.getId(), uriBuilder);
    }

    @GetMapping("/success")
    public ResponseEntity<String> handleSuccess(@RequestParam String sessionId) {
        boolean success = paymentService.handleSuccess(sessionId);
        return ResponseEntity.ok(success ? "Payment successful!" : "Payment verification failed.");
    }

    @GetMapping("/cancel")
    public ResponseEntity<String> handleCancel(@RequestParam String sessionId) {
        return ResponseEntity.ok("Payment session canceled. You can retry within 24 hours.");
    }
}