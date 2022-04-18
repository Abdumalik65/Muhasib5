package sample.Tools;

import javafx.geometry.Pos;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;


public class DoubleTextFieldBox extends VBox {
    TextField textField1;
    TextField textField2;
    Separator separator = new Separator();

    public DoubleTextFieldBox() {
        textField1 = new TextField("Text1");
        textField2 = new TextField("Text2");
        initText(textField1);
        initText(textField2);
        getChildren().addAll(textField1, separator, textField2);
    }

    public DoubleTextFieldBox(TextField textField1, TextField textField2) {
        this.textField1 = textField1;
        this.textField2 = textField2;
        getChildren().addAll(textField1, separator, textField2);
    }

    private void initText(TextField textField) {
        textField.setAlignment(Pos.CENTER);
        SetHVGrow.VerticalHorizontal(textField);
    }
}
