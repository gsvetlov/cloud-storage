package ru.svetlov.server.service.jdbc;

public interface AuthenticationProvider {
    AuthenticationResult authenticate(String login, String password);
}
