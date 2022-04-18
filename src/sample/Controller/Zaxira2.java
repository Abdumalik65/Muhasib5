package sample.Controller;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
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
import sample.Config.MySqlDBGeneral;
import sample.Data.*;
import sample.Enums.ServerType;
import sample.Model.*;
import sample.Tools.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Zaxira2 extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    SplitPane splitPane = new SplitPane();
    VBox markaziyLawha = new VBox();
    VBox markaziyLawha1 = new VBox();
    VBox markaziyLawha2 = new VBox();
    VBox ongLawha = new VBox();
    VBox leftPane = new VBox();
    TableView<Standart> leftTable;
    TableView<HisobKitob> centerTable;
    TableView<HisobKitob> rightTable;
    GridPane gridPane;

    Double donaDouble = 0d;
    Double ortachaNarhDouble = 0d;
    Double jamiNarhDouble = 0d;
    Double chakanaNarhDouble = 0d;
    Double ulgurjiNarhDouble = 0d;
    DecimalFormat decimalFormat = new MoneyShow();
    Font font = Font.font("Arial", FontWeight.BOLD,12);


    HisobKitobModels hisobKitobModels = new HisobKitobModels();

    Connection connection;
    User user;
    Standart tovar;
    Hisob hisob;
    BarCode barCode;
    int padding = 3;


    public static void main(String[] args) {
        launch(args);
    }

    public Zaxira2() {
        connection = new MySqlDBGeneral(ServerType.REMOTE).getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
        ibtido();
    }

    public Zaxira2(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        String classSimpleName = getClass().getSimpleName();
        DasturlarRoyxati.dastur(connection, user, classSimpleName);
        ibtido();
    }

    private void ibtido() {
        initTopPane();
        initLeftPane();
        initCenterPane();
        yangiOngLawha();
        initBottomPane();
        initBorderPane();
//        gridPane = initGridPane();
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

    private void initTopPane() {
    }

    private void initLeftPane() {
        leftPane.setPadding(new Insets(padding));
        SetHVGrow.VerticalHorizontal(leftPane);
        leftTable = initLeftTable();
        Tugmachalar leftButtons = initLeftButtons();
        leftPane.getChildren().addAll(leftButtons, leftTable);
    }

    private Tugmachalar initLeftButtons() {
        Tugmachalar buttons = new Tugmachalar();
        Button add = buttons.getAdd();
        Button delete = buttons.getDelete();
        Button edit = buttons.getEdit();
        Button excel= buttons.getExcel();
        excel.setOnAction(event -> {
        });
        TextField tovarTextField = new TextField();
        tovarTextField.setPromptText("Tovar nomi");
        ObservableList<Standart> hisobKitobObservableList = leftTable.getItems();
        TextFields.bindAutoCompletion(tovarTextField, hisobKitobObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Standart> autoCompletionEvent) -> {
            Standart standart = autoCompletionEvent.getCompletion();
            if (standart!=null) {
                tovarTaftish(standart);
            }
        });
        HBox.setHgrow(tovarTextField, Priority.ALWAYS);
        TextField barCodeTextField = new TextField();
        barCodeTextField.setPromptText("Barcode");
        barCodeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue!=null) {
                barCodeTaftish(newValue);
            }
        });
        HBox.setHgrow(barCodeTextField, Priority.ALWAYS);
        buttons.getChildren().removeAll(add, edit, delete);
        buttons.getChildren().addAll(tovarTextField, barCodeTextField);
        return buttons;
    }

    private void initCenterPane() {
        markaziyLawha.setPadding(new Insets(padding));
        SetHVGrow.VerticalHorizontal(markaziyLawha);
        initCenterPane1();
        initCenterPane2();
        gridPane = yangiOngJadval();
        markaziyLawha.getChildren().addAll(gridPane, markaziyLawha1, markaziyLawha2);
    }

    private void yangiOngLawha() {
        SetHVGrow.VerticalHorizontal(ongLawha);
    }

    private GridPane yangiOngJadval() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(2);
        gridPane.setVgap(2);
        gridPane.setPadding(new Insets(padding));
        Integer rowIndex = 0;
        Label donaLabel = new Label("Dona");
        Label ortachaNarhLabel = new Label("O`rtacha narh");
        Label jamiNarhLabel = new Label("Jami narh");
        Label chakanaNarhLabel = new Label("Chakana narh");
        Label ulgurjiNarhLabel = new Label("Ulgurji narh");

        TextField textField1 = new TextField();
        TextField textField2 = new TextField();
        TextField textField3 = new TextField();
        TextFieldButton textFieldButton4 = chakanaNarh();
        TextFieldButton textFieldButton5 = ulgurjiNarh();

        textField1.setFont(font);
        textField2.setFont(font);
        textField3.setFont(font);

        textField1.setEditable(false);
        textField2.setEditable(false);
        textField3.setEditable(false);

        textField1.setAlignment(Pos.CENTER_RIGHT);
        textField2.setAlignment(Pos.CENTER_RIGHT);
        textField3.setAlignment(Pos.CENTER_RIGHT);

        donaLabel.setFont(font);
        ortachaNarhLabel.setFont(font);
        jamiNarhLabel.setFont(font);
        chakanaNarhLabel.setFont(font);
        ulgurjiNarhLabel.setFont(font);

        gridPane.add(donaLabel, 0, rowIndex, 1, 1);
        gridPane.add(ortachaNarhLabel, 1, rowIndex, 1, 1);
        gridPane.add(jamiNarhLabel, 2, rowIndex, 1, 1);
        gridPane.add(chakanaNarhLabel, 3, rowIndex, 1, 1);
        gridPane.add(ulgurjiNarhLabel, 4, rowIndex, 1, 1);

        rowIndex++;
        gridPane.add(textField1, 0, rowIndex, 1, 1);
        gridPane.add(textField2, 1, rowIndex, 1, 1);
        gridPane.add(textField3, 2, rowIndex, 1, 1);
        gridPane.add(textFieldButton4, 3, rowIndex, 1, 1);
        gridPane.add(textFieldButton5, 4, rowIndex, 1, 1);

        GridPane.setHgrow(textField1, Priority.ALWAYS);
        GridPane.setHgrow(textField2, Priority.ALWAYS);
        GridPane.setHgrow(textField3, Priority.ALWAYS);
        GridPane.setHgrow(textFieldButton4, Priority.ALWAYS);
        GridPane.setHgrow(textFieldButton5, Priority.ALWAYS);
        HBox.setHgrow(gridPane, Priority.ALWAYS);
        return gridPane;
    }
    private TextFieldButton chakanaNarh() {
        TextFieldButton textFieldButton = new TextFieldButton();
        TextField textField = textFieldButton.getTextField();
        textField.setAlignment(Pos.CENTER_RIGHT);
        textField.setEditable(true);
        textField.setFont(font);
        Button button = textFieldButton.getPlusButton();
        button.setGraphic(null);
        button.setText("\uD83D\uDCBE ");
        button.setOnAction(event -> {
            Standart tovar = leftTable.getSelectionModel().getSelectedItem();
            if (tovar != null) {
                narhYoz(tovar.getId(), 1, chakanaNarhDouble);
            }
            textField.setText(decimalFormat.format(chakanaNarhDouble));
        });
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                chakanaNarhDouble = StringNumberUtils.textToDouble(newValue);
            }
        });
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue) {
                    textField.selectAll();
                } else {
                    if (chakanaNarhDouble==0d) {
                        textField.setText("");
                    } else {
                        textField.setText(decimalFormat.format(chakanaNarhDouble));
                    }
                }
            }
        });
        return textFieldButton;
    }
    private TextFieldButton ulgurjiNarh() {
        TextFieldButton textFieldButton = new TextFieldButton();
        TextField textField = textFieldButton.getTextField();
        textField.setAlignment(Pos.CENTER_RIGHT);
        textField.setEditable(true);
        textField.setFont(font);
        Button button = textFieldButton.getPlusButton();
        button.setGraphic(null);
        button.setText("\uD83D\uDCBE ");
        button.setOnAction(event -> {
            ulgurjiNarhDouble = StringNumberUtils.getDoubleFromTextField(textField);
            Standart tovar = leftTable.getSelectionModel().getSelectedItem();
            if (tovar != null) {
                narhYoz(tovar.getId(), 2, ulgurjiNarhDouble);
            }
            textField.setText(decimalFormat.format(ulgurjiNarhDouble));
        });
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                ulgurjiNarhDouble = StringNumberUtils.textToDouble(newValue);
            }
        });
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue) {
                    textField.selectAll();
                } else {
                    if (ulgurjiNarhDouble==0d) {
                        textField.setText("");
                    } else {
                        textField.setText(decimalFormat.format(ulgurjiNarhDouble));
                    }
                }
            }
        });
        return textFieldButton;
    }

    private void initCenterPane1() {
        markaziyLawha1.setPadding(new Insets(padding));
        SetHVGrow.VerticalHorizontal(markaziyLawha1);
        Tugmachalar centerButtons = initCenterButtons();
        centerTable = initCenterTable();
        SetHVGrow.VerticalHorizontal(centerTable);

        markaziyLawha1.getChildren().addAll(centerButtons, centerTable);
    }

    private Tugmachalar initCenterButtons() {
        Tugmachalar buttons = new Tugmachalar();
        Button add = buttons.getAdd();
        Button delete = buttons.getDelete();
        Button edit = buttons.getEdit();
        Button excel= buttons.getExcel();
        TextField dokonTextField = new TextField();
        dokonTextField.textProperty().addListener((observable, oldValue, newValue) -> {
        });
        dokonTextField.setPromptText("Do`kon nomi");
        HBox.setHgrow(dokonTextField, Priority.ALWAYS);
        buttons.getChildren().removeAll(add, edit, delete);
        buttons.getChildren().add(dokonTextField);
        return buttons;
    }

    private void initCenterPane2() {
        markaziyLawha2.setPadding(new Insets(padding));
        SetHVGrow.VerticalHorizontal(markaziyLawha2);
        Tugmachalar rightButtons = initRightButtons();
        rightTable = initRightTable();
        markaziyLawha2.getChildren().addAll(rightButtons, rightTable);
    }

    private Tugmachalar initRightButtons() {
        Tugmachalar buttons = new Tugmachalar();
        Button add = buttons.getAdd();
        Button delete = buttons.getDelete();
        Button edit = buttons.getEdit();
        Button excel= buttons.getExcel();
        buttons.getChildren().removeAll(add, edit, delete);
        return buttons;
    }

    private void initSplitPane() {
        splitPane.setPadding(new Insets(padding));
        SetHVGrow.VerticalHorizontal(splitPane);
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.getItems().addAll(markaziyLawha, leftPane);
        SplitPane.setResizableWithParent(leftPane, true);
        SplitPane.setResizableWithParent(markaziyLawha, true);
        splitPane.setDividerPositions(.75);
    }

    private void initBottomPane() {
    }

    private void initBorderPane() {
        initSplitPane();
        borderpane.setCenter(splitPane);
        Standart standart = leftTable.getItems().get(0);
        if (standart!=null) {
            tovarTaftish(standart);
        }
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        stage.setTitle("Zaxiradagi tovarlar");
        scene = new Scene(borderpane);
        scene.getStylesheets().add("/sample/Styles/modena.css");
        stage.setScene(scene);
    }

    // 90 919 3020 Odil B27
    private TableView<Standart> initLeftTable() {
        TableView<Standart> tableView = new TableView<>();
        tableView.setMinHeight(200);
        tableView.setMinHeight(540);
        tableView.setItems(getLeftTableData());
        tableView.getColumns().addAll(tovarColumn());
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                tovar = newValue;
                TovarNarhi tn = yakkaTovarNarhi(tovar.getId(), 2);
                if (tn != null) {
                    ulgurjiNarhDouble = tn.getNarh();
                } else {
                    ulgurjiNarhDouble = 0d;
                }
                tn = yakkaTovarNarhi(tovar.getId(), 1);
                if (tn != null) {
                    chakanaNarhDouble = tn.getNarh();
                } else {
                    chakanaNarhDouble = 0d;
                }
                ObservableList<HisobKitob> observableList = getCenterTableData(LocalDate.now(), newValue.getId());
                refreshGridPane(observableList);
                if (observableList.size()>0) {
                    centerTable.setItems(observableList);
                    centerTable.getSelectionModel().selectFirst();
                } else {
                    centerTable.setItems(FXCollections.observableArrayList());
                    centerTable.refresh();
                    rightTable.setItems(FXCollections.observableArrayList());
                    rightTable.refresh();
                }
            }
        });
        SetHVGrow.VerticalHorizontal(tableView);
        tableView.setEditable(true);
        return tableView;
    }

    private void refreshGridPane(ObservableList<HisobKitob> observableList) {
        donaDouble = 0d;
        jamiNarhDouble = 0d;
        ortachaNarhDouble = 0d;
        for (HisobKitob hk: observableList) {
            donaDouble += hk.getDona();
            jamiNarhDouble += hk.getNarh();
        }
        if (donaDouble!=0 && jamiNarhDouble != 0) {
            ortachaNarhDouble = jamiNarhDouble / donaDouble;
        }
        TextField donaDoubleTextField = (TextField) gridPane.getChildren().get(5); // donaDouble
        TextFieldButton textFieldButton0 = (TextFieldButton) gridPane.getChildren().get(8);
        TextField chakanaTextField = textFieldButton0.getTextField();
        TextField ortachaNarhDoubleTextField = (TextField) gridPane.getChildren().get(6); // ortachaNarhDouble
        TextField jamiNarhDoubleTextField = (TextField) gridPane.getChildren().get(7); // jamiNarhDouble
        TextFieldButton textFieldButton = (TextFieldButton) gridPane.getChildren().get(9);
        TextField ulgurjiTextField = textFieldButton.getTextField();

        donaDoubleTextField.setText(decimalFormat.format(donaDouble));
        ortachaNarhDoubleTextField.setText(decimalFormat.format(ortachaNarhDouble));
        jamiNarhDoubleTextField.setText(decimalFormat.format(jamiNarhDouble));
        chakanaTextField.setText(decimalFormat.format(chakanaNarhDouble));
        ulgurjiTextField.setText(decimalFormat.format(ulgurjiNarhDouble));
    }

    private TableColumn<Standart, String> tovarColumn () {
        TableColumn<Standart, String> tovar = new TableColumn<>("Tovar");
        tovar.setMaxWidth(182);
        tovar.setMinWidth(182);
        tovar.setCellValueFactory(new PropertyValueFactory<>("text"));

        tovar.setCellFactory(tc -> {
            TableCell<Standart, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(tovar.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell ;
        });
        return tovar;
    }

    private ObservableList<Standart> getLeftTableData() {
        ObservableList<Standart> observableList = FXCollections.observableArrayList();
        StandartModels standartModels = new StandartModels("Tovar");
        observableList = standartModels.get_data(connection);
        return observableList;
    }

    private TableView<HisobKitob> initCenterTable() {
        TableView<HisobKitob> tableView = new TableView<>();
        SetHVGrow.VerticalHorizontal(tableView);
        TableViewAndoza tableViewAndoza = new TableViewAndoza();
        TableColumn<HisobKitob, String> izohColumn = tableViewAndoza.getIzoh2Column();
        izohColumn.setMinWidth(200);
        TableColumn<HisobKitob, Integer> hisob1Column = tableViewAndoza.getHisob1Column();
        TableColumn<HisobKitob, Double> adadColumn = tableViewAndoza.getAdadColumn();
        TableColumn<HisobKitob, Double> narhColumn = tableViewAndoza.getNarhColumn();
        tableView.getColumns().addAll(hisob1Column, adadColumn, narhColumn);
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                hisob = GetDbData.getHisob(newValue.getHisob1());
                rightTable.setItems(getRightTableData(LocalDate.now(), hisob.getId(), tovar.getId()));
                rightTable.refresh();
            }
        });
        return tableView;
    }

    private TableView<HisobKitob> initRightTable() {
        TableView<HisobKitob> tableView = new TableView<>();
        SetHVGrow.VerticalHorizontal(tableView);
        TableViewAndoza tableViewAndoza = new TableViewAndoza();
        TableColumn<HisobKitob, Date> sanaColumn = tableViewAndoza.getDateTimeColumn();
        TableColumn<HisobKitob, String> textColumn = tableViewAndoza.getIzoh2Column();
        TableColumn<HisobKitob, Double> kursColumn = tableViewAndoza.getKursColumn();
        TableColumn<HisobKitob, Integer> valutaColumn = tableViewAndoza.getValutaColumn();
        TableColumn<HisobKitob, Double> adadColumn = tableViewAndoza.getAdadColumn();
        TableColumn<HisobKitob, Double> narhColumn = tableViewAndoza.getNarhColumn();
        textColumn.setText("Izoh");
        textColumn.setMinWidth(200);
        tableView.getColumns().addAll(sanaColumn, valutaColumn, kursColumn, adadColumn, narhColumn);
        if (tovar != null && hisob != null) {
            tableView.setItems(getRightTableData(LocalDate.now(), hisob.getId(), tovar.getId()));
            tableView.refresh();
        }
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("leftTableAddListener");
        });
        tableView.setRowFactory(tv -> new TableRow<HisobKitob>() {
            @Override
            protected void updateItem(HisobKitob hisobKitob, boolean empty) {
                super.updateItem(hisobKitob, empty);
                if (hisobKitob == null || hisobKitob.getId() == null)
                    setStyle("");
                else if (hisobKitob.getHisob2().equals(hisob.getId()))
                    setStyle("-fx-background-color: white;");
                else if (hisobKitob.getHisob1().equals(hisob.getId()))
                    setStyle("-fx-background-color: #baffba;");
                else
                    setStyle("");
            }
        });
        return tableView;
    }

    private ObservableList<HisobKitob> getCenterTableData(LocalDate localDate, Integer tovarId) {
        ObservableList<HisobKitob> observableList = FXCollections.observableArrayList();
        String kirimHisoblari = "select id3, text from dokonlar;";
        ResultSet rs1 = hisobKitobModels.getResultSet(connection, kirimHisoblari);
        try {
            while (rs1.next()) {
                Integer id = rs1.getInt(1);
                String text = rs1.getString(2);
                Double donaDouble = 0d;
                Double narhDouble = 0d;
                String donaSelect = "Select sum(if(hisob2="+id+",dona,0)) as kirim, sum(if(hisob1="+id+",dona,0)) as chiqim  from HisobKitob where (hisob1=" + id + " or hisob2=" +id + ") and tovar ="+tovarId+" group by barcode";
                ResultSet tempRs = hisobKitobModels.getResultSet(connection, donaSelect);
                while (tempRs.next()) {
                    donaDouble = tempRs.getDouble(1) - tempRs.getDouble(2);
                }
                tempRs.close();
                String narhSelect = "Select sum(if(hisob2="+id+",narh*dona/kurs,0)) as kirim, sum(if(hisob1="+id+",narh*dona/kurs,0)) as chiqim  from HisobKitob where (hisob1=" + id + " or hisob2=" +id + ") and tovar ="+tovarId+" group by barcode";
                tempRs = hisobKitobModels.getResultSet(connection, narhSelect);
                while (tempRs.next()) {
                    narhDouble = tempRs.getDouble(1) - tempRs.getDouble(2);
                }
                tempRs.close();
                HisobKitob hisobKitob = new HisobKitob(0, 0, 0, 4, id, 0, 1, 0, 1d, "", donaDouble, narhDouble, 0, text, user.getId(), null);
                if (hisobKitob.getDona()>0)
                    observableList.add(hisobKitob);
            }
            rs1.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return observableList;
    }

    private ObservableList<HisobKitob> getRightTableData(LocalDate localDate, Integer hisob1Id, Integer tovarId) {
        ObservableList<HisobKitob> observableList = FXCollections.observableArrayList();
        BarCode bc = null;
        Standart tovar = GetDbData.getTovar(tovarId);
        if (tovar != null) {
            bc = GetDbData.getBarCode(tovar.getId());
            DecimalFormat decimalFormat = new MoneyShow();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String localDateString = localDate.format(formatter);
            String kirimHisoblari = "(hisob1=" + hisob1Id + " or hisob2="+hisob1Id+") and tovar = " + tovarId;
            observableList = hisobKitobModels.getAnyData(connection, kirimHisoblari,"datetime");
        }
        return observableList;
    }
    private ObservableList<HisobKitob> getBottomTableData(LocalDate localDate, Integer hisob1Id, String barCodeString) {
        ObservableList<HisobKitob> observableList = FXCollections.observableArrayList();
        BarCode bc = null;
        if (tovar != null) {
            bc = GetDbData.getBarCode(tovar.getId());
            System.out.println("BarCode mavjud emas: " + bc.getBarCode());
            DecimalFormat decimalFormat = new MoneyShow();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String localDateString = localDate.format(formatter);
            String kirimHisoblari =
                    "select datetime, izoh, barcode, dona, manba  from hisobkitob where hisob1=" + hisob1Id + " and barcode = " + barCodeString + " and amal = 4 and manba>0 order by dateTime, tovar;";
            ResultSet rs1 = hisobKitobModels.getResultSet(connection, kirimHisoblari);
            try {
                while (rs1.next()) {
                    Date date = sdf.parse(rs1.getString(1));
                    String text = rs1.getString(2);
                    String bcText = rs1.getString(3);
                    bc = GetDbData.getBarCode(bcText);
                    Standart birlik = GetDbData.getBirlik(bc.getBirlik());
                    Double adad = rs1.getDouble(4);
                    Integer manba = rs1.getInt(5);
                    HisobKitob hisobKitob = new HisobKitob(0, 0, 0, 4, hisob1Id, 0, 1, tovar.getId(), 1d, bc.getBarCode(), adad, 0d, manba, birlik.getText(), user.getId(), date);
                    observableList.add(hisobKitob);
                }
                rs1.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return observableList;
    }

    public BorderPane getBorderpane() {
        return borderpane;
    }

    public GridPane getGridPane() {
        return gridPane;
    }

    public TableView<Standart> getLeftTable() {
        return leftTable;
    }
    private  TableColumn<HisobKitob, Double> getOrtachaNarhColumn() {
        TableColumn<HisobKitob, Double>  chakanaColumn = new TableColumn<>("O`rtacha narh");
        chakanaColumn.setMinWidth(100);
        chakanaColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, Double>, ObservableValue<Double>>() {

            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<HisobKitob, Double> param) {
                HisobKitob hk = param.getValue();
                Double narhDouble = 0d;
                if (hk!= null) {
                    narhDouble = hk.getNarh() / hk.getDona();
                }
                return new SimpleObjectProperty<Double>(narhDouble);
            }
        });
        chakanaColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setMinimumIntegerDigits (1);
                numberFormat.setMaximumIntegerDigits (10);

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
        chakanaColumn.setStyle( "-fx-alignment: CENTER;");
        return chakanaColumn;
    }
    private  TableColumn<HisobKitob, Double> getChakanaNarhColumn() {
        StandartModels standartModels = new StandartModels("NarhTuri");
        ObservableList<Standart> standarts = standartModels.get_data(connection);
        Standart standart = standarts.get(0);
        TableColumn<HisobKitob, Double>  chakanaColumn = new TableColumn<>(standart.getText());
        chakanaColumn.setMinWidth(100);
        chakanaColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, Double>, ObservableValue<Double>>() {

            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<HisobKitob, Double> param) {
                HisobKitob hk = param.getValue();
                Double narhDouble = 0.0;
                Standart6 s6 = narhOl(hk.getTovar());
                if (s6 != null) {
                    narhDouble = s6.getChakana();
                }
                else {
                    TovarNarhi tn = yakkaTovarNarhi(hk.getTovar(), 1);
                    if (tn != null) {
                        narhDouble = tn.getNarh();
                    }
                }
                hk.setSummaCol(narhDouble);
                return new SimpleObjectProperty<Double>(narhDouble);
            }
        });
        chakanaColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setMinimumIntegerDigits (1);
                numberFormat.setMaximumIntegerDigits (10);

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
        chakanaColumn.setOnEditCommit(event -> {
            TableView<HisobKitob> tableView = event.getTableView();
            HisobKitob hk = event.getRowValue();
            Double newValue = event.getNewValue();
            if (newValue != null) {
                narhYoz(hk.getTovar(), 1, newValue/hk.getKurs());
                hk.setSummaCol(newValue/hk.getKurs());
            }
            tableView.refresh();
        });
        chakanaColumn.setStyle( "-fx-alignment: CENTER;");
        return chakanaColumn;
    }

    private  TableColumn<HisobKitob, Double> getUlgurjiNarhColumn() {
        StandartModels standartModels = new StandartModels("NarhTuri");
        ObservableList<Standart> standarts = standartModels.get_data(connection);
        Standart standart = standarts.get(1);
        TableColumn<HisobKitob, Double>  ulgurjiColumn = new TableColumn<>(standart.getText());
        ulgurjiColumn.setMinWidth(100);
        ulgurjiColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, Double>, ObservableValue<Double>>() {

            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<HisobKitob, Double> param) {
                HisobKitob hk = param.getValue();
                Double narhDouble = 0.0;
                Standart6 s6 = narhOl(hk.getTovar());
                if (s6 != null) {
                    narhDouble = s6.getUlgurji();

                }
                else {
                    TovarNarhi tn = yakkaTovarNarhi(hk.getTovar(), 2);
                    if (tn != null) {
                        narhDouble = tn.getNarh();
                    }
                }
                hk.setBalans(narhDouble);
                return new SimpleObjectProperty<Double>(narhDouble);
            }
        });
        ulgurjiColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setMinimumIntegerDigits (1);
                numberFormat.setMaximumIntegerDigits (10);

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
        ulgurjiColumn.setOnEditCommit(event -> {
            TableView<HisobKitob> tableView = event.getTableView();
            HisobKitob hk = event.getRowValue();
            Double newValue = event.getNewValue();
            if (newValue != null) {
                narhYoz(hk.getTovar(), 2, newValue/hk.getKurs());
                hk.setBalans(newValue/hk.getKurs());
            }
            tableView.refresh();
        });
        ulgurjiColumn.setStyle( "-fx-alignment: CENTER;");
        return ulgurjiColumn;
    }

    public Standart6 narhOl(int tovarId) {
        Standart6 s6 = null;
        Standart6Models standart6Models = new Standart6Models("TGuruh1");
        Standart3Models standart3Models = new Standart3Models();
        standart3Models.setTABLENAME("TGuruh2");
        ObservableList<Standart3> s3List = standart3Models.getAnyData(connection, "id3 = " + tovarId, "");
        if (s3List.size()>0) {
            Standart3 s3 = s3List.get(0);
            s6 = standart6Models.getWithId(connection, s3.getId2());
        }
        return s6;
    }

    private TovarNarhi yakkaTovarNarhi(int tovarId, int narhTuri) {
        TovarNarhi tovarNarhi = null;
        TovarNarhiModels tovarNarhiModels = new TovarNarhiModels();
        ObservableList<TovarNarhi> observableList = tovarNarhiModels.getAnyData(
                connection, "tovar = " + tovarId + " AND narhTuri = " + narhTuri, "id desc"
        );
        if (observableList.size()>0) {
            tovarNarhi = observableList.get(0);
        }
        return tovarNarhi;
    }

    public void narhYoz(int tovarId, int narhTuri, Double narhDouble) {
        Standart6Models standart6Models = new Standart6Models("TGuruh1");
        Standart3Models standart3Models = new Standart3Models();
        standart3Models.setTABLENAME("TGuruh2");
        ObservableList<Standart3> s3List = standart3Models.getAnyData(connection, "id3 = " + tovarId, "");
        if (s3List.size()>0) {
            Standart3 s3 = s3List.get(0);
            Standart6 s6 = standart6Models.getWithId(connection, s3.getId2());
            if (s6 != null) {
                switch (narhTuri) {
                    case 0:
                        s6.setNarh(narhDouble);
                        break;
                    case 1:
                        s6.setChakana(narhDouble);
                        break;
                    case 2:
                        s6.setUlgurji(narhDouble);
                        break;
                    case 3:
                        s6.setBoj(narhDouble);
                        break;
                    case 4:
                        s6.setNds(narhDouble);
                        break;
                }
                standart6Models.update_data(connection, s6);
                GuruhNarhModels guruhNarhModels = new GuruhNarhModels();
                GuruhNarh guruhNarh = new GuruhNarh(
                        null, new Date(), s6.getId(), narhTuri, narhDouble, user.getId(), new Date()
                );
                guruhNarhModels.insert_data(connection, guruhNarh);
            }
        } else {
            TovarNarhiModels tovarNarhiModels = new TovarNarhiModels();
            TovarNarhi tovarNarhi = new TovarNarhi(
                    null, new Date(), tovarId, narhTuri, 1, 1d, narhDouble, user.getId(), null
            );
            tovarNarhiModels.insert_data(connection, tovarNarhi);
        }
    }

    private void tovarTaftish(String oldValue, String newValue) {
        ObservableList<Standart> observableList = getLeftTableData();
        ObservableList<Standart> subentries = FXCollections.observableArrayList();
        newValue = newValue.toLowerCase();

        if ( oldValue != null && (newValue.length() < oldValue.length()) ) {
            leftTable.setItems(observableList);
        }

        for ( Standart standart: observableList) {
            if (standart.getText().toLowerCase().contains(newValue)) {
                subentries.add(standart);
            }
        }
        leftTable.setItems(subentries);
    }

    private void barCodeTaftish(String newValue) {
        BarCode barCode = GetDbData.getBarCode(newValue);
        if (barCode != null) {
            ObservableList<Standart> observableList = leftTable.getItems();

            for (Standart standart : observableList) {
                if (barCode.getTovar().equals(standart.getId())) {
                    leftTable.getSelectionModel().select(standart);
                    leftTable.scrollTo(standart);
                    leftTable.requestFocus();
                    break;
                }
            }
        }
    }
    private void tovarTaftish(Standart standart) {
        leftTable.getSelectionModel().select(standart);
        leftTable.scrollTo(standart);
        leftTable.requestFocus();
    }
}