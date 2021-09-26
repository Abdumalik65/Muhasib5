package sample.Controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.Data.Kassa;
import sample.Data.User;
import sample.Model.KassaModels;
import sample.Model.UserModels;
import sample.Tools.ConnectionType;
import sample.Tools.GetDbData;
import sample.Tools.Shake;

import java.sql.Connection;

public class LoginUserController extends Application {
    Stage stage;
    BorderPane borderpane = new BorderPane();
    VBox centerPane = new VBox();
    GridPane gridPane = new GridPane();
    TextField userNameTextField = new TextField();
    PasswordField passwordField = new PasswordField();

    Button loginButton = new Button("Login");
    Button cancelButton = new Button("Cancel");
    HBox buttonsHBox = new HBox();
    int padding = 3;
    Boolean logged = false;
    User user;

    Connection connection;

    public static void main(String[] args) {
        launch(args);
    }

    public LoginUserController(Connection connection) {
        this.connection = connection;
    }

    private void ibtido() {
        initButtons();
        initGridPane();
        initCenterPane();
        initBorderPane();
    }

    public Boolean display() {
        ibtido();
        stage = new Stage();
        initStage(stage);
        stage.showAndWait();
        return logged;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ibtido();
        initStage(primaryStage);
        stage.show();
    }

    private void initButtons() {
        loginButton.setMaxWidth(2000);
        loginButton.setPrefWidth(150);
        HBox.setHgrow(loginButton, Priority.ALWAYS);

        cancelButton.setMaxWidth(2000);
        cancelButton.setPrefWidth(150);
        HBox.setHgrow(cancelButton, Priority.ALWAYS);

        buttonsHBox.getChildren().addAll(cancelButton, loginButton);
        HBox.setHgrow(buttonsHBox, Priority.ALWAYS);

        loginButton.setOnAction(event -> {
            if (!(userNameTextField.getText() + passwordField.getText()).isEmpty()) {
                user = null;
                UserModels userModels = new UserModels();
                User userPar = new User(userNameTextField.getText(), passwordField.getText());
                ObservableList<User> users = userModels.getData(connection);
                if (users.size()>0) {
                    for (User u: users) {
                        if (u.getIsm().trim().equalsIgnoreCase(userPar.getIsm().trim()) && u.getParol().trim().equals(userPar.getParol().trim())) {
                            user = u;
                            break;
                        }
                    }
                }
                if (user != null) {
                    logged = true;
                    user.setOnline(1);
                    userModels.changeUser(connection, user);
                    System.out.println("Kirdik");
                    GetDbData.setUser(user);
                    buKassami();
                    stage.close();
                } else {
                    logged = false;
                    System.out.println("Kira olmadik");
                    Shake shake = new Shake(userNameTextField);
                    shake.playAnim();
                    Shake shake1 = new Shake(passwordField);
                    shake1.playAnim();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Diqqat!!!");
                alert.setHeaderText("Ism va parol kiritilmadi");
                alert.setContentText("Ism va parol kiriting");
                alert.showAndWait();
            }
        });

        cancelButton.setOnAction(event -> {
            System.out.println("Chiqvoldik");
            Platform.exit();
            System.exit(0);
            logged = false;
        });
    }

    private void initCenterPane() {
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        VBox.setVgrow(centerPane, Priority.ALWAYS);
        centerPane.setPadding(new Insets(padding));
        centerPane.getChildren().add(gridPane);
    }

    private void initGridPane() {
        gridPane.setHgap(padding);
        gridPane.setVgap(padding);

        int rowIndex = 0;
        gridPane.getChildren().removeAll(gridPane.getChildren());

        gridPane.add(new Label("Xodim"), 0, rowIndex, 1, 1);
        gridPane.add(userNameTextField, 1, 0, 1, 1);

        rowIndex++;

        gridPane.add(new Label("Parol"), 0, rowIndex, 1, 1);
        gridPane.add(passwordField, 1, rowIndex, 1,1);

        rowIndex++;

        gridPane.add(buttonsHBox, 0, rowIndex, 2, 1);
    }

    private void initSystemMenu() {
    }

    private void initBorderPane() {
        borderpane.setCenter(centerPane);
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Kirish");
        Scene scene = new Scene(borderpane, 213, 87);
        stage.setOnCloseRequest(event -> {
            user =  null;
            logged = false;
        });
        stage.setScene(scene);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private Boolean buKassami() {
        String serialNumber = ConnectionType.getAloqa().getText().trim();
        Boolean buKassami = false;
        ObservableList<Kassa> kassaObservableList = null;
        KassaModels kassaModels = new KassaModels();
        Kassa kassa = null;
        kassaObservableList = kassaModels.getAnyData(connection, "serialNumber = '" + serialNumber + "'", "");
        if (kassaObservableList.size() > 0) {
            kassa = kassaObservableList.get(0);
            if (kassa.getSerialNumber().trim().equalsIgnoreCase(serialNumber)) {
                if (kassa.getIsLocked() == 0) {
                    kassa.setOnline(1);
                    buKassami = true;
                    kassaModels.update_data(connection, kassa);
                } else {
                    System.out.println(kassa.getKassaNomi().trim() + " nozir tomonidan qulflangan");
                    Platform.exit();
                    System.exit(0);
                }
            }
        }
        return buKassami;
    }

}
