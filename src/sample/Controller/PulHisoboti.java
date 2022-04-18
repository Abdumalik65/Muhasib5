package sample.Controller;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.Config.MySqlDBGeneral;
import sample.Data.*;
import sample.Enums.ServerType;
import sample.Model.HisobKitobModels;
import sample.Tools.DasturlarRoyxati;
import sample.Tools.GetDbData;
import sample.Tools.TableViewAndoza;
import sample.Tools.Hisobot2;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PulHisoboti extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    VBox centerPane = new VBox();
    TableView<HisobKitob> pulTable = new TableView<>();
    TableView<Hisob> pulTable2 = new TableView<>();
    ObservableList<HisobKitob> pulList = FXCollections.observableArrayList();
    ObservableList<Hisob> pulList2 = FXCollections.observableArrayList();
    Hisob hisob;

    Connection connection;
    User user;
    int padding = 3;


    public static void main(String[] args) {
        launch(args);
    }

    public PulHisoboti() {
        connection = new MySqlDBGeneral(ServerType.REMOTE).getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
        ibtido();
    }

    public PulHisoboti(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        String classSimpleName = getClass().getSimpleName();
        DasturlarRoyxati.dastur(connection, user, classSimpleName);
        ibtido();
    }

    public PulHisoboti(Connection connection, User user, Hisob hisob) {
        this.connection = connection;
        this.user = user;
        this.hisob = hisob;
        ibtido();
    }

    private void ibtido() {
        initData();
        initPulTable();
        initCenterPane();
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

    private void initData() {
        GetDbData.initData(connection);
        refreshHisobKitobTable(hisob);
    }

    private void initCenterPane() {
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        VBox.setVgrow(centerPane, Priority.ALWAYS);
        centerPane.getChildren().addAll(pulTable);
    }

    private void initBorderPane() {
        borderpane.setCenter(centerPane);
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Pul hisoboti");
        scene = new Scene(borderpane, 310, 300);
        stage.setScene(scene);
    }

    private void initPulTable() {
        HBox.setHgrow(pulTable, Priority.ALWAYS);
        VBox.setVgrow(pulTable, Priority.ALWAYS);
        pulTable.setPadding(new Insets(padding));
        TableViewAndoza tableViewAndoza = new TableViewAndoza();
        TableColumn<HisobKitob, Integer> valutaColumn = tableViewAndoza.getValutaColumn();
        valutaColumn.setMinWidth(150);
        valutaColumn.setStyle( "-fx-alignment: CENTER_LEFT;");
        TableColumn<HisobKitob, Double> narhColumn = tableViewAndoza.getNarhColumn();
        narhColumn.setMinWidth(80);
        pulTable.getColumns().addAll(valutaColumn, narhColumn);
        pulTable.setItems(pulList);
    }

    private void initPulTable2() {
        HBox.setHgrow(pulTable2, Priority.ALWAYS);
        VBox.setVgrow(pulTable2, Priority.ALWAYS);
        pulTable2.setPadding(new Insets(padding));
        TableViewAndoza tableViewAndoza = new TableViewAndoza();
        TableColumn<Hisob, String> valutaColumn = tableViewAndoza.getHisobTextColumn();
        valutaColumn.setMinWidth(150);
        valutaColumn.setStyle( "-fx-alignment: CENTER_LEFT;");
        TableColumn<Hisob, Double> balansColumn = tableViewAndoza.getHisobBalansColumn();
        balansColumn.setMinWidth(80);
        pulTable2.getColumns().addAll(valutaColumn, balansColumn);
        pulTable2.setItems(pulList2);
    }

    private void refreshHisobKitobTableEski(Hisob hisob) {
        Hisobot2 hisobot = new Hisobot2(connection,user);
        pulList.removeAll(pulList);
        pulList.addAll(hisobot.getPul(hisob.getId(), user.getId()));
        pulTable.setItems(pulList);
        pulTable.refresh();
    }

    private void refreshHisobKitobTable(Hisob hisob) {
        pulList = pulList3(hisob.getId());
        pulTable.setItems(pulList);
        pulTable.refresh();
    }

    private ObservableList<HisobKitob> pulList3(Integer hisobId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.now();
        String localDateString = localDate.format(formatter);
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        ObservableList<HisobKitob> hisobKitobObservableList = FXCollections.observableArrayList();
        String select =
                "Select valuta, sum(if(hisob2="+hisobId+",narh,0)) as kirim, sum(if(hisob1="+hisobId+",narh,0)) as chiqim from HisobKitob where tovar=0 and valuta>0 and dateTime<='" + localDateString + " 23:59:59' group by valuta";
        ResultSet rs = hisobKitobModels.getResultSet(connection, select);
            try {
                while (rs.next()) {
                    Integer id = rs.getInt(1);
                    Double kirim = rs.getDouble(2);
                    Double chiqim = rs.getDouble(3);
                    Double jami = kirim - chiqim;
                    Valuta v = GetDbData.getValuta(id);
                    HisobKitob hisobKitob = new HisobKitob();
                    hisobKitob.setValuta(v.getId());
                    hisobKitob.setIzoh(v.getValuta());
                    hisobKitob.setNarh(jami);
                    hisobKitobObservableList.add(hisobKitob);
                }
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return hisobKitobObservableList;
    }

}
