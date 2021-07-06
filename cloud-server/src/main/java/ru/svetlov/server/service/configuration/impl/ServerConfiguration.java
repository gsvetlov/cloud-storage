package ru.svetlov.server.service.configuration.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.svetlov.server.service.configuration.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ServerConfiguration implements Configuration {
    private static final Logger log = LogManager.getLogger();
    private static final ServerConfiguration instance;
    private static Properties properties;

    static {
        instance = new ServerConfiguration();
    }

    public static ServerConfiguration getInstance() {
        return instance;
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    protected ServerConfiguration() {
        properties = new Properties();
        try (InputStream in = ServerConfiguration.class.getResourceAsStream("/server.properties")) {
            properties.load(in);
            log.debug("Configuration loaded");
        } catch (IOException e) {
            log.throwing(e);
        }
    }
}
