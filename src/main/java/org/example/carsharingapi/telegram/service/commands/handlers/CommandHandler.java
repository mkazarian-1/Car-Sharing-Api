package org.example.carsharingapi.telegram.service.commands.handlers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface CommandHandler {
    String getCommandName();

    SendMessage handle(Update update);
}
