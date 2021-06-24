package ru.svetlov.domain.command;

import ru.svetlov.domain.command.base.RequestCommand;

public class LoginRequest extends RequestCommand {
    private final static String COMMAND = "login_request";

    public LoginRequest(int requestId, String login, String password) {
        super(requestId, COMMAND, new Object[]{ login, password });
    }
}
