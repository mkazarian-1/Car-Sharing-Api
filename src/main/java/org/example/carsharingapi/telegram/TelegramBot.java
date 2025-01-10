package org.example.carsharingapi.telegram;



import jakarta.annotation.PostConstruct;
import org.example.carsharingapi.telegram.controller.TelegramController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final TelegramController telegramController;
    private final String botName;

    @PostConstruct
    public void init(){
        telegramController.initTelegramBot(this);
    }

    public TelegramBot(@Value("${telegram.bot.token}") String botToken,
                       @Value("${telegram.bot.name}") String botName, TelegramController telegramController) {
        super(botToken);
        this.botName = botName;
        this.telegramController = telegramController;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        telegramController.processUpdate(update);
    }
}
