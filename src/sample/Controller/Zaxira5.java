package sample.Controller;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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
import sample.Excel.PriceListExcel;
import sample.Excel.Zaxira2Excel;
import sample.Excel.Zaxira3Excel;
import sample.Model.*;
import sample.Tools.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Zaxira5 extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    SplitPane splitPane = new SplitPane();
    VBox centerPane = new VBox();
    VBox rightPane = new VBox();
    VBox leftPane = new VBox();
    TableView<HisobKitob> leftTable;
    TableView<HisobKitob> centerTable;
    TableView<HisobKitob> rightTable;
    GridPane gridPane;
    Text adad = new Text("Adad");
    Text narh = new Text("Narh");
    Text adadDouble = new Text("");
    Text narhDouble = new Text("");
    DecimalFormat decimalFormat = new MoneyShow();

    HisobKitobModels hisobKitobModels = new HisobKitobModels();

    Connection connection;
    User user;
    Standart tovar;
    Hisob hisob;
    BarCode barCode;
    int padding = 3;


    public static void main(String[] args) {
        launch(args);
    }

    public Zaxira5() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
        ibtido();
    }
    public Zaxira5(Connection connection, User user) {
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
        initBottomPane();
        initBorderPane();
//        gridPane = initGridPane();
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

    private void initTopPane() {
    }
    private void initLeftPane() {
        leftPane.setPadding(new Insets(padding));
        SetHVGrow.VerticalHorizontal(leftPane);
        leftTable = initLeftTable();
        Tugmachalar leftButtons = initLeftButtons();
        leftPane.getChildren().addAll(leftButtons, leftTable);
    }
    private void initCenterPane() {
        centerPane.setPadding(new Insets(padding));
        SetHVGrow.VerticalHorizontal(centerPane);
        Tugmachalar centerButtons = initCenterButtons();
        centerTable = initCenterTable();
        GridPane pastLawha = pastLawha();

        centerPane.getChildren().addAll(centerButtons, centerTable, pastLawha);
    }
    private GridPane pastLawha() {
        GridPane gridPane = new GridPane();
        DoubleTextBox adadNarh = new DoubleTextBox(adad, narh);
        DoubleTextBox adadNarhDouble = new DoubleTextBox(adadDouble, narhDouble);
        adadNarh.setAlignment(Pos.CENTER_LEFT);
        adadNarhDouble.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(adadNarh, Priority.ALWAYS);
        HBox.setHgrow(adadNarhDouble, Priority.ALWAYS);
        HBox.setHgrow(gridPane, Priority.ALWAYS);
        gridPane.add(adadNarh, 0, 0, 1, 1);
        gridPane.add(adadNarhDouble, 1, 0, 1, 1);
        GridPane.setHalignment(adadNarh, HPos.LEFT);
        GridPane.setHalignment(adadNarhDouble, HPos.RIGHT);
        GridPane.setHgrow(adadNarh, Priority.ALWAYS);
        GridPane.setHgrow(adadNarhDouble, Priority.ALWAYS);
        return gridPane;
    }
    private void initRightPane() {
        rightPane.setPadding(new Insets(padding));
        SetHVGrow.VerticalHorizontal(rightPane);
        Tugmachalar rightButtons = initRightButtons();
        rightTable = initRightTable();
        rightPane.getChildren().addAll(rightButtons, rightTable);
    }
    private void initSplitPane() {
        splitPane.setPadding(new Insets(padding));
        SetHVGrow.VerticalHorizontal(splitPane);
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.getItems().addAll(leftPane, centerPane, rightPane);
        splitPane.setDividerPositions(.45,.70);
    }
    private void initBottomPane() {
    }
    private void initBorderPane() {
        initSplitPane();
        borderpane.setCenter(splitPane);
    }
    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        stage.setTitle("Zaxiradagi tovarlar");
        scene = new Scene(borderpane);
        scene.getStylesheets().add("/sample/Styles/modena.css");
        stage.setScene(scene);
    }

    private Tugmachalar initLeftButtons() {
        Tugmachalar buttons = new Tugmachalar();
        Button add = buttons.getAdd();
        Button delete = buttons.getDelete();
        Button edit = buttons.getEdit();
        Button excel= buttons.getExcel();
        excel.setOnAction(event -> {
            sotishNarhlari();
            ObservableList<HisobKitob> observableList = leftTable.getItems();
            if (observableList.size()>0) {
                Collections.sort(observableList, Comparator.comparing(HisobKitob::getIzoh));
            }
            PriceListExcel priceListExcel = new PriceListExcel();
            priceListExcel.print(observableList);
        });
        TextField tovarTextField = new TextField();
        tovarTextField.setPromptText("Tovar nomi");
        ObservableList<HisobKitob> hisobKitobObservableList = leftTable.getItems();
        TextFields.bindAutoCompletion(tovarTextField, hisobKitobObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<HisobKitob> autoCompletionEvent) -> {
            HisobKitob hisobKitob = autoCompletionEvent.getCompletion();
            if (hisobKitob!=null) {
                tovarniTanla(hisobKitob);
            }
        });
        HBox.setHgrow(tovarTextField, Priority.ALWAYS);
        TextField barCodeTextField = new TextField();
        barCodeTextField.setPromptText("Barcode");
        barCodeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue!=null) {
                barCodeTaftish(newValue);
            }
        });
        HBox.setHgrow(barCodeTextField, Priority.ALWAYS);
        buttons.getChildren().removeAll(add, edit, delete);
        buttons.getChildren().addAll(tovarTextField, barCodeTextField);
        return buttons;
    }
    private void sotishNarhlari() {
        ObservableList<HisobKitob> observableList = leftTable.getItems();
        Double narhDouble = 0d;
        for (HisobKitob hisobKitob: observableList) {
            Standart6 s6 = narhOl(hisobKitob.getTovar());
            if (s6 != null) {
                narhDouble = s6.getUlgurji();
            }
            else {
                TovarNarhi tn = yakkaTovarNarhi(hisobKitob.getTovar(), 1);
                if (tn != null) {
                    narhDouble = tn.getNarh();
                }
            }
            hisobKitob.setSummaCol(narhDouble);
        }
        narhDouble = 0d;
        for (HisobKitob hisobKitob: observableList) {
            Standart6 s6 = narhOl(hisobKitob.getTovar());
            if (s6 != null) {
                narhDouble = s6.getUlgurji();
            }
            else {
                TovarNarhi tn = yakkaTovarNarhi(hisobKitob.getTovar(), 2);
                if (tn != null) {
                    narhDouble = tn.getNarh();
                }
            }
            hisobKitob.setBalans(narhDouble);
        }
    }
    private Tugmachalar initCenterButtons() {
        Tugmachalar buttons = new Tugmachalar();
        Button add = buttons.getAdd();
        Button delete = buttons.getDelete();
        Button edit = buttons.getEdit();
        Button excel= buttons.getExcel();

        excel.setOnAction(event -> {
            Zaxira2Excel zaxira2Excel = new Zaxira2Excel();
            zaxira2Excel.print(centerTable.getItems());
        });
        TextField dokonTextField = new TextField();
        dokonTextField.textProperty().addListener((observable, oldValue, newValue) -> {
        });
        dokonTextField.setPromptText("Do`kon nomi");
        HBox.setHgrow(dokonTextField, Priority.ALWAYS);
        buttons.getChildren().removeAll(add, edit, delete);
        buttons.getChildren().add(dokonTextField);
        return buttons;
    }
    private Tugmachalar initRightButtons() {
        Tugmachalar buttons = new Tugmachalar();
        Button add = buttons.getAdd();
        Button delete = buttons.getDelete();
        Button edit = buttons.getEdit();
        Button excel= buttons.getExcel();
        excel.setOnAction(event -> {
            Zaxira3Excel zaxira3Excel = new Zaxira3Excel(hisob, tovar, rightTable.getItems());
            zaxira3Excel.print();
        });
        buttons.getChildren().removeAll(add, edit, delete);
        return buttons;
    }
    // 90 919 3020 Odil B27
    private TableView<HisobKitob> initLeftTable() {
        TableView<HisobKitob> tableView = new TableView<>();
        ObservableList<HisobKitob> hisobKitobObservableList = balanceHisobKitobga();
        TableViewAndoza tableViewAndoza = new TableViewAndoza();
        tableView.setMinHeight(200);
        tableView.setMinHeight(540);
        TableColumn<HisobKitob, DoubleTextBox> adadColumn = adadNarh();
        TableColumn<HisobKitob, String> izohColumn = tableViewAndoza.getIzoh2Column();
        izohColumn.setText("Tovarlar");
        tableView.setItems(hisobKitobObservableList);
        tableView.getColumns().addAll(izohColumn, getChakanaNarhColumn(), getUlgurjiNarhColumn());
        HisobKitob hisobKitob = null;
        if (hisobKitobObservableList.size()>0) {
            hisobKitob = hisobKitobObservableList.get(0);
            tableView.getSelectionModel().select(hisobKitob);
            tableView.scrollTo(hisobKitob);
            tableView.requestFocus();
        }
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                tovar = GetDbData.getTovar(newValue.getTovar());
                ObservableList<Balance> balanceList = dokonlarZaxirasiniOl(connection, tovar.getId());
                Double jamiAdad = 0d;
                Double jamiNarh = 0d;
                for (Balance b: balanceList) {
                    jamiAdad += b.getAdadJami();
                    jamiNarh += b.getNarhJami();
                }
                adadDouble.setText(decimalFormat.format(jamiAdad));
                narhDouble.setText(decimalFormat.format(jamiNarh / jamiAdad));
                ObservableList<HisobKitob> observableList = balanceHisobKitobga(balanceList, tovar.getId());
                centerTable.setItems(observableList);
                if (observableList.size()>0) {
                    rightTable.setItems(FXCollections.observableArrayList());
                    hisob = GetDbData.getHisob(observableList.get(0).getHisob1());
                    ObservableList<HisobKitob> rightDataList = getRightTableData(LocalDate.now(), hisob.getId(), tovar.getId());
                    rightTable.setItems(rightDataList);
                    rightTable.refresh();
                }
                centerTable.refresh();
            }
        });
        tableView.setEditable(true);
        SetHVGrow.VerticalHorizontal(tableView);
        return tableView;
    }
    private ObservableList<HisobKitob> getLeftTableData(LocalDate localDate) {
        ObservableList<HisobKitob> kirimList = FXCollections.observableArrayList();
        ObservableList<HisobKitob> chiqimList = FXCollections.observableArrayList();
        String kirim =
                "SELECT id, text, (Select sum(dona(a.id, id3)) from dokonlar), (Select sum(Narh(a.id, id3)) from dokonlar) FROM tovar as a;";
        ResultSet rs1 = hisobKitobModels.getResultSet(connection, kirim);
        try {
            while (rs1.next()) {
                Integer id = rs1.getInt(1);
                String text = rs1.getString(2);
                Double jamiDona = rs1.getDouble(3);
                Double jamiNarh = rs1.getDouble(4);
                if (jamiDona==null)
                    jamiDona = 0d;
                if (jamiNarh==null)
                    jamiNarh = 0d;
                HisobKitob hisobKitob = new HisobKitob(id, 0, 0, 4, 0, 0, 1, id, 1d, "", jamiDona, jamiNarh, 0, text, user.getId(), null);
                if (hisobKitob.getDona()>0)
                    kirimList.add(hisobKitob);
            }
            rs1.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kirimList;
    }

    private HisobKitob getHisobKitob(ObservableList<HisobKitob> hisobList, Integer id) {
        HisobKitob hisobKitob = null;
        for (HisobKitob hk: hisobList) {
            if (hk.getId().equals(id)) {
                hisobKitob = hk;
                break;
            }
        }
        return hisobKitob;
    }

    public TableColumn<HisobKitob, DoubleTextBox> adadNarh() {
        TableColumn<HisobKitob, DoubleTextBox> valutaKurs = new TableColumn<>("Dona/Narh");
        valutaKurs.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, DoubleTextBox>, ObservableValue<DoubleTextBox>>() {

            @Override
            public ObservableValue<DoubleTextBox> call(TableColumn.CellDataFeatures<HisobKitob, DoubleTextBox> param) {
                HisobKitob hisobKitob = param.getValue();
                Text text1 = new Text(decimalFormat.format(hisobKitob.getDona()));
                Text text2 = new Text(decimalFormat.format(hisobKitob.getNarh() / hisobKitob.getDona()));
                text1.setFill(Color.GREEN);
                text2.setFill(Color.BLUE);
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

        valutaKurs.setMinWidth(20);
        valutaKurs.setMinWidth(150);
        valutaKurs.setStyle( "-fx-alignment: CENTER;");
        return valutaKurs;
    }
    private TableView<HisobKitob> initCenterTable() {
        TableView<HisobKitob> tableView = new TableView<>();
        TableViewAndoza tableViewAndoza = new TableViewAndoza();
        TableColumn<HisobKitob, String> izohColumn = tableViewAndoza.getIzoh2Column();
        izohColumn.setMinWidth(200);
        TableColumn<HisobKitob, Integer> hisob1Column = tableViewAndoza.getHisob1Column();
        TableColumn<HisobKitob, DoubleTextBox> adadColumn = tableViewAndoza.adadNarh();
        tableView.getColumns().addAll(hisob1Column, adadColumn);
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                hisob = GetDbData.getHisob(newValue.getHisob1());
                rightTable.setItems(getRightTableData(LocalDate.now(), hisob.getId(), tovar.getId()));
                rightTable.refresh();
            }
        });
        SetHVGrow.VerticalHorizontal(tableView);
        return tableView;
    }

    private TableView<HisobKitob> initRightTable() {
        TableView<HisobKitob> tableView = new TableView<>();
        TableViewAndoza tableViewAndoza = new TableViewAndoza();
        tableView.setMinHeight(200);
        tableView.setMinHeight(540);
        TableColumn<HisobKitob, Date> sanaColumn = tableViewAndoza.getDateTimeColumn();
        TableColumn<HisobKitob, DoubleTextBox> amalHujjat = tableViewAndoza.amalHujjat();
        TableColumn<HisobKitob, String> textColumn = tableViewAndoza.getIzoh2Column();
        TableColumn<HisobKitob, DoubleTextBox> valutaKurs = tableViewAndoza.valutaKurs();
        TableColumn<HisobKitob, DoubleTextBox> adadNarh = tableViewAndoza.adadNarh();
        textColumn.setText("Izoh");
        textColumn.setMinWidth(200);
        tableView.getColumns().addAll(sanaColumn, amalHujjat, valutaKurs, adadNarh);
        if (tovar != null && hisob != null) {
            tableView.setItems(getRightTableData(LocalDate.now(), hisob.getId(), tovar.getId()));
            tableView.refresh();
        }
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
        });
        tableView.setRowFactory(tv -> new TableRow<HisobKitob>() {
            @Override
            protected void updateItem(HisobKitob hisobKitob, boolean empty) {
                super.updateItem(hisobKitob, empty);
                if (hisobKitob == null || hisobKitob.getId() == null)
                    setStyle("");
                else if (hisobKitob.getHisob2().equals(hisob.getId()))
                    setStyle("-fx-background-color: white;");
                else if (hisobKitob.getHisob1().equals(hisob.getId()))
                    setStyle("-fx-background-color: #baffba;");
                else
                    setStyle("");
            }
        });
        SetHVGrow.VerticalHorizontal(tableView);
        return tableView;
    }

    private ObservableList<HisobKitob> getCenterTableData(LocalDate localDate, Integer tovarId) {
        ObservableList<HisobKitob> observableList = FXCollections.observableArrayList();
        String kirimHisoblari = "select id3, text from dokonlar;";
        ResultSet rs1 = hisobKitobModels.getResultSet(connection, kirimHisoblari);
        try {
            while (rs1.next()) {
                Integer id = rs1.getInt(1);
                String text = rs1.getString(2);
                Double donaDouble = 0d;
                Double narhDouble = 0d;
                String donaSelect = "Select sum(if(hisob2="+id+",dona,0)) as kirim, sum(if(hisob1="+id+",dona,0)) as chiqim  from HisobKitob where (hisob1=" + id + " or hisob2=" +id + ") and tovar ="+tovarId+" group by barcode";
                ResultSet tempRs = hisobKitobModels.getResultSet(connection, donaSelect);
                while (tempRs.next()) {
                    donaDouble = tempRs.getDouble(1) - tempRs.getDouble(2);
                }
                tempRs.close();
                String narhSelect = "Select sum(if(hisob2="+id+",narh*dona/kurs,0)) as kirim, sum(if(hisob1="+id+",narh*dona/kurs,0)) as chiqim  from HisobKitob where (hisob1=" + id + " or hisob2=" +id + ") and tovar ="+tovarId+" group by barcode";
                tempRs = hisobKitobModels.getResultSet(connection, narhSelect);
                while (tempRs.next()) {
                    narhDouble = tempRs.getDouble(1) - tempRs.getDouble(2);
                }
                tempRs.close();
                HisobKitob hisobKitob = new HisobKitob(0, 0, 0, 4, id, 0, 1, 0, 1d, "", donaDouble, narhDouble, 0, text, user.getId(), null);
                if (hisobKitob.getDona()>0)
                    observableList.add(hisobKitob);
            }
            rs1.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return observableList;
    }

    private ObservableList<HisobKitob> getRightTableData(LocalDate localDate, Integer hisob1Id, Integer tovarId) {
        ObservableList<HisobKitob> observableList = FXCollections.observableArrayList();
        BarCode bc = null;
        Standart tovar = GetDbData.getTovar(tovarId);
        if (tovar != null) {
            bc = GetDbData.getBarCode(tovar.getId());
            DecimalFormat decimalFormat = new MoneyShow();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String localDateString = localDate.format(formatter);
            String kirimHisoblari = "(hisob1=" + hisob1Id + " or hisob2="+hisob1Id+") and tovar = " + tovarId;
            observableList = hisobKitobModels.getAnyData(connection, kirimHisoblari,"datetime");
        }
        return observableList;
    }
    private ObservableList<HisobKitob> getBottomTableData(LocalDate localDate, Integer hisob1Id, String barCodeString) {
        ObservableList<HisobKitob> observableList = FXCollections.observableArrayList();
        BarCode bc = null;
        if (tovar != null) {
            bc = GetDbData.getBarCode(tovar.getId());
            DecimalFormat decimalFormat = new MoneyShow();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String localDateString = localDate.format(formatter);
            String kirimHisoblari =
                    "select datetime, izoh, barcode, dona, manba  from hisobkitob where hisob1=" + hisob1Id + " and barcode = " + barCodeString + " and amal = 4 and manba>0 order by dateTime, tovar;";
            ResultSet rs1 = hisobKitobModels.getResultSet(connection, kirimHisoblari);
            try {
                while (rs1.next()) {
                    Date date = sdf.parse(rs1.getString(1));
                    String text = rs1.getString(2);
                    String bcText = rs1.getString(3);
                    bc = GetDbData.getBarCode(bcText);
                    Standart birlik = GetDbData.getBirlik(bc.getBirlik());
                    Double adad = rs1.getDouble(4);
                    Integer manba = rs1.getInt(5);
                    HisobKitob hisobKitob = new HisobKitob(0, 0, 0, 4, hisob1Id, 0, 1, tovar.getId(), 1d, bc.getBarCode(), adad, 0d, manba, birlik.getText(), user.getId(), date);
                    observableList.add(hisobKitob);
                }
                rs1.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return observableList;
    }

    public BorderPane getBorderpane() {
        return borderpane;
    }
    public GridPane getGridPane() {
        return gridPane;
    }

    public TableView<HisobKitob> getLeftTable() {
        return leftTable;
    }
    private TableColumn<HisobKitob, Double> getOrtachaNarhColumn() {
        TableColumn<HisobKitob, Double>  chakanaColumn = new TableColumn<>("O`rtacha narh");
        chakanaColumn.setMinWidth(100);
        chakanaColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, Double>, ObservableValue<Double>>() {

            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<HisobKitob, Double> param) {
                HisobKitob hk = param.getValue();
                Double narhDouble = 0d;
                if (hk!= null) {
                    narhDouble = hk.getNarh() / hk.getDona();
                }
                return new SimpleObjectProperty<Double>(narhDouble);
            }
        });
        chakanaColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setMinimumIntegerDigits (1);
                numberFormat.setMaximumIntegerDigits (10);

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
        chakanaColumn.setStyle( "-fx-alignment: CENTER;");
        return chakanaColumn;
    }
    private TableColumn<HisobKitob, Double> getChakanaNarhColumn() {
        StandartModels standartModels = new StandartModels("NarhTuri");
        ObservableList<Standart> standarts = standartModels.get_data(connection);
        Standart standart = standarts.get(0);
        TableColumn<HisobKitob, Double>  chakanaColumn = new TableColumn<>(standart.getText());
        chakanaColumn.setMinWidth(100);
        chakanaColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, Double>, ObservableValue<Double>>() {

            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<HisobKitob, Double> param) {
                HisobKitob hk = param.getValue();
                Double narhDouble = 0.0;
                Standart6 s6 = narhOl(hk.getTovar());
                if (s6 != null) {
                    narhDouble = s6.getChakana();
                }
                else {
                    TovarNarhi tn = yakkaTovarNarhi(hk.getTovar(), 1);
                    if (tn != null) {
                        narhDouble = tn.getNarh();
                    }
                }
                hk.setSummaCol(narhDouble);
                return new SimpleObjectProperty<Double>(narhDouble);
            }
        });
        chakanaColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setMinimumIntegerDigits (1);
                numberFormat.setMaximumIntegerDigits (10);

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
            TableView<HisobKitob> tableView = event.getTableView();
            HisobKitob hk = event.getRowValue();
            Double newValue = event.getNewValue();
            if (newValue != null) {
                narhYoz(hk.getTovar(), 1, newValue/hk.getKurs());
                hk.setSummaCol(newValue/hk.getKurs());
            }
            tableView.refresh();
        });
        chakanaColumn.setStyle( "-fx-alignment: CENTER;");
        return chakanaColumn;
    }
    private  TableColumn<HisobKitob, Double> getUlgurjiNarhColumn() {
        StandartModels standartModels = new StandartModels("NarhTuri");
        ObservableList<Standart> standarts = standartModels.get_data(connection);
        Standart standart = standarts.get(1);
        TableColumn<HisobKitob, Double>  ulgurjiColumn = new TableColumn<>(standart.getText());
        ulgurjiColumn.setMinWidth(100);
        ulgurjiColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, Double>, ObservableValue<Double>>() {

            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<HisobKitob, Double> param) {
                HisobKitob hk = param.getValue();
                Double narhDouble = 0.0;
                Standart6 s6 = narhOl(hk.getTovar());
                if (s6 != null) {
                    narhDouble = s6.getUlgurji();

                }
                else {
                    TovarNarhi tn = yakkaTovarNarhi(hk.getTovar(), 2);
                    if (tn != null) {
                        narhDouble = tn.getNarh();
                    }
                }
                hk.setBalans(narhDouble);
                return new SimpleObjectProperty<Double>(narhDouble);
            }
        });
        ulgurjiColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setMinimumIntegerDigits (1);
                numberFormat.setMaximumIntegerDigits (10);

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
            TableView<HisobKitob> tableView = event.getTableView();
            HisobKitob hk = event.getRowValue();
            Double newValue = event.getNewValue();
            if (newValue != null) {
                narhYoz(hk.getTovar(), 2, newValue/hk.getKurs());
                hk.setBalans(newValue/hk.getKurs());
            }
            tableView.refresh();
        });
        ulgurjiColumn.setStyle( "-fx-alignment: CENTER;");
        return ulgurjiColumn;
    }
    public Standart6 narhOl(int tovarId) {
        Standart6 s6 = null;
        Standart6Models standart6Models = new Standart6Models("TGuruh1");
        Standart3Models standart3Models = new Standart3Models();
        standart3Models.setTABLENAME("TGuruh2");
        ObservableList<Standart3> s3List = standart3Models.getAnyData(connection, "id3 = " + tovarId, "");
        if (s3List.size()>0) {
            Standart3 s3 = s3List.get(0);
            s6 = standart6Models.getWithId(connection, s3.getId2());
        }
        return s6;
    }
    private TovarNarhi yakkaTovarNarhi(int tovarId, int narhTuri) {
        TovarNarhi tovarNarhi = null;
        TovarNarhiModels tovarNarhiModels = new TovarNarhiModels();
        ObservableList<TovarNarhi> observableList = tovarNarhiModels.getAnyData(
                connection, "tovar = " + tovarId + " AND narhTuri = " + narhTuri, "id desc"
        );
        if (observableList.size()>0) {
            tovarNarhi = observableList.get(0);
        }
        return tovarNarhi;
    }
    public void narhYoz(int tovarId, int narhTuri, Double narhDouble) {
        Standart6Models standart6Models = new Standart6Models("TGuruh1");
        Standart3Models standart3Models = new Standart3Models();
        standart3Models.setTABLENAME("TGuruh2");
        ObservableList<Standart3> s3List = standart3Models.getAnyData(connection, "id3 = " + tovarId, "");
        if (s3List.size()>0) {
            Standart3 s3 = s3List.get(0);
            Standart6 s6 = standart6Models.getWithId(connection, s3.getId2());
            if (s6 != null) {
                switch (narhTuri) {
                    case 0:
                        s6.setNarh(narhDouble);
                        break;
                    case 1:
                        s6.setChakana(narhDouble);
                        break;
                    case 2:
                        s6.setUlgurji(narhDouble);
                        break;
                    case 3:
                        s6.setBoj(narhDouble);
                        break;
                    case 4:
                        s6.setNds(narhDouble);
                        break;
                }
                standart6Models.update_data(connection, s6);
                GuruhNarhModels guruhNarhModels = new GuruhNarhModels();
                GuruhNarh guruhNarh = new GuruhNarh(
                        null, new Date(), s6.getId(), narhTuri, narhDouble, user.getId(), new Date()
                );
                guruhNarhModels.insert_data(connection, guruhNarh);
            }
        } else {
            TovarNarhiModels tovarNarhiModels = new TovarNarhiModels();
            TovarNarhi tovarNarhi = new TovarNarhi(
                    null, new Date(), tovarId, narhTuri, 1, 1d, narhDouble, user.getId(), null
            );
            tovarNarhiModels.insert_data(connection, tovarNarhi);
        }
    }
    private void tovarniTanla(String oldValue, String newValue) {
        ObservableList<HisobKitob> observableList = getLeftTableData(LocalDate.now());
        ObservableList<HisobKitob> subentries = FXCollections.observableArrayList();
        newValue = newValue.toLowerCase();

        if ( oldValue != null && (newValue.length() < oldValue.length()) ) {
            leftTable.setItems(observableList);
        }

        for ( HisobKitob hisobKitob: observableList) {
            if (hisobKitob.getIzoh().toLowerCase().contains(newValue)) {
                subentries.add(hisobKitob);
            }
        }
        leftTable.setItems(subentries);
    }
    private void barCodeTaftish(String newValue) {
        BarCode barCode = GetDbData.getBarCode(newValue);
        if (barCode != null) {
            ObservableList<HisobKitob> observableList = leftTable.getItems();
            for (HisobKitob hk : observableList) {
                if (barCode.getTovar().equals(hk.getTovar())) {
                    leftTable.getSelectionModel().select(hk);
                    leftTable.scrollTo(hk);
                    leftTable.requestFocus();
                    break;
                }
            }
        }
    }
    private void tovarniTanla(HisobKitob hk) {
        leftTable.getSelectionModel().select(hk);
        leftTable.scrollTo(hk);
        leftTable.requestFocus();
        leftTable.refresh();
    }

    private ObservableList<Balance> zaxiraniOl(Connection connection) {
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        Map<String, Hisob> barCodeMap = new HashMap<>();
        ObservableList<Balance> books=FXCollections.observableArrayList();
        String dokonlar = "";
        List<Integer> dokonlarRoyxati = new ArrayList<>();
        Qoldiq qoldiq = new Qoldiq();
        String select = "Select group_concat(ID3, '') FROM DOKONLAR;";
        ResultSet rs = hisobKitobModels.getResultSet(connection, select);

        try{
            if (rs.next()) {
                dokonlar = rs.getString(1);
                dokonlarRoyxati = dokonlarRoyxati(dokonlar);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        select = "select * from HisobKitob where (hisob1 in("+ dokonlar +") or hisob2 in("+ dokonlar +")) and tovar>0 order by tovar";
        rs = hisobKitobModels.getResultSet(connection, select);
        int i=0;
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Map<Integer, Balance> tovarlarBalansi = new HashMap<>();
            while (rs.next()) {
                i++;
                Integer id =  rs.getInt(1);
                Integer qaydId =  rs.getInt(2);
                Integer hujjatId =  rs.getInt(3);
                Integer amalId =  rs.getInt(4);
                Integer hisob1Id =  rs.getInt(5);
                Integer hisob2Id =  rs.getInt(6);
                Integer valutaId =  rs.getInt(7);
                Integer tovarId =  rs.getInt(8);
                Double kurs = rs.getDouble(9);
                String barCode =  rs.getString(10);
                Double dona = rs.getDouble(11);
                Double narh = StringNumberUtils.yaxlitla(rs.getDouble(12), -2);
                Integer manba =  rs.getInt(13);
                String izoh =  rs.getString(14);
                Integer userId =  rs.getInt(15);
                Date dateTime =  sdf.parse(rs.getString(16));
                BarCode bc = GetDbData.getBarCode(barCode);
                Standart tovar = GetDbData.getTovar(tovarId);
                Balance balance = null;
                dona *= tovarDonasi(bc);
                narh /= kurs;
                if (tovarlarBalansi.containsKey(tovarId)) {
                    balance = tovarlarBalansi.get(tovarId);
                } else {
                    balance = new Balance();
                    balance.setId(tovarId);
                    balance.setText(tovar.getText());
                    tovarlarBalansi.put(tovarId, balance);
                    books.add(balance);
                }
                if (hisobBiznikimi(dokonlarRoyxati, hisob1Id)) {
                    Double adadBalance = balance.getAdadChiqim();
                    Double narhBalance = balance.getNarhChiqim();
                    balance.setAdadChiqim(adadBalance + dona);
                    balance.setNarhChiqim(narhBalance + narh * dona);
                }
                if (hisobBiznikimi(dokonlarRoyxati, hisob2Id)) {
                    Double adadBalance = balance.getAdadKirim();
                    Double narhBalance = balance.getNarhKirim();
                    balance.setAdadKirim(adadBalance + dona);
                    balance.setNarhKirim(narhBalance + narh * dona);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        for (Balance b: books) {
            b.setAdadJami(b.getAdadKirim() - b.getAdadChiqim());
            b.setNarhJami(b.getNarhKirim() - b.getNarhChiqim());
        }
        books.removeIf(balance -> balance.getAdadJami() == 0d);
        return books;
    }
    private ObservableList<Balance> dokonlarZaxirasiniOl(Connection connection, Integer tovar1Id) {
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        ObservableList<Balance> books=FXCollections.observableArrayList();
        String dokonlar = "";
        List<Integer> dokonlarRoyxati = new ArrayList<>();
        Map<Integer, Balance> hisoblarBalansi = new HashMap<>();
        Qoldiq qoldiq = new Qoldiq();
        String select = "Select group_concat(ID3, '') FROM DOKONLAR;";
        ResultSet rs = hisobKitobModels.getResultSet(connection, select);

        try{
            if (rs.next()) {
                dokonlar = rs.getString(1);
                dokonlarRoyxati = dokonlarRoyxati(dokonlar);
                dokonlarRoyxati.forEach(dokon -> {
                    Balance balance = new Balance();
                    Hisob hisob = GetDbData.getHisob(dokon);
                    if (hisob == null) {
                        Alerts.AlertString(dokon + " raqamli hisob yo`q");
                    }
                    balance.setId(hisob.getId());
                    balance.setText(hisob.getText());
                    hisoblarBalansi.put(hisob.getId(), balance);
                    books.add(balance);
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        select = "select * from HisobKitob where (hisob1 in("+ dokonlar +") or hisob2 in("+ dokonlar +")) and tovar = " + tovar1Id + " order by tovar";
        rs = hisobKitobModels.getResultSet(connection, select);
        int i=0;
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            while (rs.next()) {
                i++;
                Integer id =  rs.getInt(1);
                Integer qaydId =  rs.getInt(2);
                Integer hujjatId =  rs.getInt(3);
                Integer amalId =  rs.getInt(4);
                Integer hisob1Id =  rs.getInt(5);
                Integer hisob2Id =  rs.getInt(6);
                Integer valutaId =  rs.getInt(7);
                Integer tovarId =  rs.getInt(8);
                Double kurs = rs.getDouble(9);
                String barCode =  rs.getString(10);
                Double dona = rs.getDouble(11);
                Double narh = rs.getDouble(12);
                Integer manba =  rs.getInt(13);
                String izoh =  rs.getString(14);
                Integer userId =  rs.getInt(15);
                Date dateTime =  sdf.parse(rs.getString(16));
                BarCode bc = GetDbData.getBarCode(barCode);
                Standart tovar = GetDbData.getTovar(tovarId);
                Balance balance = null;
                dona *= tovarDonasi(bc);
                narh /= kurs;
                if (hisobBiznikimi(dokonlarRoyxati, hisob1Id)) {
                    balance = hisoblarBalansi.get(hisob1Id);
                    Double adadBalance = balance.getAdadChiqim();
                    Double narhBalance = balance.getNarhChiqim();
                    balance.setAdadChiqim(adadBalance + dona);
                    balance.setNarhChiqim(narhBalance + narh * dona);
                }
                if (hisobBiznikimi(dokonlarRoyxati, hisob2Id)) {
                    balance = hisoblarBalansi.get(hisob2Id);
                    Double adadBalance = balance.getAdadKirim();
                    Double narhBalance = balance.getNarhKirim();
                    balance.setAdadKirim(adadBalance + dona);
                    balance.setNarhKirim(narhBalance + narh * dona);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        for (Balance b: books) {
            b.setAdadJami(b.getAdadKirim() - b.getAdadChiqim());
            b.setNarhJami(b.getNarhKirim() - b.getNarhChiqim());
        }
        books.removeIf(balance -> balance.getAdadJami() == 0d);
        return books;
    }
    private List<Integer> dokonlarRoyxati(String dokonlar) {
        String[] dokonlarRoyxati = dokonlar.split(",");
        List<Integer> dokonlarInteger = new ArrayList<>();
        for (String s: dokonlarRoyxati) {
            dokonlarInteger.add(Integer.valueOf(s));
        }
        return dokonlarInteger;
    }

    private Boolean hisobBiznikimi(List<Integer> dokonlar, Integer hisobId) {
        Boolean bizniki = false;
        for (Integer dokon: dokonlar) {
            if (hisobId.equals(dokon)) {
                bizniki = true;
                break;
            }
        }
        return bizniki;
    }
    private double tovarDonasi(BarCode barCode) {
        double dona = 1.0;
        if (barCode !=null) {
            double adadBarCode2 = 0;
            dona *= barCode.getAdad();
            int tarkibInt = barCode.getTarkib();
            if (tarkibInt > 0) {
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
        }
        return dona;
    }

    private ObservableList<HisobKitob> balanceHisobKitobga() {
        ObservableList<Balance> balanceObservableList = zaxiraniOl(connection);
        ObservableList<HisobKitob> hisobKitobObservableList = FXCollections.observableArrayList();
        for (Balance b: balanceObservableList) {
            HisobKitob hisobKitob = new HisobKitob();
            hisobKitob.setTovar(b.getId());
            hisobKitob.setIzoh(b.getText());
            hisobKitob.setDona(b.getAdadJami());
            hisobKitob.setNarh(b.getNarhJami());
            hisobKitob.setKurs(1d);
            hisobKitobObservableList.add(hisobKitob);
        }
        return hisobKitobObservableList;
    }
    private ObservableList<HisobKitob> balanceHisobKitobga(ObservableList<Balance> balanceObservableList, Integer tovarId) {
        ObservableList<HisobKitob> hisobKitobObservableList = FXCollections.observableArrayList();
        for (Balance b: balanceObservableList) {
            HisobKitob hisobKitob = new HisobKitob();
            hisobKitob.setTovar(tovarId);
            hisobKitob.setHisob1(b.getId());
            hisobKitob.setIzoh(b.getText());
            hisobKitob.setDona(b.getAdadJami());
            hisobKitob.setNarh(b.getNarhJami());
            hisobKitobObservableList.add(hisobKitob);
        }
        return hisobKitobObservableList;
    }

}