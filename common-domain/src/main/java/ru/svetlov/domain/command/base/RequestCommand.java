package ru.svetlov.domain.command.base;

import lombok.Getter;

@Getter
public abstract class RequestCommand extends GenericCommand {
    protected int requestId;

    public RequestCommand(int requestId, String command, Object[] parameters) {
        super(command, parameters);
        this.requestId = requestId;
    }
}
