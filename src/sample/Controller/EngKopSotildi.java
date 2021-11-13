package sample.Controller;

import com.sun.xml.bind.api.impl.NameConverter;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import sample.Config.MySqlDBGeneral;
import sample.Data.*;
import sample.Enums.ServerType;
import sample.Model.HisobKitobModels;
import sample.Tools.GetDbData;
import sample.Tools.GetTableView2;
import sample.Tools.MoneyShow;
import sample.Tools.SetHVGrow;

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

    ObservableList leftTableData;
    ObservableList centerTableData;
    ObservableList rightTableData;
    HisobKitobModels hisobKitobModels = new HisobKitobModels();

    Connection connection;
    User user;
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
        ibtido();
    }

    private void ibtido() {
        initTopPane();
        initLeftPane();
        initCenterPane();
        initRightPane();
        initBottomPane();
        initBorderPane();
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
        splitPane.getItems().addAll(centerPane, rightPane);
        splitPane.setDividerPositions(20);
    }

    private void initBottomPane() {}

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
        stage.setTitle("Eng ko`p sotilgan tovarlar");
        scene = new Scene(borderpane);
        stage.setScene(scene);
    }
// 90 919 3020 Odil B27
    private TableView<HisobKitob> initLeftTable() {
        TableView<HisobKitob> tableView = new TableView<>();
        GetTableView2 getTableView2 = new GetTableView2();
        tableView.setMinHeight(200);
        tableView.setMinHeight(540);
        tableView.setItems(getLeftTableData(LocalDate.now(), 1));
        TableColumn<HisobKitob, String> barCodeColumn = getTableView2.getBarCodeColumn();
        barCodeColumn.setText("Eng ko`p sotilgan tovarlar");
        TableColumn<HisobKitob, Double> adadColumn = getTableView2.getAdadColumn();
        TableColumn<HisobKitob, String> izohColumn = getTableView2.getIzoh2Column();
        tableView.getColumns().addAll(izohColumn, adadColumn);
        tableView.getSelectionModel().selectedItemProperty().addListener((observable ,oldValue, newValue)->{
            if (newValue != null) {
                System.out.println("leftTableAddListener");
                ObservableList<HisobKitob> observableList = getCenterTableData(LocalDate.now(), newValue.getTovar());
                centerTable.setItems(observableList);
            }
        });
        SetHVGrow.VerticalHorizontal(tableView);
        return  tableView;
    }

    private ObservableList<HisobKitob> getLeftTableData(LocalDate localDate, Integer limit) {
        ObservableList<HisobKitob> observableList = FXCollections.observableArrayList();
        DecimalFormat decimalFormat = new MoneyShow();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String localDateString = localDate.format(formatter);
        String kirimHisoblari =
                "select tovar as tovar, sum(dona) as adad from muhasib.hisobkitob where tovar>0 and amal = 4 and manba>0 group by tovar order by adad desc;";
        ResultSet rs1 = hisobKitobModels.getResultSet(connection, kirimHisoblari);
        try {
            while (rs1.next()) {
                Integer tovarId = rs1.getInt(1);
                Double adad = rs1.getDouble(2);
                Standart tovar = GetDbData.getTovar(tovarId);
                HisobKitob hisobKitob = new HisobKitob(0, 0, 0, 4, 0, 0, 1, tovar.getId(), 1d, "", adad, 0d, 0, tovar.getText(), user.getId(), null);
                observableList.add(hisobKitob);
            }
            rs1.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return observableList;
    }

    private TableView<HisobKitob> initCenterTable() {
        TableView<HisobKitob> tableView = new TableView<>();
        GetTableView2 getTableView2 = new GetTableView2();
        TableColumn<HisobKitob, String> izohColumn = getTableView2.getIzoh2Column();
        izohColumn.setMinWidth(200);
        TableColumn<HisobKitob, Integer> hisob1Column = getTableView2.getHisob1Column();
        TableColumn<HisobKitob, Double> adadColumn = getTableView2.getAdadColumn();
        tableView.getColumns().addAll(hisob1Column, izohColumn, adadColumn);
        tableView.getSelectionModel().selectedItemProperty().addListener((observable ,oldValue, newValue)->{
            if (newValue != null) {
                System.out.println("centerTableAddListener");
                HisobKitob hisobKitob = leftTable.getSelectionModel().getSelectedItem();
                ObservableList<HisobKitob> observableList = getRightTableData(LocalDate.now(), newValue.getHisob1(), hisobKitob.getBarCode());
                rightTable.setItems(observableList);
                rightTable.refresh();
            }
        });
        SetHVGrow.VerticalHorizontal(tableView);
        return  tableView;
    }

    private ObservableList<HisobKitob> getCenterTableData(LocalDate localDate, Integer tovarId) {
        ObservableList<HisobKitob> observableList = FXCollections.observableArrayList();
        DecimalFormat decimalFormat = new MoneyShow();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String localDateString = localDate.format(formatter);
        Standart tovar = GetDbData.getTovar(tovarId);
        String kirimHisoblari =
                "select hisob1 as hisob1, sum(dona) as dona from muhasib.hisobkitob where tovar = " + tovarId + " and amal = 4 and manba>0 group by hisob1 order by dona desc;";
        ResultSet rs1 = hisobKitobModels.getResultSet(connection, kirimHisoblari);
        try {
            while (rs1.next()) {
                Integer id = rs1.getInt(1);
                Double adad = rs1.getDouble(2);
                HisobKitob hisobKitob = new HisobKitob(0, 0, 0, 4, id, 0, 1, 0, 1d, "", adad, 0d, 0, tovar.getText(), user.getId(), null);
                observableList.add(hisobKitob);
            }
            rs1.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return observableList;
    }

    private TableView<HisobKitob> initRightTable() {
        TableView<HisobKitob> tableView = new TableView<>();
        GetTableView2 getTableView2 = new GetTableView2();
        tableView.setMinHeight(200);
        tableView.setMinHeight(540);
        TableColumn<HisobKitob, Date> sanaColumn = getTableView2.getDateTimeColumn();
        TableColumn<HisobKitob, String> textColumn = getTableView2.getIzoh2Column();
        textColumn.setText("Eng ko`p sotilgan tovarlar");
        textColumn.setMinWidth(200);
        TableColumn<HisobKitob, Double> adadColumn = getTableView2.getAdadColumn();
        tableView.getColumns().addAll(sanaColumn, textColumn, adadColumn);
        tableView.getSelectionModel().selectedItemProperty().addListener((observable ,oldValue, newValue)->{
            System.out.println("leftTableAddListener");
        });
        SetHVGrow.VerticalHorizontal(tableView);
        return  tableView;
    }

    private ObservableList<HisobKitob> getRightTableData(LocalDate localDate, Integer hisob1Id, String barCode) {
        ObservableList<HisobKitob> observableList = FXCollections.observableArrayList();
        DecimalFormat decimalFormat = new MoneyShow();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String localDateString = localDate.format(formatter);
        String kirimHisoblari =
                "select datetime, izoh, dona from muhasib.hisobkitob where hisob1="+ hisob1Id +" and barCode='" + barCode + "' and amal = 4 and manba>0 order by dateTime desc;";
        ResultSet rs1 = hisobKitobModels.getResultSet(connection, kirimHisoblari);
        try {
            while (rs1.next()) {
                Date date = sdf.parse(rs1.getString(1));
                String text = rs1.getString(2);
                Double adad = rs1.getDouble(3);
                HisobKitob hisobKitob = new HisobKitob(0, 0, 0, 4, hisob1Id, 0, 1, 0, 1d, barCode, adad, 0d, 0, text ,user.getId(), date);
                observableList.add(hisobKitob);
            }
            rs1.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return observableList;
    }
}
