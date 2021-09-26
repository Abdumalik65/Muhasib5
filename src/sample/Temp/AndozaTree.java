package sample.Temp;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.Data.User;
import sample.Tools.SetHVGrow;

import java.sql.Connection;

public class AndozaTree extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    TreeView treeView = new TreeView();

    Connection connection;
    User user = new User(1, "admin", "", "admin");
    int padding = 3;


    public static void main(String[] args) {
        launch(args);
    }

    public AndozaTree() {
        ibtido();
    }

    public AndozaTree(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        ibtido();
    }

    private void ibtido() {
        initTopPane();
        initLeftPane();
        initRightPane();
        initCenterPane();
        initBorderPane();
        initBottomPane();
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
    }

    private void initTreeView() {
        treeView.setPadding(new Insets(padding));
        TreeItem treeItem1 = new TreeItem("Birinchi");
        TreeItem treeItem2 = new TreeItem("Ikkinchi");
        TreeItem treeItem3 = new TreeItem("Uchinchi");
        TreeItem treeItem4 = new TreeItem("To`rtinchi");
        TreeItem treeItem5 = new TreeItem("Beshinchi");
        TreeItem rootTreeItem = new TreeItem("Asosiy");
        rootTreeItem.getChildren().addAll(treeItem1, treeItem2, treeItem3, treeItem4, treeItem5);
        treeView.setRoot(rootTreeItem);
        treeView.setShowRoot(false);
        treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
        });
    }

    private void initCenterPane() {
    }

    private void initRightPane() {
        initTreeView();
        borderpane.setRight(treeView);
    }

    private void initBottomPane() {}

    private void initBorderPane() {
        borderpane.setPadding(new Insets(padding));
        SetHVGrow.VerticalHorizontal(borderpane);
    }

    private void initStage(Stage primaryStage) {
        scene = new Scene(borderpane);
        scene.setUserAgentStylesheet("sample/Styles/caspian.css");
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        stage = primaryStage;
        stage.setTitle("Andoza");
        stage.setX(bounds.getMinX() - 3);
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth() + 7);
        stage.setHeight(bounds.getHeight() + 6);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
    }
}
