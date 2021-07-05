package ru.svetlov.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flywaydb.core.Flyway;
import ru.svetlov.server.factory.Factory;
import ru.svetlov.server.core.CloudServerService;
import ru.svetlov.server.service.configuration.impl.Configuration;

public class MainApp {
    private static final Logger log = LogManager.getLogger();
    private static Configuration configuration;

    public static void main(String[] args) {
        configuration = Factory.getInstance().getConfiguration();
        if (configuration.get("migrations").equals("on")){
            setupMigrations();
        }
        CloudServerService server = Factory.getInstance().getCloudServerService();
        log.trace(server);
        log.info("CloudServer is starting");
        if (server != null) server.startServer();
    }

    private static void setupMigrations() {
        Flyway flyway = Flyway.configure().dataSource(
                configuration.get("connection"),
                configuration.get("username"),
                configuration.get("password")).load();
        flyway.migrate();
    }
}
