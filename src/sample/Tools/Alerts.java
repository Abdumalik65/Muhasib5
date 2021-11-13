package sample.Tools;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import sample.Data.BarCode;
import sample.Data.QaydnomaData;
import sample.Data.Standart;

import java.text.DecimalFormat;
import java.util.Optional;

public class Alerts {

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
    public static boolean isNumericAlert(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Diqqat");
//            alert.setContentText();
            alert.setHeaderText("Noto`g`ri ma`lumot kiritildi");
            alert.showAndWait();
            return false;
        }
    }

    public static boolean isNumericAlert(String str, boolean showAlert) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            if (showAlert) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Diqqat");
                alert.setHeaderText("Noto`g`ri ma`lumot kiritildi");
                alert.showAndWait();
            }
            return false;
        }
    }

    public static boolean printerNotReadyAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Diqqat");
        alert.setContentText("");
        alert.setHeaderText("Printer ulanmagan");
        alert.showAndWait();
        return false;
    }

    public static boolean hisoblarBirXilAlert(String kirim, String chiqim) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Diqqat");
        alert.setHeaderText("Kirim hisobi: " + kirim + "\nChiqim hisobi: " + chiqim);
        alert.setContentText("Hisoblardan biri o`zgartirilsin");
/*
        ButtonType buttonType = alert.getDialogPane().getButtonTypes().get(0);
        Button button1 = (Button) alert.getDialogPane().lookupButton(buttonType);
        button1.setOnKeyReleased(event -> {
            if(event.getCode() == KeyCode.ENTER)
                alert.setResult(buttonType);
        });
*/
        alert.showAndWait();
        return false;
    }

    public static void showKamomat(Standart tovar, double adad, String barCode, double zaxira) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText("Tovar: " +tovar.getText() + "\nShtrix kod: " + barCode + "\nTalab etilgan adad: " + adad  + "\nZaxirada: " + zaxira  + "\nKamomat:  " + (adad - zaxira));
        alert.setContentText("Yetarsiz adad");
        alert.showAndWait();
    }

    public static void barCodeIsExist(String barCode) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(barCode + "\nBu shtrixkod bazada mavjud");
        alert.setContentText("Bazada mavjud bo`lmagan shtixkod kiriting");
        alert.showAndWait();
    }

    public static void AlertString(String string) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(string);
        alert.setContentText("");
        alert.showAndWait();
    }

    public static void barCodeNotExist(String barCode) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(barCode + "\nBu shtrixkod bazada mavjud emas");
        alert.setContentText("Bazada mavjud bo`lgan shtixkod kiriting");
        alert.showAndWait();
    }

    public static boolean nasiyagaSot(Double balanceDouble, String valutaString) {
        boolean davomEt = false;
        DecimalFormat decimalFormat = new MoneyShow();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getButtonTypes().removeAll(alert.getButtonTypes());
        ButtonType okButton = new ButtonType("Ha");
        ButtonType noButton = new ButtonType("Yo`q");
        alert.getButtonTypes().addAll(okButton, noButton);
        alert.setTitle("Diqqat !!!");
        alert.setHeaderText("To`lov to`liq emas.\n");
        alert.setContentText("Kamomat "+decimalFormat.format(balanceDouble)+" " +valutaString.trim()+".\nNasiyaga sotamizmi ???");
        Optional<ButtonType> option = alert.showAndWait();

        ButtonType buttonType = option.get();
        if (okButton.equals(buttonType)) {
            davomEt = true;
        }
        return davomEt;
    }

    public static boolean haYoq(String headerText, String contentText) {
        boolean davomEt = false;
        DecimalFormat decimalFormat = new MoneyShow();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getButtonTypes().removeAll(alert.getButtonTypes());
        ButtonType okButton = new ButtonType("Ha");
        ButtonType noButton = new ButtonType("Yo`q");
        alert.getButtonTypes().addAll(okButton, noButton);
        alert.setTitle("Diqqat !!!");
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        Optional<ButtonType> option = alert.showAndWait();

        ButtonType buttonType = option.get();
        if (okButton.equals(buttonType)) {
            davomEt = true;
        }
        return davomEt;
    }

    public static boolean xaridniOchir(QaydnomaData qaydnomaData) {
        boolean davomEt = false;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getButtonTypes().removeAll(alert.getButtonTypes());
        ButtonType okButton = new ButtonType("Ha");
        ButtonType noButton = new ButtonType("Yo`q");
        alert.getButtonTypes().addAll(okButton, noButton);
        alert.setTitle("Diqqat !!!");
        alert.setHeaderText(qaydnomaData.getHujjat() + " raqamli xarid butunlay o`chiriuladi\n");
        alert.setContentText("O`chirish kerakligiga ishonchingiz komilmi? ???");
        Optional<ButtonType> option = alert.showAndWait();
        ButtonType buttonType = option.get();
        if (okButton.equals(buttonType)) {
            davomEt = true;
        }
        return davomEt;
    }

    public static void losted() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Diqqat !!!");
        alert.setHeaderText("Server bilan aloqa yo`q");
        alert.setContentText("Sistema ishini yakunlaydi");
        alert.showAndWait();
        Platform.exit();
        System.exit(0);
    }

    public static void parseError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Diqqat !!!");
        alert.setHeaderText("Kiritilgan ma`lumotda xatolik bor");
        alert.showAndWait();
    }

    public static void kamomat(Standart tovar) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Diqqat qiling!!");
        alert.setHeaderText(tovar.getText() + " uchun narh tayinlanmagan. Shu sabab ro`yhatga qo`shilmaydi");
        alert.setContentText(tovar.getText() + " uchun narhni sistema noziri tayin etadi");
        alert.showAndWait();
    }
}
