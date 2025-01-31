package org.example.carsharingapi.telegram.service.impl;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.carsharingapi.model.TelegramUser;
import org.example.carsharingapi.repository.TelegramUserRepository;
import org.example.carsharingapi.telegram.TelegramBot;
import org.example.carsharingapi.telegram.service.NotificationService;
import org.example.carsharingapi.telegram.util.SendMessageUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class NotificationServiceImpl implements NotificationService {
    private final TelegramBot telegramBot;
    private final TelegramUserRepository telegramUserRepository;

    @Async
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
