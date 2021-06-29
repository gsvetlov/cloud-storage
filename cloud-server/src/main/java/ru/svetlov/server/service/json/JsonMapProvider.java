package ru.svetlov.server.service.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

/***
 * синглтон маппер для json
 */

public class JsonMapProvider {
    private final static Object _lock = new Object();
    private static volatile JsonMapProvider _instance;

    public static JsonMapProvider getInstance() {
        if (_instance != null) return _instance;
        synchronized (_lock) {
            if (_instance != null) return _instance;
            _instance = new JsonMapProvider();
        }
        return _instance;
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
