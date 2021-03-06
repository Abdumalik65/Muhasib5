package sample.Controller;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.Config.MySqlDBGeneral;
import sample.Config.SqliteDB;
import sample.Data.User;
import sample.Enums.ServerType;
import sample.Tools.GetDbData;

import java.sql.Connection;

public class SetServer extends Application {
    Stage stage;
    Scene scene;
    MenuBar mainMenu;
    BorderPane borderpane = new BorderPane();
    VBox centerPane = new VBox();
    VBox rightPane = new VBox();
    VBox leftPane = new VBox();

    Connection connection;
    User user;
    int padding = 3;


    public static void main(String[] args) {
        launch(args);
    }

    public SetServer() {
        ibtido();
    }

    private void ibtido() {
        initMainMenu();
        initTopPane();
        initLeftPane();
        initCenterPane();
        initRightPane();
        initBottomPane();
        initBorderPane();
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
    }

    private void initLeftPane() {
        leftPane.setPadding(new Insets(padding));
        HBox.setHgrow(leftPane, Priority.ALWAYS);
        VBox.setVgrow(leftPane, Priority.ALWAYS);
    }

    private void initCenterPane() {
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        VBox.setVgrow(centerPane, Priority.ALWAYS);
    }

    private void initRightPane() {
        rightPane.setPadding(new Insets(padding));
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        VBox.setVgrow(rightPane, Priority.ALWAYS);
    }

    private void initBottomPane() {}

    private void initBorderPane() {
        borderpane.setTop(mainMenu);
        borderpane.setCenter(centerPane);
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Ikki panel");
        scene = new Scene(borderpane, 600, 400);
        stage.setScene(scene);
    }

    private <S, T> TableView initLeftTable() {
        TableView<S> tableView = new TableView<S>();
        tableView.setItems(getLeftTableData());
        tableView.getColumns().addAll();
        tableView.getSelectionModel().selectedItemProperty().addListener((observable ,oldValue, newValue)->{
            System.out.println("leftTableAddListener");
        });
        return  tableView;
    }

    private <T> ObservableList<T> getLeftTableData() {
        ObservableList<T> observableList = FXCollections.observableArrayList();
        return observableList;
    }

    private void initMainMenu() {
        mainMenu = new MenuBar();
        mainMenu.setPadding(new Insets(5));
        Menu menu = new Menu("Server");
        MenuItem localServer = new MenuItem("Local server");
        MenuItem remoteServer = new MenuItem("Remote server");
        menu.getItems().addAll(localServer, remoteServer);
        mainMenu.getMenus().add(menu);

        localServer.setOnAction(e->{
            ServerLocalController serverLocalController = new ServerLocalController();
            serverLocalController.display();
        });

        remoteServer.setOnAction(e->{
            ServerRemoteController serverRemoteController = new ServerRemoteController();
            serverRemoteController.display();
        });
    }

}
