package org.example.carsharingapi.telegram;

import lombok.extern.log4j.Log4j2;
import org.example.carsharingapi.model.User;
import org.example.carsharingapi.telegram.model.TelegramUser;
import org.example.carsharingapi.telegram.repository.TelegramUserRepository;
import org.example.carsharingapi.telegram.service.impl.NotificationServiceImpl;
import org.example.carsharingapi.telegram.util.SendMessageUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Log4j2
class NotificationServiceImplTest {

    @Mock
    private TelegramBot telegramBot;

    @Mock
    private TelegramUserRepository telegramUserRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    @DisplayName("sendNotification - user exists, message sent")
    void testSendNotification_UserExists_MessageSent() {
        // given
        User user = new User();
        user.setId(1L);
        user.setFirstName("Old First Name");
        user.setFirstName("Old Second Name");

        Long userId = 1L;
        String message = "Hello, this is a test notification!";
        TelegramUser telegramUser = new TelegramUser();
        telegramUser.setUser(user);
        telegramUser.setChatId(12345L);

        when(telegramUserRepository.findByUserId(userId)).thenReturn(Optional.of(telegramUser));

        // when
        notificationService.sendNotification(userId, message);
        SendMessage expected = SendMessageUtil.createMessage(telegramUser.getChatId(), message);
        // then
        verify(telegramUserRepository).findByUserId(userId);
        verify(telegramBot).sendMessage(expected);
        verifyNoMoreInteractions(telegramBot);
    }

    @Test
    @DisplayName("sendNotification - user does not exist, no message sent")
    void testSendNotification_UserDoesNotExist_NoMessageSent() {
        // given
        Long userId = 2L;
        String message = "Hello, this is a test notification!";
        when(telegramUserRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // when
        notificationService.sendNotification(userId, message);

        // then
        verify(telegramUserRepository).findByUserId(userId);
        verifyNoInteractions(telegramBot);
    }
}
