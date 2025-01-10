package org.example.carsharingapi.telegram.service.command;

import org.example.carsharingapi.telegram.util.SendMessageUtil;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class StartCommandHandler implements CommandHandler{

    @Override
    public String getCommandName() {
        return "/start";
    }

    @Override
    public SendMessage handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        return SendMessageUtil.createMessage(chatId,
                "Welcome to the bot! Use command /login is you want to get notification.");
    }
}
