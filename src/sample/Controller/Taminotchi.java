package sample.Controller;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import sample.Config.MySqlDB;
import sample.Config.MySqlDBLocal;
import sample.Data.*;
import sample.Model.*;
import sample.Tools.*;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Optional;

public class Taminotchi extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    SplitPane centerPane = new SplitPane();
    VBox birinchiPane = new VBox();
    VBox ikkinchiPane = new VBox();
    VBox uchinchiPane = new VBox();
    HBox buttonsHBox = new HBox();
    Double kirimJami = 0d;
    Double chiqimJami = 0d;
    Double qoldiJami = 0d;

    Label kirimLabel = new Label("");
    Label chiqimLabel = new Label("");
    Label jamiLabel = new Label("");
    Button qaydEtButton = new Button();
    Button cancelButton = new Button("<<");
    EventHandler<ActionEvent> leftCancelEvent;
    ChangeListener<Hisob> hisobChangeListener;

    Tugmachalar leftButtons = new Tugmachalar();
    TextField textField1 = new TextField();
    TextField textField2 = new TextField();
    TextField textField3 = new TextField();

    TableView<Hisob> sotuvchiTable = new TableView<>();
    TableView<Hisob> taminotchiTable = new TableView<>();
    TableView<HisobKitob> tovarTable = new TableView<>();
    GetTableView2 tableMaker = new GetTableView2();

    GridPane gridPane = new GridPane();

    ObservableList<Hisob> sotuvchilar = FXCollections.observableArrayList();
    ObservableList<Hisob> hisobList = FXCollections.observableArrayList();
    ObservableList<Hisob> taminotchilar = FXCollections.observableArrayList();
    ObservableList<HisobKitob> tovarList = FXCollections.observableArrayList();
    ObservableList<Balans> balanceList = FXCollections.observableArrayList();

    BarCodeModels barCodeModels = new BarCodeModels();
    HisobModels hisobModels = new HisobModels("Hisob1");
    GuruhNarhModels guruhNarhModels = new GuruhNarhModels();

    Connection connection;
    User user = new User(1, "admin", "", "admin", "", "", 1, "", 1, new Date());
    int padding = 3;
    DecimalFormat decimalFormat = new MoneyShow();
    Font font = Font.font("Arial", FontWeight.BOLD,20);
    StringBuffer stringBuffer = new StringBuffer();

    public static void main(String[] args) {
        launch(args);
    }

    public Taminotchi() {
        connection = new MySqlDB().getDbConnection();
        GetDbData.initData(connection);
        ibtido();
    }

    public Taminotchi(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        ibtido();
    }

    private void ibtido() {
        initData();
        initBirinchiPane();
        initUchinchiPane();
        initCenterPane();
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
        stage = primaryStage;
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        stage.setX(bounds.getMinX() - 3);
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth() + 7);
        stage.setHeight(bounds.getHeight() + 6);
        stage.setResizable(false);
        stage.setTitle("Ta`minotchilar");
        scene = new Scene(borderpane);
        stage.setScene(scene);
    }

    private void initBirinchiPane() {
        HBox.setHgrow(textField1, Priority.ALWAYS);
        birinchiPane.setPadding(new Insets(padding));
        birinchiPane.setMinWidth(235);
        birinchiPane.setMaxWidth(235);
        HBox.setHgrow(birinchiPane, Priority.ALWAYS);
        VBox.setVgrow(birinchiPane, Priority.ALWAYS);
        cancelButton.setMaxWidth(2000);
        cancelButton.setPrefWidth(168);
        HBox.setHgrow(cancelButton, Priority.ALWAYS);
        initLeftButtons();
        initsotuvchiTableView();
        birinchiPane.getChildren().addAll(leftButtons, textField1, sotuvchiTable);
        textField1.textProperty().addListener((observable, oldValue, newValue) -> {
            birinchiTaftish(oldValue, newValue);
        });

        ikkinchiPane.setPadding(new Insets(padding));
        ikkinchiPane.setMinWidth(240);
        ikkinchiPane.setMaxWidth(240);
        HBox.setHgrow(ikkinchiPane, Priority.ALWAYS);
        VBox.setVgrow(ikkinchiPane, Priority.ALWAYS);
        initTaminotchiTable();
        HBox.setHgrow(textField2, Priority.ALWAYS);
        birinchiPane.getChildren().addAll(textField2, taminotchiTable);
        textField2.textProperty().addListener((observable, oldValue, newValue) -> {
            ikkinchiTaftish(oldValue, newValue);
        });
    }

    private void initData() {
        hisobModels.setTABLENAME("Hisob");
        ObservableList<Hisob> hisobObservableList = hisobModels.get_data(connection);

        hisobModels.setTABLENAME("Hisob1");
        sotuvchilar = hisobModels.get_data(connection);
        for (Hisob h: hisobObservableList) {
            boolean addHisob = true;
            for (Hisob s: sotuvchilar) {
                if (s.getId().equals(h.getId())) {
                    addHisob = false;
                    break;
                }
            }
            if (addHisob) {
                hisobList.add(h);
            }
        }
        if (sotuvchilar.size()>0) {
            taminotchilar = taminotchilar(sotuvchilar.get(0));
        }
    }

    private void initCenterPane() {
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        VBox.setVgrow(centerPane, Priority.ALWAYS);
        centerPane.getItems().addAll(uchinchiPane, birinchiPane);
    }

    private void initUchinchiPane() {
        HBox.setHgrow(textField3, Priority.ALWAYS);
        uchinchiPane.setPadding(new Insets(padding));
        HBox.setHgrow(uchinchiPane, Priority.ALWAYS);
        VBox.setVgrow(uchinchiPane, Priority.ALWAYS);
        initTovarTable();
        uchinchiPane.getChildren().addAll(textField3, tovarTable);
        textField3.textProperty().addListener((observable, oldValue, newValue) -> {
            uchinchiTaftish(oldValue, newValue);
        });

        initGridPane();
        uchinchiPane.getChildren().add(gridPane);
    }

    private void initBorderPane() {
        borderpane.setCenter(centerPane);
    }

    private void initLeftButtons() {
        Button addButton = leftButtons.getAdd();
        Button editButton = leftButtons.getEdit();
        Button deleteButton = leftButtons.getDelete();
        Button excelButton = leftButtons.getExcel();
        HBox.setHgrow(textField1, Priority.ALWAYS);
        textField1.setPromptText("Guruh nomi");
        leftButtons.getChildren().remove(editButton);

        leftButtons.getAdd().setOnAction(event -> {
            ikkinchiPane.setDisable(true);
            birinchiPane.getChildren().remove(leftButtons);
            qaydEtButton = new Tugmachalar().getAdd();
            qaydEtButton.setMaxWidth(2000);
            qaydEtButton.setPrefWidth(168);
            HBox.setHgrow(qaydEtButton, Priority.ALWAYS);
            buttonsHBox.getChildren().removeAll(buttonsHBox.getChildren());
            buttonsHBox.getChildren().addAll(cancelButton, qaydEtButton);
            birinchiPane.getChildren().add(buttonsHBox);
            sotuvchiTable.setItems(hisobList);
            sotuvchiTable.getSelectionModel().selectedItemProperty().removeListener(hisobChangeListener);
            sotuvchiTable.refresh();

            qaydEtButton.setOnAction(event1->{
                Hisob h = sotuvchiTable.getSelectionModel().getSelectedItem();
                if (h != null) {
                    hisobModels.setTABLENAME("Hisob1");
                    hisobModels.insert_dataID(connection, h);
                    sotuvchilar.add(h);
                    hisobList.remove(h);
                    taminotchilar = taminotchilar(h);
                    taminotchiTable.setItems(taminotchilar);
                    taminotchiTable.refresh();
                }
                cancelButton.fire();
            });

            cancelButton.setOnAction(leftCancelEvent);
        });

        leftButtons.getDelete().setOnAction(event -> {
            Hisob s6 = sotuvchiTable.getSelectionModel().getSelectedItem();
            if (s6 != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.getButtonTypes().removeAll(alert.getButtonTypes());
                ButtonType okButton = new ButtonType("Ha");
                ButtonType noButton = new ButtonType("Yo`q");
                alert.getButtonTypes().addAll(okButton, noButton);
                alert.setTitle("Diqqat !!!");
                alert.setHeaderText(s6.getText() + " guruhi va unga taalluqli hamma tovarllar jadvaldan o`chirilladi");
                alert.setContentText("Rozimisiz");
                Optional<ButtonType> option = alert.showAndWait();
                ButtonType buttonType = option.get();
                if (okButton.equals(buttonType)) {
                    hisobModels.setTABLENAME("Hisob1");
                    hisobModels.delete_data(connection, s6);
                    sotuvchilar.remove(s6);
                    hisobList.add(s6);
                }
            }
        });

        leftButtons.getExcel().setOnAction(event -> {
//            ExportToExcel exportToExcel = new ExportToExcel();
//            exportToExcel.priceList(sotuvchilar);
        });
        leftCancelEvent = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                ikkinchiPane.setDisable(false);
                birinchiPane.getChildren().remove(buttonsHBox);
                birinchiPane.getChildren().add(0, leftButtons);
                sotuvchiTable.setItems(sotuvchilar);
                sotuvchiTable.getSelectionModel().selectedItemProperty().addListener(hisobChangeListener);
                sotuvchiTable.refresh();
            }
        };

    }

    private void initsotuvchiTableView() {
        HBox.setHgrow(sotuvchiTable, Priority.ALWAYS);
        VBox.setVgrow(sotuvchiTable, Priority.ALWAYS);
        sotuvchiTable.getColumns().addAll(
                getGuruhColumn()/*,
                getNarhColumn(),
                getUlgurjiNarhColumn(),
                getChakanaNarhColumn(),
                getBojColumn(),
                getNDSColumn()*/
        );
        sotuvchiTable.setItems(sotuvchilar);
        if (sotuvchilar.size()>0) {
            sotuvchiTable.getSelectionModel().select(sotuvchilar.get(0));
        }

        hisobChangeListener = new ChangeListener<Hisob>() {
            @Override
            public void changed(ObservableValue<? extends Hisob> observable, Hisob oldValue, Hisob newValue) {
                if (newValue != null) {
                    taminotchilar = taminotchilar(newValue);
                    taminotchiTable.setItems(taminotchilar);
                    taminotchiTable.refresh();
                }
            }
        };
        sotuvchiTable.getSelectionModel().selectedItemProperty().addListener(hisobChangeListener);

    }

    private TableColumn<Hisob, String> getGuruhColumn() {
        TableColumn<Hisob, String> textColumn = new TableColumn<>("Ta`minotchi");
        textColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
        textColumn.setCellFactory(TextFieldTableCell.<Hisob> forTableColumn());
        textColumn.setMinWidth(230);
        textColumn.setOnEditCommit((TableColumn.CellEditEvent<Hisob, String> event) -> {
            String newString = event.getNewValue();
            if (newString != null) {
                Hisob guruh = event.getRowValue();
                guruh.setText(newString);
                hisobModels.setTABLENAME("Hisob1");
                hisobModels.update_data(connection, guruh);
            }
        });
        return textColumn;
    }

    private void initTaminotchiTable() {
        GetTableView2 getTableView2 = new GetTableView2();
        taminotchiTable = getTableView2.getHisobTableView();
        SetHVGrow.VerticalHorizontal(taminotchiTable);
        TableColumn<Hisob, String> hisobNomi = getTableView2.getHisobTextColumn();
        hisobNomi.setMinWidth(230);
        taminotchiTable.getColumns().addAll(hisobNomi);
        taminotchiTable.setItems(taminotchilar);
        if (taminotchilar.size()>0) {
            taminotchiTable.getSelectionModel().select(taminotchilar.get(0));
        }
        taminotchiTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Hisob h = sotuvchiTable.getSelectionModel().getSelectedItem();
                if (h != null) {
                    tovarTable.setItems(hisobKitobniYangila(newValue, h));
                    tovarTable.refresh();
                }
            }
        });
    }

    private ObservableList<HisobKitob> hisobKitobniYangila(Hisob taminotchiHisobi, Hisob sotuvchiHisobi) {
        kirimJami = 0d;
        chiqimJami = 0d;
        qoldiJami = 0d;
        double narhDouble = 0;

        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        ObservableList<HisobKitob> hisobKitobObservableList = hisobKitobModels.getAnyData(connection, "hisob1 = " + taminotchiHisobi.getId() + " and hisob2 = " + sotuvchiHisobi.getId() + " and amal = 2", "");
        Integer tranzitHisob = hisobKitobModels.yordamchiHisob(connection, sotuvchiHisobi.getId(), "tranzithisobguruhi", "tranzithisob");
        ObservableList<HisobKitob> sotilganTovarlarList = hisobKitobModels.getAnyData(connection, "hisob1 = " + sotuvchiHisobi.getId() + " and hisob2 = " + tranzitHisob + " and amal = 4", "");
        balanceList.removeAll(balanceList);
        if (hisobKitobObservableList.size()>0) {
            for (HisobKitob hk: hisobKitobObservableList) {
                Double olingani = hk.getDona();
                Double sotilgani = sotilgani(hk.getId(), sotilganTovarlarList);
                balanceList.add(new Balans(hk.getId(), olingani, sotilgani, olingani - sotilgani, olingani - sotilgani));
                kirimJami += olingani*hk.getNarh()/hk.getKurs();
                chiqimJami += sotilgani*hk.getNarh()/hk.getKurs();
                qoldiJami += (olingani-sotilgani)*hk.getNarh()/hk.getKurs();
            }
        }
        kirimLabel.setText(decimalFormat.format(kirimJami));
        chiqimLabel.setText(decimalFormat.format(chiqimJami));
        jamiLabel.setText(decimalFormat.format(qoldiJami));
        return hisobKitobObservableList;
    }

    private Double sotilgani(int manba, ObservableList<HisobKitob> sotilganTovarlarList) {
        Double dona = 0d;
        for (HisobKitob hk: sotilganTovarlarList) {
            if (hk.getManba().equals(manba)) {
                dona += hk.getDona();
            }
        }
        return dona;
    }

    private ObservableList<Hisob> taminotchilar(Hisob hisob) {
        QaydnomaModel qaydnomaModel = new QaydnomaModel();
        ObservableList<Hisob> observableList = qaydnomaModel.get_data(connection, hisob.getId(), 2);
        return observableList;
    }

    private void initTovarTable() {
        tovarTable.setPadding(new Insets(padding));
        tovarTable = tableMaker.hisobKitobTableView;
        SetHVGrow.VerticalHorizontal(tovarTable);
        tovarTable.getColumns().addAll(
                tableMaker.getIzoh2Column(),
                getKirimHeaderColumn(),
                getChiqimHeaderColumn(),
                getQoldiqHeaderColumn()
        );
        if (taminotchilar.size()>0) {
            Hisob taminotchi = taminotchiTable.getSelectionModel().getSelectedItem();
            Hisob sotuvchi = sotuvchiTable.getSelectionModel().getSelectedItem();
            tovarList = hisobKitobniYangila(taminotchi, sotuvchi);
        }
        tovarTable.setItems(tovarList);
    }

    private TableColumn<HisobKitob, Double> getKirimHeaderColumn() {
        TableColumn<HisobKitob, Double> kirimColumn = new TableColumn<>("Keldi");
        kirimColumn.setMinWidth(100);
        kirimColumn.setStyle( "-fx-alignment: CENTER;");
        kirimColumn.getColumns().addAll(
                tableMaker.getAdadColumn(),
                tableMaker.getNarhColumn(),
                getJamiKirimColumn()
        );

        return kirimColumn;
    }

    private TableColumn<HisobKitob, Double> getChiqimHeaderColumn() {
        TableColumn<HisobKitob, Double> kirimColumn = new TableColumn<>("Sotildi");
        kirimColumn.setMinWidth(100);
        kirimColumn.setStyle( "-fx-alignment: CENTER;");
        kirimColumn.getColumns().addAll(
                getChiqimColumn(),
                getJamiChiqimColumn()
        );

        return kirimColumn;
    }

    private TableColumn<HisobKitob, Double> getQoldiqHeaderColumn() {
        TableColumn<HisobKitob, Double> kirimColumn = new TableColumn<>("Qoldi");
        kirimColumn.setMinWidth(100);
        kirimColumn.setStyle( "-fx-alignment: CENTER;");
        kirimColumn.getColumns().addAll(
                getQoldiColumn(),
                getJamiQoldiqColumn()
        );

        return kirimColumn;
    }

    private TableColumn<HisobKitob, Double> getJamiKirimColumn() {
        TableColumn<HisobKitob, Double> balansColumn = new TableColumn<>("Jami");
        Callback balansCellValueFactory = new Callback<TableColumn.CellDataFeatures<HisobKitob, Double>, ObservableValue<Double>>() {
            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<HisobKitob, Double> param) {
                HisobKitob hisobKitob = param.getValue();
                Double doubleValue = hisobKitob.getDona() * hisobKitob.getNarh();
                return new SimpleObjectProperty<Double>(doubleValue);
            }

        };
        balansColumn.setMinWidth(100);
        balansColumn.setCellValueFactory(balansCellValueFactory);
        balansColumn.setCellFactory(column -> {
            TableCell<HisobKitob, Double> cell = new TableCell<HisobKitob, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        if (item != null)
                            setText(decimalFormat.format(item));
                    }
                }
            };
            return cell;
        });
        balansColumn.setStyle( "-fx-alignment: CENTER;");

        return balansColumn;
    }

    private TableColumn<HisobKitob, Double> getJamiChiqimColumn() {
        TableColumn<HisobKitob, Double> balansColumn = new TableColumn<>("Jami");
        Callback balansCellValueFactory = new Callback<TableColumn.CellDataFeatures<HisobKitob, Double>, ObservableValue<Double>>() {
            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<HisobKitob, Double> param) {
                HisobKitob hisobKitob = param.getValue();
                Balans b = getBalans(hisobKitob.getId());
                Double doubleValue = b.getChiqim() * hisobKitob.getNarh();
                return new SimpleObjectProperty<Double>(doubleValue);
            }

        };
        balansColumn.setMinWidth(100);
        balansColumn.setCellValueFactory(balansCellValueFactory);
        balansColumn.setCellFactory(column -> {
            TableCell<HisobKitob, Double> cell = new TableCell<HisobKitob, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        if (item != null)
                            setText(decimalFormat.format(item));
                    }
                }
            };
            return cell;
        });
        balansColumn.setStyle( "-fx-alignment: CENTER;");

        return balansColumn;
    }

    private TableColumn<HisobKitob, Double> getJamiQoldiqColumn() {
        TableColumn<HisobKitob, Double> balansColumn = new TableColumn<>("Jami");
        Callback balansCellValueFactory = new Callback<TableColumn.CellDataFeatures<HisobKitob, Double>, ObservableValue<Double>>() {
            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<HisobKitob, Double> param) {
                HisobKitob hisobKitob = param.getValue();
                Balans b = getBalans(hisobKitob.getId());
                Double doubleValue = b.getJami() * hisobKitob.getNarh();
                return new SimpleObjectProperty<Double>(doubleValue);
            }

        };
        balansColumn.setMinWidth(100);
        balansColumn.setCellValueFactory(balansCellValueFactory);
        balansColumn.setCellFactory(column -> {
            TableCell<HisobKitob, Double> cell = new TableCell<HisobKitob, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        if (item != null)
                            setText(decimalFormat.format(item));
                    }
                }
            };
            return cell;
        });
        balansColumn.setStyle( "-fx-alignment: CENTER;");

        return balansColumn;
    }

    private TableColumn<HisobKitob, Double> getChiqimColumn() {
        TableColumn<HisobKitob, Double> balansColumn = new TableColumn<>("Dona");
        Callback balansCellValueFactory = new Callback<TableColumn.CellDataFeatures<HisobKitob, Double>, ObservableValue<Double>>() {
            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<HisobKitob, Double> param) {
                HisobKitob hisobKitob = param.getValue();
                Balans b = getBalans(hisobKitob.getId());
                Double doubleValue = b.getChiqim();
                return new SimpleObjectProperty<Double>(doubleValue);
            }

        };
        balansColumn.setMinWidth(100);
        balansColumn.setCellValueFactory(balansCellValueFactory);
        balansColumn.setCellFactory(column -> {
            TableCell<HisobKitob, Double> cell = new TableCell<HisobKitob, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        if (item != null)
                            setText(decimalFormat.format(item));
                    }
                }
            };
            return cell;
        });
        balansColumn.setStyle( "-fx-alignment: CENTER;");

        return balansColumn;
    }

    private TableColumn<HisobKitob, Double> getQoldiColumn() {
        TableColumn<HisobKitob, Double> balansColumn = new TableColumn<>("Dona");
        Callback balansCellValueFactory = new Callback<TableColumn.CellDataFeatures<HisobKitob, Double>, ObservableValue<Double>>() {
            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<HisobKitob, Double> param) {
                HisobKitob hisobKitob = param.getValue();
                Balans b = getBalans(hisobKitob.getId());
                Double doubleValue = b.getBalans();
                return new SimpleObjectProperty<Double>(doubleValue);
            }

        };
        balansColumn.setMinWidth(100);
        balansColumn.setCellValueFactory(balansCellValueFactory);
        balansColumn.setCellFactory(column -> {
            TableCell<HisobKitob, Double> cell = new TableCell<HisobKitob, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        if (item != null)
                            setText(decimalFormat.format(item));
                    }
                }
            };
            return cell;
        });
        balansColumn.setStyle( "-fx-alignment: CENTER;");

        return balansColumn;
    }

    public void birinchiTaftish(String oldValue, String newValue) {
        ObservableList<Hisob> subentries = FXCollections.observableArrayList();
        newValue = newValue.toLowerCase();

        if ( oldValue != null && (newValue.length() < oldValue.length()) ) {
            sotuvchiTable.setItems(sotuvchilar);
        }

        for ( Hisob h: sotuvchilar) {
            if (h.getText().toLowerCase().contains(newValue)) {
                subentries.add(h);
            }
        }
        sotuvchiTable.setItems(subentries);
        sotuvchiTable.refresh();
    }

    public void ikkinchiTaftish(String oldValue, String newValue) {
        ObservableList<Hisob> subentries = FXCollections.observableArrayList();
        newValue = newValue.toLowerCase();

        if ( oldValue != null && (newValue.length() < oldValue.length()) ) {
            taminotchiTable.setItems(taminotchilar);
        }

        for ( Hisob h: taminotchilar) {
            if (h.getText().toLowerCase().contains(newValue)) {
                subentries.add(h);
            }
        }
        taminotchiTable.setItems(subentries);
        taminotchiTable.refresh();
    }

    public void uchinchiTaftish(String oldValue, String newValue) {
        ObservableList<HisobKitob> subentries = FXCollections.observableArrayList();
        newValue = newValue.toLowerCase();

        if ( oldValue != null && (newValue.length() < oldValue.length()) ) {
            tovarTable.setItems(tovarList);
        }

        for ( HisobKitob hk: tovarList) {
            if (hk.getIzoh().toLowerCase().contains(newValue)) {
                subentries.add(hk);
            }
        }
        tovarTable.setItems(subentries);
        tovarTable.refresh();
    }

    private Balans getBalans(Integer id) {
        Balans balans = new Balans(0,0d,0d,0d,0d);
        for (Balans b: balanceList) {
            if (b.getId().equals(id)) {
                balans = b;
                break;
            }
        }
        return balans;
    }

    private void initGridPane() {
        gridPane.setMaxHeight(50);
        SetHVGrow.VerticalHorizontal(gridPane);
        gridPane.setPadding(new Insets(padding));

        int rowIndex = 0;
        Label label1 = new Label("Keldi");
        label1.setFont(font);
        Label label2 = new Label("Sotildi");
        label2.setFont(font);
        Label label3 = new Label("Qoldi");
        label3.setFont(font);
        gridPane.add(label1, 0, rowIndex, 1, 1);
        gridPane.add(label2, 1, rowIndex, 1, 1);
        gridPane.add(label3, 2, rowIndex, 1, 1);

        rowIndex++;
        kirimLabel = new Label(decimalFormat.format(kirimJami));
        kirimLabel.setFont(font);
        gridPane.add(kirimLabel, 0, rowIndex, 1, 1);
        GridPane.setHgrow(kirimLabel, Priority.ALWAYS);

        chiqimLabel = new Label(decimalFormat.format(chiqimJami));
        chiqimLabel.setFont(font);
        gridPane.add(chiqimLabel, 1, rowIndex, 1, 1);
        GridPane.setHgrow(chiqimLabel, Priority.ALWAYS);

        jamiLabel = new Label(decimalFormat.format(qoldiJami));
        jamiLabel.setFont(font);
        gridPane.add(jamiLabel, 2, rowIndex, 1, 1);
        GridPane.setHgrow(jamiLabel, Priority.ALWAYS);
    }
}
