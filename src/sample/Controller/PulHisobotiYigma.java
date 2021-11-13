package sample.Controller;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import sample.Config.MySqlDBGeneral;
import sample.Data.*;
import sample.Enums.ServerType;
import sample.Model.*;
import sample.Temp.Hisobot2;
import sample.Tools.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;

public class PulHisobotiYigma extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    SplitPane centerPane = new SplitPane();
    VBox rightPane = new VBox();
    VBox leftPane = new VBox();
    HBox leftHBox = new HBox();
    DatePicker datePicker;
    LocalDate localDate;

    TextField qidirBarCodeTextField = new TextField();
    TextField tovarNomiTextField = new TextField();
    TextField qidirTextField = new TextField();
    Button hisoblarToExcelButton = new Button("");
    Button hisobToExcelButton = new Button("");

    TableView<Hisob> hisobTableView = new TableView<>();
    TableView<HisobKitob> hisobKitobTableView = new TableView<>();
    GetTableView2 getTableView2 = new GetTableView2();

    ObservableList<Hisob> hisobListAll;
    ObservableList<Hisob> hisobListForTable = FXCollections.observableArrayList();
    ObservableList<HisobKitob> hisobKitobObservableList = FXCollections.observableArrayList();
    ObservableList<HisobKitob> rightObservableList = FXCollections.observableArrayList();
    ObservableList<Balans> balansObservableList = FXCollections.observableArrayList();

    Standart2Models standart2Models = new Standart2Models();
    HisobKitobModels hisobKitobModels = new HisobKitobModels();

    Connection connection;
    User user;

    int padding = 3;


    public static void main(String[] args) {
        launch(args);
    }

    public PulHisobotiYigma() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
        ibtido();
    }

    public PulHisobotiYigma(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        ibtido();
    }

    private void ibtido() {
        initDatePicker();
        initDataYangi();
        initButtons();
        initTextFields();
        initLeftHBox();
        getTableView2.initTableViews();
        initHisobTableView();
        initLeftPane();
        initHisobKitobTable();
        initRightPane();
        initCenterPane();
        initBorderPane();
    }

    private void initData() {
        hisobListAll = GetDbData.getHisobObservableList();
        for (Hisob h: hisobListAll) {
            h.setBalans(hisobBalans(h.getId()));
        }
    }

    private void initDataYangi() {
        hisoblarniYangila2(localDate);
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
        hisoblarToExcelButton.setOnAction(event -> {
            ExportToExcel exportToExcel = new ExportToExcel();
            exportToExcel.hisoblar(hisobListAll);
        });
        hisobToExcelButton.setGraphic(new PathToImageView("/sample/images/Icons/excel.png").getImageView());
        hisobToExcelButton.setOnAction(event -> {
            Hisob hisob = hisobTableView.getSelectionModel().getSelectedItem();
            if (hisob != null) {
                ExportToExcel exportToExcel = new ExportToExcel();
                exportToExcel.hisob(hisob.getId(), rightObservableList);
            }
        });

    }

    private void initTextFields() {
        HBox.setHgrow(qidirTextField, Priority.ALWAYS);
        TextFields.bindAutoCompletion(qidirTextField, hisobListAll).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Hisob> autoCompletionEvent) -> {
            Hisob hisob = autoCompletionEvent.getCompletion();
            hisobTableView.getSelectionModel().select(hisob);
            hisobTableView.scrollTo(hisob);
            hisobTableView.requestFocus();
        });
        HBox.setHgrow(tovarNomiTextField, Priority.ALWAYS);
        HBox.setHgrow(qidirBarCodeTextField, Priority.ALWAYS);
        qidirBarCodeTextField.setPromptText("Shtrix kod");
        qidirBarCodeTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                BarCodeModels barCodeModels = new BarCodeModels();
                String string = qidirBarCodeTextField.getText().trim();
                if (event.getCode()== KeyCode.ENTER) {
                    BarCode barCode = barCodeModels.getBarCode(connection, string);
                    if (barCode !=  null) {
                        HisobKitob hk = getHisobKitob(barCode);
                        if (hk != null) {
                            qidirBarCodeTextField.setText("");
                            hisobKitobTableView.getSelectionModel().select(hk);
                            hisobKitobTableView.scrollTo(hk);
                            hisobKitobTableView.requestFocus();
                            hisobKitobTableView.refresh();
                        } else {
                            Alerts.barCodeNotExist(barCode.getBarCode());
                        }
                    } else {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Diqqat !!!");
                        alert.setHeaderText(string +  "\n shtrix kodga muvofiq tovar topiilmadi" );
                        alert.setContentText("");
                        alert.showAndWait();
                    }
                }
            }
        });

        tovarNomiTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            Taftish(oldValue, newValue);
        });
        tovarNomiTextField.setPromptText("TOVAR NOMI");
    }

    private void initLeftHBox() {
        leftHBox.setPadding(new Insets(padding));
        HBox.setHgrow(leftHBox, Priority.ALWAYS);
        leftHBox.getChildren().addAll(qidirTextField, datePicker, hisoblarToExcelButton);
    }

    private void initRightHBox() {
        leftHBox.setPadding(new Insets(padding));
        HBox.setHgrow(leftHBox, Priority.ALWAYS);
        leftHBox.getChildren().addAll(hisoblarToExcelButton, qidirTextField);
    }

    private void initLeftPane() {
        leftPane.setPadding(new Insets(padding));
        HBox.setHgrow(leftPane, Priority.ALWAYS);
        VBox.setVgrow(leftPane, Priority.ALWAYS);
        leftPane.getChildren().addAll(leftHBox, hisobTableView);
        leftPane.setMinWidth(280);
        leftPane.setMaxWidth(280);

    }

    private void initHisobTableView() {
        hisobTableView = getTableView2.getHisobTableView();
        HBox.setHgrow(hisobTableView, Priority.ALWAYS);
        VBox.setVgrow(hisobTableView, Priority.ALWAYS);
        hisobTableView.getColumns().get(1).setMinWidth(150);
        hisobTableView.getColumns().get(1).setMaxWidth(150);
        hisobTableView.setItems(hisobListForTable);
        if (hisobListForTable.size()>0) {
            refreshHisobKitobTable(hisobListForTable    .get(0), localDate);
            hisobTableView.getSelectionModel().selectFirst();
        }
        hisobTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                refreshHisobKitobTable(newValue, localDate);
            }
        });
    }

    private ObservableList<HisobKitob> pulList3(Integer hisobId, LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String localDateString = localDate.format(formatter);
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        ObservableList<HisobKitob> hisobKitobObservableList = FXCollections.observableArrayList();
        String select =
                "Select valuta, sum(if(hisob2="+hisobId+",narh,0)) as kirim, sum(if(hisob1="+hisobId+",narh,0)) as chiqim from HisobKitob where tovar=0 and valuta>0 and dateTime<='" + localDateString + " 23:59:59' group by valuta";
        ResultSet rs = hisobKitobModels.getResultSet(connection, select);
        try {
            while (rs.next()) {
                Integer id = rs.getInt(1);
                Double kirim = rs.getDouble(2);
                Double chiqim = rs.getDouble(3);
                Double jami = kirim - chiqim;
                Valuta v = GetDbData.getValuta(id);
                HisobKitob hisobKitob = new HisobKitob();
                hisobKitob.setValuta(v.getId());
                hisobKitob.setIzoh(v.getValuta());
                hisobKitob.setNarh(jami);
                hisobKitobObservableList.add(hisobKitob);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hisobKitobObservableList;
    }

    private void refreshHisobKitobTable(Hisob hisob, LocalDate localDate) {
        rightObservableList.removeAll(rightObservableList);
        rightObservableList.addAll(pulList3(hisob.getId(), localDate));
        hisobKitobTableView.setItems(rightObservableList);
        hisobKitobTableView.refresh();
/*
        Hisobot2 hisobot = new Hisobot2(connection,user);
        rightObservableList.removeAll(rightObservableList);
        rightObservableList.addAll(hisobot.getPul(hisob.getId(), localDate));
        hisobKitobTableView.setItems(rightObservableList);
        hisobKitobTableView.refresh();
*/
    }

    private void initHisobKitobTable() {
        hisobKitobTableView.getColumns().addAll(getTableView2.getIzoh2Column(), getTableView2.getAdadColumn(), getTableView2.getNarhColumn());
        HBox.setHgrow(hisobKitobTableView, Priority.ALWAYS);
        VBox.setVgrow(hisobKitobTableView, Priority.ALWAYS);
        hisobKitobTableView.setItems(rightObservableList);
    }

    private void initRightPane() {
        rightPane.setPadding(new Insets(padding));
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        VBox.setVgrow(rightPane, Priority.ALWAYS);
        HBox hBox = new HBox();
        hBox.getChildren().addAll(hisobToExcelButton, tovarNomiTextField, qidirBarCodeTextField);
        rightPane.getChildren().addAll(hBox, hisobKitobTableView);
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
        stage.setTitle("Yig`ma hisobot");
        scene = new Scene(borderpane, 700, 400);
        stage.setScene(scene);
    }

    private double hisobBalans(int hisobId) {
        double kirim = 0.0;
        double chiqim = 0.0;
        double balans = 0.0;
        ObservableList<HisobKitob> kirimObservableList;
        ObservableList<HisobKitob> chiqimObservableList;
        kirimObservableList = hisobKitobModels.getAnyData(connection, "hisob2 = " + hisobId, "");
        for (HisobKitob k: kirimObservableList) {
            double jami = (k.getTovar() == 0 ? 1: k.getDona()) * k.getNarh()/k.getKurs();
            kirim += jami;
        }
        chiqimObservableList = hisobKitobModels.getAnyData(connection, "hisob1 = " + hisobId, "");
        for (HisobKitob ch: chiqimObservableList) {
            double jami = (ch.getTovar() == 0 ? 1: ch.getDona()) * ch.getNarh()/ch.getKurs();
            chiqim +=  jami;
        }
        balans = kirim - chiqim;
        return balans;
    }
    private void Taftish(String oldValue, String newValue) {
        ObservableList<HisobKitob> subentries = FXCollections.observableArrayList();
        newValue = newValue.toLowerCase();

        if ( oldValue != null && (newValue.length() < oldValue.length()) ) {
            hisobKitobTableView.setItems( rightObservableList );
        }

        for ( HisobKitob hk: rightObservableList ) {
            if (hk.getIzoh().toLowerCase().contains(newValue)) {
                subentries.add(hk);
            }
        }
        hisobKitobTableView.setItems(subentries);
        hisobKitobTableView.refresh();
    }

    private HisobKitob getHisobKitob(BarCode bc) {
        HisobKitob hisobKitob = null;
        String barCodeString = bc.getBarCode();
        for (HisobKitob hk : rightObservableList) {
            if (barCodeString.equalsIgnoreCase(hk.getBarCode().trim())) {
                hisobKitob = hk;
                break;
            }
        }
        return hisobKitob;
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
                hisoblarniYangila(localDate);
                hisobTableView.setItems(hisobListForTable);
                hisobTableView.refresh();
                hisobTableView.getSelectionModel().selectFirst();
                Hisob hisob = hisobTableView.getSelectionModel().getSelectedItem();
                hisobTableView.scrollTo(hisob);
                hisobTableView.requestFocus();
                if (hisob != null) {
                    refreshHisobKitobTable(hisob, localDate);
                }
            }
        });
    }

    private void hisoblarniYangila(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String localDateString = localDate.format(formatter);
        HisobModels hisobModels = new HisobModels();
        hisobListAll = hisobModels.get_data(connection);
        hisobKitobObservableList = hisobKitobModels.getAnyData(connection,"substr(datetime, 1, 10) <= '" + localDateString + "'", "");
        for (HisobKitob hk: hisobKitobObservableList) {
            Hisob chiqimHisobi = GetDbData.hisobniTop(hk.getHisob1(), hisobListAll);
            Hisob kirimHisobi = GetDbData.hisobniTop(hk.getHisob2(), hisobListAll);
            if (kirimHisobi == null) {
                System.out.println(hk.getHisob2());
            } else {
                Double chiqimBalans = chiqimHisobi.getBalans();
                Double kirimBalans = kirimHisobi.getBalans();
                Double jami = (hk.getTovar() == 0 ? 1 : hk.getDona()) * hk.getNarh() / hk.getKurs();
                chiqimHisobi.setBalans(chiqimBalans - jami);
                kirimHisobi.setBalans(kirimBalans + jami);
            }
        }
        Standart3Models standart3Models = new Standart3Models();
        standart3Models.setTABLENAME("CheklanganHisobTarkibi");
        ObservableList<Standart3> standart3List = standart3Models.getAnyData(connection, "id2 = " + user.getId(), "");
        hisobListForTable = hisobModels.get_data(connection, standart3List);
        for (Hisob h: hisobListAll) {
            Hisob h1 = GetDbData.hisobniTop(h.getId(), hisobListForTable);
            if (h1!=null) {
                h1.setBalans(h.getBalans());
            }
        }
    }

    private void hisoblarniYangila2(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String localDateString = localDate.format(formatter);
        HisobModels hisobModels = new HisobModels();
        hisobListAll = hisobModels.get_data(connection);
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
                "Select hisob2, sum(narh/kurs) from HisobKitob where tovar=0 and valuta>0 and dateTime<='" + localDateString + " 23:59:59' group by hisob2 order by hisob2";
        ResultSet rs1 = hisobKitobModels.getResultSet(connection, kirimHisoblari);
        try {
            while (rs1.next()) {
                Integer id = rs1.getInt(1);
                Double balance = rs1.getDouble(2);
                Hisob hisob = GetDbData.getHisob(id);
                hisobObservableList.add(new Hisob(id, hisob.getText(), 0d, 0d, balance));
            }
            rs1.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String chiqimHisoblari =
                "Select hisob1, sum(narh/kurs) from HisobKitob where tovar=0 and valuta>0 and dateTime<='" + localDateString + " 23:59:59' group by hisob1 order by hisob1";
        ResultSet rs2 = hisobKitobModels.getResultSet(connection, chiqimHisoblari);
        try {
            while (rs2.next()) {
                Integer id = rs2.getInt(1);
                Double balance = rs2.getDouble(2);
                Hisob hisob = GetDbData.hisobniTop(id, hisobObservableList);
                if (hisob != null) {
                    hisob.setBalans(hisob.getBalans() - balance);
                } else {
                    Hisob h = GetDbData.getHisob(id);
                    hisobObservableList.add(new Hisob(id, h.getText(), 0d, 0d, balance));
                }
            }
            rs2.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (hisobObservableList.size()>0) {
            Comparator<Hisob> comparator = Comparator.comparing(Hisob::getId);
            Collections.sort(hisobObservableList, comparator);
        }
//        hisobObservableList.removeIf(hisob -> yahlitla(hisob.getBalans(), 2) == 0d);
        return hisobObservableList;
    }

    private double yahlitla(double son, int daraja) {
        double darajalanganSon = Math.pow(10, daraja);
        double natija = son/darajalanganSon;
        double roundNatija = Math.round(natija)*darajalanganSon;
        System.out.println(new MoneyShow().format(roundNatija));
        return roundNatija;
    }


}
