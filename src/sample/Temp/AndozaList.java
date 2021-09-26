package sample.Temp;

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
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import sample.Config.MySqlDBLocal;
import sample.Data.*;
import sample.Model.HisobKitobModels;
import sample.Model.QaydnomaModel;
import sample.Tools.*;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AndozaList extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    GridPane gridPane = new GridPane();
    GridPane gridPane1 = new GridPane();
    VBox rightPane = new VBox();
    TableView<QaydnomaData> xaridChiptasiTableView = new TableView();
    TableView<HisobKitob> tovarTableView = new TableView();
    Button xaridChiptasiButton = new Button("Xarid chiptasi");
    ObservableList<QaydnomaData> qaydnomaDataObservableList;
    ObservableList<HisobKitob> tableObservableList = FXCollections.observableArrayList();

    HisobKitobModels hisobKitobModels = new HisobKitobModels();

    Connection connection;
    User user = new User(1, "admin", "", "admin");
    QaydnomaData qaydnomaData;

    DecimalFormat decimalFormat = new MoneyShow();
    int padding = 3;
    int amalTuri = 4;

    Double jamiMablag = 0.0;
    Double kassagaDouble = 0.0;
    Double chegirmaDouble = 0.0;
    Double naqdDouble = 0.0;
    Double plasticDouble = 0.0;
    Double balansDouble = 0.0;
    Double qaytimDouble = 0.0;

    Label jamiMablagLabel = new Label();
    Label chegirmaLabel = new Label();
    Label kassagaLabel = new Label();
    Label naqdLabel = new Label();
    Label plasticLabel = new Label();
    Label balansLabel = new Label();
    Label qaytimLabel = new Label();
    Font font = Font.font("Arial",20);

    public static void main(String[] args) {
        launch(args);
    }

    public AndozaList() {
        connection = new MySqlDBLocal().getDbConnection();
        GetDbData.initData(connection);
        ibtido();
    }

    public AndozaList(Connection connection, User user) {
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
    }

    private void initRightPane() {
        initXaridChiptasiTableView();
        initGridPane2();
        initXaridChiptasiButton();
        SetHVGrow.VerticalHorizontal(rightPane);
        rightPane.getChildren().addAll(xaridChiptasiTableView, gridPane1, xaridChiptasiButton);
    }

    private void initBottomPane() {}

    private void initBorderPane() {
        borderpane.setPadding(new Insets(padding));
        SetHVGrow.VerticalHorizontal(borderpane);
        borderpane.setCenter(gridPane);
        borderpane.setRight(rightPane);
    }

    private void initStage(Stage primaryStage) {
        scene = new Scene(borderpane, 1135, 760);
        scene.setUserAgentStylesheet("sample/Styles/caspian.css");
        stage = primaryStage;
        stage.setTitle("Andoza");
/*
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        stage.setX(bounds.getMinX() - 3);
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth() + 7);
        stage.setHeight(bounds.getHeight() + 6);
*/
//        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
    }

    private void initXaridChiptasiTableView() {
        SetHVGrow.VerticalHorizontal(xaridChiptasiTableView);
        xaridChiptasiTableView.setPadding(new Insets(padding));
        QaydnomaModel qaydnomaModel = new QaydnomaModel();
        qaydnomaDataObservableList = qaydnomaModel.getAnyData(connection, "amalTuri = " + amalTuri, "");
        if (qaydnomaDataObservableList.size()>0) {
            qaydnomaData = qaydnomaDataObservableList.get(0);
            refreshData(qaydnomaData);
            tovarTableView.refresh();
        }
        GetTableView2 getTableView2 = new GetTableView2();
        TableColumn<QaydnomaData, Integer> hujjatColumn = getTableView2.getHujjatColumn();
        hujjatColumn.setMinWidth(73);
        xaridChiptasiTableView.getColumns().addAll(
                getTableView2.getSanaColumn(),
                hujjatColumn,
                getTableView2.getChiqimNomiColumn(),
                getTableView2.getKirimNomiColumn()
        );
        xaridChiptasiTableView.setItems(qaydnomaDataObservableList);
/*
        xaridChiptasiTableView.setMinWidth(120);
        xaridChiptasiTableView.setMaxWidth(120);
*/

        xaridChiptasiTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                refreshData(newValue);
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
            QaydnomaData qaydnomaData1 = xaridChiptasiTableView.getSelectionModel().getSelectedItem();
            ObservableList<HisobKitob> hkList = tovarTableView.getItems();
            if (qaydnomaData1 != null) {
                if (hkList != null) {
                    tolovChiptasiniBer();
                }
            }

        });

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
//                        setText(GetDbData.getTovar(item).getText());
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
        Double aDouble = (naqdDouble + plasticDouble) - (jamiMablag - chegirmaDouble);
        if (aDouble > 0) {
            qaytimDouble = (aDouble);
        } else {
            qaytimDouble = 0.0;
        }
        qaytimLabel.setText(decimalFormat.format(qaytimDouble));
        balansDouble = (naqdDouble + plasticDouble - qaytimDouble) - (jamiMablag - chegirmaDouble);
        balansLabel.setText(decimalFormat.format(balansDouble));
    }

    private void showData(QaydnomaData qaydnomaData) {
        tableObservableList = hisobKitobModels.getAnyData(connection,
                "qaydId = " + qaydnomaData.getId() + " AND " + "hujjatId = " + qaydnomaData.getHujjat() + " AND " + "hisob2 = " + qaydnomaData.getKirimId() + " AND tovar>0",
                "");
        tovarTableView.setItems(tableObservableList);
    }

    private void initGridPane() {
        initTovarTable();
        SetHVGrow.VerticalHorizontal(gridPane);
        gridPane.setPadding(new Insets(padding));

        int rowIndex = 0;
        gridPane.add(tovarTableView, 0, rowIndex, 1, 1);
        GridPane.setHgrow(tovarTableView, Priority.ALWAYS);
        GridPane.setVgrow(tovarTableView, Priority.ALWAYS);

    }

    private void initGridPane2() {
        SetHVGrow.VerticalHorizontal(gridPane1);
//        gridPane1.setMaxHeight(60);
        gridPane1.setPadding(new Insets(padding));

        int rowIndex = 0;
        Label label1 = new Label("Xarid narhi");
        label1.setFont(font);
        HBox.setHgrow(label1, Priority.ALWAYS);
        GridPane.setHgrow(label1, Priority.ALWAYS);
        HBox.setHgrow(jamiMablagLabel, Priority.ALWAYS);
        GridPane.setHgrow(jamiMablagLabel, Priority.ALWAYS);
        gridPane1.add(label1, 0, rowIndex, 1, 1);
        gridPane1.add(jamiMablagLabel, 1, rowIndex, 1,1);

        rowIndex++;
        Label label2 = new Label("Chegirma");
        label2.setFont(font);
        HBox.setHgrow(label2, Priority.ALWAYS);
        GridPane.setHgrow(label2, Priority.ALWAYS);
        HBox.setHgrow(chegirmaLabel, Priority.ALWAYS);
        GridPane.setHgrow(chegirmaLabel, Priority.ALWAYS);
        gridPane1.add(label2, 0, rowIndex, 1, 1);
        gridPane1.add(chegirmaLabel,1, rowIndex,1, 1);

        rowIndex++;
        Label label3 = new Label("Kassaga");
        label3.setFont(font);
        HBox.setHgrow(label3, Priority.ALWAYS);
        GridPane.setHgrow(label3, Priority.ALWAYS);
        HBox.setHgrow(kassagaLabel, Priority.ALWAYS);
        GridPane.setHgrow(kassagaLabel, Priority.ALWAYS);
        gridPane1.add(label3, 0, rowIndex, 1, 1);
        gridPane1.add(kassagaLabel,1, rowIndex,1, 1);

        rowIndex++;
        Label label4 = new Label("Naqd");
        label4.setFont(font);
        HBox.setHgrow(label4, Priority.ALWAYS);
        GridPane.setHgrow(label4, Priority.ALWAYS);
        HBox.setHgrow(naqdLabel, Priority.ALWAYS);
        GridPane.setHgrow(naqdLabel, Priority.ALWAYS);
        gridPane1.add(label4, 0, rowIndex, 1, 1);
        gridPane1.add(naqdLabel, 1, rowIndex, 1,1);

        rowIndex++;
        Label label5 = new Label("Plastik");
        label5.setFont(font);
        HBox.setHgrow(label5, Priority.ALWAYS);
        GridPane.setHgrow(label5, Priority.ALWAYS);
        HBox.setHgrow(plasticLabel, Priority.ALWAYS);
        GridPane.setHgrow(plasticLabel, Priority.ALWAYS);
        gridPane1.add(label5, 0, rowIndex, 1, 1);
        gridPane1.add(plasticLabel,1, rowIndex,1, 1);

        rowIndex++;
        Label label6 = new Label("Qaytim");
        label6.setFont(font);
        HBox.setHgrow(label6, Priority.ALWAYS);
        GridPane.setHgrow(label6, Priority.ALWAYS);
        HBox.setHgrow(qaytimLabel, Priority.ALWAYS);
        GridPane.setHgrow(qaytimLabel, Priority.ALWAYS);
        gridPane1.add(label6, 0, rowIndex, 1, 1);
        gridPane1.add(qaytimLabel,1, rowIndex,1, 1);

        rowIndex++;
        Label label7 = new Label("Balans");
        label7.setFont(font);
        HBox.setHgrow(label7, Priority.ALWAYS);
        GridPane.setHgrow(label7, Priority.ALWAYS);
        HBox.setHgrow(balansLabel, Priority.ALWAYS);
        GridPane.setHgrow(balansLabel, Priority.ALWAYS);
        gridPane1.add(label7, 0, rowIndex, 1, 1);
        gridPane1.add(balansLabel,1, rowIndex,1, 1);
    }

    private void refreshData(QaydnomaData qaydnomaData) {
        ObservableList<HisobKitob> hisobKitobObservableList = hisobKitobModels.getAnyData(
                connection, "qaydId = " + qaydnomaData.getId() + " AND " + "hujjatId = " + qaydnomaData.getHujjat(), "");
        tableObservableList.removeAll(tableObservableList);
        jamiMablag = 0.0;
        chegirmaDouble = 0.0;
        naqdDouble = 0.0;
        plasticDouble = 0.0;
        qaytimDouble = 0.0;
        balansDouble = 0.0;
        for (HisobKitob hk: hisobKitobObservableList) {
            switch (hk.getAmal()) {
                case 4: //Tovar
                    if (hk.getHisob2()==qaydnomaData.getKirimId()) {
                        tableObservableList.add(hk);
                        jamiMablag += hk.getNarh() * hk.getDona();
                    }
                    break;
                case 7: //To`lov naqd
                    naqdDouble = hk.getNarh();
                    break;
                case 8: //Qaytim
                    qaytimDouble = hk.getNarh();
                    break;
                case 13:    //Chegirma
                    chegirmaDouble = hk.getNarh();
                    break;
                case 15:    //To`lov plastic
                    plasticDouble = hk.getNarh();
                    break;
            }
        }
        kassagaDouble = jamiMablag - chegirmaDouble;
        balansDouble = (naqdDouble + plasticDouble) - kassagaDouble - qaytimDouble;

        jamiMablagLabel.setText(decimalFormat.format(jamiMablag));
        chegirmaLabel.setText(decimalFormat.format(chegirmaDouble));
        kassagaLabel.setText(decimalFormat.format(kassagaDouble));
        naqdLabel.setText(decimalFormat.format(naqdDouble));
        plasticLabel.setText(decimalFormat.format(plasticDouble));
        qaytimLabel.setText(decimalFormat.format(qaytimDouble));
        balansLabel.setText(decimalFormat.format(balansDouble));
    }

    private void initLabelFont() {
        jamiMablagLabel.setFont(font);
        chegirmaLabel.setFont(font);
        kassagaLabel.setFont(font);
        naqdLabel.setFont(font);
        plasticLabel.setFont(font);
        qaytimLabel.setFont(font);
        balansLabel.setFont(font);
    }

    private void tolovChiptasiniBer() {
        StringBuffer printStringBuffer = new StringBuffer();
        String line1 = String.format("%-15s%6s%10s\n", "----------------", "-----", "----------");
        String line2 = String.format("%-15s %5s %10s\n", "---------------", "----", "----------");
        SimpleDateFormat sana = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat vaqt = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        String sanaString = sana.format(date);
        String vaqtString = vaqt.format(date);
        printStringBuffer.append(line1);
        printStringBuffer.append(String.format("%23s\n", "BEST PERFUMERY"));
        printStringBuffer.append(line1);
        printStringBuffer.append(String.format("%-15s %16s\n", "Telefon", user.getPhone()));
        printStringBuffer.append(String.format("%-11s %20s\n", "Telegram", "t.me/best_perfumery"));
        printStringBuffer.append(String.format("%-15s %16s\n", "Sana", sanaString));
        printStringBuffer.append(String.format("%-15s %16s\n", "Vaqt", vaqtString));
        printStringBuffer.append(String.format("%-15s %16s\n", "Chipta N", qaydnomaData.getHujjat()));
        printStringBuffer.append(line1);

        printStringBuffer.append(String.format("%-15s %5s %10s\n", "Mahsulot", "Dona", "Narh"));
        printStringBuffer.append(line2);

        String space = "                    ";
        for (HisobKitob hk: tableObservableList) {
            Double dona = hk.getDona();
            Double narh = hk.getDona() * hk.getNarh();
            String line = String.format("%.15s %5s %10s\n", hk.getIzoh() + space, decimalFormat.format(dona), decimalFormat.format(narh));
            printStringBuffer.append(line);
        }

        printStringBuffer.append(line2);

        if (jamiMablag > 0) {
            String line = String.format("%-15s %5s %10s\n", "Xarid jami", " ", decimalFormat.format(jamiMablag));
            printStringBuffer.append(line);
        }
        if (chegirmaDouble > 0) {
            String line = String.format("%-15s %5s %10s\n", "Chegirma", " ", decimalFormat.format(chegirmaDouble));
            printStringBuffer.append(line);
        }

        if (naqdDouble > 0) {
            String line = String.format("%-15s %5s %10s\n", "Naqd", " ", decimalFormat.format(naqdDouble));
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
        printStringBuffer.append(line2);

        printStringBuffer.append(String.format("%29s\n", "XARIDINGIZ UCHUN TASHAKKUR"));
        printStringBuffer.append(line1);
        printStringBuffer.append(String.format("%s\n\n\n\n", ""));

        String chipta = printStringBuffer.toString().trim();
        System.out.println(chipta);

        PrinterService printerService = new PrinterService();
        printerService.printString("POS-58", chipta);

    }


}
