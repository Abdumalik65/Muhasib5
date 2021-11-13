package sample.Temp;

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
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import sample.Config.MySqlDBGeneral;
import sample.Controller.HisobController;
import sample.Controller.YangiTovar1;
import sample.Data.*;
import sample.Enums.ServerType;
import sample.Model.BarCodeModels;
import sample.Model.HisobKitobModels;
import sample.Model.StandartModels;
import sample.Tools.GetDbData;
import sample.Tools.MoneyShow;
import sample.Tools.PathToImageView;
import sample.Tools.SetHVGrow;

import java.sql.Connection;
import java.util.Date;

public class YangiSanoq extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    GridPane gridPane = new GridPane();
    VBox centerPane = new VBox();
    Connection connection;
    User user;
    int padding = 3;

    TextField hisob1TextField = new TextField();
    TextField hisob2TextField = new TextField();
    TextField tovarTextField = new TextField();
    TextField barCodeTextField = new TextField();
    TextArea izohTextArea = new TextArea();

    HBox hisob1Hbox;
    HBox hisob2Hbox;
    HBox tovarHBox;
    HBox birlikHbox;

    ComboBox<Standart> birlikComboBox = new ComboBox<>();

    TableView<HisobKitob> sanoqTableView = new TableView<>();
    Button sanoqniYakunlaButton = new Button("Sanoqni yakunla");
    Button sanoqniBekorQilButton = new Button("Sanoqni bekor qil");

    Hisob hisob1;
    Hisob hisob2;
    Standart tovar;
    Font font = Font.font("Arial", FontWeight.BOLD,20);

    ObservableList<HisobKitob> sanoqObservableList = FXCollections.observableArrayList();
    ObservableList<Hisob> hisobObservableList;
    ObservableList<Standart> tovarObservableList;
    ObservableList<Standart> birlikObservableList;

    HisobKitobModels hisobKitobModels = new HisobKitobModels();
    StandartModels standartModels = new StandartModels();
    BarCodeModels barCodeModels = new BarCodeModels();

    public static void main(String[] args) {
        launch(args);
    }

    public YangiSanoq() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
    }

    public YangiSanoq(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
    }

    private void ibtido() {
        initData();
        initHisob1Hbox();
        initHisob2Hbox();
        initIzohTextArea();
        initTovarHbox();
        initBirlikComboBox();
        initBarCodeTextField();
        initSanoqTableView();
        initYakunlaButton();
        initGridPane();
        initCenterPane();
        initBorderPane();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        GetDbData.initData(connection);
        ibtido();
        initStage(primaryStage);
        stage.show();
    }

    public void display() {
        stage = new Stage();
        GetDbData.initData(connection);
        ibtido();
        initStage(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    private void initData() {
        hisobObservableList = GetDbData.getHisobObservableList();
        tovarObservableList = GetDbData.getTovarObservableList();
        standartModels.setTABLENAME("Birlik");
        birlikObservableList = standartModels.get_data(connection);
    }

    private void initCenterPane() {
        SetHVGrow.VerticalHorizontal(centerPane);
        centerPane.setPadding(new Insets(padding));
        centerPane.getChildren().addAll(gridPane, barCodeTextField,sanoqTableView, sanoqniYakunlaButton);
    }

    private void initSanoqTableView() {
        sanoqTableView.setDisable(true);
        SetHVGrow.VerticalHorizontal(sanoqTableView);
        sanoqTableView.getColumns().addAll(getBarCodeColumn(), getAdadColumn(), getBalanceColumn());
        sanoqTableView.setItems(sanoqObservableList);
    }

    private void initHisob1Hbox() {
        hisob1Hbox = new HBox();
        hisob1TextField.setFont(font);
        hisob1TextField.setPromptText("Chiqim hisobi");
        HBox.setHgrow(hisob1Hbox, Priority.ALWAYS);
        HBox.setHgrow(hisob1Hbox, Priority.ALWAYS);
        Button addButton = new Button();
        addButton.setMinHeight(37);
        addButton.setGraphic(new PathToImageView("/sample/images/Icons/add.png").getImageView());
        HBox.setHgrow(addButton, Priority.ALWAYS);
        HBox.setHgrow(hisob1TextField, Priority.ALWAYS);
        TextFields.bindAutoCompletion(hisob1TextField, hisobObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Hisob> autoCompletionEvent) -> {
            Hisob newValue = autoCompletionEvent.getCompletion();
            if (newValue != null) {
                hisob1 = newValue;
                hisob2Hbox.setDisable(false);
            }
        });
        hisob1Hbox.getChildren().addAll(hisob1TextField, addButton);
        addButton.setOnAction(event -> {
            Hisob newValue = addHisob();
            if (newValue != null) {
                hisob1 = newValue;
                hisob2Hbox.setDisable(false);
                hisob1TextField.setText(hisob1.getText());
            }
        });
    }

    private void initHisob2Hbox() {
        hisob2Hbox = new HBox();
        hisob2Hbox.setDisable(true);
        hisob2TextField.setFont(font);
        hisob2TextField.setPromptText("Chiqim hisobi");
        HBox.setHgrow(hisob2Hbox, Priority.ALWAYS);
        Button addButton = new Button();
        addButton.setMinHeight(37);
        addButton.setGraphic(new PathToImageView("/sample/images/Icons/add.png").getImageView());
        HBox.setHgrow(addButton, Priority.ALWAYS);
        HBox.setHgrow(hisob2TextField, Priority.ALWAYS);
        TextFields.bindAutoCompletion(hisob2TextField, hisobObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Hisob> autoCompletionEvent) -> {
            hisob2 = autoCompletionEvent.getCompletion();
            tovarHBox.setDisable(false);
            birlikComboBox.setDisable(false);
            barCodeTextField.setDisable(false);
            sanoqTableView.setDisable(false);
        });
        hisob2Hbox.getChildren().addAll(hisob2TextField, addButton);
        addButton.setOnAction(event -> {
            hisob2 = addHisob();
            tovarHBox.setDisable(false);
            birlikComboBox.setDisable(false);
            barCodeTextField.setDisable(false);
            sanoqTableView.setDisable(false);
            hisob2TextField.setText(hisob2.getText());
        });
    }

    private void initTovarHbox() {
        tovarHBox = new HBox();
        tovarHBox.setDisable(true);
        tovarTextField.setFont(font);
        tovarTextField.setPromptText("Tovar nomi");
        HBox.setHgrow(tovarTextField, Priority.ALWAYS);
        HBox.setHgrow(tovarHBox, Priority.ALWAYS);
        Button addButton = new Button();
        addButton.setMinHeight(37);
        addButton.setGraphic(new PathToImageView("/sample/images/Icons/add.png").getImageView());
        HBox.setHgrow(tovarTextField, Priority.ALWAYS);

        TextFields.bindAutoCompletion(tovarTextField, tovarObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Standart> autoCompletionEvent) -> {
            ObservableList<Standart> birlikList = FXCollections.observableArrayList();
            tovar = autoCompletionEvent.getCompletion();

            ObservableList<BarCode> barCodeList = barCodeModels.getAnyData(connection, "tovar = " + tovar.getId(), "");
            for (BarCode bc: barCodeList) {
                HisobKitob balance = hisobKitobModels.getBarCodeBalans(connection, hisob1.getId(), bc, new Date());
//                if (balance.getNarh()>.0) {
                    Standart b = GetDbData.getBirlik(bc.getBirlik(), birlikObservableList);
                    if (b != null) {
                        birlikList.add(b);
                    }
//                }
            }
            birlikComboBox.setItems(birlikList);
            if (birlikList.size()>0) {
                birlikComboBox.getSelectionModel().selectFirst();
                barCodeTextField.setText(barCodeList.get(0).getBarCode());
            };

            birlikComboBox.setOnAction(event -> {
                Standart birlik = birlikComboBox.getValue();
                int pos = birlikComboBox.getSelectionModel().getSelectedIndex();
                if (birlik != null) {
                    barCodeTextField.setText(barCodeList.get(pos).getBarCode());
                }
            });
        });

        tovarHBox.getChildren().addAll(tovarTextField, addButton);
        addButton.setOnAction(event -> {
            YangiTovar1 yangiTovar = new YangiTovar1(connection, user);
            Standart tovar = yangiTovar.display();
            if (tovar != null) {
                tovarTextField.setText(tovar.getText());
            }
        });
    }

    private void initBarCodeTextField() {
        barCodeTextField.setDisable(true);
        barCodeTextField.setFont(font);
        barCodeTextField.setPromptText("Shtrixkod");
        HBox.setHgrow(barCodeTextField, Priority.ALWAYS);
    }

    private void initBirlikComboBox() {
        birlikComboBox.setDisable(true);
        String style20 = "-fx-font: 20px Arial";
        birlikComboBox.setPromptText("Birlik");
        birlikComboBox.setStyle(style20);
        birlikHbox = new HBox();
        HBox.setHgrow(birlikHbox, Priority.ALWAYS);
        HBox.setHgrow(birlikComboBox, Priority.ALWAYS);
        birlikComboBox.setMaxWidth(2000);
        birlikComboBox.setPrefWidth(150);
        HBox.setHgrow(birlikComboBox, Priority.ALWAYS);
        birlikHbox.getChildren().add(birlikComboBox);
    }

    private void initYakunlaButton() {
        sanoqniYakunlaButton.setMaxWidth(2000);
        sanoqniYakunlaButton.setPrefWidth(150);
        HBox.setHgrow(sanoqniYakunlaButton, Priority.ALWAYS);
        sanoqniYakunlaButton.setFont(font);
        sanoqniYakunlaButton.setOnAction(event -> {
            System.out.println("Bismillah");
        });
    }

    private void initGridPane() {
        HBox.setHgrow(gridPane, Priority.ALWAYS);
        int rowIndex = 0;

        gridPane.add(hisob1Hbox, 0, rowIndex, 1, 1);
        GridPane.setHgrow(hisob1Hbox, Priority.ALWAYS);
        gridPane.add(hisob2Hbox, 1, rowIndex, 1,1);
        GridPane.setHgrow(hisob2Hbox, Priority.ALWAYS);

        rowIndex++;
        gridPane.add(izohTextArea, 0, rowIndex, 2, 1);

        rowIndex++;
        gridPane.add(tovarHBox, 0, rowIndex, 1, 1);
        GridPane.setHgrow(tovarHBox, Priority.ALWAYS);
        gridPane.add(birlikHbox, 1, rowIndex, 1,1);
        GridPane.setHgrow(birlikHbox, Priority.ALWAYS);
    }

    private void initBorderPane() {
        borderpane.setCenter(centerPane);
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Yangi sanoq");
        scene = new Scene(borderpane, 600, 400);
        stage.setScene(scene);
    }

    private Hisob addHisob() {
        Hisob hisob = null;
        HisobController hisobController = new HisobController();
        hisobController.display(connection, user);
        if (hisobController.getDoubleClick()) {
            hisob = hisobController.getDoubleClickedRow();
        }
        return hisob;
    }

    public TableColumn<HisobKitob, String> getBarCodeColumn() {
        TableColumn<HisobKitob, String> barCodeColumn = new TableColumn<>("BarCode");
        barCodeColumn.setMinWidth(150);
        barCodeColumn.setCellValueFactory(new PropertyValueFactory<>("barCode"));
        barCodeColumn.setCellFactory(column -> {
            TableCell<HisobKitob, String> cell = new TableCell<HisobKitob, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        BarCode barCode = GetDbData.getBarCode(item);
                        if (barCode != null) {
                            Standart tovar = GetDbData.getTovar(barCode.getTovar());
                            Standart birlik = GetDbData.getBirlik(barCode.getBirlik());
                            Double dona = barCode.getAdad();
                            setText(tovar.getText() + "\nShtrixkod: " + item + "\n" + dona + " " + birlik.getText().toLowerCase());
                        }else {
                            setText("");
                        }
                    }
                }
            };
            return cell;
        });
        return barCodeColumn;
    }

    public TableColumn<HisobKitob, Double> getAdadColumn() {
        TableColumn<HisobKitob, Double> adadColumn = new TableColumn<>("Dona");
        adadColumn.setMinWidth(100);
        adadColumn.setCellValueFactory(new PropertyValueFactory<>("adad"));
        adadColumn.setCellFactory(column -> {
            TableCell<HisobKitob, Double> cell = new TableCell<HisobKitob, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        setText(new MoneyShow().format(item));
                    }
                }
            };
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        return adadColumn;
    }

    public TableColumn<HisobKitob, Double> getNarhColumn() {
        TableColumn<HisobKitob, Double> narhColumn = new TableColumn<>("Narh");
        narhColumn.setMinWidth(150);
        narhColumn.setCellValueFactory(new PropertyValueFactory<>("narh"));
        narhColumn.setCellFactory(column -> {
            TableCell<HisobKitob, Double> cell = new TableCell<HisobKitob, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        setText(new MoneyShow().format(item));
                    }
                }
            };
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        return narhColumn;
    }

    private TableColumn<HisobKitob, Double> getHisobdaColumn() {
        TableColumn<HisobKitob, Double> valuta = new TableColumn<>("Zaxirada");
        valuta.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, Double>, ObservableValue<Double>>() {
            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<HisobKitob, Double> param) {
                HisobKitob hisobKitob = param.getValue();
                Double d = hisobKitobModels.getBarCodeCount(connection, hisob1.getId(), hisobKitob.getBarCode());
                return new SimpleObjectProperty<Double>(d);
            }
        });
        valuta.setMinWidth(100);
        valuta.setStyle( "-fx-alignment: CENTER;");
        return valuta;
    }

    private TableColumn<HisobKitob, Double> getBalanceColumn() {
        TableColumn<HisobKitob, Double> valuta = new TableColumn<>("Balans");
        valuta.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, Double>, ObservableValue<Double>>() {
            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<HisobKitob, Double> param) {
                HisobKitob hisobKitob = param.getValue();
                Double d = hisobKitobModels.getBarCodeCount(connection, 13, hisobKitob.getBarCode());
                return new SimpleObjectProperty<Double>(d);
            }
        });
        valuta.setMinWidth(100);
        valuta.setStyle( "-fx-alignment: CENTER;");
        return valuta;
    }

    private void addSanoq(BarCode barCode) {
        Sanoq sanoq = new Sanoq(null, null, barCode.getBarCode(), 1.0, .0, user.getId(), new Date());
    }

    private void initIzohTextArea() {
        SetHVGrow.VerticalHorizontal(izohTextArea);
        izohTextArea.setWrapText(true);
    }

}
