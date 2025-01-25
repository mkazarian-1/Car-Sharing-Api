package org.example.carsharingapi.telegram.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateProcessorService {
    SendMessage processUpdate(Update update);
}
