package sample.Temp;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import sample.Config.MySqlDBLocal;
import sample.Tools.GetDbData;
import sample.Tools.MoneyShow;
import sample.Data.*;
import sample.Model.*;
import sample.Tools.SetHVGrow;
import sample.Tools.Tugmachalar;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SanoqController extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    SplitPane centerPane = new SplitPane();
    VBox rightPane = new VBox();
    VBox leftPane = new VBox();

    TableView<SanoqQaydi> sanoqQaydiTableView = new TableView<>();

    Tugmachalar sanoqQaydiButtons = new Tugmachalar();
    TextField barCodeTextField = new TextField();

    ObservableList<SanoqQaydi> sanoqQaydiObservableList = FXCollections.observableArrayList();
    ObservableList<Standart> narhTuriObservableList;
    ObservableList<Standart> birlikObservableList;
    ObservableList<Hisob> hisobObservableList;

    int padding = 3;
    int amalTuri = 10;
    DecimalFormat decimalFormat = new MoneyShow();
    StringBuffer stringBuffer = new StringBuffer();

    HisobModels hisobModels = new HisobModels();
    StandartModels standartModels = new StandartModels();
    SanoqQaydiModels sanoqQaydiModels = new SanoqQaydiModels();

    SanoqQaydi sanoqQaydi;

    Connection connection;
    User user;


    public static void main(String[] args) {
        launch(args);
    }

    public SanoqController() {
        connection = new MySqlDBLocal().getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
    }

    public SanoqController(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ibtido();
        initStage(primaryStage);
        stage.show();
    }

    private void display() {
        stage = new Stage();
        ibtido();
        initStage(stage);
        stage.show();
    }

    private void ibtido() {
        initData();
        initLeftPane();
        initRightPane();
        initCenterPane();
        initBottomPane();
        initBorderPane();
    }

    private void initData() {
        hisobObservableList = GetDbData.getHisobObservableList();
        sanoqQaydiObservableList = sanoqQaydiModels.get_data(connection);
        standartModels.setTABLENAME("Birlik");
        birlikObservableList = standartModels.get_data(connection);
        standartModels.setTABLENAME("NarhTuri");
        narhTuriObservableList = standartModels.get_data(connection);
    }

    private void initLeftPane() {
        leftPane.setPadding(new Insets(padding));
        HBox.setHgrow(leftPane, Priority.ALWAYS);
        VBox.setVgrow(leftPane, Priority.ALWAYS);
        initTugmachalar();
        sanoqQaydiTableView = initSanoqQaydiTableView();
        leftPane.getChildren().addAll(sanoqQaydiButtons, sanoqQaydiTableView);
    }

    private void initTugmachalar() {
        sanoqQaydiButtons.getAdd().setOnAction(event -> {
            YangiSanoq yangiSanoq = new YangiSanoq(connection, user);
            yangiSanoq.display();
        });
        sanoqQaydiButtons.getDelete().setOnAction(event -> {});
        sanoqQaydiButtons.getEdit().setOnAction(event -> {});
    }


    private void initRightPane() {
        rightPane.setPadding(new Insets(padding));
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        VBox.setVgrow(rightPane, Priority.ALWAYS);
    }

    private void initCenterPane() {
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        VBox.setVgrow(centerPane, Priority.ALWAYS);
        centerPane.getItems().addAll(leftPane, rightPane);
    }

    private void initBottomPane() {
    }

    private void initBorderPane() {
        borderpane.setCenter(centerPane);
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        stage.setX(bounds.getMinX() - 3);
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth() + 7);
        stage.setHeight(bounds.getHeight() + 6);
        stage.setTitle("Tovarlar sanog`i");
        scene = new Scene(borderpane);
        stage.setScene(scene);
        stage.setResizable(false);
    }

    private TableView<SanoqQaydi> initSanoqQaydiTableView() {
        TableView<SanoqQaydi> sanoqQaydiTableView = new TableView<>();
        ObservableList<SanoqQaydi> sanoqQaydiObservableList = sanoqQaydiModels.get_data(connection);
        SetHVGrow.VerticalHorizontal(sanoqQaydiTableView);
        sanoqQaydiTableView.setPadding(new Insets(padding));
        sanoqQaydiTableView.getColumns().addAll(getIdColumn(), getSanaColumn(), getHisob1Column(), getHisob2Column(), getBalanceColumn());
        sanoqQaydiTableView.setItems(sanoqQaydiObservableList);
        sanoqQaydiTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                sanoqQaydi = newValue;
            }
        });
        return sanoqQaydiTableView;
    }

    private TableColumn<SanoqQaydi, Integer> getIdColumn() {
        TableColumn<SanoqQaydi, Integer> tovarColumn = new TableColumn<>("Id");
        tovarColumn.setMinWidth(20);
        tovarColumn.setMaxWidth(20);
        tovarColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        return tovarColumn;
    }

    private TableColumn<SanoqQaydi, Date> getSanaColumn() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy \nHH:mm:ss");
        TableColumn<SanoqQaydi, Date> sana = new TableColumn<>("Sana");
        sana.setMinWidth(80);
        sana.setMaxWidth(150);
        sana.setCellValueFactory(new PropertyValueFactory<>("sana"));
        sana.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Date>() {
            @Override
            public String toString(Date object) {
                return sdf.format(object);
            }

            @Override
            public Date fromString(String string) {
                string = string.replaceAll(" ", "");
                Date date = null;
                try {
                    date = sdf.parse(string);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return date;
            }
        }));
        sana.setStyle("-fx-alignment: CENTER;");
        return sana;
    }

    private TableColumn<SanoqQaydi, Integer> getHisob1Column() {
        TableColumn<SanoqQaydi, Integer> hisob1 = new TableColumn<>("Hisob1");
        hisob1.setMinWidth(100);
        hisob1.setMaxWidth(100);
        hisob1.setCellValueFactory(new PropertyValueFactory<>("hisob1"));
        return hisob1;
    }

    private TableColumn<SanoqQaydi, Integer> getHisob2Column() {
        TableColumn<SanoqQaydi, Integer> hisob2 = new TableColumn<>("Hisob2");
        hisob2.setMinWidth(200);
        hisob2.setMaxWidth(200);
        hisob2.setCellValueFactory(new PropertyValueFactory<>("hisob2"));
        return hisob2;
    }

    private TableColumn<SanoqQaydi, Integer> getBalanceColumn() {
        TableColumn<SanoqQaydi, Integer> balance = new TableColumn<>("Balans");
        balance.setMinWidth(200);
        balance.setMaxWidth(200);
        balance.setCellValueFactory(new PropertyValueFactory<>("balance"));
        return balance;
    }

    private TableColumn<SanoqQaydi, ComboBox<Standart>> getTaqdimColumn() {
        TableColumn<SanoqQaydi, ComboBox<Standart>> taqdimColumn = new TableColumn<>("ComboColumn");
        taqdimColumn.setMinWidth(100);
/*
        taqdimColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SanoqQaydi, ComboBox<Standart>>, ObservableValue<ComboBox<Standart>>>() {

            @Override
            public ObservableValue<ComboBox<Standart>> call(TableColumn.CellDataFeatures<SanoqQaydi, ComboBox<Standart>> param) {
                SanoqQaydi sanoqQaydi = param.getValue();
                ObservableList<Standart> comboBoxItems = getTovarComboBoxItems(sanoqQaydi.getTovar());
                ComboBox<Standart> comboBox = new ComboBox<>(comboBoxItems);

                if (sanoqQaydi.getDonaPochka() == 1) {
                    comboBox.getSelectionModel().selectLast();
                } else {
                    comboBox.getSelectionModel().selectFirst();
                }

                comboBox.setOnAction(event -> {
                    Standart newValue = comboBox.getSelectionModel().getSelectedItem();
                    if (newValue != null) {
                        Standart tovar = GetDbData.getTovar(sanoqQaydi.getTovar());
                        sanoqQaydi.setDonaPochka(comboBox.getSelectionModel().getSelectedIndex());
                        sanoqModels.update_data(connection, sanoqQaydi);
                    }
                });

                return new SimpleObjectProperty<>(comboBox);
            }
        });
*/
        taqdimColumn.setStyle("-fx-alignment: CENTER;");
        return taqdimColumn;
    }

    private void barCodeOn() {
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                stringBuffer.append(event.getText());
            }
        });

        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    String string = stringBuffer.toString().trim();
                    BarCode barCode = GetDbData.getBarCode(string);
                    Standart tovar = GetDbData.getTovar(barCode.getTovar());
                    if (tovar != null) {
                        addTovar(tovar);
                    }
                    stringBuffer.delete(0, stringBuffer.length());
                }
            }
        });

        barCodeTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    String string = barCodeTextField.getText().trim();
                    BarCode barCode = GetDbData.getBarCode(string);
                    Standart tovar = GetDbData.getTovar(barCode.getTovar());
                    if (tovar != null) {
                        barCodeTextField.setText("");
                        addTovar(tovar);
                    }
                }
            }
        });
    }

    private void barCodeOff() {
        scene.setOnKeyPressed(null);
        scene.setOnKeyReleased(null);
        barCodeTextField.setOnKeyPressed(null);
    }

    private void addTovar(Standart tovar) {
/*
        SanoqModels sanoqModels = new SanoqModels();
        Hisob hisob = hisobComboBox.getSelectionModel().getSelectedItem();
        SanoqQaydi sanoqQaydi = new SanoqQaydi(null, sanoq1Cursor.getId(),
                tovar.getId(), 1, 1.0, user.getId(), new Date()
        );
        sanoqModels.insert_data(connection, sanoqQaydi);
        tableViewObservableList.add(sanoqQaydi);

        Boolean tovarBormi = false;
        for (Sanoq3 t: sanoq3ObservableList) {
            if (t.getTovarId() == tovar.getId()) {
                tovarBormi = true;
                t.setSanalganAdad(t.getSanalganAdad() + 1);
                break;
            }
        }
        if (!tovarBormi) {
            sanoq3ObservableList.add(new Sanoq3(sanoq3ObservableList.size(), sanoq1Cursor.getId(),
                    tovar.getId(), .0, 1.0, user.getId(), null));
        }
        tovarAdadiTableView.refresh();
*/
    }

    private Standart getItem(ObservableList<Standart> standarts, int index) {
        Standart standart = null;
        for (Standart s : standarts) {
            if (s.getId() == index) {
                standart = s;
                break;
            }
        }
        return standart;
    }

    private ObservableList<Standart> getTovarComboBoxItems(Integer tovarId) {
        ObservableList<Standart> standartObservableList = FXCollections.observableArrayList();
        Standart tovar = GetDbData.getTovar(tovarId);
        return standartObservableList;
    }
}

