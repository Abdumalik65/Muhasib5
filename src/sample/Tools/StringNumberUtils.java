package sample.Tools;

import javafx.scene.control.TextField;

public class StringNumberUtils {
    static public double yaxlitla(double son, int daraja) {
        double darajalanganSon = Math.pow(10, daraja);
        double natija = son/darajalanganSon;
        double roundNatija = Math.round(natija)*darajalanganSon;
//        System.out.println(new MoneyShow().format(roundNatija));
        return roundNatija;
    }

    public static boolean isNumeric(String str) {
        String string = replaceSymbols(str);
        try {
            Double.parseDouble(string);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    public static String replaceSymbols(String oldText) {
        String newText = "";
        newText = oldText.replaceAll(",", ".");
        newText = oldText.replaceAll(" ", "");
        return newText;
    }

    public static Double getDoubleFromTextField(TextField textField) {
        Double doubleValue = 0d;
        String textValue = textField.getText();
        doubleValue = textToDouble(textValue);
        return doubleValue;
    }

    public static Double textToDouble(String textValue) {
        Double doubleValue = 0d;
        if (textValue != null) {
            textValue = textValue.replaceAll(",", ".");
            textValue = textValue.replaceAll(" ", "");
            doubleValue = textValue.isEmpty() ? 0d : Double.valueOf(textValue);
        }
        return doubleValue;
    }
    public static String padLeft(String s, int size,String pad) {
        StringBuilder builder = new StringBuilder(s);
        builder = builder.reverse(); // reverse initial string
        while(builder.length()<size) {
            builder.append(pad); // append at end
        }
        return builder.reverse().toString(); // reverse again!
    }
}
