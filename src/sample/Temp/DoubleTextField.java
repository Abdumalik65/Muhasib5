package sample.Temp;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

import java.text.NumberFormat;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class DoubleTextField {
    static Pattern validEditingState = Pattern.compile("-?(([1-9][0-9]*)|0)?(\\.[0-9]*)?");

    static UnaryOperator<TextFormatter.Change> filter = c -> {
        String text = c.getControlNewText();
        if (validEditingState.matcher(text).matches()) {
            return c ;
        } else {
            return null ;
        }
    };

    static StringConverter<Double> converter = new StringConverter<Double>() {

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
    static NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
    static TextFormatter<Double> textFormatter = new TextFormatter<>(converter, 0.0, filter);

    public static TextField getFormattedTextField(TextField textField) {
        textField.setTextFormatter(textFormatter);
        return textField;
    }
}
