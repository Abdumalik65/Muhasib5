package sample.Controller;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.Config.MySqlDBGeneral;
import sample.Data.HisobKitob;
import sample.Data.User;
import sample.Data.Valuta;
import sample.Enums.ServerType;
import sample.Model.HisobKitobModels;
import sample.Tools.GetDbData;
import sample.Tools.PulBox;

import java.sql.Connection;

public class ExchangeController extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    HBox topPane = new HBox();
    VBox leftPane = new VBox();
    VBox centerPane = new VBox();
    VBox rightPane = new VBox();
    HBox bottomPane = new HBox();
    Connection connection;
    User user;
    int padding = 3;

    ObservableList<HisobKitob> hisobKitobObservableList = FXCollections.observableArrayList();
    ObservableList<GridPane> gridPaneObservableList = FXCollections.observableArrayList();
    TableView<HisobKitob> tableView;


    public static void main(String[] args) {
        launch(args);
    }

    public ExchangeController() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
        ibtido();
    }

    public ExchangeController(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        ibtido();
    }

    private void ibtido() {
        initData();
        initTopPane();
        initLeftPane();
        initCenterPane();
        initRightPane();
        initBottomPane();
        initBorderPane();
    }

    private void initData() {
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        if (user.getStatus().equals(99)) {
            hisobKitobObservableList = hisobKitobModels.getAnyData(connection, "amal = 16", "qaydId desc");
        } else {
            hisobKitobObservableList = hisobKitobModels.getAnyData(connection, "amal = 16 and userId = " + user.getId(), "qaydId desc");
        }
        Integer qaydId = 0;
        ObservableList<HisobKitob> observableList = FXCollections.observableArrayList();
        for (int i=0; i<hisobKitobObservableList.size(); i++) {
            HisobKitob hisobKitob = hisobKitobObservableList.get(0);
            if (!qaydId.equals(hisobKitob.getQaydId())) {
                yangiGridPane(observableList);
                qaydId = hisobKitob.getQaydId();
                observableList = FXCollections.observableArrayList();
            }
            if (qaydId.equals(hisobKitob.getQaydId())) {
                observableList.add(hisobKitob);
            }
        }
    }

    private GridPane yangiGridPane(ObservableList<HisobKitob> observableList) {
        Integer qaydId;
        Integer rowIndex = 0;
        HisobKitob hisobKitob1;
        HisobKitob hisobKitob2;
        HisobKitob hisobKitob3;
        GridPane gridPane = new GridPane();
        HBox.setHgrow(gridPane, Priority.ALWAYS);
        if (observableList.size()>2) {
            hisobKitob1 = observableList.get(0);
            hisobKitob2 = observableList.get(1);
            hisobKitob3 = observableList.get(2);
        } else {
            hisobKitob2 = observableList.get(1);
            hisobKitob3 = observableList.get(2);
            hisobKitob1 = new HisobKitob(
                    0, hisobKitob2.getQaydId(), hisobKitob2.getHujjatId(), hisobKitob2.getAmal(), hisobKitob2.getHisob1(), hisobKitob2.getHisob2(),
                    hisobKitob2.getValuta(), 0, 0d, "", 0d, 0d, 0, hisobKitob2.getIzoh(), hisobKitob2.getUserId(), hisobKitob2.getDateTime()
            );
        }
        TextField qaydIdTextField = new TextField(hisobKitob1.getQaydId().toString());
        qaydIdTextField.setEditable(false);
        gridPane.add(qaydIdTextField, 0, rowIndex, 1, 1);

        TextField hujjatIdTextField = new TextField(hisobKitob1.getHujjatId().toString());
        hujjatIdTextField.setEditable(false);
        gridPane.add(hujjatIdTextField, 1, rowIndex, 1, 1);

        TextField hisob1TextField = new TextField(hisobKitob1.getHisob1().toString());
        hisob1TextField.setEditable(false);
        gridPane.add(hisob1TextField, 2, rowIndex, 1, 1);

        TextField hisob2TextField = new TextField(hisobKitob1.getHisob2().toString());
        hisob2TextField.setEditable(false);
        gridPane.add(hisob2TextField, 3, rowIndex, 1, 1);

        TextArea izohTextArea = new TextArea(hisobKitob1.getIzoh().toString());
        izohTextArea.setEditable(false);
        gridPane.add(izohTextArea, 4, rowIndex, 1, 1);

        rowIndex++;
        PulBox pulBox1 = new PulBox(connection, user);
        pulBox1.getTextField().setEditable(false);
        pulBox1.getPlusButton().setDisable(true);
        Valuta valuta1 = GetDbData.getValuta(hisobKitob2.getValuta());
        pulBox1.setValuta(valuta1);
        gridPane.add(pulBox1, 0, rowIndex, 1, 1);

        TextField narh1TextField = new TextField(hisobKitob2.getNarh().toString());
        narh1TextField.setEditable(false);
        gridPane.add(narh1TextField, 3, rowIndex, 1, 1);

        TextField kurs1TextField = new TextField(hisobKitob2.getKurs().toString());
        kurs1TextField.setEditable(false);
        gridPane.add(kurs1TextField, 3, rowIndex, 1, 1);
        return gridPane;
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
        HBox.setHgrow(topPane, Priority.ALWAYS);
        VBox.setVgrow(topPane, Priority.ALWAYS);
        topPane.setPadding(new Insets(padding));
        topPane.getChildren().addAll();
    }

    private void initLeftPane() {
        HBox.setHgrow(leftPane, Priority.ALWAYS);
        VBox.setVgrow(leftPane, Priority.ALWAYS);
        leftPane.setPadding(new Insets(padding));
    }

    private void initCenterPane() {
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        VBox.setVgrow(centerPane, Priority.ALWAYS);
        centerPane.setPadding(new Insets(padding));

        centerPane.getChildren().addAll();
    }

    private void initRightPane() {
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        VBox.setVgrow(rightPane, Priority.ALWAYS);
        rightPane.setPadding(new Insets(padding));
        rightPane.getChildren().addAll();
    }

    private void initBottomPane() {
        HBox.setHgrow(bottomPane, Priority.ALWAYS);
        VBox.setVgrow(bottomPane, Priority.ALWAYS);
        bottomPane.setPadding(new Insets(padding));
        bottomPane.getChildren().addAll();
    }

    private void initBorderPane() {
        borderpane.setTop(topPane);
        borderpane.setLeft(leftPane);
        borderpane.setCenter(centerPane);
        borderpane.setRight(rightPane);
        borderpane.setBottom(bottomPane);
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Pul ayriboshalsh");
        scene = new Scene(borderpane, 600, 400);
        stage.setScene(scene);
    }

    private TableView<HisobKitob> yangiTableView() {
        TableView<HisobKitob> tableView = new TableView<>();
        return tableView;
    }
}
