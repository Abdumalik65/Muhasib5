package sample.Tools;

import javafx.event.EventHandler;
import javafx.scene.control.Separator;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;


public class DoubleTextBox extends VBox {
    Text text1;
    Text text2;
    Separator separator = new Separator();

    public DoubleTextBox() {
        text1 = new Text("Text1");
        text2 = new Text("Text2");
        initText(text1);
        initText(text2);
        getChildren().addAll(text1, separator, text2);
    }

    public DoubleTextBox(Text text1, Text text2) {
        this.text1 = text1;
        this.text2 = text2;
        getChildren().addAll(text1, separator, text2);
    }

    private void initText(Text text) {
        text.setTextAlignment(TextAlignment.CENTER);
        SetHVGrow.VerticalHorizontal(text);
    }

}
