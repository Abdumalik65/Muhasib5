package sample.Temp;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class WordLikeMenuButton extends Application {

    @Override
    public void start(Stage primaryStage) {
        MenuButton menuButton = new MenuButton();
        menuButton.getItems().addAll(
                Stream.of("Info", "New", "Open", "Save", "Save As", "Print", "Share", "Export", "Close")
                    .map(MenuItem::new).collect(Collectors.toList()));
        BorderPane root = new BorderPane(null, menuButton, null, null, null);
        Scene scene = new Scene(root, 350, 75);
        scene.getStylesheets().add("style2020.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}