package ru.svetlov.server.service.jdbc.impl;

import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.postgresql.ds.PGConnectionPoolDataSource;
import org.postgresql.ds.PGPoolingDataSource;
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
            statement = connection.prepareStatement("select home_dir from users where user_name=? and user_password=? limit 1");
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
        boolean isSuccess = false;
        String rootPath = null;
        String reason = "Login successful";
        long token = 0;
        try {
            prepareStatement(login, password);
            QueryResult result = queryUserRootPath();
            rootPath = result.getPath();
            isSuccess = rootPath != null;
            if (!isSuccess && result.getReason() == null) {
                reason = "user/login incorrect";
            }
            if (!isSuccess && result.getReason() != null)
                reason = result.getReason();
        } catch (IllegalArgumentException e) {
            log.throwing(e);
            reason = e.getMessage();
        }

        AuthenticationResult authenticationResult = new AuthenticationResult(isSuccess, reason, 0, rootPath);
        log.debug(authenticationResult);
        return authenticationResult;
    }

    private QueryResult queryUserRootPath() {
        QueryResult result = new QueryResult();
        try (ResultSet rs = statement.executeQuery()) {
            if (rs.next()) {
                result.setPath(rs.getString(1));            }
        } catch (SQLException e) {
            log.throwing(e);
            result.setReason(e.getMessage());
        }
        return result;
    }

    private void prepareStatement(String login, String password) throws IllegalArgumentException {
        try {
            statement.setString(1, login);
            statement.setString(2, password);
        } catch (SQLException e) {
            log.throwing(e);
            throw new IllegalArgumentException("Statement arguments not valid");
        }
    }
    @Data
    private static class QueryResult{
        public String path;
        public String reason;
    }
}
