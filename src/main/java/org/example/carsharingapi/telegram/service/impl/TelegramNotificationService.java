package org.example.carsharingapi.telegram.service.impl;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.carsharingapi.telegram.TelegramBot;
import org.example.carsharingapi.telegram.model.TelegramUser;
import org.example.carsharingapi.telegram.repository.TelegramUserRepository;
import org.example.carsharingapi.telegram.service.NotificationService;
import org.example.carsharingapi.telegram.util.SendMessageUtil;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class TelegramNotificationService implements NotificationService {
    private final TelegramBot telegramBot;
    private final TelegramUserRepository telegramUserRepository;

    @Override
    public void sendNotification(Long userId, String message) {
        Optional<TelegramUser> nullableTelegramUser = telegramUserRepository.findByUserId(userId);
        if (nullableTelegramUser.isEmpty()) {
            log.info("Can't find telegram info about user with current user id:{}", userId);
            return;
        }
        TelegramUser telegramUser = nullableTelegramUser.get();

        telegramBot.sendMessage(SendMessageUtil
                .createMessage(telegramUser.getChatId(), message));
    }
}
