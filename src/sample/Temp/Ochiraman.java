package sample.Temp;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import sample.Config.MySqlDBGeneral;
import sample.Data.*;
import sample.Enums.ServerType;
import sample.Model.HisobKitobModels;
import sample.Model.QaydnomaModel;
import sample.Tools.*;

import java.sql.Connection;

public class Ochiraman extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    VBox centerPane = new VBox();
    VBox rightPane = new VBox();
    VBox leftPane = new VBox();
    TableView<QaydnomaData> leftTable;
    TableView centerTable;
    Tugmachalar tugmachalar = new Tugmachalar();
    TableView<HisobKitob> rightTable;

    ObservableList<QaydnomaData> leftTableData;
    ObservableList centerTableData;
    ObservableList<HisobKitob> rightTableData;

    Connection connection;
    User user;
    int padding = 3;


    public static void main(String[] args) {
        launch(args);
    }

    public Ochiraman() {
        connection = new MySqlDBGeneral(ServerType.REMOTE).getDbConnection();
        GetDbData.initData(connection);
        ibtido();
    }

    public Ochiraman(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        ibtido();
    }

    private void ibtido() {
        initTopPane();
        initLeftPane();
        initCenterPane();
        initRightPane();
        initBottomPane();
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

    private void initTopPane() {}

    private void initLeftPane() {
        leftPane.setPadding(new Insets(padding));
        HBox.setHgrow(leftPane, Priority.ALWAYS);
        VBox.setVgrow(leftPane, Priority.ALWAYS);
        leftTable = initLeftTable();
        leftPane.getChildren().add(leftTable);
    }

    private void initCenterPane() {
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        VBox.setVgrow(centerPane, Priority.ALWAYS);
        centerPane.getChildren().addAll(leftPane, rightPane);
    }

    private void initRightPane() {
        rightPane.setPadding(new Insets(padding));
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        VBox.setVgrow(rightPane, Priority.ALWAYS);
        rightTable = initRightTable();
        rightPane.getChildren().add(rightTable);
    }

    private void initBottomPane() {}

    private void initBorderPane() {
        borderpane.setCenter(centerPane);
    }

    private void initStage(Stage primaryStage) {
        scene = new Scene(borderpane);
        scene.setUserAgentStylesheet("sample/Styles/caspian.css");
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        stage = primaryStage;
        stage.setTitle("Savdo");
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        stage.setScene(scene);
    }

    private TableView<QaydnomaData> initLeftTable() {
        TableView<QaydnomaData> tableView = new TableView<>();
        SetHVGrow.VerticalHorizontal(tableView);
        tableView.getColumns().addAll(getAmalIdColumn(), getAmalTuriColumn(), getChiqimNomiColumn(), getKirimNomiColumn());
        tableView.getSelectionModel().selectedItemProperty().addListener((observable ,oldValue, newValue)->{
            System.out.println("leftTableAddListener");
            if (newValue != null) {
                rightTable.setItems(getRightTableData(newValue.getId()));
                rightTable.refresh();
            }

        });
        if (tableView.getItems().size()>0) {
            QaydnomaData qaydnomaData = tableView.getItems().get(0);
            tableView.getSelectionModel().select(qaydnomaData);
        }
        tableView.setItems(getLeftTableData());
        tableView.setEditable(true);
        return  tableView;
    }

    private ObservableList<QaydnomaData> getLeftTableData() {
        QaydnomaModel qaydnomaModel = new QaydnomaModel();
        ObservableList<QaydnomaData> observableList = qaydnomaModel.getAnyData(connection, "amalturi =4 and userid=5 and id>930" , "");
        return observableList;
    }

    private TableView<HisobKitob> initRightTable() {
        TableView<HisobKitob> tableView = new TableView<>();
        TableViewAndoza tableViewAndoza = new TableViewAndoza();
        SetHVGrow.VerticalHorizontal(tableView);
        tableView.getColumns().addAll(
                getAmalColumn(),
                getHisob1Column(),
                getHisob2Column(),
                tableViewAndoza.getValutaColumn(),
                tableViewAndoza.getTovarColumn(),
                tableViewAndoza.getAdadColumn(),
                tableViewAndoza.getNarhColumn()
                );
        tableView.getSelectionModel().selectedItemProperty().addListener((observable ,oldValue, newValue)->{
            System.out.println("leftTableAddListener");
        });
        return  tableView;
    }

    private ObservableList<HisobKitob> getRightTableData(Integer qaydId) {
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        ObservableList<HisobKitob> observableList = hisobKitobModels.getAnyData(connection, "qaydId =" + qaydId, "");
        return observableList;
    }

    public TableColumn<QaydnomaData, Integer> getAmalTuriColumn() {
        TableColumn<QaydnomaData, Integer> amalTuriColumn = new TableColumn("Amal turi");
        amalTuriColumn.setMinWidth(30);
        amalTuriColumn.setCellValueFactory(new PropertyValueFactory<>("amalTuri"));
        amalTuriColumn.setCellFactory(column -> {
            TableCell<QaydnomaData, Integer> cell = new TableCell<QaydnomaData, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        Standart amal = GetDbData.getAmal(item);
                        if (amal != null) {
                            setText(amal.getText());
                        } else {
                            setText("");
                        }
                    }
                }
            };
//            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        return amalTuriColumn;
    }

    public TableColumn<QaydnomaData, String> getChiqimNomiColumn() {
        TableColumn<QaydnomaData, String> chiqimIdColumn = new TableColumn("Chiqim hisobi");
        chiqimIdColumn.setMinWidth(150);
        chiqimIdColumn.setCellValueFactory(new PropertyValueFactory<>("chiqimNomi"));
        return chiqimIdColumn;
    }

    public TableColumn<QaydnomaData, String> getKirimNomiColumn() {
        TableColumn<QaydnomaData, String> kirimNomiColumn = new TableColumn("Kirim hisobi");
        kirimNomiColumn.setMinWidth(150);
        kirimNomiColumn.setCellValueFactory(new PropertyValueFactory<>("kirimNomi"));
        return kirimNomiColumn;
    }

    private TableColumn<QaydnomaData, Integer> getAmalIdColumn() {
        TableColumn<QaydnomaData, Integer>  amalId = new TableColumn<>("Amal");
        amalId.setMinWidth(100);
        amalId.setMaxWidth(100);
        amalId.setCellValueFactory(new PropertyValueFactory<>("amalTuri"));
        amalId.setStyle( "-fx-alignment: CENTER;");
        amalId.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Integer>() {
            @Override
            public String toString(Integer object) {
                return String.valueOf(object);
            }

            @Override
            public Integer fromString(String string) {
                string = string.replaceAll(" ", "");
                string = string.replaceAll(",", ".");
                if (!Alerts.isNumericAlert(string)) {
                    string = "0.0";
                }
                return Integer.valueOf(string);
            }
        }));
        amalId.setOnEditCommit((TableColumn.CellEditEvent<QaydnomaData, Integer> event) -> {
            Integer newValue = event.getNewValue();
            QaydnomaData q = event.getRowValue();
            if (newValue != null) {
                QaydnomaModel qaydnomaModel = new QaydnomaModel();
                q.setAmalTuri(newValue);
                qaydnomaModel.update_data(connection, q);
            }
            event.getTableView().refresh();
        });
        return amalId;
    }

    public TableColumn<HisobKitob, Integer> getHisob1Column() {
        TableColumn<HisobKitob, Integer> hisob1 = new TableColumn<>("Hisob1");
        hisob1.setMinWidth(200);
        hisob1.setCellValueFactory(new PropertyValueFactory<>("hisob1"));
        hisob1.setCellFactory(column -> {
            TableCell<HisobKitob, Integer> cell = new TableCell<HisobKitob, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        Hisob hisob = GetDbData.getHisob(item);
                        if (hisob != null) {
                            setText(hisob.getText());
                        } else {
                            setText("");
                        }
                    }
                }
            };
            return cell;
        });
        return hisob1;
    }

    public TableColumn<HisobKitob, Integer> getHisob2Column() {
        TableColumn<HisobKitob, Integer> hisob = new TableColumn<>("Hisob");
        hisob.setMinWidth(200);
        hisob.setCellValueFactory(new PropertyValueFactory<>("hisob2"));
        hisob.setCellFactory(column -> {
            TableCell<HisobKitob, Integer> cell = new TableCell<HisobKitob, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        Hisob hisob = GetDbData.getHisob(item);
                        if (hisob != null) {
                            setText(hisob.getText());
                        } else {
                            setText("");
                        }
                    }
                }
            };
            return cell;
        });
        return hisob;
    }

    public TableColumn<HisobKitob, Integer> getAmalColumn() {
        TableColumn<HisobKitob, Integer> amalColumn = new TableColumn<>("Amal");
        amalColumn.setMinWidth(100);
        amalColumn.setCellValueFactory(new PropertyValueFactory<>("amal"));
        amalColumn.setCellFactory(column -> {
            TableCell<HisobKitob, Integer> cell = new TableCell<HisobKitob, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        Standart amal = GetDbData.getAmal(item);
                        if (amal != null) {
                            Text text = new Text(amal.getText());
                            text.setStyle("-fx-text-alignment:justify;");
                            text.wrappingWidthProperty().bind(getTableColumn().widthProperty().subtract(2));
                            setGraphic(text);
                        } else {
                            setText("Amalda xato");
                        }
                    }
                }
            };
            return cell;
        });
        return amalColumn;
    }


}
