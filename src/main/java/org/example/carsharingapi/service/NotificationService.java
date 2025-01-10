package org.example.carsharingapi.service;

import org.example.carsharingapi.dto.rental.RentalDto;

public interface NotificationService {
    void sendNotification(Long userId, String message);
}
