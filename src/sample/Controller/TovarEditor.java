package sample.Controller;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import sample.Config.MySqlDBGeneral;
import sample.Data.*;
import sample.Enums.ServerType;
import sample.Model.*;
import sample.Tools.*;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Optional;

public class TovarEditor extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    HBox topPane = new HBox();
    VBox leftPane = new VBox();
    VBox centerPane = new VBox();
    VBox rightPane = new VBox();
    HBox bottomPane = new HBox();
    GridPane gridPane = new GridPane();
    Label qaydIdLabel = new Label();
    Label hujjatIdLabel = new Label();
    Label sotuvchiLabel = new Label();
    Label xaridorLabel = new Label();
    Label kassagaLabel = new Label();
    Label balanceLabel = new Label();
    HBoxTextFieldPlusButton tovarHBox = new HBoxTextFieldPlusButton();
    HBoxTextFieldPlusButton valutaHBox = new HBoxTextFieldPlusButton();
    TextField barCodeTextField = new TextField();
    ComboBox<Standart> birlikComboBox = new ComboBox<>();
    ComboBox<Standart> narhTuriComboBox = new ComboBox<>();
    TextField kursTextField = new TextField();
    TextField adadTextField = new TextField();
    TextField narhTextField = new TextField();
    TextField chegirmaTextField = new TextField();
    TextField naqdTextField = new TextField();
    TextField naqdMilliyTextField = new TextField();
    TextField kursMilliyTextField = new TextField();
    TextField plasticTextField = new TextField();
    TextField kursPlasticTextField = new TextField();
    Button yakunlaButton;
    Font font = Font.font("Arial", FontWeight.BOLD,20);
    DecimalFormat decimalFormat = new MoneyShow();

    Connection connection;
    User user = new User(1, "admin", "", "admin");
    Kassa kassa;
    Kurs kurs;
    Standart tovar;
    Valuta valuta;
    QaydnomaData qaydnomaData;
    HisobKitob hisobKitob;
    HisobKitobModels hisobKitobModels = new HisobKitobModels();
    int padding = 3;
    Date date = new Date();
    Double adad = 1d;
    Double narh = 0d;

    ObservableList<Standart> tovarObservableList = FXCollections.observableArrayList();
    ObservableList<BarCode> barCodeList = FXCollections.observableArrayList();
    ObservableList<Standart> birlikObservableList = FXCollections.observableArrayList();
    ObservableList<HisobKitob> hisobKitobObservableList;
    ObservableList<Hisob> hisobObservableList;
    ObservableList<Valuta> valutaObservableList;
    ObservableList<Standart> narhTuriObservableList;

    public static void main(String[] args) {
        launch(args);
    }

    public TovarEditor() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
        qaydnomaData = new QaydnomaData(1, 4, 1, date, 10, "DO`KON 1/40", 13, "DO`KON 3/85", "", 0d, 1, user.getId(), date);
        initData();
        ibtido();
    }

    public TovarEditor(Connection connection, User user, QaydnomaData qaydnomaData, HisobKitob hisobKitob) {
        this.connection = connection;
        this.user = user;
        this.qaydnomaData = qaydnomaData;
        this.hisobKitob = hisobKitob;
        initData();
    }

    public TovarEditor(Connection connection, User user, QaydnomaData qaydnomaData) {
        this.connection = connection;
        this.user = user;
        this.qaydnomaData = qaydnomaData;
        hisobKitobObservableList = hisobKitobModels.getAnyData(
                connection,
                "qaydId = " +qaydnomaData.getId(),
                ""
        );
        initTolovNodes();
        initData();
    }

    private void ibtido() {
        initTopPane();
        initLeftPane();
        initCenterPane();
        initRightPane();
        initBottomPane();
        initBorderPane();
        initBarCodeTextField();
    }

    private void initData() {
        Aloqa aloqa = ConnectionType.getAloqa();
        kassa = Sotuvchi3.getKassaData(connection, aloqa.getText().trim());
        hisobObservableList = GetDbData.getHisobObservableList();
        StandartModels standartModels = new StandartModels("Tovar");
        tovarObservableList = standartModels.get_data(connection);
        valutaObservableList = GetDbData.getValutaObservableList();
        standartModels.setTABLENAME("NarhTuri");
        narhTuriObservableList = standartModels.get_data(connection);
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

    public void add() {
        Integer amal = 4;
        KursModels kursModels = new KursModels();
        hisobKitobObservableList = hisobKitobModels.getAnyData(connection,
                "qaydId = " + qaydnomaData.getId()
                        + " and amal = 4" + " and hisob2 = " + qaydnomaData.getKirimId() + " and tovar>0","");
        if (hisobKitobObservableList.size()>0) {
            HisobKitob h = hisobKitobObservableList.get(0);
            kurs = kursModels.getKurs(connection, h.getValuta(), qaydnomaData.getSana(), "sana desc");
        } else {
            kurs = kursModels.getKurs(connection, kassa.getValuta(), qaydnomaData.getSana(), "sana desc");
        }
        hisobKitob = yangiHisob(kurs, amal);
        hisobKitob.setDona(1d);
        yakunlaButton = new Tugmachalar().getAdd();
        ibtido();
        initGridPane();
        hkToForm(hisobKitob);
        disable(false);
        valutaHBox.setDisable(true);
        kursTextField.setDisable(true);
        centerPane.getChildren().addAll(gridPane);

        yakunlaButton.setOnAction(event -> {
            formToHk(hisobKitob);
            double zaxiradagiAdad = hisobKitobModels.getBarCodeCount(connection, kassa.getTovarHisobi(), barCodeTextField.getText());
            String adadString = adadTextField.getText();
            double adad;
            if (!adadString.isEmpty()) {
                adad = Double.valueOf(adadString);
            } else {
                adad = 0d;
            }
            if (zaxiradagiAdad < adad) {
                Alerts.showKamomat(tovar, adad, hisobKitob.getBarCode(), zaxiradagiAdad);
                return;
            }
            Savdo savdo = new Savdo(connection);
            savdo.setQaydnomaData(qaydnomaData);
            savdo.initHisobKitob(hisobKitob);
            if (!savdo.sot()){
                Alerts.AlertString("Yetarsiz adad");
            }
            valutaHBox.setDisable(false);
            kursTextField.setDisable(false);
            stage.close();
        });

        stage = new Stage();
        initStage(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    public void delete(HisobKitob hisobKitob) {
        this.hisobKitob = hisobKitob;
        hisobKitobObservableList = hisobKitobModels.getAnyData(connection,
                "qaydId = " + hisobKitob.getQaydId()
                        + " and barcode = '" + hisobKitob.getBarCode() + "'","");
        if (hisobKitobObservableList.size()==0) {
            return;
        }
        yakunlaButton = new Tugmachalar().getDelete();
        disable(true);
        hkToForm(this.hisobKitob);
        ibtido();
        initGridPane();
        centerPane.getChildren().addAll(gridPane);

        yakunlaButton.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.getButtonTypes().removeAll(alert.getButtonTypes());
            ButtonType okButton = new ButtonType("Ha");
            ButtonType noButton = new ButtonType("Yo`q");
            alert.getButtonTypes().addAll(okButton, noButton);
            alert.setTitle("Diqqat !!!");
            BarCode barCode = GetDbData.getBarCode(hisobKitob.getBarCode());
            Standart birlik = GetDbData.getBirlik(barCode.getBirlik());
            Valuta valuta = GetDbData.getValuta(hisobKitob.getValuta());
            alert.setHeaderText(hisobKitob.getIzoh() + "\n"
                    + hisobKitob.getDona() + " " + birlik.getText().toLowerCase() + "\n"
                    + decimalFormat.format(hisobKitob.getNarh()) + " " + valuta.getValuta().toLowerCase() + "\n"
                    + "Jami " + decimalFormat.format(hisobKitob.getDona()*hisobKitob.getNarh()) + " " + valuta.getValuta().toLowerCase() + " lik xarid butunlay o`chiriuladi\n"
            );
            alert.setContentText("O`chirish kerakligiga ishonchingiz komilmi? ???");
            Optional<ButtonType> option = alert.showAndWait();
            ButtonType buttonType = option.get();
            if (okButton.equals(buttonType)) {
                if (hisobKitobObservableList.size() > 0) {
                    for (HisobKitob hk: hisobKitobObservableList) {
                        hisobKitobModels.delete_data(connection, hk);
                    }
                }
            }
            stage.close();
        });

        stage = new Stage();
        initStage(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    public void edit(HisobKitob hisobKitob) {
        this.hisobKitob = hisobKitob;
        double zaxiradagiAdad = hisobKitobModels.getBarCodeCount(connection, kassa.getTovarHisobi(), hisobKitob.getBarCode());
        String adadString = adadTextField.getText();
        double adad;
        if (!adadString.isEmpty()) {
            adad = Double.valueOf(adadString);
        } else {
            adad = 0d;
        }
/*
        if (zaxiradagiAdad + hisobKitob.getDona() < adad) {
            Alerts.showKamomat(tovar, adad, hisobKitob.getBarCode(), zaxiradagiAdad + hisobKitob.getDona());
            return;
        }
*/
        hisobKitobObservableList = hisobKitobModels.getAnyData(connection,
                "qaydId = " + hisobKitob.getQaydId()
                        + " and barcode = '" + hisobKitob.getBarCode()
                        + "'","");
        if (hisobKitobObservableList.size()==0) {
            return;
        }
        yakunlaButton = new Tugmachalar().getEdit();
        disable(false);
        tovarHBox.setDisable(true);
        valutaHBox.setDisable(true);
        birlikComboBox.setDisable(true);
        barCodeTextField.setDisable(true);
        hkToForm(hisobKitob);
        ibtido();
        initGridPane();
        centerPane.getChildren().addAll(gridPane);
        yakunlaButton.setOnAction(event -> {
            formToHk(hisobKitob);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.getButtonTypes().removeAll(alert.getButtonTypes());
            ButtonType okButton = new ButtonType("Ha");
            ButtonType noButton = new ButtonType("Yo`q");
            alert.getButtonTypes().addAll(okButton, noButton);
            alert.setTitle("Diqqat !!!");
            BarCode barCode = GetDbData.getBarCode(hisobKitob.getBarCode());
            Standart birlik = GetDbData.getBirlik(barCode.getBirlik());
            Valuta valuta = GetDbData.getValuta(hisobKitob.getValuta());
            alert.setHeaderText(hisobKitob.getIzoh() + "\n"
                    + hisobKitob.getDona() + " " + birlik.getText().toLowerCase() + " "
                    + decimalFormat.format(hisobKitob.getNarh()) + " " + valuta.getValuta().toLowerCase() + "\n"
                    + "Jami " + decimalFormat.format(hisobKitob.getDona()*hisobKitob.getNarh()) + " " + valuta.getValuta().toLowerCase() + " lik xarid o`zgartiriuladi\n"
            );
            alert.setContentText("O`zgartirish lozimligiga ishonchingiz komilmi? ???");
            Optional<ButtonType> option = alert.showAndWait();
            ButtonType buttonType = option.get();
            if (okButton.equals(buttonType)) {
                if (hisobKitobObservableList.size() > 0) {
                    for (HisobKitob hk: hisobKitobObservableList) {
                        hisobKitobModels.delete_data(connection, hk);
                    }
                }
                formToHk(hisobKitob);
                Savdo savdo = new Savdo(connection);
                savdo.setQaydnomaData(qaydnomaData);
                savdo.initHisobKitob(hisobKitob);
                if (!savdo.sot()){
                    Alerts.AlertString("Yetarsiz adad");
                }
            }
            stage.close();
        });
        stage = new Stage();
        initStage(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    public void tolov() {
        yakunlaButton = new Button("To`lovni yakunla");
        qaydToForm(qaydnomaData);
        yakunlaButton.setOnAction(event -> {
            saveHisobKitob();
            stage.close();
        });
        ibtido();
        initGridPane2();
        centerPane.getChildren().addAll(gridPane);
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
        stage.setTitle("Yangi tovar");
        scene = new Scene(borderpane, 400, 265);
        stage.setScene(scene);
    }

    private void initGridPane() {
        initNarhTuriComboBox();
        initTovarHBox();
        initBirlikComboBox();
        initValutaHBox();
        initYakunlaButton();
        SetHVGrow.VerticalHorizontal(gridPane);
        gridPane.setPadding(new Insets(padding));

        int rowIndex = 0;
        gridPane.add(new Label("Qayd N: "), 0, rowIndex, 1, 1);
        qaydIdLabel = new Label(hisobKitob.getQaydId()+"");
        gridPane.add(qaydIdLabel, 1, rowIndex, 1, 1);
        GridPane.setHalignment(qaydIdLabel, HPos.RIGHT);

        rowIndex++;
        gridPane.add(new Label("Hujjat N: "), 0, rowIndex, 1, 1);
        hujjatIdLabel = new Label(hisobKitob.getHujjatId()+"");
        gridPane.add(hujjatIdLabel, 1, rowIndex, 1, 1);
        GridPane.setHalignment(hujjatIdLabel, HPos.RIGHT);

        rowIndex++;
        sotuvchiLabel = new Label(qaydnomaData.getChiqimNomi());
        gridPane.add(new Label("Sotuvchi : "), 0, rowIndex, 1, 1);
        gridPane.add(sotuvchiLabel, 1, rowIndex, 1, 1);
        GridPane.setHalignment(sotuvchiLabel, HPos.RIGHT);

        rowIndex++;
        gridPane.add(new Label("Xaridor : "), 0, rowIndex, 1, 1);
        xaridorLabel = new Label(qaydnomaData.getKirimNomi());
        gridPane.add(xaridorLabel, 1, rowIndex, 1, 1);
        GridPane.setHalignment(xaridorLabel, HPos.RIGHT);

        rowIndex++;
        gridPane.add(new Label("Narh turi"), 0, rowIndex, 1, 1);
        gridPane.add(narhTuriComboBox, 1, rowIndex, 1, 1);
        GridPane.setHalignment(narhTuriComboBox, HPos.RIGHT);

        rowIndex++;
        gridPane.add(new Label("Tovar"), 0, rowIndex, 1, 1);
        gridPane.add(tovarHBox, 1, rowIndex, 1, 1);
        GridPane.setHalignment(tovarHBox, HPos.RIGHT);

        rowIndex++;
        gridPane.add(new Label("Birlik"), 0, rowIndex, 1, 1);
        gridPane.add(birlikComboBox, 1, rowIndex, 1, 1);
        GridPane.setHalignment(birlikComboBox, HPos.RIGHT);

        rowIndex++;
        gridPane.add(new Label("BarCode"), 0, rowIndex, 1, 1);
        gridPane.add(barCodeTextField, 1, rowIndex, 1, 1);
        GridPane.setHalignment(barCodeTextField, HPos.RIGHT);

        rowIndex++;
        gridPane.add(new Label("Valuta"), 0, rowIndex, 1, 1);
        gridPane.add(valutaHBox, 1, rowIndex, 1, 1);
        GridPane.setHalignment(valutaHBox, HPos.RIGHT);

        rowIndex++;
        gridPane.add(new Label("Kurs"), 0, rowIndex, 1, 1);
        gridPane.add(kursTextField, 1, rowIndex, 1, 1);
        GridPane.setHalignment(kursTextField, HPos.RIGHT);

        rowIndex++;
        gridPane.add(new Label("Adad"), 0, rowIndex, 1, 1);
        gridPane.add(adadTextField, 1, rowIndex, 1, 1);
        GridPane.setHalignment(adadTextField, HPos.RIGHT);

        rowIndex++;
        gridPane.add(new Label("Narh"), 0, rowIndex, 1, 1);
        gridPane.add(narhTextField, 1, rowIndex, 1, 1);
        GridPane.setHalignment(narhTextField, HPos.RIGHT);

        rowIndex++;
        gridPane.add(yakunlaButton, 0, rowIndex, 2, 1);
        GridPane.setHalignment(yakunlaButton, HPos.CENTER);
    }

    private void initGridPane2() {
        initYakunlaButton();
        SetHVGrow.VerticalHorizontal(gridPane);
        gridPane.setPadding(new Insets(padding));

        int rowIndex = 0;
        gridPane.add(new Label("Qayd N: "), 0, rowIndex, 1, 1);
        qaydIdLabel = new Label(qaydnomaData.getId()+"");
        gridPane.add(qaydIdLabel, 1, rowIndex, 2, 1);
        GridPane.setHalignment(qaydIdLabel, HPos.RIGHT);

        rowIndex++;
        gridPane.add(new Label("Hujjat N: "), 0, rowIndex, 1, 1);
        hujjatIdLabel = new Label(qaydnomaData.getHujjat()+"");
        gridPane.add(hujjatIdLabel, 1, rowIndex, 2, 1);
        GridPane.setHalignment(hujjatIdLabel, HPos.RIGHT);

        rowIndex++;
        sotuvchiLabel = new Label(qaydnomaData.getChiqimNomi());
        gridPane.add(new Label("Sotuvchi : "), 0, rowIndex, 1, 1);
        gridPane.add(sotuvchiLabel, 1, rowIndex, 2, 1);
        GridPane.setHalignment(sotuvchiLabel, HPos.RIGHT);

        rowIndex++;
        gridPane.add(new Label("Xaridor : "), 0, rowIndex, 1, 1);
        xaridorLabel = new Label(qaydnomaData.getKirimNomi());
        gridPane.add(xaridorLabel, 1, rowIndex, 2, 1);
        GridPane.setHalignment(xaridorLabel, HPos.RIGHT);

        rowIndex++;
        gridPane.add(new Label("Kassaga"), 0, rowIndex, 1, 1);
        gridPane.add(kassagaLabel, 1, rowIndex, 2, 1);
        GridPane.setHalignment(kassagaLabel, HPos.RIGHT);

        rowIndex++;
        gridPane.add(new Label("Chegirma"), 0, rowIndex, 1, 1);
        gridPane.add(chegirmaTextField, 1, rowIndex, 2, 1);
        GridPane.setHalignment(chegirmaTextField, HPos.RIGHT);
        GridPane.setHgrow(chegirmaTextField, Priority.ALWAYS);

        rowIndex++;
        Valuta v1 = GetDbData.getValuta(1);
        gridPane.add(new Label(v1.getValuta()), 0, rowIndex, 1, 1);
        gridPane.add(naqdTextField, 1, rowIndex, 2, 1);
        GridPane.setHalignment(naqdTextField, HPos.RIGHT);
        GridPane.setHgrow(naqdTextField, Priority.ALWAYS);

        rowIndex++;
        Valuta v2 = GetDbData.getValuta(2);
        gridPane.add(new Label(v2.getValuta()), 0, rowIndex, 1, 1);
        gridPane.add(naqdMilliyTextField, 1, rowIndex, 1, 1);
        gridPane.add(kursMilliyTextField, 2, rowIndex, 1, 1);
        GridPane.setHalignment(naqdMilliyTextField, HPos.RIGHT);
        GridPane.setHgrow(naqdMilliyTextField, Priority.ALWAYS);
        GridPane.setHalignment(kursMilliyTextField, HPos.RIGHT);
        GridPane.setHgrow(kursMilliyTextField, Priority.ALWAYS);

        rowIndex++;
        gridPane.add(new Label("Plastic"), 0, rowIndex, 1, 1);
        gridPane.add(plasticTextField, 1, rowIndex, 1, 1);
        gridPane.add(kursPlasticTextField, 2, rowIndex, 1, 1);
        GridPane.setHalignment(plasticTextField, HPos.RIGHT);
        GridPane.setHgrow(plasticTextField, Priority.ALWAYS);
        GridPane.setHalignment(kursPlasticTextField, HPos.RIGHT);
        GridPane.setHgrow(kursPlasticTextField, Priority.ALWAYS);

        rowIndex++;
        gridPane.add(new Label("Balans"), 0, rowIndex, 1, 1);
        gridPane.add(balanceLabel, 1, rowIndex, 1, 1);
        GridPane.setHalignment(balanceLabel, HPos.RIGHT);

        rowIndex++;
        gridPane.add(yakunlaButton, 0, rowIndex, 3, 1);
        GridPane.setHalignment(yakunlaButton, HPos.CENTER);
    }

    private void initTovarHBox() {
        HBox.setHgrow(tovarHBox, Priority.ALWAYS);
        TextField textField = tovarHBox.getTextField();
        TextFields.bindAutoCompletion(textField, tovarObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Standart> autoCompletionEvent) -> {
            Standart newValue = autoCompletionEvent.getCompletion();
            if (newValue != null) {
                tovar = newValue;
                tovarniYangila(tovar);
            }
        });

        Button addButton = tovarHBox.getPlusButton();
        addButton.setOnAction(event -> {
            Standart newValue = addTovar();
            if (newValue != null) {
                tovar = newValue;
                textField.setText(tovar.getText());
                tovarniYangila(tovar);
            }
        });
    }

    private void initValutaHBox() {
        HBox.setHgrow(valutaHBox, Priority.ALWAYS);
        valuta = GetDbData.getValuta(kassa.getValuta());
        TextField textField = valutaHBox.getTextField();
        textField.setText(valuta.getValuta());
        TextFields.bindAutoCompletion(textField, valutaObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Valuta> autoCompletionEvent) -> {
            Valuta newValue = autoCompletionEvent.getCompletion();
            if (newValue != null) {
                valuta = newValue;
                hisobKitob.setValuta(valuta.getId());
                KursModels kursModels = new KursModels();
                Kurs kurs = kursModels.getKurs(connection, valuta.getId(), date, "sana desc");
                if (valuta.getStatus()==1) {
                    hisobKitob.setKurs(1d);
                    kursTextField.setText("1.0");
                } else {
                    hisobKitob.setKurs(kurs.getKurs());
                    kursTextField.setText(decimalFormat.format(kurs.getKurs()));
                }
            }
        });

        Button addButton = valutaHBox.getPlusButton();
        addButton.setOnAction(event -> {
            Valuta newValuta = addValuta();
            if (newValuta != null) {
                valuta = newValuta;
                hisobKitob.setValuta(valuta.getId());
                KursModels kursModels = new KursModels();
                Kurs kurs = kursModels.getKurs(connection, valuta.getId(), date, "sana desc");
                hisobKitob.setKurs(kurs.getKurs());
                textField.setText(valuta.getValuta());
                kursTextField.setText(decimalFormat.format(kurs.getKurs()));
            }
        });
    }

    private Valuta addValuta() {
        Valuta valuta1 = null;
        ValutaController valutaController = new ValutaController();
        valutaController.display(connection, user);
        if (valutaController.getDoubleClick()) {
            valuta1 = valutaController.getDoubleClickedRow();
            boolean yangi = true;
            for (Valuta v: valutaObservableList) {
                if (v.getId().equals(valuta1.getId())) {
                    yangi = false;
                    break;
                }
            }
            if (yangi) {
                valutaObservableList.add(valuta1);
                TextField textField = valutaHBox.getTextField();
                textField.setText(valuta1.getValuta());
                TextFields.bindAutoCompletion(textField, valutaObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Valuta> autoCompletionEvent) -> {
                    Valuta newValue = autoCompletionEvent.getCompletion();
                    if (newValue != null) {
                        valuta = newValue;
                    }
                });
            }
        }
        return valuta1;
    }

    private void initBirlikComboBox() {
        birlikComboBox.setMaxWidth(2000);
        birlikComboBox.setPrefWidth(168);
        birlikComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                for (BarCode bc: barCodeList) {
                    if (bc.getBirlik().equals(newValue.getId())) {
                        barCodeTextField.setText(bc.getBarCode());
                        Standart narhTuri = narhTuriComboBox.getValue();
                        TovarNarhiModels tovarNarhiModels = new TovarNarhiModels();
                        Double tovarNarhiDouble = tovarNarhiModels.tovarNarhiniOl(connection, bc, narhTuri.getId());
                        if (tovarNarhiDouble == null) {
                            tovarNarhiDouble = 0d;
                        }
                        tovarNarhiDouble = tovarNarhiDouble * converter(kursTextField) * tovarDonasi(bc);
                        narhTextField.setText(decimalFormat.format(tovarNarhiDouble));
                        break;
                    }
                }
            }
        });
    }

    private void tovarniYangila(Standart tovar) {
        hisobKitob.setTovar(tovar.getId());
        barCodeList.removeAll(barCodeList);
        barCodeList = GetDbData.getBarCodeList(tovar.getId());
        if (barCodeList.size()>0) {
            hisobKitob.setBarCode(barCodeList.get(0).getBarCode());
            birlikObservableList.removeAll(birlikObservableList);
            for (BarCode bc : barCodeList) {
                Standart birlik = GetDbData.getBirlik(bc.getBirlik());
                if (birlik != null) {
                    birlikObservableList.add(birlik);
                }
            }
            birlikComboBox.setItems(birlikObservableList);
            if (birlikObservableList.size() > 0) {
                birlikComboBox.getSelectionModel().selectFirst();
            }
            BarCode barCode = barCodeList.get(0);
            barCodeTextField.setText(barCode.getBarCode());
            Standart narhTuri = narhTuriComboBox.getValue();
            TovarNarhiModels tovarNarhiModels = new TovarNarhiModels();
            Double tovarNarhiDouble = tovarNarhiModels.tovarNarhiniOl(connection, barCode, narhTuri.getId());
            if (tovarNarhiDouble == null) {
                tovarNarhiDouble = 0d;
            }
            tovarNarhiDouble = tovarNarhiDouble * converter(kursTextField) * tovarDonasi(barCode);
            narhTextField.setText(decimalFormat.format(tovarNarhiDouble));
        }
    }

    private void initBarCodeTextField() {
        StringBuffer stringBuffer = new StringBuffer();
        barCodeTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode()== KeyCode.ENTER) {
                    stringBuffer.delete(0, stringBuffer.length());
                    String string = barCodeTextField.getText().trim();
                    if (!string.isEmpty()) {
                        BarCode barCode = GetDbData.getBarCode(string);
                        if (barCode != null) {
                            tovar = GetDbData.getTovar(barCode.getTovar());
                            if (tovar != null) {
                                TextField tovarTextField = tovarHBox.getTextField();
                                tovarTextField.setText(tovar.getText());
                            }
                            barCodeList = GetDbData.getBarCodeList(tovar.getId());
                            birlikObservableList.removeAll(birlikObservableList);
                            Standart birlik = null;
                            for (BarCode bc: barCodeList) {
                                Standart b = GetDbData.getBirlik(bc.getBirlik());
                                birlikObservableList.add(b);
                                if (barCode.getBarCode().equals(bc.getBarCode())) {
                                    birlik = b;
                                    Standart narhTuri = narhTuriComboBox.getValue();
                                    TovarNarhiModels tovarNarhiModels = new TovarNarhiModels();
                                    Double tovarNarhiDouble = tovarNarhiModels.tovarNarhiniOl(connection, barCode, narhTuri.getId());
                                    if (tovarNarhiDouble == null) {
                                        tovarNarhiDouble = 0d;
                                    }
                                    tovarNarhiDouble = tovarNarhiDouble * converter(kursTextField) * tovarDonasi(bc);
                                    narhTextField.setText(decimalFormat.format(tovarNarhiDouble));
                                }
                            }
                            birlikComboBox.setItems(birlikObservableList);
                            birlikComboBox.getSelectionModel().select(birlik);
                        } else {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Diqqat!!!");
                            alert.setHeaderText(string + " shtrixkodga muvofiq tovar topilmadi");
                            alert.setContentText("");
                            alert.showAndWait();
                        }
                    }
                }
            }
        });
    }

    private void initYakunlaButton() {
        yakunlaButton.setMaxWidth(2000);
        yakunlaButton.setPrefWidth(168);
        yakunlaButton.setFont(font);
    }

    private Standart addTovar() {
        Standart tovar1 = null;
        TovarController1 tovarController = new TovarController1(connection, user);
        tovarController.display();
        if (tovarController.getDoubleClick()) {
            tovar1 = tovarController.getDoubleClickedRow();
            boolean yangi = true;
            for (Standart t: tovarObservableList) {
                if (t.getId().equals(tovar1.getId())) {
                    yangi = false;
                    break;
                }
            }
            if (yangi) {
                tovarObservableList.add(tovar1);
                TextField textField = tovarHBox.getTextField();
                TextFields.bindAutoCompletion(textField, tovarObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Standart> autoCompletionEvent) -> {
                    Standart newValue = autoCompletionEvent.getCompletion();
                    if (newValue != null) {
                        tovar = newValue;
                        tovarniYangila(tovar);
                    }
                });

            }
        }
        return tovar1;
    }

    private void initNarhTuriComboBox() {
        narhTuriComboBox.setMaxWidth(2000);
        narhTuriComboBox.setPrefWidth(150);
        SetHVGrow.VerticalHorizontal(narhTuriComboBox);
        narhTuriComboBox.setItems(narhTuriObservableList);
        int savdoTuri = kassa.getSavdoTuri();
        Standart narhTuri = null;
        for (Standart n: narhTuriObservableList) {
            if (n.getId().equals(savdoTuri)) {
                narhTuri = n;
            }
        }
        if (narhTuri != null) {
            narhTuriComboBox.getSelectionModel().select(narhTuri);
        } else {
            narhTuriComboBox.getSelectionModel().selectFirst();
        }

        narhTuriComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {});
    }

    private void disable(Boolean disable) {
        narhTuriComboBox.setDisable(disable);
        tovarHBox.setDisable(disable);
        birlikComboBox.setDisable(disable);
        barCodeTextField.setDisable(disable);
        adadTextField.setDisable(disable);
        narhTextField.setDisable(disable);
        valutaHBox.setDisable(disable);
        kursTextField.setDisable(disable);
    }

    private void hkToForm(HisobKitob hk) {
        qaydIdLabel.setText(hk.getQaydId()+"");
        hujjatIdLabel.setText(hk.getHujjatId()+"");
        sotuvchiLabel.setText(qaydnomaData.getChiqimNomi());
        xaridorLabel.setText(qaydnomaData.getKirimNomi());
        kursTextField.setText(decimalFormat.format(hk.getKurs()));
        for (Standart s: narhTuriObservableList) {
            if (s.getId().equals(kassa.getSavdoTuri())) {
                narhTuriComboBox.getSelectionModel().select(s);
                break;
            }
        }
        TextField valutaTextField = valutaHBox.getTextField();
        valuta = GetDbData.getValuta(hk.getValuta());
        if (valuta != null) {
            valutaHBox.getTextField().setText(valuta.getValuta());
            KursModels kursModels = new KursModels();
            kurs = kursModels.getKurs(connection, valuta.getId(), date, "sana desc");
            if (valuta.getStatus()==1) {
                kursTextField.setText("1.00");
            } else {
                kursTextField.setText(decimalFormat.format(kurs.getKurs()));
            }
            kursTextField.setDisable(true);
        }
        adadTextField.setText(decimalFormat.format(hk.getDona()));
        narhTextField.setText(decimalFormat.format(hk.getNarh()));
        System.out.println(hk.getKurs());
    }

    private void formToHk(HisobKitob hk) {
        hk.setHisob1(qaydnomaData.getChiqimId());
        hk.setHisob2(qaydnomaData.getKirimId());
        hk.setTovar(tovar.getId());
        hk.setIzoh(tovar.getText());
        KursModels kursModels = new KursModels();
        hk.setValuta(valuta.getId());
        hk.setKurs(converter(kursTextField));
        hk.setBarCode(barCodeTextField.getText().trim());
        Double adad = null;
        if (adadTextField.getText().isEmpty()) {
            adad = 0d;
        } else {
            adad = converter(adadTextField);
        }
        hk.setDona(adad);
        Double narh = null;
        if (narhTextField.getText().isEmpty()) {
            narh = 0d;
        } else {
            narh = converter(narhTextField);
        }
        hk.setNarh(narh);
        hk.setKurs(1d);
        hk.setIzoh(tovar.getText());
    }

    private void qaydToForm(QaydnomaData qaydnomaData) {
        for (HisobKitob hk: hisobKitobObservableList) {
            switch (hk.getAmal()) {
                case 7: //tolov
                    if (hk.getValuta().equals(1)) {
                        naqdTextField.setText(decimalFormat.format(hk.getNarh()));
                    } else {
                        naqdMilliyTextField.setText(decimalFormat.format(hk.getNarh()));
                        kursMilliyTextField.setText(decimalFormat.format(hk.getKurs()));
                    }
                    break;
                case 13: //chegirma
                    chegirmaTextField.setText(decimalFormat.format(hk.getNarh()));
                    break;
                case 15: //plastic
                    plasticTextField.setText(decimalFormat.format(hk.getNarh()));
                    kursPlasticTextField.setText(decimalFormat.format(hk.getKurs()));
                    break;
            }
        }
    }

    private void initTolovNodes() {
        HBox.setHgrow(kassagaLabel, Priority.ALWAYS);
        naqdTextField.setMaxWidth(2000);
        naqdTextField.setPrefWidth(150);

        naqdMilliyTextField.setMaxWidth(2000);
        naqdMilliyTextField.setPrefWidth(150);
        kursMilliyTextField.setMaxWidth(2000);
        kursMilliyTextField.setPrefWidth(150);

        plasticTextField.setMaxWidth(2000);
        plasticTextField.setPrefWidth(150);
        kursPlasticTextField.setMaxWidth(2000);
        kursPlasticTextField.setPrefWidth(150);

        chegirmaTextField.setMaxWidth(2000);
        chegirmaTextField.setPrefWidth(150);

        HBox.setHgrow(naqdTextField, Priority.ALWAYS);
        HBox.setHgrow(naqdMilliyTextField, Priority.ALWAYS);
        HBox.setHgrow(kursMilliyTextField, Priority.ALWAYS);
        HBox.setHgrow(chegirmaTextField, Priority.ALWAYS);
        HBox.setHgrow(plasticTextField, Priority.ALWAYS);
        HBox.setHgrow(kursPlasticTextField, Priority.ALWAYS);
    }

    private Double converter(TextField textField) {
        Double value = 0d;
        String string = textField.getText().trim();
        string = string.replaceAll(" ", "");
        string = string.replaceAll(",", ".");
        if (!string.isEmpty()) {
            value = Double.valueOf(string);
        }
        return value;
    }

    private Kurs kursOl() {
        Kurs kurs = null;
        for (HisobKitob hk: hisobKitobObservableList) {
            if (hk.getAmal() == 4 && hk.getHisob2().equals(qaydnomaData.getKirimId())) {
                kurs = new Kurs(0, null, hk.getValuta(), hk.getKurs(), 1, null);
                break;
            }
        }
        return kurs;
    }

    private HisobKitob chegirmaHisobi() {
        HisobKitob hisobKitob = null;
        Integer amal = 13;
        for (HisobKitob hk: hisobKitobObservableList) {
            if (hk.getAmal().equals(amal)) {
                hisobKitob = hk;
                break;
            }
        }
        if (hisobKitob == null) {
            Kurs kurs = kursOl();
            hisobKitob = yangiHisob(kurs, amal);
            hisobKitob.setHisob1(qaydnomaData.getKirimId());
            Integer chegirmaHisobi = hisobKitobModels.yordamchiHisob(connection, qaydnomaData.getKirimId(), "ChegirmaGuruhi", "Chegirma");
            hisobKitob.setHisob2(chegirmaHisobi);
            hisobKitob.setIzoh("Chegirma Xarid № " + qaydnomaData.getHujjat());
            hisobKitob.setAmal(amal);
        }
        hisobKitob.setNarh(converter(chegirmaTextField));
        return hisobKitob;
    }

    private HisobKitob naqdHisobi() {
        HisobKitob hisobKitob = null;
        Integer amal = 7;
        for (HisobKitob hk: hisobKitobObservableList) {
            if (hk.getAmal().equals(amal) && hk.getValuta().equals(1)) {
                hisobKitob = hk;
                break;
            }
        }
        if (hisobKitob == null) {
            Double kursDouble = 1d;
            Kurs kurs = new Kurs(null, null, 1, 1d, user.getId(), null);
            hisobKitob = yangiHisob(kurs, amal);
            hisobKitob.setHisob1(qaydnomaData.getKirimId());
            hisobKitob.setHisob2(kassa.getPulHisobi());
            hisobKitob.setIzoh("To`lov Xarid № " + qaydnomaData.getHujjat());
            hisobKitob.setAmal(amal);
        }
        hisobKitob.setNarh(converter(naqdTextField));
        return hisobKitob;
    }

    private HisobKitob naqdMilliyHisobi() {
        HisobKitob hisobKitob = null;
        ObservableList<HisobKitob> naqdList = FXCollections.observableArrayList();
        Integer amal = 7;
        for (HisobKitob hk: hisobKitobObservableList) {
            if (hk.getAmal().equals(amal) && hk.getValuta().equals(2)) {
                hisobKitob = hk;
                break;
            }
        }
        if (hisobKitob == null) {
            Double kursDouble = converter(kursMilliyTextField);
            Kurs kurs = new Kurs(null, null, hisobKitob.getValuta(), kursDouble, user.getId(), null);
            Valuta valuta = GetDbData.getValuta(hisobKitob.getValuta());
            hisobKitob = yangiHisob(kurs, amal);
            hisobKitob.setHisob1(qaydnomaData.getKirimId());
            hisobKitob.setHisob2(kassa.getPulHisobi());
            hisobKitob.setIzoh("To`lov " + valuta.getValuta() + " Xarid № " + qaydnomaData.getHujjat());
            hisobKitob.setAmal(amal);
        }
        hisobKitob.setNarh(converter(naqdMilliyTextField));
        return hisobKitob;
    }

    private HisobKitob plasticHisobi() {
        Integer amal = 15;
        HisobKitob hisobKitob = null;
        for (HisobKitob hk: hisobKitobObservableList) {
            if (hk.getAmal().equals(amal)) {
                hisobKitob = hk;
                break;
            }
        }
        if (hisobKitob == null) {
            Kurs kurs = kursOl();
            hisobKitob = yangiHisob(kurs, amal);
            Integer bankHisobi = hisobKitobModels.yordamchiHisob(connection, qaydnomaData.getChiqimId(), "Bank1"," Bank");
            hisobKitob.setHisob1(qaydnomaData.getKirimId());
            hisobKitob.setHisob2(bankHisobi);
            hisobKitob.setIzoh("Plastic : Xarid № " + qaydnomaData.getHujjat());
            hisobKitob.setAmal(amal);
        }
        hisobKitob.setNarh(converter(plasticTextField));
        return hisobKitob;
    }

    private HisobKitob yangiHisob(Kurs kurs, Integer amal) {
        HisobKitob hisobKitob = new HisobKitob(
                0,
                qaydnomaData.getId(),
                qaydnomaData.getHujjat(),
                amal,
                qaydnomaData.getChiqimId(),
                qaydnomaData.getKirimId(),
                kurs.getValuta(),
                0,
                kurs.getKurs(),
                "",
                0d,
                0d,
                0,
                "",
                user.getId(),
                date
        );
        return hisobKitob;
    }

    private void saveHisobKitob() {
        HisobKitob chegirmaHisobKitob = chegirmaHisobi();
        if (chegirmaHisobKitob.getId() == 0) {
            if (chegirmaHisobKitob.getNarh() > 0d) {
                hisobKitobModels.insert_data(connection, chegirmaHisobKitob);
            }
        } else {
            hisobKitobModels.update_data(connection, chegirmaHisobKitob);
        }
        HisobKitob naqdHisobKitob = naqdHisobi();
        if (naqdHisobKitob.getId() == 0) {
            if (naqdHisobKitob.getNarh() > 0d) {
                hisobKitobModels.insert_data(connection, naqdHisobKitob);
            }
        } else {
            hisobKitobModels.update_data(connection, naqdHisobKitob);
        }
        HisobKitob naqdMilliyHisobKitob = naqdMilliyHisobi();
        if (naqdMilliyHisobKitob.getId() == 0) {
            if (naqdMilliyHisobKitob.getNarh() > 0d) {
                hisobKitobModels.insert_data(connection, naqdMilliyHisobKitob);
            }
        } else {
            hisobKitobModels.update_data(connection, naqdMilliyHisobKitob);
        }
        HisobKitob plasticHisobKitob = plasticHisobi();
        if (plasticHisobKitob.getId() == 0) {
            if (plasticHisobKitob.getNarh() > 0d) {
                hisobKitobModels.insert_data(connection, plasticHisobKitob);
            }
        } else {
            hisobKitobModels.update_data(connection, plasticHisobKitob);
        }
    }

    private double tovarDonasi(BarCode barCode) {
        double dona = 1.0;
        double adadBarCode2 = 0;
        dona *= barCode.getAdad();
        int tarkibInt = barCode.getTarkib();
        if (tarkibInt>0) {
            while (true) {
                BarCode barCode2 = GetDbData.getBarCode(tarkibInt);
                adadBarCode2 = barCode2.getAdad();
                dona *= adadBarCode2;
                tarkibInt = barCode2.getTarkib();
                if (adadBarCode2 == 1.0) {
                    break;
                }
            }
        }
        return dona;
    }
}
