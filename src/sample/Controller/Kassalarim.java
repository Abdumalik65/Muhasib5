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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import sample.Config.MySqlDB;
import sample.Config.MySqlDBGeneral;
import sample.Data.*;
import sample.Enums.ServerType;
import sample.Model.HisobModels;
import sample.Model.KassaModels;
import sample.Model.StandartModels;
import sample.Model.ValutaModels;
import sample.Tools.DasturlarRoyxati;
import sample.Tools.Encryptor;
import sample.Tools.GetDbData;
import sample.Tools.Tugmachalar;

import java.sql.Connection;
import java.util.Date;

public class Kassalarim extends Application {
    User user = new User(1, "admin", "", "admin");
    Stage stage;
    BorderPane borderpane = new BorderPane();
    MenuBar mainMenu;
    VBox centerPane = new VBox();
    HBox bottom = new HBox();

    Tugmachalar tugmachalar = new Tugmachalar();
    TableView<Kassa> kassaTableView = new TableView();

    Connection connection;
    KassaModels kassaModels = new KassaModels();
    HisobModels hisobModels = new HisobModels();
    ValutaModels valutaModels = new ValutaModels();
    StandartModels standartModels = new StandartModels();

    ObservableList<Kassa> kassaObservableList;
    ObservableList<Hisob> hisobObservableList;
    ObservableList<Standart> tovarObservableList;
    ObservableList<Valuta> valutaObservableList;
    ObservableList<Standart> savdoTuriObservableList;
    ObservableList<Circle> lockObservableList = FXCollections.observableArrayList();

    public static void main(String[] args) {
        launch(args);
    }

    public Kassalarim() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
    }

    public Kassalarim(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        String classSimpleName = getClass().getSimpleName();
        DasturlarRoyxati.dastur(connection, user, classSimpleName);
        initSystemMenu();
        initData();
        initTable();
        initTugmachalar();
        initCenterPane();
        initBottomPane();
        initBorderPane();
    }

    public void display() {
        stage = new Stage();
        initStage(stage);
        stage.showAndWait();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initStage(primaryStage);
        stage.show();
    }
    private void initBottomPane() {
    }

    private void initData() {
        hisobObservableList = hisobModels.get_data(connection);
        standartModels.setTABLENAME("Tovar");
        tovarObservableList = standartModels.get_data(connection);
        valutaObservableList = valutaModels.get_data(connection);
        kassaObservableList = kassaModels.get_data(connection);
        standartModels.setTABLENAME("NarhTuri");
        savdoTuriObservableList = standartModels.get_data(connection);
        Circle circle1 = new Circle(6, Color.LIGHTGREEN);
        circle1.setId("0");
        Circle circle2 = new Circle(6, Color.RED);
        circle2.setId("1");
        lockObservableList.add(circle1);
        lockObservableList.add(circle2);
    }

    private void initTable() {
        kassaTableView.getColumns().addAll(getKassaNomi(), getPulHisobi(), getTovarHisobi(), getXaridorHisobi(), getValutaColumn(), getSavdoTuri(), getSerialNumber(), userIdColumn(), getStatus(), getLockColumn());
        kassaTableView.setItems(kassaObservableList);
        kassaTableView.setEditable(true);
    }

    private TableColumn<Kassa, String> getKassaNomi() {
        TableColumn<Kassa, String> kassaNomi = new TableColumn<>("Kassa nomi");
        kassaNomi.setCellValueFactory(new PropertyValueFactory<>("kassaNomi"));
        kassaNomi.setCellFactory(TextFieldTableCell.<Kassa> forTableColumn());
        kassaNomi.setOnEditCommit((TableColumn.CellEditEvent<Kassa, String> event) -> {
            TablePosition<Kassa, String> pos = event.getTablePosition();
            String newString = event.getNewValue();
            int row = pos.getRow();
            Kassa kassa = event.getTableView().getItems().get(row);
            kassa.setKassaNomi(newString);
            kassaModels.update_data(connection, kassa);
        });
        return kassaNomi;
    }

    private TableColumn<Kassa, Hisob> getPulHisobi() {
        TableColumn<Kassa, Hisob> pulHisobi = new TableColumn<>("Pul hisobi");
        pulHisobi.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Kassa, Hisob>, ObservableValue<Hisob>>() {

            @Override
            public ObservableValue<Hisob> call(TableColumn.CellDataFeatures<Kassa, Hisob> param) {
                Kassa kassa = param.getValue();
                Integer hisobId = kassa.getPulHisobi();
                Hisob h = getHisob(hisobId);
                return new SimpleObjectProperty<Hisob>(h);
            }
        });

        pulHisobi.setCellFactory(ComboBoxTableCell.forTableColumn(hisobObservableList));
        pulHisobi.setOnEditCommit((TableColumn.CellEditEvent<Kassa, Hisob> event) -> {
            TablePosition<Kassa, Hisob> pos = event.getTablePosition();
            Hisob newHisob = event.getNewValue();
            int row = pos.getRow();
            Kassa kassa = event.getTableView().getItems().get(row);
            kassa.setPulHisobi(newHisob.getId());
            kassaModels.update_data(connection, kassa);
        });
        pulHisobi.setStyle( "-fx-alignment: CENTER;");
        return pulHisobi;
    }

    private TableColumn<Kassa, Hisob> getXaridorHisobi() {
        TableColumn<Kassa, Hisob> xaridorHisobi = new TableColumn<>("Xaridor hisobi");
        xaridorHisobi.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Kassa, Hisob>, ObservableValue<Hisob>>() {

            @Override
            public ObservableValue<Hisob> call(TableColumn.CellDataFeatures<Kassa, Hisob> param) {
                Kassa kassa = param.getValue();
                Integer hisobId = kassa.getXaridorHisobi();
                Hisob h = getHisob(hisobId);
                return new SimpleObjectProperty<Hisob>(h);
            }
        });

        xaridorHisobi.setCellFactory(ComboBoxTableCell.forTableColumn(hisobObservableList));
        xaridorHisobi.setOnEditCommit((TableColumn.CellEditEvent<Kassa, Hisob> event) -> {
            TablePosition<Kassa, Hisob> pos = event.getTablePosition();
            Hisob newHisob = event.getNewValue();
            int row = pos.getRow();
            Kassa kassa = event.getTableView().getItems().get(row);
            kassa.setXaridorHisobi(newHisob.getId());
            kassaModels.update_data(connection, kassa);
        });
        xaridorHisobi.setStyle( "-fx-alignment: CENTER;");
        return xaridorHisobi;
    }

    private TableColumn<Kassa, Hisob> getTovarHisobi() {
        TableColumn<Kassa, Hisob> tovarHisobi = new TableColumn<>("Tovar hisobi");
        tovarHisobi.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Kassa, Hisob>, ObservableValue<Hisob>>() {

            @Override
            public ObservableValue<Hisob> call(TableColumn.CellDataFeatures<Kassa, Hisob> param) {
                Kassa kassa = param.getValue();
                Integer tovarId = kassa.getTovarHisobi();
                Hisob h = getHisob(tovarId);
                return new SimpleObjectProperty<Hisob>(h);
            }
        });

        tovarHisobi.setCellFactory(ComboBoxTableCell.forTableColumn(hisobObservableList));
        tovarHisobi.setOnEditCommit((TableColumn.CellEditEvent<Kassa, Hisob> event) -> {
            TablePosition<Kassa, Hisob> pos = event.getTablePosition();
            Hisob hisob = event.getNewValue();
            int row = pos.getRow();
            Kassa kassa = event.getTableView().getItems().get(row);
            kassa.setTovarHisobi(hisob.getId());
            kassaModels.update_data(connection, kassa);
        });
        tovarHisobi.setStyle( "-fx-alignment: CENTER;");
        return tovarHisobi;
    }

    private TableColumn<Kassa, Valuta> getValutaColumn() {
        TableColumn<Kassa, Valuta> valutaColumn = new TableColumn<>("Pul turi");
        valutaColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Kassa, Valuta>, ObservableValue<Valuta>>() {

            @Override
            public ObservableValue<Valuta> call(TableColumn.CellDataFeatures<Kassa, Valuta> param) {
                Kassa kassa = param.getValue();
                Integer valutaId = kassa.getValuta();
                Valuta v = getValuta(valutaId);
                return new SimpleObjectProperty<Valuta>(v);
            }
        });

        valutaColumn.setCellFactory(ComboBoxTableCell.forTableColumn(valutaObservableList));
        valutaColumn.setOnEditCommit((TableColumn.CellEditEvent<Kassa, Valuta> event) -> {
            TablePosition<Kassa, Valuta> pos = event.getTablePosition();
            Valuta valuta = event.getNewValue();
            int row = pos.getRow();
            Kassa kassa = event.getTableView().getItems().get(row);
            kassa.setValuta(valuta.getId());
            kassaModels.update_data(connection, kassa);
        });
        valutaColumn.setStyle( "-fx-alignment: CENTER;");
        return valutaColumn;
    }

    private TableColumn<Kassa, Standart> getSavdoTuri() {
        TableColumn<Kassa, Standart> narhTuriColumn = new TableColumn<>("Narh turi");
        narhTuriColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Kassa, Standart>, ObservableValue<Standart>>() {

            @Override
            public ObservableValue<Standart> call(TableColumn.CellDataFeatures<Kassa, Standart> param) {
                Kassa kassa = param.getValue();
                Integer rowId = kassa.getSavdoTuri();
                Standart s = getNarhTuri(rowId);
                return new SimpleObjectProperty<Standart>(s);
            }
        });

        narhTuriColumn.setCellFactory(ComboBoxTableCell.forTableColumn(savdoTuriObservableList));
        narhTuriColumn.setOnEditCommit((TableColumn.CellEditEvent<Kassa, Standart> event) -> {
            TablePosition<Kassa, Standart> pos = event.getTablePosition();
            Standart standart = event.getNewValue();
            int row = pos.getRow();
            Kassa kassa = event.getTableView().getItems().get(row);
            kassa.setSavdoTuri(standart.getId());
            kassaModels.update_data(connection, kassa);
        });
        narhTuriColumn.setStyle( "-fx-alignment: CENTER;");
        return narhTuriColumn;
    }

    private TableColumn<Kassa, Integer> getStatus() {
        TableColumn<Kassa, Integer> status = new TableColumn<>("Status");
        status.setCellValueFactory(new PropertyValueFactory<>("online"));
        status.setCellFactory(column -> {
            TableCell<Kassa, Integer> cell = new TableCell<Kassa, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    Circle circle = new Circle(6);
                    circle.setFill(Color.RED);
                    Circle circle1 = new Circle(6);
                    circle1.setFill(Color.LIGHTGREEN);
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        setText("");
                        if (item == 0) {
                            setGraphic(circle);
                        } else {
                            setGraphic(circle1);
                        }
                    }
                    setAlignment(Pos.CENTER);
                }
            };
            return cell;
        });
        return status;
    }

    private TableColumn<Kassa, String> getSerialNumber() {
        TableColumn<Kassa, String> serialNumber = new TableColumn<>("Serial");
        serialNumber.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Kassa, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Kassa, String> param) {
                Kassa kassa = param.getValue();
                String seriaNumber = Encryptor.decrypt(kassa.getSerialNumber());

                return new SimpleObjectProperty<String>(seriaNumber);
            }
        });
        serialNumber.setCellFactory(TextFieldTableCell.<Kassa> forTableColumn());
        serialNumber.setOnEditCommit((TableColumn.CellEditEvent<Kassa, String> event) -> {
            TablePosition<Kassa, String> pos = event.getTablePosition();
            String newString = Encryptor.encrypt(event.getNewValue());
            int row = pos.getRow();
            Kassa kassa = event.getTableView().getItems().get(row);
            kassa.setSerialNumber(newString);
            kassaModels.update_data(connection, kassa); });
        serialNumber.setStyle( "-fx-alignment: CENTER;");
        return serialNumber;
    }

    private TableColumn<Kassa, Integer> userIdColumn () {
        TableColumn<Kassa, Integer> userId = new TableColumn<>("Dastur yurituvchi");
        userId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        userId.setCellFactory(column -> {
            TableCell<Kassa, Integer> cell = new TableCell<Kassa, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        setText(GetDbData.getUser(item).getIsm());
                    }
                }
            };
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
      return userId;
    }

    private TableColumn<Kassa, Circle> getLock() {
        TableColumn<Kassa, Circle> lockColumn = new TableColumn<>("Ochiq/Yopiq");
        lockColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Kassa, Circle>, ObservableValue<Circle>>() {
            @Override
            public ObservableValue<Circle> call(TableColumn.CellDataFeatures<Kassa, Circle> param) {
                Kassa kassa = param.getValue();
                Integer lockId = kassa.getIsLocked();
                Circle circle = new Circle();
                if (lockId == 0) {
                    circle.setId("0");
                    circle.setFill(Color.LIGHTGREEN);
                } else {
                    circle.setId("1");
                    circle.setFill(Color.RED);
                }
                return new SimpleObjectProperty(circle);
            }
        });

        lockColumn.setCellFactory(ComboBoxTableCell.forTableColumn(lockObservableList));
        lockColumn.setOnEditCommit((TableColumn.CellEditEvent<Kassa, Circle> event) -> {
            TablePosition<Kassa, Circle> pos = event.getTablePosition();
            Circle circle = event.getNewValue();
            int row = pos.getRow();
            Kassa kassa = event.getTableView().getItems().get(row);
            kassa.setIsLocked(Integer.valueOf(circle.getId()));
            kassaModels.update_data(connection, kassa);
        });
        lockColumn.setStyle( "-fx-alignment: CENTER;");
        return lockColumn;
    }

    private TableColumn<Kassa, Integer> getLock2() {
        TableColumn<Kassa, Integer> lockColumn = new TableColumn<>("Ochiq/Yopiq");
        lockColumn.setCellValueFactory(new PropertyValueFactory<>("lock"));
        lockColumn.setCellFactory(column -> {
            TableCell<Kassa, Integer> cell = new TableCell<Kassa, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    Circle circle = new Circle(6);
                    circle.setFill(Color.RED);
                    Circle circle1 = new Circle(6);
                    circle1.setFill(Color.LIGHTGREEN);
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        setText("");
                        if (item == 0) {
                            setGraphic(circle);
                        } else {
                            setGraphic(circle1);
                        }
                    }
                    setAlignment(Pos.CENTER);
                }
            };
            return cell;
        });
        return lockColumn;
    }

    private TableColumn<Kassa, Button> getLockColumn() {
        TableColumn<Kassa, Button> deleteColumn = new TableColumn<>("O`chir");
        deleteColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Kassa, Button>, ObservableValue<Button>>() {

            @Override
            public ObservableValue<Button> call(TableColumn.CellDataFeatures<Kassa, Button> param) {
                Kassa kassa = param.getValue();
                Button b = new Button("");
                b.setMaxWidth(2000);
                b.setPrefWidth(150);
                HBox.setHgrow(b, Priority.ALWAYS);
                Circle circle = null;
                if (kassa.getIsLocked() == 1) {
                    circle = new Circle(6);
                    circle.setFill(Color.RED);
                }
                else {
                    circle = new Circle(6);
                    circle.setFill(Color.LIGHTGREEN);
                }
                b.setGraphic(circle);

                Circle finalCircle = circle;
                b.setOnAction(event -> {
                    if (kassa.getIsLocked() ==  1) {
                        finalCircle.setFill(Color.LIGHTGREEN);
                        kassa.setIsLocked(0);
                    }
                    else {
                        finalCircle.setFill(Color.RED);
                        kassa.setIsLocked(1);
                    }
                    b.setGraphic(finalCircle);
                    kassaModels.update_data(connection,  kassa);
                });
                return new SimpleObjectProperty<Button>(b);
            }
        });

        deleteColumn.setMinWidth(20);
        deleteColumn.setMaxWidth(40);
        deleteColumn.setStyle( "-fx-alignment: CENTER;");
        return deleteColumn;
    }

    private void initTugmachalar() {
        tugmachalar.getChildren().remove(tugmachalar.getEdit());
        tugmachalar.getAdd().setOnAction(event -> {
            Kassa kassa = new Kassa(null, "Kassa 1", 1, 5, 2, 3, 1, "1234567890", 1, 1, user.getId(), new Date());
            kassaObservableList.add(kassa);
            kassaModels.insert_data(connection, kassa);
            kassaTableView.refresh();
        });
        tugmachalar.getDelete().setOnAction(event -> {
            Kassa kassa = kassaTableView.getSelectionModel().getSelectedItem();
            if (kassa != null) {
                kassaModels.delete_data(connection, kassa);
                kassaObservableList.remove(kassa);
            }
        });
    }

    private void initCenterPane() {
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        VBox.setVgrow(centerPane, Priority.ALWAYS);
        centerPane.setPadding(new Insets(3));
        centerPane.getChildren().addAll(tugmachalar, kassaTableView);
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
        borderpane.setCenter(centerPane);
        borderpane.setBottom(bottom);
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Kassalar");
        Scene scene = new Scene(borderpane, 865, 400);
        stage.setScene(scene);
    }

    private Hisob getHisob(int hisobId) {
        Hisob hisob = null;
        for (Hisob h: hisobObservableList) {
            if (h.getId().equals(hisobId)) {
                hisob = h;
                break;
            }
        }
        return hisob;
    }

    private Valuta getValuta(int id) {
        Valuta valuta = null;
        for (Valuta v: valutaObservableList) {
            if (v.getId().equals(id)) {
                valuta = v;
                break;
            }
        }
        return valuta;
    }
    private Standart getNarhTuri(int id) {
        Standart standart = null;
        for (Standart s: savdoTuriObservableList) {
            if (s.getId().equals(id)) {
                standart = s;
                break;
            }
        }
        return standart;
    }

    public class LockCircle extends Circle {
        private Integer circleId;

        public LockCircle(double radius, Paint fill, Integer id) {
            super(radius, fill);
            this.circleId = id;
        }

        public Integer getCircleId() {
            return circleId;
        }

        public void setCircleId(Integer circleId) {
            this.circleId = circleId;
        }

    }
}
