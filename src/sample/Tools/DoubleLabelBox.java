package sample.Tools;

import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;


public class DoubleLabelBox extends VBox {
    Label text1;
    Label text2;
    Separator separator = new Separator();

    public DoubleLabelBox() {
        text1 = new Label("Text1");
        text2 = new Label("Text2");
        initText(text1);
        initText(text2);
        getChildren().addAll(text1, separator, text2);
    }

    public DoubleLabelBox(Label text1, Label text2) {
        this.text1 = text1;
        this.text2 = text2;
        getChildren().addAll(text1, separator, text2);
    }

    private void initText(Label text) {
        text.setTextAlignment(TextAlignment.CENTER);
        SetHVGrow.VerticalHorizontal(text);
    }
}
