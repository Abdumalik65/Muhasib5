package sample.Controller;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import sample.Config.MySqlDBGeneral;
import sample.Data.Standart;
import sample.Data.User;
import sample.Enums.ServerType;
import sample.Tools.DasturlarRoyxati;
import sample.Tools.GetDbData;
import sample.Tools.SetHVGrow;
import sample.Tools.Tugmachalar;

import java.sql.Connection;
import java.util.Date;

public class YordamchiHisoblar extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    SplitPane splitPane = new SplitPane();
    VBox centerPane = new VBox();
    VBox rightPane = new VBox();
    VBox leftPane = new VBox();
    String centerTableName = "TranzitHisob";
    String rightTableName = "TranzitHisobGuruhi";
    TableView rootTable;
    TableView leftTable;
    TableView rightTable;

    ObservableList rootTableData;
    ObservableList leftTableData;
    ObservableList rightTableData;

    Tugmachalar leftButtons;
    Tugmachalar rightButtons;

    Connection connection;
    User user;
    int padding = 3;


    public static void main(String[] args) {
        launch(args);
    }

    public YordamchiHisoblar() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        GetDbData.initData(connection);
        this.user = GetDbData.getUser(1);
        ibtido();
    }

    public YordamchiHisoblar(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        String classSimpleName = getClass().getSimpleName();
        DasturlarRoyxati.dastur(connection, user, classSimpleName);
        ibtido();
    }

    private void ibtido() {
        initTopPane();
        initRootPane();
        initLeftPane();
        initRightPane();
        initBottomPane();
        initSplitPane();
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

    private void initRootPane() {
        leftPane.setPadding(new Insets(padding));
        HBox.setHgrow(leftPane, Priority.ALWAYS);
        VBox.setVgrow(leftPane, Priority.ALWAYS);
        leftPane.setMaxWidth(160);
        leftPane.setMinWidth(160);
        rootTable = initLeftTable();
        leftPane.getChildren().add(rootTable);
    }

    private void initLeftPane() {
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        VBox.setVgrow(centerPane, Priority.ALWAYS);
    }

    private void initRightPane() {
        rightPane.setPadding(new Insets(padding));
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        VBox.setVgrow(rightPane, Priority.ALWAYS);
    }

    private void initBottomPane() {}

    private void initSplitPane() {
        VBox.setVgrow(splitPane, Priority.ALWAYS);
        HBox.setHgrow(splitPane, Priority.ALWAYS);
        splitPane.getItems().addAll(leftPane, centerPane, rightPane);
    }

    private void initBorderPane() {
        SetHVGrow.VerticalHorizontal(borderpane);
        borderpane.setCenter(splitPane);
    }

    private void initStage(Stage primaryStage) {
        this.stage = primaryStage;
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        stage.setTitle("Hisobot");
        stage.setResizable(false);
        Scene scene = new Scene(borderpane);
        stage.setScene(scene);
    }

    private TableView initLeftTable() {
        TableView<Standart> tableView = new TableView();
        SetHVGrow.VerticalHorizontal(tableView);
        TableColumn<Standart, String> tableColumn = getTextColumn();
        tableColumn.setMinWidth(150);
        tableColumn.setMaxWidth(150);
        tableView.setItems(getRootTableData());
        tableView.getColumns().add(tableColumn);
        tableView.getSelectionModel().selectedItemProperty().addListener((observable ,oldValue, newValue)->{
            if (newValue != null) {
                System.out.println(newValue.getText());
            }
        });
        return  tableView;
    }

    private ObservableList<Standart> getRootTableData() {
        ObservableList<Standart> observableList = FXCollections.observableArrayList(
                new Standart(1, "Keldi-ketdi hisobi", user.getId(), new Date()),
                new Standart(2, "Foyda hisobi", user.getId(), new Date()),
                new Standart(3, "Zarar hisobi", user.getId(), new Date()),
                new Standart(4, "NDS hisobi", user.getId(), new Date()),
                new Standart(5, "Chegirma hisobi", user.getId(), new Date()),
                new Standart(6, "Bank hisobi", user.getId(), new Date()),
                new Standart(7, "Bank xarajatlari hisobi", user.getId(), new Date()),
                new Standart(8, "Bojxona solig`i hisobi", user.getId(), new Date()),
                new Standart(9, "Tasdiq kitish hisobi", user.getId(), new Date())
        );
        return observableList;
    }

    private TableColumn<Standart, String> getTextColumn() {
        TableColumn<Standart, String> textColumn = new TableColumn<>("Yordamchi hisoblar");
        textColumn.setCellValueFactory(new PropertyValueFactory<>("text"));

        textColumn.setCellFactory(tc -> {
            TableCell<Standart, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(textColumn.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell ;
        });
        return textColumn;
    }

    public Tugmachalar initLeftButtons() {
        Tugmachalar tugmachalar = new Tugmachalar();
        return tugmachalar;
    }

}
