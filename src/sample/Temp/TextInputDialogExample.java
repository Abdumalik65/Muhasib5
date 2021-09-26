package sample.Temp;

import java.util.Optional;
 
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
 
public class TextInputDialogExample extends Application {
 
    private Label label;
 
    private void showInputTextDialog() {
 
        TextInputDialog dialog = new TextInputDialog("Tran");
 
        dialog.setTitle("o7planning");
        dialog.setHeaderText("Enter your name:");
        dialog.setContentText("Name:");
 
        Optional<String> result = dialog.showAndWait();
 
            result.ifPresent(name -> {
                this.label.setText(name);
            });
    }
 
    @Override
    public void start(Stage stage) {
 
        VBox root = new VBox();
        root.setPadding(new Insets(10));
        root.setSpacing(10);
 
        this.label = new Label();
 
        Button button = new Button("Enter your name");
 
        button.setOnAction(new EventHandler<ActionEvent>() {
 
            @Override
            public void handle(ActionEvent event) {
                showInputTextDialog();
            }
        });
 
        root.getChildren().addAll(button, label);
 
        Scene scene = new Scene(root, 450, 250);
        stage.setTitle("JavaFX TextInputDialog (o7planning.org)");
        stage.setScene(scene);
 
        stage.show();
 
    }
 
    public static void main(String args[]) {
        launch(args);
    }
 
}