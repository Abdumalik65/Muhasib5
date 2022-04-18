package sample.Controller;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import sample.Config.MySqlDBGeneral;
import sample.Data.Hisob;
import sample.Data.SerialNumber;
import sample.Data.Standart;
import sample.Data.User;
import sample.Enums.ServerType;
import sample.Model.HisobModels;
import sample.Model.SerialNumbersModels;
import sample.Model.StandartModels;
import sample.Tools.*;

import java.sql.Connection;
import java.util.Date;

public class YangiSeriyaRaqami extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    HBox topPane = new HBox();
    VBox leftPane = new VBox();
    VBox centerPane = new VBox();
    VBox rightPane = new VBox();
    HBox bottomPane = new HBox();
    HBoxTextFieldPlusButton hisob1Hbox = new HBoxTextFieldPlusButton();
    Hisob hisob;
    HBoxTextFieldPlusButton tovarHBox = new HBoxTextFieldPlusButton();
    ObservableList<Hisob> hisobObservableList;
    Standart tovar;
    Connection connection;
    User user;
    int padding = 3;
    Font font = Font.font("Arial", FontWeight.BOLD,20);
    HisobModels hisobModels = new HisobModels();
    StandartModels standartModels = new StandartModels("Tovar");
    SerialNumbersModels serialNumbersModels = new SerialNumbersModels();
    ObservableList<Standart> tovarObservableList;
    TextField invoiceTextField = new TextField();
    TextField seriyaRaqamiTextField = new TextField();
    Button qaydEtButton = new Button("Qayd et");
    Button cancelButton = new Button("Bekor qil");
    Boolean yangiSeriyaRaqami = false;


    public static void main(String[] args) {
        launch(args);
    }

    public YangiSeriyaRaqami() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
        ibtido();
    }

    public YangiSeriyaRaqami(Connection connection, User user) {
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
        hisobObservableList = hisobModels.get_data(connection);
        tovarObservableList = standartModels.get_data(connection);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initStage(primaryStage);
        stage.show();
    }

    public Boolean display() {
        stage = new Stage();
        initStage(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        return yangiSeriyaRaqami;
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
        initHisobHbox();
        initTovarHBox();
        initTextFields();
        HBox hBox = initButtons();
        Separator separator1 = new Separator(Orientation.HORIZONTAL);
        Separator separator2= new Separator(Orientation.HORIZONTAL);
        Separator separator3 = new Separator(Orientation.HORIZONTAL);
        centerPane.getChildren().addAll(seriyaRaqamiTextField, separator1, separator2, separator3, invoiceTextField, hisob1Hbox, tovarHBox, hBox);
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
        stage.setTitle("Yangi seriya raqami");
        scene = new Scene(borderpane, 300, 210);
        stage.setScene(scene);
    }

    private void initHisobHbox() {
        hisob1Hbox = new HBoxTextFieldPlusButton();
        TextField textField = hisob1Hbox.getTextField();
        textField.setFont(font);
        textField.setPromptText("Chiqim hisobi");
        Button button = hisob1Hbox.getPlusButton();
        button.setMinHeight(37);
        button.setGraphic(new PathToImageView("/sample/images/Icons/add.png").getImageView());
        TextFields.bindAutoCompletion(textField, hisobObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Hisob> autoCompletionEvent) -> {
            hisob = autoCompletionEvent.getCompletion();
        });
        button.setOnAction(event -> {
            hisob = addHisob();
            if (hisob != null) {
                textField.setText(hisob.getText());
            }
        });
    }

    private Hisob addHisob() {
        Hisob hisob = null;
        HisobController hisobController = new HisobController();
        hisobController.display(connection, user);
        if (hisobController.getDoubleClick()) {
            hisob = hisobController.getDoubleClickedRow();
        }
        return hisob;
    }

    private void initTovarHBox() {
        HBox.setHgrow(tovarHBox, Priority.ALWAYS);
        TextField textField = tovarHBox.getTextField();
        textField.setPromptText("Tovar nomi");
        textField.setFont(font);
        TextFields.bindAutoCompletion(textField, tovarObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Standart> autoCompletionEvent) -> {
            Standart newValue = autoCompletionEvent.getCompletion();
            if (newValue != null) {
                tovar = newValue;
            }
        });

        Button addButton = tovarHBox.getPlusButton();
        addButton.setMinHeight(37);
        addButton.setOnAction(event -> {
            Standart newValue = addTovar();
            if (newValue != null) {
                tovar = newValue;
                textField.setText(tovar.getText());
            }
        });
    }

    private Standart addTovar() {
        Standart tovar = null;
        TovarController tovarController = new TovarController(connection, user);
        tovarController.display();
        if (tovarController.getDoubleClick()) {
            tovar = tovarController.getDoubleClickedRow();
        }
        return tovar;
    }

    private void initTextFields() {
        seriyaRaqamiTextField.setPromptText("Seriya raqami");
        seriyaRaqamiTextField.setFont(font);
        HBox.setHgrow(seriyaRaqamiTextField, Priority.ALWAYS);
        invoiceTextField.setPromptText("Invoice raqami");
        invoiceTextField.setFont(font);
        HBox.setHgrow(invoiceTextField, Priority.ALWAYS);
    }

    private void initQaydEtButton() {
        qaydEtButton.setFont(font);
        qaydEtButton.setMaxWidth(2000);
        qaydEtButton.setPrefWidth(150);
        qaydEtButton.setOnAction(event -> {
            if (!serialNumbersModels.getSerialNumber(connection, seriyaRaqamiTextField.getText())) {
                add();
                stage.close();
            } else {
                Alerts.AlertString("Seriya raqami kiritilmadi.");
            }
        });
    }

    private void initCancelButton() {
        cancelButton.setFont(font);
        cancelButton.setMaxWidth(2000);
        cancelButton.setPrefWidth(150);
        cancelButton.setOnAction(event -> {
            System.out.println("Cancel button");
            stage.close();
        });
    }

    private HBox initButtons() {
        HBox hBox = new HBox();
        initCancelButton();
        initQaydEtButton();
        HBox.setHgrow(hBox, Priority.ALWAYS);
        hBox.getChildren().addAll(qaydEtButton, cancelButton);
        return hBox;
    }

    private SerialNumber add() {
        Date sana = new Date();
        String sn = seriyaRaqamiTextField.getText();
        SerialNumber serialNumber = new SerialNumber(
                null,
                sana,
                hisob.getId(),
                invoiceTextField.getText(),
                tovar.getId(),
                sn,
                user.getId(),
                null);
        serialNumbersModels.insert(connection, serialNumber);
        return serialNumber;
    }
}
