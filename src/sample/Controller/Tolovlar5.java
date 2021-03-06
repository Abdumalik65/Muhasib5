package sample.Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
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
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import sample.Config.MySqlDBGeneral;
import sample.Data.*;
import sample.Enums.ServerType;
import sample.Model.*;
import sample.Printer.SavdoChiptasiniChopEtish;
import sample.Tools.*;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.Date;

public class Tolovlar5 extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderPane;
    VBox centerPane = new VBox();
    HBox bottomHBox = new HBox();
    TableView<HisobKitob> tableView;
    ToggleGroup radioGroup;
    Connection connection;
    TextArea textArea;
    User user;
    QaydnomaData qaydnomaData;
    int padding = 5;
    Integer joriyJadval = 1;
    DecimalFormat decimalFormat = new MoneyShow();
    Font font = Font.font("Arial", FontWeight.BOLD,16);
    Font font1 = Font.font("Arial", FontWeight.BOLD,32);

    HisobModels hisobModels = new HisobModels();

    ObservableList<GridPane> gridPaneObservableList;
    ObservableList<Valuta> valutaObservableList;
    ObservableList<HisobKitob> xaridRoyxati = FXCollections.observableArrayList();
    ObservableList<JFXTolov2> naqdTolovRoyxatiJFXTolov2 = FXCollections.observableArrayList();
    ObservableList<JFXTolov2> bankToloviRoyxatiJFXTolov2 = FXCollections.observableArrayList();
    ObservableList<JFXTolov2> qaytimRoyxatiJFXTolov2 = FXCollections.observableArrayList();
    ObservableList<JFXTolov2> qoshimchaDaromadRoyxatiJFXTolov2 = FXCollections.observableArrayList();
    ObservableList<JFXTolov2> chegirmaRoyxatiJFXTolov2 = FXCollections.observableArrayList();

    ObservableList<HisobKitob> amallarRoyxati = FXCollections.observableArrayList();
    ObservableList<HisobKitob> naqdTolovRoyxati = FXCollections.observableArrayList();
    ObservableList<HisobKitob> bankToloviRoyxati = FXCollections.observableArrayList();
    ObservableList<HisobKitob> qaytimRoyxati = FXCollections.observableArrayList();
    ObservableList<HisobKitob> chegirmaRoyxati = FXCollections.observableArrayList();
    ObservableList<HisobKitob> qoshimchaDaromadRoyxati = FXCollections.observableArrayList();
    ObservableList<HisobKitob> oldiBerdiRoyxati = FXCollections.observableArrayList();

    Double jamiMablag = 0d;
    Double kelishmaDouble = 0d;
    Hisob hisob1;
    Hisob hisob2;
    Valuta joriyValuta;
    Valuta valuta;
    Double joriyValutaKursi = 0d;
    Double balansDouble;
    Text qoldiqLabel;
    Integer narhTuri;
    Kassa kassa;

    Boolean sotildi = false;


    public static void main(String[] args) {
        launch(args);
    }

    public Tolovlar5() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
        HisobKitob hisobKitob = new HisobKitob(1, 0, 0, 4, 9, 28, 1, 13, 1d, "195553212571", 3d, 1600d, 0, "Asus ROG Strix G513Q R9-5900HX/16/1000/RTX3060 6G/15.6\" FHD IPS", user.getId(), null);
        HisobKitob hisobKitob1 = new HisobKitob(1, 0, 0, 4, 9, 28, 1, 284, 1d, "195553269476", 1d, 437d, 0, "Asus R565E i3-1115G4/4/128/15.6\" FHD/Slate Gray", user.getId(), null);
        xaridRoyxati.add(hisobKitob);
        xaridRoyxati.add(hisobKitob1);
        joriyValuta = GetDbData.getValuta(1);
        valuta = GetDbData.getValuta(1);
        HisobModels hisobModels = new HisobModels();
        hisob1 = hisobModels.getHisob(connection, hisobKitob.getHisob1());
        user.setTovarHisobi(hisob1.getId());
        hisob2 = hisobModels.getHisob(connection, hisobKitob.getHisob2());
        user.setTovarHisobi(hisob1.getId());
        user.setXaridorHisobi(hisob2.getId());
        user.setValuta(hisobKitob.getValuta());
        user.setSavdoTuri(1);
        kassa = GetDbData.getKassa("6D9S9aZAVhEtxHAokw8LRg==");
        kassa.setSavdoTuri(1);
//        kassa.setPulHisobi(hisobModels.pulHisobi(connection, user, hisob1).getId());
        user.setPulHisobi(kassa.getPulHisobi());
    }

    public Tolovlar5(Connection connection, User user, Hisob hisob1, Hisob hisob2, ObservableList<HisobKitob> xaridRoyxati, TextArea textArea) {
        this.connection = connection;
        this.user = user;
        this.hisob1 = hisob1;
        this.hisob2 = hisob2;
        this.textArea = textArea;
        this.xaridRoyxati = xaridRoyxati;
    }

    private void ibtido() {
        initData();
        initCenterPane();
        initRightPane();
        borderPane = borderPane();
    }

    private void initData() {
        String serialNumber = ConnectionType.getAloqa().getText().trim();
        kassa = Sotuvchi5.getKassaData(connection, serialNumber);
        KursModels kursModels = new KursModels();
        valuta = GetDbData.getValuta(1);
        joriyValuta = GetDbData.getValuta(1);

        Kurs kurs = kursModels.getKurs(connection, valuta.getId(), new Date(), "sana desc");
        joriyValutaKursi = 1d;
        jamiMablag = 0d;
        for (HisobKitob hisobKitob: xaridRoyxati) {
            if (hisobKitob.getAmal().equals(2))
                jamiMablag += hisobKitob.getSummaCol();
            else
                jamiMablag -= hisobKitob.getSummaCol();
        }
        kelishmaDouble = jamiMablag;
        balansDouble = -kelishmaDouble;

        oldiBerdiRoyxati = yangiMalumotlar();
        naqdTolovRoyxatiJFXTolov2 = yangiRoyxat(naqdTolovRoyxati, "tolov");
        bankToloviRoyxatiJFXTolov2 = yangiRoyxat(bankToloviRoyxati, "plastic");
        qaytimRoyxatiJFXTolov2 = yangiRoyxat(qaytimRoyxati, "qaytim");
        chegirmaRoyxatiJFXTolov2 = yangiRoyxat(chegirmaRoyxati, "chegirma");
        qoshimchaDaromadRoyxatiJFXTolov2 = yangiRoyxat(qoshimchaDaromadRoyxati, "qo`shimcha daromad");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ibtido();
        initStage(primaryStage);
        stage.show();
    }

    public QaydnomaData display() {
        stage = new Stage();
        ibtido();
        initStage(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        stage.setOnCloseRequest(event -> {
            if (!sotildi)
                qaydnomaData = null;
        });
        return qaydnomaData;
    }

    private void initCenterPane() {
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        VBox.setVgrow(centerPane, Priority.ALWAYS);
        centerPane.setPadding(new Insets(padding));
        centerPane.setSpacing(5);
        gridPaneObservableList = yangiJadvallar();
        if (gridPaneObservableList.size()>0) {
            bottomHBox = bottomHBox();
            bottomHBox.setAlignment(Pos.CENTER);
            HBox.setHgrow(bottomHBox, Priority.ALWAYS);
            Pane pane = new Pane();
            SetHVGrow.VerticalHorizontal(pane);
            GridPane qoldiqLawhasi = yangiQoldiqLawhasi();
            GridPane gridPane = gridPaneObservableList.get(0);
            centerPane.getChildren().addAll(gridPane, pane, qoldiqLawhasi, bottomHBox);
        }
    }

    private void initRightPane() {
        ongLawha();
    }

    private GridPane yangiQoldiqLawhasi() {
        GridPane gridPane = new GridPane();
        gridPane.setVgap(5);
        gridPane.setHgap(5);
        gridPane.setPadding(new Insets(padding));
        Label label = new Label("Valuta");
        Label label1 = new Label("Balans");
        Integer rowIndex = 0;
        gridPane.add(label, 0, rowIndex, 1, 1);
        GridPane.setHgrow(label, Priority.ALWAYS);
        GridPane.setHalignment(label, HPos.LEFT);
        gridPane.add(label1, 1, rowIndex, 1, 1);
        GridPane.setHgrow(label1, Priority.ALWAYS);
        GridPane.setHalignment(label1, HPos.RIGHT);
        HBox.setHgrow(gridPane, Priority.ALWAYS);
        radioGroup = new ToggleGroup();
        valutaObservableList = GetDbData.getValutaObservableList();
        valutaObservableList.removeIf(valuta -> valuta.getStatus()>2);
        for (Valuta valuta: valutaObservableList) {
            rowIndex++;
            RadioButton radioButton = new RadioButton(valuta.getValuta());
            radioButton.setToggleGroup(radioGroup);
            radioButton.setId(valuta.getId().toString());
            gridPane.add(radioButton, 0, rowIndex, 1, 1);
            GridPane.setHalignment(radioButton, HPos.LEFT);
            if (valuta.getId().equals(joriyValuta.getId())) {
                radioGroup.selectToggle(radioButton);
            }
        }
        radioGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                RadioButton oldRadioButton = (RadioButton) oldValue;
                System.out.println(oldRadioButton.getId());
                RadioButton newRadioButton = (RadioButton) newValue;
                System.out.println(newValue);
                KursModels kursModels = new KursModels();
                Kurs eskiKurs = kursModels.getKurs(connection, Integer.valueOf(oldRadioButton.getId()), new Date(), "sana desc");
                Kurs yangiKurs = kursModels.getKurs(connection, Integer.valueOf(newRadioButton.getId()), new Date(), "sana desc");
                Double eskiKursDouble = eskiKurs.getKurs();
                Double yangiKursDouble = yangiKurs.getKurs();
                balansDouble = balansDouble * yangiKursDouble / eskiKursDouble;
                qoldiqLabel.setText(decimalFormat.format(balansDouble));
            }
        });
        qoldiqLabel = new Text();
        qoldiqLabel.setFont(font1);
        gridPane.add(qoldiqLabel, 1, 1, 1, rowIndex);
        GridPane.setHalignment(qoldiqLabel, HPos.RIGHT);
        oldiBerdiniYangila();
        return gridPane;
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("To`lovlar");
        scene = new Scene(borderPane, 900, 400);
        scene.setUserAgentStylesheet("sample/Styles/caspian.css");
//        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
    }

    private void yoz(ObservableList<JFXTolov2> observableList, Valuta valuta, Double narh) {
        for (JFXTolov2 t2: observableList) {
            if (t2.getValuta().getId().equals(valuta.getId())) {
                HisobKitob hisobKitob = t2.getHisobKitob();
                hisobKitob.setNarh(narh);
                JFXTextField textField = t2.getJfxTextFieldButton().getTextField();
                if (narh.equals(0d)) {
                    textField.setText("");
                } else {
                    textField.setText(decimalFormat.format(narh));
                }
                break;
            }
        }
    }

    private void yoz(JFXTextField jfxTextField, HisobKitob hisobKitob, Double narh) {
        hisobKitob.setNarh(narh);
        jfxTextField.setText(decimalFormat.format(narh));
    }

    private void jfxRoyxatniOchir(ObservableList<JFXTolov2> observableList) {
        observableList.forEach(tolov2->{
            HisobKitob hisobKitob = tolov2.getHisobKitob();
            hisobKitob.setNarh(0d);
            JFXTextFieldButton jfxTextFieldButton = tolov2.getJfxTextFieldButton();
            JFXTextField textField = jfxTextFieldButton.getTextField();
            textField.setText("");
        });
    }
    private void hammasiniOchir() {
        jfxRoyxatniOchir(naqdTolovRoyxatiJFXTolov2);
        jfxRoyxatniOchir(bankToloviRoyxatiJFXTolov2);
        jfxRoyxatniOchir(qaytimRoyxatiJFXTolov2);
        jfxRoyxatniOchir(qoshimchaDaromadRoyxatiJFXTolov2);
        jfxRoyxatniOchir(chegirmaRoyxatiJFXTolov2);
        amallarRoyxati.forEach(hisobKitob -> {
            hisobKitob.setNarh(0d);
        });
    }

    private ObservableList<GridPane> yangiJadvallar() {
        ObservableList<GridPane> gridPaneObservableList = FXCollections.observableArrayList();
        gridPaneObservableList.add(kelishma());
        gridPaneObservableList.add(naqdTolov());
        gridPaneObservableList.add(bankTolovi());
        gridPaneObservableList.add(qaytim());
        gridPaneObservableList.add(chegirma());
        gridPaneObservableList.add(qoshimcha());
        return gridPaneObservableList;
    }

    public GridPane kelishma() {
        GridPane gridPane = new GridPane();
        HBox.setHgrow(gridPane, Priority.ALWAYS);
        gridPane.setPadding(new Insets(padding));
        gridPane.setHgap(2);
        gridPane.setVgap(5);
        Label label1 = new Label("Xarid narhi");
        Label label2 = new Label(decimalFormat.format(jamiMablag));
        Label label3 = new Label("Kelishilgan \nnarh");
        label1.setFont(font);
        label2.setFont(font);
        label3.setFont(font);
        JFXTextField textField = new JFXTextField(decimalFormat.format(jamiMablag));
        textField.setFont(font);
        textField.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(textField, Priority.ALWAYS);

        Integer rowIndex = 0;
        gridPane.add(label1, 0, rowIndex, 1, 1);
        gridPane.add(label2, 1, rowIndex, 1, 1);
        GridPane.setHalignment(label2, HPos.RIGHT);
        GridPane.setHgrow(label3, Priority.ALWAYS);
        GridPane.setHgrow(textField, Priority.ALWAYS);

        rowIndex++;
        gridPane.add(label3, 0, rowIndex, 1, 1);
        gridPane.add(textField, 1, rowIndex, 1, 1);
        GridPane.setHalignment(textField, HPos.RIGHT);

        rowIndex++;
        Label label4 = new Label("Chegirma");
        Label label5 = new Label("");
        label4.setFont(font);
        label5.setFont(font);
        gridPane.add(label4, 0, rowIndex, 1, 1);
        gridPane.add(label5, 1, rowIndex, 1, 1);
        GridPane.setHalignment(label5, HPos.RIGHT);

        rowIndex++;
        Label label6 = new Label("Qo`shimcha \ndaromad");
        Label label7 = new Label("");
        label6.setFont(font);
        label7.setFont(font);
        gridPane.add(label6, 0, rowIndex, 1, 1);
        gridPane.add(label7, 1, rowIndex, 1, 1);
        GridPane.setHalignment(label7, HPos.RIGHT);
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (StringNumberUtils.isNumeric(newValue)) {
                    kelishmaDouble = StringNumberUtils.textToDouble(newValue);
                    Double mablag = jamiMablag - kelishmaDouble;
                    hammasiniOchir();
                    label5.setText("");
                    label7.setText("");
                    if (mablag < 0) {
                        label5.setText("");
                        label7.setText(decimalFormat.format(-mablag));
                        yoz(qoshimchaDaromadRoyxatiJFXTolov2, joriyValuta, -mablag);
                    } else if (mablag > 0) {
                        label7.setText("");
                        label5.setText(decimalFormat.format(mablag));
                        yoz(chegirmaRoyxatiJFXTolov2, joriyValuta, mablag);
                    }
                    oldiBerdiniYangila();
                    amallarRoyxati.get(0).setNarh(jamiMablag);
                    balansHisobi(amallarRoyxati);
                    tableView.refresh();
                }
            }
        });
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (!newValue) {
                    textField.setText(decimalFormat.format(kelishmaDouble));
                } else {
                    textField.selectAll();
                }
            }
        });
        return gridPane;
    }

    public Double oldiBerdiniYangila() {
        asosiyRadioButton();
        Double a0 = kelishmaDouble / joriyValutaKursi;
        Double a1 = jamiMablag / joriyValutaKursi;
        Double a2 = jami(chegirmaRoyxati);
        Double a3 = jami(qoshimchaDaromadRoyxati);
        Double a4 = jami(naqdTolovRoyxati);
        Double a5 = jami(bankToloviRoyxati);
        Double a6 = jami(qaytimRoyxati);
        Double oldiBerdiDouble = a1 - a2 + a3 - (a4 + a5 - a6) ;
        Double yaxlit = StringNumberUtils.yaxlitla(oldiBerdiDouble, -2);

        if (yaxlit > 0.00) {
            qoldiqLabel.setFill(Color.BLUE);
            qoldiqLabel.setText(decimalFormat.format(yaxlit));
        } else if (yaxlit < 0.00) {
            qoldiqLabel.setFill(Color.RED);
            qoldiqLabel.setText(decimalFormat.format(-yaxlit));
        } else if (yaxlit == 0.00) {
            qoldiqLabel.setFill(Color.BLACK);
            qoldiqLabel.setText("");
            qoldiqLabel.setText(decimalFormat.format(yaxlit));
        }

        balansDouble = oldiBerdiDouble;
        return oldiBerdiDouble;
    }

    private void asosiyRadioButton() {
        RadioButton radioButton = (RadioButton) radioGroup.getToggles().get(0);
        radioGroup.selectToggle(radioButton);
    }

    public GridPane naqdTolov() {
        GridPane gridPane = new GridPane();
        HBox.setHgrow(gridPane, Priority.ALWAYS);
        gridPane.setPadding(new Insets(padding));
        gridPane.setHgap(2);
        gridPane.setVgap(15);
        Integer rowIndex = 0;
        for (JFXTolov2 tolov2: naqdTolovRoyxatiJFXTolov2) {
            rowIndex++;
            HisobKitob hisobKitob = tolov2.getHisobKitob();
            JFXTextFieldButton jfxTextFieldButton = tolov2.getJfxTextFieldButton();
            JFXTextField textField = jfxTextFieldButton.getTextField();
            Valuta valuta = tolov2.getValuta();
            textField.setLabelFloat(true);
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    if (StringNumberUtils.isNumeric(newValue)) {
                        Double narh = StringNumberUtils.textToDouble(newValue);
                        jfxRoyxatniOchir(qaytimRoyxatiJFXTolov2);
                        hisobKitob.setNarh(narh);
                        oldiBerdiniYangila();
                        Double balance = jami(naqdTolovRoyxati, hisobKitob) + narh / hisobKitob.getKurs();
                        amallarRoyxati.get(1).setNarh(balance);
                        balansHisobi(amallarRoyxati);
                        tableView.refresh();

                    }
                }
            });
            gridPane.add(jfxTextFieldButton, 0, rowIndex, 1, 1);
            GridPane.setHgrow(jfxTextFieldButton, Priority.ALWAYS);
            JFXButton button = jfxTextFieldButton.getPlusButton();
            button.setOnAction(event -> {
                Double narh = narhHisobi(hisobKitob) * hisobKitob.getKurs();
                jfxRoyxatniOchir(qaytimRoyxatiJFXTolov2);
                yoz(textField, hisobKitob, narh);
                oldiBerdiniYangila();
                Double balance = jami(naqdTolovRoyxati);
                amallarRoyxati.get(1).setNarh(balance);
                balansHisobi(amallarRoyxati);
                tableView.refresh();
            });
        }
        return gridPane;
    }
    public GridPane bankTolovi() {
        GridPane gridPane = new GridPane();
        HBox.setHgrow(gridPane, Priority.ALWAYS);
        gridPane.setPadding(new Insets(padding));
        gridPane.setHgap(2);
        gridPane.setVgap(15);
        Integer rowIndex = 0;
        for (JFXTolov2 tolov2: bankToloviRoyxatiJFXTolov2) {
            rowIndex++;
            HisobKitob hisobKitob = tolov2.getHisobKitob();
            JFXTextFieldButton jfxTextFieldButton = tolov2.getJfxTextFieldButton();
            JFXTextField textField = jfxTextFieldButton.getTextField();
            textField.setLabelFloat(true);
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    if (StringNumberUtils.isNumeric(newValue)) {
                        Double narh = StringNumberUtils.textToDouble(newValue);
                        hisobKitob.setNarh(narh);
                        oldiBerdiniYangila();
                        Double balance = jami(bankToloviRoyxati, hisobKitob) + narh / hisobKitob.getKurs();
                        amallarRoyxati.get(2).setNarh(balance);
                        balansHisobi(amallarRoyxati);
                        tableView.refresh();
                    }
                }
            });
            gridPane.add(jfxTextFieldButton, 0, rowIndex, 1, 1);
            GridPane.setHgrow(jfxTextFieldButton, Priority.ALWAYS);
            JFXButton button = jfxTextFieldButton.getPlusButton();
            button.setOnAction(event -> {
                Double narh = narhHisobi(hisobKitob) * hisobKitob.getKurs();
                yoz(textField, hisobKitob, narh);
                oldiBerdiniYangila();
                Double balance = jami(bankToloviRoyxati);
                amallarRoyxati.get(2).setNarh(balance);
                balansHisobi(amallarRoyxati);
                tableView.refresh();
            });
        }
        return gridPane;
    }
    public GridPane qaytim() {
        GridPane gridPane = new GridPane();
        HBox.setHgrow(gridPane, Priority.ALWAYS);
        gridPane.setPadding(new Insets(padding));
        gridPane.setHgap(2);
        gridPane.setVgap(15);
        Integer rowIndex = 0;
        for (JFXTolov2 tolov2: qaytimRoyxatiJFXTolov2) {
            rowIndex++;
            HisobKitob hisobKitob = tolov2.getHisobKitob();
            JFXTextFieldButton jfxTextFieldButton = tolov2.getJfxTextFieldButton();
            Valuta valuta = tolov2.getValuta();
            JFXTextField textField = jfxTextFieldButton.getTextField();
            textField.setLabelFloat(true);
            gridPane.add(jfxTextFieldButton, 0, rowIndex, 1, 1);
            GridPane.setHgrow(jfxTextFieldButton, Priority.ALWAYS);
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    Double narh = StringNumberUtils.textToDouble(newValue);
                    hisobKitob.setNarh(narh);
                    oldiBerdiniYangila();
                    Double balance = jami(qaytimRoyxati, hisobKitob) + narh / hisobKitob.getKurs();
                    amallarRoyxati.get(3).setNarh(balance);
                    balansHisobi(amallarRoyxati);
                    tableView.refresh();
                }
            });

            JFXButton button = jfxTextFieldButton.getPlusButton();
            button.setOnAction(event -> {
                Double narh = narhHisobi(hisobKitob) * hisobKitob.getKurs();
                yoz(textField, hisobKitob, narh);
                oldiBerdiniYangila();
                Double balance = jami(qaytimRoyxati);
                amallarRoyxati.get(3).setNarh(balance);
                balansHisobi(amallarRoyxati);
                tableView.refresh();
            });

        }
        return gridPane;
    }
    public GridPane qoshimcha() {
        GridPane gridPane = new GridPane();
        HBox.setHgrow(gridPane, Priority.ALWAYS);
        gridPane.setPadding(new Insets(padding));
        gridPane.setHgap(2);
        gridPane.setVgap(15);
        Integer rowIndex = 0;
        for (JFXTolov2 tolov2: qoshimchaDaromadRoyxatiJFXTolov2) {
            rowIndex++;
            HisobKitob hisobKitob = tolov2.getHisobKitob();
            JFXTextFieldButton jfxTextFieldButton = tolov2.getJfxTextFieldButton();
            JFXTextField textField = jfxTextFieldButton.getTextField();
            textField.setLabelFloat(true);
            gridPane.add(jfxTextFieldButton, 0, rowIndex, 1, 1);
            GridPane.setHgrow(jfxTextFieldButton, Priority.ALWAYS);
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    Double narh = StringNumberUtils.textToDouble(newValue);
                    hisobKitob.setNarh(narh);
                    oldiBerdiniYangila();
                    Double balance = jami(qoshimchaDaromadRoyxati, hisobKitob) + narh / hisobKitob.getKurs();
                    amallarRoyxati.get(5).setNarh(balance);
                    balansHisobi(amallarRoyxati);
                    tableView.refresh();
                }
            });

            JFXButton button = jfxTextFieldButton.getPlusButton();
            button.setOnAction(event -> {
                Double narh = narhHisobi(hisobKitob) * hisobKitob.getKurs();
                yoz(textField, hisobKitob, narh);
                oldiBerdiniYangila();
                Double balance = jami(qoshimchaDaromadRoyxati, hisobKitob) + narh / hisobKitob.getKurs();
                amallarRoyxati.get(5).setNarh(balance);
                balansHisobi(amallarRoyxati);
                tableView.refresh();
            });
        }
        return gridPane;
    }
    public GridPane chegirma() {
        GridPane gridPane = new GridPane();
        HBox.setHgrow(gridPane, Priority.ALWAYS);
        gridPane.setPadding(new Insets(padding));
        gridPane.setHgap(2);
        gridPane.setVgap(15);
        Integer rowIndex = 0;
        for (JFXTolov2 tolov2: chegirmaRoyxatiJFXTolov2) {
            rowIndex++;
            HisobKitob hisobKitob = tolov2.getHisobKitob();
            Valuta valuta = tolov2.getValuta();
            JFXTextFieldButton jfxTextFieldButton = tolov2.getJfxTextFieldButton();
            JFXTextField textField = jfxTextFieldButton.getTextField();
            textField.setLabelFloat(true);
            gridPane.add(jfxTextFieldButton, 0, rowIndex, 1, 1);
            GridPane.setHgrow(jfxTextFieldButton, Priority.ALWAYS);
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    Double narh = StringNumberUtils.textToDouble(newValue);
                    hisobKitob.setNarh(narh);
                    oldiBerdiniYangila();
                    Double balance = jami(chegirmaRoyxati, hisobKitob) + narh / hisobKitob.getKurs();
                    amallarRoyxati.get(4).setNarh(balance);
                    balansHisobi(amallarRoyxati);
                    tableView.refresh();
                }
            });

            JFXButton button = jfxTextFieldButton.getPlusButton();
            button.setOnAction(event -> {
                Double narh = narhHisobi(hisobKitob) * hisobKitob.getKurs();
                yoz(textField, hisobKitob, narh);
                balansHisobi(oldiBerdiRoyxati);
                Double balance = jami(chegirmaRoyxati);
                amallarRoyxati.get(4).setNarh(balance);
                balansHisobi(amallarRoyxati);
                tableView.refresh();
            });
        }
        return gridPane;
    }

    private HBox bottomHBox() {
        HBox hBox = new HBox();
        HBox.setHgrow(hBox, Priority.ALWAYS);
        JFXButton orqagaButton = orqagaButton();
        JFXButton oldingaButton = oldingaButton();
        JFXButton yakunlaButton = yakunlaButton();
        hBox.getChildren().addAll(orqagaButton, yakunlaButton, oldingaButton);
        return hBox;
    }

    private JFXButton yakunlaButton() {
        JFXButton button = new JFXButton("Yakunla");
        button.setMaxWidth(2000);
        button.setPrefWidth(8);
        ImageView imageView = new PathToImageView("/sample/images/Icons/floppy.png", 24, 24).getImageView();
        button.setGraphic(imageView);
        HBox.setHgrow(button, Priority.ALWAYS);
        button.setOnAction(event -> {
            if (xaridniYakunla()) {
                xaridRoyxati.removeAll(xaridRoyxati);
                stage.close();
            }

        });
        return button;
    }

    private Boolean xaridniYakunla() {
        sotildi = false;
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        if (xaridRoyxati.size() == 0) {
            Alerts.AlertString("Sotiladigan xech vaqo yo'q");
            return false;
        }
        Double oldiBerdiDouble = oldiBerdiniYangila();
        Double oldiBerdiYaxlit = StringNumberUtils.yaxlitla(oldiBerdiDouble, -2);

        if (oldiBerdiYaxlit < 0.00) {
            // ortiqcha tolov
            Alerts.AlertString("Ortiqcha pul to`landi");
            return false;
        }

        if (oldiBerdiYaxlit > 0.00) {
            // nasiya
            if (!Alerts.haYoq("To`lov to`liq emas", "Nasiyaga sotamizmi ???")) {
                return false;
            }
        }

        qaydnomaData = yangiQaydnoma();
        HisobKitob xossa = oldiBerdiRoyxati.get(0);
        xossa.setAmal(2);
        xossa.setHisob1(hisob1.getId());
        xossa.setHisob2(hisob2.getId());
        xossa.setNarh(0d);
        xossa.setQaydId(qaydnomaData.getId());
        xossa.setHujjatId(qaydnomaData.getHujjat());
        xossa.setIzoh(qaydnomaData.getIzoh());
        xossa.setUserId(user.getId());
        xossa.setDateTime(qaydnomaData.getSana());

        hisobKitobModels.insert(connection, xossa);

        xaridRoyxati.forEach(hisobKitob -> {
            hisobKitob.setHisob1(hisob1.getId());
            hisobKitob.setHisob2(hisob2.getId());
            hisobKitob.setQaydId(qaydnomaData.getId());
            hisobKitob.setHujjatId(qaydnomaData.getHujjat());
            hisobKitob.setUserId(user.getId());
            hisobKitob.setDateTime(qaydnomaData.getSana());
/*
            Standart tovar = GetDbData.getTovar(hisobKitob.getTovar());
            hisobKitob.setIzoh(tovar.getText());
*/
        });
        hisobKitobModels.addBatch(connection, xaridRoyxati);

        ObservableList<HisobKitob> oldiBerdiRoyxati = FXCollections.observableArrayList();
        naqdTolovRoyxati.forEach(hisobKitob -> {
            if (!hisobKitob.getNarh().equals(0d)) {
                oldiBerdiRoyxati.add(hisobKitob);
            }
        });
        bankToloviRoyxati.forEach(hisobKitob -> {
            if (!hisobKitob.getNarh().equals(0d)) {
                oldiBerdiRoyxati.add(hisobKitob);
            }
        });
        qaytimRoyxati.forEach(hisobKitob -> {
            if (!hisobKitob.getNarh().equals(0d)) {
                oldiBerdiRoyxati.add(hisobKitob);
            }
        });
        chegirmaRoyxati.forEach(hisobKitob -> {
            if (!hisobKitob.getNarh().equals(0d)) {
                oldiBerdiRoyxati.add(hisobKitob);
            }
        });
        qoshimchaDaromadRoyxati.forEach(hisobKitob -> {
            if (!hisobKitob.getNarh().equals(0d)) {
                oldiBerdiRoyxati.add(hisobKitob);
            }
        });
        oldiBerdiRoyxati.forEach(hisobKitob -> {
            hisobKitob.setQaydId(qaydnomaData.getId());
            hisobKitob.setHujjatId(qaydnomaData.getHujjat());
            hisobKitob.setUserId(user.getId());
            hisobKitob.setDateTime(qaydnomaData.getSana());
        });
        if (oldiBerdiDouble > 0.00) {
            // Tolov to'liq
            HisobKitob hisobKitob = balansniZarargaUr(oldiBerdiDouble);
            if (hisobKitob != null)
                oldiBerdiRoyxati.add(hisobKitob);
        }

        if (oldiBerdiDouble < 0.00) {
            // ortiqcha to'lov
            HisobKitob hisobKitob = balansniFoydagaUr(-oldiBerdiDouble);
            if (hisobKitob != null)
                oldiBerdiRoyxati.add(hisobKitob);
        }
        if (oldiBerdiRoyxati.size()>0) {
            hisobKitobModels.addBatch(connection, oldiBerdiRoyxati);
        }
        ObservableList<HisobKitob> hisobKitobObservableList = hisobKitobModels.getAnyData(connection, "qaydId = " + qaydnomaData.getId(), "amal asc");
        SavdoChiptasiniChopEtish xaridChiptasi = new SavdoChiptasiniChopEtish(user, qaydnomaData, hisobKitobObservableList);
        String printerNomi = xaridChiptasi.printerim().toLowerCase();
        if (printerNomi.contains("POS-58".toLowerCase())) {
            xaridChiptasi.tolovChiptasiniBerPos58(printerNomi);
        } else if (printerNomi.contains("XP-80C".toLowerCase())) {
            xaridChiptasi.tolovChiptasiniBerXP80(printerNomi);
        }
        sotildi = true;
        return sotildi;
    }

    private HisobKitob balansniZarargaUr(Double oldiBerdiDouble) {
        HisobModels hisobModels = new HisobModels();
        ValutaModels valutaModels = new ValutaModels();
        Valuta valuta = valutaModels.getValuta(connection, 1);
        Hisob foydaHisobi = hisobModels.zararHisobi(connection, hisob1);
        Hisob zararHisobi = hisobModels.zararHisobi(connection, hisob2);
        HisobKitob hisobKitob = xaridRoyxati.get(0);
        hisobKitob = null;
        String izohText = "";
        if (StringNumberUtils.yaxlitla(balansDouble, -2) == 0d) {
            zararHisobi = hisobModels.zararHisobi(connection, hisob1);
            izohText = "Yaxlitlash tafovuti: " + valuta.getValuta() +  "\n Xarid ??? " + qaydnomaData.getHujjat().toString().trim();
            double kurs = 1d;
            //97 400 63 32
            hisobKitob = new HisobKitob(
                    null,
                    qaydnomaData.getId(),
                    qaydnomaData.getHujjat(),
                    17,
                    foydaHisobi.getId(),
                    zararHisobi.getId(),
                    valuta.getId(),
                    0,
                    kurs,
                    "",
                    .0,
                    oldiBerdiDouble,
                    0,
                    izohText,
                    user.getId(),
                    qaydnomaData.getSana()
            );
        }
        return hisobKitob;
    }

    private HisobKitob balansniFoydagaUr(Double oldiBerdiDouble) {
        HisobModels hisobModels = new HisobModels();
        ValutaModels valutaModels = new ValutaModels();
        Valuta valuta = valutaModels.getValuta(connection, 1);
        HisobKitob hisobKitob = null;
        Hisob foydaHisobi = hisobModels.foydaHisobi(connection, hisob1);
        Hisob zararHisobi = hisobModels.foydaHisobi(connection, hisob2);
        String izohText = "";
        if (StringNumberUtils.yaxlitla(balansDouble, -2) == 0d) {
            foydaHisobi = hisobModels.foydaHisobi(connection, hisob1);
            izohText = "Yaxlitlash tafovuti: " + valuta.getValuta() +  "\n Xarid ??? " + qaydnomaData.getHujjat().toString().trim();
            double kurs = 1d;
            //97 400 63 32
            hisobKitob = new HisobKitob(
                    null,
                    qaydnomaData.getId(),
                    qaydnomaData.getHujjat(),
                    17,
                    foydaHisobi.getId(),
                    zararHisobi.getId(),
                    valuta.getId(),
                    0,
                    kurs,
                    "",
                    .0,
                    oldiBerdiDouble,
                    0,
                    izohText,
                    user.getId(),
                    qaydnomaData.getSana()
            );
        }
        return hisobKitob;
    }

    private QaydnomaData yangiQaydnoma() {
        QaydnomaModel qaydnomaModel = new QaydnomaModel();
        Date joriySana = new Date();
        Integer amal = 2;
        if (textArea == null)
            textArea = new TextArea("");
        QaydnomaData qaydnomaData = new QaydnomaData(
                null,
                amal,
                0,
                joriySana,
                hisob1.getId(),
                hisob1.getText(),
                hisob2.getId(),
                hisob2.getText(),
                textArea.getText(),
                0.00,
                0,
                user.getId(),
                null
        );
        qaydnomaModel.insert_data(connection, qaydnomaData);
        return qaydnomaData;
    }

    private JFXButton oldingaButton() {
        JFXButton button = new JFXButton(">>");
        button.setMaxWidth(2000);
        button.setPrefWidth(8);
        HBox.setHgrow(button, Priority.ALWAYS);
        button.setOnAction(event -> {
            if (joriyJadval<gridPaneObservableList.size()) {
                joriyJadval++;
            } else {
                joriyJadval = 1;
            }
            tableView.getSelectionModel().select(joriyJadval - 1);
        });
        return button;
    }

    private JFXButton orqagaButton() {
        JFXButton button = new JFXButton("<<");
        button.setMaxWidth(2000);
        button.setPrefWidth(8);
        HBox.setHgrow(button, Priority.ALWAYS);
        button.setOnAction(event -> {
            if (joriyJadval>1) {
                joriyJadval--;
            } else {
                joriyJadval = gridPaneObservableList.size();
            }
            tableView.getSelectionModel().select(joriyJadval - 1);
        });
        return button;
    }

    public Integer getNarhTuri() {
        return narhTuri;
    }

    public void setNarhTuri(Integer narhTuri) {
        this.narhTuri = narhTuri;
    }

    private ObservableList<JFXTolov2> yangiRoyxat(ObservableList<HisobKitob> hisobKitobObservableList, String string) {
        ObservableList<JFXTolov2> observableList = FXCollections.observableArrayList();
        for (HisobKitob hisobKitob: hisobKitobObservableList) {
            Valuta valuta = GetDbData.getValuta(hisobKitob.getValuta());
            Valuta valuta1 = new Valuta(valuta.getId(), valuta.getValuta() + " " + string, valuta.getStatus(), valuta.getUserId(), valuta.getDateTime());
            JFXTolov2 tolov = new JFXTolov2(valuta1, hisobKitob);
            observableList.add(tolov);
        }
        return observableList;
    }

    private BorderPane borderPane() {
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(centerPane);
        borderPane.setRight(ongLawha());
        return borderPane;
    }

    private VBox ongLawha() {
        VBox vBox = new VBox();
        tableView = new TableView<>();
        SetHVGrow.VerticalHorizontal(tableView);
        TableViewAndoza tableViewAndoza = new TableViewAndoza();
        TableColumn<HisobKitob, DoubleTextBox> hisob1Hisob2 = tableViewAndoza.hisob1Hisob2();
        hisob1Hisob2.setMinWidth(200);

        tableView.getColumns().addAll(
                tableViewAndoza.getIzoh2Column(),
                hisob1Hisob2,
                tableViewAndoza.getNarhColumn(),
                tableViewAndoza.getBalans2Column()
        );
        tableView.setItems(amallarRoyxati);
        balansHisobi(amallarRoyxati);
        tableView.getSelectionModel().selectFirst();
        vBox.getChildren().addAll(tableView);
        tableView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                joriyJadval = newValue.intValue() +1;
                centerPane.getChildren().remove(0);
                centerPane.getChildren().add(0, gridPaneObservableList.get(joriyJadval-1));
            }
        });
        return vBox;
    }

    private ObservableList<HisobKitob> pulRoyxati(Integer amal, Hisob hisob1, Hisob hisob2) {
        ObservableList<HisobKitob> observableList = FXCollections.observableArrayList();
        ValutaModels valutaModels = new ValutaModels();
        ObservableList<Valuta> valutaObservableList = valutaModels.getAnyData(connection, "status<3", "");
        for (Valuta v: valutaObservableList) {
            HisobKitob hisobKitob = new HisobKitob();
            hisobKitob.setHisob1(hisob1.getId());
            hisobKitob.setHisob2(hisob2.getId());
            hisobKitob.setAmal(amal);
            hisobKitob.setValuta(v.getId());
            Double kursDouble = 1d;
            Kurs kurs = getKurs(v.getId(), new Date());
            if (kurs != null) {
                kursDouble = kurs.getKurs();
            }
            hisobKitob.setKurs(kursDouble);
            observableList.add(hisobKitob);
        }
        return observableList;
    }
    private HisobKitob yangiSavdoXususiyatlari(Kassa kassa) {
        HisobKitob hisobKitob = new HisobKitob();
        hisobKitob.setQaydId(0);
        hisobKitob.setHujjatId(0);
        Integer savdoTuriInteger = kassa.getSavdoTuri();
        hisobKitob.setAmal(4);
        hisobKitob.setHisob1(hisob1.getId());
        hisobKitob.setHisob2(hisob2.getId());
        hisobKitob.setValuta(valuta.getId());
        hisobKitob.setTovar(0);
        hisobKitob.setKurs(getKurs(valuta.getId(), new Date()).getKurs());
        hisobKitob.setBarCode("");
        hisobKitob.setDona(0d);
        hisobKitob.setNarh(0d);
        hisobKitob.setManba(0);
        hisobKitob.setIzoh(" Savdo ???: ");
        hisobKitob.setUserId(user.getId());
        hisobKitob.setDateTime(new Date());
        return hisobKitob;
    }
    private Kurs getKurs(int valutaId, Date sana) {
        Valuta v = GetDbData.getValuta(valutaId);
        Kurs kurs = v.getStatus() == 1 ? new Kurs(1, new Date(), valutaId, 1.0, user.getId(), new Date()) : null;
        KursModels kursModels = new KursModels();
        ObservableList<Kurs> kursObservableList = null;
        kursObservableList = kursModels.getDate(connection, valutaId, sana, "sana desc");
        if (kursObservableList.size()>0) {
            kurs = kursObservableList.get(0);
        }
        return kurs;
    }
    private ObservableList<HisobKitob> yangiMalumotlar() {
        ObservableList<HisobKitob> observableList = oldiBerdiRoyxati(hisob1, hisob2);
        return observableList;
    }
    private ObservableList<HisobKitob> oldiBerdiRoyxati(Hisob hisob1, Hisob hisob2) {
        ObservableList<HisobKitob> observableList = FXCollections.observableArrayList();
        HisobKitob xossalar = yangiSavdoXususiyatlari(kassa);
        xossalar.setNarh(jamiMablag);
        observableList.add(xossalar);
        amallarRoyxati.add(xossalar);

        Hisob naqdKirimHisobi = hisobModels.pulHisobi(connection, user, hisob1);
        Hisob naqdChiqimHisobi = hisobModels.pulHisobi(connection, user, hisob2);
        naqdTolovRoyxati = pulRoyxati(7,  naqdChiqimHisobi, naqdKirimHisobi);
        observableList.addAll(naqdTolovRoyxati);
        amallarRoyxati.add(new HisobKitob(1, 0, 0, 7, naqdChiqimHisobi.getId(), naqdKirimHisobi.getId(), 1, 0, 1d, "", 0d, 0d, 0, "Naqd to'lov", user.getId(), new Date()));

        Hisob bankKirimHisobi = hisobModels.bankHisobi(connection, hisob1);
        Hisob bankChiqimHisobi = hisobModels.bankHisobi(connection, hisob2);
        bankToloviRoyxati = pulRoyxati(15, bankChiqimHisobi, bankKirimHisobi);
        observableList.addAll(bankToloviRoyxati);
        amallarRoyxati.add(new HisobKitob(1, 0, 0, 15, bankChiqimHisobi.getId(), bankKirimHisobi.getId(), 1, 0, 1d, "", 0d, 0d, 0, "Bankdan to'lov", user.getId(), new Date()));

        Hisob qaytim1 = hisobModels.pulHisobi(connection, user, hisob1);
        qaytimRoyxati = pulRoyxati(8, qaytim1, naqdKirimHisobi);
        observableList.addAll(qaytimRoyxati);
        amallarRoyxati.add(new HisobKitob(1, 0, 0, 8, qaytim1.getId(), naqdChiqimHisobi.getId(), 1, 0, 1d, "", 0d, 0d, 0, "Qaytim", user.getId(), new Date()));

        Hisob chegirmaKirimHisobi = hisobModels.chegirmaHisobi(connection, hisob1);
        Hisob chegirmaChiqimHisobi = hisobModels.chegirmaHisobi(connection, hisob2);
        chegirmaRoyxati = pulRoyxati(13, chegirmaChiqimHisobi, chegirmaKirimHisobi);
        observableList.addAll(chegirmaRoyxati);
        amallarRoyxati.add(new HisobKitob(1, 0, 0, 13, chegirmaChiqimHisobi.getId(), chegirmaKirimHisobi.getId(), 1, 0, 1d, "", 0d, 0d, 0, "Chegirma", user.getId(), new Date()));

        Hisob qoshimchaKirimDaromadi = hisobModels.qoshimchaDaromadHisobi(connection, hisob1);
        Hisob qoshimchaChiqimDaromadi = hisobModels.qoshimchaDaromadHisobi(connection, hisob2);
        qoshimchaDaromadRoyxati = pulRoyxati(18, qoshimchaChiqimDaromadi, qoshimchaKirimDaromadi);
        observableList.addAll(qoshimchaDaromadRoyxati);
        amallarRoyxati.add(new HisobKitob(1, 0, 0, 18, qoshimchaChiqimDaromadi.getId(), qoshimchaKirimDaromadi.getId(), 1, 0, 1d, "", 0d, 0d, 0, "Qo'shimcha \ndaromad", user.getId(), new Date()));

        return observableList;
    }

    private Double balansHisobi(ObservableList<HisobKitob> observableList) {
        Double balance = 0d;
        if (observableList.size()>0) {
            for (HisobKitob hisobKitob: observableList) {
                switch (hisobKitob.getAmal()) {
                    case 4:
                        balance -= hisobKitob.getSummaCol();
                        break;
                    case 7: //tolov
                        balance += hisobKitob.getSummaCol();
                        break;
                    case 8: //qaytim
                        balance -= hisobKitob.getSummaCol();
                        break;
                    case 13: // chegirma
                        balance += hisobKitob.getSummaCol();
                        break;
                    case 15: //bank tolovi
                        balance += hisobKitob.getSummaCol();
                        break;
                    case 18: //qoshimcha daromad
                        balance -= hisobKitob.getSummaCol();
                        break;
                }
                hisobKitob.setBalans(balance);
            }
        }
        return balance;
    }
    private Double narhHisobi(HisobKitob hisobKitob) {
        Double narh = 0d;
        Double a0 = kelishmaDouble / joriyValutaKursi;
        Double a1 = jamiTovar(xaridRoyxati); // jamiMablaq / joriyValutaKursi
        Double a2 = jami(chegirmaRoyxati);
        Double a3 = jami(qoshimchaDaromadRoyxati);
        Double a4 = jami(naqdTolovRoyxati);
        Double a5 = jami(bankToloviRoyxati);
        Double a6 = jami(qaytimRoyxati);
        switch (hisobKitob.getAmal()) {
            case 4:
                narh = a1;
                break;
            case 7: //Naqd tolov
                jfxRoyxatniOchir(qaytimRoyxatiJFXTolov2);
                narh = a0 - (jami(naqdTolovRoyxati, hisobKitob) + a5);
                break;
            case 8: //qaytim
                narh = a1 - a2 + a3 - (a4 + a5 - jami(qaytimRoyxati, hisobKitob));
                if (StringNumberUtils.yaxlitla(narh, -2) < 0)
                    narh = -narh;
                else
                    narh = 0d;
                break;
            case 13: // chegirma
                narh = a1 - jami(chegirmaRoyxati, hisobKitob) + a3 - (a4 + a5 - a6);
                if (StringNumberUtils.yaxlitla(narh, -2) <= 0)
                    narh = 0d;
                else
                    narh = narh;
                break;
            case 15: //bank tolovi
                jfxRoyxatniOchir(qaytimRoyxatiJFXTolov2);
                narh = a0  - (a4 + jami(bankToloviRoyxati, hisobKitob));
                break;
            case 17: //yaxlitlash tafovuti daromad
                break;
            case 18: //qoshimcha daromad
                narh = a1 - a2 + jami(qoshimchaDaromadRoyxati, hisobKitob) - (a4 + a5 - a6);
                if (StringNumberUtils.yaxlitla(narh, -2) >= 0)
                    narh = 0d;
                if (StringNumberUtils.yaxlitla(narh, -2) < 0)
                    narh = - narh;
                break;
        }
        hisobKitob. setNarh(narh * hisobKitob.getKurs());
        return narh;
    }
    private Double jamiTovar(ObservableList<HisobKitob> xaridRoyxati) {
        Double jami = 0d;
        for (HisobKitob hisobKitob: xaridRoyxati) {
            if (hisobKitob.getAmal().equals(2)) {
                jami += hisobKitob.getSummaCol();
            }
        }
        return jami;
    }

    private Double jami(ObservableList<HisobKitob> observableList) {
        Double jami = 0d;
        for (HisobKitob hisobKitob: observableList) {
            jami += hisobKitob.getSummaCol();
        }
        return jami;
    }
    private Double jami(ObservableList<HisobKitob> observableList, HisobKitob hk) {
        Double jami = 0d;
        for (HisobKitob hisobKitob: observableList) {
            if (!hisobKitob.equals(hk))
                jami += hisobKitob.getSummaCol();
        }
        return jami;
    }

    public void setKassa(Kassa kassa) {
        this.kassa = kassa;
    }
    private TableColumn<HisobKitob, DoubleTextBox> hisob1Hisob2() {
        TableColumn<HisobKitob, DoubleTextBox> hisoblar = new TableColumn<>("Chiqim/Kirim");
        hisoblar.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, DoubleTextBox>, ObservableValue<DoubleTextBox>>() {

            @Override
            public ObservableValue<DoubleTextBox> call(TableColumn.CellDataFeatures<HisobKitob, DoubleTextBox> param) {
                HisobKitob hisobKitob = param.getValue();
                Hisob hisob1= GetDbData.getHisob(hisobKitob.getHisob1());
                Hisob hisob2= GetDbData.getHisob(hisobKitob.getHisob2());
                Text text1 = new Text(hisob1.getText());
                text1.setStyle("-fx-text-alignment:justify;");
                text1.wrappingWidthProperty().bind(param.getTableColumn().widthProperty().subtract(2));
                text1.setFill(Color.GREEN);
                Text text2 = new Text(hisob2.getText());
                text2.setStyle("-fx-text-alignment:justify;");
                text2.wrappingWidthProperty().bind(param.getTableColumn().widthProperty().subtract(2));
                text2.setFill(Color.BLUE);
                DoubleTextBox b = new DoubleTextBox(text1, text2);
                b.setMaxWidth(2000);
                b.setPrefWidth(250);
                b.setMaxHeight(2000);
                b.setPrefHeight(20);
                HBox.setHgrow(text1, Priority.ALWAYS);
                VBox.setVgrow(text1, Priority.ALWAYS);
                HBox.setHgrow(text2, Priority.ALWAYS);
                VBox.setVgrow(text2, Priority.ALWAYS);
                HBox.setHgrow(b, Priority.ALWAYS);
                VBox.setVgrow(b, Priority.ALWAYS);

                return new SimpleObjectProperty<DoubleTextBox>(b);
            }
        });

        hisoblar.setMinWidth(20);
        hisoblar.setMaxWidth(250);
        hisoblar.setStyle( "-fx-alignment: CENTER;");
        return hisoblar;
    }
}
