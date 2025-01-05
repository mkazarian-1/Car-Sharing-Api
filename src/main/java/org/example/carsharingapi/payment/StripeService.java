package org.example.carsharingapi.payment;

import com.stripe.model.checkout.Session;

import java.math.BigDecimal;

public interface StripeService {
    boolean isPaymentSuccessful(String sessionId);

    Session createStripeSession(BigDecimal amountToPay, String successUrl, String cancelUrl);
}
