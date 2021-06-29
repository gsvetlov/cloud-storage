package ru.svetlov.storage.client.service.router;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RemoteOperationResult {
    private boolean success;
    private String message;
}
