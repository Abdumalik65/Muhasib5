package sample.Tools;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class MoneyShow extends DecimalFormat {
    String pattern = "###,###,###,###,###.##";
    DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance();

    public MoneyShow() {
        applyPattern(pattern);
        decimalFormatSymbols.setGroupingSeparator(' ');
        decimalFormatSymbols.setDecimalSeparator('.');
        setDecimalFormatSymbols(decimalFormatSymbols);
    }
}
