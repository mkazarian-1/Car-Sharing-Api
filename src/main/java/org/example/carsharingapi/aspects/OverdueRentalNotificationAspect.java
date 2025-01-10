package org.example.carsharingapi.aspects;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.example.carsharingapi.model.Rental;
import org.example.carsharingapi.service.NotificationService;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
@Log4j2
public class OverdueRentalNotificationAspect {
    private final NotificationService notificationService;

    public OverdueRentalNotificationAspect(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @AfterReturning(
            pointcut = "execution(* org.example.carsharingapi.task.OverdueRentalAnalysisTask.analyzeRentals(..))",
            returning = "rentals"
    )

    public void sendNotificationsFromTask(List<Rental> rentals) {
        if (rentals == null || rentals.isEmpty()) {
            log.info("No overdue rentals found for notification.");
            return;
        }

        rentals.forEach(rental -> {
            String message = String.format("Hi %s %s, rental with ID %d is overdue.\n"
                            + "Please complete the rental or you will be charged a fine.",
                    rental.getUser().getFirstName(),
                    rental.getUser().getSecondName(),
                    rental.getId());
            notificationService.sendNotification(rental.getUser().getId(), message);
            log.info("Notification sent for rental ID: {}", rental.getId());
        });
    }
}