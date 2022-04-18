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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
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
import sample.Model.*;
import sample.Tools.*;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

public class Sotuvchi6 extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    MenuBar mainMenu;
    TabPane tabPane;
    TreeView<TreeItemClass> treeView = new TreeView();
    TableView<HisobKitob> tovarTableView = new TableView();
    TableView<HisobKitob> oldiBerdiJadvali;
    TableView<HisobKitob> sotibOlishJadvali;

    VBox rightPane = new VBox();
    VBox centerPane = new VBox();
    HBox topHBox = new HBox();
    VBox jamiVBox = new VBox();
    VBox bottomVBox = new VBox();
    Boolean sotildi = false;

    TextField tovarTextField = new TextField();
    TextField barCodeTextField = new TextField();
    Text jamiText;
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

    HBoxTextFieldPlusButton hisob1HBox = new HBoxTextFieldPlusButton();
    HBoxTextFieldPlusButton hisob2HBox = new HBoxTextFieldPlusButton();
    TovarBox tovarBox;
    HBoxTextFieldPlusButton valutaHBox = new HBoxTextFieldPlusButton();
    Button navbatdagiXaridorButton = new Button("Navbatdagi xaridor");
    Button xaridniBekorQilButton = new Button("Xaridni bekor qil");
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
    DecimalFormat decimalFormat = new MoneyShow();
    Font font = Font.font("Arial",16);
    Font font1 = Font.font("Arial", FontWeight.BOLD,16);
    Font buttonFont1 = Font.font("Arial", FontWeight.BOLD,16);
    Font jamiFont = Font.font("Arial", FontWeight.BOLD,24);
    StringBuffer stringBuffer = new StringBuffer();

    ObservableList<HisobKitob> tableViewObservableList = FXCollections.observableArrayList();
    ObservableList<HisobKitob> naqdTolovRoyxati = FXCollections.observableArrayList();
    ObservableList<HisobKitob> bankToloviRoyxati = FXCollections.observableArrayList();
    ObservableList<HisobKitob> qaytimRoyxati = FXCollections.observableArrayList();
    ObservableList<HisobKitob> chegirmaRoyxati = FXCollections.observableArrayList();
    ObservableList<HisobKitob> qoshimchaDaromadRoyxati = FXCollections.observableArrayList();
    ObservableList<HisobKitob> oldiBerdiRoyxati = FXCollections.observableArrayList();
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

    public Sotuvchi6() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        loginUserController = new LoginUserController(connection);
        user = loginUserController.login();
        GetDbData.initData(connection);
    }

    public Sotuvchi6(Connection connection, User user) {
        this.connection = connection;
        loginUserController = new LoginUserController(connection, user);
        this.user = user;
        ibtido();
    }

    public Sotuvchi6(Connection connection, User user, QaydnomaData qaydnomaData) {
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
    }

    private void initTopPane() {
        initNavbatdagiXaridorButton();
        initXaridorBekorQilButton();
        HBox.setHgrow(topHBox, Priority.ALWAYS);
        topHBox.getChildren().addAll(navbatdagiXaridorButton, xaridniBekorQilButton);
    }

    private void initCenterPane() {
        centerPane = new VBox();
        initTopPane();
        tabPane = yangiTabPane();
        oldiBerdiJadvali = yangiOldiBerdiJadvali();
        initXaridniYakunlaButton();
        SetHVGrow.VerticalHorizontal(centerPane);
        centerPane.getChildren().addAll(topHBox, tabPane, oldiBerdiJadvali, xaridniYakunlaButton);
    }

    private TabPane yangiTabPane() {
        TabPane tabPane = new TabPane();
        Tab tab1 = new Tab("Sotiladi");
        Tab tab2 = new Tab("Sotib olinadi");
        tabPane.getTabs().addAll(tab1, tab2);
        initTableView();
        sotibOlishJadvali = yangiSotibOlishJadvali();
        tab1.setContent(tovarTableView);
        tab2.setContent(sotibOlishJadvali);
        tab1.setClosable(false);
        tab2.setClosable(false);
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.getText().equals("Sotiladi")) {
                    tovarBoxRefresh();
                } else {
                    tovarBoxRefresh2();
                }
            }
        });
        SetHVGrow.VerticalHorizontal(tabPane);
        return tabPane;
    }

    private VBox jamiVBox() {
        VBox vBox = new VBox(2);
        HBox.setHgrow(vBox, Priority.ALWAYS);
        vBox.setPadding(new Insets(padding));
        kelishmaTextField = initKelishmaTextField();
        kelishmaTextField.setFont(jamiFont);
        jamiText = initJamiText();
        vBox.setAlignment(Pos.CENTER);
        vBox.setDisable(true);
        return vBox;
    }

    private TextField initKelishmaTextField() {
        TextField textField = new TextField();
        textField.setFont(font1);
        textField.setPromptText(decimalFormat.format(jamiMablag));
        textField.setAlignment(Pos.CENTER_LEFT);
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                royxatlarniOchir();
                Double ayirma = jamiMablag - StringNumberUtils.getDoubleFromTextField(kelishmaTextField);
                if (ayirma > 0d) {
                    xossaniYangila();
                    royxatgaYoz(chegirmaRoyxati, ayirma, valuta.getId());
                } else if (ayirma< 0d) {
                    royxatgaYoz(qoshimchaDaromadRoyxati, -ayirma, valuta.getId());
                }
                balansHisobi(oldiBerdiJadvali.getItems());
                oldiBerdiJadvali.refresh();
            }
        });
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (!newValue) {
                    Double d1 = StringNumberUtils.getDoubleFromTextField(textField);
                    textField.setText(decimalFormat.format(d1));
                }
            }
        });
        return textField;
    }

    private Text initJamiText() {
        Text text = new Text();
        text.setFont(jamiFont);
        return text;
    }

    private void initRightPane() {
        jamiVBox = jamiVBox();
        initTreeView();
        oldiBerdiRoyxati = yangiMalumotlar();
        SetHVGrow.VerticalHorizontal(rightPane);
        rightPane.getChildren().addAll(treeView, jamiVBox);
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
        tovarTableView.getColumns().addAll(getAmalColumn(), getTovarColumn(), getTaqdimColumn(), getAdadColumn(), getNarhColumn(), getSummaColumn(), getDeleteColumn());
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
                    jamiVBox.setDisable(true);
                    xossaniYangila();
                    oldiBerdiJadvali.setDisable(true);
                    kelishmaTextField.setText("");
                }

                if (c.getList().size() > 0) {
                    xaridniYakunlaButton.setDisable(false);
                    oldiBerdiJadvali.setDisable(false);
                    jamiVBox.setDisable(false);
                    xossaniYangila();
                    oldiBerdiJadvali.refresh();
                }
            }
        });
        if (tableViewObservableList.size()>0) {
            tovarTableView.getSelectionModel().selectFirst();
        }
    }

    private TableView<HisobKitob> yangiSotibOlishJadvali() {
        TableView<HisobKitob> tableView = new TableView<>();
        ObservableList<HisobKitob> observableList = FXCollections.observableArrayList();
        SetHVGrow.VerticalHorizontal(tableView);
        tableView.getColumns().addAll(getAmalColumn(), getTovarColumn(), getTaqdimColumn(), getAdad2Column(), getNarh2Column(), getSummaColumn(), getDeleteColumn());
        tableView.setEditable(true);
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
            }
        });
        tableView.setItems(observableList);
        return tableView;
    }

    private  void xossaniYangila() {
        HisobKitob hisobKitob = oldiBerdiJadvali.getItems().get(0);
        Double jami = 0d;
        KursModels kursModels = new KursModels();
        Double d1 = kursModels.getKurs(connection, valuta.getId(), new Date(), "sana desc").getKurs();
        for (HisobKitob hk: tableViewObservableList) {
            jami += hk.getDona() * hk.getNarh();
        }
        hisobKitob.setValuta(valuta.getId());
        hisobKitob.setKurs(d1);
        hisobKitob.setNarh(jami);
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
            double barCodeCount = hisobKitobModels.getBarCodeCount(connection, hisobKitob.getHisob1(), hisobKitob.getBarCode());
            if (barCodeCount >= event.getNewValue()) {
                hisobKitob.setDona(event.getNewValue());
            } else {
                hisobKitob.setDona(event.getOldValue());
                Alerts.showKamomat(tovar, event.getNewValue(), hisobKitob.getBarCode(), barCodeCount);
            }
            refreshTableData();
            event.getTableView().refresh();
        });
        adad.setStyle( "-fx-alignment: CENTER;");
        return adad;
    }
    private TableColumn<HisobKitob, Double> getAdad2Column() {
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
            hisobKitob.setDona(event.getNewValue());
            refreshTableData();
            event.getTableView().refresh();
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
        narh.setOnEditCommit((TableColumn.CellEditEvent<HisobKitob, Double> event) -> {
            Double newValue = event.getNewValue();
            HisobKitob hisobKitob = event.getRowValue();
            BarCode barCode = GetDbData.getBarCode(hisobKitob.getBarCode());
            Standart tovar = GetDbData.getTovar(hisobKitob.getTovar());
            if (newValue != null) {
                hisobKitob.setNarh(newValue);
                if (narhComboBox.getValue().getId() == 1) {
                    Double yangiNarhDouble = newValue / tovarDonasi(barCode) / hisobKitob.getKurs();
//                    narhYoz(tovar.getId(), 1, yangiNarhDouble);
                }
            }
            refreshTableData();
            event.getTableView().refresh();
//            jamiHisob(event.getTableView().getItems());
        });
        return narh;
    }
    private TableColumn<HisobKitob, Double> getNarh2Column() {
        TableColumn<HisobKitob, Double>  narh = new TableColumn<>("Narh");
        narh.setMinWidth(100);
        narh.setMaxWidth(100);
        narh.setCellValueFactory(new PropertyValueFactory<>("narh"));
        narh.setStyle( "-fx-alignment: CENTER;");
        narh.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
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
        narh.setOnEditCommit((TableColumn.CellEditEvent<HisobKitob, Double> event) -> {
            Double newValue = event.getNewValue();
            HisobKitob hisobKitob = event.getRowValue();
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
                InputStream inputStream = getClass().getResourceAsStream("/sample/images/Icons/delete.png");
                Image image = new Image(inputStream);
                ImageView imageView = new ImageView(image);
                b.setGraphic(imageView);
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                image = null;

                b.setOnAction(event -> {
                    param.getTableView().getItems().remove(hisobKitob);
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
    private TableColumn<HisobKitob, String> getTaqdimColumn() {
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
        TreeItem<TreeItemClass> rootTreeItem = new TreeItem(getRootTreeItem());
        rootTreeItem.getChildren().addAll(
                getSozlovTreeItem(),    //40
                getTovarTreeItem(),
                getJamiLabelTreeItem(),
                getKelishmaLabelTreeItem()
        );

        treeView.setRoot(rootTreeItem);
        treeView.setShowRoot(false);
        treeView.setMaxWidth(280);
    }

    private TreeItem<TreeItemClass> getRootTreeItem() {
        Standart standart = new Standart(0, "Asosiy", user.getId(), new Date());
        TreeItemClass treeItemClass = new TreeItemClass(standart);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            TreeItemClass treeItemClass2 = newValue.getValue();
            if ( treeItemClass2 != null) {
                setOnAction(treeItemClass2);
            }
        });
        return treeItem;
    }

    private void setOnAction(TreeItemClass treeItemClass) {
        Hisob pulHisobi = GetDbData.getHisob(kassa.getPulHisobi());
        switch (treeItemClass.getItemId()) {
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

    private TreeItem<TreeItemClass> getSozlovTreeItem() {
        TreeItemClass treeItemClass = new TreeItemClass(40, new Label("Savdo sozlovlari"));
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        Label label = treeItemClass.getLabel();
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

    private TreeItem<TreeItemClass> getHisob1TreeItem() {
        initHisob1HBox();
        TreeItemClass treeItemClass = new TreeItemClass(41, hisob1HBox);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getHisob2TreeItem() {
        initHisob2HBox();
        TreeItemClass treeItemClass = new TreeItemClass(42, hisob2HBox);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getValutaTreeItem() {
        initValutaHBox();
        TreeItemClass treeItemClass = new TreeItemClass(43, valutaHBox);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getNarhTuriTreeItem() {
        initNarhComboBox();
        TreeItemClass treeItemClass = new TreeItemClass(44, narhComboBox);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getTovarTreeItem() {
        TreeItemClass treeItemClass = new TreeItemClass(50, new Label("Tovar"));
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        Label label = treeItemClass.getLabel();
        label.setFont(font);
        treeItem.getChildren().addAll(
                getTovarNomiTreeItem(),
                getBirlikTreeItem(),
                getBarCodeTreeItem()
        );
        treeItem.setExpanded(true);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getTovarNomiTreeItem() {
        TovarBox tovarBox = initTovarBox();
        TreeItemClass treeItemClass = new TreeItemClass(51, tovarBox);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getBirlikTreeItem() {
        initBirlikComboBox();
        TreeItemClass treeItemClass = new TreeItemClass(52, birlikComboBox);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getBarCodeTreeItem() {
        TreeItemClass treeItemClass = new TreeItemClass(53, barCodeTextField);
        TextField textField = treeItemClass.getTextField();
        textField.setPromptText("STRIXKOD");
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                refreshTableData();
            }
        });
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getKelishmaTreeItem() {
        TreeItemClass treeItemClass = new TreeItemClass(91, kelishmaTextField);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getJamiItem() {
        TreeItemClass treeItemClass = new TreeItemClass(81, jamiText);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        Label label = treeItemClass.getLabel();
        label.setFont(font1);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getJamiLabelTreeItem() {
        TreeItemClass treeItemClass = new TreeItemClass(80, new Label("Jami mablag`: "));
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        treeItem.getChildren().add(getJamiTreeItem());
        treeItem.setExpanded(true);
        Label label = treeItemClass.getLabel();
        label.setFont(font);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getJamiTreeItem() {
        TreeItemClass treeItemClass = new TreeItemClass(81, jamiText);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        return treeItem;
    }


    private TreeItem<TreeItemClass> getKelishmaLabelTreeItem() {
        TreeItemClass treeItemClass = new TreeItemClass(70, new Label("Kelishuv: "));
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        treeItem.getChildren().addAll(
                getKelishma1TreeItem()
        );
        treeItem.setExpanded(true);
        Label label = treeItemClass.getLabel();
        label.setFont(font);
        return treeItem;
    }
    private TreeItem<TreeItemClass> getKelishma1TreeItem() {
        TreeItemClass treeItemClass = new TreeItemClass(71, kelishmaTextField);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        TextField textField = treeItemClass.getTextField();
        textField.setPromptText("KELISHUV");
        return treeItem;
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
            String serialNumber = Sotuvchi6.getSerialNumber();
            Kassa kassa = Sotuvchi6.getKassaData(connection, serialNumber);
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
                tolovMalumotlariniYangila(hisob1, hisob2, oldiBerdiJadvali.getItems());
                oldiBerdiJadvali.refresh();
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
                tolovMalumotlariniYangila(hisob1, hisob2, oldiBerdiJadvali.getItems());
                oldiBerdiJadvali.refresh();
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
                tolovMalumotlariniYangila(hisob1, hisob2, oldiBerdiJadvali.getItems());
                oldiBerdiJadvali.refresh();
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
                tolovMalumotlariniYangila(hisob1, hisob2, oldiBerdiJadvali.getItems());
                oldiBerdiJadvali.refresh();
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
                tolovMalumotlariniYangila(hisob1, hisob2, oldiBerdiJadvali.getItems());
                oldiBerdiJadvali.refresh();
            }
        });

        Button addButton = valutaHBox.getPlusButton();
        addButton.setOnAction(event -> {
            Valuta newValuta = addValuta();
            if (newValuta != null) {
                valuta = newValuta;
                textField.setText(valuta.getValuta());
                tolovMalumotlariniYangila(hisob1, hisob2, oldiBerdiJadvali.getItems());
                oldiBerdiJadvali.refresh();
                oldiBerdiJadvali.refresh();
            }
        });
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

    private void navbatdagiXaridor() {
        kassa = getKassaData(connection, getSerialNumber());
        setUser(kassa);
        hisob1 = GetDbData.getHisob(kassa.getTovarHisobi());
        hisob2 = GetDbData.getHisob(kassa.getXaridorHisobi());
        hisob1HBox.getTextField().setText(hisob1.getText());
        hisob2HBox.getTextField().setText(hisob2.getText());
        valuta = GetDbData.getValuta(kassa.getValuta());
        valutaHBox.getTextField().setText(valuta.getValuta());
        oldiBerdiJadvali = yangiOldiBerdiJadvali();
        tableViewObservableList = FXCollections.observableArrayList();
        tovarTableView.setItems(tableViewObservableList);
        tovarTableView.refresh();

        jamiMablag = 0.0;
        refreshTableData();
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

    private void initXaridorBekorQilButton() {
        HBox.setHgrow(xaridniBekorQilButton, Priority.ALWAYS);
        xaridniBekorQilButton.setFont(buttonFont1);
        xaridniBekorQilButton.setMaxWidth(2000);
        xaridniBekorQilButton.setPrefWidth(150);
        xaridniBekorQilButton.setOnAction(event -> {
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
            Boolean sotildi = xaridniYakunla();
            if (sotildi) {
                tovarBoxRefresh();
                xaridniBekorQilButton.fire();
            }
        });
    }

    private void kassaniTozala() {
        tableViewObservableList.removeAll(tableViewObservableList);
        tovarTableView.refresh();
        sotibOlishJadvali.setItems(FXCollections.observableArrayList());
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
        royxatlarniOchir();
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
            if (hisobKitob.getAmal().equals(4)) {
                jamiDouble += hisobKitob.getDona() * hisobKitob.getNarh() * hisobKitob.getKurs() / kursDouble;
            } else {
                jamiDouble -= hisobKitob.getDona() * hisobKitob.getNarh() * hisobKitob.getKurs() / kursDouble;
            }
            tovarBor = true;
        }
        jamiMablag = jamiDouble;
        jamiText.setText(decimalFormat.format(jamiDouble));
        kelishmaTextField.setText(decimalFormat.format(jamiDouble));
        xossaniYangila();
        oldiBerdiJadvali.refresh();
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
                                if (tabPane.getSelectionModel().getSelectedIndex() == 0) {
                                    addTovar(barCode);
                                } else {
                                    addTovar2(barCode);
                                }
                                refreshTableData();
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
                                if (tabPane.getSelectionModel().getSelectedIndex() == 0) {
                                    addTovar(barCode);
                                } else {
                                    addTovar2(barCode);
                                }
                                refreshTableData();
                                refreshTableData();
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
        HisobKitob hisobKitob = new HisobKitob(0, 0, 0, amalTuri, kassa.getTovarHisobi(),
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
        tovarTableView.refresh();
        tovarBox.getTextField().setText("");
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
        sotibOlishJadvali.refresh();
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
                tovarTableView.refresh();
            }
            refreshTableData();
//            jamiHisob(tableViewObservableList);
        }
        return adad;
    }
    private Double addTovarToTable2(BarCode barCode, HisobKitob hisobKitob) {
        ObservableList<HisobKitob> observableList = sotibOlishJadvali.getItems();
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
            sotibOlishJadvali.refresh();
        }
        refreshTableData();
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
                oldiBerdiJadvali.setDisable(true);
                jamiVBox.setDisable(true);
                xaridniYakunlaButton.setDisable(true);
            }
        }
        navbatdagiXaridorButton.setDisable(!disable);
        xaridniBekorQilButton.setDisable(disable);
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

    public class TreeItemClass extends HBox{
        private Integer itemId;
        private Double aDouble;
        private Label label = new Label();
        private Label label2 = new Label();
        private Standart standart;
        private HBoxTextFieldPlusButton hisobHBox;
        private TovarBox tovarBox;
        private Button button;
        private Button button2;
        private TextField textField;
        private TextField textField2;
        private TextField kursTextField;
        private ComboBox<Standart> comboBox;
        private Separator separator;
        Valuta valuta;
        TextFieldButton textFieldButton;
        TextFieldButton textFieldButton1;
        HisobKitob hisobKitob;
        Text text;

        public TreeItemClass(Integer itemId, String string, Double aDouble) {
            super(5);
            this.itemId = itemId;
            this.aDouble = aDouble;
            label.setText(string);
            label2.setText(decimalFormat.format(aDouble));

            this.getChildren().addAll(label, label2);
            this.setAlignment(Pos.CENTER_LEFT);
        }

        public TreeItemClass(Integer itemId, Text text) {
            super(5);
            this.itemId = itemId;
            this.text = text;
            this.getChildren().add(text);
        }

        public TreeItemClass(Integer itemId, TextField textField) {
            super(5);
            this.itemId = itemId;
            this.textField = textField;
            textField.setMaxWidth(200);

            this.getChildren().addAll(textField);
            this.setAlignment(Pos.CENTER_LEFT);
        }

        public TreeItemClass(Integer itemId, HisobKitob hisobKitob) {
            super(5);
            this.itemId = itemId;
            this.hisobKitob = hisobKitob;
            textField = new TextField();
            textField.setFont(font1);
            valuta = GetDbData.getValuta(hisobKitob.getValuta());
            textField.setPromptText(valuta.getValuta());
            this.getChildren().addAll(textField);
            this.setAlignment(Pos.CENTER_LEFT);
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    Double aDouble = StringNumberUtils.textToDouble(newValue);
                    hisobKitob.setNarh(aDouble);
                }
            });
            textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    if (newValue) {
                        textField.selectAll();
                    } else {
                        textField.setText(decimalFormat.format(hisobKitob.getNarh()));
                    }
                }
            });
        }

        public TreeItemClass(Integer itemId, TextField textField, TextField kursTextField, Valuta valuta) {
            super(2);
            this.itemId = itemId;
            this.textField = textField;
            this.kursTextField = kursTextField;
            textField.setMaxWidth(135);
            kursTextField.setMaxWidth(59);
            Kurs kurs = getKurs(valuta.getId(), new Date());
            Double kursDouble = 0d;
            initTextField(textField);
            if (kurs != null) {
                kursDouble = kurs.getKurs();
            }
            kursTextField.setText(decimalFormat.format(kursDouble));
            this.getChildren().addAll(textField, kursTextField);
            this.setAlignment(Pos.CENTER_LEFT);
        }

        public TreeItemClass(Integer itemId, TextField textField, TextField kursTextField, Valuta valuta, Boolean calculatorBormi) {
            super(2);
            HBox hBox = new HBox();
            HBox hBox2 = new HBox();
            VBox vBox = new VBox();

            this.itemId = itemId;
            this.textField = textField;
            this.kursTextField = kursTextField;
            hBox.getChildren().addAll(textField, kursTextField);
            vBox.getChildren().add(hBox);
            if (calculatorBormi) {
                button = new Button("", new PathToImageView("/sample/images/Icons/calculator.png").getImageView());
                button.setMaxWidth(97);
                button.setPrefWidth(150);
                HBox.setHgrow(button, Priority.ALWAYS);
                button2 = new Button("<=>");
                button2.setMaxWidth(97);
                HBox.setHgrow(button2, Priority.ALWAYS);
                hBox2.getChildren().addAll(button, button2);
                vBox.getChildren().add(hBox2);
            }
            textField.setMaxWidth(135);
            kursTextField.setMaxWidth(59);
            Kurs kurs = getKurs(valuta.getId(), new Date());
            Double kursDouble = 0d;
            initTextField(textField);
            if (kurs != null) {
                kursDouble = kurs.getKurs();
            }
            kursTextField.setText(decimalFormat.format(kursDouble));
            this.getChildren().add(vBox);
            this.setAlignment(Pos.CENTER_LEFT);
        }

        public TreeItemClass(Integer itemId, TextField textField, TextField textField2) {
            super(2);
            this.itemId = itemId;
            this.textField = textField;
            this.textField2 = textField2;
            textField.setMaxWidth(97);
            textField2.setMaxWidth(97);
            textField.setAlignment(Pos.CENTER);
            textField2.setAlignment(Pos.CENTER);
            this.getChildren().addAll(textField, textField2);
        }

        public TreeItemClass(Integer itemId, ComboBox comboBox) {
            super(5);
            this.itemId = itemId;
            this.comboBox = comboBox;
            comboBox.setMaxWidth(200);

            this.getChildren().addAll(comboBox);
            this.setAlignment(Pos.CENTER_LEFT);
        }

        public TreeItemClass(Integer itemId, Label label) {
            super();
            this.itemId = itemId;
            this.label = label;
            label.setMaxWidth(200);

            this.getChildren().add(label);
            this.setAlignment(Pos.CENTER_LEFT);
        }

        public TreeItemClass(Integer itemId, Separator separator) {
            super();
            this.itemId = itemId;
            this.separator = separator;
            SetHVGrow.VerticalHorizontal(separator);

            this.getChildren().add(separator);
            this.setAlignment(Pos.CENTER_LEFT);
        }

        public TreeItemClass(Standart standart) {
            super();
            this.standart = standart;
            itemId = standart.getId();
            label.setText(standart.getText());
            label.setMaxWidth(200);
            this.getChildren().add(label);
            this.setAlignment(Pos.CENTER_LEFT);
        }

        public TreeItemClass(Integer itemId, HBoxTextFieldPlusButton hisobHBox) {
            super();
            this.itemId = itemId;
            this.hisobHBox = hisobHBox;
            hisobHBox.setMaxWidth(200);
            this.getChildren().add(hisobHBox);
            this.setAlignment(Pos.CENTER_LEFT);
        }

        public TreeItemClass(Integer itemId, TovarBox tovarBox) {
            super();
            this.itemId = itemId;
            this.tovarBox = tovarBox;
            tovarBox.setMaxWidth(200);
            this.getChildren().add(tovarBox);
            this.setAlignment(Pos.CENTER_LEFT);
        }

        public TreeItemClass(Integer itemId, Button button) {
            super();
            this.itemId = itemId;
            this.button = button;
            button.setMaxWidth(210);
            this.getChildren().add(button);
            this.setAlignment(Pos.CENTER_LEFT);
        }

        public void initTextField(TextField textField) {
            textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    if (!newValue) {
                        Double aDouble = StringNumberUtils.getDoubleFromTextField(textField);
                        textField.setText(decimalFormat.format(aDouble));
                    }
                }
            });
        }

        public Integer getItemId() {
            return itemId;
        }

        public void setItemId(Integer itemId) {
            this.itemId = itemId;
        }

        public TextField getTextField() {
            return textField;
        }

        public void setTextField(TextField textField) {
            this.textField = textField;
        }

        public Label getLabel() {
            return label;
        }

        public void setLabel(Label label) {
            this.label = label;
        }

        public TextField getKursTextField() {
            return kursTextField;
        }

        public void setKursTextField(TextField kursTextField) {
            this.kursTextField = kursTextField;
        }

        public TextField getTextField2() {
            return textField2;
        }

        public void setTextField2(TextField textField2) {
            this.textField2 = textField2;
        }

        public Button getButton() {
            return button;
        }

        public void setButton(Button button) {
            this.button = button;
        }

        public Button getButton2() {
            return button2;
        }

        public void setButton2(Button button2) {
            this.button2 = button2;
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
        hisobKitob.setKurs(getKurs(valuta.getId(), new Date()).getKurs());
        hisobKitob.setBarCode("");
        hisobKitob.setDona(0d);
        hisobKitob.setNarh(0d);
        hisobKitob.setManba(0);
        hisobKitob.setIzoh(" Savdo №: ");
        hisobKitob.setUserId(user.getId());
        hisobKitob.setDateTime(new Date());
        return hisobKitob;
    }

    private TableView<HisobKitob> yangiOldiBerdiJadvali() {
        TableView<HisobKitob> tableView = new TableView<>();
        SetHVGrow.VerticalHorizontal(tableView);
        TableViewAndoza tableViewAndoza = new TableViewAndoza();
        TableColumn<HisobKitob, Integer> amalColumn = tableViewAndoza.getAmalColumn();
        amalColumn.setStyle( "-fx-alignment: CENTER;");
        TableColumn<HisobKitob, Integer> valutaColumn = tableViewAndoza.getValutaColumn();
        valutaColumn.setStyle( "-fx-alignment: CENTER;");

        tableView.getColumns().addAll(
                amalColumn,
                hisob1Hisob2(),
                valutaKurs(),
                narh(),
                tableViewAndoza.getSummaColumn(),
                tableViewAndoza.getBalans2Column(),
                calc()
        );
        tableView.setItems(oldiBerdiRoyxati);
        tableView.setEditable(true);

        return tableView;
    }

    private ObservableList<HisobKitob> yangiMalumotlar() {
        ObservableList<HisobKitob> observableList = oldiBerdiRoyxati(hisob1, hisob2);
        return observableList;
    }

    private ObservableList<HisobKitob> oldiBerdiRoyxati(Hisob hisob1, Hisob hisob2) {
        ObservableList<HisobKitob> observableList = FXCollections.observableArrayList();
        observableList.add(yangiSavdoXususiyatlari(kassa));

        Hisob naqdTolov = hisobModels.pulHisobi(connection, user, hisob1);
        naqdTolovRoyxati = pulRoyxati(7,  hisob2,naqdTolov);
        observableList.addAll(naqdTolovRoyxati);

        Hisob bankTolovi = hisobModels.bankHisobi(connection, hisob1);
        bankToloviRoyxati = pulRoyxati(15, hisob2, bankTolovi);
        observableList.addAll(bankToloviRoyxati);

        Hisob qaytim1 = hisobModels.pulHisobi(connection, user, hisob1);
        qaytimRoyxati = pulRoyxati(8, qaytim1, hisob2);
        observableList.addAll(qaytimRoyxati);

        Hisob chegirma = hisobModels.chegirmaHisobi(connection, hisob1);
        chegirmaRoyxati = pulRoyxati(13, hisob2, chegirma);
        observableList.addAll(chegirmaRoyxati);

        Hisob qoshimchaDaromad1 = hisobModels.qoshimchaDaromadHisobi(connection, hisob1);
        qoshimchaDaromadRoyxati = pulRoyxati(18, qoshimchaDaromad1, hisob2);
        observableList.addAll(qoshimchaDaromadRoyxati);

        return observableList;
    }

    private ObservableList<HisobKitob> pulRoyxati(Integer amal, Hisob hisob1, Hisob hisob2) {
        ObservableList<HisobKitob> observableList = FXCollections.observableArrayList();
        ValutaModels valutaModels = new ValutaModels();
        ObservableList<Valuta> valutaObservableList = valutaModels.getAnyData(connection, "status<3", "");
        for (Valuta v: valutaObservableList) {
            HisobKitob hisobKitob = new HisobKitob();
            hisobKitob.setHisob1(hisob1.getId());
            hisobKitob.setHisob2(hisob2.getId());
            hisobKitob.setAmal(amal);
            hisobKitob.setValuta(v.getId());
            Double kursDouble = 1d;
            Kurs kurs = getKurs(v.getId(), new Date());
            if (kurs != null) {
                kursDouble = kurs.getKurs();
            }
            hisobKitob.setKurs(kursDouble);
            observableList.add(hisobKitob);
        }
        return observableList;
    }

    private TableColumn<HisobKitob, DoubleTextBox> valutaKurs() {
        TableColumn<HisobKitob, DoubleTextBox> valutaKurs = new TableColumn<>("Valuta/Kurs");
        valutaKurs.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, DoubleTextBox>, ObservableValue<DoubleTextBox>>() {

            @Override
            public ObservableValue<DoubleTextBox> call(TableColumn.CellDataFeatures<HisobKitob, DoubleTextBox> param) {
                DecimalFormat decimalFormat = new MoneyShow();
                HisobKitob hisobKitob = param.getValue();
                Valuta valuta = GetDbData.getValuta(hisobKitob.getValuta());
                Double narhKurs = hisobKitob.getTovar() > 0 ? hisobKitob.getDona()*hisobKitob.getNarh() : hisobKitob.getNarh();
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
        valutaKurs.setMinWidth(150);
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
        valutaKurs.setMinWidth(150);
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
                Text text2 = new Text(hisob2.getText());
                text2.setFill(Color.BLUE);
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
    private TableColumn<HisobKitob, Double> narh() {
        TableColumn<HisobKitob, Double>  narh = new TableColumn<>("Narh");
        narh.setMinWidth(100);
        narh.setMaxWidth(100);
        narh.setCellValueFactory(new PropertyValueFactory<>("narh"));
        narh.setStyle( "-fx-alignment: CENTER;");
        narh.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
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
        narh.setOnEditCommit((TableColumn.CellEditEvent<HisobKitob, Double> event) -> {
            Double newValue = event.getNewValue();
            HisobKitob hisobKitob = event.getRowValue();
            if (newValue != null) {
                if (!hisobKitob.getAmal().equals(4)) {
                    hisobKitob.setNarh(newValue);
                    narhHisobi2(hisobKitob);
                    balansHisobi(oldiBerdiJadvali.getItems());
                }
                event.getTableView().refresh();
            }
        });
        return narh;
    }
    private TableColumn<HisobKitob, Button> calc() {
        TableColumn<HisobKitob, Button> deleteColumn = new TableColumn<>("Hisobla");
        deleteColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, Button>, ObservableValue<Button>>() {

            @Override
            public ObservableValue<Button> call(TableColumn.CellDataFeatures<HisobKitob, Button> param) {
                HisobKitob hisobKitob = param.getValue();
                Button b = new Button();
                b.setMaxWidth(2000);
                b.setPrefWidth(150);
                b.setMaxHeight(2000);
                b.setPrefHeight(20);
                HBox.setHgrow(b, Priority.ALWAYS);
                VBox.setVgrow(b, Priority.ALWAYS);
                b.setGraphic(new PathToImageView("/sample/images/Icons/calculator.png", 24, 24).getImageView());
                b.setOnAction(event -> {
                    narhHisobi(hisobKitob);
                    balansHisobi(oldiBerdiJadvali.getItems());
                    param.getTableView().refresh();
                });
                return new SimpleObjectProperty<Button>(b);
            }
        });

        deleteColumn.setMinWidth(20);
        deleteColumn.setMaxWidth(100);
        deleteColumn.setStyle( "-fx-alignment: CENTER;");
        return deleteColumn;
    }

    private void tolovMalumotlariniYangila(Hisob hisob1, Hisob hisob2, ObservableList<HisobKitob> observableList) {
        for (HisobKitob hisobKitob: observableList) {
            Hisob h1 = null;
            Hisob h2 = null;
            switch (hisobKitob.getAmal()) {
                case 4: // xossalar
                    h1 = hisob1;
                    h2 = hisob2;
                    hisobKitob.setValuta(valuta.getId());
                    hisobKitob.setKurs(getKurs(valuta.getId(), new Date()).getKurs());
                    break;
                case 7: // naqd to`lov
                    h1 = hisob2;
                    h2 = hisobModels.pulHisobi(connection, user, hisob1);
                    break;
                case 8: //qaytim
                    h1 = hisobModels.pulHisobi(connection, user, hisob1);
                    h2 = hisob2;
                    break;
                case 13: // chegirma
                    h1 = hisob2;
                    h2 = hisobModels.chegirmaHisobi(connection, hisob1);
                    break;
                case 15: // bankdan to`lov
                    h1 = hisob2;
                    h2 = hisobModels.bankHisobi(connection, hisob1);
                    break;
                case 18: //qo`shimcha daromad
                    h1 = hisobModels.qoshimchaDaromadHisobi(connection, hisob1);
                    h2 = hisob2;
                    break;
            }
            hisobKitob.setHisob1(h1.getId());
            hisobKitob.setHisob2(h2.getId());
        }
    }

    private Double balansHisobi(ObservableList<HisobKitob> observableList) {
        Double balance = 0d;
        if (observableList.size()>0) {
            for (HisobKitob hisobKitob: observableList) {
                switch (hisobKitob.getAmal()) {
                    case 4:
                        balance -= hisobKitob.getSummaCol();
                        break;
                    case 7: //tolov
                        balance += hisobKitob.getSummaCol();
                        break;
                    case 8: //qaytim
                        balance -= hisobKitob.getSummaCol();
                        break;
                    case 13: // chegirma
                        balance += hisobKitob.getSummaCol();
                        break;
                    case 15: //bank tolovi
                        balance += hisobKitob.getSummaCol();
                        break;
                    case 18: //qoshimcha daromad
                        balance -= hisobKitob.getSummaCol();
                        break;
                }
                hisobKitob.setBalans(balance);
            }
        }
        return balance;
    }

    private Double jami(ObservableList<HisobKitob> observableList) {
        Double jami = 0d;
        for (HisobKitob hisobKitob: observableList) {
            jami += hisobKitob.getSummaCol();
        }
        return jami;
    }
    private Double jami(ObservableList<HisobKitob> observableList, HisobKitob hk) {
        Double jami = 0d;
        for (HisobKitob hisobKitob: observableList) {
            if (!hisobKitob.equals(hk))
                jami += hisobKitob.getSummaCol();
        }
        return jami;
    }

    private Double narhHisobi(HisobKitob hisobKitob) {
        Double narh = 0d;
        Double a1 = jami(tovarTableView.getItems()); // jamiMablaq
        a1 -= jami(sotibOlishJadvali.getItems());
        Double a2 = jami(chegirmaRoyxati);
        Double a3 = jami(qoshimchaDaromadRoyxati);
        Double a4 = jami(naqdTolovRoyxati);
        Double a5 = jami(bankToloviRoyxati);
        Double a6 = jami(qaytimRoyxati);
        switch (hisobKitob.getAmal()) {
            case 4:
                narh = a1;
                break;
            case 7: //Naqd tolov
                royxatniOchir(qaytimRoyxati);
                a6 = 0d;
                narh = a1 - a2 + a3 - (jami(naqdTolovRoyxati, hisobKitob) + a5 - a6);
                break;
            case 8: //qaytim
                narh = a1 - a2 + a3 - (a4 + a5 - jami(qaytimRoyxati, hisobKitob));
                if (StringNumberUtils.yaxlitla(narh, -2) < 0)
                    narh = -narh;
                else
                    narh = 0d;
                break;
            case 13: // chegirma
                narh = a1 - jami(chegirmaRoyxati, hisobKitob) + a3 - (a4 + a5 - a6);
                if (StringNumberUtils.yaxlitla(narh, -2) <= 0)
                    narh = 0d;
                break;
            case 15: //bank tolovi
                narh = a1 - a2 + a3 - (a4 + jami(bankToloviRoyxati, hisobKitob) - a6);
                break;
            case 17: //yaxlitlash tafovuti daromad
                break;
            case 18: //qoshimcha daromad
                narh = a1 - a2 + jami(qoshimchaDaromadRoyxati, hisobKitob) - (a4 + a5 - a6);
                if (StringNumberUtils.yaxlitla(narh, -2) < 0)
                    narh = -narh;
                else
                    narh = 0d;
                break;
        }
        hisobKitob. setNarh(narh * hisobKitob.getKurs());
        return narh;
    }

    private Double narhHisobi2(HisobKitob hisobKitob) {
        Double narh = 0d;
        Double a1 = jami(tovarTableView.getItems()); // jamiMablaq
        a1 -= jami(sotibOlishJadvali.getItems());
        Double a2 = jami(chegirmaRoyxati);
        Double a3 = jami(qoshimchaDaromadRoyxati);
        Double a4 = jami(naqdTolovRoyxati);
        Double a5 = jami(bankToloviRoyxati);
        Double a6 = jami(qaytimRoyxati);
        switch (hisobKitob.getAmal()) {
            case 4:
                break;
            case 7: //Naqd tolov
                royxatniOchir(qaytimRoyxati);
                a6 = 0d;
                narh = (a1 - a2 + a3 - (a4 + a5 - a6)) * hisobKitob.getKurs();
                if (StringNumberUtils.yaxlitla(narh, -2) < 0)
                    royxatgaYoz(qaytimRoyxati, -narh, hisobKitob.getValuta());
                break;
            case 8: //qaytim
                break;
            case 13: // chegirma
                break;
            case 15: //bank tolovi
                break;
            case 17: //yaxlitlash tafovuti daromad
                break;
            case 18: //qoshimcha daromad
                break;
        }
        return narh;
    }

    private void royxatniOchir(ObservableList<HisobKitob> observableList) {
        observableList.forEach(hisobKitob->{hisobKitob.setNarh(0d);});
    }

    private void royxatlarniOchir() {
        xossaniYangila();
        royxatniOchir(naqdTolovRoyxati);
        royxatniOchir(bankToloviRoyxati);
        royxatniOchir(qaytimRoyxati);
        royxatniOchir(chegirmaRoyxati);
        royxatniOchir(qoshimchaDaromadRoyxati);
    }
    private void royxatgaYoz(ObservableList<HisobKitob> observableList, Double narh, Integer valutaId) {
        for (HisobKitob hisobKitob: observableList) {
            if (hisobKitob.getValuta().equals(valutaId)) {
                hisobKitob.setNarh(narh);
                break;
            }
        }
    }

    private Boolean xaridniYakunla() {
        sotildi = false;
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        if (tovarTableView.getItems().size() == 0) {
            Alerts.AlertString("Sotiladigan xech vaqo yo'q");
            return false;
        }
        Double oldiBerdiDouble = balansHisobi(oldiBerdiJadvali.getItems());
        Double oldiBerdiYaxlit = StringNumberUtils.yaxlitla(oldiBerdiDouble, -2);

        if (oldiBerdiYaxlit > 0.00) {
            // ortiqcha tolov
            Alerts.AlertString("Ortiqcha pul to`landi");
            return false;
        }

        if (oldiBerdiYaxlit < 0.00) {
            // nasiya
            if (!Alerts.haYoq("To`lov to`liq emas", "Nasiyaga sotamizmi ???")) {
                return false;
            }
        }

        qaydnomaData = yangiQaydnoma();
        HisobKitob xossa = oldiBerdiJadvali.getItems().get(0);
        xossa.setHisob1(hisob1.getId());
        xossa.setHisob2(hisob2.getId());
        xossa.setQaydId(qaydnomaData.getId());
        xossa.setHujjatId(qaydnomaData.getHujjat());
        xossa.setUserId(user.getId());
        xossa.setDateTime(qaydnomaData.getSana());


        hisobKitobModels.insert(connection, xossa);

        Savdo savdo = new Savdo(connection);
        Boolean yetarliAdad = false;
        tovarTableView.getItems().forEach(hisobKitob -> {
            hisobKitob.setHisob1(hisob1.getId());
            hisobKitob.setHisob2(hisob2.getId());
            hisobKitob.setQaydId(qaydnomaData.getId());
            hisobKitob.setHujjatId(qaydnomaData.getHujjat());
            hisobKitob.setUserId(user.getId());
            hisobKitob.setDateTime(qaydnomaData.getSana());
            savdo.setQaydnomaData(qaydnomaData);
            savdo.initHisobKitob(hisobKitob);
            savdo.sot();

        });
        ObservableList<HisobKitob> sotibOlishRoyxati = sotibOlishJadvali.getItems();
        sotibOlishRoyxati.forEach(hisobKitob -> {
            hisobKitob.setQaydId(qaydnomaData.getId());
            hisobKitob.setHujjatId(qaydnomaData.getHujjat());
            hisobKitob.setUserId(user.getId());
            hisobKitob.setDateTime(qaydnomaData.getSana());
        });
        hisobKitobModels.addBatch(connection, sotibOlishRoyxati);

        ObservableList<HisobKitob> oldiBerdiRoyxati = FXCollections.observableArrayList();
        naqdTolovRoyxati.forEach(hisobKitob -> {
            if (!hisobKitob.getNarh().equals(0d)) {
                oldiBerdiRoyxati.add(hisobKitob);
            }
        });
        bankToloviRoyxati.forEach(hisobKitob -> {
            if (!hisobKitob.getNarh().equals(0d)) {
                oldiBerdiRoyxati.add(hisobKitob);
            }
        });
        qaytimRoyxati.forEach(hisobKitob -> {
            if (!hisobKitob.getNarh().equals(0d)) {
                oldiBerdiRoyxati.add(hisobKitob);
            }
        });
        chegirmaRoyxati.forEach(hisobKitob -> {
            if (!hisobKitob.getNarh().equals(0d)) {
                oldiBerdiRoyxati.add(hisobKitob);
            }
        });
        qoshimchaDaromadRoyxati.forEach(hisobKitob -> {
            if (!hisobKitob.getNarh().equals(0d)) {
                oldiBerdiRoyxati.add(hisobKitob);
            }
        });
        oldiBerdiRoyxati.forEach(hisobKitob -> {
            hisobKitob.setQaydId(qaydnomaData.getId());
            hisobKitob.setHujjatId(qaydnomaData.getHujjat());
            hisobKitob.setUserId(user.getId());
            hisobKitob.setDateTime(qaydnomaData.getSana());
        });
        if (oldiBerdiDouble > 0.00) {
            // Tolov to'liq
            HisobKitob hisobKitob = balansniZarargaUr(oldiBerdiDouble);
            if (hisobKitob != null)
                oldiBerdiRoyxati.add(hisobKitob);
        }

        if (oldiBerdiDouble < 0.00) {
            // ortiqcha to'lov
            HisobKitob hisobKitob = balansniFoydagaUr(-oldiBerdiDouble);
            if (hisobKitob != null)
                oldiBerdiRoyxati.add(hisobKitob);
        }
        if (oldiBerdiRoyxati.size()>0) {
            hisobKitobModels.addBatch(connection, oldiBerdiRoyxati);
        }
        sotildi = true;
        return sotildi;
    }

    private HisobKitob balansniZarargaUr(Double oldiBerdiDouble) {
        HisobModels hisobModels = new HisobModels();
        ValutaModels valutaModels = new ValutaModels();
        Valuta valuta = valutaModels.getValuta(connection, 1);
        Hisob zararHisobi = null;
        HisobKitob hisobKitob = oldiBerdiJadvali.getItems().get(0);
        hisobKitob = null;
        String izohText = "";
        if (StringNumberUtils.yaxlitla(oldiBerdiDouble, -2) == 0d) {
            zararHisobi = hisobModels.zararHisobi(connection, hisob1);
            izohText = "Yaxlitlash tafovuti: " + valuta.getValuta() +  "\n Xarid № " + qaydnomaData.getHujjat().toString().trim();
            double kurs = 1d;
            //97 400 63 32
            hisobKitob = new HisobKitob(
                    null,
                    qaydnomaData.getId(),
                    qaydnomaData.getHujjat(),
                    17,
                    hisob2.getId(),
                    zararHisobi.getId(),
                    valuta.getId(),
                    0,
                    kurs,
                    "",
                    .0,
                    oldiBerdiDouble,
                    0,
                    izohText,
                    user.getId(),
                    qaydnomaData.getSana()
            );
        }
        return hisobKitob;
    }

    private HisobKitob balansniFoydagaUr(Double oldiBerdiDouble) {
        HisobModels hisobModels = new HisobModels();
        ValutaModels valutaModels = new ValutaModels();
        Valuta valuta = valutaModels.getValuta(connection, 1);
        Hisob foydaHisobi = null;
        HisobKitob hisobKitob = null;
        String izohText = "";
        if (StringNumberUtils.yaxlitla(oldiBerdiDouble, -2) == 0d) {
            foydaHisobi = hisobModels.foydaHisobi(connection, hisob1);
            izohText = "Yaxlitlash tafovuti: " + valuta.getValuta() +  "\n Xarid № " + qaydnomaData.getHujjat().toString().trim();
            double kurs = 1d;
            //97 400 63 32
            hisobKitob = new HisobKitob(
                    null,
                    qaydnomaData.getId(),
                    qaydnomaData.getHujjat(),
                    17,
                    foydaHisobi.getId(),
                    hisob2.getId(),
                    valuta.getId(),
                    0,
                    kurs,
                    "",
                    .0,
                    oldiBerdiDouble,
                    0,
                    izohText,
                    user.getId(),
                    qaydnomaData.getSana()
            );
        }
        return hisobKitob;
    }

    private QaydnomaData yangiQaydnoma() {
        QaydnomaModel qaydnomaModel = new QaydnomaModel();
        Date joriySana = new Date();
        Integer amal = 4;
        QaydnomaData qaydnomaData = new QaydnomaData(
                null,
                amal,
                0,
                joriySana,
                hisob1.getId(),
                hisob1.getText(),
                hisob2.getId(),
                hisob2.getText(),
                "",
                0.00,
                0,
                user.getId(),
                null
        );
        qaydnomaModel.insert_data(connection, qaydnomaData);
        return qaydnomaData;
    }

    private HisobKitob xossa(ObservableList<HisobKitob> observableList) {
        HisobKitob hisobKitob = observableList.get(0);
        return hisobKitob;
    }
}
