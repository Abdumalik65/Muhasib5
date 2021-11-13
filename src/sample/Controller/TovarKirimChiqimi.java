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
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TovarKirimChiqimi extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    GridPane gridPane = new GridPane();
    VBox centerPane = new VBox();
    Connection connection;
    User user = new User(1, "admin", "", "admin");
    int padding = 3;
    int amalTuri = 3;
    String style20 = "-fx-font: 20px Arial; -fx-font-weight: bold;";

    TextField hisob1TextField = new TextField();
    TextField hisob2TextField = new TextField();
    TextArea izohTextArea = new TextArea();
    TextField tovarTextField = new TextField();
    TextField barCodeTextField = new TextField();

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
    Button xaridniYakunlaButton = new Button("Amalni yakunla");
    Button xaridniBekorQilButton = new Button("Amalni bekor qil");

    Hisob hisob1;
    Hisob hisob2;
    Standart tovar;
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
    Kassa kassa;
    Boolean kirim = false;

    public static void main(String[] args) {
        launch(args);
    }

    public TovarKirimChiqimi() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        GetDbData.initData(connection);
        String serialNumber = Sotuvchi3.getSerialNumber();
        kassa = Sotuvchi3.getKassaData(connection, serialNumber);
        if (kirim) {
            hisob1 = GetDbData.getHisob(kassa.getXaridorHisobi());
            hisob2 = GetDbData.getHisob(kassa.getTovarHisobi());
            hisob1TextField.setEditable(true);
            hisob2TextField.setEditable(false);
        } else {
            hisob1 = GetDbData.getHisob(kassa.getTovarHisobi());
            hisob2 = GetDbData.getHisob(kassa.getXaridorHisobi());
            hisob1TextField.setEditable(false);
            hisob2TextField.setEditable(true);
        }
        hisob1TextField.setText(hisob1.getText());
        hisob2TextField.setText(hisob2.getText());
    }

    public TovarKirimChiqimi(Connection connection, User user, Boolean kirim, Kassa kassa) {
        this.connection = connection;
        this.user = user;
        this.kirim = kirim;
        this.kassa = kassa;
        if (kirim) {
            hisob1 = GetDbData.getHisob(kassa.getXaridorHisobi());
            hisob2 = GetDbData.getHisob(kassa.getTovarHisobi());
            hisob1TextField.setEditable(true);
            hisob2TextField.setEditable(false);
        } else {
            hisob1 = GetDbData.getHisob(kassa.getTovarHisobi());
            hisob2 = GetDbData.getHisob(kassa.getXaridorHisobi());
            hisob1TextField.setEditable(false);
            hisob2TextField.setEditable(true);
        }
        hisob1TextField.setText(hisob1.getText());
        hisob2TextField.setText(hisob2.getText());
    }

    public TovarKirimChiqimi(Connection connection, User user) {
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
        GetDbData.initData(connection);
        ibtido();
        initStage(primaryStage);
        stage.show();
        stage.setOnCloseRequest(event -> {
            barCodeOff();
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

    private void initCenterPane() {
        SetHVGrow.VerticalHorizontal(centerPane);
        centerPane.setPadding(new Insets(padding));
        centerPane.getChildren().addAll(gridPane, barCodeTextField, hisobKitobTableView, xaridniYakunlaButton);
    }

    private void initHisobKitobTableView() {
        SetHVGrow.VerticalHorizontal(hisobKitobTableView);
        hisobKitobTableView.getColumns().addAll(getIzohColumn(), getTaqdimColumn(), getValutaColumn(), getKursColumn(), getAdadColumn(), getXaridNarhiColumn(), getUlgurjiNarhColumn(), getChakanaNarhColumn(), getNarhColumn(), getSummaColumn(), getDeleteColumn());
//        hisobKitobTableView.getColumns().addAll(getIzohColumn(), getTaqdimColumn(), getAdadColumn(), getDeleteColumn());
        hisobKitobTableView.setItems(hisobKitobObservableList);
        hisobKitobTableView.setEditable(true);
        hisobKitobTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                hisobKitob = newValue;
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
        adad.setMinWidth(80);
        adad.setMaxWidth(150);
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
                Double xaridNarhiDouble = 0.0;
                Narh xaridNarhi = xaridNarhi(hk.getTovar());
                if (xaridNarhi != null) {
                    xaridNarhiDouble = xaridNarhi.getXaridDouble();
                    BarCode bc = GetDbData.getBarCode(hk.getBarCode());
                    hk.setNarh(tovarDonasi(bc)*xaridNarhi.getXaridDouble());
                }
                return new SimpleObjectProperty<Double>(xaridNarhiDouble);
            }
        });
        xaridColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setMinimumIntegerDigits (1);
                numberFormat.setMaximumIntegerDigits (8);

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
                hk.setNarh(tovarDonasi(bc)*newValue);
                NarhModels narhModels = new NarhModels();
                Narh yangiNarh = new Narh(null, hk.getTovar(), new Date(), newValue, user.getId(), null);
                narhModels.insert_data(connection, yangiNarh);
            }
            event.getTableView().getSelectionModel().select(hk);
            event.getTableView().scrollTo(hk);
            event.getTableView().requestFocus();
            event.getTableView().refresh();
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
                TovarNarhi narh = getTovarNarhi(hk.getTovar(), 1);

                if (narh != null) {
                    narhDouble = narh.getNarh();

                }
                return new SimpleObjectProperty<Double>(narhDouble);
            }
        });
        chakanaColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setMinimumIntegerDigits (1);
                numberFormat.setMaximumIntegerDigits (8);

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
                TovarNarhiModels tovarNarhiModels = new TovarNarhiModels();
                Date date = new Date();
                TovarNarhi yangiNarh = new TovarNarhi(null, date, hk.getTovar(), 1, 1, 1.0, newValue, user.getId(), null);
                tovarNarhiModels.insert_data(connection, yangiNarh);
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
        TableColumn<HisobKitob, Double>  ulgurjiColumn = new TableColumn<>("Оптом");
        ulgurjiColumn.setMinWidth(100);
        ulgurjiColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, Double>, ObservableValue<Double>>() {

            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<HisobKitob, Double> param) {
                HisobKitob hk = param.getValue();
                Double narhDouble = 0.0;
                TovarNarhi narh = getTovarNarhi(hk.getTovar(), 2);

                if (narh != null) {
                    narhDouble = narh.getNarh();

                }
                return new SimpleObjectProperty<Double>(narhDouble);
            }
        });
        ulgurjiColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setMinimumIntegerDigits (1);
                numberFormat.setMaximumIntegerDigits (8);

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
                TovarNarhiModels tovarNarhiModels = new TovarNarhiModels();
                Date date = new Date();
                TovarNarhi yangiNarh = new TovarNarhi(null, date, hk.getTovar(), 2, 1, 1.0, newValue, user.getId(), null);
                tovarNarhiModels.insert_data(connection, yangiNarh);
            }
            event.getTableView().getSelectionModel().select(hk);
            event.getTableView().scrollTo(hk);
            event.getTableView().requestFocus();
            event.getTableView().refresh();
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
                numberFormat.setMaximumIntegerDigits (8);

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
            TablePosition<HisobKitob, Double> pos = event.getTablePosition();

            Double newValue = event.getNewValue();
            HisobKitob hisobKitob = event.getRowValue();
            event.getTableView().refresh();

            hisobKitob.setNarh(newValue);
        });
        return narh;
    }


/*
    private TableColumn<HisobKitob, Double> getNarhColumn() {
        TableColumn<HisobKitob, Double>  narh = new TableColumn<>("Narh");
        narh.setMinWidth(150);
        narh.setMaxWidth(250);
        narh.setCellValueFactory(new PropertyValueFactory<>("narh"));
        narh.setStyle( "-fx-alignment: CENTER;");
        narh.setCellFactory(TextFieldButtonTableCell.forTableColumn(new StringConverter<Double>() {
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
            TablePosition<HisobKitob, Double> pos = event.getTablePosition();

            Double newValue = event.getNewValue();

            int row = pos.getRow();
            HisobKitob hisobKitob = event.getTableView().getItems().get(row);
            event.getTableView().refresh();

            hisobKitob.setNarh(newValue);
        });
        return narh;
    }
*/

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
            hisobKitob.setValuta(newValuta.getId());
            if (newValuta.getStatus() == 1) {
                hisobKitob.setKurs(1.0);
            } else {
                ObservableList<Kurs> kurslar = kursModels.getDate(connection, newValuta.getId(), date, "sana desc");
                if (kurslar.size() > 0) {
                    Kurs k = kurslar.get(0);
                    hisobKitob.setKurs(k.getKurs());
                }
            }
            event.getTableView().refresh();
        });
        valutaTableColumn.setMinWidth(120);
        valutaTableColumn.setStyle( "-fx-alignment: CENTER;");
        return valutaTableColumn;
    }

    private TableColumn<HisobKitob, Double> getKursColumn() {
        TableColumn<HisobKitob, Double>  kursColumn = new TableColumn<>("Kurs");
        kursColumn.setMinWidth(150);
        kursColumn.setMaxWidth(250);
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
                });
                return new SimpleObjectProperty<Button>(b);
            }
        });

        deleteColumn.setMinWidth(80);
        deleteColumn.setMaxWidth(80);
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
        summaCol.setMaxWidth(300);
        summaCol.setStyle( "-fx-alignment: CENTER;");

        summaCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<HisobKitob, String> param) {
                HisobKitob hisobKitob = param.getValue();
                Double total = hisobKitob.getDona()*hisobKitob.getNarh();
                return new SimpleObjectProperty<String>(decimalFormat.format(total));
            }
        });
        return summaCol;
    }

    private TableColumn<HisobKitob, String> getTaqdimColumn() {
        TableColumn<HisobKitob, String> taqdimColumn = new TableColumn<>("Taqdim shakli");
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
        taqdimColumn.setStyle( "-fx-alignment: CENTER;");
        taqdimColumn.setMinWidth(120);
        return taqdimColumn;
    }

    private void initHisob1Hbox() {
        hisob1Hbox = new HBox();
        hisob1TextField.setFont(font);
        hisob1TextField.setPromptText("Chiqim hisobi");
        HBox.setHgrow(hisob1Hbox, Priority.ALWAYS);
        HBox.setHgrow(hisob1Hbox, Priority.ALWAYS);
        Button addButton = new Button();
        addButton.setDisable(kirim ? false: true);
        hisob1TextField.setEditable(kirim ? true: false);
        addButton.setMinHeight(37);
        addButton.setGraphic(new PathToImageView("/sample/images/Icons/add.png").getImageView());
        HBox.setHgrow(addButton, Priority.ALWAYS);
        HBox.setHgrow(hisob1TextField, Priority.ALWAYS);
        if (kirim) {
            TextFields.bindAutoCompletion(hisob1TextField, hisobObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Hisob> autoCompletionEvent) -> {
                Hisob newValue = autoCompletionEvent.getCompletion();
                if (newValue != null) {
                    hisob1 = newValue;
                }
            });
            addButton.setOnAction(event -> {
                Hisob newValue = addHisob();
                if (newValue != null) {
                    hisob1 = newValue;
                    hisob1TextField.setText(hisob1.getText());
                }
            });
        }
        hisob1Hbox.getChildren().addAll(hisob1TextField, addButton);
    }

    private void initHisob2Hbox() {
        hisob2Hbox = new HBox();
        hisob2TextField.setText(hisob2.getText());
        hisob2TextField.setFont(font);
        hisob2TextField.setPromptText("Kirim hisobi");
        HBox.setHgrow(hisob2Hbox, Priority.ALWAYS);
        Button addButton = new Button();
        addButton.setDisable(kirim ? true: false);
        hisob2TextField.setEditable(kirim ? false: true);
        addButton.setMinHeight(37);
        addButton.setGraphic(new PathToImageView("/sample/images/Icons/add.png").getImageView());
        HBox.setHgrow(addButton, Priority.ALWAYS);
        HBox.setHgrow(hisob2TextField, Priority.ALWAYS);
        if (!kirim) {
            TextFields.bindAutoCompletion(hisob2TextField, hisobObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Hisob> autoCompletionEvent) -> {
                hisob2 = autoCompletionEvent.getCompletion();
                disableNodes();
            });
            addButton.setOnAction(event -> {
                hisob2 = addHisob();
                if (hisob2 != null) {
                    hisob2TextField.setText(hisob2.getText());
                    disableNodes();
                }
            });
        }
        hisob2Hbox.getChildren().addAll(hisob2TextField, addButton);
    }

    private void disableNodes() {
        qaydSanasiDatePicker.setDisable(false);
        qaydVaqtiTextField.setDisable(false);
        tovarHBox.setDisable(false);
        birlikComboBox.setDisable(false);
        barCodeTextField.setDisable(false);
        hisobKitobTableView.setDisable(false);
        izohTextArea.setDisable(false);
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
        qaydSanasiDatePicker.setStyle(style20);
    }

    private void initQaydVaqtiTextField()  {
        qaydVaqtiTextField.setText(sdf.format(date));
        qaydVaqtiTextField.setFont(font);
        HBox.setHgrow(qaydVaqtiTextField, Priority.ALWAYS);
    }

    private void initIzohTextArea() {
        SetHVGrow.VerticalHorizontal(izohTextArea);
        izohTextArea.setWrapText(true);
        izohTextArea.setEditable(true);
    }

    private void initTovarHbox() {
        tovarHBox = new HBox();
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
            TovarController1 tovarController = new TovarController1(connection, user);
            tovar = tovarController.display();
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
            HisobKitob balance = hisobKitobModels.getBarCodeBalans(connection, hisob1.getId(), bc, new Date());
            Standart b = getStandart(bc.getBirlik(), birlikObservableList, "Birlik");
            if (b != null) {
                birlikList.add(b);
            }
        }
        birlikComboBox.setItems(birlikList);
        if (birlikList.size()>0) {
            birlikComboBox.getSelectionModel().selectFirst();
            barCodeTextField.setText(barCodeList.get(0).getBarCode());
        };

        birlikComboBox.setOnAction(event -> {
            Standart birlik = birlikComboBox.getValue();
            int pos = birlikComboBox.getSelectionModel().getSelectedIndex();
            if (birlik != null) {
                barCodeTextField.setText(barCodeList.get(pos).getBarCode());
            }
        });
    }

    private void initBarCodeTextField() {
        barCodeTextField.setFont(font);
        barCodeTextField.setPromptText("Shtrixkod");
        HBox.setHgrow(barCodeTextField, Priority.ALWAYS);
    }

    private void initBirlikComboBox() {
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
        ObservableList<HisobKitob> qoldiqList = null;
        String talabString = "";
        String mavjudString = "";
        String kamomatString = "";
        String birlikString = "";
        String alertString = "";
        for (HisobKitob hk: hisobKitobObservableList) {
            Double talabDouble = hk.getDona();
            BarCode barCode = GetDbData.getBarCode(hk.getBarCode());
            birlikString = " " + GetDbData.getBirlikFromBarCodeString(barCode).trim().toLowerCase() + "\n";
            talabString = "Talab: " + talabDouble.toString().trim() + birlikString;
            hk.setQaydId(qData.getId());
            hk.setHujjatId(qData.getHujjat());
            hk.setAmal(amalTuri);
            qoldiqList = hisobKitobModels.getBarCodeQoldiq(connection,hk.getHisob1(), barCode, qData.getSana());
            for (HisobKitob q: qoldiqList) {
                HisobKitob clonedHK = cloneHisobKitob(q);
                clonedHK.setQaydId(qData.getId());
                clonedHK.setHujjatId(qData.getHujjat());
                clonedHK.setAmal(amalTuri);
                clonedHK.setHisob1(hk.getHisob1());
                clonedHK.setHisob2(hk.getHisob2());
                clonedHK.setManba(q.getId());
                if (q.getDona()>=talabDouble) {
                    clonedHK.setDona(talabDouble);
                    talabDouble = 0.0;
                    hisobKitobModels.insert_data(connection, clonedHK);
                    break;
                } else {
                    clonedHK.setDona(q.getDona());
                    hisobKitobModels.insert_data(connection, clonedHK);
                    talabDouble -= q.getDona();
                }
            }
            if (talabDouble>0) {
                kamomatString = "Kamomat: " + talabDouble.toString().trim() + birlikString;
                mavjudString = "O`tkazildi: " + String.valueOf(hk.getDona()-talabDouble).trim() + birlikString;
                String birlik = GetDbData.getBirlikFromBarCodeString(barCode);
                alertString += hk.getIzoh() + "\n" + talabString + mavjudString + kamomatString;
            }
        }
        if (!alertString.isEmpty()) {
            qData.setIzoh(alertString);
            qaydnomaModel.update_data(connection, qData);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Diqqat !!!");
            alert.setHeaderText(alertString);
            alert.setContentText("Ogoh bo`ling!!!");
            alert.showAndWait();
        }
    }

    public HisobKitob cloneHisobKitob(HisobKitob hk) {
        HisobKitob clonedHisobKitob = new HisobKitob(
                hk.getId(),
                hk.getQaydId(),
                hk.getHujjatId(),
                hk.getAmal(),
                hk.getHisob1(),
                hk.getHisob2(),
                hk.getValuta(),
                hk.getTovar(),
                hk.getKurs(),
                hk.getBarCode(),
                hk.getDona(),
                hk.getNarh(),
                hk.getManba(),
                hk.getIzoh(),
                hk.getUserId(),
                hk.getDateTime()
        );
        return clonedHisobKitob;
    }

    private void initGridPane() {
        HBox.setHgrow(gridPane, Priority.ALWAYS);
        int rowIndex = 0;

        gridPane.add(hisob1Hbox, 0, rowIndex, 1, 1);
        GridPane.setHgrow(hisob1Hbox, Priority.ALWAYS);
        gridPane.add(hisob2Hbox, 1, rowIndex, 1,1);
        GridPane.setHgrow(hisob2Hbox, Priority.ALWAYS);

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
        stage.setTitle("Tovar harakatlari");
        scene = new Scene(borderpane);
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
//        scene.getStylesheets().add("/sample/Styles/caspian.css");
        stage.setResizable(false);
        stage.setScene(scene);
        barCodeOn();
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

    private TovarNarhi getTovarNarhi(Integer tovarId, Integer narhTuri) {
        TovarNarhiModels tovarNarhiModels = new TovarNarhiModels();
        TovarNarhi narh = null;
        ObservableList<TovarNarhi> list = tovarNarhiModels.getDate3(connection, tovarId, narhTuri, new Date(), "sana desc");
        if (list.size()>0) {
            narh = list.get(0);
        }
        return narh;
    }
}
