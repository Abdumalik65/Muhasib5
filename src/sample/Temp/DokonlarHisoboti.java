package sample.Temp;

import apple.laf.JRSUIUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import sample.Config.MySqlDBGeneral;
import sample.Data.*;
import sample.Enums.ServerType;
import sample.Model.HisobKitobModels;
import sample.Model.HisobModels;
import sample.Model.Standart3Models;
import sample.Tools.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DokonlarHisoboti extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    SplitPane centerPane = new SplitPane();
    VBox rightPane = new VBox();
    HBox leftPane = new HBox();
    TableView leftTable;
    DatePicker datePicker;
    TableView<Hisob> hisoblarJadvali;
    TableView<Standart3> rightTable;
    TreeView<Butoq> treeView;
    Tugmachalar tugmachalar;
    VBox chapLawha;

    ObservableList<Standart3> leftTableData;
    ObservableList centerTableData;
    ObservableList<Standart3> rightTableData;
    Map<String, TreeItem<Butoq>> kunlarButogiMap = new HashMap<>();

    Standart3Models standart3Models = new Standart3Models("Dokonlar");

    Connection connection;
    User user;
    int padding = 3;
    LocalDate localDate = LocalDate.now();



    public DokonlarHisoboti() {
        connection = new MySqlDBGeneral(ServerType.REMOTE).getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
        ibtido();
    }
    public DokonlarHisoboti(Connection connection, User user) {
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
    public static void main(String[] args) {
        launch(args);
    }

    private void initTopPane() {}
    private void initLeftPane() {
        leftPane.setPadding(new Insets(padding));
        HBox.setHgrow(leftPane, Priority.ALWAYS);
        VBox.setVgrow(leftPane, Priority.ALWAYS);
    }
    private void initCenterPane() {
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        VBox.setVgrow(centerPane, Priority.ALWAYS);
        chapLawha = chapLawha();
        centerPane.getItems().addAll(chapLawha, rightPane);
    }
    private void initRightPane() {
        rightPane.setPadding(new Insets(padding));
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        VBox.setVgrow(rightPane, Priority.ALWAYS);
        rightPane.setMinWidth(200);
        rightPane.setMaxWidth(200);
        treeView = initTreeView();
        datePicker = initDatePicker();
        rightPane.getChildren().addAll(datePicker, treeView);
    }
    private void initBottomPane() {}
    private DatePicker initDatePicker() {
        DatePicker datePicker = new DatePicker(localDate);
        datePicker.setMaxWidth(2000);
        datePicker.setMinWidth(115);
        datePicker.setPrefWidth(150);
        HBox.setHgrow(datePicker, Priority.ALWAYS);

        datePicker.setOnAction(event -> {
            LocalDate newDate = datePicker.getValue();
            if (newDate != null) {
                localDate = newDate;
                localDate.toString();
            }
        });
        // Converter
        StringConverter<LocalDate> converter = new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

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
        datePicker.setConverter(converter);
        return datePicker;
    }
    private void initBorderPane() {
        borderpane.setCenter(centerPane);
    }

    private Tugmachalar tugmachalar() {
        Tugmachalar tugmachalar = new Tugmachalar();
        Button add = tugmachalar.getAdd();
        add.setOnAction(event -> {});
        Button excel = tugmachalar.getExcel();
        excel.setOnAction(event -> {});
        return tugmachalar;
    }
    private VBox chapLawha() {
        VBox vBox = new VBox();
        SetHVGrow.VerticalHorizontal(vBox);
        Integer rowIndex = 0;
        rightTable = dokonlarJadvali();
        tugmachalar = tugmachalar();
        hisoblarJadvali = hisoblarJadvali();
        vBox.getChildren().addAll(rightTable, tugmachalar, hisoblarJadvali);
        return vBox;
    }

    private TableView<Standart3> dokonlarJadvali() {
        TableView<Standart3> tableView = new TableView<>();
        TableColumn<Standart3, String> textUstun = getTextColumn();
        rightTableData = dokonlarRoyxati();
        SetHVGrow.VerticalHorizontal(tableView);
        tableView.getColumns().addAll(getTextColumn());
        tableView.setItems(rightTableData);
        tableView.getSelectionModel().selectedItemProperty().addListener((observable ,oldValue, newValue)->{
            System.out.println("RightTableAddListener");
        });
        return  tableView;
    }
    private ObservableList<Date> sanalarRoyxati(TreeItem<Butoq> yillarButogi) {
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String select = "select distinct date(datetime) from hisobkitob group by (datetime) order by datetime ;";
        ResultSet rs = hisobKitobModels.getResultSet(connection, select);
        ObservableList<Date> timestampObservableList = FXCollections.observableArrayList();
        DateTimeFormatter dateFormatter =
                DateTimeFormatter.ofPattern("dd.MM.yyyy");
        Map<Integer, TreeItem<Butoq>> yillarButogiMap = new HashMap<>();
        Map<String, TreeItem<Butoq>> oylarButogiMap = new HashMap<>();
        try {
            while (rs.next()) {
                TreeItem<Butoq> yilButoq = null;
                TreeItem<Butoq> oyButoq = null;
                TreeItem<Butoq> kunButoq = null;
                Date date = rs.getDate(1);
                Timestamp timestamp = rs.getTimestamp(1);
                LocalDateTime localDateTime = timestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                int yil  = localDateTime.getYear();
                int oy = localDateTime.getMonthValue();
                int kun   = localDateTime.getDayOfMonth();
                LocalDate localDate = localDateTime.toLocalDate();
                if (!yillarButogiMap.containsKey(yil)) {
                    yilButoq = yangiYilTreeItem(yil);
                    yillarButogiMap.put(yil, yilButoq);
                    yillarButogi.getChildren().add(yilButoq);
                } else {
                    yilButoq = yillarButogiMap.get(yil);
                }
                String oyString = yil+"/"+oy;
                if (!oylarButogiMap.containsKey(oyString)) {
                    oyButoq =  yangiOyTreeItem(oy);
                    oylarButogiMap.put(oyString, oyButoq);
                    yilButoq.getChildren().add(oyButoq);
                } else {
                    oyButoq = oylarButogiMap.get(oyString);
                }
                String kunString = dateFormatter.format(localDateTime);
                if (!kunlarButogiMap.containsKey(kunString)) {
                    kunButoq =  yangiKunTreeItem(kunString);
                    kunlarButogiMap.put(kunString, kunButoq);
                    oyButoq.getChildren().add(kunButoq);
                } else {
                    kunButoq = kunlarButogiMap.get(kunString);
                }
                timestampObservableList.add(date);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return timestampObservableList;
    }
    private void yangiOngLawha() {
        TreeView<Butoq> treeView = initTreeView();
        SetHVGrow.VerticalHorizontal(treeView);
    }
    private TreeView<Butoq> initTreeView() {
        TreeView<Butoq> treeView = new TreeView<>();
        treeView.setMinWidth(230);
        SetHVGrow.VerticalHorizontal(treeView);
        TreeItem<Butoq> rootTreeItem = new TreeItem(getRootTreeItem());
        rootTreeItem.getChildren().addAll(
                jamiTreeItem()
        );

        treeView.setRoot(rootTreeItem);
        treeView.setShowRoot(false);
        treeView.setMaxWidth(280);
        return treeView;
    }

    private TreeItem<Butoq> jamiTreeItem() {
        Butoq butoq = new Butoq(01, new Label("Jami"));
        TreeItem<Butoq> treeItem = new TreeItem(butoq);
        sanalarRoyxati(treeItem);
        treeItem.setExpanded(true);
        return treeItem;
    }
    private TreeItem<Butoq> yangiYilTreeItem(Integer yil) {
        Butoq butoq = new Butoq(01, new Label(yil.toString()));
        TreeItem<Butoq> treeItem = new TreeItem(butoq);
        Label label = butoq.getLabel();
        treeItem.getChildren().addAll(
        );
        treeItem.setExpanded(false);
        return treeItem;
    }
    private TreeItem<Butoq> yangiOyTreeItem(Integer oyInteger) {
        String oyString = "";
        switch (oyInteger) {
            case 1:
                oyString = "Yanvar";
                break;
            case 2:
                oyString = "Fevral";
                break;
            case 3:
                oyString = "Mart";
                break;
            case 4:
                oyString = "Aprel";
                break;
            case 5:
                oyString = "May";
                break;
            case 6:
                oyString = "Iyyun";
                break;
            case 7:
                oyString = "Iyyul";
                break;
            case 8:
                oyString = "Avgust";
                break;
            case 9:
                oyString = "Sentyabr";
                break;
            case 10:
                oyString = "Oktyabr";
                break;
            case 11:
                oyString = "Noyabr";
                break;
            case 12:
                oyString = "Dekabr";
                break;
        }
        Butoq butoq = new Butoq(01, new Label(oyString));
        TreeItem<Butoq> treeItem = new TreeItem(butoq);
        treeItem.getChildren().addAll(
        );
        treeItem.setExpanded(false);
        return treeItem;
    }
    private TreeItem<Butoq> yangiKunTreeItem(String kun) {
        Butoq butoq = new Butoq(01, new Label(kun));
        TreeItem<Butoq> treeItem = new TreeItem(butoq);
        treeItem.getChildren().addAll(
        );
        treeItem.setExpanded(false);
        return treeItem;
    }

    private TreeItem<Butoq> getRootTreeItem() {
        Label label = new Label( "Jami");
        Butoq butoq = new Butoq(0, label);
        TreeItem<Butoq> treeItem = new TreeItem(butoq);
        return treeItem;
    }
    private ObservableList<Standart3> ongJadvalRoyxati() {
        standart3Models.setTABLENAME("Dokonlar");
        ObservableList<Standart3> observableList = standart3Models.get_data(connection);
        return observableList;
    }
    private TableView<Standart3> ortaJadval() {
        TableView<Standart3> tableView = new TableView<>();
        TableColumn<Standart3, String> textUstun = getTextColumn();
        rightTableData = dokonlarRoyxati();
        SetHVGrow.VerticalHorizontal(tableView);
        tableView.getColumns().addAll(getTextColumn());
        tableView.setItems(rightTableData);
        tableView.getSelectionModel().selectedItemProperty().addListener((observable ,oldValue, newValue)->{
            System.out.println("RightTableAddListener");
        });
        return  tableView;
    }
    private ObservableList<Standart3> dokonlarRoyxati() {
        standart3Models.setTABLENAME("Dokonlar");
        ObservableList<Standart3> observableList = standart3Models.get_data(connection);
        return observableList;
    }
    private TableColumn<Standart3, String> getTextColumn() {
        TableColumn<Standart3, String> textColumn = new TableColumn<>("Do'konlar");
        textColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
        textColumn.setCellFactory(tc -> {
            TableCell<Standart3, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            text.wrappingWidthProperty().bind(textColumn.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell ;
        });
        textColumn.setMinWidth(190);
        return textColumn;
    }
    private TableColumn<Standart, String> oraliqColumn() {
        TableColumn<Standart, String> textColumn = new TableColumn<>("Do'konlar");
        textColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
        textColumn.setCellFactory(tc -> {
            TableCell<Standart, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            text.wrappingWidthProperty().bind(textColumn.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell ;
        });
        textColumn.setMinWidth(190);
        return textColumn;
    }
    private TableView<Hisob> hisoblarJadvali() {
        TableViewAndoza tableViewAndoza = new TableViewAndoza();
        tableViewAndoza.initHisobTableView();
        TableView<Hisob> tableView = tableViewAndoza.getHisobTableView();
        SetHVGrow.VerticalHorizontal(tableView);
        HisobModels hisobModels = new HisobModels();
        ObservableList<Hisob> observableList = hisobModels.get_data(connection);
        tableView.setItems(observableList);
        tableView.getSelectionModel().selectedItemProperty().addListener((observable ,oldValue, newValue)->{
            System.out.println("HisobTableAddListener");
        });
        return  tableView;
    }

    private void initStage(Stage primaryStage) {
        scene = new Scene(borderpane);
        scene.setUserAgentStylesheet("sample/Styles/caspian.css");
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        stage = primaryStage;
        stage.setTitle("Do'konlar");
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
            stage.close();
        });
        stage.setScene(scene);
    }
}
