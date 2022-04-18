package sample.Config;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import sample.Enums.ServerType;
import sample.Tools.Alerts;
import sample.Tools.MoneyShow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Optional;

public class MySqlStatus {
    static Connection connection;

    public static Boolean checkMyConnection(Connection connection) {
        Boolean isChecked = true;
        while (!isValid(connection)) {
            isChecked = true;
        }
        return isChecked;

    }
    public static Boolean isValid(Connection connection) {
        Boolean isValid = true;
        String select = "SELECT 1";
        PreparedStatement prSt = null;
        try {
            prSt=connection.prepareStatement(select);
            prSt.executeQuery();
        } catch (SQLException e) {
            isValid = false;
            Boolean davomEt = Alerts.haYoq("Server bilan aloqa yo`q", "Aloqa o`rnatamizmi?");
            if (davomEt) {
               MySqlStatus.connection = reconnect();
            } else {
                System.exit(0);
                Platform.exit();
            }
        }
        return isValid;
    }

    public static Connection reconnect() {
        Connection connection = getConnection();
        if (MyServer.getId()==1) {
            connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        } else {
            connection = new MySqlDBGeneral(ServerType.REMOTE).getDbConnection();
        }
        return connection;
    }

    public static Connection getConnection() {
        return connection;
    }

    public static boolean haYoq(String headerText, String contentText) {
        boolean davomEt = false;
        DecimalFormat decimalFormat = new MoneyShow();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getButtonTypes().removeAll(alert.getButtonTypes());
        ButtonType okButton = new ButtonType("Ha");
        ButtonType noButton = new ButtonType("Yo`q");
        alert.getButtonTypes().addAll(okButton, noButton);
        alert.setTitle("Diqqat !!!");
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        Optional<ButtonType> option = alert.showAndWait();

        ButtonType buttonType = option.get();
        if (okButton.equals(buttonType)) {
            davomEt = true;
        }
        return davomEt;
    }

}
