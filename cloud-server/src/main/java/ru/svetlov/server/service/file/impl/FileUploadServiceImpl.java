package ru.svetlov.server.service.file.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.svetlov.server.core.domain.UserContext;
import ru.svetlov.server.service.file.FileUploadService;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileUploadServiceImpl implements FileUploadService {
    private final Map<UserContext, List<FileUploadTask>> userTaskMap = new HashMap<>();

    @Override
    public boolean prepare(UserContext context, int requestId, String path, String filename, long fileSize) {
        if (!userTaskMap.containsKey(context))
            userTaskMap.put(context, new ArrayList<>());
        FileUploadTask fileUploadTask = new FileUploadTask(requestId, Paths.get(context.getRootPath(), path, filename).normalize(), fileSize);
        if (!checkTaskValidity(context, fileUploadTask)) return false;
        userTaskMap.get(context).add(fileUploadTask);
        return true;
    }

    @Override
    public boolean upload(UserContext context, int requestId, byte[] fileContents) {
        List<FileUploadTask> tasks = userTaskMap.get(context);
        FileUploadTask activeTask = null;
        for (FileUploadTask task : tasks)
            if (task.getRequestId() == requestId){
                activeTask = task;
                break;
            }
        if (activeTask == null) return false; // TODO: add fileSize check
        return writeFile(fileContents, activeTask);

    }

    private boolean writeFile(byte[] fileContents, FileUploadTask activeTask) {
        try {
            if (!Files.exists(activeTask.getFile())) Files.createFile(activeTask.getFile());
            Files.write(activeTask.getFile(), fileContents, StandardOpenOption.APPEND);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean checkTaskValidity(UserContext context, FileUploadTask task) {
        return !userTaskMap.get(context).contains(task)
                && !Files.exists(task.getFile())
                && checkFileStoreForSpace(task);
    }

    private boolean checkFileStoreForSpace(FileUploadTask task){
        try {
            return Files.getFileStore(task.getFile().getRoot()).getUsableSpace() > task.getSize();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Data
    @AllArgsConstructor
    @EqualsAndHashCode
    private class FileUploadTask {
        private int requestId;
        private Path file;
        private long size;
    }
}
