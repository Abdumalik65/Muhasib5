package sample.Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Application;
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
import sample.Config.MySqlDBGeneral;
import sample.Data.*;
import sample.Enums.ServerType;
import sample.Model.*;
import sample.Tools.*;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

public class Tolovlar extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderPane;
    VBox centerPane = new VBox();
    HBox bottomHBox = new HBox();
    ListView<Label> listView;
    ToggleGroup radioGroup;
    Connection connection;
    User user;
    QaydnomaData qaydnomaData;
    HisobKitobAndoza hisobKitobAndoza;
    int padding = 5;
    Integer joriyJadval = 1;
    DecimalFormat decimalFormat = new MoneyShow();
    Font font = Font.font("Arial", FontWeight.BOLD,16);
    Font font1 = Font.font("Arial", FontWeight.BOLD,32);

    ObservableList<GridPane> gridPaneObservableList;
    ObservableList<Valuta> valutaObservableList;
    ObservableList<HisobKitob> xaridRoyxati = FXCollections.observableArrayList();
    ObservableList<JFXTolov2> tolovRoyxati = FXCollections.observableArrayList();
    ObservableList<JFXTolov2> bankToloviRoyxati = FXCollections.observableArrayList();
    ObservableList<JFXTolov2> qaytimRoyxati = FXCollections.observableArrayList();
    ObservableList<JFXTolov2> qoshimchaDaromadRoyxati = FXCollections.observableArrayList();
    ObservableList<JFXTolov2> chegirmaRoyxati = FXCollections.observableArrayList();

    Double jamiMablag = 0d;
    Double kelishmaDouble = 0d;
    Hisob hisob1;
    Hisob hisob2;
    Valuta joriyValuta;
    Double joriyValutaKursi = 0d;
    Double balansDouble;
    Text qoldiqLabel;
    Integer narhTuri;
    Kassa kassa;

    Boolean sotildi = false;


    public static void main(String[] args) {
        launch(args);
    }

    public Tolovlar(Connection connection, User user, Hisob hisob1, Hisob hisob2, ObservableList<HisobKitob> xaridRoyxati) {
        this.connection = connection;
        this.user = user;
        this.hisob1 = hisob1;
        this.hisob2 = hisob2;
        this.xaridRoyxati = xaridRoyxati;
        hisobKitobAndoza = new HisobKitobAndoza(connection, user, hisob1, hisob2, xaridRoyxati);
    }

    private void ibtido() {
        initData();
        initCenterPane();
        borderPane = borderPane();
    }

    private void initData() {
        String serialNumber = ConnectionType.getAloqa().getText().trim();
        kassa = Sotuvchi5.getKassaData(connection, serialNumber);
        KursModels kursModels = new KursModels();
        Kurs kurs = kursModels.getKurs(connection, joriyValuta.getId(), new Date(), "sana desc");
        joriyValutaKursi = kurs.getKurs();
        jamiMablag = 0d;
        for (HisobKitob hisobKitob: xaridRoyxati) {
            if (hisobKitob.getAmal().equals(4))
                jamiMablag += hisobKitob.getSummaCol();
            else
                jamiMablag -= hisobKitob.getSummaCol();
        }
        kelishmaDouble = jamiMablag;
        balansDouble = -kelishmaDouble;

        tolovRoyxati = yangiRoyxat(hisobKitobAndoza.getTolovRoyxati(), "tolov");
        bankToloviRoyxati = yangiRoyxat(hisobKitobAndoza.getBankToloviRoyxati(), "plastic");
        tolovRoyxati.addAll(bankToloviRoyxati);
        qaytimRoyxati = yangiRoyxat(hisobKitobAndoza.getQaytimRoyxati(), "qaytim");
        chegirmaRoyxati = yangiRoyxat(hisobKitobAndoza.getChegirmaRoyxati(), "chegirma");
        qoshimchaDaromadRoyxati = yangiRoyxat(hisobKitobAndoza.getQoshimchaDaromadRoyxati(), "qo`shimcha daromad");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ibtido();
        initStage(primaryStage);
        stage.show();
    }

    public Boolean display() {
        stage = new Stage();
        ibtido();
        initStage(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        return sotildi;
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
        scene = new Scene(borderPane, 540, 400);
        scene.setUserAgentStylesheet("sample/Styles/caspian.css");
//        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
    }

    private void yoz(ObservableList<JFXTolov2> observableList, Valuta valuta, Double narh) {
        for (JFXTolov2 t2: observableList) {
            if (t2.getValuta().getId().equals(valuta.getId())) {
                HisobKitob hisobKitob = t2.getHisobKitob();
                hisobKitob.setNarh(narh);
                JFXTextFieldButton textFieldButton = t2.getJfxTextFieldButton();
                JFXTextField textField = textFieldButton.getTextField();
                if (narh.equals(0d)) {
                    textField.setText("");
                } else {
                    textField.setText(decimalFormat.format(narh));
                }
                break;
            }
        }
    }

    private void yoz(ObservableList<JFXTolov2> observableList, HisobKitob hk) {
        for (JFXTolov2 t2: observableList) {
            HisobKitob hisobKitob = t2.getHisobKitob();
            if (hisobKitob.getValuta().equals(hk.getValuta())) {
                hisobKitob = hk;
                break;
            }
        }
    }

    private void royxatniOchir(ObservableList<JFXTolov2> observableList) {
        observableList.forEach(tolov2->{
            HisobKitob hisobKitob = tolov2.getHisobKitob();
            hisobKitob.setNarh(0d);
            JFXTextFieldButton jfxTextFieldButton = tolov2.getJfxTextFieldButton();
            JFXTextField textField = jfxTextFieldButton.getTextField();
            textField.setText("");
        });
    }

    private Double royxatValutaKursi(ObservableList<JFXTolov2> observableList, Valuta valuta) {
        Double kurs = 0d;
        for (JFXTolov2 tolov2: observableList) {
            HisobKitob hisobKitob = tolov2.getHisobKitob();
            if (valuta.getId().equals(hisobKitob.getValuta())) {
                kurs = hisobKitob.getKurs();
                break;
            }
        };
        return kurs;
    }

    private void hammasiniOchir() {
        royxatniOchir(tolovRoyxati);
        royxatniOchir(qaytimRoyxati);
        royxatniOchir(qoshimchaDaromadRoyxati);
        royxatniOchir(chegirmaRoyxati);
    }

    private void ochir(ObservableList<JFXTolov2> observableList, Valuta valuta) {
        for (JFXTolov2 t2: observableList) {
            if (t2.getValuta().getId().equals(valuta.getId())) {
                HisobKitob hisobKitob = t2.getHisobKitob();
                hisobKitob.setNarh(0d);
                JFXTextFieldButton jfxTextFieldButton = t2.getJfxTextFieldButton();
                JFXTextField textField = jfxTextFieldButton.getTextField();
                textField.setText("");
                break;
            }
        }
    }

    private void yukla(ObservableList<Tolov2> observableList, Valuta valuta) {
    }

    private ObservableList<GridPane> yangiJadvallar() {
        ObservableList<GridPane> gridPaneObservableList = FXCollections.observableArrayList();
        gridPaneObservableList.add(kelishma());
        gridPaneObservableList.add(tolov());
        gridPaneObservableList.add(qaytim());
        gridPaneObservableList.add(qoshimcha());
        gridPaneObservableList.add(chegirma());
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
                        yoz(qoshimchaDaromadRoyxati, joriyValuta, -mablag);
                    } else if (mablag > 0) {
                        label7.setText("");
                        label5.setText(decimalFormat.format(mablag));
                        yoz(chegirmaRoyxati, joriyValuta, mablag);
                    }
                    oldiBerdiniYangila();
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

    public GridPane tolov() {
        GridPane gridPane = new GridPane();
        HBox.setHgrow(gridPane, Priority.ALWAYS);
        gridPane.setPadding(new Insets(padding));
        gridPane.setHgap(2);
        gridPane.setVgap(15);
        Integer rowIndex = 0;
        for (JFXTolov2 tolov2: tolovRoyxati) {
            rowIndex++;
            HisobKitob hisobKitob = tolov2.getHisobKitob();
            JFXTextFieldButton jfxTextFieldButton = tolov2.getJfxTextFieldButton();
            JFXTextField textField = jfxTextFieldButton.getTextField();
            textField.setLabelFloat(true);
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    if (StringNumberUtils.isNumeric(newValue)) {
                        Double aDouble = StringNumberUtils.textToDouble(newValue) / hisobKitob.getKurs();
                        Double jami = jami(tolovRoyxati, hisobKitob);
                        Double ayirma = jamiMablag - jami(chegirmaRoyxati) + jami(qoshimchaDaromadRoyxati) - (aDouble + jami);
                        royxatniOchir(qaytimRoyxati);
                        if (ayirma < 0) {
                            Valuta milliyValuta = milliyValuta(qaytimRoyxati);
                            Double kursDouble = royxatValutaKursi(qaytimRoyxati, milliyValuta);
                            if (kursDouble.equals(0d)) {
                                ayirma = 0d;
                            } else {
                                ayirma *= kursDouble;
                            }
                            yoz(qaytimRoyxati, milliyValuta, -ayirma);
                        }
                        if (newValue.isEmpty()) {
                            hisobKitob.setNarh(0d);

                        }
                        oldiBerdiniYangila();
                    }
                }
                if (newValue.isEmpty()) {
                    oldiBerdiniYangila();
                }
            });
            gridPane.add(jfxTextFieldButton, 0, rowIndex, 1, 1);
            GridPane.setHgrow(jfxTextFieldButton, Priority.ALWAYS);
            JFXButton button = jfxTextFieldButton.getPlusButton();
            button.setOnAction(event -> {
                Double jami = jami(tolovRoyxati, hisobKitob);
                Double farqDouble = jamiMablag - jami(chegirmaRoyxati) + jami(qoshimchaDaromadRoyxati) - jami;
                if (farqDouble > 0) {
                    royxatniOchir(qaytimRoyxati);
                    hisobKitob.setNarh(farqDouble * hisobKitob.getKurs());
                    textField.setText(decimalFormat.format(hisobKitob.getNarh()));
                } else {
                    hisobKitob.setNarh(0d);
                    textField.setText("");
                }
                oldiBerdiniYangila();
            });
        }
        return gridPane;
    }

    public Double oldiBerdiniYangila() {
        asosiyRadioButton();
        Double a1 = jamiMablag;
        Double a2 = jami(chegirmaRoyxati);
        Double a3 = jami(qoshimchaDaromadRoyxati);
        Double a4 = jami(tolovRoyxati);
        Double a5 = jami(qaytimRoyxati);
        Double oldiBerdiDouble = (a1 - a2 + a3) - (a4 - a5);

//        Double oldiBerdiDouble = - jamiMablag / joriyValutaKursi + (jami(tolovRoyxati) - jami(qaytimRoyxati)) + jami(chegirmaRoyxati) - jami(qoshimchaDaromadRoyxati);
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumIntegerDigits (1);
        numberFormat.setMaximumIntegerDigits (10);

        numberFormat.setMinimumFractionDigits (1);
        numberFormat.setMaximumFractionDigits (25);
        System.out.println(numberFormat.format(oldiBerdiDouble));

        if (StringNumberUtils.yaxlitla(oldiBerdiDouble, -2) > 0.00) {
            qoldiqLabel.setFill(Color.BLUE);
            qoldiqLabel.setText(decimalFormat.format(oldiBerdiDouble));
        } else if (StringNumberUtils.yaxlitla(oldiBerdiDouble, -2) < 0.00) {
            qoldiqLabel.setFill(Color.RED);
            qoldiqLabel.setText(decimalFormat.format(-oldiBerdiDouble));
        } else if (StringNumberUtils.yaxlitla(oldiBerdiDouble, -2) == 0.00) {
            qoldiqLabel.setFill(Color.BLACK);
            qoldiqLabel.setText("");
            qoldiqLabel.setText(decimalFormat.format(oldiBerdiDouble));
        }

        balansDouble = oldiBerdiDouble;
        return oldiBerdiDouble;
    }

    private void asosiyRadioButton() {
        RadioButton radioButton = (RadioButton) radioGroup.getToggles().get(0);
        radioGroup.selectToggle(radioButton);
    }

    public GridPane qaytim() {
        GridPane gridPane = new GridPane();
        HBox.setHgrow(gridPane, Priority.ALWAYS);
        gridPane.setPadding(new Insets(padding));
        gridPane.setHgap(2);
        gridPane.setVgap(15);
        Integer rowIndex = 0;
        for (JFXTolov2 tolov2: qaytimRoyxati) {
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
                    oldiBerdiniYangila();
                }
            });

            JFXButton button = jfxTextFieldButton.getPlusButton();
            button.setOnAction(event -> {
                Double jamiTolov = jami(tolovRoyxati);
                Double qaytimDouble = kelishmaDouble  - jamiTolov;
                if (qaytimDouble < 0d) {
                    Double jamiQaytim = jami(qaytimRoyxati, hisobKitob);
                    qaytimDouble = -qaytimDouble - jamiQaytim;
                    yoz(qaytimRoyxati, valuta, qaytimDouble * hisobKitob.getKurs());
                    oldiBerdiniYangila();
                }
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
        for (JFXTolov2 tolov2: qoshimchaDaromadRoyxati) {
            rowIndex++;
            HisobKitob hisobKitob = tolov2.getHisobKitob();
            JFXTextFieldButton jfxTextFieldButton = tolov2.getJfxTextFieldButton();
            JFXTextField textField = jfxTextFieldButton.getTextField();
            textField.setLabelFloat(true);
            gridPane.add(jfxTextFieldButton, 0, rowIndex, 1, 1);
            GridPane.setHgrow(jfxTextFieldButton, Priority.ALWAYS);
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    oldiBerdiniYangila();
                }
            });

            JFXButton button = jfxTextFieldButton.getPlusButton();
            button.setOnAction(event -> {
                Double balance = jamiMablag - (jami(tolovRoyxati) - jami(qaytimRoyxati)) - jami(chegirmaRoyxati);
                if (balance < 0d) {
                    Double jamiQoshimcha = jami(qoshimchaDaromadRoyxati, hisobKitob);
                    balance += jamiQoshimcha;
                    Valuta valuta = tolov2.getValuta();
                    yoz(qoshimchaDaromadRoyxati, valuta, -balance * hisobKitob.getKurs());
                }
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
        for (JFXTolov2 tolov2: chegirmaRoyxati) {
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
                    oldiBerdiniYangila();
                }
            });

            JFXButton button = jfxTextFieldButton.getPlusButton();
            button.setOnAction(event -> {
                Double balance = jamiMablag - (jami(tolovRoyxati) - jami(qaytimRoyxati)) + jami(qoshimchaDaromadRoyxati);
                if (balance > 0d) {
                    Double jamiChegirma = jami(chegirmaRoyxati, hisobKitob);
                    balance -= jamiChegirma;
                    yoz(chegirmaRoyxati, valuta, balance * hisobKitob.getKurs());
                }
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
        if (hisobKitobAndoza.getTolovRoyxati().size() == 0) {
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
        HisobKitob savdoXossalari = hisobKitobAndoza.yangiSavdoXossalari(qaydnomaData, joriyValuta);
        hisobKitobModels.insert(connection, savdoXossalari);

        ObservableList<HisobKitob> tolov = hisobKitobAndoza.getTolovRoyxati();
        ObservableList<HisobKitob> bank = hisobKitobAndoza.getBankToloviRoyxati();
        ObservableList<HisobKitob> qaytim = hisobKitobAndoza.getQaytimRoyxati();
        ObservableList<HisobKitob> chegirma = hisobKitobAndoza.getChegirmaRoyxati();
        ObservableList<HisobKitob> qoshimchaDaromad = hisobKitobAndoza.getQoshimchaDaromadRoyxati();
        ObservableList<HisobKitob> hisobKitobRoyxati = FXCollections.observableArrayList();
        addTolovHisobKitob(hisobKitobRoyxati, tolov, qaydnomaData);
        addBankHisobKitob(hisobKitobRoyxati, bank, qaydnomaData);
        addQaytimHisobKitob(hisobKitobRoyxati, qaytim, qaydnomaData);
        addChegirmaHisobKitob(hisobKitobRoyxati, chegirma, qaydnomaData);
        addQoshimchaDaromadHisobKitob(hisobKitobRoyxati, qoshimchaDaromad, qaydnomaData);
        if (oldiBerdiDouble > 0.00) {
            // Tolov to'liq
            HisobKitob hisobKitob = balansniZarargaUr(oldiBerdiDouble);
            if (hisobKitob != null)
                hisobKitobRoyxati.add(hisobKitob);
        }

        if (oldiBerdiDouble < 0.00) {
            // ortiqcha to'lov
            HisobKitob hisobKitob = balansniFoydagaUr(-oldiBerdiDouble);
            if (hisobKitob != null)
                hisobKitobRoyxati.add(hisobKitob);
        }
        Savdo savdo = new Savdo(connection);
        Boolean yetarliAdad = false;
        for (HisobKitob h: xaridRoyxati) {
            h.setQaydId(qaydnomaData.getId());
            h.setHujjatId(qaydnomaData.getHujjat());
            h.setDateTime(qaydnomaData.getSana());
            if (h.getAmal().equals(4)) {
                h.setHisob1(hisob1.getId());
                h.setHisob2(hisob2.getId());
                savdo.setQaydnomaData(qaydnomaData);
                savdo.initHisobKitob(h);
                yetarliAdad = savdo.sot();
            } else if (h.getAmal().equals(2)) {
                hisobKitobRoyxati.add(0, h);
            }

        }

        if (hisobKitobRoyxati.size()>0) {
            hisobKitobModels.addBatch(connection, hisobKitobRoyxati);
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
            izohText = "Yaxlitlash tafovuti: " + valuta.getValuta() +  "\n Xarid № " + qaydnomaData.getHujjat().toString().trim();
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
            izohText = "Yaxlitlash tafovuti: " + valuta.getValuta() +  "\n Xarid № " + qaydnomaData.getHujjat().toString().trim();
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
        Integer amal = 4;
        QaydnomaData qaydnomaData = new QaydnomaData(
                null,
                amal,
                0,
                joriySana,
                hisob1.getId(),
                hisob1.getText(),
                hisob2.getId(),
                hisob2.getText(),
                "",
                0.00,
                0,
                user.getId(),
                null
        );
        qaydnomaModel.insert_data(connection, qaydnomaData);
        return qaydnomaData;
    }

    private void addTolovHisobKitob(ObservableList<HisobKitob> hisobKitobRoyxati, ObservableList<HisobKitob> tolovlarRoyxati, QaydnomaData qaydnomaData) {
        String izoh = "To'lov № " + qaydnomaData.getHujjat();
        royxatgaMalumotQosh(hisobKitobRoyxati, tolovlarRoyxati, qaydnomaData, izoh);
    }

    private void addBankHisobKitob(ObservableList<HisobKitob> hisobKitobRoyxati, ObservableList<HisobKitob> tolovlarRoyxati, QaydnomaData qaydnomaData) {
        String izoh = "Bankga to`lov № " + qaydnomaData.getHujjat();
        royxatgaMalumotQosh(hisobKitobRoyxati, tolovlarRoyxati, qaydnomaData, izoh);
    }

    private void addQaytimHisobKitob(ObservableList<HisobKitob> hisobKitobRoyxati, ObservableList<HisobKitob> tolovlarRoyxati, QaydnomaData qaydnomaData) {
        String izoh = "Qaytim № " + qaydnomaData.getHujjat();
        royxatgaMalumotQosh(hisobKitobRoyxati, tolovlarRoyxati, qaydnomaData, izoh);
    }

    private void addChegirmaHisobKitob(ObservableList<HisobKitob> hisobKitobRoyxati, ObservableList<HisobKitob> tolovlarRoyxati, QaydnomaData qaydnomaData) {
        String izoh = "Chegirma № " + qaydnomaData.getHujjat();
        royxatgaMalumotQosh(hisobKitobRoyxati, tolovlarRoyxati, qaydnomaData, izoh);
    }

    private void addQoshimchaDaromadHisobKitob(ObservableList<HisobKitob> hisobKitobRoyxati, ObservableList<HisobKitob> tolovlarRoyxati, QaydnomaData qaydnomaData) {
        String izoh = "Qoshimcha daromad № " + qaydnomaData.getHujjat();
        royxatgaMalumotQosh(hisobKitobRoyxati, tolovlarRoyxati, qaydnomaData, izoh);
    }

    private void royxatgaMalumotQosh(ObservableList<HisobKitob> hisobKitobRoyxati, ObservableList<HisobKitob> tolovlarRoyxati, QaydnomaData qaydnomaData, String izoh) {
        for (HisobKitob hisobKitob: tolovlarRoyxati) {
            hisobKitob.setQaydId(qaydnomaData.getId());
            hisobKitob.setHujjatId(qaydnomaData.getHujjat());
            hisobKitob.setUserId(user.getId());
            hisobKitob.setIzoh(izoh);
            if (!hisobKitob.getNarh().equals(0d)) {
                hisobKitobRoyxati.add(hisobKitob);
            }
        }

    }

    private void addHisobKitob(ObservableList<HisobKitob> hisobKitobRoyxati, ObservableList<HisobKitob> tolovlarRoyxati) {
        for (HisobKitob hisobKitob: tolovlarRoyxati) {
            if (!hisobKitob.getNarh().equals(0d)) {
                hisobKitobRoyxati.add(hisobKitob);
            }
        }
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
            listView.getSelectionModel().select(joriyJadval - 1);
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
            listView.getSelectionModel().select(joriyJadval - 1);
        });
        return button;
    }

    public Hisob getHisob1() {
        return hisob1;
    }

    public void setHisob1(Hisob hisob1) {
        this.hisob1 = hisob1;
    }

    public Hisob getHisob2() {
        return hisob2;
    }

    public void setHisob2(Hisob hisob2) {
        this.hisob2 = hisob2;
    }

    public Valuta getJoriyValuta(ObservableList<HisobKitob> jamiSavdoRoyxati) {
        Valuta valuta = null;
        ValutaModels valutaModels = new ValutaModels();
        for (HisobKitob hisobKitob: jamiSavdoRoyxati) {
            if (hisobKitob.getIzoh().contains(" Savdo №")) {
                valuta = valutaModels.getValuta(connection, hisobKitob.getValuta());
            }
        }

        return valuta;
    }

    public Valuta getJoriyValuta() {
        return joriyValuta;
    }

    public void setJoriyValuta(Valuta joriyValuta) {
        this.joriyValuta = joriyValuta;
    }

    public Integer getNarhTuri() {
        return narhTuri;
    }

    public void setNarhTuri(Integer narhTuri) {
        this.narhTuri = narhTuri;
    }

    public ObservableList<HisobKitob> getXaridRoyxati() {
        return xaridRoyxati;
    }

    public void setXaridRoyxati(ObservableList<HisobKitob> xaridRoyxati) {
        this.xaridRoyxati = xaridRoyxati;
    }

    public Double jami(ObservableList<JFXTolov2> observableList) {
        Double jami = 0d;
        for (JFXTolov2 tolov2: observableList) {
            HisobKitob hisobKitob = tolov2.getHisobKitob();
            jami += hisobKitob.getNarh() / hisobKitob.getKurs();
        }
        return jami;
    }
    public Double jami(ObservableList<JFXTolov2> observableList, HisobKitob hk) {
        Double jami = 0d;
        for (JFXTolov2 tolov2: observableList) {
            HisobKitob hisobKitob = tolov2.getHisobKitob();
            if (!hisobKitob.equals(hk)) {
                jami += hisobKitob.getNarh() / hisobKitob.getKurs();
            }
        }
        return jami;
    }

    private Valuta milliyValuta(ObservableList<JFXTolov2> royxat) {
        Valuta valuta = null;
        for (JFXTolov2 t2: royxat) {
            Valuta v = t2.getValuta();
            if (v.getStatus().equals(2)) {
                valuta = v;
                break;
            }
        }
        return valuta;
    }

    private ObservableList<JFXTolov2> tolovRoyxati() {
        KursModels kursModels = new KursModels();
        Kurs kurs = null;
        ObservableList<JFXTolov2> observableList = FXCollections.observableArrayList();
        ObservableList<Valuta> milliyValutaObservableList = FXCollections.observableArrayList();
        Integer id = 1;
        for (Valuta valuta: valutaObservableList) {
            if (valuta.getStatus().equals(2)) {
                milliyValutaObservableList.add(valuta);
            }
            HisobKitob hisobKitob = tolovHisobKitob(kassa, valuta, id);
            kurs = kursModels.getKurs(connection, valuta.getId(), new Date(), "sana desc");
            if (valuta.getId().equals(joriyValuta.getId())) {
                joriyValutaKursi = kurs.getKurs();
            }
            if (kurs != null)
                hisobKitob.setKurs(kurs.getKurs());
            else
                hisobKitob.setKurs(1d);
            JFXTolov2 tolov = new JFXTolov2(valuta, hisobKitob);
            observableList.add(tolov);
            id ++;
        }

        for (Valuta valuta: milliyValutaObservableList) {
            Valuta valuta1 = new Valuta(valuta.getId(),"Plastik " + valuta.getValuta(), valuta.getStatus(), valuta.getUserId(), valuta.getDateTime());
            HisobKitobModels hisobKitobModels = new HisobKitobModels();
            HisobKitob hisobKitob = new HisobKitob();
            hisobKitob.setId(valuta1.getId());
            hisobKitob.setValuta(valuta1.getId());
            kurs = kursModels.getKurs(connection, valuta1.getId(), new Date(), "sana desc");
            hisobKitob.setKurs(kurs.getKurs());
            JFXTolov2 tolov2 = new JFXTolov2(valuta1, hisobKitob);
            observableList.add(tolov2);
            id++;
        }
        return observableList;
    }

    private ObservableList<JFXTolov2> yangiRoyxat(String string) {
        KursModels kursModels = new KursModels();
        Kurs kurs = null;
        ObservableList<JFXTolov2> observableList = FXCollections.observableArrayList();
        Integer id = 1;
        for (Valuta valuta: valutaObservableList) {
            Valuta valuta1 = new Valuta(valuta.getId(),valuta.getValuta() + " " + string, valuta.getStatus(), valuta.getUserId(), valuta.getDateTime());
            HisobKitob hisobKitob = null;
            switch (string) {
                case "tolov":
                    hisobKitob = tolovHisobKitob(kassa, valuta, id);
                    break;
                case "plastic":
                    hisobKitob = qaytimHisobKitob(kassa, valuta, id);
                    break;
                case "qaytim":
                    hisobKitob = plasticHisobKitob(kassa, valuta, id);
                    break;
                case "chegirma":
                    hisobKitob = chegirmaHisobKitob(kassa, valuta, id);
                    break;
                case "qo`shimcha daromad":
                    hisobKitob = qoshimchaDaromadHisobKitob(kassa, valuta, id);
                    break;
            }
            JFXTolov2 tolov = new JFXTolov2(valuta1, hisobKitob);
            observableList.add(tolov);
            id ++;
        }
        return observableList;
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

    private HisobKitob tolovHisobKitob(Kassa kassa, Valuta valuta, Integer id) {
        HisobModels hisobModels = new HisobModels();
        Hisob pulHisobi = hisobModels.pulHisobi(connection, user, hisob1);
        KursModels kursModels = new KursModels();
        Integer amal = 7;
        HisobKitob hisobKitob = new HisobKitob();
        hisobKitob.setId(id);
        hisobKitob.setQaydId(0);
        hisobKitob.setHujjatId(0);
        hisobKitob.setHisob1(hisob2.getId());
        hisobKitob.setHisob2(pulHisobi.getId());
        hisobKitob.setAmal(amal);
        hisobKitob.setValuta(valuta.getId());
        hisobKitob.setUserId(user.getId());
        hisobKitob.setKurs(kursModels.getKurs(connection, valuta.getId(), new Date(), "sana desc").getKurs());
        return hisobKitob;
    }
    private HisobKitob plasticHisobKitob(Kassa kassa, Valuta valuta, Integer id) {
        HisobModels hisobModels = new HisobModels();
        Hisob bankHisobi = hisobModels.bankHisobi(connection, hisob1);
        KursModels kursModels = new KursModels();
        Integer amal = 15;
        HisobKitob hisobKitob = new HisobKitob();
        hisobKitob.setId(id);
        hisobKitob.setQaydId(0);
        hisobKitob.setHujjatId(0);
        hisobKitob.setHisob1(hisob2.getId());
        hisobKitob.setHisob2(bankHisobi.getId());
        hisobKitob.setAmal(amal);
        hisobKitob.setValuta(valuta.getId());
        hisobKitob.setUserId(user.getId());
        hisobKitob.setKurs(kursModels.getKurs(connection, valuta.getId(), new Date(), "sana desc").getKurs());
        return hisobKitob;
    }
    private HisobKitob qaytimHisobKitob(Kassa kassa, Valuta valuta, Integer id) {
        HisobModels hisobModels = new HisobModels();
        Hisob pulHisobi = hisobModels.pulHisobi(connection, user, hisob1);
        Integer amal = 8;
        KursModels kursModels = new KursModels();
        HisobKitob hisobKitob = new HisobKitob();
        hisobKitob.setId(id);
        hisobKitob.setQaydId(0);
        hisobKitob.setHujjatId(0);
        hisobKitob.setHisob1(pulHisobi.getId());
        hisobKitob.setHisob2(hisob2.getId());
        hisobKitob.setAmal(amal);
        hisobKitob.setValuta(valuta.getId());
        hisobKitob.setUserId(user.getId());
        hisobKitob.setKurs(kursModels.getKurs(connection, valuta.getId(), new Date(), "sana desc").getKurs());
        return hisobKitob;
    }
    private HisobKitob chegirmaHisobKitob(Kassa kassa, Valuta valuta, Integer hisobKitobId) {
        HisobModels hisobModels = new HisobModels();
        Hisob chegirmaHisobi = hisobModels.chegirmaHisobi(connection, hisob1);
        if (chegirmaHisobi == null)
            chegirmaHisobi = hisob1;
        Integer amal = 13;
        KursModels kursModels = new KursModels();
        HisobKitob hisobKitob = new HisobKitob();
        hisobKitob.setId(hisobKitobId);
        hisobKitob.setQaydId(0);
        hisobKitob.setHujjatId(0);
        hisobKitob.setHisob1(hisob2.getId());
        hisobKitob.setHisob2(chegirmaHisobi.getId());
        hisobKitob.setAmal(amal);
        hisobKitob.setValuta(valuta.getId());
        hisobKitob.setUserId(user.getId());
        hisobKitob.setKurs(kursModels.getKurs(connection, valuta.getId(), new Date(), "sana desc").getKurs());
        return hisobKitob;
    }
    private HisobKitob qoshimchaDaromadHisobKitob(Kassa kassa, Valuta valuta, Integer id) {
        HisobModels hisobModels = new HisobModels();
        Hisob qoshimchaDaromadHisobi = hisobModels.qoshimchaDaromadHisobi(connection, hisob1);
        Integer amal = 18;
        KursModels kursModels = new KursModels();
        HisobKitob hisobKitob = new HisobKitob();
        hisobKitob.setId(id);
        hisobKitob.setQaydId(0);
        hisobKitob.setHujjatId(0);
        hisobKitob.setHisob2(hisob2.getId());
        hisobKitob.setHisob1(qoshimchaDaromadHisobi.getId());
        hisobKitob.setAmal(amal);
        hisobKitob.setValuta(valuta.getId());
        hisobKitob.setUserId(user.getId());
        hisobKitob.setKurs(kursModels.getKurs(connection, valuta.getId(), new Date(), "sana desc").getKurs());
        return hisobKitob;
    }

    private BorderPane borderPane() {
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(centerPane);
        borderPane.setRight(ongLawha());
        return borderPane;
    }

    private VBox ongLawha() {
        VBox vBox = new VBox();
        listView = new ListView<>();
        ObservableList<Label> labels = FXCollections.observableArrayList(
                new Label("Kelishuv"),
                new Label("To`lov"),
                new Label("Qaytim"),
                new Label("Oo`shimcha daromad"),
                new Label("Chegirma")
        );
        listView.setItems(labels);
        listView.setMaxWidth(150);
        listView.getSelectionModel().selectFirst();
        vBox.getChildren().addAll(listView);
        listView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                joriyJadval = newValue.intValue() +1;
                centerPane.getChildren().remove(0);
                centerPane.getChildren().add(0, gridPaneObservableList.get(joriyJadval-1));
            }
        });
        return vBox;
    }

    public HisobKitobAndoza getHisobKitobAndoza() {
        return hisobKitobAndoza;
    }

    public void setHisobKitobAndoza(HisobKitobAndoza hisobKitobAndoza) {
        this.hisobKitobAndoza = hisobKitobAndoza;
    }

    public ObservableList<JFXTolov2> getTolovRoyxati() {
        return tolovRoyxati;
    }

    public void setTolovRoyxati(ObservableList<JFXTolov2> tolovRoyxati) {
        this.tolovRoyxati = tolovRoyxati;
    }

    public ObservableList<JFXTolov2> getBankToloviRoyxati() {
        return bankToloviRoyxati;
    }

    public void setBankToloviRoyxati(ObservableList<JFXTolov2> bankToloviRoyxati) {
        this.bankToloviRoyxati = bankToloviRoyxati;
    }

    public ObservableList<JFXTolov2> getQaytimRoyxati() {
        return qaytimRoyxati;
    }

    public void setQaytimRoyxati(ObservableList<JFXTolov2> qaytimRoyxati) {
        this.qaytimRoyxati = qaytimRoyxati;
    }

    public ObservableList<JFXTolov2> getQoshimchaDaromadRoyxati() {
        return qoshimchaDaromadRoyxati;
    }

    public void setQoshimchaDaromadRoyxati(ObservableList<JFXTolov2> qoshimchaDaromadRoyxati) {
        this.qoshimchaDaromadRoyxati = qoshimchaDaromadRoyxati;
    }

    public ObservableList<JFXTolov2> getChegirmaRoyxati() {
        return chegirmaRoyxati;
    }

    public void setChegirmaRoyxati(ObservableList<JFXTolov2> chegirmaRoyxati) {
        this.chegirmaRoyxati = chegirmaRoyxati;
    }
}
