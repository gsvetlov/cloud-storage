package ru.svetlov.storage.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.svetlov.storage.client.controller.MainWindowController;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/MainWindow.fxml"));
        Parent parent = loader.load();
        primaryStage.setScene(new Scene(parent));
        primaryStage.setTitle("Cloud-storage client");
        primaryStage.setResizable(true);
        MainWindowController controller = loader.getController();
        primaryStage.setOnCloseRequest(controller::shutdown);
        primaryStage.show();
    }
}
