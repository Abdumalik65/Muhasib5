package sample.Temp;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.Config.MySqlDBGeneral;
import sample.Data.Standart;
import sample.Data.User;
import sample.Enums.ServerType;
import sample.Tools.GetDbData;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Collection;

public class IkkiPanel extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    SplitPane centerPane = new SplitPane();
    VBox rightPane = new VBox();
    VBox leftPane = new VBox();
    TableView leftTable;
    TableView centerTable;
    TableView rightTable;

    ObservableList leftTableData;
    ObservableList centerTableData;
    ObservableList rightTableData;

    Connection connection;
    User user;
    int padding = 3;


    public static void main(String[] args) {
        launch(args);
    }

    public IkkiPanel() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        GetDbData.initData(connection);
        ibtido();
    }

    public IkkiPanel(Connection connection, User user) {
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
        borderpane = initBorderPane();
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

    private BorderPane initBorderPane() {
        BorderPane borderPane = new BorderPane();
        InputStream inputStream1 = getClass().getResourceAsStream("/sample/images/Icons/borbaraka.jpg");
        InputStream inputStream2 = getClass().getResourceAsStream("/sample/images/Icons/borbaraka.png");
        Image image1 = new Image(inputStream1);
        Image image2 = new Image(inputStream2);


        BackgroundSize bSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false);

        Background background2 = new Background(new BackgroundImage(image2,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                bSize));

        borderPane.setBackground(new Background(new BackgroundImage(image1,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                bSize)));

        Button btn = new Button("Change Background");
        btn.setOnAction((ActionEvent event) -> {
            borderPane.setBackground(background2);
        });

        borderPane.setCenter(btn);
//        borderpane.setCenter(centerPane);
        return borderPane;
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
}
