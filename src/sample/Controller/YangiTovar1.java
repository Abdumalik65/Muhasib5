package sample.Controller;

import com.jfoenix.controls.JFXButton;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import sample.Config.MySqlDB;
import sample.Config.MySqlDBGeneral;
import sample.Data.*;
import sample.Enums.ServerType;
import sample.Model.*;
import sample.Temp.YangiTovarGuruhi;
import sample.Tools.*;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Optional;

public class YangiTovar1 extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    GridPane centerGridPane = new GridPane();
    GridPane yakkaGrid = new GridPane();
    ToggleGroup toggleGroup = new ToggleGroup();

    TextField tovarNomiTextField = new TextField();
    TextField xaridNarhiTextField = new TextField();
    TextField chakanaNarhTextField = new TextField();
    TextField ulgurjiNarhTextField = new TextField();
    TextField ndsTextField = new TextField();
    TextField bojTextField = new TextField();
    Tugmachalar barCodeAddButton = new Tugmachalar();

    TableView<BarCode> barCodesTableView = new TableView<>();
    ComboBox<Standart> tartibComboBox = new ComboBox<>();

    HBox hBox = new HBox();
    HBoxTextFieldPlusButton tovarGurugiHBox = new HBoxTextFieldPlusButton();
    Button qaydEtButton = new Button("Qayd et");

    ObservableList<Standart> tovarObservableList;
    ObservableList<BarCode> barCodeObservableList = FXCollections.observableArrayList();
    ObservableList<BarCode> tarkibObservableList = FXCollections.observableArrayList();
    ObservableList<Standart> birlikObservableList = FXCollections.observableArrayList();
    ObservableList<Standart> tartibObservableList;

    BarCodeModels barCodeModels = new BarCodeModels();
    StandartModels standartModels =  new StandartModels();
    Standart4Models standart4Models = new Standart4Models();
    TovarNarhiModels tovarNarhiModels = new TovarNarhiModels();
    TovarSanaModels tovarSanaModels = new TovarSanaModels();

    BarCode barCodeCursor;
    Standart tovarCursor = null;
    Standart6 s6Cursor;

    Connection connection;
    User user = new User(1, "admin", "", "admin");

    DecimalFormat decimalFormat = new MoneyShow();
    Font font = Font.font("Arial", FontWeight.BOLD,20);
    int birlikIndex = 1;
    int barCodeId = 0;
    Double tovarAdadi = 1.0;
    boolean qaydEtdim = false;
    Integer tovarId = null;

    public static void main(String[] args) {
        launch(args);
    }

    public YangiTovar1(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
    }

    public YangiTovar1() {
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        initStage(primaryStage);
        ibtido();
        stage.show();
    }

    public Standart display() {
        stage   = new Stage();
        initStage(stage);
        ibtido();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        return tovarCursor;
    }

    public void ibtido() {
        initData();
        initGrows();
        initTextFields();
        initTovarGuruhiHBox();
        initTartibCobmoBox();
        initButtons();
        initBarCodesTableView();
        initCenterGridPane();
        initBorderPane();
    }

    private void initGrows() {
        HBox.setHgrow(tovarNomiTextField, Priority.ALWAYS);
        SetHVGrow.VerticalHorizontal(barCodesTableView);
    }

    private void initTartibCobmoBox() {
        tartibComboBox.setMaxWidth(2000);
        tartibComboBox.setPrefWidth(168);
        tartibComboBox.setItems(tartibObservableList);
        tartibComboBox.getSelectionModel().selectFirst();
        HBox.setHgrow(tartibComboBox, Priority.ALWAYS);
    }

    private void initData() {
        GetDbData.initData(connection);
        standartModels.setTABLENAME("Birlik");
        birlikObservableList = standartModels.get_data(connection);
        standartModels.setTABLENAME("ChiqimShakli");
        tartibObservableList = standartModels.get_data(connection);
        barCodeId++;
        BarCode barCode = new BarCode(barCodeId, 0, "", birlikIndex, 1.0, 0, .0, .0, user.getId(),  null);
        barCodeObservableList.add(barCode);
        tarkibObservableList.add(barCode);
        standartModels.setTABLENAME("Tovar");
        tovarObservableList = standartModels.get_data(connection);
    }

    private void initCenterGridPane() {
        int rowIndex = 0;
        centerGridPane.setPadding(new Insets(3));
        centerGridPane.setHgap(5);
        centerGridPane.setVgap(5);
        centerGridPane.setPadding(new Insets(3));

        HBox.setHgrow(centerGridPane, Priority.ALWAYS);
        VBox.setVgrow(centerGridPane, Priority.ALWAYS);

        HBox tovarHBox = new HBox(5);
        HBox.setHgrow(tovarHBox, Priority.ALWAYS);

        tovarHBox.getChildren().addAll(new Label("Tovar nomi"), tovarNomiTextField, tartibComboBox);
        centerGridPane.add(tovarHBox, 1, rowIndex, 3, 1);
        GridPane.setHgrow(tovarHBox, Priority.ALWAYS);

        rowIndex++;
        centerGridPane.add(barCodeAddButton, 0, rowIndex, 3, 1);
        GridPane.setHgrow(barCodeAddButton, Priority.ALWAYS);

        rowIndex++;
        centerGridPane.add(barCodesTableView, 0, rowIndex, 3, 1);
        GridPane.setHgrow(barCodesTableView, Priority.ALWAYS);
        GridPane.setVgrow(barCodesTableView, Priority.ALWAYS);

        rowIndex++;
        HBox groupHbox = initToggleGroup();
        centerGridPane.add(groupHbox, 0, rowIndex, 3, 1);
        GridPane.setHgrow(groupHbox, Priority.ALWAYS);

        rowIndex++;
        HBox.setHgrow(tovarGurugiHBox, Priority.ALWAYS);
        centerGridPane.add(tovarGurugiHBox, 0, rowIndex, 3, 1);
        GridPane.setHgrow(tovarGurugiHBox, Priority.ALWAYS);

        rowIndex++;
        initYakkaGrid();
        centerGridPane.add(yakkaGrid, 0, rowIndex, 3, 1);
        GridPane.setHgrow(yakkaGrid, Priority.ALWAYS);

        rowIndex++;;
        centerGridPane.add(qaydEtButton, 0, rowIndex, 3, 1);
        GridPane.setHgrow(qaydEtButton, Priority.ALWAYS);
    }

    private HBox initToggleGroup() {
        HBox hBox = new HBox(10);
        HBox.setHgrow(hBox, Priority.ALWAYS);
        RadioButton radioButton = new RadioButton("Guruhga mansub tovar");
        radioButton.setId("1");
        radioButton.setToggleGroup(toggleGroup);
        radioButton.setSelected(true);
        yakkaGrid.setDisable(true);
        RadioButton radioButton1 = new RadioButton("Yakka tovar");
        radioButton1.setId("2");
        radioButton1.setToggleGroup(toggleGroup);
        hBox.getChildren().addAll(radioButton, radioButton1);
        toggleGroup.selectToggle(radioButton1);
        showYakka();
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                RadioButton rb = (RadioButton) newValue;
                String buttonName = rb.getText();
                if (buttonName.equalsIgnoreCase("Guruhga mansub tovar")) {
                    showGuruh();
                } else if (buttonName.equalsIgnoreCase("Yakka tovar")){
                    showYakka();
                }
            }
        });
        return hBox;
    }

    private void showGuruh() {
        tovarGurugiHBox.setDisable(false);
        yakkaGrid.setDisable(true);
    }

    private void showYakka() {
        tovarGurugiHBox.setDisable(true);
        yakkaGrid.setDisable(false);
    }

    private void initYakkaGrid() {
        int rowIndex = 0;
        yakkaGrid.setPadding(new Insets(3));
        yakkaGrid.setHgap(5);
//        yakkaGrid.setVgap(5);
        yakkaGrid.setPadding(new Insets(3));
        SetHVGrow.VerticalHorizontal(yakkaGrid);

        yakkaGrid.add(new Label("Xarid narhi"), 0, rowIndex, 1, 1);
        yakkaGrid.add(xaridNarhiTextField, 1, rowIndex, 1, 1);
        GridPane.setHgrow(xaridNarhiTextField, Priority.ALWAYS);
        yakkaGrid.add(new Label("Ulgurgi(optom) narhi"), 2, rowIndex, 1, 1);
        yakkaGrid.add(ulgurjiNarhTextField, 3, rowIndex, 1, 1);
        GridPane.setHgrow(ulgurjiNarhTextField, Priority.ALWAYS);
        yakkaGrid.add(new Label("Chakana(shtuchniy) narhi"), 4, rowIndex, 1, 1);
        yakkaGrid.add(chakanaNarhTextField, 5, rowIndex, 1, 1);
        GridPane.setHgrow(chakanaNarhTextField, Priority.ALWAYS);

        rowIndex++;
        yakkaGrid.add(new Label("NDS foizi"), 0, rowIndex, 1, 1);
        yakkaGrid.add(ndsTextField, 1, rowIndex, 1, 1);
        GridPane.setHgrow(ndsTextField, Priority.ALWAYS);
        yakkaGrid.add(new Label("Bojxona solig`i"), 2, rowIndex, 1, 1);
        yakkaGrid.add(bojTextField, 3, rowIndex, 1, 1);
        GridPane.setHgrow(bojTextField, Priority.ALWAYS);
    }


    private void initBorderPane() {
        borderpane.setCenter(centerGridPane);
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Yangi  tovar");
        scene = new Scene(borderpane, 1000, 420);
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            tovarCursor = null;
        });
    }

    private void initBarCodesTableView() {
        HBox.setHgrow(barCodesTableView, Priority.ALWAYS);
        VBox.setVgrow(barCodesTableView, Priority.ALWAYS);
        barCodesTableView.setMinHeight(130);
        barCodesTableView.getColumns().addAll(
                getBirlikColumn(),
                getTextButtonColumn(),
                getAdadColumn(),
                getBirlik2Column(),
                getBarCode2Column(),
                getVaznColumn(),
                getVaznCalcColumn(),
                getHajmColumn(),
                getHajmCalcColumn(),
                getBarCodeDeleteColumn()
        );
        barCodesTableView.setEditable(true);
        barCodesTableView.setItems(barCodeObservableList);
        /*
        id
        barcode
        birlik
        dona
        delete
        */
        barCodesTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                barCodeCursor = newValue;
            }
        });
    }

    private TableColumn<BarCode, ComboBox<Standart>> getBirlikColumn() {
        TableColumn<BarCode, ComboBox<Standart>> taqdimColumn = new TableColumn<>("Birlik");
        taqdimColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<BarCode, ComboBox<Standart>>, ObservableValue<ComboBox<Standart>>>() {

            @Override
            public ObservableValue<ComboBox<Standart>> call(TableColumn.CellDataFeatures<BarCode, ComboBox<Standart>> param) {
                BarCode barCode = param.getValue();
                ObservableList<Standart> comboBoxItems = birlikObservableList;
                ComboBox<Standart> comboBox = new ComboBox<>(comboBoxItems);
                for (Standart s: comboBoxItems) {
                    if (s.getId().equals(barCode.getBirlik())) {
                        comboBox.getSelectionModel().select(s);
                        break;
                    }
                }
                comboBox.setMaxWidth(2000);
                comboBox.setPrefWidth(150);
                HBox.setHgrow(comboBox, Priority.ALWAYS);

                comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->  {
                    Standart item = comboBox.getSelectionModel().getSelectedItem();
                    if (item != null) {
                        barCode.setBirlik(item.getId());
                    }
                });
                return new SimpleObjectProperty<>(comboBox);
            }
        });
        taqdimColumn.setStyle( "-fx-alignment: CENTER;");
        taqdimColumn.setMinWidth(120);
        return taqdimColumn;
    }

    private TableColumn<BarCode, String> getBarCodeColumn() {
        TableColumn<BarCode, String> textColumn = new TableColumn<>("Shtrix kod");
        textColumn.setMinWidth(150);
        textColumn.setCellValueFactory(new PropertyValueFactory<>("barCode"));
        textColumn.setCellFactory(TextFieldTableCell.<BarCode> forTableColumn());
        textColumn.setOnEditCommit((TableColumn.CellEditEvent<BarCode, String> event) -> {
            TablePosition<BarCode, String> pos = event.getTablePosition();
            String newString = event.getNewValue();
            int row = pos.getRow();
            BarCode barCode = event.getTableView().getItems().get(row);
            BarCode newBarCode = GetDbData.getBarCode(newString);
            if (newBarCode == null) {
                barCode.setBarCode(newString);
            }
            else {
                barCode.setBarCode(event.getOldValue());
                Standart tovar = GetDbData.getTovar(newBarCode.getTovar());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Diqqat qiling !!!");
                alert.setHeaderText(tovar.getText() + "\n" + newBarCode.getBarCode());
                alert.setContentText("Bu shtrix kod bazada bor");
                alert.showAndWait();
                event.getTableView().refresh();
            }
        });
        return textColumn;
    }

    private  TableColumn<BarCode, Double> getAdadColumn() {
        TableColumn<BarCode, Double>  adad = new TableColumn<>("Adad");
        adad.setMinWidth(80);
        adad.setMaxWidth(150);
        adad.setCellValueFactory(new PropertyValueFactory<>("adad"));
        adad.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {

                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setMinimumIntegerDigits (1);
                numberFormat.setMaximumIntegerDigits (8);

                numberFormat.setMinimumFractionDigits (0);
                numberFormat.setMaximumFractionDigits (5);
                return numberFormat.format(object);
            }

            @Override
            public Double fromString(String string) {
                string = string.replaceAll(" ", "");
                string = string.replaceAll(",", ".");
                if (!Alerts.isNumericAlert(string)) {
                    string = "0.0";
                }
                return Double.valueOf(string);
            }
        }));
        adad.setOnEditCommit(event -> {
            Double newValue = event.getNewValue();
            if (newValue != null) {
                barCodeCursor.setAdad(newValue);
                barCodesTableView.refresh();
            }
        });
        adad.setStyle( "-fx-alignment: CENTER;");
        return adad;
    }

    private TableColumn<BarCode, Button> getBarCodeDeleteColumn() {
        TableColumn<BarCode, Button> deleteColumn = new TableColumn<>("O`chir");
        deleteColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<BarCode, Button>, ObservableValue<Button>>() {

            @Override
            public ObservableValue<Button> call(TableColumn.CellDataFeatures<BarCode, Button> param) {
                BarCode barCode = param.getValue();
                JFXButton b = new JFXButton("");
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
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.getButtonTypes().removeAll(alert.getButtonTypes());
                    ButtonType okButton = new ButtonType("Ha");
                    ButtonType noButton = new ButtonType("Yo`q");
                    alert.getButtonTypes().addAll(okButton, noButton);
                    alert.setTitle("Diqqat !!!");
                    alert.setHeaderText("Shtrix kod: "  +  GetDbData.getBirlik(barCode.getBirlik()) + " " + barCode.getBarCode() + " " + barCode.getAdad());
                    alert.setContentText("Shtrix kod o`chiriladi. Davom etaymi ???");
                    Optional<ButtonType> option = alert.showAndWait();
                    ButtonType buttonType = option.get();
                    if (okButton.equals(buttonType)) {
                        barCodeObservableList.remove(barCode);
                    }
                });
                return new SimpleObjectProperty<Button>(b);
            }
        });

        deleteColumn.setMinWidth(20);
        deleteColumn.setMaxWidth(40);
        deleteColumn.setStyle( "-fx-alignment: CENTER;");
        return deleteColumn;
    }

    private TableColumn<BarCode, Integer> getBirlik2Column() {
        TableColumn<BarCode, Integer> taqdimColumn = new TableColumn<>("Birlik2");
        taqdimColumn.setCellValueFactory(new PropertyValueFactory<>("tarkib"));
        taqdimColumn.setCellFactory(column -> {
            TableCell<BarCode, Integer> cell = new TableCell<BarCode, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }  else {
                        BarCode barCode = null;
                        for (BarCode bc: barCodeObservableList) {
                            if (bc.getId().equals(item)) {
                                barCode = bc;
                                break;
                            }
                        }
                        if (barCode != null) {
                            setText(getBirlik(barCode.getBirlik()));
                        }
                    }
                    setAlignment(Pos.CENTER_LEFT);
                }
            };
            return cell;
        });
        taqdimColumn.setStyle( "-fx-alignment: CENTER;");
        taqdimColumn.setMinWidth(120);
        return taqdimColumn;
    }

    private TableColumn<BarCode, ComboBox<BarCode>> getBarCode2Column() {
        TableColumn<BarCode, ComboBox<BarCode>> barCode2Column = new TableColumn<>("Shtrixkod2");
        barCode2Column.setMinWidth(180);
        barCode2Column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<BarCode, ComboBox<BarCode>>, ObservableValue<ComboBox<BarCode>>>() {

            @Override
            public ObservableValue<ComboBox<BarCode>> call(TableColumn.CellDataFeatures<BarCode, ComboBox<BarCode>> param) {
                BarCode barCode = param.getValue();
                ComboBox<BarCode> comboBox = new ComboBox<>(tarkibObservableList);
                comboBox.setMaxWidth(2000);
                comboBox.setPrefWidth(150);
                HBox.setHgrow(comboBox, Priority.ALWAYS);
                for (BarCode bc: barCodeObservableList) {
                    if (bc.getId().equals(barCode.getTarkib())) {
                        comboBox.getSelectionModel().select(bc);
                        break;
                    }
                }

                comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->  {
                    BarCode item = comboBox.getSelectionModel().getSelectedItem();
                    if (item != null) {
                        barCode.setTarkib(item.getId());
                        param.getTableView().refresh();
                    }
                });
                return new SimpleObjectProperty<>(comboBox);
            }
        });
        barCode2Column.setStyle( "-fx-alignment: CENTER;");
        barCode2Column.setMinWidth(120);
        return barCode2Column;
    }

    private  TableColumn<BarCode, Double> getVaznColumn() {
        TableColumn<BarCode, Double>  vaznColumn = new TableColumn<>("Vazn");
        vaznColumn.setMinWidth(130);
        vaznColumn.setCellValueFactory(new PropertyValueFactory<>("vazn"));
        vaznColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setMinimumIntegerDigits (1);
                numberFormat.setMaximumIntegerDigits (8);

                numberFormat.setMinimumFractionDigits (1);
                numberFormat.setMaximumFractionDigits (5);
                return numberFormat.format(object);
            }

            @Override
            public Double fromString(String string) {
                string = string.replaceAll(" ", "");
                string = string.replaceAll(",", ".");
                if (!Alerts.isNumericAlert(string)) {
                    string = "0.0";
                }
                return Double.valueOf(string);
            }
        }));
        vaznColumn.setOnEditCommit(event -> {
            Double newValue = event.getNewValue();
            if (newValue != null) {
                barCodeCursor.setVazn(newValue);
                barCodesTableView.refresh();
            }
        });
        vaznColumn.setStyle( "-fx-alignment: CENTER;");
        return vaznColumn;
    }

    private TableColumn<BarCode, Button> getVaznCalcColumn() {
        TableColumn<BarCode, Button> vaznCalcColumn = new TableColumn<>("Calc");
        vaznCalcColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<BarCode, Button>, ObservableValue<Button>>() {

            @Override
            public ObservableValue<Button> call(TableColumn.CellDataFeatures<BarCode, Button> param) {
                BarCode barCode = param.getValue();
                JFXButton b = new JFXButton("?");
                b.setMaxWidth(2000);
                b.setPrefWidth(150);
                HBox.setHgrow(b, Priority.ALWAYS);

                b.setOnAction(event -> {
                    BarCode barCode1 = null;
                    for (BarCode bc: barCodeObservableList) {
                        if (bc.getId().equals(barCode.getTarkib())){
                            barCode1 = bc;
                            break;
                        }
                    }
                    if (barCode1 != null) {
                        VaznCalculator vaznCalculator = new VaznCalculator(barCode.getAdad(), barCode1.getVazn());
                        Double vaznDouble = vaznCalculator.display();
                        if (vaznCalculator.qaydEtdimBoolean) {
                            barCode.setVazn(vaznDouble);
                            barCodesTableView.refresh();
                        }
                    }
                });
                return new SimpleObjectProperty<Button>(b);
            }
        });

        vaznCalcColumn.setMinWidth(20);
        vaznCalcColumn.setMaxWidth(40);
        vaznCalcColumn.setStyle( "-fx-alignment: CENTER;");
        return vaznCalcColumn;
    }

    private  TableColumn<BarCode, Double> getHajmColumn() {
        TableColumn<BarCode, Double>  hajmColumn = new TableColumn<>("Hajm");
        hajmColumn.setMinWidth(130);
        hajmColumn.setCellValueFactory(new PropertyValueFactory<>("hajm"));
        hajmColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setMinimumIntegerDigits (1);
                numberFormat.setMaximumIntegerDigits (8);

                numberFormat.setMinimumFractionDigits (1);
                numberFormat.setMaximumFractionDigits (5);
                return numberFormat.format(object);
            }

            @Override
            public Double fromString(String string) {
                string = string.replaceAll(" ", "");
                string = string.replaceAll(",", ".");
                if (!Alerts.isNumericAlert(string)) {
                    string = "0.0";
                }
                return Double.valueOf(string);
            }
        }));
        hajmColumn.setOnEditCommit(event -> {
            Double newValue = event.getNewValue();
            if (newValue != null) {
                barCodeCursor.setHajm(newValue);
                barCodesTableView.refresh();
            }
        });
        hajmColumn.setStyle( "-fx-alignment: CENTER;");
        return hajmColumn;
    }

    private TableColumn<BarCode, Button> getHajmCalcColumn() {
        TableColumn<BarCode, Button> hajmCalcColumn = new TableColumn<>("Calc");
        hajmCalcColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<BarCode, Button>, ObservableValue<Button>>() {

            @Override
            public ObservableValue<Button> call(TableColumn.CellDataFeatures<BarCode, Button> param) {
                BarCode barCode = param.getValue();
                JFXButton b = new JFXButton("?");
                b.setMaxWidth(2000);
                b.setPrefWidth(150);
                HBox.setHgrow(b, Priority.ALWAYS);
/*
                InputStream inputStream = getClass().getResourceAsStream("/sample/images/Icons/calculator.png");
                Image image = new Image(inputStream);
                ImageView imageView = new ImageView(image);
                b.setGraphic(imageView);
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                image = null;
*/

                b.setOnAction(event -> {
                    HajmCalculator hajmCalculator = new HajmCalculator();
                    Double hajmDouble = hajmCalculator.display();
                    if (hajmCalculator.qaydEtdimBoolean) {
                        barCode.setHajm(hajmDouble);
                        barCodesTableView.refresh();
                    }
                });
                return new SimpleObjectProperty<Button>(b);
            }
        });

        hajmCalcColumn.setMinWidth(20);
        hajmCalcColumn.setMaxWidth(40);
        hajmCalcColumn.setStyle( "-fx-alignment: CENTER;");
        return hajmCalcColumn;
    }

    private TableColumn<BarCode, HBoxTextFieldPlusButton> getTextButtonColumn() {
        TableColumn<BarCode, HBoxTextFieldPlusButton> textButtonColumn = new TableColumn<>("TextButtun");
        textButtonColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<BarCode, HBoxTextFieldPlusButton>, ObservableValue<HBoxTextFieldPlusButton>>() {

            @Override
            public ObservableValue<HBoxTextFieldPlusButton> call(TableColumn.CellDataFeatures<BarCode, HBoxTextFieldPlusButton> param) {
                BarCode barCode = param.getValue();
                HBoxTextFieldPlusButton htb = new HBoxTextFieldPlusButton();
                htb.getTextField().setText(barCode.getBarCode());
                htb.getPlusButton().setOnAction(event -> {
                    ObservableList<BarCode> bcList = barCodeModels.getAnyData(connection, "SUBSTR(barCode,1,2) = '" + ConnectionType.getAloqa().getDbPrefix() + "'", "id desc");
                    if (bcList.size()>0) {
                        String string = bcList.get(0).getBarCode();
                        Integer number = Integer.valueOf(string.substring(2));
                        number++;
                        string = ConnectionType.getAloqa().getDbPrefix() + TovarController.padLeft(number.toString().trim(), 5, '0');
                        htb.getTextField().setText(string);
                    } else {
                        Integer number = 1;
                        String string = ConnectionType.getAloqa().getDbPrefix() + TovarController.padLeft(number.toString().trim(), 5, '0');
                        htb.getTextField().setText(string);
                    }
                });
                htb.getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        barCode.setBarCode(newValue);
                        System.out.println(newValue);
                    }
                });
                return new SimpleObjectProperty<HBoxTextFieldPlusButton>(htb);
            }
        });

        textButtonColumn.setCellFactory(column -> {
            TableCell<BarCode, HBoxTextFieldPlusButton> cell = new TableCell<BarCode, HBoxTextFieldPlusButton>() {
                @Override
                protected void updateItem(HBoxTextFieldPlusButton item, boolean empty) {
                    HBoxTextFieldPlusButton hBoxTextFieldPlusButton = new HBoxTextFieldPlusButton();
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                        setGraphic(null);
                    }
                    else {
                        setText(item.getTextField().getText());
                        setGraphic(item);
                    }
                }
            };
            return cell;
        });
        textButtonColumn.setMinWidth(120);
        textButtonColumn.setStyle( "-fx-alignment: CENTER;");
        return textButtonColumn;
    }

    private void initTextFields() {
        HBox.setHgrow(tovarNomiTextField, Priority.ALWAYS);
        tovarNomiTextField.setPromptText("Tovar nomi");
        TextFields.bindAutoCompletion(tovarNomiTextField, tovarObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Standart> autoCompletionEvent) -> {
            Standart tovar = autoCompletionEvent.getCompletion();
        });

        HBox.setHgrow(xaridNarhiTextField, Priority.ALWAYS);
        HBox.setHgrow(ulgurjiNarhTextField, Priority.ALWAYS);
        HBox.setHgrow(chakanaNarhTextField, Priority.ALWAYS);
        HBox.setHgrow(bojTextField, Priority.ALWAYS);
        xaridNarhiTextField.setPromptText("Xarid narhi");
        chakanaNarhTextField.setPromptText("Штучный");
        ulgurjiNarhTextField.setPromptText("Оптом");
        tovarNomiTextField.setPromptText("Tovar nomi");
        ndsTextField.setPromptText("NDS");
        bojTextField.setPromptText("Bojxona solig`i");
        xaridNarhiTextField.setTextFormatter(new TextFieldDouble().getTextFormatter());
        chakanaNarhTextField.setTextFormatter(new TextFieldDouble().getTextFormatter());
        ulgurjiNarhTextField.setTextFormatter(new TextFieldDouble().getTextFormatter());
        ndsTextField.setTextFormatter(new TextFieldDouble().getTextFormatter());
        bojTextField.setTextFormatter(new TextFieldDouble().getTextFormatter());
    }

    private void initTovarGuruhiHBox() {
        Standart6Models standart6Models = new Standart6Models("TGuruh1");
        ObservableList<Standart6> s6List = standart6Models.get_data(connection);
        HBox.setHgrow(tovarGurugiHBox, Priority.ALWAYS);
        TextField textField = tovarGurugiHBox.getTextField();
        textField.setPromptText("TOVAR GURUHI");
        TextFields.bindAutoCompletion(textField, s6List).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Standart6> autoCompletionEvent) -> {
            Standart6 newValue = autoCompletionEvent.getCompletion();
            if (newValue != null) {
                s6Cursor = newValue;
            }
        });

        Button addButton = tovarGurugiHBox.getPlusButton();
        addButton.setOnAction(event -> {
            YangiTovarGuruhi yangiTovarGuruhi = new YangiTovarGuruhi(connection, user);
            Standart6 newValue = yangiTovarGuruhi.display();
            if (newValue != null) {
                s6Cursor = newValue;
                s6List.add(newValue);
                textField.setText(newValue.getText());
            }
        });

    }

    private void initButtons() {
        HBox.setHgrow(barCodeAddButton, Priority.ALWAYS);
        barCodeAddButton.getChildren().remove(1);
        barCodeAddButton.getChildren().remove(1);
        barCodeAddButton.setMaxWidth(2000);
        barCodeAddButton.setPrefWidth(150);
        barCodeAddButton.getAdd().setOnAction(event -> {
            int tarkibInt = barCodeObservableList.size();
            barCodeId++;
            birlikIndex++;
            BarCode barCode = new BarCode(barCodeId, 0, "", birlikIndex, tovarAdadi, tarkibInt, .0, .0, user.getId(), new Date());
            tovarAdadi = .0;
            barCodeObservableList.add(barCode);
            tarkibObservableList.add(barCode);
            barCodesTableView.refresh();
        });

        HBox.setHgrow(qaydEtButton, Priority.ALWAYS);
        qaydEtButton.setMaxWidth(2000);
        qaydEtButton.setPrefWidth(150);
        qaydEtButton.setMaxHeight(60);
        qaydEtButton.setPrefHeight(150);
        qaydEtButton.setFont(font);

        qaydEtButton.setOnAction(event -> {
            RadioButton rb = (RadioButton) toggleGroup.getSelectedToggle();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Diqqat qiling !!!");
            String tovarNomiString = tovarNomiTextField.getText();
            if (tovarNomiString.isEmpty()) {
                alert.setHeaderText("Tovar nomi kiritilmadi");
                alert.setContentText("");
                alert.showAndWait();
                return;
            }
            for (BarCode bc: barCodeObservableList) {
                if (bc.getBarCode().isEmpty()) {
                    alert.setHeaderText("Shtrixkod kiritilmadi");
                    alert.setContentText("");
                    alert.showAndWait();
                    return;
                } else {
                    if (bc != null) {
                        if (GetDbData.getBarCode(bc.getBarCode()) != null) {
                            Alerts.barCodeIsExist(bc.getBarCode());
                            return;
                        }
                    }
                }

            }
            if (rb.getId().equals("1")) {
                if (tovarGurugiHBox.getTextField().getText().isEmpty()) {
                    alert.setHeaderText("Guruh nomi kiritilmadi");
                    alert.setContentText("");
                    alert.showAndWait();
                    return;
                }
            }
            Date sana = new Date();
            Standart tovar = new Standart(null, tovarNomiString, user.getId(), sana);
            standartModels.setTABLENAME("Tovar");
            standartModels.insert_data(connection, tovar);
            tovarCursor = tovar;
            GetDbData.getTovarObservableList().add(tovar);

            int recordsCount = barCodeModels.getRecordsCount(connection);
            int currentRecord = 0;
            for (BarCode bc: barCodeObservableList) {
                bc.setTovar(tovar.getId());
                if (bc.getTarkib() != 0) {
                    bc.setTarkib(currentRecord);
                }
                barCodeModels.insert_data(connection, bc);
                currentRecord = bc.getId();
            }
            GetDbData.getBarCodeObservableList().addAll(barCodeObservableList);

            standart4Models.setTABLENAME("Tartib");
            Double miqdor = tartibComboBox.getValue().getId().doubleValue();
            standart4Models.insert_data(connection, new Standart4(null, tovar.getId(), new Date(), miqdor, user.getId(), null));

            Standart3Models standart3Models = new Standart3Models();

            if (rb.getId().equals("1")) {
                standart3Models.setTABLENAME("TGuruh2");
                Standart3 s3 = new Standart3(
                        null, s6Cursor.getId(), tovar.getId(), tovarNomiTextField.getText(), user.getId(), sana
                );
                standart3Models.insert_data(connection, s3);
            }

            if (rb.getId().equals("2")) {
                guruhsizTovarNarhlari(tovar);
            }

            tovarNomiTextField.setText("");
            barCodeObservableList.removeAll(barCodeObservableList);
            barCodesTableView.refresh();
            barCodeId = 0;
            birlikIndex = 0;
            tovarAdadi = 1.0;
            qaydEtdim = true;
            stage.close();
        });
    }

    private String getBirlik(int  id)  {
        String birlik = "";
        for (Standart b: birlikObservableList) {
            if (b.getId().equals(id)) {
                birlik = b.getText();
                break;
            }
        }
        return birlik;
    }

    private void guruhsizTovarNarhlari(Standart tovar) {
        Date date = new Date();
        Integer narhTuri = 0;
        ObservableList<TextField> observableList = FXCollections.observableArrayList(
                xaridNarhiTextField,
                chakanaNarhTextField,
                ulgurjiNarhTextField,
                ndsTextField,
                bojTextField
        );
        for (TextField textField: observableList) {
            String s = textField.getText();
            if (!s.isEmpty()) {
                s = s.replaceAll(" ", "");
                s = s.replaceAll(",", ".");
                if (!Alerts.isNumeric(s)) {
                    s = "0.0";
                }
                Double newValue = Double.valueOf(s);
                if (newValue != 0d) {
                    TovarNarhi yangiNarh = new TovarNarhi(null, date, tovar.getId(), narhTuri, 1, 1.0, newValue, user.getId(), null);
                    tovarNarhiModels.insert_data(connection, yangiNarh);
                }
            }
            textField.setText("0.0");
            narhTuri++;
        }
    }
}
