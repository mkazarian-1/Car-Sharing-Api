package org.example.carsharingapi.telegram.service;

public interface NotificationService {
    void sendNotification(Long userId, String message);
}
