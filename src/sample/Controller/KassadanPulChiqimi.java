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
import sample.Tools.Alerts;
import sample.Tools.MoneyShow;
import sample.Tools.PathToImageView;
import sample.Tools.SetHVGrow;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class KassadanPulChiqimi extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    GridPane gridPane = new GridPane();
    VBox centerPane = new VBox();
    Connection connection;
    User user = new User(1, "admin", "", "admin");
    int padding = 3;
    int amalTuri = 1;

    TextField hisob1TextField = new TextField();
    TextField hisob2TextField = new TextField();
    TextArea izohTextArea = new TextArea();
    TextField valutaTextField = new TextField();
    TextField valutaIdTextField = new TextField();
    TextField qaydVaqtiTextField = new TextField();

    HBox hisob1Hbox;
    HBox hisob2Hbox;

    Date date = new Date();
    DatePicker qaydSanasiDatePicker;
    HBox valutaHbox;
    DecimalFormat decimalFormat = new MoneyShow();

    TableView<HisobKitob> hisobKitobTableView = new TableView<>();
    Button xaridniYakunlaButton = new Button("Xaridni yakunla");

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

    HisobKitobModels hisobKitobModels = new HisobKitobModels();
    HisobModels hisobModels = new HisobModels();
    StandartModels standartModels = new StandartModels();
    BarCodeModels barCodeModels = new BarCodeModels();
    KursModels kursModels = new KursModels();
    QaydnomaModel qaydnomaModel = new QaydnomaModel();
    ValutaModels valutaModels = new ValutaModels();

    public static void main(String[] args) {
        launch(args);
    }

    public KassadanPulChiqimi() {
        connection = new MySqlDBLocal().getDbConnection();
    }

    public KassadanPulChiqimi(Connection connection, User user, Hisob hisob) {
        this.connection = connection;
        this.user = user;
        this.hisob1 = hisob;
    }

    private void ibtido() {
        initData();
        initHisob1Hbox();
        initHisob2Hbox();
        initQaydSanasiDatePicker();
        initQaydVaqtiTextField();
        initValutaHbox();
        initValutaIdTextField();
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

    private void initCenterPane() {
        SetHVGrow.VerticalHorizontal(centerPane);
        centerPane.setPadding(new Insets(padding));
        centerPane.getChildren().addAll(gridPane, hisobKitobTableView, xaridniYakunlaButton);
    }

    private void initHisobKitobTableView() {
        hisobKitobTableView.setDisable(true);
        SetHVGrow.VerticalHorizontal(hisobKitobTableView);
        hisobKitobTableView.getColumns().addAll(getValutaColumn(), getKursColumn(), getNarhColumn(), getSummaColumn(), getDeleteColumn());
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
//                        setText(GetDbData.getTovar(item).getText());
                        Text text = new Text(getStandart(item,tovarObservableList, "Tovar").getText());
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
                BarCode barCode = barCodeModels.getBarCode(connection, hisobKitob.getBarCode());
                Standart birlikStandart = getStandart(barCode.getBirlik(), birlikObservableList, "Birlik");
                String birlikString = birlikStandart.getText();
                if (barCode.getTarkib() > 0) {
                    BarCode barCode1 = barCodeModels.getBarCode(connection, barCode.getTarkib());
                    Standart birlik1Standart = getStandart(barCode1.getBirlik(), birlikObservableList, "Birlik");
                    birlikString += "\nTarkibida " + barCode.getAdad() + " " + birlik1Standart.getText().toLowerCase() + " bor";
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
        hisob1TextField.setEditable(false);
        hisob1TextField.setText(hisob1.getText());
        hisob1TextField.setFont(font);
        hisob1TextField.setPromptText("Chiqim hisobi");
        HBox.setHgrow(hisob1Hbox, Priority.ALWAYS);
        HBox.setHgrow(hisob1Hbox, Priority.ALWAYS);
        Button addButton = new Button();
        addButton.setDisable(true);
        addButton.setMinHeight(37);
        addButton.setGraphic(new PathToImageView("/sample/images/Icons/add.png").getImageView());
        HBox.setHgrow(addButton, Priority.ALWAYS);
        HBox.setHgrow(hisob1TextField, Priority.ALWAYS);
        hisob1Hbox.getChildren().addAll(hisob1TextField, addButton);
    }

    private void initHisob2Hbox() {
        hisob2Hbox = new HBox();
        hisob2TextField.setFont(font);
        hisob2TextField.setPromptText("Kirim hisobi");
        HBox.setHgrow(hisob2Hbox, Priority.ALWAYS);
        Button addButton = new Button();
        addButton.setMinHeight(37);
        addButton.setGraphic(new PathToImageView("/sample/images/Icons/add.png").getImageView());
        HBox.setHgrow(addButton, Priority.ALWAYS);
        HBox.setHgrow(hisob2TextField, Priority.ALWAYS);
        TextFields.bindAutoCompletion(hisob2TextField, hisobObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Hisob> autoCompletionEvent) -> {
            hisob2 = autoCompletionEvent.getCompletion();
            qaydSanasiDatePicker.setDisable(false);
            qaydVaqtiTextField.setDisable(false);
            valutaHbox.setDisable(false);
            valutaIdTextField.setDisable(false);
            hisobKitobTableView.setDisable(false);
            izohTextArea.setDisable(false);
        });
        hisob2Hbox.getChildren().addAll(hisob2TextField, addButton);
        addButton.setOnAction(event -> {
            hisob2 = addHisob();
            if (hisob2 != null) {
                qaydSanasiDatePicker.setDisable(false);
                qaydVaqtiTextField.setDisable(false);
                valutaHbox.setDisable(false);
                valutaIdTextField.setDisable(false);
                hisobKitobTableView.setDisable(false);
                izohTextArea.setDisable(false);
                hisob2TextField.setText(hisob2.getText());
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

    private void initValutaHbox() {
        valutaHbox = new HBox();
        valutaHbox.setDisable(true);
        valutaTextField.setFont(font);
        valutaTextField.setPromptText("Valyuta nomi");
        HBox.setHgrow(valutaTextField, Priority.ALWAYS);
        HBox.setHgrow(valutaHbox, Priority.ALWAYS);
        Button addButton = new Button();
        addButton.setMinHeight(37);
        addButton.setGraphic(new PathToImageView("/sample/images/Icons/add.png").getImageView());
        HBox.setHgrow(valutaTextField, Priority.ALWAYS);

        TextFields.bindAutoCompletion(valutaTextField, valutaObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Valuta> autoCompletionEvent) -> {
            ObservableList<Standart> birlikList = FXCollections.observableArrayList();
            valuta = autoCompletionEvent.getCompletion();
            valutaTextField.setText(valuta.getValuta());
            valutaIdTextField.setText(valuta.getId().toString());
        });

        valutaHbox.getChildren().addAll(valutaTextField, addButton);
        addButton.setOnAction(event -> {
            ValutaController valutaController = new ValutaController();
            valutaController.display(connection, user);
            if (valutaController.getDoubleClick()) {
                valuta = valutaController.getDoubleClickedRow();
                valutaTextField.setText(valuta.getValuta());
                valutaIdTextField.setText(valuta.getId().toString());
            }
        });
    }

    private void initValutaIdTextField() {
        valutaIdTextField.setDisable(true);
        valutaIdTextField.setFont(font);
        valutaIdTextField.setPromptText("Valyuta kodi");
        HBox.setHgrow(valutaIdTextField, Priority.ALWAYS);
        valutaIdTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode()== KeyCode.ENTER) {
                    stringBuffer.delete(0, stringBuffer.length());
                    String string = valutaIdTextField.getText().trim();
                    if (!string.isEmpty()) {
                        valuta = getValuta(Integer.valueOf(string));
                        if (valuta != null) {
                            valutaIdTextField.setText("");
                            addPul(valuta.getId());
                        }
                    }
                }
            }
        });

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
            for (HisobKitob hk: hisobKitobObservableList) {
                if (hk.getNarh() == 0d) {
                    Alerts.AlertString("Pul miqdori kiritilmadi");
                    hisobKitobTableView.getSelectionModel().select(hk);
                    hisobKitobTableView.scrollTo(hk);
                    hisobKitobTableView.requestFocus();
                    return;
                }
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
        gridPane.add(qaydSanasiDatePicker, 0, rowIndex, 1, 1);
        GridPane.setHgrow(qaydSanasiDatePicker, Priority.ALWAYS);
        gridPane.add(qaydVaqtiTextField, 1, rowIndex, 1,1);
        GridPane.setHgrow(qaydVaqtiTextField, Priority.ALWAYS);

        rowIndex++;
        gridPane.add(izohTextArea, 0, rowIndex, 2, 1);
        GridPane.setHgrow(izohTextArea, Priority.ALWAYS);
        GridPane.setVgrow(izohTextArea, Priority.ALWAYS);

        rowIndex++;
        gridPane.add(valutaHbox, 0, rowIndex, 1, 1);
        gridPane.add(valutaIdTextField, 1, rowIndex, 1, 1);
        GridPane.setHgrow(valutaHbox, Priority.ALWAYS);
        GridPane.setHgrow(valutaIdTextField, Priority.ALWAYS);
    }

    private void initBorderPane() {
        borderpane.setCenter(centerPane);
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Tovar Xaridi");
        scene = new Scene(borderpane, 1000, 600);
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
        if (hk1 == null) {
            hisobKitobObservableList.add(hisobKitob);
        } else {
            hk1.setDona(adad);
        }
        hisobKitobTableView.getSelectionModel().select(hisobKitob);
        hisobKitobTableView.scrollTo(hisobKitob);
        hisobKitobTableView.refresh();
    }

    private void addPul(Integer valutaId) {
        Valuta valuta = getValuta(valutaId);
        HisobKitob hisobKitob = new HisobKitob(0, 0, 0, amalTuri, hisob1.getId(),
                hisob2.getId(), valuta.getId(), 0, 1.0, "", 1.0,
                0.00, 0, izohTextArea.getText().trim(), user.getId(), date
        );
        if (valuta.getStatus() == 1) {
            hisobKitob.setKurs(1.00);
        } else {
            hisobKitob.setKurs(getKurs(hisobKitob.getValuta(), hisobKitob.getDateTime()).getKurs());
        }
        hisobKitobObservableList.add(hisobKitob);
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

}
