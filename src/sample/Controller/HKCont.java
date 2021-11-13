package sample.Controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import sample.Data.*;
import sample.Model.HisobKitobModels;
import sample.Model.QaydnomaModel;
import sample.Model.StandartModels;
import sample.Tools.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class HKCont {
    Stage stage = new Stage();
    Scene scene;
    BorderPane borderpane = new BorderPane();
    SplitPane centerPane = new SplitPane();
    HBox bottom = new HBox();

    Connection connection;
    User user;
    Integer amalTuri;

    VBox rightPane = new VBox();
    Tugmachalar rightPaneButtons = new Tugmachalar();
    TableView<HisobKitob> rightPaneTableView = new TableView<>();
    HisobKitobModels hisobKitobModels = new HisobKitobModels();
    ObservableList<HisobKitob> hisobKitobObservableList = FXCollections.observableArrayList();
    HisobKitob hisobKitob = new HisobKitob();

    QaydnomaJami qaydnomaJami;
    TextArea qaydnomaIzohTextArea = new TextArea();
    RightGridPane rightGridPane;
    TableViewMaster tableViewMaster;

    VBox leftPane = new VBox();
    Tugmachalar leftPaneButtons = new Tugmachalar();
    TextField textField = new TextField();
    ComboBox<Standart> comboBox;
    ObservableList<Standart> comboData = FXCollections.observableArrayList();
    TableView<QaydnomaData> leftPaneTableView = new TableView<>();
    QaydnomaModel qaydnomaModel = new QaydnomaModel();
    ObservableList<QaydnomaData> qaydnomaDataObservableList = FXCollections.observableArrayList();
    QaydnomaData qaydnomaData = new QaydnomaData();
    GridPane leftPaneGridPane = new GridPane();
    LeftGridPane leftGridPane;
    Button leftCancelButton;

    EventHandler<ActionEvent> leftCancelEvent;
    EventHandler<ActionEvent> rightCancelEvent;

    StringBuffer stringBuffer = new StringBuffer();
    ObservableList<Standart> tovarObservableList;
    ObservableList<Valuta> valutaObservableList;
    ObservableList<Standart> jumlaObservableList;
    ObservableList<Standart> birlikObservableList;
    ObservableList<Standart> chiqimShakliObservableList;
    ObservableList<Standart> statusObservableList = FXCollections.observableArrayList();

    public HKCont(Connection connection, User user, Integer amalTuri) {
        this.connection = connection;
        this.user = user;
        this.amalTuri = amalTuri;
//        initData(connection);
        switch (amalTuri) {
            case 1:
                stage.setTitle("Pul harakatlari");
                stage.getIcons().add(new Image("/sample/images/Icons/WindowsTable.png"));
                break;
            case 2:
                stage.setTitle("Tovar xaridi");
                stage.getIcons().add(new Image("/sample/images/Icons/shopping_cart.png"));
                break;
            case 3:
                stage.setTitle("Tovar naqliyoti");
                stage.getIcons().add(new Image("/sample/images/Icons/data_transport.png"));
                break;
            case 4:
                break;
            case 15:
                stage.setTitle("Pul konvertatsiyasi naqliyoti");
                stage.getIcons().add(new Image("/sample/images/Icons/data_transport.png"));
                break;
        }
        qaydnomaDataObservableList = qaydnomaModel.getAnyData(connection,"amalTuri = " + amalTuri, "sana desc");
        qaydnomaJami = new QaydnomaJami(connection);
//        qaydnomaJami.refresh(amalTuri, qaydnomaDataObservableList);
        leftPaneTableView.setItems(qaydnomaDataObservableList);
        comboData.add(new Standart(1, "Hujjat №", null, null));
        comboData.add(new Standart(2, "Sana", null, null));
        comboData.add(new Standart(3, "To`lovchi", null, null));
        comboData.add(new Standart(4, "Oluvchi", null, null));
        comboData.add(new Standart(5, "Izoh", null, null));
        comboBox = new ComboBox<Standart>(FXCollections.observableArrayList(comboData));
        comboBox.getSelectionModel().selectFirst();
        leftPaneButtons.getChildren().addAll(comboBox, textField);
        leftPaneButtons.setDisableAll();
        rightPaneButtons.setDisableAll();
        if (qaydnomaDataObservableList.size()>0) {
            leftPaneTableView.getSelectionModel().selectFirst();
            qaydnomaData = qaydnomaDataObservableList.get(0);
            qaydnomaIzohTextArea.setText(qaydnomaDataObservableList.get(0).getIzoh());
            leftPaneButtons.setEnableAll();
            rightPaneButtons.getAdd().setDisable(false);
            hisobKitobObservableList.addAll(hisobKitobModels.getAnyData(connection, "qaydId = " + qaydnomaData.getId() + " AND amal = " + amalTuri, ""));
            for (HisobKitob hk: hisobKitobObservableList) {
                int ishora = qaydnomaData.getChiqimId().equals(hk.getHisob1()) ? 1 : -1;
                hk.setNarh(ishora*hk.getNarh());
            }
        } else {
            leftPaneButtons.getAdd().setDisable(false);
        }
        leftGridPane = new LeftGridPane(connection, user);
        leftCancelButton = leftGridPane.getCancelButton();
        tableViewMaster = new TableViewMaster(connection, user, amalTuri, qaydnomaJami, leftPaneTableView);
        rightPaneTableView = tableViewMaster.getTableView();
        rightPaneTableView.setItems(hisobKitobObservableList);
        rightPaneButtons.setDisableAll();
    }

    public void start() throws ClassNotFoundException, SQLException, ParseException, IOException {
        statusObservableList.add(new Standart(0, "Ochiq", user.getId(), null));
        statusObservableList.add(new Standart(1, "Yopiq", user.getId(), null));

        /*************************************************
         **                  Chap                       **
         *************************************************/
        TableColumn<QaydnomaData, Integer> hisobKitobRaqami = new TableColumn<>();
        hisobKitobRaqami.setText(" №");
        hisobKitobRaqami.setMinWidth(30);
        hisobKitobRaqami.setCellValueFactory(new PropertyValueFactory<>("hujjat"));

        TableColumn<QaydnomaData, Date> hisobKitobSanasi = new TableColumn<>();
        hisobKitobSanasi.setText("Sana");
        hisobKitobSanasi.setMinWidth(120);
        hisobKitobSanasi.setCellValueFactory(new PropertyValueFactory<>("sana"));
        hisobKitobSanasi.setCellFactory(column -> {
            TableCell<QaydnomaData, Date> cell = new TableCell<QaydnomaData, Date>() {
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

            return cell;
        });

        TableColumn<QaydnomaData, Integer> oluvchi = new TableColumn<>();
        oluvchi.setText("Oluvchi");
        oluvchi.setMinWidth(170);
        oluvchi.setCellValueFactory(new PropertyValueFactory<>("kirimNomi"));

        TableColumn<QaydnomaData, Integer> beruvchi = new TableColumn<>();
        beruvchi.setText("To`lovchi");
        beruvchi.setMinWidth(170);
        beruvchi.setCellValueFactory(new PropertyValueFactory<>("chiqimNomi"));

        TableColumn<QaydnomaData, Double> jami = new TableColumn<>("Jami");
        jami.setMinWidth(150);
        jami.setCellValueFactory(new PropertyValueFactory<>("jami"));
        jami.setCellFactory(column -> {
            TableCell<QaydnomaData, Double> cell = new TableCell<QaydnomaData, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    DecimalFormat decimalFormat = new MoneyShow();
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        setText(decimalFormat.format(item));
                    }
                }
            };
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        leftPaneTableView.getColumns().addAll(hisobKitobRaqami, hisobKitobSanasi, beruvchi, oluvchi, jami, getStatusColumn());
        leftPaneTableView.setEditable(true);

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            Taftish(oldValue, newValue,comboBox.getSelectionModel().getSelectedIndex());
        });
        leftPaneTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                qaydnomaData = newValue;
                qaydnomaIzohTextArea.setText(newValue.getIzoh());
                try {
                    hisobKitobObservableList.removeAll(hisobKitobObservableList);
                    hisobKitobObservableList.addAll(hisobKitobModels.getAnyData(connection, "qaydId = " + newValue.getId()  + " AND amal = " + newValue.getAmalTuri(), ""));
                    if (hisobKitobObservableList.size()>0) {
                        for (HisobKitob hk: hisobKitobObservableList) {
                            int ishora = qaydnomaData.getChiqimId().equals(hk.getHisob1()) ? 1 : -1;
                            hk.setNarh(ishora * hk.getNarh());
                        }
                        qaydnomaJami.getJami(newValue, hisobKitobObservableList);
                        rightPaneTableView.getSelectionModel().selectFirst();
                        qaydnomaJami.getJamiFromGrid(newValue, hisobKitobObservableList);
                        rightPaneButtons.setEnableAll();
                    } else {
                        rightPaneButtons.setDisableAll();
                        rightPaneButtons.getAdd().setDisable(false);
                    }
                    rightPaneTableView.refresh();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        leftPane.getChildren().addAll(leftPaneButtons, leftPaneTableView);
        /*************************************************
         **                  O`ng                       **
         *************************************************/
        rightGridPane = new RightGridPane(connection, user, qaydnomaData, qaydnomaJami, rightPaneButtons);
        hisobKitobObservableList = rightPaneTableView.getItems();
        if (hisobKitobObservableList.size()>0) {
            rightPaneTableView.getSelectionModel().selectFirst();
            qaydnomaJami.getJamiFromGrid(qaydnomaData, hisobKitobObservableList);
            rightPaneButtons.setEnableAll();
        } else {
            if (qaydnomaDataObservableList.size()>0) {
                rightPaneButtons.setDisableAll();
                rightPaneButtons.getAdd().setDisable(false);
            } else {
                rightPaneButtons.setDisableAll();
            }
        }
        rightPane.getChildren().addAll(rightPaneButtons, rightPaneTableView, qaydnomaJami, qaydnomaIzohTextArea);
        /*************************************************
         **                   Methods                   **
         *************************************************/
        leftCancelEvent = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                leftPane.getChildren().removeAll(leftPane.getChildren());
                leftPane.getChildren().addAll(leftPaneButtons, leftPaneTableView);
                rightPane.setDisable(false);
            }
        };

        leftPaneButtons.getAdd().setOnAction(event -> {
            if (amalTuri == 1) {
                PulHarakatlari pulHarakatlari = new PulHarakatlari(connection, user);
                qaydnomaData = pulHarakatlari.display();
            } else if(amalTuri == 2) {
                TovarXaridi tovarXaridi = new TovarXaridi(connection, user);
                qaydnomaData = tovarXaridi.display();
            } else if(amalTuri == 3) {
                TovarHarakatlari tovarHarakatlari = new TovarHarakatlari(connection, user);
                qaydnomaData = tovarHarakatlari.display();
            }
            if (qaydnomaData != null) {
                qaydnomaDataObservableList.add(qaydnomaData);
                Collections.sort(qaydnomaDataObservableList, Comparator.comparingInt(QaydnomaData::getHujjat).thenComparingInt(QaydnomaData::getAmalTuri).reversed());
                leftPaneTableView.setItems(qaydnomaDataObservableList);
                leftPaneTableView.getSelectionModel().select(qaydnomaData);
                leftPaneTableView.scrollTo(qaydnomaData);
            }
        });

        leftPaneButtons.getDelete().setOnAction(event -> {
            rightPane.setDisable(true);
            qaydnomaData = leftPaneTableView.getSelectionModel().getSelectedItem();
            leftGridPane.getGridItems(qaydnomaData, 2);
            leftGridPane.setGridItems(true);
            leftPane.getChildren().removeAll(leftPane.getChildren());
            leftPane.getChildren().add(leftGridPane.getGridPane());

            leftGridPane.getQaydEtButton().setOnAction(event1 -> {
                leftGridPane.saveGridToQaydnomaData();
                qaydnomaModel.delete_data(connection, qaydnomaData);
                hisobKitobModels.deleteWhere(connection, "qaydId = " + qaydnomaData.getId());
                qaydnomaDataObservableList.remove(qaydnomaData);
                if (qaydnomaDataObservableList.size()==0) {
                    leftPaneButtons.setDisableAll();
                    leftPaneButtons.getAdd().setDisable(false);
                }
                leftGridPane.getCancelButton().fire();
            });
            leftGridPane.getCancelButton().setOnAction(leftCancelEvent);
        });

        leftPaneButtons.getEdit().setOnAction(event -> {
            rightPane.setDisable(true);
            qaydnomaData = leftPaneTableView.getSelectionModel().getSelectedItem();
            leftGridPane.getGridItems(qaydnomaData, 3);
            leftGridPane.setGridItems(false);
            leftPane.getChildren().removeAll(leftPane.getChildren());
            leftPane.getChildren().add(leftGridPane.getGridPane());

            leftGridPane.getQaydEtButton().setOnAction(event1 -> {
                Boolean sahih = leftGridPane.getBeruvchiTextField().getText().trim().equals(leftGridPane.getOluvchiTextField().getText().trim());
                if (sahih) {
                    Alerts.hisoblarBirXilAlert(leftGridPane.getOluvchiTextField().getText(), leftGridPane.getBeruvchiTextField().getText());
                    return;
                }
                leftGridPane.saveGridToQaydnomaData();
                if (hisobKitobObservableList.size()>0) {
                    for (HisobKitob hk: hisobKitobObservableList) {
                        hk.setHujjatId(qaydnomaData.getHujjat());
                        hk.setHisob1(qaydnomaData.getChiqimId());
                        hk.setHisob2(qaydnomaData.getKirimId());
                        hk.setDateTime(qaydnomaData.getSana());
                        hisobKitobModels.update_data(connection, hk);
                    }
                }
                qaydnomaModel.update_data(connection, qaydnomaData);
                leftPaneTableView.refresh();
                leftGridPane.getCancelButton().fire();
            });
            leftGridPane.getCancelButton().setOnAction(leftCancelEvent);
        });

        rightCancelEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                rightPane.getChildren().removeAll(rightPane.getChildren());
                rightPane.getChildren().addAll(rightPaneButtons, rightPaneTableView, qaydnomaJami, qaydnomaIzohTextArea);
                leftPane.setDisable(false);
            }
        };

        rightPaneButtons.getAdd().setOnAction(event -> {
            leftPane.setDisable(true);
            qaydnomaData = leftPaneTableView.getSelectionModel().getSelectedItem();
            hisobKitob = new HisobKitob(qaydnomaData.getId(), qaydnomaData.getHujjat(), qaydnomaData.getAmalTuri(), qaydnomaData.getChiqimId(), qaydnomaData.getKirimId());
            try {
                rightPane.getChildren().removeAll(rightPaneButtons, rightPaneTableView, qaydnomaJami);
                rightGridPane.add(leftPaneTableView, hisobKitob, rightPaneTableView);
                rightPane.getChildren().add(0, rightGridPane);
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            rightGridPane.getQaydEtButton().setOnAction(event1 -> {
                try {
                    rightGridPane.gridToHisobKitob(hisobKitobObservableList,1);
                    switch (amalTuri) {
                        case 3:
                            break;
                        case 5:
                            break;
                        default:
                            hisobKitob.setId(hisobKitobModels.insert_data(connection, hisobKitob));
                            hisobKitobObservableList.add(hisobKitob);
                            rightPaneTableView.getSelectionModel().select(hisobKitob);
                            rightPaneTableView.scrollTo(hisobKitob);
                            qaydnomaJami.getJamiFromGrid(qaydnomaData, hisobKitobObservableList);
                            qaydnomaModel.update_data(connection, qaydnomaData);
                            rightPaneTableView.refresh();
                            rightPaneButtons.setEnableAll();
                            leftPaneTableView.refresh();
                            rightGridPane.getCancelButton().fire();
                            break;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            });

            rightGridPane.getCancelButton().setOnAction(rightCancelEvent);
        });

        rightPaneButtons.getDelete().setOnAction(event -> {
            leftPane.setDisable(true);
            hisobKitob = rightPaneTableView.getSelectionModel().getSelectedItem();
            if (hisobKitob != null) {
                try {
                    rightPane.getChildren().removeAll(rightPaneButtons, rightPaneTableView, qaydnomaJami);
                    rightGridPane.delete(leftPaneTableView, hisobKitob, rightPaneTableView);
                    rightPane.getChildren().add(0, rightGridPane);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                rightGridPane.getQaydEtButton().setOnAction(event1 -> {
                    try {
                        rightGridPane.gridToHisobKitob(hisobKitobObservableList,2);
                        hisobKitobModels.delete_data(connection, hisobKitob);
                        hisobKitobObservableList.remove(hisobKitob);
                        if (hisobKitobObservableList.size() == 0) {
                            rightPaneButtons.setDisableAll();
                            rightPaneButtons.getAdd().setDisable(false);
                        } else {
                            rightPaneButtons.setEnableAll();
                        }
                        qaydnomaJami.getJamiFromGrid(qaydnomaData, hisobKitobObservableList);
                        qaydnomaModel.update_data(connection, qaydnomaData);
                        rightPaneTableView.refresh();
                        leftPaneTableView.refresh();
                        rightGridPane.getCancelButton().fire();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                });
                rightGridPane.getCancelButton().setOnAction(rightCancelEvent);
            }
        });

        rightPaneButtons.getEdit().setOnAction(event -> {
            leftPane.setDisable(true);
            hisobKitob = rightPaneTableView.getSelectionModel().getSelectedItem();
            if (hisobKitob != null) {
                try {
                    rightPane.getChildren().removeAll(rightPaneButtons, rightPaneTableView, qaydnomaJami);
                    rightGridPane.edit(leftPaneTableView, hisobKitob, rightPaneTableView);
                    rightPane.getChildren().add(0, rightGridPane);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                rightGridPane.getQaydEtButton().setOnAction(event1 -> {
                    try {
                        rightGridPane.gridToHisobKitob(hisobKitobObservableList, 3);
                        hisobKitobModels.update_data(connection, hisobKitob);
                        qaydnomaModel.update_data(connection, qaydnomaData);
                        rightPaneTableView.getSelectionModel().select(hisobKitob);
                        qaydnomaJami.getJamiFromGrid(qaydnomaData, hisobKitobObservableList);
                        rightPaneTableView.refresh();
                        leftPaneTableView.refresh();
                        rightGridPane.getCancelButton().fire();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                });
                rightGridPane.getCancelButton().setOnAction(rightCancelEvent);

            }
        });
        /*************************************************
         **                Umumiy kod                   **
         *************************************************/
        centerPane.setDividerPositions(.65);
        centerPane.setPadding(new Insets(3));
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        VBox.setVgrow(centerPane, Priority.ALWAYS);

        HBox.setHgrow(leftPane, Priority.ALWAYS);
        VBox.setVgrow(leftPane, Priority.ALWAYS);
        HBox.setHgrow(leftPaneTableView, Priority.ALWAYS);
        VBox.setVgrow(leftPaneTableView, Priority.ALWAYS);

        HBox.setHgrow(rightPane, Priority.ALWAYS);
        VBox.setVgrow(rightPane, Priority.ALWAYS);
        HBox.setHgrow(rightGridPane, Priority.ALWAYS);
        VBox.setVgrow(rightGridPane, Priority.ALWAYS);
        HBox.setHgrow(rightPaneTableView, Priority.ALWAYS);
        VBox.setVgrow(rightPaneTableView, Priority.ALWAYS);
        HBox.setHgrow(qaydnomaJami, Priority.ALWAYS);
        VBox.setVgrow(qaydnomaJami, Priority.ALWAYS);
        HBox.setHgrow(qaydnomaIzohTextArea, Priority.ALWAYS);
        VBox.setVgrow(qaydnomaIzohTextArea, Priority.ALWAYS);

        centerPane.getItems().addAll(leftPane, rightPane);
        bottom.setAlignment(Pos.CENTER);
        borderpane.setCenter(centerPane);
        scene = new Scene(borderpane, 600, 400);
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        stage.setScene(scene);
/*
        if (amalTuri == 2) {
            barCodeOn();
        }
        stage.setOnCloseRequest(event -> {
            if (amalTuri == 2) {
                barCodeOff();
            }
        });
*/
        stage.showAndWait();
    }

    private TableColumn<QaydnomaData, Standart> getStatusColumn() {
        TableColumn<QaydnomaData, Standart> statusColumn = new TableColumn<>("Holat");
        statusColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<QaydnomaData, Standart>, ObservableValue<Standart>>() {

            @Override
            public ObservableValue<Standart> call(TableColumn.CellDataFeatures<QaydnomaData, Standart> param) {
                QaydnomaData q = param.getValue();
                Integer statusId = q.getStatus();
                Standart s = statusObservableList.get(statusId);
                return new SimpleObjectProperty<Standart>(s);
            }
        });

        statusColumn.setCellFactory(ComboBoxTableCell.forTableColumn(statusObservableList));
        statusColumn.setOnEditCommit((TableColumn.CellEditEvent<QaydnomaData, Standart> event) -> {
            TablePosition<QaydnomaData, Standart> pos = event.getTablePosition();
            Standart s = event.getNewValue();
            int row = pos.getRow();
            QaydnomaData q = event.getTableView().getItems().get(row);
            q.setStatus(s.getId());
            qaydnomaModel.update_data(connection, q);
        });
        statusColumn.setStyle( "-fx-alignment: CENTER;");
        return statusColumn;
    }

    private void Taftish(String oldValue, String newValue, Integer option) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        ObservableList<QaydnomaData> subentries = FXCollections.observableArrayList();

        newValue = newValue.toLowerCase();

        if ( oldValue != null && (newValue.length() < oldValue.length()) ) {
            leftPaneTableView.setItems(qaydnomaDataObservableList);
        }

        for ( QaydnomaData qayd: qaydnomaDataObservableList ) {
            switch (option) {
                case 0:
                    if (qayd.getHujjat().toString().contains(newValue)) {
                        subentries.add(qayd);
                    }
                    break;
                case 1:
                    if (sdf.format(qayd.getSana()).contains(newValue)) {
                        subentries.add(qayd);
                    }
                    break;
                case 2:
                    if (qayd.getChiqimNomi().toLowerCase().contains(newValue)) {
                        subentries.add(qayd);
                    }
                    break;
                case 3:
                    if (qayd.getKirimNomi().toLowerCase().contains(newValue)) {
                        subentries.add(qayd);
                    }
                    break;
                case 4:
                    if (qayd.getIzoh().toLowerCase().contains(newValue)) {
                        subentries.add(qayd);
                    }
                    break;
            }
        }
        leftPaneTableView.setItems(subentries);
    }

    private void barCodeOn() {
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                stringBuffer.append(event.getText());
            }
        });

        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    String string = stringBuffer.toString().trim();
                    BarCode barCode = GetDbData.getBarCode(string.trim());
                    Standart tovar = GetDbData.getTovar(barCode.getTovar());
                    if (tovar != null) {
                        addTovar(tovar);
                    }
                    stringBuffer.delete(0, stringBuffer.length());
                }
            }
        });
    }

    private void barCodeOff() {
        scene.setOnKeyPressed(null);
        scene.setOnKeyReleased(null);
    }

    private void addTovar(Standart tovar) {
    }

    private void initData(Connection connection) {
        GetDbData.initData(connection);
        StandartModels standartModels = new StandartModels();
        GetDbData.initData(connection);
        valutaObservableList = GetDbData.getValutaObservableList();
        tovarObservableList = GetDbData.getTovarObservableList();
        standartModels.setTABLENAME("Jumla");
        jumlaObservableList = standartModels.get_data(connection);
        standartModels.setTABLENAME("Birlik");
        birlikObservableList = standartModels.get_data(connection);
        standartModels.setTABLENAME("ChiqimShakli");
        chiqimShakliObservableList = standartModels.get_data(connection);
    }

    private Standart getItem(ObservableList<Standart> standarts, int index) {
        Standart standart = null;
        for (Standart s: standarts) {
            if (s.getId().equals(index)) {
                standart = s;
                break;
            }
        }
        return standart;
    }
}