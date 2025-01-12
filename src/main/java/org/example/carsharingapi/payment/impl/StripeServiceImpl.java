package org.example.carsharingapi.payment.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import org.example.carsharingapi.payment.StripeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeServiceImpl implements StripeService {

    public StripeServiceImpl(@Value("${stripe.secret.key}") String secretKey) {
        Stripe.apiKey = secretKey;
    }

    @Override
    public boolean isPaymentSuccessful(String sessionId) {
        try {
            Session session = Session.retrieve(sessionId);
            return "paid".equals(session.getPaymentStatus());
        } catch (StripeException e) {
            throw new RuntimeException("Failed to verify payment", e);
        }
    }

    @Override
    public Session createStripeSession(BigDecimal amountToPay,
                                       String successUrl,
                                       String cancelUrl) {
        try {
            long amountInCents = amountToPay.multiply(BigDecimal.valueOf(100)).longValueExact();

            SessionCreateParams params = SessionCreateParams.builder()
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(cancelUrl)
                    .addLineItem(SessionCreateParams.LineItem.builder()
                            .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("usd")
                                    .setUnitAmount(amountInCents)
                                    .setProductData(SessionCreateParams
                                            .LineItem.PriceData.ProductData.builder()
                                            .setName("Rental Payment")
                                            .build())
                                    .build())
                            .setQuantity(1L)
                            .build())
                    .build();

            return Session.create(params);
        } catch (StripeException e) {
            throw new RuntimeException("Stripe session creation failed", e);
        }
    }
}
