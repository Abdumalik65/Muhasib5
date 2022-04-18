package sample.Temp;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

public class QaydnomaController extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    SplitPane centerPane = new SplitPane();
    VBox rightPane = new VBox();
    VBox leftPane = new VBox();
    Tugmachalar chapTugmalar;
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

    public QaydnomaController() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        GetDbData.initData(connection);
        ibtido();
    }

    public QaydnomaController(Connection connection, User user) {
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
        chapTugmalar = initchapTugmalar();
        initLeftTable();
        leftPane.getChildren().addAll(chapTugmalar, leftTable);
    }

    private Tugmachalar initchapTugmalar() {
        Tugmachalar tugmachalar = new Tugmachalar();
        return tugmachalar;
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

    private TableView initLeftTable() {
        TableView<QaydnomaData> tableView = new TableView<>();
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
