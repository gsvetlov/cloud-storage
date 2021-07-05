package ru.svetlov.server.service.jdbc;

import ru.svetlov.server.service.jdbc.domain.AuthenticationResult;

public interface AuthenticationProvider {
    AuthenticationResult authenticate(String login, String password);
}
