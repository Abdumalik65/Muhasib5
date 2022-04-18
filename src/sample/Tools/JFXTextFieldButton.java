package sample.Tools;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.EventHandler;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import sample.Data.Hisob;

import java.util.Collection;

public class JFXTextFieldButton extends HBox {
    DoubleTextBox doubleTextBox = new DoubleTextBox();
    JFXTextField textField = new JFXTextField();
    JFXButton plusButton = new JFXButton();
    String imageNameString = "/sample/images/Icons/calculator.png";
    Object object;
    Integer objectId;
    Collection collection;

    public JFXTextFieldButton() {
        ibtido();
    }
    public JFXTextFieldButton(DoubleTextBox doubleTextBox) {
        this.doubleTextBox = doubleTextBox;
        ibtido();
    }




    public <T> JFXTextFieldButton(Collection<T> collection) {
        this.collection = collection;
        ibtido();
        setAutoCompletion(collection);
    }



    public void ibtido() {
        initDoubleTextBox();
        initTextField();
        initPlusButton();
        initHBox();
    }

    private void initDoubleTextBox() {
    }

    public void initHBox() {
        getChildren().addAll(textField, plusButton);
    }


    public void initTextField() {
        HBox.setHgrow(textField, Priority.ALWAYS);
    }

    public <T> void setAutoCompletion(Collection<T> collection) {
        TextFields.bindAutoCompletion(textField, collection).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<T> autoCompletionEvent) -> {
            Object newValue = autoCompletionEvent.getCompletion();
            if (newValue != null) {
                object = newValue;
            }
        });
    }

    public void setButtonEvent(EventHandler eventHandler) {
        plusButton.setOnAction(eventHandler);
    }

    public void initPlusButton() {
        plusButton.setGraphic(new PathToImageView(imageNameString, 24, 24).getImageView());
    }

    public JFXTextField getTextField() {
        return textField;
    }

    public void setTextField(JFXTextField textField) {
        this.textField = textField;
    }

    public JFXButton getPlusButton() {
        return plusButton;
    }

    public void setPlusButton(JFXButton plusButton) {
        this.plusButton = plusButton;
    }

    public Integer getObjectId() {
        return objectId;
    }

    public void setObjectId(Integer objectId) {
        this.objectId = objectId;
    }

    public DoubleTextBox getDoubleTextBox() {
        return doubleTextBox;
    }

    public void setDoubleTextBox(DoubleTextBox doubleTextBox) {
        this.doubleTextBox = doubleTextBox;
    }
}
