package sample.Controller;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import sample.Config.MySqlDB;
import sample.Config.MySqlDBGeneral;
import sample.Data.*;
import sample.Model.HisobKitobModels;
import sample.Model.HisobModels;
import sample.Model.QaydnomaModel;
import sample.Model.Standart3Models;
import sample.Tools.GetDbData;
import sample.Tools.GetTableView2;
import sample.Tools.MoneyShow;
import java.sql.Connection;
import java.util.Collections;
import java.util.Comparator;

public class Hisobot1 extends Application {
    Stage stage;
    BorderPane borderpane = new BorderPane();
    SplitPane splitPane = new SplitPane();
    VBox rightPane = new VBox();
    VBox centerPane = new VBox();
    VBox leftPane = new VBox();
    HBox jamiHBox = new HBox();
    Label jamiLabel = new Label();
    Font font = Font.font("Arial", FontWeight.BOLD,20);
    int padding = 3;

    TableView<Hisob> hisobTableView;
    TableView<QaydnomaData> qaydnomaTableView;
    TableView<HisobKitob> hisobKitobTableView;

    ObservableList<Hisob> hisobObservableList;
    ObservableList<QaydnomaData> qaydnomaObservableList;
    ObservableList<HisobKitob> hisobKitobObservableList;

    QaydnomaModel qaydnomaModel = new QaydnomaModel();
    HisobKitobModels hisobKitobModels = new HisobKitobModels();

    Hisob hisobCursor;

    Connection connection;
    User user;
    GetTableView2 getTableView2 = new GetTableView2();


    public static void main(String[] args) {
        launch(args);
    }

    public Hisobot1() {
        connection = new MySqlDB().getDbConnection();
        initConnection();
        ibtido();
    }

    public Hisobot1(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        ibtido();
    }

    private void ibtido() {
        getTableView2.initTableViews();
        GetDbData.initData(connection);
        initData();
        initHisobTableView();
        initQaydnomaTableView();
        initHisobKitobTableView();
        initLeftPane();
        initCenterPane();
        initRightPane();
        initSplitPane();
        initBottomPane();
        initBorderPane();
    }

    public void display() {
        stage = new Stage();
        initStage(stage);
        stage.show();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initStage(primaryStage);
        stage.show();
    }
    private void initBottomPane() {
    }

    private void initSplitPane() {
        HBox.setHgrow(splitPane, Priority.ALWAYS);
        VBox.setVgrow(splitPane, Priority.ALWAYS);
        splitPane.setDividerPositions(.3, .7);
        splitPane.getItems().addAll(leftPane, rightPane);
    }

    private void initLeftPane() {
        leftPane.setMinWidth(250);
        leftPane.setMaxWidth(250);
        leftPane.setPadding(new Insets(padding));
        HBox.setHgrow(leftPane, Priority.ALWAYS);
        VBox.setVgrow(leftPane, Priority.ALWAYS);
        initJamiHBox();
        leftPane.getChildren().addAll(hisobTableView, jamiHBox);
    }

    private void initJamiHBox() {
        jamiHBox.setPadding(new Insets(padding));
        HBox.setHgrow(jamiHBox, Priority.ALWAYS);
        Label label = new Label("Jami ");
        Pane pane = new Pane();
        HBox.setHgrow(pane, Priority.ALWAYS);
        label.setFont(font);
        jamiLabel.setFont(font);
        double jami = 0.0;
        for (Hisob h: hisobObservableList) {
            jami += h.getBalans();
        }
        String jamiString = new MoneyShow().format(jami);
        if (jamiString.trim().equals("-0")) {
            jamiString = "0";
        }
        jamiLabel.setText(jamiString);
        jamiHBox.getChildren().addAll(label, pane, jamiLabel);
    }

    private void initCenterPane() {
        centerPane.setPadding(new Insets(padding));
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        VBox.setVgrow(centerPane, Priority.ALWAYS);
    }

    private void initRightPane() {
        rightPane.setPadding(new Insets(padding));
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        VBox.setVgrow(rightPane, Priority.ALWAYS);
        rightPane.getChildren().addAll(qaydnomaTableView, hisobKitobTableView);
    }

    public void initConnection() {
    }

    private void initData() {
        HisobModels hisobModels = new HisobModels();
        Standart3Models standart3Models = new Standart3Models();
        standart3Models.setTABLENAME("CheklanganHisobTarkibi");
        ObservableList<Standart3> guruhTarkibi = standart3Models.getAnyData(connection, "id2 = " + user.getId(), "");
        hisobObservableList = hisobModels.get_data(connection, guruhTarkibi);
        for (Hisob h: hisobObservableList) {
            h.setBalans(hisobBalans(h.getId()));
        }
        if (hisobObservableList.size()>0) {
            hisobCursor = hisobObservableList.get(0);
            qaydnomaObservableList = getQaydFromHisobKitob(hisobCursor.getId());
        }
        if (qaydnomaObservableList.size()>0) {
            QaydnomaData qaydnomaData = qaydnomaObservableList.get(0);
            if (qaydnomaData.getChiqimId().equals(hisobCursor.getId())) {
                hisobKitobObservableList = hisobKitobModels.getAnyData(connection, "qaydid = " + qaydnomaData.getId() + " AND hisob1 = " + hisobCursor.getId(), "");
                hisobKitobObservableList.addAll(hisobKitobModels.getAnyData(connection, "qaydid = " + qaydnomaData.getId() + " AND  hisob2 = " + hisobCursor.getId(), ""));
            } else {
                hisobKitobObservableList = hisobKitobModels.getAnyData(connection, "qaydid = " + qaydnomaData.getId() + " AND hisob2 = " + hisobCursor.getId(), "");
                hisobKitobObservableList.addAll(hisobKitobModels.getAnyData(connection, "qaydid = " + qaydnomaData.getId() + " AND  hisob1 = " + hisobCursor.getId(), ""));
            }
            hkKirimChiqim();
            yigindi(hisobKitobObservableList);
        }
    }

    private double hisobBalans(int hisobId) {
        double kirim = 0.0;
        double chiqim = 0.0;
        double balans = 0.0;
        ObservableList<HisobKitob> kirimObservableList;
        ObservableList<HisobKitob> chiqimObservableList;
        kirimObservableList = hisobKitobModels.getAnyData(connection, "hisob2 = " + hisobId, "");
        for (HisobKitob k: kirimObservableList) {
            double jami = (k.getTovar() == 0 ? 1: k.getDona()) * k.getNarh()/k.getKurs();
            kirim += jami;
        }
        chiqimObservableList = hisobKitobModels.getAnyData(connection, "hisob1 = " + hisobId, "");
        for (HisobKitob ch: chiqimObservableList) {
            double jami = (ch.getTovar() == 0 ? 1: ch.getDona()) * ch.getNarh()/ch.getKurs();
            chiqim +=  jami;
        }
        balans = kirim - chiqim;
        return balans;
    }

    public void hkKirimChiqim() {
        for (HisobKitob hk: hisobKitobObservableList) {
            if (hk.getHisob1().equals(hisobCursor.getId())) {
                hk.setHisob2(1);
            } else {
                hk.setHisob2(2);
            }
        }
    }

    private void initHisobTableView() {
        hisobTableView = getTableView2.getHisobTableView();
        hisobTableView.setItems(hisobObservableList);
        hisobTableView.getSelectionModel().selectFirst();
        HBox.setHgrow(hisobTableView, Priority.ALWAYS);
        VBox.setVgrow(hisobTableView, Priority.ALWAYS);

        hisobTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                hisobCursor = newValue;
                qaydnomaObservableList.removeAll(qaydnomaObservableList);
                qaydnomaObservableList.addAll(getQaydFromHisobKitob(hisobCursor.getId()));
                qaydnomaBalans(qaydnomaObservableList, newValue);
                hisobKitobObservableList = FXCollections.observableArrayList();
                qaydnomaTableView.refresh();
                hisobKitobTableView.refresh();
                if (qaydnomaObservableList.size()>0) {
                    qaydnomaTableView.getSelectionModel().selectFirst();
                }
                hkKirimChiqim();
            }
        });
    }

    private void qaydnomaBalans(ObservableList<QaydnomaData> qList, Hisob hisob) {
        for (QaydnomaData q: qList) {
            double chiqim = 0;
            ObservableList<HisobKitob> chiqimList = hisobKitobModels.getAnyData(connection, "qaydId =" + q.getId() + " AND hisob1 = " + hisob.getId(), "");
            for (HisobKitob h: chiqimList) {
                chiqim += (h.getTovar() == 0 ? 1: h.getDona()) * h.getNarh()/h.getKurs();
            }
            double kirim = 0;
            ObservableList<HisobKitob> kirimList = hisobKitobModels.getAnyData(connection, "qaydId =" + q.getId() + " AND hisob2 = " + hisob.getId(), "");
            for (HisobKitob h: kirimList) {
                kirim += (h.getTovar() == 0 ? 1: h.getDona()) * h.getNarh()/h.getKurs();
            }
            q.setJami(kirim - chiqim);
        }
    }

    private ObservableList<QaydnomaData> getQaydFromHisobKitob(int hisobId) {
        ObservableList<QaydnomaData> qList = hisobKitobModels.getDistinct(connection, hisobCursor.getId());
        Collections.sort(qList, Comparator.comparingInt(QaydnomaData::getId).reversed());
        return qList;
    }

    private void initQaydnomaTableView() {
        qaydnomaTableView = getTableView2.getQaydnomaTableView();
        HBox.setHgrow(qaydnomaTableView, Priority.ALWAYS);
        VBox.setVgrow(qaydnomaTableView, Priority.ALWAYS);
        qaydnomaTableView.setItems(qaydnomaObservableList);
        qaydnomaTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            hisobKitobObservableList.removeAll(hisobKitobObservableList);
            if (newValue != null) {
                ObservableList<HisobKitob> chiqimList = hisobKitobModels.getAnyData(
                        connection,
                        "qaydid = " + newValue.getId() + " AND hisob1 = " + hisobCursor.getId(),
                        ""
                );
                ObservableList<HisobKitob> kirimList = hisobKitobModels.getAnyData(
                        connection,
                        "qaydid = " + newValue.getId() + " AND hisob2 = " + hisobCursor.getId(),

                        ""
                );
                if (hisobCursor.getId().equals(newValue.getChiqimId())) {
                    hisobKitobObservableList.addAll(chiqimList);
                    hisobKitobObservableList.addAll(kirimList);
                } else {
                    hisobKitobObservableList.addAll(kirimList);
                    hisobKitobObservableList.addAll(chiqimList);
                }
                Collections.sort(hisobKitobObservableList, Comparator.comparingInt(HisobKitob::getId));
                hisobKitobTableView.setItems(hisobKitobObservableList);
                hisobKitobTableView.getSelectionModel().selectFirst();
                hkKirimChiqim();
                yigindi(hisobKitobObservableList);
            }
            hisobKitobTableView.refresh();
        });
    }

    private void initHisobKitobTableView() {
        hisobKitobTableView = getTableView2.getHisobKitobTableView();
        hisobKitobTableView.getColumns().add(getTableView2.getBalans2Column());
        HBox.setHgrow(hisobKitobTableView, Priority.ALWAYS);
        VBox.setVgrow(hisobKitobTableView, Priority.ALWAYS);
        hisobKitobTableView.setItems(hisobKitobObservableList);
    }

    private void initBorderPane() {
        borderpane.setCenter(splitPane);
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Umumiy hisobot");
        Scene scene = new Scene(borderpane);
//        scene.getStylesheets().add("/sample/Styles/caspian.css");
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        stage.setResizable(false);
        stage.setScene(scene);
    }

    public void yigindi(ObservableList<HisobKitob> hisobKitobObservableList) {
        double yigindi = 0;
        for (HisobKitob h: hisobKitobObservableList) {
            if (h.getHisob2() == 1) {
                yigindi -= h.getSummaCol();
            } else {
                yigindi += h.getSummaCol();
            }
            h.setBalans(yigindi);
        }
    }

    public String spaceDelete(String string) {
        string = string.replaceAll("\\s+", "");
        string = string.replaceAll(",", ".");
        return string;
    }
}
