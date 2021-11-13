package sample.Temp;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.Config.MySqlDBGeneral;
import sample.Data.HisobKitob;
import sample.Data.QaydnomaData;
import sample.Data.Standart;
import sample.Data.User;
import sample.Enums.ServerType;
import sample.Model.HisobKitobModels;
import sample.Model.QaydnomaModel;
import sample.Model.StandartModels;
import sample.Tools.GetDbData;

import java.sql.Connection;

public class Analitic extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    SplitPane centerPane = new SplitPane();
    VBox rightPane = new VBox();
    VBox leftPane = new VBox();
    TableView<Standart> tovarTableView = new TableView<>();

    ObservableList<Standart> tovarObservableList;
    ObservableList<QaydnomaData> qaydnomaDataObservableList;
    ObservableList<HisobKitob> hisobKitobObservableList;

    StandartModels standartModels = new StandartModels();
    QaydnomaModel qaydnomaModel = new QaydnomaModel();
    HisobKitobModels hisobKitobModels = new HisobKitobModels();

    Connection connection;
    User user = new User(1, "admin", "", "admin");
    int padding = 3;


    public static void main(String[] args) {
        launch(args);
    }

    public Analitic() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        GetDbData.initData(connection);
    }

    public Analitic(Connection connection, User user) {
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

    @Override
    public void start(Stage primaryStage) throws Exception {
        ibtido();
        initStage(primaryStage);
        stage.show();
    }

    public void display() {
        stage = new Stage();
        initStage(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    private void initData() {
        qaydnomaDataObservableList = qaydnomaModel.getAnyData(connection, "amalTuri = 4", "");
        hisobKitobObservableList = hisobKitobModels.getAnyData(connection,"amalTuri = 4", "");

    }

    private void initTopPane() {}

    private void initLeftPane() {
        leftPane.setPadding(new Insets(padding));
        HBox.setHgrow(leftPane, Priority.ALWAYS);
        VBox.setVgrow(leftPane, Priority.ALWAYS);
        initTovarTableView();
        leftPane.getChildren().add(tovarTableView);
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
        stage.setTitle("Analitic");
        scene = new Scene(borderpane, 600, 400);
        stage.setScene(scene);
    }

    private void initTovarTableView() {
        HBox.setHgrow(tovarTableView, Priority.ALWAYS);
        VBox.setVgrow(tovarTableView, Priority.ALWAYS);
        standartModels.setTABLENAME("Tovar");
        tovarObservableList = standartModels.get_data(connection);
        tovarTableView.getColumns().add(getTextColumn());
        tovarTableView.setItems(tovarObservableList);
        tovarTableView.setEditable(true);
    }

    private TableColumn<Standart, String> getTextColumn() {
        TableColumn<Standart, String> textColumn = new TableColumn<>("Tovar");
        textColumn.setMinWidth(150);
        textColumn.setCellValueFactory(new PropertyValueFactory<>("text"));

        textColumn.setCellFactory(tc -> {
            TableCell<Standart, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(textColumn.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell ;
        });
        return textColumn;
    }

}
