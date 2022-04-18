package sample.Temp;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.Config.MySqlDBGeneral;
import sample.Data.Standart;
import sample.Data.User;
import sample.Enums.ServerType;
import sample.Model.StandartModels;
import sample.Tools.GetDbData;
import sample.Tools.TableViewAndoza;

import java.sql.Connection;

public class DragDropTest extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    SplitPane centerPane = new SplitPane();
    VBox rightPane = new VBox();
    VBox leftPane = new VBox();
    TableView leftTable;
    TableView centerTable;
    TableView rightTable;

    ObservableList leftTableData;
    ObservableList centerTableData;
    ObservableList rightTableData;

    StandartModels standartModels = new StandartModels();

    Connection connection;
    User user;
    int padding = 3;


    public static void main(String[] args) {
        launch(args);
    }

    public DragDropTest() {
        connection = new MySqlDBGeneral(ServerType.REMOTE).getDbConnection();
        GetDbData.initData(connection);
        ibtido();
    }

    public DragDropTest(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        ibtido();
    }

    private void ibtido() {
        initTopPane();
        initLeftPane();
        initCenterPane();
        initRightPane();
        initBottomPane();
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

    private void initLeftPane() {
        leftPane.setPadding(new Insets(padding));
        HBox.setHgrow(leftPane, Priority.ALWAYS);
        VBox.setVgrow(leftPane, Priority.ALWAYS);
        leftTable = initLeftTable();
        leftPane.getChildren().add(leftTable);
    }

    private void initCenterPane() {
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        VBox.setVgrow(centerPane, Priority.ALWAYS);
        centerPane.getItems().addAll(leftPane, rightPane);
    }

    private void initRightPane() {
        rightPane.setPadding(new Insets(padding));
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        VBox.setVgrow(rightPane, Priority.ALWAYS);
        rightTable = initRightTable();
        rightPane.getChildren().add(rightTable);
    }

    private void initBottomPane() {}

    private void initBorderPane() {
        borderpane.setCenter(centerPane);
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Ikki panel");
        scene = new Scene(borderpane, 1000, 400);
        stage.setScene(scene);
    }

    private TableView initLeftTable() {
        TableView<Standart> tableView = new TableView<>();
        tableView.setItems(getLeftTableData());
        tableView.getColumns().addAll(idColumn(), textColumn());
        tableView.getSelectionModel().selectedItemProperty().addListener((observable ,oldValue, newValue)->{
            System.out.println("leftTableAddListener");
        });
        return  tableView;
    }

    private ObservableList<Standart> getLeftTableData() {
        standartModels.setTABLENAME("Tovar");
        ObservableList<Standart> observableList = standartModels.get_data(connection);

        return observableList;
    }
    private TableView initRightTable() {
        TableView<Standart> tableView = new TableView<>();
        tableView.setItems(getRightTableData());
        tableView.getColumns().addAll(idColumn(), textColumn());
        tableView.getSelectionModel().selectedItemProperty().addListener((observable ,oldValue, newValue)->{
            System.out.println("leftTableAddListener");
        });
        return  tableView;
    }

    private ObservableList<Standart> getRightTableData() {
        standartModels.setTABLENAME("Amal");
        ObservableList<Standart> observableList = FXCollections.observableArrayList();
        return observableList;
    }

    private TableColumn<Standart, Integer> idColumn() {
        TableColumn<Standart, Integer> idColumn = new TableColumn<>("№");
        idColumn.setMinWidth(15);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        return idColumn;
    }

    private TableColumn<Standart, String> textColumn() {
        TableColumn<Standart, String> textColumn = new TableColumn<>("Text");
        textColumn.setMinWidth(350);
        textColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
        textColumn.setCellFactory(TextFieldTableCell.<Standart> forTableColumn());
        textColumn.setOnEditCommit((TableColumn.CellEditEvent<Standart, String> event) -> {
            String newString = event.getNewValue();
            if (newString != null) {
                TablePosition<Standart, String> pos = event.getTablePosition();
                int row = pos.getRow();
                Standart standart = event.getTableView().getItems().get(row);
                standart.setText(newString);
                standartModels.update_data(connection, standart);
            }
        });
        return textColumn;
    }

    private void source() {
        Text source = new Text();
        source.setOnDragDetected(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                /* drag was detected, start drag-and-drop gesture*/
                System.out.println("onDragDetected");

                /* allow any transfer mode */
                Dragboard db = source.startDragAndDrop(TransferMode.ANY);

                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(source.getText());
                db.setContent(content);

                event.consume();
            }
        });

    }
}
