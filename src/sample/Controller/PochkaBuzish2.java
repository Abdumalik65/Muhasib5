package sample.Controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import sample.Config.MySqlDB;
import sample.Config.MySqlDBLocal;
import sample.Data.*;
import sample.Model.*;
import sample.Temp.Hisobot2;
import sample.Tools.*;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static sample.Tools.GetDbData.getValuta;

public class PochkaBuzish2 extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    GridPane gridPane = new GridPane();
    VBox centerPane = new VBox();
    Connection connection;
    User user = new User(1, "admin", "", "admin");
    int padding = 3;
    int amalTuri = 5;
    Boolean doubleClick = false;
    HisobKitob hisobKitobCursor;

    TextField qidirBarCodeTextField = new TextField();
    TextField tovarNomiTextField = new TextField();
    TextField qaydVaqtiTextField = new TextField();
    DecimalFormat decimalFormat = new MoneyShow();

    TableView<HisobKitob> hisob1TarkibTableView = new TableView<>();
    TableView<HisobKitob> maydaTarkibTableView = new TableView<>();
    Button xaridniYakunlaButton = new Button("Qayd et");

    Hisob hisob1;
    Hisob hisob2;
    Standart tovar;
    HisobKitob hisobKitob;
    QaydnomaData qaydnomaData = null;

    Font font = Font.font("Arial",20);
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    StringBuffer stringBuffer = new StringBuffer();
    String style20 = "-fx-font: 20px Arial";

    ObservableList<HisobKitob> hisob1TarkibList = FXCollections.observableArrayList();
    ObservableList<HisobKitob> maydaTarkibList = FXCollections.observableArrayList();
    ObservableList<Hisob> hisobObservableList = FXCollections.observableArrayList();
    ObservableList<Standart> tovarObservableList;
    ObservableList<Standart> birlikObservableList;

    HisobKitobModels hisobKitobModels = new HisobKitobModels();
    HisobModels hisobModels = new HisobModels();
    StandartModels standartModels = new StandartModels();
    BarCodeModels barCodeModels = new BarCodeModels();
    KursModels kursModels = new KursModels();
    QaydnomaModel qaydnomaModel = new QaydnomaModel();
    Integer yordamchiHisob;

    public static void main(String[] args) {
        launch(args);
    }

    public PochkaBuzish2() {
        connection = new MySqlDB().getDbConnection();
        GetDbData.initData(connection);
        hisob1 = GetDbData.getHisob(13);
    }

    public PochkaBuzish2(Connection connection, User user, Hisob hisob) {
        this.connection = connection;
        this.user = user;
        this.hisob1 = hisob;
    }

    private void ibtido() {
        initData();
        initTextFields();
        initHisob1TarkibTableView();
        initMaydaTarkibTableView();
        initYakunlaButton();
        initGridPane();
        initCenterPane();
        initBorderPane();
    }

    @Override
    public void start(Stage primaryStage) {
        ibtido();
        initStage(primaryStage);
        stage.show();
        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public QaydnomaData display() {
        stage = new Stage();
        ibtido();
        initStage(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        stage.setOnCloseRequest(event -> {
            stage.close();
        });
        return qaydnomaData;
    }

    private void refreshHisob1TarkibList() {
        Hisobot2 hisobot2 = new Hisobot2(connection, user);
        hisob1TarkibList.removeAll(hisob1TarkibList);
        hisob1TarkibList.addAll(hisobot2.getBarCodes(hisob1.getId()));
        hisob1TarkibList = donaliklarniOchir(hisob1TarkibList);
    }

    private ObservableList<HisobKitob> donaliklarniOchir(ObservableList<HisobKitob> hisobKitobObservableList) {
        ObservableList<HisobKitob> hisobKitobList = FXCollections.observableArrayList();
        for (HisobKitob hk: hisobKitobObservableList) {
            BarCode bc = GetDbData.getBarCode(hk.getBarCode());
            if (bc.getTarkib() != 0) {
                hisobKitobList.add(hk);
            }
        }
        return hisobKitobList;
    }

    private void initData() {
        yordamchiHisob = hisobKitobModels.yordamchiHisob(connection, hisob1.getId(), "TranzitHisobGuruhi", "TranzitHisob");
        hisob2 = GetDbData.getHisob(yordamchiHisob);
    }

    private void initCenterPane() {
        SetHVGrow.VerticalHorizontal(centerPane);
        centerPane.setPadding(new Insets(padding));
        centerPane.getChildren().removeAll(centerPane.getChildren());
        centerPane.getChildren().addAll(gridPane,xaridniYakunlaButton);
    }

    private void initHisob1TarkibTableView() {
        SetHVGrow.VerticalHorizontal(hisob1TarkibTableView);
        GetTableView2 getTableView2 = new GetTableView2();
        hisob1TarkibTableView.getColumns().removeAll(hisob1TarkibTableView.getColumns());
        hisob1TarkibTableView.getColumns().addAll(getTableView2.getIzoh2Column(), getTableView2.getAdadColumn());
        refreshHisob1TarkibList();
        hisob1TarkibTableView.setItems(hisob1TarkibList);
        hisob1TarkibTableView.setRowFactory( tv -> {
            TableRow<HisobKitob> row = new TableRow<>();
            row.setOnMouseClicked(event1 -> {
                if (event1.getClickCount() == 2 && (! row.isEmpty()) ) {
                    doubleClick = true;
                    HisobKitob doubleClickedRow = row.getItem();
                    addTovar(doubleClickedRow);
                }
            });
            return row ;
        });

    }

    private void initTextFields() {
        HBox.setHgrow(tovarNomiTextField, Priority.ALWAYS);
        HBox.setHgrow(qidirBarCodeTextField, Priority.ALWAYS);
        qidirBarCodeTextField.setPromptText("Shtrix kod");
        qidirBarCodeTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                String string = qidirBarCodeTextField.getText().trim();
                if (event.getCode()== KeyCode.ENTER) {
                    BarCode barCode = barCodeModels.getBarCode(connection, string);
                    if (barCode !=  null) {
                        HisobKitob hk = getHisobKitob(barCode);
                        qidirBarCodeTextField.setText("");
                        hisob1TarkibTableView.getSelectionModel().select(hk);
                        hisob1TarkibTableView.scrollTo(hk);
                        hisob1TarkibTableView.requestFocus();
                        hisob1TarkibTableView.refresh();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Diqqat !!!");
                        alert.setHeaderText(string +  "\n shtrix kodga muvofiq tovar topiilmadi" );
                        alert.setContentText("");
                        alert.showAndWait();
                    }
                }
            }
        });

        tovarNomiTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            Taftish(oldValue, newValue);
        });
        tovarNomiTextField.setPromptText("TOVAR NOMI");


    }

    private HisobKitob getHisobKitob(BarCode bc) {
        HisobKitob hisobKitob = null;
        String barCodeString = bc.getBarCode();
        for (HisobKitob hk : hisob1TarkibList) {
            if (barCodeString.equalsIgnoreCase(hk.getBarCode().trim())) {
                hisobKitob = hk;
                break;
            }
        }
        return hisobKitob;
    }

    private TableColumn<HisobKitob, Button> getDeleteColumn() {
        TableColumn<HisobKitob, Button> deleteColumn = new TableColumn<>("O`chir");
        deleteColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, Button>, ObservableValue<Button>>() {

            @Override
            public ObservableValue<Button> call(TableColumn.CellDataFeatures<HisobKitob, Button> param) {
                HisobKitob hisobKitob = param.getValue();
                Button b = new Button("");
                b.setMaxWidth(2000);
                b.setPrefWidth(150);
                HBox.setHgrow(b, Priority.ALWAYS);
                InputStream inputStream = getClass().getResourceAsStream("/sample/images/Icons/delete.png");
                Image image = new Image(inputStream);
                ImageView imageView = new ImageView(image);
                b.setGraphic(imageView);
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                image = null;

                b.setOnAction(event -> {
                    maydaTarkibList.remove(hisobKitob);
                    param.getTableView().refresh();
                });
                return new SimpleObjectProperty<Button>(b);
            }
        });

        deleteColumn.setMinWidth(60);
        deleteColumn.setMaxWidth(60);
        deleteColumn.setStyle( "-fx-alignment: CENTER;");
        return deleteColumn;
    }

    private void initMaydaTarkibTableView() {
        SetHVGrow.VerticalHorizontal(maydaTarkibTableView);
        GetTableView2 getTableView2 = new GetTableView2();
        maydaTarkibTableView.getColumns().removeAll(maydaTarkibTableView.getColumns());
        maydaTarkibTableView.getColumns().addAll(
                getTableView2.getIzoh2Column(),
                getAdadColumn(),
                getDeleteColumn()
        );
        maydaTarkibTableView.setItems(maydaTarkibList);
        maydaTarkibTableView.setEditable(true);
        maydaTarkibTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                hisobKitobCursor = newValue;
            }
        });
    }

    private void initYakunlaButton() {
        xaridniYakunlaButton.setMaxWidth(2000);
        xaridniYakunlaButton.setPrefWidth(150);
        HBox.setHgrow(xaridniYakunlaButton, Priority.ALWAYS);
        VBox.setVgrow(xaridniYakunlaButton, Priority.NEVER);
        xaridniYakunlaButton.setFont(font);
        xaridniYakunlaButton.setOnAction(event -> {
            System.out.println("Bismillah");
            qaydnomaData = qaydnomaSaqlash();
            xaridSaqlash1(qaydnomaData);
            stage.close();
        });
    }

    private QaydnomaData qaydnomaSaqlash() {
        int hujjatInt = getQaydnomaNumber();
        String izohString = "Pochka buzish amali";
        Double jamiDouble = getJami(hisob1TarkibList);
        Date date = new Date();
        QaydnomaData qaydnomaData = new QaydnomaData(null, amalTuri, hujjatInt, date,
                hisob1.getId(), hisob1.getText(), hisob2.getId(), hisob2.getText(),
                izohString, jamiDouble, 0, user.getId(), new Date());
        qaydnomaModel.insert_data(connection, qaydnomaData);
        return qaydnomaData;
    }

    private int getQaydnomaNumber() {
        int qaydnomaInt = 1;
        ObservableList<QaydnomaData> qaydList = qaydnomaModel.getAnyData(connection, "amalTuri = " + amalTuri, "hujjat desc");
        if (qaydList.size()>0) {
            qaydnomaInt = qaydList.get(0).getHujjat() + 1;
        }
        return qaydnomaInt;
    }

    private void xaridSaqlash(QaydnomaData qData) {
        for (HisobKitob hk: maydaTarkibList) {
            BarCode bc = GetDbData.getBarCode(hk.getBarCode());
            if (bc != null) {
                BarCode bc1 = GetDbData.getBarCode(bc.getTarkib());
                if (bc1 != null) {
                    hk.setQaydId(qData.getId());
                    hk.setHujjatId(qData.getHujjat());
                    hisobKitobModels.insert_data(connection, hk);
                    HisobKitob hk2 = hisobKitobModels.cloneHisobKitob(hk);
                    hk2.setHisob1(hk.getHisob2());
                    hk2.setHisob2(hk.getHisob1());
                    hk2.setBarCode(bc1.getBarCode());
                    hk2.setDona(hk.getDona() * bc.getAdad());
                    hk2.setNarh(hk.getNarh() / bc.getAdad());
                    hk2.setManba(hk.getId());
                    hisobKitobModels.insert_data(connection, hk2);
                }
            }
        }
    }

    private void xaridSaqlash1(QaydnomaData qData) {
        ObservableList<HisobKitob> hkList = FXCollections.observableArrayList();
        for (HisobKitob hk: maydaTarkibList) {
            Standart tovar = GetDbData.getTovar(hk.getTovar());
            BarCode bc = GetDbData.getBarCode(hk.getBarCode());
            if (bc != null) {
                BarCode bc1 = GetDbData.getBarCode(bc.getTarkib());
                if (bc1 != null) {
                    Standart birlik = GetDbData.getBirlik(bc1.getBirlik());
                    hk.setQaydId(qData.getId());
                    hk.setHujjatId(qData.getHujjat());
                    ObservableList<HisobKitob> qoldiqList = hisobKitobModels.getBarCodeQoldiq(connection, qData.getChiqimId(), bc, qData.getSana());
                    for (HisobKitob hkQoldiq: qoldiqList) {
                        if (hkQoldiq.getDona()>=hk.getDona()) {
                            HisobKitob hkChiqim = hisobKitobModels.cloneHisobKitob(hk);
                            hkChiqim.setKurs(hkQoldiq.getKurs());
                            hkChiqim.setValuta(hkQoldiq.getValuta());
                            hkChiqim.setDona(hk.getDona());
                            hkChiqim.setNarh(hkQoldiq.getNarh());
                            hkChiqim.setManba(hkQoldiq.getId());
                            hkList.add(hkChiqim);

                            HisobKitob hkKirim = hisobKitobModels.cloneHisobKitob(hk);
                            hkKirim.setHisob1(hk.getHisob2());
                            hkKirim.setHisob2(hk.getHisob1());
                            hkKirim.setBarCode(bc1.getBarCode());
                            hkKirim.setKurs(hkQoldiq.getKurs());
                            hkKirim.setValuta(hkQoldiq.getValuta());
                            hkKirim.setDona(hk.getDona() * bc.getAdad());
                            hkKirim.setNarh(hkQoldiq.getNarh() / bc.getAdad());
                            hkKirim.setIzoh(tovar.getText().trim() + " " + bc1.getBarCode().trim() + " " + birlik.getText().trim());
                            hkKirim.setManba(0);
                            hkList.add(hkKirim);
                            break;
                        } else {
                            HisobKitob hkChiqim = hisobKitobModels.cloneHisobKitob(hk);
                            hkChiqim.setKurs(hkQoldiq.getKurs());
                            hkChiqim.setValuta(hkQoldiq.getValuta());
                            hkChiqim.setDona(hkQoldiq.getDona());
                            hkChiqim.setNarh(hkQoldiq.getNarh());
                            hkChiqim.setManba(hkQoldiq.getId());
                            hkList.add(hkChiqim);

                            HisobKitob hkKirim = hisobKitobModels.cloneHisobKitob(hk);
                            hkKirim.setHisob1(hk.getHisob2());
                            hkKirim.setHisob2(hk.getHisob1());
                            hkKirim.setBarCode(bc1.getBarCode());
                            hkKirim.setDona(hkQoldiq.getDona() * bc.getAdad());
                            hkKirim.setNarh(hkQoldiq.getNarh() / bc.getAdad());
                            hkKirim.setIzoh(tovar.getText().trim() + " " + bc1.getBarCode().trim() + " " + birlik.getText().trim());
                            hkKirim.setManba(0);
                            hkList.add(hkKirim);
                            hk.setDona(hk.getDona()-hkChiqim.getDona());
                        }
                    }
                }
            }
        }
        hisobKitobModels.addBatch(connection, hkList);
    }

    private void initGridPane() {
        gridPane.getChildren().removeAll(gridPane.getChildren());
        SetHVGrow.VerticalHorizontal(gridPane);
        int rowIndex = 0;

        rowIndex++;
        gridPane.add(tovarNomiTextField, 0, rowIndex, 1,1);
        gridPane.add(qidirBarCodeTextField, 1, rowIndex, 1,1);

        rowIndex += 2;
        gridPane.add(hisob1TarkibTableView, 0, rowIndex, 1, 1);
        GridPane.setHgrow(hisob1TarkibTableView, Priority.ALWAYS);
        GridPane.setVgrow(hisob1TarkibTableView, Priority.ALWAYS);

        gridPane.add(maydaTarkibTableView, 1, rowIndex, 1, 1);
        GridPane.setHgrow(maydaTarkibTableView, Priority.ALWAYS);
        GridPane.setVgrow(maydaTarkibTableView, Priority.ALWAYS);

    }

    private void initBorderPane() {
        borderpane.setCenter(centerPane);
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Tovar Xaridi");
        scene = new Scene(borderpane, 600, 600);
        stage.setScene(scene);
    }

    private void addTovar(HisobKitob butunTovar) {
        boolean ortiqchaDona = false;
        HisobKitob maydalanuvchiTovar = hisobKitobModels.cloneHisobKitob(butunTovar);
        maydalanuvchiTovar.setHisob1(hisob1.getId());
        maydalanuvchiTovar.setHisob2(yordamchiHisob);
        maydalanuvchiTovar.setAmal(amalTuri);
        maydalanuvchiTovar.setManba(butunTovar.getId());
        maydalanuvchiTovar.setDona(1.0);
        double adad = 1.0;
        HisobKitob hk1 = null;
        for (HisobKitob hk : maydaTarkibList) {
            if (hk.getBarCode().equalsIgnoreCase(maydalanuvchiTovar.getBarCode())) {
                if (butunTovar.getDona()>=hk.getDona()+1) {
                    hk1 = hk;
                    adad += hk.getDona();
                    break;
                } else {
                    Standart tovar = GetDbData.getTovar(hk.getTovar());
                    showKamomat(tovar, hk.getDona()+1, hisobKitobCursor.getBarCode(), butunTovar.getDona());
                    ortiqchaDona = true;
                }
            }
        }
        if (!ortiqchaDona) {
            if (hk1 == null) {
                maydaTarkibList.add(maydalanuvchiTovar);
            } else {
                hk1.setDona(adad);
            }
        }
        maydaTarkibTableView.getSelectionModel().select(maydalanuvchiTovar);
        maydaTarkibTableView.scrollTo(maydalanuvchiTovar);
        maydaTarkibTableView.refresh();
        maydaTarkibTableView.requestFocus();
    }

    private Double getJami(ObservableList<HisobKitob> hisobKitobs) {
        Double jamiDouble = .0;
        for (HisobKitob hk: hisobKitobs) {
            jamiDouble += hk.getDona()*hk.getNarh()/hk.getKurs();
        }
        return jamiDouble;
    }

    public QaydnomaData getQaydnomaData() {
        return qaydnomaData;
    }

    public void setQaydnomaData(QaydnomaData qaydnomaData) {
        this.qaydnomaData = qaydnomaData;
    }

    public Standart getStandart(int id, ObservableList<Standart> standartObservableList, String tableName) {
        Standart standart = null;
        for (Standart s: standartObservableList) {
            if (s.getId().equals(id)) {
                standart = s;
                break;
            }
        }

        if (standart == null) {
            standartModels.setTABLENAME(tableName);
            standart = standartModels.getDataId(connection, id);
            standartObservableList.add(standart);
        }
        return standart;
    }

    private  TableColumn<HisobKitob, Double> getAdadColumn() {
        TableColumn<HisobKitob, Double>  adad = new TableColumn<>("Adad");
        adad.setMinWidth(80);
        adad.setMaxWidth(80);
        adad.setCellValueFactory(new PropertyValueFactory<>("dona"));
        adad.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                return decimalFormat.format(object);
            }

            @Override
            public Double fromString(String string) {
                string = string.replaceAll(" ", "");
                return Double.valueOf(string);
            }
        }));
        adad.setOnEditCommit(event -> {
            Standart tovar = GetDbData.getTovar(hisobKitobCursor.getTovar());
            double barCodeCount = hisobKitobModels.getBarCodeCount(connection, hisobKitobCursor.getHisob1(), hisobKitobCursor.getBarCode());
            if (barCodeCount >= event.getNewValue()) {
                hisobKitobCursor.setDona(event.getNewValue());
            } else {
                hisobKitobCursor.setDona(event.getOldValue());
                showKamomat(tovar, event.getNewValue(), hisobKitobCursor.getBarCode(), barCodeCount);
            }
            maydaTarkibTableView.refresh();
        });
        adad.setStyle( "-fx-alignment: CENTER;");
        return adad;
    }

    private void showKamomat(Standart tovar, double adad, String barCode, double zaxira) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText("Tovar: " +tovar.getText() + "\nShtrix kod: " + barCode + "\nTalab etilgan adad: " + adad  + "\nZaxirada: " + zaxira  + "\nKamomat:  " + (adad - zaxira));
        alert.setContentText("Yetarsiz adad");
        alert.showAndWait();
    }
    private void Taftish(String oldValue, String newValue) {
        ObservableList<HisobKitob> subentries = FXCollections.observableArrayList();
        newValue = newValue.toLowerCase();

        if ( oldValue != null && (newValue.length() < oldValue.length()) ) {
            hisob1TarkibTableView.setItems( hisob1TarkibList );
        }

        for ( HisobKitob hk: hisob1TarkibList ) {
            if (hk.getIzoh().toLowerCase().contains(newValue)) {
                subentries.add(hk);
            }
        }
        hisob1TarkibTableView.setItems(subentries);
        hisob1TarkibTableView.refresh();
    }


}
