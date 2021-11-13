package sample.Controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.Config.SqliteDB;
import sample.Data.Aloqa;
import sample.Model.AloqaModels;
import sample.Tools.Encryptor;

import java.sql.Connection;

public class ServerLocalController extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    GridPane gridPane = new GridPane();
    HBox topPane = new HBox();
    VBox leftPane = new VBox();
    VBox centerPane = new VBox();
    VBox rightPane = new VBox();
    HBox bottomPane = new HBox();

    TextField compNameTextField = new TextField();
    TextField dbHostTextField = new TextField();
    TextField dbPortTextField = new TextField();
    TextField dbUserTextField = new TextField();
    TextField dbPassTextField = new TextField();
    TextField dbNameTextField = new TextField();
    TextField dbPrefixTextField = new TextField();
    Button qaydEtButton = new Button("Qayd et");

    AloqaModels aloqaModels = new AloqaModels();
    Aloqa aloqa;


    Connection connection;
    int padding = 3;


    public static void main(String[] args) {
        launch(args);
    }

    public ServerLocalController() {
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
        connection = new SqliteDB().getDbConnection();
        aloqa = aloqaModels.getWithId(connection, 1);
        if (aloqa!=null) {
            String compNameStrind = Encryptor.decrypt(aloqa.getText().trim());
            compNameTextField.setText(compNameStrind);
            dbHostTextField.setText(aloqa.getDbHost());
            dbPortTextField.setText(aloqa.getDbPort());
            dbUserTextField.setText(aloqa.getDbUser());
            dbPassTextField.setText(aloqa.getDbPass());
            dbNameTextField.setText(aloqa.getDbName());
            dbPrefixTextField.setText(aloqa.getDbPrefix());
        }
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

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("LOCAL CONNECTION");
        scene = new Scene(borderpane, 300, 240);
        stage.setScene(scene);
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
        leftPane.getChildren().addAll();
    }

    private void initCenterPane() {
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        VBox.setVgrow(centerPane, Priority.ALWAYS);
        centerPane.setPadding(new Insets(padding));
        initGridPane();
        initButton();
        centerPane.getChildren().addAll(gridPane);
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

    private void initGridPane() {
        HBox.setHgrow(gridPane, Priority.ALWAYS);
        VBox.setVgrow(gridPane, Priority.ALWAYS);
        gridPane.setPadding(new Insets(padding));

        int rowIndex = 0;
        gridPane.add(new Label("Computer Name: "), 0 , rowIndex, 1,1);
        gridPane.add(compNameTextField, 1, rowIndex, 1, 1);

        rowIndex++;
        gridPane.add(new Label("Host: "), 0 , rowIndex, 1,1);
        gridPane.add(dbHostTextField, 1, rowIndex, 1, 1);

        rowIndex++;
        gridPane.add(new Label("Port: "), 0 , rowIndex, 1,1);
        gridPane.add(dbPortTextField, 1, rowIndex, 1, 1);

        rowIndex++;
        gridPane.add(new Label("User: "), 0 , rowIndex, 1,1);
        gridPane.add(dbUserTextField, 1, rowIndex, 1, 1);

        rowIndex++;
        gridPane.add(new Label("Password: "), 0 , rowIndex, 1,1);
        gridPane.add(dbPassTextField, 1, rowIndex, 1, 1);

        rowIndex++;
        gridPane.add(new Label("Database Name: "), 0 , rowIndex, 1,1);
        gridPane.add(dbNameTextField, 1, rowIndex, 1, 1);

        rowIndex++;
        gridPane.add(new Label("Ptrefix: "), 0 , rowIndex, 1,1);
        gridPane.add(dbPrefixTextField, 1, rowIndex, 1, 1);

        rowIndex++;
        gridPane.add(qaydEtButton, 1 , rowIndex, 1,1);
    }

    private void initButton() {
        qaydEtButton.setOnAction(event -> {
            String enCryptString = Encryptor.encrypt(compNameTextField.getText().trim());
            if (aloqa != null) {
                aloqa.setText(enCryptString);
                aloqa.setDbHost(dbHostTextField.getText().trim());
                aloqa.setDbPort(dbPortTextField.getText().trim());
                aloqa.setDbUser(dbUserTextField.getText().trim());
                aloqa.setDbPass(dbPassTextField.getText().trim());
                aloqa.setDbName(dbNameTextField.getText().trim());
                aloqa.setDbPrefix(dbPrefixTextField.getText().trim());
                aloqaModels.update_data(connection, aloqa);
            } else {
                aloqa = new Aloqa(null,
                        enCryptString,
                        dbHostTextField.getText().trim(),
                        dbPortTextField.getText().trim(),
                        dbUserTextField.getText().trim(),
                        dbPassTextField.getText().trim(),
                        dbNameTextField.getText().trim(),
                        dbPrefixTextField.getText().trim(),
                        1,
                        null
                );
                aloqaModels.insert_data(connection, aloqa);
            }
            stage.close();
        });
    }
}
