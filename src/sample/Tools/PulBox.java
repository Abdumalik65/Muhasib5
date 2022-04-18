package sample.Tools;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import sample.Config.MySqlDBGeneral;
import sample.Controller.HisobController;
import sample.Controller.ValutaController;
import sample.Data.Hisob;
import sample.Data.User;
import sample.Data.Valuta;
import sample.Enums.ServerType;
import sample.Model.HisobModels;
import sample.Model.ValutaModels;

import java.sql.Connection;

public class PulBox extends HBox {
    TextField textField = new TextField();
    Button plusButton = new Button();
    String imageNameString = "/sample/images/Icons/add.png";
    Valuta valuta;
    ValutaModels valutaModels = new ValutaModels();
    ObservableList<Valuta> observableList;
    Connection connection;
    User user;
    EventHandler<ActionEvent> eventHandler;

    public PulBox() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
        ibtido();
    }
    public PulBox(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        GetDbData.initData(connection);
        ibtido();
    }


    public void ibtido() {
        initData();
        initHBox();
    }

    private void initData() {
        observableList = valutaModels.get_data(connection);
    }

    public void initHBox() {
        setAlignment(Pos.CENTER);
        textField = initTextField();
        plusButton = initPlusButton();
        getChildren().addAll(textField, plusButton);
    }


    public TextField initTextField() {
        TextField textField = new TextField();
        HBox.setHgrow(textField, Priority.ALWAYS);
        refreshAutoCompletion(observableList, textField);
        return textField;
    }

    public void refreshAutoCompletion(ObservableList<Valuta> observableList, TextField textField) {
        this.textField = textField;
        TextFields.bindAutoCompletion(textField, observableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Valuta> autoCompletionEvent) -> {
            Valuta newValue = autoCompletionEvent.getCompletion();
            if (newValue != null) {
                valuta = newValue;
            }
        });
    }

    public Button initPlusButton() {
        Button plusButton = new Button("");
        eventHandler = initEventHandler();
        plusButton.setGraphic(new PathToImageView(imageNameString).getImageView());
        plusButton.setOnAction(eventHandler);
        return plusButton;
    }

    private EventHandler<ActionEvent> initEventHandler() {
        EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Valuta newValuta = addValuta();
                if (newValuta != null) {
                    valuta = newValuta;
                    textField.setText(valuta.getValuta());
                }
            }
        };
        return eventHandler;
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
    private Valuta addValuta() {
        Valuta valuta = null;
        ValutaController valutaController = new ValutaController();
        valutaController.display(connection, user);
        if (valutaController.getDoubleClick()) {
            valuta = valutaController.getDoubleClickedRow();
        }
        return valuta;
    }

    public void refresh() {
        initHBox();
    }

    public ObservableList<Valuta> getObservableList() {
        return observableList;
    }

    public void setObservableList(ObservableList<Valuta> observableList) {
        this.observableList = observableList;
        refreshAutoCompletion(observableList, textField);
    }

    public EventHandler<ActionEvent> getEventHandler() {
        return eventHandler;
    }

    public void setEventHandler(EventHandler<ActionEvent> eventHandler) {
        this.eventHandler = eventHandler;
        plusButton = initPlusButton();
    }

    public Valuta getValuta() {
        return valuta;
    }

    public void setValuta(Valuta valuta) {
        textField.setText(valuta.getValuta());
        this.valuta = valuta;
    }
}
