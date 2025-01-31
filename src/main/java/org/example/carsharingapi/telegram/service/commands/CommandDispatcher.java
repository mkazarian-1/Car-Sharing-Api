package org.example.carsharingapi.telegram.service.commands;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.example.carsharingapi.telegram.service.commands.handlers.CommandHandler;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class CommandDispatcher {
    private final Map<String, CommandHandler> commandHandlers;

    public CommandDispatcher(List<CommandHandler> handlers) {
        this.commandHandlers = handlers.stream()
                .collect(Collectors.toMap(CommandHandler::getCommandName, handler -> handler));
    }

    public CommandHandler dispatch(String command) {
        return commandHandlers.get(command);
    }

    public boolean isCommandExist(String command) {
        return commandHandlers.containsKey(command);
    }
}
