package ru.svetlov.server.service.command.pool;


import ru.svetlov.domain.command.base.GenericCommand;
import ru.svetlov.server.service.command.handler.CommandHandler;

public interface CommandPool {
    CommandHandler getHandler(GenericCommand command);
}
