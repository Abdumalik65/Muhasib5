package sample.Tools;

import impl.org.controlsfx.autocompletion.SuggestionProvider;
import javafx.collections.FXCollections;
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
import sample.Controller.TovarController;
import sample.Data.Standart;
import sample.Data.User;
import sample.Enums.ServerType;
import sample.Model.StandartModels;

import java.sql.Connection;

public class BirlikBox extends HBox {
    TextField textField = new TextField();
    Button plusButton = new Button();
    String imageNameString = "/sample/images/Icons/add.png";
    Standart tovar;
    StandartModels tovarModels = new StandartModels("Tovar");
    ObservableList<Standart> observableList = FXCollections.observableArrayList();
    Connection connection;
    User user;
    EventHandler<ActionEvent> eventHandler;
    AutoCompletionBinding<Standart> binding;
    Font font = Font.getDefault();

    public BirlikBox() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
        ibtido();
    }
    public BirlikBox(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        ibtido();
    }

    public BirlikBox(Connection connection, User user, Font font) {
        this.connection = connection;
        this.user = user;
        this.font = font;
        ibtido();
    }

    public BirlikBox(ObservableList<Standart> observableList, User user) {
        this.observableList = observableList;
        this.user = user;
        initHBox();
    }

    public BirlikBox(ObservableList<Standart> observableList, User user, Font font) {
        this.observableList = observableList;
        this.user = user;
        this.font = font;
        initHBox();
    }

    public void ibtido() {
        initData();
        initHBox();
    }

    private void initData() {
        observableList = tovarModels.get_data(connection);
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

    public void setAutoCompletion(TextField textField, ObservableList<Standart> observableList) {
        this.textField = textField;
        binding = TextFields.bindAutoCompletion(textField, getObservableList());
    }

    public void setBindingEvent(EventHandler<AutoCompletionBinding.AutoCompletionEvent<Standart>> handler) {
        binding.setOnAutoCompleted(handler);
    }

    public void setNewList(ObservableList<Standart> standartObservableList) {
        SuggestionProvider<Standart> provider = SuggestionProvider.create(observableList);
        binding.dispose();
        provider.clearSuggestions();
        provider.addPossibleSuggestions(standartObservableList);
        binding = TextFields.bindAutoCompletion(textField, standartObservableList);
    }

    public Button initPlusButton() {
        plusButton = new Button("");
        plusButton.setFont(font);
        eventHandler = initEventHandler();
        plusButton.setGraphic(new PathToImageView(imageNameString).getImageView());
        plusButton.setOnAction(eventHandler);
        return plusButton;
    }

    private EventHandler<ActionEvent> initEventHandler() {
        EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Standart yangiTovar = addTovar();
                if (yangiTovar != null) {
                    tovar = yangiTovar;
                    textField.setText(tovar.getText());
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
    private Standart addTovar() {
        Standart tovar1 = null;
        TovarController tovarController = new TovarController(connection, user);
        tovarController.display();
        if (tovarController.getDoubleClick()) {
            tovar1 = tovarController.getDoubleClickedRow();
        }
        return tovar1;
    }


    public ObservableList<Standart> getObservableList() {
        return observableList;
    }

    public void setObservableList(ObservableList<Standart> observableList) {
        this.observableList = observableList;
    }

    public EventHandler<ActionEvent> getEventHandler() {
        return eventHandler;
    }

    public void setEventHandler(EventHandler<ActionEvent> eventHandler) {
        this.eventHandler = eventHandler;
        plusButton = initPlusButton();
    }

    public Standart getTovar() {
        return tovar;
    }

    public void setTovar(Standart tovar) {
        this.tovar = tovar;
    }

    public AutoCompletionBinding<Standart> getTovarBinding() {
        return binding;
    }

    public void setBinding(AutoCompletionBinding<Standart> binding) {
        this.binding = binding;
        setAutoCompletion(textField, observableList);
    }
}
