package sample.Tools;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public class LostConnection {
    public static void losted() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Diqqat !!!");
        alert.setHeaderText("Server bilan aloqa yo`q");
        alert.setContentText("Sistema ishini yakunlaydi");
        alert.showAndWait();
        Platform.exit();
        System.exit(0);
    }
}
