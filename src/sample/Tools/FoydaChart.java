package sample.Tools;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.TableView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import sample.Config.MySqlDBLocal;
import sample.Data.*;
import sample.Model.HisobKitobModels;
import sample.Temp.ShowCoordinatesNode2;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;

public class FoydaChart extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    HBox topPane = new HBox();
    VBox leftPane = new VBox();
    VBox centerPane = new VBox();
    VBox rightPane = new VBox();
    HBox bottomPane = new HBox();
    Connection connection;
    User user = new User(1, "admin", "", "admin");
    int padding = 3;

    TableView<HisobKitob> tableView;
    ObservableList<HisobKitob> hisobKitobObservableList = FXCollections.observableArrayList();
    final CategoryAxis xAxis = new CategoryAxis();
    final NumberAxis yAxis = new NumberAxis();
    final BarChart<String,Number> chart = new BarChart<String,Number>(xAxis,yAxis);
    XYChart.Series series1;

    public static void main(String[] args) {
        launch(args);
    }

    public FoydaChart() {
        connection = new MySqlDBLocal().getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
        ibtido();
    }

    public FoydaChart(Connection connection, User user) {
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
        series1 = getData();
        SetHVGrow.VerticalHorizontal(chart);
        chart.getData().addAll(series1);
        chart.setMinWidth(700);
        chart.setMinHeight(500);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initStage(primaryStage);
        stage.show();
    }

    public void display(LocalDate localDate, Standart guruhNomi, ObservableList<Standart3> guruhTarkibi) {
        stage = new Stage();
        initStage(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Grafik diagramma");

        chart.getData().addAll(series1);
        scene  = new Scene(borderpane,800,600);

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
        tableView = initTableView();
        leftPane.getChildren().add(tableView);
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
        rightPane.getChildren().addAll(chart);
    }

    private void initBottomPane() {
        HBox.setHgrow(bottomPane, Priority.ALWAYS);
        VBox.setVgrow(bottomPane, Priority.ALWAYS);
        bottomPane.setPadding(new Insets(padding));
        bottomPane.getChildren().addAll();
    }

    private void initBorderPane() {
        SetHVGrow.VerticalHorizontal(borderpane);
        borderpane.setTop(topPane);
        borderpane.setLeft(leftPane);
        borderpane.setCenter(centerPane);
        borderpane.setRight(rightPane);
        borderpane.setBottom(bottomPane);
    }

    private void initStage(Stage primaryStage) {
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        stage = primaryStage;
        stage.setX(bounds.getMinX() - 3);
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth() + 7);
        stage.setHeight(bounds.getHeight() + 6);
        stage.setTitle("Diagramma");
        scene = new Scene(borderpane);
        stage.setScene(scene);
    }

    public XYChart.Series getData() {
        XYChart.Series series = new XYChart.Series();
        XYChart.Data<String, Double> data = null;
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        ResultSet rs = hisobKitobModels.getResultSet(connection, "SELECT barcode, sum(dona) as adad, sum(narh/kurs) as summa FROM Muhasib.HisobKitob where amal=9 group by barcode order by summa desc limit 20");
        try {
            Integer id = 1;
            while (rs.next()) {
                System.out.println(rs.getString(1) + "| " + rs.getDouble(2) + "| " + rs.getDouble(3));
                String s = rs.getString(1);
                Double dona = rs.getDouble(2);
                Double narh = rs.getDouble(3);
                BarCode barCode = GetDbData.getBarCode(s);
                if (barCode != null) {
                    Standart tovar = GetDbData.getTovar(barCode.getTovar());
                    if (tovar != null) {
                        String s1 = tovar.getText().substring(0, 10);
                        data = new XYChart.Data<>(id.toString(), narh);
                        data.setNode(new ShowCoordinatesNode2(tovar.getText(), narh));
                        series.getData().add(data);
                        HisobKitob hisobKitob = new HisobKitob(
                                id, 0, 0, 0, 0, 0, 1, 0, 0d, barCode.getBarCode(), dona, narh, 0, tovar.getText(), user.getId(), new Date()
                        );
                        hisobKitobObservableList.add(hisobKitob);
                        id++;
                    }
                }
            }
            rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return  series;
    }

    private TableView<HisobKitob> initTableView() {
        GetTableView2 getTableView2 = new GetTableView2();
        TableView<HisobKitob> hisobKitobTableView = new TableView<>();
        SetHVGrow.VerticalHorizontal(hisobKitobTableView);
        hisobKitobTableView.getColumns().addAll(
                getTableView2.getHKIdColumn(),
                getTableView2.getBarCodeColumn(),
                getTableView2.getIzoh2Column(),
                getTableView2.getAdadColumn(),
                getTableView2.getNarhColumn()
        );
        hisobKitobTableView.setItems(hisobKitobObservableList);
        return hisobKitobTableView;
    }

    private StackPane getStackPane(String x, Double y) {
        StackPane stackPane = new StackPane();

        return stackPane;
    }
}
