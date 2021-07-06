package ru.svetlov.domain.command.base;

import lombok.Getter;

@Getter
public abstract class ReplyCommand extends RequestCommand {
    public ReplyCommand(int requestId, CommandType command, Object[] parameters) {
        super(requestId, command, parameters);
    }

}
