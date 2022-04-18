package sample.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import sample.Data.Standart;
import sample.Data.User;
import sample.Excel.ExportToExcel;
import sample.Model.StandartModels;
import sample.Tools.CustomWindow;
import sample.Tools.DasturlarRoyxati;
import sample.Tools.Tugmachalar;

import java.sql.Connection;

public class PrintersController {
    Tugmachalar tugmachalar = new Tugmachalar();
    TextField textField = new TextField();
    StandartModels standartModels = new StandartModels();
    CustomWindow customWindow = new CustomWindow(false, 600,400);
    ObservableList<Standart> standarts;
    TableView<Standart> tableView = new TableView();
    String tableName;
    Connection connection;
    User user;
    Boolean doubleClick = false;
    Standart doubleClickedRow;

    public PrintersController(Connection connection, User user, String tableName) {
        this.connection = connection;
        this.user = user;
        this.tableName = tableName;
        customWindow.setStageTitle(tableName);
        tugmachalar.getChildren().add(0, textField);
        customWindow.getRightVBox().getChildren().addAll(tugmachalar, tableView);

        textField.textProperty().addListener(((observable, oldValue, newValue) -> {
            Taftish(oldValue, newValue);
        }));
        tugmachalar.getAdd().setOnAction(event -> {
            StageTitles stageTitles = new StageTitles(connection, user, 1);
            stageTitles.showAndWait();
        });
        tugmachalar.getEdit().setOnAction(event -> {
            Standart standart = tableView.getSelectionModel().getSelectedItem();
            if (standart != null) {
                StageTitles stageTitles = new StageTitles(connection, user, standart, 2);
                stageTitles.showAndWait();
            }
        });
        tugmachalar.getDelete().setOnAction(event -> {
            Standart standart = tableView.getSelectionModel().getSelectedItem();
            if (standart != null) {
                StageTitles stageTitles = new StageTitles(connection, user, standart, 3);
                stageTitles.showAndWait();
            }
        });

        tugmachalar.getExcel().setOnAction(event -> {
            ExportToExcel exportToExcel = new ExportToExcel();
            exportToExcel.standartTable(tableName, standarts);
        });
    }

    public void showAndWait()  {
        customWindow.getStage().getIcons().add(new Image("/sample/images/Icons/WindowsTable.png"));

        standartModels.setTABLENAME(tableName);
        standarts = standartModels.get_data(connection);
        TableColumn<Standart, Integer> id = new TableColumn<>("N");
        id.setMinWidth(15);
        id.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Standart, String> text = new TableColumn<>(tableName);
        text.setMinWidth(550);
        text.setCellValueFactory(new PropertyValueFactory<>("text"));
        tableView.getColumns().addAll(id, text);
        tableView.setItems(standarts);
        tableView.setPadding(new Insets(5));
        tableView.setRowFactory( tv -> {
            TableRow<Standart> row = new TableRow<>();
            row.setOnMouseClicked(event1 -> {
                if (event1.getClickCount() == 2 && (! row.isEmpty()) ) {
                    doubleClick = true;
                    doubleClickedRow = row.getItem();
                    customWindow.getStage().close();
                }
            });
            return row ;
        });
        tugmachalar.setPadding(new Insets(5));
        customWindow.showAndWait();
    }

    class StageTitles {
        Integer amal = 1;
/*
        1 - add
        2 - edit
        3 - delete
*/
        Standart standart = new Standart();
        CustomWindow customWindow = new CustomWindow(false, 230, 80);
        Connection connection;
        User user;
        GridPane gridPane = new GridPane();
        Button qaydEtButton = new Button("Qayd et");
        Label standartLabel = new Label(tableName);
        TextField standartTextField = new TextField();

        public StageTitles(Connection connection, User user, Integer amal) {
            this.connection = connection;
            this.user = user;
            this.amal = amal;
            customWindow.setStageTitle("Qo`sh");
        }

        public StageTitles(Connection connection, User user, Standart standart, Integer amal) {
            this.connection = connection;
            this.user = user;
            this.standart = standart;
            this.amal = amal;
            standartTextField.setText(standart.getText());
             switch (amal) {
                case 1:
                    customWindow.setStageTitle("Qo`sh");
                    break;
                case 2:
                    customWindow.setStageTitle("O`zgartir");
                    break;
                case 3:
                    customWindow.setStageTitle("O`chir");
                    break;
            }
        }

        public void showAndWait() {
            switch (amal) {
                case 1:
                    customWindow.getStage().getIcons().add(new Image("/sample/images/Icons/add.png"));
                    qaydEtButton = new Tugmachalar().getAdd();
                    break;
                case 2:
                    customWindow.getStage().getIcons().add(new Image("/sample/images/Icons/edit.png"));
                    qaydEtButton = new Tugmachalar().getEdit();
                    break;
                case 3:
                    customWindow.getStage().getIcons().add(new Image("/sample/images/Icons/delete.png"));
                    qaydEtButton = new Tugmachalar().getDelete();
                    break;
            }
            qaydEtButton.setOnAction(event -> {
                switch (amal) {
                    case 1:
                        standart.setText(standartTextField.getText());
                        standart.setUserId(user.getId());
                        standart.setId(standartModels.insert_data(connection, standart));
                        standarts.add(standart);
                        tableView.scrollTo(standart);
                        break;
                    case 2:
                        standart.setText(standartTextField.getText());
                        standart.setUserId(user.getId());
                        standartModels.update_data(connection, standart);
                        break;
                    case 3:
                        standartModels.delete_data(connection, standart);
                        standarts.remove(standart);
                        break;
                }
                tableView.refresh();
                customWindow.getStage().close();
            });
            gridPane.setVgap(10);
            gridPane.setHgap(10);
            gridPane.setPadding(new Insets(10));
            gridPane.add(standartLabel, 0, 0,1,1);
            gridPane.add(standartTextField,1,0,1,1);
            gridPane.add(qaydEtButton,1,1,1,1);
            GridPane.setHalignment(qaydEtButton, HPos.RIGHT);
            HBox.setHgrow(textField, Priority.ALWAYS);
            customWindow.getRightVBox().getChildren().add(gridPane);
            customWindow.showAndWait();
        }

        public Integer getAmal() {
            return amal;
        }

        public void setAmal(Integer amal) {
            this.amal = amal;
        }
    }
    private void Taftish(String oldValue, String newValue) {
        ObservableList<Standart> subentries = FXCollections.observableArrayList();

        newValue = newValue.toLowerCase();

        if ( oldValue != null && (newValue.length() < oldValue.length()) ) {
            tableView.setItems(standarts);
        }

        for ( Standart s: standarts ) {
            if (s.getText().toString().contains(newValue)) {
                subentries.add(s);
            }
        }
        tableView.setItems(subentries);
    }

    public Boolean getDoubleClick() {
        return doubleClick;
    }

    public void setDoubleClick(Boolean doubleClick) {
        this.doubleClick = doubleClick;
    }

    public Standart getDoubleClickedRow() {
        return doubleClickedRow;
    }

    public void setDoubleClickedRow(Standart doubleClickedRow) {
        this.doubleClickedRow = doubleClickedRow;
    }

    public Tugmachalar getTugmachalar() {
        return tugmachalar;
    }

    public TableView<Standart> getTableView() {
        return tableView;
    }
}
