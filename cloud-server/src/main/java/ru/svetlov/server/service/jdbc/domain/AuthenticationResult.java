package ru.svetlov.server.service.jdbc.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticationResult {
    private boolean isSuccess;
    private String reason;
    private String rootPath;

    public static AuthenticationResult Fail() {
        return new AuthenticationResult(false, null, null);
    }

    public static AuthenticationResult Fail(String reason) {
        return new AuthenticationResult(false, reason, null);
    }
}
