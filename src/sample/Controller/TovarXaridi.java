package sample.Controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import sample.Config.MySqlDBLocal;
import sample.Data.*;
import sample.Model.*;
import sample.Tools.*;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TovarXaridi extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    GridPane gridPane = new GridPane();
    VBox centerPane = new VBox();
    Connection connection;
    User user = new User(1, "admin", "", "admin");
    int padding = 3;
    int amalTuri = 2;

    TextField hisob1TextField = new TextField();
    TextField hisob2TextField = new TextField();
    TextArea izohTextArea = new TextArea();
    TextField tovarTextField = new TextField();
    TextField barCodeTextField = new TextField();
    Label hisob1BalanceLabel = new Label("");
    Label hisob2BalanceLabel = new Label("");
    Double hisob1BalanceDouble = 0D;
    Double hisob2BalanceDouble = 0D;
    Label hisob1NewBalanceLabel = new Label("");
    Label hisob2NewBalanceLabel = new Label("");

    HBox hisob1Hbox;
    HBox hisob2Hbox;

    Date date = new Date();
    LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    DatePicker qaydSanasiDatePicker;
    TextField qaydVaqtiTextField = new TextField();
    HBox tovarHBox;
    HBox birlikHbox;
    DecimalFormat decimalFormat = new MoneyShow();

    ComboBox<Standart> birlikComboBox = new ComboBox<>();
    TableView<HisobKitob> hisobKitobTableView = new TableView<>();
    Button xaridniYakunlaButton = new Button("Xaridni yakunla");

    Hisob hisob1;
    Hisob hisob2;
    Standart tovar;
    Standart6 standart6;
    Valuta valuta;
    Kurs kurs;
    HisobKitob hisobKitob;
    QaydnomaData qaydnomaData = null;

    Font font = Font.font("Arial", FontWeight.BOLD,20);
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    StringBuffer stringBuffer = new StringBuffer();

    ObservableList<HisobKitob> hisobKitobObservableList = FXCollections.observableArrayList();
    ObservableList<Hisob> hisobObservableList;
    ObservableList<Standart> tovarObservableList;
    ObservableList<Standart> birlikObservableList;
    ObservableList<Valuta> valutaObservableList;

    ValutaModels valutaModels = new ValutaModels();
    HisobModels hisobModels = new HisobModels();
    HisobKitobModels hisobKitobModels = new HisobKitobModels();
    StandartModels standartModels = new StandartModels();
    BarCodeModels barCodeModels = new BarCodeModels();
    KursModels kursModels = new KursModels();
    QaydnomaModel qaydnomaModel = new QaydnomaModel();

    public static void main(String[] args) {
        launch(args);
    }

    public TovarXaridi() {
        connection = new MySqlDBLocal().getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
    }

    public TovarXaridi(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
    }

    private void ibtido() {
        initData();
        initHisob1Hbox();
        initHisob2Hbox();
        initQaydSanasiDatePicker();
        initQaydVaqtiTextField();
        initTovarHbox();
        initBirlikComboBox();
        initBarCodeTextField();
        initHisobKitobTableView();
        initYakunlaButton();
        initIzohTextArea();
        initGridPane();
        initCenterPane();
        initBorderPane();
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
        standartModels.setTABLENAME("Tovar");
        tovarObservableList = standartModels.get_data(connection);
        standartModels.setTABLENAME("Birlik");
        birlikObservableList = standartModels.get_data(connection);
        valutaObservableList = valutaModels.get_data(connection);
        valuta = getValuta(1);
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

    private void initCenterPane() {
        HBox balanceHBox = new HBox();
        Pane pane = new Pane();
        HBox.setHgrow(pane, Priority.ALWAYS);
        hisob1NewBalanceLabel.setFont(font);
        hisob2NewBalanceLabel.setFont(font);
        balanceHBox.getChildren().addAll(hisob1NewBalanceLabel, pane, hisob2NewBalanceLabel);
        SetHVGrow.VerticalHorizontal(centerPane);
        centerPane.setPadding(new Insets(padding));
        centerPane.getChildren().addAll(gridPane, barCodeTextField, hisobKitobTableView, balanceHBox, xaridniYakunlaButton);
    }

    private void initHisobKitobTableView() {
        hisobKitobTableView.setDisable(true);
        SetHVGrow.VerticalHorizontal(hisobKitobTableView);
        hisobKitobTableView.getColumns().addAll(
                getIzohColumn(), getTaqdimColumn(), getValutaColumn(), getKursColumn(), getAdadColumn(),
                getXaridNarhiColumn(), getUlgurjiNarhColumn(), getChakanaNarhColumn(), getNarhColumn(),
                getSummaColumn(), getDeleteColumn());
        hisobKitobTableView.setItems(hisobKitobObservableList);
        hisobKitobTableView.setEditable(true);
        hisobKitobTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                hisobKitob = newValue;
                standart6 = narhOl(hisobKitob.getTovar());
            }
        });
    }

    public TableColumn<HisobKitob, String> getIzohColumn() {
        TableColumn<HisobKitob, String> izohColumn = new TableColumn<>("Tovar");
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

    private TableColumn<HisobKitob, Integer> getTovarColumn() {
        TableColumn<HisobKitob, Integer>  tovarColumn = new TableColumn<>("Tovar");
        tovarColumn.setMinWidth(200);
        tovarColumn.setMaxWidth(200);
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
                        Text text = new Text(getStandart(item, tovarObservableList, "Tovar").getText());
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
        adad.setMinWidth(100);
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
            Double newValue = event.getNewValue();
            if (newValue != null) {
                hisobKitob.setDona(newValue);
                hisobKitobTableView.refresh();
                balanceRefresh();
            }
        });
        adad.setStyle( "-fx-alignment: CENTER;");
        return adad;
    }

    private  TableColumn<HisobKitob, Double> getXaridNarhiColumn() {
        TableColumn<HisobKitob, Double>  xaridColumn = new TableColumn<>("Tannarh");
        xaridColumn.setMinWidth(100);
        xaridColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, Double>, ObservableValue<Double>>() {

            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<HisobKitob, Double> param) {
                HisobKitob hk = param.getValue();
                BarCode bc = GetDbData.getBarCode(hk.getBarCode());
                Double xaridNarhiDouble = 0.0;
                Standart6 xaridNarhi = narhOl(hk.getTovar());
                if (xaridNarhi != null) {
                    xaridNarhiDouble = xaridNarhi.getNarh();
                }
                else {
                    TovarNarhi tn = yakkaTovarNarhi(hk.getTovar(), 0);
                    if (tn != null) {
                        xaridNarhiDouble = tn.getNarh();
                    }
                }
                hk.setNarh(tovarDonasi(bc)*xaridNarhiDouble*hk.getKurs());
                return new SimpleObjectProperty<Double>(xaridNarhiDouble);
            }
        });
        xaridColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
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
        xaridColumn.setOnEditCommit(event -> {
            HisobKitob hk = event.getRowValue();
            Double newValue = event.getNewValue();
            if (newValue != null) {
                BarCode bc = GetDbData.getBarCode(hk.getBarCode());
                Double tovarDonasiDouble = tovarDonasi(bc);
                Double yangiNarhDouble = newValue / tovarDonasi(bc);

                hk.setNarh(tovarDonasiDouble*newValue);
                narhYoz(hk.getTovar(), 0, yangiNarhDouble/hk.getKurs());
            }
            event.getTableView().getSelectionModel().select(hk);
            event.getTableView().scrollTo(hk);
            event.getTableView().requestFocus();
            event.getTableView().refresh();
            balanceRefresh();
        });
        xaridColumn.setStyle( "-fx-alignment: CENTER;");
        return xaridColumn;
    }

    private  TableColumn<HisobKitob, Double> getChakanaNarhColumn() {
        TableColumn<HisobKitob, Double>  chakanaColumn = new TableColumn<>("Штучно");
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
            balanceRefresh();
        });
        chakanaColumn.setStyle( "-fx-alignment: CENTER;");
        return chakanaColumn;
    }

    private  TableColumn<HisobKitob, Double> getUlgurjiNarhColumn() {
        TableColumn<HisobKitob, Double>  ulgurjiColumn = new TableColumn<>("Оптом");
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
            balanceRefresh();
        });
        ulgurjiColumn.setStyle( "-fx-alignment: CENTER;");
        return ulgurjiColumn;
    }

    private TableColumn<HisobKitob, Double> getNarhColumn() {
        TableColumn<HisobKitob, Double>  narh = new TableColumn<>("Narh");
        narh.setMinWidth(100);
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
            event.getTableView().refresh();
            balanceRefresh();
            hisobKitob.setNarh(newValue);
        });
        return narh;
    }

    private TableColumn<HisobKitob, Valuta> getValutaColumn() {
        TableColumn<HisobKitob, Valuta> valutaTableColumn = new TableColumn<>("Valuta");
        valutaTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, Valuta>, ObservableValue<Valuta>>() {

            @Override
            public ObservableValue<Valuta> call(TableColumn.CellDataFeatures<HisobKitob, Valuta> param) {
                HisobKitob newHisobKitob = param.getValue();
                Valuta v = getValuta(newHisobKitob.getValuta()) ;
                Integer valutaCode = newHisobKitob.getValuta();
                return new SimpleObjectProperty<Valuta>(v);
            }
        });

        valutaTableColumn.setCellFactory(ComboBoxTableCell.forTableColumn(valutaObservableList));
        valutaTableColumn.setOnEditCommit((TableColumn.CellEditEvent<HisobKitob, Valuta> event) -> {
            Valuta newValuta = event.getNewValue();
            Double narhDouble = 0d;
            hisobKitob.setValuta(newValuta.getId());
            if (newValuta.getStatus() == 1) {
                hisobKitob.setKurs(1.0);
            } else {
                ObservableList<Kurs> kurslar = kursModels.getDate(connection, newValuta.getId(), date, "sana desc");
                if (kurslar.size() > 0) {
                    Kurs k = kurslar.get(0);
                    hisobKitob.setKurs(k.getKurs());
                    Standart6 xaridNarhi = narhOl(hisobKitob.getTovar());
                    if (xaridNarhi != null) {
                        narhDouble = xaridNarhi.getNarh()*hisobKitob.getKurs();
                    }
                    else {
                        TovarNarhi tn = yakkaTovarNarhi(hisobKitob.getTovar(), 1);
                        if (tn != null) {
                            narhDouble = tn.getNarh()*hisobKitob.getKurs();
                        }
                    }
                    hisobKitob.setNarh(narhDouble);
                }
            }
            event.getTableView().refresh();
            balanceRefresh();
        });
        valutaTableColumn.setMinWidth(100);
        valutaTableColumn.setStyle( "-fx-alignment: CENTER;");
        return valutaTableColumn;
    }

    private TableColumn<HisobKitob, Double> getKursColumn() {
        TableColumn<HisobKitob, Double>  kursColumn = new TableColumn<>("Kurs");
        kursColumn.setMinWidth(100);
        kursColumn.setCellValueFactory(new PropertyValueFactory<>("kurs"));
        kursColumn.setStyle( "-fx-alignment: CENTER;");
        kursColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
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
        kursColumn.setOnEditCommit((TableColumn.CellEditEvent<HisobKitob, Double> event) -> {
            Double newValue = event.getNewValue();
            if (newValue != null) {
                hisobKitob.setKurs(newValue);
                event.getTableView().refresh();
                balanceRefresh();
            }
        });
        return kursColumn;
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
                HBox.setHgrow(b, Priority.ALWAYS);
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
                    hisobKitobObservableList.remove(hisobKitob);
                    param.getTableView().refresh();
                    balanceRefresh();
                });
                return new SimpleObjectProperty<Button>(b);
            }
        });

        deleteColumn.setMinWidth(60);
        deleteColumn.setStyle( "-fx-alignment: CENTER;");
        return deleteColumn;
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
                    hisobKitobObservableList.remove(hisobKitob);
                });
                return new SimpleObjectProperty<Button>(b);
            }
        });

        editColumn.setMinWidth(60);
        editColumn.setStyle( "-fx-alignment: CENTER;");
        return editColumn;
    }

    private TableColumn<HisobKitob, Double> getTotalColumn() {
        TableColumn<HisobKitob, Double>  total = new TableColumn<>("Jami");
        total.setMinWidth(100);
        total.setCellValueFactory(new PropertyValueFactory<>("chiqim"));
        total.setStyle( "-fx-alignment: CENTER;");
        return total;
    }

    private TableColumn<HisobKitob, String> getSummaColumn() {
        TableColumn<HisobKitob, String>  summaCol = new TableColumn<>("Jami");
        summaCol.setMinWidth(100);
        summaCol.setStyle( "-fx-alignment: CENTER;");

        summaCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<HisobKitob, String> param) {
                HisobKitob hisobKitob = param.getValue();
                Double total = hisobKitob.getDona()*hisobKitob.getNarh()/hisobKitob.getKurs();
                hisobKitob.setSummaCol(total);
                balanceRefresh();
                return new SimpleObjectProperty<String>(decimalFormat.format(total));
            }
        });
        return summaCol;
    }

    private TableColumn<HisobKitob, String> getTaqdimColumn() {
        TableColumn<HisobKitob, String> taqdimColumn = new TableColumn<>("Taqdim shakli");
        taqdimColumn.setCellFactory(column -> {
            TableCell<HisobKitob, String> cell = new TableCell<HisobKitob, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        Text text = new Text(item);
                        text.setStyle("-fx-text-alignment:justify;");
                        text.wrappingWidthProperty().bind(getTableColumn().widthProperty().subtract(2));
                        setGraphic(text);
                    }
//                    setAlignment(Pos.CENTER_LEFT);
                }
            };
            return cell;
        });
        taqdimColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<HisobKitob, String> param) {
                HisobKitob hisobKitob = param.getValue();
                BarCode barCode = GetDbData.getBarCode(hisobKitob.getBarCode());
                BarCode barCode1 = null;
                Standart birlikStandart = getStandart(barCode.getBirlik(), birlikObservableList, "Birlik");
                String birlikString = birlikStandart.getText();
                if (barCode.getTarkib() > 0) {
                    barCode1 = GetDbData.getBarCode(barCode.getTarkib());
                    Standart birlik1Standart = getStandart(barCode1.getBirlik(), birlikObservableList, "Birlik");
                    birlikString += "\nTarkibi: " + barCode.getAdad() + " " + birlik1Standart.getText().toLowerCase();
                }
                return new SimpleObjectProperty<>(birlikString);
            }
        });
//        taqdimColumn.setStyle( "-fx-alignment: CENTER;");
        taqdimColumn.setMinWidth(100);
        return taqdimColumn;
    }

    private void initHisob1Hbox() {
        hisob1BalanceLabel.setFont(font);
        hisob1Hbox = new HBox();
        hisob1TextField.setFont(font);
        hisob1TextField.setPromptText("Chiqim hisobi");
        HBox.setHgrow(hisob1Hbox, Priority.ALWAYS);
        HBox.setHgrow(hisob1Hbox, Priority.ALWAYS);
        Button addButton = new Button();
        addButton.setMinHeight(37);
        addButton.setGraphic(new PathToImageView("/sample/images/Icons/add.png").getImageView());
        HBox.setHgrow(addButton, Priority.ALWAYS);
        HBox.setHgrow(hisob1TextField, Priority.ALWAYS);
        TextFields.bindAutoCompletion(hisob1TextField, hisobObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Hisob> autoCompletionEvent) -> {
            Hisob newHisob = autoCompletionEvent.getCompletion();
            if (newHisob != null) {
                hisob1 = newHisob;
                hisob1BalanceDouble = hisobKitobModels.getHisobBalance(connection, hisob1);
                balanceRefresh();
            }
            hisob2Hbox.setDisable(false);
        });
        hisob1Hbox.getChildren().addAll(hisob1TextField, addButton);
        addButton.setOnAction(event -> {
            Hisob newHisob = addHisob();
            if (newHisob != null) {
                hisob1 = newHisob;
                hisob1BalanceDouble = hisobKitobModels.getHisobBalance(connection, hisob1);
                hisob2Hbox.setDisable(false);
                hisob1TextField.setText(hisob1.getText());
            }
        });
    }

    private void initHisob2Hbox() {
        hisob2BalanceLabel.setFont(font);
        hisob2Hbox = new HBox();
        hisob2Hbox.setDisable(true);
        hisob2TextField.setFont(font);
        hisob2TextField.setPromptText("Kirim hisobi");
        HBox.setHgrow(hisob2Hbox, Priority.ALWAYS);
        Button addButton = new Button();
        addButton.setMinHeight(37);
        addButton.setGraphic(new PathToImageView("/sample/images/Icons/add.png").getImageView());
        HBox.setHgrow(addButton, Priority.ALWAYS);
        HBox.setHgrow(hisob2TextField, Priority.ALWAYS);
        TextFields.bindAutoCompletion(hisob2TextField, hisobObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Hisob> autoCompletionEvent) -> {
            Hisob newHisob = autoCompletionEvent.getCompletion();
            if (newHisob != null) {
                hisob2 = newHisob;
                if (hisob2 != null) {
                    hisob2BalanceDouble = hisobKitobModels.getHisobBalance(connection, hisob2);
                }
                qaydSanasiDatePicker.setDisable(false);
                qaydVaqtiTextField.setDisable(false);
                tovarHBox.setDisable(false);
                birlikComboBox.setDisable(false);
                barCodeTextField.setDisable(false);
                hisobKitobTableView.setDisable(false);
                izohTextArea.setDisable(false);
                barCodeOn();
                balanceRefresh();
            }
        });
        hisob2Hbox.getChildren().addAll(hisob2TextField, addButton);
        addButton.setOnAction(event -> {
            Hisob newHisob = addHisob();
            if (newHisob != null) {
                hisob2 = newHisob;
                if (hisob2 != null) {
                    hisob2BalanceDouble = hisobKitobModels.getHisobBalance(connection, hisob2);
                }
                qaydSanasiDatePicker.setDisable(false);
                qaydVaqtiTextField.setDisable(false);
                tovarHBox.setDisable(false);
                birlikComboBox.setDisable(false);
                barCodeTextField.setDisable(false);
                hisobKitobTableView.setDisable(false);
                izohTextArea.setDisable(false);
                hisob2TextField.setText(hisob2.getText());
                barCodeOn();
                balanceRefresh();
            }
        });
    }

    private void initQaydSanasiDatePicker() {
        // Converter
        StringConverter<LocalDate> converter = new StringConverter<LocalDate>() {
            final DateTimeFormatter dateFormatter =
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
        qaydSanasiDatePicker.setDisable(true);
    }

    private void initQaydVaqtiTextField()  {
        qaydVaqtiTextField.setDisable(true);
        qaydVaqtiTextField.setText(sdf.format(date));
        HBox.setHgrow(qaydVaqtiTextField, Priority.ALWAYS);
    }

    private void initIzohTextArea() {
        izohTextArea.setDisable(true);
        SetHVGrow.VerticalHorizontal(izohTextArea);
        izohTextArea.setWrapText(true);
        izohTextArea.setEditable(true);
    }

    private void initTovarHbox() {
        tovarHBox = new HBox();
        tovarHBox.setDisable(true);
        tovarTextField.setFont(font);
        tovarTextField.setPromptText("Tovar nomi");
        HBox.setHgrow(tovarTextField, Priority.ALWAYS);
        HBox.setHgrow(tovarHBox, Priority.ALWAYS);
        Button addButton = new Button();
        addButton.setMinHeight(37);
        addButton.setGraphic(new PathToImageView("/sample/images/Icons/add.png").getImageView());
        HBox.setHgrow(tovarTextField, Priority.ALWAYS);

        TextFields.bindAutoCompletion(tovarTextField, tovarObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Standart> autoCompletionEvent) -> {
            tovar = autoCompletionEvent.getCompletion();
            if (tovar != null) {
                barCodeAction();
            }
        });

        tovarHBox.getChildren().addAll(tovarTextField, addButton);
        addButton.setOnAction(event -> {
            barCodeOff();
            TovarController1 tovarController = new TovarController1(connection, user);
            tovar = tovarController.display();
            barCodeOn();
            if (tovar != null) {
                tovarTextField.setText(tovar.getText());
                barCodeAction();
            }
        });
    }

    private void barCodeAction() {
        ObservableList<Standart> birlikList = FXCollections.observableArrayList();
        ObservableList<BarCode> barCodeList = barCodeModels.getAnyData(connection, "tovar = " + tovar.getId(), "");
        for (BarCode bc: barCodeList) {
            Standart b = getStandart(bc.getBirlik(), birlikObservableList, "Birlik");
            if (b != null) {
                birlikList.add(b);
            }
        }
        birlikComboBox.setItems(birlikList);
        if (birlikList.size()>0) {
            birlikComboBox.getSelectionModel().selectFirst();
            barCodeTextField.setText(barCodeList.get(0).getBarCode());
        }

        birlikComboBox.setOnAction(event -> {
            Standart birlik = birlikComboBox.getValue();
            int pos = birlikComboBox.getSelectionModel().getSelectedIndex();
            if (birlik != null) {
                barCodeTextField.setText(barCodeList.get(pos).getBarCode());
            }
        });
    }

    private void initBarCodeTextField() {
        barCodeTextField.setDisable(true);
        barCodeTextField.setFont(font);
        barCodeTextField.setPromptText("Shtrixkod");
        HBox.setHgrow(barCodeTextField, Priority.ALWAYS);
    }

    private void initBirlikComboBox() {
        birlikComboBox.setDisable(true);
        String style20 = "-fx-font: 20px Arial";
        birlikComboBox.setPromptText("Birlik");
        birlikComboBox.setStyle(style20);
        birlikHbox = new HBox();
        HBox.setHgrow(birlikHbox, Priority.ALWAYS);
        HBox.setHgrow(birlikComboBox, Priority.ALWAYS);
        birlikComboBox.setMaxWidth(2000);
        birlikComboBox.setPrefWidth(150);
        HBox.setHgrow(birlikComboBox, Priority.ALWAYS);
        birlikHbox.getChildren().add(birlikComboBox);
    }

    private void initYakunlaButton() {
        xaridniYakunlaButton.setMaxWidth(2000);
        xaridniYakunlaButton.setPrefWidth(150);
        HBox.setHgrow(xaridniYakunlaButton, Priority.ALWAYS);
        xaridniYakunlaButton.setFont(font);
        xaridniYakunlaButton.setOnAction(event -> {
            if (hisob1.getId().equals(hisob2.getId())) {
                Alerts.hisoblarBirXilAlert(hisob2.getText(), hisob1.getText());
                return;
            }
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
        for (HisobKitob hk: hisobKitobObservableList) {
            hk.setQaydId(qData.getId());
            hk.setHujjatId(qData.getHujjat());
            hk.setDateTime(qData.getSana());
        }
        hisobKitobModels.addBatch(connection, hisobKitobObservableList);
    }

    private void initGridPane() {
        HBox.setHgrow(gridPane, Priority.ALWAYS);
        int rowIndex = 0;

        gridPane.add(hisob1Hbox, 0, rowIndex, 1, 1);
        GridPane.setHgrow(hisob1Hbox, Priority.ALWAYS);
        gridPane.add(hisob2Hbox, 1, rowIndex, 1,1);
        GridPane.setHgrow(hisob2Hbox, Priority.ALWAYS);

        rowIndex++;
        gridPane.add(hisob1BalanceLabel, 0, rowIndex, 1, 1);
        gridPane.add(hisob2BalanceLabel, 1, rowIndex, 1, 1);
        GridPane.setHalignment(hisob2BalanceLabel, HPos.RIGHT);

        rowIndex++;
        gridPane.add(qaydSanasiDatePicker, 0, rowIndex, 1, 1);
        GridPane.setHgrow(qaydSanasiDatePicker, Priority.ALWAYS);
        gridPane.add(qaydVaqtiTextField, 1, rowIndex, 1,1);
        GridPane.setHgrow(qaydVaqtiTextField, Priority.ALWAYS);

        rowIndex++;
        gridPane.add(izohTextArea, 0, rowIndex, 2, 1);
        GridPane.setHgrow(izohTextArea, Priority.ALWAYS);
        GridPane.setVgrow(izohTextArea, Priority.ALWAYS);

        rowIndex++;
        gridPane.add(tovarHBox, 0, rowIndex, 1, 1);
        GridPane.setHgrow(tovarHBox, Priority.ALWAYS);
        gridPane.add(birlikHbox, 1, rowIndex, 1,1);
        GridPane.setHgrow(birlikHbox, Priority.ALWAYS);
    }

    private void initBorderPane() {
        borderpane.setCenter(centerPane);
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Tovar Xaridi");
        scene = new Scene(borderpane, 1200, 600);
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

    private void addTovar(BarCode barCode) {
        Standart tovar = getStandart(barCode.getTovar(), tovarObservableList, "Tovar");
        HisobKitob hisobKitob = new HisobKitob(0, 0, 0, amalTuri, hisob1.getId(),
                hisob2.getId(), valuta.getId(), tovar.getId(), 1.0, barCode.getBarCode(), 1.0,
                0.00, 0, tovar.getText(), user.getId(), date
        );
        Valuta valuta = getValuta(hisobKitob.getValuta());
        if (valuta.getStatus() == 1) {
            hisobKitob.setKurs(1.00);
        } else {
            hisobKitob.setKurs(getKurs(hisobKitob.getValuta(), hisobKitob.getDateTime()).getKurs());
        }
        standart6 = narhOl(tovar.getId());
        if (standart6 != null) {
            hisobKitob.setNarh(standart6.getNarh()*hisobKitob.getKurs());
        } else {
            TovarNarhi tn = null;
            if (tn != null) {
                tn = yakkaTovarNarhi(hisobKitob.getTovar(), 3);
                hisobKitob.setNarh(tn.getNarh());
            } else {
                tn = yakkaTovarNarhi(hisobKitob.getTovar(), 1);
                if (tn != null) {
                    hisobKitob.setNarh(tn.getNarh() * hisobKitob.getKurs());
                } else {
                    hisobKitob.setNarh(0d);
                }
            }
        };
        double adad = 1.0;
        HisobKitob hk1 = null;
        for (HisobKitob hk : hisobKitobObservableList) {
            if (hk.getBarCode().equalsIgnoreCase(barCode.getBarCode())) {
                hk1 = hk;
                adad += hk.getDona();
                break;
            }
        }
        if (hk1 == null) {
            hisobKitobObservableList.add(hisobKitob);
        } else {
            hk1.setDona(adad);
        }
        hisobKitobTableView.getSelectionModel().select(hisobKitob);
        hisobKitobTableView.scrollTo(hisobKitob);
        hisobKitobTableView.refresh();
        balanceRefresh();
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
                    hisobKitobTableView.requestFocus();
                    String string = stringBuffer.toString().trim();
                    stringBuffer.delete(0, stringBuffer.length());
                    if (!string.isEmpty()) {
                        BarCode barCode = GetDbData.getBarCode(string);
                        if (barCode != null) {
                            Standart tovar = getStandart(barCode.getTovar(), tovarObservableList, "Tovar");
                            if (tovar != null) {
                                barCodeTextField.setText("");
                                tovarTextField.setText("");
                                birlikComboBox.setItems(null);
                                addTovar(barCode);
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
                            Standart tovar = getStandart(barCode.getTovar(), tovarObservableList, "Tovar");
                            if (tovar != null) {
                                barCodeTextField.setText("");
                                tovarTextField.setText("");
                                birlikComboBox.setItems(null);
                                addTovar(barCode);
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

    private Double getJami(ObservableList<HisobKitob> hisobKitobs) {
        Double jamiDouble = .0;
        for (HisobKitob hk: hisobKitobs) {
            jamiDouble += hk.getDona()*hk.getNarh()/hk.getKurs();
        }
        return jamiDouble;
    }

    public QaydnomaData getQaydnomaData() {
        return qaydnomaData;
    }

    public void setQaydnomaData(QaydnomaData qaydnomaData) {
        this.qaydnomaData = qaydnomaData;
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
    private Narh xaridNarhi(Integer tovarId) {
        NarhModels narhModels = new NarhModels();
        Narh xaridNarhi = null;
        ObservableList<Narh> list = narhModels.getDate(connection, tovarId, new Date(), "sana desc");
        if (list.size()>0) {
            xaridNarhi = list.get(0);
        }
        return xaridNarhi;
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

    private void balanceRefresh() {
        Double yangiDouble = 0d;
        hisob1BalanceLabel.setText(decimalFormat.format(hisob1BalanceDouble));
        hisob2BalanceLabel.setText(decimalFormat.format(hisob2BalanceDouble));


        for (HisobKitob hk: hisobKitobObservableList) {
            yangiDouble += hk.getNarh()*hk.getDona()/hk.getKurs();
        }
        hisob1NewBalanceLabel.setText(decimalFormat.format(hisob1BalanceDouble - yangiDouble));
        hisob2NewBalanceLabel.setText(decimalFormat.format(hisob2BalanceDouble + yangiDouble));
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
}
