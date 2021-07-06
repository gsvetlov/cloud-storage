package ru.svetlov.domain.command;

import ru.svetlov.domain.command.base.CommandType;
import ru.svetlov.domain.command.base.RequestCommand;

public class LoginRequest extends RequestCommand {

    public LoginRequest(int requestId, String login, String password) {
        super(requestId, CommandType.LOGIN_REQUEST, new Object[]{ login, password });
    }
}
