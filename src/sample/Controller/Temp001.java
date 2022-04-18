package sample.Controller;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import sample.Config.MySqlDBGeneral;
import sample.Data.*;
import sample.Enums.ServerType;
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
import java.util.Date;

public class Temp001 extends Application {
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

    public Temp001() {
        connection = new MySqlDBGeneral(ServerType.REMOTE).getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
        ibtido();
    }

    public Temp001(Connection connection, User user) {
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
        Tugmachalar leftButtons = initLeftButtons();
        leftTable = initLeftTable();
        leftPane.getChildren().addAll(leftButtons, leftTable);
    }

    private Tugmachalar initLeftButtons() {
        Tugmachalar buttons = new Tugmachalar();
        Button add = buttons.getAdd();
        Button delete = buttons.getDelete();
        Button edit = buttons.getEdit();
        Button excel= buttons.getExcel();
        TextField tovarTextField = new TextField();
        tovarTextField.setPromptText("Tovar nomi");
        tovarTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue!=null) {
                tovarTaftish(oldValue, newValue);
            }
        });
        HBox.setHgrow(tovarTextField, Priority.ALWAYS);
        TextField barCodeTextField = new TextField();
        barCodeTextField.setPromptText("Barcode");
        barCodeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue!=null) {
                barCodeTaftish(oldValue, newValue);
            }
        });
        HBox.setHgrow(barCodeTextField, Priority.ALWAYS);
        buttons.getChildren().removeAll(add, edit, delete);
        buttons.getChildren().addAll(tovarTextField, barCodeTextField);
        return buttons;
    }

    private void initCenterPane() {
        centerPane.setPadding(new Insets(padding));
        SetHVGrow.VerticalHorizontal(centerPane);
        Tugmachalar centerButtons = initCenterButtons();
        centerTable = initCenterTable();

        centerPane.getChildren().addAll(centerButtons, centerTable);
    }

    private Tugmachalar initCenterButtons() {
        Tugmachalar buttons = new Tugmachalar();
        Button add = buttons.getAdd();
        Button delete = buttons.getDelete();
        Button edit = buttons.getEdit();
        Button excel= buttons.getExcel();
        TextField dokonTextField = new TextField();
        dokonTextField.textProperty().addListener((observable, oldValue, newValue) -> {
        });
        dokonTextField.setPromptText("Do`kon nomi");
        HBox.setHgrow(dokonTextField, Priority.ALWAYS);
        buttons.getChildren().removeAll(add, edit, delete);
        buttons.getChildren().add(dokonTextField);
        return buttons;
    }

    private void initRightPane() {
        rightPane.setPadding(new Insets(padding));
        SetHVGrow.VerticalHorizontal(rightPane);
        Tugmachalar rightButtons = initRightButtons();
        rightTable = initRightTable();
        rightPane.getChildren().addAll(rightButtons, rightTable);
    }

    private Tugmachalar initRightButtons() {
        Tugmachalar buttons = new Tugmachalar();
        Button add = buttons.getAdd();
        Button delete = buttons.getDelete();
        Button edit = buttons.getEdit();
        Button excel= buttons.getExcel();
        buttons.getChildren().removeAll(add, edit, delete);
        return buttons;
    }

    private void initSplitPane() {
        splitPane.setPadding(new Insets(padding));
        SetHVGrow.VerticalHorizontal(splitPane);
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.getItems().addAll(leftPane, centerPane, rightPane);
        splitPane.setDividerPositions(.33,.66);
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
        stage.setTitle("Eng ko`p sotilgan\n   tovarlar");
        scene = new Scene(borderpane);
        stage.setScene(scene);
    }

    // 90 919 3020 Odil B27
    private TableView<HisobKitob> initLeftTable() {
        TableView<HisobKitob> tableView = new TableView<>();
        TableViewAndoza tableViewAndoza = new TableViewAndoza();
        tableView.setMinHeight(200);
        tableView.setMinHeight(540);
        tableView.setItems(getLeftTableData(LocalDate.now()));
        TableColumn<HisobKitob, Double> adadColumn = tableViewAndoza.getAdadColumn();
        TableColumn<HisobKitob, Double> narhColumn = tableViewAndoza.getNarhColumn();
        TableColumn<HisobKitob, String> izohColumn = tableViewAndoza.getIzoh2Column();
        TableColumn<HisobKitob, Double> chakanaNarhColumn = getChakanaNarhColumn();
        TableColumn<HisobKitob, Double> ulgurjiNarhColumn = getUlgurjiNarhColumn();
        izohColumn.setText("Ko`p sotilgan\n     tovarlar");
        tableView.getColumns().addAll(izohColumn, adadColumn, narhColumn, chakanaNarhColumn, ulgurjiNarhColumn);
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                tovar = GetDbData.getTovar(newValue.getTovar());
                System.out.println("leftTableAddListener");
                ObservableList<HisobKitob> observableList = getCenterTableData(LocalDate.now(), newValue.getTovar());
                centerTable.setItems(observableList);
                if (observableList.size()>0) {
                    hisob = GetDbData.getHisob(observableList.get(0).getHisob1());
                    rightTable.setItems(getRightTableData(LocalDate.now(), hisob.getId(), tovar.getId()));
                    rightTable.refresh();
                }
            }
        });
        SetHVGrow.VerticalHorizontal(tableView);
        return tableView;
    }

    private ObservableList<HisobKitob> getLeftTableData(LocalDate localDate) {
        ObservableList<HisobKitob> kirimList = FXCollections.observableArrayList();
        ObservableList<HisobKitob> chiqimList = FXCollections.observableArrayList();
        DecimalFormat decimalFormat = new MoneyShow();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String localDateString = localDate.format(formatter);
        StandartModels standartModels = new StandartModels("Tovar");
        String kirim =
                "select id, text from tovar ;";
        ResultSet rs1 = hisobKitobModels.getResultSet(connection, kirim);
        try {
            while (rs1.next()) {
                Integer id = rs1.getInt(1);
                String text = rs1.getString(2);
                ObservableList<HisobKitob> centerTableData = getCenterTableData(LocalDate.now(), id);
                Double jamiDona = 0d;
                Double jamiNarh = 0d;
                for (HisobKitob hk: centerTableData) {
                    jamiDona += hk.getDona();
                    jamiNarh += hk.getNarh();
                }
                HisobKitob hisobKitob = new HisobKitob(id, 0, 0, 4, 0, 0, 1, id, 1d, "", jamiDona, jamiNarh, 0, text, user.getId(), null);
                kirimList.add(hisobKitob);
            }
            rs1.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kirimList;
    }

    private ObservableList<HisobKitob> getLeftTableDataEski(LocalDate localDate) {
        ObservableList<HisobKitob> kirimList = FXCollections.observableArrayList();
        ObservableList<HisobKitob> chiqimList = FXCollections.observableArrayList();
        DecimalFormat decimalFormat = new MoneyShow();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String localDateString = localDate.format(formatter);
        String kirim =
                "select id, hisob2, barcode, sum(dona), sum(dona*narh/kurs) from Muhasib.hisobkitob where tovar>0 and (amal between 2 and 6) group by barcode order by tovar;";
        ResultSet rs1 = hisobKitobModels.getResultSet(connection, kirim);
        try {
            while (rs1.next()) {
                Integer id = rs1.getInt(1);
                Integer hisob2Id = rs1.getInt(2);
                BarCode barCode = GetDbData.getBarCode(rs1.getString(3));
                if (barCode!=null) {
                    Integer tovarId = barCode.getTovar();
                    Double adadDouble = hisobKitobModels.tovarDonasi(barCode) * rs1.getDouble(4);
                    Double narhDouble = rs1.getDouble(5);
                    Standart tovar = GetDbData.getTovar(tovarId);
                    if (tovar != null) {
                        HisobKitob hisobKitob = new HisobKitob(id, 0, 0, 4, 0, hisob2Id, 1, tovarId, 1d, "", adadDouble, narhDouble, 0, tovar.getText(), user.getId(), null);
                        kirimList.add(hisobKitob);
                    }
                }
            }
            rs1.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String chiqim =
                "select barcode, manba, sum(dona), sum(dona*narh/kurs) from Muhasib.hisobkitob where manba>0  group by manba order by manba;";
        ResultSet rs2 = hisobKitobModels.getResultSet(connection, kirim);
        try {
            while (rs2.next()) {
                BarCode barCode = GetDbData.getBarCode(rs2.getString(1));
                if (barCode!= null) {
                    Integer manba = rs1.getInt(2);
                    Integer tovarId = barCode.getTovar();
                    Double adad = hisobKitobModels.tovarDonasi(barCode) * rs2.getDouble(3);
                    Double narh = rs2.getDouble(4);
                    Standart tovar = GetDbData.getTovar(tovarId);
                    if (tovar != null) {
                        HisobKitob hisobKitob = new HisobKitob(0, 0, 0, 4, 0, 0, 1, 0, 1d, "", adad, narh, manba, "", 0, null);
                        chiqimList.add(hisobKitob);
                    }
                }
            }
            rs2.close();
            for (HisobKitob hk: chiqimList) {
                HisobKitob hisobKitob = getHisobKitob(kirimList, hk.getManba());
                if (hisobKitob!=null) {
                    Double donaDouble = hisobKitob.getDona();
                    Double narhDouble = hisobKitob.getNarh();
                    hisobKitob.setDona(donaDouble - hk.getDona());
                    hisobKitob.setNarh(narhDouble - hk.getNarh());
                }
            }
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

    private TableView<HisobKitob> initCenterTable() {
        TableView<HisobKitob> tableView = new TableView<>();
        TableViewAndoza tableViewAndoza = new TableViewAndoza();
        TableColumn<HisobKitob, String> izohColumn = tableViewAndoza.getIzoh2Column();
        izohColumn.setMinWidth(200);
        TableColumn<HisobKitob, Integer> hisob1Column = tableViewAndoza.getHisob1Column();
        TableColumn<HisobKitob, Double> adadColumn = tableViewAndoza.getAdadColumn();
        TableColumn<HisobKitob, Double> narhColumn = tableViewAndoza.getNarhColumn();
        tableView.getColumns().addAll(hisob1Column, adadColumn, narhColumn);
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                hisob = GetDbData.getHisob(newValue.getHisob1());
                System.out.println("centerTableAddListener");
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
        TableColumn<HisobKitob, String> textColumn = tableViewAndoza.getIzoh2Column();
        TableColumn<HisobKitob, String> barCodeColumn = tableViewAndoza.getBarCodeColumn();
        TableColumn<HisobKitob, Double> adadColumn = tableViewAndoza.getAdadColumn();
        TableColumn<HisobKitob, Double> narhColumn = tableViewAndoza.getNarhColumn();
        textColumn.setText("Eng ko`p\n   sotilgan tovarlar");
        textColumn.setMinWidth(200);
        tableView.getColumns().addAll(sanaColumn, textColumn, barCodeColumn, adadColumn, narhColumn);
        if (tovar != null && hisob != null) {
            tableView.setItems(getRightTableData(LocalDate.now(), hisob.getId(), tovar.getId()));
            tableView.refresh();
        }
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("leftTableAddListener");
        });
        SetHVGrow.VerticalHorizontal(tableView);
        return tableView;
    }

    private ObservableList<HisobKitob> getCenterTableData(LocalDate localDate, Integer tovarId) {
        ObservableList<HisobKitob> observableList = FXCollections.observableArrayList();
        DecimalFormat decimalFormat = new MoneyShow();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String kirimHisoblari =
                "select id3, text from dokonlar;";
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
            String kirimHisoblari =
                    "select datetime, izoh, barcode, dona, manba  from hisobkitob where hisob1=" + hisob1Id + " and tovar = " + tovarId + " and manba>0 order by dateTime, tovar;";
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
                    HisobKitob hisobKitob = new HisobKitob(0, 0, 0, 4, hisob1Id, 0, 1, tovarId, 1d, bc.getBarCode(), adad, 0d, manba, birlik.getText(), user.getId(), date);
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
    private ObservableList<HisobKitob> getBottomTableData(LocalDate localDate, Integer hisob1Id, String barCodeString) {
        ObservableList<HisobKitob> observableList = FXCollections.observableArrayList();
        BarCode bc = null;
        if (tovar != null) {
            bc = GetDbData.getBarCode(tovar.getId());
            System.out.println("BarCode mavjud emas: " + bc.getBarCode());
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

    private GridPane initGridPane() {
        GridPane gridPane = new GridPane();
        SetHVGrow.VerticalHorizontal(gridPane);
        Integer row = 0;

        gridPane.add(leftTable, 0, row, 1, 2);
        gridPane.add(centerTable, 1, row, 1, 1);

        row++;
        gridPane.add(rightTable, 1, row, 1, 1);
        return gridPane;
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
    private  TableColumn<HisobKitob, Double> getChakanaNarhColumn() {
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
            HisobKitob hk = event.getRowValue();
            Double newValue = event.getNewValue();
            if (newValue != null) {
                narhYoz(hk.getTovar(), 1, newValue/hk.getKurs());
            }
            event.getTableView().getSelectionModel().select(hk);
            event.getTableView().scrollTo(hk);
            event.getTableView().requestFocus();
            event.getTableView().refresh();
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
            HisobKitob hk = event.getRowValue();
            Double newValue = event.getNewValue();
            if (newValue != null) {
                narhYoz(hk.getTovar(), 2, newValue/hk.getKurs());
            }
            event.getTableView().getSelectionModel().select(hk);
            event.getTableView().scrollTo(hk);
            event.getTableView().requestFocus();
            event.getTableView().refresh();
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

    private void tovarTaftish(String oldValue, String newValue) {
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

    private void barCodeTaftish(String oldValue, String newValue) {
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
}

