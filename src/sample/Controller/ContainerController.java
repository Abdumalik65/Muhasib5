package sample.Controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
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
import sample.Config.MySqlDBLocal;
import sample.Data.*;
import sample.Model.*;
import sample.Tools.*;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ContainerController extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    GridPane gridPane = new GridPane();
    VBox centerPane = new VBox();
    Connection connection;
    User user = new User(1, "admin", "", "admin");
    int padding = 3;
    int amalTuri = 16;
    int containersId = 5;

    TextField hisob1TextField = new TextField();
    TextArea izohTextArea = new TextArea();
    TextField qaydVaqtiTextField = new TextField();
    TableView<HisobKitob> tovarTableView = new TableView<>();
    TableView<HisobKitob> valutaTableView = new TableView<>();
    TableView<HisobKitob> natijaTableView = new TableView<>();
    GetTableView2 getTableView2 = new GetTableView2();
    ComboBox<Standart> standartComboBox = new ComboBox<>();

    ObservableList<HisobKitob> tovarTableList = FXCollections.observableArrayList();
    ObservableList<HisobKitob> valutaTableList = FXCollections.observableArrayList();
    ObservableList<HisobKitob> natijaTableList = FXCollections.observableArrayList();

    HBoxTextFieldPlusButton hisob1Hbox;
    HBoxTextFieldPlusButton hisob2Hbox;

    Date date = new Date();
    LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    DatePicker qaydSanasiDatePicker;
    HBox valuta1Hbox;
    HBox valuta2Hbox;
    DecimalFormat decimalFormat = new MoneyShow();

    TableView<HisobKitob> hisobKitobTableView = new TableView<>();
    Button xaridniYakunlaButton = new Button("Xaridni yakunla");
    Button xaridniBekorQilButton = new Button("Xaridni bekor qil");

    Hisob hisob1;
    Hisob hisob2;
    Hisob tranzitHisob;
    Standart tovar;
    HisobKitob hisobKitob;
    QaydnomaData qaydnomaData = null;

    Font font = Font.font("Arial", FontWeight.BOLD,20);
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    StringBuffer stringBuffer = new StringBuffer();

    ObservableList<HisobKitob> hisobKitobObservableList = FXCollections.observableArrayList();
    ObservableList<Hisob> hisobObservableList = FXCollections.observableArrayList();
    ObservableList<Hisob> hisob2ObservableList = FXCollections.observableArrayList();
    ObservableList<Standart> tovarObservableList;
    ObservableList<Valuta> valutaObservableList;

    HisobKitobModels hisobKitobModels = new HisobKitobModels();
    HisobModels hisobModels = new HisobModels();
    StandartModels standartModels = new StandartModels();
    Standart3Models standart3Models = new Standart3Models();
    QaydnomaModel qaydnomaModel = new QaydnomaModel();

    public static void main(String[] args) {
        launch(args);
    }

    public ContainerController() {}

    public ContainerController(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
    }

    private void ibtido() {
        initData();
        initHisob1Hbox();
        hisob2Hbox = initHisob2HBox();
        initQaydSanasiDatePicker();
        initQaydVaqtiTextField();
        initYakunlaButton();
        initIzohTextArea();
        initTovarTable();
        initValutaTable();
        initComboBox();
        initNatijaTable();
        initGridPane();
        initCenterPane();
        initBorderPane();
        setDisableNodes(true);
    }

    @Override
    public void start(Stage primaryStage) {
        connection = new MySqlDBLocal().getDbConnection();
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
        GetDbData.initData(connection);
        standart3Models.setTABLENAME("hisobGuruhTarkibi");
        ObservableList<Standart3> containers = standart3Models.getAnyData(connection, "id2 = " + containersId, "");
        hisobObservableList = GetDbData.getHisobObservableList();
/*
        for (Standart3 s3: containers) {
            hisobObservableList.add(GetDbData.getHisob(s3.getId3()));
        }
        hisob2ObservableList = hisobModels.get_data(connection);
*/
    }

    private void initCenterPane() {
        SetHVGrow.VerticalHorizontal(centerPane);
        centerPane.setPadding(new Insets(padding));
        centerPane.getChildren().addAll(gridPane, xaridniYakunlaButton);
    }

    private void initHisob1Hbox() {
        hisob1Hbox = new HBoxTextFieldPlusButton();
        SetHVGrow.VerticalHorizontal(hisob1Hbox);
        TextField textField = hisob1Hbox.getTextField();
        textField.setFont(font);
        textField.setPromptText("Chiqim hisobi");
        Button button = hisob1Hbox.getPlusButton();
        Button addButton = new Button();
        button.setMinHeight(37);
        button.setGraphic(new PathToImageView("/sample/images/Icons/add.png").getImageView());
        TextFields.bindAutoCompletion(textField, hisobObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Hisob> autoCompletionEvent) -> {
            hisob1 = autoCompletionEvent.getCompletion();
            Integer yordamchiHisob = hisobKitobModels.yordamchiHisob(connection, hisob1.getId(), "TranzitHisobGuruhi", "TranzitHisob");
            tranzitHisob = GetDbData.getHisob(yordamchiHisob);
            refreshTovarTable(hisob1);
            refreshValutaTable(hisob1);
            setDisableNodes(false);
        });
        button.setOnAction(event -> {
            hisob1 = addHisob(hisobObservableList);
            if (hisob1 != null) {
                Integer yordamchiHisob = hisobKitobModels.yordamchiHisob(connection, hisob1.getId(), "TranzitHisobGuruhi", "TranzitHisob");
                tranzitHisob = GetDbData.getHisob(yordamchiHisob);
                refreshTovarTable(hisob1);
                refreshValutaTable(hisob1);
                textField.setText(hisob1.getText());
                setDisableNodes(false);
            }
        });
    }

    private HBoxTextFieldPlusButton initHisob2HBox() {
        HBoxTextFieldPlusButton hisobHbox = new HBoxTextFieldPlusButton();
        TextField textField = hisobHbox.getTextField();
        textField.setFont(font);
        Button addButton = hisobHbox.getPlusButton();
        HBox.setHgrow(hisobHbox, Priority.ALWAYS);
        HBox.setHgrow(hisobHbox, Priority.ALWAYS);
        addButton.setMinHeight(37);
        TextFields.bindAutoCompletion(textField, hisobObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Hisob> autoCompletionEvent) -> {
            hisob2 = autoCompletionEvent.getCompletion();
            refreshNatijaTable(hisob2);
        });
        addButton.setOnAction(event -> {
            Hisob newValue = addHisob(hisobObservableList);
            if (newValue != null) {
                textField.setText(newValue.getText());
                hisob2 = newValue;
                refreshNatijaTable(hisob2);
            }
        });
        return hisobHbox;
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
        izohTextArea.setWrapText(true);
        izohTextArea.setEditable(true);
        izohTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
            }
        });
    }

    private void initTovarTable() {
        TableColumn<HisobKitob, String> izohColumn = getTableView2.getIzoh2Column();
        izohColumn.setMinWidth(250);
        izohColumn.setText("Tovarlar");
        tovarTableView.getColumns().addAll(izohColumn, getTableView2.getAdadColumn(), getTableView2.getNarhColumn(), getTableView2.getSummaColumn(), getTableView2.getHajmColumn(), getTableView2.getVaznColumn());
        HBox.setHgrow(tovarTableView, Priority.ALWAYS);
        VBox.setVgrow(tovarTableView, Priority.ALWAYS);
        tovarTableView.setItems(tovarTableList);
    }

    private void initValutaTable() {
        TableColumn<HisobKitob, String> izohColumn = getTableView2.getIzoh2Column();
        izohColumn.setMinWidth(350);
        izohColumn.setText("Xarajatlar");
        valutaTableView.getColumns().addAll(izohColumn, getTableView2.getNarhColumn());
        HBox.setHgrow(valutaTableView, Priority.ALWAYS);
        VBox.setVgrow(valutaTableView, Priority.ALWAYS);
        valutaTableView.setItems(valutaTableList);
    }

    private void initComboBox() {
        ObservableList<Standart> comboBoxItems = FXCollections.observableArrayList();
        comboBoxItems.add(new Standart(1, "Hajmga taqsimlash", user.getId(), null));
        comboBoxItems.add(new Standart(2, "Vaznga taqsimlash", user.getId(), null));
        comboBoxItems.add(new Standart(3, "Narhga taqsimlash", user.getId(), null));
        standartComboBox.setMaxWidth(2000);
        standartComboBox.setPrefWidth(150);
        HBox.setHgrow(standartComboBox, Priority.ALWAYS);
        standartComboBox.setItems(comboBoxItems);
        standartComboBox.getSelectionModel().selectFirst();

        standartComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (!hisob2Hbox.getTextField().getText().isEmpty()) {
                    refreshNatijaTable(hisob2);
                }
            }
        });
    }

    private void initNatijaTable() {
        TableColumn<HisobKitob, String> izohColumn = getTableView2.getIzoh2Column();
        izohColumn.setMinWidth(250);
        izohColumn.setText("Tovarlar");
        natijaTableView.getColumns().addAll(izohColumn, getTableView2.getAdadColumn(), getTableView2.getNarhColumn(), getTableView2.getSummaColumn(), getTableView2.getHajmColumn(), getTableView2.getVaznColumn());
        HBox.setHgrow(natijaTableView, Priority.ALWAYS);
        VBox.setVgrow(natijaTableView, Priority.ALWAYS);
    }

    private void refreshValutaTable(Hisob hisob) {
        ObservableList<Valuta> valutaList = hisobKitobModels.getDistinctValuta(connection, hisob.getId(), new Date());
        valutaTableList.removeAll(valutaTableList);
        for (Valuta v: valutaList) {
            HisobKitob hk = hisobKitobModels.getValutaBalans(connection, hisob.getId(), v, new Date());
            if (hk.getNarh() != 0.0) {
                hk.setHisob2(tranzitHisob.getId());
                hk.setAmal(1);
                hk.setKurs(1.0);
                valutaTableList.add(hk);
            }
        }
        valutaTableView.refresh();
    }

    private void refreshTovarTable(Hisob hisob) {
        tovarTableList.removeAll(tovarTableList);
        ObservableList<BarCode> barCodeList = hisobKitobModels.getDistinctBarCode(connection, hisob.getId(), new Date());

        for (BarCode bc: barCodeList) {
            if (bc != null) {
                if (!bc.getBarCode().isEmpty()) {
                    ObservableList<HisobKitob> hkQoldiq = hisobKitobModels.getBarCodeQoldiq(connection, hisob.getId(), bc, new Date());
                    for (HisobKitob hk: hkQoldiq) {
                        if (hk.getNarh() != 0.0) {
                            hk.setHisob1(hisob.getId());
                            hk.setHisob2(tranzitHisob.getId());
                            hk.setManba(hk.getId());
                            tovarTableList.add(hk);
                        }
                    }
                }
            }
        }
        tovarTableView.refresh();
    }

    private void refreshNatijaTable(Hisob hisob) {
        cloneNatijaList(hisob);
        natijaTableView.setItems(natijaTableList);
        natijaTableView.refresh();
    }

    private void initYakunlaButton() {
        xaridniYakunlaButton.setMaxWidth(2000);
        xaridniYakunlaButton.setPrefWidth(150);
        HBox.setHgrow(xaridniYakunlaButton, Priority.ALWAYS);
        xaridniYakunlaButton.setFont(font);
        xaridniYakunlaButton.setOnAction(event -> {
            qaydnomaData = yangiQaydnoma();
            xaridSaqlash(qaydnomaData);
            stage.close();
        });
    }

    private QaydnomaData yangiQaydnoma() {
        int hujjatInt = getQaydnomaNumber();
        String izohString = izohTextArea.getText();
        Double jamiDouble = .0;
        date = getQaydDate();
        QaydnomaData qaydnomaData = new QaydnomaData(null, amalTuri, hujjatInt, date,
                hisob1.getId(), hisob1.getText(), tranzitHisob.getId(), tranzitHisob.getText(),
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
        int i = 0;
        for (HisobKitob hk: tovarTableList) {
            hk.setQaydId(qData.getId());
            hk.setHujjatId(qData.getHujjat());
            hisobKitobModels.insert_data(connection, hk);
            HisobKitob natijaHK = natijaTableList.get(i);
            natijaHK.setManba(hk.getId());
            i++;
        }
        hisobKitobObservableList.addAll(valutaTableList);
        hisobKitobObservableList.addAll(natijaTableList);
        for (HisobKitob hk: hisobKitobObservableList) {
            hk.setQaydId(qData.getId());
            hk.setHujjatId(qData.getHujjat());
        }
        hisobKitobModels.addBatch(connection, hisobKitobObservableList);
    }

    private void initGridPane() {
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
        gridPane.add(tovarTableView, 0, rowIndex, 3, 1);
        GridPane.setHgrow(tovarTableView, Priority.ALWAYS);
        GridPane.setVgrow(tovarTableView, Priority.ALWAYS);

        rowIndex++;
        gridPane.add(valutaTableView, 0, rowIndex, 3, 1);
        GridPane.setHgrow(valutaTableView, Priority.ALWAYS);
        GridPane.setVgrow(valutaTableView, Priority.ALWAYS);

        rowIndex++;
        gridPane.add(hisob2Hbox, 0, rowIndex, 1, 1);
        GridPane.setHgrow(hisob2Hbox, Priority.ALWAYS);
        GridPane.setVgrow(hisob2Hbox, Priority.ALWAYS);

        gridPane.add(standartComboBox, 1, rowIndex, 1, 1);
        GridPane.setHgrow(standartComboBox, Priority.ALWAYS);
        GridPane.setVgrow(standartComboBox, Priority.ALWAYS);

        rowIndex++;
        gridPane.add(natijaTableView, 0, rowIndex, 3, 1);
        GridPane.setHgrow(natijaTableView, Priority.ALWAYS);
        GridPane.setVgrow(natijaTableView, Priority.ALWAYS);
    }

    private void initBorderPane() {
        SetHVGrow.VerticalHorizontal(borderpane);
        borderpane.setCenter(centerPane);
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Valyuta konvertatsiya");
        scene = new Scene(borderpane, 770, 600);
        stage.setScene(scene);
    }

    private Hisob addHisob(ObservableList<Hisob> hisobList) {
        Hisob hisob = null;
        HisobController hisobController = new HisobController();
        hisobController.display(connection, user, hisobList);
        if (hisobController.getDoubleClick()) {
            hisob = hisobController.getDoubleClickedRow();
        }
        return hisob;
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

    public Standart getStandart(int id, ObservableList<Standart> standartObservableList, String tableName) {
        Standart standart = null;
        for (Standart s: standartObservableList) {
            if (s.getId().equals(id)) {
                standart = s;
                break;
            }
        }

        if (standart == null) {
            standartModels.setTABLENAME(tableName);
            standart = standartModels.getDataId(connection, id);
            standartObservableList.add(standart);
        }
        return standart;
    }

    private Double getDeltaDouble(Integer taqsimUsuli) {
        Double valutaSumma = 0.0;
        Double hvSumma = 0.0; // hajm yoki vazn summasi
        Double deltaDouble = 0.0;
        for (HisobKitob hk: tovarTableList) {
            BarCode barCode = GetDbData.getBarCode(hk.getBarCode());
            switch (taqsimUsuli) {
                case 1:  // Hajm taqsimoti
                    hvSumma += hk.getDona() * barCode.getHajm();
                    break;
                case 2: //Vazn taqsimoti
                    hvSumma += hk.getDona() * barCode.getVazn();
                    break;
                case 3: //mablag taqsimoti
                    hvSumma += hk.getDona() * hk.getNarh();
                    break;
            }
        }
        for (HisobKitob hk:valutaTableList) {
            valutaSumma += hk.getNarh();
        }
        deltaDouble = valutaSumma/hvSumma;
        return deltaDouble;
    }

    private void cloneNatijaList(Hisob hisob) {
        Integer taqsimUsuli = standartComboBox.getValue().getId();
        Double deltaDouble = getDeltaDouble(standartComboBox.getValue().getId());
        Double summaDouble;
        Double yangiNarh;
        natijaTableList.removeAll(natijaTableList);
        for (HisobKitob hk: tovarTableList) {
            HisobKitob nhk = hisobKitobModels.cloneHisobKitob(hk);
            nhk.setHisob1(tranzitHisob.getId());
            nhk.setHisob2(hisob.getId());
            summaDouble = 0.00;
            BarCode barCode = GetDbData.getBarCode(nhk.getBarCode());
            switch (taqsimUsuli) {
                case 1:  // Hajm taqsimoti
                    summaDouble = barCode.getHajm();
                    break;
                case 2: //Vazn taqsimoti
                    summaDouble = barCode.getVazn();
                    break;
                case 3: //mablag taqsimoti
                    summaDouble = nhk.getNarh();
                    break;
            }
            if (summaDouble != 0.00) {
                yangiNarh = summaDouble * deltaDouble + nhk.getNarh();
                nhk.setNarh(yangiNarh);
            }
            natijaTableList.add(nhk);
        }
    }

    private void setDisableNodes(Boolean disable) {
        qaydSanasiDatePicker.setDisable(disable);
        qaydVaqtiTextField.setDisable(disable);
        izohTextArea.setDisable(disable);
        xaridniYakunlaButton.setDisable(disable);
        xaridniBekorQilButton.setDisable(disable);
    }
}
