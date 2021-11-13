package sample.Controller;

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
import sample.Data.*;
import sample.Enums.ServerType;
import sample.Model.Standart2Models;
import sample.Tools.GetDbData;
import sample.Tools.Tugmachalar;

import java.sql.Connection;

public class MahsulotTarkibi extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    SplitPane centerPane = new SplitPane();
    VBox rightPane = new VBox();
    VBox rightPane2 = new VBox();
    VBox leftPane = new VBox();

    Connection connection;
    User user = new User(1, "admin", "", "admin");
    int padding = 3;

    TableView<Standart2> mahsulotTableView = new TableView<>();
    TableView<Standart4> tarkibDetalTableView = new TableView<>();
    TableView<Standart4> tarkibPulTableView = new TableView<>();

    Tugmachalar mahsulotTugmachalar = new Tugmachalar();
    Tugmachalar detalTugmachalar = new Tugmachalar();
    Tugmachalar pulTugmachalar = new Tugmachalar();

    ObservableList<Standart2> mahsulotObservableList;
    ObservableList<Standart4> TarkibDetalObservableList;
    ObservableList<Standart4> TarkibPulObservableList;

    Standart2Models standart2Models = new Standart2Models();

    public static void main(String[] args) {
        launch(args);
    }

    public MahsulotTarkibi() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        GetDbData.initData(connection);
    }

    public MahsulotTarkibi(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ibtido();
        initStage(primaryStage);
        stage.show();
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Mahsulot tarkibi");
        scene = new Scene(borderpane, 1000, 600);
        stage.setScene(scene);
    }

    public void display() {
        ibtido();
        stage = new Stage();
        initStage(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    private void ibtido() {
        initData();
        initMahsulotTugmachalar();
        initMahsulotTable();
        initDetalTable();
        initPulTable();
        initLeftPane();
        initRightPane();
        initCenterPane();
        initBorderPane();
    }

    private void initData() {
        standart2Models.setTABLENAME("Mahsulot");
        mahsulotObservableList = standart2Models.get_data(connection);
    }

    private void initCenterPane() {
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        VBox.setVgrow(centerPane, Priority.ALWAYS);
        centerPane.setDividerPositions(.4);
        centerPane.getItems().addAll(leftPane, rightPane);
    }

    private void initRightPane() {
        rightPane.setPadding(new Insets(padding));
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        VBox.setVgrow(rightPane, Priority.ALWAYS);
        rightPane.getChildren().addAll(detalTugmachalar, tarkibDetalTableView);
        rightPane.getChildren().addAll(pulTugmachalar, tarkibPulTableView);
    }

    private void initLeftPane() {
        leftPane.setPadding(new Insets(padding));
        HBox.setHgrow(leftPane, Priority.ALWAYS);
        VBox.setVgrow(leftPane, Priority.ALWAYS);
        leftPane.getChildren().addAll(mahsulotTugmachalar, mahsulotTableView);
    }

    private void initBorderPane() {
        borderpane.setCenter(centerPane);
    }

    private void initMahsulotTable() {
        HBox.setHgrow(mahsulotTableView, Priority.ALWAYS);
        VBox.setVgrow(mahsulotTableView, Priority.ALWAYS);
        mahsulotTableView.getColumns().add(getStandart2Text());
        mahsulotTableView.setItems(mahsulotObservableList);

    }

    private TableColumn<Standart2, String> getStandart2Text() {
        TableColumn<Standart2, String> textColumn = new TableColumn<>("Mahsulot");
        textColumn.setMinWidth(380);
        textColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
        textColumn.setCellFactory(tc -> {
            TableCell<Standart2, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(textColumn.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell ;
        });


        return textColumn;
    }

    private TableColumn<Standart2, Integer> getStandart2Id2() {
        TableColumn<Standart2, Integer> id2Column = new TableColumn<>("Hisob nomi");
        id2Column.setMinWidth(100);
        id2Column.setCellValueFactory(new PropertyValueFactory<>("id2"));
        return id2Column;
    }


    private void initDetalTable() {
        HBox.setHgrow(tarkibDetalTableView, Priority.ALWAYS);
        VBox.setVgrow(tarkibDetalTableView, Priority.ALWAYS);
    }

    private void initPulTable() {
        HBox.setHgrow(tarkibPulTableView, Priority.ALWAYS);
        VBox.setVgrow(tarkibPulTableView, Priority.ALWAYS);
    }

    private void initMahsulotTugmachalar() {
        mahsulotTugmachalar.getAdd().setOnAction(event -> {
            TovarController1 tovarController = new TovarController1(connection, user);
            Standart tovar = tovarController.display();
            if (tovar != null) {
                Standart2 mahsulot = new Standart2(null,
                        tovar.getId(),
                        tovar.getText(),
                        user.getId(),
                        null
                );
                standart2Models.insert_data(connection, mahsulot);
                mahsulotObservableList.add(mahsulot);
                mahsulotTableView.scrollTo(mahsulot);
                mahsulotTableView.requestFocus();
            }
        });
    }
}
