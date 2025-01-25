package org.example.carsharingapi.aspects;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.example.carsharingapi.dto.rental.RentalDto;
import org.example.carsharingapi.telegram.service.ManagerNotificationService;
import org.example.carsharingapi.telegram.service.NotificationService;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Log4j2
@RequiredArgsConstructor
public class RentalNotificationAspect {
    private final NotificationService notificationService;
    private final ManagerNotificationService managerNotificationService;

    @AfterReturning(
            pointcut = "@annotation"
                    + "(org.example.carsharingapi.aspects.annotation.NotifyOnCreateRental))",
            returning = "rentalDto")
    public void sendNotificationAfterRental(Object rentalDto) {
        if (rentalDto instanceof RentalDto rental) {
            String message = String.format("""
                    Hi, your rental:\r
                        Id:%d\r
                        Car:%s %s %s\r
                        Date:%s - %s\r
                    has been created!""",
                    rental.getId(),
                    rental.getCar().getBrand(),
                    rental.getCar().getModel(),
                    rental.getCar().getType(),
                    rental.getRentalDate(), rental.getReturnDate());
            notificationService.sendNotification(rental.getUserId(), message);

            String managerMessage = String.format("""
                    User with id: %s\r
                    Created rental:\r
                        Id:%d\r
                        Car:%s %s %s\r
                    Date:%s - %s""",
                    rental.getUserId(),
                    rental.getId(),
                    rental.getCar().getBrand(),
                    rental.getCar().getModel(),
                    rental.getCar().getType(),
                    rental.getRentalDate(), rental.getReturnDate());

            managerNotificationService.notifyAllManagers(managerMessage);
        }
    }
}
