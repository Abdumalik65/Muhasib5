package sample.Controller;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import sample.Config.MySqlDBGeneral;
import sample.Data.Hisob;
import sample.Data.HisobKitob;
import sample.Data.QaydnomaData;
import sample.Data.User;
import sample.Enums.ServerType;
import sample.Model.HisobKitobModels;
import sample.Model.QaydnomaModel;
import sample.Tools.*;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Date;

public class HisobBalans extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    GridPane centerPane = new GridPane();
    VBox rightPane = new VBox();
    TableView<Hisob> hisobTableView = new TableView<>();
    TableView<HisobKitob> hisobKitobTableView = new TableView<>();
    TableViewAndoza tableViewAndoza = new TableViewAndoza();
    ObservableList<HisobKitob> rightObservableList = FXCollections.observableArrayList();
    ObservableList<QaydnomaData> qaydnomaDataObservableList = FXCollections.observableArrayList();
    HisobKitob hisobKitob;
    TextField textField = new TextField();

    Label balansLabel = new Label();
    ObservableList<Hisob> hisobObservableList;
    HisobKitobModels hisobKitobModels = new HisobKitobModels();
    DecimalFormat decimalFormat = new MoneyShow();

    Connection connection;
    User user = new User(1, "admin", "", "admin");
    int padding = 3;
    Font font = Font.font("Arial", FontWeight.BOLD,20);

    public static void main(String[] args) {
        launch(args);
    }

    public HisobBalans() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        ibtido();
    }

    public HisobBalans(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        String classSimpleName = getClass().getSimpleName();
        DasturlarRoyxati.dastur(connection, user, classSimpleName);
        ibtido();
    }

    private void ibtido() {
        initData();
        initRightPane();
        initCenterPane();
        initBorderPane();
    }

    private void initData() {
        GetDbData.initData(connection);
        hisobObservableList = GetDbData.getHisobObservableList();
        hisobObservableList = GetDbData.cheklovlarniOlibTashla(hisobObservableList, user);
        QaydnomaModel qaydnomaModel = new QaydnomaModel();
        qaydnomaDataObservableList = qaydnomaModel.get_data(connection);
    }

    private void initHisobTableView() {
        SetHVGrow.VerticalHorizontal(hisobTableView);
        hisobTableView.setMaxWidth(210);
        hisobTableView.setPadding(new Insets(padding));
        TableColumn<Hisob, String> hisobText = tableViewAndoza.getHisobTextColumn();
        hisobText.setMinWidth(200);
        hisobTableView.getColumns().add(hisobText);
        hisobTableView.setItems(hisobObservableList);
        hisobTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                refreshHisobKitobTable(newValue);
            }
        });
    }

    private void initHisobKitobTable() {
        tableViewAndoza.getAdadColumn().setMinWidth(80);
        TableColumn<HisobKitob, Integer> amalColumn = tableViewAndoza.getAmalColumn();
        amalColumn.setStyle( "-fx-alignment: CENTER;");

        TableColumn<HisobKitob, Integer> valutaColumn = tableViewAndoza.getValutaColumn();
        valutaColumn.setStyle( "-fx-alignment: CENTER;");

        hisobKitobTableView.getColumns().addAll(tableViewAndoza.getDateTimeColumn(), tableViewAndoza.hisob1Hisob2(), amalColumn,
                tableViewAndoza.getIzoh2Column(), tableViewAndoza.valutaKurs(),
                tableViewAndoza.adadNarh(), tableViewAndoza.getSummaColumn(),
                tableViewAndoza.getBalans2Column());
        HBox.setHgrow(hisobKitobTableView, Priority.ALWAYS);
        VBox.setVgrow(hisobKitobTableView, Priority.ALWAYS);
        hisobKitobTableView.setItems(rightObservableList);
        hisobKitobTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
            }
        });
        hisobKitobTableView.setRowFactory(tv -> new TableRow<HisobKitob>() {
            @Override
            protected void updateItem(HisobKitob hisobKitob, boolean empty) {
                super.updateItem(hisobKitob, empty);
                if (hisobKitob == null || hisobKitob.getId() == null)
                    setStyle("");
                else if (hisobKitob.getId() == 2)
                    setStyle("-fx-background-color: white;");
                else if (hisobKitob.getId() == 1)
                    setStyle("-fx-background-color: #baffba;");
                else
                    setStyle("");
            }
        });
    }

    private void initBalansLabel() {
        balansLabel.setFont(font);
        balansLabel.setAlignment(Pos.CENTER_RIGHT);
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

    private void initRightPane() {
        initTaftishTextField();
        initHisobTableView();
        SetHVGrow.VerticalHorizontal(rightPane);
        rightPane.setPadding(new Insets(padding));
        rightPane.getChildren().addAll(textField, hisobTableView);
    }

    private void initCenterPane() {
        initBalansLabel();
        initHisobKitobTable();
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        VBox.setVgrow(centerPane, Priority.ALWAYS);
        int rowIndex = 0;
        centerPane.add(hisobKitobTableView, 0, rowIndex, 1, 1);
        GridPane.setHgrow(hisobKitobTableView, Priority.ALWAYS);
        GridPane.setVgrow(hisobKitobTableView, Priority.ALWAYS);
        rowIndex++;
        centerPane.add(balansLabel, 0, rowIndex, 1, 1);
        GridPane.setHgrow(balansLabel, Priority.ALWAYS);
        GridPane.setHalignment(balansLabel, HPos.CENTER);
    }

    private void initBorderPane() {
        borderpane.setTop(null);
        borderpane.setLeft(null);
        borderpane.setRight(rightPane);
        borderpane.setCenter(centerPane);
        borderpane.setBottom(null);
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Bir panel");
        scene = new Scene(borderpane, 1200, 600);
        stage.setScene(scene);
    }

    private void initTaftishTextField() {
        HBox.setHgrow(textField, Priority.ALWAYS);
        textField.setPadding(new Insets(padding));
        textField.setMaxWidth(210);
        textField.setPromptText("QIDIR");
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            Taftish(oldValue, newValue);
        });

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

    private Hisob addHisob() {
        Hisob hisob = null;
        HisobController hisobController = new HisobController();
        hisobController.display(connection, user);
        if (hisobController.getDoubleClick()) {
            hisob = hisobController.getDoubleClickedRow();
        }
        return hisob;
    }

    public TableColumn<HisobKitob, Integer> getCustom2Column() {
        TableColumn<HisobKitob, Integer> integerTableColumn = new TableColumn<>("Kirim/\nChiqim");
        integerTableColumn.setMinWidth(100);
        integerTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, Integer>, ObservableValue<Integer>>() {

            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<HisobKitob, Integer> param) {
                HisobKitob hk = param.getValue();
                Integer hkId = hk.getId();
                return new SimpleObjectProperty<Integer>(hkId);
            }
        });
        integerTableColumn.setCellFactory(column -> {
            TableCell<HisobKitob, Integer> cell = new TableCell<HisobKitob, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        Hisob hisob1 = GetDbData.getHisob(hisobKitob.getHisob1());
                        Hisob hisob2 = GetDbData.getHisob(hisobKitob.getHisob2());
                        Text text = new Text();
                        if (item == 1) {
                            text.setText("Chiqim: " + hisob2.getText().trim() + "ga");
                        } else {
                            text.setText("Kirim: " + hisob1.getText().trim() + "dan");
                        }
                        text.setStyle("-fx-text-alignment:justify;");
                        text.wrappingWidthProperty().bind(getTableColumn().widthProperty().subtract(2));
                        setGraphic(text);

                    }
                }
            };
            return cell;
        });
        return integerTableColumn;
    }

    private void refreshHisobKitobTable(Hisob hisob) {
        rightObservableList.removeAll(rightObservableList);
        if (hisob != null) {
            rightObservableList.addAll(hisobKitobModels.getAnyData(connection, "hisob1 = " + hisob.getId() + " OR hisob2 = " + hisob.getId(), "id asc"));
            setDateTime();
            hkKirimChiqim(hisob);
        }
        Double balansDouble = hisobBalans(hisob.getId());
        balansLabel.setText(decimalFormat.format(balansDouble));
        hisobKitobTableView.setItems(rightObservableList);
        hisobKitobTableView.refresh();
    }

    private void setDateTime() {
        for (HisobKitob hk: rightObservableList) {
            hk.setDateTime(getQaydDate(hk.getQaydId()));
        }
//        Collections.sort(rightObservableList, Comparator.comparing(HisobKitob::getDateTime));
    }

    public void hkKirimChiqim(Hisob hisob) {
        Double yigindi = .0;
        if (rightObservableList.size()>0) {
            hisobKitob = rightObservableList.get(0);
        }
        for (HisobKitob hk: rightObservableList) {
            if (hk.getHisob1().equals(hisob.getId()) ) {
                hk.setId(1);
            } else {
                hk.setId(2);
            }

            if (hk.getId().equals(1)) {
                yigindi -= hk.getSummaCol();
            } else {
                yigindi += hk.getSummaCol();
            }
            hk.setBalans(yigindi);
        }
    }

    private Date getQaydDate(Integer qaydId) {
        Date qaydDate = null;
        for (QaydnomaData q: qaydnomaDataObservableList) {
            if (q.getId().equals(qaydId)) {
                qaydDate = q.getSana();
                break;
            }
        }
        return qaydDate;
    }
    public void Taftish(String oldValue, String newValue) {
        ObservableList<Hisob> subentries = FXCollections.observableArrayList();
        newValue = newValue.toLowerCase();

        if ( oldValue != null && (newValue.length() < oldValue.length()) ) {
            hisobTableView.setItems( hisobObservableList );
        }

        for ( Hisob hisob: hisobObservableList ) {
            if (hisob.getText().toLowerCase().contains(newValue)) {
                subentries.add(hisob);
            }
        }
        hisobTableView.setItems(subentries);
    }

    public TableView<HisobKitob> getHisobKitobTableView() {
        return hisobKitobTableView;
    }

    public void display(LocalDate localDate, Integer hisobId) {
        borderpane = null;
        centerPane = null;

        Hisob hisob = GetDbData.getHisob(hisobId);
        refreshHisobKitobTable(hisob);
        stage = new Stage();
        Scene scene = new Scene(rightPane, 1000, 600);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

}
