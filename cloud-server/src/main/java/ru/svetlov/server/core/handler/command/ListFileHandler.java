package ru.svetlov.server.core.handler.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import ru.svetlov.domain.command.FileListRequest;
import ru.svetlov.domain.command.FileListUpdateReply;
import ru.svetlov.domain.command.base.GenericCommand;
import ru.svetlov.domain.command.base.annotations.ACommandHandler;
import ru.svetlov.domain.file.FileStructureInfo;
import ru.svetlov.server.core.common.UserContext;
import ru.svetlov.server.service.file.FileInfoProvider;
import ru.svetlov.server.service.json.JsonMapProvider;

import java.util.List;

@ACommandHandler(command = "list_files")
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
        List<FileStructureInfo> info = provider.getPath(context.getRootPath(), path);

        return new FileListUpdateReply(1, listRequest.getRequestId(), path, infoToJsonString(info));
    }

    private String infoToJsonString(List<FileStructureInfo> info){
        String result = null;
        try {
            result =  writer.writeValueAsString(info);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            result = "";
        }
        return result;
    }
}
