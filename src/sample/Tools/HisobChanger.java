package sample.Tools;

import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import sample.Config.MySqlDBGeneral;
import sample.Controller.HisobController;
import sample.Data.Hisob;
import sample.Data.User;
import sample.Enums.ServerType;

import java.sql.Connection;

public class HisobChanger extends HBox {
    Connection connection;
    User user = new User(1, "admin", "", "admin");
    ObservableList<Hisob> hisobObservableList;
    TextField textField = new TextField();
    Button plusButton = new Button();
    Hisob hisob;
    String imageNameString = "/sample/images/Icons/add.png";

    public HisobChanger() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        GetDbData.initData(connection);
        hisobObservableList = GetDbData.getHisobObservableList();
        ibtido();
    }

    public HisobChanger(Connection connection, User user, ObservableList<Hisob> hisobObservableList) {
        this.connection = connection;
        this.user = user;
        this.hisobObservableList = hisobObservableList;
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
        TextFields.bindAutoCompletion(textField, hisobObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Hisob> autoCompletionEvent) -> {
            hisob  = autoCompletionEvent.getCompletion();
            textField.setText(hisob.getText());
        });
    }

    public void initPlusButton() {
        plusButton.setGraphic(new PathToImageView(imageNameString).getImageView());
        plusButton.setOnAction(e -> {
            HisobController hisobController = new HisobController();
            hisobController.display(connection, user, hisobObservableList);
            if (hisobController.getDoubleClick()) {
                hisob = hisobController.getDoubleClickedRow();
            }

        });
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
}
