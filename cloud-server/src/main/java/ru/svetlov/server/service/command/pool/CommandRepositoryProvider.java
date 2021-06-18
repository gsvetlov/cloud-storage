package ru.svetlov.server.service.command.pool;

import ru.svetlov.server.service.command.handler.CommandHandler;

import java.util.Map;

public interface CommandRepositoryProvider {
    Map<String, CommandHandler> getCollection();
}
