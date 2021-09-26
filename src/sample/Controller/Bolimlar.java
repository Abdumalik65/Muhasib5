package sample.Controller;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import sample.Config.MySqlDB;
import sample.Config.MySqlDBLocal;
import sample.Data.*;
import sample.Model.*;
import sample.Tools.*;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Optional;

public class Bolimlar extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    SplitPane centerPane = new SplitPane();
    VBox rightPane = new VBox();
    VBox leftPane = new VBox();
    HBox buttonsHBox = new HBox();

    Button qaydEtButton = new Button();
    Button cancelButton = new Button("<<");

    Tugmachalar leftButtons = new Tugmachalar();
    TextField leftTextField = new TextField();
    TextField barCodeTextField = new TextField();
    Tugmachalar rightButtons = new Tugmachalar();
    TextField rightTextField = new TextField();
    TextField right2TextField = new TextField();

    TableView<Standart6> leftTable = new TableView<>();
    TableView<Standart3> rightTable = new TableView<>();
    ObservableList<Standart6> s6List = FXCollections.observableArrayList();
    ObservableList<Standart3> s3List = FXCollections.observableArrayList();
    ObservableList<Standart3> s3AllList = FXCollections.observableArrayList();
    ObservableList<Standart3> s3AllList2 = FXCollections.observableArrayList();

    BarCodeModels barCodeModels = new BarCodeModels();
    Standart6Models standart6Models = new Standart6Models("BGuruh1");
    Standart3Models standart3Models = new Standart3Models();
    GuruhNarhModels guruhNarhModels = new GuruhNarhModels();

    Connection connection;
    User user = new User(1, "admin", "", "admin", "", "", 1, "", 1, new Date());
    int padding = 3;
    DecimalFormat decimalFormat = new MoneyShow();
    Font font = Font.font("Arial", FontWeight.BOLD,20);
    StringBuffer stringBuffer = new StringBuffer();

    public static void main(String[] args) {
        launch(args);
    }

    public Bolimlar() {
        connection = new MySqlDBLocal().getDbConnection();
        ibtido();
    }

    public Bolimlar(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        ibtido();
    }

    private void ibtido() {
        initLeftPane();
        initRightPane();
        initCenterPane();
        initBorderPane();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initStage(primaryStage);
        stage.show();
    }

    public void display() {
        stage = new Stage();
        initStage(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Tovar guruhlari");
        stage = primaryStage;
        stage.setTitle("Savdo");
        scene = new Scene(borderpane, 600, 400);
        stage.setScene(scene);
    }

    private void initLeftPane() {
        leftPane.setPadding(new Insets(padding));
        HBox.setHgrow(leftPane, Priority.ALWAYS);
        VBox.setVgrow(leftPane, Priority.ALWAYS);
        initS3AllList();
        initLeftButtons();
        initLeftTableView();
        leftPane.getChildren().addAll(leftButtons, leftTable);
    }

    private void initS3AllList() {
        StandartModels standartModels = new StandartModels();
        standartModels.setTABLENAME("Tovar");
        ObservableList<Standart> tovarList = standartModels.get_data(connection);
        for (Standart t: tovarList) {
            Standart3 s3 = new Standart3(null,0, t.getId(), t.getText(), user.getId(), new Date());
            s3AllList.add(s3);
        }
        standart3Models.setTABLENAME("BGuruh2");
        s3List = standart3Models.get_data(connection);
        s3AllList2 = standart3Models.get_data(connection);
        for (Standart3 s3: s3List) {
            s3AllList.removeIf(s3Item -> s3Item.getId3().equals(s3.getId3()));
        }
        Comparator<Standart3> comparator = Comparator.comparing(Standart3::getText);
        Collections.sort(s3AllList, comparator);
    }

    private void initCenterPane() {
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        VBox.setVgrow(centerPane, Priority.ALWAYS);
        centerPane.getItems().addAll(leftPane, rightPane);
    }

    private void initRightPane() {
        rightPane.setPadding(new Insets(padding));
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        VBox.setVgrow(rightPane, Priority.ALWAYS);
        initRightButtons();
        initRightTable();
        rightPane.getChildren().addAll(rightButtons, rightTable);
    }

    private void initBorderPane() {
        borderpane.setCenter(centerPane);
    }

    private void initLeftButtons() {
        Button addButton = leftButtons.getAdd();
        Button editButton = leftButtons.getEdit();
        Button deleteButton = leftButtons.getDelete();
        Button excelButton = leftButtons.getExcel();
        HBox.setHgrow(leftTextField, Priority.ALWAYS);
        HBox.setHgrow(barCodeTextField, Priority.ALWAYS);
        leftTextField.setPromptText("Guruh nomi");
        barCodeTextField.setPromptText("SHTRIXKOD");
        leftButtons.getChildren().addAll(leftTextField);
        leftButtons.getChildren().addAll(barCodeTextField);
        leftButtons.getChildren().remove(editButton);

        leftButtons.getAdd().setOnAction(event -> {
            standart6Models.setTABLENAME("BGuruh1");
            Standart6 s6 = new Standart6("Yangi guruh", 0.0, 0.0, 0.0,0.0,0.0,user.getId(), null);
            s6List.add(s6);
            s6.setId(standart6Models.insert_data(connection, s6));
            leftTable.setItems(s6List);
            leftTable.getSelectionModel().select(s6);
            leftTable.scrollTo(s6);
            leftTable.requestFocus();
            leftTable.refresh();

        });

        leftButtons.getDelete().setOnAction(event -> {
            Standart6 standart6 = leftTable.getSelectionModel().getSelectedItem();
            if (standart6 != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.getButtonTypes().removeAll(alert.getButtonTypes());
                ButtonType okButton = new ButtonType("Ha");
                ButtonType noButton = new ButtonType("Yo`q");
                alert.getButtonTypes().addAll(okButton, noButton);
                alert.setTitle("Diqqat !!!");
                alert.setHeaderText(standart6.getText() + " guruhi va unga taalluqli hamma tovarllar jadvaldan o`chirilladi");
                alert.setContentText("Rozimisiz");
                Optional<ButtonType> option = alert.showAndWait();
                ButtonType buttonType = option.get();
                if (okButton.equals(buttonType)) {
                    if (s3List.size()>0) {
                        s3AllList.addAll(s3List);
                        standart3Models.deleteBatch(connection, s3List);
                        s3List.removeAll(s3List);
                        rightTable.setItems(s3List);
                        rightTable.refresh();
                    }

                    standart6Models.delete_data(connection, standart6);
                    s6List.remove(standart6);
                    leftTable.setItems(s6List);
                    leftTable.refresh();
                }
            }
        });

        leftButtons.getExcel().setOnAction(event -> {
            ExportToExcel exportToExcel = new ExportToExcel();
            exportToExcel.priceList(s6List);
        });
        leftTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            leftTaftish(oldValue, newValue);
        });
        barCodeTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                String string = barCodeTextField.getText().trim();
                if (event.getCode()== KeyCode.ENTER) {
                    if (!string.isEmpty()) {
                        BarCode bc = barCodeModels.getBarCode(connection, string);
                        if (bc != null) {
                            for (Standart3 s3: s3AllList2) {
                                if (s3.getId3().equals(bc.getTovar())) {
                                    for (Standart6 s6: s6List) {
                                        if (s6.getId().equals(s3.getId2())) {
                                            leftTable.getSelectionModel().select(s6);
                                            leftTable.scrollTo(s6);
                                            leftTable.refresh();
                                            standart3Models.setTABLENAME("BGuruh2");
                                            s3List = standart3Models.getAnyData(connection, "id2 = " + s6.getId(), "");
                                            rightTable.setItems(s3List);
                                            for (Standart3 s3w: s3List) {
                                                if (s3w.getId3().equals(bc.getTovar())) {
                                                    rightTable.getSelectionModel().select(s3w);
                                                    rightTable.scrollTo(s3w);
                                                    rightTable.requestFocus();
                                                    break;
                                                }
                                            }
                                            rightTable.refresh();
                                            break;
                                        }
                                    }
                                }
                            }
                        } else {
                            Alerts.barCodeNotExist(string);
                        }
                    }
                }
            }
        });
    }

    private void initRightButtons() {
        Button addButton = rightButtons.getAdd();
        Button editButton = rightButtons.getEdit();
        Button deleteButton = rightButtons.getDelete();
        Button excelButton = rightButtons.getExcel();
        rightButtons.getChildren().removeAll(editButton, excelButton);
        HBox.setHgrow(rightTextField, Priority.ALWAYS);
        rightTextField.setPromptText("Tovar nomi");
        rightButtons.getChildren().add(rightTextField);

        addButton.setOnAction(event -> {
            if (leftTable.getSelectionModel().getSelectedItem() != null) {
                leftPane.setDisable(true);
                Label label = new Label(leftTable.getSelectionModel().getSelectedItem().getText());
                label.setFont(font);
                qaydEtButton = new Tugmachalar().getAdd();
                qaydEtButton.setMaxWidth(2000);
                qaydEtButton.setPrefWidth(168);
                HBox.setHgrow(qaydEtButton, Priority.ALWAYS);
                buttonsHBox.getChildren().removeAll(buttonsHBox.getChildren());
                buttonsHBox.getChildren().addAll(cancelButton, qaydEtButton);
                rightButtons.getChildren().remove(rightTextField);
                rightButtons.getChildren().add(right2TextField);
                rightPane.getChildren().add(0, label);
                rightPane.getChildren().add(buttonsHBox);
                rightButtons.getAdd().setDisable(true);
                rightButtons.getDelete().setDisable(true);
                rightTable.setItems(s3AllList);
                rightTable.refresh();

                qaydEtButton.setOnAction(event1 -> {
                    Standart6 s6 = leftTable.getSelectionModel().getSelectedItem();
                    if (s6 != null) {
                        ObservableList<Standart3> selectedS3Items = rightTable.getSelectionModel().getSelectedItems();
                        if (selectedS3Items.size() > 0) {
                            for (Standart3 s3 : selectedS3Items) {
                                s3.setId2(s6.getId());
                            }
                            s3List.addAll(selectedS3Items);
                            standart3Models.addBatch(connection, selectedS3Items);
                            s3AllList.removeAll(selectedS3Items);
                            cancelButton.fire();
                        }
                    }
                });

                cancelButton.setOnAction(event1 -> {
                    rightPane.getChildren().removeAll(label, buttonsHBox);
                    rightButtons.getAdd().setDisable(false);
                    rightButtons.getDelete().setDisable(false);
                    rightButtons.getChildren().remove(right2TextField);
                    rightButtons.getChildren().add(rightTextField);
                    leftPane.setDisable(false);
                    rightTable.setItems(s3List);
                    rightTable.refresh();
                });
            }
        });

        editButton.setOnAction(event -> {
        });

        deleteButton.setOnAction(event -> {
            if (leftTable.getSelectionModel().getSelectedItem() != null) {
                leftPane.setDisable(true);
                Label label = new Label(leftTable.getSelectionModel().getSelectedItem().getText());
                label.setFont(font);
                qaydEtButton = new Tugmachalar().getDelete();
                qaydEtButton.setMaxWidth(2000);
                qaydEtButton.setPrefWidth(168);
                HBox.setHgrow(qaydEtButton, Priority.ALWAYS);
                buttonsHBox.getChildren().removeAll(buttonsHBox.getChildren());
                buttonsHBox.getChildren().addAll(cancelButton, qaydEtButton);
                rightPane.getChildren().add(0, label);
                rightPane.getChildren().add(buttonsHBox);
                rightButtons.getAdd().setDisable(true);
                rightButtons.getDelete().setDisable(true);

                qaydEtButton.setOnAction(event1 -> {
                    Standart6 s6 = leftTable.getSelectionModel().getSelectedItem();
                    if (s6 != null) {
                        ObservableList<Standart3> selectedS3Items = rightTable.getSelectionModel().getSelectedItems();
                        if (selectedS3Items.size() > 0) {
                            for (Standart3 s3 : selectedS3Items) {
                                s3.setId2(0);
                            }
                            s3AllList.addAll(selectedS3Items);
                            standart3Models.deleteBatch(connection, selectedS3Items);
                            s3List.removeAll(selectedS3Items);
                            cancelButton.fire();
                        }
                    }
                });

                cancelButton.setOnAction(event1 -> {
                    rightPane.getChildren().removeAll(label, buttonsHBox);
                    rightButtons.getAdd().setDisable(false);
                    rightButtons.getDelete().setDisable(false);
                    leftPane.setDisable(false);
                    rightTable.setItems(s3List);
                    rightTable.refresh();
                });
            }
        });

        editButton.setOnAction(event -> {
        });

        HBox.setHgrow(cancelButton, Priority.ALWAYS);

        rightTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            rightTaftish(oldValue, newValue);
        });

        right2TextField.textProperty().addListener((observable, oldValue, newValue) -> {
            right2Taftish(oldValue, newValue);
        });

        cancelButton.setMaxWidth(2000);
        cancelButton.setPrefWidth(168);

    }

    private void initLeftTableView() {
        HBox.setHgrow(leftTable, Priority.ALWAYS);
        VBox.setVgrow(leftTable, Priority.ALWAYS);
        s6List = standart6Models.get_data(connection);
        Comparator<Standart6> comparator = Comparator.comparing(Standart6::getText);
        Collections.sort(s6List, comparator);
        leftTable.getColumns().addAll(
                getGuruhColumn()/*,
                getNarhColumn(),
                getUlgurjiNarhColumn(),
                getChakanaNarhColumn(),
                getBojColumn(),
                getNDSColumn()*/
        );
        leftTable.setItems(s6List);
        leftTable.setEditable(true);
        leftTable.requestFocus();
        if (s6List.size()>0) {
            Standart6 s6 = s6List.get(0);
            leftTable.getSelectionModel().select(s6);
            leftTable.scrollTo(s6);
        }
        leftTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Standart6 s6 = observable.getValue();
                standart3Models.setTABLENAME("BGuruh2");
                s3List = standart3Models.getAnyData(connection, "id2 = " + s6.getId(), "");
                rightTable.setItems(s3List);
                rightTable.refresh();
            }
        });
    }

    private TableColumn<Standart6, Integer> getIdColumn() {
        TableColumn<Standart6, Integer> idColumn = new TableColumn<>("№");
        idColumn.setMinWidth(15);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        return idColumn;
    }

    private TableColumn<Standart6, String> getGuruhColumn() {
        TableColumn<Standart6, String> textColumn = new TableColumn<>("Guruh nomi");
        textColumn.setMinWidth(180);
        textColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
        textColumn.setCellFactory(TextFieldTableCell.<Standart6> forTableColumn());
        textColumn.setOnEditCommit((TableColumn.CellEditEvent<Standart6, String> event) -> {
            String newString = event.getNewValue();
            if (newString != null) {
                Standart6 guruh = event.getRowValue();
                guruh.setText(newString);
                standart6Models.update_data(connection, guruh);
            }
        });
        return textColumn;
    }

    private TableColumn<Standart6, Double> getNarhColumn() {
        TableColumn<Standart6, Double> narhColumn = new TableColumn<>("Xarid narhi");
        narhColumn.setCellValueFactory(new PropertyValueFactory<>("narh"));
        narhColumn.setMinWidth(100);
        narhColumn.setMaxWidth(100);
        narhColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                return decimalFormat.format(object);
            }

            @Override
            public Double fromString(String string) {
                string = string.replaceAll(" ", "");
                string = string.replaceAll(",", ".");
                if (!Alerts.isNumericAlert(string)) {
                    string = "0.0";
                }
                return Double.valueOf(string);
            }
        }));
        narhColumn.setOnEditCommit(event -> {
            Standart6 s6 = event.getRowValue();
            standart6Models.setTABLENAME("BGuruh1");
            if (s6 != null) {
                s6.setNarh(event.getNewValue());
                standart6Models.update_data(connection, s6);
                GuruhNarh guruhNarh = new GuruhNarh(
                        null, new Date(), s6.getId(), 0, event.getNewValue(), user.getId(), new Date()
                );
                guruhNarhModels.insert_data(connection, guruhNarh);
                event.getTableView().refresh();
            }
        });
        narhColumn.setStyle( "-fx-alignment: CENTER;");

        return narhColumn;
    }

    private TableColumn<Standart6, Double> getChakanaNarhColumn() {
        TableColumn<Standart6, Double> chakanaNarhColumn = new TableColumn<>("Shtuchniy narh");
        chakanaNarhColumn.setCellValueFactory(new PropertyValueFactory<>("chakana"));
        chakanaNarhColumn.setMinWidth(100);
        chakanaNarhColumn.setMaxWidth(100);
        chakanaNarhColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                return decimalFormat.format(object);
            }

            @Override
            public Double fromString(String string) {
                string = string.replaceAll(" ", "");
                string = string.replaceAll(",", ".");
                if (!Alerts.isNumericAlert(string)) {
                    string = "0.0";
                }
                return Double.valueOf(string);
            }
        }));
        chakanaNarhColumn.setOnEditCommit(event -> {
            Standart6 s6 = event.getRowValue();
            standart6Models.setTABLENAME("BGuruh1");
            if (s6 != null) {
                s6.setChakana(event.getNewValue());
                standart6Models.update_data(connection, s6);
                GuruhNarh guruhNarh = new GuruhNarh(
                        null, new Date(), s6.getId(), 1, event.getNewValue(), user.getId(), new Date()
                );
                guruhNarhModels.insert_data(connection, guruhNarh);
                event.getTableView().refresh();
            }
        });
        chakanaNarhColumn.setStyle( "-fx-alignment: CENTER;");

        return chakanaNarhColumn;
    }

    private TableColumn<Standart6, Double> getUlgurjiNarhColumn() {
        TableColumn<Standart6, Double> ulgurjiNarhColumn = new TableColumn<>("Optom narh");
        ulgurjiNarhColumn.setCellValueFactory(new PropertyValueFactory<>("ulgurji"));
        ulgurjiNarhColumn.setMinWidth(100);
        ulgurjiNarhColumn.setMaxWidth(100);
        ulgurjiNarhColumn.setStyle( "-fx-alignment: CENTER;");

        ulgurjiNarhColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                return decimalFormat.format(object);
            }

            @Override
            public Double fromString(String string) {
                string = string.replaceAll(" ", "");
                string = string.replaceAll(",", ".");
                if (!Alerts.isNumericAlert(string)) {
                    string = "0.0";
                }
                return Double.valueOf(string);
            }
        }));
        ulgurjiNarhColumn.setOnEditCommit(event -> {
            Standart6 s6 = event.getRowValue();
            standart6Models.setTABLENAME("BGuruh1");
            if (s6 != null) {
                s6.setUlgurji(event.getNewValue());
                standart6Models.update_data(connection, s6);
                GuruhNarh guruhNarh = new GuruhNarh(
                        null, new Date(), s6.getId(), 2, event.getNewValue(), user.getId(), new Date()
                );
                guruhNarhModels.insert_data(connection, guruhNarh);
                event.getTableView().refresh();
            }
        });
        ulgurjiNarhColumn.setStyle( "-fx-alignment: CENTER;");

        return ulgurjiNarhColumn;
    }

    private TableColumn<Standart6, Double> getNDSColumn() {
        TableColumn<Standart6, Double> ndsColumn = new TableColumn<>("NDS");
        ndsColumn.setCellValueFactory(new PropertyValueFactory<>("nds"));
        ndsColumn.setMinWidth(100);
        ndsColumn.setMaxWidth(100);
        ndsColumn.setStyle( "-fx-alignment: CENTER;");

        ndsColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                return decimalFormat.format(object);
            }

            @Override
            public Double fromString(String string) {
                string = string.replaceAll(" ", "");
                string = string.replaceAll(",", ".");
                if (!Alerts.isNumericAlert(string)) {
                    string = "0.0";
                }
                return Double.valueOf(string);
            }
        }));
        ndsColumn.setOnEditCommit(event -> {
            Standart6 s6 = event.getRowValue();
            standart6Models.setTABLENAME("BGuruh1");
            if (s6 != null) {
                s6.setNds(event.getNewValue());
                standart6Models.update_data(connection, s6);
                GuruhNarh guruhNarh = new GuruhNarh(
                        null, new Date(), s6.getId(), 3, event.getNewValue(), user.getId(), new Date()
                );
                guruhNarhModels.insert_data(connection, guruhNarh);
                event.getTableView().refresh();
            }
        });
        ndsColumn.setStyle( "-fx-alignment: CENTER;");

        return ndsColumn;
    }

    private TableColumn<Standart6, Double> getBojColumn() {
        TableColumn<Standart6, Double> bojSoligiColumn = new TableColumn<>("Boj");
        bojSoligiColumn.setCellValueFactory(new PropertyValueFactory<>("boj"));
        bojSoligiColumn.setMinWidth(100);
        bojSoligiColumn.setMaxWidth(100);
        bojSoligiColumn.setStyle( "-fx-alignment: CENTER;");

        bojSoligiColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                return decimalFormat.format(object);
            }

            @Override
            public Double fromString(String string) {
                string = string.replaceAll(" ", "");
                string = string.replaceAll(",", ".");
                if (!Alerts.isNumericAlert(string)) {
                    string = "0.0";
                }
                return Double.valueOf(string);
            }
        }));
        bojSoligiColumn.setOnEditCommit(event -> {
            Standart6 s6 = event.getRowValue();
            standart6Models.setTABLENAME("BGuruh1");
            if (s6 != null) {
                s6.setBoj(event.getNewValue());
                standart6Models.update_data(connection, s6);
                GuruhNarh guruhNarh = new GuruhNarh(
                        null, new Date(), s6.getId(), 4, event.getNewValue(), user.getId(), new Date()
                );
                guruhNarhModels.insert_data(connection, guruhNarh);
                event.getTableView().refresh();
            }
        });
        bojSoligiColumn.setStyle( "-fx-alignment: CENTER;");

        return bojSoligiColumn;
    }

    private void initRightTable() {
        rightTable.setPadding(new Insets(padding));
        SetHVGrow.VerticalHorizontal(rightTable);
        rightTable.getColumns().addAll(getTovarColumn());

        Standart6 s6 = leftTable.getSelectionModel().getSelectedItem();
        if (s6 != null) {
            standart3Models.setTABLENAME("BGuruh2");
            s3List = standart3Models.getAnyData(connection, "id2 = " + s6.getId(), "");
        }
        rightTable.setItems(s3List);
        rightTable.refresh();
        rightTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private TableColumn<Standart3, String> getTovarColumn() {
        TableColumn<Standart3, String> textColumn = new TableColumn<>("Tovar nomi");
        textColumn.setMinWidth(300);
        textColumn.setCellValueFactory(new PropertyValueFactory<>("text"));

        textColumn.setCellFactory(tc -> {
            TableCell<Standart3, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(textColumn.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell ;
        });
        return textColumn;
    }

    public void rightTaftish(String oldValue, String newValue) {
        ObservableList<Standart3> subentries = FXCollections.observableArrayList();
        newValue = newValue.toLowerCase();

        if ( oldValue != null && (newValue.length() < oldValue.length()) ) {
            rightTable.setItems( s3List );
        }

        for ( Standart3 s3: s3List ) {
            if (s3.getText().toLowerCase().contains(newValue)) {
                subentries.add(s3);
            }
        }
        rightTable.setItems(subentries);
    }

    public void right2Taftish(String oldValue, String newValue) {
        ObservableList<Standart3> subentries = FXCollections.observableArrayList();
        newValue = newValue.toLowerCase();

        if ( oldValue != null && (newValue.length() < oldValue.length()) ) {
            rightTable.setItems( s3AllList );
        }

        for ( Standart3 s3: s3AllList ) {
            if (s3.getText().toLowerCase().contains(newValue)) {
                subentries.add(s3);
            }
        }
        rightTable.setItems(subentries);
    }

    public void leftTaftish(String oldValue, String newValue) {
        ObservableList<Standart6> subentries = FXCollections.observableArrayList();
        newValue = newValue.toLowerCase();

        if ( oldValue != null && (newValue.length() < oldValue.length()) ) {
            leftTable.setItems( s6List );
        }

        for ( Standart6 s6: s6List ) {
            if (s6.getText().toLowerCase().contains(newValue)) {
                subentries.add(s6);
            }
        }
        leftTable.setItems(subentries);
    }

    private Standart6 getTovar(Integer tovarId) {
        Standart6 tovar = null;
        for (Standart6 s6: s6List) {
            if (s6.getId().equals(tovarId)) {
                tovar = s6;
                break;
            }
        }
        return tovar;
    }

}
