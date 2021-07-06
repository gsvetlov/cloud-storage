package integration;

import ru.svetlov.server.service.jdbc.AuthenticationProvider;
import ru.svetlov.server.service.jdbc.domain.AuthenticationResult;

import java.sql.SQLException;

/***
 * Имитируем проверку в базе. Первый логин всегда неудачный, второй - удачный
 */
public class StubAuthenticationProvider implements AuthenticationProvider {
    private int loginFailureCounter;

    @Override
    public AuthenticationResult authenticate(String login, String password) {
        loginFailureCounter++;
        if (loginFailureCounter % 2 == 0)
            return new AuthenticationResult(
                    true,
                    "Login successful",
                    null);
        return new AuthenticationResult(
                false,
                "Login failed due to stubAuthenticationProvider",
                "c:/temp");
    }

    @Override
    public void connect() throws SQLException {

    }

    @Override
    public void shutdown() {

    }
}
