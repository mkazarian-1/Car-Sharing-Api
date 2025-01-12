package org.example.carsharingapi.aspects;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.example.carsharingapi.dto.rental.RentalDto;
import org.example.carsharingapi.service.NotificationService;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Log4j2
@RequiredArgsConstructor
public class RentalNotificationAspect {
    private final NotificationService notificationService;

    @AfterReturning(
            pointcut = "@annotation"
                    + "(org.example.carsharingapi.aspects.annotation.NotifyOnCreateRental))",
            returning = "rentalDto")
    public void sendNotificationAfterRental(Object rentalDto) {
        if (rentalDto instanceof RentalDto rental) {
            String message = String.format("Hi, your rental with id: %d"
                    + "\nhas been processed!", rental.getId());
            notificationService.sendNotification(rental.getUserId(), message);
        }
    }
}
