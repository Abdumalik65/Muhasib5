package sample.Tools;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class HBoxTextFieldPlusButton extends HBox {
    TextField textField = new TextField();
    Button plusButton = new Button();
    String imageNameString = "/sample/images/Icons/add.png";
    Integer objectId;

    public HBoxTextFieldPlusButton() {
        ibtido();
    }

    public void ibtido() {
        initTextField();
        initPlusButton();
        initMethods();
        initHBox();
    }

    private void initMethods() {
    }

    public void initHBox() {
        getChildren().addAll(textField, plusButton);
    }


    public void initTextField() {
        HBox.setHgrow(textField, Priority.ALWAYS);
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
