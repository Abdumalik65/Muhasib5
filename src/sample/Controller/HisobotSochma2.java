package sample.Controller;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import sample.Config.MySqlDBGeneral;
import sample.Data.*;
import sample.Enums.ServerType;
import sample.Excel.HisoblarExcel;
import sample.Excel.SochmaHisobotExcel;
import sample.Model.HisobKitobModels;
import sample.Model.HisobModels;
import sample.Model.QaydnomaModel;
import sample.Model.Standart3Models;
import sample.Tools.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class HisobotSochma2 extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    SplitPane centerPane = new SplitPane();
    VBox rightPane = new VBox();
    VBox leftPane = new VBox();
    HBox leftHBox = new HBox();

    TextField qidirTextField = new TextField();

    TableView<Hisob> hisobTableView = new TableView<>();
    TableView<HisobKitob> hisobKitobTableView = new TableView<>();
    TableViewAndoza tableViewAndoza = new TableViewAndoza();

    ObservableList<Hisob> hisobObservableList;
    ObservableList<Hisob> hisobListForTable = FXCollections.observableArrayList();
    ObservableList<HisobKitob> hisobKitobObservableList = FXCollections.observableArrayList();
    ObservableList<HisobKitob> rightObservableList = FXCollections.observableArrayList();
    ObservableList<QaydnomaData> qaydnomaDataObservableList = FXCollections.observableArrayList();

    HisobKitobModels hisobKitobModels = new HisobKitobModels();
    QaydnomaModel qaydnomaModel = new QaydnomaModel();
    Hisob hisob;
    HisobKitob hisobKitob;

    Connection connection;
    User user;

    int padding = 3;
    HBox jamiHBox = new HBox();
    Label jamiLabel = new Label();
    Font font = Font.font("Arial", FontWeight.BOLD,20);
    Button hisoblarToExcelButton = new Button("");
    Button hisobToExcelButton = new Button("");
    Button qaydEtButton = new Tugmachalar().getAdd();
    Button backButton = new Button("<<");
    LocalDate localDate = LocalDate.now();
    DatePicker datePicker = new DatePicker(localDate);


    public static void main(String[] args) {
        launch(args);
    }

    public HisobotSochma2() {
        connection = new MySqlDBGeneral(ServerType.REMOTE).getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
        ibtido();
    }

    public HisobotSochma2(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        this.localDate = LocalDate.now();
        String classSimpleName = getClass().getSimpleName();
        DasturlarRoyxati.dastur(connection, user, classSimpleName);
        ibtido();
    }

    private void ibtido() {
        initDataYangi();
        tableViewAndoza.initTableViews();
        initHisobTableView();
        initButtons();
        initTextFields();
        initLeftHBox();
        initLeftPane();
        initHisobKitobTable();
        initRightPane();
        initCenterPane();
        initBorderPane();
    }

    private void initDataYangi() {
        hisoblarniYangila2(localDate);
    }

    private void hisoblarniYangila2(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        HisobModels hisobModels = new HisobModels();
        hisobObservableList = hisobModels.get_data(connection);
        Standart3Models standart3Models = new Standart3Models();
        standart3Models.setTABLENAME("CheklanganHisobTarkibi");
        ObservableList<Standart3> standart3List = standart3Models.getAnyData(connection, "id2 = " + user.getId(), "");
        hisobListForTable = hisoblarList(localDate);
        for (Standart3 s3: standart3List) {
            Hisob hisob = GetDbData.hisobniTop(s3.getId3(), hisobListForTable);
            if (hisob != null) {
                hisobListForTable.remove(hisob);
            }
        }
    }
    private ObservableList<Hisob> hisoblarList(LocalDate localDate) {
        DecimalFormat decimalFormat = new MoneyShow();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String localDateString = localDate.format(formatter);
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        ObservableList<Hisob> hisobObservableList = FXCollections.observableArrayList();
        String kirimHisoblari =
                "Select hisob2, sum(if(tovar>0,narh*dona/kurs,narh/kurs)) from HisobKitob where dateTime<='" + localDateString + " 23:59:59' group by hisob2 order by hisob2";
        ResultSet rs1 = hisobKitobModels.getResultSet(connection, kirimHisoblari);
        try {
            while (rs1.next()) {
                Integer id = rs1.getInt(1);
                Double balance = rs1.getDouble(2);
                Hisob hisob = GetDbData.getHisob(id);
                if (hisob == null) {
                    System.out.println(id);
                }
                if (balance==null) {
                    System.out.println(balance);
                }
                hisobObservableList.add(new Hisob(id, hisob.getText(), 0d, balance, 0d));
            }
            rs1.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String chiqimHisoblari =
                "Select hisob1, sum(if(tovar>0,narh*dona/kurs,narh/kurs)) from HisobKitob where dateTime<='" + localDateString + " 23:59:59' group by hisob1 order by hisob1";
        ResultSet rs2 = hisobKitobModels.getResultSet(connection, chiqimHisoblari);
        try {
            while (rs2.next()) {
                Integer id = rs2.getInt(1);
                Double balance = rs2.getDouble(2);
                Hisob hisob = GetDbData.hisobniTop(id, hisobObservableList);
                if (hisob != null) {
                    hisob.setChiqim(balance);
                } else {
                    Hisob h = GetDbData.getHisob(id);
                    hisobObservableList.add(new Hisob(id, h.getText(), balance, 0d, 0d));
                }
            }
            rs2.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (hisobObservableList.size()>0) {
            hisobObservableList.forEach(hisob -> {
                hisob.setBalans(hisob.getKirim() - hisob.getChiqim());
            });
            Comparator<Hisob> comparator = Comparator.comparing(Hisob::getId);
            Collections.sort(hisobObservableList, comparator);
        }
//        hisobObservableList.removeIf(hisob -> StringNumberUtils.yaxlitla(hisob.getBalans(), -2) == 0d);
        return hisobObservableList;
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

    private void initButtons() {
        hisoblarToExcelButton.setGraphic(new PathToImageView("/sample/images/Icons/excel.png").getImageView());
        hisobToExcelButton.setGraphic(new PathToImageView("/sample/images/Icons/excel.png").getImageView());

        hisoblarToExcelButton.setOnAction(event -> {
            HisoblarExcel hisoblarExcel = new HisoblarExcel();
            hisoblarExcel.hisoblar(hisobListForTable);
        });

        hisobToExcelButton.setOnAction(event -> {
            SochmaHisobotExcel sochmaHisobotExcel = new SochmaHisobotExcel();
            sochmaHisobotExcel.hisob(hisob.getId(), rightObservableList);
        });
    }

    private void initTextFields() {
        HBox.setHgrow(qidirTextField, Priority.ALWAYS);
        TextFields.bindAutoCompletion(qidirTextField, hisobListForTable).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Hisob> autoCompletionEvent) -> {
            Hisob hisob = autoCompletionEvent.getCompletion();
            if (hisob != null) {
                hisobTableView.getSelectionModel().select(hisob);
                hisobTableView.scrollTo(hisob);
                hisobTableView.requestFocus();
            }
        });
    }

    private void initLeftHBox() {
        leftHBox.setPadding(new Insets(padding));
        HBox.setHgrow(leftHBox, Priority.ALWAYS);
        initDatePicker();
        leftHBox.getChildren().addAll(hisoblarToExcelButton, qidirTextField, datePicker);
    }

    private void initLeftPane() {
        leftPane.setPadding(new Insets(padding));
        HBox.setHgrow(leftPane, Priority.ALWAYS);
        VBox.setVgrow(leftPane, Priority.ALWAYS);
        initJamiHBox();
        leftPane.getChildren().addAll(leftHBox, hisobTableView, jamiHBox);
        leftPane.setMinWidth(280);
        leftPane.setMaxWidth(280);

    }

    private void initHisobTableView() {
        hisobTableView = tableViewAndoza.getHisobTableView();
        HBox.setHgrow(hisobTableView, Priority.ALWAYS);
        VBox.setVgrow(hisobTableView, Priority.ALWAYS);
        hisobTableView.setItems(hisobListForTable);
        if (hisobListForTable.size()>0) {
            hisob = hisobListForTable.get(0);
            refreshHisobKitobTable(hisob);
            hisobTableView.getSelectionModel().selectFirst();
        }
        hisobTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                hisob = newValue;
                refreshHisobKitobTable(hisob);
            }
        });
    }

    private void initDatePicker() {
        // Converter
        StringConverter<LocalDate> converter = new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }
            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        };
        localDate = localDate.now();
        datePicker =  new DatePicker(localDate);

        datePicker.setConverter(converter);
        datePicker.setMaxWidth(2000);
        datePicker.setMinWidth(115);
        datePicker.setPrefWidth(150);
        HBox.setHgrow(datePicker, Priority.ALWAYS);

        datePicker.setOnAction(event -> {
            LocalDate newDate = datePicker.getValue();
            if (newDate != null) {
                localDate = newDate;
                hisoblarniYangila2(localDate);
                hisobTableView.setItems(hisobListForTable);
                hisobTableView.refresh();
            }
        });
    }

    private void refreshHisobKitobTable(Hisob hisob) {
        localDate = datePicker.getValue();
        DecimalFormat decimalFormat = new MoneyShow();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String localDateString = localDate.format(formatter);
        ObservableList<HisobKitob> balansList = FXCollections.observableArrayList();
        qaydnomaDataObservableList = qaydnomaModel.getAnyData(connection, "chiqimId=" + hisob.getId() + " or kirimId=" +hisob.getId() + " and dateTime<='" + localDateString + " 23:59:59'", "");
        rightObservableList.removeAll(rightObservableList);
        if (hisob != null) {
            rightObservableList.addAll(hisobKitobModels.getAnyData(connection, "hisob1 = " + hisob.getId() + " OR hisob2 = " + hisob.getId() + " and dateTime<='" + localDateString + " 23:59:59'", "id asc"));
            setDateTime();
            Comparator<HisobKitob> comparator = Comparator.comparing(HisobKitob::getDateTime);
            Collections.sort(rightObservableList, comparator);
            hkKirimChiqim(hisob);
        }
        hisobKitobTableView.setItems(rightObservableList);
        hisobKitobTableView.refresh();
    }

    private void refreshHisobKitobTableYangi(Hisob hisob) {
        rightObservableList.clear();
        if (hisob != null) {
            for (HisobKitob hk: hisobKitobObservableList) {
                if (hk.getHisob1().equals(hisob.getId()) || hk.getHisob2().equals(hisob.getId())) {
                    Date date = getQaydDate(hk.getQaydId());
                    if (date != null) {
                        hk.setDateTime(date);
                    }
                    rightObservableList.add(hk);
                }
            }
            Comparator<HisobKitob> comparator = Comparator.comparing(HisobKitob::getDateTime);
            Collections.sort(rightObservableList, comparator);
            hkKirimChiqim(hisob);
        }
        hisobKitobTableView.setItems(rightObservableList);
        hisobKitobTableView.refresh();
    }

    private void setDateTime() {
        for (HisobKitob hk : rightObservableList) {
            Date date = getQaydDate(hk.getQaydId());
            if (date!=null)
                hk.setDateTime(getQaydDate(hk.getQaydId()));
        }
    }


    private Date getQaydDate(Integer qaydId) {
        Date qaydDate = null;
        for (QaydnomaData q: qaydnomaDataObservableList) {
            if (q.getId().equals(qaydId)) {
                qaydDate = q.getSana();
                break;
            }
        }
        if (qaydDate == null) {
            System.out.println(qaydId);
        }
        return qaydDate;
    }

    private void initHisobKitobTable() {
        tableViewAndoza.getAdadColumn().setMinWidth(80);
        TableColumn<HisobKitob, Integer> amalColumn = tableViewAndoza.getAmalColumn();
        amalColumn.setStyle( "-fx-alignment: CENTER;");

        TableColumn<HisobKitob, Integer> valutaColumn = tableViewAndoza.getValutaColumn();
        valutaColumn.setStyle( "-fx-alignment: CENTER;");

        hisobKitobTableView.getColumns().addAll(
                tableViewAndoza.getDateTimeColumn(),
                hisob1Hisob2(),
                amalHujjat(),
                tableViewAndoza.getIzoh2Column(), valutaKurs(),
                adadNarh(), tableViewAndoza.getSummaColumn(),
                tableViewAndoza.getBalans2Column());
        HBox.setHgrow(hisobKitobTableView, Priority.ALWAYS);
        VBox.setVgrow(hisobKitobTableView, Priority.ALWAYS);
        hisobKitobTableView.setItems(rightObservableList);
        hisobKitobTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                hisobKitob = newValue;
            }
        });
        hisobKitobTableView.setRowFactory(tv -> new TableRow<HisobKitob>() {
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
    }

    private TableColumn<HisobKitob, DoubleTextBox> valutaKurs() {
        TableColumn<HisobKitob, DoubleTextBox> valutaKurs = new TableColumn<>("Valyuta/Kurs");
        valutaKurs.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, DoubleTextBox>, ObservableValue<DoubleTextBox>>() {

            @Override
            public ObservableValue<DoubleTextBox> call(TableColumn.CellDataFeatures<HisobKitob, DoubleTextBox> param) {
                DecimalFormat decimalFormat = new MoneyShow();
                HisobKitob hisobKitob = param.getValue();
                Valuta valuta = GetDbData.getValuta(hisobKitob.getValuta());
                Text text1 = new Text(valuta.getValuta());
                text1.setFill(Color.GREEN);
                Text text2 = new Text(decimalFormat.format(hisobKitob.getKurs()));
                text2.setFill(Color.BLACK);
                DoubleTextBox b = new DoubleTextBox(text1, text2);
                b.setAlignment(Pos.CENTER);
                b.setMaxWidth(2000);
                b.setPrefWidth(150);
                b.setMaxHeight(2000);
                b.setPrefHeight(20);
                HBox.setHgrow(text1, Priority.ALWAYS);
                VBox.setVgrow(text1, Priority.ALWAYS);
                HBox.setHgrow(text2, Priority.ALWAYS);
                VBox.setVgrow(text2, Priority.ALWAYS);
                HBox.setHgrow(b, Priority.ALWAYS);
                VBox.setVgrow(b, Priority.ALWAYS);

                return new SimpleObjectProperty<DoubleTextBox>(b);
            }
        });

        valutaKurs.setCellFactory(column -> {
            TableCell<HisobKitob, DoubleTextBox> cell = new TableCell<HisobKitob, DoubleTextBox>() {
                @Override
                protected void updateItem(DoubleTextBox item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    } else {
                        setText(null);
                        setGraphic(item);
                    }
                }
            };
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        valutaKurs.setMinWidth(20);
        valutaKurs.setMinWidth(100);
        valutaKurs.setStyle( "-fx-alignment: CENTER;");
        return valutaKurs;
    }
    private TableColumn<HisobKitob, DoubleTextBox> adadNarh() {
        TableColumn<HisobKitob, DoubleTextBox> valutaKurs = new TableColumn<>("Dona/Narh");
        valutaKurs.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, DoubleTextBox>, ObservableValue<DoubleTextBox>>() {

            @Override
            public ObservableValue<DoubleTextBox> call(TableColumn.CellDataFeatures<HisobKitob, DoubleTextBox> param) {
                DecimalFormat decimalFormat = new MoneyShow();
                HisobKitob hisobKitob = param.getValue();
                Text text1 = new Text(decimalFormat.format(hisobKitob.getDona()));
                Text text2 = new Text(decimalFormat.format(hisobKitob.getNarh()));
                text1.setFill(Color.GREEN);
                text2.setFill(Color.BLUE);
                DoubleTextBox b = new DoubleTextBox(text1, text2);
                b.setAlignment(Pos.CENTER);
                b.setMaxWidth(2000);
                b.setPrefWidth(150);
                b.setMaxHeight(2000);
                b.setPrefHeight(20);
                HBox.setHgrow(text1, Priority.ALWAYS);
                VBox.setVgrow(text1, Priority.ALWAYS);
                HBox.setHgrow(text2, Priority.ALWAYS);
                VBox.setVgrow(text2, Priority.ALWAYS);
                HBox.setHgrow(b, Priority.ALWAYS);
                VBox.setVgrow(b, Priority.ALWAYS);

                return new SimpleObjectProperty<DoubleTextBox>(b);
            }
        });

        valutaKurs.setMinWidth(20);
        valutaKurs.setMinWidth(100);
        valutaKurs.setStyle( "-fx-alignment: CENTER;");
        return valutaKurs;
    }
    private TableColumn<HisobKitob, DoubleTextBox> hisob1Hisob2() {
        TableColumn<HisobKitob, DoubleTextBox> hisoblar = new TableColumn<>("Chiqim/Kirim");
        hisoblar.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, DoubleTextBox>, ObservableValue<DoubleTextBox>>() {

            @Override
            public ObservableValue<DoubleTextBox> call(TableColumn.CellDataFeatures<HisobKitob, DoubleTextBox> param) {
                HisobKitob hisobKitob = param.getValue();
                Hisob hisob1= GetDbData.getHisob(hisobKitob.getHisob1());
                Hisob hisob2= GetDbData.getHisob(hisobKitob.getHisob2());
                Text text1 = new Text(hisob1.getText());
                text1.setFill(Color.GREEN);
                text1.setStyle("-fx-text-alignment:justify;");
                text1.wrappingWidthProperty().bind(param.getTableColumn().widthProperty().subtract(2));

                Text text2 = new Text(hisob2.getText());
                text2.setFill(Color.BLUE);
                text2.setStyle("-fx-text-alignment:justify;");
                text2.wrappingWidthProperty().bind(param.getTableColumn().widthProperty().subtract(2));

                DoubleTextBox b = new DoubleTextBox(text1, text2);
                b.setMaxWidth(2000);
                b.setPrefWidth(150);
                b.setMaxHeight(2000);
                b.setPrefHeight(20);
                HBox.setHgrow(text1, Priority.ALWAYS);
                VBox.setVgrow(text1, Priority.ALWAYS);
                HBox.setHgrow(text2, Priority.ALWAYS);
                VBox.setVgrow(text2, Priority.ALWAYS);
                HBox.setHgrow(b, Priority.ALWAYS);
                VBox.setVgrow(b, Priority.ALWAYS);

                return new SimpleObjectProperty<DoubleTextBox>(b);
            }
        });

        hisoblar.setMinWidth(20);
        hisoblar.setMaxWidth(150);
        hisoblar.setStyle( "-fx-alignment: CENTER;");
        return hisoblar;
    }
    private TableColumn<HisobKitob, DoubleTextBox> amalHujjat() {
        TableColumn<HisobKitob, DoubleTextBox> hisoblar = new TableColumn<>("Amal/Hujjat");
        hisoblar.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, DoubleTextBox>, ObservableValue<DoubleTextBox>>() {

            @Override
            public ObservableValue<DoubleTextBox> call(TableColumn.CellDataFeatures<HisobKitob, DoubleTextBox> param) {
                HisobKitob hisobKitob = param.getValue();
                Standart standart= GetDbData.getAmal(hisobKitob.getAmal());
                Integer hujjatId= hisobKitob.getHujjatId();
                Text text1 = new Text(standart.getText());
                text1.setFill(Color.GREEN);
                Text text2 = new Text("№ "+hujjatId);
                text2.setFill(Color.BLUE);
                text2.setOnMouseClicked(event -> {
                    VBox vBox = new VBox();
                    Button ibtolEtButton = new Button("<<");
                    ibtolEtButton.setMaxWidth(2000);
                    ibtolEtButton.setPrefWidth(168);
                    ibtolEtButton.setOnAction(event1 -> {
                        centerPane.getItems().remove(1);
                        centerPane.getItems().addAll(rightPane);
                        leftPane.setDisable(false);
                    });
                    if (event.getClickCount() == 2 && (!text2.getText().isEmpty())) {
                        System.out.println("Bismillah");
                        QaydnomaData qaydnomaData = qaydnomaModel.getQaydnoma(connection, hisobKitob.getQaydId());
                        switch (qaydnomaData.getAmalTuri()) {
                            case 1:
                                PulOldiBerdi pulOldiBerdi = new PulOldiBerdi(connection, user);
                                vBox = pulOldiBerdi.display(qaydnomaData);
                                centerPane.getItems().remove(1);
                                vBox.getChildren().add(ibtolEtButton);
                                centerPane.getItems().add(vBox);
                                leftPane.setDisable(true);
                                break;
                            case 2:
                                TovarOldiBerdi tovarOldiBerdi = new TovarOldiBerdi(connection, user);
                                vBox = tovarOldiBerdi.display(qaydnomaData);
                                centerPane.getItems().remove(1);
                                vBox.getChildren().add(ibtolEtButton);
                                centerPane.getItems().add(vBox);
                                leftPane.setDisable(true);
                                break;
                            case 3:
                                TovarNaqliyotlari tovarNaqliyotlari = new TovarNaqliyotlari(connection, user);
                                vBox = tovarNaqliyotlari.display(qaydnomaData);
                                centerPane.getItems().remove(1);
                                vBox.getChildren().add(ibtolEtButton);
                                centerPane.getItems().add(vBox);
                                leftPane.setDisable(true);
                                break;
                            case 4:
                                XaridlarJadvali xaridlarJadvali = new XaridlarJadvali(connection, user);
                                vBox = xaridlarJadvali.display(qaydnomaData);
                                centerPane.getItems().remove(1);
                                vBox.getChildren().add(ibtolEtButton);
                                centerPane.getItems().add(vBox);
                                leftPane.setDisable(true);
                                break;
                            case 16:
                                PulAyriboshlash pulAyriboshlash = new PulAyriboshlash(connection, user);
                                vBox = pulAyriboshlash.display(qaydnomaData);
                                centerPane.getItems().remove(1);
                                vBox.getChildren().add(ibtolEtButton);
                                centerPane.getItems().add(vBox);
                                leftPane.setDisable(true);
                                break;
                            case 19:
                                ImportController importController = new ImportController(connection, user);
                                vBox = importController.display(qaydnomaData);
                                centerPane.getItems().remove(1);
                                vBox.getChildren().add(ibtolEtButton);
                                centerPane.getItems().add(vBox);
                                leftPane.setDisable(true);
                                break;
                        }
                    }
                });

                DoubleTextBox b = new DoubleTextBox(text1, text2);
                b.setAlignment(Pos.CENTER);
                b.setMaxWidth(2000);
                b.setPrefWidth(150);
                b.setMaxHeight(2000);
                b.setPrefHeight(20);
                HBox.setHgrow(text1, Priority.ALWAYS);
                VBox.setVgrow(text1, Priority.ALWAYS);
                HBox.setHgrow(text2, Priority.ALWAYS);
                VBox.setVgrow(text2, Priority.ALWAYS);
                HBox.setHgrow(b, Priority.ALWAYS);
                VBox.setVgrow(b, Priority.ALWAYS);

                return new SimpleObjectProperty<DoubleTextBox>(b);
            }
        });

        hisoblar.setMinWidth(20);
        hisoblar.setMaxWidth(150);
        hisoblar.setStyle( "-fx-alignment: CENTER;");
        return hisoblar;
    }
    public TableColumn<HisobKitob, Integer> getQaydIdColumn() {
        TableColumn<HisobKitob, Integer> qaydId = new TableColumn<>("Sana");
        qaydId.setMinWidth(100);
        qaydId.setCellValueFactory(new PropertyValueFactory<>("qaydId"));
        qaydId.setCellFactory(column -> {
            TableCell<HisobKitob, Integer> cell = new TableCell<HisobKitob, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        for (QaydnomaData q: qaydnomaDataObservableList) {
                            if (q.getId().equals(item)) {
                                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                                setText(format.format(q.getSana()));
                                break;
                            }
                        }
                    }
                }
            };
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        qaydId.setStyle( "-fx-alignment: CENTER;");
        return qaydId;
    }

    private void initRightPane() {
        rightPane.setPadding(new Insets(padding));
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        VBox.setVgrow(rightPane, Priority.ALWAYS);
        rightPane.getChildren().addAll(hisobToExcelButton, hisobKitobTableView);
    }

    private void initCenterPane() {
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        VBox.setVgrow(centerPane, Priority.ALWAYS);
        centerPane.getItems().addAll(leftPane, rightPane);
    }

    private void initBorderPane() {
        borderpane.setCenter(centerPane);
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Sochma hisobot");
        scene = new Scene(borderpane);
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        stage.setResizable(false);
        stage.setScene(scene);
    }

    private double hisobBalans(int hisobId) {
        Double kirim = 0.0;
        Double chiqim = 0.0;
        Double balans = 0.0;
        ObservableList<HisobKitob> kirimObservableList;
        ObservableList<HisobKitob> chiqimObservableList;
        kirimObservableList = hisobKitobModels.getAnyData(connection, "hisob2 = " + hisobId, "");
        for (HisobKitob k: kirimObservableList) {
            Double jami = (k.getTovar() == 0 ? 1: k.getDona()) * k.getNarh()/k.getKurs();
            kirim += jami;
        }
        chiqimObservableList = hisobKitobModels.getAnyData(connection, "hisob1 = " + hisobId, "");
        for (HisobKitob ch: chiqimObservableList) {
            Double jami = (ch.getTovar() == 0 ? 1: ch.getDona()) * ch.getNarh()/ch.getKurs();
            chiqim +=  jami;
        }
        balans = kirim - chiqim;
        return balans;
    }

    private double hisobBalansYangi(int hisobId) {
        Double kirim = 0.0;
        Double chiqim = 0.0;
        Double balans = 0.0;
        ObservableList<HisobKitob> kirimObservableList;
        ObservableList<HisobKitob> chiqimObservableList;
        kirimObservableList = hisobKitobModels.get_data(connection);
        for (HisobKitob k: kirimObservableList) {
            Double jami = (k.getTovar() == 0 ? 1: k.getDona()) * k.getNarh()/k.getKurs();
            kirim += jami;
        }
        chiqimObservableList = hisobKitobModels.getAnyData(connection, "hisob1 = " + hisobId, "");
        for (HisobKitob ch: chiqimObservableList) {
            Double jami = (ch.getTovar() == 0 ? 1: ch.getDona()) * ch.getNarh()/ch.getKurs();
            chiqim +=  jami;
        }
        balans = kirim - chiqim;
        return balans;
    }

    public TableColumn<HisobKitob, Integer> getCustom2Column() {
        TableColumn<HisobKitob, Integer> integerTableColumn = new TableColumn<>("Kirim/\nChiqim");
        integerTableColumn.setMinWidth(100);
        integerTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, Integer>, ObservableValue<Integer>>() {

            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<HisobKitob, Integer> param) {
                HisobKitob hk = param.getValue();
                hisobKitob = hk;
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
                            if (hisob2==null) {
                                System.out.println("111111111>|>" + hisobKitob.getHisob2());
                            }
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

    public void hkKirimChiqim(Hisob hisob) {
        Double yigindi = .0;
        if (rightObservableList.size()>0) {
            hisobKitob = rightObservableList.get(0);
        }
        for (HisobKitob hk: rightObservableList) {
            if (hk.getHisob1().equals(hisob.getId())) {
                hk.setId(1);
            } else {
                hk.setId(2);
            }
            Integer tovarId = hk.getTovar();
            Double dona = 0d;
            if (tovarId > 0) {
                dona = hk.getDona();
            } else {
                dona = 1d;
            }

            if (hk.getId() == 1) {
                yigindi -= dona * hk.getNarh() / hk.getKurs();
            } else {
                yigindi += dona * hk.getNarh() / hk.getKurs();
            }
            hk.setBalans(yigindi);
        }
    }

    private void initJamiHBox() {
        jamiHBox.setPadding(new Insets(padding));
        HBox.setHgrow(jamiHBox, Priority.ALWAYS);
        Label label = new Label("Jami ");
        Pane pane = new Pane();
        HBox.setHgrow(pane, Priority.ALWAYS);
        label.setFont(font);
        jamiLabel.setFont(font);
        double jami = 0.0;
        for (Hisob h: hisobObservableList) {
            jami += h.getBalans();
        }
        String jamiString = new MoneyShow().format(jami);
        if (jamiString.trim().equals("-0")) {
            jamiString = "0";
        }
        jamiLabel.setText(jamiString);
        jamiHBox.getChildren().addAll(label, pane, jamiLabel);
    }

}
