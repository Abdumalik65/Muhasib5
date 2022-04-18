package sample.Data;


import com.jfoenix.controls.JFXTextField;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import sample.Tools.MoneyShow;
import sample.Tools.StringNumberUtils;

import java.text.DecimalFormat;

public class Tolov2 {
    Label valutaLabel;
    Valuta valuta;
    JFXTextField textField;
    HisobKitob hisobKitob;
    DecimalFormat decimalFormat = new MoneyShow();
    Font labelFont = Font.font("Arial", FontWeight.BOLD,12);
    Font textFieldFont = Font.font("Arial", FontWeight.BOLD,18);

    public Tolov2(Valuta valuta, HisobKitob hisobKitob) {
        this.valuta = valuta;
        this.hisobKitob = hisobKitob;
        valutaLabel = yangiValuta();
        textField = yangiTextField();
    }

    private Label yangiValuta() {
        Label label = new Label();
        label.setFont(labelFont);
        HBox.setHgrow(label, Priority.ALWAYS);
        label.setText(valuta.getValuta());
        return label;
    }

    private JFXTextField yangiTextField() {
        JFXTextField textField = new JFXTextField();
        textField.setFont(textFieldFont);
        HBox.setHgrow(textField, Priority.ALWAYS);
        textField.setPromptText(valuta.getValuta());
        textField.setAlignment(Pos.CENTER);
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (StringNumberUtils.isNumeric(textField.getText())) {
                    Double aDouble = StringNumberUtils.getDoubleFromTextField(textField);
                    hisobKitob.setNarh(aDouble);
                } else {
                    hisobKitob.setNarh(0d);
                }
            }
        });
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (!newValue) {
                    if (StringNumberUtils.isNumeric(textField.getText())) {
                        Double aDouble = StringNumberUtils.getDoubleFromTextField(textField);
                        textField.setText(decimalFormat.format(aDouble));
                    }
                }
            }
        });
        return textField;
    }

    public Label getValutaLabel() {
        return valutaLabel;
    }

    public void setValutaLabel(Label valutaLabel) {
        this.valutaLabel = valutaLabel;
    }

    public Valuta getValuta() {
        return valuta;
    }

    public void setValuta(Valuta valuta) {
        this.valuta = valuta;
    }

    public JFXTextField getTextField() {
        return textField;
    }

    public void setTextField(JFXTextField textField) {
        this.textField = textField;
    }

    public HisobKitob getHisobKitob() {
        return hisobKitob;
    }

    public void setHisobKitob(HisobKitob hisobKitob) {
        this.hisobKitob = hisobKitob;
        if (!hisobKitob.getNarh().equals(0)) {
            textField.setText(decimalFormat.format(hisobKitob.getNarh()));
        } else {
            textField.setText("");
        }
    }

    public Font getLabelFont() {
        return labelFont;
    }

    public void setLabelFont(Font labelFont) {
        this.labelFont = labelFont;
    }

    public Font getTextFieldFont() {
        return textFieldFont;
    }

    public void setTextFieldFont(Font textFieldFont) {
        this.textFieldFont = textFieldFont;
    }
}

