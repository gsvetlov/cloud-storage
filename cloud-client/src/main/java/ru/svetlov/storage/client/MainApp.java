package ru.svetlov.storage.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.svetlov.storage.client.controller.MainWindowController;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MainApp extends Application {
    private static final Logger log = LogManager.getLogger();

    private Properties properties;

    @Override
    public void start(Stage primaryStage) throws Exception {
        loadProperties();
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/MainWindow.fxml"));
        Parent parent = loader.load();
        primaryStage.setScene(new Scene(parent));
        primaryStage.setTitle("Cloud-storage client");
        primaryStage.setResizable(true);

        MainWindowController controller = loader.getController();
        primaryStage.setOnCloseRequest(controller::shutdown);

        primaryStage.show();
    }

    private void loadProperties() {
        try (InputStream in = MainApp.class.getClassLoader().getResourceAsStream("client.properties")) {
            properties = new Properties();
            properties.load(in);
            log.trace("properties loaded");
        } catch (IOException e) {
            log.throwing(Level.ERROR, e);
            e.printStackTrace();
        }
    }
}
