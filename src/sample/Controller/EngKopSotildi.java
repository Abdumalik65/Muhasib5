package sample.Controller;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import sample.Config.MySqlDBGeneral;
import sample.Data.*;
import sample.Enums.ServerType;
import sample.Model.HisobKitobModels;
import sample.Tools.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class EngKopSotildi extends Application {
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

    ObservableList leftTableData;
    ObservableList centerTableData;
    ObservableList rightTableData;
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

    public EngKopSotildi() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
        ibtido();
    }

    public EngKopSotildi(Connection connection, User user) {
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
        gridPane = initGridPane();
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
        leftPane.getChildren().add(leftTable);
    }

    private void initCenterPane() {
        centerPane.setPadding(new Insets(padding));
        SetHVGrow.VerticalHorizontal(centerPane);
        centerTable = initCenterTable();
        centerPane.getChildren().add(centerTable);
    }

    private void initRightPane() {
        rightPane.setPadding(new Insets(padding));
        SetHVGrow.VerticalHorizontal(rightPane);
        rightTable = initRightTable();
        rightPane.getChildren().add(rightTable);
    }

    private void initSplitPane() {
        splitPane.setPadding(new Insets(padding));
        SetHVGrow.VerticalHorizontal(splitPane);
        splitPane.setOrientation(Orientation.VERTICAL);
        splitPane.getItems().addAll(centerTable, rightTable);
        splitPane.setDividerPositions(20);
    }

    private void initBottomPane() {
    }

    private void initBorderPane() {
        initSplitPane();
        borderpane.setLeft(leftPane);
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
        tableView.setItems(getLeftTableData(LocalDate.now(), 1));
        TableColumn<HisobKitob, Double> adadColumn = tableViewAndoza.getAdadColumn();
        TableColumn<HisobKitob, String> izohColumn = tableViewAndoza.getIzoh2Column();
        izohColumn.setText("Ko`p sotilgan\n     tovarlar");
        tableView.getColumns().addAll(izohColumn, adadColumn);
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                tovar = GetDbData.getTovar(newValue.getTovar());
                System.out.println("leftTableAddListener");
                ObservableList<HisobKitob> observableList = getCenterTableData(LocalDate.now(), newValue.getTovar());
                centerTable.setItems(observableList);
                if (observableList.size()>0) {
                    hisob = GetDbData.getHisob(observableList.get(0).getHisob1());
                    rightTable.setItems(getRightTableData(LocalDate.now(), hisob.getId(), tovar.getId()));
                }
            }
        });
        SetHVGrow.VerticalHorizontal(tableView);
        return tableView;
    }

    private ObservableList<HisobKitob> getLeftTableData(LocalDate localDate, Integer limit) {
        ObservableList<HisobKitob> observableList = FXCollections.observableArrayList();
        DecimalFormat decimalFormat = new MoneyShow();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String localDateString = localDate.format(formatter);
        String kirimHisoblari =
                "select tovar as tovar, sum(dona) as adad from hisobkitob where tovar>0 and amal = 4 and manba>0 group by tovar order by adad desc;";
        ResultSet rs1 = hisobKitobModels.getResultSet(connection, kirimHisoblari);
        try {
            while (rs1.next()) {
                Integer tovarId = rs1.getInt(1);
                Double adad = rs1.getDouble(2);
                Standart tovar = GetDbData.getTovar(tovarId);
                if (tovar!=null) {
                    HisobKitob hisobKitob = new HisobKitob(0, 0, 0, 4, 0, 0, 1, tovar.getId(), 1d, "", adad, 0d, 0, tovar.getText(), user.getId(), null);
                    observableList.add(hisobKitob);
                }
            }
            rs1.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (observableList.size()>0) {
            tovar = GetDbData.getTovar(observableList.get(0).getTovar());
        }
        return observableList;
    }

    private TableView<HisobKitob> initCenterTable() {
        TableView<HisobKitob> tableView = new TableView<>();
        TableViewAndoza tableViewAndoza = new TableViewAndoza();
        TableColumn<HisobKitob, String> izohColumn = tableViewAndoza.getIzoh2Column();
        izohColumn.setMinWidth(200);
        TableColumn<HisobKitob, Integer> hisob1Column = tableViewAndoza.getHisob1Column();
        TableColumn<HisobKitob, Double> adadColumn = tableViewAndoza.getAdadColumn();
        tableView.getColumns().addAll(hisob1Column, izohColumn, adadColumn);
        if ( tovar != null ) {
            tableView.setItems(getCenterTableData(LocalDate.now(), tovar.getId()));
        }
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                hisob = GetDbData.getHisob(newValue.getHisob1());
                System.out.println("centerTableAddListener");
                HisobKitob hisobKitob = leftTable.getSelectionModel().getSelectedItem();
                ObservableList<HisobKitob> observableList = getRightTableData(LocalDate.now(), hisob.getId(), tovar.getId());
                rightTable.setItems(observableList);
                rightTable.refresh();
            }
        });
        SetHVGrow.VerticalHorizontal(tableView);
        return tableView;
    }

    private ObservableList<HisobKitob> getCenterTableData(LocalDate localDate, Integer tovarId) {
        ObservableList<HisobKitob> observableList = FXCollections.observableArrayList();
        DecimalFormat decimalFormat = new MoneyShow();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String localDateString = localDate.format(formatter);
        String kirimHisoblari =
                "select hisob1 as hisob1, sum(dona) as dona from hisobkitob where tovar = " + tovarId + " and amal = 4 and manba>0 group by hisob1 order by dona desc;";
        ResultSet rs1 = hisobKitobModels.getResultSet(connection, kirimHisoblari);
        try {
            while (rs1.next()) {
                Integer id = rs1.getInt(1);
                Double adad = rs1.getDouble(2);
                HisobKitob hisobKitob = new HisobKitob(0, 0, 0, 4, id, 0, 1, tovarId, 1d, "", adad, 0d, 0, tovar.getText(), user.getId(), null);
                observableList.add(hisobKitob);
            }
            rs1.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (observableList.size()>0) {
            hisob = GetDbData.getHisob(observableList.get(0).getHisob1());
        }
        return observableList;
    }

    private TableView<HisobKitob> initRightTable() {
        TableView<HisobKitob> tableView = new TableView<>();
        TableViewAndoza tableViewAndoza = new TableViewAndoza();
        tableView.setMinHeight(200);
        tableView.setMinHeight(540);
        TableColumn<HisobKitob, Date> sanaColumn = tableViewAndoza.getDateTimeColumn();
        TableColumn<HisobKitob, String> textColumn = tableViewAndoza.getIzoh2Column();
        TableColumn<HisobKitob, String> barCodeColumn = tableViewAndoza.getBarCodeColumn();
        textColumn.setText("Eng ko`p\n   sotilgan tovarlar");
        textColumn.setMinWidth(200);
        TableColumn<HisobKitob, Double> adadColumn = tableViewAndoza.getAdadColumn();
        tableView.getColumns().addAll(sanaColumn, textColumn, barCodeColumn, adadColumn);
        if (tovar != null && hisob != null) {
            tableView.setItems(getRightTableData(LocalDate.now(), hisob.getId(), tovar.getId()));
        }
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("leftTableAddListener");
        });
        SetHVGrow.VerticalHorizontal(tableView);
        return tableView;
    }

    private ObservableList<HisobKitob> getRightTableData(LocalDate localDate, Integer hisob1Id, Integer tovarId) {
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
                    "select datetime, izoh, barcode, dona, manba  from hisobkitob where hisob1=" + hisob1Id + " and tovar = " + tovarId + " and amal = 4 and manba>0 order by dateTime, tovar;";
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
}

