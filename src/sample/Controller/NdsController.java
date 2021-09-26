package sample.Controller;

import com.jfoenix.controls.JFXButton;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import sample.Config.MySqlDBLocal;
import sample.Data.BarCode;
import sample.Data.Standart;
import sample.Data.Standart4;
import sample.Data.User;
import sample.Model.BarCodeModels;
import sample.Model.Standart4Models;
import sample.Model.StandartModels;
import sample.Tools.Alerts;
import sample.Tools.GetDbData;
import sample.Tools.MoneyShow;
import sample.Tools.Tugmachalar;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class NdsController extends Application {
    DecimalFormat decimalFormat = new MoneyShow();
    Connection connection;
    User user = new User(1, "admin", "", "admin");
    Stage stage = new Stage();
    Scene scene;
    BorderPane borderpane = new BorderPane();
    SplitPane centerPane = new SplitPane();
    VBox rightPane = new VBox();
    VBox leftPane = new VBox();
    Pane pane = new Pane();
    Label left_label = new Label("chap");
    Label right_label = new Label("o`ng");
    HBox bottom = new HBox();
    StringBuffer stringBuffer = new StringBuffer();
    TextField barCodeTextField = new TextField();
    Tugmachalar tugmachalar = new Tugmachalar();
    TableView<Standart4> rightTableView = new TableView();
    TableView<Standart> tovarTableView = new TableView<>();
    TextField tovarTop = new TextField();

    ObservableList<Standart> tovarObservableList  = FXCollections.observableArrayList();
    ObservableList<Standart4> rightTableViewObservableList = FXCollections.observableArrayList();
    ObservableList<BarCode> barCodeObservableList;
    ObservableList<Standart> tartibObservableList;

    BarCodeModels barCodeModels = new BarCodeModels();
    StandartModels standartModels = new StandartModels();
    Standart4Models standart4Models = new Standart4Models();

    Standart tovar;
    Standart4 rightCursor;
    int padding = 3;
    String IkkinchiUstunNomi = "NDS %";

    public NdsController() {
        connection = new MySqlDBLocal().getDbConnection();
    }

    public NdsController(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        GetDbData.initData(connection);
        ibtido();
        initStage(stage);
        stage.show();
    }

    public void display() {
        stage = new Stage();
        initStage(stage);
        ibtido();
        stage.showAndWait();
    }

    private void ibtido() {
        initData();
        initTables();
        initLeftPane();
        initRightPane();
        initCenterPane();
        initTugmachalar();
        initBorderPane();
    }

    private void initTugmachalar() {
        tugmachalar.getChildren().remove(1);
        tugmachalar.getChildren().remove(1);
        tugmachalar.getAdd().setOnAction(event -> {
            Standart4 standart4 = new Standart4(null, tovar.getId(), new Date(), .0, user.getId(), null);
            rightTableViewObservableList.add(standart4);
            standart4Models.setTABLENAME("Nds");
            standart4Models.insert_data(connection, standart4);
            rightTableView.refresh();
        });
    }

    private void initData() {
        standartModels.setTABLENAME("ChiqimShakli");
        tartibObservableList = standartModels.get_data(connection);
        tovarObservableList = GetDbData.getTovarObservableList();
        standart4Models.setTABLENAME("Nds");
    }

    private void initTables() {
        initLeftTableView();
        initRightTableView();
    }

    private void initLeftTableView() {
        HBox.setHgrow(tovarTableView, Priority.ALWAYS);
        VBox.setVgrow(tovarTableView, Priority.ALWAYS);
        tovarTableView.getColumns().add(getTovarNomiColumn());
        tovarTableView.setItems(tovarObservableList);
        if (tovarObservableList.size()>0) {
            tovarTableView.getSelectionModel().selectFirst();
            tovar = tovarObservableList.get(0);
            rightTableViewObservableList = standart4Models.getAnyData(connection, "tovar = " + tovar.getId(), "");
            rightTableView.setItems(rightTableViewObservableList);
            if (rightTableViewObservableList.size()>0) {
                rightCursor = rightTableViewObservableList.get(0);
                rightTableView.getSelectionModel().selectFirst();
            }
        }
        tovarTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                tovar = newValue;
                rightTableViewObservableList.removeAll(rightTableViewObservableList);
                rightTableViewObservableList.addAll(standart4Models.getAnyData(connection,"tovar = " + tovar.getId(),"sana desc"));
//                    tovarSanaTableView.setItems(tovarSanaObservableList);
                if (rightTableViewObservableList.size()>0) {
                    rightTableView.getSelectionModel().selectFirst();
                }
            }
        });
    }

    private TableColumn<Standart, String> getTovarNomiColumn() {
        TableColumn<Standart, String> tovarNomi = new TableColumn<>("Tovar");
        tovarNomi.setMaxWidth(182);
        tovarNomi.setMinWidth(182);
        tovarNomi.setCellValueFactory(new PropertyValueFactory<>("text"));
        tovarNomi.setCellFactory(tc -> {
            TableCell<Standart, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(tovarNomi.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell ;
        });
        return tovarNomi;
    }

    private void initRightTableView() {
        HBox.setHgrow(rightTableView, Priority.NEVER);
        VBox.setVgrow(rightTableView, Priority.ALWAYS);
        rightTableView.getColumns().addAll(getSanaColumn(), getMiqdorColumn(), getDeleteColumn());
        rightTableView.setItems(rightTableViewObservableList);
        rightTableView.setEditable(true);
        HBox.setHgrow(rightTableView, Priority.NEVER);
        VBox.setVgrow(rightTableView, Priority.ALWAYS);
    }

    private TableColumn<Standart4, Date> getSanaColumn() {
        TableColumn<Standart4, Date> sanaColumn = new TableColumn<>("Sana");
        sanaColumn.setMaxWidth(120);
        sanaColumn.setMinWidth(120);
        sanaColumn.setCellValueFactory(new PropertyValueFactory<>("sana"));
        sanaColumn.setCellFactory(column -> {
            TableCell<Standart4, Date> cell = new TableCell<Standart4, Date>() {
                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                    if (empty) {
                        setText(null);
                    } else {
                        setText(format.format(item));
                    }
                }
            };
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        return sanaColumn;

    }

    private TableColumn<Standart4, ComboBox<Standart>> getTartibColumn() {
        TableColumn<Standart4, ComboBox<Standart>> taqdimColumn = new TableColumn<>("Taqdim shakli");
        taqdimColumn.setMinWidth(180);
        taqdimColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Standart4, ComboBox<Standart>>, ObservableValue<ComboBox<Standart>>>() {

            @Override
            public ObservableValue<ComboBox<Standart>> call(TableColumn.CellDataFeatures<Standart4, ComboBox<Standart>> param) {
                Standart4 tartib = param.getValue();
                ComboBox<Standart> comboBox = new ComboBox<>(tartibObservableList);
                comboBox.setMaxWidth(2000);
                comboBox.setPrefWidth(150);
                HBox.setHgrow(comboBox, Priority.ALWAYS);
                Integer i = tartib.getMiqdor().intValue();
                for (Standart s: tartibObservableList) {
                    if (s.getId() == i) {
                        comboBox.getSelectionModel().select(s);
                        break;
                    }
                }

                comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->  {
                    Standart item = comboBox.getSelectionModel().getSelectedItem();
                    if (item != null) {
                        tartib.setMiqdor(item.getId().doubleValue());
                        standart4Models.setTABLENAME("Nds");
                        standart4Models.update_data(connection, tartib);
                    }
                });
                return new SimpleObjectProperty<>(comboBox);
            }
        });
        taqdimColumn.setStyle( "-fx-alignment: CENTER;");
        taqdimColumn.setMinWidth(120);
        return taqdimColumn;
    }

    private TableColumn<Standart4, Double> getMiqdorColumn() {
        TableColumn<Standart4, Double> miqdorColumn = new TableColumn<>(getIkkinchiUstunNomi());
        miqdorColumn.setMaxWidth(120);
        miqdorColumn.setMinWidth(120);
        miqdorColumn.setCellValueFactory(new PropertyValueFactory<>("miqdor"));
        miqdorColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
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
        miqdorColumn.setOnEditCommit((TableColumn.CellEditEvent<Standart4, Double> event) -> {
            TablePosition<Standart4, Double> pos = event.getTablePosition();

            Double newValue = event.getNewValue();

            int row = pos.getRow();
            Standart4 standart4 = event.getTableView().getItems().get(row);
            standart4.setMiqdor(newValue);
            standart4Models.setTABLENAME("Nds");
            standart4Models.update_data(connection, standart4);
            event.getTableView().refresh();
        });
        miqdorColumn.setStyle( "-fx-alignment: CENTER;");
        return miqdorColumn;
    }

    private TableColumn<Standart4, Button> getDeleteColumn() {
        TableColumn<Standart4, Button> deleteColumn = new TableColumn<>("O`chir");
        deleteColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Standart4, Button>, ObservableValue<Button>>() {

            @Override
            public ObservableValue<Button> call(TableColumn.CellDataFeatures<Standart4, Button> param) {
                Standart4 standart4 = param.getValue();
                JFXButton b = new JFXButton("");
                b.setMaxWidth(2000);
                b.setPrefWidth(150);
                HBox.setHgrow(b, Priority.ALWAYS);
                InputStream inputStream = getClass().getResourceAsStream("/sample/images/Icons/delete.png");
                Image image = new Image(inputStream);
                ImageView imageView = new ImageView(image);
                b.setGraphic(imageView);
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                image = null;

                b.setOnAction(event -> {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.getButtonTypes().removeAll(alert.getButtonTypes());
                    ButtonType okButton = new ButtonType("Ha");
                    ButtonType noButton = new ButtonType("Yo`q");
                    alert.getButtonTypes().addAll(okButton, noButton);
                    alert.setTitle("Diqqat !!!");
                    alert.setHeaderText("Sana: " + sdf.format(standart4.getSana()) + "\nNDS miqdori: : " + standart4.getMiqdor().toString().trim() + " %");
                    alert.setContentText("Ma`lumot o`chiriladi. Davom etaymi ???");
                    Optional<ButtonType> option = alert.showAndWait();
                    ButtonType buttonType = option.get();
                    if (okButton.equals(buttonType)) {
                        rightTableViewObservableList.remove(standart4);
                        standart4Models.setTABLENAME("Nds");
                        standart4Models.delete_data(connection, standart4);
                    }
                });
                return new SimpleObjectProperty<Button>(b);
            }
        });

        deleteColumn.setMinWidth(20);
        deleteColumn.setMaxWidth(40);
        deleteColumn.setStyle( "-fx-alignment: CENTER;");
        return deleteColumn;
    }

    private void initCenterPane() {
        centerPane.setPadding(new Insets(padding));
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        VBox.setVgrow(centerPane, Priority.ALWAYS);
        centerPane.getItems().addAll(leftPane, rightPane);
        centerPane.setDividerPositions(.25);
    }

    private void initRightPane() {
        rightPane.getChildren().addAll(tugmachalar, rightTableView);
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        VBox.setVgrow(rightPane, Priority.ALWAYS);
    }

    private void initLeftPane() {
        leftPane.setPadding(new Insets(padding));
        leftPane.setMaxWidth(210);
        leftPane.setMinWidth(210);
        tovarTop.setMaxWidth(290);
        HBox.setHgrow(tovarTop, Priority.ALWAYS);
        HBox.setHgrow(leftPane, Priority.ALWAYS);
        VBox.setVgrow(leftPane, Priority.ALWAYS);
        tovarTop.setPromptText("Tovar nomi");
        barCodeTextField.setPromptText("Shtrixkod");
        leftPane.getChildren().addAll(barCodeTextField, tovarTop, tovarTableView);
        tovarTop.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Taftish(oldValue, newValue);
            }
        });
    }

    private void initBorderPane() {
        borderpane.setCenter(centerPane);
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("NDS miqdori");
        scene = new Scene(borderpane, 510, 500);
        stage.setScene(scene);
        barCodeOn();
        stage.setOnCloseRequest(event -> {
            barCodeOff();
        });
    }

    public Standart getTovar(Integer id) {
        Standart tovar = null;
        for (Standart t: tovarObservableList) {
            if (t.getId().equals(id)) {
                tovar = t;
                break;
            }
        }
        return tovar;
    }

    public BarCode getBarCode(String barCode) {
        BarCode barCode1 = null;
        ObservableList<BarCode> tovars = barCodeModels.getAnyData(connection, "barCode = '" + barCode + "'", "");
        if (tovars.size()>0) {
            barCode1 = tovars.get(0);
            barCodeObservableList.add(barCode1);
        }
        return barCode1;
    }


    private void Taftish(String oldValue, String newValue) {
        ObservableList<Standart> subentries = FXCollections.observableArrayList();

        newValue = newValue.toLowerCase();

        if ( oldValue != null && (newValue.length() < oldValue.length()) ) {
            tovarTableView.setItems(tovarObservableList);
        }

        for ( Standart t: tovarObservableList ) {
            if (t.getText().toLowerCase().contains(newValue)) {
                subentries.add(t);
            }
        }
        tovarTableView.setItems(subentries);
    }

    private void barCodeOn() {
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                stringBuffer.append(event.getText());
            }
        });

        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    boolean topdim = false;
                    String string = stringBuffer.toString().trim();
                    stringBuffer.delete(0, stringBuffer.length());
                    if (!string.isEmpty()) {
                        BarCode barCode = GetDbData.getBarCode(string);
                        if (barCode != null) {
                            Standart tovar = GetDbData.getTovar(barCode.getTovar());
                            barCodeTextField.setText("");
                            tovarTableView.getSelectionModel().select(tovar);
                            tovarTableView.scrollTo(tovar);
                        }
                        else {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Diqqat!!!");;
                            alert.setHeaderText(string + " shtrixkodga muvofiq tovar topilmadi");
                            alert.setContentText("");
                            alert.showAndWait();
                        }
                    }
                }
            }
        });

        barCodeTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode()== KeyCode.ENTER) {
                    String string = barCodeTextField.getText().trim();
                    boolean topdim = false;
                    if (!string.isEmpty()) {
                        BarCode barCode = GetDbData.getBarCode(string);
                        if (barCode != null) {
                            Standart tovar = GetDbData.getTovar(barCode.getTovar());
                            barCodeTextField.setText("");
                            tovarTableView.getSelectionModel().select(tovar);
                            tovarTableView.scrollTo(tovar);
                        }
                        else {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Diqqat!!!");;
                            alert.setHeaderText(string + " shtrixkodga muvofiq tovar topilmadi");
                            alert.setContentText("");
                            alert.showAndWait();
                        }
                    }
                }
            }
        });
    }

    private void barCodeOff() {
        scene.setOnKeyPressed(null);
        scene.setOnKeyReleased(null);
        barCodeTextField.setOnKeyPressed(null);
    }

    public String getIkkinchiUstunNomi() {
        return IkkinchiUstunNomi;
    }

    public void setIkkinchiUstunNomi(String ikkinchiUstunNomi) {
        IkkinchiUstunNomi = ikkinchiUstunNomi;
    }

    public Standart getTartibNomi(double idDouble) {
        int id = (int) idDouble;
        Standart tartib = null;
        for (Standart o: tartibObservableList) {
            if (o.getId().equals(id)) {
                tartib = o;
                break;
            }
        }

        if (tartib == null) {
            standartModels.setTABLENAME("Nds");
            ObservableList<Standart> tartiblar = standartModels.getAnyData(connection, "id = " + id, "");
            if (tartiblar.size()>0) {
                tartib = tartiblar.get(0);
                GetDbData.tovarObservableList.add(tartib);
            }
        }
        return tartib;
    }
}
