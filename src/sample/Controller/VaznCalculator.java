package sample.Controller;

import javafx.application.Application;
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
import sample.Data.User;
import sample.Tools.Alerts;

import java.sql.Connection;
import java.text.NumberFormat;

public class VaznCalculator extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    GridPane gridPane = new GridPane();
    HBox buttonsHBox = new HBox();

    TextField textField1 = new TextField("0.0");
    TextField textField2 = new TextField("0.0");
    TextField textField3 = new TextField("0.0");
    Label natijaLabel = new Label();

    Button qaydEtButton = new Button("QaydEt");
    Button cancelButton = new Button("<<");

    Connection connection;
    User user = new User(1, "admin", "", "admin");
    int padding = 3;
    String style20 = "-fx-font: 20px Arial";
    Font font = Font.font("Arial", FontWeight.BOLD,20);
    Double hajmDouble = 0.0;
    Boolean qaydEtdimBoolean = false;
    NumberFormat numberFormat = NumberFormat.getInstance();


    public static void main(String[] args) {
        launch(args);
    }

    public VaznCalculator() {
        ibtido();
    }

    public VaznCalculator(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        ibtido();
    }

    public VaznCalculator(Double d1, Double d2) {
        textField1.setText(numberFormat.format(d1));
        textField2.setText(numberFormat.format(d2));
        Double d3 = Double.valueOf(textFieldReplace(textField3.getText()));
        Double natija = (d1*d2)+d3;
        natijaLabel.setText(numberFormat.format(natija));    ibtido();
    }

    private void ibtido() {
        initTextFields();
        initButtons();
        initGridPane();
        initBorderPane();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initStage(primaryStage);
        stage.show();
    }

    public Double display() {
        stage = new Stage();
        initStage(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        return hajmDouble;
    }

    private void initTextFields() {
        numberFormat.setMinimumIntegerDigits (1);
        numberFormat.setMaximumIntegerDigits (6);

        numberFormat.setMinimumFractionDigits (1);
        numberFormat.setMaximumFractionDigits (5);

        HBox.setHgrow(textField1, Priority.ALWAYS);
        textField1.setAlignment(Pos.CENTER_RIGHT);
        textField1.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newValue = newValue.trim().replaceAll(" ", "");
                newValue = newValue.trim().replaceAll(",", ".");
                if (!Alerts.isNumericAlert(newValue)) {
                    newValue = oldValue;
                }
                Double d1 = Double.valueOf(textFieldReplace(newValue));
                Double d2 = Double.valueOf(textFieldReplace(textField2.getText()));
                Double d3 = Double.valueOf(textFieldReplace(textField3.getText()));
                Double natija = (d1*d2)+d3;
                natijaLabel.setText(numberFormat.format(natija));
            }
        });

        HBox.setHgrow(textField2, Priority.ALWAYS);
        textField2.setAlignment(Pos.CENTER_RIGHT);
        textField2.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newValue = newValue.trim().replaceAll(" ", "");
                newValue = newValue.trim().replaceAll(",", ".");
                if (!Alerts.isNumericAlert(newValue)) {
                    newValue = oldValue;
                }
                Double d1 = Double.valueOf(textFieldReplace(textField1.getText()));
                Double d2 = Double.valueOf(textFieldReplace(newValue));
                Double d3 = Double.valueOf(textFieldReplace(textField3.getText()));
                Double natija = (d1*d2)+d3;
                natijaLabel.setText(numberFormat.format(natija));
            }
        });

        HBox.setHgrow(textField3, Priority.ALWAYS);
        textField3.setAlignment(Pos.CENTER_RIGHT);
        textField3.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newValue = newValue.trim().replaceAll(" ", "");
                newValue = newValue.trim().replaceAll(",", ".");
                if (!Alerts.isNumericAlert(newValue)) {
                    newValue = oldValue;
                }
                Double d1 = Double.valueOf(textFieldReplace(textField1.getText()));
                Double d2 = Double.valueOf(textFieldReplace(textField2.getText()));
                Double d3 = Double.valueOf(textFieldReplace(newValue));
                Double natija = (d1*d2)+d3;
                natijaLabel.setText(numberFormat.format(natija));
            }
        });
    }

    private void initButtons() {
        qaydEtButton.setMaxWidth(2000);
        qaydEtButton.setPrefWidth(150);
        cancelButton.setMaxWidth(2000);
        cancelButton.setPrefWidth(150);

        qaydEtButton.setOnAction(event -> {
            Double d1 = Double.valueOf(textFieldReplace(textField1.getText()));
            Double d2 = Double.valueOf(textFieldReplace(textField2.getText()));
            Double d3 = Double.valueOf(textFieldReplace(textField3.getText()));
            hajmDouble = (d1*d2)+d3;
            qaydEtdimBoolean = true;
            stage.close();
        });

        cancelButton.setOnAction(event -> {
            stage.close();
        });
    }

    private void initGridPane() {
        HBox.setHgrow(gridPane, Priority.ALWAYS);
        VBox.setVgrow(gridPane, Priority.ALWAYS);
        int rowIndex = 0;
        gridPane.setPadding(new Insets(3));
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.setPadding(new Insets(3));

        gridPane.add(textField1, 0, rowIndex, 1, 1);

        rowIndex++;
        gridPane.add(textField2, 0, rowIndex, 1, 1);

        rowIndex++;
        gridPane.add(textField3, 0, rowIndex, 1, 1);

        rowIndex++;
        natijaLabel.setStyle(style20);
        GridPane.setHalignment(natijaLabel, HPos.RIGHT);
        gridPane.add(natijaLabel, 0, rowIndex, 1, 1);

        rowIndex++;
        HBox.setHgrow(buttonsHBox, Priority.ALWAYS);
        buttonsHBox.getChildren().addAll(cancelButton, qaydEtButton);
        gridPane.add(buttonsHBox, 0, rowIndex, 1, 1);
    }

    private void initBorderPane() {
        borderpane.setCenter(gridPane);
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Vazn");
        scene = new Scene(borderpane, 200, 150);
        stage.setScene(scene);
    }

    private String textFieldReplace(String string2) {
        String string = string2;
        string = string.replaceAll(" ", "");
        string = string.replaceAll(",", ".");
        return string;
    }
}
