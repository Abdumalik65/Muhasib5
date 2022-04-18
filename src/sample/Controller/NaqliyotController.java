package sample.Controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import sample.Config.MySqlDBGeneral;
import sample.Data.*;
import sample.Enums.ServerType;
import sample.Model.*;
import sample.Tools.*;
import sample.Tools.Butoq;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NaqliyotController extends Application {
    private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");
    Connection connection;
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    TreeView<Butoq> treeView;
    VBox centerPane = new VBox();

    HisobBox hisob1Box;
    HisobBox hisob2Box;
    TovarBox tovarBox;
    ComboBox<Standart> birlikComboBox;

    User user = new User(1, "admin", "", "admin");
    QaydnomaData qaydnomaData = null;

    Label jamiTovarLabel = new Label();
    Label jamiValutaLabel = new Label();
    Label jamiNatijaLabel = new Label();
    Label jamiBalansLabel = new Label();
    TextArea izohTextArea = new TextArea();
    TextField qaydVaqtiTextField = new TextField();
    TextField barCodeTextField = new TextField();

    TableView<HisobKitob> tovarTableView = new TableView<>();
    TableView<HisobKitob> natijaTableView = new TableView<>();
    TableViewAndoza tableViewAndoza = new TableViewAndoza();

    Date date = new Date();
    DatePicker qaydSanasiDatePicker;

    Button xaridniYakunlaButton = new Button("Xaridni yakunla");
    Button xaridniBekorQilButton = new Button("Xaridni bekor qil");

    ObservableList<HisobKitob> tovarTableList = FXCollections.observableArrayList();
    ObservableList<HisobKitob> natijaTableList = FXCollections.observableArrayList();
    ObservableList<Standart> narhList = FXCollections.observableArrayList();
    ObservableList<Standart> birlikObservableList = FXCollections.observableArrayList();
    ObservableList<BarCode> barCodeList = FXCollections.observableArrayList();
    ObservableList<HisobKitob> hisobKitobObservableList = FXCollections.observableArrayList();
    ObservableList<Hisob> hisobObservableList = FXCollections.observableArrayList();

    Font font = Font.font("Arial", FontWeight.BOLD,20);
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    HisobKitobModels hisobKitobModels = new HisobKitobModels();
    StandartModels standartModels = new StandartModels();
    Standart3Models standart3Models = new Standart3Models();
    Standart4Models standart4Models = new Standart4Models();
    QaydnomaModel qaydnomaModel = new QaydnomaModel();

    int padding = 3;
    int amalTuri = 3;
    Double jamiTovar = 0d;
    Double jamiValuta = 0d;
    Double jamiNatija = 0d;
    Double jamiBalans = 0d;

    Map<Integer, Double> qoldiqMap = new HashMap<>();

    public NaqliyotController() {
        connection = new MySqlDBGeneral(ServerType.REMOTE).getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
        user.setTovarHisobi(9);
    }
    public NaqliyotController(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        String classSimpleName = getClass().getSimpleName();
        DasturlarRoyxati.dastur(connection, user, classSimpleName);
    }

    public static void main(String[] args) {
        launch(args);
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
        setDisable(true);
    }

    private void initData() {
        GetDbData.initData(connection);
        standart3Models.setTABLENAME("hisobGuruhTarkibi");
        hisobObservableList = GetDbData.getHisobObservableList();
        standartModels.setTABLENAME("NarhTuri");
        narhList = standartModels.get_data(connection);
    }
    private void yangiOngLawha() {
        treeView = initTreeView();
        SetHVGrow.VerticalHorizontal(treeView);
    }
    private void initCenterPane() {
        SetHVGrow.VerticalHorizontal(centerPane);
        centerPane.setPadding(new Insets(padding));
        initYakunlaButton();
        initTovarTable();
        initNatijaTable();
        centerPane.getChildren().addAll(tovarTableView, natijaTableView, xaridniYakunlaButton);
    }

    private HisobBox initHisob1Box() {
        HisobBox hisobBox = new HisobBox(connection, user);
        TextField textField = hisobBox.getTextField();
        textField.setPromptText("Chiqim hisobi");
        EventHandler<AutoCompletionBinding.AutoCompletionEvent<Hisob>> bindingHandler = new EventHandler<AutoCompletionBinding.AutoCompletionEvent<Hisob>>() {
            @Override
            public void handle(AutoCompletionBinding.AutoCompletionEvent<Hisob> event) {
                Hisob newValue = event.getCompletion();
                if (newValue != null) {
                    hisobBox.setHisob(newValue);
                    System.out.println(hisobBox.getHisob());
                    tovarBoxRefresh();
                    tovarTableView.getItems().removeAll(tovarTableView.getItems());
                    natijaTableView.getItems().removeAll(natijaTableView.getItems());
                    tovarTableView.refresh();
                    natijaTableView.refresh();
                    hisob2Box.setDisable(false);
                    hisob2Box.getTextField().requestFocus();
                }
            }
        };
        AutoCompletionBinding<Hisob> hisobBinding = hisobBox.getHisobBinding();
        hisobBinding.setOnAutoCompleted(bindingHandler);
        Button addButton = hisobBox.getPlusButton();
        addButton.setOnAction(event -> {
            Hisob hisob = addHisob();
            if (hisob != null) {
                hisobBox.setHisob(hisob);
                hisobBox.getTextField().setText(hisob.getText());
                hisob2Box.setDisable(false);
                hisob2Box.getTextField().requestFocus();
            }
        });
        return hisobBox;
    }
    private HisobBox initHisob2Box() {
        HisobBox hisobBox = new HisobBox(connection, user);
        hisobBox.setDisable(true);
        TextField textField = hisobBox.getTextField();
        textField.setPromptText("Kirim hisobi");
        EventHandler<AutoCompletionBinding.AutoCompletionEvent<Hisob>> bindingHandler = new EventHandler<AutoCompletionBinding.AutoCompletionEvent<Hisob>>() {
            @Override
            public void handle(AutoCompletionBinding.AutoCompletionEvent<Hisob> event) {
                Hisob newValue = event.getCompletion();
                if (newValue != null) {
                    hisobBox.setHisob(newValue);
                    System.out.println(hisobBox.getHisob());
                    setDisable(false);
                }
            }
        };
        AutoCompletionBinding<Hisob> hisobBinding = hisobBox.getHisobBinding();
        hisobBinding.setOnAutoCompleted(bindingHandler);
        Button addButton = hisobBox.getPlusButton();
        addButton.setOnAction(event -> {
            Hisob hisob = addHisob();
            if (hisob != null) {
                hisobBox.setHisob(hisob);
                hisobBox.getTextField().setText(hisob.getText());
                setDisable(false);
            }
        });
        return hisobBox;
    }
    private TovarBox initTovarBox() {
        ObservableList<Standart> tovarObservableList=hisobKitobModels.getTovarCount(connection, user.getTovarHisobi(), new Date());
        TovarBox tovarBox = new TovarBox(tovarObservableList, user);
        tovarBox.setDisable(true);
        TextField textField = tovarBox.getTextField();
        textField.setPromptText("Tovar nomi");
        EventHandler<AutoCompletionBinding.AutoCompletionEvent<Standart>> bindingHandler = new EventHandler<AutoCompletionBinding.AutoCompletionEvent<Standart>>() {
            @Override
            public void handle(AutoCompletionBinding.AutoCompletionEvent<Standart> event) {
                Standart newValue = event.getCompletion();
                if (newValue != null) {
                    tovarniYangila(newValue);
                }
            }
        };
        AutoCompletionBinding<Standart> tovarBinding = tovarBox.getTovarBinding();
        tovarBinding.setOnAutoCompleted(bindingHandler);
        Button addButton = tovarBox.getPlusButton();
        addButton.setOnAction(event -> {
            Standart tovar = addTovar();
            if (tovar != null) {
                tovarBox.setTovar(tovar);
                tovarBox.getTextField().setText(tovar.getText());
                tovarniYangila(tovar);
            }
        });
        return tovarBox;
    }
    private ComboBox<Standart> initBirlikComboBox() {
        ComboBox<Standart> birlikComboBox = new ComboBox<>();
        birlikComboBox.setDisable(true);
        standartModels.setTABLENAME("Birlik");
        ObservableList<Standart> birlikObservableList = FXCollections.observableArrayList();
        birlikComboBox.setMaxWidth(2000);
        birlikComboBox.setPrefWidth(150);
        SetHVGrow.VerticalHorizontal(birlikComboBox);
        birlikComboBox.setItems(birlikObservableList);
        if (birlikObservableList.size()>0) {
            birlikComboBox.getSelectionModel().selectFirst();
        }
        birlikComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
            }
        });
        return  birlikComboBox;
    }
    private Standart addTovar() {
        Standart tovar = null;
        TovarController tovarController = new TovarController(connection, user);
        tovarController.display();
        if (tovarController.getDoubleClick()) {
            tovar = tovarController.getDoubleClickedRow();
        }
        return tovar;
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
    private void tovarniYangila(Standart tovar) {
        barCodeList.removeAll(barCodeList);
        barCodeList = GetDbData.getBarCodeList(tovar.getId());
        if (barCodeList.size()>0) {
            barCodeTextField.setText(barCodeList.get(0).getBarCode());
            birlikObservableList.removeAll(birlikObservableList);
            for (BarCode bc : barCodeList) {
                Standart birlik = GetDbData.getBirlik(bc.getBirlik());
                if (birlik != null) {
                    birlikObservableList.add(birlik);
                }
            }
            birlikComboBox.setItems(birlikObservableList);
            if (birlikObservableList.size() > 0) {
                birlikComboBox.getSelectionModel().selectFirst();
            }
            barCodeTextField.setText(barCodeList.get(0).getBarCode());
            barCodeTextField.requestFocus();
        }
    }
    private ObservableList<HisobKitob> tovarRoyxati(Integer hisobId, String bcString) {
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        HisobModels hisobModels = new HisobModels();
        Hisob hisob = GetDbData.getHisob(hisobId);
        Hisob keldiKetdiHisobi = hisobModels.keldiKetdiHisobi(connection, hisob);
        bcString = bcString.trim();
        ObservableList<HisobKitob> tovarRoyxati = hisobKitobModels.getAnyData(connection, "hisob2 ="+ hisobId +" and barCode = '" + bcString +"' and tovar > 0", "tovar");
        if (tovarRoyxati.size()>0) {
            Map<Integer, HisobKitob> kirimMap = new HashMap<>();
            for (HisobKitob hisobKitob: tovarRoyxati) {
                kirimMap.put(hisobKitob.getId(), hisobKitob);
            }
            ObservableList<HisobKitob> chiqimRoyxati = hisobKitobModels.getAnyData(connection, "hisob1 =" + hisobId + " and barCode = '" + bcString + "' and tovar > 0", "tovar");
            for (HisobKitob hisobKitob : chiqimRoyxati) {
                if (kirimMap.containsKey(hisobKitob.getManba())) {
                    HisobKitob kirimHisobKitob = kirimMap.get(hisobKitob.getManba());
                    Double chiqimDona = hisobKitob.getDona();
                    Double kirimDona = kirimHisobKitob.getDona();
                    kirimHisobKitob.setDona(kirimDona - chiqimDona);
                } else {
                    System.out.println(hisobKitob.getId() + "");
                }
            }
            tovarRoyxati.removeIf(hisobKitob -> hisobKitob.getDona().equals(0d));
        }
        return tovarRoyxati;
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
        qaydSanasiDatePicker.setDisable(true);
        qaydSanasiDatePicker.setConverter(converter);
        qaydSanasiDatePicker.setMaxWidth(200);
        HBox.setHgrow(qaydSanasiDatePicker, Priority.ALWAYS);
    }
    private void initQaydVaqtiTextField()  {
        qaydVaqtiTextField.setText(sdf.format(date));
        qaydVaqtiTextField.setDisable(true);
        HBox.setHgrow(qaydVaqtiTextField, Priority.ALWAYS);
    }
    private void initIzohTextArea() {
        SetHVGrow.VerticalHorizontal(izohTextArea);
        izohTextArea.setWrapText(true);
        izohTextArea.setMaxWidth(200);
        izohTextArea.setEditable(true);
        izohTextArea.setDisable(true);
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
    private void initBorderPane() {
        SetHVGrow.VerticalHorizontal(borderpane);
        borderpane.setCenter(centerPane);
        borderpane.setRight(treeView);
    }
    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        stage.setTitle("Naqliyot");
        scene = new Scene(borderpane);
        stage.setScene(scene);
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
    public QaydnomaData getQaydnomaData() {
        return qaydnomaData;
    }
    public void setQaydnomaData(QaydnomaData qaydnomaData) {
        this.qaydnomaData = qaydnomaData;
    }
    private QaydnomaData yangiQaydnoma() {
        Hisob hisob1 = hisob1Box.getHisob();
        Hisob hisob2 = hisob2Box.getHisob();
        int hujjatInt = getQaydnomaNumber();
        String izohString = izohTextArea.getText();
        Double jamiDouble = .0;
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
        ObservableList<HisobKitob> naqliyotRoyxati = natijaTableView.getItems();
        hisobKitobObservableList.removeAll(hisobObservableList);
        int i = 0;
        for (HisobKitob hk: naqliyotRoyxati) {
            hk.setQaydId(qData.getId());
            hk.setHujjatId(qData.getHujjat());
            hk.setHisob1(hisob1Box.getHisob().getId());
            hk.setHisob2(hisob2Box.getHisob().getId());
            hk.setDateTime(qData.getSana());
            hk.setUserId(user.getId());
            hisobKitobObservableList.add(hk);
        }
        hisobKitobModels.addBatch(connection, hisobKitobObservableList);
    }
    private void jamiBalans() {
        DecimalFormat decimalFormat = new MoneyShow();
        jamiTovarLabel.setText(decimalFormat.format(jamiTovar));
        jamiValutaLabel.setText(decimalFormat.format(jamiValuta));
        jamiNatijaLabel.setText(decimalFormat.format(jamiNatija));
        jamiBalans = jamiTovar + jamiValuta - jamiNatija;
        jamiBalansLabel.setText(decimalFormat.format(jamiBalans));
    }
    private void setDisableNodes(Boolean disable) {
        qaydSanasiDatePicker.setDisable(disable);
        qaydVaqtiTextField.setDisable(disable);
        izohTextArea.setDisable(disable);
        xaridniYakunlaButton.setDisable(disable);
        xaridniBekorQilButton.setDisable(disable);
    }

    private void initTovarTable() {
        TableColumn<HisobKitob, Integer> tovarColumn = tableViewAndoza.getTovarColumn();
        TableColumn<HisobKitob, DoubleTextBox> hisob1hisob2 = tableViewAndoza.hisob1Hisob2();
        hisob1hisob2.setMinWidth(150);
        tovarColumn.setMinWidth(250);
        tovarTableView.getColumns().addAll(
                tableViewAndoza.getDateTimeColumn(),
                hisob1hisob2,
                tableViewAndoza.getIzoh2Column(),
                tableViewAndoza.getAdadColumn(),
                tableViewAndoza.getNarhColumn(),
                getStatus()
        );
        HBox.setHgrow(tovarTableView, Priority.ALWAYS);
        VBox.setVgrow(tovarTableView, Priority.ALWAYS);
        tovarTableView.setItems(tovarTableList);
        jamiTovar = jami(tovarTableList);
        jamiBalans();

        tovarTableView.setRowFactory(tv -> {
            TableRow<HisobKitob> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                System.out.println(event.getClickCount());
                if (event.getClickCount() == 2 && (! row.isEmpty())) {
                    System.out.println("Kirdik");
                    donaQosh(row.getTableView().getSelectionModel().getSelectedItem());
                }
            });


/*
            row.setOnDragDetected(event -> {
                if (! row.isEmpty()) {
                    Integer index = row.getIndex();
                    Dragboard db = row.startDragAndDrop(TransferMode.COPY);
                    db.setDragView(row.snapshot(null, null));
                    ClipboardContent cc = new ClipboardContent();
                    cc.put(SERIALIZED_MIME_TYPE, index);
                    db.setContent(cc);
                    event.consume();
                }
            });
*/

/*            row.setOnDragOver(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    if (row.getIndex() != ((Integer)db.getContent(SERIALIZED_MIME_TYPE)).intValue()) {
                        event.acceptTransferModes(TransferMode.COPY);
                        event.consume();
                    }
                }
            });

            row.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    int draggedIndex = (Integer) db.getContent(SERIALIZED_MIME_TYPE);
                    HisobKitob draggedItem = tovarTableView.getItems().remove(draggedIndex);

                    int dropIndex ;

                    if (row.isEmpty()) {
                        dropIndex = tovarTableView.getItems().size() ;
                    } else {
                        dropIndex = row.getIndex();
                    }

                    tovarTableView.getItems().add(dropIndex, draggedItem);

                    event.setDropCompleted(true);
//                    tovarTableView.getSelectionModel().select(dropIndex);
                    event.consume();
                }
            });
*/

            return row ;
        });
    }
    private void initNatijaTable() {
        TableColumn<HisobKitob, Integer> tovarColumn = tableViewAndoza.getTovarColumn();
        TableColumn<HisobKitob, DoubleTextBox> hisob1hisob2 = tableViewAndoza.hisob1Hisob2();
        hisob1hisob2.setMinWidth(150);
        tovarColumn.setMinWidth(250);
        natijaTableView.getColumns().addAll(
                tableViewAndoza.getDateTimeColumn(),
                hisob1hisob2,
                tovarColumn,
                getAdadColumn(),
                tableViewAndoza.getNarhColumn()
        );
        natijaTableView.setEditable(true);
        HBox.setHgrow(natijaTableView, Priority.ALWAYS);
        VBox.setVgrow(natijaTableView, Priority.ALWAYS);
        natijaTableView.setItems(natijaTableList);

        natijaTableList.addListener(new ListChangeListener<HisobKitob>() {
            @Override
            public void onChanged(Change<? extends HisobKitob> c) {
                if (c.getList().size() == 0) {
                    xaridniYakunlaButton.setDisable(true);
                }

                if (c.getList().size() > 0) {
                    xaridniYakunlaButton.setDisable(false);
                }
            }
        });
/*
        natijaTableView.setOnDragOver(event -> {
            System.out.println("natijaTableView.setOnDragOver");
            Dragboard db = event.getDragboard();
            if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                System.out.println("SERIALIZED_MIME_TYPE");
                event.acceptTransferModes(TransferMode.COPY);
                event.consume();
            }
        });
*/

/*
        natijaTableView.setOnDragDropped(table ->{
            System.out.println("setOnDragDropped");
            Dragboard db = table.getDragboard();
            if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                int draggedIndex = (Integer) db.getContent(SERIALIZED_MIME_TYPE);
                HisobKitob draggedItem = tovarTableView.getItems().remove(draggedIndex);
                HisobKitob kochganTovar = hisobKitobModels.cloneHisobKitob(draggedItem);
                Integer hisob1Id = hisob1Box.getHisob().getId();
                Integer hisob2Id = hisob2Box.getHisob().getId();
                kochganTovar.setHisob1(hisob1Id);
                kochganTovar.setHisob2(hisob2Id);
                kochganTovar.setManba(draggedItem.getId());
                kochganTovar.setDateTime(getQaydDate());
                draggedItem.setDona(0d);

                int dropIndex ;
                dropIndex = natijaTableView.getItems().size() ;
                natijaTableView.getItems().add(dropIndex, kochganTovar);

                table.setDropCompleted(true);
                natijaTableView.getSelectionModel().select(dropIndex);
                tovarTableView.refresh();
                natijaTableView.refresh();
                table.consume();
            }

        });
*/

        natijaTableView.setRowFactory(tv -> {
            TableRow<HisobKitob> row = new TableRow<>();
/*
            row.setOnDragOver(event -> {
                System.out.println("setOnDragOver");
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    if (row.getIndex() != ((Integer)db.getContent(SERIALIZED_MIME_TYPE)).intValue()) {
                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                        event.consume();
                    }
                }
            });

            row.setOnDragOver(event -> {
                System.out.println("setOnDragOver");
            });

            row.setOnDragDetected(event -> {
                System.out.println("setOnDragOver");
            });

            row.setOnDragDropped(event -> {
                System.out.println("setOnDragDropped");
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    int draggedIndex = (Integer) db.getContent(SERIALIZED_MIME_TYPE);
                    HisobKitob draggedItem = tovarTableView.getItems().remove(draggedIndex);
                    HisobKitob kochganTovar = hisobKitobModels.cloneHisobKitob(draggedItem);
                    Integer hisob1Id = hisob1Box.getHisob().getId();
                    Integer hisob2Id = hisob2Box.getHisob().getId();
                    kochganTovar.setHisob1(hisob1Id);
                    kochganTovar.setHisob2(hisob2Id);
                    kochganTovar.setManba(draggedItem.getId());
                    kochganTovar.setDateTime(getQaydDate());
                    draggedItem.setDona(0d);

                    int dropIndex ;

                    if (row.isEmpty()) {
                        dropIndex = natijaTableView.getItems().size() ;
                    } else {
                        dropIndex = row.getIndex();
                    }

                    natijaTableView.getItems().add(dropIndex, kochganTovar);

                    event.setDropCompleted(true);
                    natijaTableView.getSelectionModel().select(dropIndex);
                    tovarTableView.refresh();
                    natijaTableView.refresh();
                    event.consume();
                }
            });
*/
            return row;
        });
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
    private TableColumn<HisobKitob, Double> getAdadColumn() {
        DecimalFormat decimalFormat = new MoneyShow();
        TableColumn<HisobKitob, Double>  adad = new TableColumn<>("Adad");
        adad.setMinWidth(80);
        adad.setMaxWidth(80);
        adad.setCellValueFactory(new PropertyValueFactory<>("dona"));
        adad.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                return decimalFormat.format(object);
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

        adad.setOnEditCommit(event -> {
            HisobKitob hisobKitob = event.getRowValue();
            for (HisobKitob hk: tovarTableView.getItems()) {
                if (hk.getId().equals(hisobKitob.getId())) {
                    Double adadDouble = event.getNewValue();
                    Double qoldiqDouble = qoldiqMap.get(hisobKitob.getId());
                    if (adadDouble > qoldiqDouble) {
                        Alerts.AlertString("Eng ko'pi bilan " + qoldiqDouble + " kiritishingiz mumkin");
                        hk.setDona(0d);
                        hisobKitob.setDona(qoldiqDouble);
                    } else if (adadDouble < 0d) {
                        Alerts.AlertString("Manfiy son kiritish mumkin emas");
                        hisobKitob.setDona(event.getOldValue());
                    } else if (adadDouble < qoldiqDouble) {
                        hk.setDona(qoldiqDouble - adadDouble);
                        hisobKitob.setDona(adadDouble);
                    } else if (adadDouble.equals(qoldiqDouble)) {
                        hk.setDona(0d);
                        hisobKitob.setDona(adadDouble);
                    } else if (adadDouble.equals(0d)) {
                        hk.setDona(qoldiqDouble);
                        hisobKitob.setDona(0d);
                    }
                    break;
                }
            }
            if (hisobKitob.getDona().equals(0d)) {
                event.getTableView().getItems().remove(hisobKitob);
            }
            tovarTableView.refresh();
            event.getTableView().refresh();
        });
        adad.setStyle( "-fx-alignment: CENTER;");
        return adad;
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
    private TableColumn<HisobKitob, Double> getChakanaNarhColumn() {
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
    private TableColumn<HisobKitob, Double> getUlgurjiNarhColumn() {
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
    private TableColumn<HisobKitob, Double> getStatus() {
        TableColumn<HisobKitob, Double> status = new TableColumn<>("Status");
        status.setCellValueFactory(new PropertyValueFactory<>("dona"));
        status.setCellFactory(column -> {
            TableCell<HisobKitob, Double> cell = new TableCell<HisobKitob, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    Circle circle = new Circle(7);
                    circle.setFill(Color.RED);
                    Circle circle1 = new Circle(7);
                    circle1.setFill(Color.LIGHTGREEN);
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        setText("");
                        if (item <= 0) {
                            setGraphic(circle);
                        } else {
                            setGraphic(circle1);
                        }
                    }
                    setAlignment(Pos.CENTER);
                }
            };
            return cell;
        });
        return status;
    }
    private void donaQosh(HisobKitob hisobKitob) {
        Double adadDouble = hisobKitob.getDona();
        if (adadDouble > 0d) {
            HisobKitob hisobKitob1 = null;
            ObservableList<HisobKitob> observableList = natijaTableView.getItems();
            for (HisobKitob nhk : natijaTableList) {
                if (nhk.getId().equals(hisobKitob.getId())) {
                    nhk.setDona(nhk.getDona() + 1);
                    hisobKitob1 = nhk;
                    break;
                }
            }
            if (hisobKitob1 == null) {
                hisobKitob1 = hisobKitobModels.cloneHisobKitob(hisobKitob);
                Integer hisob1Id = hisob1Box.getHisob().getId();
                Integer hisob2Id = hisob2Box.getHisob().getId();
                hisobKitob1.setHisob1(hisob1Id);
                hisobKitob1.setHisob2(hisob2Id);
                hisobKitob1.setManba(hisobKitob.getId());
                hisobKitob1.setDateTime(getQaydDate());
                hisobKitob1.setDona(1d);
                natijaTableView.getItems().add(hisobKitob1);

            }
            hisobKitob.setDona(adadDouble - 1);
            tovarTableView.refresh();
            natijaTableView.refresh();
        }
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
    private void tovarBoxRefresh() {
        ObservableList<Standart> tovarList = hisobKitobModels.getTovarCount(connection, hisob1Box.getHisob().getId(), new Date());
        tovarBox.setNewList(tovarList);
        AutoCompletionBinding<Standart> tovarBinding = tovarBox.getTovarBinding();
        EventHandler<AutoCompletionBinding.AutoCompletionEvent<Standart>> bindingHandler = new EventHandler<AutoCompletionBinding.AutoCompletionEvent<Standart>>() {
            @Override
            public void handle(AutoCompletionBinding.AutoCompletionEvent<Standart> event) {
                Standart newValue = event.getCompletion();
                if (newValue != null) {
                    tovarBox.setTovar(newValue);
                    tovarniYangila(newValue);
                }
            }
        };

        tovarBox.setBindingEvent(bindingHandler);
    }

    private TreeView<Butoq> initTreeView() {
        TreeView<Butoq> treeView = new TreeView<>();
        treeView.setMinWidth(230);
        SetHVGrow.VerticalHorizontal(treeView);
        TreeItem<Butoq> rootTreeItem = new TreeItem(getRootTreeItem());
        rootTreeItem.getChildren().addAll(
                getHisoblarTreeItem(),
                sanaVaqtButoq(),
                getTovarTreeItem(),
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
    private TreeItem<Butoq> getHisoblarTreeItem() {
        Butoq butoq = new Butoq(40, new Label("Tovar hisoblari"));
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
        hisob1Box = initHisob1Box();
        Butoq butoq = new Butoq(hisob1Box);
        TreeItem<Butoq> treeItem = new TreeItem(butoq);
        return treeItem;
    }
    private TreeItem<Butoq> getHisob2TreeItem() {
        hisob2Box = initHisob2Box();
        Butoq butoq = new Butoq(hisob2Box);
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
        treeItem.setExpanded(true);
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
    private TreeItem<Butoq> getTovarTreeItem() {
        Butoq butoq = new Butoq(50, new Label("Tovar"));
        TreeItem<Butoq> treeItem = new TreeItem(butoq);
        Label label = butoq.getLabel();
        label.setFont(font);
        treeItem.getChildren().addAll(
                getTovarNomiTreeItem(),
                getBirlikTreeItem(),
                getBarCodeTreeItem()
        );
        treeItem.setExpanded(true);
        return treeItem;
    }
    private TreeItem<Butoq> getTovarNomiTreeItem() {
        tovarBox = initTovarBox();
        Butoq butoq = new Butoq(51, tovarBox);
        TreeItem<Butoq> treeItem = new TreeItem(butoq);
        return treeItem;
    }
    private TreeItem<Butoq> getBirlikTreeItem() {
        birlikComboBox = initBirlikComboBox();
        Butoq butoq = new Butoq(52, birlikComboBox);
        TreeItem<Butoq> treeItem = new TreeItem(butoq);
        return treeItem;
    }
    private TreeItem<Butoq> getBarCodeTreeItem() {
        Butoq butoq = new Butoq(53, barCodeTextField);
        barCodeTextField.setDisable(true);
        barCodeTextField.setPromptText("Shtrixkod");
        HBox.setHgrow(this.barCodeTextField, Priority.ALWAYS);
        barCodeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
            }
        });
        barCodeTextField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                tovarTableList = tovarRoyxati(hisob1Box.getHisob().getId(), barCodeTextField.getText());
                for (HisobKitob hisobKitob: tovarTableList) {
                    qoldiqMap.put(hisobKitob.getId(), hisobKitob.getDona());
                }
                tovarTableView.setItems(tovarTableList);
                tovarTableView.refresh();
                barCodeTextField.setText("");
                tovarBox.getTextField().setText("");
            }
        });
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

    private void setDisable(Boolean disable) {
        ObservableList<TreeItem<Butoq>> butoqlar = treeView.getTreeItem(0).getChildren();
        hisob2Box.setDisable(disable);
        qaydSanasiDatePicker.setDisable(disable);
        qaydVaqtiTextField.setDisable(disable);
        birlikComboBox.setDisable(disable);
        tovarBox.setDisable(disable);
        izohTextArea.setDisable(disable);
        tovarTableView.setDisable(disable);
        natijaTableView.setDisable(disable);
        barCodeTextField.setDisable(disable);
    }
}
