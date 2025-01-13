package org.example.carsharingapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.carsharingapi.aspects.annotation.NotifyOnCreatePayment;
import org.example.carsharingapi.aspects.annotation.NotifyOnSuccessPayment;
import org.example.carsharingapi.dto.payment.CreatePaymentRequestDto;
import org.example.carsharingapi.dto.payment.PaymentDto;
import org.example.carsharingapi.model.User;
import org.example.carsharingapi.model.enums.UserRole;
import org.example.carsharingapi.payment.PaymentService;
import org.example.carsharingapi.security.util.UserUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@Tag(name = "Payments", description = "Endpoints for managing payments")
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @Operation(summary = "Get payments",
            description = """
                    Retrieves a paginated list of payments.
                    If the user is a manager, they can retrieve payments
                    for any user by specifying the user ID. Otherwise,
                    only the authenticated user's payments are returned.""")
    @GetMapping()
    public Page<PaymentDto> getPayments(@RequestParam(name = "user_id",
            required = false, defaultValue = "#{null}") Long userId, Pageable pageable) {
        User user = UserUtil.getAuthenticatedUser();
        if (user.getRoles().contains(UserRole.MANAGER)) {
            return paymentService.getAllPaymentsByUser(userId, pageable);
        }
        return paymentService.getAllPaymentsByUser(user.getId(), pageable);
    }

    @Operation(summary = "Create a payment session",
            description = "Creates a payment session for the authenticated user.")
    @NotifyOnCreatePayment
    @PostMapping
    public PaymentDto createPaymentSession(@RequestBody CreatePaymentRequestDto request,
                                           UriComponentsBuilder uriBuilder) {
        User user = UserUtil.getAuthenticatedUser();
        return paymentService.createPaymentSession(request, user.getId(), uriBuilder);
    }

    @Operation(summary = "Handle successful payment",
            description = "Handles a successful payment session identified by its session ID.")
    @NotifyOnSuccessPayment
    @GetMapping("/success")
    public ResponseEntity<String> handleSuccess(@RequestParam String sessionId) {
        boolean success = paymentService.handleSuccess(sessionId);
        return ResponseEntity.ok(success ? "Payment successful!" : "Payment verification failed.");
    }

    @Operation(summary = "Handle canceled payment",
            description = "Handles a canceled payment session identified by its session ID.")
    @GetMapping("/cancel")
    public ResponseEntity<String> handleCancel(@RequestParam String sessionId) {
        return ResponseEntity.ok("Payment session canceled. You can retry within 24 hours.");
    }
}
