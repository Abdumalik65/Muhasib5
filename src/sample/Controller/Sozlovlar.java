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
import sample.Config.MySqlDBGeneral;
import sample.Data.PlasticFoizi;
import sample.Data.User;
import sample.Enums.ServerType;
import sample.Model.PlasticFoiziModels;
import sample.Tools.DasturlarRoyxati;
import sample.Tools.GetDbData;
import sample.Tools.SetHVGrow;

import java.sql.Connection;
import java.sql.SQLException;

public class Sozlovlar extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    HBox topPane = new HBox();
    VBox leftPane = new VBox();
    VBox centerPane = new VBox();
    VBox rightPane = new VBox();
    HBox bottomPane = new HBox();
    Button saveButton;
    Connection connection;
    User user;
    int padding = 3;
    PlasticFoizi plasticFoizi;
    TextField plastikFoiziTextField;
    PlasticFoiziModels plasticFoiziModels = new PlasticFoiziModels();
    GridPane gridPane;


    public static void main(String[] args) {
        launch(args);
    }

    public Sozlovlar() {
        connection = new MySqlDBGeneral(ServerType.REMOTE).getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
        String classSimpleName = getClass().getSimpleName();
        DasturlarRoyxati.dastur(connection, user, classSimpleName);
        ibtido();
    }

    public Sozlovlar(Connection connection, User user) {
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
        plastikFoiziTextField = initPlasticFoiziTextField();
        gridPane = initGridPane();
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

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Sozlovlar");
        scene = new Scene(borderpane, 600, 400);
        stage.setScene(scene);
    }

    private GridPane initGridPane() {
        GridPane gridPane = new GridPane();
        SetHVGrow.VerticalHorizontal(gridPane);
        Integer rowIndex = 0;
        gridPane.add(new Label("Bank xizmati foizi"), 0, rowIndex, 1, 1);
        gridPane.add(plastikFoiziTextField, 1, rowIndex, 1, 1);

        return gridPane;
    }

    private TextField initPlasticFoiziTextField() {
        TextField textField = new TextField();
        plasticFoizi = GetDbData.getPlasticFoizi();
        textField.setText(plasticFoizi.getFoiz().toString());
        return textField;
    }

    private Button initSaveButton() {
        Button button = new Button("Qayd et");
        button.setOnAction(event -> {
            plasticFoizi.setFoiz(Sotuvchi.getDoubleFromTextField(plastikFoiziTextField));
            plasticFoiziModels.update_data(connection, plasticFoizi);
        });
        return button;
    }
}
