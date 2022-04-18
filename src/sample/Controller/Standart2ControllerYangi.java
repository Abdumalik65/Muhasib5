package sample.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import sample.Data.Hisob;
import sample.Data.Standart2;
import sample.Data.Standart3;
import sample.Data.User;
import sample.Model.HisobModels;
import sample.Model.Standart2Models;
import sample.Model.Standart3Models;
import sample.Tools.DasturlarRoyxati;
import sample.Tools.GetDbData;
import sample.Tools.PathToImageView;
import sample.Tools.Tugmachalar;

import java.sql.Connection;

public class Standart2ControllerYangi {
    Stage stage = new Stage();
    BorderPane borderpane = new BorderPane();
    MenuBar mainMenu;
    SplitPane centerPane = new SplitPane();
    VBox rightPane = new VBox();
    VBox leftPane = new VBox();
    Pane pane = new Pane();
    Label left_label = new Label("");
    Label right_label = new Label("");
    HBox bottom = new HBox();
    HBox buttonsPane = new HBox();
    DatePicker datePicker;
    Button button = new Tugmachalar().getExcel();

    Connection connection;
    User user = GetDbData.getUser(1);

    String leftTableName = "TranzitHisob";
    String rightTableName = "TranzitHisobGuruhi";
    String titleName = "";

    TableView<Standart2> leftTableView = new TableView<>();
    TableView<Standart3> rightTableView = new TableView<>();

    ObservableList<Standart2> leftObservableList = FXCollections.observableArrayList();
    ObservableList<Standart3> rightObservableList = FXCollections.observableArrayList();
    ObservableList<Hisob> hisoblar = FXCollections.observableArrayList();
    ObservableList<Hisob> hisoblar2 = FXCollections.observableArrayList();
    ObservableList<Hisob> hisoblar3 = FXCollections.observableArrayList();

    Tugmachalar leftButtons = new Tugmachalar();
    Tugmachalar rightButtons = new Tugmachalar();

    CustomGrid leftGrid;
    CustomGrid rightGrid;

    HisobModels hisobModels = new HisobModels();
    Standart2Models standart2Models = new Standart2Models();
    Standart3Models standart3Models = new Standart3Models();

    EventHandler<ActionEvent> leftCancelEvent, rightCancelEvent;

    public Standart2ControllerYangi(String leftTableName, String rightTableName, String titleName) {
        this.leftTableName = leftTableName;
        this.rightTableName = rightTableName;
        this.titleName = titleName;
    }

    public void start(Connection connection) {
        this.connection = connection;
        String classSimpleName = getClass().getSimpleName();
        DasturlarRoyxati.dastur(connection, user, classSimpleName);
        stage.setTitle(titleName);
        leftButtons.getChildren().remove(leftButtons.getEdit());
        rightButtons.getChildren().remove(rightButtons.getEdit());
//        stage.initModality(Modality.APPLICATION_MODAL);
        HBox.setHgrow(leftTableView, Priority.ALWAYS);
        VBox.setVgrow(leftTableView, Priority.ALWAYS);
        HBox.setHgrow(rightTableView, Priority.ALWAYS);
        VBox.setVgrow(rightTableView, Priority.ALWAYS);
        /*************************************************
         **                 Yuqori                      **
         *************************************************/
        mainMenu = new MenuBar();
        Menu menuFile = new Menu("File");
        Menu menuView = new Menu("View");
        Menu menuEdit = new Menu("Edit");
        Menu menuHelp = new Menu("Help");
        mainMenu.getMenus().addAll(menuFile, menuEdit, menuView, menuHelp);
        /*************************************************
         **                  Chap                       **
         *************************************************/
        hisoblar = hisobModels.get_data(this.connection);
        for (Hisob h: hisoblar) {
            hisoblar2.add(h);
        }
        standart2Models.setTABLENAME(leftTableName);
        leftObservableList = standart2Models.get_data(this.connection);
        for (Standart2 s2: leftObservableList) {
            Hisob hisob = getHisob(hisoblar2, s2.getId2());
            if (hisob != null) {
                hisoblar2.remove(hisob);
            }
        }
        TableColumn<Standart2, Integer> leftId = new TableColumn<>("Id");
        leftId.setMinWidth(10);
        leftId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Standart2, String> leftHisob = new TableColumn<>("Guruh nomi");
        leftHisob.setMinWidth(100);
        leftHisob.setCellValueFactory(new PropertyValueFactory<>("text"));

        TableColumn<Standart2, Integer> hisob1 = new TableColumn<>("Hisob nomi");
        hisob1.setMinWidth(100);
        hisob1.setCellValueFactory(new PropertyValueFactory<>("id2"));
        hisob1.setCellFactory(column -> {
            TableCell<Standart2, Integer> cell = new TableCell<Standart2, Integer>() {

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

        leftTableView.getColumns().addAll(leftHisob, hisob1);
        leftTableView.setItems(leftObservableList);

        leftTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                standart3Models.setTABLENAME(rightTableName);
                rightObservableList = standart3Models.getAnyData(this.connection, "id2 = " + newValue.getId2(), "");
                rightTableView.setItems(rightObservableList);
                rightTableView.refresh();
            }
        });
        rightButtons.setDisableAll();
        leftButtons.setDisableAll();
        if (leftObservableList.size()>0) {
            leftTableView.getSelectionModel().selectFirst();
            leftButtons.setEnableAll();
        } else {
            leftButtons.getAdd().setDisable(false);
        }

        leftGrid = new CustomGrid();
        leftPane.getChildren().addAll(leftButtons, leftTableView);

        leftCancelEvent = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                leftPane.getChildren().remove(leftGrid);
                leftPane.getChildren().addAll(leftButtons, leftTableView);
                rightPane.setDisable(false);
            }
        };

        leftButtons.getAdd().setOnAction(event -> {
            if (hisoblar2.size()>0) {
                leftPane.getChildren().removeAll(leftButtons, leftTableView);
                leftGrid.actionLeft(1, new Standart2());
                leftGrid.showLeft();
                rightPane.setDisable(true);
                leftPane.getChildren().add(leftGrid);

                leftGrid.getQaydEtButton().setOnAction(event1 -> {
                    leftGrid.addLeft(leftObservableList);
                    leftTableView.refresh();
                    leftGrid.cancelButton.fire();
                });

                leftGrid.cancelButton.setOnAction(leftCancelEvent);
            }
        });
        leftButtons.getDelete().setOnAction(event -> {
            Standart2 standart2 = leftTableView.getSelectionModel().getSelectedItem();
            if (standart2 != null) {
                leftPane.getChildren().removeAll(leftButtons, leftTableView);
                leftGrid.actionLeft(2, standart2);
                leftGrid.showLeft();
                rightPane.setDisable(true);
                leftPane.getChildren().add(leftGrid);
            }

            leftGrid.getQaydEtButton().setOnAction(event1 -> {
                if (standart2 != null) {
                    leftGrid.deleteLeft(leftObservableList, standart2);
                    leftTableView.refresh();
                    leftGrid.cancelButton.fire();
                }
            });

            leftGrid.cancelButton.setOnAction(leftCancelEvent);

        });
        leftButtons.getEdit().setOnAction(event -> {
            Standart2 standart2 = leftTableView.getSelectionModel().getSelectedItem();
            if (standart2 != null) {
                leftPane.getChildren().removeAll(leftButtons, leftTableView);
                leftGrid.actionLeft(3, standart2);
                leftGrid.showLeft();
                leftPane.getChildren().add(leftGrid);
            }

            leftGrid.getQaydEtButton().setOnAction(event1 -> {
                if (standart2 != null) {
                    leftGrid.editLeft(leftObservableList, standart2);
                    leftTableView.refresh();
                    leftGrid.cancelButton.fire();
                }
            });

            leftGrid.cancelButton.setOnAction(leftCancelEvent);

        });
        leftPane.setPadding(new Insets(5));

        /*************************************************
         **                  O`ng                       **
         *************************************************/
        TableColumn<Standart3, Integer> rightHisob = new TableColumn<>("Hisob");
        rightHisob.setMinWidth(200);
        rightHisob.setCellValueFactory(new PropertyValueFactory<>("id3"));
        rightHisob.setCellFactory(column -> {
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


        rightTableView.getColumns().add(rightHisob);
        standart3Models.setTABLENAME(rightTableName);
        rightObservableList = standart3Models.get_data(this.connection);
        if (rightObservableList.size()>0) {
            for (Standart3 s3 : rightObservableList) {
                Hisob hisob = getHisob(hisoblar2, s3.getId3());
                if (hisob != null) {
                    hisoblar2.remove(hisob);
                }
            }
        }
        if (leftObservableList.size()>0) {
            rightObservableList = standart3Models.getAnyData(this.connection, "id2 = " + leftObservableList.get(0).getId2(), "");
        }
        rightTableView.setItems(rightObservableList);
        if (rightObservableList.size()>0) {
            rightTableView.getSelectionModel().selectFirst();
            rightButtons.setEnableAll();
        } else {
            rightButtons.getAdd().setDisable(false);
            rightButtons.getDelete().setDisable(true);
            rightButtons.getEdit().setDisable(true);
        }
        rightPane.setPadding(new Insets(5));
        rightGrid = new CustomGrid();
        rightTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        rightPane.getChildren().addAll(rightButtons, rightTableView);

        rightCancelEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                leftPane.setDisable(false);
                rightPane.getChildren().remove(rightGrid);
                rightPane.getChildren().addAll(rightButtons, rightTableView);
            }
        };

        rightButtons.getAdd().setOnAction(event -> {
            if (hisoblar2.size()>0) {
                rightPane.getChildren().removeAll(rightButtons, rightTableView);
                rightGrid.actionRight(1, new Standart3());
                rightGrid.showRightTable();
                rightPane.getChildren().add(rightGrid);
                leftPane.setDisable(true);

                rightGrid.getQaydEtButton().setOnAction(event1 -> {
                    rightGrid.addRightList(rightObservableList);
                    rightTableView.getSelectionModel().selectLast();
                    rightTableView.refresh();
                    rightGrid.cancelButton.fire();
                });

                rightGrid.cancelButton.setOnAction(rightCancelEvent);
            }
        });

        rightButtons.getDelete().setOnAction(event -> {
            ObservableList<Standart3> deletingObservableList = rightTableView.getSelectionModel().getSelectedItems();
            rightObservableList = rightTableView.getItems();
            if (deletingObservableList.size() > 0) {
                rightPane.getChildren().removeAll(rightButtons, rightTableView);
                rightGrid.actionRight(2, new Standart3());
                rightGrid.showRightTableDelete();
                rightPane.getChildren().add(rightGrid);
                leftPane.setDisable(true);

                rightGrid.getQaydEtButton().setOnAction(event1 -> {
                    rightGrid.deleteRight(deletingObservableList, rightObservableList);
                    rightTableView.refresh();
                    rightGrid.cancelButton.fire();
                });

                rightGrid.cancelButton.setOnAction(rightCancelEvent);
            }
        });

        rightButtons.getEdit().setOnAction(event -> {
            Standart3 standart3 = rightTableView.getSelectionModel().getSelectedItem();
            if (standart3 != null) {
                rightPane.getChildren().removeAll(rightButtons, rightTableView);
                rightGrid.actionRight(3, standart3);
                rightGrid.showRight();
                rightPane.getChildren().add(rightGrid);
                leftPane.setDisable(true);

                rightGrid.getQaydEtButton().setOnAction(event1 -> {
                    rightGrid.editRight(rightObservableList, standart3);
                    rightTableView.refresh();
                    rightGrid.cancelButton.fire();
                });

                rightGrid.cancelButton.setOnAction(rightCancelEvent);
            }
        });
        /*************************************************
         **                   Past                      **
         *************************************************/
        /*************************************************
         **                Umumiy kod                   **
         *************************************************/
        left_label.setPadding(new Insets(3));
        right_label.setPadding(new Insets(3));
        HBox.setHgrow(leftPane, Priority.ALWAYS);
        VBox.setVgrow(leftPane, Priority.ALWAYS);
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        VBox.setVgrow(rightPane, Priority.ALWAYS);
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        VBox.setVgrow(centerPane, Priority.ALWAYS);
        HBox.setHgrow(left_label, Priority.ALWAYS);
        HBox.setHgrow(pane, Priority.ALWAYS);
        HBox.setHgrow(right_label, Priority.NEVER);
        centerPane.getItems().addAll(leftPane, rightPane);
        bottom.getChildren().addAll(left_label, pane, right_label);
        bottom.setAlignment(Pos.CENTER);
        borderpane.setCenter(centerPane);
        borderpane.setBottom(bottom);
        Scene scene = new Scene(borderpane, 600, 400);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
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
            if (hisoblar2.size() == 0) {
                leftButtons.getAdd().setDisable(true);
                rightButtons.getAdd().setDisable(true);
            } else {
                leftButtons.getAdd().setDisable(false);
                rightButtons.getAdd().setDisable(false);
            }

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

        public void showLeft() {
            getChildren().removeAll(getChildren());
            if (hisoblar2.size() == 0) {
                leftButtons.getAdd().setDisable(true);
                rightButtons.getAdd().setDisable(true);
            } else {
                leftButtons.getAdd().setDisable(false);
                rightButtons.getAdd().setDisable(false);
            }
            rowIndex = 0;
            add(guruhLabel, 0,rowIndex,1,1);
            add(guruhTextField, 1, rowIndex,1,1);
            rowIndex++;

            add(hisobLabel, 0, rowIndex, 1,1);
            add(hisobHbox, 1, rowIndex, 1, 1);
//            setHgap(10);
            setVgap(10);
            rowIndex ++;
            add(cancelButton,0, rowIndex, 1,1);
            add(qaydEtButton, 1,rowIndex,1,1);
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
            if (hisoblar2.size() == 0) {
                leftButtons.getAdd().setDisable(true);
                rightButtons.getAdd().setDisable(true);
            } else {
                leftButtons.getAdd().setDisable(false);
                rightButtons.getAdd().setDisable(false);
            }
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
            hisoblar3 = FXCollections.observableArrayList();
            getChildren().removeAll(getChildren());
            hisobTableView.setDisable(true);
            TableColumn<Hisob, String> hisobColumn = new TableColumn<>("Hisob");
            hisobColumn.setMinWidth(200);
            hisobColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
            hisobTableView.getColumns().add(hisobColumn);
            ObservableList<Standart3> standart3ObservableList = FXCollections.observableArrayList();
            standart3ObservableList = rightTableView.getSelectionModel().getSelectedItems();
            if (standart3ObservableList.size()>0) {
                for (Standart3 s3: standart3ObservableList) {
                    hisoblar3.add(getHisob(hisoblar, s3.getId3()));
                }
                hisobTableView.setItems(hisoblar3);
            }
            hisobTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            if (hisoblar2.size() == 0) {
                leftButtons.getAdd().setDisable(true);
                rightButtons.getAdd().setDisable(true);
            } else {
                leftButtons.getAdd().setDisable(false);
                rightButtons.getAdd().setDisable(false);
            }
            rowIndex = 0;
            add(hisobTableView,0,rowIndex,2,1);
            setVgap(10);
            rowIndex ++;
            add(cancelButton,0, rowIndex, 1,1);
            add(qaydEtButton, 1,rowIndex,1,1);
        }

        public void addLeft(ObservableList<Standart2> leftObservableList) {
            Standart2 standart2;
            if (hisob != null) {
                standart2 = new Standart2(null, hisob.getId(), guruhTextField.getText(), hisob.getUserId(), hisob.getDateTime());
                standart2Models.setTABLENAME(leftTableName);
                standart2.setId(standart2Models.insert_data(connection, standart2));
                leftObservableList.add(standart2);
                leftTableView.getSelectionModel().select(standart2);
                hisob = getHisob(hisoblar2, standart2.getId2());
                hisoblar2.remove(hisob);
                hisoblar3.add(hisob);
                if (hisoblar2.size() == 0) {
                    leftButtons.getAdd().setDisable(true);
                    rightButtons.getAdd().setDisable(true);
                } else {
                    leftButtons.getAdd().setDisable(false);
                    rightButtons.getAdd().setDisable(false);
                }
                leftButtons.setEnableAll();
                rightButtons.getAdd().setDisable(false);
            }
        }

        public void deleteLeft(ObservableList<Standart2> leftObservableList, Standart2 standart2) {
            standart2Models.delete_data(connection, standart2);
            hisob = getHisob(hisoblar, standart2.getId2());
            hisoblar2.add(hisob);
            hisoblar3.remove(hisob);
            if (hisoblar2.size() == 0) {
                leftButtons.getAdd().setDisable(true);
                rightButtons.getAdd().setDisable(true);
            } else {
                leftButtons.getAdd().setDisable(false);
                rightButtons.getAdd().setDisable(false);
            }
            if (leftObservableList.size() == 0) {
                leftButtons.getDelete().setDisable(true);
                leftButtons.getEdit().setDisable(true);
                rightButtons.setDisableAll();
            }
//            rightTableView.getSelectionModel().selectAll();
//            rightObservableList = rightTableView.getSelectionModel().getSelectedItems();
            if (rightObservableList.size()>0) {
                deleteRight(rightObservableList, rightObservableList);
            }
            leftObservableList.remove(standart2);
            leftTableView.refresh();
            rightTableView.refresh();
        }

        public void editLeft(ObservableList<Standart2> leftObservableList, Standart2 standart2) {
            if (hisob != null) {
                standart2.setId2(hisob.getId());
                standart2.setText(guruhTextField.getText());
                standart2Models.setTABLENAME(leftTableName);
                standart2Models.update_data(connection, standart2);
            }
        }

        public void actionLeft(Integer i, Standart2 standart2) {
            guruhTextField.setText("");
            hisobTextField.setText("");
            guruhTextField.setText(standart2.getText());
            if (standart2.getId2() != null) {
                for (Hisob h : hisoblar) {
                    if (h.getId() .equals(standart2.getId2())) {
                        hisobTextField.setText(h.getText());
                        break;
                    }
                }
            }
            switch (i) {
                case 1:
                    qaydEtButton.setText("Qo`sh");
                    qaydEtButton.setGraphic(new PathToImageView("/sample/images/Icons/add.png").getImageView());
                    guruhTextField.setDisable(false);
                    hisobHbox.setDisable(false);
                    break;
                case 2:
                    qaydEtButton.setText("O`chir");
                    qaydEtButton.setGraphic(new PathToImageView("/sample/images/Icons/delete.png").getImageView());
                    guruhTextField.setDisable(true);
                    hisobHbox.setDisable(true);
                    qaydEtButton.setDisable(false);
                    break;
                case 3:
                    qaydEtButton.setText("O`zgartir");
                    qaydEtButton.setGraphic(new PathToImageView("/sample/images/Icons/edit.png").getImageView());
                    guruhTextField.setDisable(false);
                    hisobHbox.setDisable(false);
                    qaydEtButton.setDisable(false);
                    break;
            }
        }

        public void addRight(ObservableList<Standart3> rightObservableList) {
            Standart3 standart3;
            if (hisob != null) {
                standart3 = new Standart3(null, leftTableView.getSelectionModel().getSelectedItem().getId2(), hisob.getId(), hisobTextField.getText(), hisob.getUserId(), hisob.getDateTime());
                standart3Models.setTABLENAME(rightTableName);
                standart3.setId(standart3Models.insert_data(connection, standart3));
                rightObservableList.add(standart3);
                rightTableView.getSelectionModel().select(standart3);
                rightButtons.setEnableAll();
            }
        }

        public void addRightList(ObservableList<Standart3> rightObservableList) {
            ObservableList<Hisob> hisobObservableList = hisobTableView.getSelectionModel().getSelectedItems();
            ObservableList<Standart3> standart3ObservableList = FXCollections.observableArrayList();
            if (hisobObservableList.size()>0) {
                Integer leftId =  leftTableView.getSelectionModel().getSelectedItem().getId2();
                for (Hisob hisob: hisobObservableList) {
                    standart3ObservableList.add(new Standart3(
                            null,
                            leftId,
                            hisob.getId(),
                            hisob.getText(),
                            hisob.getUserId(),
                            hisob.getDateTime()
                    ));
                }
                rightObservableList.addAll(standart3ObservableList);
                for (Standart3 s3: standart3ObservableList) {
                    s3.setId(standart3Models.insert_data(connection, s3));
                }
                hisoblar2.removeAll(hisobObservableList);
                hisoblar3.addAll(hisobObservableList);
                rightButtons.setEnableAll();
                if (hisoblar2.size() == 0) {
                    leftButtons.getAdd().setDisable(true);
                    rightButtons.getAdd().setDisable(true);
                } else {
                    leftButtons.getAdd().setDisable(false);
                    rightButtons.getAdd().setDisable(false);
                }
            }
        }

        public void deleteRight(ObservableList<Standart3> deletingObservableList, ObservableList<Standart3> rightObservableList) {
            if (deletingObservableList.size()>0) {
                for (Standart3 s3: deletingObservableList) {
                    Hisob hisob = getHisob(hisoblar, s3.getId3());
                    hisoblar2.add(hisob);
                    hisoblar3.remove(hisob);
                }
                if (hisoblar2.size() == 0) {
                    leftButtons.getAdd().setDisable(true);
                    rightButtons.getAdd().setDisable(true);
                } else {
//                    Collections.sort(hisoblar2, Comparator.comparingInt(Hisob::getId));
                    leftButtons.getAdd().setDisable(false);
                    rightButtons.getAdd().setDisable(false);
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
}
