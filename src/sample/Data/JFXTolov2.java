package sample.Data;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import sample.Tools.*;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.text.DecimalFormat;

public class JFXTolov2 {
    Label valutaLabel;
    Valuta valuta;
    JFXTextFieldButton jfxTextFieldButton;
    HisobKitob hisobKitob;
    DecimalFormat decimalFormat = new MoneyShow();
    Font labelFont = Font.font("Arial", FontWeight.BOLD,12);
    Font textFieldFont = Font.font("Arial", FontWeight.BOLD,18);

    public JFXTolov2(Valuta valuta, HisobKitob hisobKitob) {
        this.valuta = valuta;
        this.hisobKitob = hisobKitob;
        valutaLabel = yangiValuta();
        jfxTextFieldButton = yangiTextFieldButton();
    }

    private Label yangiValuta() {
        Label label = new Label();
        label.setFont(labelFont);
        HBox.setHgrow(label, Priority.ALWAYS);
        label.setText(valuta.getValuta());
        return label;
    }
    private JFXTextFieldButton yangiTextFieldButton() {
        JFXTextFieldButton jfxTextFieldButton = new JFXTextFieldButton(yangiDoubleTextBox());
        JFXTextField textField = jfxTextFieldButton.getTextField();
        textField.setFont(textFieldFont);
        HBox.setHgrow(textField, Priority.ALWAYS);
        textField.setPromptText(valuta.getValuta());
        textField.setAlignment(Pos.CENTER);
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (StringNumberUtils.isNumeric(newValue)) {
                    Double aDouble = StringNumberUtils.textToDouble(newValue);
                    hisobKitob.setNarh(aDouble);
                }
                if (newValue.isEmpty()) {
                    hisobKitob.setNarh(0d);
                }
            }
        });
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (!newValue) {
                    if (StringNumberUtils.isNumeric(textField.getText())) {
                        Double aDouble = StringNumberUtils.getDoubleFromTextField(textField);
                        if (!aDouble.equals(0d)) {
                            textField.setText(decimalFormat.format(aDouble));
                        } else {
                            textField.setText("");
                        }
                    }
                }
            }
        });

        return jfxTextFieldButton;
    }
    private DoubleTextBox yangiDoubleTextBox() {
        Text text1 = new Text(GetDbData.getHisob(hisobKitob.getHisob1()).getText());
        Text text2 = new Text(GetDbData.getHisob(hisobKitob.getHisob2()).getText());
        text1.setFill(Color.RED);
        text1.setWrappingWidth(80);
        text2.setWrappingWidth(80);
        text1.setStyle("-fx-text-alignment:justify;");
        text2.setFill(Color.BLUE);
        text2.setStyle("-fx-text-alignment:justify;");
        DoubleTextBox doubleTextBox = new DoubleTextBox(text1, text2);
        return doubleTextBox;
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
    public JFXTextFieldButton getJfxTextFieldButton() {
        return jfxTextFieldButton;
    }
    public void setJfxTextFieldButton(JFXTextFieldButton jfxTextFieldButton) {
        this.jfxTextFieldButton = jfxTextFieldButton;
    }
    public HisobKitob getHisobKitob() {
        return hisobKitob;
    }
    public void setHisobKitob(HisobKitob hisobKitob) {
        this.hisobKitob = hisobKitob;
        if (!hisobKitob.getNarh().equals(0)) {
            jfxTextFieldButton.getTextField().setText(decimalFormat.format(hisobKitob.getNarh()));
        } else {
            jfxTextFieldButton.getTextField().setText("");
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

