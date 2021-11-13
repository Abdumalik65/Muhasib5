package sample.Controller;

import com.sun.javafx.scene.control.skin.TableViewSkin;
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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Sotuvchi4 extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    MenuBar mainMenu;

    TreeView<TreeItemClass> treeView = new TreeView();
    TableView<HisobKitob> tovarTableView = new TableView();
    TableView<HisobKitob> tolovTableView;
    TableView<HisobKitob> qaytimTableView;

    TreeItem<TreeItemClass> rootTreeItem;
    TreeItem<TreeItemClass> xaridNarhiTreeItem;
    TreeItem<TreeItemClass> chegirmaTreeItem;
    TreeItem<TreeItemClass> kassagaTreeItem;
    TreeItem<TreeItemClass> balansTreeItem;
    TreeItem<TreeItemClass> qaytimTreeItem;
    TreeItem<TreeItemClass> naqdTreeItem;

    GridPane leftGridPane = new GridPane();
    VBox rightPane = new VBox();
    HBox topHBox = new HBox();
    HBox bottomHBox = new HBox();

    TextField tovarTextField = new TextField();
    TextField barCodeTextField = new TextField();
    TextField chegirmaTextField = new TextField();
    TextField naqdTextField = new TextField();
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

    User user = new User(1, "admin", "", "admin", "", "", 1, "", 0, 1, null);
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
    HBoxTextFieldPlusButton tovarHBox = new HBoxTextFieldPlusButton();
    HBoxTextFieldPlusButton valutaHBox = new HBoxTextFieldPlusButton();
    Button navbatdagiXaridorButton = new Button("Navbatdagi xaridor");
    Button xaridnBekorQilButton = new Button("Xaridni bekor qil");
    Button xaridniYakunlaButton = new Button("Xaridni yakunla");


    int amalTuri = 4;
    int padding = 3;

    Double jamiMablag = 0.0;
    Double kassagaDouble = 0.0;
    Double chegirmaDouble = 0.0;
    Double naqdDouble = 0.0;
    Double plasticDouble = 0.0;
    Double balansDouble = 0.0;
    Double qaytimDouble = 0.0;
    Double kursDouble = 0d;
    Double vaznDouble = .0;

    Label jamiMablagLabel = new Label();
    Label kassagaLabel = new Label();
    Label balansLabel = new Label();
    Label qaytimLabel = new Label();

    DecimalFormat decimalFormat = new MoneyShow();
    String style20 = "-fx-font: 20px Arial";
    Font font = Font.font("Arial",20);
    Font font1 = Font.font("Arial", FontWeight.BOLD,20);
    StringBuffer stringBuffer = new StringBuffer();

    ObservableList<HisobKitob> tableViewObservableList = FXCollections.observableArrayList();
    ObservableList<HisobKitob> tolovList = FXCollections.observableArrayList();
//    ObservableList<HisobKitob> qaytimList = FXCollections.observableArrayList();
    ObservableList<Standart> tovarObservableList;
    ObservableList<Valuta> valutaObservableList;
    ObservableList<Standart> narhTuriObservableList;
    ObservableList<Standart> birlikObservableList;
    ObservableList<Standart> chiqimShakliObservableList;
    ObservableList<Standart> tolovShakliObservableList;
    ObservableList<Hisob> hisobObservableList;
    ObservableList<BarCode> barCodeList = FXCollections.observableArrayList();
    ObservableList<TovarNarhi> naqdList = FXCollections.observableArrayList();
    ObservableList<TextField> naqdTextFieldList = FXCollections.observableArrayList();
    ObservableList<TovarNarhi> qaytimList = FXCollections.observableArrayList();
    ObservableList<TextField> qaytimTextFieldList = FXCollections.observableArrayList();

    public static void main(String[] args) {
        launch(args);
    }

    public Sotuvchi4() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        GetDbData.initData(connection);
        ibtido();
    }

    public Sotuvchi4(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        ibtido();
    }

    public Sotuvchi4(Connection connection, User user, QaydnomaData qaydnomaData) {
        this.connection = connection;
        this.user = user;
        this.qaydnomaData = qaydnomaData;
        ibtido();
    }

    private void initData1() {
        tableViewObservableList = hisobKitobModels.getAnyData(connection, "qaydId = " + qaydnomaData.getId(), "");

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        if (buKassami()) {
            login();
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
        initTableView();
        initGridPane();
        initNavbatdagiXaridorButton();
        initXaridorBekorQilButton();
        initXaridniYakunlaButton();
        initTopPane();
        initBottomPane();
        initTreeView();
        initRightPane();
        initCenterPane();
        initSystemMenu();
        initBorderPane();
        setDisable(true);
    }

    private void initData() {
        hisobObservableList = hisobModels.get_data(connection);
        String serialNumber = getSerialNumber();
        kassa = getKassaData(connection, serialNumber);
        valuta = GetDbData.getValuta(kassa.getValuta());
        kursDouble = getKurs(valuta.getId(), new Date()).getKurs();
        valutaObservableList = GetDbData.getValutaObservableList();
        if (valutaObservableList.size()>0) {
            for (Valuta v: valutaObservableList) {
                Kurs kurs = getKurs(v.getId(), new Date());
                naqdList.add(new TovarNarhi(v.getId(), null, null, null, v.getStatus(), kurs.getKurs(),0d,null,null));
                qaytimList.add(new TovarNarhi(v.getId(), null, null, null, v.getStatus(), kurs.getKurs(),0d,null,null));
            }
        }
        tovarObservableList = GetDbData.getTovarObservableList();
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
        HBox.setHgrow(topHBox, Priority.ALWAYS);
        topHBox.getChildren().addAll(navbatdagiXaridorButton, xaridnBekorQilButton);
    }

    private void initCenterPane() {
    }

    private void initRightPane() {
        SetHVGrow.VerticalHorizontal(rightPane);
        rightPane.getChildren().add(treeView);
    }

    private void initBottomPane() {
        HBox.setHgrow(bottomHBox, Priority.ALWAYS);
        bottomHBox.getChildren().add(xaridniYakunlaButton);
    }

    private void initBorderPane() {
        borderpane.setPadding(new Insets(padding));
        SetHVGrow.VerticalHorizontal(borderpane);
        borderpane.setTop(mainMenu);
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
        });


        MenuItem tovarHisobotiMenuItem = new MenuItem("Tovar hisoboti");
        MenuItem tovarKirimiMenuItem = new MenuItem("Tovar kirimi");
        MenuItem tovarChiqimiMenuItem = new MenuItem("Tovar chiqimi");
        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
        MenuItem pochkaBizishMenuItem = new MenuItem("Pochka buzish");
        MenuItem tovarGuruhlariMenuItem = new MenuItem("Tovar guruhlari");

        tovarMenu.getItems().addAll(tovarHisobotiMenuItem, tovarKirimiMenuItem, tovarChiqimiMenuItem, separatorMenuItem, pochkaBizishMenuItem, tovarGuruhlariMenuItem);

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
        });

        tovarChiqimiMenuItem.setOnAction(event -> {
            TovarKirimChiqimi tovarKirimChiqimi = new TovarKirimChiqimi(connection, user, false, kassa);
            tovarKirimChiqimi.display();
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
            PulHisoboti pulHisoboti = new PulHisoboti(connection, user);
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

    private Menu getTovarMenu() {
        Menu menu = new Menu("Xarid");
        MenuItem menuItem = getXaridJadvaliMenuItem();
        menu.getItems().addAll(getXaridJadvaliMenuItem());
        return menu;

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
        leftGridPane.add(topHBox, 0, rowIndex, 1, 1);
        GridPane.setHgrow(topHBox, Priority.ALWAYS);

        rowIndex++;
        leftGridPane.add(tovarTableView, 0, rowIndex, 1, 1);
        GridPane.setHgrow(tovarTableView, Priority.ALWAYS);
        GridPane.setVgrow(tovarTableView, Priority.ALWAYS);
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
                    narhYoz(tovar.getId(), narhComboBox.getValue().getId(), yangiNarhDouble);
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
        rootTreeItem = new TreeItem(getRootTreeItem());
        rootTreeItem.getChildren().addAll(
                getSozlovTreeItem(),    //40
                getTovarTreeItem(),   //50
                getXaridNarhiLabelTreeItem(),   //60
                getChegirmaLabelTreeItem(), //70
                getKassagaLabelTreeItem(),  //80
                getTolandiLabelTreeItem(),  //90
                getQaytim2LabelTreeItem(),   //100
                getBalansLabelTreeItem()   //110
        );
        treeView.setRoot(rootTreeItem);
        treeView.setShowRoot(false);
        treeView.setMinWidth(300);
        treeView.setMaxWidth(300);
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
                TovarKirimChiqimi tovarKirimChiqimi = new TovarKirimChiqimi(connection, user, false, kassa);
                tovarKirimChiqimi.display();
                break;
            case 34:
                PochkaBuzish pochkaBuzish = new PochkaBuzish(connection, user, hisob1);
                pochkaBuzish.display();
                break;
        }
    }

    private TreeItem<TreeItemClass> getSozlovTreeItem() {
        initHisob1HBox();
        initHisob2HBox();
        initValutaHBox();
        initNarhComboBox();
        TreeItemClass treeItemClass = new TreeItemClass(40, new Label("Savdo sozlovlari"));
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
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
        TreeItemClass treeItemClass = new TreeItemClass(41, hisob1HBox);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getHisob2TreeItem() {
        TreeItemClass treeItemClass = new TreeItemClass(42, hisob2HBox);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getValutaTreeItem() {
        TreeItemClass treeItemClass = new TreeItemClass(43, valutaHBox);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getNarhTuriTreeItem() {
        TreeItemClass treeItemClass = new TreeItemClass(44, narhComboBox);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getTovarTreeItem() {
        initTovarHBox();
        initBirlikComboBox();
        HBoxTextFieldPlusButton hBoxTextFieldPlusButton = new HBoxTextFieldPlusButton();
        TreeItemClass treeItemClass = new TreeItemClass(50, new Label("Tovar"));
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        treeItem.getChildren().addAll(
                getTovarNomiTreeItem(),
                getBirlikTreeItem(),
                getBarCodeTreeItem()
        );
        treeItem.setExpanded(false);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getTovarNomiTreeItem() {
        TreeItemClass treeItemClass = new TreeItemClass(51, tovarHBox);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        return treeItem;

    }

    private TreeItem<TreeItemClass> getBirlikTreeItem() {
        TreeItemClass treeItemClass = new TreeItemClass(52, birlikComboBox);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getBarCodeTreeItem() {
        TreeItemClass treeItemClass = new TreeItemClass(53, barCodeTextField);
        TextField textField = treeItemClass.getTextField();
        textField.setPromptText("STRIXKOD");
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getXaridNarhiLabelTreeItem() {
        TreeItemClass treeItemClass = new TreeItemClass(60, new Label("Xarid narhi: "));
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        treeItem.getChildren().add(getXaridNarhiTreeItem());
        treeItem.setExpanded(true);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getXaridNarhiTreeItem() {
        TreeItemClass treeItemClass = new TreeItemClass(61, jamiMablagLabel, valuta);
        xaridNarhiTreeItem = new TreeItem(treeItemClass);
        return xaridNarhiTreeItem;
    }

    private TreeItem<TreeItemClass> getChegirmaLabelTreeItem() {
        TreeItemClass treeItemClass = new TreeItemClass(70, new Label("Chegirma: "));
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        treeItem.getChildren().add(getChegirmaTreeItem());
        treeItem.setExpanded(true);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getChegirmaTreeItem() {
        Integer itemId = 71;
        TreeItemClass treeItemClass = new TreeItemClass(itemId, chegirmaTextField, valuta);
        chegirmaTreeItem = new TreeItem(treeItemClass);
/*
        ObservableValue<TextField> observableValue = new SimpleObjectProperty(valutaHBox.getTextField());
        observableValue.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                textFieldValuta.getLabel().setText(newValue.getText());
            }
        });
*/

        TextField textField = treeItemClass.getTextField();
        textField.setPromptText("CHEGIRMA");
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (!newValue) {
                    String string = textField.getText();
                    string = string.replaceAll(",", ".");
                    string = string.replaceAll(" ", "");
                    if (!string.isEmpty()) {
                        Double aDouble = Double.valueOf(string);
                        textField.setText(decimalFormat.format(aDouble));
                    }
                }
            }
        });
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newValue = newValue.replaceAll(",", ".");
                newValue = newValue.replaceAll(" ", "");
                if(Alerts.isNumericAlert(newValue, false)) {
                    jamiHisob(tableViewObservableList,newValue, 1);
                } else {
                    jamiHisob(tableViewObservableList,"0.0", 1);
                }

            }
        });

        return chegirmaTreeItem;
    }

    private TreeItem<TreeItemClass> getKassagaLabelTreeItem() {
        TreeItemClass treeItemClass = new TreeItemClass(80, new Label("Kassaga: "));
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        treeItem.getChildren().add(getKassagaTreeItem());
        treeItem.setExpanded(true);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getKassagaTreeItem() {
        TreeItemClass treeItemClass = new TreeItemClass(81, kassagaLabel, valuta);
        kassagaTreeItem = new TreeItem(treeItemClass);
        return kassagaTreeItem;
    }

    private TreeItem<TreeItemClass> getTolandiLabelTreeItem() {
        GridPane gridPane = new GridPane();
        gridPane.setGridLinesVisible(true);
        TreeItemClass treeItemClass = new TreeItemClass(90, new Label("To`landi: "));
        naqdTreeItem = new TreeItem(treeItemClass);
        int itemId = 91;
        for (Valuta v: valutaObservableList) {
            TreeItem<TreeItemClass> tc = getNaqd3TreeItem(itemId, v);
            TextField tf = tc.getValue().getTextField();
            Label label = tc.getValue().getLabel();
            gridPane.add(tf, 0, itemId-91, 1, 1);
            gridPane.add(label, 1, itemId-91, 1, 1);
            naqdTreeItem.getChildren().addAll(getNaqd3TreeItem(itemId, v));
            itemId++;
        }
        TreeItem<TreeItemClass> plasticTreeItem = getPlastik3TreeItem(itemId);
        naqdTreeItem.getChildren().add(plasticTreeItem);
        naqdTreeItem.setExpanded(true);
        return naqdTreeItem;
    }

    private TreeItem<TreeItemClass> getNaqdTreeItem() {
        TreeItemClass treeItemClass = new TreeItemClass(91, naqdTextField);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        TextField textField = treeItemClass.getTextField();
        textField.setPromptText("NAQD");
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (!newValue) {
                    String string = textField.getText();
                    string = string.replaceAll(",", ".");
                    string = string.replaceAll(" ", "");
                    if (!string.isEmpty()) {
                        Double aDouble = Double.valueOf(string);
                        textField.setText(decimalFormat.format(aDouble));
                    }
                }
            }
        });
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newValue = newValue.replaceAll(",", ".");
                newValue = newValue.replaceAll(" ", "");
                if(Alerts.isNumericAlert(newValue, false)) {
                    jamiHisob(tableViewObservableList,newValue, 2);
                } else {
                    jamiHisob(tableViewObservableList,"0.0", 2);
                }
            }
        });
        return treeItem;
    }

    private TreeItem<TreeItemClass> getNaqd2TreeItem() {
        tolovTableView = initTolovTableView();
        TreeItemClass treeItemClass = new TreeItemClass(91, tolovTableView);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getNaqd3TreeItem(int itemId, Valuta valuta) {
        TextField textField = new TextField();
        setFocusPropertyListener(textField);
        TreeItemClass treeItemClass = new TreeItemClass(itemId, textField, valuta);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newValue = newValue.replaceAll(",", ".");
                newValue = newValue.replaceAll(" ", "");
                int valueId = 0;
                int count = 0;
                TovarNarhi tovarNarhi = null;
                for (TovarNarhi tn: naqdList) {
                    if (tn.getId().equals(valuta.getId())) {
                        tovarNarhi = tn;
                        valueId = count;
                        break;
                    }
                    count++;
                }
                if(Alerts.isNumericAlert(newValue, false) && !newValue.isEmpty()) {
                    tovarNarhi.setNarh(Double.valueOf(newValue));
                    jamiHisob(tableViewObservableList);
                } else {
                    tovarNarhi.setNarh(Double.valueOf(0.0));
                    jamiHisob(tableViewObservableList);
                }
            }
        });
        return treeItem;
    }

    private void naqdRefresh() {
        naqdDouble = jamiTnDouble(naqdList);
        qaytimDouble = jamiTnDouble(qaytimList);
        if ((kassagaDouble - naqdDouble) !=0) {
        }
    }

    private TreeItem<TreeItemClass> getPlastikTreeItem() {
        TreeItemClass treeItemClass = new TreeItemClass(92, plasticTextField);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        TextField textField = treeItemClass.getTextField();
        textField.setPromptText("PLASTIK");
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (!newValue) {
                    String string = textField.getText();
                    string = string.replaceAll(",", ".");
                    string = string.replaceAll(" ", "");
                    if (!string.isEmpty()) {
                        Double aDouble = Double.valueOf(string);
                        textField.setText(decimalFormat.format(aDouble));
                    }
                }
            }
        });
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newValue = newValue.replaceAll(",", ".");
                newValue = newValue.replaceAll(" ", "");
                if(Alerts.isNumericAlert(newValue, false)) {
                    jamiHisob(tableViewObservableList,newValue, 3);
                } else {
                    jamiHisob(tableViewObservableList,"0.0", 3);
                }
            }
        });
        return treeItem;
    }

    private TreeItem<TreeItemClass> getPlastik2TreeItem(Integer itemId) {
        TreeItemClass treeItemClass = new TreeItemClass(itemId, plasticTextField);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        TextField textField = treeItemClass.getTextField();
        textField.setPromptText("PLASTIK");
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (!newValue) {
                    String string = textField.getText();
                    string = string.replaceAll(",", ".");
                    string = string.replaceAll(" ", "");
                    if (!string.isEmpty()) {
                        Double aDouble = Double.valueOf(string);
                        textField.setText(decimalFormat.format(aDouble));
                    }
                }
            }
        });
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newValue = newValue.replaceAll(",", ".");
                newValue = newValue.replaceAll(" ", "");
                if(Alerts.isNumericAlert(newValue, false)) {
                    jamiHisob(tableViewObservableList,newValue, 3);
                } else {
                    jamiHisob(tableViewObservableList,"0.0", 3);
                }
            }
        });
        return treeItem;
    }

    private TreeItem<TreeItemClass> getPlastik3TreeItem(Integer itemId) {
        Valuta valuta = GetDbData.getValuta(kassa.getValuta());
        TreeItemClass treeItemClass = new TreeItemClass(itemId, plasticTextField, valuta);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);

        TextField textField = treeItemClass.getTextField();
        textField.setPromptText("PLASTIK");
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (!newValue) {
                    String string = textField.getText();
                    string = string.replaceAll(",", ".");
                    string = string.replaceAll(" ", "");
                    if (!string.isEmpty()) {
                        Double aDouble = Double.valueOf(string);
                        textField.setText(decimalFormat.format(aDouble));
                    }
                }
            }
        });
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newValue = newValue.replaceAll(",", ".");
                newValue = newValue.replaceAll(" ", "");
                if(Alerts.isNumericAlert(newValue, false)) {
                    jamiHisob(tableViewObservableList,newValue, 3);
                } else {
                    jamiHisob(tableViewObservableList,"0.0", 3);
                }
            }
        });
        return treeItem;
    }

    private TreeItem<TreeItemClass> getQaytimLabelTreeItem() {
        TreeItemClass treeItemClass = new TreeItemClass(100, new Label("Qaytim: "));
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        treeItem.getChildren().add(getQaytim2TreeItem());
        treeItem.setExpanded(true);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getQaytim2LabelTreeItem() {
        GridPane gridPane = new GridPane();
        gridPane.setGridLinesVisible(true);
        TreeItemClass treeItemClass = new TreeItemClass(100, new Label("Qaytim: "));
        qaytimTreeItem = new TreeItem(treeItemClass);
        int itemId = 101;
        for (Valuta v: valutaObservableList) {
            TreeItem<TreeItemClass> tc = getNaqd3TreeItem(itemId, v);
            TextField tf = tc.getValue().getTextField();
            Label label = tc.getValue().getLabel();
            gridPane.add(tf, 0, itemId-101, 1, 1);
            gridPane.add(label, 1, itemId-101, 1, 1);
            qaytimTreeItem.getChildren().addAll(getQaytim3TreeItem(itemId, v));
            itemId++;
        }
        qaytimTreeItem.setExpanded(true);
        return qaytimTreeItem;
    }

    private TreeItem<TreeItemClass> getQaytim3TreeItem(int itemId, Valuta valuta) {
        TextField textField = new TextField();
        setFocusPropertyListener(textField);
        TreeItemClass treeItemClass = new TreeItemClass(itemId, textField, valuta);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newValue = newValue.replaceAll(",", ".");
                newValue = newValue.replaceAll(" ", "");
                int valueId = 0;
                int count = 0;
                TovarNarhi tovarNarhi = null;
                for (TovarNarhi tn: qaytimList) {
                    if (tn.getId().equals(valuta.getId())) {
                        tovarNarhi = tn;
                        valueId = count;
                        break;
                    }
                    count++;
                }
                if(Alerts.isNumericAlert(newValue, false) && !newValue.isEmpty()) {
                    tovarNarhi.setNarh(Double.valueOf(newValue));
                    jamiHisob(tableViewObservableList);
                } else {
                    tovarNarhi.setNarh(Double.valueOf(0d));
                    jamiHisob(tableViewObservableList);
                }
            }
        });
        return treeItem;
    }

    private TreeItem<TreeItemClass> getQaytimTreeItem() {
        TreeItemClass treeItemClass = new TreeItemClass(101, qaytimLabel);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getQaytim2TreeItem() {
//        qaytimTableView = initQaytimTableView();
        TreeItemClass treeItemClass = new TreeItemClass(101, qaytimTableView);
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getBalansLabelTreeItem() {
        TreeItemClass treeItemClass = new TreeItemClass(110, new Label("Balans: "));
        TreeItem<TreeItemClass> treeItem = new TreeItem(treeItemClass);
        treeItem.getChildren().add(getBalansTreeItem());
        treeItem.setExpanded(true);
        return treeItem;
    }

    private TreeItem<TreeItemClass> getBalansTreeItem() {
        TreeItemClass treeItemClass = new TreeItemClass(111, balansLabel, valuta);
        balansTreeItem = new TreeItem(treeItemClass);
        return balansTreeItem;
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
            GetDbData.initData(connection);
            user = loginUserController.getUser();
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
            }
        });

        Button addButton = hisob1HBox.getPlusButton();
        addButton.setOnAction(event -> {
            Hisob newHisob = addHisob();
            if (newHisob != null) {
                hisob1 = newHisob;
                textField.setText(hisob1.getText());
            }
        });
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
            }
        });

        Button addButton = hisob2HBox.getPlusButton();
        addButton.setOnAction(event -> {
            Hisob newHisob = addHisob();
            if (newHisob != null) {
                hisob2 = newHisob;
                textField.setText(hisob2.getText());
            }
        });
    }

    private void initTovarHBox() {
        HBox.setHgrow(tovarHBox, Priority.ALWAYS);
        TextField textField = tovarHBox.getTextField();
        TextFields.bindAutoCompletion(textField, tovarObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Standart> autoCompletionEvent) -> {
            Standart newValue = autoCompletionEvent.getCompletion();
            if (newValue != null) {
                tovar = newValue;
                tovarniYangila(tovar);
            }
        });

        Button addButton = tovarHBox.getPlusButton();
        addButton.setOnAction(event -> {
            Standart newValue = addTovar();
            if (newValue != null) {
                tovar = newValue;
                textField.setText(tovar.getText());
                tovarniYangila(tovar);
            }
        });
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
                if (valuta.getStatus()!=1) {
                    kursDouble = getKurs(valuta.getId(), new Date()).getKurs();
                } else{
                    kursDouble = 1d;
                }
                chegirmaTreeItem.getValue().setValuta(valuta);
                chegirmaTreeItem.getValue().getLabel().setText(valuta.getValuta());
                kassagaTreeItem.getValue().getLabel2().setText(valuta.getValuta());
                kassagaTreeItem.getValue().setValuta(valuta);
                xaridNarhiTreeItem.getValue().getLabel2().setText(valuta.getValuta());
                xaridNarhiTreeItem.getValue().setValuta(valuta);
                balansTreeItem.getValue().getLabel2().setText(valuta.getValuta());
                balansTreeItem.getValue().setValuta(valuta);
            }
        });

        Button addButton = valutaHBox.getPlusButton();
        addButton.setOnAction(event -> {
            Valuta newValue = addValuta();
            if (newValue != null) {
                valuta = newValue;
                if (valuta.getStatus()!=1) {
                    kursDouble = getKurs(valuta.getId(), new Date()).getKurs();
                } else{
                    kursDouble = 1d;
                }
                textField.setText(valuta.getValuta());
                chegirmaTreeItem.getValue().setValuta(valuta);
                chegirmaTreeItem.getValue().getLabel().setText(valuta.getValuta());
                kassagaTreeItem.getValue().getLabel2().setText(valuta.getValuta());
                kassagaTreeItem.getValue().setValuta(valuta);
                xaridNarhiTreeItem.getValue().getLabel2().setText(valuta.getValuta());
                xaridNarhiTreeItem.getValue().setValuta(valuta);
                balansTreeItem.getValue().getLabel2().setText(valuta.getValuta());
                balansTreeItem.getValue().setValuta(valuta);
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
        navbatdagiXaridorButton.setFont(font1);
        navbatdagiXaridorButton.setMaxWidth(2000);
        navbatdagiXaridorButton.setPrefWidth(150);
        navbatdagiXaridorButton.setOnAction(event -> {
            barCodeOn();
            setDisable(false);
        });
    }

    private void initXaridorBekorQilButton() {
        HBox.setHgrow(xaridnBekorQilButton, Priority.ALWAYS);
        xaridnBekorQilButton.setFont(font1);
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
        xaridniYakunlaButton.setFont(font1);
        xaridniYakunlaButton.setMaxWidth(2000);
        xaridniYakunlaButton.setPrefWidth(150);
        xaridniYakunlaButton.setOnAction(event -> {
            xaridniYakunla();
        });
    }

    private TableView<HisobKitob> initTolovTableView() {
        TableView<HisobKitob> tableView = new TableView<>();
        Date date = new Date();
        ObservableList<Valuta> valutaObservableList = GetDbData.getValutaObservableList();
        for (Valuta v: valutaObservableList) {
            Kurs kurs = getKurs(v.getId(), date);
            HisobKitob hk = new HisobKitob(
                    1, 1,1,1,0, 0, v.getId(), 0, kurs.getKurs(),
                    "", 0d, 0d, 0, "", user.getId(), date
            );
            tolovList.add(hk);
        }
        GetTableView2 getTableView = new GetTableView2();
        tableView.getColumns().addAll(
                getTableView.getNarhColumn(),
                getTableView.getValutaColumn()
        );
        tableView.setItems(tolovList);
        return tableView;
    }

/*
    private TableView<HisobKitob> initQaytimTableView() {
        TableView<HisobKitob> tableView = new TableView<>();
        Date date = new Date();
        ObservableList<Valuta> valutaObservableList = GetDbData.getValutaObservableList();
        for (Valuta v: valutaObservableList) {
            Kurs kurs = getKurs(v.getId(), date);
            HisobKitob hk = new HisobKitob(
                    1, 1,1,1,0, 0, v.getId(), 0, kurs.getKurs(),
                    "", 0d, 0d, 0, "", user.getId(), date
            );
            qaytimList.add(hk);
        }
        GetTableView2 getTableView = new GetTableView2();
        tableView.getColumns().addAll(
                getTableView.getNarhColumn(),
                getTableView.getValutaColumn()
        );
        tableView.setItems(qaytimList);
        return tableView;
    }

*/
    private void xaridniYakunla() {
        Boolean tolovSahih = tolovSahih();
        boolean nasiyagaSot = true;
        if (!tolovSahih) {
            nasiyagaSot = Alerts.nasiyagaSot(balansDouble, valutaHBox.getTextField().getText());
        }
        if (nasiyagaSot) {
            qaydnomaData = yangiQaydnoma();
            tovarSot(tableViewObservableList);
            ObservableList<HisobKitob> hkList = FXCollections.observableArrayList();
            if (chegirmaDouble > 0) {
                hkList.add(chegirmaniQaydEt());
            }
            if (naqdDouble > 0) {
                hkList.add(naqdTolovniQaydEt());
            }
            if (plasticDouble > 0) {
                hkList.add(plasticToloviniQaydEt());
                hkList.add(bankXizmatiniQaydEt());
            }
            if (qaytimDouble > 0) {
                hkList.add(qaytimniQaydEt());
            }
            hisobKitobModels.addBatch(connection, hkList);
            tolovChiptasiniBer();
        }
        kassaniTozala();
        setDisable(true);
        barCodeOff();
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
            if (balansDouble == 0) {
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
            savdo.setQaydnomaData(qaydnomaData);
            savdo.initHisobKitob(h);
            yetarliAdad = savdo.sot();
            if (yetarliAdad) {
                jami += h.getDona() * h.getNarh() / h.getKurs();
            } else {
                System.out.println("Tovar adadi yetarsiz");
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
                    "Chegirma: " + valuta.getValuta() +  "\n Xarid № " + qaydnomaData.getHujjat().toString().trim(),
                    user.getId(),
                    null
            );
        }
        return hisobKitob;
    }

    private HisobKitob naqdTolovniQaydEt() {
        HisobKitob hisobKitob = null;
        if (naqdDouble > 0) {
            Hisob pulHisobi = GetDbData.getHisob(kassa.getPulHisobi());
            double kurs = getKurs(valuta.getId(), new Date()).getKurs();
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
                    "Naqd: " + valuta.getValuta() +  "\n Xarid № " + qaydnomaData.getHujjat().toString().trim(),
                    user.getId(),
                    null
            );
        }
        return hisobKitob;
    }

    private HisobKitob plasticToloviniQaydEt() {
        HisobKitob hisobKitob = null;
        if (plasticDouble > 0) {
            Integer bankHisobiInteger = hisobKitobModels.yordamchiHisob(connection, hisob1.getId(), "Bank1"," Bank");
            double kurs = getKurs(valuta.getId(), new Date()).getKurs();
            hisobKitob = new HisobKitob(
                    null,
                    qaydnomaData.getId(),
                    qaydnomaData.getHujjat(),
                    15,
                    hisob2.getId(),
                    bankHisobiInteger,
                    valuta.getId(),
                    0,
                    kurs,
                    "",
                    .0,
                    plasticDouble,
                    0,
                    "Plastik: " + valuta.getValuta() +  "\n Xarid № " + qaydnomaData.getHujjat().toString().trim(),
                    user.getId(),
                    null
            );
        }
        return hisobKitob;
    }

    private HisobKitob bankXizmatiniQaydEt() {
        HisobKitob hisobKitob = null;
        if (plasticDouble > 0) {
            Integer bankHisobiInteger = hisobKitobModels.yordamchiHisob(connection, hisob1.getId(), "Bank1"," Bank");
            Integer bankXizmatiHisobiInteger = hisobKitobModels.yordamchiHisob(connection, bankHisobiInteger, "BankXizmati1", "BankXizmati");
            double kurs = getKurs(valuta.getId(), new Date()).getKurs();
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
                    "Bank xizmati : " + valuta.getValuta() +  "\n Xarid № " + qaydnomaData.getHujjat().toString().trim(),
                    user.getId(),
                    null
            );
        }
        return hisobKitob;
    }

    private HisobKitob qaytimniQaydEt() {
        HisobKitob hisobKitob = null;
        if (qaytimDouble > 0) {
            Hisob pulHisobi = GetDbData.getHisob(kassa.getPulHisobi());
            double kurs = getKurs(valuta.getId(), new Date()).getKurs();
            hisobKitob = new HisobKitob(
                    null,
                    qaydnomaData.getId(),
                    qaydnomaData.getHujjat(),
                    8,
                    pulHisobi.getId(),
                    hisob2.getId(),
                    valuta.getId(),
                    0,
                    kurs,
                    "",
                    .0,
                    qaytimDouble,
                    0,
                    "Qaytim: " + valuta.getValuta() +  "\n Xarid № " + qaydnomaData.getHujjat().toString().trim(),
                    user.getId(),
                    null
            );
        }
        return hisobKitob;
    }

    private void tolovChiptasiniBer() {
        StringBuffer printStringBuffer = new StringBuffer();
        String lineB = String.format("%.50s\n", "--------------------------------");
        SimpleDateFormat sana = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat vaqt = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        String sanaString = sana.format(date);
        String vaqtString = vaqt.format(date);
        printStringBuffer.append(lineB);
        printStringBuffer.append(String.format("%23s\n", "BEST PERFUMERY"));
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

        if (naqdDouble > 0) {
            String line = String.format("%-15s %5s %10s\n", "Naqd", " ", decimalFormat.format(naqdDouble));
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
        printerService.printString("POS-58", chipta);

    }

    private void tolovChiptasiniBer1() {
        StringBuffer printStringBuffer = new StringBuffer();
        String lineB = String.format("%.50s\n", "------------------------------------------------");
        SimpleDateFormat sana = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat vaqt = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        String sanaString = sana.format(date);
        String vaqtString = vaqt.format(date);
        printStringBuffer.append(lineB);
        printStringBuffer.append(String.format("%23s\n", "BEST PERFUMERY"));
        printStringBuffer.append(lineB);
        printStringBuffer.append(String.format("%-15s %16s\n", "Telefon", user.getPhone()));
        printStringBuffer.append(String.format("%-11s %20s\n", "Telegram", "t.me/best_perfumery"));
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

        if (naqdDouble > 0) {
            String line = String.format("%-15s %5s %10s\n", "Naqd", " ", decimalFormat.format(naqdDouble));
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
        printerService.printString("Canon MF3010", chipta);

    }

    private void kassaniTozala() {
        tableViewObservableList.removeAll(tableViewObservableList);
        tovarTableView.refresh();
        jamiMablag = 0.0;
        kassagaDouble = 0.0;
        chegirmaDouble = 0.0;
        naqdDouble = 0.0;
        plasticDouble = 0.0;
        balansDouble = 0.0;
        qaytimDouble = 0.0;
        vaznDouble = .0;
        chegirmaTextField.setText("");
        naqdTextField.setText("");
        plasticTextField.setText("");
        jamiHisob(tableViewObservableList);
        kassa = getKassaData(connection, getSerialNumber());
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
        }
        return hisob;
    }

    private Standart addTovar() {
        Standart tovar = null;
        TovarController1 tovarController = new TovarController1(connection, user);
        tovarController.display();
        if (tovarController.getDoubleClick()) {
            tovar = tovarController.getDoubleClickedRow();
        }
        return tovar;
    }

    private Valuta addValuta() {
        Valuta valuta = null;
        ValutaController valutaController = new ValutaController();
        valutaController.display(connection, user);
        if (valutaController.getDoubleClick()) {
            valuta = valutaController.getDoubleClickedRow();
        }
        return valuta;
    }

    public class TreeItemClass extends HBox{
        private Integer itemId;
        private Double aDouble;
        private Label label = new Label();
        private Label label2 = new Label();
        private Standart standart;
        private HBoxTextFieldPlusButton hisobHBox;
        private Button button;
        private TextField textField;
        private ComboBox<Standart> comboBox;
        private TableView<HisobKitob> tableView;
        private Separator separator;
        private Method columnToFitMethod;
        private Valuta valuta;

        public TreeItemClass(Integer itemId, TextField textField) {
            super(5);
            this.itemId = itemId;
            this.textField = textField;
            textField.setPrefWidth(180);

            this.getChildren().addAll(textField);
            this.setAlignment(Pos.CENTER_LEFT);
        }

        public TreeItemClass(Integer itemId, TextField textField, Valuta valuta) {
            super(2);
            this.itemId = itemId;
            this.textField = textField;
            this.valuta = valuta;
            label.setText(valuta.getValuta());
            textField.setPrefWidth(180);
            label.setPrefWidth(80);
            this.getChildren().addAll(textField, label);
            this.setAlignment(Pos.CENTER_LEFT);
        }

        public TreeItemClass(Integer itemId, TableView<HisobKitob> tableView) {
//            super(5);
            this.itemId = itemId;
            this.tableView = tableView;
            HBox.setHgrow(tableView, Priority.ALWAYS);
            tableView.setPrefHeight(100);
            try {
                columnToFitMethod = TableViewSkin.class.getDeclaredMethod("resizeColumnToFitContent", TableColumn.class, int.class);
                columnToFitMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            this.getChildren().addAll(tableView);
            this.setAlignment(Pos.CENTER_LEFT);
        }

        public TreeItemClass(Integer itemId, ComboBox comboBox) {
            super(5);
            this.itemId = itemId;
            this.comboBox = comboBox;

            HBox.setHgrow(comboBox, Priority.ALWAYS);
            this.getChildren().addAll(comboBox);
            this.setAlignment(Pos.CENTER_LEFT);
        }

        public TreeItemClass(Integer itemId, Label label) {
            super();
            this.itemId = itemId;
            this.label = label;

            HBox.setHgrow(label, Priority.ALWAYS);
            this.getChildren().add(label);
            this.setAlignment(Pos.CENTER_LEFT);
        }

        public TreeItemClass(Integer itemId, Label label, Valuta valuta) {
            super(2);
            this.itemId = itemId;
            this.label = label;
            this.valuta = valuta;

            HBox.setHgrow(label, Priority.ALWAYS);
            label2.setText(valuta.getValuta());
            this.getChildren().addAll(label, label2);
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
            this.getChildren().add(label);
            this.setAlignment(Pos.CENTER_LEFT);
        }

        public TreeItemClass(Integer itemId, HBoxTextFieldPlusButton hisobHBox) {
            super();
            this.itemId = itemId;
            this.hisobHBox = hisobHBox;
            HBox.setHgrow(hisobHBox, Priority.ALWAYS);
            this.getChildren().add(hisobHBox);
            this.setAlignment(Pos.CENTER_LEFT);
        }

        public TreeItemClass(Integer itemId, Button button) {
            super();
            this.itemId = itemId;
            this.button = button;
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
        public TableView<HisobKitob> getTableView() {
            return tableView;
        }

        public void setTableView(TableView<HisobKitob> tableView) {
            this.tableView = tableView;
        }

        public Label getLabel() {
            return label;
        }

        public void setLabel(Label label) {
            this.label = label;
        }

        public Valuta getValuta() {
            return valuta;
        }

        public void setValuta(Valuta valuta) {
            this.valuta = valuta;
        }

        public Label getLabel2() {
            return label2;
        }

        public void setLabel2(Label label2) {
            this.label2 = label2;
        }
    }

    private void jamiHisob(ObservableList<HisobKitob> hisobKitobObservableList) {
        jamiMablag = jamiHkDouble(hisobKitobObservableList);
        jamiMablagLabel.setText(decimalFormat.format(jamiMablag));
        kassagaDouble = jamiMablag - chegirmaDouble;
        kassagaLabel.setText(decimalFormat.format(kassagaDouble));

        naqdDouble = jamiTnDouble(naqdList);
        qaytimDouble = jamiTnDouble(qaytimList);

        Double aDouble = (naqdDouble + plasticDouble) - (jamiMablag - chegirmaDouble);
        TextField textField = null;
        if (aDouble > 0) {
            qaytimDouble = 0d;
            for (TovarNarhi tn: qaytimList) {
                if (tn.getValuta() != 2) {
                    qaytimDouble += tn.getNarh() * kursDouble/tn.getKurs();
                } else {
                    textField = getTextFieldFromTree(qaytimTreeItem.getChildren(),tn.getId());
                }
            }
            if (aDouble - qaytimDouble !=0 ) {
                textField.setText(""+(aDouble - qaytimDouble));
                qaytimDouble = aDouble - qaytimDouble;
            }
            qaytimDouble = jamiTnDouble(qaytimList);
        }
        balansDouble = (naqdDouble + plasticDouble - qaytimDouble) - (jamiMablag - chegirmaDouble);
        balansLabel.setText(decimalFormat.format(balansDouble));
    }

    private void jamiHisob(ObservableList<HisobKitob> hisobKitobObservableList, String value, int valueId) {
        jamiMablag = jamiHkDouble(hisobKitobObservableList);
        jamiMablagLabel.setText(decimalFormat.format(jamiMablag));

        naqdDouble = jamiTnDouble(naqdList);
        qaytimDouble = jamiTnDouble(qaytimList);

        switch (valueId) {
            case 1: //chegirma
                chegirmaDouble = Double.valueOf(value);
                break;
            case 2: //naqd
                break;
            case 3: //plastic
                plasticDouble = Double.valueOf(value);
                break;
            case 4: //qaytim
                break;
        }
        kassagaDouble = jamiMablag - chegirmaDouble;
        kassagaLabel.setText(decimalFormat.format(kassagaDouble));
        Double aDouble = (naqdDouble + plasticDouble) - (jamiMablag - chegirmaDouble);
        if (aDouble > 0) {
            qaytimDouble = 0d;
            for (TovarNarhi tn: qaytimList) {
                if (tn.getValuta() == 2) {
                    tn.setNarh(aDouble * kursDouble/tn.getKurs());
                    TextField textField = getTextFieldFromTree(qaytimTreeItem.getChildren(),tn.getId());
                    textField.setText(decimalFormat.format(tn.getNarh()));
                }
                qaytimDouble += tn.getNarh();
            }
        }
        qaytimLabel.setText(decimalFormat.format(qaytimDouble));
        balansDouble = (naqdDouble + plasticDouble - qaytimDouble) - (jamiMablag - chegirmaDouble);
        balansLabel.setText(decimalFormat.format(balansDouble));
    }

    private TextField getTextFieldFromTree(ObservableList<TreeItem<TreeItemClass>> list, Integer id) {
        TextField textField = null;
        for (TreeItem<TreeItemClass> treeItem: list) {
            if (treeItem.getValue().getValuta().getId().equals(id)) {
                textField = treeItem.getValue().getTextField();
                break;
            }
        }

        return textField;
    }

    private TextField getTextFieldFromStatusTree(ObservableList<TreeItem<TreeItemClass>> list, Integer id) {
        TextField textField = null;
        for (TreeItem<TreeItemClass> treeItem: list) {
            if (treeItem.getValue().getValuta().getStatus().equals(id)) {
                textField = treeItem.getValue().getTextField();
                break;
            }
        }

        return textField;
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
    }

    private void addTovar(BarCode barCode) {
        Integer narhTuri = narhComboBox.getValue().getId();
        Standart tovar = GetDbData.getTovar(barCode.getTovar());
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

        double pochkadaDona = tovarDonasi(barCode);
        double narhDouble = 0d;
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
                    if (valuta.getStatus() == 1) {
                        narhDouble = pochkadaDona * s6.getChakana() * (1 + nds);
                    } else {
                        narhDouble = yahlitla(pochkadaDona * (s6.getChakana() * hisobKitob.getKurs() * (1 + nds)), 1);
                    }
                } else if (narhTuri == 2) {
                    if (valuta.getStatus() == 1) {
                        narhDouble = pochkadaDona * s6.getUlgurji() * (1 + nds);
                    } else {
                        narhDouble = yahlitla(pochkadaDona * (s6.getUlgurji() * hisobKitob.getKurs() * (1 + nds)), 1);
                    }
                }
                hisobKitob.setNarh(narhDouble);
                double zaxiradagiAdad = hisobKitobModels.getBarCodeCount(connection, kassa.getTovarHisobi(), barCode.getBarCode());
                double adad = 1.0;
                if (zaxiradagiAdad >= adad) {
                    addTovarToTable(barCode, hisobKitob);
                } else {
                    Alerts.showKamomat(tovar, adad, barCode.getBarCode(), zaxiradagiAdad);
                }
            }
            tovarTableView.refresh();
            tovarHBox.getTextField().setText("");
        } else  {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Diqqat qiling!!");
            alert.setHeaderText(tovar.getText() + " uchun narh tayinlanmagan. Shu sabab ro`yhatga qo`shilmaydi");
            alert.setContentText(tovar.getText() + " uchun narhni sistema noziri tayin etadi");
            alert.showAndWait();
        }
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

    private void addTovarToTable(BarCode barCode, HisobKitob hisobKitob) {
        double adad = 1.0;
        HisobKitob hk1 = null;
        for (HisobKitob hk : tableViewObservableList) {
            if (hk.getBarCode() .equals(barCode.getBarCode())) {
                hk1 = hk;
                adad += hk.getDona();
                break;
            }
        }
        if (hk1 == null) {
            tableViewObservableList.add(hisobKitob);
        } else {
            hk1.setDona(adad);
            tovarTableView.refresh();
        }
        jamiHisob(tableViewObservableList);
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

    private void setFocusPropertyListener(TextField textField) {
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (!newValue) {
                    String string = textField.getText();
                    string = string.replaceAll(",", ".");
                    string = string.replaceAll(" ", "");
                    if (Alerts.isNumericAlert(string, false)) {
                        Double aDouble = Double.valueOf(string);
                        textField.setText(decimalFormat.format(aDouble));
                    } else {
                        textField.setText("0.0");
                    }
                }
            }
        });
    }

    private void initQaytimTextFields() {
        ObservableList<TreeItem<TreeItemClass>> list = qaytimTreeItem.getChildren();
        for (TreeItem<TreeItemClass> treeItem: list) {
            TextField textField = treeItem.getValue().getTextField();
            textField.setText("");
        }
        for (TovarNarhi tn: qaytimList) {
            tn.setNarh(0d);
        }
    }

    private Double jamiHkDouble(ObservableList<HisobKitob> observableList) {
        Double jami = 0d;
        for (HisobKitob hk : observableList) {
            jami += hk.getDona() * hk.getNarh();
        }
        return jami;
    }

    private Double jamiTnDouble(ObservableList<TovarNarhi> observableList) {
        Double jami = 0d;
        for (TovarNarhi tn : observableList) {
            jami += tn.getNarh() * kursDouble/tn.getKurs();
        }
        return jami;
    }

}
