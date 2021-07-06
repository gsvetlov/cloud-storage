package ru.svetlov.server.service.jdbc.flyway;

import org.flywaydb.core.Flyway;
import ru.svetlov.server.service.configuration.Configuration;

public class FlywaySetup {
    public static void setupMigrations(Configuration configuration) {
        Flyway flyway = Flyway.configure().dataSource(
                configuration.get("connection"),
                configuration.get("user"),
                configuration.get("password")).load();
        flyway.migrate();
    }
}
