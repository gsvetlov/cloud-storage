package ru.svetlov.server.service.jdbc.impl;

import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.svetlov.server.service.configuration.impl.Configuration;
import ru.svetlov.server.service.jdbc.AuthenticationProvider;
import ru.svetlov.server.service.jdbc.domain.AuthenticationResult;

import java.sql.*;

public class AuthenticationProviderImpl implements AuthenticationProvider {

    private final static Logger log = LogManager.getLogger();
    private final String connectionString;
    private final String login;
    private final String password;
    private Connection connection;
    private PreparedStatement statement;

    public AuthenticationProviderImpl(Configuration configuration) {
        connectionString = configuration.get("connection");
        login = configuration.get("user");
        password = configuration.get("password");
        connect();
        prepareStatement();
    }

    private void connect() {
        try {
            connection = DriverManager.getConnection(connectionString, login, password);
            log.debug("Connected");
            log.trace(connection);
        } catch (SQLException e) {
            log.throwing(e);
        }
    }

    private void prepareStatement() {
        try {
            statement = connection.prepareStatement(
                    "select home_dir from users where user_name=? and user_password=? limit 1");
        } catch (SQLException e) {
            log.throwing(e);
        }
    }

    public void shutdown() {
        try {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
            log.debug("Disconnected");
        } catch (SQLException e) {
            log.throwing(e);
        }
    }

    @Override
    public synchronized AuthenticationResult authenticate(String login, String password) {
        AuthenticationResult result;
        try {
            setupQuery(login, password);

            QueryResult query = runQuery();

            result = processQueryResult(query);

        } catch (IllegalArgumentException e) {
            log.throwing(e);
            result = AuthenticationResult.Fail(e.getMessage());
        }
        log.debug(result);
        return result;
    }

    private void setupQuery(String login, String password) throws IllegalArgumentException {
        try {
            statement.setString(1, login);
            statement.setString(2, password);
        } catch (SQLException e) {
            log.throwing(e);
            throw new IllegalArgumentException("Statement arguments are not valid");
        }
    }

    private QueryResult runQuery() {
        QueryResult result = new QueryResult();
        try (ResultSet rs = statement.executeQuery()) {
            if (rs.next()) {
                result.setPath(rs.getString(1));
            } else {
                result.setReason("user/login incorrect");
            }
        } catch (SQLException e) {
            log.throwing(e);
            result.setReason(e.getMessage());
        }
        return result;
    }

    private AuthenticationResult processQueryResult(QueryResult query) {
        String reason = query.getReason() == null ? "login_successful" : query.getReason();
        String rootPath = query.getPath();
        boolean isSuccess = rootPath != null;
        return new AuthenticationResult(isSuccess, reason, rootPath);
    }

    @Data
    private static class QueryResult{
        public String path;
        public String reason;
    }
}
