package sample.Controller;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.Config.MySqlDBGeneral;
import sample.Data.Hisob;
import sample.Data.User;
import sample.Enums.ServerType;
import sample.Excel.HaqdorQarzdorExcel;
import sample.Model.HisobKitobModels;
import sample.Tools.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;

public class Haqdor extends Application {
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

    public Haqdor() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
        ibtido();
    }

    public Haqdor(Connection connection, User user) {
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
        tableViewAndoza.initHisobTableView();
        ObservableList<Hisob> observableList = hisoblarList(LocalDate.now());
        TableView<Hisob> tableView = tableViewAndoza.getHisobTableView();
        tableView.getColumns().remove(0);
        tableView.getColumns().get(0).setText("Haqdorlar");
        tableView.setItems(observableList);
        SetHVGrow.VerticalHorizontal(tableView);
        return tableView;
    }

    private ObservableList<Hisob> hisoblarList(LocalDate localDate) {
        DecimalFormat decimalFormat = new MoneyShow();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String localDateString = localDate.format(formatter);
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        ObservableList<Hisob> hisobObservableList = FXCollections.observableArrayList();
        String kirimHisoblari =
                "Select hisob2, sum(narh*if(tovar>0,dona,1)/kurs) as kirim from HisobKitob where dateTime<='" + localDateString + " 23:59:59' group by hisob2 order by kirim";
        ResultSet rs1 = hisobKitobModels.getResultSet(connection, kirimHisoblari);
        try {
            while (rs1.next()) {
                Integer id = rs1.getInt(1);
                Double balance = rs1.getDouble(2);
                Hisob hisob = GetDbData.getHisob(id);
                if (hisob != null) {
                    hisobObservableList.add(new Hisob(id, hisob.getText(), 0d, 0d, balance));
                } else {
                    System.out.println("Select Hisob topilmadi " + id);
                }
            }
            rs1.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String chiqimHisoblari =
                "Select hisob1, sum(narh*if(tovar>0,dona,1)/kurs) as chiqim from HisobKitob where dateTime<='" + localDateString + " 23:59:59' group by hisob1 order by chiqim";
        ResultSet rs2 = hisobKitobModels.getResultSet(connection, chiqimHisoblari);
        try {
            while (rs2.next()) {
                Integer id = rs2.getInt(1);
                Double balance = rs2.getDouble(2);
                Hisob hisob = GetDbData.hisobniTop(id, hisobObservableList);
                if (hisob != null) {
                    hisob.setBalans(hisob.getBalans() - balance);
                } else {
                    Hisob h = GetDbData.getHisob(id);
                    if (h!=null)
                    hisobObservableList.add(new Hisob(id, h.getText(), 0d, 0d, -balance));
                }
            }
            rs2.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (hisobObservableList.size()>0) {
            Comparator<Hisob> comparator = Comparator.comparing(Hisob::getBalans);
            Collections.sort(hisobObservableList, comparator);
        }
        hisobObservableList.removeIf(hisob -> StringNumberUtils.yaxlitla(hisob.getBalans(), -2 ) >= 0d);
        return hisobObservableList;
    }

    public TableView<Hisob> getTableView() {
        return tableView;
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
            ObservableList<Hisob> observableList = hisoblarList(LocalDate.now());
            tableView.setItems(observableList);
            tableView.refresh();
        });

        Button excel = buttons.getExcel();
        excel.setOnAction(e->{
            HaqdorQarzdorExcel haqdorQarzdorExcel = new HaqdorQarzdorExcel();
            haqdorQarzdorExcel.print(tableView.getItems());
        });

        return buttons;
    }

    public VBox getCenterPane() {
        return centerPane;
    }

}
