package sample.Controller;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import sample.Config.MySqlDBGeneral;
import sample.Data.Hisob;
import sample.Data.Standart3;
import sample.Data.User;
import sample.Enums.ServerType;
import sample.Model.HisobModels;
import sample.Model.Standart3Models;
import sample.Tools.*;
import java.sql.Connection;

public class BizningDokonlar extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    VBox centerPane = new VBox();
    HBox hisobButtonsHBox;
    Integer padding = 5;

    Connection connection;
    User user;

    String rightTableName = "Dokonlar";

    TableView<Hisob> hisobTableView = new TableView<>();
    TableView<Standart3> rightTableView = new TableView<>();

    ObservableList<Standart3> rightObservableList = FXCollections.observableArrayList();
    ObservableList<Hisob> hisoblar = FXCollections.observableArrayList();
    ObservableList<Hisob> hisoblar2 = FXCollections.observableArrayList();
    ObservableList<Hisob> hisoblar3 = FXCollections.observableArrayList();

    Tugmachalar rightButtons;
    Button cancelButton;
    Button qaydEtButton;
    CustomGrid rightGrid;

    HisobModels hisobModels = new HisobModels();
    Standart3Models standart3Models = new Standart3Models();

    EventHandler<ActionEvent> rightCancelEvent;

    public BizningDokonlar() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        GetDbData.initData(connection);
        ibtido();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initStage(primaryStage);
        stage.show();
    }

    public BizningDokonlar(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        String classSimpleName = getClass().getSimpleName();
        DasturlarRoyxati.dastur(connection, user, classSimpleName);
        ibtido();
    }

    public void display() {
        stage = new Stage();
        initStage(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }



    public static void main(String[] args) {
        launch(args);
    }

    private void ibtido() {
        initData();
        initCenterPane();
        initBorderPane();
    }

    private void initData() {
        hisoblar = hisobModels.get_data(this.connection);
        for (Hisob h: hisoblar) {
            hisoblar2.add(h);
        }
    }

    private void initTopPane() {
    }

    private void initLeftPane() {
    }

    private void initCenterPane() {
        centerPane.setPadding(new Insets(padding));
        VBox.setVgrow(centerPane, Priority.ALWAYS);
        SetHVGrow.VerticalHorizontal(centerPane);
        rightButtons = initTugmachalar();
        hisobButtonsHBox = initHisobButtons();
        rightTableView = initTableView();
        VBox.setVgrow(rightTableView, Priority.ALWAYS);
        hisobTableView = initHisobTableView();
        centerPane.getChildren().addAll(rightButtons, rightTableView);
    }

    private VBox initHisobPane() {
        VBox vBox = new VBox();
        SetHVGrow.VerticalHorizontal(vBox);

        return vBox;
    }

    private HBox initHisobButtons() {
        HBox hBox = new HBox();
        HBox.setHgrow(hBox, Priority.ALWAYS);
        qaydEtButton = new Tugmachalar().getAdd();
        cancelButton = new Button("<<");
        qaydEtButton.setMaxWidth(2000);
        qaydEtButton.setPrefWidth(150);
        HBox.setHgrow(qaydEtButton, Priority.ALWAYS);
        cancelButton.setMaxWidth(2000);
        cancelButton.setPrefWidth(150);
        HBox.setHgrow(cancelButton, Priority.ALWAYS);
        EventHandler<ActionEvent> cancelEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                centerPane.getChildren().remove(0);
                centerPane.getChildren().remove(0);
                centerPane.getChildren().addAll(rightButtons, rightTableView);
            }
        };

        qaydEtButton.setOnAction(event -> {});
        cancelButton.setOnAction(cancelEvent);
        hBox.getChildren().addAll(cancelButton, qaydEtButton);
        return hBox;
    }

    private void initRightPane() {
    }

    private void initSplitPane() {
    }

    private void initBottomPane() {
    }

    private void initBorderPane() {
        borderpane.setCenter(centerPane);
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Bir panel");
        scene = new Scene(borderpane, 600, 400);
        stage.setScene(scene);
    }


    private Tugmachalar initTugmachalar() {
        Tugmachalar buttons = new Tugmachalar();
        Button add = buttons.getAdd();
        Button delete = buttons.getDelete();
        Button edit = buttons.getEdit();
        Button excel = buttons.getExcel();
        rightCancelEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                centerPane.getChildren().removeAll(hisobTableView, hisobButtonsHBox);
                centerPane.getChildren().addAll(rightButtons, rightTableView);
            }
        };

        add.setOnAction(event -> {
            if (hisoblar2.size()>0) {
                centerPane.getChildren().remove(0);
                centerPane.getChildren().remove(0);
                hisobButtonsHBox = initHisobButtons();
                centerPane.getChildren().addAll(hisobTableView, hisobButtonsHBox);
                qaydEtButton.setOnAction(event1 -> {
                    addRightList();
                    cancelButton.fire();
                });
            }
        });

        delete.setOnAction(event -> {
            deleteRightList();
        });

        edit.setOnAction(event -> {
        });
        buttons.getChildren().remove(excel);
        buttons.getChildren().remove(edit);
        return buttons;
    }

    private TableView<Standart3> initTableView() {
        TableView<Standart3> tableView = new TableView<>();
        tableView.getColumns().add(getHisobColumn());
        tableView.setItems(refreshTableData());
        SetHVGrow.VerticalHorizontal(tableView);
        return tableView;
    }

    private TableColumn<Standart3, Integer> getHisobColumn() {
        TableColumn<Standart3, Integer> tableColumn = new TableColumn<>("Hisob");
        tableColumn.setMinWidth(200);
        tableColumn.setCellValueFactory(new PropertyValueFactory<>("id3"));
        tableColumn.setCellFactory(column -> {
            TableCell<Standart3, Integer> cell = new TableCell<Standart3, Integer>() {

                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        for (Hisob h: hisoblar) {
                            if (h.getId().equals(item)) {
                                setText(h.getText());
                                break;
                            } else
                                setText("");
                        }
                    }
                }
            };
            return cell;
        });
        return tableColumn;
    }

    private ObservableList<Standart3> refreshTableData() {
        standart3Models.setTABLENAME("Dokonlar");
        ObservableList<Standart3> observableList = standart3Models.get_data(connection);
        if (observableList.size()>0) {
            for (Standart3 s3 : observableList) {
                Hisob hisob = getHisob(hisoblar2, s3.getId3());
                if (hisob != null) {
                    hisoblar2.remove(hisob);
                }
            }
        }
        return observableList;
    }

    public class CustomGrid extends GridPane {
        Label guruhLabel = new Label("Guruh");
        TextField guruhTextField = new TextField();
        Label hisobLabel = new Label("Hisob");
        TextField hisobTextField = new TextField();
        Button hisobButton = new Button();
        Button qaydEtButton = new Tugmachalar().getAdd();
        Button cancelButton = new Button("<<");
        Hisob hisob;

        HBox hisobHbox = new HBox();
        Integer rowIndex = 0;

        TableView<Hisob> hisobTableView;

        public CustomGrid() {
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
            GridPane.setHgrow(guruhTextField, Priority.ALWAYS);

            TextFields.bindAutoCompletion(hisobTextField, hisoblar2).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Hisob> autoCompletionEvent) -> {
                hisob = autoCompletionEvent.getCompletion();
                qaydEtButton.setDisable(false);
            });

            hisobButton.setOnAction(event -> {
                HisobController hisobController = new HisobController();
                    hisobController.display(connection, user, hisoblar2);
                    if (hisobController.getDoubleClick()) {
                        Hisob newValue = hisobController.getDoubleClickedRow();
                        if (newValue != null) {
                            ObservableList<Hisob> addedHisob = hisobController.getAddedHIsobList();
                            if (addedHisob.size()>0) {
                                for (Hisob h: addedHisob) {
                                    hisoblar.add(h);
//                                    hisoblar2.add(h);
                                }
                            }
                            hisobTextField.setText(newValue.getText());
                            hisob = newValue;
                            qaydEtButton.setDisable(false);
                        }
                    }

            });
        }

        public void showRight() {
            getChildren().removeAll(getChildren());
            rowIndex = 0;
            add(hisobLabel, 0, rowIndex, 1,1);
            add(hisobHbox, 1, rowIndex, 1, 1);
//            setHgap(10);
            setVgap(10);
            rowIndex ++;
            add(cancelButton,0, rowIndex, 1,1);
            add(qaydEtButton, 1,rowIndex,1,1);
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
            hisobTableView.setItems(hisoblar2);
            if (hisoblar2.size()>0) {
                qaydEtButton.setDisable(false);
            }
            hisobTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            rowIndex = 0;
            add(hisobTableView,0,rowIndex,2,1);
            setVgap(10);
            rowIndex ++;
            add(cancelButton,0, rowIndex, 1,1);
            add(qaydEtButton, 1,rowIndex,1,1);
        }

        public void addRight(ObservableList<Standart3> rightObservableList) {
            Standart3 standart3;
            if (hisob != null) {
                standart3 = new Standart3(null, 0, hisob.getId(), hisobTextField.getText(), hisob.getUserId(), hisob.getDateTime());
                standart3Models.setTABLENAME(rightTableName);
                standart3.setId(standart3Models.insert_data(connection, standart3));
                rightObservableList.add(standart3);
                rightTableView.getSelectionModel().select(standart3);
            }
        }


        public void deleteRight(ObservableList<Standart3> deletingObservableList, ObservableList<Standart3> rightObservableList) {
            if (deletingObservableList.size()>0) {
                for (Standart3 s3: deletingObservableList) {
                    Hisob hisob = getHisob(hisoblar, s3.getId3());
                    hisoblar2.add(hisob);
                    hisoblar3.remove(hisob);
                }
                standart3Models.deleteBatch(connection, deletingObservableList);
                rightObservableList.removeAll(deletingObservableList);
            }
        }

        public void editRight(ObservableList<Standart3> rightObservableList, Standart3 standart3) {
            if (hisob != null) {
                standart3.setId2(hisob.getId());
                standart3.setText(hisob.getText());
                standart3Models.setTABLENAME(rightTableName);
                standart3Models.update_data(connection, standart3);
            }
        }

        public void actionRight(int i, Standart3 standart3) {
            hisobTextField.setText("");
            if (standart3.getId3() != null) {
                for (Hisob h : hisoblar) {
                    if (h.getId() .equals(standart3.getId3())) {
                        hisobTextField.setText(h.getText());
                        break;
                    }
                }
            }
            switch (i) {
                case 1:
                    qaydEtButton.setText("Qo`sh");
                    qaydEtButton.setGraphic(new PathToImageView("/sample/images/Icons/add.png").getImageView());
                    hisobHbox.setDisable(false);
                    break;
                case 2:
                    qaydEtButton.setText("O`chir");
                    qaydEtButton.setGraphic(new PathToImageView("/sample/images/Icons/delete.png").getImageView());
                    hisobHbox.setDisable(true);
                    qaydEtButton.setDisable(false);
                    break;
                case 3:
                    qaydEtButton.setText("O`zgartir");
                    qaydEtButton.setGraphic(new PathToImageView("/sample/images/Icons/edit.png").getImageView());
                    hisobHbox.setDisable(false);
                    qaydEtButton.setDisable(false);
                    break;
            }
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
            if (h.getId() .equals(id)) {
                hisob = h;
                break;
            }
        }
        return hisob;
    }
    private TableView<Hisob> initHisobTableView() {
        TableViewAndoza tableViewAndoza = new TableViewAndoza();
        tableViewAndoza.initHisobTableView();
        TableView<Hisob> tableView = new TableView<>();
        TableColumn<Hisob, String> textColumn = tableViewAndoza.getHisobTextColumn();
        textColumn.setMinWidth(200);
        textColumn.setMaxWidth(200);
        tableView.getColumns().add(textColumn);
        tableView.setItems(hisoblar2);
        SetHVGrow.VerticalHorizontal(tableView);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        return tableView;
    }

    public void addRightList() {
        rightObservableList = rightTableView.getItems();
        ObservableList<Hisob> hisobObservableList = hisobTableView.getSelectionModel().getSelectedItems();
        ObservableList<Standart3> standart3ObservableList = FXCollections.observableArrayList();
        if (hisobObservableList.size()>0) {
            for (Hisob hisob: hisobObservableList) {
                Standart3 standart3 = new Standart3(
                        null,
                        0,
                        hisob.getId(),
                        hisob.getText(),
                        hisob.getUserId(),
                        hisob.getDateTime()
                );
                standart3ObservableList.add(standart3);
            }
            rightObservableList.addAll(standart3ObservableList);
            for (Standart3 s3: standart3ObservableList) {
                standart3Models.insert_data(connection, s3);
            }
            hisobTableView.getItems().removeAll(hisobObservableList);
            rightTableView.getSelectionModel().selectLast();
            rightTableView.refresh();
        }
    }

    public void deleteRightList() {
        Standart3 standart3 = rightTableView.getSelectionModel().getSelectedItem();
        if (standart3!=null) {
            Boolean ha = Alerts.haYoq("Diqqat !!!", standart3.getText() + " hisobi ro`yhatdan o`chiriladi. Rozimisiz?");
            if (ha) {
                Hisob hisob = GetDbData.getHisob(standart3.getId3());
                if (hisob != null) {
                    standart3Models.delete_data(connection, standart3);
                    rightTableView.getItems().remove(standart3);
                    hisobTableView.getItems().add(hisob);
                }
                rightTableView.refresh();
            }
        }
    }
}
