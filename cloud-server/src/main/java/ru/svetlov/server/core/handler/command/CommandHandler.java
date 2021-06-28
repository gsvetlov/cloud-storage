package ru.svetlov.server.core.handler.command;

import ru.svetlov.domain.command.base.GenericCommand;
import ru.svetlov.server.core.common.UserContext;

@FunctionalInterface
public interface CommandHandler {
    GenericCommand process(GenericCommand command, UserContext context);
}
