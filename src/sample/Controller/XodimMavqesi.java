package sample.Controller;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import sample.Config.MySqlDBGeneral;
import sample.Data.Narh;
import sample.Data.Standart;
import sample.Data.Standart7;
import sample.Data.User;
import sample.Enums.ServerType;
import sample.Model.Standart7Models;
import sample.Tools.Alerts;
import sample.Tools.DasturlarRoyxati;
import sample.Tools.GetDbData;

import java.sql.Connection;
import java.text.NumberFormat;
import java.util.Date;

public class XodimMavqesi extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    HBox topPane = new HBox();
    VBox leftPane = new VBox();
    VBox centerPane = new VBox();
    VBox rightPane = new VBox();
    HBox bottomPane = new HBox();
    Button button = new Button ("12345");
    TableView<Standart7> tableView;
    Standart7Models standart7Models = new Standart7Models("XodimMavqesi");
    ObservableList<Standart7> mavqeList;
    Connection connection;
    User user;
    int padding = 3;


    public static void main(String[] args) {
        launch(args);
    }

    public XodimMavqesi() {
        connection = new MySqlDBGeneral(ServerType.REMOTE).getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
        ibtido();
    }

    public XodimMavqesi(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        String classSimpleName = getClass().getSimpleName();
        DasturlarRoyxati.dastur(connection, user, classSimpleName);
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
        mavqeList = standart7Models.get_data(connection);
    }

    private TableView<Standart7> initTableView() {
        TableView<Standart7> tableView = new TableView<>();
        return tableView;
    }

    private TableColumn<Standart7, Integer> getIdColumn() {
        TableColumn<Standart7, Integer> idColumn = new TableColumn<>("N");
        idColumn.setMinWidth(15);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        return idColumn;
    }

    private TableColumn<Standart7, String> getTextColumn() {
        TableColumn<Standart7, String> textColumn = new TableColumn<>("Mavqe");
        textColumn.setMinWidth(150);
        textColumn.setCellValueFactory(new PropertyValueFactory<>("text"));

        textColumn.setCellFactory(tc -> {
            TableCell<Standart7, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(textColumn.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell ;
        });
        return textColumn;
    }

    private  TableColumn<Standart7, Double> getADoubleColumn() {
        TableColumn<Standart7, Double>  aDoubleColumn = new TableColumn<>("Mavqe");
        aDoubleColumn.setMinWidth(100);
        aDoubleColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Standart7, Double>, ObservableValue<Double>>() {

            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<Standart7, Double> param) {
                Standart7 standart7 = param.getValue();
                Double aDouble = standart7.getaDouble();
                return new SimpleObjectProperty<Double>(aDouble);
            }
        });
        aDoubleColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setMinimumIntegerDigits (1);
                numberFormat.setMaximumIntegerDigits (8);

                numberFormat.setMinimumFractionDigits (0);
                numberFormat.setMaximumFractionDigits (0);
                return numberFormat.format(object);
            }

            @Override
            public Double fromString(String string) {
                string = string.replaceAll(" ", "");
                string = string.replaceAll(",", ".");
                if (!Alerts.isNumericAlert(string)) {
                    string = "0.0";
                }
                return Double.valueOf(string);
            }
        }));
        aDoubleColumn.setOnEditCommit(event -> {
            Standart7 standart7 = event.getRowValue();
            Double newValue = event.getNewValue();
            if (newValue != null) {
            }
        });
        aDoubleColumn.setStyle( "-fx-alignment: CENTER;");
        return aDoubleColumn;
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
        centerPane.getChildren().addAll(button);
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
}
