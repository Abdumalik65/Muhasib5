package sample.Temp;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import sample.Config.MySqlDBGeneral;
import sample.Data.*;
import sample.Enums.ServerType;
import sample.Model.GuruhNarhModels;
import sample.Model.Standart6Models;
import sample.Tools.SetHVGrow;
import sample.Tools.TextFieldDouble;

import java.sql.Connection;
import java.util.Date;

public class YangiTovarGuruhi extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    HBox topPane = new HBox();
    VBox leftPane = new VBox();
    VBox centerPane = new VBox();
    VBox rightPane = new VBox();
    HBox bottomPane = new HBox();
    GridPane gridPane = new GridPane();


    ObservableList<Standart6> s6List;
    Connection connection;
    User user = new User(1, "admin", "", "admin");
    Standart6 yangiGuruh;
    int padding = 3;


    public static void main(String[] args) {
        launch(args);
    }

    public YangiTovarGuruhi() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        ibtido();
    }

    public YangiTovarGuruhi(Connection connection, User user) {
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

    public Standart6 display() {
        stage = new Stage();
        initStage(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        return yangiGuruh;
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
        leftPane.getChildren().addAll();
    }

    private void initCenterPane() {
        SetHVGrow.VerticalHorizontal(centerPane);
        centerPane.setPadding(new Insets(padding));
        initGridPane();
        centerPane.getChildren().add(gridPane);
    }

    private void initGridPane() {
        TextField guruhNomiTextField = initGuruhTextField();
        Label label1 = new Label("Tannarh");
        Label label2 = new Label("Ulgurji");
        Label label3 = new Label("Chakana");
        Label label4 = new Label("NDS");
        Label label5 = new Label("Bojxona solig`i");
        HBox.setHgrow(label1, Priority.ALWAYS);
        HBox.setHgrow(label2, Priority.ALWAYS);
        HBox.setHgrow(label3, Priority.ALWAYS);
        HBox.setHgrow(label4, Priority.ALWAYS);
        HBox.setHgrow(label5, Priority.ALWAYS);

        TextField textField1 = new TextField();
        TextField textField2 = new TextField();
        TextField textField3 = new TextField();
        TextField textField4 = new TextField();
        TextField textField5 = new TextField();
        HBox.setHgrow(textField1, Priority.ALWAYS);
        HBox.setHgrow(textField2, Priority.ALWAYS);
        HBox.setHgrow(textField3, Priority.ALWAYS);
        HBox.setHgrow(textField4, Priority.ALWAYS);
        HBox.setHgrow(textField5, Priority.ALWAYS);

        HBox hBox = initButtonsHBox();
        Button cancelButton = (Button) hBox.getChildren().get(0);
        Button qaydEtButton = (Button) hBox.getChildren().get(1);

        SetHVGrow.VerticalHorizontal(gridPane);
        gridPane.setPadding(new Insets(padding));
        int rowIndex = 0;
        gridPane.add(guruhNomiTextField, 0, rowIndex, 2, 1);
        GridPane.setHgrow(guruhNomiTextField, Priority.ALWAYS);

        rowIndex++;
        gridPane.add(label1, 0, rowIndex, 1, 1);
        gridPane.add(textField1, 1, rowIndex, 1, 1);
        GridPane.setHgrow(label1, Priority.ALWAYS);
        GridPane.setHgrow(textField1, Priority.ALWAYS);
        textField1.setTextFormatter(new TextFieldDouble().getTextFormatter());
        textField1.setAlignment(Pos.CENTER_RIGHT);

        rowIndex++;
        gridPane.add(label2, 0, rowIndex, 1, 1);
        gridPane.add(textField2, 1, rowIndex, 1, 1);
        GridPane.setHgrow(label2, Priority.ALWAYS);
        GridPane.setHgrow(textField2, Priority.ALWAYS);
        textField2.setTextFormatter(new TextFieldDouble().getTextFormatter());
        textField2.setAlignment(Pos.CENTER_RIGHT);

        rowIndex++;
        gridPane.add(label3, 0, rowIndex, 1, 1);
        gridPane.add(textField3, 1, rowIndex, 1, 1);
        GridPane.setHgrow(label3, Priority.ALWAYS);
        GridPane.setHgrow(textField3, Priority.ALWAYS);
        textField3.setTextFormatter(new TextFieldDouble().getTextFormatter());
        textField3.setAlignment(Pos.CENTER_RIGHT);

        rowIndex++;
        gridPane.add(label4, 0, rowIndex, 1, 1);
        gridPane.add(textField4, 1, rowIndex, 1, 1);
        GridPane.setHgrow(label4, Priority.ALWAYS);
        GridPane.setHgrow(textField4, Priority.ALWAYS);
        textField4.setTextFormatter(new TextFieldDouble().getTextFormatter());
        textField4.setAlignment(Pos.CENTER_RIGHT);

        rowIndex++;
        gridPane.add(label5, 0, rowIndex, 1, 1);
        gridPane.add(textField5, 1, rowIndex, 1, 1);
        GridPane.setHgrow(label5, Priority.ALWAYS);
        GridPane.setHgrow(textField5, Priority.ALWAYS);
        textField5.setTextFormatter(new TextFieldDouble().getTextFormatter());
        textField5.setAlignment(Pos.CENTER_RIGHT);

        rowIndex++;
        gridPane.add(cancelButton, 0, rowIndex, 1, 1);
        gridPane.add(qaydEtButton, 1, rowIndex, 1, 1);
        GridPane.setHgrow(hBox, Priority.ALWAYS);

        qaydEtButton.setOnAction(event -> {
            Date sana = new Date();
            Standart6 s6 = new Standart6(
                    guruhNomiTextField.getText(),
                    Double.valueOf(textField1.getText()),
                    Double.valueOf(textField2.getText()),
                    Double.valueOf(textField3.getText()),
                    Double.valueOf(textField4.getText()),
                    Double.valueOf(textField5.getText()),
                    user.getId(),
                    sana
            );
            Standart6Models standart6Models = new Standart6Models("TGuruh1");
            standart6Models.insert_data(connection, s6);
            ObservableList<GuruhNarh> gnList = FXCollections.observableArrayList();
            GuruhNarh guruhNarh1 = new GuruhNarh(
                    null, sana, s6.getId(), 0, s6.getNarh(), user.getId(), sana
            );
            GuruhNarh guruhNarh2 = new GuruhNarh(
                    null, sana, s6.getId(), 1, s6.getUlgurji(), user.getId(), sana
            );
            GuruhNarh guruhNarh3 = new GuruhNarh(
                    null, sana, s6.getId(), 2, s6.getChakana(), user.getId(), sana
            );
            GuruhNarh guruhNarh4 = new GuruhNarh(
                    null, sana, s6.getId(), 3, s6.getBoj(), user.getId(), sana
            );
            GuruhNarh guruhNarh5 = new GuruhNarh(
                    null, sana, s6.getId(), 4, s6.getNds(), user.getId(), sana
            );
            gnList.addAll(guruhNarh1, guruhNarh2, guruhNarh3, guruhNarh4, guruhNarh5);

            GuruhNarhModels guruhNarhModels = new GuruhNarhModels();
            guruhNarhModels.addBatch(connection, gnList);
            yangiGuruh = s6;
            stage.close();
        });

        cancelButton.setOnAction(event -> {
            System.out.println("Cancel");
            yangiGuruh = null;
            stage.close();
        });
    }

    private TextField initGuruhTextField() {
        TextField textField = new TextField();
        textField.setPromptText("GURUH NOMI");
        HBox.setHgrow(textField, Priority.ALWAYS);
        HBox.setHgrow(textField, Priority.ALWAYS);
        Standart6Models standart6Models = new Standart6Models("TGuruh1");
        s6List = standart6Models.get_data(connection);
        TextFields.bindAutoCompletion(textField, s6List).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Standart6> autoCompletionEvent) -> {
        });
        return textField;
    }

    private HBox initButtonsHBox() {
        HBox hBox = new HBox();
        Button cancelButton = new Button("<<");
        Button qaydEtButton = new Button("Qayd et");
        cancelButton.setMaxWidth(2000);
        cancelButton.setPrefWidth(150);
        qaydEtButton.setMaxWidth(2000);
        qaydEtButton.setPrefWidth(150);
        HBox.setHgrow(cancelButton, Priority.ALWAYS);
        HBox.setHgrow(qaydEtButton, Priority.ALWAYS);
        HBox.setHgrow(hBox, Priority.ALWAYS);
        VBox.setVgrow(hBox, Priority.ALWAYS);
        hBox.getChildren().addAll(cancelButton, qaydEtButton);
        return hBox;
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
        stage.setTitle("Yangi tovar guruhi");
        scene = new Scene(borderpane, 300, 210);
        stage.setScene(scene);
        stage.onCloseRequestProperty();
        stage.setOnCloseRequest(event -> {

        });
    }
}
