package sample.Controller;

import com.jfoenix.controls.JFXButton;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.Config.MySqlDBGeneral;
import sample.Data.User;
import sample.Enums.ServerType;
import sample.Tools.GetDbData;
import sample.Tools.SetHVGrow;

import java.sql.Connection;
import java.util.Locale;

public class Calculator extends Application {
    Stage stage;
    Scene scene;
    GridPane gridPane;
    Connection connection;
    User user;
    int padding = 3;


    public static void main(String[] args) {
        launch(args);
    }

    public Calculator() {
        connection = new MySqlDBGeneral(ServerType.REMOTE).getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
        ibtido();
    }

    public Calculator(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        ibtido();
    }

    private void ibtido() {
        initData();
        gridPane = initGridPane();
    }

    private void initData() {
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initStage(primaryStage);
        stage.show();
    }

    public void display() {
        stage = new Stage();
        initStage(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    private GridPane initGridPane() {
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(padding));
        Integer rowIndex = 0;
        Label label = new Label();
        label.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(label, Priority.ALWAYS);

        gridPane.add(label, 0, rowIndex, 3, 1);
        GridPane.setHalignment(label, HPos.RIGHT);
        GridPane.setHgrow(label, Priority.ALWAYS);

        rowIndex++;
        JFXButton button1 = tugmacha(1, label);
        JFXButton button2 = tugmacha(2, label);
        JFXButton button3 = tugmacha(3, label);
        gridPane.add(button1, 0, rowIndex, 1, 1);
        gridPane.add(button2, 1, rowIndex, 1, 1);
        gridPane.add(button3, 2, rowIndex, 1, 1);
        GridPane.setHgrow(button1, Priority.ALWAYS);
        GridPane.setHgrow(button2, Priority.ALWAYS);
        GridPane.setHgrow(button3, Priority.ALWAYS);

        rowIndex++;
        JFXButton button4 = tugmacha(4, label);
        JFXButton button5 = tugmacha(5, label);
        JFXButton button6 = tugmacha(6, label);
        gridPane.add(button4, 0, rowIndex, 1, 1);
        gridPane.add(button5, 1, rowIndex, 1, 1);
        gridPane.add(button6, 2, rowIndex, 1, 1);

        rowIndex++;
        JFXButton button7 = tugmacha(7, label);
        JFXButton button8 = tugmacha(8, label);
        JFXButton button9 = tugmacha(9, label);
        gridPane.add(button7, 0, rowIndex, 1, 1);
        gridPane.add(button8, 1, rowIndex, 1, 1);
        gridPane.add(button9, 2, rowIndex, 1, 1);
        return gridPane;
    }

    private JFXButton tugmacha(Integer raqam, Label label) {
        String raqamString = raqam.toString().trim();
        JFXButton jfxButton = new JFXButton(raqamString);
        jfxButton.setPrefSize(16, 16);
        jfxButton.setMaxSize(2000, 2000);
        SetHVGrow.VerticalHorizontal(jfxButton);
        jfxButton.setOnAction(event -> {
            String sonLabel = label.getText().trim();
            sonLabel += raqam;
            label.setText(sonLabel);
        });
        return jfxButton;
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Bir panel");
        scene = new Scene(gridPane, 250, 350);
        stage.setScene(scene);
    }
}
