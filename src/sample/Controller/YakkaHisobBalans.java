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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import sample.Data.*;
import sample.Model.HisobKitobModels;
import sample.Model.QaydnomaModel;
import sample.Tools.*;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class YakkaHisobBalans extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    HBox topPane = new HBox();
    VBox leftPane = new VBox();
    GridPane centerPane = new GridPane();
    VBox rightPane = new VBox();
    HBox bottomPane = new HBox();
    TableView<HisobKitob> tableView;
    Connection connection;
    User user;
    Hisob hisob;
    HisobKitobModels hisobKitobModels = new HisobKitobModels();
    ObservableList<QaydnomaData> qaydnomaDataObservableList = FXCollections.observableArrayList();
    int padding = 3;
    HisobKitob hisobKitob;
    Label balansLabel = new Label();
    Font font = Font.font("Arial", FontWeight.BOLD,20);

    public static void main(String[] args) {
        launch(args);
    }

    public YakkaHisobBalans(Connection connection, User user, Integer hisobId) {
        this.connection = connection;
        this.user = user;
        this.hisob = GetDbData.getHisob(hisobId);
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
        QaydnomaModel qaydnomaModel = new QaydnomaModel();
        qaydnomaDataObservableList = qaydnomaModel.get_data(connection);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initStage(primaryStage);
        stage.show();
    }

    public void display() {
        refreshHisobKitobTable(hisob);
        ObservableList<HisobKitob> observableList = tableView.getItems();
        if (observableList.size()>0)
            hisobKitob = observableList.get(0);
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
        initBalansLabel();
        initHisobKitobTable();
        Label hisobLabel = new Label(hisob.getText());
        hisobLabel.setFont(font);
        centerPane.add(hisobLabel, 0, 0, 1, 1);
        centerPane.add(tableView, 0, 1, 1, 1);
        centerPane.add(balansLabel, 0, 2, 1, 1);
        GridPane.setHalignment(hisobLabel, HPos.CENTER);
        GridPane.setHalignment(tableView, HPos.CENTER);
        GridPane.setHalignment(balansLabel, HPos.CENTER);
        GridPane.setHgrow(tableView, Priority.ALWAYS);
        GridPane.setVgrow(tableView, Priority.ALWAYS);
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
        scene = new Scene(borderpane, 1200, 400);
        stage.setScene(scene);
    }
    private void refreshHisobKitobTable(Hisob hisob) {
        DecimalFormat decimalFormat = new MoneyShow();
        ObservableList<HisobKitob> observableList = null;
        if (hisob != null) {
            observableList = hisobKitobModels.getAnyData(connection, "hisob1 = " + hisob.getId() + " OR hisob2 = " + hisob.getId(), "id asc");
            setDateTime();
            hkKirimChiqim(hisob, observableList);
        }
        Double balansDouble = hisobKitobModels.getHisobBalance(connection, hisob);
        balansLabel.setText(decimalFormat.format(balansDouble));
        tableView.setItems(observableList);
        tableView.refresh();
    }
    private void setDateTime() {
        ObservableList<HisobKitob> observableList = tableView.getItems();
        for (HisobKitob hk: observableList) {
            hk.setDateTime(getQaydDate(hk.getQaydId()));
        }
//        Collections.sort(observableList, Comparator.comparing(HisobKitob::getDateTime));
    }

    private Date getQaydDate(Integer qaydId) {
        Date qaydDate = null;
        for (QaydnomaData q: qaydnomaDataObservableList) {
            if (q.getId().equals(qaydId)) {
                qaydDate = q.getSana();
                break;
            }
        }
        return qaydDate;
    }
    public void hkKirimChiqim(Hisob hisob, ObservableList<HisobKitob> observableList) {
        Double yigindi = .0;
        if (observableList.size()>0) {
            hisobKitob = observableList.get(0);
        }
        for (HisobKitob hk: observableList) {
            if (hk.getHisob1().equals(hisob.getId()) ) {
                hk.setId(1);
            } else {
                hk.setId(2);
            }

            if (hk.getId().equals(1)) {
                yigindi -= hk.getSummaCol();
            } else {
                yigindi += hk.getSummaCol();
            }
            hk.setBalans(yigindi);
        }
    }

    public TableView<HisobKitob> initHisobKitobTableView() {
        tableView = new TableView<>();
        tableView.getColumns().addAll(getCustomColumn(), getAmalColumn(), getValutaColumn(), getKursColumn(), getIzoh2Column(), getAdadColumn(), getNarhColumn(), getSummaColumn());
        return tableView;
    }

    public TableColumn<HisobKitob, Integer> getAmalColumn() {
        TableColumn<HisobKitob, Integer> amalColumn = new TableColumn<>("Amal");
        amalColumn.setMinWidth(100);
        amalColumn.setCellValueFactory(new PropertyValueFactory<>("amal"));
        amalColumn.setCellFactory(column -> {
            TableCell<HisobKitob, Integer> cell = new TableCell<HisobKitob, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        Standart amal = GetDbData.getAmal(item);
                        if (amal != null) {
                            Text text = new Text(amal.getText());
                            text.setStyle("-fx-text-alignment:justify;");
                            text.wrappingWidthProperty().bind(getTableColumn().widthProperty().subtract(2));
                            setGraphic(text);
                        } else {
                            setText("Amalda xato");
                        }
                    }
                }
            };
            return cell;
        });
        return amalColumn;
    }

    public TableColumn<HisobKitob, Integer> getCustomColumn() {
        TableColumn<HisobKitob, Integer> integerTableColumn = new TableColumn<>("Kirim/\nChiqim");
        integerTableColumn.setMinWidth(100);
        integerTableColumn.setCellValueFactory(new PropertyValueFactory<>("hisob2"));
        integerTableColumn.setCellFactory(column -> {
            TableCell<HisobKitob, Integer> cell = new TableCell<HisobKitob, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        if (item == 1) {
                            setText("Chiqim");
                        } else {
                            setText("Kirim");
                        }
                    }
                }
            };
            return cell;
        });
        return integerTableColumn;
    }

    public TableColumn<HisobKitob, Integer> getValutaColumn() {
        TableColumn<HisobKitob, Integer> valutaColumn = new TableColumn<>("Pul turi");
        valutaColumn.setMinWidth(100);
        valutaColumn.setCellValueFactory(new PropertyValueFactory<>("valuta"));
        valutaColumn.setCellFactory(column -> {
            TableCell<HisobKitob, Integer> cell = new TableCell<HisobKitob, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        Valuta valuta = GetDbData.getValuta(item);
                        if (valuta != null) {
                            setText(valuta.getValuta());
                        } else {
                            setText("");
                        }
                    }
                    setAlignment(Pos.TOP_CENTER);
                }
            };
            return cell;
        });
        return valutaColumn;
    }

    public TableColumn<HisobKitob, Double> getKursColumn() {
        TableColumn<HisobKitob, Double> kursColumn = new TableColumn<>("Kurs");
        kursColumn.setMinWidth(100);
        kursColumn.setCellValueFactory(new PropertyValueFactory<>("kurs"));
        kursColumn.setCellFactory(column -> {
            TableCell<HisobKitob, Double> cell = new TableCell<HisobKitob, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        setText(new MoneyShow().format(item));
                    }
                }
            };
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        return kursColumn;
    }

    public TableColumn<HisobKitob, Integer> getTovarColumn() {
        TableColumn<HisobKitob, Integer> tovarColumn = new TableColumn<>("Tovar");
        tovarColumn.setMinWidth(200);
        tovarColumn.setCellValueFactory(new PropertyValueFactory<>("tovar"));
        tovarColumn.setCellFactory(column -> {
            TableCell<HisobKitob, Integer> cell = new TableCell<HisobKitob, Integer>() {
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

    public TableColumn<HisobKitob, String> getBarCodeColumn() {
        TableColumn<HisobKitob, String> hisob2 = new TableColumn<>("Shtrixkod");
        hisob2.setMinWidth(80);
        hisob2.setCellValueFactory(new PropertyValueFactory<>("barCode"));
        return hisob2;
    }

    public TableColumn<HisobKitob, String> getBirlikColumn() {
        TableColumn<HisobKitob, String> birlikColumn = new TableColumn<>("Birlik");
        birlikColumn.setCellValueFactory(new PropertyValueFactory<>("barCode"));
        birlikColumn.setMinWidth(100);
        birlikColumn.setMaxWidth(100);
        birlikColumn.setCellFactory(column -> {
            TableCell<HisobKitob, String> cell = new TableCell<HisobKitob, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        BarCode bc = GetDbData.getBarCode(item);
                        Standart birlik = GetDbData.getBirlik(bc.getBirlik());
                        setText(bc.getBarCode() + "\n" + birlik.getText());
                    }
                    setAlignment(Pos.CENTER_LEFT);
                }
            };
            return cell;
        });
        return birlikColumn;
    }

    public TableColumn<HisobKitob, String> getIzoh2Column() {
        TableColumn<HisobKitob, String> izohColumn = new TableColumn<>("Eslatma");
        izohColumn.setMinWidth(150);
        izohColumn.setCellValueFactory(new PropertyValueFactory<>("izoh"));
        izohColumn.setCellFactory(column -> {
            TableCell<HisobKitob, String> cell = new TableCell<HisobKitob, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
//                        setText(GetDbData.getTovar(item).getText());
                        Text text = new Text(item);
                        text.setStyle("-fx-text-alignment:justify;");
                        text.wrappingWidthProperty().bind(getTableColumn().widthProperty().subtract(2));
                        setGraphic(text);
                    }
                    setAlignment(Pos.CENTER_LEFT);
                }
            };
            return cell;
        });
        return izohColumn;
    }

    public TableColumn<HisobKitob, Double> getAdadColumn() {
        TableColumn<HisobKitob, Double> adadColumn = new TableColumn<>("Dona");
        adadColumn.setMinWidth(100);
        adadColumn.setCellValueFactory(new PropertyValueFactory<>("dona"));
        adadColumn.setCellFactory(column -> {
            TableCell<HisobKitob, Double> cell = new TableCell<HisobKitob, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        setText(new MoneyShow().format(item));
                    }
                }
            };
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        adadColumn.setStyle( "-fx-alignment: CENTER;");
        return adadColumn;
    }

    public TableColumn<HisobKitob, Double> getNarhColumn() {
        TableColumn<HisobKitob, Double> narhColumn = new TableColumn<>("Narh");
        narhColumn.setMinWidth(150);
        narhColumn.setCellValueFactory(new PropertyValueFactory<>("narh"));
        narhColumn.setCellFactory(column -> {
            TableCell<HisobKitob, Double> cell = new TableCell<HisobKitob, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        setText(new MoneyShow().format(item));
                    }
                }
            };
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        return narhColumn;
    }

    public TableColumn<HisobKitob, Double> getSummaColumn() {
        TableColumn<HisobKitob, Double> summaColumn = new TableColumn<>("Summa");
        summaColumn.setMinWidth(150);
        summaColumn.setCellValueFactory(new PropertyValueFactory<>("summaCol"));
        summaColumn.setCellFactory(column -> {
            TableCell<HisobKitob, Double> cell = new TableCell<HisobKitob, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        setText(new MoneyShow().format(item));
                    }
                }
            };
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        return summaColumn;
    }

    public TableColumn<HisobKitob, Date> getDateTimeColumn() {
        TableColumn<HisobKitob, Date> sanaColumn = new TableColumn("Sana");
        sanaColumn.setMinWidth(80);
        sanaColumn.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        sanaColumn.setCellFactory(column -> {
            TableCell<HisobKitob, Date> cell = new TableCell<HisobKitob, Date>() {
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy\n HH.mm.ss");
                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    } else {
                        if (item != null) {
                            Text text = new Text(format.format(item));
                            setGraphic(text);
                        } else {
                            setText("");
                        }
                    }
                }
            };
            cell.setAlignment(Pos.TOP_CENTER);

            return cell;
        });
        return sanaColumn;
    }
    private void initHisobKitobTable() {
        tableView = new TableView<>();
        tableView.setMinWidth(1184);
        TableViewAndoza tableViewAndoza = new TableViewAndoza();
        tableViewAndoza.getAdadColumn().setMinWidth(80);
        TableColumn<HisobKitob, Integer> amalColumn = tableViewAndoza.getAmalColumn();
        amalColumn.setStyle( "-fx-alignment: CENTER;");

        TableColumn<HisobKitob, DoubleTextBox> valutaKursColumn = tableViewAndoza.valutaKurs();
        valutaKursColumn.setStyle( "-fx-alignment: CENTER;");

        tableView.getColumns().addAll(getDateTimeColumn(), tableViewAndoza.hisob1Hisob2(), amalColumn,
                tableViewAndoza.getIzoh2Column(), valutaKursColumn,
                tableViewAndoza.adadNarh(),tableViewAndoza.getSummaColumn(),
                tableViewAndoza.getBalans2Column());
        HBox.setHgrow(tableView, Priority.ALWAYS);
        VBox.setVgrow(tableView, Priority.ALWAYS);
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
            }
        });
        tableView.setRowFactory(tv -> new TableRow<HisobKitob>() {
            @Override
            protected void updateItem(HisobKitob hisobKitob, boolean empty) {
                super.updateItem(hisobKitob, empty);
                if (hisobKitob == null || hisobKitob.getId() == null)
                    setStyle("");
                else if (hisobKitob.getId() == 2)
                    setStyle("-fx-background-color: white;");
                else if (hisobKitob.getId() == 1)
                    setStyle("-fx-background-color: #baffba;");
                else
                    setStyle("");
            }
        });
        tableView.
                setStyle("-fx-selection-bar: red; -fx-selection-bar-non-focused: salmon;");
    }
    public TableColumn<HisobKitob, Integer> getCustom2Column() {
        TableColumn<HisobKitob, Integer> integerTableColumn = new TableColumn<>("Kirim/\nChiqim");
        integerTableColumn.setMinWidth(100);
        integerTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, Integer>, ObservableValue<Integer>>() {

            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<HisobKitob, Integer> param) {
                HisobKitob hk = param.getValue();
                Integer hkId = hk.getId();
                return new SimpleObjectProperty<Integer>(hkId);
            }
        });
        integerTableColumn.setCellFactory(column -> {
            TableCell<HisobKitob, Integer> cell = new TableCell<HisobKitob, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        Hisob hisob1 = GetDbData.getHisob(hisobKitob.getHisob1());
                        Hisob hisob2 = GetDbData.getHisob(hisobKitob.getHisob2());
                        Text text = new Text();
                        if (item == 1) {
                            text.setText("Chiqim: " + hisob2.getText().trim() + "ga");
                        } else {
                            text.setText("Kirim: " + hisob1.getText().trim() + "dan");
                        }
                        text.setStyle("-fx-text-alignment:justify;");
                        text.wrappingWidthProperty().bind(getTableColumn().widthProperty().subtract(2));
                        setGraphic(text);

                    }
                }
            };
            return cell;
        });
        return integerTableColumn;
    }


    private void initBalansLabel() {
        HBox.setHgrow(balansLabel, Priority.ALWAYS);
        balansLabel.setFont(font);
        balansLabel.setAlignment(Pos.CENTER);
    }


}
