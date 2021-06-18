package ru.svetlov.server;

import ru.svetlov.server.factory.Factory;
import ru.svetlov.server.service.CloudServerService;

public class MainApp {
    public static void main(String[] args) {
        CloudServerService server = Factory.getInstance().getCloudServerService();
        if (server != null) server.startServer();
    }
}
