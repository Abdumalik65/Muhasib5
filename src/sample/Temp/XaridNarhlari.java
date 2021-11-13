package sample.Temp;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.Config.MySqlDBGeneral;
import sample.Data.*;
import sample.Enums.ServerType;
import sample.Model.QaydnomaModel;
import sample.Tools.GetDbData;
import sample.Tools.TreeTableViewSample;

import java.sql.Connection;
import java.util.Date;

public class XaridNarhlari extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    VBox centerPane = new VBox();

    TreeItem<Standart> root = new TreeItem<Standart>(new Standart(1, "Asosiy", 1, null));
    TreeTableView<Standart> tovarTreeTableView = new TreeTableView<Standart>(root);
    ObservableList<Standart> tovarObservableList;
    ObservableList<BarCode> barCodeObservableList;

    User user = new User(1, "admin", "", "admin");

    Connection connection;
    int padding = 3;
    Integer amalTuri = 16;


    public static void main(String[] args) {
        launch(args);
    }

    public XaridNarhlari() {
    }

    public XaridNarhlari(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        ibtido();
    }

    private void ibtido() {
        initData();
        initTreeTableView();
        initCenterPane();
        initBorderPane();
    }

    private void initData() {
        GetDbData.initData(connection);
        tovarObservableList = GetDbData.getTovarObservableList();
        barCodeObservableList = GetDbData.getBarCodeObservableList();
    }

    private int getQaydnomaNumber() {
        QaydnomaModel qaydnomaModel = new QaydnomaModel();
        int qaydnomaInt = 1;
        ObservableList<QaydnomaData> qaydList = qaydnomaModel.getAnyData(connection, "amalTuri = " + amalTuri, "hujjat desc");
        if (qaydList.size()>0) {
            qaydnomaInt = qaydList.get(0).getHujjat() + 1;
        }
        return qaydnomaInt;
    }



    @Override
    public void start(Stage primaryStage) throws Exception {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        ibtido();
        initStage(primaryStage);
        stage.show();
    }

    public void display() {
        stage = new Stage();
        initStage(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    private void initCenterPane() {
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        VBox.setVgrow(centerPane, Priority.ALWAYS);
        centerPane.setPadding(new Insets(padding));
        centerPane.getChildren().addAll(tovarTreeTableView);
    }

    private void initBorderPane() {
        borderpane.setCenter(centerPane);
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Bir panel");
        scene = new Scene(borderpane, 600, 400);
        stage.setScene(scene);
    }

    private void initTreeTableView() {
        HBox.setHgrow(tovarTreeTableView,Priority.ALWAYS);
        VBox.setVgrow(tovarTreeTableView,Priority.ALWAYS);
        tovarTreeTableView.getColumns().add(getTextColumn());
        initTovarTreeItems();
        tovarTreeTableView.setRoot(root);

    }

    private void initTovarTreeItems() {
        for (Standart t: tovarObservableList) {
            TreeItem<Standart> tovarTreeItem = new TreeItem<>(t);
            root.getChildren().add(tovarTreeItem);
            ObservableList<BarCode> barCodes = GetDbData.getBarCodeList(t.getId());
            for (BarCode bc: barCodes) {
                TreeItem<BarCode> barCodesTreeItem = new TreeItem<>(bc);
            }
        }
    }

    private TreeTableColumn<Standart, String> getTextColumn() {
        TreeTableColumn<Standart, String> textColumn = new TreeTableColumn<>("Tovar");
        textColumn.setMinWidth(300);
        textColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<Standart, String> param) ->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getText())
        );
        return textColumn;
    }
}

