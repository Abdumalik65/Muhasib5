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
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
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
import java.text.DecimalFormat;
import java.util.Date;

public class Sotuvchi5 extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    MenuBar mainMenu;
    TreeView<Butoq> treeView = new TreeView();
    TableView<HisobKitob> tovarTableView = new TableView();
    ToggleGroup toggleGroup;

    VBox rightPane = new VBox();
    VBox centerPane = new VBox();
    HBox topHBox = new HBox();
    HBox jamiHBox = new HBox();
    VBox bottomVBox = new VBox();

    TextField tovarTextField = new TextField();
    TextField barCodeTextField = new TextField();
    TextField jamiTextField = new TextField();
    TextField kelishmaTextField = new TextField();
    TextArea izohTextArea = new TextArea();
    ComboBox<Standart> barCodeComboBox = new ComboBox<>();
    ComboBox<Standart> narhComboBox = new ComboBox<>();
    ComboBox<Standart> birlikComboBox = new ComboBox<>();

    Connection connection;

    HisobModels hisobModels = new HisobModels();
    HisobKitobModels hisobKitobModels = new HisobKitobModels();
    Standart4Models standart4Models = new Standart4Models();
    StandartModels standartModels = new StandartModels();
    QaydnomaModel qaydnomaModel = new QaydnomaModel();

    User user;
    Hisob hisob1;
    Hisob hisob2;
    Kassa kassa;
    Valuta valuta;
    Standart asosiyTolovShakli;
    Standart tovar;
    Standart birlik;
    QaydnomaData qaydnomaData;
    Integer tovarAmali = 4;

    HBoxTextFieldPlusButton hisob1HBox = new HBoxTextFieldPlusButton();
    HBoxTextFieldPlusButton hisob2HBox = new HBoxTextFieldPlusButton();
    TovarBox tovarBox;
    HBoxTextFieldPlusButton valutaHBox = new HBoxTextFieldPlusButton();
    Button navbatdagiXaridorButton = new Button("Navbatdagi xaridor");
    Button xaridnBekorQilButton = new Button("Xaridni bekor qil");
    Button xaridniYakunlaButton = new Button("Xaridni yakunla");
    KeyCombination kc = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.ALT_DOWN);
    KeyCombination kcB = new KeyCodeCombination(KeyCode.B, KeyCombination.CONTROL_DOWN);
    KeyCombination kcC = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);
    KeyCombination kcN = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN);
    KeyCombination kcP = new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN);
    KeyCombination kcT = new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN);
    KeyCombination kcS = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
    KeyCombination kcEnter = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.CONTROL_DOWN);

    int amalTuri = 4;
    int padding = 3;

    Double jamiMablag = 0.0;

    Label jamiMablagLabel = new Label();
    DecimalFormat decimalFormat = new MoneyShow();
    Font font = Font.font("Arial",16);
    Font font1 = Font.font("Arial", FontWeight.BOLD,16);
    Font buttonFont1 = Font.font("Arial", FontWeight.BOLD,16);
    Font jamiFont = Font.font("Arial", FontWeight.BOLD,24);
    StringBuffer stringBuffer = new StringBuffer();

    ObservableList<HisobKitob> tableViewObservableList = FXCollections.observableArrayList();
    ObservableList<Standart> tovarObservableList;
    ObservableList<Valuta> valutaObservableList;
    ObservableList<Standart> narhTuriObservableList;
    ObservableList<Standart> birlikObservableList;
    ObservableList<Standart> chiqimShakliObservableList;
    ObservableList<Standart> tolovShakliObservableList;
    ObservableList<Hisob> hisobObservableList;
    ObservableList<BarCode> barCodeList= FXCollections.observableArrayList();
    LoginUserController loginUserController;

    public static void main(String[] args) {
        launch(args);
    }

    public Sotuvchi5() {
        connection = new MySqlDBGeneral(ServerType.REMOTE).getDbConnection();
        loginUserController = new LoginUserController(connection);
        user = loginUserController.login();
        GetDbData.initData(connection);
    }

    public Sotuvchi5(Connection connection, User user) {
        this.connection = connection;
        loginUserController = new LoginUserController(connection, user);
        this.user = user;
        ibtido();
    }

    public Sotuvchi5(Connection connection, User user, QaydnomaData qaydnomaData) {
        this.connection = connection;
        this.user = user;
        this.qaydnomaData = qaydnomaData;
        ibtido();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        if (buKassami()) {
            ibtido();
        } else {
            System.out.println("Bu kompyuter sistema qaydidan o`tmagan");
            Platform.exit();
            System.exit(0);
        }
        initStage(primaryStage);
        stage.setOnCloseRequest(event -> {
            barCodeOff();
            loginUserController.logOut();
            Platform.exit();
            System.exit(0);
        });
        stage.show();
    }

    public void display() {
        stage = new Stage();
        initStage(stage);
        stage.setOnCloseRequest(event -> {
            barCodeOff();
            stage.close();
        });
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    private void ibtido() {
        initData();
        initRightPane();
        initCenterPane();
        initBorderPane();
        setDisable(true);
    }

    private void initData() {
        hisobObservableList = hisobModels.get_data(connection);
        String serialNumber = getSerialNumber();
        kassa = getKassaData(connection, serialNumber);
        if (kassa != null) {
            user.setPulHisobi(kassa.getPulHisobi());
            user.setTovarHisobi(kassa.getTovarHisobi());
            user.setXaridorHisobi(kassa.getXaridorHisobi());
        }
        valuta = GetDbData.getValuta(kassa.getValuta());
        valutaObservableList = GetDbData.getValutaObservableList();
        tovarObservableList = hisobKitobModels.getTovarCount(connection, user.getTovarHisobi(), new Date());
        standartModels.setTABLENAME("Birlik");
        birlikObservableList = standartModels.get_data(connection);
        standartModels.setTABLENAME("TolovShakli");
        tolovShakliObservableList = standartModels.get_data(connection);
        asosiyTolovShakli = tolovShakliObservableList.get(0);
        standartModels.setTABLENAME("ChiqimShakli");
        chiqimShakliObservableList = standartModels.get_data(connection);
        standartModels.setTABLENAME("NarhTuri");
        narhTuriObservableList = standartModels.get_data(connection);
        yangiSavdo(kassa);
    }

    private void initTopPane() {
        initNavbatdagiXaridorButton();
        initXaridorBekorQilButton();
        HBox.setHgrow(topHBox, Priority.ALWAYS);
        topHBox.getChildren().addAll(navbatdagiXaridorButton, xaridnBekorQilButton);
    }

    private void initCenterPane() {
        centerPane = new VBox();
        initTopPane();
        initTableView();
        jamiHBox = jamiHBox();
        initXaridniYakunlaButton();
        SetHVGrow.VerticalHorizontal(centerPane);
        centerPane.getChildren().addAll(topHBox, tovarTableView, xaridniYakunlaButton);
    }

    private HBox jamiHBox() {
        HBox hBox = new HBox(2);
        HBox.setHgrow(hBox, Priority.ALWAYS);
        hBox.setPadding(new Insets(padding));
        Label label = new Label(decimalFormat.format(jamiMablag));
        label.setFont(font1);
        label.setAlignment(Pos.CENTER);
        Label label1 = new Label("Kelishilgan narh");
        label1.setFont(font1);
        label1.setAlignment(Pos.CENTER);
        Pane pane = new Pane();
        HBox.setHgrow(pane, Priority.ALWAYS);
        kelishmaTextField = initKelishmaTextField();
        jamiTextField = initJamiTextField();
        hBox.setAlignment(Pos.CENTER);
        jamiMablagLabel.setFont(jamiFont);
        jamiMablagLabel.setAlignment(Pos.CENTER_RIGHT);
        hBox.getChildren().addAll(jamiTextField);
        return hBox;
    }

    private TextField initKelishmaTextField() {
        TextField textField = new TextField();
        textField.setFont(jamiFont);
        textField.setText(decimalFormat.format(jamiMablag));
        textField.setAlignment(Pos.CENTER_RIGHT);
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {}
        });
        return textField;
    }

    private TextField initJamiTextField() {
        TextField textField = new TextField();
        textField.setFont(jamiFont);
        textField.setEditable(false);
        textField.setPromptText(decimalFormat.format(jamiMablag));
        textField.setAlignment(Pos.CENTER_RIGHT);
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {}
        });
        return textField;
    }

    private Label initJamiMablagLabel() {
        Label label = new Label();
        label.setFont(font1);
        label.setPadding(new Insets(padding));
        return label;
    }

    private void initRightPane() {
        initTreeView();
        SetHVGrow.VerticalHorizontal(rightPane);
        rightPane.getChildren().add(treeView);
    }

    private void initBorderPane() {
        borderpane.setPadding(new Insets(padding));
        SetHVGrow.VerticalHorizontal(borderpane);
        initSystemMenu();
        borderpane.setTop(mainMenu);
        borderpane.setCenter(centerPane);
        borderpane.setRight(rightPane);
    }

    private void initSystemMenu() {
        /*************************************************
         **                 System menu                 **
         *************************************************/
        mainMenu = new MenuBar();
        mainMenu.setPadding(new Insets(5));
        Menu xodimlarMenu = new Menu("Xodim");
        Menu hisobotMenu = new Menu("Hisobot");
        Menu pulMenu = new Menu("Pul");
        Menu tovarMenu = new Menu("Tovar");
        Menu xaridMenu = getXaridMenu();
        mainMenu.getMenus().addAll(xodimlarMenu, pulMenu, tovarMenu, xaridMenu);

        MenuItem changeUserMenuItem = new MenuItem("Dastur yurituvchini alishtir");
        xodimlarMenu.getItems().add(changeUserMenuItem);

        changeUserMenuItem.setOnAction(event -> {
            logOut();
            login();
            String serialNumber = getSerialNumber();
            kassa = getKassaData(connection, serialNumber);

        });


        MenuItem tovarHisobotiMenuItem = new MenuItem("Tovar hisoboti");
        MenuItem tovarKirimiMenuItem = new MenuItem("Tovar kirimi");
        MenuItem tovarChiqimiMenuItem = new MenuItem("Tovar chiqimi");
        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
        MenuItem pochkaBizishMenuItem = new MenuItem("Pochka buzish");
        MenuItem tovarGuruhlariMenuItem = new MenuItem("Tovar guruhlari");
        MenuItem serialNumberMenuItem = new MenuItem("Seriya raqami");

        tovarMenu.getItems().addAll(
                tovarHisobotiMenuItem,
                tovarKirimiMenuItem,
                tovarChiqimiMenuItem,
                separatorMenuItem,
                pochkaBizishMenuItem,
                serialNumberMenuItem
        );

        tovarHisobotiMenuItem.setOnAction(event -> {
            Hisob hisob = GetDbData.getHisob(kassa.getTovarHisobi());
            if (hisob != null) {
                TovarHisoboti tovarHisoboti = new TovarHisoboti(connection, user, hisob);
                tovarHisoboti.display();
            }
        });

        tovarKirimiMenuItem.setOnAction(event -> {
            TovarXaridi tovarXaridi = new TovarXaridi(connection, user);
            qaydnomaData = tovarXaridi.display();
            tovarObservableList = hisobKitobModels.getTovarCount(connection, user.getTovarHisobi(), new Date());
            TextField textField = tovarBox.getTextField();
            TextFields.bindAutoCompletion(textField, tovarObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Standart> autoCompletionEvent) -> {
                Standart newValue = autoCompletionEvent.getCompletion();
                if (newValue != null) {
                    tovar = newValue;
                    tovarniYangila(tovar);
                }
            });
        });

        tovarChiqimiMenuItem.setOnAction(event -> {
            TovarHarakatlari tovarHarakatlari = new TovarHarakatlari(connection, user);
            tovarHarakatlari.display();
        });

        serialNumberMenuItem.setOnAction(event -> {
            SerialNumbersController serialNumbersController = new SerialNumbersController(connection, user);
            serialNumbersController.display();
        });

        pochkaBizishMenuItem.setOnAction(event -> {
            Hisob hisob = GetDbData.getHisob(kassa.getTovarHisobi());
            PochkaBuzish2 pochkaBuzish = new PochkaBuzish2(connection, user, hisob);
            pochkaBuzish.display();
        });

        tovarGuruhlariMenuItem.setOnAction(event -> {
            TovarGuruhlari tovarGuruhlari = new TovarGuruhlari(connection, user);
            tovarGuruhlari.display();
        });

        MenuItem pulHisobotiMenuItem = new MenuItem("Pul hisoboti");
        MenuItem pulKirimiMenuItem = new MenuItem("Pul kirimi");
        MenuItem pulChiqimiMenuItem = new MenuItem("Pul chiqimi");
        MenuItem pulConvertMenuItem = new MenuItem("Konvertatsiya");
        pulMenu.getItems().addAll(pulHisobotiMenuItem, pulKirimiMenuItem, pulChiqimiMenuItem, pulConvertMenuItem);

        pulHisobotiMenuItem.setOnAction(event -> {
            Hisob hisob = GetDbData.getHisob(kassa.getPulHisobi());
            PulHisoboti pulHisoboti = new PulHisoboti(connection, user, hisob);
            pulHisoboti.display();
        });

        pulKirimiMenuItem.setOnAction(event -> {
            Hisob hisob = GetDbData.getHisob(kassa.getPulHisobi());
            KassagaPulKirimi kassagaPulKirimi = new KassagaPulKirimi(connection, user, hisob);
            kassagaPulKirimi.display();
        });

        pulChiqimiMenuItem.setOnAction(event -> {
            Hisob hisob = GetDbData.getHisob(kassa.getPulHisobi());
            KassadanPulChiqimi kassadanPulChiqimi = new KassadanPulChiqimi(connection, user, hisob);
            kassadanPulChiqimi.display();
        });

        pulConvertMenuItem.setOnAction(event -> {
            Hisob pulHisobi = GetDbData.getHisob(kassa.getPulHisobi());
            ConvertController convertController = new ConvertController(connection, user, pulHisobi);
            convertController.display();
        });
    }

    private Menu getXaridMenu() {
        Menu menu = new Menu("Xarid");
        MenuItem menuItem = getXaridJadvaliMenuItem();
        menu.getItems().addAll(getXaridJadvaliMenuItem(), getXaridorBalansiMenuItem());
        return menu;

    }

    private MenuItem getXaridJadvaliMenuItem() {
        MenuItem menuItem = new MenuItem("Xaridlar jadvali");
        menuItem.setOnAction(event -> {
            XaridlarJadvali xaridlarJadvali = new XaridlarJadvali(connection, user);
            xaridlarJadvali.display();
            if (tovarAmali == 4)
                tovarBoxRefresh();
            else
                tovarBoxRefresh2();
        });
        return menuItem;
    }

    private MenuItem getXaridorBalansiMenuItem() {
        MenuItem menuItem = new MenuItem("Xaridor hisoboti");
        menuItem.setOnAction(event -> {
            HisobBalans hisobBalans = new HisobBalans(connection, user);
            hisobBalans.display();
        });
        return menuItem;
    }

    private void initTableView() {
        HBox.setHgrow(tovarTableView, Priority.ALWAYS);
        VBox.setVgrow(tovarTableView, Priority.ALWAYS);
        tovarTableView.getColumns().addAll(getAmalColumn(), getTovarColumn(), getTaqdimColumn(), getAdadColumn(), getNarhColumn(), getSummaColumn(), getDeleteColumn(), getIzohColumn());
        tovarTableView.setEditable(true);
        tovarTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
            }
        });
        tovarTableView.setItems(tableViewObservableList);
        tableViewObservableList.addListener(new ListChangeListener<HisobKitob>() {
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
        if (tableViewObservableList.size()>0) {
            tovarTableView.getSelectionModel().selectFirst();
        }

        tovarTableView.setRowFactory(tv -> new TableRow<HisobKitob>() {
            @Override
            protected void updateItem(HisobKitob hisobKitob, boolean empty) {
                super.updateItem(hisobKitob, empty);
                if (hisobKitob == null || hisobKitob.getId() == null)
                    setStyle("");
                else if (hisobKitob.getAmal() == 4)
                    setStyle("-fx-text-fill: blue");
                else if (hisobKitob.getAmal() == 2)
                    setStyle("-fx-text-fill: red");
                else
                    setStyle("");


            }
        });
    }

    private TableColumn<HisobKitob, Integer> getTovarColumn() {
        TableColumn<HisobKitob, Integer>  tovarColumn = new TableColumn<>("Tovar");
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
//                        setText(GetDbData.getTovar(item).getText());
                        Text text = new Text(GetDbData.getTovar(item).getText());
                        text.setStyle("-fx-text-alignment:justify;");
                        text.wrappingWidthProperty().bind(getTableColumn().widthProperty().subtract(35));
                        setGraphic(text);
                    }
                    setAlignment(Pos.CENTER_LEFT);
                }
            };
            return cell;
        });
        return tovarColumn;
    }
    private TableColumn<HisobKitob, Integer> getAmalColumn() {
        TableColumn<HisobKitob, Integer>  amalColumn = new TableColumn<>("Amal");
        amalColumn.setMinWidth(50);
        amalColumn.setMaxWidth(50);
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
                        ImageView imageView = null;
                        if (item == 4) {
                            imageView = new PathToImageView("/sample/images/Icons/chiqim.png", 32, 32).getImageView();
                        } else {
                            imageView = new PathToImageView("/sample/images/Icons/kirim.png", 32, 32).getImageView();

                        }
                        setGraphic(imageView);
                    }
                    setAlignment(Pos.CENTER_LEFT);
                }
            };
            return cell;
        });
        return amalColumn;
    }
    private TableColumn<HisobKitob, Double> getAdadColumn() {
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
            Standart tovar = GetDbData.getTovar(hisobKitob.getTovar());
            if (hisobKitob.getAmal().equals(4)) {
                double barCodeCount = hisobKitobModels.getBarCodeCount(connection, hisobKitob.getHisob1(), hisobKitob.getBarCode());
                if (barCodeCount >= event.getNewValue()) {
                    hisobKitob.setDona(event.getNewValue());
                } else {
                    hisobKitob.setDona(event.getOldValue());
                    Alerts.showKamomat(tovar, event.getNewValue(), hisobKitob.getBarCode(), barCodeCount);
                }
            } else {
                hisobKitob.setDona(event.getNewValue());
            }
            refreshTableData();
            tovarTableView.refresh();
//            jamiHisob(tableViewObservableList);
        });
        adad.setStyle( "-fx-alignment: CENTER;");
        return adad;
    }
    private TableColumn<HisobKitob, Double> getNarhColumn() {
        TableColumn<HisobKitob, Double>  narh = new TableColumn<>("Narh");
        narh.setMinWidth(100);
        narh.setMaxWidth(100);
        narh.setCellValueFactory(new PropertyValueFactory<>("narh"));
        narh.setStyle( "-fx-alignment: CENTER;");
        narh.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
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
        narh.setOnEditCommit((TableColumn.CellEditEvent<HisobKitob, Double> event) -> {
            Double newValue = event.getNewValue();
            HisobKitob hisobKitob = event.getRowValue();
            BarCode barCode = GetDbData.getBarCode(hisobKitob.getBarCode());
            Standart tovar = GetDbData.getTovar(hisobKitob.getTovar());
            if (newValue != null) {
                hisobKitob.setNarh(newValue);
            }
            refreshTableData();
            event.getTableView().refresh();
        });
        return narh;
    }
    private TableColumn<HisobKitob, Button> getDeleteColumn() {
        TableColumn<HisobKitob, Button> deleteColumn = new TableColumn<>("O`chir");
        deleteColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, Button>, ObservableValue<Button>>() {

            @Override
            public ObservableValue<Button> call(TableColumn.CellDataFeatures<HisobKitob, Button> param) {
                HisobKitob hisobKitob = param.getValue();
                Button b = new Button("");
                b.setMaxWidth(2000);
                b.setPrefWidth(150);
                b.setMaxHeight(2000);
                b.setPrefHeight(20);
                HBox.setHgrow(b, Priority.ALWAYS);
                VBox.setVgrow(b, Priority.ALWAYS);
                ImageView imageView = new PathToImageView("/sample/images/Icons/delete.png").getImageView();
                b.setGraphic(imageView);

                b.setOnAction(event -> {
                    tableViewObservableList.remove(hisobKitob);
                    param.getTableView().refresh();
                    refreshTableData();
                });
                return new SimpleObjectProperty<Button>(b);
            }
        });

        deleteColumn.setMinWidth(20);
        deleteColumn.setMaxWidth(100);
        deleteColumn.setStyle( "-fx-alignment: CENTER;");
        return deleteColumn;
    }
    private TableColumn<HisobKitob, String> getSummaColumn() {
        TableColumn<HisobKitob, String>  summaCol = new TableColumn<>("Jami");
        summaCol.setMinWidth(150);
        summaCol.setMaxWidth(150);
        summaCol.setStyle( "-fx-alignment: CENTER;");
        summaCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<HisobKitob, String> param) {
                HisobKitob hisobKitob = param.getValue();
                Double total = hisobKitob.getDona() * hisobKitob.getNarh();
                return new SimpleObjectProperty<String>(decimalFormat.format(total));
            }
        });
        return summaCol;
    }
    private TableColumn<HisobKitob, String> getIzohColumn() {
        TableColumn<HisobKitob, String>  izohColumn = new TableColumn<>("Izoh");
        izohColumn.setMinWidth(100);
        izohColumn.setMaxWidth(100);
        izohColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<HisobKitob, String> param) {
                HisobKitob hisobKitob = param.getValue();
                return new SimpleObjectProperty<String>(hisobKitob.getIzoh());
            }
        });
        izohColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        izohColumn.setStyle( "-fx-alignment: CENTER;");
        izohColumn.setOnEditCommit((TableColumn.CellEditEvent<HisobKitob, String> event) -> {
            String newValue = event.getNewValue();
            if (newValue != null) {
                HisobKitob hisobKitob = event.getRowValue();
                hisobKitob.setIzoh(newValue);
                event.getTableView().refresh();
            }
        });
        return izohColumn;
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

    private TableColumn<HisobKitob, String> getTaqdimColumn() {
        TableColumn<HisobKitob, String>  tovarColumn = new TableColumn<>("Birlik");
        tovarColumn.setMinWidth(80);
        tovarColumn.setCellValueFactory(new PropertyValueFactory<>("barCode"));

        tovarColumn.setCellFactory(column -> {
            TableCell<HisobKitob, String> cell = new TableCell<HisobKitob, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        BarCode barCode = GetDbData.getBarCode(item);
                        Text text = new Text(GetDbData.getBirlikFromBarCodeString(barCode));
                        text.setStyle("-fx-text-alignment:justify;");
                        text.wrappingWidthProperty().bind(getTableColumn().widthProperty().subtract(35));
                        setGraphic(text);
                    }
                    setAlignment(Pos.CENTER_LEFT);
                }
            };
            return cell;
        });
        return tovarColumn;
    }


    private TableColumn<HisobKitob, String> getTaqdim3Column() {
        TableColumn<HisobKitob, String> taqdimColumn = new TableColumn<>("Birlik");
        taqdimColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<HisobKitob, String> param) {
                HisobKitob hisobKitob = param.getValue();
                BarCode barCode = GetDbData.getBarCode(hisobKitob.getBarCode());
                Standart birlikStandart = GetDbData.getBirlik(barCode.getBirlik());
                String birlikString = birlikStandart.getText();
                return new SimpleObjectProperty<>(birlikString);
            }
        });
        taqdimColumn.setStyle( "-fx-alignment: CENTER;");
        taqdimColumn.setMinWidth(120);
        return taqdimColumn;
    }

    private void initTreeView() {
        SetHVGrow.VerticalHorizontal(treeView);
        TreeItem<Butoq> rootTreeItem = new TreeItem(getRootTreeItem());
        rootTreeItem.getChildren().addAll(
                getSozlovTreeItem(),    //40
                getTovarTreeItem(),
                getJamiLabelTreeItem(),
                izohButoq()
        );

        treeView.setRoot(rootTreeItem);
        treeView.setShowRoot(false);
        treeView.setMaxWidth(280);
    }

    private TreeItem<Butoq> getRootTreeItem() {
        Standart standart = new Standart(0, "Asosiy", user.getId(), new Date());
        Butoq butoq = new Butoq(standart);
        TreeItem<Butoq> treeItem = new TreeItem(butoq);
        treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Butoq butoq2 = newValue.getValue();
            if ( butoq2 != null) {
                setOnAction(butoq2);
            }
        });
        return treeItem;
    }

    private void setOnAction(Butoq butoq) {
        Hisob pulHisobi = GetDbData.getHisob(kassa.getPulHisobi());
        switch (butoq.getItemId()) {
            case 11:
                logOut();
                login();
                break;
            case 21:
                PulHisoboti pulHisoboti = new PulHisoboti(connection, user);
                pulHisoboti.display();
                break;
            case 22:
                KassadanPulChiqimi kassadanPulChiqimi = new KassadanPulChiqimi(connection, user, pulHisobi);
                kassadanPulChiqimi.display();
                break;
            case 23:
                KassagaPulKirimi kassagaPulKirimi = new KassagaPulKirimi(connection, user, hisob1);
                kassagaPulKirimi.display();
                break;
            case 24:
                ConvertController convertController = new ConvertController(connection, user, pulHisobi);
                convertController.display();
                break;
            case 31:
                TovarHisoboti tovarHisoboti = new TovarHisoboti(connection, user);
                tovarHisoboti.display();
                break;
            case 32:
                TovarXaridi tovarXaridi = new TovarXaridi(connection, user);
                tovarXaridi.display();
                break;
            case 33:
                TovarHarakatlari tovarHarakatlari = new TovarHarakatlari(connection, user);
                tovarHarakatlari.display();
                break;
            case 34:
                PochkaBuzish2 pochkaBuzish = new PochkaBuzish2(connection, user, hisob1);
                pochkaBuzish.display();
                break;
        }
    }

    private TreeItem<Butoq> getSozlovTreeItem() {
        Butoq butoq = new Butoq(40, new Label("Savdo sozlovlari"));
        TreeItem<Butoq> treeItem = new TreeItem(butoq);
        Label label = butoq.getLabel();
        label.setFont(font);
        treeItem.getChildren().addAll(
                getHisob1TreeItem(),
                getHisob2TreeItem(),
                getValutaTreeItem(),
                getNarhTuriTreeItem()
        );
        treeItem.setExpanded(true);
        return treeItem;
    }

    private TreeItem<Butoq> getHisob1TreeItem() {
        initHisob1HBox();
        Butoq butoq = new Butoq(41, hisob1HBox);
        TreeItem<Butoq> treeItem = new TreeItem(butoq);
        return treeItem;
    }

    private TreeItem<Butoq> getHisob2TreeItem() {
        initHisob2HBox();
        Butoq butoq = new Butoq(42, hisob2HBox);
        TreeItem<Butoq> treeItem = new TreeItem(butoq);
        return treeItem;
    }

    private TreeItem<Butoq> getValutaTreeItem() {
        initValutaHBox();
        Butoq butoq = new Butoq(43, valutaHBox);
        TreeItem<Butoq> treeItem = new TreeItem(butoq);
        return treeItem;
    }

    private TreeItem<Butoq> getNarhTuriTreeItem() {
        initNarhComboBox();
        Butoq butoq = new Butoq(44, narhComboBox);
        TreeItem<Butoq> treeItem = new TreeItem(butoq);
        return treeItem;
    }

    private TreeItem<Butoq> getTovarTreeItem() {
        Butoq butoq = new Butoq(50, new Label("Tovar"));
        TreeItem<Butoq> treeItem = new TreeItem(butoq);
        Label label = butoq.getLabel();
        label.setFont(font);
        treeItem.getChildren().addAll(
                getTovarAmalTreeItem(),
                getTovarNomiTreeItem(),
                getBirlikTreeItem(),
                getBarCodeTreeItem()
        );
        treeItem.setExpanded(true);
        return treeItem;
    }

    private TreeItem<Butoq> getTovarAmalTreeItem() {
        toggleGroup = new ToggleGroup();
        RadioButton radioButton1 = new RadioButton("Sotamiz");
        RadioButton radioButton2 = new RadioButton("Sotib olamiz");
        toggleGroup.getToggles().addAll(radioButton1, radioButton2);
        toggleGroup.selectToggle(radioButton1);
        radioButton1.setOnAction(event -> {
            tovarAmali = 4;
            tovarBoxRefresh();
        });
        radioButton2.setOnAction(event -> {
            tovarAmali = 2;
            tovarBoxRefresh2();
        });
        Butoq butoq = new Butoq(59, toggleGroup);
        TreeItem<Butoq> treeItem = new TreeItem(butoq);
        return treeItem;
    }
    private TreeItem<Butoq> getTovarNomiTreeItem() {
        TovarBox tovarBox = initTovarBox();
        Butoq butoq = new Butoq(51, tovarBox);
        TreeItem<Butoq> treeItem = new TreeItem(butoq);
        return treeItem;
    }

    private TreeItem<Butoq> getBirlikTreeItem() {
        initBirlikComboBox();
        Butoq butoq = new Butoq(52, birlikComboBox);
        TreeItem<Butoq> treeItem = new TreeItem(butoq);
        return treeItem;
    }

    private TreeItem<Butoq> getBarCodeTreeItem() {
        Butoq butoq = new Butoq(53, barCodeTextField);
        TextField textField = butoq.getTextField();
        textField.setPromptText("STRIXKOD");
        HBox.setHgrow(barCodeTextField, Priority.ALWAYS);
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                refreshTableData();
            }
        });
        TreeItem<Butoq> treeItem = new TreeItem(butoq);
        return treeItem;
    }

    private TreeItem<Butoq> getJamiLabelTreeItem() {
        Butoq butoq = new Butoq(80, new Label("Jami mablag`: "));
        TreeItem<Butoq> treeItem = new TreeItem(butoq);
        treeItem.getChildren().addAll(
                getJamiTreeItem()
        );
        treeItem.setExpanded(true);
        Label label = butoq.getLabel();
        label.setFont(font);
        return treeItem;
    }

    private TreeItem<Butoq> getJamiTreeItem() {
        Butoq butoq = new Butoq(81, jamiMablagLabel);
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
    private void initIzohTextArea() {
        SetHVGrow.VerticalHorizontal(izohTextArea);
        izohTextArea.setWrapText(true);
        izohTextArea.setEditable(true);
        izohTextArea.setMaxWidth(200);
    }

    private void initStage(Stage primaryStage) {
        scene = new Scene(borderpane);
        scene.setUserAgentStylesheet("sample/Styles/caspian.css");
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        stage = primaryStage;
        stage.setTitle("Savdo");
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        stage.setScene(scene);
    }
    private void login() {
        LoginUserController loginUserController = new LoginUserController(connection);
        if (!loginUserController.display()) {
            Platform.exit();
            System.exit(0);
        } else {
            user = loginUserController.getUser();
            String serialNumber = Sotuvchi5.getSerialNumber();
            Kassa kassa = Sotuvchi5.getKassaData(connection, serialNumber);
            if (kassa != null) {
                user.setPulHisobi(kassa.getPulHisobi());
                user.setTovarHisobi(kassa.getTovarHisobi());
                user.setXaridorHisobi(kassa.getXaridorHisobi());
                user.setValuta(kassa.getValuta());
            }
        }
    }

    private void logOut() {
        UserModels userModels = new UserModels();
        user.setOnline(0);
        userModels.changeUser(connection, user);
    }

    public static String getSerialNumber() {
        String serialNumber = ConnectionType.getAloqa().getText().trim();
        return serialNumber;
    }

    public static Kassa getKassaData(Connection connection, String serialNumber) {
        Kassa kassa = null;
        ObservableList<Kassa> kassaObservableList = null;
        KassaModels kassaModels = new KassaModels();
        kassa = null;
        kassaObservableList = kassaModels.getAnyData(connection, "serialNumber = '" + serialNumber + "'", "");
        if (kassaObservableList.size() > 0) {
            kassa = kassaObservableList.get(0);
        }
        return kassa;
    }

    private Boolean buKassami() {
        KassaModels kassaModels = new KassaModels();
        Boolean buKassami = false;
        String serialNumber = getSerialNumber();
        kassa = getKassaData(connection, serialNumber);
        if (kassa != null) {
            if (kassa.getSerialNumber().trim().equalsIgnoreCase(serialNumber)) {
                if (kassa.getIsLocked() == 0) {
                    kassa.setOnline(1);
                    buKassami = true;
                    kassaModels.update_data(connection, kassa);
                } else {
                    System.out.println(kassa.getKassaNomi().trim() + " nozir tomonidan qulflangan");
                }
            }
        }
        return buKassami;
    }
    private void initHisob1HBox() {
        hisob1 = GetDbData.getHisob(kassa.getTovarHisobi());
        HBox.setHgrow(hisob1HBox, Priority.ALWAYS);
        TextField textField = hisob1HBox.getTextField();
        textField.setText(hisob1.getText());
        TextFields.bindAutoCompletion(textField, hisobObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Hisob> autoCompletionEvent) -> {
            Hisob newValue = autoCompletionEvent.getCompletion();
            if (newValue != null) {
                hisob1 = newValue;
                hisob2HBox.setDisable(false);
                kassa.setTovarHisobi(hisob1.getId());
                user.setTovarHisobi(hisob1.getId());
                Integer xaridorHisobiInteger = yordamchHisob(hisob1.getId(), "Xaridor1");
                Integer pulHisobiInteger = yordamchHisob(hisob1.getId(), "PulHisobi1");
                if (xaridorHisobiInteger>0) {
                    hisob2 = GetDbData.getHisob(xaridorHisobiInteger);
                    hisob2HBox.getTextField().setText(hisob2.getText());
                    kassa.setXaridorHisobi(xaridorHisobiInteger);
                    user.setXaridorHisobi(xaridorHisobiInteger);
                }
                if (pulHisobiInteger>0) {
                    kassa.setPulHisobi(pulHisobiInteger);
                    user.setPulHisobi(pulHisobiInteger);
                }
                tovarBoxRefresh();
            }
        });

        Button addButton = hisob1HBox.getPlusButton();
        if (user.getStatus().equals(99)) {
            addButton.setDisable(false);
            textField.setDisable(false);
        } else {
            addButton.setDisable(true);
            textField.setDisable(true);
        }
        addButton.setOnAction(event -> {
            Hisob newHisob = addHisob();
            if (newHisob != null) {
                hisob1 = newHisob;
                textField.setText(hisob1.getText());
                kassa.setTovarHisobi(hisob1.getId());
                user.setTovarHisobi(hisob1.getId());
//                initTovarBox();
            }
        });
    }

    private void tovarBoxRefresh() {
        tovarObservableList = hisobKitobModels.getTovarCount(connection, user.getTovarHisobi(), new Date());
        tovarBox.setNewList(tovarObservableList);
        AutoCompletionBinding<Standart> tovarBinding = tovarBox.getTovarBinding();
        EventHandler<AutoCompletionBinding.AutoCompletionEvent<Standart>> bindingHandler = new EventHandler<AutoCompletionBinding.AutoCompletionEvent<Standart>>() {
            @Override
            public void handle(AutoCompletionBinding.AutoCompletionEvent<Standart> event) {
                Standart newValue = event.getCompletion();
                if (newValue != null) {
                    tovar = newValue;
                    tovarniYangila(tovar);
                }
            }
        };

        tovarBox.setBindingEvent(bindingHandler);
    }

    private Integer yordamchHisob(Integer hisobId, String tableName) {
        Standart3Models standart3Models = new Standart3Models(tableName);
        Integer hisobInteger = 0;
        ObservableList<Standart3> standart3ObservableList = standart3Models.getAnyData(connection, "id3 = " + hisobId,"");
        if (standart3ObservableList.size()>0) {
            hisobInteger = standart3ObservableList.get(0).getId2();
        }

        return hisobInteger;
    }

    private void initHisob2HBox() {
        HBox.setHgrow(hisob2HBox, Priority.ALWAYS);
        hisob2 = GetDbData.getHisob(kassa.getXaridorHisobi());
        TextField textField = hisob2HBox.getTextField();
        textField.setText(hisob2.getText());
        TextFields.bindAutoCompletion(textField, hisobObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Hisob> autoCompletionEvent) -> {
            Hisob newValue = autoCompletionEvent.getCompletion();
            if (newValue != null) {
                hisob2 = newValue;
                kassa.setXaridorHisobi(hisob2.getId());
                user.setXaridorHisobi(hisob2.getId());
            }
        });

        Button addButton = hisob2HBox.getPlusButton();
        addButton.setOnAction(event -> {
            Hisob newHisob = addHisob();
            if (newHisob != null) {
                hisob2 = newHisob;
                textField.setText(hisob2.getText());
                kassa.setXaridorHisobi(hisob2.getId());
                user.setXaridorHisobi(hisob2.getId());
            }
        });
    }

    private TovarBox initTovarBox() {
        ObservableList<Standart> tovarObservableList=hisobKitobModels.getTovarCount(connection, user.getTovarHisobi(), new Date());
        tovarBox = new TovarBox(tovarObservableList, user, font1);
        TextField textField = tovarBox.getTextField();
        EventHandler<AutoCompletionBinding.AutoCompletionEvent<Standart>> bindingHandler = new EventHandler<AutoCompletionBinding.AutoCompletionEvent<Standart>>() {
            @Override
            public void handle(AutoCompletionBinding.AutoCompletionEvent<Standart> event) {
                Standart newValue = event.getCompletion();
                if (newValue != null) {
                    tovar = newValue;
                    tovarniYangila(tovar);
                }
            }
        };
        AutoCompletionBinding<Standart> tovarBinding = tovarBox.getTovarBinding();
        tovarBinding.setOnAutoCompleted(bindingHandler);
        Button addButton = tovarBox.getPlusButton();
        addButton.setOnAction(event -> {
            TovarXaridi tovarXaridi = new TovarXaridi(connection, user);
            qaydnomaData = tovarXaridi.display();
        });
        return tovarBox;
    }

    private void tovarniYangila(Standart tovar) {
        barCodeList.removeAll(barCodeList);
        barCodeList = GetDbData.getBarCodeList(tovar.getId());
        if (barCodeList.size()>0) {
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

    private void initValutaHBox() {
        HBox.setHgrow(valutaHBox, Priority.ALWAYS);
        valuta = GetDbData.getValuta(kassa.getValuta());
        TextField textField = valutaHBox.getTextField();
        textField.setText(valuta.getValuta());
        TextFields.bindAutoCompletion(textField, valutaObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Valuta> autoCompletionEvent) -> {
            Valuta newValue = autoCompletionEvent.getCompletion();
            if (newValue != null) {
                valuta = newValue;
                valutaniYangila(valuta);
            }
        });

        Button addButton = valutaHBox.getPlusButton();
        addButton.setOnAction(event -> {
            Valuta newValuta = addValuta();
            if (newValuta != null) {
                valuta = newValuta;
                textField.setText(valuta.getValuta());
                valutaniYangila(valuta);
            }
        });
    }

    private void valutaniYangila(Valuta valuta) {
        ObservableList<HisobKitob> observableList = tovarTableView.getItems();
        if (observableList.size()>0) {
            Double yangiKurs = getKurs(valuta.getId(), new Date()).getKurs();
            for (HisobKitob hisobKitob : observableList) {
                Double yanginarh = hisobKitob.getNarh() * yangiKurs;
                hisobKitob.setNarh(yanginarh);
                hisobKitob.setKurs(yangiKurs);
            }
            tovarTableView.refresh();
        }
    }

    private void initBirlikComboBox() {
        birlikComboBox.setMaxWidth(2000);
        birlikComboBox.setPrefWidth(150);
        SetHVGrow.VerticalHorizontal(birlikComboBox);
        birlikComboBox.setItems(birlikObservableList);
        if (birlikObservableList.size()>0) {
            birlik = birlikObservableList.get(0);
            birlikComboBox.getSelectionModel().selectFirst();
        }
        birlikComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                birlik = newValue;
                barCodeTextField.setText(getBarCodeString());
            }
        });
    }

    private String getBarCodeString() {
        String barCodeString = null;
        for (BarCode bc: barCodeList) {
            if (bc.getTovar().equals(tovar.getId()) && bc.getBirlik().equals(birlik.getId())) {
                barCodeString = bc.getBarCode();
                break;
            }
        }
        return barCodeString;
    }

    private void initNarhComboBox() {
        narhComboBox.setMaxWidth(2000);
        narhComboBox.setPrefWidth(150);
        SetHVGrow.VerticalHorizontal(narhComboBox);
        narhComboBox.setItems(narhTuriObservableList);
        int savdoTuri = kassa.getSavdoTuri();
        Standart narhTuri = null;
        for (Standart n: narhTuriObservableList) {
            if (n.getId().equals(savdoTuri)) {
                narhTuri = n;
            }
        }
        if (narhTuri != null) {
            narhComboBox.getSelectionModel().select(narhTuri);
        } else {
            narhComboBox.getSelectionModel().selectFirst();
        }
    }

    private void initNavbatdagiXaridorButton() {
        HBox.setHgrow(navbatdagiXaridorButton, Priority.ALWAYS);
        navbatdagiXaridorButton.setFont(buttonFont1);
        navbatdagiXaridorButton.setMaxWidth(2000);
        navbatdagiXaridorButton.setPrefWidth(150);
        navbatdagiXaridorButton.setOnAction(event -> {
            tovarBoxRefresh();
            barCodeOn();
            setDisable(false);
        });
    }

    private void initXaridorBekorQilButton() {
        HBox.setHgrow(xaridnBekorQilButton, Priority.ALWAYS);
        xaridnBekorQilButton.setFont(buttonFont1);
        xaridnBekorQilButton.setMaxWidth(2000);
        xaridnBekorQilButton.setPrefWidth(150);
        xaridnBekorQilButton.setOnAction(event -> {
            barCodeOff();
            kassaniTozala();
            setDisable(true);
        });
    }

    private void initXaridniYakunlaButton() {
        HBox.setHgrow(xaridniYakunlaButton, Priority.ALWAYS);
        xaridniYakunlaButton.setFont(buttonFont1);
        xaridniYakunlaButton.setMaxWidth(3000);
        xaridniYakunlaButton.setPrefWidth(150);
        xaridniYakunlaButton.setDisable(true);
        xaridniYakunlaButton.setOnAction(event -> {
            Tolovlar4 tolovlar = new Tolovlar4(connection, user, hisob1, hisob2, valuta, tableViewObservableList, izohTextArea);
            tolovlar.setNarhTuri(narhComboBox.getValue().getId());
            Boolean sotildi = tolovlar.display();
            if (sotildi) {
                xaridnBekorQilButton.fire();
            }
        });
    }
    private void kassaniTozala() {
        izohTextArea.setText("");
        tableViewObservableList.removeAll(tableViewObservableList);
        tovarTableView.refresh();
        jamiMablag = 0.0;
        refreshTableData();
        kassa = getKassaData(connection, getSerialNumber());
        setUser(kassa);
        hisob1 = GetDbData.getHisob(kassa.getTovarHisobi());
        hisob1HBox.getTextField().setText(hisob1.getText());
        hisob2 = GetDbData.getHisob(kassa.getXaridorHisobi());
        hisob2HBox.getTextField().setText(hisob2.getText());
        valuta = GetDbData.getValuta(kassa.getValuta());
        valutaHBox.getTextField().setText(valuta.getValuta());
        int savdoTuri = kassa.getSavdoTuri();
        Standart narhTuri = null;
        for (Standart n: narhTuriObservableList) {
            if (n.getId().equals(savdoTuri)) {
                narhTuri = n;
            }
        }
        if (narhTuri != null) {
            narhComboBox.getSelectionModel().select(narhTuri);
        } else {
            narhComboBox.getSelectionModel().selectFirst();
        }
    }
    private Hisob addHisob() {
        Hisob hisob = null;
        HisobController hisobController = new HisobController();
        hisobController.display(connection, user);
        if (hisobController.getDoubleClick()) {
            hisob = hisobController.getDoubleClickedRow();
            boolean yangi = true;
            for (Hisob h: hisobObservableList) {
                if (h.getId().equals(hisob.getId())) {
                    yangi = false;
                    break;
                }
            }
            if (yangi) {
                hisobObservableList.add(hisob);
                TextField textField = hisob2HBox.getTextField();
                TextFields.bindAutoCompletion(textField, hisobObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Hisob> autoCompletionEvent) -> {
                    Hisob newValue = autoCompletionEvent.getCompletion();
                    if (newValue != null) {
                        hisob2 = newValue;
                    }
                });
            }
        }
        return hisob;
    }
    private Valuta addValuta() {
        Valuta valuta1 = null;
        ValutaController valutaController = new ValutaController();
        valutaController.display(connection, user);
        if (valutaController.getDoubleClick()) {
            valuta1 = valutaController.getDoubleClickedRow();
            boolean yangi = true;
            for (Valuta v: valutaObservableList) {
                if (v.getId().equals(valuta1.getId())) {
                    yangi = false;
                    break;
                }
            }
            if (yangi) {
                valutaObservableList.add(valuta1);
                TextField textField = valutaHBox.getTextField();
                textField.setText(valuta1.getValuta());
                TextFields.bindAutoCompletion(textField, valutaObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Valuta> autoCompletionEvent) -> {
                    Valuta newValue = autoCompletionEvent.getCompletion();
                    if (newValue != null) {
                        valuta = newValue;
                    }
                });
            }
        }
        return valuta1;
    }
    private Double refreshTableData() {
        Double jamiDouble = 0d;
        Boolean tovarBor = false;
        ObservableList<HisobKitob> observableList = tovarTableView.getItems();
        Double kursDouble = getKurs(valuta.getId(), new Date()).getKurs();
        for (HisobKitob hisobKitob: observableList) {
            if (hisobKitob.getAmal().equals(4))
                jamiDouble += hisobKitob.getDona() * hisobKitob.getNarh() * hisobKitob.getKurs() / kursDouble;
            else
                jamiDouble -= hisobKitob.getDona() * hisobKitob.getNarh() * hisobKitob.getKurs() / kursDouble;
        }
        jamiMablag = jamiDouble;
        jamiMablagLabel.setText(decimalFormat.format(jamiDouble));
        jamiTextField.setText(decimalFormat.format(jamiDouble));
        kelishmaTextField.setText(decimalFormat.format(jamiDouble));
        return jamiDouble;
    }
    private Kurs getKurs(int valutaId, Date sana) {
        Valuta v = GetDbData.getValuta(valutaId);
        Kurs kurs = v.getStatus() == 1 ? new Kurs(1, new Date(), valutaId, 1.0, user.getId(), new Date()) : null;
        KursModels kursModels = new KursModels();
        ObservableList<Kurs> kursObservableList = null;
        kursObservableList = kursModels.getDate(connection, valutaId, sana, "sana desc");
        if (kursObservableList.size()>0) {
            kurs = kursObservableList.get(0);
        }
        return kurs;
    }
    private void barCodeOn() {
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                stringBuffer.append(event.getText());
            }
        });

        Runnable rn = ()-> xaridniYakunlaButton.fire();
        scene.getAccelerators().put(kc, rn);

        scene.getAccelerators().put(kc, rn);

        Runnable runTovar = () -> {
            TextField textField  = tovarBox.getTextField();
            textField.requestFocus();
        };
        scene.getAccelerators().put(kcT, runTovar);

        Runnable runBarCode = () -> barCodeTextField.requestFocus();
        scene.getAccelerators().put(kcB, runBarCode);

        Runnable runEditCell = ()-> {
            HisobKitob hk = tovarTableView.getSelectionModel().getSelectedItem();
            int rowIndex = tovarTableView.getSelectionModel().getSelectedIndex();
            if (hk != null) {
                TablePosition<HisobKitob, Double> doubleTablePosition = new TablePosition<>(tovarTableView, rowIndex, null);
                tovarTableView.getFocusModel().focus(doubleTablePosition);
            }
        };
        scene.getAccelerators().put(kcEnter, runEditCell);
        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    String string = stringBuffer.toString().trim();
                    stringBuffer.delete(0, stringBuffer.length());
                    if (!string.isEmpty()) {
                        BarCode barCode = GetDbData.getBarCode(string);
                        if (barCode != null) {
                            Standart tovar = GetDbData.getTovar(barCode.getTovar());
                            if (tovar != null) {
                                barCodeComboBox.setValue(null);
                                birlikObservableList.removeAll(birlikObservableList);
                                tovarTextField.setText("");
                                if (tovarAmali == 4) {
                                    addTovar(barCode);
                                } else {
                                    addTovar2(barCode);
                                }
                            }
                        } else {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Diqqat!!!");
                            alert.setHeaderText(string + " shtrixkodga muvofiq tovar topilmadi");
                            alert.setContentText("");
                            alert.showAndWait();
                        }
                    }
                }
            }
        });

        barCodeTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode()== KeyCode.ENTER) {
                    stringBuffer.delete(0, stringBuffer.length());
                    String string = barCodeTextField.getText().trim();
                    if (!string.isEmpty()) {
                        BarCode barCode = GetDbData.getBarCode(string);
                        if (barCode != null) {
                            Standart tovar = GetDbData.getTovar(barCode.getTovar());
                            if (tovar != null) {
                                barCodeTextField.setText("");
                                barCodeComboBox.setValue(null);
                                birlikObservableList.removeAll(birlikObservableList);
                                tovarTextField.setText("");
                                if (tovarAmali == 4) {
                                    addTovar(barCode);
                                } else {
                                    addTovar2(barCode);
                                }
                            }
                        } else {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Diqqat!!!");
                            alert.setHeaderText(string + " shtrixkodga muvofiq tovar topilmadi");
                            alert.setContentText("");
                            alert.showAndWait();
                        }
                    }
                }
            }
        });
    }
    private void barCodeOff() {
        scene.setOnKeyPressed(null);
        scene.setOnKeyReleased(null);
        barCodeTextField.setOnKeyPressed(null);
        scene.getAccelerators().remove(kc);
        scene.getAccelerators().remove(kcC);
        scene.getAccelerators().remove(kcN);
        scene.getAccelerators().remove(kcP);
        scene.getAccelerators().remove(kcT);
        scene.getAccelerators().remove(kcS);
    }
    private void addTovar(BarCode barCode) {
        Integer narhTuri = narhComboBox.getValue().getId();
        standartModels.setTABLENAME("Tovar");
        Standart tovar = GetDbData.getTovar(barCode.getTovar());
        if (tovar == null) {
            Alerts.AlertString("Tovar id: " + barCode.getTovar() + "\nBu tovar bazada yo`q");
            return;
        }
        HisobKitob hisobKitob = new HisobKitob(0, 0, 0, 4, kassa.getTovarHisobi(),
                hisob2.getId(), valuta.getId(), tovar.getId(), 1.0, barCode.getBarCode(), 1.0,
                0.00, 0, tovar.getText(), user.getId(), new Date()
        );
        if (valuta.getStatus() == 1) {
            hisobKitob.setKurs(1.00);
        } else {
            hisobKitob.setKurs(getKurs(hisobKitob.getValuta(), hisobKitob.getDateTime()).getKurs());
        }

        standart4Models.setTABLENAME("Nds");
        Standart4 ndsStandart4 = standart4Models.getTartibForDate(connection, hisobKitob.getTovar(), new Date(), "dateTime desc");
        double nds = 0.0;
        if (ndsStandart4 != null) {
            nds = ndsStandart4.getMiqdor() * 0.01;
        }

        double narhDouble = 0d;
        double narhDoubleFinal = 0d;
        Standart3Models standart3Models = new Standart3Models();
        standart3Models.setTABLENAME("TGuruh2");
        Standart3 s3 = null;
        ObservableList<Standart3> s3List = standart3Models.getAnyData(connection, "id3 = " + tovar.getId(), "");
        if (s3List.size()>0) {
            s3 = s3List.get(0);
            Standart6Models standart6Models = new Standart6Models("TGuruh1");
            Standart6 s6 = standart6Models.getWithId(connection, s3.getId2());
            if (s6 != null) {
                if (narhTuri == 1) {
                    narhDouble = s6.getChakana();
                } else if (narhTuri == 2) {
                    narhDouble = s6.getUlgurji();
                }
            }
        }
        else  {
            TovarNarhi tn = null;
            if (narhTuri == 1) {
                tn = yakkaTovarNarhi(hisobKitob.getTovar(), 1);
            } else {
                tn = yakkaTovarNarhi(hisobKitob.getTovar(), 2);
            }
            if (tn != null) {
                narhDouble = tn.getNarh();
            }
        }

        if (narhDouble == 0d) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Diqqat qiling!!");
            alert.setHeaderText(tovar.getText() + " uchun narh tayinlanmagan. Shu sabab ro`yhatga qo`shilmaydi");
            alert.setContentText(tovar.getText() + " uchun narhni sistema noziri tayin etadi");
            alert.showAndWait();
            return;
        }

        double pochkadaDona = tovarDonasi(barCode);
        if (narhTuri == 1) {
            if (valuta.getStatus() == 1) {
                narhDoubleFinal = pochkadaDona * narhDouble * (1 + nds);
            } else {
                narhDoubleFinal = yahlitla(pochkadaDona * (narhDouble * hisobKitob.getKurs() * (1 + nds)), 1);
            }
        } else if (narhTuri == 2) {
            if (valuta.getStatus() == 1) {
                narhDoubleFinal = pochkadaDona * narhDouble * (1 + nds);
            } else {
                narhDoubleFinal = yahlitla(pochkadaDona * (narhDouble * hisobKitob.getKurs() * (1 + nds)), 1);
            }
        }

        hisobKitob.setNarh(narhDoubleFinal);
        double zaxiradagiAdad = hisobKitobModels.getBarCodeCount(connection, kassa.getTovarHisobi(), hisobKitob.getBarCode());
        double adad = addTovarToTable(barCode, hisobKitob, zaxiradagiAdad);
        if (zaxiradagiAdad < adad) {
            Alerts.showKamomat(tovar, adad, barCode.getBarCode(), zaxiradagiAdad);
        }
        refreshTableData();
        tovarTableView.refresh();
        tovarBox.getTextField().setText("");
    }
    private double tovarDonasi(BarCode barCode) {
        double dona = 1.0;
        double adadBarCode2 = 0;
        dona *= barCode.getAdad();
        int tarkibInt = barCode.getTarkib();
        if (tarkibInt>0) {
            while (true) {
                BarCode barCode2 = GetDbData.getBarCode(tarkibInt);
                adadBarCode2 = barCode2.getAdad();
                dona *= adadBarCode2;
                tarkibInt = barCode2.getTarkib();
                if (adadBarCode2 == 1.0) {
                    break;
                }
            }
        }
        return dona;
    }
    private Double addTovarToTable(BarCode barCode, HisobKitob hisobKitob, Double zaxiradagiAdad) {
        double adad = 1.0;
        HisobKitob hk1 = null;
        for (HisobKitob hk : tableViewObservableList) {
            if (hk.getBarCode() .equals(barCode.getBarCode())) {
                hk1 = hk;
                adad += hk.getDona();
                break;
            }
        }
        if (zaxiradagiAdad >= adad) {
            if (hk1 == null) {
                tableViewObservableList.add(hisobKitob);
            } else {
                hk1.setDona(adad);
            }
//            jamiHisob(tableViewObservableList);
        }
        return adad;
    }
    private double yahlitla(double son, int daraja) {
        double darajalanganSon = Math.pow(10, daraja);
        double natija = son/darajalanganSon;
        double roundNatija = Math.round(natija)*darajalanganSon;
        System.out.println(new MoneyShow().format(roundNatija));
        return roundNatija;
    }
    private void setDisable(Boolean disable) {
        for (Node node: centerPane.getChildren()) {
            node.setDisable(disable);
        }
        for (Node node: rightPane.getChildren()) {
            node.setDisable(disable);
        }
        for (Node node: bottomVBox.getChildren()) {
            node.setDisable(disable);
        }
        topHBox.setDisable(false);
        if (tableViewObservableList.size()==0) {
            if (!disable) {
                xaridniYakunlaButton.setDisable(true);
            }
        }
        navbatdagiXaridorButton.setDisable(!disable);
        xaridnBekorQilButton.setDisable(disable);
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
    private void setUser(Kassa kassa) {
        String serialNumber = ConnectionType.getAloqa().getText().trim();
        if (kassa != null) {
            user.setPulHisobi(kassa.getPulHisobi());
            user.setTovarHisobi(kassa.getTovarHisobi());
            user.setXaridorHisobi(kassa.getXaridorHisobi());
            user.setValuta(kassa.getValuta());
        }
    }
    private HisobKitob yangiSavdoXususiyatlari(Kassa kassa) {
        HisobKitob hisobKitob = new HisobKitob();
        hisobKitob.setQaydId(0);
        hisobKitob.setHujjatId(0);
        Integer savdoTuriInteger = kassa.getSavdoTuri();
        hisobKitob.setAmal(4);
        hisobKitob.setHisob1(kassa.getTovarHisobi());
        hisobKitob.setHisob2(kassa.getXaridorHisobi());
        hisobKitob.setValuta(valuta.getId());
        hisobKitob.setTovar(0);
        hisobKitob.setKurs(1d);
        hisobKitob.setBarCode("");
        hisobKitob.setDona(0d);
        hisobKitob.setNarh(0d);
        hisobKitob.setManba(0);
        hisobKitob.setIzoh(" Savdo №: ");
        hisobKitob.setUserId(user.getId());
        hisobKitob.setDateTime(new Date());
        return hisobKitob;
    }
    private ObservableList<HisobKitob> yangiSavdo(Kassa kassa) {
        ObservableList<HisobKitob> observableList = FXCollections.observableArrayList();
        HisobKitob savdoXossalari = yangiSavdoXususiyatlari(kassa);
        observableList.add(savdoXossalari);
        // Tovarlar
        // Tolov birlik va milliy valutalarda. Plastic
        ObservableList<HisobKitob> tolovList = yangiTolovHisobKitob(kassa);
        observableList.addAll(tolovList);
        // Qaytim birlik va milliy valutalarda
        ObservableList<HisobKitob> qaytimList = yangiQaytimHisobKitob(kassa);
        observableList.addAll(qaytimList);
        // Chegirma birlik va milliy valutalarda
        ObservableList<HisobKitob> chegirmaList = yangiChegirmaHisobKitob(kassa);
        observableList.addAll(chegirmaList);
        return observableList;
    }
    private ObservableList<HisobKitob> yangiTolovHisobKitob(Kassa kassa) {
        Integer amal = 7;
        ObservableList<HisobKitob> observableList = FXCollections.observableArrayList();
        for (Valuta valuta: valutaObservableList) {
            if (valuta.getStatus()<3) {
                HisobKitob hisobKitob = new HisobKitob();
                hisobKitob.setQaydId(0);
                hisobKitob.setHujjatId(0);
                hisobKitob.setHisob1(kassa.getXaridorHisobi());
                hisobKitob.setHisob2(kassa.getPulHisobi());
                hisobKitob.setAmal(amal);
                hisobKitob.setValuta(valuta.getId());
                hisobKitob.setUserId(user.getId());
                hisobKitob.setKurs(getKurs(valuta.getId(), new Date()).getKurs());
                observableList.add(hisobKitob);
            }
        }
        return observableList;
    }
    private ObservableList<HisobKitob> yangiQaytimHisobKitob(Kassa kassa) {
        Integer amal = 8;
        ObservableList<HisobKitob> observableList = FXCollections.observableArrayList();
        for (Valuta valuta: valutaObservableList) {
            if (valuta.getStatus()<3) {
                HisobKitob hisobKitob = new HisobKitob();
                hisobKitob.setQaydId(0);
                hisobKitob.setHujjatId(0);
                hisobKitob.setHisob1(kassa.getPulHisobi());
                hisobKitob.setHisob2(kassa.getXaridorHisobi());
                hisobKitob.setAmal(amal);
                hisobKitob.setValuta(valuta.getId());
                hisobKitob.setUserId(user.getId());
                hisobKitob.setKurs(getKurs(valuta.getId(), new Date()).getKurs());
                observableList.add(hisobKitob);
            }
        }
        return observableList;
    }
    private ObservableList<HisobKitob> yangiChegirmaHisobKitob(Kassa kassa) {
        Integer amal = 13;
        ObservableList<HisobKitob> observableList = FXCollections.observableArrayList();
        for (Valuta valuta: valutaObservableList) {
            if (valuta.getStatus()<3) {
                HisobKitob hisobKitob = new HisobKitob();
                hisobKitob.setQaydId(0);
                hisobKitob.setHujjatId(0);
                hisobKitob.setHisob1(kassa.getXaridorHisobi());
                int chegirmaHisobi = hisobKitobModels.yordamchiHisob(connection, kassa.getTovarHisobi(), "ChegirmaGuruhi", "Chegirma");
                hisobKitob.setHisob2(chegirmaHisobi);
                hisobKitob.setAmal(amal);
                hisobKitob.setValuta(valuta.getId());
                hisobKitob.setUserId(user.getId());
                hisobKitob.setKurs(getKurs(valuta.getId(), new Date()).getKurs());
                observableList.add(hisobKitob);
            }
        }
        return observableList;
    }
    private void tovarBoxRefresh2() {
        StandartModels standartModels = new StandartModels("Tovar");
        tovarObservableList = standartModels.get_data(connection);
        tovarBox.setNewList(tovarObservableList);
        AutoCompletionBinding<Standart> tovarBinding = tovarBox.getTovarBinding();
        EventHandler<AutoCompletionBinding.AutoCompletionEvent<Standart>> bindingHandler = new EventHandler<AutoCompletionBinding.AutoCompletionEvent<Standart>>() {
            @Override
            public void handle(AutoCompletionBinding.AutoCompletionEvent<Standart> event) {
                Standart newValue = event.getCompletion();
                if (newValue != null) {
                    tovar = newValue;
                    tovarniYangila(tovar);
                }
            }
        };

        tovarBox.setBindingEvent(bindingHandler);
    }
    private void addTovar2(BarCode barCode) {
        Integer narhTuri = narhComboBox.getValue().getId();
        standartModels.setTABLENAME("Tovar");
        Standart tovar = GetDbData.getTovar(barCode.getTovar());
        if (tovar == null) {
            Alerts.AlertString("Tovar id: " + barCode.getTovar() + "\nBu tovar bazada yo`q");
            return;
        }
        HisobKitob hisobKitob = new HisobKitob(0, 0, 0, 2, hisob2.getId(),
                hisob1.getId(), valuta.getId(), tovar.getId(), 1.0, barCode.getBarCode(), 1.0,
                0.00, 0, tovar.getText(), user.getId(), new Date()
        );
        if (valuta.getStatus() == 1) {
            hisobKitob.setKurs(1.00);
        } else {
            hisobKitob.setKurs(getKurs(hisobKitob.getValuta(), hisobKitob.getDateTime()).getKurs());
        }

        standart4Models.setTABLENAME("Nds");
        Standart4 ndsStandart4 = standart4Models.getTartibForDate(connection, hisobKitob.getTovar(), new Date(), "dateTime desc");
        double nds = 0.0;
        if (ndsStandart4 != null) {
            nds = ndsStandart4.getMiqdor() * 0.01;
        }

        double narhDouble = 0d;
        double narhDoubleFinal = 0d;
        Standart3Models standart3Models = new Standart3Models();
        standart3Models.setTABLENAME("TGuruh2");
        Standart3 s3 = null;
        ObservableList<Standart3> s3List = standart3Models.getAnyData(connection, "id3 = " + tovar.getId(), "");
        if (s3List.size()>0) {
            s3 = s3List.get(0);
            Standart6Models standart6Models = new Standart6Models("TGuruh1");
            Standart6 s6 = standart6Models.getWithId(connection, s3.getId2());
            if (s6 != null) {
                if (narhTuri == 1) {
                    narhDouble = s6.getChakana();
                } else if (narhTuri == 2) {
                    narhDouble = s6.getUlgurji();
                }
            }
        }
        else  {
            TovarNarhi tn = null;
            if (narhTuri == 1) {
                tn = yakkaTovarNarhi(hisobKitob.getTovar(), 1);
            } else {
                tn = yakkaTovarNarhi(hisobKitob.getTovar(), 2);
            }
            if (tn != null) {
                narhDouble = tn.getNarh();
            }
        }

        if (narhDouble == 0d) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Diqqat qiling!!");
            alert.setHeaderText(tovar.getText() + " uchun narh tayinlanmagan. Shu sabab ro`yhatga qo`shilmaydi");
            alert.setContentText(tovar.getText() + " uchun narhni sistema noziri tayin etadi");
            alert.showAndWait();
            return;
        }

        double pochkadaDona = tovarDonasi(barCode);
        if (narhTuri == 1) {
            if (valuta.getStatus() == 1) {
                narhDoubleFinal = pochkadaDona * narhDouble * (1 + nds);
            } else {
                narhDoubleFinal = yahlitla(pochkadaDona * (narhDouble * hisobKitob.getKurs() * (1 + nds)), 1);
            }
        } else if (narhTuri == 2) {
            if (valuta.getStatus() == 1) {
                narhDoubleFinal = pochkadaDona * narhDouble * (1 + nds);
            } else {
                narhDoubleFinal = yahlitla(pochkadaDona * (narhDouble * hisobKitob.getKurs() * (1 + nds)), 1);
            }
        }

        hisobKitob.setNarh(narhDoubleFinal);
        double adad = addTovarToTable2(barCode, hisobKitob);
        refreshTableData();
        tovarTableView.refresh();
        tovarBox.getTextField().setText("");
        toggleGroup.selectToggle(toggleGroup.getToggles().get(0));
        tovarAmali = 4;
        tovarBoxRefresh();
    }
    private Double addTovarToTable2(BarCode barCode, HisobKitob hisobKitob) {
        ObservableList<HisobKitob> observableList = tovarTableView.getItems();
        double adad = 1.0;
        HisobKitob hk1 = null;
        for (HisobKitob hk : observableList) {
            if (hk.getBarCode().equals(barCode.getBarCode())) {
                hk1 = hk;
                adad += hk.getDona();
                break;
            }
        }
        if (hk1 == null) {
            observableList.add(hisobKitob);
        } else {
            hk1.setDona(adad);
        }
        return adad;
    }

}
