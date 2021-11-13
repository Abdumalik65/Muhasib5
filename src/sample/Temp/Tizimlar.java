package sample.Temp;

import javafx.application.Application;
import javafx.application.Platform;
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
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import sample.Config.MySqlDBGeneral;
import sample.Controller.TovarController1;
import sample.Data.BarCode;
import sample.Data.HisobKitob;
import sample.Data.Standart;
import sample.Data.User;
import sample.Enums.ServerType;
import sample.Tools.*;

import java.sql.Connection;

public class Tizimlar extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    SplitPane centerPane = new SplitPane();
    VBox rightSplitPane = new VBox();
    VBox centerSplitPane = new VBox();
    VBox leftSplitPane = new VBox();

    Tugmachalar tizimTugmachalar = new Tugmachalar();
    HBoxTextFieldPlusButton tovarHBox = new HBoxTextFieldPlusButton();
    TableView<Standart> tizimTableView;
    ObservableList<Standart> tovarObservableList;
    TableView<HisobKitob> kirimTableView;
    TableView<HisobKitob> chiqimTableView;

    Connection connection;
    User user;
    Standart tovar;
    int padding = 3;


    public static void main(String[] args) {
        launch(args);
    }

    public Tizimlar() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
        ibtido();
    }

    public Tizimlar(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        ibtido();
    }

    private void ibtido() {
        initTopPane();
        initLeftSplitPane();
        initCenterSplitPane();
        initRightSplitPane();
        initCenterPane();
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

    private void initStage(Stage primaryStage) {
        scene = new Scene(borderpane);
        scene.setUserAgentStylesheet("sample/Styles/caspian.css");
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        stage = primaryStage;
        stage.setTitle("Ishlab-chiqarish tizimlari");
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
//        stage.initStyle(StageStyle.UNDECORATED);
        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
            stage.close();
        });
        stage.setScene(scene);
    }

    private void initTopPane() {}

    private void initLeftSplitPane() {
        leftSplitPane.setPadding(new Insets(padding));
        SetHVGrow.VerticalHorizontal(leftSplitPane);
        leftSplitPane.getChildren().addAll(getChiqimTableView());
    }

    private void initCenterSplitPane() {
        centerSplitPane.setPadding(new Insets(padding));
        SetHVGrow.VerticalHorizontal(centerSplitPane);
        centerSplitPane.getChildren().addAll(getKirimTableView());
    }

    private void initRightSplitPane() {
        rightSplitPane.setPadding(new Insets(padding));
        SetHVGrow.VerticalHorizontal(rightSplitPane);
        rightSplitPane.getChildren().addAll(tizimTugmachalar, getTizimTableView());
    }

    private void initCenterPane() {
        SetHVGrow.VerticalHorizontal(centerPane);
        centerPane.setDividerPositions(.4, .8);
        centerPane.getItems().addAll(leftSplitPane, centerSplitPane, rightSplitPane);
    }

    private void initBottomPane() {}

    private void initBorderPane() {
        borderpane.setCenter(centerPane);
    }

    private TableView<HisobKitob> getKirimTableView() {
        TableView<HisobKitob> hisobKitobTableView = new TableView<>();
        SetHVGrow.VerticalHorizontal(hisobKitobTableView);
        GetTableView2 getTableView2 = new GetTableView2();
        hisobKitobTableView.getColumns().addAll(
                getTableView2.getIzoh2Column(),
                getTableView2.getAdadColumn(),
                getTableView2.getNarhColumn()
        );
        return hisobKitobTableView;
    }

    private TableView<HisobKitob> getChiqimTableView() {
        TableView<HisobKitob> hisobKitobTableView = new TableView<>();
        SetHVGrow.VerticalHorizontal(hisobKitobTableView);
        GetTableView2 getTableView2 = new GetTableView2();
        hisobKitobTableView.getColumns().addAll(
                getTableView2.getIzoh2Column(),
                getTableView2.getAdadColumn(),
                getTableView2.getNarhColumn()
        );
        return hisobKitobTableView;
    }

    private TableView<Standart> getTizimTableView() {
        TableView<Standart> standartTableView = new TableView<>();
        SetHVGrow.VerticalHorizontal(standartTableView);
        standartTableView.getColumns().addAll(getTizimIdColumn(), getTizimTextColumn());
        return standartTableView;
    }

    private TableColumn<Standart, Integer> getTizimIdColumn() {
        TableColumn<Standart, Integer> column = new TableColumn<>("id");
        column.setCellValueFactory(new PropertyValueFactory<>("id"));
        return column;
    }

    private TableColumn<Standart, String> getTizimTextColumn() {
        TableColumn<Standart, String> column = new TableColumn<>("text");
        column.setCellValueFactory(new PropertyValueFactory<>("text"));
        return column;
    }
    private void initTovarHBox() {
        HBox.setHgrow(tovarHBox, Priority.ALWAYS);
        TextField textField = tovarHBox.getTextField();
        TextFields.bindAutoCompletion(textField, tovarObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Standart> autoCompletionEvent) -> {
            Standart newValue = autoCompletionEvent.getCompletion();
            if (newValue != null) {
                tovar = newValue;
//                tovarniYangila(tovar);
            }
        });

        Button addButton = tovarHBox.getPlusButton();
        addButton.setOnAction(event -> {
//                tovar = newValue;
//                textField.setText(tovar.getText());
//                tovarniYangila(tovar);
        });
    }

}
