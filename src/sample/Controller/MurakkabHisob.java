package sample.Controller;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.Config.MySqlDBGeneral;
import sample.Data.Hisob;
import sample.Data.Standart2;
import sample.Data.Standart3;
import sample.Data.User;
import sample.Enums.ServerType;
import sample.Model.HisobModels;
import sample.Model.Standart2Models;
import sample.Model.Standart3Models;
import sample.Tools.DasturlarRoyxati;
import sample.Tools.GetDbData;
import sample.Tools.HisobBox;
import sample.Tools.SetHVGrow;

import java.sql.Connection;

public class MurakkabHisob extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    HBox topPane = new HBox();
    VBox leftPane = new VBox();
    VBox centerPane = new VBox();
    VBox rightPane = new VBox();
    HBox bottomPane = new HBox();
    GridPane gridPane;

    HisobBox asosiyHisobBox;
    HisobBox pulHisobBox;
    HisobBox foydaHisobBox;
    HisobBox zararHisobBox;
    HisobBox tranzitHisobBox;
    HisobBox ndsHisobBox;
    HisobBox bankHisobBox;
    HisobBox bankXarajatlariHisobBox;
    HisobBox bojxonaHisobBox;
    HisobBox tasdiqHisobBox;
    HisobBox qoshimchaDaromadHisobBox;
    HisobBox chegirmaHisobBox;
    HisobBox xaridorHisobBox;

    Connection connection;
    User user;
    int padding = 3;


    public static void main(String[] args) {
        launch(args);
    }

    public MurakkabHisob() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
        ibtido();
    }

    public MurakkabHisob(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        String classSimpleName = getClass().getSimpleName();
        DasturlarRoyxati.dastur(connection, user, classSimpleName);
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
        gridPane = initGridPane();
        centerPane.getChildren().addAll(gridPane);
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
        stage.setTitle("Murakkab hisob");
        scene = new Scene(borderpane, 400, 480);
        stage.setScene(scene);
    }

    private GridPane initGridPane() {
        GridPane gridPane = new GridPane();
        SetHVGrow.VerticalHorizontal(gridPane);
        initHisobBox();
        Separator separator = new Separator();

        TextField asosiyHisobTextField = new TextField();
        asosiyHisobTextField.setId("Asosiy");
        asosiyHisobTextField.setPromptText("Asosiy hisob");
        TextField prefixTextField = new TextField();
        prefixTextField.setId("Prefix");
        prefixTextField.setPromptText("Prefix");
        TextField pulHisobiTextField = new TextField("Pul hisobi");
        pulHisobiTextField.setId("PulHisobi");
        TextField keldiKetdiHisobiTextField = new TextField("Keldi-ketdi");
        keldiKetdiHisobiTextField.setId("keldiKetdi");
        TextField foydaHisobiTextField = new TextField("Foyda");
        foydaHisobiTextField.setId("Foyda");
        TextField zararHisobiTextField = new TextField("Zarar");
        zararHisobiTextField.setId("Zarar");
        TextField xaridorHisobiTextField = new TextField("Xaridor");
        xaridorHisobiTextField.setId("Xaridor");
        TextField chegirmaHisobiTextField = new TextField("Chegirma");
        xaridorHisobiTextField.setId("Chegirma");
        TextField bankHisobiTextField = new TextField("Bank");
        bankHisobiTextField.setId("Bank");
        TextField bankXarajatHisobiTextField = new TextField("Bank xarajatlari");
        bankXarajatHisobiTextField.setId("BankXarajatlari");
        TextField ndsHisobiTextField = new TextField("Nds");
        ndsHisobiTextField.setId("Nds");
        TextField bojxonaHisobiTextField = new TextField("Bojxona");
        bojxonaHisobiTextField.setId("Bojxona");
        TextField taqsimHisobiTextField = new TextField("Tasdiq");
        taqsimHisobiTextField.setId("Tasdiq");
        TextField qoshimchaDaromadHisobiTextField = new TextField("Qo`shimcha daromad");
        qoshimchaDaromadHisobiTextField.setId("Qo`shimcha daromad");

        Button backButton = initBackButton();
        Button qaydEtButton = initQaydEtButton(gridPane);

        Integer rowIndex = 0;
        Label label = new Label("Prefix");
        gridPane.add(label, 0, rowIndex, 1, 1);
        gridPane.add(prefixTextField, 1, rowIndex, 1, 1);
        GridPane.setHgrow(prefixTextField, Priority.ALWAYS);
        GridPane.setHgrow(prefixTextField, Priority.ALWAYS);

        rowIndex++;
        gridPane.add(asosiyHisobTextField, 0, rowIndex, 1, 1);
        gridPane.add(asosiyHisobBox, 1,rowIndex, 1, 1);
        HBox.setHgrow(asosiyHisobTextField, Priority.ALWAYS);
        GridPane.setHgrow(asosiyHisobTextField, Priority.ALWAYS);
        GridPane.setHgrow(asosiyHisobBox, Priority.ALWAYS);

        rowIndex++;
        gridPane.add(pulHisobiTextField, 0, rowIndex, 1, 1);
        gridPane.add(pulHisobBox, 1,rowIndex, 1, 1);

        rowIndex++;
        gridPane.add(keldiKetdiHisobiTextField, 0, rowIndex, 1, 1);
        gridPane.add(tranzitHisobBox, 1,rowIndex, 1, 1);

        rowIndex++;
        gridPane.add(foydaHisobiTextField, 0, rowIndex, 1, 1);
        gridPane.add(foydaHisobBox, 1,rowIndex, 1, 1);

        rowIndex++;
        gridPane.add(zararHisobiTextField, 0, rowIndex, 1, 1);
        gridPane.add(zararHisobBox, 1,rowIndex, 1, 1);

        rowIndex++;
        gridPane.add(xaridorHisobiTextField, 0, rowIndex, 1, 1);
        gridPane.add(xaridorHisobBox, 1,rowIndex, 1, 1);

        rowIndex++;
        gridPane.add(chegirmaHisobiTextField, 0, rowIndex, 1, 1);
        gridPane.add(chegirmaHisobBox, 1,rowIndex, 1, 1);

        rowIndex++;
        gridPane.add(bankHisobiTextField, 0, rowIndex, 1, 1);
        gridPane.add(bankHisobBox, 1,rowIndex, 1, 1);

        rowIndex++;
        gridPane.add(bankXarajatHisobiTextField, 0, rowIndex, 1, 1);
        gridPane.add(bankXarajatlariHisobBox, 1,rowIndex, 1, 1);

        rowIndex++;
        gridPane.add(ndsHisobiTextField, 0, rowIndex, 1, 1);
        gridPane.add(ndsHisobBox, 1,rowIndex, 1, 1);

        rowIndex++;
        gridPane.add(bojxonaHisobiTextField, 0, rowIndex, 1, 1);
        gridPane.add(bojxonaHisobBox, 1,rowIndex, 1, 1);

        rowIndex++;
        gridPane.add(taqsimHisobiTextField, 0, rowIndex, 1, 1);
        gridPane.add(tasdiqHisobBox, 1,rowIndex, 1, 1);

        rowIndex++;
        gridPane.add(qoshimchaDaromadHisobiTextField, 0, rowIndex, 1, 1);
        gridPane.add(qoshimchaDaromadHisobBox, 1,rowIndex, 1, 1);

        rowIndex++;
        gridPane.add(backButton, 0, rowIndex, 1, 1);
        gridPane.add(qaydEtButton, 1, rowIndex, 1, 1);
        GridPane.setHgrow(backButton, Priority.ALWAYS);
        GridPane.setHgrow(qaydEtButton, Priority.ALWAYS);
        return gridPane;
    }

    private Button initBackButton() {
        Button button = new Button("<<");
        button.setMaxWidth(2000);
        button.setPrefWidth(168);
        button.setOnAction(event -> {
            stage.close();
        });
        return button;
    }
    private Button initQaydEtButton(GridPane gridPane) {
        Button button = new Button("Qayd et");
        button.setMaxWidth(2000);
        button.setPrefWidth(168);
        button.setOnAction(event -> {
            murakkabHisobOch(gridPane);
            stage.close();
        });
        return button;
    }

    private void initHisobBox() {
        asosiyHisobBox = new HisobBox(connection, user);
        pulHisobBox = new HisobBox(connection, user);
        foydaHisobBox = new HisobBox(connection, user);
        zararHisobBox = new HisobBox(connection, user);
        tranzitHisobBox = new HisobBox(connection, user);
        ndsHisobBox = new HisobBox(connection, user);
        bankHisobBox = new HisobBox(connection, user);
        bankXarajatlariHisobBox = new HisobBox(connection, user);
        bojxonaHisobBox = new HisobBox(connection, user);
        tasdiqHisobBox = new HisobBox(connection, user);
        chegirmaHisobBox = new HisobBox(connection, user);
        xaridorHisobBox = new HisobBox(connection, user);
        qoshimchaDaromadHisobBox = new HisobBox(connection, user);
    }

    private void murakkabHisobOch(GridPane gridPane) {
        HisobModels hisobModels = new HisobModels();
        ObservableList<Hisob> hisobObservableList = FXCollections.observableArrayList();

        TextField prefixTextField = (TextField) gridPane.getChildren().get(1);
        String prefix = "";
        if (!prefixTextField.getText().isEmpty()) {
            prefix = prefixTextField.getText().trim() + " ";
        }

        TextField asosiyHisobTextField = (TextField) gridPane.getChildren().get(2);
        HisobBox asosiyHisobBox = (HisobBox) gridPane.getChildren().get(3);
        Hisob asosiyHisob = null;
        if (!asosiyHisobBox.getTextField().getText().isEmpty()) {
            asosiyHisob = asosiyHisobBox.getHisob();
        } else {
            asosiyHisob = asosiyHisobSaqla(asosiyHisobTextField, prefix);
            if (asosiyHisob == null) {
                return;
            }
        }

        TextField pulHisobiTextField = (TextField) gridPane.getChildren().get(4);
        HisobBox pulHisobBox = (HisobBox) gridPane.getChildren().get(5);
        save(asosiyHisob, pulHisobBox, pulHisobiTextField, "PulHisobi", "PulHisobi1", prefix);

        TextField keldiKetdiHisobiTextField = (TextField) gridPane.getChildren().get(6);
        HisobBox keldiKetdiHisobBox = (HisobBox) gridPane.getChildren().get(7);
        save(asosiyHisob, keldiKetdiHisobBox, keldiKetdiHisobiTextField, "TranzitHisob", "TranzitHisobGuruhi", prefix);

        TextField foydaHisobiTextField = (TextField) gridPane.getChildren().get(8);
        HisobBox foydaHisobBox = (HisobBox) gridPane.getChildren().get(9);
        save(asosiyHisob, foydaHisobBox, foydaHisobiTextField, "FoydaHisobi", "FoydaHisobiGuruhi", prefix);

        TextField zararHisobiTextField = (TextField) gridPane.getChildren().get(10);
        HisobBox zararHisobBox = (HisobBox) gridPane.getChildren().get(11);
        save(asosiyHisob, zararHisobBox, zararHisobiTextField, "Zarar", "ZararGuruhi", prefix);

        TextField xaridorHisobiTextField = (TextField) gridPane.getChildren().get(12);
        HisobBox xaridorHisobBox = (HisobBox) gridPane.getChildren().get(13);
        save(asosiyHisob, xaridorHisobBox, xaridorHisobiTextField, "Xaridor", "Xaridor1", prefix);

        TextField chegirmaHisobiTextField = (TextField) gridPane.getChildren().get(14);
        HisobBox chegirmaHisobBox = (HisobBox) gridPane.getChildren().get(15);
        save(asosiyHisob, chegirmaHisobBox, chegirmaHisobiTextField, "Chegirma", "ChegirmaGuruhi", prefix);

        TextField bankHisobiTextField = (TextField) gridPane.getChildren().get(16);
        HisobBox bankHisobBox = (HisobBox) gridPane.getChildren().get(17);
        save(asosiyHisob, bankHisobBox, bankHisobiTextField, "Bank", "Bank1", prefix);

        TextField bankXarajatHisobiTextField = (TextField) gridPane.getChildren().get(18);
        HisobBox bankXarajatHisobBox = (HisobBox) gridPane.getChildren().get(19);
        save(asosiyHisob, bankXarajatHisobBox, bankXarajatHisobiTextField, "BankXizmati", "BankXizmati1", prefix);

        TextField ndsHisobiTextField = (TextField) gridPane.getChildren().get(20);
        HisobBox ndsHisobBox = (HisobBox) gridPane.getChildren().get(21);
        save(asosiyHisob, ndsHisobBox, ndsHisobiTextField, "NDS1", "NDS2", prefix);

        TextField bojxonaHisobiTextField = (TextField) gridPane.getChildren().get(22);
        HisobBox bojxonaHisobBox = (HisobBox) gridPane.getChildren().get(23);
        save(asosiyHisob, bojxonaHisobBox, bojxonaHisobiTextField, "Bojxona", "Bojxona2", prefix);

        TextField tasdiqHisobiTextField = (TextField) gridPane.getChildren().get(24);
        HisobBox tasdiqHisobBox = (HisobBox) gridPane.getChildren().get(25);
        save(asosiyHisob, tasdiqHisobBox, tasdiqHisobiTextField, "Tasdiq", "Tasdiq2", prefix);

        TextField qoshimchaDaromadTextField = (TextField) gridPane.getChildren().get(26);
        HisobBox qoshimchaDaromadHisobBox = (HisobBox) gridPane.getChildren().get(27);
        save(asosiyHisob, qoshimchaDaromadHisobBox, qoshimchaDaromadTextField, "QoshimchaDaromad", "QoshimchaDaromad2", prefix);
    }

    private void s2S3Saqla(Hisob asosiyHisob, Hisob yordamchiHisob, String fileName1, String fileName2 ) {
        Standart2Models standart2Models = new Standart2Models(fileName1);
        ObservableList<Standart2> s2List = standart2Models.getAnyData(connection, "id2=" + yordamchiHisob.getId(), "");
        if (s2List.size()==0) {
            Standart2 s2 = new Standart2(null, yordamchiHisob.getId(), yordamchiHisob.getText(), user.getId(), null);
            standart2Models.insert_data(connection, s2);
        }
        Standart3Models standart3Models = new Standart3Models(fileName2);
        ObservableList<Standart3> observableList = standart3Models.getAnyData(connection, "id2 = " + asosiyHisob.getId(), "");

        if (observableList.size()==0) {
            Standart3 standart3 = new Standart3(null, yordamchiHisob.getId(), asosiyHisob.getId(), asosiyHisob.getText(), user.getId(), null);
            standart3Models.insert_data(connection, standart3);
        }
    }

    private Hisob asosiyHisobSaqla(TextField textField, String prefix) {
        Hisob h = null;
        if (prefix.isEmpty() && textField.getText().isEmpty())
            return h;
        HisobModels hisobModels = new HisobModels();
        h = new Hisob(1, prefix + textField.getText(), 0d, "", "", "", user.getId(), null);
        hisobModels.insert_data(connection, h);
        Standart3Models standart3Models = new Standart3Models("Dokonlar");
        Standart3 standart3 = new Standart3(null, 0, h.getId(), h.getText(), user.getId(), null);
        standart3Models.insert_data(connection, standart3);
        return h;
    }

    private Hisob hisobSaqla(TextField textField, String prefix) {
        Hisob h = null;
        if (!textField.getText().isEmpty()) {
            HisobModels hisobModels = new HisobModels();
            h = new Hisob(1, prefix + textField.getText(), 0d, "", "", "", user.getId(), null);
            hisobModels.insert_data(connection, h);
        }
        return h;
    }

    private Hisob save(Hisob asosiyHisob, HisobBox hisobBox, TextField textField, String fileName1, String  filaName2, String prefix) {
        Hisob hisob = null;
        if (!hisobBox.getTextField().getText().isEmpty()) {
            hisob = hisobBox.getHisob();
        } else {
            if (!textField.getText().isEmpty()) {
                hisob = hisobSaqla(textField, prefix);
            }
        }

        if (hisob!=null) {
            s2S3Saqla(asosiyHisob, hisob, fileName1, filaName2);
        }
        return hisob;
    }
}
