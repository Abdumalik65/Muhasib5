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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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

import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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
    ObservableList<Standart> narhList = FXCollections.observableArrayList();

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
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
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
        standartModels.setTABLENAME("NarhTuri");
        narhList = standartModels.get_data(connection);
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
        tovarTableView.getColumns().addAll(
                izohColumn,
                getTableView2.getAdadColumn(),
                getNarhColumn(),
                getSotishNarhiColumn(),
                getHajmColumn(),
                getVaznColumn()
        );
        tovarTableView.setEditable(true);
        HBox.setHgrow(tovarTableView, Priority.ALWAYS);
        VBox.setVgrow(tovarTableView, Priority.ALWAYS);
        tovarTableView.setItems(tovarTableList);
    }

    private void initValutaTable() {
        TableColumn<HisobKitob, String> izohColumn = getTableView2.getIzoh2Column();
        izohColumn.setMinWidth(350);
        izohColumn.setText("Xarajatlar");
        valutaTableView.getColumns().addAll(
                izohColumn,
                getTableView2.getNarhColumn()
        );
        HBox.setHgrow(valutaTableView, Priority.ALWAYS);
        VBox.setVgrow(valutaTableView, Priority.ALWAYS);
        valutaTableView.setItems(valutaTableList);
    }

    private ObservableList<Standart> initComboBox() {
        ObservableList<Standart> comboBoxItems = FXCollections.observableArrayList();
        comboBoxItems.add(new Standart(1, "Hajmga taqsimlash", user.getId(), null));
        comboBoxItems.add(new Standart(2, "Vaznga taqsimlash", user.getId(), null));
        comboBoxItems.add(new Standart(3, "Narhga taqsimlash", user.getId(), null));
        comboBoxItems.add(new Standart(4, "Donaga taqsimlash", user.getId(), null));
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
        return comboBoxItems;
    }

    private void initNatijaTable() {
        TableColumn<HisobKitob, String> izohColumn = getTableView2.getIzoh2Column();
        izohColumn.setMinWidth(250);
        izohColumn.setText("Tovarlar");
        natijaTableView.getColumns().addAll(
                izohColumn,
                getTableView2.getAdadColumn(),
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
        gridPane.add(standartComboBox, 0, rowIndex, 1, 1);
        GridPane.setHgrow(standartComboBox, Priority.ALWAYS);
        GridPane.setVgrow(standartComboBox, Priority.ALWAYS);

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
                case 3: //mablag taqsimoti
                    hvSumma += hk.getDona() * hk.getNarh()/hk.getKurs();
                    break;
                case 4: //dona taqsimoti
                    hvSumma += hk.getDona();
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
                    summaDouble = nhk.getNarh()/nhk.getKurs();
                    break;
                case 4: //dona taqsimoti
                    summaDouble = 1d;
                    break;
            }
            if (summaDouble != 0.00) {
                yangiNarh = summaDouble * deltaDouble + nhk.getNarh()/nhk.getKurs();
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

    private TableColumn<HisobKitob, Double> getNarhColumn() {
        TableColumn<HisobKitob, Double> hajmColumn = new TableColumn<>("Tannarh");
        TableColumn<HisobKitob, Double> donaColumn = getTableView2.getNarhColumn();
        donaColumn.setText("Dona");
        TableColumn<HisobKitob, Double> jamiColumn = getTableView2.getSummaColumn();
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
        TableColumn<HisobKitob, Double> chakanaColumn = getTableView2.getNarhColumn();
        chakanaColumn.setText("Chakana");
        TableColumn<HisobKitob, Double> ulgurjiColumn = getTableView2.getSummaColumn();
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
                connection, "tovar = " + tovarId + " AND narhTuri = " + narhTuri, "sana desc"
        );
        if (observableList.size()>0) {
            tovarNarhi = observableList.get(0);
        }
        return tovarNarhi;
    }

}
