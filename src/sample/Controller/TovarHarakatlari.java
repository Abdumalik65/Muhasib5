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
import sample.Config.MySqlDBGeneral;
import sample.Data.*;
import sample.Enums.ServerType;
import sample.Model.*;
import sample.Tools.*;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TovarHarakatlari extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    GridPane gridPane = new GridPane();
    VBox centerPane = new VBox();
    Connection connection;
    User user = new User(1, "admin", "", "admin");
    int padding = 3;
    int amalTuri = 3;

    TextField hisob1TextField = new TextField();
    TextField hisob2TextField = new TextField();
    TextArea izohTextArea = new TextArea();
    TextField tovarTextField = new TextField();
    TextField barCodeTextField = new TextField();
    Label hisob1BalanceLabel = new Label("");
    Label hisob2BalanceLabel = new Label("");
    Label hisob1NewBalanceLabel = new Label("");
    Label hisob2NewBalanceLabel = new Label("");

    HBox hisob1Hbox;
    HBox hisob2Hbox;

    Date date = new Date();
    LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    DatePicker qaydSanasiDatePicker;
    TextField qaydVaqtiTextField = new TextField();
    HBox tovarHBox = new HBox();
    HBox birlikHbox;
    DecimalFormat decimalFormat = new MoneyShow();

    ComboBox<Standart> birlikComboBox = new ComboBox<>();
    TableView<HisobKitob> hisobKitobTableView = new TableView<>();
    Button xaridniYakunlaButton = new Button("Amalni yakunla");

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
    ObservableList<Standart> tovarObservableList = FXCollections.observableArrayList();
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

    public TovarHarakatlari() {
        connection = new MySqlDBGeneral(ServerType.REMOTE).getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
    }

    public TovarHarakatlari(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        String classSimpleName = getClass().getSimpleName();
        DasturlarRoyxati.dastur(connection, user, classSimpleName);
    }

    private void ibtido() {
        initData();
        initHisob1Hbox();
        initHisob2Hbox();
        initQaydSanasiDatePicker();
        initQaydVaqtiTextField();
        initTovarHbox(true);
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
        standartModels.setTABLENAME("Birlik");
        birlikObservableList = standartModels.get_data(connection);
        valutaObservableList = valutaModels.get_data(connection);
        valuta = getValuta(1);
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
        hisobKitobTableView.getColumns().addAll(getIzohColumn(), getTaqdimColumn(), getAdadColumn(), getDeleteColumn());
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
            if (event.getNewValue() != null) {
                HisobKitob hisobKitob = event.getRowValue();
                Standart tovar = GetDbData.getTovar(hisobKitob.getTovar());
                double barCodeCount = hisobKitobModels.getBarCodeCount(connection, hisobKitob.getHisob1(), hisobKitob.getBarCode());
                if (barCodeCount >= event.getNewValue()) {
                    hisobKitob.setDona(event.getNewValue());
                } else {
                    hisobKitob.setDona(event.getOldValue());
                    Alerts.showKamomat(tovar, event.getNewValue(), hisobKitob.getBarCode(), barCodeCount);
                }
                hisobKitobTableView.refresh();
            }
        });
        adad.setStyle( "-fx-alignment: CENTER;");
        return adad;
    }

    private TableColumn<HisobKitob, Double> getNarhColumn() {
        TableColumn<HisobKitob, Double>  narh = new TableColumn<>("Narh");
        narh.setMinWidth(150);
        narh.setMaxWidth(250);
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
            TablePosition<HisobKitob, Double> pos = event.getTablePosition();

            Double newValue = event.getNewValue();

            int row = pos.getRow();
            HisobKitob hisobKitob = event.getTableView().getItems().get(row);
            event.getTableView().refresh();

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

        deleteColumn.setMinWidth(20);
        deleteColumn.setMaxWidth(40);
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
                hisobKitob.setSummaCol(hisobKitob.getDona()+hisobKitob.getNarh()/hisobKitob.getKurs());
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
        addButton.setMinHeight(37);
        addButton.setGraphic(new PathToImageView("/sample/images/Icons/add.png").getImageView());
        HBox.setHgrow(addButton, Priority.ALWAYS);
        HBox.setHgrow(hisob1TextField, Priority.ALWAYS);
        TextFields.bindAutoCompletion(hisob1TextField, hisobObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Hisob> autoCompletionEvent) -> {
            Hisob newHisob = autoCompletionEvent.getCompletion();
            if (newHisob != null) {
                hisob1 = newHisob;
                tovarObservableList = hisobKitobModels.getTovarCount(connection, hisob1.getId(), new Date());
                initTovarHbox(true);
                balanceRefresh();
            }
            hisob2Hbox.setDisable(false);
        });
        hisob1Hbox.getChildren().addAll(hisob1TextField, addButton);
        addButton.setOnAction(event -> {
            Hisob newHisob = addHisob();
            if (newHisob != null) {
                hisob1 = newHisob;
                tovarObservableList = hisobKitobModels.getTovarCount(connection, hisob1.getId(), new Date());
                initTovarHbox(true);
                hisob1TextField.setText(hisob1.getText());
            }
        });
    }

    private void initHisob2Hbox() {
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
                qaydSanasiDatePicker.setDisable(false);
                qaydVaqtiTextField.setDisable(false);
                birlikComboBox.setDisable(false);
                barCodeTextField.setDisable(false);
                hisobKitobTableView.setDisable(false);
                izohTextArea.setDisable(false);
                tovarHBox.setDisable(false);
                barCodeOn();
                balanceRefresh();
            }
        });
        hisob2Hbox.getChildren().addAll(hisob2TextField, addButton);
        addButton.setOnAction(event -> {
            Hisob newHisob = addHisob();
            if (newHisob != null) {
                hisob2 = newHisob;
                qaydSanasiDatePicker.setDisable(false);
                qaydVaqtiTextField.setDisable(false);
                birlikComboBox.setDisable(false);
                barCodeTextField.setDisable(false);
                hisobKitobTableView.setDisable(false);
                izohTextArea.setDisable(false);
                hisob2TextField.setText(hisob2.getText());
                tovarHBox.setDisable(false);
                barCodeOn();
                balanceRefresh();
            }
        });
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

    private void initTovarHbox(Boolean disable) {
        if (tovarHBox.getChildren().size()>0) {
            tovarHBox.getChildren().removeAll(tovarHBox.getChildren());
        }
        tovarHBox.setDisable(disable);
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
            TovarController tovarController = new TovarController(connection, user);
            tovar = tovarController.display();
            if (tovar != null) {
                tovarTextField.setText(tovar.getText());
                barCodeAction();
            }
        });
    }

    private void barCodeAction() {
        ObservableList<Standart> birlikList = FXCollections.observableArrayList();
        ObservableList<BarCode> barCodeList = hisobKitobModels.getBarCodeCount(connection, hisob1.getId(), tovar.getId(), new Date());
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
            hk.setDateTime(qData.getSana());
            hk.setHisob1(qData.getChiqimId());
            hk.setHisob2(qData.getKirimId());
            hk.setIzoh(qData.getIzoh());
            qoldiqList = hisobKitobModels.getBarCodeQoldiq(connection,hk.getHisob1(), barCode, qData.getSana());
            for (HisobKitob q: qoldiqList) {
                HisobKitob clonedHK = cloneHisobKitob(q);
                clonedHK.setQaydId(qData.getId());
                clonedHK.setHujjatId(qData.getHujjat());
                clonedHK.setAmal(amalTuri);
                clonedHK.setHisob1(hk.getHisob1());
                clonedHK.setHisob2(hk.getHisob2());
                clonedHK.setManba(q.getId());
                clonedHK.setDateTime(qData.getSana());
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
        hisob1BalanceLabel.setFont(font);
        hisob2BalanceLabel.setFont(font);
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
        stage.setTitle("Tovar harakatlari");
        scene = new Scene(borderpane, 500, 600);
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
        double adad = 1.0;
        HisobKitob hk1 = null;
        for (HisobKitob hk : hisobKitobObservableList) {
            if (hk.getBarCode().equalsIgnoreCase(barCode.getBarCode())) {
                hk1 = hk;
                adad += hk.getDona();
                break;
            }
        }
        double barCodeCount = hisobKitobModels.getBarCodeCount(connection, hisobKitob.getHisob1(), hisobKitob.getBarCode());
        if (barCodeCount >= adad) {
            if (hk1 == null) {
                hisobKitobObservableList.add(hisobKitob);
            }
            hisobKitob.setDona(adad);
            hisobKitobTableView.getSelectionModel().select(hisobKitob);
            hisobKitobTableView.scrollTo(hisobKitob);
            hisobKitobTableView.refresh();
            balanceRefresh();
        } else {
            Alerts.showKamomat(tovar, adad, hisobKitob.getBarCode(), barCodeCount);
        }
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
    private void balanceRefresh() {
        Double hisob1Double = hisobKitobModels.getHisobBalance(connection, hisob1);
        Double hisob2Double = 0d;
        if (hisob2 != null) {
            hisob2Double = hisobKitobModels.getHisobBalance(connection, hisob2);
        }
        Double yangiDouble = 0d;
        hisob1BalanceLabel.setText(decimalFormat.format(hisob1Double));
        hisob2BalanceLabel.setText(decimalFormat.format(hisob2Double));

        for (HisobKitob hk: hisobKitobObservableList) {
            yangiDouble += hk.getDona()*hk.getNarh()/hk.getKurs();
        }
        hisob1NewBalanceLabel.setText(decimalFormat.format(hisob1Double - yangiDouble));
        hisob2NewBalanceLabel.setText(decimalFormat.format(hisob2Double + yangiDouble));
    }
}
