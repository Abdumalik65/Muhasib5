package sample.Data;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import sample.Model.KursModels;
import sample.Tools.MoneyShow;
import sample.Tools.PathToImageView;
import sample.Tools.StringNumberUtils;
import sample.Tools.TextFieldButton;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.Date;

public class Tolov {
    Valuta valuta;
    HisobKitob naqd;
    HisobKitob bank;
    HisobKitob qaytim;
    HisobKitob chegirma;
    HisobKitob jami;
    TextFieldButton naqdTextFieldButton;
    TextFieldButton bankTextFieldButton;
    TextFieldButton qaytimTextFieldButton;
    TextFieldButton chegirmaTextFieldButton;
    Label jamiLabel;
    DecimalFormat decimalFormat = new MoneyShow();
    Connection connection;
    User user;
    Date dateTime;
    KursModels kursModels = new KursModels();

    public Tolov() {

    }

    public Tolov(Valuta valuta, HisobKitob naqd, HisobKitob bank, HisobKitob qaytim, HisobKitob chegirma) {
        this.valuta = valuta;
        this.naqd = naqd;
        this.bank = bank;
        this.qaytim = qaytim;
        this.chegirma = chegirma;
    }

    public Tolov(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
    }

    public Tolov(Valuta valuta, Connection connection, User user) {
        this.valuta = valuta;
        this.connection = connection;
        this.user = user;
        Kurs kurs = kursModels.getKurs(connection, valuta.getId(), new Date(), "sana desc");
        yangiHisobKitob(valuta, kurs);
        naqdTextFieldButton = yangiTextFieldButton(naqd);
        bankTextFieldButton = yangiTextFieldButton(bank);
        qaytimTextFieldButton = yangiTextFieldButton(qaytim);
        chegirmaTextFieldButton = yangiTextFieldButton(chegirma);
        jamiLabel = yangiJamiLabel(jami);
    }

    private Label yangiJamiLabel(HisobKitob jami) {
        Label label = new Label();
        HBox.setHgrow(label, Priority.ALWAYS);
        return label;
    }

    public Valuta getValuta() {
        return valuta;
    }

    public void yangiHisobKitob(Valuta valuta, Kurs kurs) {
        this.valuta = valuta;
        naqd = new HisobKitob(
                1, 0, 0, 7, 0, 0, valuta.getId(), 0, kurs.getKurs(), "", 1d, 0d, 0, "", user.getId(), new Date());
        bank = new HisobKitob(
                2, 0, 0, 7, 0, 0, valuta.getId(), 0, kurs.getKurs(), "", 1d, 0d, 0, "", user.getId(), new Date());
        qaytim = new HisobKitob(
                3, 0, 0, 7, 0, 0, valuta.getId(), 0, kurs.getKurs(), "", 1d, 0d, 0, "", user.getId(), new Date());
        chegirma = new HisobKitob(
                4, 0, 0, 7, 0, 0, valuta.getId(), 0, kurs.getKurs(), "", 1d, 0d, 0, "", user.getId(), new Date());
        jami = new HisobKitob(
                5, 0, 0, 7, 0, 0, valuta.getId(), 0, kurs.getKurs(), "", 1d, 0d, 0, "", user.getId(), new Date());
    }

    private TextFieldButton yangiTextFieldButton(HisobKitob hisobKitob) {
        Kurs kurs = kursModels.getKurs(connection, valuta.getId(), new Date(), "sana desc");
        yangiHisobKitob(valuta, kurs);
        TextFieldButton textFieldButton = new TextFieldButton();
        Button button = textFieldButton.getPlusButton();
        String imageNameString = "/sample/images/Icons/calculator.png";
        button.setGraphic(new PathToImageView(imageNameString).getImageView());
        TextField textField = textFieldButton.getTextField();
        textField.setPromptText(valuta.getValuta());
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Double aDouble = StringNumberUtils.textToDouble(newValue);
                hisobKitob.setNarh(aDouble);
            }
        });
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue) {
                    textField.selectAll();
                } else {
                    if (naqd.getNarh()==0) {
                        textField.setText("");
                    } else {
                        textField.setText(decimalFormat.format(hisobKitob.getNarh()));
                    }
                }
            }
        });
        return textFieldButton;
    }

    public void setValuta(Valuta valuta) {
        this.valuta = valuta;
    }

    public HisobKitob getNaqd() {
        return naqd;
    }

    public void setNaqd(HisobKitob naqd) {
        this.naqd = naqd;
    }

    public HisobKitob getBank() {
        return bank;
    }

    public void setBank(HisobKitob bank) {
        this.bank = bank;
    }

    public HisobKitob getQaytim() {
        return qaytim;
    }

    public void setQaytim(HisobKitob qaytim) {
        this.qaytim = qaytim;
    }

    public HisobKitob getChegirma() {
        return chegirma;
    }

    public void setChegirma(HisobKitob chegirma) {
        this.chegirma = chegirma;
    }

    public HisobKitob getJami() {
        return jami;
    }

    public void setJami(HisobKitob jami) {
        this.jami = jami;
    }

    public TextFieldButton getNaqdTextFieldButton() {
        return naqdTextFieldButton;
    }

    public void setNaqdTextFieldButton(TextFieldButton naqdTextFieldButton) {
        this.naqdTextFieldButton = naqdTextFieldButton;
    }

    public TextFieldButton getBankTextFieldButton() {
        return bankTextFieldButton;
    }

    public void setBankTextFieldButton(TextFieldButton bankTextFieldButton) {
        this.bankTextFieldButton = bankTextFieldButton;
    }

    public TextFieldButton getQaytimTextFieldButton() {
        return qaytimTextFieldButton;
    }

    public void setQaytimTextFieldButton(TextFieldButton qaytimTextFieldButton) {
        this.qaytimTextFieldButton = qaytimTextFieldButton;
    }

    public TextFieldButton getChegirmaTextFieldButton() {
        return chegirmaTextFieldButton;
    }

    public void setChegirmaTextFieldButton(TextFieldButton chegirmaTextFieldButton) {
        this.chegirmaTextFieldButton = chegirmaTextFieldButton;
    }

    public TextField getChegirmaTextField() {
        return chegirmaTextFieldButton.getTextField();
    }

    public Label getJamiLabel() {
        return jamiLabel;
    }

    public void setJamiLabel(Label jamiLabel) {
        this.jamiLabel = jamiLabel;
    }

    @Override
    public String toString() {
        return valuta.getValuta();
    }
}
