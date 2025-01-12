package org.example.carsharingapi.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.carsharingapi.model.Rental;
import org.example.carsharingapi.service.NotificationService;
import org.example.carsharingapi.service.OverdueRentalProcessor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class OverdueRentalAnalysisTask {
    private final OverdueRentalProcessor overdueRentalProcessor;
    private final NotificationService notificationService;

    @Async
    @Scheduled(cron = "0 0 1 * * ?")
    public void analyzeRentals() {
        log.info("Starting rental analysis task at {}, by thread: {}",
                LocalDateTime.now(),
                Thread.currentThread().getName());

        List<Rental> expiredRentals = overdueRentalProcessor.findOverdueRentals(LocalDate.now());

        if (expiredRentals.isEmpty()) {
            log.debug("No rentals overdue today!");
        } else {
            expiredRentals.forEach(rental -> log.debug(
                    "Processing overdue rental with ID: {} for user ID: {}",
                    rental.getId(),
                    rental.getUser().getId()));

            overdueRentalNotifier(expiredRentals);
        }
        log.debug("Rental analysis task completed successfully by thread: {}",
                Thread.currentThread().getName());
    }

    private void overdueRentalNotifier(List<Rental> rentals) {
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
