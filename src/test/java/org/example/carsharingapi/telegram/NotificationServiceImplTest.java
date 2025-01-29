package org.example.carsharingapi.telegram;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.example.carsharingapi.model.TelegramUser;
import org.example.carsharingapi.model.User;
import org.example.carsharingapi.repository.TelegramUserRepository;
import org.example.carsharingapi.telegram.service.impl.NotificationServiceImpl;
import org.example.carsharingapi.telegram.util.SendMessageUtil;
import org.example.carsharingapi.util.UserTestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

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
        User user = UserTestUtil.getUser();

        TelegramUser telegramUser = new TelegramUser();
        telegramUser.setUser(user);
        telegramUser.setChatId(12345L);

        when(telegramUserRepository
                .findByUserId(user.getId())).thenReturn(Optional.of(telegramUser));

        String message = "Hello, this is a test notification!";
        // when
        notificationService.sendNotification(user.getId(), message);
        SendMessage expected =
                SendMessageUtil.createMessage(telegramUser.getChatId(), message);
        // then
        verify(telegramUserRepository).findByUserId(user.getId());
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
