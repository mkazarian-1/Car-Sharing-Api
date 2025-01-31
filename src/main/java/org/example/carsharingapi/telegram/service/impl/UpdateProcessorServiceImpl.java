package org.example.carsharingapi.telegram.service.impl;

import lombok.extern.log4j.Log4j2;
import org.example.carsharingapi.telegram.service.UpdateProcessorService;
import org.example.carsharingapi.telegram.service.commands.CommandDispatcher;
import org.example.carsharingapi.telegram.session.SessionManager;
import org.example.carsharingapi.telegram.util.SendMessageUtil;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@Log4j2
public class UpdateProcessorServiceImpl implements UpdateProcessorService {
    private final CommandDispatcher commandDispatcher;
    private final SessionManager sessionManager;

    public UpdateProcessorServiceImpl(CommandDispatcher commandDispatcher,
                                      SessionManager sessionManager) {
        this.commandDispatcher = commandDispatcher;
        this.sessionManager = sessionManager;
    }

    @Override
    public SendMessage processUpdate(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            return processTextMessage(update);
        } else {
            log.warn("Unsupported update type received: {}", update);
            return null;
        }
    }

    private SendMessage processTextMessage(Update update) {
        long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();

        if (commandDispatcher.isCommandExist(messageText)) {
            return handleCommand(chatId, messageText, update);
        } else if (sessionManager.isSession(chatId)) {
            return handleSession(chatId, update);
        } else {
            return SendMessageUtil.createMessage(chatId,
                    "Command not recognized. Use /help to see available commands.");
        }
    }

    private SendMessage handleCommand(long chatId, String messageText, Update update) {
        log.info("Command '{}' received from chat ID: {}", messageText, chatId);
        sessionManager.endSession(chatId);
        return commandDispatcher.dispatch(messageText).handle(update);
    }

    private SendMessage handleSession(long chatId, Update update) {
        String activeCommand = sessionManager.getSession(chatId).getCommandList().getFirst();
        log.info("Session active for chat ID: {} with command: {}", chatId, activeCommand);
        return commandDispatcher.dispatch(activeCommand).handle(update);
    }
}
