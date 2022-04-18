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
import sample.Config.MySqlDBGeneral;
import sample.Data.*;
import sample.Enums.ServerType;
import sample.Model.*;
import sample.Tools.*;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.time.*;
import java.util.Date;
import java.util.Optional;

public class DasturCheklovlari extends Application {
    Stage stage;
    BorderPane borderpane = new BorderPane();
    MenuBar mainMenu;
    SplitPane centerPane = new SplitPane();
    Tugmachalar rightButtons = new Tugmachalar();
    TextField textField = new TextField();

    TableView<Standart> leftTableView = new TableView<>();
    TableView<Standart3> rightTableView = new TableView<>();
    TableView<Standart> dasturTableView = new TableView<>();
    HBox hisobButtonsPane = new HBox();
    Button qaydEtButton;
    Button cancelButton = new Button("<<");
    DatePicker datePicker;
    LocalDate localDate;
    ObservableList<Standart> userList;
    ObservableList<Standart3> cheklanganDasturJadvali;
    ObservableList<Standart> dasturObservableList;
    ObservableList<Standart> dasturJadvali = FXCollections.observableArrayList();
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

    public DasturCheklovlari() {
        connection = new MySqlDBGeneral(ServerType.REMOTE).getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
    }

    public DasturCheklovlari(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        String classSimpleName = getClass().getSimpleName();
        DasturlarRoyxati.dastur(connection, user, classSimpleName);
    }

    private void ibtido() {
        initData();
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

    private ObservableList<Standart> userList() {
        UserModels userModels = new UserModels();
        ObservableList<User> userObservableList = userModels.getData(connection);
        ObservableList<Standart> userList = FXCollections.observableArrayList();
        for (User u: userObservableList) {
            userList.add(new Standart(u.getId(), u.getIsm(), user.getId(), new Date()));
        }
        return userList;
    }

    private void initData() {
        standartModels.setTABLENAME("HisobGuruhlarNomi");
        userList = userList();
        if (userList.size()>0) {
            Standart standart = userList.get(0);
            standart3Models.setTABLENAME("CheklanganDasturTarkibi");
            standartModels.setTABLENAME("Dasturlar");
            dasturObservableList = standartModels.get_data(connection);
            for (Standart s: dasturObservableList) {
                dasturJadvali.add(s);
            }
            cheklanganDasturJadvali = standart3Models.getAnyData(connection, "id2 = " + standart.getId(), "");
            for (Standart s: userList) {
                ObservableList<Standart3> s3List = standart3Models.getAnyData(connection, "id2 = " + s.getId(), "");
            }
        }
    }

    private Standart getHisob(int id) {
        Standart standart = null;
        for (Standart s: dasturObservableList) {
            if (s.getId().equals(id)) {
                standart = s;
                break;
            }
        }
        return standart;
    }

    private void initLeftTableView() {
        HBox.setHgrow(leftTableView, Priority.ALWAYS);
        VBox.setVgrow(leftTableView, Priority.ALWAYS);
        leftTableView.getColumns().addAll(getGuruhColumn());
        leftTableView.setItems(userList);
        leftTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                guruhCursor = newValue;
                cheklanganDasturJadvali = standart3Models.getAnyData(connection, "id2 = " + guruhCursor.getId(), "");
                rightTableView.setItems(cheklanganDasturJadvali);
                rightTableView.refresh();
            }
        });
    }

    private TableColumn<Standart, Integer> getIdColumn() {
        TableColumn<Standart, Integer> idColumn = new TableColumn<>("№");
        idColumn.setMinWidth(15);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        return idColumn;
    }

    private TableColumn<Standart, String> getGuruhColumn() {
        TableColumn<Standart, String> textColumn = new TableColumn<>("Xodim nomi");
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
        initLeftTableView();
        leftPane.getChildren().addAll(leftTableView);
    }

    private void initCenterPane() {
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        VBox.setVgrow(centerPane, Priority.ALWAYS);
        initLeftPane();
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
                rightPane.getChildren().addAll(dasturTableView, hisobButtonsPane);
                qaydEtButton = new Tugmachalar().getAdd();
                initHBoxButtons();
                hisobButtonsPane.getChildren().addAll(cancelButton, qaydEtButton);
                standart3ToStandart();

                qaydEtButton.setOnAction(event1 -> {
                    standartToStandart3();
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
                    cheklanganDasturJadvali.remove(standart3);
                    rightTableView.setItems(cheklanganDasturJadvali);
                    rightTableView.refresh();
                }
            }
        });

        rightButtons.getExcel().setOnAction(event -> {
            ObservableList<Standart> standarts = FXCollections.observableArrayList();
            for (Standart3 s3: cheklanganDasturJadvali) {
                Standart standart = dasturniTop(s3.getId3(), dasturObservableList);
                if (standart != null) {
                    standarts.add(standart);
                }
            }
/*
            if (standarts.size()>0) {
                ExportToExcel exportToExcel = new ExportToExcel();
                exportToExcel.hisoblar(standarts);
            }
*/
        });
    }

    private Standart dasturniTop(Integer id, ObservableList<Standart> observableList) {
        Standart standart = null;
        for (Standart s: observableList) {
            if (s.getId().equals(id)) {
                standart = s;
                break;
            }
        }
        return  standart;
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
        rightTableView.setItems(cheklanganDasturJadvali);
        rightTableView.getColumns().addAll(getHisobColumn());
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
                        setText(dasturniTop(item, dasturObservableList).getText());
                    }
                }
            };
            return cell;
        });
        return idColumn;
    }

    private TableColumn<Standart3, Standart> getHisobColumn() {
        TableColumn<Standart3, Standart> hisobColumn = new TableColumn<>("Hisob nomi");
        hisobColumn.setMinWidth(180);
        hisobColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Standart3, Standart>, ObservableValue<Standart>>() {

            @Override
            public ObservableValue<Standart> call(TableColumn.CellDataFeatures<Standart3, Standart> param) {
                Standart3 standart3 = param.getValue();
                Standart standart = dasturniTop(standart3.getId3(), dasturObservableList);
                return new SimpleObjectProperty<Standart>(standart);
            }
        });

        hisobColumn.setCellFactory(ComboBoxTableCell.forTableColumn(dasturObservableList));
        hisobColumn.setStyle( "-fx-alignment: CENTER_LEFT;");
        return hisobColumn;
    }

    private TableColumn<Standart3, Standart> getHisobBalansColumn() {
        TableColumn<Standart3, Standart> balansColumn = new TableColumn<>("Balans");
        balansColumn.setMinWidth(100);
        balansColumn.setCellFactory(column -> {
            TableCell<Standart3, Standart> cell = new TableCell<Standart3, Standart>() {
                @Override
                protected void updateItem(Standart item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        if (item != null) {
                        }
                    }
                }
            };
            return cell;
        });
        Callback balansCellValueFactory = new Callback<TableColumn.CellDataFeatures<Standart3, Standart>, ObservableValue<Standart>>() {
            @Override
            public ObservableValue<Standart> call(TableColumn.CellDataFeatures<Standart3, Standart> param) {
                Standart3 standart3 = param.getValue();
                Standart standart = dasturniTop(standart3.getId3(), dasturObservableList);
                return new SimpleObjectProperty<Standart>(standart);
            }
        };
        balansColumn.setCellValueFactory(balansCellValueFactory);
        balansColumn.setStyle( "-fx-alignment: CENTER;");
        return balansColumn;
    }

    private void initHisobTableView() {
        HBox.setHgrow(dasturTableView, Priority.ALWAYS);
        VBox.setVgrow(dasturTableView, Priority.ALWAYS);
        dasturTableView.setItems(dasturJadvali);
        dasturTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        dasturTableView.getColumns().addAll(getHisobNomiColumn());
    }

    private TableColumn<Standart, String> getHisobNomiColumn() {
        TableColumn<Standart, String> hisobNomi = new TableColumn<>("Hisob");
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
        initRightButtons();
        initRightTableView();
        initHisobButtonsPane();
        initHisobTableView();
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

    private void standart3ToStandart() {
        dasturJadvali.removeAll(dasturJadvali);
        dasturJadvali.addAll(dasturObservableList);
        for (Standart3 s3: cheklanganDasturJadvali) {
            Standart s = dasturniTop(s3.getId3(), dasturObservableList);
            dasturJadvali.remove(s);
        }
    }

    private ObservableList<Standart3> standartToStandart3() {
        ObservableList<Standart> standartObservableList = dasturTableView.getSelectionModel().getSelectedItems();
        ObservableList<Standart3> guruh = FXCollections.observableArrayList();
        standart3Models.setTABLENAME("CheklanganHisobTarkibi");
        for (Standart standart: standartObservableList) {
            if (!getGuruh(standart.getId())) {
                Standart3 s3 = new Standart3(null, guruhCursor.getId(), standart.getId(), standart.getText(), user.getId(), null);
                guruh.add(s3);
                standart3Models.insert_data(connection, s3);
            }
        }
        cheklanganDasturJadvali.addAll(guruh);
        rightTableView.setItems(cheklanganDasturJadvali);
        rightTableView.refresh();
        return guruh;
    }

    private boolean getGuruh(int hisobId) {
        boolean bormi = false;
        for (Standart3 s3: cheklanganDasturJadvali) {
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

        contextMenu.getItems().add(addMenu);
        contextMenu.getItems().add(deleteMenu);

        addMenu.setOnAction(event -> {
        });

        deleteMenu.setOnAction(event -> {
        });

        editMenu.setOnAction(event -> {
        });

        return contextMenu;
    }

    private void Taftish(String oldValue, String newValue) {
        ObservableList<Standart3> subentries = FXCollections.observableArrayList();
        newValue = newValue.toLowerCase();

        if ( oldValue != null && (newValue.length() < oldValue.length()) ) {
            rightTableView.setItems(cheklanganDasturJadvali);
        }

        for ( Standart3 s3: cheklanganDasturJadvali) {
            if (s3.getText().toLowerCase().contains(newValue)) {
                subentries.add(s3);
            }
        }
        rightTableView.setItems(subentries);
    }
}
