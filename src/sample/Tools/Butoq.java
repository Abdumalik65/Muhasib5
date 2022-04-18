package sample.Tools;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import sample.Data.HisobKitob;
import sample.Data.Standart;
import sample.Data.Valuta;

import java.text.DecimalFormat;

public class Butoq extends HBox {
    private Integer itemId;
    private Double aDouble;
    private Label label = new Label();
    private Label label2 = new Label();
    private Standart standart;
    private HBoxTextFieldPlusButton hisobHBox;
    private TovarBox tovarBox;
    private Button button;
    private Button button2;
    ToggleGroup toggleGroup;
    private TextField textField;
    private TextField textField2;
    private TextField kursTextField;
    private ComboBox<Standart> comboBox;
    private Separator separator;
    Valuta valuta;
    TextFieldButton textFieldButton;
    TextFieldButton textFieldButton1;
    HisobKitob hisobKitob;
    DecimalFormat decimalFormat = new MoneyShow();
    HisobBoxY hisobBox;
    DatePicker datePicker;
    TextArea textArea;
    HBox hBox;

    Font font = Font.font("Arial", FontWeight.BOLD,16);

    public Butoq(Integer itemId, String string, Double aDouble) {
            super(5);
            this.itemId = itemId;
            this.aDouble = aDouble;
            label.setFont(font);
            label2.setFont(font);
            label.setText(string);
            label2.setText(decimalFormat.format(aDouble));

            this.getChildren().addAll(label, label2);
            this.setAlignment(Pos.CENTER_LEFT);
        }

    public Butoq(Integer itemId, TextField textField) {
        super(5);
        this.itemId = itemId;
        this.textField = textField;
        textField.setMaxWidth(200);

        this.getChildren().addAll(textField);
        this.setAlignment(Pos.CENTER_LEFT);
    }

    public Butoq(Integer itemId, ToggleGroup toggleGroup) {
        super(5);
        this.itemId = itemId;
        this.toggleGroup = toggleGroup;
        VBox vBox = new VBox(5);
        VBox.setVgrow(vBox, Priority.ALWAYS);
        HBox.setHgrow(vBox, Priority.ALWAYS);
        for (int i = 0; i<toggleGroup.getToggles().size(); i++) {
            RadioButton radioButton = (RadioButton) toggleGroup.getToggles().get(i);
            vBox.getChildren().add(radioButton);
        }
        getChildren().add(vBox);
        this.setAlignment(Pos.CENTER_LEFT);
    }

    public Butoq(VBox vBox) {
        getChildren().add(vBox);
        this.setAlignment(Pos.CENTER_LEFT);
    }

    public Butoq(HisobBoxY hisobBox) {
        super(2);
        this.hisobBox = hisobBox;
        HBox.setHgrow(hisobBox, Priority.ALWAYS);
        this.getChildren().add(hisobBox);
    }

    public Butoq(HBox hBox) {
        super(5);
        this.hBox = hBox;
        getChildren().add(hBox);
    }

    public Butoq(DatePicker qaydSanasiDatePicker) {
        super(5);
        this.datePicker = qaydSanasiDatePicker;
        this.getChildren().add(datePicker);
        this.setAlignment(Pos.CENTER_LEFT);
    }

    public Butoq(TovarBox tovarBox) {
        super(5);
        this.itemId = itemId;
        this.tovarBox = tovarBox;
        tovarBox.setMaxWidth(200);
        this.getChildren().add(tovarBox);
        this.setAlignment(Pos.CENTER_LEFT);
    }

    public Butoq(TextArea textArea) {
        super();
        this.textArea = textArea;
        getChildren().add(textArea);
    }

    public ToggleGroup getToggleGroup() {
            return toggleGroup;
        }

        public Butoq(Integer itemId, HisobKitob hisobKitob) {
            super(5);
            this.itemId = itemId;
            this.hisobKitob = hisobKitob;
            textField = new TextField();
            textField.setFont(font);
            valuta = GetDbData.getValuta(hisobKitob.getValuta());
            textField.setPromptText(valuta.getValuta());
            this.getChildren().addAll(textField);
            this.setAlignment(Pos.CENTER_LEFT);
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    Double aDouble = StringNumberUtils.textToDouble(newValue);
                    hisobKitob.setNarh(aDouble);
                }
            });
            textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    if (newValue) {
                        textField.selectAll();
                    } else {
                        textField.setText(decimalFormat.format(hisobKitob.getNarh()));
                    }
                }
            });
        }

        public Butoq(Integer itemId, TextField textField, TextField textField2) {
            super(2);
            this.itemId = itemId;
            this.textField = textField;
            this.textField2 = textField2;
            textField.setMaxWidth(97);
            textField2.setMaxWidth(97);
            textField.setAlignment(Pos.CENTER);
            textField2.setAlignment(Pos.CENTER);
            this.getChildren().addAll(textField, textField2);
        }

        public Butoq(Integer itemId, ComboBox comboBox) {
            super(5);
            this.itemId = itemId;
            this.comboBox = comboBox;
            comboBox.setMaxWidth(200);

            this.getChildren().addAll(comboBox);
            this.setAlignment(Pos.CENTER_LEFT);
        }

        public Butoq(Integer itemId, Label label) {
            super();
            this.itemId = itemId;
            this.label = label;
            label.setMaxWidth(200);

            this.getChildren().add(label);
            this.setAlignment(Pos.CENTER_LEFT);
        }

        public Butoq(Integer itemId, Separator separator) {
            super();
            this.itemId = itemId;
            this.separator = separator;
            SetHVGrow.VerticalHorizontal(separator);

            this.getChildren().add(separator);
            this.setAlignment(Pos.CENTER_LEFT);
        }

        public Butoq(Standart standart) {
            super();
            this.standart = standart;
            itemId = standart.getId();
            label.setText(standart.getText());
            label.setMaxWidth(200);
            this.getChildren().add(label);
            this.setAlignment(Pos.CENTER_LEFT);
        }

        public Butoq(Integer itemId, HBoxTextFieldPlusButton hisobHBox) {
            super();
            this.itemId = itemId;
            this.hisobHBox = hisobHBox;
            hisobHBox.setMaxWidth(200);
            this.getChildren().add(hisobHBox);
            this.setAlignment(Pos.CENTER_LEFT);
        }

        public Butoq(Integer itemId, TovarBox tovarBox) {
            super();
            this.itemId = itemId;
            this.tovarBox = tovarBox;
            tovarBox.setMaxWidth(200);
            this.getChildren().add(tovarBox);
            this.setAlignment(Pos.CENTER_LEFT);
        }

        public Butoq(Integer itemId, Button button) {
            super();
            this.itemId = itemId;
            this.button = button;
            button.setMaxWidth(210);
            this.getChildren().add(button);
            this.setAlignment(Pos.CENTER_LEFT);
        }

        public void initTextField(TextField textField) {
            textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    if (!newValue) {
                        Double aDouble = StringNumberUtils.getDoubleFromTextField(textField);
                        textField.setText(decimalFormat.format(aDouble));
                    }
                }
            });
        }

        public Integer getItemId() {
            return itemId;
        }

        public void setItemId(Integer itemId) {
            this.itemId = itemId;
        }

        public TextField getTextField() {
            return textField;
        }

        public void setTextField(TextField textField) {
            this.textField = textField;
        }

        public Label getLabel() {
            return label;
        }

        public void setLabel(Label label) {
            this.label = label;
        }

        public TextField getKursTextField() {
            return kursTextField;
        }

        public void setKursTextField(TextField kursTextField) {
            this.kursTextField = kursTextField;
        }

        public TextField getTextField2() {
            return textField2;
        }

        public void setTextField2(TextField textField2) {
            this.textField2 = textField2;
        }

        public Button getButton() {
            return button;
        }

        public void setButton(Button button) {
            this.button = button;
        }

        public Button getButton2() {
            return button2;
        }

        public void setButton2(Button button2) {
            this.button2 = button2;
        }
    }
