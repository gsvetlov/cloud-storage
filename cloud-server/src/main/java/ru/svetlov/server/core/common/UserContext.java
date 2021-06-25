package ru.svetlov.server.core.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserContext {
    private String username;
    private int sessionToken;
    private String rootPath;
}
