package sample.Tools;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import sample.Controller.ValutaController;
import sample.Data.*;
import sample.Model.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class RightGridPane extends GridPane {
    Label idLabel = new Label("Id");
    TextField idTextField = new TextField();
    TextField hujjatIdTextField = new TextField();
    DatePicker sanaDatePicker = new DatePicker();
    TextField vaqtTextField = new TextField();
    TextField hisob1TextField = new TextField();
    Button hisob1Button = new Button();
    HBox hisob1HBox = new HBox();
    TextField hisob2TextField = new TextField();
    Button hisob2Button = new Button();
    HBox hisob2HBox = new HBox();
    ComboBox<Standart> amalComboBox = new ComboBox<>();
    Button amalButton = new Button();
    HBox amalHBox = new HBox();
    Label valutaLabel = new Label("Pul turi");
    ComboBox<Valuta> valutaComboBox = new ComboBox<>();
    Button valutaButton = new Button();
    HBox valutaHBox = new HBox();
    Label tovarLabel = new Label("Tovar");
    Label tovarNomiLabel = new Label();
    Integer tovarId;
    Button tovarButton = new Button();
    HBox tovarHBox = new HBox();
    Label barCodeLabel = new Label("Strixkod");
    TextField barCodeTextField = new TextField();
    Label tovarJumlaLabel = new Label("");
    Label tovarDonaLabel = new Label();
    Label tovarBirlikLabel = new Label("");
    Label zaxiradaLabel = new Label();

    Label zaxiraLabel = new Label("Zaxira");
    Label zaxiraJumlaLabel = new Label();
    Label zaxiraJumlaAdadiLabel = new Label();
    Label zaxiraBirlikAdadiLabel = new Label();
    Label zaxiraBirlikLabel = new Label();
    TableView<HisobKitob> zaxiraTableView = new TableView();
    ObservableList<HisobKitob> zaxiraObservableList = FXCollections.observableArrayList();

    Label kursLabel = new Label("Kurs");
    TextField kursTextField = new TextField();
    Label jumlaLabel = new Label("Xarid shakli");
    ComboBox<Standart> jumlaComboBox = new ComboBox<>();
    Label donaLabel = new Label("Adad");
    TextField donaTextField = new TextField();
    ComboBox<Standart> birlikComboBox = new ComboBox<>();
    Label narhLabel = new Label("Narh");
    TextField narhTextField = new TextField();
    Label jamiLabel = new Label("Jami");
    Label jamiTovarNarhi = new Label();
    Button qaydEtButton = new Button("Qayd et");
    Button cancelButton = new Button("<<");
    Tugmachalar tugmachalar;

    Label chiqimShakli = new Label("Chiqim shakli");
    ComboBox<Standart> chiqimShakliComboBox = new ComboBox<>();
    ObservableList<Standart> chiqimShakliObservableList = FXCollections.observableArrayList();
    Button chiqimShakliButton = new Button();
    HBox chiqimShakliHBox = new HBox();

    PathToImageView pathToImageView;
    HisobModels hisobModels = new HisobModels();
    StandartModels amalModels = new StandartModels();
    StandartModels standartModels = new StandartModels();
    QaydnomaData qaydnomaData;
    HisobKitob hisobKitob;
    HisobKitobModels hisobKitobModels = new HisobKitobModels();
    User user;

    ObservableList<Hisob> hisobs;
    ObservableList<Valuta> valutas;
    ObservableList<Standart> amals;
    ObservableList<Standart> tovars;
    ObservableList<Standart> jumlas = FXCollections.observableArrayList();
    ObservableList<HisobKitob> tableViewData;

    DecimalFormat decimalFormat = new MoneyShow();
    Connection connection;
    QaydnomaJami qaydnomaJami;

    Separator separator1 = new Separator();
    Separator separator2 = new Separator();
    Separator separator3 = new Separator();
    Separator separator4 = new Separator();

    public RightGridPane(Connection connection, User user, QaydnomaData qaydnomaData, QaydnomaJami qaydnomaJami, Tugmachalar tugmachalar) throws IOException, SQLException, ClassNotFoundException, ParseException {
        this.connection = connection;
        this.user = user;
        this.qaydnomaData = qaydnomaData;
        this.qaydnomaJami = qaydnomaJami;
        this.tugmachalar = tugmachalar;
        amalModels.setTABLENAME("Amal");
        barCodeTextField.setText("");
        pathToImageView  = new PathToImageView("/sample/images/Icons/add.png");
        amalButton.setGraphic( new PathToImageView("/sample/images/Icons/add.png").getImageView());
        valutaButton.setGraphic( new PathToImageView("/sample/images/Icons/add.png").getImageView());
        valutaButton.setOnAction(event -> {
            ValutaController valutaController = new ValutaController();
            valutaController.display(connection, user);
            if (valutaController.getDoubleClick()) {
                Valuta valuta = valutaController.getDoubleClickedRow();
                valutaComboBox.getSelectionModel().select(valuta);
                if (valuta.getStatus() == 1) {
                    kursTextField.setText("1");
                } else {
                    kursTextField.setText(""); /* keyinchalik yozaman */
                }
                valutaController.getStage().close();
            }
        });
        valutaComboBox.setOnAction(event -> {
            Valuta valuta = valutaComboBox.getValue();
            if (valuta != null) {
                valutaAction(valuta);
            }
        });
        hisob1Button.setGraphic( new PathToImageView("/sample/images/Icons/add.png").getImageView());
        hisob2Button.setGraphic( new PathToImageView("/sample/images/Icons/add.png").getImageView());

        hisobs = hisobModels.get_data(connection);
        valutas = GetDbData.getValutaObservableList();
        amals = GetDbData.getAmalObservableList();
        standartModels.setTABLENAME("Tovar");
        tovars = GetDbData.getTovarObservableList();
        standartModels.setTABLENAME("Jumla");

        valutaComboBox.setItems(valutas);
        valutaComboBox.getSelectionModel().selectFirst();
        if (valutaComboBox.getSelectionModel().getSelectedItem().getStatus() == 1) {
            kursTextField.setText("1");
        } else {
            kursTextField.setText(""); /* keyinchalik yozaman */
        }
        jumlaComboBox.setItems(jumlas);
        jumlaComboBox.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            jamiHisob();
        }));

        TextFields.bindAutoCompletion(hisob1TextField, hisobs).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Hisob> autoCompletionEvent) -> {
            Hisob hisob = autoCompletionEvent.getCompletion();
            System.out.println(hisob.getId());
        });
        TextFields.bindAutoCompletion(hisob2TextField, hisobs).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Hisob> autoCompletionEvent) -> {
            Hisob hisob = autoCompletionEvent.getCompletion();
            System.out.println(hisob.getId());
        });

        tovarButton.setGraphic( new PathToImageView("/sample/images/Icons/add.png").getImageView());


        barCodeTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode()== KeyCode.ENTER) {
                    String string = barCodeTextField.getText();
                    BarCode barCode = GetDbData.getBarCode(string);
                    if (barCode != null) {
                        tovarAction(barCode);
                    }
                }
            }
        });

        hisob1HBox.getChildren().addAll(hisob1TextField, hisob1Button);
        hisob2HBox.getChildren().addAll(hisob2TextField, hisob2Button);
        amalHBox.getChildren().addAll(amalComboBox, amalButton);
        valutaHBox.getChildren().addAll(valutaComboBox, valutaButton);
        tovarHBox.getChildren().addAll(tovarNomiLabel, tovarButton);
        setPadding(new Insets(15));
        setVgap(10);
        setHgap(25);

        chiqimShakliComboBox.setMaxWidth(2000);
        chiqimShakliComboBox.setPrefWidth(150);
        standartModels.setTABLENAME("ChiqimShakli");
        chiqimShakliObservableList = standartModels.get_data(connection);
        chiqimShakliComboBox.setItems(chiqimShakliObservableList);
        chiqimShakliComboBox.getSelectionModel().selectFirst();
        chiqimShakliHBox.getChildren().addAll(chiqimShakliComboBox, chiqimShakliButton);
        chiqimShakliButton.setGraphic( new PathToImageView("/sample/images/Icons/add.png").getImageView());
        HBox.setHgrow(chiqimShakliComboBox, Priority.ALWAYS);
        HBox.setHgrow(chiqimShakliHBox, Priority.ALWAYS);
        HBox.setHgrow(tovarHBox, Priority.ALWAYS);
        HBox.setHgrow(tovarNomiLabel, Priority.ALWAYS);
        HBox.setHgrow(this, Priority.ALWAYS);
        VBox.setVgrow(this, Priority.ALWAYS);
        HBox.setHgrow(tovarDonaLabel, Priority.ALWAYS);
        HBox.setHgrow(tovarBirlikLabel, Priority.ALWAYS);
        HBox.setHgrow(tovarDonaLabel, Priority.ALWAYS);
        HBox.setHgrow(valutaHBox, Priority.ALWAYS);
        HBox.setHgrow(valutaComboBox, Priority.ALWAYS);

        amalComboBox.setMaxWidth(2000);
        amalComboBox.setPrefWidth(150);
        valutaComboBox.setMaxWidth(2000);
        valutaComboBox.setPrefWidth(150);
        jumlaComboBox.setMaxWidth(2000);
        jumlaComboBox.setPrefWidth(150);
        birlikComboBox.setMaxWidth(2000);
        birlikComboBox.setPrefWidth(150);
        narhTextField.textProperty().addListener(((observable, oldValue, newValue) -> {jamiHisob();}));
        donaTextField.textProperty().addListener(((observable, oldValue, newValue) -> {jamiHisob();}));
    }

    /***********************************************************************************************************************/
    private void initFromHisobKitob() {
        DecimalFormat decimalFormat = new MoneyShow();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        LocalDateTime localDateTime = qaydnomaData.getSana().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        Integer day = localDateTime.getDayOfMonth();
        Integer month = localDateTime.getMonthValue();
        Integer year = localDateTime.getYear();

        if (hisobKitob.getId()!=null) {
            idTextField.setText(hisobKitob.getId().toString());
        }
        hujjatIdTextField.setText(hisobKitob.getHujjatId().toString());
        sanaDatePicker = new DatePicker(LocalDate.of(year, month, day));
        vaqtTextField.setText(sdf.format(qaydnomaData.getSana()));
        BarCode barCode = GetDbData.getBarCode(hisobKitob.getBarCode());
        if (barCode != null) {
            barCodeTextField.setText(barCode.getBarCode());
            tovarAction(barCode);
        } else {
            barCodeTextField.setText("");
        }
        Hisob hisob1 = GetDbData.getHisob(hisobKitob.getHisob1());
        if (hisob1 != null) {
            hisob1TextField.setText(hisob1.getText());
        }
        Hisob hisob2 = GetDbData.getHisob(hisobKitob.getHisob2());
        if (hisob2 != null) {
            hisob2TextField.setText(hisob2.getText());
        }
        if (hisobKitob.getAmal() != null) {
            Standart amal = GetDbData.getAmal(hisobKitob.getAmal());
            if (amal != null) {
                amalComboBox.getSelectionModel().select(amal);
            }
        }

        if (hisobKitob.getValuta()  != null) {
            Valuta valuta = GetDbData.getValuta(hisobKitob.getValuta());
            if (valuta  != null) {
                valutaComboBox.getSelectionModel().select(valuta);
            }
        }

        tovarId = hisobKitob.getTovar();
        if (hisobKitob.getKurs() != null) {
            kursTextField.setText(decimalFormat.format(hisobKitob.getKurs()));
        }
        if (hisobKitob.getDona() != null) {
            donaTextField.setText(decimalFormat.format(hisobKitob.getDona()));
        }
        if (hisobKitob.getNarh() != null) {
            narhTextField.setText(decimalFormat.format(hisobKitob.getNarh()));
        }
        GridPane.setHgrow(separator1, Priority.ALWAYS);
        GridPane.setHgrow(separator2, Priority.ALWAYS);
        GridPane.setHgrow(separator3, Priority.ALWAYS);
        GridPane.setHgrow(separator4, Priority.ALWAYS);
        GridPane.setHgrow(tovarHBox, Priority.ALWAYS);
        GridPane.setHgrow(tovarNomiLabel, Priority.ALWAYS);
        GridPane.setHgrow(barCodeTextField, Priority.ALWAYS);
        GridPane.setHgrow(valutaComboBox, Priority.ALWAYS);
        GridPane.setHgrow(jumlaComboBox, Priority.ALWAYS);
        GridPane.setHgrow(chiqimShakliHBox, Priority.ALWAYS);
        GridPane.setHgrow(narhTextField, Priority.ALWAYS);
        GridPane.setHgrow(donaTextField, Priority.ALWAYS);
        GridPane.setHgrow(tovarDonaLabel, Priority.ALWAYS);
        HBox.setHgrow(this, Priority.ALWAYS);
        VBox.setVgrow(this, Priority.ALWAYS);
    }

    /***********************************************************************************************************************/
    public void add(TableView<QaydnomaData> qaydnomaTableView, HisobKitob hisobKitob, TableView<HisobKitob> tableView) throws SQLException, ParseException {
        this.hisobKitob = hisobKitob;
        this.tableViewData = tableView.getItems();
        this.qaydnomaData = qaydnomaTableView.getSelectionModel().getSelectedItem();
        jamiTovarNarhi.setText("");
        zaxiraJumlaLabel.setText("");
        zaxiraJumlaAdadiLabel.setText("");
        zaxiraBirlikLabel.setText("");
        zaxiraBirlikAdadiLabel.setText("");

        jamiHisob();
        hisobKitob.setUserId(user.getId());
        hisobKitob.setDateTime(new Date());
        initFromHisobKitob();
        barCodeTextField.setText("");
        qaydEtButton = new Tugmachalar().getAdd();
        valutaComboBox.getSelectionModel().selectFirst();
        if (valutaComboBox.getSelectionModel().getSelectedItem().getStatus() ==1) {
            kursTextField.setText("1");
        }
        gridSetDisable(false);
        show();
    }

    public void edit(TableView<QaydnomaData> qaydnomaTableView, HisobKitob hisobKitob, TableView<HisobKitob> tableView) throws SQLException, ParseException {
        this.hisobKitob = hisobKitob;
        this.tableViewData = tableView.getItems();
        this.qaydnomaData = qaydnomaTableView.getSelectionModel().getSelectedItem();
        hisobKitob.setUserId(user.getId());
        hisobKitob.setDateTime(new Date());
        initFromHisobKitob();
        jamiHisob();
        qaydEtButton = new Tugmachalar().getEdit();
        gridSetDisable(false);
        show();
    }

    public void delete(TableView<QaydnomaData> qaydnomaTableView, HisobKitob hisobKitob, TableView<HisobKitob> tableView) throws SQLException, ParseException {
        this.hisobKitob = hisobKitob;
        this.tableViewData = tableView.getItems();
        this.qaydnomaData = qaydnomaTableView.getSelectionModel().getSelectedItem();
        hisobKitob.setUserId(user.getId());
        hisobKitob.setDateTime(new Date());
        initFromHisobKitob();
        jamiHisob();
        qaydEtButton = new Tugmachalar().getDelete();
        gridSetDisable(true);
        show();
    }

    public void gridToHisobKitob(ObservableList<HisobKitob> tableViewData, Integer action) throws SQLException {
        switch (qaydnomaData.getAmalTuri()) {
            case 1:
                // Pul harakatlari
                switch (action) {
                    case 1:
                        //Add
                        hisobKitob.setIzoh(qaydnomaData.getIzoh());
                        hisobKitob.setValuta(valutaComboBox.getValue().getId());
                        hisobKitob.setKurs(Double.valueOf(spaceDelete(kursTextField.getText())));
                        hisobKitob.setNarh(Double.valueOf(spaceDelete(narhTextField.getText())));
                        break;
                    case 2:
                        //Delete
                        break;
                    case 3:
                        //Edit
                        hisobKitob.setIzoh(qaydnomaData.getIzoh());
                        hisobKitob.setValuta(valutaComboBox.getValue().getId());
                        hisobKitob.setKurs(Double.valueOf(spaceDelete(kursTextField.getText())));
                        hisobKitob.setNarh(Double.valueOf(spaceDelete(narhTextField.getText())));
                        break;
                }
                break;
            case 2:
                // Xarid
                switch (action) {
                    case 1:
                        //Add
                        hisobKitob.setBarCode(barCodeTextField.getText());
                        hisobKitob.setTovar(tovarId);
                        hisobKitob.setManba(0);
                        hisobKitob.setIzoh(tovarNomiLabel.getText());
                        hisobKitob.setValuta(valutaComboBox.getValue().getId());
                        hisobKitob.setKurs(Double.valueOf(spaceDelete(kursTextField.getText())));
                        hisobKitob.setDona(Double.valueOf(spaceDelete(donaTextField.getText())));
                        hisobKitob.setNarh(Double.valueOf(spaceDelete(narhTextField.getText())));
                        break;
                    case 2:
                        //Delete
                        break;
                    case 3:
                        //Edit
                        hisobKitob.setBarCode(barCodeTextField.getText());
                        hisobKitob.setTovar(tovarId);
                        hisobKitob.setValuta(valutaComboBox.getValue().getId());
                        hisobKitob.setKurs(Double.valueOf(spaceDelete(kursTextField.getText())));
                        hisobKitob.setDona(Double.valueOf(spaceDelete(donaTextField.getText())));
                        hisobKitob.setNarh(Double.valueOf(spaceDelete(narhTextField.getText())));
                        break;
                }
                break;
            case 3:
                // Tovar harakatlari
                switch (action) {
                    case 1:
                        //Add
                        hisobKitob.setTovar(tovarId);
                        if (jumlaComboBox.getSelectionModel().getSelectedIndex() == 0) {
                            hisobKitob.setBarCode(jumlaComboBox.getValue().getId().toString().trim()+":0:"+tovarJumlaLabel.getText() + ":" + tovarDonaLabel.getText() + ":" + tovarBirlikLabel.getText());
                        } else if (jumlaComboBox.getSelectionModel().getSelectedIndex() == 1) {
                            hisobKitob.setBarCode("0:" + jumlaComboBox.getValue().getId().toString().trim() + ":" + tovarJumlaLabel.getText() + ":" + tovarDonaLabel.getText() + ":" + tovarBirlikLabel.getText());
                        }
                        hisobKitob.setBarCode(hisobKitob.getBarCode()+":"+chiqimShakliComboBox.getValue().getId().toString()+":"+chiqimShakliComboBox.getValue().getText());
                        hisobKitob.setDona(Double.valueOf(spaceDelete(donaTextField.getText())));
                        break;
                    case 2:
                        //Delet
                        break;
                    case 3:
                        //Edit
                        hisobKitob.setTovar(tovarId);
                        if (jumlaComboBox.getSelectionModel().getSelectedIndex() == 0) {
                            hisobKitob.setBarCode(jumlaComboBox.getValue().getId().toString().trim()+":0:"+tovarJumlaLabel.getText() + ":" + tovarDonaLabel.getText() + ":" + tovarBirlikLabel.getText());
                        } else if (jumlaComboBox.getSelectionModel().getSelectedIndex() == 1) {
                            hisobKitob.setBarCode("0:" + jumlaComboBox.getValue().getId().toString().trim() + ":" + tovarJumlaLabel.getText() + ":" + tovarDonaLabel.getText() + ":" + tovarBirlikLabel.getText());
                        }
                        hisobKitob.setBarCode(hisobKitob.getBarCode()+":"+chiqimShakliComboBox.getValue().getId().toString()+":"+chiqimShakliComboBox.getValue().getText());
                        hisobKitob.setDona(Double.valueOf(spaceDelete(donaTextField.getText())));
                        break;
                }
        }
    }

    public void show() {
        GridPane.setHalignment(cancelButton, HPos.LEFT);
        GridPane.setHalignment(qaydEtButton, HPos.RIGHT);
        Integer rowIndex = 0;
        getChildren().removeAll(getChildren());
//        rowIndex++;
        switch (qaydnomaData.getAmalTuri()) {
            case 1:
                add(valutaLabel,0, rowIndex);
                add(valutaHBox, 2,rowIndex,1,1);
                rowIndex++;
                add(new Label("Zaxirada"), 0, rowIndex, 1, 1);
                add(zaxiradaLabel, 2, rowIndex, 1, 1);
                rowIndex++;
                add(separator1, 0, rowIndex, 3, 1);
                rowIndex++;
                add(kursLabel, 0,rowIndex);
                add(kursTextField, 2, rowIndex,1,1);
                rowIndex++;
                add(narhLabel, 0,rowIndex);
                add(narhTextField,2,rowIndex,1,1);
                rowIndex++;
                add(separator2, 0, rowIndex, 3, 1);
                rowIndex++;
                break;
            case 2:
                add(barCodeLabel, 0, rowIndex, 1, 1);
                add(barCodeTextField, 2, rowIndex, 1, 1);
                rowIndex++;
                add(tovarLabel,0,rowIndex,1,1);
                add(tovarNomiLabel,2,rowIndex,1,1);
                rowIndex++;
                add(new Label("O`lchov birligi"), 0, rowIndex, 1, 1);
                add(tovarBirlikLabel, 2, rowIndex,1,1);
                rowIndex++;
                add(new Label("Tarkib"), 0, rowIndex, 1, 1);
                add(tovarDonaLabel, 2, rowIndex,1,1);
                rowIndex++;
                add(new Label("Zaxirada"), 0, rowIndex, 1, 1);
                add(zaxiradaLabel, 2, rowIndex, 1, 1);

                rowIndex++;
                add(separator2, 0, rowIndex, 3, 1);
                rowIndex++;
                add(valutaLabel,0, rowIndex);
                add(valutaHBox, 2,rowIndex,1,1);
                rowIndex++;
                add(kursLabel, 0,rowIndex);
                add(kursTextField, 2, rowIndex,1,1);
                rowIndex++;
                add(donaLabel, 0, rowIndex);
                add(donaTextField, 2, rowIndex, 1,1);
                rowIndex++;
                add(narhLabel, 0,rowIndex);
                add(narhTextField,2,rowIndex,1,1);
                rowIndex++;
                add(separator3, 0, rowIndex, 3, 1);
                rowIndex++;
                add(jamiLabel, 0, rowIndex, 1,1);
                add(jamiTovarNarhi, 2, rowIndex,1,1);
                rowIndex++;
                add(separator4, 0, rowIndex, 3, 1);
                rowIndex++;
                break;
            case 3:
                add(tovarLabel,0,rowIndex);
                add(tovarHBox,2,rowIndex,2,1);
                rowIndex++;
                add(barCodeLabel, 0, rowIndex, 1, 1);
                add(barCodeTextField, 2, rowIndex, 2, 1);
                rowIndex++;
                add(separator1, 0, rowIndex, 5, 1);
                rowIndex++;
                add(tovarJumlaLabel, 0, rowIndex, 1,1);
                add(tovarDonaLabel, 2, rowIndex,1,1);
                add(tovarBirlikLabel, 3, rowIndex,1,1);

                rowIndex++;
                HBox zaxiraHBox1 = new HBox(10);
                zaxiraHBox1.getChildren().addAll(zaxiraJumlaLabel, zaxiraJumlaAdadiLabel);
                HBox zaxiraHBox2 = new HBox(10);
                zaxiraHBox2.getChildren().addAll(zaxiraBirlikLabel, zaxiraBirlikAdadiLabel);
                add(zaxiraLabel, 0, rowIndex, 1,1);
                add(zaxiraHBox1, 2, rowIndex,1,1);
                add(zaxiraHBox2, 3, rowIndex,1,1);

                rowIndex++;
                add(separator2, 0, rowIndex, 5, 1);
                rowIndex++;
                add(jumlaLabel,0,rowIndex);
                add(jumlaComboBox,2,rowIndex,2,1);
                rowIndex++;
                add(chiqimShakli,0,rowIndex);
                add(chiqimShakliHBox,2,rowIndex,2,1);
                rowIndex++;
                add(donaLabel, 0, rowIndex);
                add(donaTextField, 2, rowIndex, 2,1);
                rowIndex++;
                add(separator3, 0, rowIndex, 5, 1);
                rowIndex++;
                break;
            case 4:
                break;
            case 5:
                break;
        }
        add(cancelButton,0, rowIndex, 1,1);
        add(qaydEtButton, 2,rowIndex);
    }

    public String spaceDelete(String string) {
        string = string.replaceAll("\\s+", "");
        string = string.replaceAll(",", ".");
        return string;
    }

    public void jamiHisob() {
        Double dona = .00;
        Double narh = .00;
        Double jumla = 1.00;
        Double jami;

        if (!narhTextField.getText().isEmpty()) {
            narh = Double.parseDouble(spaceDelete(narhTextField.getText()));
        }
        if (!donaTextField.getText().isEmpty())
            dona = Double.parseDouble(spaceDelete(donaTextField.getText()));
        if (jumlaComboBox.getSelectionModel().getSelectedIndex() == 0) {
            jumla = Double.parseDouble(spaceDelete(tovarDonaLabel.getText()));
        }
        jami = dona*narh;
        jamiTovarNarhi.setText(decimalFormat.format(jami));
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public void setCancelButton(Button cancelButton) {
        this.cancelButton = cancelButton;
    }

    public void gridSetDisable(Boolean disable) {
        tovarHBox.setDisable(disable);
        valutaComboBox.setDisable(disable);
        valutaButton.setDisable(disable);
        jumlaComboBox.setDisable(disable);
        kursTextField.setDisable(disable);
        donaTextField.setDisable(disable);
        narhTextField.setDisable(disable);
        chiqimShakliHBox.setDisable(disable);
        barCodeTextField.setDisable(disable);
        tovarDonaLabel.setDisable(disable);
    }
    public Boolean isNumeric(String string) {
        boolean numeric = true;
        try {
            Integer num = Integer.parseInt(string);
        } catch (NumberFormatException e) {
            numeric = false;
        }
        return numeric;
    }

    public Button getQaydEtButton() {
        return qaydEtButton;
    }

    public void tovarAction(BarCode barCode) {
        Standart tovar = GetDbData.getTovar(barCode.getTovar());
        if (tovar != null) {
            tovarId = tovar.getId();
            tovarNomiLabel.setText(tovar.getText());
        }
        Standart birlik = GetDbData.getBirlik(barCode.getBirlik());
        if (birlik != null) {
            tovarBirlikLabel.setText(birlik.getText());
        }
        tovarDonaLabel.setText(String.valueOf(barCode.getAdad()));
        Double zaxira = hisobKitobModels.getBarCodeCount(connection, qaydnomaData.getKirimId(), barCode.getBarCode());
        zaxiradaLabel.setText(decimalFormat.format(zaxira));
    }

    private void valutaAction(Valuta valuta) {
        if (valuta.getStatus() == 1) {
            kursTextField.setText("1");
        } else {
            KursModels  kursModels  = new KursModels();
            ObservableList<Kurs>  kurslar = kursModels.getDate(connection, valuta.getId(), qaydnomaData.getSana(), "sana DESC");
            if (kurslar.size()>0) {
                kursTextField.setText(kurslar.get(0).getKurs().toString());
            } else {
                kursTextField.setText("0");
            }
        }
        zaxiradaLabel.setText(decimalFormat.format(hisobKitobModels.getValutaBalans(connection, hisobKitob.getHisob1(), valuta)));
    }
}