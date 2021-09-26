package sample.Controller;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import sample.Config.MySqlDBLocal;
import sample.Tools.*;
import sample.Data.*;
import sample.Model.*;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;

public class TovarNarhiController extends Application {
    DecimalFormat decimalFormat = new MoneyShow();
    Connection connection;
    User user = new User(1, "admin", "", "admin");
    Stage stage = new Stage();
    Scene scene;
    BorderPane borderpane = new BorderPane();
    MenuBar mainMenu;
    SplitPane centerPane = new SplitPane();
    GridPane rightPane = new GridPane();
    VBox leftPane = new VBox();
    Pane pane = new Pane();
    Label left_label = new Label("chap");
    Label right_label = new Label("o`ng");
    HBox bottom = new HBox();
    StringBuffer stringBuffer = new StringBuffer();
    TextField barCodeTextField = new TextField();

    RecordHandler recordHandlerGridPane;

    Tugmachalar tugmachalar = new Tugmachalar();
    TableView<TovarSana> tovarSanaTableView = new TableView();
    TableView<Standart> tovarTableView = new TableView<>();
    TextField tovarTop = new TextField();

    TovarNarhiModels tovarNarhiModels = new TovarNarhiModels();
    TovarSanaModels tovarSanaModels = new TovarSanaModels();
    TableView<TovarNarhi> tovarNarhiTableView = new TableView<>();

    ObservableList<Standart> narhTuriObservableList  = FXCollections.observableArrayList();
    ObservableList<Valuta> valutaObservableList = FXCollections.observableArrayList();
    ObservableList<TovarNarhi> tovarNarhiObservableList  = FXCollections.observableArrayList();
    ObservableList<Standart> tovarObservableList  = FXCollections.observableArrayList();
    ObservableList<TovarSana> tovarSanaObservableList = FXCollections.observableArrayList();
    ObservableList<BarCode> barCodeObservableList;

    ValutaModels valutaModels = new ValutaModels();
    BarCodeModels barCodeModels = new BarCodeModels();
    StandartModels standartModels = new StandartModels();


    Standart tovar;
    TovarSana tovarSana;
    TovarNarhi tovarNarhi;

    public TovarNarhiController() {
        connection = new MySqlDBLocal().getDbConnection();
    }

    public TovarNarhiController(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ibtido();
        initStage(primaryStage);
        stage.show();
    }

    public void display() {
        initStage(stage);
        ibtido();
        stage.showAndWait();
    }

    private void ibtido() {
        initData();
        initTables();
        initLeftPane();
        initRightPane();
        initCenterPane();
        initTugmachalar();
        initBottomPane();
        initBorderPane();
    }

    private void initTugmachalar() {
        EventHandler<ActionEvent> cancelEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                leftPane.setDisable(false);
                centerPane.getItems().remove(recordHandlerGridPane);
                centerPane.getItems().add(rightPane);
            }
        };

        tugmachalar.getAdd().setOnAction(event -> {
            leftPane.setDisable(true);
            centerPane.getItems().remove(rightPane);
            centerPane.getItems().add(recordHandlerGridPane);
            recordHandlerGridPane.showAdd();

            recordHandlerGridPane.qaydEtButton.setOnAction(event1 -> {
                recordHandlerGridPane.saveAdd();
                recordHandlerGridPane.cancelButton.fire();
            });

            recordHandlerGridPane.cancelButton.setOnAction(cancelEvent);
        });
        tugmachalar.getDelete().setOnAction(event -> {
            leftPane.setDisable(true);
            centerPane.getItems().remove(rightPane);
            centerPane.getItems().add(recordHandlerGridPane);
            recordHandlerGridPane.showDelete();

            recordHandlerGridPane.qaydEtButton.setOnAction(event1 -> {
                recordHandlerGridPane.saveDelete();
                recordHandlerGridPane.cancelButton.fire();
            });

            recordHandlerGridPane.cancelButton.setOnAction(cancelEvent);

        });
        tugmachalar.getEdit().setOnAction(event -> {
            leftPane.setDisable(true);
            centerPane.getItems().remove(rightPane);
            centerPane.getItems().add(recordHandlerGridPane);
            recordHandlerGridPane.showEdit();

            recordHandlerGridPane.qaydEtButton.setOnAction(event1 -> {
                recordHandlerGridPane.saveEdit();
                recordHandlerGridPane.cancelButton.fire();
            });

            recordHandlerGridPane.cancelButton.setOnAction(cancelEvent);

        });
    }

    private void initData() {
        tovarObservableList = GetDbData.getTovarObservableList();
        standartModels.setTABLENAME("NarhTuri");
/*
            standartModels.insert_data(connection, new Standart(1, "Chakana narh", 1, null));
            standartModels.insert_data(connection, new Standart(2, "Ulgurji narh", 1, null));
*/
        narhTuriObservableList = standartModels.get_data(connection);
        valutaObservableList = GetDbData.getValutaObservableList();
        barCodeObservableList = barCodeModels.get_data(connection);
    }

    private void initTables() {
        TableColumn<Standart, String> tovarNomi = new TableColumn<>("Tovar");
        tovarNomi.setMaxWidth(182);
        tovarNomi.setMinWidth(182);
        tovarNomi.setCellValueFactory(new PropertyValueFactory<>("text"));

        tovarNomi.setCellFactory(tc -> {
            TableCell<Standart, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(tovarNomi.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell ;
        });

        tovarTableView.getColumns().add(tovarNomi);
        tovarTableView.setItems(tovarObservableList);
        HBox.setHgrow(tovarTableView, Priority.ALWAYS);
        VBox.setVgrow(tovarTableView, Priority.ALWAYS);
        TableColumn<TovarSana, Date> sana = new TableColumn<>("Sana");
        sana.setMaxWidth(120);
        sana.setMinWidth(120);
        sana.setCellValueFactory(new PropertyValueFactory<>("sana"));
        sana.setCellFactory(column -> {
            TableCell<TovarSana, Date> cell = new TableCell<TovarSana, Date>() {
                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                    if (empty) {
                        setText(null);
                    } else {
                        setText(format.format(item));
                    }
                }
            };
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        tovarSanaTableView.setMaxWidth(125);
        tovarSanaTableView.getColumns().add(sana);
        tovarSanaTableView.setItems(tovarSanaObservableList);
        HBox.setHgrow(tovarSanaTableView, Priority.NEVER);
        VBox.setVgrow(tovarSanaTableView, Priority.ALWAYS);
        tovarSanaTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                tovarSana = newValue;
                tovarNarhiObservableList = tovarNarhiModels.getDate(connection, tovarSana.getTovar(), tovarSana.getSana());
                tovarNarhiTableView.setItems(tovarNarhiObservableList);
            }
        });

        TableColumn<TovarNarhi, Integer> narhTuri = new TableColumn<>("Narh turi");
        narhTuri.setMaxWidth(250);
        narhTuri.setMinWidth(250);
        narhTuri.setCellValueFactory(new PropertyValueFactory<>("narhTuri"));
        narhTuri.setCellFactory(column -> {
            TableCell<TovarNarhi, Integer> cell = new TableCell<TovarNarhi, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        setText(getNarhNomi(item));
                    }
                }
            };
            return cell;
        });

        TableColumn<TovarNarhi, Double> narh = new TableColumn<>("Narh");
        narh.setMaxWidth(150);
        narh.setMinWidth(150);
        narh.setCellValueFactory(new PropertyValueFactory<>("narh"));
        narh.setCellFactory(column -> {
            TableCell<TovarNarhi, Double> cell = new TableCell<TovarNarhi, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        NumberFormat numberFormat = NumberFormat.getInstance();
                        numberFormat.setMinimumIntegerDigits (1);
                        numberFormat.setMaximumIntegerDigits (10);

                        numberFormat.setMinimumFractionDigits (1);
                        numberFormat.setMaximumFractionDigits (2);
                        setText(numberFormat.format(item));
                    }
                }
            };
            cell.setAlignment(Pos.CENTER);
            return cell;
        });

        tovarNarhiTableView.getColumns().addAll(narhTuri, narh);

        if (tovarObservableList.size()>0) {
            tovarTableView.getSelectionModel().selectFirst();
            tovar = tovarTableView.getSelectionModel().getSelectedItem();
            tovarSanaObservableList = tovarSanaModels.getAnyData(connection, "tovar = " + tovar.getId(), "");
            tovarSanaTableView.setItems(tovarSanaObservableList);
            if (tovarSanaObservableList.size()>0) {
                tovarSana = tovarSanaObservableList.get(0);
                tovarSanaTableView.getSelectionModel().selectFirst();
                tovarNarhiObservableList = tovarNarhiModels.getDate(connection, tovarSana.getTovar(), tovarSana.getSana());
                tovarNarhiTableView.setItems(tovarNarhiObservableList);
                if (tovarNarhiObservableList.size()>0) {
                    tovarNarhiTableView.getSelectionModel().selectFirst();
                }
            }
        }

        tovarNarhiTableView.setItems(tovarNarhiObservableList);
        HBox.setHgrow(tovarNarhiTableView, Priority.NEVER);
        VBox.setVgrow(tovarNarhiTableView, Priority.ALWAYS);

        tovarTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                tovar = newValue;
                tovarSanaObservableList.removeAll(tovarSanaObservableList);
                tovarSanaObservableList.addAll(tovarSanaModels.getAnyData(connection,"tovar = " + tovar.getId(),"id desc"));
//                    tovarSanaTableView.setItems(tovarSanaObservableList);
                if (tovarSanaObservableList.size()>0) {
                    tovarSanaTableView.getSelectionModel().selectFirst();
                } else {
                    tovarNarhiObservableList.removeAll(tovarNarhiObservableList);
                }
/*
                    tovarNarhiObservableList = tovarNarhiModels.getDate(connection, tovar.getId(), tovarSana.getSana());
                    tovarNarhiTableView.setItems(tovarNarhiObservableList);
*/
            }
        });
    }

    private String getNarhNomi(Integer item) {
        String narhNomi = "";
        for (Standart nt: narhTuriObservableList) {
            if (nt.getId().equals(item)) {
                narhNomi = nt.getText();
                break;
            }
        }
        return narhNomi;
    }

    public void getNarhlar(Standart tovar) {
        TovarNarhi tovarNarhi = null;
        TovarSana tovarSana = null;
        tovarSanaObservableList = tovarSanaModels.getAnyData(connection,"tovar = " + tovar.getId(),"");tovarSanaTableView.setItems(tovarSanaObservableList);
        for (TovarNarhi tn: tovarNarhiObservableList) {
            tn = new TovarNarhi();
            tn.setUserId(user.getId());
        }
        if (tovarSanaObservableList.size()>0) {
            tovarSanaTableView.getSelectionModel().selectFirst();
            tovarSana = tovarSanaObservableList.get(0);
            tovarNarhiObservableList = tovarNarhiModels.getDate(connection, tovarSana.getTovar(), tovarSana.getSana());
        }
    }

    private void initBottomPane() {
        left_label.setPadding(new Insets(3));
        right_label.setPadding(new Insets(3));
        HBox.setHgrow(left_label, Priority.ALWAYS);
        HBox.setHgrow(pane, Priority.ALWAYS);
        HBox.setHgrow(right_label, Priority.NEVER);
        bottom.getChildren().addAll(left_label, pane, right_label);
        bottom.setAlignment(Pos.CENTER);
    }

    private void initCenterPane() {
        centerPane.setPadding(new Insets(3));
        recordHandlerGridPane = new RecordHandler();
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        VBox.setVgrow(centerPane, Priority.ALWAYS);
        centerPane.getItems().addAll(leftPane, rightPane);
        centerPane.setDividerPositions(.25);
    }

    private void initRightPane() {
        rightPane.getChildren().removeAll(rightPane.getChildren());
        Integer rowIndex = 0;
        rightPane.setPadding(new Insets(3));
        rightPane.add(tugmachalar, 0, rowIndex, 2, 1);
        rowIndex++;
        rightPane.add(tovarSanaTableView, 0, rowIndex, 1,1);
        rightPane.add(tovarNarhiTableView, 1, rowIndex,1,1);
        GridPane.setVgrow(tovarSanaTableView, Priority.ALWAYS);
        GridPane.setVgrow(tovarNarhiTableView, Priority.ALWAYS);
        GridPane.setHgrow(tovarNarhiTableView, Priority.ALWAYS);
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        VBox.setVgrow(rightPane, Priority.ALWAYS);
    }

    private void initLeftPane() {
        leftPane.setPadding(new Insets(3));
        leftPane.setMaxWidth(210);
        leftPane.setMinWidth(210);
        tovarTop.setMaxWidth(290);
        HBox.setHgrow(tovarTop, Priority.ALWAYS);
        HBox.setHgrow(leftPane, Priority.ALWAYS);
        VBox.setVgrow(leftPane, Priority.ALWAYS);
        tovarTop.setPromptText("Tovar nomi");
        barCodeTextField.setPromptText("Shtrixkod");
        leftPane.getChildren().addAll(barCodeTextField, tovarTop, tovarTableView);
        tovarTop.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Taftish(oldValue, newValue);
            }
        });
    }

    private void initSystemMenu() {
        mainMenu = new MenuBar();
        Menu menuFile = new Menu("File");
        Menu menuView = new Menu("View");
        Menu menuEdit = new Menu("Edit");
        Menu menuHelp = new Menu("Help");
        mainMenu.getMenus().addAll(menuFile, menuEdit, menuView, menuHelp);
    }

    private void initBorderPane() {
        borderpane.setTop(mainMenu);
        borderpane.setCenter(centerPane);
        borderpane.setBottom(bottom);
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Tovarlar narhi");
        scene = new Scene(borderpane, 750, 400);
        stage.setScene(scene);
        barCodeOn();
        stage.setOnCloseRequest(event -> {
            barCodeOff();
        });
    }

    public Standart getTovar(Integer id) {
        Standart tovar = null;
        for (Standart t: tovarObservableList) {
            if (t.getId().equals(id)) {
                tovar = t;
                break;
            }
        }
        return tovar;
    }

    class RecordHandler extends GridPane{
        GridPane gridPane = new GridPane();
        Button qaydEtButton = new Button("");
        Button cancelButton = new Button("<<");

        Label sanaLabel = new Label("Sana va vaqt");
        DatePicker sanaDatePicker = new DatePicker();
        TextField vaqtTextField = new TextField();
        HBox sanaHBox = new HBox(10);
        ObservableList<TovarNarhi> tovarNarhlari = FXCollections.observableArrayList();
        Integer rowIndex = 0;
        TableView<TovarNarhi> tableView = new TableView<>();
        TovarNarhi tovarNarhi;

        public RecordHandler() {
            setVgap(10);
            sanaHBox.getChildren().addAll(sanaDatePicker, vaqtTextField);
            sanaDatePicker.setMaxWidth(2000);
            sanaDatePicker.setPrefWidth(150);
            HBox.setHgrow(sanaDatePicker, Priority.ALWAYS);
            HBox.setHgrow(vaqtTextField, Priority.ALWAYS);

            qaydEtButton.setMaxWidth(2000);
            qaydEtButton.setPrefWidth(150);
            cancelButton.setMaxWidth(2000);
            cancelButton.setPrefWidth(150);
            initTable();
        }

        public void initTable() {
            TableColumn<TovarNarhi, Integer> narhTuri = new TableColumn<>("Narh turi");
            narhTuri.setMinWidth(250);
            narhTuri.setMaxWidth(250);
            narhTuri.setCellValueFactory(new PropertyValueFactory<>("narhTuri"));
            narhTuri.setCellFactory(column -> {
                TableCell<TovarNarhi, Integer> cell = new TableCell<TovarNarhi, Integer>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty) {
                            setText(null);
                        }
                        else {
                            setText(getNarhNomi(item));
                        }
                    }
                };
                cell.setAlignment(Pos.CENTER_LEFT);
                return cell;
            });

            TableColumn<TovarNarhi, Double> narh = new TableColumn<>("Narh");
            narh.setMinWidth(150);
            narh.setMaxWidth(150);
            narh.setCellValueFactory(new PropertyValueFactory<>("narh"));
/*
            narh.setCellFactory((TableColumn<TovarNarhi, Double> param) -> {
                return new EditingCell<TovarNarhi, Double>();
            });
*/
//            narh.setCellFactory(TextFieldButtonTableCell.forTableColumn(new DoubleStringConverter()));
            narh.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
                @Override
                public String toString(Double object) {
                    NumberFormat numberFormat = NumberFormat.getInstance();
                    numberFormat.setMinimumIntegerDigits (1);
                    numberFormat.setMaximumIntegerDigits (10);

                    numberFormat.setMinimumFractionDigits (1);
                    numberFormat.setMaximumFractionDigits (2);
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
            narh.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<TovarNarhi, Double>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<TovarNarhi, Double> event) {
                    if (event.getNewValue() != null) {
                        tovarNarhi.setNarh(event.getNewValue());
                        tableView.refresh();
                    }
                }
            });
            narh.setStyle( "-fx-alignment: CENTER;");
            tableView.getColumns().addAll(narhTuri, narh);
            tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    tovarNarhi = newValue;
                }
            });
            tableView.setItems(tovarNarhlari);
            tableView.setEditable(true);
        }

        public void showAdd() {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            Date sana1 = new Date();
            LocalDateTime localDateTime = sana1.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            Integer day = localDateTime.getDayOfMonth();
            Integer month = localDateTime.getMonthValue();
            Integer year = localDateTime.getYear();
            sanaDatePicker.setValue(LocalDate.of(year, month, day));
            vaqtTextField.setText(sdf.format(sana1));
            tovarNarhlari.removeAll(tovarNarhlari);
            for (Standart nt: narhTuriObservableList) {
                tovarNarhlari.add(new TovarNarhi(
                        null,
                        sana1,
                        tovar.getId(),
                        nt.getId(),
                        1,
                        1.0,
                        .0,
                        user.getId(),
                        new Date()
                ));
                tableView.setItems(tovarNarhlari);
            }

            getChildren().removeAll(getChildren());
            setPadding(new Insets(3));

            rowIndex = 0;
            add(sanaLabel, 0, rowIndex, 1,1);
            sanaLabel.setDisable(false);
            add(sanaHBox, 1, rowIndex, 1, 1);
            sanaHBox.setDisable(false);
            GridPane.setHgrow(sanaHBox, Priority.ALWAYS);

            rowIndex++;
            add(tableView, 0, rowIndex, 2,1);
            tableView.setDisable(false);
            GridPane.setHgrow(tableView, Priority.ALWAYS);
            GridPane.setVgrow(tableView, Priority.ALWAYS);

            rowIndex++;
            qaydEtButton = new Tugmachalar().getAdd();
            qaydEtButton.setMaxWidth(2000);
            qaydEtButton.setPrefWidth(150);
            add(cancelButton, 0, rowIndex, 1, 1);
            add(qaydEtButton, 1, rowIndex, 1, 1);
            GridPane.setHgrow(qaydEtButton, Priority.ALWAYS);
            GridPane.setHalignment(cancelButton, HPos.LEFT);
            GridPane.setHalignment(qaydEtButton, HPos.RIGHT);
        }

        public void saveAdd() {
            LocalDateTime localDateTime = LocalDateTime.of(sanaDatePicker.getValue(), LocalTime.parse(vaqtTextField.getText()));
            Instant instant = Instant.from(localDateTime.atZone(ZoneId.systemDefault()));
            tovarSana = new TovarSana(
                    null,
                    Date.from(instant),
                    tovarTableView.getSelectionModel().getSelectedItem().getId(),
                    user.getId(),
                    new Date()
            );
            tovarNarhiObservableList.removeAll(tovarNarhiObservableList);
            tovarSana.setId(tovarSanaModels.insert_data(connection, tovarSana));
            for (TovarNarhi tn: tovarNarhlari) {
                tn.setId(tovarNarhiModels.insert_data(connection, tn));
                tovarNarhiObservableList.add(tn);
            }
            tovarSanaObservableList.add(tovarSana);
            tovarSanaTableView.getSelectionModel().select(tovarSana);
        }

        public void showDelete() {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            tovarSana = tovarSanaTableView.getSelectionModel().getSelectedItem();
            LocalDateTime localDateTime = tovarSana.getSana().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            Integer day = localDateTime.getDayOfMonth();
            Integer month = localDateTime.getMonthValue();
            Integer year = localDateTime.getYear();
            sanaDatePicker.setValue(LocalDate.of(year, month, day));
            vaqtTextField.setText(sdf.format(tovarSana.getSana()));
            tovarNarhlari.removeAll(tovarNarhlari);
            tovarNarhlari.addAll(tovarNarhiObservableList);

            getChildren().removeAll(getChildren());
            setPadding(new Insets(3));

            rowIndex = 0;
            sanaLabel.setDisable(true);
            add(sanaLabel, 0, rowIndex, 1,1);
            sanaHBox.setDisable(true);
            add(sanaHBox, 1, rowIndex, 1, 1);
            GridPane.setHgrow(sanaHBox, Priority.ALWAYS);

            rowIndex++;
            add(tableView, 0, rowIndex, 2,1);
            tableView.setDisable(true);
            GridPane.setHgrow(tableView, Priority.ALWAYS);
            GridPane.setVgrow(tableView, Priority.ALWAYS);

            rowIndex++;
            qaydEtButton = new Tugmachalar().getDelete();
            qaydEtButton.setMaxWidth(2000);
            qaydEtButton.setPrefWidth(150);
            add(cancelButton, 0, rowIndex, 1, 1);
            add(qaydEtButton, 1, rowIndex, 1, 1);
            GridPane.setHgrow(qaydEtButton, Priority.ALWAYS);
            GridPane.setHalignment(cancelButton, HPos.LEFT);
            GridPane.setHalignment(qaydEtButton, HPos.RIGHT);
        }

        public void saveDelete() {
            for (TovarNarhi tn: tovarNarhiObservableList) {
                tovarNarhiModels.delete_data(connection, tn);
            }
            tovarNarhiObservableList.removeAll(tovarNarhiObservableList);
            tovarSanaModels.delete_data(connection, tovarSana);
            tovarSanaObservableList.remove(tovarSana);
        }

        public void showEdit() {
            tovarSana = tovarSanaTableView.getSelectionModel().getSelectedItem();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            LocalDateTime localDateTime = tovarSana.getSana().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            Integer day = localDateTime.getDayOfMonth();
            Integer month = localDateTime.getMonthValue();
            Integer year = localDateTime.getYear();
            sanaDatePicker.setValue(LocalDate.of(year, month, day));
            vaqtTextField.setText(sdf.format(tovarSana.getSana()));
            tovarNarhlari.removeAll(tovarNarhlari);
            for (TovarNarhi tn: tovarNarhiObservableList) {
                tovarNarhlari.add(tn);
            }

            getChildren().removeAll(getChildren());
            setPadding(new Insets(3));

            rowIndex = 0;
            sanaLabel.setDisable(false
            );
            add(sanaLabel, 0, rowIndex, 1,1);
            sanaHBox.setDisable(false);
            add(sanaHBox, 1, rowIndex, 1, 1);
            GridPane.setHgrow(sanaHBox, Priority.ALWAYS);

            rowIndex++;
            add(tableView, 0, rowIndex, 2,1);
            tableView.setDisable(false);
            GridPane.setHgrow(tableView, Priority.ALWAYS);
            GridPane.setVgrow(tableView, Priority.ALWAYS);

            rowIndex++;
            qaydEtButton = new Tugmachalar().getEdit();
            qaydEtButton.setMaxWidth(2000);
            qaydEtButton.setPrefWidth(150);
            add(cancelButton, 0, rowIndex, 1, 1);
            add(qaydEtButton, 1, rowIndex, 1, 1);
            GridPane.setHgrow(qaydEtButton, Priority.ALWAYS);
            GridPane.setHalignment(cancelButton, HPos.LEFT);
            GridPane.setHalignment(qaydEtButton, HPos.RIGHT);
        }

        public void saveEdit() {
            LocalDateTime localDateTime = LocalDateTime.of(sanaDatePicker.getValue(), LocalTime.parse(vaqtTextField.getText()));
            Instant instant = Instant.from(localDateTime.atZone(ZoneId.systemDefault()));
            tovarSana.setSana(Date.from(instant));
            tovarSanaModels.update_data(connection, tovarSana);
            for (TovarNarhi tn: tovarNarhlari) {
                tn.setSana(tovarSana.getSana());
                tovarNarhiModels.update_data(connection, tn);
            }
            tovarSanaTableView.refresh();
            tovarNarhiTableView.refresh();
        }


        public void initMethods() {
        }

    }

    public BarCode getBarCode(String barCode) {
        BarCode barCode1 = null;
        ObservableList<BarCode> tovars = barCodeModels.getAnyData(connection, "barCode = '" + barCode + "'", "");
        if (tovars.size()>0) {
            barCode1 = tovars.get(0);
            barCodeObservableList.add(barCode1);
        }
        return barCode1;
    }


    private void Taftish(String oldValue, String newValue) {
        ObservableList<Standart> subentries = FXCollections.observableArrayList();

        newValue = newValue.toLowerCase();

        if ( oldValue != null && (newValue.length() < oldValue.length()) ) {
            tovarTableView.setItems(tovarObservableList);
        }

        for ( Standart t: tovarObservableList ) {
            if (t.getText().toLowerCase().contains(newValue)) {
                subentries.add(t);
            }
        }
        tovarTableView.setItems(subentries);
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
                    boolean topdim = false;
                    String string = stringBuffer.toString().trim();
                    stringBuffer.delete(0, stringBuffer.length());
                    if (!string.isEmpty()) {
                        BarCode barCode = GetDbData.getBarCode(string);
                        if (barCode != null) {
                            Standart tovar = getTovar(barCode.getTovar());
                            barCodeTextField.setText("");
                            tovarTableView.getSelectionModel().select(tovar);
                            tovarTableView.scrollTo(tovar);
                            tovarTableView.refresh();
                        }
                        else {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Diqqat!!!");;
                            alert.setHeaderText(string + " shtrixkodga muvofiq tovar topilmadi");
                            alert.setContentText("");
                            alert.showAndWait();
                        }
                    }
                }
            }
        });

        barCodeTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode()== KeyCode.ENTER) {
                    String string = barCodeTextField.getText().trim();
                    boolean topdim = false;
                    if (!string.isEmpty()) {
                        BarCode barCode = getBarCode(string);
                        if (barCode != null) {
                            Standart tovar = getTovar(barCode.getTovar());
                            barCodeTextField.setText("");
                            tovarTableView.getSelectionModel().select(tovar);
                            tovarTableView.scrollTo(tovar);
                        }
                        else {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Diqqat!!!");;
                            alert.setHeaderText(string + " shtrixkodga muvofiq tovar topilmadi");
                            alert.setContentText("");
                            alert.showAndWait();
                        }
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

}
