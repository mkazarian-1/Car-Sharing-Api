package org.example.carsharingapi.aspects;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.example.carsharingapi.dto.payment.PaymentDto;
import org.example.carsharingapi.model.Payment;
import org.example.carsharingapi.repository.PaymentRepository;
import org.example.carsharingapi.service.NotificationService;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class PaymentNotificationAspect {
    private final NotificationService notificationService;
    private final PaymentRepository paymentRepository;

    @AfterReturning(
            value = "@annotation"
                    + "(org.example.carsharingapi.aspects.annotation.NotifyOnCreatePayment)",
            returning = "paymentDto")
    public void notifyOnCreatePayment(Object paymentDto) {
        if (paymentDto instanceof PaymentDto payment) {
            String message = String.format("Payment of %s %s has been "
                            + "created for rental ID %d.",
                    payment.getAmountToPay(),
                    payment.getType(),
                    payment.getRental().getId());

            notificationService.sendNotification(payment.getRental().getUserId(), message);
        }
    }

    @AfterReturning(
            value = "@annotation"
                    + "(org.example.carsharingapi.aspects.annotation.NotifyOnSuccessPayment)")
    public void notifyOnSuccess(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String sessionId = (String) args[0];

        Optional<Payment> optionalPayment = paymentRepository.findBySessionId(sessionId);

        if (optionalPayment.isPresent()) {
            Payment payment = optionalPayment.get();

            String message = String.format("Payment of %s %s has been "
                            + "successfully paid for rental ID %d.",
                    payment.getAmountToPay(),
                    payment.getType(),
                    payment.getRental().getId());

            notificationService.sendNotification(payment.getRental().getUser().getId(), message);
        }
    }
}
