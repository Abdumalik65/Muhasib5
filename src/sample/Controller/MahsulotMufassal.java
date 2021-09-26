package sample.Controller;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import sample.Config.MySqlDBLocal;
import sample.Data.*;
import sample.Model.HisobKitobModels;
import sample.Tools.GetDbData;
import sample.Tools.GetTableView2;
import sample.Tools.SetHVGrow;

import java.sql.Connection;

public class MahsulotMufassal extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    VBox topPane = new VBox();
    VBox leftPane = new VBox();
    VBox centerPane = new VBox();
    VBox rightPane = new VBox();
    VBox bottomPane = new VBox();
    Button button;
    Connection connection;
    User user = new User(1, "admin", "", "admin");
    int padding = 3;

    Hisob hisob;
    Standart tovar;
    BarCode barCode;
    HisobKitobModels hisobKitobModels = new HisobKitobModels();
    TableView<HisobKitob> kirimTableView;
    TableView<HisobKitob> chiqimTableView;
    ObservableList<HisobKitob> observableList = FXCollections.observableArrayList();
    ObservableList<HisobKitob> kirimObservableList = FXCollections.observableArrayList();
    ObservableList<HisobKitob> chiqimObservableList = FXCollections.observableArrayList();

    public static void main(String[] args) {
        launch(args);
    }

    public MahsulotMufassal() {
        connection = new MySqlDBLocal().getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
        hisob = GetDbData.getHisob(10);
        tovar = GetDbData.getTovar(560);
        observableList = hisobKitobModels.getAnyData(connection, "tovar = " + tovar.getId(), "");
        ibtido();
    }

    public MahsulotMufassal(Connection connection, User user, Hisob hisob, Standart tovar) {
        this.connection = connection;
        this.user = user;
        this.hisob = hisob;
        this.tovar = tovar;
        observableList = hisobKitobModels.getAnyData(connection, "tovar = " + tovar.getId(), "");
        ibtido();
    }

    public MahsulotMufassal(Connection connection, User user, Hisob hisob, BarCode barCode) {
        this.connection = connection;
        this.user = user;
        this.hisob = hisob;
        this.barCode = barCode;
        tovar = GetDbData.getTovar(barCode.getTovar());
        observableList = hisobKitobModels.getAnyData(connection, "barCode = '" + barCode.getBarCode() + "'", "");
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

    private void initStage(Stage primaryStage) {
        scene = new Scene(borderpane);
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        stage = primaryStage;
        stage.setTitle("Savdo");
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        if (tovar != null) {
            stage.setTitle(tovar.getText());
        }
        if (barCode != null){
            Standart birlik = GetDbData.getBirlik(barCode.getBirlik());
            String title = stage.getTitle() + " " + barCode.getBarCode() + " " + birlik.getText();
            stage.setTitle(title);
        }
        stage.setScene(scene);
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
        SetHVGrow.VerticalHorizontal(leftPane);
        leftPane.setPadding(new Insets(padding));
    }

    private void initCenterPane() {
        SetHVGrow.VerticalHorizontal(centerPane);
        kirimTableView = initKirimTableView();
        chiqimTableView = initChiqimTableView();
        if (kirimObservableList.size()>0) {
            kirimTableView.getSelectionModel().selectFirst();
            kirimTableView.scrollTo(0);
            kirimTableView.requestFocus();
        }
        centerPane.setPadding(new Insets(padding));
        centerPane.getChildren().addAll(kirimTableView, chiqimTableView);
    }

    private void initRightPane() {
        SetHVGrow.VerticalHorizontal(rightPane);
        rightPane.setPadding(new Insets(padding));
    }

    private void initBottomPane() {
    }

    private void initBorderPane() {
        HBox.setHgrow(borderpane, Priority.ALWAYS);
        VBox.setVgrow(borderpane, Priority.ALWAYS);
        borderpane.setTop(null);
        borderpane.setLeft(null);
        borderpane.setCenter(centerPane);
        borderpane.setRight(null);
        borderpane.setBottom(null);
    }

    private TableView<HisobKitob> initChiqimTableView() {
        chiqimTableView = initTableView();
        chiqimTableView.getColumns().remove(1);
        if (kirimObservableList.size()>0) {
            chiqimObservableList = chiqimObservableList(kirimObservableList.get(0));
        }
        chiqimTableView.setItems(chiqimObservableList);
        chiqimTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

        });
        return chiqimTableView;
    }

    private TableView<HisobKitob> initKirimTableView() {
        kirimTableView = initTableView();
        kirimTableView.getColumns().remove(2);
        kirimObservableList = kirimObservableList();
        kirimTableView.setItems(kirimObservableList);
        kirimTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue !=null) {
                chiqimObservableList = chiqimObservableList(newValue);
                chiqimTableView.setItems(chiqimObservableList);
                chiqimTableView.refresh();
            }
        });
        return kirimTableView;
    }

    private TableView<HisobKitob> initTableView() {
        TableView<HisobKitob> tableView = new TableView<>();
        tableView.setPadding(new Insets(padding));
        GetTableView2 getTableView2 = new GetTableView2();
        if (barCode == null) {
            tableView.getColumns().addAll(
                    getTableView2.getDateTimeColumn(),
                    getTableView2.getHisob1Column(),
                    getTableView2.getHisob2Column(),
                    getTableView2.getAmalColumn(),
                    getTableView2.getHujjatIdColumn(),
                    getTableView2.getBirlikColumn(),
                    getTableView2.getValutaColumn(),
                    getTableView2.getKursColumn(),
                    getTableView2.getAdadColumn(),
                    getTableView2.getNarhColumn(),
                    getTableView2.getSummaColumn()
            );
        } else {
            tableView.getColumns().addAll(
                    getTableView2.getDateTimeColumn(),
                    getTableView2.getHisob1Column(),
                    getTableView2.getHisob2Column(),
                    getTableView2.getAmalColumn(),
                    getTableView2.getHujjatIdColumn(),
                    getTableView2.getValutaColumn(),
                    getTableView2.getKursColumn(),
                    getTableView2.getAdadColumn(),
                    getTableView2.getNarhColumn(),
                    getTableView2.getSummaColumn()
            );
        }

        HBox.setHgrow(tableView, Priority.ALWAYS);
        VBox.setVgrow(tableView, Priority.ALWAYS);
        return tableView;
    }

    private ObservableList<HisobKitob> kirimObservableList() {
        ObservableList<HisobKitob> hisobKitobObservableList = FXCollections.observableArrayList();
        for (HisobKitob hk: observableList) {
            if (barCode == null) {
                if ((hk.getAmal()>= 2 && hk.getAmal() <= 6) && hk.getHisob2().equals(hisob.getId()) && tovar.getId().equals(hk.getTovar())) {
                    hisobKitobObservableList.add(hk);
                }
            } else {
                if ((hk.getAmal()>= 2 && hk.getAmal() <= 6) && hk.getHisob2().equals(hisob.getId()) && barCode.getBarCode().equals(hk.getBarCode())) {
                    hisobKitobObservableList.add(hk);
                }
            }
        }
        return hisobKitobObservableList;
    }

    private ObservableList<HisobKitob> chiqimObservableList(HisobKitob hisobKitob) {
        ObservableList<HisobKitob> hisobKitobObservableList = FXCollections.observableArrayList();
        ObservableList<HisobKitob> chiqimHisobKitobObservableList = FXCollections.observableArrayList();
        Integer yordamchiHisob = hisobKitobModels.yordamchiHisob(connection, hisobKitob.getHisob2(), "TranzitHisobGuruhi", "TranzitHisob");
        for (HisobKitob hk: observableList) {
            if (hk.getManba().equals(hisobKitob.getId())) {
                hisobKitobObservableList.add(hk);
            }
        }
        for (HisobKitob hk: hisobKitobObservableList) {
            HisobKitob hk1 = xaridniTop(hk);
            if (hk1 != null) {
                chiqimHisobKitobObservableList.add(hk1);
            }
        }
        return chiqimHisobKitobObservableList;
    }

    private HisobKitob xaridniTop(HisobKitob hisobKitob) {
        HisobKitob xarid = null;
        Integer qaydId = hisobKitob.getQaydId();
        Integer yordamchiHisob = hisobKitobModels.yordamchiHisob(connection, hisobKitob.getHisob1(), "TranzitHisobGuruhi", "TranzitHisob");
        if (hisobKitob.getAmal() == 3 || hisobKitob.getAmal()==5) {
            return hisobKitob;
        }
        for (HisobKitob hk: observableList) {
            if (hk.getQaydId().equals(hisobKitob.getQaydId())   &&
                    hk.getBarCode().equals(hisobKitob.getBarCode()) &&
                    hk.getHisob1().equals(yordamchiHisob)) {
                xarid = hisobKitobModels.cloneHisobKitob(hk);
                xarid.setDona(hisobKitob.getDona());
                break;
            }
        }
        return xarid;
    }
}
