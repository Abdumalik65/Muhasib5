package sample.Controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import sample.Config.MySqlDB;
import sample.Config.MySqlDBGeneral;
import sample.Data.*;
import sample.Enums.ServerType;
import sample.Model.*;
import sample.Tools.*;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ConvertController extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    GridPane gridPane = new GridPane();
    VBox centerPane = new VBox();
    Connection connection;
    User user = new User(1, "admin", "", "admin");
    int padding = 3;
    int amalTuri = 16;
    Boolean yagonaHisob = false;

    TextField hisob1TextField = new TextField();
    TextField hisob2TextField = new TextField();
    TextArea izohTextArea = new TextArea();
    TextField valuta1TextField = new TextField();
    TextField valuta2TextField = new TextField();
    TextField qaydVaqtiTextField = new TextField();
    TextField valuta1SummaTextField = new TextField("0.00");
    TextField valuta1KursTextField = new TextField("0.00");
    TextField valuta2SummaTextField = new TextField("0.00");
    TextField valuta2KursTextField = new TextField("0.00");

    HBox hisob1Hbox;
    HBox hisob2Hbox;

    HisobKitob chiqimHisobKitob = new HisobKitob();
    HisobKitob kirimHisobKitob = new HisobKitob();

    Date date = new Date();
    LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    DatePicker qaydSanasiDatePicker = new DatePicker();
    HBox valuta1Hbox;
    HBox valuta2Hbox;
    DecimalFormat decimalFormat = new MoneyShow();

    TableView<HisobKitob> hisobKitobTableView = new TableView<>();
    Button xaridniYakunlaButton = new Button("Xaridni yakunla");
    Button xaridniBekorQilButton = new Button("Xaridni bekor qil");

    Hisob hisob1;
    Hisob hisob2;
    Standart tovar;
    Valuta valuta1;
    Valuta valuta2;
    Kurs kurs;
    HisobKitob hisobKitob;
    QaydnomaData qaydnomaData = null;

    Font font = Font.font("Arial", FontWeight.BOLD,20);
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    StringBuffer stringBuffer = new StringBuffer();

    ObservableList<HisobKitob> hisobKitobObservableList = FXCollections.observableArrayList();
    ObservableList<Hisob> hisobObservableList;
    ObservableList<Valuta> valutaObservableList;

    HisobKitobModels hisobKitobModels = new HisobKitobModels();
    HisobModels hisobModels = new HisobModels();
    StandartModels standartModels = new StandartModels();
    KursModels kursModels = new KursModels();
    QaydnomaModel qaydnomaModel = new QaydnomaModel();
    ValutaModels valutaModels = new ValutaModels();

    public static void main(String[] args) {
        launch(args);
    }

    public ConvertController() {
        connection = new MySqlDBGeneral(ServerType.REMOTE).getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
    }

    public ConvertController(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
    }

    public ConvertController(Connection connection, User user, Hisob hisob1) {
        this.connection = connection;
        this.user = user;
        this.hisob1 = hisob1;
        yagonaHisob = true;
        chiqimHisobKitob.setHisob1(hisob1.getId());
        kirimHisobKitob.setHisob2(hisob1.getId());
        Integer yordamchiHisob = hisobKitobModels.yordamchiHisob(connection, hisob1.getId(), "TranzitHisobGuruhi", "TranzitHisob");
        hisob2 = hisobModels.getHisob(connection, yordamchiHisob);
        chiqimHisobKitob.setHisob2(yordamchiHisob);
        kirimHisobKitob.setHisob1(yordamchiHisob);
    }

    private void ibtido() {
        initData();
        initQaydSanasiDatePicker();
        initQaydVaqtiTextField();
        initTextFields();
        initValuta1Hbox();
        initValuta2Hbox();
        initYakunlaButton();
        initIzohTextArea();
        initHisob1Hbox();
        initGridPane();
        initCenterPane();
        initBorderPane();
        if (!yagonaHisob) {
            setDisableNodes(true);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        ibtido();
        initStage(primaryStage);
        stage.show();
        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public QaydnomaData display() {
        stage = new Stage();
        ibtido();
        initStage(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        stage.setOnCloseRequest(event -> {
            stage.close();
        });
        return qaydnomaData;
    }

    private void initData() {
        hisobObservableList = hisobModels.get_data(connection);
        valutaObservableList = valutaModels.get_data(connection);
        chiqimHisobKitob.setAmal(amalTuri);
        chiqimHisobKitob.setUserId(user.getId());
        chiqimHisobKitob.setDateTime(new Date());
        kirimHisobKitob.setAmal(amalTuri);
        kirimHisobKitob.setUserId(user.getId());
        kirimHisobKitob.setDateTime(new Date());
    }

    private void initCenterPane() {
        SetHVGrow.VerticalHorizontal(centerPane);
        centerPane.setPadding(new Insets(padding));
        centerPane.getChildren().addAll(gridPane, xaridniYakunlaButton);
    }

    private void initHisob1Hbox() {
        hisob1Hbox = new HBox();
        HBox.setHgrow(hisob1Hbox, Priority.ALWAYS);
        HBox.setHgrow(hisob1Hbox, Priority.ALWAYS);

        hisob1TextField.setFont(font);
        if (yagonaHisob) {
            hisob1TextField.setEditable(false);
            hisob1TextField.setText(hisob1.getText());
        }
        hisob1TextField.setPromptText("Chiqim hisobi");
        Button addButton = new Button();
        addButton.setMinHeight(37);
        addButton.setGraphic(new PathToImageView("/sample/images/Icons/add.png").getImageView());
        addButton.setDisable(yagonaHisob ? true: false);

        HBox.setHgrow(addButton, Priority.ALWAYS);
        HBox.setHgrow(hisob1TextField, Priority.ALWAYS);
        TextFields.bindAutoCompletion(hisob1TextField, hisobObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Hisob> autoCompletionEvent) -> {
            hisob1 = autoCompletionEvent.getCompletion();
            chiqimHisobKitob.setHisob1(hisob1.getId());
            kirimHisobKitob.setHisob2(hisob1.getId());
            Integer yordamchiHisob = hisobKitobModels.yordamchiHisob(connection, hisob1.getId(), "TranzitHisobGuruhi", "TranzitHisob");
            hisob2 = hisobModels.getHisob(connection, yordamchiHisob);
            chiqimHisobKitob.setHisob2(yordamchiHisob);
            kirimHisobKitob.setHisob1(yordamchiHisob);
            setDisableNodes(false);
        });
        hisob1Hbox.getChildren().addAll(hisob1TextField, addButton);
        addButton.setOnAction(event -> {
            hisob1 = addHisob();
            if (hisob1 != null) {
                hisob1TextField.setText(hisob1.getText());
                chiqimHisobKitob.setHisob1(hisob1.getId());
                kirimHisobKitob.setHisob2(hisob1.getId());
                Integer yordamchiHisob = hisobKitobModels.yordamchiHisob(connection, hisob1.getId(), "TranzitHisobGuruhi", "TranzitHisob");
                hisob2 = hisobModels.getHisob(connection, yordamchiHisob);
                chiqimHisobKitob.setHisob2(yordamchiHisob);
                kirimHisobKitob.setHisob1(yordamchiHisob);
                setDisableNodes(false);
            }
        });
    }

    private void initQaydSanasiDatePicker() {
        // Converter
        StringConverter<LocalDate> converter = new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter =
                    DateTimeFormatter.ofPattern("dd.MM.yyyy");

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

        qaydSanasiDatePicker =  new DatePicker(LocalDate.now());

        qaydSanasiDatePicker.setConverter(converter);
        qaydSanasiDatePicker.setMaxWidth(2000);
        qaydSanasiDatePicker.setPrefWidth(150);
        HBox.setHgrow(qaydSanasiDatePicker, Priority.ALWAYS);
    }

    private void initQaydVaqtiTextField()  {
        qaydVaqtiTextField.setText(sdf.format(date));
        HBox.setHgrow(qaydVaqtiTextField, Priority.ALWAYS);
    }

    private void initIzohTextArea() {
        SetHVGrow.VerticalHorizontal(izohTextArea);
        izohTextArea.setText("Konvertatsiya");
        izohTextArea.setWrapText(true);
        izohTextArea.setEditable(true);
        izohTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                chiqimHisobKitob.setIzoh(newValue.trim());
                kirimHisobKitob.setIzoh(newValue.trim());
            }
        });
    }

    private void initValuta1Hbox() {
        valuta1Hbox = new HBox();
        valuta1TextField.setFont(font);
        valuta1TextField.setPromptText("Kirim valyutasi");
        HBox.setHgrow(valuta1TextField, Priority.ALWAYS);
        HBox.setHgrow(valuta1Hbox, Priority.ALWAYS);
        Button addButton = new Button();
        addButton.setMinHeight(37);
        addButton.setGraphic(new PathToImageView("/sample/images/Icons/add.png").getImageView());
        HBox.setHgrow(valuta1TextField, Priority.ALWAYS);

        TextFields.bindAutoCompletion(valuta1TextField, valutaObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Valuta> autoCompletionEvent) -> {
            valuta1 = autoCompletionEvent.getCompletion();
            if (valuta1 != null) {
                valuta1TextField.setText(valuta1.getValuta());
                Kurs kurs = getKurs(valuta1.getId(), new Date());
                valuta1KursTextField.setText(decimalFormat.format(kurs.getKurs()));
                kirimHisobKitob.setValuta(valuta1.getId());
                kirimHisobKitob.setKurs(kurs.getKurs());
                valuta1SummaTextField.setDisable(false);
                valuta1KursTextField.setDisable(false);
                valuta2Hbox.setDisable(false);
            }
        });

        valuta1Hbox.getChildren().addAll(valuta1TextField, addButton);
        addButton.setOnAction(event -> {
            ValutaController valutaController = new ValutaController();
            valutaController.display(connection, user);
            if (valutaController.getDoubleClick()) {
                valuta1 = valutaController.getDoubleClickedRow();
                valuta1TextField.setText(valuta1.getValuta());
                Kurs kurs = getKurs(valuta1.getId(), new Date());
                valuta1KursTextField.setText(decimalFormat.format(kurs.getKurs()));
                kirimHisobKitob.setValuta(valuta1.getId());
                kirimHisobKitob.setKurs(kurs.getKurs());
                valuta1SummaTextField.setDisable(false);
                valuta1KursTextField.setDisable(false);
                valuta2Hbox.setDisable(false);
            }
        });
    }

    private void initTextFields() {
        valuta1KursTextField.setDisable(true);
        valuta1SummaTextField.setDisable(true);
        valuta1KursTextField.setAlignment(Pos.CENTER_RIGHT);
        valuta1SummaTextField.setAlignment(Pos.CENTER_RIGHT);
        valuta1SummaTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newValue = newValue.trim().replaceAll(" ", "");
                newValue = newValue.trim().replaceAll(",", ".");
                if (!Alerts.isNumericAlert(newValue)) {
                    newValue = oldValue;
                }
                kirimHisobKitob.setNarh(Double.valueOf(newValue));
                hisobla();
            }
        });

        valuta1KursTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newValue = newValue.trim().replaceAll(" ", "");
                newValue = newValue.trim().replaceAll(",", ".");
                if (!Alerts.isNumericAlert(newValue)) {
                    newValue = oldValue;
                }
                kirimHisobKitob.setKurs(Double.valueOf(newValue));
                hisobla();
            }
        });

        valuta2KursTextField.setDisable(true);
        valuta2SummaTextField.setDisable(true);
        valuta2KursTextField.setAlignment(Pos.CENTER_RIGHT);
        valuta2SummaTextField.setAlignment(Pos.CENTER_RIGHT);
        valuta2SummaTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newValue = newValue.trim().replaceAll(" ", "");
                newValue = newValue.trim().replaceAll(",", ".");
                if (!Alerts.isNumericAlert(newValue)) {
                    newValue = oldValue;
                }
                chiqimHisobKitob.setNarh(Double.valueOf(newValue));
                hisobla();
            }
        });

        valuta2KursTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newValue = newValue.trim().replaceAll(" ", "");
                newValue = newValue.trim().replaceAll(",", ".");
                if (!Alerts.isNumericAlert(newValue)) {
                    newValue = oldValue;
                }
                chiqimHisobKitob.setKurs(Double.valueOf(newValue));
                hisobla();
            }
        });
    }

    private void initValuta2Hbox() {
        valuta2Hbox = new HBox();
        valuta2Hbox.setDisable(true);
        valuta2TextField.setFont(font);
        valuta2TextField.setPromptText("Chiqim valyutasi");
        HBox.setHgrow(valuta2TextField, Priority.ALWAYS);
        HBox.setHgrow(valuta2Hbox, Priority.ALWAYS);
        Button addButton = new Button();
        addButton.setMinHeight(37);
        addButton.setGraphic(new PathToImageView("/sample/images/Icons/add.png").getImageView());
        HBox.setHgrow(valuta2TextField, Priority.ALWAYS);

        TextFields.bindAutoCompletion(valuta2TextField, valutaObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Valuta> autoCompletionEvent) -> {
            valuta2 = autoCompletionEvent.getCompletion();
            if (valuta2 != null) {
                valuta2TextField.setText(valuta2.getValuta());
                Kurs kurs = getKurs(valuta2.getId(), new Date());
                valuta2KursTextField.setText(decimalFormat.format(kurs.getKurs()));
                chiqimHisobKitob.setValuta(valuta2.getId());
                chiqimHisobKitob.setKurs(kurs.getKurs());
                valuta2SummaTextField.setDisable(false);
                valuta2KursTextField.setDisable(false);
            }
        });

        valuta2Hbox.getChildren().addAll(valuta2TextField, addButton);
        addButton.setOnAction(event -> {
            ValutaController valutaController = new ValutaController();
            valutaController.display(connection, user);
            if (valutaController.getDoubleClick()) {
                valuta2 = valutaController.getDoubleClickedRow();
                valuta2TextField.setText(valuta2.getValuta());
                Kurs kurs = getKurs(valuta2.getId(), new Date());
                valuta2KursTextField.setText(decimalFormat.format(kurs.getKurs()));
                chiqimHisobKitob.setValuta(valuta2.getId());
                chiqimHisobKitob.setKurs(kurs.getKurs());
                valuta2SummaTextField.setDisable(false);
                valuta2KursTextField.setDisable(false);
            }
        });
    }

    private void initYakunlaButton() {
        xaridniYakunlaButton.setMaxWidth(2000);
        xaridniYakunlaButton.setPrefWidth(150);
        HBox.setHgrow(xaridniYakunlaButton, Priority.ALWAYS);
        xaridniYakunlaButton.setFont(font);
        xaridniYakunlaButton.setOnAction(event -> {
            System.out.println("Bismillah");
            qaydnomaData = qaydnomaSaqlash();
            xaridSaqlash(qaydnomaData);
            stage.close();
        });
    }

    private QaydnomaData qaydnomaSaqlash() {
        int hujjatInt = getQaydnomaNumber();
        String izohString = izohTextArea.getText();
        Double jamiDouble = getJami(hisobKitobObservableList);
        date = getQaydDate();
        QaydnomaData qaydnomaData = new QaydnomaData(null, amalTuri, hujjatInt, date,
                hisob1.getId(), hisob1.getText(), hisob2.getId(), hisob2.getText(),
                izohString, jamiDouble, 0, user.getId(), new Date());
        qaydnomaModel.insert_data(connection, qaydnomaData);
        return qaydnomaData;
    }

    private Date getQaydDate() {
        Date qaydDate = null;
        LocalDateTime localDateTime = LocalDateTime.of(qaydSanasiDatePicker.getValue(), LocalTime.parse(qaydVaqtiTextField.getText()));
        Instant instant = Instant.from(localDateTime.atZone(ZoneId.systemDefault()));
        qaydDate = Date.from(instant);
        return qaydDate;
    }

    private int getQaydnomaNumber() {
        int qaydnomaInt = 1;
        ObservableList<QaydnomaData> qaydList = qaydnomaModel.getAnyData(connection, "amalTuri = " + amalTuri, "hujjat desc");
        if (qaydList.size()>0) {
            qaydnomaInt = qaydList.get(0).getHujjat() + 1;
        }
        return qaydnomaInt;
    }

    private void xaridSaqlash(QaydnomaData qData) {
        hisobKitobObservableList.removeAll(hisobObservableList);
        hisobKitobObservableList.addAll(chiqimHisobKitob, kirimHisobKitob);
        for (HisobKitob hk: hisobKitobObservableList) {
            hk.setQaydId(qData.getId());
            hk.setHujjatId(qData.getHujjat());
            hk.setDateTime(qData.getSana());
        }
        hisobKitobModels.addBatch(connection, hisobKitobObservableList);
    }

    private void initGridPane() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        HBox.setHgrow(gridPane, Priority.ALWAYS);
        int rowIndex = 0;

        gridPane.add(hisob1Hbox, 0, rowIndex, 1, 1);
        GridPane.setHgrow(hisob1Hbox, Priority.ALWAYS);
        gridPane.add(qaydSanasiDatePicker, 1, rowIndex, 1, 1);
        GridPane.setHgrow(qaydSanasiDatePicker, Priority.ALWAYS);
        gridPane.add(qaydVaqtiTextField, 2, rowIndex, 1,1);
        GridPane.setHgrow(qaydVaqtiTextField, Priority.ALWAYS);

        rowIndex++;
        gridPane.add(izohTextArea, 0, rowIndex, 3, 1);
        GridPane.setHgrow(izohTextArea, Priority.ALWAYS);
        GridPane.setVgrow(izohTextArea, Priority.ALWAYS);

        rowIndex++;
        gridPane.add(valuta1Hbox, 0, rowIndex, 1, 1);
        GridPane.setHgrow(valuta1Hbox, Priority.ALWAYS);
        gridPane.add(valuta1KursTextField, 2, rowIndex, 1, 1);
        gridPane.add(valuta1SummaTextField, 1, rowIndex, 1, 1);

        rowIndex++;
        gridPane.add(valuta2Hbox, 0, rowIndex, 1, 1);
        GridPane.setHgrow(valuta2Hbox, Priority.ALWAYS);
        gridPane.add(valuta2KursTextField, 2, rowIndex, 1, 1);
        gridPane.add(valuta2SummaTextField, 1, rowIndex, 1, 1);
    }

    private void initBorderPane() {
        SetHVGrow.VerticalHorizontal(borderpane);
        borderpane.setCenter(centerPane);
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Valyuta konvertatsiya");
        scene = new Scene(borderpane, 600, 300);
        stage.setScene(scene);
    }

    private Hisob addHisob() {
        Hisob hisob = null;
        HisobController hisobController = new HisobController();
        hisobController.display(connection, user);
        if (hisobController.getDoubleClick()) {
            hisob = hisobController.getDoubleClickedRow();
        }
        return hisob;
    }

    private Kurs getKurs(int valutaId, Date sana) {
        Valuta v = getValuta(valutaId);
        Kurs kurs = v.getStatus() == 1 ? new Kurs(1, new Date(), valutaId, 1.0, user.getId(), new Date()) : null;
        ObservableList<Kurs> kursObservableList = null;
        kursObservableList = kursModels.getDate(connection, valutaId, sana, "sana desc");
        if (kursObservableList.size()>0) {
            kurs = kursObservableList.get(0);
        }
        return kurs;
    }

    private Double getJami(ObservableList<HisobKitob> hisobKitobs) {
        Double jamiDouble = .0;
        for (HisobKitob hk: hisobKitobs) {
            int ishora = qaydnomaData.getChiqimId().equals(hk.getHisob1()) ? 1 : -1;
            jamiDouble += ishora*hk.getDona()*hk.getNarh()/hk.getKurs();
        }
        return jamiDouble;
    }

    public QaydnomaData getQaydnomaData() {
        return qaydnomaData;
    }

    public void setQaydnomaData(QaydnomaData qaydnomaData) {
        this.qaydnomaData = qaydnomaData;
    }

    public Valuta getValuta(int id) {
        Valuta valuta = null;
        for (Valuta o : valutaObservableList) {
            if (o.getId().equals(id)) {
                valuta = o;
                break;
            }
        }
        return valuta;
    }

    private void setDisableNodes(Boolean disable) {
        qaydSanasiDatePicker.setDisable(disable);
        qaydVaqtiTextField.setDisable(disable);
        izohTextArea.setDisable(disable);
        valuta1Hbox.setDisable(disable);
        xaridniYakunlaButton.setDisable(disable);
        xaridniBekorQilButton.setDisable(disable);
    }

    private void hisobla() {
        Double kirimKursi = Double.valueOf((valuta1KursTextField.getText().trim().replaceAll(" ", "")));
        Double kirimSummasi = Double.valueOf((valuta1SummaTextField.getText().trim().replaceAll(" ", "")));
        Double chiqimKursi = Double.valueOf((valuta2KursTextField.getText().trim().replaceAll(" ", "")));
        Double chiqimSummasi = Double.valueOf((valuta2SummaTextField.getText().trim().replaceAll(" ", "")));
        chiqimSummasi = kirimSummasi*chiqimKursi/kirimKursi;
        valuta2SummaTextField.setText(decimalFormat.format(chiqimSummasi));
    }
}
