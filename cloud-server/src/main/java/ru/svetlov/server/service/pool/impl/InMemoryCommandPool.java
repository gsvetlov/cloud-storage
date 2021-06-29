package ru.svetlov.server.service.pool.impl;

import ru.svetlov.domain.command.base.GenericCommand;
import ru.svetlov.server.core.handler.command.CommandHandler;
import ru.svetlov.server.service.pool.CommandPool;
import ru.svetlov.server.service.pool.CommandRepositoryProvider;

import java.util.Map;

public class InMemoryCommandPool implements CommandPool {

    private final Map<String, CommandHandler> handlers;

    public InMemoryCommandPool(CommandRepositoryProvider provider) {
        handlers = provider.getCollection();
    }

    @Override
    public CommandHandler getHandler(GenericCommand command) {
        if (command.getCommand().isEmpty())
            throw new IllegalArgumentException("Command name can't be empty");
        return handlers.get(command.getCommand());
    }
}
