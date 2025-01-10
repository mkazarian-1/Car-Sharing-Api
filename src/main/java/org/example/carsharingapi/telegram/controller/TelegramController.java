package org.example.carsharingapi.telegram.controller;

import lombok.extern.log4j.Log4j2;
import org.example.carsharingapi.telegram.TelegramBot;
import org.example.carsharingapi.telegram.service.CommandDispatcher;
import org.example.carsharingapi.telegram.session.SessionManager;
import org.example.carsharingapi.telegram.util.SendMessageUtil;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@Log4j2
public class TelegramController {
    private final CommandDispatcher commandDispatcher;
    private final SessionManager sessionManager;
    private TelegramBot telegramBot;

    public TelegramController(CommandDispatcher commandDispatcher,
                              SessionManager sessionManager) {
        this.commandDispatcher = commandDispatcher;
        this.sessionManager = sessionManager;
    }

    public void initTelegramBot(TelegramBot telegramBot) {
        if (telegramBot == null) {
            throw new IllegalArgumentException("TelegramBot cannot be null");
        }
        this.telegramBot = telegramBot;
    }

    public void processUpdate(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            processTextMessage(update);
        } else {
            log.warn("Unsupported update type received: {}", update);
        }
    }

    public void sendMessage(SendMessage message) {
        try {
            telegramBot.execute(message);
            log.info("Message sent to chat ID: {}", message.getChatId());
        } catch (TelegramApiException e) {
            log.error("Failed to send message to chat ID: {}", message.getChatId(), e);
        }
    }

    private void processTextMessage(Update update) {
        long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();

        if (commandDispatcher.isCommandExist(messageText)) {
            handleCommand(chatId, messageText, update);
        } else if (sessionManager.isSession(chatId)) {
            handleSession(chatId, update);
        } else {
            sendMessage(SendMessageUtil.createMessage(chatId, "Command not recognized. Use /help to see available commands."));
        }
    }

    private void handleCommand(long chatId, String messageText, Update update) {
        log.info("Command '{}' received from chat ID: {}", messageText, chatId);
        sessionManager.endSession(chatId);
        SendMessage response = commandDispatcher.dispatch(messageText).handle(update);
        sendMessage(response);
    }

    private void handleSession(long chatId, Update update) {
        String activeCommand = sessionManager.getSession(chatId).getCommandList().getFirst();
        log.info("Session active for chat ID: {} with command: {}", chatId, activeCommand);
        SendMessage response = commandDispatcher.dispatch(activeCommand).handle(update);
        sendMessage(response);
    }
}
