package ru.svetlov.domain.command.base;

import lombok.Getter;

@Getter
public abstract class ReplyCommand extends RequestCommand {
    protected int replyId;

    public ReplyCommand(int replyId, int requestId, String command, Object[] parameters) {
        super(requestId, command, parameters);
    }

}
