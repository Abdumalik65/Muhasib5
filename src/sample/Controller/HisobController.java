package sample.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.Data.User;
import sample.Model.HisobModels;
import sample.Excel.ExportToExcel;
import sample.Tools.DasturlarRoyxati;
import sample.Tools.GetDbData;
import sample.Tools.Tugmachalar;
import sample.Data.Hisob;

import java.sql.Connection;

public class HisobController {
    TableView<Hisob> tableView = new TableView<>();
    ObservableList<Hisob> tableViewObservableList = FXCollections.observableArrayList();
    Stage stage = new Stage();
    Integer width = 600;
    Integer height = 400;
    Scene scene;
    VBox vBox;
    TextField textField = new TextField();
    Tugmachalar tugmachalar = new Tugmachalar();
    User user = GetDbData.getUser();

    CustomGrid customGrid = new CustomGrid();

    HisobModels hisobModels = new HisobModels();
    Hisob hisobCursor, doubleClickedRow;

    Boolean doubleClick = false;
    ObservableList<Hisob> addedHIsobList = FXCollections.observableArrayList();

    Connection connection;
    String tableName = "Hisob";

    public HisobController() {
    }

    public HisobController(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        String classSimpleName = getClass().getSimpleName();
        DasturlarRoyxati.dastur(connection, user, classSimpleName);
    }

    public HisobController(String tableName) {
        this.tableName = tableName;
        hisobModels.setTABLENAME(tableName);
    }

    public void display(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        vBox = new VBox();
        vBox.setPadding(new Insets(5));
        setTableColumns();
        setTableItems(connection);
        if (tableViewObservableList.size()>0) {
            tableView.getSelectionModel().selectFirst();
        }
        setTableMethods();
        HBox.setHgrow(textField, Priority.ALWAYS);
        tugmachalar.getChildren().add(textField);
        vBox.getChildren().addAll(tugmachalar, tableView);
        scene = new Scene(vBox, width,height);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }

    public void display(Connection connection, User user, ObservableList<Hisob> tableViewObservableList) {
        this.tableViewObservableList = tableViewObservableList;
        this.connection = connection;
        this.user = user;
        vBox = new VBox();
        vBox.setPadding(new Insets(5));
        setTableColumns();
        tableView.setItems(tableViewObservableList);
        if (tableViewObservableList.size()>0) {
            tableView.getSelectionModel().selectFirst();
        }
        setTableMethods();
        HBox.setHgrow(textField, Priority.ALWAYS);
        tugmachalar.getChildren().add(textField);
        vBox.getChildren().addAll(tugmachalar, tableView);
        scene = new Scene(vBox, width,height);
        stage.setScene(scene);
        stage.showAndWait();
    }

    public void setTableColumns() {
        TableColumn<Hisob, Integer> id = new TableColumn<>("N");
        id.setMinWidth(15);
        id.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Hisob, String> hisob_column = new TableColumn<>("Hisob nomi");
        hisob_column.setMinWidth(200);
        hisob_column.setCellValueFactory(new PropertyValueFactory<>("text"));

        TableColumn<Hisob, Integer> hisob_balans = new TableColumn<>("Balans");
        hisob_balans.setMinWidth(45);
        hisob_balans.setCellValueFactory(new PropertyValueFactory<>("balans"));

        TableColumn<Hisob, String> hisob_rasm = new TableColumn<>("Rasm");
        hisob_rasm.setMinWidth(200);
        hisob_rasm.setCellValueFactory(new PropertyValueFactory<>("rasm"));

        TableColumn<Hisob, String> hisob_email = new TableColumn<>("Email");
        hisob_email.setMinWidth(200);
        hisob_email.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Hisob, String> hisob_mobile = new TableColumn<>("Telefon");
        hisob_mobile.setMinWidth(150);
        hisob_mobile.setCellValueFactory(new PropertyValueFactory<>("mobile"));

        tableView.getColumns().addAll(id, hisob_column, hisob_email, hisob_mobile);
    }

    public void setTableItems(Connection connection) {
        tableViewObservableList = hisobModels.get_data(this.connection);
        tableView.setItems(tableViewObservableList);
    }

    public void setTableMethods() {
        tableView.setRowFactory( tv -> {
            TableRow<Hisob> row = new TableRow<>();
            row.setOnMouseClicked(event1 -> {
                if (event1.getClickCount() == 2 && (! row.isEmpty()) ) {
                    doubleClick = true;
                    doubleClickedRow = row.getItem();
                    stage.close();
                }
            });
            return row ;
        });

        EventHandler<ActionEvent> cancelEvent = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                vBox.getChildren().remove(customGrid);
                vBox.getChildren().addAll(tugmachalar, tableView);
            }
        };

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            hisobCursor = newValue;
        });

        tableView.setRowFactory( tv -> {
            TableRow<Hisob> row = new TableRow<>();
            row.setOnMouseClicked(event1 -> {
                if (event1.getClickCount() == 2 && (! row.isEmpty()) ) {
                    doubleClick = true;
                    doubleClickedRow = row.getItem();
                    stage.close();
                }
            });
            return row ;
        });

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            Taftish(oldValue, newValue);
        });

        tugmachalar.getAdd().setOnAction(event -> {
            Hisob hisob = new Hisob(null, "", .0, "",  "", "", user.getId());
            vBox.getChildren().removeAll(tugmachalar, tableView);
            vBox.getChildren().add(customGrid);
            customGrid.showAdd(hisob);

            customGrid.getCancelButton().setOnAction(cancelEvent);
        });

        tugmachalar.getDelete().setOnAction(event -> {
            if (hisobCursor != null) {
                vBox.getChildren().removeAll(tugmachalar, tableView);
                vBox.getChildren().add(customGrid);
                customGrid.showDelete(hisobCursor);
            }

            customGrid.getCancelButton().setOnAction(cancelEvent);
        });

        tugmachalar.getEdit().setOnAction(event -> {
            if (hisobCursor != null) {
                vBox.getChildren().removeAll(tugmachalar, tableView);
                vBox.getChildren().add(customGrid);
                customGrid.showEdit(hisobCursor);
            }

            customGrid.getCancelButton().setOnAction(cancelEvent);
        });

        tugmachalar.getExcel().setOnAction(event -> {
            ExportToExcel exportToExcel = new ExportToExcel();
            exportToExcel.hisoblar(tableViewObservableList);
        });
    }

    public TableView getTableView() {
        return tableView;
    }

    public void setTableView(TableView tableView) {
        this.tableView = tableView;
    }

    public ObservableList getTableViewObservableList() {
        return tableViewObservableList;
    }

    public void setTableViewObservableList(ObservableList tableViewObservableList) {
        this.tableViewObservableList = tableViewObservableList;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public void Taftish(String oldValue, String newValue) {
        ObservableList<Hisob> subentries = FXCollections.observableArrayList();
        newValue = newValue.toLowerCase();

        if ( oldValue != null && (newValue.length() < oldValue.length()) ) {
            tableView.setItems( tableViewObservableList );
        }

        for ( Hisob hisob: tableViewObservableList ) {
            if (hisob.getText().toLowerCase().contains(newValue)) {
                subentries.add(hisob);
            }
        }
        tableView.setItems(subentries);
    }

    class CustomGrid extends GridPane {
        Label hisobNomiLabel = new Label("Hisob nomi");
        Label eMailLabel = new Label("@eMail");
        Label telefonLabel = new Label("Telefon");
        TextField hisobNomiTextField = new TextField();
        TextField eMailTextField = new TextField();
        TextField telefonTextField = new TextField();
        Button qaydEtButton = new Button("Qayd et");
        Button cancelButton = new Button("<<");
        Integer rowIndex = 0;
        Hisob hisob;

        public CustomGrid() {
            qaydEtButton.setMaxWidth(2000);
            qaydEtButton.setPrefWidth(150);
            cancelButton.setMaxWidth(2000);
            cancelButton.setPrefWidth(150);
            setVgap(10);
        }

        public void showAdd(Hisob hisob) {
            this.hisob = hisob;
            qaydEtButton = new Tugmachalar().getAdd();
            qaydEtButton.setMaxWidth(2000);
            qaydEtButton.setPrefWidth(150);
            disableGrid(false);
            initData(hisob);
            getGrid();
            qaydEtButton.setOnAction(event -> {
                addAction(hisob);
                addedHIsobList.add(hisob);
                cancelButton.fire();
                tableView.getSelectionModel().select(hisob);
                tableView.scrollTo(hisob);
                tableView.refresh();
            });
        }

        public void showDelete(Hisob hisob) {
            this.hisob = hisob;
            qaydEtButton = new Tugmachalar().getDelete();
            qaydEtButton.setMaxWidth(2000);
            qaydEtButton.setPrefWidth(150);
            disableGrid(true);
            initData(hisob);
            getGrid();
            qaydEtButton.setOnAction(event -> {
                deleteAction(hisob);
                cancelButton.fire();
                tableView.refresh();
            });
        }

        public void showEdit(Hisob hisob) {
            this.hisob = hisob;
            qaydEtButton = new Tugmachalar().getEdit();
            qaydEtButton.setMaxWidth(2000);
            qaydEtButton.setPrefWidth(150);
            disableGrid(false);
            initData(hisob);
            getGrid();
            qaydEtButton.setOnAction(event -> {
                editAction(hisob);
                cancelButton.fire();
                tableView.refresh();
            });
        }

        public void initData (Hisob hisob) {
            hisobNomiTextField.setText(hisob.getText());
            eMailTextField.setText(hisob.getEmail());
            telefonTextField.setText(hisob.getMobile());
        }

        public void saveData (Hisob hisob) {
            hisob.setText(hisobNomiTextField.getText());
            hisob.setEmail(eMailTextField.getText());
            hisob.setMobile(telefonTextField.getText());
        }

        public void getGrid() {
            getChildren().removeAll(getChildren());
            rowIndex = 0;
            add(hisobNomiLabel, 0, rowIndex, 1, 1);
            add(hisobNomiTextField, 1, rowIndex, 1, 1);
            GridPane.setHgrow(hisobNomiTextField,  Priority.ALWAYS);
            rowIndex++;
            add(eMailLabel, 0, rowIndex, 1, 1);
            add(eMailTextField, 1, rowIndex, 1, 1);
            GridPane.setHgrow(eMailTextField, Priority.ALWAYS);
            rowIndex++;
            add(telefonLabel, 0, rowIndex, 1, 1);
            add(telefonTextField, 1, rowIndex, 1, 1);
            GridPane.setHgrow(telefonTextField, Priority.ALWAYS);
            rowIndex++;
            add(qaydEtButton, 1, rowIndex, 1, 1);
            GridPane.setHgrow(qaydEtButton, Priority.ALWAYS);
            add(cancelButton, 0, rowIndex, 1, 1);
            GridPane.setHgrow(cancelButton, Priority.ALWAYS);
        }

        public void addAction(Hisob hisob) {
            saveData(hisob);
            hisobModels.insert_data(connection, hisob);
            GetDbData.getHisobObservableList().add(hisob);
            tableViewObservableList.add(hisob);
        }

        public void deleteAction(Hisob hisob) {
            hisobModels.delete_data(connection, hisob);
            tableViewObservableList.remove(hisob);
        }

        public void editAction(Hisob hisob) {
            saveData(hisob);
            hisobModels.update_data(connection, hisob);
        }
        public void disableGrid(Boolean disable) {
            hisobNomiTextField.setDisable(disable);
            eMailTextField.setDisable(disable);
            telefonTextField.setDisable(disable);
        }

        public Button getCancelButton() {
            return cancelButton;
        }
    }

    public Boolean getDoubleClick() {
        return doubleClick;
    }

    public Hisob getDoubleClickedRow() {
        doubleClickedRow = tableView.getSelectionModel().getSelectedItem();
        return doubleClickedRow;
    }

    public ObservableList<Hisob> getAddedHIsobList() {
        return addedHIsobList;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
