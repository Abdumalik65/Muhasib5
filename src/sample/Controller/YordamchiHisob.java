package sample.Controller;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import sample.Config.MySqlDBGeneral;
import sample.Data.Hisob;
import sample.Data.Standart;
import sample.Data.Standart2;
import sample.Data.User;
import sample.Enums.ServerType;
import sample.Model.Standart2Models;
import sample.Tools.*;

import java.sql.Connection;

public class YordamchiHisob extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    VBox centerPane = new VBox();
    Tugmachalar tugmachalar = new Tugmachalar();
    TableView<Standart2> tableView;
    ObservableList<Standart2> observableList;
    GridPane gridPane;
    Button qaydEtButton = new Button("Qayd et");
    Button cancelButton = new Button("<<");

    Connection connection;
    User user;
    int padding = 3;
    Standart2Models standart2Models = new Standart2Models("YordamchiHisobRoyxati");


    public static void main(String[] args) {
        launch(args);
    }

    public YordamchiHisob() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
        ibtido();
    }

    public YordamchiHisob(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        ibtido();
    }

    private void ibtido() {
        initTopPane();
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

    private void initTopPane() {}
    private void initCenterPane() {
        SetHVGrow.VerticalHorizontal(centerPane);
        centerPane.setPadding(new Insets(padding));
        tugmachalar = yangiTugmachalar();
        tableView = initLeftTable();
        centerPane.getChildren().addAll(tugmachalar, tableView);
    }

    private void initBottomPane() {}

    private void initBorderPane() {
        borderpane.setCenter(centerPane);
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Ikki panel");
        scene = new Scene(borderpane, 300, 400);
        stage.setScene(scene);
    }

    private Tugmachalar yangiTugmachalar() {
        Tugmachalar tugmachalar = new Tugmachalar();
        tugmachalar.getAdd().setOnAction(event -> {
            centerPane.getChildren().removeAll(centerPane.getChildren());
            gridPane = yangiGridPaneJadval();
            centerPane.getChildren().add(gridPane);
            HBox hBox = (HBox) gridPane.getChildren().get(2);
            qaydEtButton = (Button) hBox.getChildren().get(1);
            cancelButton = (Button) hBox.getChildren().get(0);
            qaydEtButton.setOnAction(event1 -> {
                Standart2 standarrt2 = yangiHisobQosh(gridPane);
                cancelButton.fire();
                tableView.getItems().add(standarrt2);
                tableView.refresh();
            });

            cancelButton.setOnAction(event1 -> {
                centerPane.getChildren().remove(gridPane);
                centerPane.getChildren().addAll(tugmachalar, tableView);
            });

        });
        tugmachalar.getDelete().setOnAction(event -> {
            Standart2 standart2 = tableView.getSelectionModel().getSelectedItem();
            if (standart2 != null) {
                if (Alerts.haYoq(standart2.getText() + " ochiriladi", "O`chirrish lozimligiga \nishonchingiz komilmi ???")) {
                    standart2Models.delete_data(connection, standart2);
                    tableView.getItems().remove(standart2);
                    tableView.refresh();
                }
            }
        });
        tugmachalar.getEdit().setOnAction(event -> {
            Standart2 standart2 = tableView.getSelectionModel().getSelectedItem();
            Hisob hisob = GetDbData.getHisob(standart2.getId2());
            gridPane = yangiGridPaneJadval();
            TextField textField = (TextField) gridPane.getChildren().get(0);
            HisobBox hisobBox = (HisobBox) gridPane.getChildren().get(1);
            textField.setText(standart2.getText());
            hisobBox.setHisob(hisob);
            centerPane.getChildren().removeAll(centerPane.getChildren());
            centerPane.getChildren().add(gridPane);
            HBox hBox = (HBox) gridPane.getChildren().get(2);
            qaydEtButton = (Button) hBox.getChildren().get(1);
            cancelButton = (Button) hBox.getChildren().get(0);
            qaydEtButton.setOnAction(event1 -> {
                standart2.setText(textField.getText());
                standart2.setId2(hisobBox.getHisob().getId());
                hisobniOzgartir(standart2);
                tableView.refresh();
                cancelButton.fire();
            });

            cancelButton.setOnAction(event1 -> {
                centerPane.getChildren().removeAll(centerPane.getChildren());
                centerPane.getChildren().addAll(tugmachalar, tableView);
            });

        });
        return tugmachalar;
    }

    private Standart2 yangiHisobQosh(GridPane gridPane) {
        TextField textField = (TextField) gridPane.getChildren().get(0);
        HisobBox hisobBox = (HisobBox) gridPane.getChildren().get(1);
        String guruhNomi = textField.getText();
        Hisob hisob = hisobBox.getHisob();
        Standart2 standart2 = new Standart2(null, hisob.getId(), guruhNomi, user.getId(), null);
        standart2Models.insert_data(connection, standart2);
        return standart2;
    }

    private void hisobniOzgartir(Standart2 standart2) {
        if (standart2 != null) {
            standart2Models.update_data(connection, standart2);
        }
    }

    private TableView initLeftTable() {
        TableView<Standart2> tableView = new TableView<>();
        tableView.getColumns().addAll(
                getStandart2TextColumn(),
                getStandart2Id2Column()
        );
        tableView.setItems(getLeftTableData());
        tableView.getSelectionModel().selectedItemProperty().addListener((observable ,oldValue, newValue)->{
            System.out.println("leftTableAddListener");
        });
        return  tableView;
    }

    private ObservableList<Standart2> getLeftTableData() {
        ObservableList<Standart2> observableList = standart2Models.get_data(connection);
        return observableList;
    }
    public TableColumn<Standart2, Integer> getStandart2TextColumn() {
        TableColumn<Standart2, Integer> id2Column = new TableColumn<>("Text");
        id2Column.setMinWidth(100);
        id2Column.setCellValueFactory(new PropertyValueFactory<>("text"));
        return id2Column;
    }
    public TableColumn<Standart2, Integer> getStandart2Id2Column() {
        TableColumn<Standart2, Integer> id2Column = new TableColumn<>("Odatiy \nhisob");
        id2Column.setMinWidth(100);
        id2Column.setCellValueFactory(new PropertyValueFactory<>("id2"));
        id2Column.setCellFactory(column -> {
            TableCell<Standart2, Integer> cell = new TableCell<Standart2, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        Hisob hisob = GetDbData.getHisob(item);
                        if (hisob != null) {
                            setText(hisob.getText());
                        } else {
                            setText("");
                        }
                    }
                }
            };
            return cell;
        });
        return id2Column;
    }

    private GridPane yangiGridPaneJadval() {
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(padding));
        HisobBox hisobBox = yangiHisobBox();
        TextField textField = new TextField();
        textField.setPromptText("Yordamchi hisob nomi");
        Button qaydEtButton = new Button("Qayd et");
        Button cancelButton = new Button("<<");
        qaydEtButton.setMaxWidth(2000);
        qaydEtButton.setPrefWidth(150);
        cancelButton.setMaxWidth(2000);
        cancelButton.setPrefWidth(150);
        HBox hBox = new HBox();
        hBox.getChildren().addAll(cancelButton, qaydEtButton);
        HBox.setHgrow(qaydEtButton, Priority.ALWAYS);
        HBox.setHgrow(cancelButton, Priority.ALWAYS);
        HBox.setHgrow(hBox, Priority.ALWAYS);


        int rowIndex = 0;
        gridPane.add(textField, 0, rowIndex, 1, 1);

        rowIndex++;
        gridPane.add(hisobBox, 0, rowIndex, 1, 1);
        rowIndex++;
        gridPane.add(hBox, 0, rowIndex, 1, 1);
        return gridPane;
    }

    private HisobBox yangiHisobBox() {
        HisobBox hisobBox = new HisobBox(connection, user);
        EventHandler<AutoCompletionBinding.AutoCompletionEvent<Hisob>> bindingHandler = new EventHandler<AutoCompletionBinding.AutoCompletionEvent<Hisob>>() {
            @Override
            public void handle(AutoCompletionBinding.AutoCompletionEvent<Hisob> event) {
                Hisob newValue = event.getCompletion();
                if (newValue != null) {
                    System.out.println("Bismillah");
                    hisobBox.setHisob(newValue);
                    System.out.println(hisobBox.getHisob().getText());
                }
            }
        };
        AutoCompletionBinding<Hisob> hisobBinding = hisobBox.getHisobBinding();
        hisobBinding.setOnAutoCompleted(bindingHandler);
        return hisobBox;
    }
}
