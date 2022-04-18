package sample.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.Data.Standart;
import sample.Data.User;
import sample.Data.Valuta;
import sample.Model.StandartModels;
import sample.Model.ValutaModels;
import sample.Excel.ExportToExcel;
import sample.Tools.Alerts;
import sample.Tools.DasturlarRoyxati;
import sample.Tools.GUIUtils;
import sample.Tools.Tugmachalar;
import java.sql.Connection;

public class ValutaController {
    Valuta valutaCursor;
    ValutaModels valutaModels = new ValutaModels();
    Valuta addingValuta = new Valuta();
    ObservableList<Valuta> valuta_options = FXCollections.observableArrayList();
    TableView<Valuta> tableView;
    ComboBox<Standart> mavqeComboBox;
    TextField textField = new TextField();
    Tugmachalar toolBar = new Tugmachalar();
    Boolean qaydButtonClicked = false;
    Boolean doubleClick = false;
    Valuta doubleClickedRow;
    Stage stage = new Stage();
    Connection connection;
    User user;

    public void display(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        String classSimpleName = getClass().getSimpleName();
        DasturlarRoyxati.dastur(connection, user, classSimpleName);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Valuta turlari");
        BorderPane borderpane = new BorderPane();
        MenuBar mainMenu;
        VBox centerPane = new VBox();
        Pane pane = new Pane();
        Label left_label = new Label("Chap tomon");
        Label right_label = new Label("O`ng tomon");
        HBox bottom = new HBox();
/*************************************************
 **                 Yuqori                      **
 *************************************************/
        mainMenu = new MenuBar();
        Menu menuFile = new Menu("File");
        Menu menuView = new Menu("View");
        Menu menuEdit = new Menu("Edit");
        Menu menuHelp = new Menu("Help");
        mainMenu.getMenus().addAll(menuFile, menuEdit, menuView, menuHelp);

        textField.textProperty().addListener((observable, oldValue, newValue) ->
        {
            Taftish(oldValue, newValue);
        });

/*************************************************
 **                  O`rta                      **
 *************************************************/
/*************************************************
 **                   Table                      **
 *************************************************/
        tableView = new TableView<Valuta>();
        mavqeComboBox = initMavqeComboBox();
        TableColumn<Valuta, Integer> id = new TableColumn<>();
        id.setText("N");
        id.setMinWidth(15);
        id.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Valuta, String> valuta_column = new TableColumn<>();
        valuta_column.setText("Valuta turi");
        valuta_column.setMinWidth(200);
        valuta_column.setCellValueFactory(new PropertyValueFactory<>("valuta"));

        TableColumn<Valuta, Integer> valuta_status = new TableColumn<>();
        valuta_status.setText("Mavqe");
        valuta_status.setMinWidth(15);
        valuta_status.setCellValueFactory(new PropertyValueFactory<>("status"));

        tableView.getColumns().addAll(id, valuta_column, statusColumn());
        valuta_options = valutaModels.get_data(connection);
        tableView.setItems(valuta_options);
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            valutaCursor = newValue;
        });
        tableView.setRowFactory( tv -> {
            TableRow<Valuta> row = new TableRow<>();
            row.setOnMouseClicked(event1 -> {
                if (event1.getClickCount() == 2 && (! row.isEmpty()) ) {
                    doubleClick = true;
                    doubleClickedRow = row.getItem();
                    stage.close();
                }
            });
            return row ;
        });
        centerPane.getChildren().addAll(toolBar, tableView);
/*************************************************
 **                End Table                    **
 *************************************************/
/*************************************************
 **               Add Delete                    **
 *************************************************/
        toolBar.getAdd().setOnAction(e->{
                AddRecord addRecord = new AddRecord(connection, user);
                valutaCursor = tableView.getSelectionModel().getSelectedItem();
                addRecord.display();
                tableView.refresh();
        });
        toolBar.getEdit().setOnAction(e->{
            ReplaceRecord replaceRecord  = new ReplaceRecord(connection, user);
            valutaCursor = tableView.getSelectionModel().getSelectedItem();
            if (valutaCursor != null) {
                replaceRecord.display(valutaCursor);
            }
        });
        toolBar.getDelete().setOnAction(e -> {
            ObservableList<Valuta> selectedRow, allRows;
            allRows = tableView.getItems();
            selectedRow = tableView.getSelectionModel().getSelectedItems();
            if (selectedRow != null) {
                if (selectedRow.size()>0) {
                    if (Alerts.haYoq(selectedRow.get(0).getValuta() + " o`chiriladi", "Davom etamizmi???")) {
                        selectedRow.forEach(item -> {
                            valutaModels.delete_data(connection, item);
                        });
                        selectedRow.forEach(allRows::remove);
                        tableView.refresh();
                    }
                }
            }
        });

        toolBar.getExcel().setOnAction(event -> {
            ExportToExcel exportToExcel = new ExportToExcel();
            exportToExcel.ValutTable("Valyuta", valuta_options);
        });
        toolBar.getChildren().add(0, textField);
/*************************************************
 **             End Add Delete                  **
 *************************************************/
/*************************************************
 **                   Past                      **
 *************************************************/
/*************************************************
 **                Umumiy kod                   **
 *************************************************/
        left_label.setPadding(new Insets(5));
        right_label.setPadding(new Insets(5));
        HBox.setHgrow(left_label, Priority.ALWAYS);
        HBox.setHgrow(pane, Priority.ALWAYS);
        HBox.setHgrow(right_label, Priority.NEVER);
        VBox.setVgrow(tableView, Priority.ALWAYS);
        bottom.getChildren().addAll(left_label, pane, right_label);
        bottom.setAlignment(Pos.CENTER);
//        borderpane.setTop(mainMenu);
        borderpane.setCenter(centerPane);
        borderpane.setBottom(bottom);
        Scene scene = new Scene(borderpane, 400, 400);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }

    public void Taftish(String oldValue, String newValue) {
        ObservableList<Valuta> subentries = FXCollections.observableArrayList();
        newValue = newValue.toLowerCase();

        if ( oldValue != null && (newValue.length() < oldValue.length()) ) {
            tableView.setItems( valuta_options );
        }

        for ( Valuta valuta: valuta_options ) {
            if (valuta.getValuta().toLowerCase().contains(newValue)) {
                subentries.add(valuta);
            }
        }
            tableView.setItems(subentries);
    }

    class AddRecord {
        Stage stage;
        Scene scene;
        GridPane gridPane = new GridPane();
        Label valutaTuriLabel = new Label("Valuta turi");
        Label valutaMavqesiLabel = new Label("Valuta mavqesi");
        TextField valutaTuriTextField = new TextField();
        ComboBox<Standart> mavqeComboBox = new ComboBox<>();
        Button qaydButton = new Button("Qayd et");
        Connection connection;
        User user;
        String alertString = "";

        public AddRecord(Connection connection, User user) {
            this.connection = connection;
            this.user = user;
        }

        private ComboBox<Standart> yangiMavqeComboBox() {
            ComboBox<Standart> comboBox = new ComboBox<>();
            HBox.setHgrow(comboBox, Priority.ALWAYS);
            comboBox.setMaxWidth(2000);
            comboBox.setPrefWidth(15);
            StandartModels standartModels = new StandartModels("PulMavqesi");
            ObservableList<Standart> observableList = standartModels.get_data(connection);
            comboBox.setItems(observableList);
            comboBox.getSelectionModel().selectFirst();
            return comboBox;
        }

        public void display() {
            HBox.setHgrow(valutaTuriLabel, Priority.NEVER);
            VBox.setVgrow(valutaTuriLabel, Priority.NEVER);
            stage = new Stage();
            mavqeComboBox = yangiMavqeComboBox();
            stage.setTitle("Yangi valuta");
            gridPane.setPadding(new Insets(10));
            gridPane.setHgap(5);
            gridPane.setVgap(5);
            gridPane.add(valutaTuriLabel, 0, 0, 1, 1);
            gridPane.add(valutaTuriTextField, 1, 0, 1, 1);
            HBox.setHgrow(valutaMavqesiLabel, Priority.ALWAYS);
            gridPane.add(valutaMavqesiLabel, 0, 1, 1, 1);
            GridPane.setHgrow(valutaMavqesiLabel, Priority.ALWAYS);

            gridPane.add(mavqeComboBox, 1, 1, 1, 1);

            gridPane.add(qaydButton, 1, 3, 1, 1);

            qaydButtonClicked = false;

            qaydButton.setOnAction(e->{
                int qaydEtma = checkData();
                if (qaydEtma == 0) {
                    valuta_options.add(valuta_options.size(), new Valuta());
                    addingValuta = valuta_options.get(valuta_options.size() - 1);
                    addingValuta.setValuta(valutaTuriTextField.getText());
                    addingValuta.setStatus(mavqeComboBox.getValue().getId());
                    addingValuta.setUserId(user.getId());
                    qaydButtonClicked = true;
                    addingValuta.setId(valutaModels.insert_data(connection, addingValuta));
                    stage.close();
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Diqqat qiling !!!");
                    alert.setHeaderText(alertString);
                    alert.setContentText("Malumotni to`liq kiriting");
                    alert.showAndWait();
                }
            });
            Scene scene = new Scene(gridPane, 300, 130);
            stage.setScene(scene);
            stage.showAndWait();
            HBox.setHgrow(gridPane, Priority.ALWAYS);
            VBox.setVgrow(gridPane, Priority.ALWAYS);
        }
        private int checkData() {
            alertString = "";
            int qaydEtma = 0;
            addingValuta =  valutaCursor;
            if (valutaTuriTextField.getText().trim().isEmpty()) {
                qaydEtma++;
                alertString += qaydEtma+ ". Valuta nomi kiritilmadi\n";
            }
            return qaydEtma;
        }
    }
    public Valuta add(Connection connection, User user) {
        AddRecord addRecord = new AddRecord(connection, user);
        addRecord.display();
        return addingValuta;
    }

    class DeleteRecord {}

    class ReplaceRecord {
        Stage stage;
        Scene scene;
        GridPane gridPane = new GridPane();
        Label valutaTuriLabel = new Label("Valuta turi");
        Label valutaMavqesiLabel = new Label("Valuta mavqesi");
        TextField valutaTuriTextField = new TextField();
        ComboBox<Standart> mavqeComboBox = new ComboBox<>();
        Button replaceButton = new Button("Qayd et");
        Connection connection;
        User user;
        String alertString = "";

        public ReplaceRecord(Connection connection, User user) {
            this.connection = connection;
            this.user = user;
        }

        private ComboBox<Standart> yangiMavqeComboBox() {
            ComboBox<Standart> comboBox = new ComboBox<>();
            HBox.setHgrow(comboBox, Priority.ALWAYS);
            comboBox.setMaxWidth(2000);
            comboBox.setPrefWidth(15);
            StandartModels standartModels = new StandartModels("PulMavqesi");
            ObservableList<Standart> observableList = standartModels.get_data(connection);
            comboBox.setItems(observableList);
            comboBox.getSelectionModel().selectFirst();
            return comboBox;
        }

        public void display(Valuta valuta) {
            HBox.setHgrow(valutaTuriLabel, Priority.NEVER);
            VBox.setVgrow(valutaTuriLabel, Priority.NEVER);
            stage = new Stage();
            stage.setTitle("Yangi valuta");
            mavqeComboBox = yangiMavqeComboBox();
            ObservableList<Standart> observableList = mavqeComboBox.getItems();
            if (observableList.size()>0) {
                for (Standart standart: observableList) {
                    if (standart.getId().equals(valuta.getStatus())) {
                        mavqeComboBox.getSelectionModel().select(standart);
                        break;
                    }
                }
            }
            valutaTuriTextField.setText(valuta.getValuta());
            gridPane.setPadding(new Insets(10));
            gridPane.setHgap(5);
            gridPane.setVgap(5);
            gridPane.add(valutaTuriLabel, 0, 0, 1, 1);
            gridPane.add(valutaTuriTextField, 1, 0, 1, 1);
            gridPane.add(valutaMavqesiLabel, 0, 1, 1, 1);
            gridPane.add(mavqeComboBox, 1, 1, 1, 1);
            gridPane.add(replaceButton, 1, 3, 1, 1);
            HBox.setHgrow(valutaMavqesiLabel, Priority.ALWAYS);
            GridPane.setHgrow(valutaMavqesiLabel, Priority.ALWAYS);
            replaceButton.setOnAction(e -> {
                int qaydEtma = checkData();
                if (qaydEtma == 0) {
                    valuta.setValuta(valutaTuriTextField.getText());
                    valuta.setStatus(mavqeComboBox.getValue().getId());
                    valuta.setUserId(user.getId());
                    tableView.refresh();
                    valutaModels.update_data(connection, valuta);
                    stage.close();
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Diqqat qiling !!!");
                    alert.setHeaderText(alertString);
                    alert.setContentText("Malumotni to`liq kiriting");
                    alert.showAndWait();
                }
            });
            Scene scene = new Scene(gridPane, 300, 130);
            stage.setScene(scene);
            stage.showAndWait();
            HBox.setHgrow(gridPane, Priority.ALWAYS);
            VBox.setVgrow(gridPane, Priority.ALWAYS);
        }

        private int checkData() {
            int qaydEtma = 0;
            addingValuta =  valutaCursor;
            if (valutaTuriTextField.getText().trim().isEmpty()) {
                qaydEtma++;
                alertString += qaydEtma+ ". Valuta nomi kiritilmadi\n";
            }
            return qaydEtma;
        }
    }

    public Boolean getDoubleClick() {
        return doubleClick;
    }

    public void setDoubleClick(Boolean doubleClick) {
        this.doubleClick = doubleClick;
    }

    public Valuta getDoubleClickedRow() {
        return doubleClickedRow;
    }

    public void setDoubleClickedRow(Valuta doubleClickedRow) {
        this.doubleClickedRow = doubleClickedRow;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private ComboBox<Standart> initMavqeComboBox() {
        ComboBox<Standart> comboBox = new ComboBox<>();
        StandartModels standartModels = new StandartModels("PulMavqesi");
        ObservableList<Standart> mavqeList = standartModels.get_data(connection);
        comboBox = new ComboBox<>(mavqeList);
        if (mavqeList.size()>0) {
            comboBox.getSelectionModel().select(mavqeList.get(0));
        }
        comboBox.setMaxWidth(2000);
        comboBox.setPrefWidth(150);
        comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                valutaCursor.setStatus(newValue.getId());
/*
                valutaModels.update_data(connection, valutaCursor);
                tableView.refresh();
*/
            }
        });
        return comboBox;
    }

    private TableColumn<Valuta, Integer> statusColumn() {
        TableColumn<Valuta, Integer> onlineColumn = new TableColumn<>("Mavqe");
        onlineColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        onlineColumn.setCellFactory(column -> {
            TableCell<Valuta, Integer> cell = new TableCell<Valuta, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        Standart standart = pulMavqesi(item);
                        if (standart != null) {
                            setText(standart.getText());
                        } else {
                            setText("");
                        }
                    }
                    setAlignment(Pos.CENTER);
                }
            };
            return cell;
        });
        return onlineColumn;
    }

    private Standart pulMavqesi(Integer id) {
        Standart standart = null;
        for (Standart s: mavqeComboBox.getItems()) {
            if (s.getId().equals(id)) {
                standart = s;
                break;
            }
        }
        return standart;
    }



}
