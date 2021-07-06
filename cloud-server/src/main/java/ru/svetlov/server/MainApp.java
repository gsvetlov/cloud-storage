package ru.svetlov.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.svetlov.server.factory.Factory;
import ru.svetlov.server.core.CloudServerService;
import ru.svetlov.server.service.configuration.Configuration;
import ru.svetlov.server.service.jdbc.flyway.FlywaySetup;

public class MainApp {
    private static final Logger log = LogManager.getLogger();

    public static void main(String[] args) {
        checkMigrationsEnabled();
        startServer();
    }

    private static void startServer() {
        CloudServerService server = Factory.getInstance().getCloudServerService();
        log.trace(server);
        log.info("CloudServer is starting");
        if (server != null) server.startServer();
    }

    private static void checkMigrationsEnabled() {
        Configuration configuration = Factory.getInstance().getConfiguration();
        if (configuration.get("migrations").equals("on")){
            FlywaySetup.setupMigrations(configuration);
        }
    }
}
