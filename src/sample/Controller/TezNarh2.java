package sample.Controller;

import javafx.application.Application;
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
import sample.Config.MySqlDBLocal;
import sample.Data.*;
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

public class TezNarh2 extends Application {
    Stage stage;
    Scene scene;
    GridPane gridPane = new GridPane();
    BorderPane borderpane = new BorderPane();
    VBox centerPane = new VBox();
    HBox bottom = new HBox();
    HBoxTextFieldPlusButton hisob1HBox = new HBoxTextFieldPlusButton();
    HBoxTextFieldPlusButton hisob2HBox = new HBoxTextFieldPlusButton();
    HBoxTextFieldPlusButton tovarHBox;
    HBox birlikHbox;
    ComboBox<Standart> birlikComboBox = new ComboBox<>();

    TableView<HisobKitob> dataTableView = new TableView<>();
    Connection connection;
    MySqlDBLocal mySqlDBLocal = new MySqlDBLocal();

    Double narhDouble = 0.0;
    Double chakanaDouble = 0.0;
    Double ulgurjiDouble = 0.0;
    Double hajmDouble = 0.0;
    Double vaznDouble = 0.0;
    Double ndsDouble = 0.0;
    Double adadDouble = 0.0;

    int padding = 3;
    Integer amalTuri = 3;
    Integer narhTuriId = 1;

    TextField tovarNomiTextField = new TextField();
    TextField barCodeTextField = new TextField();
    TextField qaydVaqtiTextField = new TextField();
    TextArea izohTextArea = new TextArea();

    Button bazagaYozButton = new Tugmachalar().getAdd();
    Button qaydEtButton = new Button("Qayd et");

    StringBuffer stringBuffer = new StringBuffer();
    DecimalFormat  decimalFormat = new MoneyShow();
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    TovarNarhiModels tovarNarhiModels = new TovarNarhiModels();

    HisobKitob hkCursor;
    BarCode bcCursor;
    TovarData tovarDataCursor;

    User user = new User(1, "admin", "", "admin");
    Hisob hisob1;
    Hisob hisob2;
    Standart tovar;

    Date date = new Date();
    LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    DatePicker qaydSanasiDatePicker;

    ObservableList<Standart> tovarObservableList;
    ObservableList<Standart4> ndsObservableList = FXCollections.observableArrayList();
    ObservableList<Hisob> hisobObservableList;
    ObservableList<TovarNarhi> tovarNarhlari = FXCollections.observableArrayList();
    ObservableList<TovarSana> tovarSanalari = FXCollections.observableArrayList();
    ObservableList<BarCode> barCodes = FXCollections.observableArrayList();
    ObservableList<TovarData> tovarDataList = FXCollections.observableArrayList();
    ObservableList<HisobKitob> hkObservableList = FXCollections.observableArrayList();
    ObservableList<Standart> narhTuri;

    Font font = Font.font("Arial", FontWeight.BOLD,20);

    public static void main(String[] args) {
        launch(args);
    }

    public TezNarh2() {
//        ibtido();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ibtido();
        initStage(primaryStage);
        stage.show();
    }

    public void display() {
        stage = new Stage();
        ibtido();
        initStage(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    private void initData() {
        hisobObservableList = GetDbData.getHisobObservableList();
        tovarObservableList = GetDbData.getTovarObservableList();
        narhTuri = initNarhTuri();
    }

    private void ibtido() {
        initConnection();
        GetDbData.initData(connection);
        initData();
        initQaydSanasiDatePicker();
        initQaydVaqtiTextField();
        initIzohTextArea();
        initHisob1HBox();
        initHisob2HBox();
        initTovarHbox();
        initBirlikComboBox();
        initTableView();
        initQatdEtButton();
        initGridPane();
        initCenterPane();
        initBottomPane();
        initBorderPane();
        disableNodes(true);
    }

    private void initConnection() {
        connection = mySqlDBLocal.getDbConnection();
    }

    private void initGridPane() {
        HBox.setHgrow(gridPane, Priority.ALWAYS);
        int rowIndex = 0;

        gridPane.add(hisob1HBox, 0, rowIndex, 1, 1);
        GridPane.setHgrow(hisob1HBox, Priority.ALWAYS);
        gridPane.add(hisob2HBox, 1, rowIndex, 1,1);
        GridPane.setHgrow(hisob2HBox, Priority.ALWAYS);

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

        rowIndex++;
        gridPane.add(barCodeTextField, 0, rowIndex, 2, 1);
        GridPane.setHgrow(barCodeTextField, Priority.ALWAYS);

        rowIndex++;
        gridPane.add(dataTableView, 0, rowIndex, 2, 1);
        GridPane.setHgrow(dataTableView, Priority.ALWAYS);
        GridPane.setVgrow(dataTableView, Priority.ALWAYS);

        rowIndex++;
        gridPane.add(qaydEtButton, 0, rowIndex, 2, 1);
        GridPane.setHgrow(qaydEtButton, Priority.ALWAYS);
    }

    private void initHisob1HBox() {
        HBox.setHgrow(hisob1HBox, Priority.ALWAYS);
        TextField textField = hisob1HBox.getTextField();
        TextFields.bindAutoCompletion(textField, hisobObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Hisob> autoCompletionEvent) -> {
            Hisob newValue = autoCompletionEvent.getCompletion();
            if (newValue != null) {
                hisob1 = newValue;
                hisob2HBox.setDisable(false);
            }
        });

        Button addButton = hisob1HBox.getPlusButton();
        addButton.setOnAction(event -> {
            hisob1 = addHisob();
            if (hisob1 != null) {
                textField.setText(hisob1.getText());
                hisob2HBox.setDisable(false);
            }
        });
    }

    private void initHisob2HBox() {
        HBox.setHgrow(hisob2HBox, Priority.ALWAYS);
        TextField textField = hisob2HBox.getTextField();
        TextFields.bindAutoCompletion(textField, hisobObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Hisob> autoCompletionEvent) -> {
            Hisob newValue = autoCompletionEvent.getCompletion();
            if (newValue != null) {
                hisob2 = newValue;
                disableNodes(false);
            }
        });

        Button addButton = hisob2HBox.getPlusButton();
        addButton.setOnAction(event -> {
            hisob2 = addHisob();
            if (hisob2 != null) {
                textField.setText(hisob2.getText());
                disableNodes(false);
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
    }

    private void initQaydVaqtiTextField()  {
        qaydVaqtiTextField.setText(sdf.format(date));
        HBox.setHgrow(qaydVaqtiTextField, Priority.ALWAYS);
    }

    private void initIzohTextArea() {
        HBox.setHgrow(izohTextArea, Priority.ALWAYS);
        VBox.setVgrow(izohTextArea, Priority.ALWAYS);
    }

    private void initTovarHbox() {
        tovarHBox = new HBoxTextFieldPlusButton();
        TextField textField = tovarHBox.getTextField();
        textField.setFont(font);
        textField.setPromptText("Tovar nomi");
        HBox.setHgrow(textField, Priority.ALWAYS);
        HBox.setHgrow(tovarHBox, Priority.ALWAYS);
        Button addButton = tovarHBox.getPlusButton();
        addButton.setMinHeight(37);
        HBox.setHgrow(textField, Priority.ALWAYS);

        TextFields.bindAutoCompletion(textField, tovarObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Standart> autoCompletionEvent) -> {
            Standart newValue = autoCompletionEvent.getCompletion();
            if (newValue != null) {
                tovar = newValue;
                barCodeAction();
            }
        });

        addButton.setOnAction(event -> {
            barCodeOff();
            TovarController1 tovarController = new TovarController1(connection, user);
            Standart newValue = tovarController.display();
            barCodeOn();
            if (newValue != null) {
                tovar = newValue;
                textField.setText(tovar.getText());
                barCodeAction();
            }
        });
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

    private void initTableView() {
        HBox.setHgrow(dataTableView, Priority.ALWAYS);
        VBox.setVgrow(dataTableView, Priority.ALWAYS);
        dataTableView.setEditable(true);
        dataTableView.setItems(hkObservableList);
        dataTableView.getColumns().addAll(getTovarColumn(), getBarCodeColumn(), getBirlikColumn(), getAdadColumn(), getNarhColumn(), getChakanaNarhColumn(), getUlgurjiNarhColumn(), getVaznColumn(), getHajmColumn(), getNdsColumn(), getDeleteColumn());

        dataTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                hkCursor = newValue;
                if (!hkCursor.getBarCode().isEmpty()) {
                    bcCursor = GetDbData.getBarCode(hkCursor.getBarCode());
                }
                if (hkCursor.getTovar()>0) {
                    tovarDataCursor = getTovarData(hkCursor.getTovar());
                }
            }
        });
    }

    private TableColumn<HisobKitob, Integer> getTovarColumn() {
        TableColumn<HisobKitob, Integer> tovarColumn = new TableColumn<>("Tovar");
        tovarColumn.setCellValueFactory(new PropertyValueFactory<>("tovar"));
        tovarColumn.setMinWidth(200);
        tovarColumn.setMaxWidth(200);
        tovarColumn.setCellFactory(column -> {
            TableCell<HisobKitob, Integer> cell = new TableCell<HisobKitob, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        Text text = new Text(GetDbData.getTovar(item).getText());
                        text.setStyle("-fx-text-alignment:justify;");
                        text.wrappingWidthProperty().bind(getTableColumn().widthProperty().subtract(1));
                        setGraphic(text);
                    }
                    setAlignment(Pos.CENTER_LEFT);
                }
            };
            return cell;
        });
        return tovarColumn;
    }

    private TableColumn<HisobKitob, String> getBirlikColumn() {
        TableColumn<HisobKitob, String> birlikColumn = new TableColumn<>("Birlik");
        birlikColumn.setCellValueFactory(new PropertyValueFactory<>("barCode"));
        birlikColumn.setMinWidth(100);
        birlikColumn.setMaxWidth(100);
        birlikColumn.setCellFactory(column -> {
            TableCell<HisobKitob, String> cell = new TableCell<HisobKitob, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        BarCode bc = GetDbData.getBarCode(item);
                        Standart birlik = GetDbData.getBirlik(bc.getBirlik());
                        setText(birlik.getText());
                    }
                    setAlignment(Pos.CENTER_LEFT);
                }
            };
            return cell;
        });
        return birlikColumn;
    }

    private TableColumn<HisobKitob, String> getBarCodeColumn() {
        TableColumn<HisobKitob, String> barCodeColumn = new TableColumn<>("Shtrix kod");
        barCodeColumn.setCellValueFactory(new PropertyValueFactory<>("barCode"));
        barCodeColumn.setMinWidth(150);
        barCodeColumn.setMaxWidth(150);
        barCodeColumn.setStyle( "-fx-alignment: CENTER;");

        return barCodeColumn;
    }

    private TableColumn<HisobKitob, Double> getNarhColumn() {
        TableColumn<HisobKitob, Double> naNarhColumn = new TableColumn<>("Xarid narhi");
        naNarhColumn.setCellValueFactory(new PropertyValueFactory<>("narh"));
        naNarhColumn.setMinWidth(100);
        naNarhColumn.setMaxWidth(100);
        naNarhColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
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
        naNarhColumn.setOnEditCommit(event -> {
            hkCursor.setNarh(event.getNewValue());
            narhDouble = event.getNewValue();
            event.getTableView().refresh();
        });
        naNarhColumn.setStyle( "-fx-alignment: CENTER;");

        return naNarhColumn;
    }

    private TableColumn<HisobKitob, Double> getAdadColumn() {
        TableColumn<HisobKitob, Double> adadColumn = new TableColumn<>("Dona");
        adadColumn.setCellValueFactory(new PropertyValueFactory<>("dona"));
        adadColumn.setMinWidth(100);
        adadColumn.setMaxWidth(100);
        adadColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
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
        adadColumn.setOnEditCommit(event -> {
            hkCursor.setDona(event.getNewValue());
            adadDouble = event.getNewValue();
            event.getTableView().refresh();
        });
        adadColumn.setStyle( "-fx-alignment: CENTER;");

        return adadColumn;
    }

    private TableColumn<HisobKitob, Double> getChakanaNarhColumn() {
        TableColumn<HisobKitob, Double> chakanaNarhColumn = new TableColumn<>("Chakana narh");
        chakanaNarhColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, Double>, ObservableValue<Double>>() {

            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<HisobKitob, Double> param) {
                HisobKitob hisobKitob = param.getValue();
                TovarData tovarData = getTovarData(hisobKitob.getTovar());

                return new SimpleObjectProperty<Double>(tovarData.getNarh(1));
            }
        });
        chakanaNarhColumn.setMinWidth(100);
        chakanaNarhColumn.setMaxWidth(100);
        chakanaNarhColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
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
        chakanaNarhColumn.setOnEditCommit(event -> {
            tovarDataCursor.setNarh(1, event.getNewValue());
            chakanaDouble = event.getNewValue();
            event.getTableView().refresh();
        });
        chakanaNarhColumn.setStyle( "-fx-alignment: CENTER;");

        return chakanaNarhColumn;
    }

    private TableColumn<HisobKitob, Double> getUlgurjiNarhColumn() {
        TableColumn<HisobKitob, Double> ulgurjiNarhColumn = new TableColumn<>("Ulgurji narh");
        ulgurjiNarhColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, Double>, ObservableValue<Double>>() {

            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<HisobKitob, Double> param) {
                HisobKitob hisobKitob = param.getValue();
                TovarData tovarData = getTovarData(hisobKitob.getTovar());

                return new SimpleObjectProperty<Double>(tovarData.getNarh(2));
            }
        });
        ulgurjiNarhColumn.setMinWidth(100);
        ulgurjiNarhColumn.setMaxWidth(100);
        ulgurjiNarhColumn.setStyle( "-fx-alignment: CENTER;");

        ulgurjiNarhColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
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
        ulgurjiNarhColumn.setOnEditCommit(event -> {
            tovarDataCursor.setNarh(2, event.getNewValue());
            ulgurjiDouble = event.getNewValue();
        });
        ulgurjiNarhColumn.setStyle( "-fx-alignment: CENTER;");

        return ulgurjiNarhColumn;
    }

    private TableColumn<HisobKitob, Double> getNdsColumn() {
        TableColumn<HisobKitob, Double> ndsColumn = new TableColumn<>("Nds");
        ndsColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, Double>, ObservableValue<Double>>() {

            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<HisobKitob, Double> param) {
                HisobKitob hisobKitob = param.getValue();
                TovarData tovarData = getTovarData(hisobKitob.getTovar());

                return new SimpleObjectProperty<Double>(tovarData.getNdsDouble());
            }
        });
        ndsColumn.setMinWidth(50);
        ndsColumn.setMaxWidth(50);
        ndsColumn.setStyle( "-fx-alignment: CENTER;");

        ndsColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
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
        ndsColumn.setOnEditCommit(event -> {
            tovarDataCursor.setNdsDouble(event.getNewValue());
            ndsDouble = event.getNewValue();
        });
        ndsColumn.setStyle( "-fx-alignment: CENTER;");

        return ndsColumn;
    }

    private TableColumn<HisobKitob, Double> getVaznColumn() {
        TableColumn<HisobKitob, Double> vaznColumn = new TableColumn<>("Vazn");
        vaznColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, Double>, ObservableValue<Double>>() {

            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<HisobKitob, Double> param) {
                HisobKitob hisobKitob = param.getValue();
                BarCode bc = GetDbData.getBarCode(hisobKitob.getBarCode());

                return new SimpleObjectProperty<Double>(bc.getVazn());
            }
        });
        vaznColumn.setMinWidth(100);
        vaznColumn.setMaxWidth(100);
        vaznColumn.setStyle( "-fx-alignment: CENTER;");

        vaznColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
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
        vaznColumn.setOnEditCommit(event -> {
            bcCursor.setVazn(event.getNewValue());
            vaznDouble = event.getNewValue();
        });
        vaznColumn.setStyle( "-fx-alignment: CENTER;");

        return vaznColumn;
    }

    private TableColumn<HisobKitob, Double> getHajmColumn() {
        TableColumn<HisobKitob, Double> hajmColumn = new TableColumn<>("Hajm");
        hajmColumn.setCellValueFactory(new PropertyValueFactory<>("hajmDouble"));
        hajmColumn.setMinWidth(100);
        hajmColumn.setMaxWidth(100);
        hajmColumn.setStyle( "-fx-alignment: CENTER;");
        hajmColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, Double>, ObservableValue<Double>>() {

            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<HisobKitob, Double> param) {
                HisobKitob hisobKitob = param.getValue();
                BarCode bc = GetDbData.getBarCode(hisobKitob.getBarCode());

                return new SimpleObjectProperty<Double>(bc.getHajm());
            }
        });

        hajmColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
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
        hajmColumn.setOnEditCommit(event -> {
            bcCursor.setHajm(event.getNewValue());
            hajmDouble = event.getNewValue();
        });
        hajmColumn.setStyle( "-fx-alignment: CENTER;");
        return hajmColumn;
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
                    hkObservableList.remove(hisobKitob);
                    dataTableView.refresh();
                });
                return new SimpleObjectProperty<Button>(b);
            }
        });

        deleteColumn.setMinWidth(100);
        deleteColumn.setMaxWidth(100);
        deleteColumn.setStyle( "-fx-alignment: CENTER;");
        return deleteColumn;
    }

    private void initQatdEtButton() {
        qaydEtButton.setMaxWidth(2000);
        qaydEtButton.setPrefWidth(150);
        qaydEtButton.setMaxHeight(60);
        qaydEtButton.setPrefHeight(150);
        qaydEtButton.setFont(font);
        qaydEtButton.setOnAction(event -> {
            bazagaYoz();
        });
    }

    private void initBottomPane() {
        bottom.setAlignment(Pos.CENTER);
    }

    private void initCenterPane() {
        centerPane.setPadding(new Insets(padding));
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        VBox.setVgrow(centerPane, Priority.ALWAYS);
        centerPane.getChildren().add(gridPane);
    }

    private void initBorderPane() {
        borderpane.setCenter(centerPane);
        borderpane.setBottom(bottom);
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Narh kiritish");
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        stage.setX(bounds.getMinX() - 3);
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth() + 7);
        stage.setHeight(bounds.getHeight() + 6);
        scene = new Scene(borderpane);
        barCodeOn();
        stage.setOnCloseRequest(event -> {
            barCodeOff();
        });
        stage.setScene(scene);
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
                    if (string.equals(tovarNomiTextField.getText().trim())) {
                        tovarNomiTextField.setText("");
                        string = "";
                    }
                    if (!string.isEmpty()) {
                        BarCode barCode = GetDbData.getBarCode(string);
                        if (barCode != null) {
                            Standart tovar = GetDbData.getTovar(barCode.getTovar());
                            if (tovar != null) {
                                addBarCode(barCode);
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
                    String string = barCodeTextField.getText().trim();
                    barCodeTextField.setText("");
                    if (!string.isEmpty()) {
                        BarCode barCode = GetDbData.getBarCode(string);
                        if (barCode != null) {
                            Standart tovar = GetDbData.getTovar(barCode.getTovar());
                            if (tovar != null) {
                                addBarCode(barCode);
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

    private void bazagaYoz() {
        Date date = new Date();
        Double pochkadaDona = 1.0;
        QaydnomaData qaydnomaData = qaydnomaSaqlash();
        for (HisobKitob hk: hkObservableList) {
            pochkadaDona = tovarDonasi(hk.getBarCode());
            hk.setNarh(pochkadaDona * hk.getNarh());
            hk.setQaydId(qaydnomaData.getId());
            hk.setHujjatId(qaydnomaData.getHujjat());
            vaznHajmList();
        }
        sotishNarhlariList(date);
        saqla();
        hkObservableList.removeAll(hkObservableList);
        dataTableView.refresh();
    }

    private void vaznHajmList() {
        BarCodeModels barCodeModels = new BarCodeModels();
        for (BarCode bc: barCodes) {
            if (bc.getChanged()) {
                barCodeModels.update_data(connection, bc);
            }
        }
    }

    private void sotishNarhlariList(Date date) {
        StandartModels standartModels = new StandartModels();
        standartModels.setTABLENAME("NarhTuri");
        ObservableList<Standart> narhTurlari = standartModels.get_data(connection);
        for (TovarData td: tovarDataList) {
            if (td.getChanged()) {
                ndsList(td);
                TovarSana tovarSana = new TovarSana(null, date, td.getTovar(), user.getId(), date);
                tovarSanalari.add(tovarSana);
                if (narhTurlari.size() > 0) {
                    Standart narhTuri1 = narhTurlari.get(0);
                    tovarNarhlari.add(new TovarNarhi(null, date, td.getTovar(), narhTuri1.getId(), 1, 1.0, td.getNarh(1), user.getId(), date));
                    Standart narhTuri2 = narhTurlari.get(1);
                    tovarNarhlari.add(new TovarNarhi(null, date, td.getTovar(), narhTuri2.getId(), 1, 1.0, td.getNarh(2), user.getId(), date));
                }
            }
        }
    }

    private void ndsList(TovarData td) {
        if (td.getChanged()) {
            Standart4 ndsStandart4 = new Standart4(
                    null,
                    td.getTovar(),
                    new Date(),
                    td.getNdsDouble(),
                    user.getId(),
                    null
            );
            ndsObservableList.add(ndsStandart4);
        }
    }

    private void saqla() {
        TovarSanaModels tovarSanaModels = new TovarSanaModels();
        tovarSanaModels.addBatch(connection, tovarSanalari);
        TovarNarhiModels tovarNarhiModels = new TovarNarhiModels();
        tovarNarhiModels.addBath(connection, tovarNarhlari);
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        hisobKitobModels.addBatch(connection, hkObservableList);
        Standart4Models standart4Models = new Standart4Models();
        standart4Models.setTABLENAME("Nds");
        standart4Models.addBatch(connection, ndsObservableList);
    }

    private Double ndsOl(Integer tovarId) {
        Standart4 ndsStandart4 = null;
        Standart4Models standart4Models = new Standart4Models();
        standart4Models.setTABLENAME("Nds");
        ObservableList<Standart4> ndsObservableList = standart4Models.getDate(connection, tovarId, new Date(), "sana desc");
        if (ndsObservableList.size()>0) {
            ndsStandart4 = ndsObservableList.get(0);
            ndsDouble = ndsStandart4.getMiqdor();
        }
        return ndsDouble;
    }

    private double tovarDonasi(String barCodeString) {
        double dona = 1.0;
        double adadBarCode2 = 0;
        BarCode barCode = GetDbData.getBarCode(barCodeString);
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
                if (tarkibInt == 0) {
                    break;
                }
            }
        }
        return dona;
    }

    private  HisobKitob addBarCode(BarCode bc) {
        bcCursor = bc;
        barCodes.add(bc);
        hajmDouble = bc.getHajm();
        vaznDouble = bc.getVazn();

        TovarData tovarData = getTovarData(bc.getTovar());
        if (tovarData == null) {
            ObservableList<TovarNarhi> narhList = tovarNarhiOl(bc.getTovar());
            ndsOl(bc.getTovar());
            tovarData = new TovarData(bc.getTovar(), narhList, ndsDouble);
            tovarData.setChanged(true);
            tovarDataList.add(tovarData);
        }
        tovarDataCursor = tovarData;
        HisobKitob hisobKitob = barCodeTopildimi(bc.getBarCode());
        if (hisobKitob == null) {
            hisobKitob = new HisobKitob(
                    null,
                    null,
                    null,
                    amalTuri,
                    hisob1.getId(),
                    hisob2.getId(),
                    1,
                    bc.getTovar(),
                    1.0,
                    bc.getBarCode(),
                    1.0,
                    tovarDonasi(bc.getBarCode()),
                    0,
                    tovarDataCursor.getText(),
                    user.getId(),
                    null
            );
            hkObservableList.add(hisobKitob);
        } else {
            Double adadDouble = hisobKitob.getDona() + 1;
            hisobKitob.setDona(adadDouble);
        }
        hkCursor = hisobKitob;
        dataTableView.getSelectionModel().select(hisobKitob);
        dataTableView.scrollTo(hisobKitob);
        dataTableView.requestFocus();
        dataTableView.refresh();
        return hisobKitob;
    }

    private ObservableList<TovarNarhi> tovarNarhiOl(Integer tovarId) {
        TovarNarhi tovarNarhi = null;
        TovarNarhiModels tovarNarhiModels = new TovarNarhiModels();
        ObservableList<TovarNarhi> narhlar = FXCollections.observableArrayList();
        ObservableList<TovarNarhi> tovarNarhiObservableList = tovarNarhiModels.getDate2(
                connection,
                tovarId,
                new Date(),
                "dateTime desc"
        );
        for (Standart nt: narhTuri) {
            for (TovarNarhi tn: tovarNarhiObservableList) {
                if (nt.getId().equals(tn.getNarhTuri())) {
                    narhlar.add(tn);
                    if (tn.getNarhTuri() == 1) {
                        chakanaDouble = tn.getNarh();
                    }
                    if (tn.getNarhTuri() == 2) {
                        ulgurjiDouble = tn.getNarh();
                    }
                    break;
                }
            }
        }
        return narhlar;
    }

    private HisobKitob barCodeTopildimi(String string) {
        HisobKitob hisobKitob = null;
        for (HisobKitob hk: hkObservableList) {
            if (hk.getBarCode().trim().equalsIgnoreCase(string)) {
                hisobKitob = hk;
                break;
            }
        }
        return hisobKitob;
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

    private QaydnomaData qaydnomaSaqlash() {
        QaydnomaModel qaydnomaModel = new QaydnomaModel();
        int hujjatInt = getQaydnomaNumber(qaydnomaModel);
        String izohString = izohTextArea.getText();
        Double jamiDouble = getJami(hkObservableList);
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

    private int getQaydnomaNumber(QaydnomaModel qaydnomaModel) {
        int qaydnomaInt = 1;
        ObservableList<QaydnomaData> qaydList = qaydnomaModel.getAnyData(connection, "amalTuri = " + amalTuri, "hujjat desc");
        if (qaydList.size()>0) {
            qaydnomaInt = qaydList.get(0).getHujjat() + 1;
        }
        return qaydnomaInt;
    }

    private Double getJami(ObservableList<HisobKitob> hisobKitobs) {
        Double jamiDouble = .0;
        for (HisobKitob hk: hisobKitobs) {
            jamiDouble += hk.getDona()*hk.getNarh()/hk.getKurs();
        }
        return jamiDouble;
    }

    private void barCodeAction() {
        ObservableList<Standart> birlikList = FXCollections.observableArrayList();
        ObservableList<BarCode> barCodeList = GetDbData.getBarCodeList(tovar.getId());
        for (BarCode bc: barCodeList) {
            Standart b = GetDbData.getBirlik(bc.getBirlik());
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

    private void disableNodes(Boolean disable) {
        hisob2HBox.setDisable(disable);
        qaydSanasiDatePicker.setDisable(disable);
        qaydVaqtiTextField.setDisable(disable);
        izohTextArea.setDisable(disable);
        tovarHBox.setDisable(disable);
        birlikComboBox.setDisable(disable);
        barCodeTextField.setDisable(disable);
        dataTableView.setDisable(disable);
    }

    public class TovarData {
        Integer tovar = 0;
        String text;
        ObservableList<TovarNarhi> tovarNarhiList = FXCollections.observableArrayList();
        Double ndsDouble = .0;
        Boolean changed = false;

        public TovarData() {
        }

        public TovarData(Integer tovar, String text, ObservableList<TovarNarhi> tovarNarhiList, Double ndsDouble) {
            this.tovar = tovar;
            this.text = text;
            this.tovarNarhiList = tovarNarhiList;
            this.ndsDouble = ndsDouble;
        }

        public TovarData(Integer tovar, ObservableList<TovarNarhi> tovarNarhiList, Double ndsDouble) {
            this.tovar = tovar;
            this.tovarNarhiList = tovarNarhiList;
            this.ndsDouble = ndsDouble;
            Standart tovar2 = GetDbData.getTovar(tovar);
            text = tovar2.getText();
        }

        public TovarData(Integer tovar) {
            this.tovar = tovar;
            Standart tovar2 = GetDbData.getTovar(tovar);
            text = tovar2.getText();
            ObservableList<TovarNarhi> tovarNarhiList = tovarNarhiOl(tovar);
        }

        public Integer getTovar() {
            return tovar;
        }

        public void setTovar(Integer tovar) {
            this.tovar = tovar;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public ObservableList<TovarNarhi> getTovarNarhiList() {
            return tovarNarhiList;
        }

        public void setTovarNarhiList(ObservableList<TovarNarhi> tovarNarhiList) {
            this.tovarNarhiList = tovarNarhiList;
        }

        public Double getNdsDouble() {
            return ndsDouble;
        }

        public void setNdsDouble(Double ndsDouble) {
            this.ndsDouble = ndsDouble;
        }

        public Double getNarh(Integer narhId) {
            Double narh = 0.0;
            for (TovarNarhi tn: tovarNarhiList) {
                if (tn.getNarhTuri().equals(narhId)) {
                    narh = tn.getNarh();
                    break;
                }
            }
            return narh;
        }

        public Double setNarh(Integer narhId, Double narh) {
            for (TovarNarhi tn: tovarNarhiList) {
                if (tn.getNarhTuri().equals(narhId)) {
                    tn.setNarh(narh);
                    changed = true;
                    break;
                }
            }
            return narh;
        }

        public Boolean getChanged() {
            return changed;
        }

        public void setChanged(Boolean changed) {
            this.changed = changed;
        }
    }

    private TovarData getTovarData(Integer tovarId) {
        TovarData tovarData = null;
        for (TovarData td: tovarDataList) {
            if (td.tovar.equals(tovarId)) {
                tovarData = td;
                break;
            }
        }
        return tovarData;
    }

    public ObservableList<Standart> initNarhTuri() {
        StandartModels standartModels = new StandartModels();
        standartModels.setTABLENAME("NarhTuri");
        ObservableList<Standart> list = standartModels.get_data(connection);
        return list;
    }

}