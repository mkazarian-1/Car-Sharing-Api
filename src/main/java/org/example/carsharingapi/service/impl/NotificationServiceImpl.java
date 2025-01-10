package org.example.carsharingapi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.carsharingapi.dto.rental.RentalDto;
import org.example.carsharingapi.model.User;
import org.example.carsharingapi.repository.UserRepository;
import org.example.carsharingapi.service.NotificationService;
import org.example.carsharingapi.telegram.controller.TelegramController;
import org.example.carsharingapi.telegram.model.TelegramUser;
import org.example.carsharingapi.telegram.repository.TelegramUserRepository;
import org.example.carsharingapi.telegram.util.SendMessageUtil;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class NotificationServiceImpl implements NotificationService {
    private final TelegramController telegramController;
    private final TelegramUserRepository telegramUserRepository;

    @Override
    public void sendNotification(Long userId, String message) {
        Optional<TelegramUser> nullableTelegramUser = telegramUserRepository.findByUserId(userId);
        if(nullableTelegramUser.isEmpty()){
            log.info("Can't find telegram info about user with current user id:{}", userId);
            return;
        }
        TelegramUser telegramUser = nullableTelegramUser.get();

        telegramController.sendMessage(SendMessageUtil.createMessage(telegramUser.getChatId(), message));
    }
}
