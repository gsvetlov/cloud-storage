package ru.svetlov.storage.client.service.network;

import ru.svetlov.domain.command.base.ReplyCommand;
import ru.svetlov.domain.command.base.RequestCommand;

import java.io.IOException;
import java.util.function.Consumer;

public interface NetworkClient {
    boolean connect(String host, int port) throws IOException;
    void disconnect();
    void post(RequestCommand command);
    void setReplyHandler(Consumer<ReplyCommand> callback);
    void postRequest(RequestCommand request, Consumer<ReplyCommand> replyCallback);
}
