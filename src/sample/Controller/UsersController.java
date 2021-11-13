package sample.Controller;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.Config.MySqlDB;
import sample.Config.MySqlDBGeneral;
import sample.Data.User;
import sample.Enums.ServerType;
import sample.Model.UserModels;
import sample.Tools.Tugmachalar;
import java.io.File;
import java.sql.Connection;

public class UsersController extends Application {
    Stage stage;
    BorderPane borderpane = new BorderPane();
    VBox centerPane = new VBox();
    ObservableList<User> userObservableList;
    Tugmachalar tugmachalar = new Tugmachalar();
    TableView<User> userTableView = new TableView<>();
    GridPane userGridPane = new GridPane();
    int padding = 3;
    Connection connection;
    UserModels userModels = new UserModels();
    User user;

    TextField ismTextField = new TextField();
    TextField rasmTextField = new TextField();
    Button rasmButton = new Button("...");
    HBox rasmHBox = new HBox();
    TextField parolTextField = new TextField();
    TextField emailTextField = new TextField();
    TextField telefonTextField = new TextField();
    TextField jinsTextField = new TextField();
    Button qaydEtButton = new Button("Qayd et");
    Button cancelButton = new Button("<<");
    HBox hBoxButtons = new HBox();

    public static void main(String[] args) {
        launch(args);
    }

    public UsersController() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
    }

    public UsersController(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        initUserGridPane();
    }

    private TableColumn<User, String> ismColumn()  {
        TableColumn<User, String> ismColumn = new TableColumn<>("Ism");
        ismColumn.setMinWidth(200);
        ismColumn.setCellValueFactory(new PropertyValueFactory("ism"));
        return ismColumn;
    }

    private TableColumn<User, String> parolColumn()  {
        TableColumn<User, String> parolColumn = new TableColumn<>("Parol");
        parolColumn.setMinWidth(100);
        parolColumn.setCellValueFactory(new PropertyValueFactory("parol"));
        return parolColumn;
    }

    private TableColumn<User, Integer> onlineColumn() {
        TableColumn<User, Integer> onlineColumn = new TableColumn<>("Status");
        onlineColumn.setCellValueFactory(new PropertyValueFactory<>("online"));
        onlineColumn.setCellFactory(column -> {
            TableCell<User, Integer> cell = new TableCell<User, Integer>() {
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
        return onlineColumn;
    }


    private TableColumn<User, Integer> idColumn()  {
        TableColumn<User, Integer> idColumn = new TableColumn<>("N");
        idColumn.setMinWidth(30);
        idColumn.setCellValueFactory(new PropertyValueFactory("id"));
        return idColumn;
    }

    public void initUserTableView() {
        HBox.setHgrow(userTableView, Priority.ALWAYS);
        VBox.setVgrow(userTableView, Priority.ALWAYS);
        userTableView.getColumns().addAll(idColumn(), ismColumn(), parolColumn(), onlineColumn());
        userTableView.setItems(userObservableList);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initStage(primaryStage);
        connection = new MySqlDB().getDbConnection();
        ibtido();
        stage.show();
    }

    private void ibtido() {
        initData();
        initButtons();
        initUserTableView();
        initCenterPane();
        initBorderPane();
    }

    public void display() {
        ibtido();
        stage = new Stage();
        initStage(stage);
        stage.show();
    }

    private void initData() {
        userObservableList = userModels.getData(connection);
    }

    private void initButtons() {
        tugmachalar.getAdd().setOnAction(event -> {
            centerPane.getChildren().removeAll(centerPane.getChildren());
            centerPane.getChildren().add(userGridPane);
            qaydEtButton.setOnAction(event1 -> {
                addNewUser();
                cancelButton.fire();
            });

            cancelButton.setOnAction(event1 -> {
                centerPane.getChildren().removeAll(centerPane.getChildren());
                centerPane.getChildren().addAll(tugmachalar, userTableView);
            });

        });
        tugmachalar.getDelete().setOnAction(event -> {
            User selectedItem = userTableView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                userModels.delete_data(connection, selectedItem);
                userObservableList.remove(selectedItem);
                userTableView.refresh();
            }
        });
        tugmachalar.getEdit().setOnAction(event -> {
            User selectedItem = userTableView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                setUserToGrid(selectedItem);
                centerPane.getChildren().removeAll(centerPane.getChildren());
                centerPane.getChildren().add(userGridPane);
            }
            qaydEtButton.setOnAction(event1 -> {
                editUser(selectedItem);
                userTableView.refresh();
                cancelButton.fire();
            });

            cancelButton.setOnAction(event1 -> {
                centerPane.getChildren().removeAll(centerPane.getChildren());
                centerPane.getChildren().addAll(tugmachalar, userTableView);
            });

        });
    }

    private void initCenterPane() {
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        VBox.setVgrow(centerPane, Priority.ALWAYS);
        centerPane.setPadding(new Insets(padding));
        centerPane.getChildren().addAll(tugmachalar, userTableView);
    }

    private void initBorderPane() {
        borderpane.setCenter(centerPane);
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Dastur yurituvchilar");
        Scene scene = new Scene(borderpane, 600, 400);
        stage.setScene(scene);
    }

    private GridPane initUserGridPane() {
        int rowIndex = 0;

        HBox.setHgrow(userGridPane, Priority.ALWAYS);
        VBox.setVgrow(userGridPane, Priority.ALWAYS);
        userGridPane.setVgap(10);
        userGridPane.setHgap(10);
        userGridPane.setPadding(new Insets(padding));

        HBox.setHgrow(hBoxButtons, Priority.ALWAYS);
        hBoxButtons.getChildren().addAll(cancelButton, qaydEtButton);
        qaydEtButton.setMaxWidth(2000);
        qaydEtButton.setPrefWidth(150);
        cancelButton.setMaxWidth(2000);
        cancelButton.setPrefWidth(150);

        GridPane.setHgrow(ismTextField, Priority.ALWAYS);
        userGridPane.add(new Label("Ism"), 0, rowIndex, 1, 1);
        userGridPane.add(ismTextField, 1, rowIndex, 1, 1);

        rowIndex++;
        HBox.setHgrow(rasmTextField, Priority.ALWAYS);
        rasmHBox.getChildren().addAll(rasmTextField, rasmButton);
        userGridPane.add(new Label("Rasm"), 0, rowIndex, 1, 1);
        userGridPane.add(rasmHBox, 1, rowIndex, 1, 1);
        rasmButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Barcha fayllar","*.*"));
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                rasmTextField.setText(file.toString());
            }
        });

        rowIndex++;
        userGridPane.add(new Label("Parol"), 0, rowIndex, 1, 1);
        userGridPane.add(parolTextField, 1, rowIndex, 1, 1);

        rowIndex++;
        userGridPane.add(new Label("email"), 0, rowIndex, 1, 1);
        userGridPane.add(emailTextField, 1, rowIndex, 1, 1);

        rowIndex++;
        userGridPane.add(new Label("Telefon"), 0, rowIndex, 1, 1);
        userGridPane.add(telefonTextField, 1, rowIndex, 1, 1);

        rowIndex++;
        userGridPane.add(new Label("Jins"), 0, rowIndex, 1, 1);
        userGridPane.add(jinsTextField, 1, rowIndex, 1, 1);

        rowIndex++;
        userGridPane.add(hBoxButtons, 1, rowIndex, 1, 1);

        return userGridPane;
    }

    private User getUserFromGrid() {
        User newUser = new User(
                null,
                ismTextField.getText(),
                "",
                parolTextField.getText(),
                emailTextField.getText(),
                telefonTextField.getText(),
                1,
                jinsTextField.getText(),
                0,
                user.getId(),
                null
        );
        return newUser;
    }

    private void setUserToGrid(User user) {
        ismTextField.setText(user.getIsm());
        rasmTextField.setText(user.getRasm());
        parolTextField.setText(user.getParol());
        emailTextField.setText(user.geteMail());
        telefonTextField.setText(user.getPhone());
        jinsTextField.setText(user.getJins());
    }

    private User addNewUser() {
        User newUser = getUserFromGrid();
        if (userValidation(newUser)) {
            UserModels userModels = new UserModels();
            newUser.setId(userModels.addUser(connection, newUser));
            userObservableList.add(newUser);
        }
        return newUser;
    }

    private User editUser(User selectedItem) {
        selectedItem.setIsm(ismTextField.getText());
        selectedItem.setRasm(rasmTextField.getText());
        selectedItem.setParol(parolTextField.getText());
        selectedItem.seteMail(emailTextField.getText());
        selectedItem.setPhone(telefonTextField.getText());
        selectedItem.setJins(jinsTextField.getText());
        UserModels userModels = new UserModels();
        userModels.changeUser(connection, selectedItem);
        return selectedItem;
    }

    private Boolean userValidation(User userValidation) {
        Boolean valid = true;
        UserModels userModels = new UserModels();
        ObservableList<User> users = userModels.getData(connection);
        for (User u: users) {
            if (u.getIsm().trim().equalsIgnoreCase(userValidation.getIsm().trim()) && u.getParol().trim().equalsIgnoreCase(userValidation.getParol())) {
                valid = false;
            }
        }
        return valid;
    }
}