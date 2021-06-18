package ru.svetlov.server.service.command.pool.impl;

import ru.svetlov.domain.command.base.GenericCommand;
import ru.svetlov.server.service.command.handler.CommandHandler;
import ru.svetlov.server.service.command.pool.CommandPool;
import ru.svetlov.server.service.command.pool.CommandRepositoryProvider;

import java.util.Map;

public class StaticCommandPool implements CommandPool {

    private final Map<String, ru.svetlov.server.service.command.handler.CommandHandler> handlers;

    public StaticCommandPool(CommandRepositoryProvider provider) {
        handlers = provider.getCollection();
    }

    @Override
    public CommandHandler getHandler(GenericCommand command) {
        if (command.getCommand().isEmpty())
            throw new IllegalArgumentException("Command name can't be empty");
        CommandHandler result = handlers.get(command.getCommand());
        if (result == null)
            throw new RuntimeException("Command " + command.getCommand() + " not found");
        return result;
    }
}
