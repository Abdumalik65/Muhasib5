package sample.Controller;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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
import sample.Excel.ExportToExcel;
import sample.Excel.XaridChiptasiExcel;
import sample.Model.HisobKitobModels;
import sample.Model.QaydnomaModel;
import sample.Model.StandartModels;
import sample.Printer.SavdoChiptasiniChopEtish;
import sample.Tools.*;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class XaridlarJadvali extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    VBox rightPane = new VBox();
    VBox centerPane = new VBox();
    TableView<QaydnomaData> xaridlarTableView = new TableView();
    TableView<HisobKitob> tovarTableView = new TableView();
    TableView<HisobKitob> tolovJadvali;
    Tugmachalar tugmachalar = new Tugmachalar();
    HBox taftishHBox = new HBox();
    Button xaridChiptasiButton = new Button("Xarid chiptasi");
    ObservableList<QaydnomaData> qaydnomaDataObservableList;
    ObservableList<HisobKitob> tableObservableList = FXCollections.observableArrayList();
    ContextMenu contextMenu;

    HisobKitobModels hisobKitobModels = new HisobKitobModels();

    Connection connection;
    User user;
    QaydnomaData qaydnomaData;

    DecimalFormat decimalFormat = new MoneyShow();
    int padding = 3;
    int amalTuri = 4;

    Double jamiMablag = 0.0;
    Label jamiMablagLabel = new Label();
    Font font = Font.font("Arial", FontWeight.BOLD,24);

    public static void main(String[] args) {
        launch(args);
    }

    public XaridlarJadvali() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
        ibtido();
    }

    public XaridlarJadvali(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        String classSimpleName = getClass().getSimpleName();
        DasturlarRoyxati.dastur(connection, user, classSimpleName);
        ibtido();
    }

    private void ibtido() {
        initTopPane();
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
        initTovarTable();
        initTugmachalar();
        HBox hBox = yangiJamiPane();
        tolovJadvali = yangiTolovJadvali();
        centerPane.getChildren().addAll(tugmachalar, tovarTableView, tolovJadvali, hBox);
    }

    private void initRightPane() {
        initTaftishHBox();
        initXaridChiptasiTableView();
        initXaridChiptasiButton();
        SetHVGrow.VerticalHorizontal(rightPane);
        rightPane.setMinWidth(281);
        rightPane.getChildren().addAll(taftishHBox, xaridlarTableView);
    }

    private void initBottomPane() {}

    private void initBorderPane() {
        borderpane.setPadding(new Insets(padding));
        SetHVGrow.VerticalHorizontal(borderpane);
        borderpane.setCenter(centerPane);
        borderpane.setRight(rightPane);
    }

    private void initStage(Stage primaryStage) {
        scene = new Scene(borderpane);
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        stage = primaryStage;
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        scene.setUserAgentStylesheet("sample/Styles/caspian.css");
        stage.setTitle("Xaridlar jadvali");
        stage.setScene(scene);
    }

    private HBox yangiJamiPane() {
        HBox hBox = new HBox();
        Pane pane = new Pane();
        Pane pane1 = new Pane();
        jamiMablagLabel.setFont(font);
        HBox.setHgrow(pane, Priority.ALWAYS);
        HBox.setHgrow(pane1, Priority.ALWAYS);
        hBox.getChildren().addAll(pane, jamiMablagLabel, pane1);
        return hBox;
    }

    private void initXaridChiptasiTableView() {
        SetHVGrow.VerticalHorizontal(xaridlarTableView);
        xaridlarTableView.setPadding(new Insets(padding));
        QaydnomaModel qaydnomaModel = new QaydnomaModel();
        if (user.getStatus() == 99) {
            qaydnomaDataObservableList = qaydnomaModel.getAnyData(connection, "amalTuri = " + amalTuri, "sana desc");
        } else {
            qaydnomaDataObservableList = qaydnomaModel.getAnyData(connection, "amalTuri = " + amalTuri + " AND userId = " + user.getId(), "sana desc");
        }
        if (qaydnomaDataObservableList.size()>0) {
            qaydnomaData = qaydnomaDataObservableList.get(0);
            refreshData(qaydnomaData);
            tovarTableView.refresh();
        }
        TableViewAndoza tableViewAndoza = new TableViewAndoza();
        TableColumn<QaydnomaData, DoubleTextBox> hujjatColumn = tableViewAndoza.idHujjatQaydnoma();
        hujjatColumn.setMinWidth(70);
        hujjatColumn.setMaxWidth(70);
        xaridlarTableView.getColumns().addAll(
                tableViewAndoza.getSanaColumn(),
                hujjatColumn,
                tableViewAndoza.hisob1Hisob2Qaydnoma()
        );
        xaridlarTableView.setItems(qaydnomaDataObservableList);

        xaridlarTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                tugmachalar.setDisable(false);
                refreshData(newValue);
                qaydnomaData = newValue;
            } else {
                tugmachalar.setDisable(true);
            }
            tovarTableView.refresh();
        });
    }

    private void initXaridChiptasiButton() {
        xaridChiptasiButton.setMaxWidth(2000);
        xaridChiptasiButton.setPrefWidth(150);
        xaridChiptasiButton.setMaxHeight(60);
        xaridChiptasiButton.setPrefHeight(150);
        xaridChiptasiButton.setFont(font);
        SetHVGrow.VerticalHorizontal(xaridChiptasiButton);
        xaridChiptasiButton.setOnAction(event -> {
            QaydnomaData qaydnomaData1 = xaridlarTableView.getSelectionModel().getSelectedItem();
            ObservableList<HisobKitob> hkList = tovarTableView.getItems();
            if (qaydnomaData1 != null) {
                if (hkList != null) {
                    ObservableList<HisobKitob> hisobKitobObservableList = hisobKitobModels.getAnyData(connection, "qaydId = " + qaydnomaData.getId(), "amal asc");
                    SavdoChiptasiniChopEtish xaridChiptasi = new SavdoChiptasiniChopEtish(user, qaydnomaData1, hisobKitobObservableList);
                    String printerNomi = xaridChiptasi.printerim().toLowerCase();
                    if (printerNomi.contains("POS-58".toLowerCase())) {
                        xaridChiptasi.tolovChiptasiniBerPos58(printerNomi);
                    } else if (printerNomi.contains("XP-80C".toLowerCase())) {
                        xaridChiptasi.tolovChiptasiniBerXP80(printerNomi);
                    }
                }
            }
        });

    }

    private void initTaftishHBox() {
        HBox.setHgrow(taftishHBox, Priority.ALWAYS);
//        taftishHBox.setPadding(new Insets(padding));
        ObservableList<Standart> observableList = FXCollections.observableArrayList(
                new Standart(1, "Sana", null, null),
                new Standart(2, "Hujjat", null, null),
                new Standart(3, "Kirim hisobi", null, null),
                new Standart(4, "Tovar qidir", null, null)
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

    private void Taftish(String oldValue, String newValue, Integer option) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        ObservableList<QaydnomaData> subentries = FXCollections.observableArrayList();

        newValue = newValue.toLowerCase();

        if ( oldValue != null && (newValue.length() < oldValue.length()) ) {
            xaridlarTableView.setItems(qaydnomaDataObservableList);
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
                    if (qayd.getKirimNomi().toLowerCase().contains(newValue)) {
                        subentries.add(qayd);
                    }
                    break;
                case 3:

                    break;
            }
        }
        xaridlarTableView.setItems(subentries);
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

    private void initTovarTable() {
        SetHVGrow.VerticalHorizontal(tovarTableView);
        tovarTableView.setPadding(new Insets(padding));
        tovarTableView.getColumns().addAll(
                getAmalColumn(),
                getTovarColumn(),
                getTaqdimColumn(),
                getAdadColumn(),
                getNarhColumn(),
                getSummaColumn()
        );
        tovarTableView.setItems(tableObservableList);
        initContextMenu();
        tovarTableView.setContextMenu(contextMenu);
    }

    private void initTugmachalar() {
        tugmachalar.setDisable(true);
        tugmachalar.getChildren().removeAll(tugmachalar.getAdd(), tugmachalar.getEdit());

        Button chiptaButton = new Button();
        chiptaButton.setText("Xarid chiptasi");
        tugmachalar.getChildren().add(0, chiptaButton);
        chiptaButton.setOnAction(event -> {
            QaydnomaData qaydnomaData = xaridlarTableView.getSelectionModel().getSelectedItem();
            if (qaydnomaData != null) {
                ObservableList<HisobKitob> hisobKitobObservableList = hisobKitobModels.getAnyData(connection, "qaydId = " + qaydnomaData.getId(), "amal asc");
                SavdoChiptasiniChopEtish xaridChiptasi = new SavdoChiptasiniChopEtish(user, qaydnomaData, hisobKitobObservableList);
                String printerNomi = xaridChiptasi.printerim().toLowerCase();
                if (printerNomi.contains("POS-58".toLowerCase())) {
                    xaridChiptasi.tolovChiptasiniBerPos58(printerNomi);
                } else if (printerNomi.contains("XP-80C".toLowerCase())) {
                    xaridChiptasi.tolovChiptasiniBerXP80(printerNomi);
                }
            }
        });

        Button deleteButton = tugmachalar.getDelete();
        deleteButton.setOnAction(event -> {
            qaydnomaData = xaridlarTableView.getSelectionModel().getSelectedItem();
            if (Alerts.xaridniOchir(qaydnomaData)) {
                xaridniOchir();
            }
        });

        Button excelButton = tugmachalar.getExcel();
        excelButton.setOnAction(event -> {
            QaydnomaData qaydnomaData = xaridlarTableView.getSelectionModel().getSelectedItem();
            if (qaydnomaData != null) {
                XaridChiptasiExcel xaridChiptasiExcel = new XaridChiptasiExcel(connection, user);
                User user1 = GetDbData.getUser(qaydnomaData.getUserId());
                user1.setTovarHisobi(user.getTovarHisobi());

                xaridChiptasiExcel.savdoChiptasi(qaydnomaData, user1);
            }
        });
    }

    private void xaridniOchir() {
        QaydnomaModel qaydnomaModel = new QaydnomaModel();
        qaydnomaData = xaridlarTableView.getSelectionModel().getSelectedItem();
        int qaydId = qaydnomaData.getId();
        hisobKitobModels.deleteWhere(connection, "qaydId = " + qaydId);
        tableObservableList.removeAll(tableObservableList);
        qaydnomaModel.delete_data(connection, qaydnomaData);
        qaydnomaDataObservableList.remove(qaydnomaData);
        tovarTableView.refresh();
        xaridlarTableView.refresh();
        System.out.println("Xaridni o`chir");
    }

    private TableColumn<HisobKitob, Integer> getAmalColumn() {
        TableColumn<HisobKitob, Integer>  amalColumn = new TableColumn<>("Amal");
        amalColumn.setMinWidth(50);
        amalColumn.setMaxWidth(50);
        amalColumn.setCellValueFactory(new PropertyValueFactory<>("amal"));

        amalColumn.setCellFactory(column -> {
            TableCell<HisobKitob, Integer> cell = new TableCell<HisobKitob, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        ImageView imageView = null;
                        if (item == 4) {
                            imageView = new PathToImageView("/sample/images/Icons/chiqim.png", 32, 32).getImageView();
                        } else {
                            imageView = new PathToImageView("/sample/images/Icons/kirim.png", 32, 32).getImageView();

                        }
                        setGraphic(imageView);
                    }
                    setAlignment(Pos.CENTER_LEFT);
                }
            };
            return cell;
        });
        return amalColumn;
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
    private TableColumn<HisobKitob, Double> getAdadColumn() {
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
                String birlikString = "??? " + hisobKitob.getBarCode() + " ??";
                if (barCode != null) {
                    Standart birlikStandart = GetDbData.getBirlik(barCode.getBirlik());
                    birlikString = birlikStandart.getText();
                }
                return new SimpleObjectProperty<>(birlikString);
            }
        });
        taqdimColumn.setStyle( "-fx-alignment: CENTER;");
        taqdimColumn.setMinWidth(120);
        return taqdimColumn;
    }
    private TableColumn<HisobKitob, DoubleTextBox> narhKurs() {
        TableColumn<HisobKitob, DoubleTextBox> valutaKurs = new TableColumn<>("Narh/Kurs");
        valutaKurs.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, DoubleTextBox>, ObservableValue<DoubleTextBox>>() {

            @Override
            public ObservableValue<DoubleTextBox> call(TableColumn.CellDataFeatures<HisobKitob, DoubleTextBox> param) {
                DecimalFormat decimalFormat = new MoneyShow();
                HisobKitob hisobKitob = param.getValue();
                Double narhKurs = hisobKitob.getTovar() > 0 ? hisobKitob.getDona()*hisobKitob.getNarh() : hisobKitob.getNarh();
                Text text1 = new Text(decimalFormat.format(narhKurs));
                Text text2 = new Text(decimalFormat.format(hisobKitob.getKurs()));
                DoubleTextBox b = new DoubleTextBox(text1, text2);
                b.setAlignment(Pos.CENTER);
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
                text1.setStyle("-fx-text-alignment:justify;");
                text1.wrappingWidthProperty().bind(param.getTableColumn().widthProperty().subtract(2));
                Text text2 = new Text(hisob2.getText());
                text2.setFill(Color.BLUE);
                text2.setStyle("-fx-text-alignment:justify;");
                text2.wrappingWidthProperty().bind(param.getTableColumn().widthProperty().subtract(2));
                DoubleTextBox b = new DoubleTextBox(text1, text2);
                HBox.setHgrow(text1, Priority.ALWAYS);
                VBox.setVgrow(text1, Priority.ALWAYS);
                HBox.setHgrow(text2, Priority.ALWAYS);
                VBox.setVgrow(text2, Priority.ALWAYS);
                HBox.setHgrow(b, Priority.ALWAYS);
                VBox.setVgrow(b, Priority.ALWAYS);

                return new SimpleObjectProperty<DoubleTextBox>(b);
            }
        });

        hisoblar.setMinWidth(150);
        hisoblar.setStyle( "-fx-alignment: CENTER;");
        return hisoblar;
    }

    private Double jami(ObservableList<HisobKitob> observableList) {
        Double jami = 0d;
        for (HisobKitob hk: observableList) {
            if (hk.getAmal().equals(4)) {
                if (hk.getTovar() == 0) {
                    jami += hk.getNarh();
                } else {
                    jami += hk.getDona() * hk.getNarh();
                }
            } else if (hk.getAmal().equals(2)) {
                if (hk.getTovar() == 0) {
                    jami -= hk.getNarh();
                } else {
                    jami -= hk.getDona() * hk.getNarh();
                }
            }
        }
        return jami;
    }

    private TableView<HisobKitob> yangiTolovJadvali() {
        TableView<HisobKitob> tableView = new TableView<>();
        SetHVGrow.VerticalHorizontal(tableView);
        TableViewAndoza tableViewAndoza = new TableViewAndoza();
        TableColumn<HisobKitob, Integer> amalColumn = tableViewAndoza.getAmalColumn();
        TableColumn<HisobKitob, Double> summaColumn = tableViewAndoza.getSummaColumn();
        TableColumn<HisobKitob, Double> balanceColumn = tableViewAndoza.getBalans2Column();
        amalColumn.setStyle( "-fx-alignment: CENTER;");
        TableColumn<HisobKitob, Integer> valutaColumn = tableViewAndoza.getValutaColumn();
        valutaColumn.setMinWidth(60);
        summaColumn.setMinWidth(100);
        balanceColumn.setMinWidth(100);
        valutaColumn.setStyle( "-fx-alignment: CENTER;");

        tableView.getColumns().addAll(
                amalColumn,
                hisob1Hisob2(),
                valutaColumn,
                narhKurs(),
                summaColumn,
                balanceColumn
        );

        return tableView;
    }

    private void refreshData(QaydnomaData qaydnomaData) {

        ObservableList<HisobKitob> hisobKitobObservableList = hisobKitobModels.getAnyData(connection, "qaydId = " + qaydnomaData.getId(), "amal asc");
        ObservableList<HisobKitob> tovarlar = tovarlar(hisobKitobObservableList, qaydnomaData);
        ObservableList<HisobKitob> sotibOlindi = sotibOlindi(hisobKitobObservableList, qaydnomaData);
        tovarlar.addAll(sotibOlindi);
        ObservableList<HisobKitob> tolovlar = FXCollections.observableArrayList();
        if (tovarlar.size()>0) {
            Integer valutaId = tovarlar.get(0).getValuta();
            Double kurs = tovarlar.get(0).getKurs();
            Double jamiMablag = jami(tovarlar);
            HisobKitob hisobKitob = new HisobKitob(
                    0, qaydnomaData.getId(), qaydnomaData.getHujjat(), 4, qaydnomaData.getChiqimId(), qaydnomaData.getKirimId(), valutaId,
                    0, kurs, "", 1d, jamiMablag, 0, "", 1, null
            );
            hisobKitob.setBalans(jamiMablag/kurs);
            tolovlar.add(0, hisobKitob);
            tovarTableView.setItems(tovarlar);
            tovarTableView.refresh();
            ObservableList<HisobKitob> tolov = amallar(hisobKitobObservableList, 7);
            if (tolov.size()>0)
                tolovlar.addAll(tolov);
            ObservableList<HisobKitob> plastic = amallar(hisobKitobObservableList, 15);
            if (plastic.size()>0)
                tolovlar.addAll(plastic);

            ObservableList<HisobKitob> qaytim = amallar(hisobKitobObservableList, 8);
            if (qaytim.size()>0)
                tolovlar.addAll(qaytim);
            ObservableList<HisobKitob> chegirmalar = amallar(hisobKitobObservableList, 13);
            if (chegirmalar.size()>0)
                tolovlar.addAll(chegirmalar);
            ObservableList<HisobKitob> qoshimchaDaromadlar = amallar(hisobKitobObservableList, 18);
            if (qoshimchaDaromadlar.size()>0)
                tolovlar.addAll(qoshimchaDaromadlar);
            Double balansHisobi = balansHisobi(tolovlar);
            balansHisobi = StringNumberUtils.yaxlitla(balansHisobi, -2);
            jamiMablagLabel.setText(decimalFormat.format(balansHisobi));
        }
        tovarTableView.setItems(tovarlar);
        tovarTableView.refresh();

        tolovJadvali.setItems(tolovlar);
        tolovJadvali.refresh();
    }

    private Double balansHisobi(ObservableList<HisobKitob> observableList) {
        Double balance = 0d;
        if (observableList.size()>0) {
            for (HisobKitob hisobKitob: observableList) {
                switch (hisobKitob.getAmal()) {
                    case 2:
                        balance += hisobKitob.getSummaCol();
                        break;
                    case 4:
                        balance -= hisobKitob.getSummaCol();
                        break;
                    case 7: //tolov
                        balance += hisobKitob.getSummaCol();
                        break;
                    case 8: //qaytim
                        balance -= hisobKitob.getSummaCol();
                        break;
                    case 13: // chegirma
                        balance += hisobKitob.getSummaCol();
                        break;
                    case 15: //bank tolovi
                        balance += hisobKitob.getSummaCol();
                        break;
                    case 18: //qoshimcha daromad
                        balance -= hisobKitob.getSummaCol();
                        break;
                }
                hisobKitob.setBalans(balance);
            }
        }
        return balance;
    }

    private  ObservableList<HisobKitob> tovarlar(ObservableList<HisobKitob> hisobKitobObservableList, QaydnomaData qaydnomaData) {
        ObservableList<HisobKitob> observableList = FXCollections.observableArrayList();
        for (HisobKitob hisobKitob: hisobKitobObservableList) {
            if (hisobKitob.getTovar()>0 && hisobKitob.getHisob2().equals(qaydnomaData.getKirimId())) {
                observableList.add(hisobKitob);
            }
        }
        return observableList;
    }

    private  ObservableList<HisobKitob> sotibOlindi(ObservableList<HisobKitob> hisobKitobObservableList, QaydnomaData qaydnomaData) {
        ObservableList<HisobKitob> observableList = FXCollections.observableArrayList();
        for (HisobKitob hisobKitob: hisobKitobObservableList) {
            if (hisobKitob.getTovar()>0 && hisobKitob.getAmal().equals(2) && hisobKitob.getHisob2().equals(qaydnomaData.getChiqimId()) && hisobKitob.getHisob1().equals(qaydnomaData.getKirimId())) {
                observableList.add(hisobKitob);
            }
        }
        return observableList;
    }

    private  ObservableList<HisobKitob> amallar(ObservableList<HisobKitob> hisobKitobObservableList, Integer amal) {
        ObservableList<HisobKitob> observableList = FXCollections.observableArrayList();
        for (HisobKitob hisobKitob: hisobKitobObservableList) {
            if (hisobKitob.getAmal().equals(amal)) {
                observableList.add(hisobKitob);
            }
        }
        return observableList;
    }

    private void initContextMenu() {
        contextMenu = new ContextMenu();
        ImageView imageView = new PathToImageView("/sample/images/Icons/add.png").getImageView();
        MenuItem addMenu = new MenuItem("Qo`sh", imageView);

        imageView = new PathToImageView("/sample/images/Icons/delete.png").getImageView();
        MenuItem deleteMenu = new MenuItem("O`chir", imageView);

        imageView = new PathToImageView("/sample/images/Icons/edit.png").getImageView();
        MenuItem editMenu = new MenuItem("O`zgartir", imageView);

        imageView = new PathToImageView("/sample/images/Icons/loan.png", 24,24).getImageView();
        MenuItem tolovMenu = new MenuItem("To`lov", imageView);


        contextMenu.getItems().add(addMenu);
        contextMenu.getItems().add(deleteMenu);
        contextMenu.getItems().add(tolovMenu);

        addMenu.setOnAction(event -> {
            QaydnomaData qaydnomaData = xaridlarTableView.getSelectionModel().getSelectedItem();
            if (qaydnomaData != null) {
                HisobKitob hisobKitob = null;
                TovarEditor tovarEditor = new TovarEditor(connection, user, qaydnomaData, hisobKitob);
                tovarEditor.add();
                refreshData(qaydnomaData);
                tovarTableView.refresh();
            } else {
                Alerts.AlertString("Qator tanlanmadi");
            }
        });

        deleteMenu.setOnAction(event -> {
            QaydnomaData qaydnomaData = xaridlarTableView.getSelectionModel().getSelectedItem();
            if (qaydnomaData != null) {
                HisobKitob hisobKitob = tovarTableView.getSelectionModel().getSelectedItem();
                if (hisobKitob != null) {
                    TovarEditor tovarEditor = new TovarEditor(connection, user, qaydnomaData, hisobKitob);
                    tovarEditor.delete(hisobKitob);
                    refreshData(qaydnomaData);
                    tovarTableView.refresh();
                } else {
                    Alerts.AlertString("Qator tanlanmadi");
                }
            }
        });

        editMenu.setOnAction(event -> {
            QaydnomaData qaydnomaData = xaridlarTableView.getSelectionModel().getSelectedItem();
            if (qaydnomaData != null) {
                HisobKitob hisobKitob = tovarTableView.getSelectionModel().getSelectedItem();
                if (hisobKitob != null) {
                    TovarEditor tovarEditor = new TovarEditor(connection, user, qaydnomaData, hisobKitob);
                    tovarEditor.edit(hisobKitob);
                    refreshData(qaydnomaData);
                    tovarTableView.refresh();
                } else {
                    Alerts.AlertString("Qator tanlanmadi");
                }
            }
        });

        tolovMenu.setOnAction(event -> {
            QaydnomaData qaydnomaData = xaridlarTableView.getSelectionModel().getSelectedItem();
            if (qaydnomaData != null) {
                TovarEditor tovarEditor = new TovarEditor(connection, user, qaydnomaData);
                tovarEditor.tolov();
                refreshData(qaydnomaData);
                tovarTableView.refresh();
            }
        });
    }

    public VBox display(QaydnomaData qaydnomaData) {
        this.qaydnomaData = qaydnomaData;
        refreshData(qaydnomaData);
        return centerPane;
    }

}
