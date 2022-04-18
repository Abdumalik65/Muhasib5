package sample.Tools;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import java.text.DecimalFormat;

public class ShowCoordinatesNode2 extends StackPane {

public ShowCoordinatesNode2(String x, double y) {

    final Label label = createDataThresholdLabel(x, y);

    setOnMouseEntered(new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
                setScaleX(1);
                setScaleY(1);
                getChildren().setAll(label);
                setCursor(Cursor.NONE);
                toFront();
        }
    });
    setOnMouseExited(new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
                getChildren().clear();
                setCursor(Cursor.CROSSHAIR);
        }
    });
}

private Label createDataThresholdLabel(String x, double y) {
    DecimalFormat df = new MoneyShow();
    final Label label = new Label(x + "\n" + df.format(y));
    label.getStyleClass().addAll("default-color0", "chart-line-symbol", "chart-series-line");
    label.setStyle("-fx-font-size: 10; -fx-font-weight: bold;");
    label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
    return label;
}
}
