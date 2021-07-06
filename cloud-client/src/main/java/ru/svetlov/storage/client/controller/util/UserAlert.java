package ru.svetlov.storage.client.controller.util;

import javafx.scene.control.Alert;

public class UserAlert {
    public static Alert get(String msg, String title){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.setTitle(title);
        alert.setHeaderText(null);
        return alert;
    }
}
