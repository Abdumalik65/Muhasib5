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
import sample.Model.*;
import sample.Tools.*;
import sample.Data.*;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TezNarhKiritish extends Application {
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

    TableView<TableViewData> dataTableView = new TableView<>();
    Connection connection;

    Double narhDouble = 0.0;
    Double chakanaDouble = 0.0;
    Double ulgurjiDouble = 0.0;
    Double hajmDouble = 0.0;
    Double vaznDouble = 0.0;
    Double ndsDouble = 0.0;
    Double adadDouble = 0.0;

    int padding = 3;
    Integer amalTuri = 3;

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

    TableViewData tableViewDataCursor;
    User user = new User(1, "admin", "", "admin");
    Hisob hisob1;
    Hisob hisob2;
    Standart tovar;

    Date date = new Date();
    LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    DatePicker qaydSanasiDatePicker;

    ObservableList<TableViewData> dataObservableList = FXCollections.observableArrayList();
    ObservableList<Standart> tovarObservableList;
    ObservableList<Standart4> ndsObservableList = FXCollections.observableArrayList();
    ObservableList<Hisob> hisobObservableList;
    ObservableList<BarCode> barCodes = FXCollections.observableArrayList();
    ObservableList<TovarNarhi> tovarNarhlari = FXCollections.observableArrayList();
    ObservableList<TovarSana> tovarSanalari = FXCollections.observableArrayList();
    ObservableList<HisobKitob> hisobKitobObservableList = FXCollections.observableArrayList();

    Font font = Font.font("Arial", FontWeight.BOLD,20);

    public static void main(String[] args) {
        launch(args);
    }

    public TezNarhKiritish() {
        connection = new MySqlDBLocal().getDbConnection();
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
    }

    private void ibtido() {
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
        dataTableView.setItems(dataObservableList);
        dataTableView.getColumns().addAll(getTovarColumn(), getBarCodeColumn(), getBirlikColumn(), getAdadColumn(), getNarhColumn(), getChakanaNarhColumn(), getUlgurjiNarhColumn(), getVaznColumn(), getHajmColumn(), getNdsColumn(), getDeleteColumn());

        dataTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                tableViewDataCursor = newValue;
            }
        });
    }

    private TableColumn<TableViewData, Integer> getBirlikColumn() {
        TableColumn<TableViewData, Integer> birlikColumn = new TableColumn<>("Birlik");
        birlikColumn.setCellValueFactory(new PropertyValueFactory<>("birlik"));
        birlikColumn.setMinWidth(100);
        birlikColumn.setMaxWidth(100);
        birlikColumn.setCellFactory(column -> {
            TableCell<TableViewData, Integer> cell = new TableCell<TableViewData, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        Text text = new Text(GetDbData.getBirlik(item).getText());
                        text.setStyle("-fx-text-alignment:justify;");
                        text.wrappingWidthProperty().bind(getTableColumn().widthProperty().subtract(1));
                        setGraphic(text);
                    }
                    setAlignment(Pos.CENTER_LEFT);
                }
            };
            return cell;
        });
        return birlikColumn;
    }

    private TableColumn<TableViewData, String> getBarCodeColumn() {
        TableColumn<TableViewData, String> barCodeColumn = new TableColumn<>("Shtrix kod");
        barCodeColumn.setCellValueFactory(new PropertyValueFactory<>("barCodeString"));
        barCodeColumn.setMinWidth(150);
        barCodeColumn.setMaxWidth(150);
        barCodeColumn.setStyle( "-fx-alignment: CENTER;");

        return barCodeColumn;
    }

    private TableColumn<TableViewData, Double> getNarhColumn() {
        TableColumn<TableViewData, Double> naNarhColumn = new TableColumn<>("Xarid narhi");
        naNarhColumn.setCellValueFactory(new PropertyValueFactory<>("narhDouble"));
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
            tableViewDataCursor.setNarhDouble(event.getNewValue());
            narhDouble = event.getNewValue();
            event.getTableView().refresh();
        });
        naNarhColumn.setStyle( "-fx-alignment: CENTER;");

        return naNarhColumn;
    }

    private TableColumn<TableViewData, Double> getChakanaNarhColumn() {
        TableColumn<TableViewData, Double> chakanaNarhColumn = new TableColumn<>("Chakana narh");
        chakanaNarhColumn.setCellValueFactory(new PropertyValueFactory<>("chakanaDouble"));
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
            tableViewDataCursor.setChakanaDouble(event.getNewValue());
            chakanaDouble = event.getNewValue();
            event.getTableView().refresh();
        });
        chakanaNarhColumn.setStyle( "-fx-alignment: CENTER;");

        return chakanaNarhColumn;
    }

    private TableColumn<TableViewData, Double> getAdadColumn() {
        TableColumn<TableViewData, Double> adadColumn = new TableColumn<>("Dona");
        adadColumn.setCellValueFactory(new PropertyValueFactory<>("adadDouble"));
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
            tableViewDataCursor.setAdadDouble(event.getNewValue());
            adadDouble = event.getNewValue();
            event.getTableView().refresh();
        });
        adadColumn.setStyle( "-fx-alignment: CENTER;");

        return adadColumn;
    }

    private TableColumn<TableViewData, Double> getUlgurjiNarhColumn() {
        TableColumn<TableViewData, Double> ulgurjiNarhColumn = new TableColumn<>("Ulgurji narh");
        ulgurjiNarhColumn.setCellValueFactory(new PropertyValueFactory<>("ulgurjiDouble"));
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
            tableViewDataCursor.setUlgurjiDouble(event.getNewValue());
            ulgurjiDouble = event.getNewValue();
        });
        ulgurjiNarhColumn.setStyle( "-fx-alignment: CENTER;");

        return ulgurjiNarhColumn;
    }

    private TableColumn<TableViewData, Double> getNdsColumn() {
        TableColumn<TableViewData, Double> ndsColumn = new TableColumn<>("NDS");
        ndsColumn.setCellValueFactory(new PropertyValueFactory<>("ndsDouble"));
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
            tableViewDataCursor.setNdsDouble(event.getNewValue());
            ndsDouble = event.getNewValue();
        });
        ndsColumn.setStyle( "-fx-alignment: CENTER;");

        return ndsColumn;
    }

    private TableColumn<TableViewData, Double> getVaznColumn() {
        TableColumn<TableViewData, Double> vaznColumn = new TableColumn<>("Vazn");
        vaznColumn.setCellValueFactory(new PropertyValueFactory<>("vaznDouble"));
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
            tableViewDataCursor.setVaznDouble(event.getNewValue());
            vaznDouble = event.getNewValue();
        });
        vaznColumn.setStyle( "-fx-alignment: CENTER;");

        return vaznColumn;
    }

    private TableColumn<TableViewData, Double> getHajmColumn() {
        TableColumn<TableViewData, Double> hajmColumn = new TableColumn<>("Hajm");
        hajmColumn.setCellValueFactory(new PropertyValueFactory<>("hajmDouble"));
        hajmColumn.setMinWidth(100);
        hajmColumn.setMaxWidth(100);
        hajmColumn.setStyle( "-fx-alignment: CENTER;");

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
            tableViewDataCursor.setHajmDouble(event.getNewValue());
            hajmDouble = event.getNewValue();
        });
        hajmColumn.setStyle( "-fx-alignment: CENTER;");
        return hajmColumn;
    }

    private TableColumn<TableViewData, Button> getDeleteColumn() {
        TableColumn<TableViewData, Button> deleteColumn = new TableColumn<>("O`chir");
        deleteColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TableViewData, Button>, ObservableValue<Button>>() {

            @Override
            public ObservableValue<Button> call(TableColumn.CellDataFeatures<TableViewData, Button> param) {
                TableViewData tableViewData = param.getValue();
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
                    dataObservableList.remove(tableViewData);
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

    private TableColumn<TableViewData, Integer> getTovarColumn() {
        TableColumn<TableViewData, Integer> tovarColumn = new TableColumn<>("Tovar");
        tovarColumn.setCellValueFactory(new PropertyValueFactory<>("tovar"));
        tovarColumn.setMinWidth(200);
        tovarColumn.setMaxWidth(200);
        tovarColumn.setCellFactory(column -> {
            TableCell<TableViewData, Integer> cell = new TableCell<TableViewData, Integer>() {
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
//                                addTovar(tovar);
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
        hisobKitobObservableList.removeAll(hisobKitobObservableList);
        QaydnomaData qaydnomaData = qaydnomaSaqlash();
        for (TableViewData td: dataObservableList) {
            vaznHajmList(td);
            sotishNarhlariList(td, date);
            ndsList(td);
            hisobKitobList(qaydnomaData, td);
        }
        saqla();
        dataObservableList.removeAll(dataObservableList);
        dataTableView.refresh();
    }

    private void vaznHajmList(TableViewData td) {
        BarCodeModels barCodeModels = new BarCodeModels();
        BarCode barCode = GetDbData.getBarCode(td.getBarCodeString());
        Boolean edited = false;
        if (td.getVaznDouble() != td.getVaznDouble2()) {
            barCode.setVazn(td.getVaznDouble());
            edited = true;
        }
        if (td.getHajmDouble() != td.getHajmDouble2()) {
            barCode.setHajm(td.getHajmDouble());
            edited = true;
        }
        if (edited) {
            barCodeModels.update_data(connection, barCode);
        }
    }

    private void sotishNarhlariList(TableViewData td, Date date) {
        if (td.getUlgurjiDouble() != td.getUlgurjiDouble2() && td.getChakanaDouble() != td.getChakanaDouble2()) {
            StandartModels standartModels = new StandartModels();
            standartModels.setTABLENAME("NarhTuri");
            ObservableList<Standart> narhTurlari = standartModels.get_data(connection);
            TovarSana tovarSana = new TovarSana(null, date, td.tovar, user.getId(), date);
            tovarSanalari.add(tovarSana);
            if (narhTurlari.size() > 0) {
                Standart narhTuri1 = narhTurlari.get(0);
                tovarNarhlari.add(new TovarNarhi(null, date, td.tovar, narhTuri1.getId(), 1, 1.0, td.getChakanaDouble(), user.getId(), date));
                Standart narhTuri2 = narhTurlari.get(1);
                tovarNarhlari.add(new TovarNarhi(null, date, td.tovar, narhTuri2.getId(), 1, 1.0, td.getUlgurjiDouble(), user.getId(), date));
            }
        }
    }

    private void ndsList(TableViewData td) {
        if (td.getNdsDouble() != td.getNdsDouble2()) {
            Standart4 ndsStandart4 = new Standart4(
                    null,
                    td.tovar,
                    new Date(),
                    td.ndsDouble,
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
        hisobKitobModels.addBatch(connection, hisobKitobObservableList);
        Standart4Models standart4Models = new Standart4Models();
        standart4Models.setTABLENAME("Nds");
        standart4Models.addBatch(connection, ndsObservableList);
    }

    private void ndsOl(Integer tovarId) {
        Standart4 ndsStandart4 = null;
        Standart4Models standart4Models = new Standart4Models();
        standart4Models.setTABLENAME("NDS");
        ObservableList<Standart4> ndsObservableList = standart4Models.getDate(connection, tovarId, new Date(), "sana desc");
        if (ndsObservableList.size()>0) {
            ndsStandart4 = ndsObservableList.get(0);
            ndsDouble = ndsStandart4.getMiqdor();
        }
    }

    private void hisobKitobList(QaydnomaData qaydnomaData, TableViewData td) {
        Standart tovarStandart = GetDbData.getTovar(td.tovar);
        Double umumiyAdad = tovarDonasi(td.getBarCodeString());
        HisobKitob hisobKitob = new HisobKitob(
                null,
                qaydnomaData.getId(),
                qaydnomaData.getHujjat(),
                amalTuri,
                hisob1.getId(),
                hisob2.getId(),
                1,
                td.getTovar(),
                1.0,
                td.getBarCodeString(),
                td.getAdadDouble(),
                umumiyAdad * td.getNarhDouble(),
                0,
                tovarStandart.getText(),
                user.getId(),
                null
        );
        hisobKitobObservableList.add(hisobKitob);
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
            }
        }
        return dona;
    }

    private  TableViewData addBarCode(BarCode bc) {
        TableViewData tableViewData = null;
        if (bc != null) {
            tableViewData = barCodeTopildimi(bc.getBarCode());
            if (tableViewData == null) {
                if (bc.getHajm()>0) {
                    hajmDouble = bc.getHajm();
                }

                if (bc.getVazn()>0) {
                    vaznDouble = bc.getVazn();
                }

                tovarNarhiOl(bc.getTovar());
                ndsOl(bc.getTovar());

                tableViewData = new TableViewData(
                        bc.getTovar(),
                        bc.getBarCode(),
                        bc.getBirlik(),
                        1.0,
                        narhDouble,
                        ulgurjiDouble,
                        chakanaDouble,
                        ndsDouble,
                        hajmDouble,
                        vaznDouble,
                        ulgurjiDouble,
                        chakanaDouble
                );
                tableViewData.setHajmDouble2(hajmDouble);
                tableViewData.setVaznDouble2(vaznDouble);
                dataObservableList.add(tableViewData);
            } else {
                Double adadDouble = tableViewData.getAdadDouble() + 1;
                tableViewData.setAdadDouble(adadDouble);
            }
            dataTableView.getSelectionModel().select(tableViewData);
            dataTableView.scrollTo(tableViewData);
            dataTableView.requestFocus();
            dataTableView.refresh();
        }
        return tableViewData;
    }

    private void tovarNarhiOl(Integer tovarId) {
        TovarNarhi tovarNarhi = null;
        TovarNarhiModels tovarNarhiModels = new TovarNarhiModels();
        ObservableList<TovarNarhi> tovarNarhiObservableList = tovarNarhiModels.getDate2(
                connection,
                tovarId,
                new Date(),
                "dateTime desc"
        );
        int i = 0;
        for (TovarNarhi tn: tovarNarhiObservableList) {
            if (tn.getNarhTuri() == 1) {
                i++;
                chakanaDouble = tn.getNarh();
            }
            if (tn.getNarhTuri() == 2) {
                i++;
                ulgurjiDouble = tn.getNarh();
            }
            if (i == 2) {
                break;
            }
        }
    }

    private TableViewData barCodeTopildimi(String string) {
        TableViewData tableViewData = null;
        for (TableViewData td: dataObservableList) {
            if (td.getBarCodeString().trim().equalsIgnoreCase(string)) {
                tableViewData = td;
                break;
            }
        }
        return tableViewData;
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
    public class TableViewData {
        Integer tovar = 0;
        String barCodeString = "";
        Double adadDouble = 0.00;
        Double narhDouble = 0.00;
        Double ulgurjiDouble = 0.00;
        Double chakanaDouble = 0.00;
        Double ndsDouble = 0.00;
        Double hajmDouble = 0.00;
        Double vaznDouble = 0.00;
        Integer birlik = 0;
        Double ulgurjiDouble2 = 0.00;
        Double chakanaDouble2 = 0.00;
        Double ndsDouble2 = 0.00;
        Double hajmDouble2 = 0.00;
        Double vaznDouble2 = 0.00;

        public TableViewData() {
        }

        public TableViewData(Integer tovar, String barCodeString, Double adadDouble, Double narhDouble, Double ulgurjiDouble, Double chakanaDouble, Double ndsDouble, Double hajmDouble, Double vaznDouble, Integer birlik) {
            this.tovar = tovar;
            this.barCodeString = barCodeString;
            this.adadDouble = adadDouble;
            this.narhDouble = narhDouble;
            this.ulgurjiDouble = ulgurjiDouble;
            this.chakanaDouble = chakanaDouble;
            this.ndsDouble = ndsDouble;
            this.hajmDouble = hajmDouble;
            this.vaznDouble = vaznDouble;
            this.birlik = birlik;
        }

        public TableViewData(Integer tovar, String barCodeString, Integer birlik, Double adadDouble, Double narhDouble, Double ulgurjiDouble, Double chakanaDouble, Double ndsDouble, Double hajmDouble, Double vaznDouble) {
            this.tovar = tovar;
            this.barCodeString = barCodeString;
            this.birlik = birlik;
            this.adadDouble = adadDouble;
            this.narhDouble = narhDouble;
            this.ulgurjiDouble = ulgurjiDouble;
            this.chakanaDouble = chakanaDouble;
            this.ndsDouble = ndsDouble;
            this.hajmDouble = hajmDouble;
            this.vaznDouble = vaznDouble;
        }

        public TableViewData(Integer tovar, String barCodeString, Integer birlik, Double adadDouble, Double narhDouble, Double ulgurjiDouble, Double chakanaDouble, Double ndsDouble, Double hajmDouble, Double vaznDouble, Double ulgurjiDouble2, Double chakanaDouble2) {
            this.tovar = tovar;
            this.barCodeString = barCodeString;
            this.birlik = birlik;
            this.adadDouble = adadDouble;
            this.narhDouble = narhDouble;
            this.ulgurjiDouble = ulgurjiDouble;
            this.chakanaDouble = chakanaDouble;
            this.ndsDouble = ndsDouble;
            this.hajmDouble = hajmDouble;
            this.vaznDouble = vaznDouble;
            this.ulgurjiDouble2 = ulgurjiDouble2;
            this.chakanaDouble2 = chakanaDouble2;
        }

        public Integer getTovar() {
            return tovar;
        }

        public void setTovar(Integer tovar) {
            this.tovar = tovar;
        }

        public String getBarCodeString() {
            return barCodeString;
        }

        public void setBarCodeString(String barCodeString) {
            this.barCodeString = barCodeString;
        }

        public Double getAdadDouble() {
            return adadDouble;
        }

        public void setAdadDouble(Double adadDouble) {
            this.adadDouble = adadDouble;
        }

        public Double getNarhDouble() {
            return narhDouble;
        }

        public void setNarhDouble(Double narhDouble) {
            this.narhDouble = narhDouble;
        }

        public Double getUlgurjiDouble() {
            return ulgurjiDouble;
        }

        public void setUlgurjiDouble(Double ulgurjiDouble) {
            this.ulgurjiDouble = ulgurjiDouble;
        }

        public Double getChakanaDouble() {
            return chakanaDouble;
        }

        public void setChakanaDouble(Double chakanaDouble) {
            this.chakanaDouble = chakanaDouble;
        }

        public Double getNdsDouble() {
            return ndsDouble;
        }

        public void setNdsDouble(Double ndsDouble) {
            this.ndsDouble = ndsDouble;
        }

        public Double getHajmDouble() {
            return hajmDouble;
        }

        public void setHajmDouble(Double hajmDouble) {
            this.hajmDouble = hajmDouble;
        }

        public Double getVaznDouble() {
            return vaznDouble;
        }

        public void setVaznDouble(Double vaznDouble) {
            this.vaznDouble = vaznDouble;
        }

        public Integer getBirlik() {
            return birlik;
        }

        public void setBirlik(Integer birlik) {
            this.birlik = birlik;
        }

        public Double getUlgurjiDouble2() {
            return ulgurjiDouble2;
        }

        public void setUlgurjiDouble2(Double ulgurjiDouble2) {
            this.ulgurjiDouble2 = ulgurjiDouble2;
        }

        public Double getChakanaDouble2() {
            return chakanaDouble2;
        }

        public void setChakanaDouble2(Double chakanaDouble2) {
            this.chakanaDouble2 = chakanaDouble2;
        }

        public Double getNdsDouble2() {
            return ndsDouble2;
        }

        public void setNdsDouble2(Double ndsDouble2) {
            this.ndsDouble2 = ndsDouble2;
        }

        public Double getHajmDouble2() {
            return hajmDouble2;
        }

        public void setHajmDouble2(Double hajmDouble2) {
            this.hajmDouble2 = hajmDouble2;
        }

        public Double getVaznDouble2() {
            return vaznDouble2;
        }

        public void setVaznDouble2(Double vaznDouble2) {
            this.vaznDouble2 = vaznDouble2;
        }
    }
}