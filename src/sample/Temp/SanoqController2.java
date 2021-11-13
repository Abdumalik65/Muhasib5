package sample.Temp;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.Config.MySqlDBGeneral;
import sample.Data.QaydnomaData;
import sample.Data.User;
import sample.Enums.ServerType;
import sample.Tools.GetDbData;
import sample.Tools.Tugmachalar;

import java.sql.Connection;

public class SanoqController2 extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    SplitPane centerPane = new SplitPane();
    VBox rightPane = new VBox();
    VBox leftPane = new VBox();

    Tugmachalar tugmachalar;
    TableView<QaydnomaData> sanoqQaydiTable;

    Connection connection;
    User user;
    int padding = 3;


    public static void main(String[] args) {
        launch(args);
    }

    public SanoqController2() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        GetDbData.initData(connection);
        ibtido();
    }

    public SanoqController2(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        ibtido();
    }

    private void ibtido() {
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

    private void initTopPane() {}

    private void initLeftPane() {
        leftPane.setPadding(new Insets(padding));
        HBox.setHgrow(leftPane, Priority.ALWAYS);
        VBox.setVgrow(leftPane, Priority.ALWAYS);
    }

    private void initCenterPane() {
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        VBox.setVgrow(centerPane, Priority.ALWAYS);
        centerPane.getItems().addAll(leftPane, rightPane);
    }

    private void initRightPane() {
        rightPane.setPadding(new Insets(padding));
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        VBox.setVgrow(rightPane, Priority.ALWAYS);
    }

    private void initBottomPane() {}

    private void initBorderPane() {
        borderpane.setCenter(centerPane);
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Ikki panel");
        scene = new Scene(borderpane, 600, 400);
        stage.setScene(scene);
    }
}
