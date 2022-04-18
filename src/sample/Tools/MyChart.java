package sample.Tools;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import sample.Config.MySqlDB;
import sample.Config.MySqlDBGeneral;
import sample.Data.*;
import sample.Enums.ServerType;
import sample.Model.HisobKitobModels;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class MyChart extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    HBox topPane = new HBox();
    VBox leftPane = new VBox();
    VBox centerPane = new VBox();
    VBox rightPane = new VBox();
    HBox bottomPane = new HBox();
    Connection connection;
    HisobKitobModels hisobKitobModels = new HisobKitobModels();
    User user = new User(1, "admin", "", "admin");
    int padding = 3;


    public static void main(String[] args) {
        launch(args);
    }

    public MyChart() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
        ibtido();
    }

    public MyChart(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
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

    public void display(LocalDate localDate, Standart guruh, ObservableList<Standart3> guruhTarkibi) {
        Stage stage = new Stage();
        initStage(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Grafik diagramma");
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Sana");
        LineChart<String,Number> lineChart = new LineChart<String,Number>(xAxis,yAxis);
        lineChart.setTitle(guruh.getText());
        XYChart.Series series1 = getData(localDate, guruhTarkibi);
        series1.setName(guruh.getText());

        Scene scene  = new Scene(lineChart,800,600);
        lineChart.getData().addAll(series1);

        stage.setScene(scene);
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
        centerPane.getChildren().addAll();
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
        stage.initModality(Modality.APPLICATION_MODAL);
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        stage.setTitle("Bir panel");
        scene = new Scene(borderpane);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setOnCloseRequest(event -> {

        });
    }
    public XYChart.Series getData(LocalDate localDate, ObservableList<Standart3> s3List) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd.MM.yyyy");
        String dateString = dateFormatter.format(localDate);
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        ObservableList<HisobKitob> hisobKitobObservableList = hisobKitobModels.getAnyData(connection, "substr(dateTime,1,10) <= '" + dateString + "'","dateTime asc");
        XYChart.Series series = new XYChart.Series();
        String hkDateString = "";
        Double hkSummaCol = 0d;
        Double jamiSummaCol = 0d;
        XYChart.Data<String, Double> data = null;
        XYChart.Data<String, Double> data1 = null;
        for (HisobKitob hk: hisobKitobObservableList) {
            hkSummaCol = hk.getSummaCol();
            for (Standart3 s3: s3List) {
                if (s3.getId3().equals(hk.getHisob1())) {
                    jamiSummaCol = jamiSummaCol - hkSummaCol;
                }
                if (s3.getId3().equals(hk.getHisob2())) {
                    jamiSummaCol = jamiSummaCol + hkSummaCol;
                }
            }
            if (sdf2.format(hk.getDateTime()).equals(hkDateString)) {
                data.setYValue(jamiSummaCol);
            } else {
                hkDateString = sdf2.format(hk.getDateTime());
                data = new XYChart.Data(hkDateString, jamiSummaCol);
                series.getData().add(data);
            }
            data.setNode(new ShowCoordinatesNode2(hkDateString, jamiSummaCol));
        }
        return series;
    }
}
