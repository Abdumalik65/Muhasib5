package sample.Controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
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
import sample.Model.*;
import sample.Tools.*;
import sample.Tools.Butoq;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ContainerController extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    GridPane gridPane = new GridPane();
    TreeView<Butoq> treeView;
    ToggleGroup toggleGroup;
    VBox centerPane = new VBox();
    Connection connection;
    User user;
    int padding = 3;
    int amalTuri = 19;
    int taqsimUsuli = 1;

    TextArea izohTextArea = new TextArea();
    TextField qaydVaqtiTextField = new TextField();
    TableView<HisobKitob> tovarTableView = new TableView<>();
    TableView<HisobKitob> valutaTableView = new TableView<>();
    TableView<HisobKitob> natijaTableView = new TableView<>();
    TableViewAndoza tableViewAndoza = new TableViewAndoza();

    ObservableList<HisobKitob> tovarTableList = FXCollections.observableArrayList();
    ObservableList<HisobKitob> valutaTableList = FXCollections.observableArrayList();
    ObservableList<HisobKitob> natijaTableList = FXCollections.observableArrayList();
    ObservableList<Standart> narhList = FXCollections.observableArrayList();

    HBoxTextFieldPlusButton hisob1Hbox;
    HBoxTextFieldPlusButton hisob2Hbox;

    Date date = new Date();
    LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    DatePicker qaydSanasiDatePicker;

    Button xaridniYakunlaButton = new Button("Xaridni yakunla");
    Button xaridniBekorQilButton = new Button("Xaridni bekor qil");

    Hisob hisob1;
    Hisob hisob2;
    Hisob tranzitHisob;
    QaydnomaData qaydnomaData = null;

    Font font = Font.font("Arial", FontWeight.BOLD,20);
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    ObservableList<HisobKitob> hisobKitobObservableList = FXCollections.observableArrayList();
    ObservableList<Hisob> hisobObservableList = FXCollections.observableArrayList();
    ObservableList<Valuta> valutaObservableList;

    HisobKitobModels hisobKitobModels = new HisobKitobModels();
    StandartModels standartModels = new StandartModels();
    Standart3Models standart3Models = new Standart3Models();
    Standart4Models standart4Models = new Standart4Models();
    QaydnomaModel qaydnomaModel = new QaydnomaModel();

    Double jamiTovar = 0d;
    Double jamiValuta = 0d;
    Double jamiNatija = 0d;
    Double jamiBalans = 0d;
    Label jamiTovarLabel = new Label();
    Label jamiValutaLabel = new Label();
    Label jamiNatijaLabel = new Label();
    Label jamiBalansLabel = new Label();

    public static void main(String[] args) {
        launch(args);
    }

    public ContainerController() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
    }

    public ContainerController(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        String classSimpleName = getClass().getSimpleName();
        DasturlarRoyxati.dastur(connection, user, classSimpleName);
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
    private void ibtido() {
        initData();
        yangiOngLawha();
        initCenterPane();
        initBorderPane();
    }

    private void yangiOngLawha() {
        treeView = initTreeView();
        SetHVGrow.VerticalHorizontal(treeView);
    }


    private void initData() {
        GetDbData.initData(connection);
        standart3Models.setTABLENAME("hisobGuruhTarkibi");
        hisobObservableList = GetDbData.getHisobObservableList();
        standartModels.setTABLENAME("NarhTuri");
        narhList = standartModels.get_data(connection);
    }

    private void initCenterPane() {
        SetHVGrow.VerticalHorizontal(centerPane);
        centerPane.setPadding(new Insets(padding));
        initYakunlaButton();
        initTovarTable();
        initValutaTable();
        initNatijaTable();
        centerPane.getChildren().addAll(tovarTableView, valutaTableView, natijaTableView, xaridniYakunlaButton);
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
            tovarTableList = tovarRoyxati(hisob1.getId());
            tovarTableView.setItems(tovarTableList);
            tovarTableView.refresh();
            jamiTovar = jami(tovarTableList);
            valutaTableList = pulRoyxati(connection, hisob1.getId());
            jamiValuta = jami(valutaTableList);
            jamiBalans();
            valutaTableView.setItems(valutaTableList);
            valutaTableView.refresh();

            setDisableNodes(false);
        });
        button.setOnAction(event -> {
            hisob1 = addHisob(hisobObservableList);
            if (hisob1 != null) {
                Integer yordamchiHisob = hisobKitobModels.yordamchiHisob(connection, hisob1.getId(), "TranzitHisobGuruhi", "TranzitHisob");
                tranzitHisob = GetDbData.getHisob(yordamchiHisob);
                tovarTableList = tovarRoyxati(hisob1.getId());
                tovarTableView.setItems(tovarTableList);
                tovarTableView.refresh();
                jamiTovar = jami(tovarTableView.getItems());
                refreshValutaTable(hisob1);
                jamiValuta = jami(valutaTableView.getItems());
                jamiBalans();
                textField.setText(hisob1.getText());
                setDisableNodes(false);
            }
        });
    }

    private HBoxTextFieldPlusButton initHisob2HBox() {
        HBoxTextFieldPlusButton hisobHbox = new HBoxTextFieldPlusButton();
        TextField textField = hisobHbox.getTextField();
        textField.setPromptText("Natija hisobi");
        textField.setFont(font);
        Button addButton = hisobHbox.getPlusButton();
        HBox.setHgrow(hisobHbox, Priority.ALWAYS);
        HBox.setHgrow(hisobHbox, Priority.ALWAYS);
        addButton.setMinHeight(37);
        TextFields.bindAutoCompletion(textField, hisobObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Hisob> autoCompletionEvent) -> {
            hisob2 = autoCompletionEvent.getCompletion();
            refreshNatijaTable(hisob2);
            jamiNatija = jami(natijaTableView.getItems());
            jamiBalans();
        });
        addButton.setOnAction(event -> {
            Hisob newValue = addHisob(hisobObservableList);
            if (newValue != null) {
                textField.setText(newValue.getText());
                hisob2 = newValue;
                refreshNatijaTable(hisob2);
                jamiNatija = jami(natijaTableView.getItems());
                jamiBalans();
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
        qaydSanasiDatePicker.setMaxWidth(200);
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
    }

    private void initTovarTable() {
        TableColumn<HisobKitob, String> izohColumn = tableViewAndoza.getIzoh2Column();
        izohColumn.setMinWidth(250);
        izohColumn.setText("Tovarlar");
        tovarTableView.getColumns().addAll(
                izohColumn,
                tableViewAndoza.getAdadColumn(),
                getNarhColumn(),
                getSotishNarhiColumn(),
                getHajmColumn(),
                getVaznColumn(),
                getBojColumn()
        );
        tovarTableView.setEditable(true);
        visibleColumn(1);
        HBox.setHgrow(tovarTableView, Priority.ALWAYS);
        VBox.setVgrow(tovarTableView, Priority.ALWAYS);
        tovarTableView.setItems(tovarTableList);
        jamiTovar = jami(tovarTableList);
        jamiBalans();
    }

    private Double jami(ObservableList<HisobKitob> observableList) {
        Double jami = 0d;
        for (HisobKitob hisobKitob: observableList) {
            jami += hisobKitob.getSummaCol();
        }
        return jami;
    }

    private void disableAll() {
        tovarTableView.getColumns().get(3).setVisible(false);
        tovarTableView.getColumns().get(4).setVisible(false);
        tovarTableView.getColumns().get(5).setVisible(false);
        tovarTableView.getColumns().get(6).setVisible(false);
    }

    private void visibleColumn(Integer i) {
        disableAll();
        switch (i) {
            case 1: //Hajm
                tovarTableView.getColumns().get(4).setVisible(true);
                break;
            case 2: //Vazn
                tovarTableView.getColumns().get(5).setVisible(true);
                break;
            case 3: //Narh
                tovarTableView.getColumns().get(2).setVisible(true);
                break;
            case 4: //Dona
                tovarTableView.getColumns().get(2).setVisible(true);
                break;
            case 5: //Boj
                tovarTableView.getColumns().get(6).setVisible(true);
                break;
        }
    }

    private void initValutaTable() {
        TableColumn<HisobKitob, String> izohColumn = tableViewAndoza.getIzoh2Column();
        izohColumn.setMinWidth(350);
        izohColumn.setText("Xarajatlar");
        valutaTableView.getColumns().addAll(
                izohColumn,
                tableViewAndoza.getNarhColumn()
        );
        HBox.setHgrow(valutaTableView, Priority.ALWAYS);
        VBox.setVgrow(valutaTableView, Priority.ALWAYS);
        valutaTableView.setItems(valutaTableList);
        jamiValuta = jami(valutaTableList);
        jamiBalans();
    }

    private void initNatijaTable() {
        TableColumn<HisobKitob, String> izohColumn = tableViewAndoza.getIzoh2Column();
        izohColumn.setMinWidth(250);
        izohColumn.setText("Tovarlar");
        natijaTableView.getColumns().addAll(
                izohColumn,
                tableViewAndoza.getAdadColumn(),
                getNarhColumn()
        );
        HBox.setHgrow(natijaTableView, Priority.ALWAYS);
        VBox.setVgrow(natijaTableView, Priority.ALWAYS);
    }

    private void refreshValutaTable(Hisob hisob) {
        ObservableList<Valuta> valutaList = hisobKitobModels.getDistinctValuta(connection, hisob.getId(), new Date());
        valutaTableList.removeAll(valutaTableList);
        KursModels kursModels = new KursModels();
        for (Valuta v: valutaList) {
            HisobKitob hk = hisobKitobModels.getValutaBalans(connection, hisob.getId(), v, new Date());
            if (hk.getNarh() != 0.0) {
                hk.setHisob2(tranzitHisob.getId());
                hk.setAmal(1);
                Kurs kurs = kursModels.getKurs(connection, v.getId(),new Date(), "sana desc");
                hk.setKurs(kurs.getKurs());
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
        Date date = getQaydDate();
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
        int i = 0;
        for (HisobKitob hk: tovarTableList) {
            hk.setQaydId(qData.getId());
            hk.setHujjatId(qData.getHujjat());
            hk.setDateTime(qData.getSana());
            Integer id = hisobKitobModels.insert_data(connection, hk);
            HisobKitob natijaHK = natijaTableList.get(i);
            i++;
            natijaHK.setManba(id);
        }
        hisobKitobObservableList.addAll(valutaTableList);
        hisobKitobObservableList.addAll(natijaTableList);
        for (HisobKitob hk: hisobKitobObservableList) {
            hk.setQaydId(qData.getId());
            hk.setHujjatId(qData.getHujjat());
            hk.setDateTime(qData.getSana());
        }
        hisobKitobModels.addBatch(connection, hisobKitobObservableList);
    }

    private void initBorderPane() {
        SetHVGrow.VerticalHorizontal(borderpane);
        borderpane.setCenter(centerPane);
        borderpane.setRight(treeView);
    }
    private VBox taqsimlashTuri() {
        toggleGroup = new ToggleGroup();
        RadioButton radioButton1 = new RadioButton("Hajm");
        RadioButton radioButton2 = new RadioButton("Vazn");
        RadioButton radioButton3 = new RadioButton("Narh");
        RadioButton radioButton4 = new RadioButton("Dona");
        RadioButton radioButton5 = new RadioButton("Bojxona solig`i");
        toggleGroup.getToggles().addAll(radioButton1, radioButton2, radioButton3, radioButton4, radioButton5);
        toggleGroup.selectToggle(radioButton1);
        VBox vBox = new VBox(5);
        HBox.setHgrow(vBox, Priority.ALWAYS);
        VBox.setVgrow(vBox, Priority.ALWAYS);
        vBox.getChildren().addAll(radioButton1, radioButton2, radioButton3, radioButton4, radioButton5);
        radioButton1.setOnAction(event -> {
            taqsimUsuli = 1;
            visibleColumn(taqsimUsuli);
            if (!hisob2Hbox.getTextField().getText().isEmpty()) {
                refreshNatijaTable(hisob2);
                jamiNatija = jami(natijaTableView.getItems());
                jamiBalans();
                tovarTableView.refresh();
            }
        });
        radioButton2.setOnAction(event -> {
            taqsimUsuli = 2;
            visibleColumn(taqsimUsuli);
            if (!hisob2Hbox.getTextField().getText().isEmpty()) {
                refreshNatijaTable(hisob2);
                jamiNatija = jami(natijaTableView.getItems());
                jamiBalans();
                tovarTableView.refresh();
            }
        });
        radioButton3.setOnAction(event -> {
            taqsimUsuli = 3;
            visibleColumn(taqsimUsuli);
            if (!hisob2Hbox.getTextField().getText().isEmpty()) {
                refreshNatijaTable(hisob2);
                jamiNatija = jami(natijaTableView.getItems());
                jamiBalans();
                tovarTableView.refresh();
            }
        });
        radioButton4.setOnAction(event -> {
            taqsimUsuli = 4;
            visibleColumn(taqsimUsuli);
            if (!hisob2Hbox.getTextField().getText().isEmpty()) {
                refreshNatijaTable(hisob2);
                jamiNatija = jami(natijaTableView.getItems());
                jamiBalans();
                tovarTableView.refresh();
            }
        });
        radioButton5.setOnAction(event -> {
            taqsimUsuli = 5;
            visibleColumn(taqsimUsuli);
            if (!hisob2Hbox.getTextField().getText().isEmpty()) {
                refreshNatijaTable(hisob2);
                jamiNatija = jami(natijaTableView.getItems());
                jamiBalans();
                tovarTableView.refresh();
            }
        });
        return vBox;
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        stage.setTitle("Konteyner");
        scene = new Scene(borderpane);
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
                case 3: //narh taqsimoti
                    hvSumma += hk.getDona() * hk.getNarh()/hk.getKurs();
                    break;
                case 4: //dona taqsimoti
                    hvSumma += hk.getDona();
                    break;
                case 5: //boj taqsimoti
                    hvSumma += hk.getDona() * bojSoligi(hk);
                    break;
            }
        }
        for (HisobKitob hk:valutaTableList) {
            valutaSumma += hk.getNarh()/hk.getKurs();
        }
        deltaDouble = valutaSumma/hvSumma;
        return deltaDouble;
    }

    private void cloneNatijaList(Hisob hisob) {
        Double deltaDouble = getDeltaDouble(taqsimUsuli);
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
                    summaDouble = nhk.getNarh()/nhk.getKurs();
                    break;
                case 4: //dona taqsimoti
                    summaDouble = 1d;
                    break;
                case 5: //boj taqsimoti
                    summaDouble = bojSoligi(nhk);
                    break;
            }
            if (summaDouble != 0.00) {
                yangiNarh = summaDouble * deltaDouble + nhk.getNarh()/nhk.getKurs();
                nhk.setNarh(yangiNarh);
            }
            nhk.setValuta(1);
            nhk.setKurs(1d);
            natijaTableList.add(nhk);
        }
    }

    private void jamiBalans() {
        DecimalFormat decimalFormat = new MoneyShow();
        jamiTovarLabel.setText(decimalFormat.format(jamiTovar));
        jamiValutaLabel.setText(decimalFormat.format(jamiValuta));
        jamiNatijaLabel.setText(decimalFormat.format(jamiNatija));
        jamiBalans = jamiTovar + jamiValuta - jamiNatija;
        jamiBalansLabel.setText(decimalFormat.format(jamiBalans));
    }

    private Double bojSoligi(HisobKitob nhk) {
        Double boj = 0d;
        standart4Models.setTABLENAME("BojxonaSoligi");
        Standart4 standart4 = null;
        ObservableList<Standart4> observableList = standart4Models.getAnyData(connection, "tovar = " + nhk.getTovar(), "sana desc");
        if (observableList.size()>0) {
            standart4 = observableList.get(0);
        } else {
            standart4 = new Standart4(0, nhk.getTovar(), new Date(), 0d, user.getId(), null);
        }
        boj = standart4.getMiqdor();
        return boj;
    }

    private void setDisableNodes(Boolean disable) {
        qaydSanasiDatePicker.setDisable(disable);
        qaydVaqtiTextField.setDisable(disable);
        izohTextArea.setDisable(disable);
        xaridniYakunlaButton.setDisable(disable);
        xaridniBekorQilButton.setDisable(disable);
    }

    private TableColumn<HisobKitob, Double> getNarhColumn() {
        TableColumn<HisobKitob, Double> hajmColumn = new TableColumn<>("Tannarh");
        TableColumn<HisobKitob, Double> donaColumn = tableViewAndoza.getNarhColumn();
        donaColumn.setText("Dona");
        TableColumn<HisobKitob, Double> jamiColumn = tableViewAndoza.getSummaColumn();
        jamiColumn.setText("Jami");
        hajmColumn.setStyle( "-fx-alignment: CENTER;");
        hajmColumn.getColumns().addAll(
                donaColumn,
                jamiColumn
        );
        return hajmColumn;
    }

    private TableColumn<HisobKitob, Double> getSotishNarhiColumn() {
        TableColumn<HisobKitob, Double> sotishNarhiColumn = new TableColumn<>("Sotish narhlari");
        TableColumn<HisobKitob, Double> chakanaColumn = tableViewAndoza.getNarhColumn();
        chakanaColumn.setText("Chakana");
        TableColumn<HisobKitob, Double> ulgurjiColumn = tableViewAndoza.getSummaColumn();
        ulgurjiColumn.setText("Ulgurji");
        sotishNarhiColumn.setStyle( "-fx-alignment: CENTER;");
        sotishNarhiColumn.getColumns().addAll(
                chakanaColumn,
                ulgurjiColumn
        );
        return sotishNarhiColumn;
    }

    private TableColumn<HisobKitob, Double> getHajmColumn() {
        TableColumn<HisobKitob, Double> hajmColumn = new TableColumn<>("Hajm");
        hajmColumn.setStyle( "-fx-alignment: CENTER;");
        hajmColumn.getColumns().addAll(
                getHajmDonaColumn(),
                getHajmJamiColumn()
        );
        return hajmColumn;
    }

    private TableColumn<HisobKitob, Double> getHajmDonaColumn() {
        TableColumn<HisobKitob, Double> hajmColumn = new TableColumn<>("Dona");
        hajmColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, Double>, ObservableValue<Double>>() {

            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<HisobKitob, Double> param) {
                HisobKitob hisobKitob = param.getValue();
                BarCode b = GetDbData.getBarCode(hisobKitob.getBarCode());
                Double hajmDouble = .0;
                if (b != null) {
                    hajmDouble = b.getHajm();
                }
                return new SimpleObjectProperty<Double>(hajmDouble);
            }
        });
        hajmColumn.setMinWidth(100);
        hajmColumn.setMaxWidth(100);
        hajmColumn.setStyle( "-fx-alignment: CENTER;");
        hajmColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setMinimumIntegerDigits (1);
                numberFormat.setMaximumIntegerDigits (10);

                numberFormat.setMinimumFractionDigits (1);
                numberFormat.setMaximumFractionDigits (5);
                return numberFormat.format(object);
            }

            @Override
            public Double fromString(String string) {
                string = string.replaceAll(" ", "");
                string = string.replaceAll(",", ".");
                if (!Alerts.isNumericAlert(string)) {
                    string = "0.0";
                }
                return Double.valueOf(string);
            }
        }));
        hajmColumn.setOnEditCommit((TableColumn.CellEditEvent<HisobKitob, Double> event) -> {
            Double newValue = event.getNewValue();
            HisobKitob hisobKitob = event.getRowValue();
            BarCode barCode = GetDbData.getBarCode(hisobKitob.getBarCode());
            if (newValue != null) {
                BarCodeModels barCodeModels = new BarCodeModels();
                barCode.setHajm(newValue);
                barCodeModels.update_data(connection, barCode);
            }
            event.getTableView().refresh();
        });
        return hajmColumn;
    }

    private TableColumn<HisobKitob, Double> getHajmJamiColumn() {
        TableColumn<HisobKitob, Double> hajmColumn = new TableColumn<>("Jami");
        hajmColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, Double>, ObservableValue<Double>>() {

            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<HisobKitob, Double> param) {
                HisobKitob hisobKitob = param.getValue();
                BarCode b = GetDbData.getBarCode(hisobKitob.getBarCode());
                Double hajmDouble = .0;
                if (b != null) {
                    hajmDouble = b.getHajm() * hisobKitob.getDona();
                }
                return new SimpleObjectProperty<Double>(hajmDouble);
            }
        });
        hajmColumn.setMinWidth(100);
        hajmColumn.setMaxWidth(100);
        hajmColumn.setStyle( "-fx-alignment: CENTER;");
        hajmColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setMinimumIntegerDigits (1);
                numberFormat.setMaximumIntegerDigits (10);

                numberFormat.setMinimumFractionDigits (1);
                numberFormat.setMaximumFractionDigits (5);
                return numberFormat.format(object);
            }

            @Override
            public Double fromString(String string) {
                string = string.replaceAll(" ", "");
                string = string.replaceAll(",", ".");
                if (!Alerts.isNumericAlert(string)) {
                    string = "0.0";
                }
                return Double.valueOf(string);
            }
        }));
        return hajmColumn;
    }

    private TableColumn<HisobKitob, Double> getVaznColumn() {
        TableColumn<HisobKitob, Double> vaznColumn = new TableColumn<>("Vazn");
        vaznColumn.setStyle( "-fx-alignment: CENTER;");
        vaznColumn.getColumns().addAll(
                getVaznDonaColumn(),
                getVaznJamiColumn()
        );
        return vaznColumn;
    }

    private TableColumn<HisobKitob, Double> getVaznDonaColumn() {
        TableColumn<HisobKitob, Double> vaznColumn = new TableColumn<>("Dona");
        vaznColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, Double>, ObservableValue<Double>>() {

            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<HisobKitob, Double> param) {
                HisobKitob hisobKitob = param.getValue();
                BarCode b = GetDbData.getBarCode(hisobKitob.getBarCode());
                Double vaznDouble = .0;
                if (b != null) {
                    vaznDouble = b.getVazn();
                }
                return new SimpleObjectProperty<Double>(vaznDouble);
            }
        });
        vaznColumn.setMinWidth(100);
        vaznColumn.setMaxWidth(100);
        vaznColumn.setStyle( "-fx-alignment: CENTER;");
        vaznColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setMinimumIntegerDigits (1);
                numberFormat.setMaximumIntegerDigits (10);

                numberFormat.setMinimumFractionDigits (1);
                numberFormat.setMaximumFractionDigits (5);
                return numberFormat.format(object);
            }

            @Override
            public Double fromString(String string) {
                string = string.replaceAll(" ", "");
                string = string.replaceAll(",", ".");
                if (!Alerts.isNumericAlert(string)) {
                    string = "0.0";
                }
                return Double.valueOf(string);
            }
        }));
        vaznColumn.setOnEditCommit((TableColumn.CellEditEvent<HisobKitob, Double> event) -> {
            Double newValue = event.getNewValue();
            HisobKitob hisobKitob = event.getRowValue();
            BarCode barCode = GetDbData.getBarCode(hisobKitob.getBarCode());
            if (newValue != null) {
                BarCodeModels barCodeModels = new BarCodeModels();
                barCode.setVazn(newValue);
                barCodeModels.update_data(connection, barCode);
            }
            event.getTableView().refresh();
        });
        return vaznColumn;
    }

    private TableColumn<HisobKitob, Double> getVaznJamiColumn() {
        TableColumn<HisobKitob, Double> vaznColumn = new TableColumn<>("Jami");
        vaznColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, Double>, ObservableValue<Double>>() {

            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<HisobKitob, Double> param) {
                HisobKitob hisobKitob = param.getValue();
                BarCode b = GetDbData.getBarCode(hisobKitob.getBarCode());
                Double vaznDouble = .0;
                if (b != null) {
                    vaznDouble = b.getVazn() * hisobKitob.getDona();
                }
                return new SimpleObjectProperty<Double>(vaznDouble);
            }
        });
        vaznColumn.setMinWidth(100);
        vaznColumn.setMaxWidth(100);
        vaznColumn.setStyle( "-fx-alignment: CENTER;");
        vaznColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setMinimumIntegerDigits (1);
                numberFormat.setMaximumIntegerDigits (10);

                numberFormat.setMinimumFractionDigits (1);
                numberFormat.setMaximumFractionDigits (5);
                return numberFormat.format(object);
            }

            @Override
            public Double fromString(String string) {
                string = string.replaceAll(" ", "");
                string = string.replaceAll(",", ".");
                if (!Alerts.isNumericAlert(string)) {
                    string = "0.0";
                }
                return Double.valueOf(string);
            }
        }));
        return vaznColumn;
    }

    private TableColumn<HisobKitob, Double> getBojColumn() {
        TableColumn<HisobKitob, Double> bojColumn = new TableColumn<>("Bojxona solig`i");
        bojColumn.setStyle( "-fx-alignment: CENTER;");
        bojColumn.getColumns().addAll(
                getBojDonaColumn(),
                getBojJamiColumn()
        );
        return bojColumn;
    }

    private TableColumn<HisobKitob, Double> getBojDonaColumn() {
        TableColumn<HisobKitob, Double> bojColumn = new TableColumn<>("Dona");
        bojColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, Double>, ObservableValue<Double>>() {

            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<HisobKitob, Double> param) {
                HisobKitob hisobKitob = param.getValue();
                standart4Models.setTABLENAME("BojxonaSoligi");
                Standart4 standart4 = null;
                ObservableList<Standart4> observableList = standart4Models.getAnyData(connection, "tovar = " + hisobKitob.getTovar(), "sana desc");
                if (observableList.size()>0) {
                    standart4 = observableList.get(0);
                } else {
                    standart4 = new Standart4(0, hisobKitob.getTovar(), new Date(), 0d, user.getId(), null);
                }
                return new SimpleObjectProperty<Double>(standart4.getMiqdor());
            }
        });
        bojColumn.setMinWidth(100);
        bojColumn.setMaxWidth(100);
        bojColumn.setStyle( "-fx-alignment: CENTER;");
        bojColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setMinimumIntegerDigits (1);
                numberFormat.setMaximumIntegerDigits (10);

                numberFormat.setMinimumFractionDigits (1);
                numberFormat.setMaximumFractionDigits (5);
                return numberFormat.format(object);
            }

            @Override
            public Double fromString(String string) {
                string = string.replaceAll(" ", "");
                string = string.replaceAll(",", ".");
                if (!Alerts.isNumericAlert(string)) {
                    string = "0.0";
                }
                return Double.valueOf(string);
            }
        }));

        bojColumn.setOnEditCommit((TableColumn.CellEditEvent<HisobKitob, Double> event) -> {
            Double newValue = event.getNewValue();
            HisobKitob hisobKitob = event.getRowValue();
            Standart4 standart4 = null;
            if (newValue != null) {
                standart4 = new Standart4(0, hisobKitob.getTovar(), new Date(), newValue, user.getId(), null);
                standart4Models.setTABLENAME("BojxonaSoligi");
                standart4Models.insert_data(connection, standart4);
            }
            event.getTableView().refresh();
        });
        return bojColumn;
    }

    private TableColumn<HisobKitob, Double> getBojJamiColumn() {
        TableColumn<HisobKitob, Double> jamiColumn = new TableColumn<>("Jami");
        jamiColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, Double>, ObservableValue<Double>>() {

            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<HisobKitob, Double> param) {
                HisobKitob hisobKitob = param.getValue();
                TableColumn<HisobKitob, Double> tovarDona = (TableColumn<HisobKitob, Double>) param.getTableView().getColumns().get(1);
                TableColumn<HisobKitob, Double> bojDona = (TableColumn<HisobKitob, Double>) param.getTableView().getColumns().get(6).getColumns().get(0);
                Double bojValue = bojDona.getCellObservableValue(hisobKitob).getValue();
                Double tovarValue = tovarDona.getCellObservableValue(hisobKitob).getValue();
                Double jamiDouble = tovarValue * bojValue;
                return new SimpleObjectProperty<Double>(jamiDouble);
            }
        });
        jamiColumn.setMinWidth(100);
        jamiColumn.setMaxWidth(100);
        jamiColumn.setStyle( "-fx-alignment: CENTER;");
        jamiColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setMinimumIntegerDigits (1);
                numberFormat.setMaximumIntegerDigits (10);

                numberFormat.setMinimumFractionDigits (1);
                numberFormat.setMaximumFractionDigits (5);
                return numberFormat.format(object);
            }

            @Override
            public Double fromString(String string) {
                string = string.replaceAll(" ", "");
                string = string.replaceAll(",", ".");
                if (!Alerts.isNumericAlert(string)) {
                    string = "0.0";
                }
                return Double.valueOf(string);
            }
        }));
        return jamiColumn;
    }

    private  TableColumn<HisobKitob, Double> getChakanaNarhColumn() {
        String chakana = narhList.get(0).getText();
        TableColumn<HisobKitob, Double>  chakanaColumn = new TableColumn<>(chakana);
        chakanaColumn.setMinWidth(100);
        chakanaColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, Double>, ObservableValue<Double>>() {

            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<HisobKitob, Double> param) {
                HisobKitob hk = param.getValue();
                Double narhDouble = 0.0;
                Standart6 s6 = narhOl(hk.getTovar());
                if (s6 != null) {
                    narhDouble = s6.getChakana();

                }
                else {
                    TovarNarhi tn = yakkaTovarNarhi(hk.getTovar(), 1);
                    if (tn != null) {
                        narhDouble = tn.getNarh();
                    }
                }
                return new SimpleObjectProperty<Double>(narhDouble);
            }
        });
        chakanaColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setMinimumIntegerDigits (1);
                numberFormat.setMaximumIntegerDigits (10);

                numberFormat.setMinimumFractionDigits (1);
                numberFormat.setMaximumFractionDigits (5);
                return numberFormat.format(object);
            }

            @Override
            public Double fromString(String string) {
                string = string.replaceAll(" ", "");
                string = string.replaceAll(",", ".");
                if (!Alerts.isNumericAlert(string)) {
                    string = "0.0";
                }
                return Double.valueOf(string);
            }
        }));
        chakanaColumn.setOnEditCommit(event -> {
            HisobKitob hk = event.getRowValue();
            Double newValue = event.getNewValue();
            if (newValue != null) {
                narhYoz(hk.getTovar(), 1, newValue/hk.getKurs());
            }
            event.getTableView().getSelectionModel().select(hk);
            event.getTableView().scrollTo(hk);
            event.getTableView().requestFocus();
            event.getTableView().refresh();
        });
        chakanaColumn.setStyle( "-fx-alignment: CENTER;");
        return chakanaColumn;
    }

    private  TableColumn<HisobKitob, Double> getUlgurjiNarhColumn() {
        String ulgurji = narhList.get(1).getText();
        TableColumn<HisobKitob, Double>  ulgurjiColumn = new TableColumn<>(ulgurji);
        ulgurjiColumn.setMinWidth(100);
        ulgurjiColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, Double>, ObservableValue<Double>>() {

            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<HisobKitob, Double> param) {
                HisobKitob hk = param.getValue();
                Double narhDouble = 0.0;
                Standart6 s6 = narhOl(hk.getTovar());
                if (s6 != null) {
                    narhDouble = s6.getUlgurji();

                }
                else {
                    TovarNarhi tn = yakkaTovarNarhi(hk.getTovar(), 2);
                    if (tn != null) {
                        narhDouble = tn.getNarh();
                    }
                }
                return new SimpleObjectProperty<Double>(narhDouble);
            }
        });
        ulgurjiColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setMinimumIntegerDigits (1);
                numberFormat.setMaximumIntegerDigits (10);

                numberFormat.setMinimumFractionDigits (1);
                numberFormat.setMaximumFractionDigits (5);
                return numberFormat.format(object);
            }

            @Override
            public Double fromString(String string) {
                string = string.replaceAll(" ", "");
                string = string.replaceAll(",", ".");
                if (!Alerts.isNumericAlert(string)) {
                    string = "0.0";
                }
                return Double.valueOf(string);
            }
        }));
        ulgurjiColumn.setOnEditCommit(event -> {
            HisobKitob hk = event.getRowValue();
            Double newValue = event.getNewValue();
            if (newValue != null) {
                narhYoz(hk.getTovar(), 2, newValue/hk.getKurs());
            }
            event.getTableView().getSelectionModel().select(hk);
            event.getTableView().scrollTo(hk);
            event.getTableView().requestFocus();
            event.getTableView().refresh();
        });
        ulgurjiColumn.setStyle( "-fx-alignment: CENTER;");
        return ulgurjiColumn;
    }

    public Standart6 narhOl(int tovarId) {
        Standart6 s6 = null;
        Standart6Models standart6Models = new Standart6Models("TGuruh1");
        Standart3Models standart3Models = new Standart3Models();
        standart3Models.setTABLENAME("TGuruh2");
        ObservableList<Standart3> s3List = standart3Models.getAnyData(connection, "id3 = " + tovarId, "");
        if (s3List.size()>0) {
            Standart3 s3 = s3List.get(0);
            s6 = standart6Models.getWithId(connection, s3.getId2());
        }
        return s6;
    }

    public void narhYoz(int tovarId, int narhTuri, Double narhDouble) {
        Standart6Models standart6Models = new Standart6Models("TGuruh1");
        Standart3Models standart3Models = new Standart3Models();
        standart3Models.setTABLENAME("TGuruh2");
        ObservableList<Standart3> s3List = standart3Models.getAnyData(connection, "id3 = " + tovarId, "");
        if (s3List.size()>0) {
            Standart3 s3 = s3List.get(0);
            Standart6 s6 = standart6Models.getWithId(connection, s3.getId2());
            if (s6 != null) {
                switch (narhTuri) {
                    case 0:
                        s6.setNarh(narhDouble);
                        break;
                    case 1:
                        s6.setChakana(narhDouble);
                        break;
                    case 2:
                        s6.setUlgurji(narhDouble);
                        break;
                    case 3:
                        s6.setBoj(narhDouble);
                        break;
                    case 4:
                        s6.setNds(narhDouble);
                        break;
                }
                standart6Models.update_data(connection, s6);
                GuruhNarhModels guruhNarhModels = new GuruhNarhModels();
                GuruhNarh guruhNarh = new GuruhNarh(
                        null, new Date(), s6.getId(), narhTuri, narhDouble, user.getId(), new Date()
                );
                guruhNarhModels.insert_data(connection, guruhNarh);
            }
        } else {
            TovarNarhiModels tovarNarhiModels = new TovarNarhiModels();
            TovarNarhi tovarNarhi = new TovarNarhi(
                    null, date, tovarId, narhTuri, 1, 1d, narhDouble, user.getId(), null
            );
            tovarNarhiModels.insert_data(connection, tovarNarhi);
        }
    }

    private TovarNarhi yakkaTovarNarhi(int tovarId, int narhTuri) {
        TovarNarhi tovarNarhi = null;
        TovarNarhiModels tovarNarhiModels = new TovarNarhiModels();
        ObservableList<TovarNarhi> observableList = tovarNarhiModels.getAnyData(
                connection, "tovar = " + tovarId + " AND narhTuri = " + narhTuri, "id desc"
        );
        if (observableList.size()>0) {
            tovarNarhi = observableList.get(0);
        }
        return tovarNarhi;
    }

    private ObservableList<HisobKitob> tovarList3(Integer hisobId, LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String localDateString = localDate.format(formatter);
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        ObservableList<HisobKitob> hisobKitobObservableList = FXCollections.observableArrayList();

        String select =
                "Select barCode, sum(if(hisob2="+hisobId+",narh*dona/kurs,0)), sum(if(hisob1="+hisobId+",narh*dona/kurs,0)), sum(if(hisob2="+hisobId+",dona,-dona)) from HisobKitob where (hisob1=" + hisobId + " or hisob2=" + hisobId + ") and tovar>0 and dateTime<='" + localDateString + " 23:59:59' group by barCode order by barcode";
        ResultSet rs = hisobKitobModels.getResultSet(connection, select);
        try {
            while (rs.next()) {
                String barCodeString = rs.getString(1);
                Double kirim = rs.getDouble(2);
                Double chiqim = rs.getDouble(3);
                Double dona = rs.getDouble(4);
                BarCode bc = GetDbData.getBarCode(barCodeString);
                Standart tovar = GetDbData.getTovar(bc.getTovar());
                Integer id = tovar.getId();
                Double jami = kirim - chiqim;
                HisobKitob hisobKitob = new HisobKitob();
                hisobKitob.setTovar(tovar.getId());
                hisobKitob.setKurs(1d);
                hisobKitob.setValuta(1);
                hisobKitob.setBarCode(barCodeString);
                hisobKitob.setIzoh(tovar.getText());
                hisobKitob.setNarh(jami);
                hisobKitob.setDona(dona);
                hisobKitob.setHisob1(hisobId);
                hisobKitob.setHisob2(tranzitHisob.getId());
                hisobKitobObservableList.add(hisobKitob);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hisobKitobObservableList;
    }
    private static ObservableList<HisobKitob> pulRoyxati(Connection connection, Integer hisobId) {
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        HisobModels hisobModels = new HisobModels();
        KursModels kursModels = new KursModels();
        Hisob hisob = GetDbData.getHisob(hisobId);
        Hisob keldiKetdiHisobi = hisobModels.keldiKetdiHisobi(connection, hisob);
        ObservableList<HisobKitob> pulLRoyxati = FXCollections.observableArrayList();
        String select = "select valuta, sum(if(hisob1 = " + hisobId + ", -narh, narh)) from HisobKitob where (hisob1 ="+ hisobId +" or hisob2 ="+ hisobId +") and tovar = 0 group by valuta order by valuta";
        ResultSet rs  = hisobKitobModels.getResultSet(connection, select);
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            while (rs.next()) {
                Integer valutaId = rs.getInt(1);
                Double narh = rs.getDouble(2);
                if (StringNumberUtils.yaxlitla(narh, -2) != 0d) {
                    Valuta valuta = GetDbData.getValuta(valutaId);
                    Kurs kurs = kursModels.getKurs(connection, valutaId, new Date(), "sana desc");
                    Double kursDouble = kurs.getKurs();
                    HisobKitob hisobKitob = new HisobKitob(
                            0,
                            0,
                            0,
                            1,
                            hisobId,
                            keldiKetdiHisobi.getId(),
                            valutaId,
                            0,
                            kursDouble,
                            "",
                            0d,
                            narh,
                            0,
                            valuta.getValuta(),
                            1,
                            new Date()
                    );
                    pulLRoyxati.add(hisobKitob);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pulLRoyxati;
    }

    private ObservableList<HisobKitob> tovarRoyxatiM(Connection connection, Integer hisobId) {
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        HisobModels hisobModels = new HisobModels();
        Hisob hisob = GetDbData.getHisob(hisobId);
        HisobKitob hisobKitob1 = null;
        BarCode barCode = null;
        Map<String, HisobKitob> barCodeMap = new HashMap<>();
        Hisob keldiKetdiHisobi = hisobModels.keldiKetdiHisobi(connection, hisob);
        ObservableList<HisobKitob> observableList = FXCollections.observableArrayList();
        String select = "select * from HisobKitob where (hisob1 ="+ hisobId +" or hisob2 ="+ hisobId +") and tovar > 0 order by barCode";
        ResultSet rs  = hisobKitobModels.getResultSet(connection, select);
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            while (rs.next()) {
                Standart tovar = GetDbData.getTovar(rs.getInt(8));
                HisobKitob hisobKitob = new HisobKitob(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getInt(4),
                        rs.getInt(5),
                        rs.getInt(6),
                        rs.getInt(7),
                        rs.getInt(8),
                        rs.getDouble(9),
                        rs.getString(10),
                        rs.getDouble(11),
                        rs.getDouble(12),
                        rs.getInt(13),
                        tovar.getText(),
                        rs.getInt(15),
                        sdf.parse(rs.getString(16))
                );
                barCode = GetDbData.getBarCode(hisobKitob.getBarCode());
                if (barCode != null) {
                    if (barCodeMap.containsKey(hisobKitob.getBarCode())) {
                        hisobKitob1 = barCodeMap.get(hisobKitob.getBarCode());
                    } else {
                        hisobKitob1 = hisobKitobModels.cloneHisobKitob(hisobKitob);
                        hisobKitob1.setQaydId(0);
                        hisobKitob1.setHujjatId(0);
                        hisobKitob1.setHisob1(hisobId);
                        hisobKitob1.setHisob2(keldiKetdiHisobi.getId());
                        hisobKitob1.setValuta(1);
                        hisobKitob1.setKurs(1d);
                        hisobKitob1.setDona(0d);
                        hisobKitob1.setNarh(0d);
                        barCodeMap.put(hisobKitob.getBarCode(), hisobKitob1);
                        observableList.add(hisobKitob1);
                    }
                    Double dona = hisobKitob.getDona();
                    Double narh = dona * hisobKitob.getNarh() / hisobKitob.getKurs();
                    if (hisobKitob.getHisob1().equals(hisobId)) {
                        hisobKitob1.setDona(hisobKitob1.getDona() - dona);
                        hisobKitob1.setBalans(hisobKitob1.getBalans() - narh);
                    } else {
                        hisobKitob1.setDona(hisobKitob1.getDona() + dona);
                        hisobKitob1.setBalans(hisobKitob1.getBalans() + narh);
                    }
                } else {
                    Alerts.AlertString(barCode.getBarCode() + " barcodiga muvofiq tovar topilmadi");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        observableList.forEach(hisobKitob -> hisobKitob.setNarh(hisobKitob.getBalans() / hisobKitob.getDona()));
        observableList.removeIf(hisobKitob -> hisobKitob.getDona().equals(0d));
        return observableList;
    }
    private ObservableList<HisobKitob> tovarRoyxati2(Connection connection, Integer hisobId) {
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        HisobModels hisobModels = new HisobModels();
        Hisob hisob = GetDbData.getHisob(hisobId);
        HisobKitob hisobKitob1 = null;
        BarCode barCode = null;
        Map<String, HisobKitob> barCodeMap = new HashMap<>();
        Hisob keldiKetdiHisobi = hisobModels.keldiKetdiHisobi(connection, hisob);
        ObservableList<HisobKitob> observableList = FXCollections.observableArrayList();
        String select = "select * from HisobKitob where (hisob1 ="+ hisobId +" or hisob2 ="+ hisobId +") and tovar > 0 order by barCode";
        ResultSet rs  = hisobKitobModels.getResultSet(connection, select);
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            while (rs.next()) {
                Standart tovar = GetDbData.getTovar(rs.getInt(8));
                HisobKitob hisobKitob = new HisobKitob(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getInt(4),
                        rs.getInt(5),
                        rs.getInt(6),
                        rs.getInt(7),
                        rs.getInt(8),
                        rs.getDouble(9),
                        rs.getString(10),
                        rs.getDouble(11),
                        rs.getDouble(12),
                        rs.getInt(13),
                        tovar.getText(),
                        rs.getInt(15),
                        sdf.parse(rs.getString(16))
                );
                barCode = GetDbData.getBarCode(hisobKitob.getBarCode());
                if (barCode != null) {
                    if (barCodeMap.containsKey(hisobKitob.getBarCode())) {
                        hisobKitob1 = barCodeMap.get(hisobKitob.getBarCode());
                    } else {
                        hisobKitob1 = hisobKitobModels.cloneHisobKitob(hisobKitob);
                        hisobKitob1.setQaydId(0);
                        hisobKitob1.setHujjatId(0);
                        hisobKitob1.setHisob1(hisobId);
                        hisobKitob1.setHisob2(keldiKetdiHisobi.getId());
                        hisobKitob1.setValuta(1);
                        hisobKitob1.setKurs(1d);
                        hisobKitob1.setDona(0d);
                        hisobKitob1.setNarh(0d);
                        barCodeMap.put(hisobKitob.getBarCode(), hisobKitob1);
                        observableList.add(hisobKitob1);
                    }
                    Double dona = hisobKitob.getDona();
                    Double narh = dona * hisobKitob.getNarh() / hisobKitob.getKurs();
                    if (hisobKitob.getHisob1().equals(hisobId)) {
                        hisobKitob1.setDona(hisobKitob1.getDona() - dona);
                        hisobKitob1.setBalans(hisobKitob1.getBalans() - narh);
                    } else {
                        hisobKitob1.setDona(hisobKitob1.getDona() + dona);
                        hisobKitob1.setBalans(hisobKitob1.getBalans() + narh);
                    }
                } else {
                    Alerts.AlertString(barCode.getBarCode() + " barcodiga muvofiq tovar topilmadi");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        observableList.forEach(hisobKitob -> hisobKitob.setNarh(hisobKitob.getBalans() / hisobKitob.getDona()));
        observableList.removeIf(hisobKitob -> hisobKitob.getDona().equals(0d));
        return observableList;
    }

    private ObservableList<HisobKitob> tovarRoyxati(Integer hisobId) {
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        HisobModels hisobModels = new HisobModels();
        Hisob hisob = GetDbData.getHisob(hisobId);
        Hisob keldiKetdiHisobi = hisobModels.keldiKetdiHisobi(connection, hisob);
        ObservableList<HisobKitob> tovarRoyxati = hisobKitobModels.getAnyData(connection, "hisob2 ="+ hisobId +" and tovar > 0", "tovar");
        Map<Integer, HisobKitob> kirimMap = new HashMap<>();
        tovarRoyxati.forEach(hisobKitob -> {
            hisobKitob.setHisob1(hisobId);
            hisobKitob.setHisob2(keldiKetdiHisobi.getId());
            hisobKitob.setManba(hisobKitob.getId());
            kirimMap.put(hisobKitob.getId(), hisobKitob);
        });
        ObservableList<HisobKitob> chiqimRoyxati = hisobKitobModels.getAnyData(connection, "hisob1 ="+ hisobId +" and tovar > 0", "tovar");
        for (HisobKitob hisobKitob: chiqimRoyxati) {
            if (kirimMap.containsKey(hisobKitob.getManba())) {
                HisobKitob kirimHisobKitob = kirimMap.get(hisobKitob.getManba());
                Double chiqimDona = hisobKitob.getDona();
                Double kirimDona = kirimHisobKitob.getDona();
                kirimHisobKitob.setDona(kirimDona - chiqimDona);
            } else {
                System.out.println(hisobKitob.getId()+"");
            }
        }
        tovarRoyxati.removeIf(hisobKitob -> hisobKitob.getDona().equals(0d));
        return tovarRoyxati;
    }
    private TreeView<Butoq> initTreeView() {
        TreeView<Butoq> treeView = new TreeView<>();
        treeView.setMinWidth(230);
        SetHVGrow.VerticalHorizontal(treeView);
        TreeItem<Butoq> rootTreeItem = new TreeItem(getRootTreeItem());
        rootTreeItem.getChildren().addAll(
                getSozlovTreeItem(),
                sanaVaqtButoq(),
                taqsimotUsuliButoq(),
                jamiButoq(),
                izohButoq()
        );

        treeView.setRoot(rootTreeItem);
        treeView.setShowRoot(false);
        treeView.setMaxWidth(280);
        return treeView;
    }

    private TreeItem<Butoq> getRootTreeItem() {
        Standart standart = new Standart(0, "Asosiy", user.getId(), new Date());
        Butoq butoq = new Butoq(standart);
        TreeItem<Butoq> treeItem = new TreeItem(butoq);
        return treeItem;
    }
    private TreeItem<Butoq> getSozlovTreeItem() {
        Butoq butoq = new Butoq(40, new Label("Savdo sozlovlari"));
        TreeItem<Butoq> treeItem = new TreeItem(butoq);
        Label label = butoq.getLabel();
        label.setFont(font);
        treeItem.getChildren().addAll(
                getHisob1TreeItem(),
                getHisob2TreeItem()
        );
        treeItem.setExpanded(true);
        return treeItem;
    }

    private TreeItem<Butoq> getHisob1TreeItem() {
        initHisob1Hbox();
        Butoq butoq = new Butoq(41, hisob1Hbox);
        TreeItem<Butoq> treeItem = new TreeItem(butoq);
        return treeItem;
    }

    private TreeItem<Butoq> getHisob2TreeItem() {
        hisob2Hbox = initHisob2HBox();
        Butoq butoq = new Butoq(42, hisob2Hbox);
        TreeItem<Butoq> treeItem = new TreeItem(butoq);
        return treeItem;
    }

    private TreeItem<Butoq> sanaVaqtButoq() {
        Butoq butoq = new Butoq(40, new Label("Sana vaqt"));
        TreeItem<Butoq> treeItem = new TreeItem(butoq);
        Label label = butoq.getLabel();
        label.setFont(font);
        treeItem.getChildren().addAll(
                sanaButoq(),
                vaqtButoq()
        );
        treeItem.setExpanded(false);
        return treeItem;
    }

    private TreeItem<Butoq> sanaButoq() {
        initQaydSanasiDatePicker();
        Butoq butoq = new Butoq(qaydSanasiDatePicker);
        TreeItem<Butoq> treeItem = new TreeItem(butoq);
        return treeItem;
    }
    private TreeItem<Butoq> vaqtButoq() {
        initQaydVaqtiTextField();
        Butoq butoq = new Butoq(1, qaydVaqtiTextField);
        TreeItem<Butoq> treeItem = new TreeItem(butoq);
        return treeItem;
    }
    private TreeItem<Butoq> taqsimotUsuliButoq() {
        Butoq butoq = new Butoq(40, new Label("Taqsimot usuli"));
        TreeItem<Butoq> treeItem = new TreeItem(butoq);
        Label label = butoq.getLabel();
        label.setFont(font);
        treeItem.getChildren().addAll(
                tugmachalarButoq()
        );
        treeItem.setExpanded(true);
        return treeItem;
    }
    private TreeItem<Butoq> tugmachalarButoq() {
        VBox taqsimlashTugmachalari = taqsimlashTuri();
        Butoq butoq = new Butoq(taqsimlashTugmachalari);
        TreeItem<Butoq> treeItem = new TreeItem(butoq);
        return treeItem;
    }
    private TreeItem<Butoq> izohButoq() {
        Butoq butoq = new Butoq(40, new Label("Izoh"));
        TreeItem<Butoq> treeItem = new TreeItem(butoq);
        Label label = butoq.getLabel();
        label.setFont(font);
        treeItem.getChildren().addAll(
                eslatmaButoq()
        );
        treeItem.setExpanded(true);
        return treeItem;
    }
    private TreeItem<Butoq> eslatmaButoq() {
        initIzohTextArea();
        Butoq butoq = new Butoq(izohTextArea);
        TreeItem<Butoq> treeItem = new TreeItem(butoq);
        return treeItem;
    }
    private TreeItem<Butoq> jamiButoq() {
        Butoq butoq = new Butoq(40, new Label("Jami"));
        TreeItem<Butoq> treeItem = new TreeItem(butoq);
        Label label = butoq.getLabel();
        label.setFont(font);
        treeItem.getChildren().addAll(
                jamiTovarButoq(),
                jamiValutaButoq(),
                jamiNatijaButoq(),
                balansButoq()
        );
        treeItem.setExpanded(true);
        return treeItem;
    }
    private TreeItem<Butoq> jamiTovarButoq() {
        jamiTovarLabel.setFont(font);
        HBox hBox = yangiHBox("Tovar", jamiTovarLabel);
        Butoq butoq = new Butoq(hBox);
        TreeItem<Butoq> treeItem = new TreeItem(butoq);
        return treeItem;
    }
    private TreeItem<Butoq> jamiValutaButoq() {
        jamiValutaLabel.setFont(font);
        HBox hBox = yangiHBox("Valyuta", jamiValutaLabel);
        Butoq butoq = new Butoq(hBox);
        TreeItem<Butoq> treeItem = new TreeItem(butoq);
        return treeItem;
    }
    private TreeItem<Butoq> jamiNatijaButoq() {
        jamiNatijaLabel.setFont(font);
        HBox hBox = yangiHBox("Natija", jamiNatijaLabel);
        Butoq butoq = new Butoq(hBox);
        TreeItem<Butoq> treeItem = new TreeItem(butoq);
        return treeItem;
    }

    private TreeItem<Butoq> balansButoq() {
        jamiBalansLabel.setFont(font);
        HBox hBox = yangiHBox("Balans", jamiBalansLabel);
        Butoq butoq = new Butoq(hBox);
        TreeItem<Butoq> treeItem = new TreeItem(butoq);
        return treeItem;
    }

    private HBox yangiHBox(String string, Label jamiLabel) {
        HBox hBox = new HBox(5);
        Text text = new Text(string);
        HBox.setHgrow(hBox, Priority.ALWAYS);
        HBox.setHgrow(text, Priority.NEVER);
        hBox.getChildren().addAll(text, jamiLabel);
        return hBox;
    }

}
