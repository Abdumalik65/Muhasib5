package sample.Controller;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import sample.Config.MySqlDBGeneral;
import sample.Config.SqliteDB;
import sample.Data.*;
import sample.Enums.ServerType;
import sample.Model.HisobKitobModels;
import sample.Model.QaydnomaModel;
import sample.Model.StandartModels;
import sample.Tools.*;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class TovarNaqliyotlari extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    VBox rightPane = new VBox();
    VBox centerPane = new VBox();
    TableView<QaydnomaData> qaydnomaTableView = new TableView();
    TableView<HisobKitob> tovarTableView = new TableView();
    Tugmachalar tugmachalar;
    HBox taftishHBox = new HBox();
    ObservableList<QaydnomaData> qaydnomaDataObservableList;
    ObservableList<HisobKitob> tableObservableList = FXCollections.observableArrayList();
    ContextMenu contextMenu;

    HisobKitobModels hisobKitobModels = new HisobKitobModels();

    Connection connection;
    User user;
    QaydnomaData qaydnomaData;

    DecimalFormat decimalFormat = new MoneyShow();
    int padding = 3;
    int amalTuri = 3;

    Font font = Font.font("Arial",20);

    public static void main(String[] args) {
        launch(args);
    }

    public TovarNaqliyotlari() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
        ibtido();
    }

    public TovarNaqliyotlari(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        String classSimpleName = getClass().getSimpleName();
        DasturlarRoyxati.dastur(connection, user, classSimpleName);
        ibtido();
    }

    private void ibtido() {
        initTopPane();
        initLeftPane();
        initCenterPane();
        initRightPane();
        initBorderPane();
        initBottomPane();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initStage(primaryStage);
        stage.show();
    }

    public void display() {
        stage = new Stage();
        initStage(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    private void initTopPane() {}

    private void initLeftPane() {
    }

    private void initCenterPane() {
        SetHVGrow.VerticalHorizontal(centerPane);
        centerPane.setPadding(new Insets(padding));
        tovarTableView = yangiHisobKitobJadval();
        centerPane.getChildren().addAll(tovarTableView);
    }

    private void initRightPane() {
        initTaftishHBox();
        qaydnomaTableView();
        SetHVGrow.VerticalHorizontal(rightPane);
        tugmachalar = yangiTugmachalar();
        rightPane.getChildren().addAll(tugmachalar, taftishHBox, qaydnomaTableView);
    }

    private void initBottomPane() {}

    private void initBorderPane() {
        borderpane.setPadding(new Insets(padding));
        SetHVGrow.VerticalHorizontal(borderpane);
        borderpane.setCenter(centerPane);
        borderpane.setRight(rightPane);
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        stage.setTitle("Tovar naqliyotlari");
        scene = new Scene(borderpane);
        scene.setUserAgentStylesheet("sample/Styles/caspian.css");
        stage.setScene(scene);
    }

    private void qaydnomaTableView() {
        SetHVGrow.VerticalHorizontal(qaydnomaTableView);
        qaydnomaTableView.setPadding(new Insets(padding));
        QaydnomaModel qaydnomaModel = new QaydnomaModel();
        if (user.getStatus() == 99) {
            qaydnomaDataObservableList = qaydnomaModel.getAnyData(connection, "amalTuri = " + amalTuri, "sana desc");
        } else {
            qaydnomaDataObservableList = qaydnomaModel.getAnyData(connection, "amalTuri = " + amalTuri + " AND userId = " + user.getId(), "sana desc");
        }
        TableViewAndoza tableViewAndoza = new TableViewAndoza();
        TableColumn<QaydnomaData, Integer> hujjatColumn = tableViewAndoza.getHujjatColumn();
        hujjatColumn.setMinWidth(73);
        qaydnomaTableView.getColumns().addAll(
                tableViewAndoza.getSanaColumn(),
                hujjatColumn,
                hisoblarUstuni()
        );
        qaydnomaTableView.setItems(qaydnomaDataObservableList);
        if (qaydnomaDataObservableList.size()>0) {
            qaydnomaData = qaydnomaDataObservableList.get(0);
            qaydnomaTableView.scrollTo(qaydnomaData);
            qaydnomaTableView.getSelectionModel().select(qaydnomaData);
            qaydnomaTableView.requestFocus();
            tovarTableView.refresh();
            malumotniYangila(qaydnomaData);
        }

        qaydnomaTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                qaydnomaData = newValue;
                malumotniYangila(qaydnomaData);
            }
        });
    }

    private void initTaftishHBox() {
        HBox.setHgrow(taftishHBox, Priority.ALWAYS);
//        taftishHBox.setPadding(new Insets(padding));
        ObservableList<Standart> observableList = FXCollections.observableArrayList(
                new Standart(1, "Sana", null, null),
                new Standart(2, "Hujjat", null, null),
                new Standart(3, "Chiqim hisobi", null, null),
                new Standart(4, "Kirim hisobi", null, null)
        );
        ComboBox comboBox = new ComboBox(observableList);
        comboBox.getSelectionModel().selectFirst();

        TextField textField = new TextField();
        HBox.setHgrow(textField, Priority.ALWAYS);
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            Taftish(oldValue, newValue,comboBox.getSelectionModel().getSelectedIndex());
        });

        taftishHBox.getChildren().addAll(comboBox, textField);
    }

    private Tugmachalar yangiTugmachalar() {
        Tugmachalar tugmachalar = new Tugmachalar();
        Button add = tugmachalar.getAdd();
        add.setFont(font);
        add.setMaxWidth(2000);
        add.setPrefWidth(168);
        add.setOnAction(event -> {
            NaqliyotController naqliyotController = new NaqliyotController(connection, user);
            qaydnomaData = naqliyotController.display();
            if (qaydnomaData != null) {
                qaydnomaTableView.getItems().add(0, qaydnomaData);
                qaydnomaTableView.scrollTo(qaydnomaData);
                qaydnomaTableView.requestFocus();
                qaydnomaTableView.getSelectionModel().select(qaydnomaData);
                qaydnomaTableView.refresh();
            }
        });

        Button delete = tugmachalar.getDelete();
        delete.setFont(font);
        delete.setMaxWidth(2000);
        delete.setPrefWidth(168);
        delete.setOnAction(event -> {
            QaydnomaData qaydnomaData = qaydnomaTableView.getSelectionModel().getSelectedItem();
            if (qaydnomaData != null) {
                if (Alerts.haYoq(qaydnomaData.getHujjat() + " raqamli qayd va unga tegishli barcha ma'lumot o'chiriladi", "O'chirasizmi???"))  {
                    QaydnomaModel qaydnomaModel = new QaydnomaModel();
                    hisobKitobModels.deleteWhere(connection, "qaydId=" + qaydnomaData.getId());
                    qaydnomaTableView.getItems().remove(qaydnomaData);
                    qaydnomaModel.delete_data(connection, qaydnomaData);
                    qaydnomaTableView.refresh();
                    tovarTableView.refresh();
                }
            }
        });

        Button edit = tugmachalar.getEdit();
        tugmachalar.getChildren().remove(edit);
        Button excel = tugmachalar.getExcel();
        tugmachalar.getChildren().remove(excel);
        HBox.setHgrow(tugmachalar, Priority.ALWAYS);
        return tugmachalar;
    }

    private void Taftish(String oldValue, String newValue, Integer option) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        ObservableList<QaydnomaData> subentries = FXCollections.observableArrayList();

        newValue = newValue.toLowerCase();

        if ( oldValue != null && (newValue.length() < oldValue.length()) ) {
            qaydnomaTableView.setItems(qaydnomaDataObservableList);
        }

        for ( QaydnomaData qayd: qaydnomaDataObservableList ) {
            switch (option) {
                case 0:
                    if (sdf.format(qayd.getSana()).contains(newValue)) {
                        subentries.add(qayd);
                    }
                    break;
                case 1:
                    if (qayd.getHujjat().toString().contains(newValue)) {
                        subentries.add(qayd);
                    }
                    break;
                case 2:
                    if (qayd.getChiqimNomi().toLowerCase().contains(newValue)) {
                        subentries.add(qayd);
                    }
                    break;
                case 3:
                    if (qayd.getKirimNomi().toLowerCase().contains(newValue)) {
                        subentries.add(qayd);
                    }
                    break;
            }
        }
        qaydnomaTableView.setItems(subentries);
    }

    private ObservableList<QaydnomaData> tovarNomidanTop(String string) {
        ObservableList<QaydnomaData> qaydnomaDataObservableList = null;
        ObservableList<HisobKitob> hisobKitobObservableList = hisobKitobModels.getAnyData(connection,"","");
        return qaydnomaDataObservableList;
    }
    private String printerim() {
        Connection printersConnection = new SqliteDB().getDbConnection();
        StandartModels printerModels = new StandartModels("Printers");
        ObservableList<Standart> printers = printerModels.get_data(printersConnection);
        String printerNomi = "Topmadim";
        Standart myPrinter = null;
        if (printers.size()>0) {
            myPrinter = printers.get(0);
            printerNomi = myPrinter.getText();
        }
        return printerNomi;
    }

    private TableColumn<QaydnomaData, Integer> getHujjatColumn() {
        TableColumn<QaydnomaData, Integer>  chipta = new TableColumn<>("Xarid raqami");
        chipta.setMinWidth(100);
        chipta.setMaxWidth(100);
        chipta.setCellValueFactory(new PropertyValueFactory<>("hujjat"));
        chipta.setStyle( "-fx-alignment: CENTER;");
        return chipta;
    }


    private void xaridniOchir() {
        QaydnomaModel qaydnomaModel = new QaydnomaModel();
        int qaydId = qaydnomaData.getId();
        hisobKitobModels.deleteWhere(connection, "qaydId = " + qaydId);
        tableObservableList.removeAll(tableObservableList);
        qaydnomaModel.delete_data(connection, qaydnomaData);
        qaydnomaDataObservableList.remove(qaydnomaData);
        tovarTableView.refresh();
        qaydnomaTableView.refresh();
        System.out.println("Xaridni o`chir");
    }

    private TableView<HisobKitob> yangiHisobKitobJadval() {
        TableView<HisobKitob> tableView = new TableView<>();
        SetHVGrow.VerticalHorizontal(tableView);
        TableViewAndoza tableViewAndoza = new TableViewAndoza();
        tableView.getColumns().addAll(
                tableViewAndoza.getDateTimeColumn(),
                hisob1Hisob2(),
                tableViewAndoza.getIzoh2Column(),
                valutaKurs(),
                tableViewAndoza.getAdadColumn(),
                tableViewAndoza.getNarhColumn(),
                tableViewAndoza.getSummaColumn()
        );
        tableView.setRowFactory(tv -> new TableRow<HisobKitob>() {
            @Override
            protected void updateItem(HisobKitob hisobKitob, boolean empty) {
                super.updateItem(hisobKitob, empty);
                if (hisobKitob == null || hisobKitob.getId() == null)
                    setStyle("");
                else if (hisobKitob.getHisob1().equals(qaydnomaData.getChiqimId()) )
                    setStyle("-fx-background-color: #baffba;");
                else
                    setStyle("-fx-background-color: white;");
            }
        });
        return tableView;
    }

    private ObservableList<HisobKitob> malumotniYangila(QaydnomaData qaydnomaData) {
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        ObservableList<HisobKitob> observableList = FXCollections.observableArrayList();
        observableList = hisobKitobModels.getAnyData(connection, "qaydId = " + qaydnomaData.getId(), "");
        tovarTableView.setItems(observableList);
        tovarTableView.refresh();
        return observableList;
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
                        if (!item.equals(0)) {
                            Text text = new Text(GetDbData.getTovar(item).getText());
                            text.setStyle("-fx-text-alignment:justify;");
                            text.wrappingWidthProperty().bind(getTableColumn().widthProperty().subtract(35));
                            setGraphic(text);
                        }
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

    private TableColumn<HisobKitob, String> getSummaColumn() {
        TableColumn<HisobKitob, String>  summaCol = new TableColumn<>("Jami");
        summaCol.setMinWidth(150);
        summaCol.setMaxWidth(150);
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
    private TableColumn<QaydnomaData, DoubleTextBox> hisoblarUstuni() {
        TableColumn<QaydnomaData, DoubleTextBox> hisoblar = new TableColumn<>("Chiqim/Kirim");
        hisoblar.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<QaydnomaData, DoubleTextBox>, ObservableValue<DoubleTextBox>>() {

            @Override
            public ObservableValue<DoubleTextBox> call(TableColumn.CellDataFeatures<QaydnomaData, DoubleTextBox> param) {
                QaydnomaData qaydnomaData = param.getValue();
                Text text1 = new Text(qaydnomaData.getChiqimNomi());
                text1.setFill(Color.RED);
                Text text2 = new Text(qaydnomaData.getKirimNomi());
                text2.setFill(Color.BLACK);
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

    private TableColumn<HisobKitob, DoubleTextBox> valutaKurs() {
        TableColumn<HisobKitob, DoubleTextBox> valutaKurs = new TableColumn<>("Valyuta/Kurs");
        valutaKurs.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, DoubleTextBox>, ObservableValue<DoubleTextBox>>() {

            @Override
            public ObservableValue<DoubleTextBox> call(TableColumn.CellDataFeatures<HisobKitob, DoubleTextBox> param) {
                DecimalFormat decimalFormat = new MoneyShow();
                HisobKitob hisobKitob = param.getValue();
                Valuta valuta = GetDbData.getValuta(hisobKitob.getValuta());
                Text text1 = new Text(valuta.getValuta());
                text1.setFill(Color.RED);
                Text text2 = new Text(decimalFormat.format(hisobKitob.getKurs()));
                text2.setFill(Color.BLACK);
                DoubleTextBox b = new DoubleTextBox(text1, text2);
                b.setAlignment(Pos.CENTER);
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
        valutaKurs.setMinWidth(100);
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
                text1.setFill(Color.RED);
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
                text1.setFill(Color.RED);
                Text text2 = new Text(hisob2.getText());
                text2.setFill(Color.BLUE);
                DoubleTextBox b = new DoubleTextBox(text1, text2);
                b.setAlignment(Pos.CENTER);
                HBox.setHgrow(text1, Priority.ALWAYS);
                VBox.setVgrow(text1, Priority.ALWAYS);
                HBox.setHgrow(text2, Priority.ALWAYS);
                VBox.setVgrow(text2, Priority.ALWAYS);
                HBox.setHgrow(b, Priority.ALWAYS);
                VBox.setVgrow(b, Priority.ALWAYS);

                return new SimpleObjectProperty<DoubleTextBox>(b);
            }
        });
        hisoblar.setMinWidth(200);

        hisoblar.setStyle( "-fx-alignment: CENTER;");
        return hisoblar;
    }
    public VBox display(QaydnomaData qaydnomaData) {
        malumotniYangila(qaydnomaData);
        return centerPane;
    }
}
