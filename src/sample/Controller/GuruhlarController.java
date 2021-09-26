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
import javafx.scene.layout.VBox;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import sample.Config.MySqlDBLocal;
import sample.Tools.CustomWindow;
import sample.Tools.PathToImageView;
import sample.Tools.Tugmachalar;
import sample.Data.*;
import sample.Model.HisobModels;
import sample.Model.Standart2Models;
import sample.Model.StandartModels;

import java.sql.Connection;
import java.util.Date;

public class GuruhlarController {
    Tugmachalar leftButtons = new Tugmachalar();
    Tugmachalar rightButtons = new Tugmachalar();
    HisobModels hisobModels = new HisobModels();
    StandartModels standartModels = new StandartModels();
    Standart2Models standart2Models = new Standart2Models();
    CustomWindow customWindow = new CustomWindow(true, 600,400);
    ObservableList<Hisob> hisoblar = FXCollections.observableArrayList();
    ObservableList<Standart> leftObservableList;
    ObservableList<Standart2> rightObservableList;
    TableView<Standart> leftTableView = new TableView();
    TableView<Standart2> rightTableView = new TableView();
    String tableName;
    Connection connection;
    User user;
    int padding = 3;

    public GuruhlarController() {
        connection = new MySqlDBLocal().getDbConnection();
    }

    public GuruhlarController(Connection connection, User user, String tableName) {
        this.connection = connection;
        this.user = user;
        this.tableName = tableName;
        init();
    }

    private void init() {
        initData();
        initLeftTable();
        initRightTable();
        leftMethods();
        rightMethods();
    }

    public void show() {
        customWindow.showAndWait();
    }

    public void leftMethods() {
        leftButtons.getAdd().setOnAction(event -> {
            LeftGridPane leftGridPane = new LeftGridPane(connection, user, 1);
            leftGridPane.showAndWait();
        });
        leftButtons.getEdit().setOnAction(event -> {
            Standart standart = leftTableView.getSelectionModel().getSelectedItem();
            if (standart != null) {
                LeftGridPane leftGridPane = new LeftGridPane(connection, user, standart, 2);
                leftGridPane.showAndWait();
            }
        });
        leftButtons.getDelete().setOnAction(event -> {
            Standart standart = leftTableView.getSelectionModel().getSelectedItem();
            if (standart != null) {
                LeftGridPane leftGridPane = new LeftGridPane(connection, user, standart, 3);
                leftGridPane.showAndWait();
            }
        });
    }

    private void rightMethods() {
        rightButtons.getAdd().setOnAction(event -> {
        });
        rightButtons.getEdit().setOnAction(event -> {
        });
        rightButtons.getDelete().setOnAction(event -> {
        });
    }

    public void initStage() {
        customWindow.getStage().getIcons().add(new Image("/sample/images/Icons/WindowsTable.png"));
        customWindow.setStageTitle(tableName);
    }

    public void initData() {
        hisoblar = hisobModels.get_data(connection);
        tableName = "HisobGuruhlari";
        standartModels.setTABLENAME(tableName);
        leftObservableList = standartModels.get_data(connection);
        tableName = "HisobGuruhiJadval";
        standart2Models.setTABLENAME(tableName);
        rightObservableList = standart2Models.get_data(connection);
    }

    public void initLeftTable(){
        tableName = "HisobGuruhlari";
        TableColumn<Standart, Integer> id = new TableColumn<>("N");
        id.setMinWidth(15);
        id.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Standart, String> text = new TableColumn<>(tableName);
        text.setMinWidth(200);
        text.setCellValueFactory(new PropertyValueFactory<>("text"));
        customWindow.getLeftVBox().getChildren().addAll(leftButtons, leftTableView);
        leftTableView.getColumns().addAll(id, text);
        leftTableView.setItems(leftObservableList);
        leftTableView.setPadding(new Insets(padding));
        HBox.setHgrow(leftTableView, Priority.ALWAYS);
        VBox.setVgrow(leftTableView, Priority.ALWAYS);
        leftButtons.setPadding(new Insets(padding));
    }

    public void initRightTable(){
        tableName = "HisobGuruhiJadval";
        TableColumn<Standart2, String> text = new TableColumn<>(tableName);
        text.setMinWidth(200);
        text.setCellValueFactory(new PropertyValueFactory<>("text"));
        customWindow.getRightVBox().getChildren().addAll(rightButtons, rightTableView);
        rightTableView.getColumns().add(text);
        rightTableView.setItems(rightObservableList);
        rightTableView.setPadding(new Insets(padding));
        HBox.setHgrow(rightTableView, Priority.ALWAYS);
        VBox.setVgrow(rightTableView, Priority.ALWAYS);
        rightButtons.setPadding(new Insets(padding));
    }

    class LeftGridPane extends GridPane {
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
        Button qaydEtButton = new Button("Qayd et");
        Label standartLabel = new Label(tableName);
        TextField standartTextField = new TextField();

        public LeftGridPane(Connection connection, User user, Integer amal) {
            this.connection = connection;
            this.user = user;
            this.amal = amal;
            customWindow.setStageTitle("Qo`sh");
        }

        public LeftGridPane(Connection connection, User user, Standart standart, Integer amal) {
            this.connection = connection;
            this.user = user;
            this.standart = standart;
            this.amal = amal;
            standartTextField.setText(standart.getText());
            switch (amal) {
                case 2:
                    customWindow.setStageTitle("O`zgartir");
                    break;
                case 3:
                    customWindow.setStageTitle("O`chir");
                    break;
            }
        }

        public void showAndWait() {
            qaydEtButton.setOnAction(event -> {
                switch (amal) {
                    case 1:
                        standart.setText(standartTextField.getText());
                        standart.setUserId(user.getId());
                        standart.setId(standartModels.insert_data(connection, standart));
                        leftObservableList.add(standart);
                        break;
                    case 2:
                        standart.setText(standartTextField.getText());
                        standart.setUserId(user.getId());
                        standartModels.update_data(connection, standart);
                        break;
                    case 3:
                        standartModels.delete_data(connection, standart);
                        leftObservableList.remove(standart);
                        break;
                }
                leftTableView.refresh();
                customWindow.getStage().close();
            });
            setVgap(10);
            setHgap(10);
            setPadding(new Insets(padding));
            add(standartLabel, 0, 0,1,1);
            add(standartTextField,1,0,1,1);
            add(qaydEtButton,1,1,1,1);
            GridPane.setHalignment(qaydEtButton, HPos.RIGHT);
            customWindow.getRightVBox().getChildren().add(this);
            customWindow.showAndWait();
        }

        public Integer getAmal() {
            return amal;
        }

        public void setAmal(Integer amal) {
            this.amal = amal;
        }
    }

    public class CustomGrid extends GridPane {
        Label hisobLabel = new Label("Hisob");
        TextField hisobTextField = new TextField();
        Button hisobButton = new Button();
        Button qaydEtButton = new Tugmachalar().getAdd();
        Button cancelButton = new Button("<<");
        Hisob hisob;

        HBox hisobHbox = new HBox();
        Integer rowIndex = 0;

        TableView<Hisob> hisobTableView;

        public CustomGrid(){
            hisobButton.setGraphic( new PathToImageView("/sample/images/Icons/add.png").getImageView());
            qaydEtButton.setMaxWidth(2000);
            qaydEtButton.setPrefWidth(150);
            qaydEtButton.setDisable(true);

            cancelButton.setMaxWidth(2000);
            cancelButton.setPrefWidth(150);

            hisobHbox.getChildren().addAll(hisobTextField, hisobButton);
            HBox.setHgrow(hisobTextField, Priority.ALWAYS);
            GridPane.setHgrow(hisobHbox, Priority.ALWAYS);
            GridPane.setHgrow(qaydEtButton, Priority.ALWAYS);
            GridPane.setHgrow(cancelButton, Priority.ALWAYS);

            TextFields.bindAutoCompletion(hisobTextField, hisoblar).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Hisob> autoCompletionEvent) -> {
                hisob = autoCompletionEvent.getCompletion();
                qaydEtButton.setDisable(false);
            });

            hisobButton.setOnAction(event -> {
                HisobController hisobController = new HisobController();
                hisobController.display(connection, user, hisoblar);
                if (hisobController.getDoubleClick()) {
                    Hisob newValue = hisobController.getDoubleClickedRow();
                    if (newValue != null) {
                        ObservableList<Hisob> addedHisob = hisobController.getAddedHIsobList();
                        ObservableList<Standart2> st2 = FXCollections.observableArrayList();
                        if (addedHisob.size()>0) {
                            for (Hisob h: addedHisob) {
                                Standart2 s = new Standart2(null, h.getId(), h.getText(), user.getId(), new Date());
                                st2.add(s);
                            }
                        }
                        hisobTextField.setText(newValue.getText());
                        hisob = newValue;
                        qaydEtButton.setDisable(false);
                    }
                }

            });
        }

        public void  showRightTable() {
            hisobTableView = new TableView<>();
            HBox.setHgrow(hisobTableView, Priority.ALWAYS);
            VBox.setVgrow(hisobTableView, Priority.ALWAYS);
            getChildren().removeAll(getChildren());
            hisobTableView.setDisable(false);
            TableColumn<Hisob, String> hisobColumn = new TableColumn<>("Hisob");
            hisobColumn.setMinWidth(200);
            hisobColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
            hisobTableView.getColumns().add(hisobColumn);
            hisobTableView.setItems(hisoblar);
            hisobTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            rowIndex = 0;
            add(hisobTableView,0,rowIndex,2,1);
            setVgap(10);
            rowIndex ++;
            add(cancelButton,0, rowIndex, 1,1);
            add(qaydEtButton, 1,rowIndex,1,1);
        }

        public void  showRightTableDelete() {
            hisobTableView = new TableView<>();
            HBox.setHgrow(hisobTableView, Priority.ALWAYS);
            VBox.setVgrow(hisobTableView, Priority.ALWAYS);
            getChildren().removeAll(getChildren());
            hisobTableView.setDisable(true);
            TableColumn<Hisob, String> hisobColumn = new TableColumn<>("Hisob");
            hisobColumn.setMinWidth(200);
            hisobColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
            hisobTableView.getColumns().add(hisobColumn);
            ObservableList<Standart3> standart3ObservableList = FXCollections.observableArrayList();
            rightObservableList = rightTableView.getSelectionModel().getSelectedItems();
            hisobTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            rowIndex = 0;
            add(hisobTableView,0,rowIndex,2,1);
            setVgap(10);
            rowIndex ++;
            add(cancelButton,0, rowIndex, 1,1);
            add(qaydEtButton, 1,rowIndex,1,1);
        }

        public void addLeft(ObservableList<Standart2> leftObservableList) {
        }

        public void deleteLeft(ObservableList<Standart2> leftObservableList, Standart2 standart2) {
        }

        public void editLeft(ObservableList<Standart2> leftObservableList, Standart2 standart2) {
        }

        public void actionLeft(Integer i, Standart2 standart2) {
        }

        public void addRight(ObservableList<Standart3> rightObservableList) {
        }

        public void addRightList(ObservableList<Standart3> rightObservableList) {
        }

        public void deleteRight(ObservableList<Standart3> deletingObservableList, ObservableList<Standart3> rightObservableList) {
        }

        public void editRight(ObservableList<Standart3> rightObservableList, Standart3 standart3) {
        }

        public void actionRight(int i, Standart3 standart3) {
        }

        public Button getQaydEtButton() {
            return qaydEtButton;
        }

        public void setQaydEtButton(Button qaydEtButton) {
            this.qaydEtButton = qaydEtButton;
        }

        public Button getCancelButton() {
            return cancelButton;
        }

        public void setCancelButton(Button cancelButton) {
            this.cancelButton = cancelButton;
        }

        public TableView<Hisob> getHisobTableView() {
            return hisobTableView;
        }

    }

    public Hisob getHisob(ObservableList<Hisob> hisobs, Integer id) {
        Hisob hisob = null;
        for (Hisob h: hisobs) {
            if (h.getId().equals(id)) {
                hisob = h;
                break;
            }
        }
        return hisob;
    }
}
