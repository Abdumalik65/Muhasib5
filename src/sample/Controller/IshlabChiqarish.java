package sample.Controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class IshlabChiqarish extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    GridPane gridPane = new GridPane();
    GridPane xarajatGridPane = new GridPane();
    Tugmachalar xarajatugmachalar1;
    VBox rightPane = new VBox();
    VBox centerPane = new VBox();
    VBox leftPane = new VBox();
    HBox bottomPane = new HBox();
    VBox xarajatlarVBox = new VBox();

    Connection connection;
    User user;

    TextField hisob1TextField;
    TextField hisob2TextField;
    TextArea izohTextArea = new TextArea();
    TextField tizimTextField;
    TextField barCodeTextField = new TextField();
    TextField qaydVaqtiTextField = new TextField();
    TextField adadTextField;
    TextFieldButton hisob1Hbox;
    TextFieldButton hisob2Hbox;
    TextFieldButton tizimHBox;

    Label leftLabel = new Label("Xomashyo jadvali");
    Label centerLabel = new Label("Tayyor mahsulot jadvali");
    Label rightLabel = new Label("Xarajatlar jadvali");

    Date date = new Date();
    DatePicker qaydSanasiDatePicker;
    DecimalFormat decimalFormat = new MoneyShow();

    ComboBox<Standart> birlikComboBox = new ComboBox<>();
    Button xaridniYakunlaButton = new Button("Amalni yakunla");
    Button qaydEtButton = new Tugmachalar().getAdd();
    Button backButton = new Button("<<");

    TableView<HisobKitob> xomashyoTableView;
    TableView<HisobKitob> xarajatlarTableView;
    TableView<HisobKitob> tayyorMahsulotTableView;

    Hisob hisob1;
    Hisob hisob2;
    Hisob hisob3;
    Valuta valuta;
    Standart tizim;
    HisobKitob hisobKitob;
    QaydnomaData qaydnomaData = null;

    Font font = Font.font("Arial", FontWeight.BOLD,20);
    Font font1 = Font.font("Arial", FontWeight.NORMAL,20);
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    StringBuffer stringBuffer = new StringBuffer();

    ObservableList<Hisob> hisobObservableList;
    ObservableList<Standart> tizimObservableList;
    ObservableList<Standart> birlikObservableList;
    ObservableList<Valuta> valutaObservableList;

    HisobModels hisobModels = new HisobModels();
    HisobKitobModels hisobKitobModels = new HisobKitobModels();
    StandartModels standartModels = new StandartModels();
    BarCodeModels barCodeModels = new BarCodeModels();
    KursModels kursModels = new KursModels();
    QaydnomaModel qaydnomaModel = new QaydnomaModel();

    int padding = 3;
    int amalTuri = 3;

    public static void main(String[] args) {
        launch(args);
    }

    public IshlabChiqarish() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
    }

    public IshlabChiqarish(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        String classSimpleName = getClass().getSimpleName();
        DasturlarRoyxati.dastur(connection, user, classSimpleName);
    }

    private void ibtido() {
        initData();
        initTopPane();
        initLeftPane();
        initCenterPane();
        initRightPane();
        initBottomPane();
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

    private void initTopPane() {}

    private void initLeftPane() {
        leftPane.setPadding(new Insets(padding));
        HBox.setHgrow(leftPane, Priority.ALWAYS);
        VBox.setVgrow(leftPane, Priority.ALWAYS);
    }

    private void initCenterPane() {
        centerPane.setPadding(new Insets(padding));
        hisob1Hbox = initHisob1Hbox();
        hisob2Hbox = initHisob2Hbox();
        initQaydSanasiDatePicker();
        initQaydVaqtiTextField();
        initLabels();
        tizimHBox = initTizimHbox(true);
        gridPane = initGridPane();
        centerPane.getChildren().addAll(gridPane, xaridniYakunlaButton);
    }

    private void initRightPane() {
        rightPane.setPadding(new Insets(padding));
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        VBox.setVgrow(rightPane, Priority.ALWAYS);
    }

    private void initBottomPane() {
        bottomPane.setPadding(new Insets(padding));
        HBox.setHgrow(bottomPane, Priority.ALWAYS);
        initYakunlaButton();
        bottomPane.getChildren().add(xaridniYakunlaButton);
    }

    private void initBorderPane() {
        HBox.setHgrow(borderpane, Priority.ALWAYS);
        VBox.setVgrow(borderpane, Priority.ALWAYS);
        borderpane.setLeft(leftPane);
        borderpane.setCenter(centerPane);
        borderpane.setRight(rightPane);
        borderpane.setBottom(bottomPane);
    }

    private void initData() {
        hisobObservableList = hisobModels.get_data(connection);
        standartModels.setTABLENAME("Tizimlar");
        tizimObservableList = standartModels.get_data(connection);
        valutaObservableList = GetDbData.getValutaObservableList();
    }

    private void initLabels() {
        HBox.setHgrow(leftLabel, Priority.ALWAYS);
        HBox.setHgrow(centerLabel, Priority.ALWAYS);
        HBox.setHgrow(rightLabel, Priority.ALWAYS);
        leftLabel.setDisable(true);
        centerLabel.setDisable(true);
        rightLabel.setDisable(true);
        leftLabel.setFont(font1);
        centerLabel.setFont(font1);
        rightLabel.setFont(font1);
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
                Standart tizim = GetDbData.getTovar(hisobKitob.getTovar());
                double barCodeCount = hisobKitobModels.getBarCodeCount(connection, hisobKitob.getHisob1(), hisobKitob.getBarCode());
                if (barCodeCount >= event.getNewValue()) {
                    hisobKitob.setDona(event.getNewValue());
                } else {
                    hisobKitob.setDona(event.getOldValue());
                    Alerts.showKamomat(tizim, event.getNewValue(), hisobKitob.getBarCode(), barCodeCount);
                }
                event.getTableView().refresh();
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
                Valuta v = GetDbData.getValuta(newHisobKitob.getValuta()) ;
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
                    ObservableList<HisobKitob> observableList = param.getTableView().getItems();
                    observableList.remove(hisobKitob);
                    param.getTableView().setItems(observableList);
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
                    ObservableList<HisobKitob> observableList = param.getTableView().getItems();
                    observableList.remove(hisobKitob);
                    param.getTableView().setItems(observableList);
                    param.getTableView().refresh();
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

    private TableColumn<HisobKitob, Double> getBalanceColumn() {
        TableColumn<HisobKitob, Double> column = new TableColumn<>("Bazada");
        column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, Double>, ObservableValue<Double>>() {

            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<HisobKitob, Double> param) {
                HisobKitob hisobKitob = param.getValue();
                Double kamomatDouble = hisobKitob.getBalans();
                return new SimpleObjectProperty<>(kamomatDouble);
            }
        });
        column.setStyle( "-fx-alignment: CENTER;");
        column.setMinWidth(120);
        return column;
    }

    private TableColumn<HisobKitob, Double> getDonaNarhColumn() {
        TableColumn<HisobKitob, Double> column = new TableColumn<>("Narh");
        column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, Double>, ObservableValue<Double>>() {

            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<HisobKitob, Double> param) {
                HisobKitob hisobKitob = param.getValue();
                Double kamomatDouble = hisobKitob.getBalans()/hisobKitob.getDona();
                return new SimpleObjectProperty<>(kamomatDouble);
            }
        });
        column.setStyle( "-fx-alignment: CENTER;");
        column.setMinWidth(120);
        return column;
    }

    private TableColumn<HisobKitob, Double> getStatus() {
        TableColumn<HisobKitob, Double> status = new TableColumn<>("Status");
        status.setCellValueFactory(new PropertyValueFactory<>("balans"));
        status.setCellFactory(column -> {
            TableCell<HisobKitob, Double> cell = new TableCell<HisobKitob, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    Circle circle = new Circle(6);
                    circle.setFill(Color.RED);
                    Circle circle1 = new Circle(6);
                    circle1.setFill(Color.LIGHTGREEN);
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        setText("");
                        if (item < 0) {
                            setGraphic(circle);
                        } else {
                            setGraphic(circle1);
                        }
                    }
                    setAlignment(Pos.CENTER);
                }
            };
            return cell;
        });
        return status;
    }

    private TextFieldButton initHisob1Hbox() {
        TextFieldButton hisob1Hbox = new TextFieldButton();
        hisob1TextField = hisob1Hbox.getTextField();
        Button addButton = hisob1Hbox.getPlusButton();
        hisob1TextField.setFont(font);
        hisob1TextField.setPromptText("Chiqim hisobi");
        HBox.setHgrow(hisob1Hbox, Priority.ALWAYS);
        HBox.setHgrow(hisob1Hbox, Priority.ALWAYS);
        addButton.setMinHeight(37);
        addButton.setGraphic(new PathToImageView("/sample/images/Icons/add.png").getImageView());
        HBox.setHgrow(addButton, Priority.ALWAYS);
        HBox.setHgrow(hisob1TextField, Priority.ALWAYS);
        TextFields.bindAutoCompletion(hisob1TextField, hisobObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Hisob> autoCompletionEvent) -> {
            Hisob newHisob = autoCompletionEvent.getCompletion();
            if (newHisob != null) {
                hisob1 = newHisob;
                hisob2Hbox.setDisable(false);
                hisob2Hbox.getChildren().get(0).requestFocus();
            }
        });
        addButton.setOnAction(event -> {
            Hisob newHisob = addHisob();
            if (newHisob != null) {
                hisob1 = newHisob;
                hisob1TextField.setText(hisob1.getText());
                hisob2Hbox.setDisable(false);
                hisob2Hbox.getChildren().get(0).requestFocus();
            }
        });
        return hisob1Hbox;
    }

    private TextFieldButton initHisob2Hbox() {
        hisob2Hbox = new TextFieldButton();
        hisob2Hbox.setDisable(true);
        hisob2TextField = hisob2Hbox.getTextField();
        Button addButton = hisob2Hbox.getPlusButton();
        hisob2TextField.setFont(font);
        hisob2TextField.setPromptText("Kirim hisobi");
        HBox.setHgrow(hisob2Hbox, Priority.ALWAYS);
        addButton.setMinHeight(37);
        addButton.setGraphic(new PathToImageView("/sample/images/Icons/add.png").getImageView());
        HBox.setHgrow(addButton, Priority.ALWAYS);
        HBox.setHgrow(hisob2TextField, Priority.ALWAYS);
        TextFields.bindAutoCompletion(hisob2TextField, hisobObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Hisob> autoCompletionEvent) -> {
            Hisob newHisob = autoCompletionEvent.getCompletion();
            if (newHisob != null) {
                hisob2 = newHisob;
                changeDisable(false);
                tizimHBox.getChildren().get(0).requestFocus();
            }
        });
        addButton.setOnAction(event -> {
            Hisob newHisob = addHisob();
            if (newHisob != null) {
                hisob2 = newHisob;
                hisob2TextField.setText(hisob2.getText());
                changeDisable(false);
                tizimHBox.getChildren().get(0).requestFocus();
            }
        });
        return hisob2Hbox;
    }

    private TextFieldButton initHisob3Hbox() {
        TextFieldButton hisobHbox = new TextFieldButton();
        hisob1TextField = hisobHbox.getTextField();
        Button addButton = hisobHbox.getPlusButton();
        hisob1TextField.setPromptText("Chiqim hisobi");
        HBox.setHgrow(hisobHbox, Priority.ALWAYS);
        HBox.setHgrow(hisobHbox, Priority.ALWAYS);
        HBox.setHgrow(addButton, Priority.ALWAYS);
        HBox.setHgrow(hisob1TextField, Priority.ALWAYS);
        TextFields.bindAutoCompletion(hisob1TextField, hisobObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Hisob> autoCompletionEvent) -> {
            Hisob newHisob = autoCompletionEvent.getCompletion();
            if (newHisob != null) {
                hisob3 = newHisob;
            }
        });
        addButton.setOnAction(event -> {
            Hisob newHisob = addHisob();
            if (newHisob != null) {
                hisob3 = newHisob;
                hisob1TextField.setText(hisob3.getText());
            }
        });
        return hisobHbox;
    }

    private TextFieldButton initValutaHBox(TextField kursTextField) {
        TextFieldButton valutaHBox = new TextFieldButton();
        HBox.setHgrow(valutaHBox, Priority.ALWAYS);
        valuta = GetDbData.getValuta(1);
        kursTextField.setText("1");
        TextField textField = valutaHBox.getTextField();
        textField.setText(valuta.getValuta());
        TextFields.bindAutoCompletion(textField, valutaObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Valuta> autoCompletionEvent) -> {
            Valuta newValue = autoCompletionEvent.getCompletion();
            if (newValue != null) {
                valuta = newValue;
                Kurs kurs = kursModels.getKurs(connection, valuta.getId(), new Date(), "sana desc");
                if (kurs != null) {
                    kursTextField.setText(decimalFormat.format(kurs.getKurs()));
                }
            }
        });

        Button addButton = valutaHBox.getPlusButton();
        addButton.setOnAction(event -> {
            Valuta newValuta = addValuta(valutaHBox);
            if (newValuta != null) {
                valuta = newValuta;
                textField.setText(valuta.getValuta());
                Kurs kurs = kursModels.getKurs(connection, valuta.getId(), new Date(), "sana desc");
                if (kurs != null) {
                    kursTextField.setText(decimalFormat.format(kurs.getKurs()));
                }
            }
        });
        return valutaHBox;
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
        HBox.setHgrow(izohTextArea, Priority.ALWAYS);
        VBox.setVgrow(izohTextArea, Priority.NEVER);
        izohTextArea.setWrapText(true);
        izohTextArea.setEditable(true);
        izohTextArea.setMaxHeight(20);
    }

    private TextFieldButton initTizimHbox(Boolean disable) {
        tizimHBox = new TextFieldButton();
        tizimHBox.setDisable(disable);
        tizimTextField = tizimHBox.getTextField();
        Button addButton = tizimHBox.getPlusButton();
        tizimTextField.setFont(font);
        tizimTextField.setPromptText("Tizim nomi");
        HBox.setHgrow(tizimTextField, Priority.ALWAYS);
        HBox.setHgrow(tizimHBox, Priority.ALWAYS);
        addButton.setMinHeight(37);
        addButton.setGraphic(new PathToImageView("/sample/images/Icons/add.png").getImageView());
        HBox.setHgrow(tizimTextField, Priority.ALWAYS);

        TextFields.bindAutoCompletion(tizimTextField, tizimObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Standart> autoCompletionEvent) -> {
            Standart newValue = autoCompletionEvent.getCompletion();
            if (newValue != null) {
                tizim = newValue;
                jadvallarniYangila(tizim, 1d);
                adadTextField.requestFocus();
            }
        });

        addButton.setOnAction(event -> {
            StandartController tizimController = new StandartController(connection, user, "Tizimlar");
            tizimController.showAndWait();
            if (tizimController.getDoubleClick()) {
                Standart newValue = tizimController.getDoubleClickedRow();
                if (newValue != null) {
                    tizim = newValue;
                    tizimTextField.setText(tizim.getText());
                    jadvallarniYangila(tizim, 1d);
                    adadTextField.requestFocus();
                }

            }
        });
        return tizimHBox;
    }

    private void initYakunlaButton() {
        xaridniYakunlaButton.setMaxWidth(5000);
        xaridniYakunlaButton.setPrefWidth(150);
        HBox.setHgrow(xaridniYakunlaButton, Priority.ALWAYS);
        xaridniYakunlaButton.setFont(font);
        xaridniYakunlaButton.setOnAction(event -> {
            System.out.println("Bismillah");
            qaydnomaData = yangiQaydnoma();
            xaridSaqlash(qaydnomaData);
            stage.close();
        });
    }

    private QaydnomaData yangiQaydnoma() {
        int hujjatInt = getQaydnomaNumber();
        String izohString = izohTextArea.getText();
        date = getQaydDate();
        QaydnomaData qaydnomaData = new QaydnomaData(null, amalTuri, hujjatInt, date,
                hisob1.getId(), hisob1.getText(), hisob2.getId(), hisob2.getText(),
                izohString, 0d, 0, user.getId(), new Date());
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
        xomAshyoChiqimi(qData);
        xarajatChiqimi(qData);
        tayyorMaxsulotKirimi(qData);
    }

    private void xomAshyoChiqimi(QaydnomaData qData) {
        hisobKitobModels.setTABLENAME("HisobKitob");
        ObservableList<HisobKitob> hisobKitobObservableList = xomashyoTableView.getItems();
        if (hisobKitobObservableList.size()==0) {
            return;
        }
        Integer yordamchiHisob = hisobKitobModels.yordamchiHisob(connection, hisob1.getId(), "TranzitHisobGuruhi", "TranzitHisob");
        ObservableList<HisobKitob> qoldiqList = null;
        String talabString = "";
        String mavjudString = "";
        String kamomatString = "";
        String birlikString = "";
        String alertString = "";
        for (HisobKitob hk: hisobKitobObservableList) {
            hk.setHisob1(hisob1.getId());
            hk.setHisob2(yordamchiHisob);
            hk.setAmal(3);
            hk.setQaydId(qData.getId());
            hk.setHujjatId(qData.getHujjat());
            hk.setDateTime(qData.getSana());
            Double talabDouble = hk.getDona();
            BarCode barCode = GetDbData.getBarCode(hk.getBarCode());
            birlikString = " " + GetDbData.getBirlikFromBarCodeString(barCode).trim().toLowerCase() + "\n";
            talabString = "Talab: " + talabDouble.toString().trim() + birlikString;
            qoldiqList = hisobKitobModels.getBarCodeQoldiq(connection,hk.getHisob1(), barCode, qData.getSana());
            for (HisobKitob q: qoldiqList) {
                HisobKitob clonedHK = hisobKitobModels.cloneHisobKitob(q);
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

    private void xarajatChiqimi(QaydnomaData qData) {
        ObservableList<HisobKitob> xarajatList = xarajatlarTableView.getItems();
        if (xarajatList.size()==0) {
            return;
        }
        Integer yordamchiHisob = hisobKitobModels.yordamchiHisob(connection, hisob1.getId(), "TranzitHisobGuruhi", "TranzitHisob");
        for (HisobKitob hk: xarajatList) {
            hk.setQaydId(qData.getId());
            hk.setHujjatId(qData.getHujjat());
            hk.setAmal(1);
            hk.setHisob2(yordamchiHisob);
        }
        hisobKitobModels.addBatch(connection, xarajatList);
    }
    private void tayyorMaxsulotKirimi(QaydnomaData qData) {
        ObservableList<HisobKitob> tayyoMaxsulotList = tayyorMahsulotTableView.getItems();
        if (tayyoMaxsulotList.size()==0) {
            return;
        }
        Integer yordamchiHisob = hisobKitobModels.yordamchiHisob(connection, hisob1.getId(), "TranzitHisobGuruhi", "TranzitHisob");
        for (HisobKitob hk: tayyoMaxsulotList) {
            hk.setQaydId(qData.getId());
            hk.setHujjatId(qData.getHujjat());
            hk.setAmal(2);
            hk.setNarh(hk.getBalans()/hk.getDona());
            hk.setHisob1(yordamchiHisob);
            hk.setHisob2(hisob2.getId());
        }
        hisobKitobModels.addBatch(connection, tayyoMaxsulotList);
    }

    private GridPane initGridPane() {
        gridPane = new GridPane();
        HBox.setHgrow(gridPane, Priority.ALWAYS);
        VBox.setVgrow(gridPane, Priority.ALWAYS);

        int rowIndex = 0;
        gridPane.add(hisob1Hbox, 0, rowIndex, 1, 1);
        GridPane.setHgrow(hisob1Hbox, Priority.ALWAYS);
        gridPane.add(hisob2Hbox, 1, rowIndex, 1,1);
        GridPane.setHgrow(hisob2Hbox, Priority.ALWAYS);
        gridPane.add(qaydSanasiDatePicker, 2, rowIndex, 1, 1);
        GridPane.setHgrow(qaydSanasiDatePicker, Priority.ALWAYS);

        rowIndex++;
        gridPane.add(tizimHBox, 0, rowIndex, 1, 1);
        GridPane.setHgrow(tizimHBox, Priority.ALWAYS);
        adadTextField = initAdadTextField();
        gridPane.add(adadTextField, 1, rowIndex, 1, 1);
        HBox.setHgrow(adadTextField, Priority.ALWAYS);
        gridPane.add(qaydVaqtiTextField, 2, rowIndex, 1,1);
        GridPane.setHgrow(qaydVaqtiTextField, Priority.ALWAYS);

        rowIndex++;
        gridPane.add(leftLabel, 0, rowIndex, 1, 1);
        gridPane.add(centerLabel, 1, rowIndex, 1, 1);
        gridPane.add(rightLabel, 2, rowIndex, 1, 1);
        GridPane.setHgrow(leftLabel, Priority.ALWAYS);
        GridPane.setHgrow(centerLabel, Priority.ALWAYS);
        GridPane.setHgrow(rightLabel, Priority.ALWAYS);
        GridPane.setHalignment(leftLabel, HPos.CENTER);
        GridPane.setHalignment(centerLabel, HPos.CENTER);
        GridPane.setHalignment(rightLabel, HPos.CENTER);

        rowIndex++;
        xomashyoTableView = initXomashyoTable();
        GridPane.setHgrow(xomashyoTableView, Priority.ALWAYS);
        GridPane.setVgrow(xomashyoTableView, Priority.ALWAYS);
        gridPane.add(xomashyoTableView, 0, rowIndex, 1, 1);

        tayyorMahsulotTableView = initTayyorMaxsulotTable();
        GridPane.setHgrow(tayyorMahsulotTableView, Priority.ALWAYS);
        GridPane.setVgrow(tayyorMahsulotTableView, Priority.ALWAYS);
        gridPane.add(tayyorMahsulotTableView, 1, rowIndex, 1, 1);

        xarajatlarVBox = initXarajatlarHBox();
        GridPane.setHgrow(xarajatlarVBox, Priority.ALWAYS);
        GridPane.setVgrow(xarajatlarVBox, Priority.ALWAYS);
        gridPane.add(xarajatlarVBox, 2, rowIndex, 1, 1);

        rowIndex++;
        initIzohTextArea();
        gridPane.add(izohTextArea, 0, rowIndex, 3, 1);
        GridPane.setHgrow(izohTextArea, Priority.ALWAYS);
        GridPane.setVgrow(izohTextArea, Priority.ALWAYS);

        return gridPane;
    }

    private void initStage(Stage primaryStage) {
        scene = new Scene(borderpane);
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        stage = primaryStage;
        stage.setTitle("Savdo");
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
//        scene.setUserAgentStylesheet("sample/Styles/caspian.css");
        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
            stage.close();
        });
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

    public QaydnomaData getQaydnomaData() {
        return qaydnomaData;
    }

    public void setQaydnomaData(QaydnomaData qaydnomaData) {
        this.qaydnomaData = qaydnomaData;
    }

    private ObservableList<HisobKitob> initXomAshyoList(Double adadDouble) {
        ObservableList<HisobKitob> xomAshyoList = hisobKitobModels.getAnyData(connection, "qaydId=" + tizim.getId(), "");
        Integer tranzitHisob = hisobKitobModels.yordamchiHisob(connection, hisob1.getId(), "TranzitHisobGuruhi", "TranzitHisob");
        for (HisobKitob hk: xomAshyoList) {
            Double adad = hk.getDona()*adadDouble;
            hk.setAmal(3);
            hk.setDona(adad);
            hk.setHisob1(hisob1.getId());
            hk.setHisob2(tranzitHisob);
            double barCodeCount = hisobKitobModels.getBarCodeCount(connection, hk.getHisob1(), hk.getBarCode());
            hk.setBalans(barCodeCount - hk.getDona());
            if (hk.getBalans()<0d) {
                xaridniYakunlaButton.setDisable(true);
            }
            narhJami(hk);
        }
        return xomAshyoList;
    }

    private ObservableList<HisobKitob> initTayyorMaxsulotList() {
        hisobKitobModels.setTABLENAME("TayyorMaxsulot");
        ObservableList<HisobKitob> tayyoMaxsulotList = hisobKitobModels.getAnyData(connection, "qaydId=" + tizim.getId(), "");
        return tayyoMaxsulotList;
    }

    private ObservableList<HisobKitob> initXomAshyoList() {
        hisobKitobModels.setTABLENAME("XomAshyo");
        ObservableList<HisobKitob> xomAshyoList = hisobKitobModels.getAnyData(connection, "qaydId=" + tizim.getId(), "");
        return xomAshyoList;
    }


    private void jadvallarniYangila(Standart tizim, Double adadDouble) {
        ObservableList<HisobKitob> xomAshyoList = FXCollections.observableArrayList();
        ObservableList<HisobKitob> tayyoMaxsulotList = FXCollections.observableArrayList();
        if (adadDouble>0) {
            hisobKitobModels.setTABLENAME("XomAshyo");
            xomAshyoList = xomAshyoList = hisobKitobModels.getAnyData(connection, "qaydId=" + tizim.getId(), "");
            ObservableList<HisobKitob> xarajatList = xarajatlarTableView.getItems();
            hisobKitobModels.setTABLENAME("HisobKitob");
            Double jamiXomAshyo = 0d;
            Double jamiXarajat = 0d;
            xaridniYakunlaButton.setDisable(false);
            for (HisobKitob hk : xomAshyoList) {
                Double adad = hk.getDona() * adadDouble;
                hk.setDona(adad);
                hk.setHisob1(hisob1.getId());
                hk.setHisob2(hisob2.getId());
                double barCodeCount = hisobKitobModels.getBarCodeCount(connection, hk.getHisob1(), hk.getBarCode());
                hk.setBalans(barCodeCount - hk.getDona());
                if (hk.getBalans() < 0d) {
                    xaridniYakunlaButton.setDisable(true);
                }
                narhJami(hk);
                if (hk.getBalans() > 0) {
                    jamiXomAshyo += hk.getDona() * hk.getNarh() / hk.getKurs();
                }
            }

            for (HisobKitob hk : xarajatList) {
                jamiXarajat += hk.getNarh() / hk.getKurs();
            }

            hisobKitobModels.setTABLENAME("TayyorMaxsulot");
            Double jamiDouble = jamiXomAshyo + jamiXarajat;
            if (!xaridniYakunlaButton.isDisable()) {
                tayyoMaxsulotList = hisobKitobModels.getAnyData(connection, "qaydId=" + tizim.getId(), "");
                for (HisobKitob hk : tayyoMaxsulotList) {
                    Double adad = hk.getDona() * adadDouble;
                    hk.setDona(adad);
                    hk.setBalans(hk.getNarh() / hk.getKurs() * jamiDouble / 100);
                }
            }
        }
        xomashyoTableView.setItems(xomAshyoList);
        xomashyoTableView.refresh();
        tayyorMahsulotTableView.setItems(tayyoMaxsulotList);
        tayyorMahsulotTableView.refresh();
    }

    private void narhJami(HisobKitob hk) {
        if (hk.getBalans()>0d) {
            BarCode barCode = GetDbData.getBarCode(hk.getBarCode());
            ObservableList<HisobKitob> observableList = hisobKitobModels.getBarCodeQoldiq(connection, hk.getHisob1(), barCode, new Date());
            Double donaDouble = hk.getDona();
            Double jamiNarhDouble = 0d;
            for (HisobKitob hk1 : observableList) {
                if (hk.getDona() <= hk1.getDona()) {
                    hk.setNarh(hk1.getNarh());
                    jamiNarhDouble += hk1.getNarh() * hk.getDona();
                    hk.setDona(0d);
                    break;
                } else {
                    hk.setDona(hk.getDona() - hk1.getDona());
                    jamiNarhDouble += hk1.getNarh() * hk1.getDona();
                    hk.setNarh(hk.getNarh() + hk1.getDona() * hk1.getNarh());
                }
            }
            hk.setDona(donaDouble);
            hk.setNarh(jamiNarhDouble / donaDouble);
        }
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

    private TextField initAdadTextField() {
        TextField textField = new TextField("1");
        textField.setFont(font);
        textField.setDisable(true);
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue!=null) {
                String textValue = newValue.replaceAll(",", ".");
                textValue = textValue.replaceAll(" ", "");
//                tayyorMaxsulotniYangila(tizim);
                jadvallarniYangila(tizim, getDoubleFromTextField(adadTextField));
            }
        });
        return textField;
    }

    private void tayyorMaxsulotniYangila(Standart tizim) {
        ObservableList<HisobKitob> xomAshyoList = xomashyoTableView.getItems();
        ObservableList<HisobKitob> xarajatList = xarajatlarTableView.getItems();
        ObservableList<HisobKitob> tayyorMaxsulotList = initTayyorMaxsulotList();
        Double jamiXomAshyo = 0d;
        Double jamiXarajat = 0d;
        Double adadDouble = getDoubleFromTextField(adadTextField);
        for (HisobKitob hk: xomAshyoList) {
            jamiXomAshyo += hk.getBalans();
        }

        for (HisobKitob hk: xarajatList) {
            jamiXarajat += hk.getNarh()/hk.getKurs();
        }

        for (HisobKitob hk: tayyorMaxsulotList) {

        }

    }

    private void tayyorMaxsulotniYukla(Standart tizim) {
        ObservableList<HisobKitob> xomAshyoList = xomashyoTableView.getItems();
        ObservableList<HisobKitob> xarajatList = xarajatlarTableView.getItems();
        ObservableList<HisobKitob> tayyorMaxsulotList = initTayyorMaxsulotList();
        Double jamiXomAshyo = 0d;
        Double jamiXarajat = 0d;
        Double adadDouble = getDoubleFromTextField(adadTextField);
        for (HisobKitob hk: xomAshyoList) {
            jamiXomAshyo += hk.getBalans();
        }

        for (HisobKitob hk: xarajatList) {
            jamiXarajat += hk.getNarh()/hk.getKurs();
        }

        for (HisobKitob hk: tayyorMaxsulotList) {

        }

    }

    private VBox initXarajatlarHBox() {
        VBox vBox = new VBox();
        SetHVGrow.VerticalHorizontal(vBox);
        xarajatugmachalar1 = initTugmachalar();
        xarajatlarTableView = initXarajatlarTable();
        vBox.getChildren().addAll(xarajatugmachalar1, xarajatlarTableView);
        return vBox;
    }

    private Tugmachalar initTugmachalar() {
        Tugmachalar xarajatugmachalar1 = new Tugmachalar();
        Button add = xarajatugmachalar1.getAdd();
        add.setOnAction(event -> {
            HBox.setHgrow(backButton, Priority.ALWAYS);
            HBox.setHgrow(qaydEtButton, Priority.ALWAYS);
            System.out.println("Bismillah");
            xomashyoTableView.setDisable(true);
            tayyorMahsulotTableView.setDisable(true);
            xarajatlarVBox.getChildren().remove(xarajatugmachalar1);
            xarajatlarVBox.getChildren().remove(xarajatlarTableView);
            xarajatGridPane = getXarajatGridPane();
            xarajatlarVBox.getChildren().add(xarajatGridPane);
            qaydEtButton.setOnAction(event1 -> {
                HisobKitob hisobKitob = gridToHisobKitob();
                if (hisobKitob != null) {
                    ObservableList<HisobKitob> observableList = xarajatlarTableView.getItems();
                    observableList.add(hisobKitob);
                    xarajatlarTableView.refresh();
                    jadvallarniYangila(tizim, getDoubleFromTextField(adadTextField));
                    backButton.fire();
                }
            });

            backButton.setOnAction(event1 -> {
                xarajatlarVBox.getChildren().remove(xarajatGridPane);
                xarajatlarVBox.getChildren().addAll(xarajatugmachalar1, xarajatlarTableView);
                xomashyoTableView.setDisable(false);
                tayyorMahsulotTableView.setDisable(false);
            });
        });

        Button edit = xarajatugmachalar1.getEdit();
        edit.setOnAction(event -> {});

        Button delete = xarajatugmachalar1.getDelete();
        delete.setOnAction(event -> {});

        Button excel = xarajatugmachalar1.getExcel();
        excel.setOnAction(event -> {});

        return xarajatugmachalar1;
    }

    private HisobKitob gridToHisobKitob() {
        TextField kursTextField = (TextField) xarajatGridPane.getChildren().get(2);
        TextField narhTextField = (TextField) xarajatGridPane.getChildren().get(3);
        TextField izohTextField = (TextField) xarajatGridPane.getChildren().get(4);
        if (hisob3 == null) {
            Alerts.AlertString("Xarajat hisobi kiritilmadi");
            return null;
        }
        if (valuta == null) {
            Alerts.AlertString("Pul turi kiritilmadi");
            return null;
        }
        if (kursTextField.getText().isEmpty()) {
            Alerts.AlertString("Valyuta kursi kiritilmadi");
            return null;
        }
        if (narhTextField.getText().isEmpty()) {
            Alerts.AlertString("Pul miqdori kiritilmadi");
            return null;
        }
        Double kursDouble = getDoubleFromTextField(kursTextField);
        Double narhDouble = getDoubleFromTextField(narhTextField);

        HisobKitob hisobKitob = new HisobKitob(
                null,
                null,
                null,
                2,
                hisob3.getId(),
                hisob1.getId(),
                valuta.getId(),
                0,
                kursDouble,
                "",
                1d,
                narhDouble,
                0,
                izohTextField.getText(),
                user.getId(),
                new Date()
        );
        return hisobKitob;
    }

    private void initTextFields() {
    }

    private void changeDisable(Boolean disable) {
        qaydSanasiDatePicker.setDisable(disable);
        qaydVaqtiTextField.setDisable(disable);
        birlikComboBox.setDisable(disable);
        barCodeTextField.setDisable(disable);
        xomashyoTableView.setDisable(disable);
        xarajatlarTableView.setDisable(disable);
        tayyorMahsulotTableView.setDisable(disable);
        izohTextArea.setDisable(disable);
        tizimHBox.setDisable(disable);
        leftLabel.setDisable(disable);
        centerLabel.setDisable(disable);
        rightLabel.setDisable(disable);
        adadTextField.setDisable(disable);
        xarajatlarVBox.setDisable(disable);
    }

    private  TableView<HisobKitob> initXomashyoTable() {
        TableView<HisobKitob> tableView = new TableView<>();
        tableView.setDisable(true);
        SetHVGrow.VerticalHorizontal(tableView);
        tableView.getColumns().addAll(getIzohColumn(), getAdadColumn(), getNarhColumn(), getSummaColumn(), getBalanceColumn(), getStatus());
        tableView.setEditable(true);
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
            }
        });
        return tableView;
    }
    private  TableView<HisobKitob> initTayyorMaxsulotTable() {
        TableView<HisobKitob> tableView = new TableView<>();
        TableColumn<HisobKitob, Double> adadColumn = getAdadColumn();
        TableColumn<HisobKitob, Double> foizColumn = getNarhColumn();
        TableColumn<HisobKitob, Double> narhColumn = getDonaNarhColumn();
        TableColumn<HisobKitob, Double> jamiColumn = getBalanceColumn();
        foizColumn.setText("Foiz");
        jamiColumn.setText("Jami");
        foizColumn.setMaxWidth(60);
        foizColumn.setMinWidth(60);
        tableView.setDisable(true);
        SetHVGrow.VerticalHorizontal(tableView);
        tableView.getColumns().addAll(getIzohColumn(), foizColumn, adadColumn, narhColumn, jamiColumn);
        tableView.setEditable(true);
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
            }
        });
        return tableView;
    }
    private  TableView<HisobKitob> initXarajatlarTable() {
        TableView<HisobKitob> tableView = new TableView<>();
        tableView.setDisable(true);
        SetHVGrow.VerticalHorizontal(tableView);
        tableView.getColumns().addAll(getNarhColumn(), getValutaColumn(), getIzohColumn());
        tableView.setEditable(true);
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
            }
        });
        return tableView;
    }

    private GridPane getXarajatGridPane() {
        GridPane gridPane = new GridPane();
        Integer rowIndex = 0;
        TextFieldButton hisobTextFieldButton = initHisob3Hbox();
        hisobTextFieldButton.getTextField().setPromptText("Hisob");
        TextField kursTextField = new TextField();
        TextFieldButton valutaTextFieldButton = initValutaHBox(kursTextField);
        valutaTextFieldButton.getTextField().setPromptText("Valuta");
        kursTextField.setPromptText("Kurs");
        TextField miqdorTextField = new TextField();
        miqdorTextField.setPromptText("Miqdor");
        TextField izohTextField = new TextField();
        izohTextField.setPromptText("Izoh");

        gridPane.add(hisobTextFieldButton, 0, rowIndex, 2, 1);

        rowIndex++;
        gridPane.add(valutaTextFieldButton, 0, rowIndex, 2, 1);

        rowIndex++;
        gridPane.add(kursTextField, 0, rowIndex, 2, 1);

        rowIndex++;
        gridPane.add(miqdorTextField, 0, rowIndex, 2, 1);

        rowIndex++;
        gridPane.add(izohTextField, 0, rowIndex, 2, 1);

        backButton.setMaxWidth(2000);
        backButton.setPrefWidth(150);
        qaydEtButton.setMaxWidth(2000);
        qaydEtButton.setPrefWidth(150);

        rowIndex++;
        gridPane.add(backButton, 0, rowIndex, 1, 1);
        gridPane.add(qaydEtButton, 1, rowIndex, 1, 1);
        GridPane.setHgrow(backButton, Priority.ALWAYS);
        GridPane.setHgrow(qaydEtButton, Priority.ALWAYS);

        return gridPane;
    }

    private Valuta addValuta(TextFieldButton valutaHBox) {
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

    private void xomAshyoniSaqla(QaydnomaData qaydnomaData) {
        Integer tranzitHisob = hisobKitobModels.yordamchiHisob(connection, hisob1.getId(), "TranzitHisobGuruhi", "TranzitHisob");
        ObservableList<HisobKitob> observableList = xomashyoTableView.getItems();
        for (HisobKitob hk: observableList) {
            hk.setQaydId(qaydnomaData.getId());
            hk.setHujjatId(qaydnomaData.getHujjat());
            hk.setHisob2(tranzitHisob);
            hk.setDateTime(qaydnomaData.getSana());
            BarCode barCode = GetDbData.getBarCode(hk.getBarCode());
            ObservableList<HisobKitob> qoldiqList = hisobKitobModels.getBarCodeQoldiq(connection, hisob1.getId(), barCode, qaydnomaData.getSana());
            for (HisobKitob qhk: qoldiqList) {

            }
        }
        hisobKitobModels.addBatch(connection, observableList);
    }
    private void tayyorMaxsulotniSaqla(QaydnomaData qaydnomaData) {
        Integer tranzitHisob = hisobKitobModels.yordamchiHisob(connection, hisob1.getId(), "TranzitHisobGuruhi", "TranzitHisob");
        ObservableList<HisobKitob> observableList = tayyorMahsulotTableView.getItems();
        for (HisobKitob hisobKitob: observableList) {
            hisobKitob.setQaydId(qaydnomaData.getId());
            hisobKitob.setHujjatId(qaydnomaData.getHujjat());
            hisobKitob.setHisob1(tranzitHisob);
            hisobKitob.setDateTime(qaydnomaData.getSana());
        }
        hisobKitobModels.addBatch(connection, observableList);
    }
}