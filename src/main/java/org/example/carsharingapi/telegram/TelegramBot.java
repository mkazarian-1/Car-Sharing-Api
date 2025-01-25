package org.example.carsharingapi.telegram;

import lombok.extern.log4j.Log4j2;
import org.example.carsharingapi.telegram.service.UpdateProcessorService;
import org.example.carsharingapi.telegram.service.impl.UpdateProcessorServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Log4j2
public class TelegramBot extends TelegramLongPollingBot {
    private final UpdateProcessorService updateProcessorServiceImpl;
    private final String botName;

    public TelegramBot(@Value("${telegram.bot.token}") String botToken,
                       @Value("${telegram.bot.name}") String botName,
                       UpdateProcessorServiceImpl updateProcessorServiceImpl) {
        super(botToken);
        this.botName = botName;
        this.updateProcessorServiceImpl = updateProcessorServiceImpl;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        sendMessage(updateProcessorServiceImpl.processUpdate(update));
    }

    public void sendMessage(SendMessage message) {
        try {
            if (message != null) {
                this.execute(message);
                log.info("Message sent to chat ID: {}", message.getChatId());
            } else {
                log.info("Message is null");
            }
        } catch (TelegramApiException e) {
            log.error("Failed to send message to chat ID: {}", message.getChatId(), e);
        }
    }
}
