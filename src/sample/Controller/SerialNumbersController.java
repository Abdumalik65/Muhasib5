package sample.Controller;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import sample.Config.MySqlDB;
import sample.Config.MySqlDBLocal;
import sample.Data.Hisob;
import sample.Data.HisobKitob;
import sample.Data.QaydnomaData;
import sample.Data.User;
import sample.Model.HisobKitobModels;
import sample.Model.QaydnomaModel;
import sample.Tools.GetDbData;
import sample.Tools.GetTableView2;
import sample.Tools.MoneyShow;
import sample.Tools.SetHVGrow;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.Date;

public class SerialNumbersController extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    GridPane centerPane = new GridPane();
    VBox rightPane = new VBox();
    TableView<Hisob> hisobTableView = new TableView<>();
    GetTableView2 getTableView2 = new GetTableView2();
    TextField textField = new TextField();
    ObservableList<Hisob> hisobObservableList;
    Connection connection;
    User user = new User(1, "admin", "", "admin");
    int padding = 3;
    Font font = Font.font("Arial", FontWeight.BOLD,20);

    public static void main(String[] args) {
        launch(args);
    }

    public SerialNumbersController() {
        connection = new MySqlDBLocal().getDbConnection();
        GetDbData.initData(connection);
        ibtido();
    }

    public SerialNumbersController(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        ibtido();
    }

    private void ibtido() {
        initData();
        initRightPane();
        initCenterPane();
        initBorderPane();
    }

    private void initData() {
        hisobObservableList = GetDbData.getHisobObservableList();
        QaydnomaModel qaydnomaModel = new QaydnomaModel();
    }

    private void initHisobTableView() {
        SetHVGrow.VerticalHorizontal(hisobTableView);
        hisobTableView.setMaxWidth(210);
        hisobTableView.setPadding(new Insets(padding));
        TableColumn<Hisob, String> hisobText = getTableView2.getHisobTextColumn();
        hisobText.setMinWidth(200);
        hisobTableView.getColumns().add(hisobText);
        hisobTableView.setItems(hisobObservableList);
        hisobTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
            }
        });
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

    private void initRightPane() {
        initTaftishTextField();
        initHisobTableView();
        SetHVGrow.VerticalHorizontal(rightPane);
        rightPane.setPadding(new Insets(padding));
        rightPane.getChildren().addAll(textField, hisobTableView);
    }

    private void initCenterPane() {
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        VBox.setVgrow(centerPane, Priority.ALWAYS);
        int rowIndex = 0;
    }

    private void initBorderPane() {
        borderpane.setTop(null);
        borderpane.setLeft(null);
        borderpane.setRight(rightPane);
        borderpane.setCenter(centerPane);
        borderpane.setBottom(null);
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Bir panel");
        scene = new Scene(borderpane, 1200, 600);
        stage.setScene(scene);
    }

    private void initTaftishTextField() {
        HBox.setHgrow(textField, Priority.ALWAYS);
        textField.setPadding(new Insets(padding));
        textField.setMaxWidth(210);
        textField.setPromptText("QIDIR");
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            Taftish(oldValue, newValue);
        });

    }

    public void Taftish(String oldValue, String newValue) {
        ObservableList<Hisob> subentries = FXCollections.observableArrayList();
        newValue = newValue.toLowerCase();

        if ( oldValue != null && (newValue.length() < oldValue.length()) ) {
            hisobTableView.setItems( hisobObservableList );
        }

        for ( Hisob hisob: hisobObservableList ) {
            if (hisob.getText().toLowerCase().contains(newValue)) {
                subentries.add(hisob);
            }
        }
        hisobTableView.setItems(subentries);
    }

}
