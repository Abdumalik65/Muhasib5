package sample.Controller;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import sample.Config.MySqlDBLocal;
import sample.Data.Hisob;
import sample.Data.HisobKitob;
import sample.Data.QaydnomaData;
import sample.Data.User;
import sample.Model.HisobKitobModels;
import sample.Model.HisobModels;
import sample.Model.QaydnomaModel;
import sample.Tools.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class HisobotSochma extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    SplitPane centerPane = new SplitPane();
    VBox rightPane = new VBox();
    VBox leftPane = new VBox();
    HBox leftHBox = new HBox();

    TextField qidirTextField = new TextField();

    TableView<Hisob> hisobTableView = new TableView<>();
    TableView<HisobKitob> hisobKitobTableView = new TableView<>();
    GetTableView2 getTableView2 = new GetTableView2();

    ObservableList<Hisob> hisobObservableList;
    ObservableList<HisobKitob> hisobKitobObservableList = FXCollections.observableArrayList();
    ObservableList<HisobKitob> rightObservableList = FXCollections.observableArrayList();
    ObservableList<QaydnomaData> qaydnomaDataObservableList = FXCollections.observableArrayList();

    HisobKitobModels hisobKitobModels = new HisobKitobModels();
    QaydnomaModel qaydnomaModel = new QaydnomaModel();
    HisobModels hisobModels = new HisobModels();
    Hisob hisob;
    HisobKitob hisobKitob;

    Connection connection;
    User user;

    int padding = 3;
    HBox jamiHBox = new HBox();
    Label jamiLabel = new Label();
    Font font = Font.font("Arial", FontWeight.BOLD,20);
    Button hisoblarToExcelButton = new Button("");
    Button hisobToExcelButton = new Button("");
    DatePicker datePicker;
    LocalDate localDate;


    public static void main(String[] args) {
        launch(args);
    }

    public HisobotSochma() {
        connection = new MySqlDBLocal().getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
        ibtido();
    }

    public HisobotSochma(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        ibtido();
    }

    private void ibtido() {
        initDataYangi();
        getTableView2.initTableViews();
        initHisobTableView();
        initButtons();
        initTextFields();
        initLeftHBox();
        initLeftPane();
        initHisobKitobTable();
        initRightPane();
        initCenterPane();
        initBorderPane();
    }

    private void initDataYangi() {
        initDatePicker();
        hisobObservableList  = hisobModels.get_data1(connection);
        if (hisobObservableList.size()>0) {
            hisob = hisobObservableList.get(0);
            Date date = null;
            LocalDateTime localDateTime = LocalDateTime.of(localDate, LocalTime.of(23,59,59));
            String select1 = "dateTime <= '" + localDateTime + "' and hisob1 = " + hisob.getId();
            String select2 = "dateTime <= '" + localDateTime + "' and hisob2 = " + hisob.getId();
            ObservableList<HisobKitob> hisob1List = hisobKitobModels.getAnyData(connection, select1, "dateTime");
            if (hisob1List.size()>0) {
                hisobKitobObservableList.addAll(hisob1List);
            }
            ObservableList<HisobKitob> hisob2List = hisobKitobModels.getAnyData(connection, select2, "dateTime");
            if (hisob2List.size()>0) {
                hisobKitobObservableList.addAll(hisob2List);
            }
            if (hisobKitobObservableList.size()>0) {
                Collections.sort(hisobKitobObservableList, Comparator.comparingInt(HisobKitob::getId));
            }
        }
        qaydnomaDataObservableList = qaydnomaModel.get_data(connection);
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

    private void initButtons() {
        hisoblarToExcelButton.setGraphic(new PathToImageView("/sample/images/Icons/excel.png").getImageView());
        hisobToExcelButton.setGraphic(new PathToImageView("/sample/images/Icons/excel.png").getImageView());

        hisoblarToExcelButton.setOnAction(event -> {
            ExportToExcel exportToExcel = new ExportToExcel();
            exportToExcel.hisobMufassal(connection);
        });

        hisobToExcelButton.setOnAction(event -> {
            ExportToExcel exportToExcel = new ExportToExcel();
            exportToExcel.hisob(hisob.getId(), rightObservableList);
        });
    }

    private void initTextFields() {
        HBox.setHgrow(qidirTextField, Priority.ALWAYS);
        TextFields.bindAutoCompletion(qidirTextField, hisobObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Hisob> autoCompletionEvent) -> {
            Hisob hisob = autoCompletionEvent.getCompletion();
            hisobTableView.getSelectionModel().select(hisob);
            hisobTableView.scrollTo(hisob);
            hisobTableView.requestFocus();
        });
    }

    private void initLeftHBox() {
        leftHBox.setPadding(new Insets(padding));
        HBox.setHgrow(leftHBox, Priority.ALWAYS);
        leftHBox.getChildren().addAll(hisoblarToExcelButton, qidirTextField, datePicker);
    }

    private void initLeftPane() {
        leftPane.setPadding(new Insets(padding));
        HBox.setHgrow(leftPane, Priority.ALWAYS);
        VBox.setVgrow(leftPane, Priority.ALWAYS);
        initJamiHBox();
        leftPane.getChildren().addAll(leftHBox, hisobTableView, jamiHBox);
        leftPane.setMinWidth(280);
        leftPane.setMaxWidth(280);

    }

    private void initHisobTableView() {
        hisobTableView = getTableView2.getHisobTableView();
        HBox.setHgrow(hisobTableView, Priority.ALWAYS);
        VBox.setVgrow(hisobTableView, Priority.ALWAYS);
        hisobTableView.getColumns().get(1).setMinWidth(150);
        hisobTableView.getColumns().get(1).setMaxWidth(150);
        hisobTableView.setItems(hisobObservableList);
        if (hisobObservableList.size()>0) {
            hisob = hisobObservableList.get(0);
            refreshHisobKitobTableYangi(hisob);
            hisobTableView.getSelectionModel().selectFirst();
        }
        hisobTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                hisob = newValue;
                refreshHisobKitobTableYangi(hisob);
            }
        });
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
        localDate = localDate.now();
        datePicker =  new DatePicker(localDate);

        datePicker.setConverter(converter);
        datePicker.setMaxWidth(2000);
        datePicker.setMinWidth(115);
        datePicker.setPrefWidth(150);
        HBox.setHgrow(datePicker, Priority.ALWAYS);

        datePicker.setOnAction(event -> {
            LocalDate newDate = datePicker.getValue();
            if (newDate != null) {
                localDate = newDate;
                localDate = newDate;
                Date date = null;
                LocalDateTime localDateTime = LocalDateTime.of(localDate, LocalTime.of(23,59,59));
                Instant instant = Instant.from(localDateTime.atZone(ZoneId.systemDefault()));
                date = Date.from(instant);
                hisobObservableList = hisobModels.get_data1(connection, date);
                hisobTableView.setItems(hisobObservableList);
                hisobTableView.refresh();
            }
        });
    }

    private void refreshHisobKitobTable(Hisob hisob) {
        ObservableList<HisobKitob> balansList = FXCollections.observableArrayList();
        rightObservableList.removeAll(rightObservableList);
        if (hisob != null) {
            rightObservableList.addAll(hisobKitobModels.getAnyData(connection, "hisob1 = " + hisob.getId() + " OR hisob2 = " + hisob.getId(), "id asc"));
            setDateTime();
            Comparator<HisobKitob> comparator = Comparator.comparing(HisobKitob::getDateTime);
            Collections.sort(rightObservableList, comparator);
            hkKirimChiqim(hisob);
        }
        hisobKitobTableView.setItems(rightObservableList);
        hisobKitobTableView.refresh();
    }

    private void refreshHisobKitobTableYangi(Hisob hisob) {
        rightObservableList.clear();
        if (hisob != null) {
            LocalDateTime localDateTime = LocalDateTime.of(localDate, LocalTime.of(23,59,59));
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String joriySanaVaqt = dateTimeFormatter.format(localDateTime);
            String select1 = "dateTime <= '" + joriySanaVaqt + "' and hisob1 = " + hisob.getId();
            String select2 = "dateTime <= '" + joriySanaVaqt + "' and hisob2 = " + hisob.getId();
            ObservableList<HisobKitob> kirimList = hisobKitobModels.getAnyData(connection, select2, "");
            ObservableList<HisobKitob> chiqimList = hisobKitobModels.getAnyData(connection, select1, "");
            rightObservableList.addAll(kirimList);
            rightObservableList.addAll(chiqimList);
            Comparator<HisobKitob> comparator = Comparator.comparing(HisobKitob::getDateTime);
            Collections.sort(rightObservableList, comparator);
            for (HisobKitob hk: rightObservableList) {
                if (hk.getHisob1().equals(hisob.getId()) || hk.getHisob2().equals(hisob.getId())) {
                    Date date = getQaydDate(hk.getQaydId());
                    if (date != null) {
                        hk.setDateTime(date);
                    }
                }
            }
            hkKirimChiqim(hisob);
        }
        hisobKitobTableView.setItems(rightObservableList);
        hisobKitobTableView.refresh();
    }

    private void setDateTime() {
        for (HisobKitob hk: rightObservableList) {
            hk.setDateTime(getQaydDate(hk.getQaydId()));
            if (hk.getDateTime()==null) {
                System.out.println("NULL  " + hk.getIzoh() + " | " + hk.getQaydId() + " | " + hk.getId());
    //            hisobKitobModels.update_data(connection, hk);
            }
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

    private void initHisobKitobTable() {
        getTableView2.getAdadColumn().setMinWidth(80);
        TableColumn<HisobKitob, Integer> amalColumn = getTableView2.getAmalColumn();
        amalColumn.setStyle( "-fx-alignment: CENTER;");

        TableColumn<HisobKitob, Integer> valutaColumn = getTableView2.getValutaColumn();
        valutaColumn.setStyle( "-fx-alignment: CENTER;");

        hisobKitobTableView.getColumns().addAll(getTableView2.getDateTimeColumn(), getCustom2Column(), amalColumn,
                getTableView2.getIzoh2Column(), valutaColumn, getTableView2.getKursColumn(),
                getTableView2.getAdadColumn(), getTableView2.getNarhColumn(), getTableView2.getSummaColumn(),
                getTableView2.getBalans2Column());
        HBox.setHgrow(hisobKitobTableView, Priority.ALWAYS);
        VBox.setVgrow(hisobKitobTableView, Priority.ALWAYS);
        hisobKitobTableView.setItems(rightObservableList);
        hisobKitobTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                hisobKitob = newValue;
            }
        });
    }

    public TableColumn<HisobKitob, Integer> getQaydIdColumn() {
        TableColumn<HisobKitob, Integer> qaydId = new TableColumn<>("Sana");
        qaydId.setMinWidth(100);
        qaydId.setCellValueFactory(new PropertyValueFactory<>("qaydId"));
        qaydId.setCellFactory(column -> {
            TableCell<HisobKitob, Integer> cell = new TableCell<HisobKitob, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        for (QaydnomaData q: qaydnomaDataObservableList) {
                            if (q.getId().equals(item)) {
                                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                                setText(format.format(q.getSana()));
                                break;
                            }
                        }
                    }
                }
            };
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        qaydId.setStyle( "-fx-alignment: CENTER;");
        return qaydId;
    }

    private void initRightPane() {
        rightPane.setPadding(new Insets(padding));
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        VBox.setVgrow(rightPane, Priority.ALWAYS);
        rightPane.getChildren().addAll(hisobToExcelButton, hisobKitobTableView);
    }

    private void initCenterPane() {
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        VBox.setVgrow(centerPane, Priority.ALWAYS);
        centerPane.getItems().addAll(leftPane, rightPane);
    }

    private void initBorderPane() {
        borderpane.setCenter(centerPane);
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Sochma hisobot");
        scene = new Scene(borderpane);
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        stage.setX(bounds.getMinX() - 3);
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth() + 7);
        stage.setHeight(bounds.getHeight() + 6);
        stage.setResizable(false);
        stage.setScene(scene);
    }

    private double hisobBalans(int hisobId) {
        Double kirim = 0.0;
        Double chiqim = 0.0;
        Double balans = 0.0;
        ObservableList<HisobKitob> kirimObservableList;
        ObservableList<HisobKitob> chiqimObservableList;
        kirimObservableList = hisobKitobModels.getAnyData(connection, "hisob2 = " + hisobId, "");
        for (HisobKitob k: kirimObservableList) {
            Double jami = (k.getTovar() == 0 ? 1: k.getDona()) * k.getNarh()/k.getKurs();
            kirim += jami;
        }
        chiqimObservableList = hisobKitobModels.getAnyData(connection, "hisob1 = " + hisobId, "");
        for (HisobKitob ch: chiqimObservableList) {
            Double jami = (ch.getTovar() == 0 ? 1: ch.getDona()) * ch.getNarh()/ch.getKurs();
            chiqim +=  jami;
        }
        balans = kirim - chiqim;
        return balans;
    }

    private double hisobBalansYangi(int hisobId) {
        Double kirim = 0.0;
        Double chiqim = 0.0;
        Double balans = 0.0;
        ObservableList<HisobKitob> kirimObservableList;
        ObservableList<HisobKitob> chiqimObservableList;
        kirimObservableList = hisobKitobModels.get_data(connection);
        for (HisobKitob k: kirimObservableList) {
            Double jami = (k.getTovar() == 0 ? 1: k.getDona()) * k.getNarh()/k.getKurs();
            kirim += jami;
        }
        chiqimObservableList = hisobKitobModels.getAnyData(connection, "hisob1 = " + hisobId, "");
        for (HisobKitob ch: chiqimObservableList) {
            Double jami = (ch.getTovar() == 0 ? 1: ch.getDona()) * ch.getNarh()/ch.getKurs();
            chiqim +=  jami;
        }
        balans = kirim - chiqim;
        return balans;
    }

    public TableColumn<HisobKitob, Integer> getCustom2Column() {
        TableColumn<HisobKitob, Integer> integerTableColumn = new TableColumn<>("Kirim/\nChiqim");
        integerTableColumn.setMinWidth(100);
        integerTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, Integer>, ObservableValue<Integer>>() {

            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<HisobKitob, Integer> param) {
                HisobKitob hk = param.getValue();
                hisobKitob = hk;
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
                            if (hisob2==null) {
                                System.out.println("111111111>|>" + hisobKitob.getHisob2());
                            }
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

    public void hkKirimChiqim(Hisob hisob) {
        Double yigindi = .0;
        if (rightObservableList.size()>0) {
            hisobKitob = rightObservableList.get(0);
        }
        for (HisobKitob hk: rightObservableList) {
            if (hk.getHisob1().equals(hisob.getId())) {
                hk.setId(1);
            } else {
                hk.setId(2);
            }

            if (hk.getId() == 1) {
                yigindi -= hk.getSummaCol();
            } else {
                yigindi += hk.getSummaCol();
            }
            hk.setBalans(yigindi);
        }
    }

    private void initJamiHBox() {
        jamiHBox.setPadding(new Insets(padding));
        HBox.setHgrow(jamiHBox, Priority.ALWAYS);
        Label label = new Label("Jami ");
        Pane pane = new Pane();
        HBox.setHgrow(pane, Priority.ALWAYS);
        label.setFont(font);
        jamiLabel.setFont(font);
        double jami = 0.0;
        for (Hisob h: hisobObservableList) {
            jami += h.getBalans();
        }
        String jamiString = new MoneyShow().format(jami);
        if (jamiString.trim().equals("-0")) {
            jamiString = "0";
        }
        jamiLabel.setText(jamiString);
        jamiHBox.getChildren().addAll(label, pane, jamiLabel);
    }

}
