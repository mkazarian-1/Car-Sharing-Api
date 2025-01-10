package org.example.carsharingapi.aspects;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.example.carsharingapi.dto.rental.RentalDto;
import org.example.carsharingapi.service.NotificationService;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Log4j2
public class RentalNotificationAspect {
    private final NotificationService notificationService;

    public RentalNotificationAspect(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @AfterReturning(pointcut = "execution(* org.example.carsharingapi.controller.RentalController.addRental(..))",
            returning = "rentalDto")
    public void sendNotificationAfterRental(Object rentalDto) {
        if (rentalDto instanceof RentalDto rental) {
            String report = String.format("Rental added successfully: %s", rental);
            log.info("Sending notification: {}", report);

            String message = String.format("Hi, your rental with id: %d\nhas been processed!",rental.getId());
            notificationService.sendNotification(rental.getUserId(), message);
        }
    }


}
