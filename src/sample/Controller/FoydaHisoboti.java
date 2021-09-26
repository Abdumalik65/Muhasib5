package sample.Controller;

import javafx.application.Application;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import sample.Config.MySqlDBLocal;
import sample.Data.*;
import sample.Excel.FoydaExcel;
import sample.Model.HisobKitobModels;
import sample.Model.QaydnomaModel;
import sample.Model.Standart2Models;
import sample.Model.Standart3Models;
import sample.Tools.*;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FoydaHisoboti extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    SplitPane centerPane = new SplitPane();
    VBox rightPane = new VBox();
    VBox leftPane = new VBox();
    HBox buttonsPane = new HBox();
    HBox jamiHBoxPane = new HBox();
    DatePicker datePicker;
    Button excelButton = new Tugmachalar().getExcel();
    DecimalFormat decimalFormat = new MoneyShow();
    Font font = Font.font("Arial", FontWeight.BOLD,20);
    ToggleGroup toggleGroup = new ToggleGroup();
    RadioButton selectedRadioButton;
    GridPane valutaGridPane = new GridPane();

    TableView<Hisob> hisobTableView = new TableView<>();
    ObservableList<Hisob> hisobTableList = FXCollections.observableArrayList();
    ObservableList<Standart5> valutaList = FXCollections.observableArrayList();

    TableView<HisobKitob> foydaTableView = new TableView<>();
    ObservableList<HisobKitob> foydaTableList = FXCollections.observableArrayList();

    Label jamiFoydaLabel = new Label();
    Double jamiFoydaDouble = 0d;

    Connection connection;
    User user = new User(1, "admin", "", "admin");
    int padding = 3;


    public static void main(String[] args) {
        launch(args);
    }

    public FoydaHisoboti() {
        connection = new MySqlDBLocal().getDbConnection();
        GetDbData.initData(connection);
        ibtido();
    }

    public FoydaHisoboti(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        ibtido();
    }

    private void ibtido() {
        initTopPane();
        initCenterPane();
        initLeftPane();
        initRightPane();
        initBottomPane();
        initBorderPane();
        refreshValutaGridPane();
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
        HBox.setHgrow(leftPane, Priority.ALWAYS);
        VBox.setVgrow(leftPane, Priority.ALWAYS);
        leftPane.getChildren().addAll(buttonsPane, foydaTableView);
    }

    private void initCenterPane() {
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        VBox.setVgrow(centerPane, Priority.ALWAYS);
        HBox toggleGroupHBox = initToggleGroup();
        buttonsPane.getChildren().add(0, toggleGroupHBox);
        initHisobTable();
        initButtons();
        initFoydaTable();
        centerPane.getItems().addAll(leftPane, rightPane);
        centerPane.setDividerPositions(.7);
    }

    private void initRightPane() {
        rightPane.setPadding(new Insets(padding));
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        VBox.setVgrow(rightPane, Priority.ALWAYS);
        rightPane.getChildren().add(hisobTableView);
    }

    private void initBottomPane() {}

    private void initBorderPane() {
        borderpane.setCenter(centerPane);
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Foyda hisoboti");
        scene = new Scene(borderpane, 800, 400);
        stage.setScene(scene);
    }

    private void initHisobTable() {
        Standart2Models standart2Models = new Standart2Models();
        standart2Models.setTABLENAME("foydahisobi");
        ObservableList<Standart2> standart2ObservableList = standart2Models.get_data(connection);
        GetTableView2 getTableView2 = new GetTableView2();
        hisobTableView = getTableView2.getHisobTableView();
        SetHVGrow.VerticalHorizontal(hisobTableView);
        hisobTableView.getColumns().add(getTableView2.getHisobTextColumn());
        for (Standart2 s2: standart2ObservableList) {
            hisobTableList.add(new Hisob(s2.getId2(), s2.getText(), 0d,"","", "", user.getId()));
        }
        hisobTableView.setItems(hisobTableList);
        if (hisobTableList.size()>0) {
            hisobTableView.getSelectionModel().selectFirst();
        }
    }

    private void initFoydaTable() {
        GetTableView2 getTableView2 = new GetTableView2();
        foydaTableView = getTableView2.getHisobKitobTableView();
        SetHVGrow.VerticalHorizontal(foydaTableView);
        foydaTableView.getColumns().addAll(
                getTableView2.getDateTimeColumn(),
                getTableView2.getIzoh2Column(),
                getTableView2.getAdadColumn(),
                getTableView2.getNarhColumn(),
                getTableView2.getSummaColumn()
        );
        foydaTableList = refreshFoydaList();
        foydaTableView.setItems(foydaTableList);
    }

    private void initDatePicker() {
        // Converter
        StringConverter<LocalDate> converter = new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }
            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        };
        datePicker =  new DatePicker(LocalDate.now());

        datePicker.setConverter(converter);
        datePicker.setMaxWidth(2000);
        datePicker.setPrefWidth(150);
        HBox.setHgrow(datePicker, Priority.ALWAYS);
    }

    private void initJamiHBoxPane() {
        HBox.setHgrow(jamiHBoxPane, Priority.ALWAYS);
        jamiHBoxPane.setPadding(new Insets(padding));
        Pane pane = new Pane();
        HBox.setHgrow(pane, Priority.ALWAYS);
        Label label = new Label("Jami: ");
        label.setFont(font);
        label.setAlignment(Pos.CENTER_LEFT);
        jamiFoydaLabel.setFont(font);
        jamiFoydaLabel.setAlignment(Pos.CENTER_RIGHT);
        GridPane gridPane = new GridPane();
        jamiHBoxPane.getChildren().addAll(gridPane);
    }

    private void initButtons() {
        HBox.setHgrow(buttonsPane, Priority.ALWAYS);
        initDatePicker();
        buttonsPane.getChildren().addAll(datePicker, excelButton);
        excelButton.setOnAction(event -> {
            LocalDate newDate = datePicker.getValue();
            if (newDate != null) {
                hisobot2();
            }
        });

        datePicker.setOnAction(event -> {
            LocalDate newDate = datePicker.getValue();
            if (newDate != null) {
                foydaTableList = refreshFoydaList();
                refreshValutaGridPane();
                foydaTableView.setItems(foydaTableList);
                foydaTableView.refresh();
            }
        });
    }

    public  void hisobot(LocalDate date) {
        Double jamiDouble = 0d;
        Double mablagDouble = 0d;
        Double chegirmaDouble = 0d;
        int foydaHisobi = hisobTableView.getSelectionModel().getSelectedItem().getId();
        int amalTuri = 4;
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateString = dateTimeFormatter.format(date);
        QaydnomaModel qaydnomaModel = new QaydnomaModel();
        ObservableList<HisobKitob> hisobKitobObservableList = FXCollections.observableArrayList();
        ObservableList<QaydnomaData> qaydnomaDataObservableList = null;
        qaydnomaDataObservableList = qaydnomaModel.getAnyData(connection, "amalTuri = " + amalTuri + " and substr(sana, 1,10) = '" + dateString + "'", "sana asc");
        for (QaydnomaData q: qaydnomaDataObservableList) {
            ObservableList<HisobKitob> hkList = hisobKitobModels.getAnyData(connection, "qaydId =" + q.getId() + " and hujjatId = " + q.getHujjat() + " and hisob1 = " + foydaHisobi, "");
            hisobKitobObservableList.addAll(hkList);
        }
        ObservableList<HisobKitob> hk2List = null;
        for (QaydnomaData q: qaydnomaDataObservableList) {
            hk2List = hisobKitobModels.getAnyData(connection, "qaydId = " + q.getId() + " AND hujjatId = " + q.getHujjat()   , "");
            for (HisobKitob hk : hk2List) {
                if (hk.getHisob2().equals(q.getKirimId())) {
                    if (hk.getTovar() > 0) {
                        mablagDouble = mablagDouble + hk.getDona() * hk.getNarh();
                    }
                }
                if (hk.getAmal() == 13) {
                    chegirmaDouble = hk.getNarh();
                }
            }
            jamiDouble = jamiDouble + mablagDouble - chegirmaDouble;
            mablagDouble = 0d;
            chegirmaDouble = 0d;
        }
        jamiFoydaLabel.setText(decimalFormat.format(jamiDouble));

        ObservableList<Standart3>  s3List = initBGuruh(connection);
        FoydaExcel foydaExcel = new FoydaExcel();
        foydaExcel.hisob(3, hisobKitobObservableList, s3List, jamiDouble);
    }

    public  void hisobot1() {
        ObservableList<Standart3>  s3List = initBGuruh(connection);
        FoydaExcel foydaExcel = new FoydaExcel();
        foydaExcel.hisob(3, foydaTableList, s3List, jamiFoydaDouble);
    }

    public  void hisobot2() {
        ObservableList<Standart3>  s3List = initBGuruh(connection);
        FoydaExcel foydaExcel = new FoydaExcel();
        foydaExcel.hisob2(3, foydaTableList, s3List, valutaList);
    }

    public ObservableList<Standart3> initBGuruh(Connection connection) {
        ObservableList<Standart3> s3List;
        Standart3Models standart3Models = new Standart3Models();
        standart3Models.setTABLENAME("BGuruh2");
        s3List = standart3Models.get_data(connection);
        return s3List;
    }

    private HBox initToggleGroup() {
        HBox hBox = new HBox(10);
        HBox.setHgrow(hBox, Priority.ALWAYS);
        RadioButton radioButton = new RadioButton("Jami sanalar");
        radioButton.setId("1");
        radioButton.setToggleGroup(toggleGroup);
        RadioButton radioButton1 = new RadioButton("Yakka sana");
        radioButton1.setId("2");
        radioButton1.setSelected(true);
        radioButton1.setToggleGroup(toggleGroup);
        selectedRadioButton = radioButton1;
        hBox.getChildren().addAll(radioButton, radioButton1);

        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                RadioButton rb = (RadioButton) newValue;
                String buttonName = rb.getText();
                if (buttonName.equalsIgnoreCase("Jami sanalar")) {
                    datePicker.setDisable(true);
                } else if (buttonName.equalsIgnoreCase("Yakka sana")){
                    datePicker.setDisable(false);
                }
                selectedRadioButton = rb;
                foydaTableList = refreshFoydaList();
                refreshValutaGridPane();
                foydaTableView.setItems(foydaTableList);
                foydaTableView.refresh();
            }
        });
        return hBox;
    }

    private ObservableList<HisobKitob> refreshFoydaList() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = datePicker.getValue();
        String dateS = dateTimeFormatter.format(localDate);
        ObservableList<HisobKitob> foydaList = null;
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        String select = "";
        String select2 = "";
        Hisob hisob = hisobTableView.getSelectionModel().getSelectedItem();
        if (hisob != null) {
            switch (selectedRadioButton.getId()) {
                case "1" :
                    select = "hisob1 = " + hisob.getId();
                    select2 = "amal = 4";
                    break;
                case "2" :
                    select = "hisob1 = " + hisob.getId() + " and substr(datetime,1,10) = '" + dateS + "'";
                    break;
            }
            foydaList = hisobKitobModels.getAnyData(connection, select, "");
            valutaList = getValutaData();
            ObservableList<HisobKitob> hkListAll = hisobKitobModels.getAnyData(connection, "amal = 4 and manba = 0", "");
            for (HisobKitob hk: hkListAll) {
                for (HisobKitob f: foydaList) {
                    if (hk.getQaydId().equals(f.getQaydId()) && hk.getBarCode().equals(f.getBarCode())) {
                        for (Standart5 s5: valutaList) {
                            if (hk.getValuta().equals(s5.getId())) {
                                Double s5Summa = s5.getDona();
                                s5.setDona(s5Summa + hk.getNarh() * hk.getDona());
                                break;
                            }
                        }
                        jamiFoydaDouble += hk.getNarh();
                        break;
                    }
                }
            }
        }
        return foydaList;
    }

    private ObservableList<Standart5> getValutaData() {
        ObservableList<Standart5> valutaList = FXCollections.observableArrayList();
        ObservableList<Valuta> valutaObservableList = GetDbData.getValutaObservableList();
        for (Valuta v: valutaObservableList) {
            valutaList.add(new Standart5(v.getId(), 0, 0, v.getValuta(), 0d, user.getId(), null));
        }
        return valutaList;
    }

    private GridPane s5GridPane() {
        GridPane gridPane = new GridPane();
        HBox.setHgrow(gridPane, Priority.ALWAYS);
        VBox.setVgrow(gridPane, Priority.NEVER);
        GridPane.setHgrow(gridPane, Priority.ALWAYS);
        GridPane.setVgrow(gridPane, Priority.ALWAYS);
        Integer rowIndex = 0;
        for (Standart5 s5: valutaList) {
            Label valutaNomi = new Label(s5.getText());
            HBox.setHgrow(valutaNomi, Priority.ALWAYS);
            Pane pane = new Pane();
            HBox.setHgrow(pane, Priority.ALWAYS);
            Label valutaMiqdori = new Label(decimalFormat.format(s5.getDona()));
            HBox.setHgrow(valutaMiqdori, Priority.ALWAYS);
            gridPane.add(valutaNomi, 0, rowIndex, 1, 1);
            GridPane.setHalignment(valutaNomi, HPos.LEFT);
            gridPane.add(pane, 1, rowIndex, 1, 1);
            GridPane.setHgrow(pane, Priority.ALWAYS);
            gridPane.add(valutaMiqdori, 2, rowIndex, 1, 1);
            GridPane.setHalignment(valutaMiqdori, HPos.RIGHT);
            rowIndex++;
        }
        return gridPane;
    }

    private void refreshValutaGridPane() {
        leftPane.getChildren().remove(valutaGridPane);
        valutaGridPane = null;
        valutaGridPane = s5GridPane();
        leftPane.getChildren().add(2,valutaGridPane);
    }
}
