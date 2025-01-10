package org.example.carsharingapi.telegram.service.command;

import lombok.RequiredArgsConstructor;
import org.example.carsharingapi.telegram.util.SendMessageUtil;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class HelpCommandHandler implements CommandHandler {
    private final String message;

    public HelpCommandHandler(List<CommandHandler> handlers) {
        message = handlers.stream().map(CommandHandler::getCommandName).reduce("Available commands:",(a,b)-> a +"\n"+ b);
    }

    @Override
    public String getCommandName() {
        return "/help";
    }

    @Override
    public SendMessage handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        return SendMessageUtil.createMessage(chatId, message);
    }
}
