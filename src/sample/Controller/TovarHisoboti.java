package sample.Controller;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.Config.MySqlDBGeneral;
import sample.Data.*;
import sample.Enums.ServerType;
import sample.Model.BarCodeModels;
import sample.Model.HisobKitobModels;
import sample.Tools.DasturlarRoyxati;
import sample.Tools.GetDbData;
import sample.Tools.TableViewAndoza;
import sample.Tools.Hisobot2;

import java.sql.Connection;
import java.util.Date;

public class TovarHisoboti extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    VBox centerPane = new VBox();
    HBox qidirHBox = new HBox();
    TableView<HisobKitob> tovarTableView = new TableView<>();
    ObservableList<HisobKitob> tovarList = FXCollections.observableArrayList();
    Hisob hisob;

    TextField tovarTextField = new TextField();
    TextField barCodeTextField = new TextField();

    BarCodeModels barCodeModels = new BarCodeModels();

    Connection connection;
    User user = new User(1, "admin", "", "admin");
    StringBuffer stringBuffer = new StringBuffer();
    int padding = 3;


    public static void main(String[] args) {
        launch(args);
    }

    public TovarHisoboti() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        GetDbData.initData(connection);
        hisob = GetDbData.getHisob(13);
        ibtido();
    }

    public TovarHisoboti(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        String classSimpleName = getClass().getSimpleName();
        DasturlarRoyxati.dastur(connection, user, classSimpleName);
        ibtido();
    }

    public TovarHisoboti(Connection connection, User user, Hisob hisob) {
        this.connection = connection;
        this.user = user;
        this.hisob = hisob;
        ibtido();
    }

    private void ibtido() {
        initData();
        initTovarTable();
        initTextFields();
        initQidirHBox();
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

    private void initData() {
        refreshHisobKitobTable(hisob);
    }

    private void initCenterPane() {
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        VBox.setVgrow(centerPane, Priority.ALWAYS);
        centerPane.getChildren().addAll(qidirHBox, tovarTableView);
    }

    private void initBorderPane() {
        borderpane.setCenter(centerPane);
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Pul va Tovar Hisoboti");
        scene = new Scene(borderpane, 700, 500);
        stage.setScene(scene);
        barCodeOn();
        stage.setOnCloseRequest(event -> {
            barCodeOff();
        });
    }

    private void initTextFields() {
        HBox.setHgrow(tovarTextField, Priority.ALWAYS);
        tovarTextField.setPromptText("Tovar nomi");
        tovarTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            Taftish(oldValue, newValue);
        });
        HBox.setHgrow(barCodeTextField, Priority.ALWAYS);
        barCodeTextField.setPromptText("Shtrixkod");
    }

    private void initQidirHBox() {
        HBox.setHgrow(qidirHBox, Priority.ALWAYS);
        qidirHBox.setPadding(new Insets(padding));
        qidirHBox.getChildren().addAll(tovarTextField, barCodeTextField);
    }

    private void initTovarTable() {
        HBox.setHgrow(tovarTableView, Priority.ALWAYS);
        VBox.setVgrow(tovarTableView, Priority.ALWAYS);
        tovarTableView.setPadding(new Insets(padding));
        TableViewAndoza tableViewAndoza = new TableViewAndoza();
        TableColumn<HisobKitob, String> tovarColumn = tableViewAndoza.getIzoh2Column();
        TableColumn<HisobKitob, String> birlikColumn = tableViewAndoza.getBirlikColumn();
        TableColumn<HisobKitob, Double> adadColumn = tableViewAndoza.getAdadColumn();
        TableColumn<HisobKitob, Double> narhColumn = tableViewAndoza.getNarhColumn();
        tovarTableView.getColumns().addAll(tovarColumn, /*barCodeColumn, birlikColumn, */adadColumn, narhColumn);
        tovarTableView.setItems(tovarList);
    }

    private void refreshHisobKitobTable(Hisob hisob) {
        Hisobot2 hisobot = new Hisobot2(connection,user);
        tovarList.removeAll(tovarList);
        tovarList.addAll(hisobot.getMahsulot(hisob.getId()));
        tovarTableView.setItems(tovarList);
        tovarTableView.refresh();
    }

    private void refreshHisobKitobTable2(Hisob hisob) {
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        ObservableList<BarCode> barCodeList = hisobKitobModels.getDistinctBarCode(connection, hisob.getId(), new Date());
        for (BarCode bc: barCodeList) {
            if (bc != null) {
                HisobKitob hk = hisobKitobModels.getBarCodeBalans(connection, hisob.getId(), bc, new Date());
                if (hk.getNarh() != 0.0) {
                    Standart tovar = GetDbData.getTovar(bc.getTovar());
                    hk.setIzoh(tovar.getText());
                    tovarList.add(hk);
                }
            }
        }
        tovarTableView.setItems(tovarList);
        tovarTableView.refresh();
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
                    if (!string.isEmpty()) {
                        stringBuffer.delete(0, stringBuffer.length());
                        BarCode barCode = barCodeModels.getBarCode(connection, string);
                        if (barCode != null) {
                            HisobKitob tovar = getBarCode(barCode);
                            barCodeTextField.setText("");
                            tovarTableView.getSelectionModel().select(tovar);
                            tovarTableView.scrollTo(tovar);
                            tovarTableView.requestFocus();
                            tovarTableView.refresh();
                        } else {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Diqqat !!!");
                            alert.setHeaderText(string + "\n shtrix kodga muvofiq tovar topiilmadi");
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
                stringBuffer.delete(0, stringBuffer.length());
                String string = barCodeTextField.getText().trim();
                if (event.getCode()== KeyCode.ENTER) {
                    BarCode barCode = barCodeModels.getBarCode(connection, string);
                    if (barCode !=  null) {
                        HisobKitob tovar = getBarCode(barCode);
                        barCodeTextField.setText("");
                        tovarTableView.getSelectionModel().select(tovar);
                        tovarTableView.scrollTo(tovar);
                        tovarTableView.refresh();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Diqqat !!!");
                        alert.setHeaderText(string +  "\n shtrix kodga muvofiq tovar topiilmadi" );
                        alert.setContentText("");
                        alert.showAndWait();
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

    private HisobKitob getTovar(Integer tovarId) {
        HisobKitob tovar = null;
        for (HisobKitob t: tovarList) {
            if (t.getTovar().equals(tovarId)) {
                tovar = t;
                break;
            }
        }
        return tovar;
    }

    private HisobKitob getBarCode(BarCode barCode) {
        HisobKitob tovar = null;
        for (HisobKitob t: tovarList) {
            if (t.getBarCode().equals(barCode.getBarCode())) {
                tovar = t;
                break;
            }
        }
        return tovar;
    }

    private void Taftish(String oldValue, String newValue) {
        ObservableList<HisobKitob> subentries = FXCollections.observableArrayList();
        newValue = newValue.toLowerCase();

        if ( oldValue != null && (newValue.length() < oldValue.length()) ) {
            tovarTableView.setItems( tovarList );
        }

        for ( HisobKitob hk: tovarList ) {
            if (hk.getIzoh().toLowerCase().contains(newValue)) {
                subentries.add(hk);
            }
        }
        tovarTableView.setItems(subentries);
        tovarTableView.refresh();
    }

}
