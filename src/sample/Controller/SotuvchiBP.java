package sample.Controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
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
import sample.Config.*;
import sample.Data.*;
import sample.Enums.ServerType;
import sample.Model.*;
import sample.Tools.*;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SotuvchiBP extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    MenuBar mainMenu;
    TreeView<TreeItemClass> treeView = new TreeView();
    TableView<HisobKitob> tovarTableView = new TableView();

    GridPane leftGridPane = new GridPane();
    VBox rightPane = new VBox();
    HBox topHBox = new HBox();
    HBox bottomHBox = new HBox();

    TextField tovarTextField = new TextField();
    TextField barCodeTextField = new TextField();
    TextField chegirmaTextField = new TextField();
    TextField milliyKurs = new TextField();
    TextField plasticKurs= new TextField();
    TextField naqdTextField = new TextField();
    TextField naqdMilliyTextField = new TextField();
    TextField plasticTextField = new TextField();
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
    Double kassagaDouble = 0.0;
    Double chegirmaDouble = 0.0;
    Double naqdUsdDouble = 0.0;
    Double naqdMilliyDouble = 0.0;
    Double plasticDouble = 0.0;
    Double balansDouble = 0.0;
    Double qaytimDouble = 0.0;
    Double vaznDouble = .0;

    Label jamiMablagLabel = new Label();
    Label kassagaLabel = new Label();
    Label balansLabel = new Label();
    Label qaytimLabel = new Label();

    DecimalFormat decimalFormat = new MoneyShow();
    String style20 = "-fx-font: 20px Arial";
    Font font = Font.font("Arial",16);
    Font font1 = Font.font("Arial", FontWeight.BOLD,16);
    Font buttonFont1 = Font.font("Arial", FontWeight.BOLD,16);
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

    public static void main(String[] args) {
        launch(args);
    }

    public SotuvchiBP() {
        connection = new MySqlDBGeneral(ServerType.REMOTE).getDbConnection();
        GetDbData.initData(connection);
    }

    public SotuvchiBP(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        ibtido();
    }

    public SotuvchiBP(Connection connection, User user, QaydnomaData qaydnomaData) {
        this.connection = connection;
        this.user = user;
        this.qaydnomaData = qaydnomaData;
        ibtido();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        if (buKassami()) {
            login();
            ibtido();
        } else {
            System.out.println("Bu kompyuter sistema qaydidan o`tmagan");
            Platform.exit();
            System.exit(0);
        }
        initStage(primaryStage);
        stage.show();
    }

    public void display() {
        stage = new Stage();
        initStage(stage);
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
        topHBox.getChildren().addAll(navbatdagiXaridorButton, xaridnBekorQilButton);
    }

    private void initCenterPane() {
    }

    private void initRightPane() {
        initTreeView();
        SetHVGrow.VerticalHorizontal(rightPane);
        rightPane.getChildren().add(treeView);
    }

    private void initBottomPane() {
    }

    private void initBorderPane() {
        borderpane.setPadding(new Insets(padding));
        SetHVGrow.VerticalHorizontal(borderpane);
        initSystemMenu();
        borderpane.setTop(mainMenu);
        initGridPane();
        borderpane.setCenter(leftGridPane);
        borderpane.setRight(rightPane);
        borderpane.setBottom(bottomHBox);
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

    private void initGridPane() {
        SetHVGrow.VerticalHorizontal(leftGridPane);
        leftGridPane.setPadding(new Insets(padding));

        int rowIndex = 0;
        initTopPane();
        leftGridPane.add(topHBox, 0, rowIndex, 1, 1);
        GridPane.setHgrow(topHBox, Priority.ALWAYS);

        rowIndex++;
        initTableView();
        leftGridPane.add(tovarTableView, 0, rowIndex, 1, 1);
        GridPane.setHgrow(tovarTableView, Priority.ALWAYS);
        GridPane.setVgrow(tovarTableView, Priority.ALWAYS);

        rowIndex++;
        initXaridniYakunlaButton();
        leftGridPane.add(xaridniYakunlaButton, 0, rowIndex, 2, 1);
        GridPane.setHgrow(xaridniYakunlaButton, Priority.ALWAYS);
    }

    private void initTableView() {
        HBox.setHgrow(tovarTableView, Priority.ALWAYS);
        VBox.setVgrow(tovarTableView, Priority.ALWAYS);
        tovarTableView.getColumns().addAll(getTovarColumn(), getTaqdimColumn(), getAdadColumn(), getNarhColumn(), getSummaColumn(), getDeleteColumn());
        tovarTableView.setEditable(true);
        tovarTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
            }
        });
        tovarTableView.setItems(tableViewObservableList);
        if (tableViewObservableList.size()>0) {
            tovarTableView.getSelectionModel().selectFirst();
        }
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

    private  TableColumn<HisobKitob, Double> getAdadColumn() {
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
            tovarTableView.refresh();
            jamiHisob(tableViewObservableList);
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
                    narhYoz(tovar.getId(), 1, yangiNarhDouble);
                }
            }
            event.getTableView().refresh();
            jamiHisob(event.getTableView().getItems());
        });
        return narh;
    }

    private TableColumn<HisobKitob, Valuta> getValutaColumn() {
        TableColumn<HisobKitob, Valuta> valuta = new TableColumn<>("Valuta");
        valuta.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, Valuta>, ObservableValue<Valuta>>() {

            @Override
            public ObservableValue<Valuta> call(TableColumn.CellDataFeatures<HisobKitob, Valuta> param) {
                HisobKitob hisobKitob = param.getValue();
                Integer valutaCode = hisobKitob.getValuta();
                Valuta v = GetDbData.getValuta(valutaCode);
                return new SimpleObjectProperty<Valuta>(v);
            }
        });

        valuta.setCellFactory(ComboBoxTableCell.forTableColumn(valutaObservableList));
        valuta.setOnEditCommit((TableColumn.CellEditEvent<HisobKitob, Valuta> event) -> {
            Valuta newValuta = event.getNewValue();
            HisobKitob hisobKitob = event.getRowValue();
            hisobKitob.setValuta(newValuta.getId());
        });
        valuta.setMinWidth(120);
        valuta.setStyle( "-fx-alignment: CENTER;");
        return valuta;
    }

    private TableColumn<HisobKitob, ComboBox<Valuta>> getValuta2Column() {
        TableColumn<HisobKitob, ComboBox<Valuta>> valutaColumn = new TableColumn<>("Valuta");
        valutaColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, ComboBox<Valuta>>, ObservableValue<ComboBox<Valuta>>>() {
            ComboBox<Valuta> comboBox = new ComboBox<Valuta>(valutaObservableList);

            @Override
            public ObservableValue<ComboBox<Valuta>> call(TableColumn.CellDataFeatures<HisobKitob, ComboBox<Valuta>> param) {
                HisobKitob hisobKitob = param.getValue();
                Integer valutaCode = hisobKitob.getValuta();
                for (Valuta v: valutaObservableList) {
                    if (v.getId().equals(valutaCode)) {
                        comboBox.getSelectionModel().select(v);
                        break;
                    }
                }
                comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                    }
                });
                return new SimpleObjectProperty<ComboBox<Valuta>>(comboBox);
            }
        });
        valutaColumn.setMinWidth(120);
        valutaColumn.setStyle( "-fx-alignment: CENTER;");
        return valutaColumn;
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
                    tableViewObservableList.remove(hisobKitob);
                    param.getTableView().refresh();
                    jamiHisob(param.getTableView().getItems());
                });
                return new SimpleObjectProperty<Button>(b);
            }
        });

        deleteColumn.setMinWidth(20);
        deleteColumn.setMaxWidth(100);
        deleteColumn.setStyle( "-fx-alignment: CENTER;");
        return deleteColumn;
    }

    private TableColumn<HisobKitob, Double> getVaznColumn() {
        TableColumn<HisobKitob, Double> vaznColumn = new TableColumn<>("Vazn");
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

        vaznColumn.setMinWidth(20);
        vaznColumn.setMaxWidth(40);
        vaznColumn.setStyle( "-fx-alignment: CENTER;");
        return vaznColumn;
    }

    private TableColumn<HisobKitob, Button> getEditColumn() {
        TableColumn<HisobKitob, Button> editColumn = new TableColumn<>("O`chir");
        editColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, Button>, ObservableValue<Button>>() {

            @Override
            public ObservableValue<Button> call(TableColumn.CellDataFeatures<HisobKitob, Button> param) {
                HisobKitob hisobKitob = param.getValue();
                Button b = new Button("");
                b.setMaxWidth(2000);
                b.setPrefWidth(150);
                HBox.setHgrow(b, Priority.ALWAYS);
                InputStream inputStream = getClass().getResourceAsStream("/sample/images/Icons/edit.png");
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
                    tableViewObservableList.remove(hisobKitob);
                });
                return new SimpleObjectProperty<Button>(b);
            }
        });

        editColumn.setMinWidth(20);
        editColumn.setMaxWidth(40);
        editColumn.setStyle( "-fx-alignment: CENTER;");
        return editColumn;
    }

    private TableColumn<HisobKitob, Double> getTotalColumn() {
        TableColumn<HisobKitob, Double>  total = new TableColumn<>("Jami");
        total.setMinWidth(150);
        total.setMaxWidth(300);
        total.setCellValueFactory(new PropertyValueFactory<>("chiqim"));
        total.setStyle( "-fx-alignment: CENTER;");
        return total;
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
                getTovarTreeItem(),   //50
                getXaridNarhiLabelTreeItem(),   //60
                getChegirmaLabelTreeItem(), //70
                getKassagaLabelTreeItem(),  //80
                getTolandiLabelTreeItem(),  //90
                getQaytimLabelTreeItem(),   //100
                getBalansLabelTreeItem()   //110
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

    private TreeItem<TreeItemClass> getXodimTreeItem() {
        Standart standart = new Standart(10, "Xodim", user.getId(), new Date());
        TreeItemClass treeItemClass = new TreeItemClass(standart);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        treeItem.getChildren().addAll(getChangeUserTreeItem());
        return treeItem;
    }

    private TreeItem<TreeItemClass> getChangeUserTreeItem() {
        Standart standart = new Standart(11, "Dastur yurituvchini alishtir", user.getId(), new Date());
        TreeItemClass treeItemClass = new TreeItemClass(standart);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getPulTreeItem() {
        Standart standart = new Standart(20, "Pul", user.getId(), new Date());
        TreeItemClass treeItemClass = new TreeItemClass(standart);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        treeItem.getChildren().addAll(
                getPulHisobotiTreeItem(),
                getPulKirimiTreeItem(),
                getPulChiqimiTreeItem(),
                getConvertTreeItem()
        );
        return treeItem;
    }

    private TreeItem<TreeItemClass> getPulHisobotiTreeItem() {
        Standart standart = new Standart(21, "Pul hisoboti", user.getId(), new Date());
        TreeItemClass treeItemClass = new TreeItemClass(standart);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getPulChiqimiTreeItem() {
        Standart standart = new Standart(22, "Pul chiqimi", user.getId(), new Date());
        TreeItemClass treeItemClass = new TreeItemClass(standart);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getPulKirimiTreeItem() {
        Standart standart = new Standart(23, "Pul kirimi", user.getId(), new Date());
        TreeItemClass treeItemClass = new TreeItemClass(standart);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getConvertTreeItem() {
        Standart standart = new Standart(24, "Konvertatsiya", user.getId(), new Date());
        TreeItemClass treeItemClass = new TreeItemClass(standart);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getMahsulotTreeItem() {
        Standart standart = new Standart(30, "Mahsulot", user.getId(), new Date());
        TreeItemClass treeItemClass = new TreeItemClass(standart);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        treeItem.getChildren().addAll(
                getMahsulotHisobotiTreeItem(),
                getMahsulotKirimiTreeItem(),
                getMahsulotChiqimiTreeItem(),
                getPochkaBuzishTreeItem()
        );
        return treeItem;
    }

    private TreeItem<TreeItemClass> getMahsulotHisobotiTreeItem() {
        Standart standart = new Standart(31, "Mahsulot hisoboti", user.getId(), new Date());
        TreeItemClass treeItemClass = new TreeItemClass(standart);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getMahsulotKirimiTreeItem() {
        Standart standart = new Standart(32, "Mahsulot kirimi", user.getId(), new Date());
        TreeItemClass treeItemClass = new TreeItemClass(standart);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getMahsulotChiqimiTreeItem() {
        Standart standart = new Standart(33, "Mahsulot chiqimi", user.getId(), new Date());
        TreeItemClass treeItemClass = new TreeItemClass(standart);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getPochkaBuzishTreeItem() {
        Standart standart = new Standart(34, "Pochka buzish", user.getId(), new Date());
        TreeItemClass treeItemClass = new TreeItemClass(standart);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        return treeItem;
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
        treeItem.setExpanded(false);
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
                jamiHisob(tableViewObservableList);
            }
        });
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getXaridNarhiLabelTreeItem() {
        TreeItemClass treeItemClass = new TreeItemClass(60, new Label("Xarid narhi: "));
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        treeItem.getChildren().add(getXaridNarhiTreeItem());
        treeItem.setExpanded(true);
        Label label = treeItemClass.getLabel();
        label.setFont(font);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getXaridNarhiTreeItem() {
        TreeItemClass treeItemClass = new TreeItemClass(61, jamiMablagLabel);
        Label label = treeItemClass.getLabel();
        label.setFont(font);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        TextField textField = treeItemClass.getTextField();
        return treeItem;
    }

    private TreeItem<TreeItemClass> getChegirmaLabelTreeItem() {
        TreeItemClass treeItemClass = new TreeItemClass(70, new Label("Chegirma: "));
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        treeItem.getChildren().addAll(
                getChegirmaTreeItem()
        );
        treeItem.setExpanded(true);
        Label label = treeItemClass.getLabel();
        label.setFont(font);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getChegirmaTreeItem() {
        TreeItemClass treeItemClass = new TreeItemClass(71, chegirmaTextField);
        chegirmaTextField.setFont(font1);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        TextField textField = treeItemClass.getTextField();
        textField.setPromptText("CHEGIRMA");
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                jamiHisob(tableViewObservableList);
            }
        });
        return treeItem;
    }

    private TreeItem<TreeItemClass> getKassagaLabelTreeItem() {
        TreeItemClass treeItemClass = new TreeItemClass(80, new Label("Kassaga: "));
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        treeItem.getChildren().add(getKassagaTreeItem());
        treeItem.setExpanded(true);
        Label label = treeItemClass.getLabel();
        label.setFont(font);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getKassagaTreeItem() {
        TreeItemClass treeItemClass = new TreeItemClass(81, kassagaLabel);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        Label label = treeItemClass.getLabel();
        label.setFont(font1);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getTolandiLabelTreeItem() {
        TreeItemClass treeItemClass = new TreeItemClass(90, new Label("To`landi: "));
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        treeItem.getChildren().addAll(
                getNaqdUsdTreeItem(),
                getNaqdMilliyTreeItem(),
                getPlastikTreeItem()
        );
        Label label = treeItemClass.getLabel();
        label.setFont(font);
        treeItem.setExpanded(true);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getNaqdUsdTreeItem() {
        TreeItemClass treeItemClass = new TreeItemClass(91, naqdTextField);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        TextField textField = treeItemClass.getTextField();
        Valuta valuta1 = GetDbData.getValuta(1);
        textField.setPromptText(valuta1.getValuta());
        textField.setFont(font1);
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                jamiHisob(tableViewObservableList);
            }
        });
        return treeItem;
    }

    private TreeItem<TreeItemClass> getNaqdMilliyTreeItem() {
        Valuta valuta1 = GetDbData.getValuta(2);
        TreeItemClass treeItemClass = new TreeItemClass(92, naqdMilliyTextField, milliyKurs, valuta1);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        TextField textField = treeItemClass.getTextField();
        textField.setPromptText(valuta1.getValuta());
        textField.setFont(font1);
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                jamiHisob(tableViewObservableList);
            }
        });
        return treeItem;
    }

    private TreeItem<TreeItemClass> getPlastikTreeItem() {
        Valuta valuta1 = GetDbData.getValuta(2);
        TreeItemClass treeItemClass = new TreeItemClass(93, plasticTextField, plasticKurs, valuta1);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        TextField textField = treeItemClass.getTextField();
        textField.setPromptText("PLASTIK");
        Label label = treeItemClass.getLabel();
        label.setFont(font);
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                jamiHisob(tableViewObservableList);
            }
        });
        textField.setFont(font1);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getQaytimLabelTreeItem() {
        TreeItemClass treeItemClass = new TreeItemClass(100, new Label("Qaytim: "));
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        treeItem.getChildren().add(getQaytimTreeItem());
        treeItem.setExpanded(true);
        Label label = treeItemClass.getLabel();
        label.setFont(font);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getQaytimTreeItem() {
        TreeItemClass treeItemClass = new TreeItemClass(101, qaytimLabel);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        Label label = treeItemClass.getLabel();
        label.setFont(font1);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getBalansLabelTreeItem() {
        TreeItemClass treeItemClass = new TreeItemClass(110, new Label("Balans: "));
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        treeItem.getChildren().add(getBalansTreeItem());
        treeItem.setExpanded(true);
        Label label = treeItemClass.getLabel();
        label.setFont(font);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getBalansTreeItem() {
        TreeItemClass treeItemClass = new TreeItemClass(111, balansLabel);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        Label label = treeItemClass.getLabel();
        label.setFont(font1);
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
//        stage.initStyle(StageStyle.UNDECORATED);
        stage.setOnCloseRequest(event -> {
            barCodeOff();
            Platform.exit();
            System.exit(0);
            stage.close();
        });
        stage.setScene(scene);
    }
    private void login() {
        LoginUserController loginUserController = new LoginUserController(connection);
        if (!loginUserController.display()) {
            Platform.exit();
            System.exit(0);
        } else {
            user = loginUserController.getUser();
            String serialNumber = SotuvchiBP.getSerialNumber();
            Kassa kassa = SotuvchiBP.getKassaData(connection, serialNumber);
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

/*
                tovarBinding.dispose();
                tovarBox.setAutoCompletion(textField, tovarObservableList);
*/
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
        tovarBox = new TovarBox(tovarObservableList, user);
        TextField textField = tovarBox.getTextField();
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
            }
        });

        Button addButton = valutaHBox.getPlusButton();
        addButton.setOnAction(event -> {
            Valuta newValuta = addValuta();
            if (newValuta != null) {
                valuta = newValuta;
                textField.setText(valuta.getValuta());
            }
        });
    }

    private void initBirlikComboBox() {
        birlikComboBox.setMaxWidth(2000);
        birlikComboBox.setPrefWidth(150);
        SetHVGrow.VerticalHorizontal(birlikComboBox);
        birlikComboBox.setItems(birlikObservableList);
        Standart narhTuri = null;
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
            barCodeOn();
//            initTovarBox();
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
        xaridniYakunlaButton.setMaxWidth(2000);
        xaridniYakunlaButton.setPrefWidth(150);
        xaridniYakunlaButton.setOnAction(event -> {
            xaridniYakunla();
        });
    }

    private void xaridniYakunla() {
        Boolean tolovSahih = tolovSahih();
        boolean nasiyagaSot = true;
        if (!tolovSahih) {
            nasiyagaSot = Alerts.nasiyagaSot(balansDouble, valutaHBox.getTextField().getText());
        }
        if (nasiyagaSot) {
            qaydnomaData = yangiQaydnoma();
            tovarSot(tableViewObservableList);
            jamiMablag = 0.0;
            for (HisobKitob hk : tableViewObservableList) {
                hk.setDateTime(qaydnomaData.getSana());
                jamiMablag += hk.getDona() * hk.getNarh();
            }
            double uKurs = 1d;
            double mKurs = getDoubleFromTextField(milliyKurs);
            if (valuta.getId().equals(1)) {
                naqdUsdDouble = getDoubleFromTextField(naqdTextField) * uKurs;
                naqdMilliyDouble = getDoubleFromTextField(naqdMilliyTextField) / mKurs;
            } else if (valuta.getId().equals(2)) {
                naqdUsdDouble = getDoubleFromTextField(naqdTextField) * mKurs;
                naqdMilliyDouble = getDoubleFromTextField(naqdMilliyTextField) / uKurs;
            }
            plasticDouble = getDoubleFromTextField(plasticTextField);
            Double pKurs = getDoubleFromTextField(plasticKurs);
            chegirmaDouble = getDoubleFromTextField(chegirmaTextField);
            Double tolandi = naqdUsdDouble + naqdMilliyDouble + plasticDouble / pKurs;
            kassagaDouble = jamiMablag - chegirmaDouble;
            Double jamiDouble = tolandi - kassagaDouble;
            if (jamiDouble > 0d) {
                qaytimDouble = jamiDouble;
            }
            if (jamiDouble == 0d) {}
            if (jamiDouble < 0d) {
                balansDouble = -jamiDouble;
            }
            ObservableList<HisobKitob> hkList = FXCollections.observableArrayList();
            if (chegirmaDouble > 0) {
                hkList.add(chegirmaniQaydEt());
            }
            if (naqdUsdDouble > 0) {
                hkList.add(naqdUsdTolovniQaydEt());
            }
            if (naqdMilliyDouble > 0) {
                hkList.add(naqdMilliyTolovniQaydEt());
            }
            if (plasticDouble > 0) {
                hkList.add(plasticToloviniQaydEt());
                HisobKitob bankXizmati = bankXizmatiniQaydEt();
                if (bankXizmati != null) {
                    hkList.add(bankXizmati);
                }
            }
            if (qaytimDouble > 0) {
                hkList.add( qaytimniQaydEt());
            }
            if (balansDouble > 0) {
                HisobKitob hk1 = balansniZarargaUr();
                if (hk1!=null) {
                    hkList.add(hk1);
                }
            }
            hisobKitobModels.addBatch(connection, hkList);
            String printerNomi = printerim().toLowerCase();
            if (printerNomi.contains("POS-58".toLowerCase())) {
                tolovChiptasiniBer("POS-58");
            } else if (printerNomi.contains("XP-80C".toLowerCase())) {
                tolovChiptasiniBerXP80(printerNomi);
            }
        } else {return;}
        kassaniTozala();
        setDisable(true);
        barCodeOff();
    }
    private String printerim() {
        Connection printersConnection = new SqliteDB().getDbConnection();
        StandartModels printerModels = new StandartModels("Printers");
        ObservableList<Standart> printers = printerModels.get_data(printersConnection);
        Standart myPrinter = null;
        String printerNomi = "";
        if (printers.size()>0) {
            myPrinter = printers.get(0);
            printerNomi = myPrinter.getText();
        }
        return printerNomi;
    }

    private QaydnomaData yangiQaydnoma() {
        Date joriySana = new Date();
        Integer hujjat =  qaydnomaModel.getCount(connection, "amalTuri = " + amalTuri) + 1;
        QaydnomaData qaydnomaData = new QaydnomaData(
                null,
                amalTuri,
                hujjat,
                joriySana,
                hisob1.getId(),
                hisob1.getText(),
                hisob2.getId(),
                hisob2.getText(),
                izohTextArea.getText(),
                0.00,
                0,
                user.getId(),
                null
        );
        qaydnomaData.setId(qaydnomaModel.insert_data(connection, qaydnomaData));
        return qaydnomaData;
    }

    private boolean tolovSahih() {
        boolean sahih = false;
        if (tableViewObservableList.size() > 0) {
            jamiHisob(tableViewObservableList);
            if (StringNumberUtils.yaxlitla(balansDouble,-2) == 0) {
                sahih = true;
            }
        }
        return sahih;
    }

    public void tovarSot(ObservableList<HisobKitob> hk) {
        double jami = 0.00;
        boolean yetarliAdad = false;
        Savdo savdo = new Savdo(connection);
        for (HisobKitob h: hk) {
            h.setHisob1(hisob1.getId());
            h.setHisob2(hisob2.getId());
            h.setQaydId(qaydnomaData.getId());
            h.setHujjatId(qaydnomaData.getHujjat());
            h.setDateTime(qaydnomaData.getSana());
            savdo.setQaydnomaData(qaydnomaData);
            savdo.initHisobKitob(h);
            yetarliAdad = savdo.sot();
            if (yetarliAdad) {
                jami += h.getDona() * h.getNarh() / h.getKurs();
            } else {
                Alerts.AlertString("Tovar adadi yetarsiz");
            }
        }
        qaydnomaData.setJami(jami);
        qaydnomaData.setDateTime(new Date());
        qaydnomaModel.update_data(connection, qaydnomaData);
    }

    private HisobKitob chegirmaniQaydEt() {
        HisobKitob hisobKitob = null;
        if (chegirmaDouble != .0) {
            Hisob pulHisobi = GetDbData.getHisob(kassa.getPulHisobi());
            int chegirmaHisobi = hisobKitobModels.yordamchiHisob(connection, pulHisobi.getId(), "ChegirmaGuruhi", "Chegirma");
            double kurs = getKurs(valuta.getId(), new Date()).getKurs();
            hisobKitob = new HisobKitob(
                    null,
                    qaydnomaData.getId(),
                    qaydnomaData.getHujjat(),
                    13,
                    hisob2.getId(),
                    chegirmaHisobi,
                    valuta.getId(),
                    0,
                    kurs,
                    "",
                    .0,
                    chegirmaDouble,
                    0,
                    "Chegirma: " + valuta.getValuta() +  "\n Xarid ??? " + qaydnomaData.getHujjat().toString().trim(),
                    user.getId(),
                    qaydnomaData.getSana()
            );
        }
        return hisobKitob;
    }

    private HisobKitob naqdUsdTolovniQaydEt() {
        HisobKitob hisobKitob = null;
        Valuta valuta = GetDbData.getValuta(1);
        Double naqdDouble = getDoubleFromTextField(naqdTextField);
        if (naqdDouble > 0) {
            Hisob pulHisobi = GetDbData.getHisob(kassa.getPulHisobi());
            double kurs = 1d;
            hisobKitob = new HisobKitob(
                    null,
                    qaydnomaData.getId(),
                    qaydnomaData.getHujjat(),
                    7,
                    hisob2.getId(),
                    pulHisobi.getId(),
                    valuta.getId(),
                    0,
                    kurs,
                    "",
                    .0,
                    naqdDouble,
                    0,
                    "Naqd: " + valuta.getValuta() +  "\n Xarid ??? " + qaydnomaData.getHujjat().toString().trim(),
                    user.getId(),
                    qaydnomaData.getSana()
            );
        }
        return hisobKitob;
    }

    private HisobKitob naqdMilliyTolovniQaydEt() {
        HisobKitob hisobKitob = null;
        Double naqdMilliyDouble = getDoubleFromTextField(naqdMilliyTextField);
        Valuta valuta = GetDbData.getValuta(2);
        if (naqdMilliyDouble > 0) {
            Hisob pulHisobi = GetDbData.getHisob(kassa.getPulHisobi());
            Double kurs = getDoubleFromTextField(milliyKurs);
            hisobKitob = new HisobKitob(
                    null,
                    qaydnomaData.getId(),
                    qaydnomaData.getHujjat(),
                    7,
                    hisob2.getId(),
                    pulHisobi.getId(),
                    valuta.getId(),
                    0,
                    kurs,
                    "",
                    .0,
                    naqdMilliyDouble,
                    0,
                    "Naqd: " + valuta.getValuta() +  "\n Xarid ??? " + qaydnomaData.getHujjat().toString().trim(),
                    user.getId(),
                    qaydnomaData.getSana()
            );
        }
        return hisobKitob;
    }

    private HisobKitob plasticToloviniQaydEt() {
        HisobKitob hisobKitob = null;
        Valuta valuta = GetDbData.getValuta(2);
        if (plasticDouble > 0) {
            Integer bankHisobiInteger = hisobKitobModels.yordamchiHisob(connection, hisob1.getId(), "Bank1"," Bank");
            double kurs = getDoubleFromTextField(plasticKurs);
            hisobKitob = new HisobKitob(
                    null,
                    qaydnomaData.getId(),
                    qaydnomaData.getHujjat(),
                    15,
                    hisob2.getId(),
                    bankHisobiInteger,
                    2,
                    0,
                    kurs,
                    "",
                    .0,
                    plasticDouble,
                    0,
                    "Plastik: " + valuta.getValuta() +  "\n Xarid ??? " + qaydnomaData.getHujjat().toString().trim(),
                    user.getId(),
                    qaydnomaData.getSana()
            );
        }
        return hisobKitob;
    }

    private HisobKitob bankXizmatiniQaydEt() {
        HisobKitob hisobKitob = null;
        Valuta valuta = GetDbData.getValuta(2);
        PlasticFoizi plasticFoizi = GetDbData.getPlasticFoizi();
        if (plasticFoizi == null) {
            return  null;
        }
        if (plasticDouble > 0) {
            Integer bankHisobiInteger = hisobKitobModels.yordamchiHisob(connection, hisob1.getId(), "Bank1"," Bank");
            Integer bankXizmatiHisobiInteger = hisobKitobModels.yordamchiHisob(connection, bankHisobiInteger, "BankXizmati1", "BankXizmati");
            double kurs = getDoubleFromTextField(plasticKurs);
            hisobKitob = new HisobKitob(
                    null,
                    qaydnomaData.getId(),
                    qaydnomaData.getHujjat(),
                    14,
                    bankHisobiInteger,
                    bankXizmatiHisobiInteger,
                    valuta.getId(),
                    0,
                    kurs,
                    "",
                    .0,
                    plasticDouble * 0.002,
                    0,
                    "Bank xizmati : " + valuta.getValuta() +  "\n Xarid ??? " + qaydnomaData.getHujjat().toString().trim(),
                    user.getId(),
                    qaydnomaData.getSana()
            );
        }
        return hisobKitob;
    }

    private HisobKitob qaytimniQaydEt() {
        Integer pulHisobiInteger = 0;
        Integer amal = 0;
        Hisob pulHisobi = null;
        HisobKitob hisobKitob = null;
        String izohText = "";
        if (StringNumberUtils.yaxlitla(qaytimDouble, -2) > 0.00) {
            pulHisobi = GetDbData.getHisob(kassa.getPulHisobi());
            izohText = "Qaytim: " + valuta.getValuta() +  "\n Xarid ??? " + qaydnomaData.getHujjat().toString().trim();
            amal = 8;
        } else {
            pulHisobiInteger = hisobKitobModels.yordamchiHisob(connection, hisob1.getId(), "FoydaHisobiGuruhi", "FoydaHisobi");
            pulHisobi = GetDbData.getHisob(pulHisobiInteger);
            izohText = "Yaxlitlash tafovuti: " + valuta.getValuta() +  "\n Xarid ??? " + qaydnomaData.getHujjat().toString().trim();
            amal = 17;
        }
        double kurs = getKurs(valuta.getId(), new Date()).getKurs();
        hisobKitob = new HisobKitob(
                null,
                qaydnomaData.getId(),
                qaydnomaData.getHujjat(),
                amal,
                pulHisobi.getId(),
                hisob2.getId(),
                valuta.getId(),
                0,
                kurs,
                "",
                .0,
                qaytimDouble,
                0,
                izohText,
                user.getId(),
                qaydnomaData.getSana()
        );
        return hisobKitob;
    }

    private HisobKitob balansniZarargaUr() {
        Integer pulHisobiInteger = 0;
        Hisob pulHisobi = null;
        HisobKitob hisobKitob = null;
        String izohText = "";
        if (StringNumberUtils.yaxlitla(balansDouble, -2) == 0d) {
            pulHisobiInteger = hisobKitobModels.yordamchiHisob(connection, hisob1.getId(), "ZararGuruhi", "Zarar");
            pulHisobi = GetDbData.getHisob(pulHisobiInteger);
            izohText = "Yaxlitlash tafovuti: " + valuta.getValuta() +  "\n Xarid ??? " + qaydnomaData.getHujjat().toString().trim();
            double kurs = getKurs(valuta.getId(), new Date()).getKurs();
            //97 400 63 32
            hisobKitob = new HisobKitob(
                    null,
                    qaydnomaData.getId(),
                    qaydnomaData.getHujjat(),
                    17,
                    hisob2.getId(),
                    pulHisobi.getId(),
                    valuta.getId(),
                    0,
                    kurs,
                    "",
                    .0,
                    balansDouble,
                    0,
                    izohText,
                    user.getId(),
                    qaydnomaData.getSana()
            );
        }
        return hisobKitob;
    }

    private void tolovChiptasiniBerXP80(String printerNomi) {
        StringBuffer printStringBuffer = new StringBuffer();
        String lineB = String.format("%.63s\n", "---------------------------------------------");
        SimpleDateFormat sana = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat vaqt = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        String sanaString = sana.format(date);
        String vaqtString = vaqt.format(date);
        printStringBuffer.append(lineB);
        String shirkatNomi = GetDbData.getHisob(user.getTovarHisobi()).getText();
        printStringBuffer.append(String.format("%29s\n", shirkatNomi));
        printStringBuffer.append(lineB);
        printStringBuffer.append(String.format("%-15s %29s\n", "Telefon", user.getPhone()));
        printStringBuffer.append(String.format("%-15s %29s\n", "Sana", sanaString));
        printStringBuffer.append(String.format("%-15s %29s\n", "Vaqt", vaqtString));
        printStringBuffer.append(String.format("%-15s %29s\n", "Sotuvchi", user.getIsm()));
        printStringBuffer.append(String.format("%-15s %29s\n", "Oluvchi", qaydnomaData.getKirimNomi()));
        printStringBuffer.append(String.format("%-15s %29s\n", "Chipta N", qaydnomaData.getHujjat()));
        printStringBuffer.append(lineB);
        printStringBuffer.append(String.format("%-15s %5s %10s %12s\n", "Mahsulot", "Dona", "Narh", "Jami"));
        printStringBuffer.append(lineB);

        String space = "                    ";
        for (HisobKitob hk: tableViewObservableList) {
            Double dona = hk.getDona();
            Double narh = hk.getDona() * hk.getNarh();
            String line = String.format("%.15s %5s %10s %12s\n", hk.getIzoh() + space, decimalFormat.format(dona), decimalFormat.format(hk.getNarh()), decimalFormat.format(narh));
            printStringBuffer.append(line);
        }

        printStringBuffer.append(lineB);

        if (jamiMablag > 0) {
            String line = String.format("%-15s %5s %10s\n", "Xarid jami", " ", decimalFormat.format(jamiMablag));
            printStringBuffer.append(line);
        }
        if (chegirmaDouble > 0) {
            String line = String.format("%-15s %5s %10s\n", "Chegirma", " ", decimalFormat.format(chegirmaDouble));
            printStringBuffer.append(line);
        }

        if (naqdUsdDouble > 0) {
            Valuta v1 = GetDbData.getValuta(1);
            String line = String.format("%-15s %5s %10s\n", "Naqd " + v1.getValuta(), " ", decimalFormat.format(naqdUsdDouble));
            printStringBuffer.append(line);
        }
        if (naqdMilliyDouble > 0) {
            Valuta v1 = GetDbData.getValuta(2);
            String line = String.format("%-15s %5s %10s\n", "Naqd " + v1.getValuta(), " ", decimalFormat.format(naqdMilliyDouble));
            printStringBuffer.append(line);
        }
        if (plasticDouble > 0) {
            String line = String.format("%-15s %5s %10s\n", "Plastik", " ", decimalFormat.format(plasticDouble));
            printStringBuffer.append(line);
        }
        if (qaytimDouble > 0) {
            String line = String.format("%-15s %5s %10s\n", "Qaytim", " ", decimalFormat.format(qaytimDouble));
            printStringBuffer.append(line);
        }
        String line = String.format("%-15s %5s %10s\n", "Balans", " ", decimalFormat.format(balansDouble));
        printStringBuffer.append(line);
        printStringBuffer.append(lineB);

        printStringBuffer.append(String.format("%29" +
                "s\n", "XARIDINGIZ UCHUN TASHAKKUR"));
        printStringBuffer.append(lineB);
        printStringBuffer.append(String.format("%s\n\n\n\n", ""));

        String chipta = printStringBuffer.toString().trim();
        System.out.println(chipta);

        PrinterService printerService = new PrinterService();
        printerService.printString(printerNomi, chipta);

    }

    private void tolovChiptasiniBer(String printerNomi) {
        StringBuffer printStringBuffer = new StringBuffer();
        String lineB = String.format("%.50s\n", "--------------------------------");
        SimpleDateFormat sana = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat vaqt = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        String sanaString = sana.format(date);
        String vaqtString = vaqt.format(date);
        printStringBuffer.append(lineB);
        String shirkatNomi = GetDbData.getHisob(user.getTovarHisobi()).getText();
        printStringBuffer.append(String.format("%23s\n", shirkatNomi));
        printStringBuffer.append(lineB);
        printStringBuffer.append(String.format("%-15s %16s\n", "Telefon", user.getPhone()));
        printStringBuffer.append(String.format("%-11s %20s\n", "Telegram", "t.me/best_perfumery"));
        printStringBuffer.append(String.format("%-15s %16s\n", "Sana", sanaString));
        printStringBuffer.append(String.format("%-15s %16s\n", "Vaqt", vaqtString));
        printStringBuffer.append(String.format("%-15s %16s\n", "Sotuvchi", user.getIsm()));
        printStringBuffer.append(String.format("%-15s %16s\n", "Oluvchi", qaydnomaData.getKirimNomi()));
        printStringBuffer.append(String.format("%-15s %16s\n", "Chipta N", qaydnomaData.getHujjat()));
        printStringBuffer.append(lineB);
        printStringBuffer.append(String.format("%-15s %5s %10s\n", "Mahsulot", "Dona", "Narh"));
        printStringBuffer.append(lineB);

        String space = "                    ";
        for (HisobKitob hk: tableViewObservableList) {
            Double dona = hk.getDona();
            Double narh = hk.getDona() * hk.getNarh();
            String line = String.format("%.15s %5s %10s\n", hk.getIzoh() + space, decimalFormat.format(dona), decimalFormat.format(hk.getNarh()));
            printStringBuffer.append(line);
            String lineS = String.format("%32s\n", decimalFormat.format(narh));
            printStringBuffer.append(lineS);
            printStringBuffer.append(lineB);
        }


        if (jamiMablag > 0) {
            String line = String.format("%-15s %5s %10s\n", "Xarid jami", " ", decimalFormat.format(jamiMablag));
            printStringBuffer.append(line);
        }
        if (chegirmaDouble > 0) {
            String line = String.format("%-15s %5s %10s\n", "Chegirma", " ", decimalFormat.format(chegirmaDouble));
            printStringBuffer.append(line);
        }

        if (naqdUsdDouble > 0) {
            Valuta v1 = GetDbData.getValuta(1);
            String line = String.format("%-15s %5s %10s\n", "Naqd " + v1.getValuta(), " ", decimalFormat.format(naqdUsdDouble));
            printStringBuffer.append(line);
        }
        if (naqdMilliyDouble > 0) {
            Valuta v1 = GetDbData.getValuta(2);
            String line = String.format("%-15s %5s %10s\n", "Naqd " + v1.getValuta(), " ", decimalFormat.format(naqdMilliyDouble));
            printStringBuffer.append(line);
        }
        if (plasticDouble > 0) {
            String line = String.format("%-15s %5s %10s\n", "Plastik", " ", decimalFormat.format(plasticDouble));
            printStringBuffer.append(line);
        }
        if (qaytimDouble > 0) {
            String line = String.format("%-15s %5s %10s\n", "Qaytim", " ", decimalFormat.format(qaytimDouble));
            printStringBuffer.append(line);
        }
        String line = String.format("%-15s %5s %10s\n", "Balans", " ", decimalFormat.format(balansDouble));
        printStringBuffer.append(line);
        printStringBuffer.append(lineB);

        printStringBuffer.append(String.format("%32s\n", "XARIDINGIZ UCHUN TASHAKKUR\n"));
        printStringBuffer.append(lineB);
        printStringBuffer.append(String.format("%s\n\n\n\n", ""));

        String chipta = printStringBuffer.toString().trim();
        System.out.println(chipta);

        PrinterService printerService = new PrinterService();
        printerService.printString(printerNomi, chipta);

    }

    private void tolovChiptasiniBer1(String printerNomi) {
        StringBuffer printStringBuffer = new StringBuffer();
        String lineB = String.format("%.50s\n", "------------------------------------------------");
        SimpleDateFormat sana = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat vaqt = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        String sanaString = sana.format(date);
        String vaqtString = vaqt.format(date);
        printStringBuffer.append(lineB);
        String shirkatNomi = GetDbData.getHisob(user.getTovarHisobi()).getText();
        printStringBuffer.append(String.format("%23s\n", shirkatNomi));
        printStringBuffer.append(lineB);
        printStringBuffer.append(String.format("%-15s %16s\n", "Telefon", user.getPhone()));
        printStringBuffer.append(String.format("%-15s %16s\n", "Sana", sanaString));
        printStringBuffer.append(String.format("%-15s %16s\n", "Vaqt", vaqtString));
        printStringBuffer.append(String.format("%-15s %16s\n", "Sotuvchi", user.getIsm()));
        printStringBuffer.append(String.format("%-15s %16s\n", "Oluvchi", qaydnomaData.getKirimNomi()));
        printStringBuffer.append(String.format("%-15s %16s\n", "Chipta N", qaydnomaData.getHujjat()));
        printStringBuffer.append(lineB);
        printStringBuffer.append(String.format("%-15s %5s %10s %10s\n", "Mahsulot", "Dona", "Narh", "Jami"));
        printStringBuffer.append(lineB);

        String space = "                    ";
        for (HisobKitob hk: tableViewObservableList) {
            Double dona = hk.getDona();
            Double narh = hk.getDona() * hk.getNarh();
            String line = String.format("%.15s %5s %10s %10s\n", hk.getIzoh() + space, decimalFormat.format(dona), decimalFormat.format(narh));
            printStringBuffer.append(line);
            String lineS = String.format("%32s\n", decimalFormat.format(narh));
            printStringBuffer.append(lineS);
            printStringBuffer.append(lineB);
        }


        if (jamiMablag > 0) {
            String line = String.format("%-15s %5s %10s\n", "Xarid jami", " ", decimalFormat.format(jamiMablag));
            printStringBuffer.append(line);
        }
        if (chegirmaDouble > 0) {
            String line = String.format("%-15s %5s %10s\n", "Chegirma", " ", decimalFormat.format(chegirmaDouble));
            printStringBuffer.append(line);
        }

        if (naqdUsdDouble > 0) {
            String line = String.format("%-15s %5s %10s\n", "Naqd", " ", decimalFormat.format(naqdUsdDouble));
            printStringBuffer.append(line);
        }
        if (plasticDouble > 0) {
            String line = String.format("%-15s %5s %10s\n", "Plastik", " ", decimalFormat.format(plasticDouble));
            printStringBuffer.append(line);
        }
        if (qaytimDouble > 0) {
            String line = String.format("%-15s %5s %10s\n", "Qaytim", " ", decimalFormat.format(qaytimDouble));
            printStringBuffer.append(line);
        }
        String line = String.format("%-15s %5s %10s\n", "Balans", " ", decimalFormat.format(balansDouble));
        printStringBuffer.append(line);
        printStringBuffer.append(lineB);

        printStringBuffer.append(String.format("%32s\n", "XARIDINGIZ UCHUN TASHAKKUR\n"));
        printStringBuffer.append(lineB);
        printStringBuffer.append(String.format("%s\n\n\n\n", ""));

        String chipta = printStringBuffer.toString().trim();
        System.out.println(chipta);

        PrinterService printerService = new PrinterService();
        printerService.printString(printerNomi, chipta);

    }

    private void kassaniTozala() {
        tableViewObservableList.removeAll(tableViewObservableList);
        tovarTableView.refresh();
        jamiMablag = 0.0;
        kassagaDouble = 0.0;
        chegirmaDouble = 0.0;
        naqdUsdDouble = 0.0;
        naqdMilliyDouble = 0.0;
        plasticDouble = 0.0;
        balansDouble = 0.0;
        qaytimDouble = 0.0;
        vaznDouble = .0;
        chegirmaTextField.setText("");
        naqdTextField.setText("");
        naqdMilliyTextField.setText("");
        plasticTextField.setText("");
        jamiHisob(tableViewObservableList);
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

    private Standart addTovar() {
        Standart tovar1 = null;
        TovarController tovarController = new TovarController(connection, user);
        tovarController.display();
        if (tovarController.getDoubleClick()) {
            tovar1 = tovarController.getDoubleClickedRow();
            boolean yangi = true;
            for (Standart t: tovarObservableList) {
                if (t.getId().equals(tovar1.getId())) {
                    yangi = false;
                    break;
                }
            }
            if (yangi) {
                tovarObservableList.add(tovar1);
                TextField textField = tovarBox.getTextField();
                TextFields.bindAutoCompletion(textField, tovarObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Standart> autoCompletionEvent) -> {
                    Standart newValue = autoCompletionEvent.getCompletion();
                    if (newValue != null) {
                        tovar = newValue;
                        tovarniYangila(tovar);
                    }
                });

            }
        }
        return tovar1;
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

    public class TreeItemClass extends HBox{
        private Integer itemId;
        private Double aDouble;
        private Label label = new Label();
        private Label label2 = new Label();
        private Standart standart;
        private HBoxTextFieldPlusButton hisobHBox;
        private TovarBox tovarBox;
        private Button button;
        private TextField textField;
        private TextField kursTextField;
        private ComboBox<Standart> comboBox;
        private Separator separator;
        Valuta valuta;

        public TreeItemClass(Integer itemId, String string, Double aDouble) {
            super(5);
            this.itemId = itemId;
            this.aDouble = aDouble;
            label.setText(string);
            label2.setText(decimalFormat.format(aDouble));

            this.getChildren().addAll(label, label2);
            this.setAlignment(Pos.CENTER_LEFT);
        }

        public TreeItemClass(Integer itemId, TextField textField) {
            super(5);
            this.itemId = itemId;
            this.textField = textField;
            textField.setMaxWidth(200);

            this.getChildren().addAll(textField);
            this.setAlignment(Pos.CENTER_LEFT);
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
            if (kurs != null) {
                kursDouble = kurs.getKurs();
            }
            kursTextField.setText(decimalFormat.format(kursDouble));
            this.getChildren().addAll(textField, kursTextField);
            this.setAlignment(Pos.CENTER_LEFT);
            kursTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    jamiHisob(tableViewObservableList);
                }
            });
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
    }

    private void jamiHisob(ObservableList<HisobKitob> hisobKitobViewObservableList) {
        Double kursMilliyDouble = getDoubleFromTextField(milliyKurs);
        Double kursPlasticDouble = getDoubleFromTextField(plasticKurs);
        chegirmaDouble = getDoubleFromTextField(chegirmaTextField);
        naqdUsdDouble = getDoubleFromTextField(naqdTextField);
        naqdMilliyDouble = getDoubleFromTextField(naqdMilliyTextField);
        plasticDouble = getDoubleFromTextField(plasticTextField);
        if (valuta.getId() == 2) {
            naqdUsdDouble = naqdUsdDouble * kursMilliyDouble;
        }
        if (valuta.getId() == 1) {
            naqdMilliyDouble = naqdMilliyDouble / kursMilliyDouble;
        }
        if (valuta.getId() == 1) {
            plasticDouble = plasticDouble / kursPlasticDouble;
        }

        Double jamiMablag = 0.0;
        for (HisobKitob hk : hisobKitobViewObservableList) {
            jamiMablag += hk.getDona() * hk.getNarh();
        }
        jamiMablagLabel.setText(decimalFormat.format(jamiMablag));
        kassagaDouble = jamiMablag - chegirmaDouble;
        kassagaLabel.setText(decimalFormat.format(kassagaDouble));
        Double jamiTolov = naqdUsdDouble + naqdMilliyDouble + plasticDouble;
        Double jamiDouble = jamiTolov - kassagaDouble;
        if (jamiTolov > kassagaDouble) {
            qaytimDouble = jamiDouble;
            balansDouble = 0d;
        } else {
            balansDouble = jamiDouble;
            qaytimDouble = 0d;
        }
        qaytimLabel.setText(decimalFormat.format(qaytimDouble));
        balansLabel.setText(decimalFormat.format(balansDouble));
    }

    public static Double getDoubleFromTextField(TextField textField) {
        Double doubleValue = 0d;
        String textValue = textField.getText();
        if (textValue != null) {
            textValue = textValue.replaceAll(",", ".");
            textValue = textValue.replaceAll(" ", "");
            doubleValue = textValue.isEmpty() ? 0d : Double.valueOf(textValue);
        }
        return doubleValue;
    }

/*
    private void jamiHisob(ObservableList<HisobKitob> hisobKitobViewObservableList, String value, int valueId) {
        Double kursMilliyDouble = getDoubleFromTextField(milliyKurs);
        Double kursPlasticDouble = getDoubleFromTextField(plasticKurs);
        if (value == null) {
            value = "0.0";
        }
        if (value.isEmpty()) {
            value = "0.0";
        }
        jamiMablag = 0.0;
        for (HisobKitob hk : hisobKitobViewObservableList) {
            jamiMablag += hk.getDona() * hk.getNarh();
        }
        jamiMablagLabel.setText(decimalFormat.format(jamiMablag));

        switch (valueId) {
            case 1: //chegirma
                chegirmaDouble = Double.valueOf(value);
                break;
            case 2: //naqd usd
                naqdDouble = Double.valueOf(value);
                if (valuta.getId() == 2) {
                    naqdDouble = naqdDouble * kursMilliyDouble;
                }
                break;
            case 3:
                naqdMilliyDouble = Double.valueOf(value);
                if (valuta.getId() == 1) {
                    naqdMilliyDouble = naqdMilliyDouble / kursMilliyDouble;
                }
                break;
            case 4: //plastic
                plasticDouble = Double.valueOf(value);
                if (valuta.getId() == 1) {
                    plasticDouble = plasticDouble / kursPlasticDouble;
                }
                break;
        }
        kassagaDouble = jamiMablag - chegirmaDouble;
        kassagaLabel.setText(decimalFormat.format(kassagaDouble));
        Double jamiTolov = naqdDouble + naqdMilliyDouble + plasticDouble;
        Double jamiDouble = jamiTolov - kassagaDouble;
        if (jamiDouble > 0) {
            qaytimDouble = jamiDouble;
        } else {
            qaytimDouble = 0.0;
        }
        qaytimLabel.setText(decimalFormat.format(qaytimDouble));
        balansDouble = (jamiTolov - qaytimDouble) - kassagaDouble;
        balansLabel.setText(decimalFormat.format(balansDouble));
    }
*/

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

    private boolean isAltCombination(String eventText) {
        boolean isAltCombination = false;
        if (eventText.equals("??") ||
                eventText.equals("??") ||
                eventText.equals(",")
        ) {
            isAltCombination = true;
        }
        return isAltCombination;
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

        Runnable runChegirma = () -> {
            chegirmaTextField.requestFocus();
        };
        scene.getAccelerators().put(kcC, runChegirma);

        Runnable runNaqd = () -> naqdTextField.requestFocus();
        scene.getAccelerators().put(kcN, runNaqd);

        Runnable runPlastic = () -> plasticTextField.requestFocus();
        scene.getAccelerators().put(kcP, runPlastic);

        Runnable runTovar = () -> {
            TextField textField  = tovarBox.getTextField();
            textField.requestFocus();
        };
        scene.getAccelerators().put(kcT, runTovar);

        Runnable runNaqdMilliy = () -> naqdMilliyTextField.requestFocus();
        scene.getAccelerators().put(kcS, runNaqdMilliy);

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
                                addTovar(barCode);
                                jamiHisob(tableViewObservableList);
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
                                addTovar(barCode);
                                jamiHisob(tableViewObservableList);
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

    private void addTovarYangi(BarCode barCode) {
        Integer narhTuri = narhComboBox.getValue().getId();
        standartModels.setTABLENAME("Tovar");
        Standart tovar = GetDbData.getTovar(barCode.getTovar());
        if (tovar == null) {
            Alerts.AlertString("Tovar id: " + barCode.getTovar() + "\nBu tovar bazada yo`q");
            return;
        }
        HisobKitob hisobKitob = new HisobKitob(0, 0, 0, amalTuri, kassa.getTovarHisobi(),
                hisob2.getId(), valuta.getId(), tovar.getId(), 0.0, barCode.getBarCode(), 1.0,
                0.00, 0, tovar.getText(), user.getId(), new Date()
        );
        if (valuta.getStatus() == 1) {
            hisobKitob.setKurs(1.00);
        } else {
            hisobKitob.setKurs(getKurs(hisobKitob.getValuta(), hisobKitob.getDateTime()).getKurs());
        }

        narhOl(barCode, hisobKitob, narhTuri);
        double zaxiradagiAdad = hisobKitobModels.getBarCodeCount(connection, kassa.getTovarHisobi(), hisobKitob.getBarCode());
        double adad = addTovarToTable(barCode, hisobKitob, zaxiradagiAdad);
        if (zaxiradagiAdad < adad) {
            Alerts.showKamomat(tovar, adad, barCode.getBarCode(), zaxiradagiAdad);
        }
        tovarTableView.refresh();
        tovarBox.getTextField().setText("");
    }

    private void narhOl(BarCode barCode, HisobKitob hisobKitob, Integer narhTuri) {
        standart4Models.setTABLENAME("Nds");
        Standart4 ndsStandart4 = standart4Models.getTartibForDate(connection, hisobKitob.getTovar(), new Date(), "dateTime desc");
        double nds = 0.0;
        if (ndsStandart4 != null) {
            nds = ndsStandart4.getMiqdor() * 0.01;
        }

        double narhDouble = 0d;
        double narhDoubleFinal = 0d;
/*
        ObservableList<HisobKitob> narhList = hisobKitobModels.getAnyData(connection, "hisob2 = " + hisobKitob.getHisob2() + " and amal = 4 and barcode = '" + hisobKitob.getBarCode() + "'", "id desc limit 1");
        if (narhList.size()>0) {
            HisobKitob hk = narhList.get(0);
            Double eskiNarh = hk.getNarh()/hk.getKurs();
            Double yangiNarh = eskiNarh * hisobKitob.getKurs();
            hisobKitob.setNarh(yangiNarh);
            return;
        }
*/
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
        return;
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
            jamiHisob(tableViewObservableList);
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
        navbatdagiXaridorButton.setDisable(!disable);
        tovarTableView.setDisable(disable);
        xaridniYakunlaButton.setDisable(disable);
        xaridnBekorQilButton.setDisable(disable);
        treeView.setDisable(disable);
    }

    public void narhYoz(int tovarId, int narhTuri, Double narhDouble) {
        Date date = new Date();
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
    public Standart6 guruhliTovarNarhi(int tovarId) {
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
}
