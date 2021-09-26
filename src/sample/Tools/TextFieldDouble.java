package sample.Tools;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

import java.text.NumberFormat;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class TextFieldDouble {
    Pattern validEditingState = Pattern.compile("-?(([1-9][0-9]*)|0)?(\\.[0-9]*)?");

    UnaryOperator<TextFormatter.Change> filter = c -> {
        String text = c.getControlNewText();
        if (validEditingState.matcher(text).matches()) {
            return c ;
        } else {
            return null ;
        }
    };

    StringConverter<Double> converter = new StringConverter<Double>() {

        @Override
        public Double fromString(String s) {
            if (s.isEmpty() || "-".equals(s) || ".".equals(s) || "-.".equals(s)) {
                return 0.0 ;
            } else {
                return Double.valueOf(s);
            }
        }


        @Override
        public String toString(Double d) {
            return d.toString();
        }
    };
    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
    TextFormatter<Double> textFormatter = new TextFormatter<>(converter, 0.0, filter);

    public TextField getFormattedTextField(TextField textField) {
        textField.setTextFormatter(textFormatter);
        return textField;
    }

    public TextFormatter<Double> getTextFormatter() {
        return textFormatter;
    }
}
