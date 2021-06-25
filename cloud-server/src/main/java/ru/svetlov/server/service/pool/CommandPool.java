package ru.svetlov.server.service.pool;


import ru.svetlov.domain.command.base.GenericCommand;
import ru.svetlov.server.core.handler.command.CommandHandler;

public interface CommandPool {
    CommandHandler getHandler(GenericCommand command);
}
