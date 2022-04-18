package sample.Controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import sample.Config.MySqlDBGeneral;
import sample.Data.*;
import sample.Enums.ServerType;
import sample.Model.HisobKitobModels;
import sample.Model.StandartModels;
import sample.Tools.*;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Date;

public class Tizimlar extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    SplitPane centerPane = new SplitPane();
    VBox rightSplitPane = new VBox();
    VBox centerSplitPane = new VBox();
    VBox leftSplitPane = new VBox();
    GridPane leftGridPane;
    GridPane rightGridPane;

    TableView<Standart> tizimTableView;
    TableView<HisobKitob> xomAshyoTableView;
    TableView<HisobKitob> tayyorMaxsulotTableView;
    StandartModels standartModels = new StandartModels("Tizimlar");
    HisobKitobModels hisobKitobModels = new HisobKitobModels();
    StringBuffer stringBuffer = new StringBuffer();

    Connection connection;
    User user;
    Standart tizim;
    Standart tovar;
    Standart birlik;
    MoneyShow decimalFormat = new MoneyShow();

    HisobKitob hisobKitob;
    int padding = 3;


    public static void main(String[] args) {
        launch(args);
    }

    public Tizimlar() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
        ibtido();
    }

    public Tizimlar(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        String classSimpleName = getClass().getSimpleName();
        DasturlarRoyxati.dastur(connection, user, classSimpleName);
        ibtido();
    }

    private void ibtido() {
        initTopPane();
        initLeftSplitPane();
        initCenterSplitPane();
        initRightSplitPane();
        initCenterPane();
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

    private void initStage(Stage primaryStage) {
        scene = new Scene(borderpane);
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        stage = primaryStage;
        stage.setTitle("Ishlab-chiqarish tizimlari");
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
            stage.close();
        });
        stage.setScene(scene);
    }

    private void initTopPane() {}

    private void initLeftSplitPane() {
        leftSplitPane.setPadding(new Insets(padding));
        SetHVGrow.VerticalHorizontal(leftSplitPane);
        tayyorMaxsulotTableView = getTayyorMaxsulotTableView();
        leftGridPane = initTovarGridPane(2);
        leftSplitPane.getChildren().addAll(leftGridPane, tayyorMaxsulotTableView);
    }

    private void initCenterSplitPane() {
        centerSplitPane.setPadding(new Insets(padding));
        SetHVGrow.VerticalHorizontal(centerSplitPane);
        xomAshyoTableView = getXomAshyoTableView();
        rightGridPane = initTovarGridPane(1);
        centerSplitPane.getChildren().addAll(rightGridPane, xomAshyoTableView);
    }

    private void initRightSplitPane() {
        rightSplitPane.setPadding(new Insets(padding));
        SetHVGrow.VerticalHorizontal(rightSplitPane);
        Tugmachalar tugmachalar = initTizimTugmachalar();
        tizimTableView = getTizimTableView();
        tizimTableView.setEditable(true);
        TextField textField = (TextField) leftGridPane.getChildren().get(5);
        setFoizTextField(textField);
        rightSplitPane.getChildren().addAll(tugmachalar, tizimTableView);
    }

    private void initCenterPane() {
        SetHVGrow.VerticalHorizontal(centerPane);
        centerPane.setDividerPositions(.4, .8);
        centerPane.getItems().addAll(leftSplitPane, centerSplitPane, rightSplitPane);
    }

    private void initBottomPane() {}
    private Tugmachalar initTizimTugmachalar() {
        Tugmachalar tugmachalar = new Tugmachalar();
        tugmachalar.getAdd().setOnAction(e->{
            ObservableList<Standart> observableList = tizimTableView.getItems();
            Standart standart = new Standart(null, "Yangi tizim", user.getId(), new Date());
            standartModels.insert_data(connection, standart);
            observableList.add(standart);
            tizimTableView.setItems(observableList);
            tizimTableView.refresh();
        });

        tugmachalar.getChildren().remove(tugmachalar.getEdit());
        tugmachalar.getChildren().remove(tugmachalar.getExcel());
        tugmachalar.getDelete().setOnAction(e->{
            Standart standart = tizimTableView.getSelectionModel().getSelectedItem();
            if (standart != null) {
                standartModels.delete_data(connection, standart);
                ObservableList<Standart> observableList = tizimTableView.getItems();
                observableList.remove(standart);
                tizimTableView.refresh();
            }
        });
        return tugmachalar;
    }

    private void initBorderPane() {
        borderpane.setCenter(centerPane);
    }

    private TableView<HisobKitob> getXomAshyoTableView() {
        TableView<HisobKitob> hisobKitobTableView = new TableView<>();
        SetHVGrow.VerticalHorizontal(hisobKitobTableView);
        TableViewAndoza tableViewAndoza = new TableViewAndoza();
        TableColumn<HisobKitob, String> izohColumn = tableViewAndoza.getIzoh2Column();
        izohColumn.setText("Xomashyo");
        TableColumn<HisobKitob, Double> adad = getAdadColumn();
        adad.setOnEditCommit(event -> {
            HisobKitob hisobKitob = event.getRowValue();
            if (hisobKitob != null) {
                hisobKitob.setDona(event.getNewValue());
                event.getTableView().refresh();
            }
        });
        hisobKitobTableView.getColumns().addAll(
                izohColumn,
                adad,
                getDeleteColumn(1)
        );
        hisobKitobTableView.setEditable(true);
        return hisobKitobTableView;
    }

    private TableView<HisobKitob> getTayyorMaxsulotTableView() {
        TableView<HisobKitob> hisobKitobTableView = new TableView<>();
        SetHVGrow.VerticalHorizontal(hisobKitobTableView);
        TableViewAndoza tableViewAndoza = new TableViewAndoza();
        TableColumn<HisobKitob, String> izohColumn = tableViewAndoza.getIzoh2Column();
        izohColumn.setText("Tayyor maxsulot");
        TableColumn<HisobKitob, Double> foizColumn = getNarhColumn();
        foizColumn.setText("Xomashyo narxiga nisbatan %");
        hisobKitobTableView.getColumns().addAll(
                izohColumn,
                getAdadColumn(),
                foizColumn,
                getDeleteColumn(2)
        );
        hisobKitobTableView.setEditable(true);
        return hisobKitobTableView;
    }

    private TableView<Standart> getTizimTableView() {
        TableView<Standart> tableView = new TableView<>();
        tableView.setEditable(true);
        SetHVGrow.VerticalHorizontal(tableView);
        TableColumn<Standart, String> textColumn = getTizimTextColumn();
        textColumn.setMinWidth(250);
        tableView.getColumns().addAll(textColumn);
        standartModels.setTABLENAME("Tizimlar");
        ObservableList<Standart> observableList = standartModels.get_data(connection);
        if (observableList.size()>0) {
            tizim = observableList.get(0);
            refreshTables(tizim);
            tableView.getSelectionModel().select(tizim);
            tableView.scrollTo(tizim);
            tableView.requestFocus();
        }
        tableView.setItems(observableList);
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)->{
            if (newValue != null) {
                tizim = newValue;
                refreshTables(newValue);
            }
        });

        return tableView;
    }

    private TableColumn<Standart, Integer> getTizimIdColumn() {
        TableColumn<Standart, Integer> column = new TableColumn<>("id");
        column.setCellValueFactory(new PropertyValueFactory<>("id"));
        return column;
    }

    private TableColumn<Standart, String> getTizimTextColumn() {
        TableColumn<Standart, String> column = new TableColumn<>("text");
        column.setCellValueFactory(new PropertyValueFactory<>("text"));
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setOnEditCommit(event -> {
            String newString = event.getNewValue();
            if (newString != null) {
                System.out.println("keldi");
                Standart standart = event.getRowValue();
                standart.setText(newString);
                standartModels.update_data(connection, standart);
            }
        });
        return column;
    }

    private GridPane initTovarGridPane(Integer tableNumber) {
        GridPane gridPane = new GridPane();
        HBox.setHgrow(gridPane, Priority.ALWAYS);
        VBox.setVgrow(gridPane, Priority.NEVER);
        TextField foizTextField = new TextField();
        Label foizLabel = new Label("Xomashyo umumiy narhiga nisbatan foiz");

        TextField barCodeTextField = new TextField();
        barCodeTextField.setPromptText("Barcode");
        TextField adadTextField = new TextField();
        adadTextField.setPromptText("Dona");
        Button addTovarButton = new Button("Add");
        addTovarButton.setMaxWidth(2000);
        addTovarButton.setPrefWidth(150);

        TextFieldButton textFieldButton = new TextFieldButton();
        TextField textField = textFieldButton.getTextField();
        standartModels.setTABLENAME("Tovar");
        ObservableList<Standart> tovarlar = standartModels.get_data(connection);
        TextFields.bindAutoCompletion(textField, tovarlar).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Standart> autoCompletionEvent) -> {
            Standart newValue = autoCompletionEvent.getCompletion();
            if (newValue != null) {
                tovar = newValue;
                tovarniYangila(tovar, gridPane);
            }
        });
        Button button = textFieldButton.getPlusButton();
        button.setOnAction(event -> {
            tovar = addTovar(gridPane);
            tovarniYangila(tovar, gridPane);
        });

        HBoxComboBoxPlusButton birlikComboBox = new HBoxComboBoxPlusButton();
        ComboBox<Standart> comboBox = (ComboBox<Standart>) birlikComboBox.getComboBox();
        comboBox.getSelectionModel().selectedItemProperty().addListener((qbservable, oldValue, newValue)->{
            if (newValue != null) {
                birlik = newValue;
                ObservableList<BarCode> barCodeList = GetDbData.getBarCodeList(tovar.getId());
                for (BarCode bc: barCodeList) {
                    if (bc.getBirlik().equals(birlik.getId())) {
                        barCodeTextField.setText(bc.getBarCode());
                        adadTextField.setPromptText(birlik.getText());
                        break;
                    }
                }
            }
        });

        barCodeTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode()== KeyCode.ENTER) {
                    stringBuffer.delete(0, stringBuffer.length());
                    String string = barCodeTextField.getText().trim();
                    if (!string.isEmpty()) {
                        BarCode barCode = GetDbData.getBarCode(string);
                        if (barCode != null) {
                            barCodeniYangila(barCode, gridPane);
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

        addTovarButton.setOnAction(event -> {
            Boolean isClear = addHisobKitob(tizim, barCodeTextField.getText(), adadTextField, foizTextField, tableNumber);

            if (isClear) {
                clearGridPaneData(textField, comboBox, barCodeTextField, adadTextField, foizTextField);
            }
        });

        foizTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Double foizDouble = getDoubleFromTextField(foizTextField);
                ObservableList<HisobKitob> observableList = tayyorMaxsulotTableView.getItems();
                Double foizJamiDouble = 0d;
                for (HisobKitob hk: observableList) {
                    foizJamiDouble += hk.getNarh();
                }
                if (foizJamiDouble+foizDouble>100d) {
                    Alerts.AlertString("End ko`pi " + (100-foizJamiDouble) + " kiritishingiz mumkin");
                    foizTextField.setText(""+(100-foizJamiDouble));
                }
            }
        });

        Integer rowIndex = 0;
        gridPane.add(textFieldButton, 0, rowIndex, 1 ,1);
        gridPane.add(birlikComboBox, 1, rowIndex, 1 ,1);
        GridPane.setHgrow(textFieldButton, Priority.ALWAYS);
        GridPane.setHgrow(birlikComboBox, Priority.ALWAYS);

        rowIndex++;
        gridPane.add(barCodeTextField, 0, rowIndex, 1 ,1);
        gridPane.add(adadTextField, 1, rowIndex, 1 ,1);

        if (tableNumber==2) {
            rowIndex++;
            gridPane.add(foizLabel, 0, rowIndex, 1, 1);
            gridPane.add(foizTextField, 1, rowIndex, 1, 1);
            GridPane.setHgrow(foizTextField, Priority.ALWAYS);
            GridPane.setHgrow(foizLabel, Priority.ALWAYS);
        }

        rowIndex++;
        gridPane.add(addTovarButton, 0, rowIndex, 2 ,1);
        GridPane.setHgrow(addTovarButton, Priority.ALWAYS);

        return gridPane;
    }

    private Boolean addHisobKitob(Standart tizim, String bcString, TextField adadTextField, TextField foizTextField, Integer tableNumber) {

        if (tizim == null) {
            return false;
        }
        if (bcString.isEmpty()) {
            return false;
        }
        if (adadTextField.getText().isEmpty()) {
            return false;
        }
        Double foizDouble = 0d;
        if (tableNumber==2) {
            foizDouble = getDoubleFromTextField(foizTextField);
            if (foizDouble == 0d) {
                return false;
            }
        }
        Double adadDouble = getDoubleFromTextField(adadTextField);
        hisobKitobModels.setTABLENAME("XomAshyo");
        BarCode barCode = GetDbData.getBarCode(bcString);
        Standart tovar = GetDbData.getTovar(barCode.getTovar());
        Standart birlik = GetDbData.getBirlik(barCode.getBirlik());
        String izoh = tovar.getText() + " birlik: " + birlik.getText();
        HisobKitob hisobKitob = new HisobKitob(
                null,
                tizim.getId(),
                0,
                18,
                0,
                0,
                1,
                barCode.getTovar(),
                1d,
                barCode.getBarCode(),
                adadDouble,
                foizDouble,
                0,
                izoh,
                user.getId(),
                new Date()
        );
        if (tableNumber ==1) {
            hisobKitobModels.setTABLENAME("XomAshyo");
            xomAshyoTableView.getItems().add(hisobKitob);
        }
        if (tableNumber == 2) {
            hisobKitobModels.setTABLENAME("TayyorMaxsulot");
            tayyorMaxsulotTableView.getItems().add(hisobKitob);
        }
        hisobKitobModels.insert(connection, hisobKitob);
        refreshTables(tizim);
        return true;
    }

    private void clearGridPaneData(TextField tovarTextField, ComboBox<Standart> comboBox, TextField barCodeTextField, TextField adadTextField, TextField foizTextField) {
        tovarTextField.setText("");
        comboBox.setItems(null);
        barCodeTextField.setText("");
        adadTextField.setText("");
        tovar = null;
        birlik = null;
    }

    private Standart addTovar(GridPane gridPane) {
        Standart tovar1 = null;
        TovarController tovarController = new TovarController(connection, user);
        tovarController.display();
        TextFieldButton textFieldButton = (TextFieldButton) gridPane.getChildren().get(0);
        TextField textField = textFieldButton.getTextField();
        if (tovarController.getDoubleClick()) {
            ObservableList<Standart> tovarObservableList = tovarController.tovarTableView.getItems();
            tovar1 = tovarController.getDoubleClickedRow();
            boolean yangi = true;
            for (Standart t: tovarObservableList) {
                if (t.getId().equals(tovar1.getId())) {
                    yangi = false;
                    textField.setText(t.getText());
                    break;
                }
            }
            if (yangi) {
                tovarObservableList.add(tovar1);
                TextFields.bindAutoCompletion(textField, tovarObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Standart> autoCompletionEvent) -> {
                    Standart newValue = autoCompletionEvent.getCompletion();
                    if (newValue != null) {
                        tovar = newValue;
                        tovarniYangila(tovar, gridPane);
                    }
                });
            }
        }
        return tovar1;
    }
    private void tovarniYangila(Standart tovar, GridPane gridPane) {
        ObservableList<BarCode> barCodeList = GetDbData.getBarCodeList(tovar.getId());
        ObservableList<Standart> birlikObservableList = FXCollections.observableArrayList();
        HBoxComboBoxPlusButton hBoxComboBoxPlusButton =  (HBoxComboBoxPlusButton) gridPane.getChildren().get(1);
        ComboBox<Standart> birlikComboBox = (ComboBox<Standart>) hBoxComboBoxPlusButton.getComboBox();
        TextField barCodeTextField = (TextField) gridPane.getChildren().get(2);
        if (barCodeList.size()>0) {
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
        }
    }
    private void barCodeniYangila(BarCode barCode, GridPane gridPane) {
        tovar = GetDbData.getTovar(barCode.getTovar());
        TextFieldButton tovarHBox = (TextFieldButton) gridPane.getChildren().get(0);
        TextField textField = tovarHBox.getTextField();
        textField.setText(tovar.getText());
        HBoxComboBoxPlusButton comboBoxPlus = (HBoxComboBoxPlusButton) gridPane.getChildren().get(1);
        ComboBox<Standart> comboBox = (ComboBox<Standart>) comboBoxPlus.getComboBox();
        ObservableList<Standart> birlikList = FXCollections.observableArrayList();
        ObservableList<BarCode> barCodes = GetDbData.getBarCodeList(barCode.getTovar());
        for (BarCode bc: barCodes) {
            birlikList.add(GetDbData.getBirlik(bc.getBirlik()));
        }
        comboBox.setItems(birlikList);
        birlik = GetDbData.getBirlik(barCode.getBirlik());
        for (Standart bl: birlikList) {
            if (bl.getId().equals(barCode.getBirlik())) {
                birlik = bl;
                comboBox.getSelectionModel().select(bl);
                TextField adad = (TextField) gridPane.getChildren().get(3);
                adad.setPromptText(bl.getText());
                break;
            }
        }
    }

    private void refreshTables(Standart newValue) {
        hisobKitobModels.setTABLENAME("XomAshyo");
        String select = "qaydId = " + newValue.getId();
        ObservableList<HisobKitob> xomAshyoList = hisobKitobModels.getAnyData(connection, select, "");
        xomAshyoTableView.setItems(xomAshyoList);
        xomAshyoTableView.refresh();
        hisobKitobModels.setTABLENAME("TayyorMaxsulot");
        ObservableList<HisobKitob> tayyorMaxsulotList = hisobKitobModels.getAnyData(connection, select, "");
        tayyorMaxsulotTableView.setItems(tayyorMaxsulotList);
        tayyorMaxsulotTableView.refresh();
    }

    public static Double getDoubleFromTextField(TextField textField) {
        Double doubleValue = 0d;
        String textValue = textField.getText();
        if (textValue != null) {
            textValue = textValue.replaceAll(",", ".");
            textValue = textValue.replaceAll(" ", "");
            doubleValue = textValue.isEmpty() ? 0d : Double.valueOf(textValue);
        }
        return doubleValue;
    }
    private TableColumn<HisobKitob, Button> getDeleteColumn(Integer tableNumber) {
        TableColumn<HisobKitob, Button> deleteColumn = new TableColumn<>("O`chir");
        deleteColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, Button>, ObservableValue<Button>>() {

            @Override
            public ObservableValue<Button> call(TableColumn.CellDataFeatures<HisobKitob, Button> param) {
                HisobKitob hisobKitob = param.getValue();
                ObservableList<HisobKitob> observableList = param.getTableView().getItems();
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
                    observableList.remove(hisobKitob);
                    hisobKitobModels.setTABLENAME("XomAshyo");
                    if (tableNumber == 2) {
                        hisobKitobModels.setTABLENAME("TayyorMaxsulot");
                    }
                    hisobKitobModels.delete_data(connection, hisobKitob);
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
            HisobKitob hisobKitob = event.getRowValue();
            if (hisobKitob != null) {
                hisobKitob.setDona(event.getNewValue());
                event.getTableView().refresh();
            }
        });
        adad.setStyle( "-fx-alignment: CENTER;");
        return adad;
    }
    private  TableColumn<HisobKitob, Double> getNarhColumn() {
        TableColumn<HisobKitob, Double>  narh = new TableColumn<>("Narh");
        narh.setCellValueFactory(new PropertyValueFactory<>("narh"));
        narh.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
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
        narh.setOnEditCommit(event -> {
            Double narhDouble = event.getNewValue();
            if (narhDouble != null) {
                HisobKitob hisobKitob = event.getRowValue();
                ObservableList<HisobKitob> observableList = event.getTableView().getItems();
                hisobKitob.setNarh(event.getNewValue());
                hisobKitobModels.setTABLENAME("TayyorMaxsulot");
                hisobKitobModels.update_data(connection, hisobKitob);
                event.getTableView().refresh();
            }
        });
        narh.setStyle( "-fx-alignment: CENTER;");
        return narh;
    }

    private void setFoizTextField(TextField foizTextField) {
        ObservableList<HisobKitob> observableList = tayyorMaxsulotTableView.getItems();
        Double foizJamiDouble = 0d;
        for (HisobKitob hk: observableList) {
            foizJamiDouble += hk.getNarh();
        }
        if (100-foizJamiDouble>0d) {
            foizTextField.setText(""+(100-foizJamiDouble));
        } else {
            foizTextField.setText("0");
        }

    }
}
