package sample.Tools;

import impl.org.controlsfx.autocompletion.SuggestionProvider;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import sample.Config.MySqlDBGeneral;
import sample.Controller.HisobController;
import sample.Data.Hisob;
import sample.Data.Standart;
import sample.Data.User;
import sample.Enums.ServerType;
import sample.Model.HisobModels;

import java.sql.Connection;

public class HisobBox extends HBox {
    TextField textField = new TextField();
    Button plusButton = new Button();
    String imageNameString = "/sample/images/Icons/add.png";
    Hisob hisob;
    HisobModels hisobModels = new HisobModels();
    ObservableList<Hisob> observableList;
    Connection connection;
    User user;
    AutoCompletionBinding<Hisob> binding;
    EventHandler<ActionEvent> eventHandler;
    Font font = Font.getDefault();

    public HisobBox() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
        ibtido();
    }
    public HisobBox(Connection connection, User user) {
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
        observableList = hisobModels.get_data(connection);
    }

    public void initHBox() {
        setAlignment(Pos.CENTER);
        textField = initTextField();
        plusButton = initPlusButton();
        getChildren().removeAll(getChildren());
        getChildren().addAll(textField, plusButton);
    }


    public TextField initTextField() {
        textField = new TextField();
        textField.setFont(font);
        HBox.setHgrow(textField, Priority.ALWAYS);
        setAutoCompletion(textField, observableList);
        return textField;
    }
    public void setAutoCompletion(TextField textField, ObservableList<Hisob> observableList) {
        this.textField = textField;
        binding = TextFields.bindAutoCompletion(textField, getObservableList());
    }


    public Button initPlusButton() {
        Button plusButton = new Button("");
        plusButton.setMaxWidth(50);
        plusButton.setPrefWidth(12);
        plusButton.setMaxHeight(100);
        plusButton.setPrefHeight(10);
        eventHandler = initEventHandler();
        plusButton.setGraphic(new PathToImageView(imageNameString).getImageView());
        plusButton.setOnAction(eventHandler);
        return plusButton;
    }
    public void setNewList(ObservableList<Hisob> hisobtObservableList) {
        SuggestionProvider<Hisob> provider = SuggestionProvider.create(observableList);
        binding.dispose();
        provider.clearSuggestions();
        provider.addPossibleSuggestions(hisobtObservableList);
        binding = TextFields.bindAutoCompletion(textField, hisobtObservableList);
    }


    public EventHandler<ActionEvent> initEventHandler() {
        EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Hisob newHisob = addHisob();
                if (newHisob != null) {
                    hisob = newHisob;
                    textField.setText(hisob.getText());
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
    private Hisob addHisob() {
        Hisob hisob = null;
        HisobController hisobController = new HisobController();
        hisobController.display(connection, user);
        if (hisobController.getDoubleClick()) {
            hisob = hisobController.getDoubleClickedRow();
        }
        return hisob;
    }

    public void refresh() {
        initHBox();
    }

    public ObservableList<Hisob> getObservableList() {
        return observableList;
    }

    public void setObservableList(ObservableList<Hisob> observableList) {
        this.observableList = observableList;
    }

    public EventHandler<ActionEvent> getEventHandler() {
        return eventHandler;
    }

    public void setEventHandler(EventHandler<ActionEvent> eventHandler) {
        this.eventHandler = eventHandler;
        plusButton.setOnAction(eventHandler);
    }

    public Hisob getHisob() {
        return hisob;
    }

    public void setHisob(Hisob hisob) {
        this.hisob = hisob;
        textField.setText(hisob.getText());
    }

    public AutoCompletionBinding<Hisob> getHisobBinding() {
        return binding;
    }

    public void setBindingEvent(EventHandler<AutoCompletionBinding.AutoCompletionEvent<Hisob>> handler) {
        binding.setOnAutoCompleted(handler);
    }

    public void setBinding(AutoCompletionBinding<Hisob> binding) {
        this.binding = binding;
        setAutoCompletion(textField, observableList);
    }

    public AutoCompletionBinding<Hisob> getBinding() {
        return binding;
    }
}
