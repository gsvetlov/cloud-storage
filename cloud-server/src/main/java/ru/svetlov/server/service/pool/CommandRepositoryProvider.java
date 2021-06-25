package ru.svetlov.server.service.pool;

import ru.svetlov.server.core.handler.command.CommandHandler;

import java.util.Map;

public interface CommandRepositoryProvider {
    Map<String, CommandHandler> getCollection();
}
