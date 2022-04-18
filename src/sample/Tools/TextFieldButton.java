package sample.Tools;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import sample.Data.Hisob;

import java.util.Collection;

public class TextFieldButton extends HBox {
    TextField textField = new TextField();
    Button plusButton = new Button();
    String imageNameString = "/sample/images/Icons/add.png";
    Object object;
    Integer objectId;
    Collection collection;

    public TextFieldButton() {
        ibtido();
    }

    public <T> TextFieldButton(Collection<T> collection) {
        this.collection = collection;
        ibtido();
        setAutoCompletion(collection);
    }



    public void ibtido() {
        initTextField();
        initPlusButton();
        initHBox();
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
        plusButton.setGraphic(new PathToImageView(imageNameString).getImageView());
    }

    public TextField getTextField() {
        return textField;
    }

    public void setTextField(TextField textField) {
        this.textField = textField;
    }

    public Button getPlusButton() {
        return plusButton;
    }

    public void setPlusButton(Button plusButton) {
        this.plusButton = plusButton;
    }

    public Integer getObjectId() {
        return objectId;
    }

    public void setObjectId(Integer objectId) {
        this.objectId = objectId;
    }
}
