package ru.svetlov.server.core.handler.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import ru.svetlov.domain.command.FileListRequest;
import ru.svetlov.domain.command.FileListUpdateReply;
import ru.svetlov.domain.command.base.CommandType;
import ru.svetlov.domain.command.base.GenericCommand;
import ru.svetlov.domain.command.base.annotations.ACommandHandler;
import ru.svetlov.domain.service.viewer.domain.FileStructureInfo;
import ru.svetlov.server.core.domain.UserContext;
import ru.svetlov.domain.service.viewer.FileInfoProvider;
import ru.svetlov.server.service.json.JsonMapProvider;

import java.util.List;

@ACommandHandler(command = CommandType.LIST_FILES)
public class ListFileHandler implements CommandHandler {

    private final FileInfoProvider provider;
    private final ObjectWriter writer;

    public ListFileHandler(FileInfoProvider provider, JsonMapProvider jsonMapper) {
        this.provider = provider;
        this.writer = jsonMapper.getWriter();
    }

    @Override
    public GenericCommand process(GenericCommand command, UserContext context) {
        FileListRequest listRequest = ((FileListRequest) command);
        String path = ((String) listRequest.getParameters()[0]);
        List<FileStructureInfo> info = provider.getList(path, context.getRootPath());

        return new FileListUpdateReply(listRequest.getRequestId(), path, infoToJsonString(info));
    }

    private String infoToJsonString(List<FileStructureInfo> info){
        String result;
        try {
            result =  writer.writeValueAsString(info);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            result = "";
        }
        return result;
    }
}
