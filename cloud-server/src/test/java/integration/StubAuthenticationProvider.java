package integration;

import ru.svetlov.server.service.jdbc.AuthenticationProvider;
import ru.svetlov.server.service.jdbc.domain.AuthenticationResult;

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
                    loginFailureCounter, null);
        return new AuthenticationResult(
                false,
                "Login failed due to stubAuthenticationProvider",
                loginFailureCounter, "c:/temp");
    }
}
