package ru.svetlov.storage.client.service.router;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.svetlov.domain.command.FileListRequest;
import ru.svetlov.domain.command.LoginRequest;
import ru.svetlov.domain.command.base.Commands;
import ru.svetlov.domain.command.base.ReplyCommand;
import ru.svetlov.domain.file.FileStructureInfo;
import ru.svetlov.storage.client.service.network.NetworkClient;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO: Этому классу нужна декомпозиция и рефакторинг методов

public class CommandRouterService implements RemoteStorageService {
    private final static Logger log = LogManager.getLogger();

    private final static int REPLY_TIMEOUT = 10000;
    private final static ObjectMapper mapper = new ObjectMapper();
    private int requestId;
    private final Map<Integer, Integer> requests;
    private final NetworkClient network;

    private final BlockingQueue<ReplyCommand> replies = new LinkedBlockingQueue<>();

    public CommandRouterService(NetworkClient network) {
        this.network = network;
        network.setReplyHandler(this::postReply);
        requests = new HashMap<>();
    }

    private synchronized int getNewRequestId() {
        return ++requestId;
    }

    @Override
    public RemoteOperationResult connect(String host, int port) throws IOException {
        boolean isConnected = network.connect(host, port);
        return new RemoteOperationResult(isConnected, "");
    }

    @Override
    public void disconnect() {
        network.disconnect();
    }

    @Override
    public RemoteOperationResult login(String username, String password) {
        int id = getNewRequestId();
        LoginRequest loginRequest = new LoginRequest(id, username, password);
        requests.put(id, 0);
        network.post(loginRequest);
        while (true) {
            ReplyCommand reply = pollReply();
            if (reply == null) return new RemoteOperationResult(false, "Operation timeout");
            if (reply.getCommand().equals(Commands.REQUEST_PROCESSING)) continue;
            if (reply.getCommand().equals(Commands.LOGIN_REPLY)) {
                requests.remove(reply.getRequestId());
                ObjectReader reader = mapper.reader();
                try {
                    JsonNode jsonNode = reader.readTree((String) reply.getParameters()[0]);
                    boolean success = jsonNode.path("success").asBoolean();
                    String reason = jsonNode.path("reason").asText("fail to convert");
                    return new RemoteOperationResult(success, reason);
                } catch (JsonProcessingException e) {
                    return new RemoteOperationResult(false, e.getMessage());
                }
            }
        }
    }

    private ReplyCommand pollReply() {
        try {
            return replies.poll(REPLY_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            log.throwing(Level.ERROR, e);
            return null;
        }
    }

    private void postReply(ReplyCommand command) {
        if (requests.containsKey(command.getRequestId()))
            replies.add(command);
    }

    @Override
    public List<FileStructureInfo> listFiles(String path) {
        int id = getNewRequestId();
        FileListRequest listRequest = new FileListRequest(id, path);
        requests.put(id,0);
        network.post(listRequest);
        while (true) {
            ReplyCommand reply = pollReply();
            if (reply == null) return Collections.emptyList();
            if (reply.getCommand().equals(Commands.REQUEST_PROCESSING)) continue;
            if (reply.getCommand().equals(Commands.INVALID_REQUEST)){
                requests.remove(reply.getRequestId());
                return Collections.emptyList();
            }
            if (reply.getCommand().equals(Commands.LIST_FILES_UPDATE)) {
                requests.remove(reply.getRequestId());
                ObjectReader reader = mapper.reader();
                try {
                    List<FileStructureInfo> result = Stream
                            .of(reader.readValue((String) reply.getParameters()[1], FileStructureInfo[].class))
                            .collect(Collectors.toCollection(LinkedList::new));
                    return result;
                } catch ( IOException e) {
                    return Collections.emptyList();
                }
            }
        }
    }

    @Override
    public RemoteOperationResult postFile(FileStructureInfo file) {
        return new RemoteOperationResult(false, "fail");
    }

    @Override
    public RemoteOperationResult getFile(FileStructureInfo file) {
        return new RemoteOperationResult(false, "fail");
    }
}
