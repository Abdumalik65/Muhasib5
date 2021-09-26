package sample.Tools;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CustomWindow {
    Stage stage = new Stage();
    Scene scene;
    BorderPane borderpane = new BorderPane();
    HBox top = new HBox();
    VBox leftVBox = new VBox();
    VBox rightVBox = new VBox();
    MenuBar mainMenu;
    SplitPane splitPane = new SplitPane();
    Pane pane = new Pane();
    Label left_label = new Label("");
    Label right_label = new Label("");
    HBox bottom = new HBox();
    String stageTitle = "";
    Integer sceneWidth = 600;
    Integer sceneHeight = 400;
    Boolean stageResizable = true;
    Boolean doublePane = true;
    Boolean modalStage = true;
    int padding = 3;

    public CustomWindow() {}

    public CustomWindow(boolean doublePane, Integer sceneWidth, Integer sceneHeight) {
        this.doublePane = doublePane;
        this.sceneWidth = sceneWidth;
        this.sceneHeight = sceneHeight;
    }

    public void showAndWait() {
        init();
        stage.showAndWait();
    }

    public void show() {
        init();
        stage.show();
    }

    private void init() {
        stage.setTitle(stageTitle);
        stage.setResizable(stageResizable);
        stage.initModality(Modality.APPLICATION_MODAL);
        /*************************************************
         **                 Yuqori                      **
         *************************************************/
        mainMenu = new MenuBar();
        Menu menuFile = new Menu("File");
        Menu menuView = new Menu("View");
        Menu menuEdit = new Menu("Edit");
        Menu menuHelp = new Menu("Help");
        mainMenu.getMenus().addAll(menuFile, menuEdit, menuView, menuHelp);

        left_label.setPadding(new Insets(padding));
        right_label.setPadding(new Insets(padding));

        HBox.setHgrow(splitPane, Priority.ALWAYS);
        VBox.setVgrow(splitPane, Priority.ALWAYS);
        HBox.setHgrow(left_label, Priority.ALWAYS);
        HBox.setHgrow(pane, Priority.ALWAYS);
        HBox.setHgrow(right_label, Priority.NEVER);
        bottom.getChildren().addAll(left_label, pane, right_label);
        bottom.setAlignment(Pos.CENTER);
//        borderpane.setTop(mainMenu);
        splitPane.setPadding(new Insets(padding));
        if (doublePane) {
            splitPane.getItems().addAll(leftVBox, rightVBox);
            borderpane.setCenter(splitPane);
        } else {
            borderpane.setCenter(rightVBox);
        }
        borderpane.setBottom(bottom);
        scene = new Scene(borderpane, sceneWidth, sceneHeight);
        stage.setScene(scene);
    }

    public Stage getStage() {
        return stage;
    }
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() { return scene; }
    public void setScene(Scene scene) { this.scene = scene; }

    public BorderPane getBorderpane() {
        return borderpane;
    }
    public void setBorderpane(BorderPane borderpane) {
        this.borderpane = borderpane;
    }

    public MenuBar getMainMenu() { return mainMenu; }
    public void setMainMenu(MenuBar mainMenu) {
        this.mainMenu = mainMenu;
    }

    public SplitPane getSplitPane() {
        return splitPane;
    }
    public void setSplitPane(SplitPane splitPane) {
        this.splitPane = splitPane;
    }

    public Pane getPane() {
        return pane;
    }
    public void setPane(Pane pane) {
        this.pane = pane;
    }

    public Label getLeft_label() {
        return left_label;
    }
    public void setLeft_label(Label left_label) {
        this.left_label = left_label;
    }

    public Label getRight_label() {
        return right_label;
    }
    public void setRight_label(Label right_label) {
        this.right_label = right_label;
    }

    public HBox getBottom() {
        return bottom;
    }
    public void setBottom(HBox bottom) {
        this.bottom = bottom;
    }

    public String getStageTitle() {
        return stageTitle;
    }
    public void setStageTitle(String stageTitle) {
        this.stageTitle = stageTitle;
    }

    public Integer getSceneWidth() {
        return sceneWidth;
    }
    public void setSceneWidth(Integer sceneWidth) {
        this.sceneWidth = sceneWidth;
    }

    public Integer getSceneHeight() {
        return sceneHeight;
    }
    public void setSceneHeight(Integer sceneHeight) {
        this.sceneHeight = sceneHeight;
    }

    public Boolean getStageResizable() {
        return stageResizable;
    }
    public void setStageResizable(Boolean stageResizable) {
        this.stageResizable = stageResizable;
    }

    public HBox getTop() { return top; }
    public void setTop(HBox top) { this.top = top; }

    public VBox getLeftVBox() { return leftVBox; }
    public void setLeftVBox(VBox leftVBox) { this.leftVBox = leftVBox; }

    public VBox getRightVBox() { return rightVBox; }
    public void setRightVBox(VBox rightVBox) { this.rightVBox = rightVBox; }

    public Boolean getDoublePane() {
        return doublePane;
    }

    public void setDoublePane(Boolean doublePane) {
        this.doublePane = doublePane;
    }
}
