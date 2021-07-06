package ru.svetlov.server.service.jdbc;

import ru.svetlov.server.service.jdbc.domain.AuthenticationResult;

import java.sql.SQLException;

public interface AuthenticationProvider {
    AuthenticationResult authenticate(String login, String password);
    void connect() throws SQLException;
    void shutdown();
}
