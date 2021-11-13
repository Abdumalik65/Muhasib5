package sample.Controller;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import sample.Config.MySqlDBGeneral;
import sample.Config.MySqlDBGeneral;
import sample.Config.SqliteDB;
import sample.Config.SqliteDBPrinters;
import sample.Data.*;
import sample.Enums.ServerType;
import sample.Model.HisobKitobModels;
import sample.Model.QaydnomaModel;
import sample.Model.StandartModels;
import sample.Tools.*;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

public class XaridlarJadvali extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    GridPane gridPane = new GridPane();
    GridPane gridPane1 = new GridPane();
    VBox rightPane = new VBox();
    VBox centerPane = new VBox();
    TableView<QaydnomaData> xaridlarTableView = new TableView();
    TableView<HisobKitob> tovarTableView = new TableView();
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
    Double kassagaDouble = 0.0;
    Double chegirmaDouble = 0.0;
    Double naqdUsdDouble = 0.0;
    Double naqdMilliyDouble = 0.0;
    Double plasticDouble = 0.0;
    Double balansDouble = 0.0;
    Double qaytimDouble = 0.0;

    Label jamiMablagLabel = new Label();
    Label chegirmaLabel = new Label();
    Label kassagaLabel = new Label();
    Label naqdUSDLabel = new Label();
    Label naqdMilliyLabel = new Label();
    Label plasticLabel = new Label();
    Label balansLabel = new Label();
    Label qaytimLabel = new Label();
    Font font = Font.font("Arial",20);

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
        ibtido();
    }

    private void ibtido() {
        initTopPane();
        initLeftPane();
        initCenterPane();
        initRightPane();
        initBorderPane();
        initBottomPane();
        initLabelFont();
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
        initGridPane();
    }

    private void initCenterPane() {
        SetHVGrow.VerticalHorizontal(centerPane);
        centerPane.setPadding(new Insets(padding));
        centerPane.getChildren().addAll(tugmachalar, tovarTableView, gridPane1);
    }

    private void initRightPane() {
        initTaftishHBox();
        initGridPane2();
        initXaridChiptasiTableView();
        initXaridChiptasiButton();
        SetHVGrow.VerticalHorizontal(rightPane);
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
        scene = new Scene(borderpane, 1135, 700);
        scene.setUserAgentStylesheet("sample/Styles/caspian.css");
        stage = primaryStage;
        stage.setTitle("Xaridlar jadvali");
        stage.setScene(scene);
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
        GetTableView2 getTableView2 = new GetTableView2();
        TableColumn<QaydnomaData, Integer> hujjatColumn = getTableView2.getHujjatColumn();
        hujjatColumn.setMinWidth(73);
        xaridlarTableView.getColumns().addAll(
                getTableView2.getSanaColumn(),
                hujjatColumn,
                getTableView2.getKirimNomiColumn()
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
                    String printerNomi = printerim().toLowerCase();
                    if (printerNomi.contains("POS-58".toLowerCase())) {
                        tolovChiptasiniBer("POS-58");
                    } else if (printerNomi.contains("XP-80C".toLowerCase())) {
                        tolovChiptasiniBerXP80("XP-80C");
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

    private void initTovarTable() {
        SetHVGrow.VerticalHorizontal(tovarTableView);
        tovarTableView.setPadding(new Insets(padding));
        tovarTableView.getColumns().addAll(
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
            String printerNomi = printerim().toLowerCase();
            if (printerNomi.contains("POS-58".toLowerCase())) {
                tolovChiptasiniBer(printerNomi);
            } else if (printerNomi.contains("XP-80C".toLowerCase())) {
                tolovChiptasiniBerXP80(printerNomi);
            }
        });

        Button deleteButton = tugmachalar.getDelete();
        deleteButton.setOnAction(event -> {
            if (Alerts.xaridniOchir(qaydnomaData)) {
                xaridniOchir();
            }
        });

        Button excelButton = tugmachalar.getExcel();
        excelButton.setOnAction(event -> {
            String printerim = printerim();
            ExportToExcel exportToExcel = new ExportToExcel(printerim);
            User user1 = GetDbData.getUser(qaydnomaData.getUserId());
            user1.setTovarHisobi(user.getTovarHisobi());
            exportToExcel.savdoChiptasi(qaydnomaData, connection, user1);
        });
    }

    private void xaridniOchir() {
        QaydnomaModel qaydnomaModel = new QaydnomaModel();
        int qaydId = qaydnomaData.getId();
        hisobKitobModels.deleteWhere(connection, "qaydId = " + qaydId);
        tableObservableList.removeAll(tableObservableList);
        qaydnomaModel.delete_data(connection, qaydnomaData);
        qaydnomaDataObservableList.remove(qaydnomaData);
        tovarTableView.refresh();
        xaridlarTableView.refresh();
        System.out.println("Xaridni o`chir");
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
            jamiHisob(tableObservableList);
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
            jamiHisob(event.getTableView().getItems());
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

    private void jamiHisob(ObservableList<HisobKitob> hisobKitobViewObservableList) {
        jamiMablag = 0.0;
        for (HisobKitob hk : hisobKitobViewObservableList) {
            jamiMablag += hk.getDona() * hk.getNarh();
        }
        jamiMablagLabel.setText(decimalFormat.format(jamiMablag));
        kassagaDouble = jamiMablag - chegirmaDouble;
        kassagaLabel.setText(decimalFormat.format(kassagaDouble));
        Double aDouble = (naqdUsdDouble + plasticDouble) - (jamiMablag - chegirmaDouble);
        if (aDouble > 0) {
            qaytimDouble = (aDouble);
        } else {
            qaytimDouble = 0.0;
        }
        qaytimLabel.setText(decimalFormat.format(qaytimDouble));
        balansDouble = (naqdUsdDouble + plasticDouble - qaytimDouble) - (jamiMablag - chegirmaDouble);
        balansLabel.setText(decimalFormat.format(balansDouble));
    }

    private void initGridPane() {
        initTugmachalar();
        initTovarTable();
        SetHVGrow.VerticalHorizontal(gridPane);
        gridPane.setPadding(new Insets(padding));

        int rowIndex = 0;
        gridPane.add(tovarTableView, 0, rowIndex, 1, 1);
        GridPane.setHgrow(tovarTableView, Priority.ALWAYS);
        GridPane.setVgrow(tovarTableView, Priority.ALWAYS);
    }

    private void initGridPane2() {
        HBox.setHgrow(gridPane1, Priority.ALWAYS);
        VBox.setVgrow(gridPane1, Priority.NEVER);
        gridPane1.setGridLinesVisible(true);

        int rowIndex = 0;
        Label label1 = new Label("Xarid narhi");
        label1.setFont(font);
        HBox.setHgrow(label1, Priority.ALWAYS);
        GridPane.setHgrow(label1, Priority.ALWAYS);
        HBox.setHgrow(jamiMablagLabel, Priority.ALWAYS);
        GridPane.setHgrow(jamiMablagLabel, Priority.ALWAYS);
        gridPane1.add(label1, 0, rowIndex, 1, 1);
        gridPane1.add(jamiMablagLabel, 1, rowIndex, 1,1);
        GridPane.setHalignment(jamiMablagLabel, HPos.RIGHT);

        rowIndex++;
        Label label2 = new Label("Chegirma");
        label2.setFont(font);
        HBox.setHgrow(label2, Priority.ALWAYS);
        GridPane.setHgrow(label2, Priority.ALWAYS);
        HBox.setHgrow(chegirmaLabel, Priority.ALWAYS);
        GridPane.setHgrow(chegirmaLabel, Priority.ALWAYS);
        gridPane1.add(label2, 0, rowIndex, 1, 1);
        gridPane1.add(chegirmaLabel,1, rowIndex,1, 1);
        GridPane.setHalignment(chegirmaLabel, HPos.RIGHT);

        rowIndex++;
        Label label3 = new Label("Kassaga");
        label3.setFont(font);
        HBox.setHgrow(label3, Priority.ALWAYS);
        GridPane.setHgrow(label3, Priority.ALWAYS);
        HBox.setHgrow(kassagaLabel, Priority.ALWAYS);
        GridPane.setHgrow(kassagaLabel, Priority.ALWAYS);
        gridPane1.add(label3, 0, rowIndex, 1, 1);
        gridPane1.add(kassagaLabel,1, rowIndex,1, 1);
        GridPane.setHalignment(kassagaLabel, HPos.RIGHT);

        rowIndex++;
        Valuta valutaUSD = GetDbData.getValuta(1);
        Label label4 = new Label("Naqd " + valutaUSD.getValuta());
        label4.setFont(font);
        HBox.setHgrow(label4, Priority.ALWAYS);
        GridPane.setHgrow(label4, Priority.ALWAYS);
        HBox.setHgrow(naqdUSDLabel, Priority.ALWAYS);
        GridPane.setHgrow(naqdUSDLabel, Priority.ALWAYS);
        gridPane1.add(label4, 0, rowIndex, 1, 1);
        gridPane1.add(naqdUSDLabel, 1, rowIndex, 1,1);
        GridPane.setHalignment(naqdUSDLabel, HPos.RIGHT);

        rowIndex++;
        Valuta valutaMilliy = GetDbData.getValuta(2);
        Label label4b = new Label("Naqd " + valutaMilliy.getValuta());
        label4b.setFont(font);
        HBox.setHgrow(label4b, Priority.ALWAYS);
        GridPane.setHgrow(label4b, Priority.ALWAYS);
        HBox.setHgrow(naqdMilliyLabel, Priority.ALWAYS);
        GridPane.setHgrow(naqdMilliyLabel, Priority.ALWAYS);
        gridPane1.add(label4b, 0, rowIndex, 1, 1);
        gridPane1.add(naqdMilliyLabel, 1, rowIndex, 1,1);
        GridPane.setHalignment(naqdMilliyLabel, HPos.RIGHT);

        rowIndex++;
        Label label5 = new Label("Plastik");
        label5.setFont(font);
        HBox.setHgrow(label5, Priority.ALWAYS);
        GridPane.setHgrow(label5, Priority.ALWAYS);
        HBox.setHgrow(plasticLabel, Priority.ALWAYS);
        GridPane.setHgrow(plasticLabel, Priority.ALWAYS);
        gridPane1.add(label5, 0, rowIndex, 1, 1);
        gridPane1.add(plasticLabel,1, rowIndex,1, 1);
        GridPane.setHalignment(plasticLabel, HPos.RIGHT);

        rowIndex++;
        Label label6 = new Label("Qaytim");
        label6.setFont(font);
        HBox.setHgrow(label6, Priority.ALWAYS);
        GridPane.setHgrow(label6, Priority.ALWAYS);
        HBox.setHgrow(qaytimLabel, Priority.ALWAYS);
        GridPane.setHgrow(qaytimLabel, Priority.ALWAYS);
        gridPane1.add(label6, 0, rowIndex, 1, 1);
        gridPane1.add(qaytimLabel,1, rowIndex,1, 1);
        GridPane.setHalignment(qaytimLabel, HPos.RIGHT);

        rowIndex++;
        Label label7 = new Label("Balans");
        label7.setFont(font);
        HBox.setHgrow(label7, Priority.ALWAYS);
        GridPane.setHgrow(label7, Priority.ALWAYS);
        HBox.setHgrow(balansLabel, Priority.ALWAYS);
        GridPane.setHgrow(balansLabel, Priority.ALWAYS);
        gridPane1.add(label7, 0, rowIndex, 1, 1);
        gridPane1.add(balansLabel,1, rowIndex,1, 1);
        GridPane.setHalignment(balansLabel, HPos.RIGHT);
    }

    private void refreshData(QaydnomaData qaydnomaData) {
        ObservableList<HisobKitob> hisobKitobObservableList = hisobKitobModels.getAnyData(
                connection, "qaydId = " + qaydnomaData.getId() + " AND " + "hujjatId = " + qaydnomaData.getHujjat(), "");
        tableObservableList.removeAll(tableObservableList);
        jamiMablag = 0.0;
        chegirmaDouble = 0.0;
        naqdUsdDouble = 0.0;
        naqdMilliyDouble = 0.0;
        plasticDouble = 0.0;
        qaytimDouble = 0.0;
        balansDouble = 0.0;
        boolean valutaniOldim = false;
        Valuta joriyValuta = null;
        Double joriyValutaKurs = 1d;
        initLabels();
        for (HisobKitob hk: hisobKitobObservableList) {
            if (!valutaniOldim) {
                if (hk.getHisob2().equals(qaydnomaData.getKirimId()) && hk.getTovar()>0) {
                    joriyValuta = GetDbData.getValuta(hk.getValuta());
                    joriyValutaKurs = hk.getKurs();
                    valutaniOldim = true;
                    setLabelToGrid(gridPane1, 1, "Xarid narhi " + joriyValuta.getValuta());
                    setLabelToGrid(gridPane1, 3, "Chergirma " + joriyValuta.getValuta());
                    setLabelToGrid(gridPane1, 5, "Kassaga " + joriyValuta.getValuta());
                    setLabelToGrid(gridPane1, 13, "Qaytim " + joriyValuta.getValuta());
                    setLabelToGrid(gridPane1, 15, "Balans " + joriyValuta.getValuta());
                }
            }
            switch (hk.getAmal()) {
                case 4: //Tovar
                    if (hk.getHisob2().equals(qaydnomaData.getKirimId())) {
                        tableObservableList.add(hk);
                        jamiMablag += hk.getNarh() * hk.getDona();
                    }
                    break;
                case 7: //To`lov naqd
                    if (hk.getValuta().equals(1)) {
                        if (joriyValuta.getId().equals(1)) {
                            naqdUsdDouble = hk.getNarh();
                        } else if (joriyValuta.getId().equals(2)) {
                            naqdUsdDouble = hk.getNarh() * joriyValutaKurs;
                            Valuta v = GetDbData.getValuta(hk.getValuta());
                            String labelText = "Naqd " + hk.getNarh() + " " + v.getValuta() + " * " + joriyValutaKurs + " " + joriyValuta.getValuta();
                            setLabelToGrid(gridPane1, 7, labelText);
                        }
                    } else if (hk.getValuta().equals(2)) {
                        if (joriyValuta.getId().equals(1)) {
                            naqdMilliyDouble = hk.getNarh() / hk.getKurs();
                            Valuta v = GetDbData.getValuta(hk.getValuta());
                            String labelText = "Naqd " + hk.getNarh() + " " + v.getValuta() + " / " + hk.getKurs() + " " + v.getValuta();
                            setLabelToGrid(gridPane1, 9, labelText);
                        } else if (joriyValuta.getId().equals(2)) {
                            naqdMilliyDouble = hk.getNarh();
                        }
                    }
                    break;
                case 8: //Qaytim
                    qaytimDouble = hk.getNarh();
                    setLabelToGrid(gridPane1, 13, "Qaytim " + joriyValuta.getValuta());
                    break;
                case 13:    //Chegirma
                    chegirmaDouble = hk.getNarh();
                    setLabelToGrid(gridPane1, 3, "Chegirma " + joriyValuta.getValuta());
                    break;
                case 15:    //To`lov plastic
                    if (joriyValuta.getId().equals(1)) {
                        Valuta vm = GetDbData.getValuta(2);
                        plasticDouble = hk.getNarh() / hk.getKurs();
                        setLabelToGrid(gridPane1, 11, "Plastic " + joriyValuta.getValuta() + " " + decimalFormat.format(hk.getNarh()) + " " + vm.getValuta() + "/" + decimalFormat.format(hk.getKurs()));
                    } else if (joriyValuta.getId().equals(2)) {
                        plasticDouble = hk.getNarh();
                    }
                    break;
            }
        }
        kassagaDouble = jamiMablag - chegirmaDouble;
        double tolovDouble = naqdUsdDouble + naqdMilliyDouble + plasticDouble;
        balansDouble = tolovDouble - kassagaDouble - qaytimDouble;

        jamiMablagLabel.setText(decimalFormat.format(jamiMablag));
        chegirmaLabel.setText(decimalFormat.format(chegirmaDouble));
        kassagaLabel.setText(decimalFormat.format(kassagaDouble));
        naqdUSDLabel.setText(decimalFormat.format(naqdUsdDouble));
        naqdMilliyLabel.setText(decimalFormat.format(naqdMilliyDouble));
        plasticLabel.setText(decimalFormat.format(plasticDouble));
        qaytimLabel.setText(decimalFormat.format(qaytimDouble));
        balansLabel.setText(decimalFormat.format(balansDouble));
    }

    private void initLabelFont() {
        jamiMablagLabel.setFont(font);
        chegirmaLabel.setFont(font);
        kassagaLabel.setFont(font);
        naqdUSDLabel.setFont(font);
        naqdMilliyLabel.setFont(font);
        plasticLabel.setFont(font);
        qaytimLabel.setFont(font);
        balansLabel.setFont(font);
    }

    private void initLabels() {
        Valuta v1 = GetDbData.getValuta(1);
        Valuta v2 = GetDbData.getValuta(2);
        Label labelXaridNarhi = getLabelFromGrid(gridPane1, 1);
        labelXaridNarhi.setText("Xarid narhi");
        Label labelChrgirma = getLabelFromGrid(gridPane1, 3);
        labelChrgirma.setText("Chegirma");
        Label labelKassaga = getLabelFromGrid(gridPane1, 5);
        labelKassaga.setText("Kassaga");
        Label labelNaqdUSD = getLabelFromGrid(gridPane1, 7);
        labelNaqdUSD.setText("Naqd " + v1.getValuta());
        Label labelNaqdMilliy = getLabelFromGrid(gridPane1, 9);
        labelNaqdMilliy.setText("Naqd " + v2.getValuta());
        Label labelPlastic = getLabelFromGrid(gridPane1, 11);
        labelPlastic.setText("Plastic " + v2.getValuta());
        Label labelQaytim = getLabelFromGrid(gridPane1, 13);
        labelQaytim.setText("Qaytim");
        Label labelBalance = getLabelFromGrid(gridPane1, 15);
        labelBalance.setText("Balans");
    }

    public static Label getLabelFromGrid(GridPane gridPane, Integer labelId) {
        Label label = null;
        label = (Label) gridPane.getChildren().get(labelId);
        return label;
    }

    public static void setLabelToGrid(GridPane gridPane, Integer labelId, String labelText) {
        Label label = getLabelFromGrid(gridPane, labelId);
        label.setText(labelText);
    }

    private void tolovChiptasiniBer(String printerNomi) {
        StringBuffer printStringBuffer = new StringBuffer();
        String lineB = String.format("%.50s\n", "--------------------------------");
        SimpleDateFormat sana = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat vaqt = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        String sanaString = sana.format(date);
        String vaqtString = vaqt.format(date);
        printStringBuffer.append(lineB);
        String shirkatNomi = GetDbData.getHisob(user.getTovarHisobi()).getText();
        printStringBuffer.append(String.format("%23s\n", shirkatNomi));
        printStringBuffer.append(lineB);
        printStringBuffer.append(String.format("%-15s %16s\n", "Telefon", user.getPhone()));
        String lineT = String.format("%32s\n", user.getPhone());
        printStringBuffer.append(lineT);
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
        for (HisobKitob hk: tableObservableList) {
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

        if (naqdUsdDouble > 0) {
            String line = String.format("%-15s %5s %10s\n", "Naqd", " ", decimalFormat.format(naqdUsdDouble));
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

        printStringBuffer.append(String.format("%29" +
                "s\n", "XARIDINGIZ UCHUN TASHAKKUR"));
        printStringBuffer.append(lineB);
        printStringBuffer.append(String.format("%s\n\n\n\n", ""));

        String chipta = printStringBuffer.toString().trim();
        System.out.println(chipta);

        PrinterService printerService = new PrinterService();
        printerService.printString(printerNomi, chipta);

    }

    private void tolovChiptasiniBerXP80(String printerNomi) {
        StringBuffer printStringBuffer = new StringBuffer();
        String lineB = String.format("%.63s\n", "---------------------------------------------");
        SimpleDateFormat sana = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat vaqt = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        String sanaString = sana.format(date);
        String vaqtString = vaqt.format(date);
        printStringBuffer.append(lineB);
        printStringBuffer.append(String.format("%29s\n", "O`RIKZOR 1/40"));
        printStringBuffer.append(lineB);
        printStringBuffer.append(String.format("%-15s %29s\n", "Telefon", user.getPhone()));
        printStringBuffer.append(String.format("%-15s %29s\n", "Sana", sanaString));
        printStringBuffer.append(String.format("%-15s %29s\n", "Vaqt", vaqtString));
        printStringBuffer.append(String.format("%-15s %29s\n", "Sotuvchi", user.getIsm()));
        printStringBuffer.append(String.format("%-15s %29s\n", "Oluvchi", qaydnomaData.getKirimNomi()));
        printStringBuffer.append(String.format("%-15s %29s\n", "Chipta N", qaydnomaData.getHujjat()));
        printStringBuffer.append("\n" + lineB);
        printStringBuffer.append(String.format("%-15s %5s %10s %12s\n", "Mahsulot", "Dona", "Narh", "Jami"));
        printStringBuffer.append(lineB);

        String space = "                    ";
        for (HisobKitob hk: tableObservableList) {
            Double dona = hk.getDona();
            Double narh = hk.getDona() * hk.getNarh();
            String line = String.format("%.15s %5s %10s %12s\n", hk.getIzoh() + space, decimalFormat.format(dona), decimalFormat.format(hk.getNarh()), decimalFormat.format(narh));
            printStringBuffer.append(line);
        }

        printStringBuffer.append(lineB);

        if (jamiMablag > 0) {
            String line = String.format("%-15s %5s %10s\n", "Xarid jami", " ", decimalFormat.format(jamiMablag));
            printStringBuffer.append(line);
        }
        if (chegirmaDouble > 0) {
            String line = String.format("%-15s %5s %10s\n", "Chegirma", " ", decimalFormat.format(chegirmaDouble));
            printStringBuffer.append(line);
        }

        if (naqdUsdDouble > 0) {
            String line = String.format("%-15s %5s %10s\n", "Naqd", " ", decimalFormat.format(naqdUsdDouble));
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

        printStringBuffer.append(String.format("%29" +
                "s\n", "XARIDINGIZ UCHUN TASHAKKUR"));
        printStringBuffer.append(lineB);
        printStringBuffer.append(String.format("%s\n\n\n\n\n\n\n\n", ""));

        String chipta = printStringBuffer.toString().trim();
        System.out.println(chipta);

        PrinterService printerService = new PrinterService();
        printerService.printString(printerNomi, chipta);

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
}
