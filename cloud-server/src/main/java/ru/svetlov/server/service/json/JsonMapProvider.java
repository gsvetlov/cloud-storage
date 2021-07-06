package ru.svetlov.server.service.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

/***
 * синглтон маппер для json
 */

public class JsonMapProvider {
    private static final JsonMapProvider instance;

    static {
        instance = new JsonMapProvider();
    }

    public static JsonMapProvider getInstance() {
        return instance;
    }

    private final ObjectMapper mapper;

    protected JsonMapProvider() {
        mapper = new ObjectMapper();
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public ObjectReader getReader() {
        return mapper.reader();
    }

    public ObjectWriter getWriter() {
        return mapper.writer();
    }
}
