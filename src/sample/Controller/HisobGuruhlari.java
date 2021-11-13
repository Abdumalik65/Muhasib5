package sample.Controller;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import sample.Config.MySqlDB;
import sample.Data.*;
import sample.Model.HisobKitobModels;
import sample.Model.HisobModels;
import sample.Model.Standart3Models;
import sample.Model.StandartModels;
import sample.Tools.*;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

public class HisobGuruhlari extends Application {
    Stage stage;
    BorderPane borderpane = new BorderPane();
    MenuBar mainMenu;
    SplitPane centerPane = new SplitPane();
    Tugmachalar leftButtons = new Tugmachalar();
    Tugmachalar rightButtons = new Tugmachalar();
    TextField textField = new TextField();

    TableView<Standart> leftTableView = new TableView<>();
    TableView<Standart3> rightTableView = new TableView<>();
    TableView<Hisob> hisobTableView = new TableView<>();
    HBox hisobButtonsPane = new HBox();
    Button qaydEtButton;
    Button cancelButton = new Button("<<");
    DatePicker datePicker;
    LocalDate localDate;
    ObservableList<Standart> guruhlarNomi;
    ObservableList<Standart3> guruhTarkibi;
    ObservableList<Hisob> hisobObservableList;
    ObservableList<Hisob> hisobTableList = FXCollections.observableArrayList();
    ObservableList<Balans> balansList = FXCollections.observableArrayList();


    Callback balansCellValueFactory;
    Scene scene;
    User user;
    Connection connection;

    StandartModels standartModels = new StandartModels();
    Standart3Models standart3Models = new Standart3Models();
    HisobKitobModels hisobKitobModels = new HisobKitobModels();
    HisobModels hisobModels = new HisobModels();

    Standart guruhCursor;

    VBox rightPane = new VBox();
    VBox leftPane = new VBox();

    int padding = 3;
    DecimalFormat decimalFormat = new MoneyShow();

    public static void main(String[] args) {
        launch(args);
    }

    public HisobGuruhlari() {
        connection = new MySqlDB().getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
    }

    public HisobGuruhlari(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
    }

    private void ibtido() {
        initData();
        initLeftButtons();
        initLeftTableView();
        initLeftPane();
        initRightButtons();
        initRightTableView();
        initHisobButtonsPane();
        initHisobTableView();
        initRightPane();
        initCenterPane();
        initBorderPane();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initStage(primaryStage);
        ibtido();
        stage.show();
    }

    public void display() {
        stage = new Stage();
        initStage(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
        ibtido();
        stage.showAndWait();
    }

    private void initData() {
        initDatePicker();
        standartModels.setTABLENAME("HisobGuruhlarNomi");
        guruhlarNomi = standartModels.get_data(connection);
        if (guruhlarNomi.size()>0) {
            Standart standart = guruhlarNomi.get(0);
            standart3Models.setTABLENAME("HisobGuruhTarkibi");
            hisobObservableList = hisobModels.get_data1(connection);
            for (Hisob h: hisobObservableList) {
                hisobTableList.add(h);
            }
            guruhTarkibi = standart3Models.getAnyData(connection, "id2 = " + standart.getId(), "");
            for (Standart s: guruhlarNomi) {
                ObservableList<Standart3> s3List = standart3Models.getAnyData(connection, "id2 = " + s.getId(), "");
                double summa = .0;
                for (Standart3 s3: s3List) {
                    Hisob h = getHisob(s3.getId3());
                    if (h != null) {
                        summa = summa + h.getBalans();
                    }
                }
                Balans b = new Balans(s.getId(), .0, .0, .0, summa);
                balansList.add(b);
            }
        }
    }

    private Hisob getHisob(int id) {
        Hisob hisob = null;
        for (Hisob h: hisobObservableList) {
            if (h.getId().equals(id)) {
                hisob = h;
                break;
            }
        }
        return hisob;
    }

    private void initLeftButtons() {
        leftButtons.getChildren().remove(leftButtons.getEdit());
        leftButtons.getAdd().setOnAction(event -> {
            standartModels.setTABLENAME("HisobGuruhlarNomi");
            Standart standart = new Standart(null, "Yangi guruh", user.getId(), null);
            guruhlarNomi.add(standart);
            standartModels.insert_data(connection, standart);
            leftTableView.scrollTo(standart);
            leftTableView.refresh();
        });

        leftButtons.getDelete().setOnAction(event -> {
            Standart standart = leftTableView.getSelectionModel().getSelectedItem();
            if (standart != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.getButtonTypes().removeAll(alert.getButtonTypes());
                ButtonType okButton = new ButtonType("Ha");
                ButtonType noButton = new ButtonType("Yo`q");
                alert.getButtonTypes().addAll(okButton, noButton);
                alert.setTitle("Diqqat !!!");
                alert.setHeaderText(standart.getText() + " guruhi va unga taalluqli hamma hisobllar jadvaldan o`chirilladi");
                alert.setContentText("Rozimisiz");
                Optional<ButtonType> option = alert.showAndWait();
                ButtonType buttonType = option.get();
                if (okButton.equals(buttonType)) {
                    standart3Models.deleteBatch(connection, guruhTarkibi);
                    guruhTarkibi.removeAll(guruhTarkibi);
                    rightTableView.setItems(guruhTarkibi);
                    rightTableView.refresh();

                    standartModels.delete_data(connection, standart);
                    guruhlarNomi.remove(standart);
                    leftTableView.setItems(guruhlarNomi);
                    leftTableView.refresh();
                }
            }
        });

        leftButtons.getEdit().setOnAction(event -> {});

        leftButtons.getChildren().add(datePicker);
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
        datePicker.setPrefWidth(150);
        HBox.setHgrow(datePicker, Priority.ALWAYS);

        datePicker.setOnAction(event -> {
            LocalDate newDate = datePicker.getValue();
            if (newDate != null) {
                localDate = newDate;
                Date date = null;
                LocalDateTime localDateTime = LocalDateTime.of(localDate, LocalTime.of(23,59,59));
                Instant instant = Instant.from(localDateTime.atZone(ZoneId.systemDefault()));
                date = Date.from(instant);
                hisobObservableList = hisobModels.get_data1(connection, date);
                refreshBalance();
                rightTableView.refresh();
                leftTableView.refresh();
            }
        });
    }

    private void refreshBalance() {
        for (Standart s: guruhlarNomi) {
            ObservableList<Standart3> s3List = standart3Models.getAnyData(connection, "id2 = " + s.getId(), "");
            double summa = .0;
            for (Standart3 s3: s3List) {
                Hisob h = getHisob(s3.getId3());
                if (h != null) {
                    summa = summa + h.getBalans();
                }
            }
            for (Balans b: balansList){
                if (b.getId().equals(s.getId())) {
                    b.setBalans(summa);
                    break;
                }
            }
        }
    }

    private void initLeftTableView() {
        HBox.setHgrow(leftTableView, Priority.ALWAYS);
        VBox.setVgrow(leftTableView, Priority.ALWAYS);
        ContextMenu contextMenu = initContextMenu();
        leftTableView.getColumns().addAll(getGuruhColumn(), getBalansColumn());
        leftTableView.setItems(guruhlarNomi);
        leftTableView.setEditable(true);
        leftTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                guruhCursor = newValue;
                guruhTarkibi = standart3Models.getAnyData(connection, "id2 = " + guruhCursor.getId(), "");
                rightTableView.setItems(guruhTarkibi);
                rightTableView.refresh();
            }
        });
        leftTableView.setContextMenu(contextMenu);
    }

    private TableColumn<Standart, Integer> getIdColumn() {
        TableColumn<Standart, Integer> idColumn = new TableColumn<>("№");
        idColumn.setMinWidth(15);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        return idColumn;
    }

    private TableColumn<Standart, String> getGuruhColumn() {
        TableColumn<Standart, String> textColumn = new TableColumn<>("Guruh nomi");
        textColumn.setMinWidth(180);
        textColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
        textColumn.setCellFactory(TextFieldTableCell.<Standart> forTableColumn());
        textColumn.setOnEditCommit((TableColumn.CellEditEvent<Standart, String> event) -> {
            String newString = event.getNewValue();
            if (newString != null) {
                TablePosition<Standart, String> pos = event.getTablePosition();
                int row = pos.getRow();
                Standart guruh = event.getTableView().getItems().get(row);
                guruh.setText(newString);
                standartModels.update_data(connection, guruh);
            }
        });
        return textColumn;
    }

    private Balans getBalans(Integer id) {
        Balans balans = null;
        for (Balans b: balansList) {
            if (b.getId().equals(id)) {
                balans = b;
                break;
            }
        }
        return balans;
    }

    private TableColumn<Standart, Balans> getBalansColumn() {
        TableColumn<Standart, Balans> balansColumn = new TableColumn<>("Balans");
        balansCellValueFactory = new Callback<TableColumn.CellDataFeatures<Standart, Balans>, ObservableValue<Balans>>() {
            @Override
            public ObservableValue<Balans> call(TableColumn.CellDataFeatures<Standart, Balans> param) {
                Standart standart = param.getValue();
                Balans b = getBalans(standart.getId());
                return new SimpleObjectProperty<Balans>(b);
            }

        };
        balansColumn.setMinWidth(100);
        balansColumn.setCellValueFactory(balansCellValueFactory);
        balansColumn.setCellFactory(column -> {
            TableCell<Standart, Balans> cell = new TableCell<Standart, Balans>() {
                @Override
                protected void updateItem(Balans item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        if (item != null)
                        setText(decimalFormat.format(item.getBalans()));
                    }
                }
            };
            return cell;
        });
        balansColumn.setStyle( "-fx-alignment: CENTER;");

        return balansColumn;
    }

    private void initLeftPane() {
        leftPane.setPadding(new Insets(padding));
        HBox.setHgrow(leftPane, Priority.ALWAYS);
        VBox.setVgrow(leftPane, Priority.ALWAYS);
        leftPane.getChildren().addAll(leftButtons, leftTableView);
    }

    private void initCenterPane() {
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        VBox.setVgrow(centerPane, Priority.ALWAYS);
        centerPane.getItems().addAll(leftPane, rightPane);
    }

    private void initRightButtons() {
        rightButtons.getChildren().remove(rightButtons.getEdit());
        rightButtons.getChildren().add(textField);
        textField.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Taftish(oldValue, newValue);
            }
        }));

        rightButtons.getAdd().setOnAction(event -> {
            if (leftTableView.getSelectionModel().getSelectedItem() != null) {
                leftPane.setDisable(true);
                rightPane.getChildren().removeAll(rightPane.getChildren());
                hisobButtonsPane.getChildren().removeAll(hisobButtonsPane.getChildren());
                rightPane.getChildren().addAll(hisobTableView, hisobButtonsPane);
                qaydEtButton = new Tugmachalar().getAdd();
                initHBoxButtons();
                hisobButtonsPane.getChildren().addAll(cancelButton, qaydEtButton);
                guruhToHisob();

                qaydEtButton.setOnAction(event1 -> {
                    hisobToGuruh();
                    cancelButton.fire();
                });

                cancelButton.setOnAction(event1 -> {
                    leftPane.setDisable(false);
                    rightPane.getChildren().removeAll(rightPane.getChildren());
                    rightPane.getChildren().addAll(rightButtons, rightTableView);
                });
            }

        });

        rightButtons.getDelete().setOnAction(event -> {
            Standart3 standart3 = rightTableView.getSelectionModel().getSelectedItem();
            if (standart3 != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.getButtonTypes().removeAll(alert.getButtonTypes());
                ButtonType okButton = new ButtonType("Ha");
                ButtonType noButton = new ButtonType("Yo`q");
                alert.getButtonTypes().addAll(okButton, noButton);
                alert.setTitle("Diqqat !!!");
                alert.setHeaderText(standart3.getText() + " hisob jadvaldan o`chirilladi");
                alert.setContentText("Rozimisiz");
                Optional<ButtonType> option = alert.showAndWait();
                ButtonType buttonType = option.get();
                if (okButton.equals(buttonType)) {
                    standart3Models.delete_data(connection, standart3);
                    guruhTarkibi.remove(standart3);
                    rightTableView.setItems(guruhTarkibi);
                    rightTableView.refresh();
                }
            }
        });

        rightButtons.getExcel().setOnAction(event -> {
            ObservableList<Hisob> h2List = FXCollections.observableArrayList();
            for (Standart3 s3: guruhTarkibi) {
                Hisob hisob = GetDbData.hisobniTop(s3.getId3(), hisobObservableList);
                if (hisob != null) {
                    h2List.add(hisob);
                }
            }
            if (h2List.size()>0) {
                ExportToExcel exportToExcel = new ExportToExcel();
                exportToExcel.hisoblar(h2List);
            }
        });
    }

    private void initHBoxButtons() {
        HBox.setHgrow(cancelButton, Priority.ALWAYS);
        HBox.setHgrow(qaydEtButton, Priority.ALWAYS);
        cancelButton.setMaxWidth(2000);
        cancelButton.setPrefWidth(150);
        qaydEtButton.setMaxWidth(2000);
        qaydEtButton.setPrefWidth(150);
    }

    private void initRightTableView() {
        HBox.setHgrow(rightTableView, Priority.ALWAYS);
        VBox.setVgrow(rightTableView, Priority.ALWAYS);
        rightTableView.setItems(guruhTarkibi);
        rightTableView.getColumns().addAll(getHisobColumn(), getHisobBalansColumn());
    }

    private TableColumn<Standart3, Integer> getHisobIdColumn() {
        TableColumn<Standart3, Integer> idColumn = new TableColumn<>("Hisob nomi");
        idColumn.setMinWidth(150);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id3"));
        idColumn.setCellFactory(column -> {
            TableCell<Standart3, Integer> cell = new TableCell<Standart3, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        setText(GetDbData.hisobniTop(item, hisobObservableList).getText());
                    }
                }
            };
            return cell;
        });
        return idColumn;
    }

    private TableColumn<Standart3, Hisob> getHisobColumn() {
        TableColumn<Standart3, Hisob> hisobColumn = new TableColumn<>("Hisob nomi");
        hisobColumn.setMinWidth(180);
        hisobColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Standart3, Hisob>, ObservableValue<Hisob>>() {

            @Override
            public ObservableValue<Hisob> call(TableColumn.CellDataFeatures<Standart3, Hisob> param) {
                Standart3 standart3 = param.getValue();
                Hisob hisob = GetDbData.hisobniTop(standart3.getId3(), hisobObservableList);
                return new SimpleObjectProperty<Hisob>(hisob);
            }
        });

        hisobColumn.setCellFactory(ComboBoxTableCell.forTableColumn(hisobObservableList));
        hisobColumn.setStyle( "-fx-alignment: CENTER_LEFT;");
        return hisobColumn;
    }

    private TableColumn<Standart3, Hisob> getHisobBalansColumn() {
        TableColumn<Standart3, Hisob> balansColumn = new TableColumn<>("Balans");
        balansColumn.setMinWidth(100);
        balansColumn.setCellFactory(column -> {
            TableCell<Standart3, Hisob> cell = new TableCell<Standart3, Hisob>() {
                @Override
                protected void updateItem(Hisob item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        if (item != null) {
                            setText(decimalFormat.format(item.getBalans()));
                        }
                    }
                }
            };
            return cell;
        });
        Callback balansCellValueFactory = new Callback<TableColumn.CellDataFeatures<Standart3, Hisob>, ObservableValue<Hisob>>() {
            @Override
            public ObservableValue<Hisob> call(TableColumn.CellDataFeatures<Standart3, Hisob> param) {
                Standart3 standart3 = param.getValue();
                Hisob h = GetDbData.hisobniTop(standart3.getId3(), hisobObservableList);
                return new SimpleObjectProperty<Hisob>(h);
            }
        };
        balansColumn.setCellValueFactory(balansCellValueFactory);
        balansColumn.setStyle( "-fx-alignment: CENTER;");
        return balansColumn;
    }

    private void initHisobTableView() {
        HBox.setHgrow(hisobTableView, Priority.ALWAYS);
        VBox.setVgrow(hisobTableView, Priority.ALWAYS);
        hisobTableView.setItems(hisobTableList);
        hisobTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        hisobTableView.getColumns().addAll(getHisobNomiColumn(), getBalansHisobColumn());
    }

    private TableColumn<Hisob, String> getHisobNomiColumn() {
        TableColumn<Hisob, String> hisobNomi = new TableColumn<>("Hisob");
        hisobNomi.setMinWidth(200);
        hisobNomi.setCellValueFactory(new PropertyValueFactory<>("text"));
        return hisobNomi;
    }

    public TableColumn<Hisob, Double> getBalansHisobColumn() {
        TableColumn<Hisob, Double> hisobBalansColumn = new TableColumn<>("Balans");
        hisobBalansColumn.setMinWidth(90);
        hisobBalansColumn.setCellValueFactory(new PropertyValueFactory<>("balans"));
        hisobBalansColumn.setCellFactory(column -> {
            TableCell<Hisob, Double> cell = new TableCell<Hisob, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        String itemString = new MoneyShow().format(item);
                        if (itemString.trim().equals("-0")) {
                            itemString = "0";
                        }
                        setText(itemString);                    }
                }
            };
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        return hisobBalansColumn;
    }
    private void initRightPane() {
        rightPane.setPadding(new Insets(padding));
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        VBox.setVgrow(rightPane, Priority.ALWAYS);
        rightPane.getChildren().addAll(rightButtons, rightTableView);
    }

    private void initHisobButtonsPane() {
        HBox.setHgrow(hisobButtonsPane, Priority.ALWAYS);
        VBox.setVgrow(hisobButtonsPane, Priority.ALWAYS);
    }

    private void initBorderPane() {
        borderpane.setTop(mainMenu);
        borderpane.setCenter(centerPane);
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Hisob guruhlari");
        Scene scene = new Scene(borderpane, 700, 400);
        stage.setScene(scene);
    }

    private Hisob hisobBalans(Hisob hisob) {
        double kirim = 0.0;
        double chiqim = 0.0;
        double balans = 0.0;
        Date date = new Date();
        ObservableList<HisobKitob> kirimObservableList;
        ObservableList<HisobKitob> chiqimObservableList;
        kirimObservableList = hisobKitobModels.getAnyData(connection, "hisob2 = " + hisob.getId(), "");
        for (HisobKitob k: kirimObservableList) {
            if (!k.getDateTime().after(date)) {
                double jami = (k.getTovar() == 0 ? 1 : k.getDona()) * k.getNarh() / k.getKurs();
                kirim += jami;
            }
        }
        hisob.setKirim(kirim);
        chiqimObservableList = hisobKitobModels.getAnyData(connection, "hisob1 = " + hisob.getId(), "");
        for (HisobKitob ch: chiqimObservableList) {
            if (!ch.getDateTime().after(date)) {
                double jami = (ch.getTovar() == 0 ? 1 : ch.getDona()) * ch.getNarh() / ch.getKurs();
                chiqim += jami;
            }
        }
        hisob.setChiqim(chiqim);
        balans = kirim - chiqim;
        hisob.setBalans(balans);
        return hisob;
    }

    private Hisob hisobBalans2(Hisob hisob) {
        double kirim = 0.0;
        double chiqim = 0.0;
        double balans = 0.0;
        LocalDateTime localDateTime = LocalDateTime.of(datePicker.getValue(), LocalTime.parse("23:59:59"));
        Instant instant = Instant.from(localDateTime.atZone(ZoneId.systemDefault()));
        Date date1 = Date.from(instant);
        ObservableList<HisobKitob> kirimObservableList;
        ObservableList<HisobKitob> chiqimObservableList;
        kirimObservableList = hisobKitobModels.getAnyData(connection, "hisob2 = " + hisob.getId(), "");
        for (HisobKitob k: kirimObservableList) {
            if (!k.getDateTime().after(date1)) {
                double jami = (k.getTovar() == 0 ? 1 : k.getDona()) * k.getNarh() / k.getKurs();
                kirim += jami;
            }
        }
        hisob.setKirim(kirim);
        chiqimObservableList = hisobKitobModels.getAnyData(connection, "hisob1 = " + hisob.getId(), "");
        for (HisobKitob ch: chiqimObservableList) {
            if (!ch.getDateTime().after(date1)) {
                double jami = (ch.getTovar() == 0 ? 1 : ch.getDona()) * ch.getNarh() / ch.getKurs();
                chiqim += jami;
            }
        }
        hisob.setChiqim(chiqim);
        balans = kirim - chiqim;
        hisob.setBalans(balans);
        return hisob;
    }

    private void guruhToHisob() {
        hisobTableList.removeAll(hisobTableList);
        hisobTableList.addAll(hisobObservableList);
        for (Standart3 s3: guruhTarkibi) {
            Hisob h = GetDbData.hisobniTop(s3.getId3(), hisobObservableList);
            hisobTableList.remove(h);
        }
    }

    private ObservableList<Standart3> hisobToGuruh() {
        ObservableList<Hisob> hList = hisobTableView.getSelectionModel().getSelectedItems();
        ObservableList<Standart3> guruh = FXCollections.observableArrayList();
        standart3Models.setTABLENAME("HisobGuruhTarkibi");
        for (Hisob h: hList) {
            if (!getGuruh(h.getId())) {
                Standart3 s3 = new Standart3(null, guruhCursor.getId(), h.getId(), h.getText(), user.getId(), null);
                guruh.add(s3);
                standart3Models.insert_data(connection, s3);
            }
        }
        guruhTarkibi.addAll(guruh);
        rightTableView.setItems(guruhTarkibi);
        rightTableView.refresh();
        return guruh;
    }

    private boolean getGuruh(int hisobId) {
        boolean bormi = false;
        for (Standart3 s3: guruhTarkibi) {
            if (s3.getId3().equals(hisobId)) {
                bormi = true;
                break;
            }
        }
        return bormi;
    }

    private void guruhlarSetBalanns() {

    }

    private ContextMenu initContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        ImageView imageView = new PathToImageView("/sample/images/Icons/add.png").getImageView();
        MenuItem addMenu = new MenuItem("Qo`sh", imageView);

        imageView = new PathToImageView("/sample/images/Icons/delete.png").getImageView();
        MenuItem deleteMenu = new MenuItem("O`chir", imageView);

        imageView = new PathToImageView("/sample/images/Icons/edit.png").getImageView();
        MenuItem editMenu = new MenuItem("O`zgartir", imageView);

        imageView = new PathToImageView("/sample/images/png/chart.png", 24,24).getImageView();
        MenuItem grafikDiagramma = new MenuItem("Grafik diagramma", imageView);


        contextMenu.getItems().add(addMenu);
        contextMenu.getItems().add(deleteMenu);
        contextMenu.getItems().add(grafikDiagramma);

        addMenu.setOnAction(event -> {
        });

        deleteMenu.setOnAction(event -> {
        });

        editMenu.setOnAction(event -> {
        });

        grafikDiagramma.setOnAction(event -> {
            MyChart myChart = new MyChart(connection, user);
            myChart.display(localDate, guruhCursor, guruhTarkibi);
        });

        return contextMenu;
    }

    private void Taftish(String oldValue, String newValue) {
        ObservableList<Standart3> subentries = FXCollections.observableArrayList();
        newValue = newValue.toLowerCase();

        if ( oldValue != null && (newValue.length() < oldValue.length()) ) {
            rightTableView.setItems(guruhTarkibi);
        }

        for ( Standart3 s3: guruhTarkibi ) {
            if (s3.getText().toLowerCase().contains(newValue)) {
                subentries.add(s3);
            }
        }
        rightTableView.setItems(subentries);
    }
}
