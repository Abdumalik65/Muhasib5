package sample.Controller;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import sample.Config.MySqlDBGeneral;
import sample.Data.Hisob;
import sample.Data.SerialNumber;
import sample.Data.Standart;
import sample.Data.User;
import sample.Enums.ServerType;
import sample.Model.SerialNumbersModels;
import sample.Tools.GetDbData;
import sample.Tools.HBoxTextFieldPlusButton;

import java.sql.Connection;

public class SeriyaRaqami extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    HBox topPane = new HBox();
    VBox leftPane = new VBox();
    VBox centerPane = new VBox();
    VBox rightPane = new VBox();
    HBox bottomPane = new HBox();
    HBoxTextFieldPlusButton hBoxTextFieldPlusButton = new HBoxTextFieldPlusButton();
    GridPane gridPane;
    Hisob hisob;
    Standart tovar;
    SerialNumber serialNumber = new SerialNumber();
    ObservableList<SerialNumber> serialNumbers;
    SerialNumbersModels serialNumbersModels = new SerialNumbersModels();
    Connection connection;
    User user;
    int padding = 3;

    public static void main(String[] args) {
        launch(args);
    }

    public SeriyaRaqami() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
        ibtido();
    }

    public SeriyaRaqami(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        ibtido();
    }

    private void ibtido() {
        initData();
        initTopPane();
        initLeftPane();
        initCenterPane();
        initRightPane();
        initBottomPane();
        initBorderPane();
    }

    private void initData() {
        serialNumbers = serialNumbersModels.get(connection);
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

    private void initTopPane() {
        HBox.setHgrow(topPane, Priority.ALWAYS);
        VBox.setVgrow(topPane, Priority.ALWAYS);
        topPane.setPadding(new Insets(padding));
        topPane.getChildren().addAll();
    }

    private void initLeftPane() {
        HBox.setHgrow(leftPane, Priority.ALWAYS);
        VBox.setVgrow(leftPane, Priority.ALWAYS);
        leftPane.setPadding(new Insets(padding));
    }

    private void initCenterPane() {
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        VBox.setVgrow(centerPane, Priority.ALWAYS);
        centerPane.setPadding(new Insets(padding));
        initHBoxTextFieldPlus();
        centerPane.getChildren().addAll(hBoxTextFieldPlusButton);
    }

    private void initRightPane() {
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        VBox.setVgrow(rightPane, Priority.ALWAYS);
        rightPane.setPadding(new Insets(padding));
        rightPane.getChildren().addAll();
    }

    private void initBottomPane() {
        HBox.setHgrow(bottomPane, Priority.ALWAYS);
        VBox.setVgrow(bottomPane, Priority.ALWAYS);
        bottomPane.setPadding(new Insets(padding));
        bottomPane.getChildren().addAll();
    }

    private void initBorderPane() {
        borderpane.setTop(topPane);
        borderpane.setLeft(leftPane);
        borderpane.setCenter(centerPane);
        borderpane.setRight(rightPane);
        borderpane.setBottom(bottomPane);
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Bir panel");
        scene = new Scene(borderpane, 600, 400);
        stage.setScene(scene);
    }

    private void initHBoxTextFieldPlus() {
        HBox.setHgrow(hBoxTextFieldPlusButton, Priority.ALWAYS);
        TextField textField = hBoxTextFieldPlusButton.getTextField();
        textField.setText(serialNumber.getSerialNumber());
        TextFields.bindAutoCompletion(textField, serialNumbers).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<SerialNumber> autoCompletionEvent) -> {
            SerialNumber newValue = autoCompletionEvent.getCompletion();
            if (newValue != null) {
                serialNumber = newValue;
            }
        });

        Button addButton = hBoxTextFieldPlusButton.getPlusButton();
        addButton.setOnAction(event -> {
            System.out.println("Bismillah");
        });
    }

    private GridPane initGridPane() {
        GridPane gridPane = new GridPane();

        int rowIndex = 0;
        gridPane.add(new Label("Hisob egasi: "), 0, rowIndex, 1, 1);
        return gridPane;
    }
}
