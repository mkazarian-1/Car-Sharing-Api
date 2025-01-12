package org.example.carsharingapi.telegram.service.command;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.carsharingapi.model.User;
import org.example.carsharingapi.repository.UserRepository;
import org.example.carsharingapi.telegram.model.TelegramUser;
import org.example.carsharingapi.telegram.repository.TelegramUserRepository;
import org.example.carsharingapi.telegram.session.SessionManager;
import org.example.carsharingapi.telegram.session.UserSession;
import org.example.carsharingapi.telegram.util.SendMessageUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class LoginCommandHandler implements CommandHandler {
    private final SessionManager sessionManager;
    private final AuthenticationManager authenticationManager;
    private final TelegramUserRepository telegramUserRepository;
    private final UserRepository userRepository;

    @Override
    public String getCommandName() {
        return "/login";
    }

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.getMessage().getChatId();

        if (!sessionManager.isSession(chatId)) {
            return startNewSession(chatId);
        }

        UserSession userSession = sessionManager.getSession(chatId);

        return switch (userSession.getCurrentStep()) {
            case WAITING_FOR_USERNAME -> handleUsernameInput(chatId, update, userSession);
            case WAITING_FOR_PASSWORD -> handlePasswordInput(chatId, update, userSession);
            default -> SendMessageUtil.createMessage(chatId,
                    "Unknown session state. Please try again.");
        };
    }

    private SendMessage startNewSession(long chatId) {
        sessionManager.startSession(chatId, List.of(getCommandName()));
        return SendMessageUtil.createMessage(chatId, "Enter your username:");
    }

    private SendMessage handleUsernameInput(long chatId, Update update, UserSession userSession) {
        userSession.setUsername(update.getMessage().getText());
        userSession.setCurrentStep(UserSession.LoginStep.WAITING_FOR_PASSWORD);
        return SendMessageUtil.createMessage(chatId, "Enter your password:");
    }

    private SendMessage handlePasswordInput(long chatId, Update update, UserSession userSession) {
        userSession.setPassword(update.getMessage().getText());
        try {
            authenticateUser(userSession);
            saveTelegramUser(chatId, userSession.getUsername());
            sessionManager.endSession(chatId);

            return SendMessageUtil.createMessage(chatId,
                    "Authorization was successful.\nNow we can send you notifications.");
        } catch (AuthenticationException e) {
            userSession.setCurrentStep(UserSession.LoginStep.WAITING_FOR_USERNAME);
            return SendMessageUtil.createMessage(chatId,
                    "The username or password is incorrect. Try again.\n\nEnter your username:");
        }
    }

    private void authenticateUser(UserSession userSession) throws AuthenticationException {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userSession.getUsername(), userSession.getPassword()
                )
        );
    }

    private void saveTelegramUser(long chatId, String username) {
        User user = userRepository.findUserByEmail(username).orElseThrow(() ->
                new IllegalArgumentException("User not found for username: " + username)
        );

        TelegramUser telegramUser = telegramUserRepository.findByUser(user)
                .orElseGet(TelegramUser::new);
        telegramUser.setUser(user);
        telegramUser.setChatId(chatId);

        telegramUserRepository.save(telegramUser);
    }
}
