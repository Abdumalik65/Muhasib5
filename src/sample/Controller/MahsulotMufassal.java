package sample.Controller;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import sample.Config.MySqlDBGeneral;
import sample.Data.*;
import sample.Enums.ServerType;
import sample.Model.HisobKitobModels;
import sample.Tools.*;

import java.sql.Connection;
import java.text.DecimalFormat;

public class MahsulotMufassal extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    VBox topPane = new VBox();
    VBox leftPane = new VBox();
    GridPane gridPane;
    VBox centerPane = new VBox();
    VBox rightPane = new VBox();
    VBox bottomPane = new VBox();
    Button button;
    Text joriyKirim;
    Text joriyChiqim;
    Text joriyQoldiq;
    DecimalFormat decimalFormat = new MoneyShow();

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
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
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
        String classSimpleName = getClass().getSimpleName();
        DasturlarRoyxati.dastur(connection, user, classSimpleName);
        this.hisob = hisob;
        this.tovar = tovar;
        observableList = hisobKitobModels.getAnyData(connection, "(hisob1 = " +hisob.getId() + " or hisob2 = " + hisob.getId() + ") and tovar = " + tovar.getId(), "");
        ibtido();
    }

    public MahsulotMufassal(Connection connection, User user, Hisob hisob, BarCode barCode) {
        this.connection = connection;
        this.user = user;
        String classSimpleName = getClass().getSimpleName();
        DasturlarRoyxati.dastur(connection, user, classSimpleName);
        this.hisob = hisob;
        this.barCode = barCode;
        tovar = GetDbData.getTovar(barCode.getTovar());
        observableList = hisobKitobModels.getAnyData(connection, "(hisob1 = " + hisob.getId() + " or hisob2 = " + hisob.getId() + ") and barCode = '" + barCode.getBarCode() + "' and tovar = " + tovar.getId(), "");
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
        gridPane = qoldiqPane();
        centerPane.setPadding(new Insets(padding));
        centerPane.getChildren().addAll(kirimTableView, chiqimTableView, gridPane);
    }

    private GridPane qoldiqPane() {
        Font buttonFont1 = Font.font("Arial", FontWeight.BOLD,16);
        GridPane gridPane = new GridPane();
        HBox.setHgrow(gridPane, Priority.ALWAYS);
        ObservableList<HisobKitob> kirimRoyxati = kirimTableView.getItems();
        ObservableList<HisobKitob> chiqimRoyxati = chiqimTableView.getItems();
        Double kirim = jamiKirim();
        Double chiqim = jamiChiqim();
        Double qoldiq = kirim - chiqim;
        Text kirimText = new Text("Kirim ");
        Text kirimDoubleText = new Text(decimalFormat.format(kirim));
        Text chiqimText = new Text("Chiqim ");
        Text chiqimDoubleText = new Text(decimalFormat.format(chiqim));
        Text qoldiqText = new Text("Qoldiq ");
        Text qoldiqDoubleText = new Text(decimalFormat.format(qoldiq));
        joriyQoldiq = new Text(decimalFormat.format(0d));
        DoubleTextBox kirimDoubleTextBox = new DoubleTextBox(joriyKirim, kirimDoubleText);
        DoubleTextBox chiqimDoubleTextBox = new DoubleTextBox(joriyChiqim, chiqimDoubleText);
        DoubleTextBox qoldiqDoubleTextBox = new DoubleTextBox(joriyQoldiq, qoldiqDoubleText);
        gridPane.add(kirimText, 0, 0, 1, 1);
        gridPane.add(kirimDoubleTextBox, 1, 0, 1, 1);
        gridPane.add(chiqimText, 2, 0, 1, 1);
        gridPane.add(chiqimDoubleTextBox, 3, 0, 1, 1);
        gridPane.add(qoldiqText, 4, 0, 1, 1);
        gridPane.add(qoldiqDoubleTextBox, 5, 0, 1, 1);

        kirimText.setFont(Font.font("Arial", FontWeight.BOLD,16));
        GridPane.setHgrow(kirimText, Priority.ALWAYS);
        kirimDoubleText.setFont(Font.font("Arial", FontWeight.BOLD,16));
        GridPane.setHgrow(kirimDoubleTextBox, Priority.ALWAYS);
        chiqimText.setFont(Font.font("Arial", FontWeight.BOLD,16));
        GridPane.setHgrow(chiqimText, Priority.ALWAYS);
        chiqimDoubleText.setFont(Font.font("Arial", FontWeight.BOLD,16));
        qoldiqText.setFont(Font.font("Arial", FontWeight.BOLD,16));
        GridPane.setHgrow(qoldiqText, Priority.ALWAYS);
        qoldiqDoubleText.setFont(Font.font("Arial", FontWeight.BOLD,16));
        joriyKirim.setFont(Font.font("Arial", FontWeight.BOLD,16));
        joriyChiqim.setFont(Font.font("Arial", FontWeight.BOLD,16));
        joriyQoldiq.setFont(Font.font("Arial", FontWeight.BOLD,16));
        GridPane.setHgrow(chiqimDoubleTextBox, Priority.ALWAYS);
        GridPane.setHgrow(qoldiqDoubleTextBox, Priority.ALWAYS);
        kirimDoubleTextBox.setAlignment(Pos.CENTER);
        chiqimDoubleTextBox.setAlignment(Pos.CENTER);
        qoldiqDoubleTextBox.setAlignment(Pos.CENTER);

        return gridPane;
    }
    private Double jami(ObservableList<HisobKitob> observableList) {
        Double jami = 0d;
        for (HisobKitob hisobKitob: observableList) {
            jami += hisobKitob.getDona();
        }
        return jami;
    }

    private Double jamiKirim() {
        ObservableList<HisobKitob> kirimRoyxati = FXCollections.observableArrayList();
        if (barCode != null) {
            kirimRoyxati = hisobKitobModels.getAnyData(connection, "hisob2 = " + hisob.getId() + " and tovar = " + tovar.getId() + " and barcode = '" + barCode.getBarCode() + "'", "");
        } else {
            kirimRoyxati = hisobKitobModels.getAnyData(connection, "hisob2 = " + hisob.getId() + " and tovar=" + tovar.getId(), "");
        }
        Double jami = jami(kirimRoyxati);
        if (kirimRoyxati.size() > 0) {
            joriyKirim = new Text(decimalFormat.format(kirimRoyxati.get(0).getDona()));
        } else {
            joriyKirim = new Text(decimalFormat.format(0d));
        }
        return jami;
    }
    private Double jamiChiqim() {
        ObservableList<HisobKitob> chiqimRoyxati = null;
        if (barCode != null) {
            chiqimRoyxati = hisobKitobModels.getAnyData(connection, "hisob1 = " + hisob.getId() + " and tovar = " + tovar.getId() + " and barcode = '" + barCode.getBarCode() + "'", "");
        } else {
            chiqimRoyxati = hisobKitobModels.getAnyData(connection, "hisob1 = " + hisob.getId() + " and  tovar = " + tovar.getId(), "");
        }
        if (chiqimRoyxati.size() > 0) {
            joriyChiqim = new Text(decimalFormat.format(chiqimRoyxati.get(0).getDona()));
        } else {
            joriyChiqim = new Text(decimalFormat.format(0d));
        }
        Double jami = jami(chiqimRoyxati);
        return jami;
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
            chiqimObservableList = chiqimList(kirimObservableList.get(0));
        }
        chiqimTableView.setItems(chiqimObservableList);
        chiqimTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

        });
        return chiqimTableView;
    }

    private TableView<HisobKitob> initKirimTableView() {
        kirimTableView = initTableView();
        kirimTableView.getColumns().remove(2);
        kirimObservableList = kirimList();
        kirimTableView.setItems(kirimObservableList);
        if (kirimObservableList.size()>0) {
            kirimTableView.getSelectionModel().selectFirst();
            kirimTableView.scrollTo(0);
            kirimTableView.requestFocus();
        }
        kirimTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue !=null) {
                chiqimObservableList = chiqimList(newValue);
                chiqimTableView.setItems(chiqimObservableList);
                chiqimTableView.refresh();
                Double kirimDouble = newValue.getDona();
                Double chiqimDouble = jami(chiqimObservableList);

                joriyKirim.setText(decimalFormat.format(kirimDouble));
                joriyChiqim.setText(decimalFormat.format(chiqimDouble));
                joriyQoldiq.setText(decimalFormat.format(kirimDouble - chiqimDouble));

            }
        });
        return kirimTableView;
    }

    private TableView<HisobKitob> initTableView() {
        TableView<HisobKitob> tableView = new TableView<>();
        tableView.setPadding(new Insets(padding));
        TableViewAndoza tableViewAndoza = new TableViewAndoza();
        if (barCode == null) {
            tableView.getColumns().addAll(
                    tableViewAndoza.getDateTimeColumn(),
                    tableViewAndoza.getHisob1Column(),
                    tableViewAndoza.getHisob2Column(),
                    tableViewAndoza.getAmalColumn(),
                    tableViewAndoza.getHujjatIdColumn(),
                    tableViewAndoza.getBirlikColumn(),
                    tableViewAndoza.getValutaColumn(),
                    tableViewAndoza.getKursColumn(),
                    tableViewAndoza.getAdadColumn(),
                    tableViewAndoza.getNarhColumn(),
                    tableViewAndoza.getSummaColumn()
            );
        } else {
            tableView.getColumns().addAll(
                    tableViewAndoza.getDateTimeColumn(),
                    tableViewAndoza.getHisob1Column(),
                    tableViewAndoza.getHisob2Column(),
                    tableViewAndoza.getAmalColumn(),
                    tableViewAndoza.getHujjatIdColumn(),
                    tableViewAndoza.getValutaColumn(),
                    tableViewAndoza.getKursColumn(),
                    tableViewAndoza.getAdadColumn(),
                    tableViewAndoza.getNarhColumn(),
                    tableViewAndoza.getSummaColumn()
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

    private ObservableList<HisobKitob> kirimList() {
        ObservableList<HisobKitob> hisobKitobObservableList = FXCollections.observableArrayList();
        for (HisobKitob hk: observableList) {
            if (hk.getHisob2().equals(hisob.getId())) {
                hisobKitobObservableList.add(hk);
            }
        }
        return hisobKitobObservableList;
    }
    private ObservableList<HisobKitob> chiqimList(HisobKitob hisobKitob) {
        ObservableList<HisobKitob> hisobKitobObservableList = FXCollections.observableArrayList();
        for (HisobKitob hk: observableList) {
            if (hk.getHisob1().equals(hisob.getId()) && hk.getManba().equals(hisobKitob.getId())) {
                hisobKitobObservableList.add(hk);
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
