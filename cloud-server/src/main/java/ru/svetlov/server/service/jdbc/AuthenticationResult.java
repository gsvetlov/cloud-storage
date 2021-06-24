package ru.svetlov.server.service.jdbc;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticationResult {
    private boolean isSuccess;
    private String reason;
    private long token;
}
