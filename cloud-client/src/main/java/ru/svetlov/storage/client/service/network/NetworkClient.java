package ru.svetlov.storage.client.service.network;

import ru.svetlov.domain.command.base.ReplyCommand;
import ru.svetlov.domain.command.base.RequestCommand;
import ru.svetlov.storage.client.common.Callback;

import java.io.IOException;

public interface NetworkClient {
    boolean connect(String host, int port) throws IOException;
    void disconnect();
    void post(RequestCommand command);
    void setReplyHandler(Callback<ReplyCommand> callback);
    void postRequest(RequestCommand request, Callback<ReplyCommand> replyCallback);
}
