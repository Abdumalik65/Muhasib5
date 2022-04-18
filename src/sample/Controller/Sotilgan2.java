package sample.Controller;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import sample.Config.MySqlDBGeneral;
import sample.Data.Hisob;
import sample.Data.HisobKitob;
import sample.Data.Standart;
import sample.Data.User;
import sample.Enums.ServerType;
import sample.Excel.HisobStatisticExcel;
import sample.Model.HisobKitobModels;
import sample.Tools.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;

public class Sotilgan2 extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    HBox topPane = new HBox();
    VBox leftPane = new VBox();
    VBox centerPane = new VBox();
    VBox rightPane = new VBox();
    HBox bottomPane = new HBox();
    Tugmachalar tugmachalar;
    Connection connection;
    User user;
    int padding = 3;
    TableView<Hisob> tableView;


    public static void main(String[] args) {
        launch(args);
    }

    public Sotilgan2() {
        connection = new MySqlDBGeneral(ServerType.REMOTE).getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
        ibtido();
    }

    public Sotilgan2(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        String classSimpleName = getClass().getSimpleName();
        DasturlarRoyxati.dastur(connection, user, classSimpleName);
        ibtido();
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

    private void initData() {
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
        HBox.setHgrow(topPane, Priority.ALWAYS);
        VBox.setVgrow(topPane, Priority.ALWAYS);
        topPane.setPadding(new Insets(padding));
        topPane.getChildren().addAll();
    }

    private void initLeftPane() {
        HBox.setHgrow(leftPane, Priority.ALWAYS);
        VBox.setVgrow(leftPane, Priority.ALWAYS);
        leftPane.setPadding(new Insets(padding));
    }

    private void initCenterPane() {
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        VBox.setVgrow(centerPane, Priority.ALWAYS);
        centerPane.setPadding(new Insets(padding));
        tugmachalar = initTugmachalar();
        tableView = initTable();
        centerPane.getChildren().addAll(tugmachalar, tableView);
    }

    private void initRightPane() {
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        VBox.setVgrow(rightPane, Priority.ALWAYS);
        rightPane.setPadding(new Insets(padding));
        rightPane.getChildren().addAll();
    }

    private void initBottomPane() {
        HBox.setHgrow(bottomPane, Priority.ALWAYS);
        VBox.setVgrow(bottomPane, Priority.ALWAYS);
        bottomPane.setPadding(new Insets(padding));
        bottomPane.getChildren().addAll();
    }

    private void initBorderPane() {
        borderpane.setTop(topPane);
        borderpane.setLeft(leftPane);
        borderpane.setCenter(centerPane);
        borderpane.setRight(rightPane);
        borderpane.setBottom(bottomPane);
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Bir panel");
        scene = new Scene(borderpane, 600, 400);
        stage.setScene(scene);
    }

    public TableView<Hisob> initTable() {
        TableViewAndoza tableViewAndoza = new TableViewAndoza();
        ObservableList<Hisob> observableList = getTableData(connection);
        TableView<Hisob> tableView = tableViewAndoza.getHisobTableView();
        TableColumn<Hisob, String> textColumn = tableViewAndoza.getHisobTextColumn();
        textColumn.setText("Ko`p sotilgan\n  tovarlar");
        tableView.getColumns().addAll(
                textColumn,
                tableViewAndoza.getHisobKirimColumn(),
                tableViewAndoza.getHisobBalansColumn()
        );
        tableView.setItems(observableList);
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        tableView.setMinHeight(bounds.getHeight()-70);
        SetHVGrow.VerticalHorizontal(tableView);
        return tableView;
    }

    private ObservableList<HisobKitob> getTableData() {
        ObservableList<HisobKitob> observableList = FXCollections.observableArrayList();
        DecimalFormat decimalFormat = new MoneyShow();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        ObservableList<Hisob> hisobObservableList = FXCollections.observableArrayList();
        String select =
                "Select DISTINCT id3 from Dokonlar";
        ResultSet rs = hisobKitobModels.getResultSet(connection, select);
        try {
            while (rs.next()) {
                Integer id = rs.getInt(1);
                Hisob hisob = GetDbData.getHisob(id);
                if (hisob != null) {
                    hisobObservableList.add(new Hisob(id, hisob.getText(), 0d, 0d, 0d));
                } else {
                    System.out.println("Select Hisob topilmadi " + id);
                }
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (hisobObservableList.size() > 0) {
            boolean birinchi = true;
            String whereString = "(";
            if (hisobObservableList.size() > 0) {
                for (Hisob h : hisobObservableList) {
                    if (birinchi) {
                        birinchi = false;
                    } else {
                        whereString += " OR hisob2 = ";
                    }
                    whereString += h.getId();
                }
            }
            select =
                    "Select tovar, sum(narh*dona/kurs) as kirim from HisobKitob where " + whereString + " group by hisob1 order by chiqim";
            rs = hisobKitobModels.getResultSet(connection, select);
            try {
                while (rs.next()) {
                    Integer id = rs.getInt(1);
                    Double balance = rs.getDouble(2);
                    Hisob hisob = GetDbData.hisobniTop(id, hisobObservableList);
                    if (hisob != null) {
                        hisob.setBalans(hisob.getBalans() - balance);
                    } else {
                        Hisob h = GetDbData.getHisob(id);
                        if (h != null)
                            hisobObservableList.add(new Hisob(id, h.getText(), 0d, 0d, -balance));
                    }
                }
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (hisobObservableList.size() > 0) {
                Comparator<Hisob> comparator = Comparator.comparing(Hisob::getBalans);
                Collections.sort(hisobObservableList, comparator.reversed());
            }
            hisobObservableList.removeIf(hisob -> StringNumberUtils.yaxlitla(hisob.getBalans(), -2) <= 0d);
        }
        return observableList;
    }

    public Hisob findHisobKitob(ObservableList<Hisob> observableList, Integer tovarId) {
        Hisob hisob = null;
        for (Hisob h: observableList) {
            if (h.getId().equals(tovarId)) {
                hisob = h;
                break;
            }
        }
        return hisob;
    }
    private ObservableList<Hisob> getTableData(Connection connection) {
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        ObservableList<Hisob> hisobObservableList = FXCollections.observableArrayList();
        String select =
                "Select id3 from Dokonlar";
        ResultSet rs = hisobKitobModels.getResultSet(connection, select);
        try {
            while (rs.next()) {
                Integer id = rs.getInt(1);
                Hisob hisob = GetDbData.getHisob(id);
                if (hisob != null) {
                    hisobObservableList.add(new Hisob(id, hisob.getText(), 0d, 0d, 0d));
                } else {
                    System.out.println("Select Hisob topilmadi " + id);
                }
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        boolean birinchi = true;
        String whereString = "(";
        if (hisobObservableList.size()>0) {
            for (Hisob h: hisobObservableList) {
                if (birinchi) {
                    birinchi = false;
                } else {
                    whereString += " OR";
                }
                whereString += " Hisob2 = " + h.getId();
            }
            whereString += ")";
        }
        System.out.println(whereString);
        ObservableList<Hisob> observableList = FXCollections.observableArrayList();
        if (hisobObservableList.size()==0) {
            return observableList;
        }
        select = "Select tovar, sum(dona) as dona, sum(narh*dona/kurs) as narh from hisobkitob where tovar>0 and " + whereString + " group by tovar order by tovar";
        rs = hisobKitobModels.getResultSet(connection, select);
        try {
            while (rs.next()) {
                Standart tovar = GetDbData.getTovar(rs.getInt(1));
                Double dona = rs.getDouble(2);
                Double summa = rs.getDouble(3);
                Hisob hisob = new Hisob(
                        tovar.getId(),
                        tovar.getText(),
                        0d,
                        dona,
                        summa
                );
                observableList.add(hisob);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        whereString = whereString.replaceAll("Hisob2", "Hisob1");
        select = "Select tovar, sum(dona) as dona, sum(narh*dona/kurs) as narh from hisobkitob where tovar>0 and " + whereString + " and manba>0 group by tovar order by tovar";
        rs = hisobKitobModels.getResultSet(connection, select);
        try {
            while (rs.next()) {
                Standart tovar = GetDbData.getTovar(rs.getInt(1));
                Double dona = rs.getDouble(2);
                Double summa = rs.getDouble(3);
                Hisob hisob = findHisobKitob(observableList, tovar.getId());
                if (hisob != null) {
                    hisob.setChiqim(dona);
                    hisob.setBalans(summa);
                } else {
                    hisob = new Hisob(
                            tovar.getId(),
                            tovar.getText(),
                            dona,
                            0d,
                            0d
                    );
                    observableList.add(hisob);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        observableList.forEach(item->{
            Double aDouble = item.getBalans();
            aDouble = StringNumberUtils.yaxlitla(aDouble, -2);
            item.setBalans(aDouble);
        });
        observableList.removeIf(item -> item.getChiqim() == 0d);
        Comparator<Hisob> comparator = Comparator.comparing(Hisob::getBalans);
        Collections.sort(observableList, comparator.reversed());

        return observableList;
    }
    public Tugmachalar initTugmachalar() {
        Tugmachalar buttons = new Tugmachalar();
        buttons.getChildren().remove(0);
        buttons.getChildren().remove(1);
        Button refresh = buttons.getDelete();
        ImageView imageView = new PathToImageView("/sample/images/Icons/refresh1.png", 16, 16).getImageView();
        refresh.setGraphic(imageView);
        refresh.setText("Yangilash");
        refresh.setOnAction(e->{
            ObservableList<Hisob> observableList = getTableData(connection);
            tableView.setItems(observableList);
            tableView.refresh();
        });

        Button excel = buttons.getExcel();
        excel.setOnAction(e->{
            HisobStatisticExcel hisobStatisticExcel = new HisobStatisticExcel();
            hisobStatisticExcel.print(tableView.getItems());
        });

        return buttons;
    }

    public VBox getCenterPane() {
        return centerPane;
    }
}
