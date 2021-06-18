package ru.svetlov.server.service.command.handler;

import ru.svetlov.domain.command.base.GenericCommand;
@FunctionalInterface
public interface CommandHandler {
    GenericCommand process(GenericCommand command);
}
