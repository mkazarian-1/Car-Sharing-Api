package org.example.carsharingapi.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.carsharingapi.model.Rental;
import org.example.carsharingapi.service.OverdueRentalAnalysisService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class OverdueRentalAnalysisTask {
    private final OverdueRentalAnalysisService overdueRentalAnalysisService;

    @Async
    @Scheduled(cron = "0 0 1 * * ?")
    public void analyzeRentals() {
        log.info("Starting rental analysis task at {}, by thread: {}",
                LocalDateTime.now(),
                Thread.currentThread().getName());

        List<Rental> expiredRentals = overdueRentalAnalysisService.findOverdueRentals(LocalDate.now());
        if (expiredRentals.isEmpty()) {
            log.debug("No rentals overdue today!");
        } else {
            expiredRentals.forEach(rental -> log.debug(
                    "Processing overdue rental with ID: {} for user ID: {}",
                    rental.getId(),
                    rental.getUser().getId()));
        }
        log.debug("Rental analysis task completed successfully by thread: {}",
                Thread.currentThread().getName());
    }
}
