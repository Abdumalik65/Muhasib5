package sample.Controller;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.Config.MySqlDBGeneral;
import sample.Data.*;
import sample.Enums.ServerType;
import sample.Model.SerialNumbersModels;
import sample.Tools.*;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SerialNumbersController extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    VBox centerPane = new VBox();
    VBox rightPane = new VBox();
    TableView<Hisob> hisobTableView = new TableView<>();
    TableView<SerialNumber> serialNumberTableView = new TableView<>();
    Tugmachalar tugmachalar = new Tugmachalar();
    TableViewAndoza tableViewAndoza = new TableViewAndoza();
    TextField textField = new TextField();
    TextField serialNumberQidir = new TextField();
    ObservableList<Hisob> hisobObservableList;
    ObservableList<SerialNumber> serialNumbers;
    Connection connection;
    User user = new User(1, "admin", "", "admin");
    int padding = 3;
    Font font = Font.font("Arial", FontWeight.BOLD,20);
    SerialNumbersModels serialNumbersModels = new SerialNumbersModels();

    public static void main(String[] args) {
        launch(args);
    }

    public SerialNumbersController() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        GetDbData.initData(connection);
        ibtido();
    }

    public SerialNumbersController(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        String classSimpleName = getClass().getSimpleName();
        DasturlarRoyxati.dastur(connection, user, classSimpleName);
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
        SerialNumbersModels serialNumbersModels = new SerialNumbersModels();
        serialNumbers = serialNumbersModels.get(connection);
    }

    private void initHisobTableView() {
        SetHVGrow.VerticalHorizontal(hisobTableView);
        hisobTableView.setMaxWidth(210);
        hisobTableView.setPadding(new Insets(padding));
        TableColumn<Hisob, String> hisobText = tableViewAndoza.getHisobTextColumn();
        hisobText.setMinWidth(200);
        hisobTableView.getColumns().add(hisobText);
        hisobTableView.setItems(hisobObservableList);
        hisobTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
            }
        });
    }

    private void initTugmachalar() {
        tugmachalar.getChildren().remove(tugmachalar.getExcel());
        tugmachalar.getChildren().remove(tugmachalar.getEdit());
        tugmachalar.getChildren().add(serialNumberQidir);
        tugmachalar.getAdd().setOnAction(event -> {
            System.out.println("Add");
            YangiSeriyaRaqami yangiSeriyaRaqami = new YangiSeriyaRaqami(connection, user);
            Boolean yangi = yangiSeriyaRaqami.display();
            serialNumbers.removeAll(serialNumbers);
            serialNumbers.addAll(serialNumbersModels.get(connection));
            serialNumberTableView.refresh();
        });
        tugmachalar.getDelete().setOnAction(event -> {
            System.out.println("Delate");
            SerialNumber serialNumber = serialNumberTableView.getSelectionModel().getSelectedItem();
            if (serialNumber != null) {
                Boolean delete = Alerts.haYoq("Diqqat !!! "+ serialNumber.getSerialNumber() + " seriya raqami o`chirilish arafasida", "Davom etaymi ???");
                if (delete) {
                    serialNumbersModels.delete(connection, serialNumber);
                    serialNumbers.remove(serialNumber);
                    serialNumberTableView.refresh();
                }
            }
        });
    }

    private TableView<SerialNumber> initSerialNumbersTableView() {
        TableView<SerialNumber> tableView = new TableView<>();
        SetHVGrow.VerticalHorizontal(tableView);
        tableView.getColumns().addAll(serialNumbersColumn(), sanaColumn(), invoiceColumn(), hisobColumn(), tovarColumn());
        tableView.setItems(serialNumbers);
        return tableView;
    }

    private TableColumn<SerialNumber, Date> sanaColumn() {
        TableColumn<SerialNumber, Date> sanaColumn = new TableColumn<>("Sana");
        sanaColumn.setMinWidth(80);
        sanaColumn.setCellValueFactory(new PropertyValueFactory<>("sana"));
        sanaColumn.setCellFactory(column -> {
            TableCell<SerialNumber, Date> cell = new TableCell<SerialNumber, Date>() {
                private SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy\n  HH:mm:ss");
                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        setText(format.format(item));
                    }
                }
            };
            cell.setAlignment(Pos.TOP_CENTER);
            return cell;
        });
        return sanaColumn;
    }

    private TableColumn<SerialNumber, String> invoiceColumn() {
        TableColumn<SerialNumber, String> invoiceColumn = new TableColumn<>("Invoice №");
        invoiceColumn.setMinWidth(100);
        invoiceColumn.setCellValueFactory(new PropertyValueFactory<>("invoice"));
        return invoiceColumn;
    }

    public TableColumn<SerialNumber, Integer> hisobColumn() {
        TableColumn<SerialNumber, Integer> tovarColumn = new TableColumn<>("Hisob");
        tovarColumn.setMinWidth(200);
        tovarColumn.setCellValueFactory(new PropertyValueFactory<>("hisob"));
        tovarColumn.setCellFactory(column -> {
            TableCell<SerialNumber, Integer> cell = new TableCell<SerialNumber, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        Hisob hisob = GetDbData.getHisob(item);
                        if (hisob != null) {

                            setText(hisob.getText());
                        } else {
                            setText("");
                        }
                    }
                }
            };
            return cell;
        });
        return tovarColumn;
    }

    public TableColumn<SerialNumber, Integer> tovarColumn() {
        TableColumn<SerialNumber, Integer> tovarColumn = new TableColumn<>("Tovar");
        tovarColumn.setMinWidth(200);
        tovarColumn.setCellValueFactory(new PropertyValueFactory<>("tovar"));
        tovarColumn.setCellFactory(column -> {
            TableCell<SerialNumber, Integer> cell = new TableCell<SerialNumber, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        Standart tovar = GetDbData.getTovar(item);
                        if (tovar != null) {

                            setText(tovar.getText());
                        } else {
                            setText("");
                        }
                    }
                }
            };
            return cell;
        });
        return tovarColumn;
    }

    private TableColumn<SerialNumber, String> serialNumbersColumn() {
        TableColumn<SerialNumber, String> invoiceColumn = new TableColumn<>("Serial number");
        invoiceColumn.setMinWidth(200);
        invoiceColumn.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        return invoiceColumn;
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
    }

    private void initCenterPane() {
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        VBox.setVgrow(centerPane, Priority.ALWAYS);
        centerPane.setPadding(new Insets(padding));
        serialNumberTableView = initSerialNumbersTableView();
        initTugmachalar();
        initTaftishTextField();
        centerPane.getChildren().addAll(tugmachalar, serialNumberTableView);
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
        scene = new Scene(borderpane, 800, 500);
        stage.setScene(scene);
    }

    private void initTaftishTextField() {
        HBox.setHgrow(serialNumberQidir, Priority.ALWAYS);
        serialNumberQidir.setPadding(new Insets(padding));
        serialNumberQidir.setMaxWidth(210);
        serialNumberQidir.setPromptText("QIDIR");
        serialNumberQidir.textProperty().addListener((observable, oldValue, newValue) -> {
            Taftish(oldValue, newValue);
        });

    }

    public void Taftish(String oldValue, String newValue) {
        ObservableList<SerialNumber> subentries = FXCollections.observableArrayList();
        newValue = newValue.toLowerCase();

        if ( oldValue != null && (newValue.length() < oldValue.length()) ) {
            serialNumberTableView.setItems( serialNumbers );
        }

        for ( SerialNumber serialNumber: serialNumbers ) {
            if (serialNumber.getSerialNumber().toLowerCase().contains(newValue)) {
                subentries.add(serialNumber);
            }
        }
        serialNumberTableView.setItems(subentries);
    }

}
