package sample.Tools;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.io.IOException;

public class HBoxComboBoxPlusButton extends  HBox{
    ComboBox comboBox = new ComboBox<>();
    Button plusButton = new Button();
    ObservableList comboData = FXCollections.observableArrayList();
    Integer objectId;

    public HBoxComboBoxPlusButton() {
        ibtido();
    }

    public HBoxComboBoxPlusButton(ObservableList<?> comboData) {
        this.comboData = comboData;
        setComboData(comboData);
        ibtido();
    }

    public void ibtido() {
        initComboBox();
        initPlusButton();
        initHBox();
    }

    public void initHBox() {
        getChildren().addAll(comboBox, plusButton);
    }

    public void initComboBox() {
        comboBox.setMaxWidth(1.7976931348623157E308);
        comboBox.setPrefWidth(150);
        HBox.setHgrow(comboBox, Priority.ALWAYS);
    }

    public void initPlusButton() {
        plusButton.setGraphic(new PathToImageView("/sample/images/Icons/add.png").getImageView());
   }

    public ComboBox<?> getComboBox() {
        return comboBox;
    }

    public void setComboBox(ComboBox<?> comboBox) {
        this.comboBox = comboBox;
        getChildren().removeAll(getChildren());
        getChildren().addAll(comboBox, plusButton);
    }

    public Button getPlusButton() {
        return plusButton;
    }

    public void setPlusButton(Button plusButton) {
        this.plusButton = plusButton;
    }

    public ObservableList<?> getComboData() {
        return comboData;
    }

    public void setComboData(ObservableList<?> comboData) {
        this.comboData = comboData;
        comboBox.setItems(comboData);
    }

    public Integer getObjectId() {
        return objectId;
    }

    public void setObjectId(Integer objectId) {
        this.objectId = objectId;
    }
}