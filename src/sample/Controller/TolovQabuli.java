package sample.Controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.Config.MySqlDBGeneral;
import sample.Data.HisobKitob;
import sample.Data.User;
import sample.Enums.ServerType;
import sample.Tools.GetDbData;
import sample.Tools.MoneyShow;
import sample.Tools.SetHVGrow;

import java.sql.Connection;
import java.text.DecimalFormat;

public class TolovQabuli extends Application {
    Stage stage;
    Scene scene;
    GridPane gridPane;
    Pane topPane = new Pane();
    VBox leftPane = new VBox();
    VBox centerPane = new VBox();
    VBox rightPane = new VBox();
    Pane bottomPane = new Pane();
    Connection connection;
    User user;
    int padding = 3;

    GridPane kelishmaGridPane;
    GridPane tolovGridPane;
    GridPane qaytimGridPane;
    GridPane chegirmaGridPane;
    GridPane yakuniyGridPane;

    Double jamiMablagDouble;
    Double kelishmaDouble;
    ObservableList<HisobKitob> tolov;
    ObservableList<HisobKitob> qaytim;
    ObservableList<HisobKitob> chegirma;
    HisobKitob tolovPlastic;
    HisobKitob qaytimPlastic;

    Button orqagaButton;
    Button oldingaButton;
    Button yakunlaButton;
    HBox buttunHBox;
    DecimalFormat decimalFormat = new MoneyShow();
    Font font = Font.font("Arial", FontWeight.BOLD,16);

    public static void main(String[] args) {
        launch(args);
    }

    public TolovQabuli() {
        connection = new MySqlDBGeneral(ServerType.REMOTE).getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
        ibtido();
    }

    public TolovQabuli(Connection connection, User user) {
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
        centerPane.getChildren().addAll(new Button("12345"));
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
    }

    private void initBorderPane() {
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Bir panel");
        gridPane = kelishma(1250000d);
        scene = new Scene(gridPane);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
    }

    private HBox buttoHBox() {
        HBox hBox = new HBox();
        HBox.setHgrow(hBox, Priority.ALWAYS);
        orqagaButton = orqagaButton();
        oldingaButton = oldingaButton();
        yakunlaButton = yakunlaButton();
        hBox.getChildren().addAll(orqagaButton, yakunlaButton, oldingaButton);
        return hBox;
    }

    private Button yakunlaButton() {
        Button button = new Button("Yakunla");
        button.setMaxWidth(2000);
        button.setPrefWidth(50);
        HBox.setHgrow(button, Priority.ALWAYS);
        button.setOnAction(event -> {
            Platform.exit();
            System.exit(0);
        });
        return button;
    }

    private Button oldingaButton() {
        Button button = new Button(">>");
        button.setMaxWidth(2000);
        button.setPrefWidth(50);
        HBox.setHgrow(button, Priority.ALWAYS);
        return button;
    }

    private Button orqagaButton() {
        Button button = new Button("<<");
        button.setMaxWidth(2000);
        button.setPrefWidth(50);
        HBox.setHgrow(button, Priority.ALWAYS);
        return button;
    }

    public GridPane kelishma(Double jamiMablag) {
        this.jamiMablagDouble = jamiMablagDouble;
        this.kelishmaDouble = kelishmaDouble;
        GridPane gridPane = new GridPane();
        HBox.setHgrow(gridPane, Priority.ALWAYS);
        gridPane.setPadding(new Insets(padding));
        gridPane.setHgap(2);
        Label label = new Label("Kelishilgan narh");
        label.setFont(font);
        Label label1 = new Label("Xarid narhi");
        Label label2 = new Label(decimalFormat.format(jamiMablag));
        Label label3 = new Label("Kelishilgan narh");
        HBox.setHgrow(label, Priority.ALWAYS);
        TextField kelishmaTextField = new TextField(decimalFormat.format(jamiMablag));
        kelishmaTextField.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(kelishmaTextField, Priority.ALWAYS);

        Integer rowIndex = 0;
        gridPane.add(label, 0, rowIndex, 2, 1);
        GridPane.setHalignment(label, HPos.CENTER);

        rowIndex++;
        gridPane.add(label1, 0, rowIndex, 1, 1);
        gridPane.add(label2, 1, rowIndex, 1, 1);
        GridPane.setHalignment(label2, HPos.RIGHT);

        rowIndex++;
        gridPane.add(label3, 0, rowIndex, 1, 1);
        gridPane.add(kelishmaTextField, 1, rowIndex, 1, 1);
        GridPane.setHalignment(kelishmaTextField, HPos.RIGHT);

        rowIndex++;
        Label label4 = new Label("Chegirma");
        Label label5 = new Label("1 000 000");
        gridPane.add(label4, 0, rowIndex, 1, 1);
        gridPane.add(label5, 1, rowIndex, 1, 1);
        GridPane.setHalignment(label5, HPos.RIGHT);

        rowIndex++;
        Label label6 = new Label("Qo`shimcha daromad");
        Label label7 = new Label("1 000 000");
        gridPane.add(label6, 0, rowIndex, 1, 1);
        gridPane.add(label7, 1, rowIndex, 1, 1);
        GridPane.setHalignment(label7, HPos.RIGHT);

        rowIndex++;
        buttunHBox = buttoHBox();
        gridPane.add(buttunHBox, 0, rowIndex, 2, 1);

        GridPane.setHgrow(buttunHBox, Priority.ALWAYS);
        return gridPane;
    }

    public GridPane tolov(Double jamiMablag, Double kelishmaDouble ) {
        GridPane gridPane = new GridPane();
        return gridPane;
    }

    public GridPane qaytim(Double jamiMablag, Double kelishmaDouble ) {
        GridPane gridPane = new GridPane();
        return gridPane;
    }
    public GridPane chegirma(Double jamiMablag, Double kelishmaDouble ) {
        GridPane gridPane = new GridPane();
        return gridPane;
    }

    public GridPane yakun(Double jamiMablagDouble, Double kelishmaDouble ) {
        GridPane gridPane = new GridPane();

        return gridPane;
    }

    public GridPane boshGridPane() {
        GridPane gridPane = new GridPane();
        gridPane.setPrefWidth(600);
        HBox.setHgrow(gridPane, Priority.ALWAYS);
        buttunHBox = buttoHBox();
        gridPane.add(buttunHBox, 0, 0, 1, 1);
        GridPane.setHgrow(buttunHBox, Priority.ALWAYS);
        GridPane.setVgrow(buttunHBox, Priority.ALWAYS);
        return gridPane;
    }
}
