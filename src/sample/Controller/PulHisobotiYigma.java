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
import sample.Config.MySqlDB;
import sample.Config.MySqlDBLocal;
import sample.Data.*;
import sample.Model.BarCodeModels;
import sample.Model.HisobKitobModels;
import sample.Model.HisobModels;
import sample.Model.Standart2Models;
import sample.Temp.Hisobot2;
import sample.Tools.*;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

    ObservableList<Hisob> hisobObservableList;
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
        connection = new MySqlDBLocal().getDbConnection();
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
        hisobObservableList  = GetDbData.getHisobObservableList();
        for (Hisob h: hisobObservableList) {
            h.setBalans(hisobBalans(h.getId()));
        }
    }

    private void initDataYangi() {
        hisoblarniYangila(localDate);
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
            exportToExcel.hisoblar(hisobObservableList);
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
        TextFields.bindAutoCompletion(qidirTextField, hisobObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Hisob> autoCompletionEvent) -> {
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
        hisobTableView.setItems(hisobObservableList);
        if (hisobObservableList.size()>0) {
            refreshHisobKitobTable(hisobObservableList.get(0), localDate);
            hisobTableView.getSelectionModel().selectFirst();
        }
        hisobTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                refreshHisobKitobTable(newValue, localDate);
            }
        });
    }

    private void refreshHisobKitobTable(Hisob hisob, LocalDate localDate) {
        Hisobot2 hisobot = new Hisobot2(connection,user);
        rightObservableList.removeAll(rightObservableList);
        rightObservableList.addAll(hisobot.getPul(hisob.getId(), localDate));
        hisobKitobTableView.setItems(rightObservableList);
        hisobKitobTableView.refresh();
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
                hisobTableView.setItems(hisobObservableList);
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
        hisobObservableList  = hisobModels.get_data(connection);
        hisobKitobObservableList = hisobKitobModels.getAnyData(connection,"substr(datetime, 1, 10) <= '" + localDateString + "'", "");
        for (HisobKitob hk: hisobKitobObservableList) {
            Hisob chiqimHisobi = GetDbData.hisobniTop(hk.getHisob1(), hisobObservableList);
            Hisob kirimHisobi = GetDbData.hisobniTop(hk.getHisob2(), hisobObservableList);
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
    }
}
