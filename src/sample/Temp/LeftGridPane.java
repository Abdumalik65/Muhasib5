package sample.Temp;

import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import sample.Controller.HisobController;
import sample.Data.Hisob;
import sample.Data.QaydnomaData;
import sample.Data.User;
import sample.Model.HisobModels;
import sample.Tools.PathToImageView;
import sample.Tools.Tugmachalar;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class LeftGridPane {
    GridPane gridPane = new GridPane();
    Connection connection;
    User user;
    Integer tugmacha = 0;
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    String pattern = "###,###,###,###,###.##";

    Label qaydRaqamiLabel = new Label("Qayd raqami");
    TextField qaydRaqamiTextField = new TextField();
    Date date = new Date();
    LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    int year  = localDateTime.getYear();
    int month = localDateTime.getMonthValue();
    int day   = localDateTime.getDayOfMonth();

    Label qaydSanasiLabel = new Label("Sana va vaqt");
    DatePicker qaydSanasiDatePicker = new DatePicker(LocalDate.of(year, month, day));
    TextField qaydVaqtiTextField = new TextField();

    Label beruvchiLabel = new Label("Chiqim hisobi");
    TextField beruvchiTextField = new TextField();
    Button beruvchiButton = new Button();
    HBox beruvchiHbox = new HBox();
    Integer hisob1Id;

    Label oluvchiLabel = new Label("Kirim hisobi");
    TextField oluvchiTextField = new TextField();
    Button oluvchiButton = new Button();
    HBox oluvchiHbox = new HBox();
    Integer hisob2Id;

    Label izohLabel = new Label("Ma`lumotnoma");
    TextArea izohTextArea = new TextArea();

    Button qaydEtButton = new Button("Qayd et");
    Button cancelButton = new Button("<<");

    QaydnomaData qaydnomaData;
    ObservableList<Hisob> hisoblar;
    HisobModels hisobModels = new HisobModels();
    ObservableList<QaydnomaData> leftTableData;

    public LeftGridPane(Connection connection, User user) {
        this.user = user;
        this.connection = connection;
        gridPane.setPadding(new Insets(10));
        gridPane.setVgap(5);
        gridPane.setHgap(5);
        beruvchiHbox.getChildren().addAll(beruvchiTextField, beruvchiButton);
        oluvchiHbox.getChildren().addAll(oluvchiTextField, oluvchiButton);
        hisoblar = hisobModels.get_data(connection);
        qaydSanasiDatePicker.setMaxWidth(2000);
        qaydSanasiDatePicker.setPrefWidth(15);
        oluvchiButton.setGraphic(new PathToImageView("/sample/images/Icons/add.png").getImageView());
        beruvchiButton.setGraphic(new PathToImageView("/sample/images/Icons/add.png").getImageView());

        TextFields.bindAutoCompletion(beruvchiTextField, hisoblar).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Hisob> autoCompletionEvent) -> {
            Hisob hisob = autoCompletionEvent.getCompletion();
            System.out.println(hisob.getId());
            qaydnomaData.setChiqimId(hisob.getId());
            qaydnomaData.setChiqimNomi(hisob.getText());
            hisob1Id = hisob.getId();
        });

        TextFields.bindAutoCompletion(oluvchiTextField, hisoblar).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Hisob> autoCompletionEvent) -> {
            Hisob hisob = autoCompletionEvent.getCompletion();
            System.out.println(hisob.getId());
            qaydnomaData.setKirimId(hisob.getId());
            qaydnomaData.setKirimNomi(hisob.getText());
            hisob2Id = hisob.getId();
        });

        beruvchiButton.setOnAction(event -> {
            HisobController hisobController = new HisobController();
                hisobController.display(connection, user);
                if (hisobController.getDoubleClick()) {
                    Hisob newValue = hisobController.getDoubleClickedRow();
                    if (newValue != null) {
                        beruvchiTextField  .setText(newValue.getText());
                        qaydnomaData.setChiqimNomi(newValue.getText());
                        qaydnomaData.setChiqimId(newValue.getId());
                        hisob1Id = newValue.getId();
                    }
                }
        });

        oluvchiButton.setOnAction(event -> {
            HisobController hisobController = new HisobController();
                hisobController.display(connection, user);
                if (hisobController.getDoubleClick()) {
                    Hisob newValue = hisobController.getDoubleClickedRow();
                    if (newValue != null) {
                        oluvchiTextField.setText(newValue.getText());
                        qaydnomaData.setKirimNomi(newValue.getText());
                        qaydnomaData.setKirimId(newValue.getId());
                        hisob2Id = newValue.getId();
                        hisobController.getStage().close();
                    }
                }
        });
    }

    public void getGridItems(QaydnomaData qaydnomaData, Integer tugmacha) {
        this.qaydnomaData = qaydnomaData;
        this.tugmacha = tugmacha;
        localDateTime = qaydnomaData.getSana().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        qaydRaqamiTextField.setText(String.valueOf(qaydnomaData.getHujjat()));
        qaydVaqtiTextField.setText(sdf.format(qaydnomaData.getSana()));
        // Converter
        StringConverter<LocalDate> converter = new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter =
                    DateTimeFormatter.ofPattern("dd.MM.yyyy");

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }
            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        };

        qaydSanasiDatePicker =  new DatePicker(qaydnomaData.getSana().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        qaydSanasiDatePicker.setConverter(converter);
        beruvchiTextField.setText(qaydnomaData.getChiqimNomi());
        hisob1Id = qaydnomaData.getChiqimId();
        oluvchiTextField.setText(qaydnomaData.getKirimNomi());
        hisob2Id = qaydnomaData.getKirimId();
        izohTextArea.setText(qaydnomaData.getIzoh());
        switch (tugmacha) {
            case 1:
                qaydEtButton = new Tugmachalar().getAdd();
                break;
            case 2:
                qaydEtButton = new Tugmachalar().getDelete();
                break;
            case 3:
                qaydEtButton = new Tugmachalar().getEdit();
                break;
        }
    }

    public void setGridItems(Boolean isDisable) {
        if (gridPane.getChildren().size() > 0) {
            gridPane.getChildren().removeAll(gridPane.getChildren());
        }
        qaydRaqamiTextField.setDisable(isDisable);
        qaydSanasiDatePicker.setDisable(isDisable);
        qaydVaqtiTextField.setDisable(isDisable);
        beruvchiHbox.setDisable(isDisable);
        oluvchiHbox.setDisable(isDisable);
        izohTextArea.setDisable(isDisable);
        gridPane.add(qaydRaqamiLabel, 0, 0, 2, 1);
        gridPane.add(qaydRaqamiTextField, 3, 0, 2, 1);
        gridPane.add(qaydSanasiLabel, 0, 1, 1, 1);
        gridPane.add(qaydSanasiDatePicker, 3, 1);
        gridPane.add(qaydVaqtiTextField, 4, 1);
        gridPane.add(beruvchiLabel, 0, 2, 2, 1);
        gridPane.add(beruvchiHbox, 3, 2, 2, 1);
        gridPane.add(oluvchiLabel, 0, 3, 2, 1);
        gridPane.add(oluvchiHbox, 3, 3, 2, 1);
        gridPane.add(izohLabel, 0, 4, 2, 1);
        gridPane.add(izohTextArea, 3, 4, 2, 1);
        gridPane.add(cancelButton, 0, 5);
        gridPane.add(qaydEtButton, 4, 5);
        GridPane.setHalignment(cancelButton, HPos.LEFT);
        GridPane.setHalignment(qaydEtButton, HPos.RIGHT);

        /*************************************************
         **                 End of Metods               **
         *************************************************/
    }

    public Boolean saveGridToQaydnomaData() {
        LocalDateTime localDateTime = LocalDateTime.of(qaydSanasiDatePicker.getValue(), LocalTime.parse(qaydVaqtiTextField.getText()));
        Instant instant = Instant.from(localDateTime.atZone(ZoneId.systemDefault()));
        qaydnomaData.setSana(Date.from(instant));
        qaydnomaData.setHujjat(Integer.valueOf(qaydRaqamiTextField.getText()));
        qaydnomaData.setChiqimNomi(beruvchiTextField.getText());
        qaydnomaData.setKirimNomi(oluvchiTextField.getText());
        qaydnomaData.setIzoh(izohTextArea.getText().trim());
        qaydnomaData.setStatus(0);
        qaydnomaData.setDateTime(new Date());
        qaydnomaData.setUserId(user.getId());
        return true;
    }

    public Button getQaydEtButton() {
        return qaydEtButton;
    }

    public void setQaydEtButton(Button qaydEtButton) {
        this.qaydEtButton = qaydEtButton;
    }

    public VBox getGridPane() {
        VBox  vBox =  new VBox();

        vBox.setPadding(new Insets(10));
        HBox.setHgrow(gridPane, Priority.ALWAYS);
        GridPane.setHgrow(qaydRaqamiTextField, Priority.ALWAYS);
        GridPane.setHgrow(qaydSanasiDatePicker, Priority.ALWAYS);
        GridPane.setHgrow(qaydVaqtiTextField, Priority.ALWAYS);
        GridPane.setHgrow(oluvchiHbox, Priority.ALWAYS);
        HBox.setHgrow(oluvchiTextField, Priority.ALWAYS);
        GridPane.setHgrow(beruvchiHbox, Priority.ALWAYS);
        HBox.setHgrow(beruvchiTextField, Priority.ALWAYS);
        GridPane.setHgrow(qaydSanasiDatePicker, Priority.ALWAYS);
        VBox.setVgrow(vBox, Priority.ALWAYS);
        HBox.setHgrow(vBox, Priority.ALWAYS);
        vBox.getChildren().add(gridPane);
        return vBox;
    }
    public GridPane getGridPane1() {
        GridPane.setHgrow(qaydRaqamiTextField, Priority.ALWAYS);
        GridPane.setHgrow(qaydSanasiDatePicker, Priority.ALWAYS);
        GridPane.setHgrow(qaydVaqtiTextField, Priority.ALWAYS);
        GridPane.setHgrow(oluvchiHbox, Priority.ALWAYS);
        HBox.setHgrow(oluvchiTextField, Priority.ALWAYS);
        GridPane.setHgrow(beruvchiHbox, Priority.ALWAYS);
        HBox.setHgrow(beruvchiTextField, Priority.ALWAYS);
        GridPane.setHgrow(qaydSanasiDatePicker, Priority.ALWAYS);
        HBox.setHgrow(gridPane, Priority.ALWAYS);
        VBox.setVgrow(gridPane, Priority.ALWAYS);
        return gridPane;
    }

    public Integer getTugmacha() {
        return tugmacha;
    }

    public void setTugmacha(Integer tugmacha) {
        this.tugmacha = tugmacha;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public void setCancelButton(Button cancelButton) {
        this.cancelButton = cancelButton;
    }

    public TextField getBeruvchiTextField() {
        return beruvchiTextField;
    }

    public TextField getOluvchiTextField() {
        return oluvchiTextField;
    }
}
