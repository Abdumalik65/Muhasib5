package sample.Temp;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import sample.Config.MySqlDBGeneral;
import sample.Controller.ConvertController;
import sample.Data.*;
import sample.Enums.ServerType;
import sample.Model.HisobKitobModels;
import sample.Model.QaydnomaModel;
import sample.Tools.*;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class QaydAndoza extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    SplitPane centerPane = new SplitPane();
    VBox rightPane = new VBox();
    VBox leftPane = new VBox();
    Tugmachalar chapTugmalar;
    TableView<QaydnomaData> leftTable;
    GridPane gridPane;
    PulBox pulBox1;
    PulBox pulBox2;
    TextField mablag1TextField;
    TextField mablag2TextField;
    TextField kurs1TextField;
    TextField kurs2TextField;
    Button qulf;
    ImageView ochiq = new PathToImageView("/sample/images/Icons/ochiq.png", 32, 32).getImageView();
    ImageView yopiq = new PathToImageView("/sample/images/Icons/yopiq.png", 32, 32).getImageView();
    TextArea textArea;

    Connection connection;
    User user;
    int padding = 3;
    Integer amal = 16;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    DecimalFormat decimalFormat = new MoneyShow();
    Font font = Font.font("Arial", FontWeight.BOLD,40);
    ObservableList<HisobKitob> observableList;

    public static void main(String[] args) {
        launch(args);
    }

    public QaydAndoza() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
        ibtido();
    }

    public QaydAndoza(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        ibtido();
    }

    private void ibtido() {
        initTopPane();
        initLeftPane();
        initCenterPane();
        initRightPane();
        initBottomPane();
        initBorderPane();
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
        gridPane = gridPane();
        chapTugmalar = initchapTugmalar();
        leftTable = initLeftTable();
        VBox.setVgrow(leftTable, Priority.ALWAYS);
        leftPane.getChildren().addAll(chapTugmalar, leftTable);
    }

    private Tugmachalar initchapTugmalar() {
        Tugmachalar tugmachalar = new Tugmachalar();
        ObservableList<Standart> comboData = FXCollections.observableArrayList();
        comboData.add(new Standart(1, "Hujjat №", null, null));
        comboData.add(new Standart(2, "Sana", null, null));
        comboData.add(new Standart(3, "To`lovchi", null, null));
        comboData.add(new Standart(4, "Oluvchi", null, null));
        comboData.add(new Standart(5, "Izoh", null, null));
        ComboBox<Standart> comboBox = new ComboBox<>(comboData);
        comboBox.getSelectionModel().selectFirst();
        TextField textField = new TextField();
        HBox.setHgrow(textField, Priority.ALWAYS);
        textField.setPromptText("QIDIR");
        tugmachalar.getChildren().addAll(comboBox, textField);

        Button add = tugmachalar.getAdd();
        add.setOnAction(event -> {
            ConvertController convertController = new ConvertController(connection, user);
            QaydnomaData qaydnomaData = convertController.display();
            if (qaydnomaData != null) {
                leftTable.getItems().add(qaydnomaData);
                ObservableList<QaydnomaData> observableList = leftTable.getItems();
                Collections.sort(observableList, Comparator.comparing(QaydnomaData::getSana).reversed());
                leftTable.getSelectionModel().select(qaydnomaData);
                leftTable.scrollTo(qaydnomaData);
                leftTable.requestFocus();
                leftTable.refresh();
            }
        });

        Button delete = tugmachalar.getDelete();
        delete.setOnAction(event -> {
            HisobKitobModels hisobKitobModels = new HisobKitobModels();
            QaydnomaModel qaydnomaModel = new QaydnomaModel();
            QaydnomaData q = leftTable.getSelectionModel().getSelectedItem();
            if (q != null) {
                if (Alerts.haYoq(q.getId() + " raqamli qayd va unga tegishli ma`lumotlar o`chiriladi", "Davom etamizmi")) {
                    hisobKitobModels.deleteWhere(connection, "qaydId = " + q.getId());
                    qaydnomaModel.delete_data(connection, q);
                    leftTable.getItems().remove(q);
                    leftTable.refresh();
                }
            }
        });
        return tugmachalar;
    }

    private void initCenterPane() {
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        VBox.setVgrow(centerPane, Priority.ALWAYS);
        centerPane.getItems().addAll(leftPane, rightPane);
    }

    private void initRightPane() {
        rightPane.setPadding(new Insets(padding));
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        VBox.setVgrow(rightPane, Priority.ALWAYS);
        rightPane.getChildren().add(gridPane);
    }

    private void initBottomPane() {}

    private void initBorderPane() {
        borderpane.setCenter(centerPane);
    }

    private void initStage(Stage primaryStage) {
        scene = new Scene(borderpane);
        scene.setUserAgentStylesheet("sample/Styles/caspian.css");
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        stage = primaryStage;
        stage.setTitle("Qaydlar ro`yxati");
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        stage.setScene(scene);
    }

    private TableView initLeftTable() {
        TableView<QaydnomaData> tableView =  new TableView<>();
        tableView.getColumns().addAll(getSanaColumn(), getIdColumn(), getChiqimIdColumn());
        ObservableList<QaydnomaData> qaydnomaDataObservableList = getLeftTableData();
        if (qaydnomaDataObservableList.size()>0) {
            QaydnomaData qaydnomaData= qaydnomaDataObservableList.get(0);
            tableView.getSelectionModel().select(qaydnomaData);
            tableView.scrollTo(qaydnomaData);
            tableView.requestFocus();
            observableList = amalniYukla(qaydnomaData);
        }
        tableView.setItems(qaydnomaDataObservableList);
        tableView.getSelectionModel().selectedItemProperty().addListener((observable ,oldValue, newValue)->{
            if (newValue != null) {
                disable(true);
                gridPane.getChildren().remove(qulf);
                qulf = qulfniOchTugmasi();
                gridPane.add(qulf, 3, 0, 1, 2);
                observableList = amalniYukla(newValue);
            } else {

            }
        });
        return  tableView;
    }

    private TextArea yangiTextArea() {
        TextArea textArea = new TextArea();
        SetHVGrow.VerticalHorizontal(textArea);
        textArea.setPadding(new Insets(padding));
        return textArea;
    }

    private TextField mablag1TextField() {
        TextField textField = new TextField();
        textField.setDisable(true);
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
            }
        });
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (StringNumberUtils.isNumeric(mablag1TextField.getText())) {
                Double a1 = StringNumberUtils.getDoubleFromTextField(textField);
                Double a2;
                Double b1 = StringNumberUtils.getDoubleFromTextField(kurs1TextField);
                Double b2 = StringNumberUtils.getDoubleFromTextField(kurs2TextField);
                a2 = a1 * b2 / b1;
                mablag2TextField.setText(decimalFormat.format(a2));
            }
        });
        return textField;
    }
    private TextField mablag2TextField() {
        TextField textField = new TextField();
        textField.setDisable(true);
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
            }
        });
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (StringNumberUtils.isNumeric(mablag2TextField.getText())) {
                Double a1;
                Double a2 = StringNumberUtils.getDoubleFromTextField(textField);
                Double b1 = StringNumberUtils.getDoubleFromTextField(kurs1TextField);
                Double b2 = StringNumberUtils.getDoubleFromTextField(kurs2TextField);
                a1 = a2 * b1 / b2;
                mablag1TextField.setText(decimalFormat.format(a1));
            }
        });
        return textField;
    }
    private TextField kurs1TextField() {
        TextField textField = new TextField();
        textField.setDisable(true);
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Valuta valuta = pulBox1.getValuta();
                if (valuta.getStatus().equals(1)) {
                    newValue = oldValue;
                }
            }
        });
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (StringNumberUtils.isNumeric(mablag1TextField.getText())) {
                Double a1 = null;
                Double a2 = StringNumberUtils.getDoubleFromTextField(mablag2TextField);
                Double b1 = null;
                Valuta valuta = pulBox2.getValuta();
                if (valuta.getStatus().equals(1)) {
                    b1 = 1d;
                    textField.setText("1");
                } else {
                    b1 = StringNumberUtils.getDoubleFromTextField(textField);
                }
                Double b2 = StringNumberUtils.getDoubleFromTextField(kurs2TextField);
                a1 = a2 * b1 / b2;
                mablag1TextField.setText(decimalFormat.format(a1));
            }
        });
        return textField;
    }
    private TextField kurs2TextField() {
        TextField textField = new TextField();
        textField.setDisable(true);
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Valuta valuta = pulBox2.getValuta();
                if (valuta.getStatus().equals(1)) {
                    newValue = oldValue;
                }
            }
        });
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (StringNumberUtils.isNumeric(textField.getText())) {
                    Double a1 = StringNumberUtils.getDoubleFromTextField(mablag1TextField);
                    Double a2 = null;
                    Double b1 = StringNumberUtils.getDoubleFromTextField(kurs1TextField);
                    Double b2 = null;
                    Valuta valuta = pulBox2.getValuta();
                    if (valuta.getStatus().equals(1)) {
                        b2 = 1d;
                        textField.setText("1");
                    } else {
                        b2 = StringNumberUtils.getDoubleFromTextField(textField);
                    }
                    a2 = a1 * b2 / b1;
                    mablag2TextField.setText(decimalFormat.format(a2));
                    textField.setText(decimalFormat.format(b2));
                }
            }
        });
        return textField;
    }

    private ObservableList<QaydnomaData> getLeftTableData() {
        ObservableList<QaydnomaData> observableList = FXCollections.observableArrayList();
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        String select = "SELECT qaydId, amal, datetime, hisob1, hisob2 FROM hisobKitob where amal = 16 group by qaydid order by datetime desc;";
        ResultSet resultSet = hisobKitobModels.getResultSet(connection, select);
        try {
            while (resultSet.next()) {
                Integer qaydId = resultSet.getInt(1);
                Integer hisob2 = resultSet.getInt(2);
                Boolean yangiQayd = true;
                for (QaydnomaData q: observableList) {
                    if (q.getId().equals(qaydId)) {
                        yangiQayd = false;
                        break;
                    }
                }
                if (yangiQayd) {
                    QaydnomaData qaydnomaData = new QaydnomaData(
                            resultSet.getInt(1),
                            resultSet.getInt(2),
                            0,
                            sdf.parse(resultSet.getString(3)),
                            resultSet.getInt(4),
                            "",
                            resultSet.getInt(5),
                            "",
                            "",
                            0d,
                            0,
                            user.getId(),
                            null
                    );
                    observableList.add(qaydnomaData);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return observableList;
    }

    public TableColumn<QaydnomaData, Date> getSanaColumn() {
        TableColumn<QaydnomaData, Date> sanaColumn = new TableColumn("Sana");
        sanaColumn.setMinWidth(80);
        sanaColumn.setCellValueFactory(new PropertyValueFactory<>("sana"));
        sanaColumn.setCellFactory(column -> {
            TableCell<QaydnomaData, Date> cell = new TableCell<QaydnomaData, Date>() {
                private SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy\n  HH:mm:ss");
                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        setText(format.format(item));
                    }
                }
            };
            cell.setAlignment(Pos.TOP_CENTER);

            return cell;
        });
        return sanaColumn;
    }
    public TableColumn<QaydnomaData, Integer> getIdColumn() {
        TableColumn<QaydnomaData, Integer> idColumn = new TableColumn("N");
        idColumn.setMinWidth(30);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        return idColumn;
    }
    public TableColumn<QaydnomaData, Integer> getChiqimIdColumn() {
        TableColumn<QaydnomaData, Integer> chiqimIdColumn = new TableColumn("Chiqim N");
        chiqimIdColumn.setMinWidth(30);
        chiqimIdColumn.setCellValueFactory(new PropertyValueFactory<>("chiqimId"));
        chiqimIdColumn.setCellFactory(column -> {
            TableCell<QaydnomaData, Integer> cell = new TableCell<QaydnomaData, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        Hisob hisob = GetDbData.getHisob(item);
                        if (hisob != null) {
                            setText(hisob.getText());
                        } else {
                            setText("");
                        }
                    }
                }
            };
//            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        return chiqimIdColumn;
    }
    public TableColumn<QaydnomaData, Integer> getKirimIdColumn() {
        TableColumn<QaydnomaData, Integer> kirimIdColumn = new TableColumn("Kirim N");
        kirimIdColumn.setMinWidth(30);
        kirimIdColumn.setCellValueFactory(new PropertyValueFactory<>("kirimId"));
        kirimIdColumn.setCellFactory(column -> {
            TableCell<QaydnomaData, Integer> cell = new TableCell<QaydnomaData, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        Hisob hisob = GetDbData.getHisob(item);
                        if (hisob != null) {
                            setText(hisob.getText());
                        } else {
                            setText("");
                        }
                    }
                }
            };
//            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        return kirimIdColumn;
    }
    public TableColumn<QaydnomaData, Integer> getAmalTuriColumn() {
        TableColumn<QaydnomaData, Integer> amalTuriColumn = new TableColumn("Amal turi");
        amalTuriColumn.setMinWidth(30);
        amalTuriColumn.setCellValueFactory(new PropertyValueFactory<>("amalTuri"));
        amalTuriColumn.setCellFactory(column -> {
            TableCell<QaydnomaData, Integer> cell = new TableCell<QaydnomaData, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        Standart amal = GetDbData.getAmal(item);
                        if (amal != null) {
                            setText(amal.getText());
                        } else {
                            setText("");
                        }
                    }
                }
            };
//            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        return amalTuriColumn;
    }

    private Button ochirishTugmasi() {
        Button button = new Button();
        SetHVGrow.VerticalHorizontal(button);
        button.setPrefHeight(10);
        button.setMaxHeight(500);
        button.setPrefWidth(10);
        button.setMaxWidth(500);
        InputStream inputStream = getClass().getResourceAsStream("/sample/images/Icons/delete.png");
        Image image = new Image(inputStream);
        ImageView imageView = new PathToImageView("/sample/images/Icons/delete.png", 24, 24).getImageView();
        button.setGraphic(imageView);
        button.setOnAction(event -> {
            QaydnomaData qaydnomaData = leftTable.getSelectionModel().getSelectedItem();
            HisobKitobModels hisobKitobModels = new HisobKitobModels();
            QaydnomaModel qaydnomaModel = new QaydnomaModel();
            if (qaydnomaData != null) {
                if (Alerts.haYoq(qaydnomaData.getId() + " raqamli qayd va unga tegishli ma`lumotlar o`chiriladi", "Davom etamizmi")) {
                    hisobKitobModels.deleteWhere(connection, "qaydId = " + qaydnomaData.getId());
                    qaydnomaModel.delete_data(connection, qaydnomaData);
                    leftTable.getItems().remove(qaydnomaData);
                    leftTable.refresh();
                }
            }


        });
        return button;
    }

    private Button ozgartirishTugmasi() {
        Button button = new Button();
        SetHVGrow.VerticalHorizontal(button);
        button.setPrefHeight(10);
        button.setMaxHeight(500);
        button.setPrefWidth(10);
        button.setMaxWidth(500);
        ImageView imageView = new PathToImageView("/sample/images/Icons/floppy.png", 32, 32).getImageView();
        button.setGraphic(imageView);
        button.setOnAction(event -> {
            QaydnomaData qaydnomaData = leftTable.getSelectionModel().getSelectedItem();
            if (qaydnomaData != null) {
                amalniSaqla(qaydnomaData);
            }
        });
        return button;
    }

    private Button qulfniOchTugmasi() {
        Button button = new Button();
        SetHVGrow.VerticalHorizontal(button);
        button.setPrefHeight(10);
        button.setMaxHeight(500);
        button.setPrefWidth(10);
        button.setMaxWidth(500);
        button.setGraphic(yopiq);
        button.setOnAction(event -> {
            disable(false);
            gridPane.getChildren().remove(qulf);
            qulf = qulfniYopTugmasi();
            gridPane.add(qulf, 3, 0, 1, 2);
        });
        return button;
    }

    private Button qulfniYopTugmasi() {
        Button button = new Button();
        SetHVGrow.VerticalHorizontal(button);
        button.setPrefHeight(10);
        button.setMaxHeight(500);
        button.setPrefWidth(10);
        button.setMaxWidth(500);
        button.setGraphic(ochiq);
        button.setOnAction(event -> {
            disable(true);
            gridPane.getChildren().remove(qulf);
            qulf = qulfniOchTugmasi();
            gridPane.add(qulf, 3, 0, 1, 2);
        });
        return button;
    }

    private PulBox yangiPulBox() {
        PulBox pulBox = new PulBox(connection, user);
        return pulBox;
    }

    private GridPane gridPane() {
        GridPane gridPane = new GridPane();
        Integer rowIndex = 0;

        pulBox1 = new PulBox(connection, user);
        pulBox1.getTextField().setDisable(true);
        pulBox1.getPlusButton().setDisable(true);
        mablag1TextField = mablag1TextField();
        kurs1TextField = kurs1TextField();

        pulBox2 = new PulBox(connection, user);
        pulBox2.getTextField().setDisable(true);
        pulBox2.getPlusButton().setDisable(true);
        mablag2TextField = mablag2TextField();
        kurs2TextField = kurs2TextField();
        Button button1 = ozgartirishTugmasi();
        Button button2 = ochirishTugmasi();
        Button qulf = qulfniOchTugmasi();

        gridPane.add(pulBox1, 0, rowIndex, 1, 1);
        gridPane.add(mablag1TextField, 1, rowIndex, 1, 1);
        gridPane.add(kurs1TextField, 2, rowIndex, 1, 1);
        GridPane.setHgrow(pulBox1, Priority.ALWAYS);
        GridPane.setHgrow(mablag1TextField, Priority.ALWAYS);
        GridPane.setHgrow(kurs1TextField, Priority.ALWAYS);
        GridPane.setHgrow(pulBox1, Priority.ALWAYS);

        rowIndex++;
        gridPane.add(pulBox2, 0, rowIndex, 1, 1);
        gridPane.add(mablag2TextField, 1, rowIndex, 1, 1);
        gridPane.add(kurs2TextField, 2, rowIndex, 1, 1);
        gridPane.add(qulf, 3, 0, 1, 2);
        gridPane.add(button1, 4, 0, 1, 2);
        gridPane.add(button2, 5, 0, 1, 2);

        rowIndex++;
        textArea = yangiTextArea();
        gridPane.add(textArea, 0, rowIndex, 6, 1);

        return gridPane;
    }

    private void disable(Boolean disable) {
        mablag1TextField.setDisable(disable);
        mablag2TextField.setDisable(disable);
        if (pulBox1.getValuta().getStatus().equals(1)) {
            kurs1TextField.setDisable(true);
        } else {
            kurs1TextField.setDisable(disable);
        }
        if (pulBox2.getValuta().getStatus().equals(1)) {
            kurs2TextField.setDisable(true);
        } else {
            kurs2TextField.setDisable(disable);
        }
    }

    private ObservableList<HisobKitob> amalniYukla(QaydnomaData qaydnomaData) {
        DecimalFormat decimalFormat = new MoneyShow();
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        ObservableList<HisobKitob> observableList = hisobKitobModels.getAnyData(connection, "qaydId = " + qaydnomaData.getId(), "");
        HisobKitob hisobKitob;
        HisobKitob hisobKitob1;
        HisobKitob hisobKitob2;
        Boolean yangi = false;
        switch (observableList.size()) {
            case 0:
                Alerts.AlertString(qaydnomaData.getId() + " raqamli pul ayriboshlash amali to`liq emas!!!");
                return observableList;
            case 1:
                Alerts.AlertString(qaydnomaData.getId() + " raqamli pul ayriboshlash amali to`liq emas!!!");
                return observableList;
            case 2:
                yangi = true;
                hisobKitob = new HisobKitob(
                        0, qaydnomaData.getId(), qaydnomaData.getHujjat(), qaydnomaData.getAmalTuri(), qaydnomaData.getChiqimId(), qaydnomaData.getKirimId(),
                        0, 0, 0d, "", 0d, 0d, 0, qaydnomaData.getIzoh(), qaydnomaData.getUserId(), qaydnomaData.getSana());
                observableList.add(0, hisobKitob);
                break;
            case 3:
                break;
        }
        hisobKitob = observableList.get(0);
        hisobKitob1 = observableList.get(1);
        hisobKitob.setHisob1(hisobKitob1.getHisob1());
        hisobKitob.setValuta(hisobKitob1.getValuta());
        hisobKitob.setKurs(hisobKitob1.getKurs());
        hisobKitob2 = observableList.get(2);

        Valuta valuta1 = GetDbData.getValuta(hisobKitob1.getValuta());
        pulBox1.setValuta(valuta1);
        if (valuta1.getStatus().equals(1)) {
            kurs1TextField.setDisable(true);
        }
        pulBox1.getTextField().setText("KIRIM " + valuta1.getValuta());
        mablag1TextField.setText(decimalFormat.format(hisobKitob1.getNarh()));
        kurs1TextField.setText(decimalFormat.format(hisobKitob1.getKurs()));

        Valuta valuta2 = GetDbData.getValuta(hisobKitob2.getValuta());
        if (valuta2.getStatus().equals(1)) {
            kurs2TextField.setDisable(true);
        }
        pulBox2.setValuta(valuta2);
        pulBox2.getTextField().setText("CHIQIM " + valuta2.getValuta());
        mablag2TextField.setText(decimalFormat.format(hisobKitob2.getNarh()));
        kurs2TextField.setText(decimalFormat.format(hisobKitob2.getKurs()));

        textArea.setText(hisobKitob.getIzoh());

        return observableList;
    }

    private void amalniSaqla(QaydnomaData qaydnomaData) {
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
//        ObservableList<HisobKitob> observableList = hisobKitobModels.getAnyData(connection, "qaydId = " + qaydnomaData.getId(), "sana desc");
        HisobKitob hisobKitob;
        HisobKitob hisobKitob1;
        HisobKitob hisobKitob2;
        Boolean yangi = false;
        switch (observableList.size()) {
            case 0:
                Alerts.AlertString(qaydnomaData.getId() + " raqamli pul ayriboshlash amali to`liq emas!!!");
                return;
            case 1:
                Alerts.AlertString(qaydnomaData.getId() + " raqamli pul ayriboshlash amali to`liq emas!!!");
                return;
            case 2:
                yangi = true;
                hisobKitob = new HisobKitob(
                        0, qaydnomaData.getId(), qaydnomaData.getHujjat(), qaydnomaData.getAmalTuri(), qaydnomaData.getChiqimId(), qaydnomaData.getKirimId(),
                        0, 0, 0d, "", 0d, 0d, 0, qaydnomaData.getIzoh(), qaydnomaData.getUserId(), qaydnomaData.getSana());
                observableList.add(0, hisobKitob);
                break;
            case 3:
                break;
        }
        hisobKitob = observableList.get(0);
        hisobKitob1 = observableList.get(1);
        hisobKitob.setHisob1(hisobKitob1.getHisob1());
        hisobKitob.setValuta(hisobKitob1.getValuta());
        hisobKitob.setKurs(hisobKitob1.getKurs());
        hisobKitob2 = observableList.get(2);

        Valuta valuta1 = pulBox1.getValuta();
        hisobKitob1.setValuta(valuta1.getId());
        hisobKitob1.setNarh(StringNumberUtils.getDoubleFromTextField(mablag1TextField));
        hisobKitob1.setKurs(StringNumberUtils.getDoubleFromTextField(kurs1TextField));

        Valuta valuta2 = pulBox2.getValuta();
        hisobKitob2.setValuta(valuta2.getId());
        hisobKitob2.setNarh(StringNumberUtils.getDoubleFromTextField(mablag2TextField));
        hisobKitob2.setKurs(StringNumberUtils.getDoubleFromTextField(kurs2TextField));

        hisobKitob.setIzoh(textArea.getText().trim());

        if (yangi) {
            hisobKitobModels.insert(connection, hisobKitob);
        } else {
            hisobKitobModels.update_data(connection, hisobKitob);
        }
        hisobKitobModels.update_data(connection, hisobKitob1);
        hisobKitobModels.update_data(connection, hisobKitob2);
    }
}
