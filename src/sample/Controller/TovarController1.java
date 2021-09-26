package sample.Controller;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import com.jfoenix.controls.JFXButton;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import sample.Config.MySqlDB;
import sample.Config.MySqlDBLocal;
import sample.Data.*;
import sample.Model.*;
import sample.Temp.YangiTovarGuruhi;
import sample.Tools.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.*;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Optional;

public class TovarController1 extends Application {
    Stage stage;
    Scene scene;
    BorderPane borderpane = new BorderPane();
    SplitPane splitPane = new SplitPane();
    VBox rightPane = new VBox();
    HBox qidirHBox = new HBox();
    GridPane leftGridPane = new GridPane();
    GridPane rightGridPane = new GridPane();
    Label sarlawhaLabel = new Label();
    Label barCodeLabel = new Label("");
    VBox imagePane = new VBox();
    int padding = 3;
    DecimalFormat decimalFormat = new MoneyShow();

    Boolean doubleClick = false;
    Standart doubleClickedRow;
    Font font = Font.font("Arial", FontWeight.BOLD,20);

    Connection connection;
    User user = new User(1, "admin", "", "admin");

    Tugmachalar leftButtons = new Tugmachalar();
    Tugmachalar rightButtons = new Tugmachalar();

    TextField barCodeTextField = new TextField();
    TextField qidirBarCodeTextField = new TextField();
    TextField vaznTextField = new TextField();
    TextField hajmTextField = new TextField();

    StringBuffer stringBuffer = new StringBuffer();
    TextField tovarNomiTextField = new TextField();

    TableView<Standart> tovarTableView = new TableView<>();
    TableView<BarCode> barCodesTableView = new TableView<>();

    ObservableList<Standart> tovarObservableList;
    ObservableList<BarCode> barCodeObservableList = FXCollections.observableArrayList();
    ObservableList<Standart> birlikObservableList = FXCollections.observableArrayList();

    StandartModels standartModels = new StandartModels();
    Standart3Models standart3Models = new Standart3Models();
    NarhModels narhModels = new NarhModels();
    BarCodeModels barCodeModels = new BarCodeModels();
    TovarNarhiModels tovarNarhiModels = new TovarNarhiModels();

    BarCode barCodeCursor;
    Standart tovarCursor;
    TovarNarhi chakanaNarh;
    TovarNarhi ulgurjiNarh;

    GridPane barCodeGridPane = new GridPane();
    ComboBox<Standart> birlikComboBox = new ComboBox<>();
    ComboBox<BarCode> barCodeComboBox = new ComboBox<>();
    Label birlik2Label = new Label("");
    TextField barCodeTextField2 = new TextField();
    TextField adadTextField = new TextField();
    Button barCodeImageViewButton = new Button("");
    Label guruhLabel = new Label();

    Button newBarCodeButton = new Button("Yangi kod");
    Button qaydEtButton = new Button("Qayd et");
    Button cancelButton = new Button("<<");
    HBox barCodeHBox = new HBox();
    HBox buttonsHBox = new HBox();


    public static void main(String[] args) {
        launch(args);
    }

    public TovarController1() {
        connection = new MySqlDBLocal().getDbConnection();
    }

    public TovarController1(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
    }

    private void ibtido() {    HBox buttonsHBox = new HBox();

        initSplitPane();
        initData();
        initButtons();
        initTextFields();
        initSarlawhaLabel();
        initBarCodeGridPane();
        initTovarTableView();
        initBarCodesTableView();
        initBarCodeImageViewButton();
        initImagePane();
        initLeftGridPane();
        initBarCodeHbox();
        initRightPane();
        initCenterPane();
        initBorderPane();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        GetDbData.initData(connection);
        ibtido();
        initStage(primaryStage);
        stage.show();
    }

    public void display(Connection connection, User user) {
        stage = new Stage();
        ibtido();
        initStage(stage);

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    public Standart display() {
        stage = new Stage();
        ibtido();
        initStage(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        return doubleClickedRow;
    }

    private void initSplitPane() {
        splitPane.setDividerPositions(.25);
    }

    private Standart6 guruhniTop(int tovarId) {
        Standart6 s6 = null;
        standart3Models.setTABLENAME("TGuruh2");
        System.out.println(tovarId);
        ObservableList<Standart3> s3List = standart3Models.getAnyData(connection, "id3 = " + tovarId, "");
        guruhLabel.setText("");
        if (s3List.size()>0) {
            Standart3 s3 = s3List.get(0);
            Standart6Models standart6Models = new Standart6Models("TGuruh1");
            ObservableList<Standart6> s6List = standart6Models.getAnyData(connection, "id = " + s3.getId2(), "");
            if (s6List.size()>0) {
                s6 = s6List.get(0);
                guruhLabel.setText(s6.getText());
            }
        }
        return s6;
    }

    private void initData() {
        standartModels.setTABLENAME("Birlik");
        birlikObservableList = standartModels.get_data(connection);
        standartModels.setTABLENAME("Tovar");
        tovarObservableList = standartModels.get_data(connection);
    }

    private void initButtons() {

//        leftButtons.getChildren().addAll(tovarNomiTextField, barCodeTextField);

        leftButtons.getAdd().setOnAction(event -> {
            YangiTovar1 yangiTovar = new YangiTovar1(connection, user);
            Standart tovar = yangiTovar.display();
            if (tovar != null) {
                tovarObservableList.add(tovar);
                getTovar(tovar.getId());
                tovarTableView.setItems(tovarObservableList);
                tovarTableView.getSelectionModel().select(tovar);
                tovarTableView.scrollTo(tovar);
                tovarTableView.refresh();
            }
        });

        leftButtons.getEdit().setOnAction(event1 -> {
            Standart tovar = tovarTableView.getSelectionModel().getSelectedItem();
            if (tovar != null) {
                TextInputDialog dialog = new TextInputDialog(tovar.getText());
                dialog.setTitle("O`zgartirish");
                dialog.setHeaderText("Tovar nomini o`zgartiring");
                dialog.setContentText("Tovar: ");

                Optional<String> result = dialog.showAndWait();
                result.ifPresent(name -> {
                    tovar.setText(name);
                    standartModels.setTABLENAME("Tovar");
                    standartModels.update_data(connection, tovar);
                    tovarTableView.refresh();
                });
            }
        });

        leftButtons.getExcel().setOnAction(event -> {
            ExportToExcel exportToExcel = new ExportToExcel();
            exportToExcel.standartTable("Tovar", tovarObservableList);
        });

        leftButtons.getDelete().setOnAction(event1 -> {
            Standart tovar = tovarTableView.getSelectionModel().getSelectedItem();
            if (tovar != null) {
                ObservableList<BarCode> barCodeList = barCodeModels.getAnyData(connection, "tovar = " + tovar.getId(), "");
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.getButtonTypes().removeAll(alert.getButtonTypes());
                ButtonType okButton = new ButtonType("Ha");
                ButtonType noButton = new ButtonType("Yo`q");
                alert.getButtonTypes().addAll(okButton, noButton);
                alert.setTitle("Diqqat !!!");
                String barCodes = "";
                for (BarCode bc : barCodeList) {
                    barCodes += "\n" + getBirlik(bc.getBirlik()) + " " + bc.getBarCode() + " " + bc.getAdad();
                }
                if (!barCodes.isEmpty()) {
                    barCodes = "\nShtrix kodlar: " + barCodes;
                    ;
                }
                String headerText = "Tovar: " + tovar.getText() + barCodes;
                alert.setHeaderText(headerText);
                alert.setContentText("Tovar va unga taalluqli shtrix kodlar o`chiriladi\nDavom etaymi ???");
                Optional<ButtonType> option = alert.showAndWait();
                ButtonType buttonType = option.get();
                if (okButton.equals(buttonType)) {
                    tovarObservableList.remove(tovar);
                    standartModels.setTABLENAME("Tovar");
                    standartModels.delete_data(connection, tovar);
                    for (BarCode bc : barCodeList) {
                        barCodeModels.delete_data(connection, bc);
                        GetDbData.getBarCodeObservableList().removeIf(item -> item.getId().equals(bc.getId()));
                    }
                    barCodeObservableList.removeAll(barCodeObservableList);
                }
            }
            tovarTableView.setItems(tovarObservableList);
            tovarTableView.refresh();
            tovar = tovarTableView.getSelectionModel().getSelectedItem();
            if (tovar != null) {
                barCodeObservableList = barCodeModels.getAnyData(connection, "tovar = " + tovar.getId(), "");
            }
            barCodesTableView.setItems(barCodeObservableList);
            barCodesTableView.refresh();

        });

        rightButtons.getAdd().setOnAction(event -> {
            Standart tovarCursor = tovarTableView.getSelectionModel().getSelectedItem();
            barCodeTextField.setText("");
            int birlikInt = 1;
            int bcTarkibInt = 0;
            if (barCodeObservableList.size()>0) {
                BarCode bc =  barCodeObservableList.get(barCodeObservableList.size()-1);
                birlikInt = bc.getBirlik() + 1;
                bcTarkibInt = bc.getId();
            }
            BarCode barCode = new BarCode(null, tovarCursor.getId(), "", birlikInt, 1.0, bcTarkibInt, .0,.0,user.getId(), new Date());
            barCodeToGrid(barCode);
            leftGridPane.setDisable(true);
            rightPane.getChildren().removeAll(rightPane.getChildren());
            rightPane.getChildren().addAll(sarlawhaLabel, barCodeGridPane);

            qaydEtButton.setOnAction(event1 -> {
                gridToBarCode(barCode);
                BarCode newBarCode = GetDbData.getBarCode(barCode.getBarCode());
                if (newBarCode == null) {
                    barCodeObservableList.add(barCode);
                    barCodesTableView.setItems(barCodeObservableList);
                    barCodeModels.insert_data(connection, barCode);
                    GetDbData.getBarCodeObservableList().add(barCode);
                    barCodesTableView.getSelectionModel().select(barCode);
                    barCodesTableView.scrollTo(barCode);
                    barCodesTableView.refresh();
                    cancelButton.fire();
                }
                else {
                    Standart tovar = getTovar(newBarCode.getTovar());
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Diqqat qiling !!!");
                    alert.setHeaderText(tovar.getText() + "\n" + newBarCode.getBarCode());
                    alert.setContentText("Bu shtrix kod bazada bor");
                    alert.showAndWait();
                }
            });

            cancelButton.setOnAction(event1 -> {
                rightPane.getChildren().removeAll(rightPane.getChildren());
                rightPane.getChildren().addAll(sarlawhaLabel, rightButtons, barCodesTableView, barCodeImageViewButton);
                leftGridPane.setDisable(false);
            });
        });

        rightButtons.getDelete().setOnAction(event -> {
            BarCode barCode = barCodesTableView.getSelectionModel().getSelectedItem();
            if (barCode != null) {
                barCodeToGrid(barCode);
                leftGridPane.setDisable(true);
                rightPane.getChildren().removeAll(rightPane.getChildren());
                rightPane.getChildren().addAll(sarlawhaLabel, barCodeGridPane);

                qaydEtButton.setOnAction(event1 -> {
                    barCodeObservableList.remove(barCode);
                    GetDbData.getBarCodeObservableList().removeIf(item -> item.getId().equals(barCode.getId()));
                    barCodeModels.delete_data(connection, barCode);
                    barCodesTableView.refresh();
                    cancelButton.fire();
                });

                cancelButton.setOnAction(event1 -> {
                    rightPane.getChildren().removeAll(rightPane.getChildren());
                    rightPane.getChildren().addAll(sarlawhaLabel, rightButtons, barCodesTableView, barCodeImageViewButton);
                    leftGridPane.setDisable(false);
                });
            }
        });

        rightButtons.getEdit().setOnAction(event -> {
            BarCode barCode = barCodesTableView.getSelectionModel().getSelectedItem();
            if (barCode != null) {
                String barCodeAwwal = barCode.getBarCode();
                barCodeToGrid(barCode);
                barCodeTextField2.setDisable(false);
                leftGridPane.setDisable(true);
                rightPane.getChildren().removeAll(rightPane.getChildren());
                rightPane.getChildren().addAll(sarlawhaLabel, barCodeGridPane);

                qaydEtButton.setOnAction(event1 -> {
                    barCodeTextField2.setDisable(false);
                    gridToBarCode(barCode);
                    GetDbData.
                    barCodeModels.update_data(connection, barCode);
                    barCodesTableView.getSelectionModel().select(barCode);
                    barCodesTableView.refresh();
                    cancelButton.fire();
                });
                cancelButton.setOnAction(event1 -> {
                    rightPane.getChildren().removeAll(rightPane.getChildren());
                    rightPane.getChildren().addAll(sarlawhaLabel, rightButtons, barCodesTableView, barCodeImageViewButton);
                    leftGridPane.setDisable(false);
                });
            }
        });
    }

    private void initBarCodeHbox() {
        HBox.setHgrow(barCodeHBox, Priority.ALWAYS);
        HBox.setHgrow(barCodeTextField, Priority.ALWAYS);
        barCodeHBox.getChildren().addAll(barCodeTextField, newBarCodeButton);
        newBarCodeButton.setOnAction(event -> {
            ObservableList<BarCode> bcList = barCodeModels.getAnyData(connection, "SUBSTR(barCode,1,2) = '" + ConnectionType.getAloqa().getDbPrefix().trim() + "'", "id desc");
            if (bcList.size()>0) {
                String string = bcList.get(0).getBarCode();
                Integer number = Integer.valueOf(string.substring(2));
                number++;
                string = ConnectionType.getAloqa().getDbPrefix() + padLeft(number.toString().trim(), 5, '0');
                barCodeTextField.setText(string);
            }
        });
    }

    private void initSarlawhaLabel() {
        sarlawhaLabel.setFont(font);
        sarlawhaLabel.setWrapText(true);
    }


    private void initTextFields() {
/*
        HBox.setHgrow(tovarNomiTextField, Priority.ALWAYS);
        HBox.setHgrow(barCodeTextField, Priority.ALWAYS);
*/
        HBox.setHgrow(tovarNomiTextField, Priority.ALWAYS);
        HBox.setHgrow(qidirBarCodeTextField, Priority.ALWAYS);
        qidirBarCodeTextField.setPromptText("Shtrix kod");
        qidirBarCodeTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                stringBuffer.delete(0, stringBuffer.length());
                String string = qidirBarCodeTextField.getText().trim();
                if (event.getCode()== KeyCode.ENTER) {
                    BarCode barCode = barCodeModels.getBarCode(connection, string);
                    if (barCode !=  null) {
                        Standart tovar = getTovar(barCode.getTovar());
                        qidirBarCodeTextField.setText("");
                        tovarTableView.getSelectionModel().select(tovar);
                        tovarTableView.scrollTo(tovar);
                        tovarTableView.requestFocus();
                        tovarTableView.refresh();
                        barCodesTableView.getSelectionModel().select(barCode);
                        barCodesTableView.scrollTo(barCode);
                        barCodesTableView.refresh();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Diqqat !!!");
                        alert.setHeaderText(string +  "\n shtrix kodga muvofiq tovar topiilmadi" );
                        alert.setContentText("");
                        alert.showAndWait();
                    }
                }
            }
        });
/*
        tovarNomiTextField.setPromptText("Tovar nomi");
        TextFields.bindAutoCompletion(tovarNomiTextField, tovarObservableList).setOnAutoCompleted((AutoCompletionBinding.AutoCompletionEvent<Standart> autoCompletionEvent) -> {
            Standart tovar = autoCompletionEvent.getCompletion();
            tovarTableView.getSelectionModel().select(tovar);
            tovarTableView.scrollTo(tovar);
            tovarNomiTextField.setText("");
        });
*/
        tovarNomiTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            Taftish(oldValue, newValue);
        });

        barCodeTextField.setPromptText("Shtrix kod");
    }

    private void initBarCodeImageViewButton() {
        barCodeImageViewButton.setOnAction(event -> {
            String mahsulotNomi = "";
            String birlikString = "";
            Standart tovar = tovarTableView.getSelectionModel().getSelectedItem();
            if (tovar != null) {
                mahsulotNomi = tovar.getText();
            }
            BarCode bc = barCodesTableView.getSelectionModel().getSelectedItem();
            if (bc != null) {
                String path = "PrintedBarCode.pdf";
                Document document = new Document();
                PdfWriter pdfWriter = null;
                try {
                    pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(path));
                } catch (DocumentException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Rectangle one = new Rectangle(156,85);
                one.rotate();
                document.setPageSize(one);
                document.setMargins(0, 0, 0, 0);
                document.open();
                
                Paragraph paragraph = new Paragraph(tovar.getText());
                paragraph.setAlignment(Element.ALIGN_CENTER);
                com.itextpdf.text.Font font = new com.itextpdf.text.Font();
                font.setSize(6);
                font.setFamily("Arial");
                paragraph.setFont(font);

                try {
                    Barcode128 barcode128 = new Barcode128();
                    barcode128.setCode(bc.getBarCode());
                    barcode128.setCodeType(Barcode128.CODE128);
                    PdfContentByte pdfContentByte = pdfWriter.getDirectContent();
                    com.itextpdf.text.Image code128Image = barcode128.createImageWithBarcode(pdfContentByte, BaseColor.BLACK, BaseColor.BLACK);
                    code128Image.scalePercent(100);
                    code128Image.setAlignment(Element.ALIGN_CENTER);
                    document.add(paragraph);
                    document.add(code128Image);
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                document.close();
            }
        });
    }

    private void initLeftGridPane() {
        leftGridPane.setPadding(new Insets(padding));
        leftGridPane.setVgap(padding);
        leftGridPane.setHgap(padding);
        HBox.setHgrow(leftGridPane, Priority.ALWAYS);
        VBox.setVgrow(leftGridPane, Priority.ALWAYS);

        int rowIndex = 0;
        leftGridPane.add(leftButtons, 0, rowIndex, 2, 1);

        rowIndex++;
        leftGridPane.add(tovarNomiTextField, 0, rowIndex, 1,1);
        leftGridPane.add(qidirBarCodeTextField, 1, rowIndex,1, 1);
        GridPane.setHgrow(tovarNomiTextField, Priority.ALWAYS);
        GridPane.setHgrow(qidirBarCodeTextField, Priority.ALWAYS);

        rowIndex++;
        leftGridPane.add(tovarTableView, 0, rowIndex, 2, 1);
        GridPane.setHgrow(tovarTableView, Priority.ALWAYS);
        GridPane.setVgrow(tovarTableView, Priority.ALWAYS);
    }

    private void initTovarTableView() {
        HBox.setHgrow(tovarTableView, Priority.ALWAYS);
        VBox.setVgrow(tovarTableView, Priority.ALWAYS);
        tovarTableView.getColumns().addAll(getIdColumn(), getTextColumn()/*, getXaridNarhiColumn(), getUlgurjiNarhColumn(), getChakanaNarhColumn()*/);
        tovarTableView.setItems(tovarObservableList);
        tovarTableView.setEditable(true);
        if (tovarObservableList.size()>0) {
            tovarCursor = tovarObservableList.get(0);
            guruhniTop(tovarCursor.getId());
            chakanaNarh = sotishNarhi(tovarCursor.getId(), 1);
            ulgurjiNarh = sotishNarhi(tovarCursor.getId(), 2);
            sarlawhaLabel.setText(tovarCursor.getText());
            tovarTableView.getSelectionModel().selectFirst();
            barCodeObservableList = barCodeModels.getAnyData(connection,"tovar = " + tovarCursor.getId(), "");
            barCodesTableView.setItems(barCodeObservableList);
            if (barCodeObservableList.size()>0) {
                String bcStr = barCodeObservableList.get(0).getBarCode();
                barCodeLabel.setText(bcStr);
                barCodeImageViewButton.setGraphic(getBarCodeImageView(bcStr));
            }
            barCodesTableView.refresh();
        }
        tovarTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue !=  null) {
                tovarCursor = newValue;
                guruhniTop(tovarCursor.getId());
                standart3Models.setTABLENAME("TGuruh2");
                chakanaNarh = sotishNarhi(tovarCursor.getId(), 1);
                ulgurjiNarh = sotishNarhi(tovarCursor.getId(), 2);
                sarlawhaLabel.setText(tovarCursor.getText());
                sarlawhaLabel.setWrapText(true);
                barCodeObservableList = barCodeModels.getAnyData(connection,"tovar = " + tovarCursor.getId(), "");
                barCodesTableView.setItems(barCodeObservableList);
                if (barCodeObservableList.size()>0) {
                    String bcStr = barCodeObservableList.get(0).getBarCode();
                    barCodeLabel.setText(bcStr);
                    barCodeImageViewButton.setGraphic(getBarCodeImageView(bcStr));
                }
                barCodesTableView.refresh();
            }
        });
        tovarTableView.setRowFactory( tv -> {
            TableRow<Standart> row = new TableRow<>();
            row.setOnMouseClicked(event1 -> {
                if (event1.getClickCount() == 2 && (! row.isEmpty()) ) {
                    doubleClick = true;
                    doubleClickedRow = row.getItem();
                    stage.close();
                }
            });
            return row ;
        });
        tovarTableView.requestFocus();
    }

    private Narh xaridNarhi(Standart tovar) {
        Narh xaridNarhi = null;
        ObservableList<Narh> list = narhModels.getDate(connection, tovar.getId(), new Date(), "sana desc");
        if (list.size()>0) {
            xaridNarhi = list.get(0);
        }
        return xaridNarhi;
    }

    private TovarNarhi sotishNarhi(Integer tovarId, Integer narhTuri) {
        Date date = new Date();
        TovarNarhi tovarNarhi = null;
        ObservableList<TovarNarhi> list = tovarNarhiModels.getDate3(connection, tovarId, narhTuri, date,"sana desc");
        if (list.size()>0) {
            tovarNarhi = list.get(0);
        }
        return tovarNarhi;
    }

    private TovarNarhi getTovarNarhi(Standart tovar, Integer narhTuri) {
        TovarNarhi narh = null;
        ObservableList<TovarNarhi> list = tovarNarhiModels.getDate3(connection, tovar.getId(), narhTuri, new Date(), "sana desc");
        if (list.size()>0) {
            narh = list.get(0);
        }
        return narh;
    }

    private void initBarCodesTableView() {
        HBox.setHgrow(barCodesTableView, Priority.ALWAYS);
        VBox.setVgrow(barCodesTableView, Priority.ALWAYS);
        barCodesTableView.setEditable(true);
        barCodesTableView.getColumns().addAll(getBirlikColumn(), getBarCodeColumn(), getAdadColumn(), getBirlik2Column(), getBarCode2Column(), getVaznColumn(), getHajmColumn());
        barCodesTableView.setItems(barCodeObservableList);
        /*
        id
        barcode
        birlik
        dona
        delete
        */
        barCodesTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                barCodeCursor = newValue;
                String bcStr = barCodeCursor.getBarCode();
                barCodeLabel.setText(bcStr);
                barCodeImageViewButton.setGraphic(getBarCodeImageView(bcStr));
            }
        });
    }

    private TableColumn<Standart, Integer> getIdColumn() {
        TableColumn<Standart, Integer> idColumn = new TableColumn<>("N");
        idColumn.setMinWidth(15);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        return idColumn;
    }

    private TableColumn<Standart, String> getTextColumn() {
        TableColumn<Standart, String> textColumn = new TableColumn<>("Tovar");
        textColumn.setMinWidth(150);
        textColumn.setCellValueFactory(new PropertyValueFactory<>("text"));

        textColumn.setCellFactory(tc -> {
            TableCell<Standart, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(textColumn.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell ;
        });
        return textColumn;
    }

    private  TableColumn<Standart, Double> getXaridNarhiColumn() {
        TableColumn<Standart, Double>  xaridColumn = new TableColumn<>("Xarid");
        xaridColumn.setMinWidth(100);
        xaridColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Standart, Double>, ObservableValue<Double>>() {

            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<Standart, Double> param) {
                Standart tovar = param.getValue();
                Double xaridNarhiDouble = 0.0;
                Narh xaridNarhi = xaridNarhi(tovar);
                if (xaridNarhi != null) {
                    xaridNarhiDouble = xaridNarhi.getXaridDouble();
                }
                return new SimpleObjectProperty<Double>(xaridNarhiDouble);
            }
        });
        xaridColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setMinimumIntegerDigits (1);
                numberFormat.setMaximumIntegerDigits (8);

                numberFormat.setMinimumFractionDigits (1);
                numberFormat.setMaximumFractionDigits (5);
                return numberFormat.format(object);
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
        xaridColumn.setOnEditCommit(event -> {
            Standart tovar = event.getRowValue();
            Double newValue = event.getNewValue();
            if (newValue != null) {
                Narh yangiNarh = new Narh(null, tovar.getId(), new Date(), newValue, user.getId(), null);
                narhModels.insert_data(connection, yangiNarh);
                tovarTableView.refresh();
            }
            tovarTableView.requestFocus();
        });
        xaridColumn.setStyle( "-fx-alignment: CENTER;");
        return xaridColumn;
    }

    private  TableColumn<Standart, Double> getChakanaNarhColumn() {
        TableColumn<Standart, Double>  chakanaColumn = new TableColumn<>("Штучный");
        chakanaColumn.setMinWidth(100);
        chakanaColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Standart, Double>, ObservableValue<Double>>() {

            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<Standart, Double> param) {
                Standart tovar = param.getValue();
                Double narhDouble = 0.0;
                TovarNarhi narh = getTovarNarhi(tovar, 1);

                if (narh != null) {
                    narhDouble = narh.getNarh();

                }
                return new SimpleObjectProperty<Double>(narhDouble);
            }
        });
        chakanaColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setMinimumIntegerDigits (1);
                numberFormat.setMaximumIntegerDigits (8);

                numberFormat.setMinimumFractionDigits (1);
                numberFormat.setMaximumFractionDigits (5);
                return numberFormat.format(object);
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
        chakanaColumn.setOnEditCommit(event -> {
            Standart tovar = event.getRowValue();
            Double newValue = event.getNewValue();
            if (newValue != null) {
                Date date = new Date();
                TovarNarhi yangiNarh = new TovarNarhi(null, date, tovar.getId(), 1, 1, 1.0, newValue, user.getId(), null);
                tovarNarhiModels.insert_data(connection, yangiNarh);
                chakanaNarh = yangiNarh;
                tovarTableView.refresh();
            }
        });
        tovarTableView.requestFocus();
        chakanaColumn.setStyle( "-fx-alignment: CENTER;");
        return chakanaColumn;
    }

    private  TableColumn<Standart, Double> getUlgurjiNarhColumn() {
        TableColumn<Standart, Double>  ulgurjiColumn = new TableColumn<>("Оптом");
        ulgurjiColumn.setMinWidth(100);
        ulgurjiColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Standart, Double>, ObservableValue<Double>>() {

            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<Standart, Double> param) {
                Standart tovar = param.getValue();
                Double narhDouble = 0.0;
                TovarNarhi narh = getTovarNarhi(tovar, 2);

                if (narh != null) {
                    narhDouble = narh.getNarh();

                }
                return new SimpleObjectProperty<Double>(narhDouble);
            }
        });
        ulgurjiColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setMinimumIntegerDigits (1);
                numberFormat.setMaximumIntegerDigits (8);

                numberFormat.setMinimumFractionDigits (1);
                numberFormat.setMaximumFractionDigits (5);
                return numberFormat.format(object);
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
        ulgurjiColumn.setOnEditCommit(event -> {
            Standart tovar = event.getRowValue();
            Double newValue = event.getNewValue();
            if (newValue != null) {
                Date date = new Date();
                TovarNarhi yangiNarh = new TovarNarhi(null, date, tovar.getId(), 2, 1, 1.0, newValue, user.getId(), null);
                tovarNarhiModels.insert_data(connection, yangiNarh);
                ulgurjiNarh = yangiNarh;
                tovarTableView.refresh();
            }
            tovarTableView.requestFocus();
        });
        ulgurjiColumn.setStyle( "-fx-alignment: CENTER;");
        return ulgurjiColumn;
    }

    private TableColumn<Standart, Button> getEditColumn() {
        TableColumn<Standart, Button> editColumn = new TableColumn<>("O`zgartir");
        editColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Standart, Button>, ObservableValue<Button>>() {

            @Override
            public ObservableValue<Button> call(TableColumn.CellDataFeatures<Standart, Button> param) {
                Standart tovar = param.getValue();
                JFXButton b = new JFXButton("");
                b.setMaxWidth(2000);
                b.setPrefWidth(150);
                HBox.setHgrow(b, Priority.ALWAYS);
                InputStream inputStream = getClass().getResourceAsStream("/sample/images/Icons/edit.png");
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
                    TextInputDialog dialog = new TextInputDialog(tovar.getText());
                    dialog.setTitle("O`zgartirish");
                    dialog.setHeaderText("Tovar nomini o`zgartiring");
                    dialog.setContentText("Tovar: ");

                    Optional<String> result = dialog.showAndWait();
                    result.ifPresent(name -> {
                        tovar.setText(name);
                        standartModels.setTABLENAME("Tovar");
                        standartModels.update_data(connection, tovar);
                        tovarTableView.refresh();
                    });
                });
                return new SimpleObjectProperty<Button>(b);
            }
        });

        editColumn.setMinWidth(100);
        editColumn.setMaxWidth(100);
        editColumn.setStyle( "-fx-alignment: CENTER;");
        return editColumn;
    }

    private TableColumn<Standart, Button> getDeleteColumn() {
        TableColumn<Standart, Button> deleteColumn = new TableColumn<>("O`chir");
        deleteColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Standart, Button>, ObservableValue<Button>>() {

            @Override
            public ObservableValue<Button> call(TableColumn.CellDataFeatures<Standart, Button> param) {
                Standart tovar = param.getValue();
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
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.getButtonTypes().removeAll(alert.getButtonTypes());
                    ButtonType okButton = new ButtonType("Ha");
                    ButtonType noButton = new ButtonType("Yo`q");
                    alert.getButtonTypes().addAll(okButton, noButton);
                    alert.setTitle("Diqqat !!!");
                    String barCodes = "";
                    for (BarCode bc: barCodeObservableList) {
                        barCodes += "\n" + getBirlik(bc.getBirlik()) + " " + bc.getBarCode() + " " + bc.getAdad();
                    }
                    if (!barCodes.isEmpty()) {
                        barCodes = "\nShtrix kodlar: " + barCodes;;
                    }
                    String headerText = "Tovar: " + tovar.getText() + barCodes;
                    alert.setHeaderText(headerText);
                    alert.setContentText("Tovar va unga taalluqli shtrix kodlar o`chiriladi\nDavom etaymi ???");
                    Optional<ButtonType> option = alert.showAndWait();
                    ButtonType buttonType = option.get();
                    if (okButton.equals(buttonType)) {
                        tovarObservableList.remove(tovar);
                        standartModels.setTABLENAME("Tovar");
                        standartModels.delete_data(connection, tovar);
                        for (BarCode bc: barCodeObservableList) {
                            barCodeModels.delete_data(connection, bc);
                        }
                        barCodeObservableList.removeAll(barCodeObservableList);
                    }
                });
                return new SimpleObjectProperty<Button>(b);
            }
        });

        deleteColumn.setMinWidth(40);
        deleteColumn.setMaxWidth(40);
        deleteColumn.setStyle( "-fx-alignment: CENTER;");
        return deleteColumn;
    }

    private TableColumn<BarCode, String> getBarCodeColumn() {
        TableColumn<BarCode, String> textColumn = new TableColumn<>("Shtrixkod");
        textColumn.setMinWidth(130);
        textColumn.setCellValueFactory(new PropertyValueFactory<>("barCode"));

        return textColumn;
    }

    private TableColumn<BarCode, Integer> getBirlikColumn() {
        TableColumn<BarCode, Integer> taqdimColumn = new TableColumn<>("Birlik");
        taqdimColumn.setCellValueFactory(new PropertyValueFactory<>("birlik"));
        taqdimColumn.setCellFactory(column -> {
            TableCell<BarCode, Integer> cell = new TableCell<BarCode, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        if (item != null) {
                            Standart birlik = GetDbData.getBirlik(item);
                            if (birlik != null) {
                                setText(birlik.getText());
                            } else {
                                setText("");
                            }
                        }
                    }
                    setAlignment(Pos.CENTER_LEFT);
                }
            };
            return cell;
        });
        taqdimColumn.setStyle( "-fx-alignment: CENTER;");
        taqdimColumn.setMinWidth(80);
        return taqdimColumn;
    }

    private TableColumn<BarCode, Integer> getBirlik2Column() {
        TableColumn<BarCode, Integer> taqdimColumn = new TableColumn<>("Birlik2");
        taqdimColumn.setCellValueFactory(new PropertyValueFactory<>("tarkib"));
        taqdimColumn.setCellFactory(column -> {
            TableCell<BarCode, Integer> cell = new TableCell<BarCode, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }  else {
                        BarCode barCode = null;
                        for (BarCode bc: barCodeObservableList) {
                            if (bc.getId().equals(item)) {
                                barCode = bc;
                                setText(getBirlik(barCode.getBirlik()));
                            }
                        }
                    }
                    setAlignment(Pos.CENTER_LEFT);
                }
            };
            return cell;
        });
        taqdimColumn.setStyle( "-fx-alignment: CENTER;");
        taqdimColumn.setMinWidth(80);
        return taqdimColumn;
    }

    private TableColumn<BarCode, Integer> getBarCode2Column() {
        TableColumn<BarCode, Integer> taqdimColumn = new TableColumn<>("Shtrixkod2");
        taqdimColumn.setCellValueFactory(new PropertyValueFactory<>("tarkib"));
        taqdimColumn.setCellFactory(column -> {
            TableCell<BarCode, Integer> cell = new TableCell<BarCode, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        BarCode barCode = null;
                        for (BarCode bc: barCodeObservableList) {
                            if (bc.getId().equals(item)) {
                                barCode = bc;
                                setText(bc.getBarCode());
                                break;
                            }
                        }
                    }
                    setAlignment(Pos.CENTER_LEFT);
                }
            };
            return cell;
        });
        taqdimColumn.setStyle( "-fx-alignment: CENTER;");
        taqdimColumn.setMinWidth(120);
        return taqdimColumn;
    }

    private  TableColumn<BarCode, Double> getAdadColumn() {
        TableColumn<BarCode, Double>  adad = new TableColumn<>("Adad");
        adad.setMinWidth(50);
        adad.setCellValueFactory(new PropertyValueFactory<>("adad"));
        adad.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {

                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setMinimumIntegerDigits (1);
                numberFormat.setMaximumIntegerDigits (8);

                numberFormat.setMinimumFractionDigits (0);
                numberFormat.setMaximumFractionDigits (5);
                return numberFormat.format(object);
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
        adad.setOnEditCommit(event -> {
            Double newValue = event.getNewValue();
            BarCode bc = event.getRowValue();
            if (newValue != null) {
                bc.setAdad(newValue);
                barCodeModels.update_data(connection, bc);
                barCodesTableView.refresh();
            }
        });
        adad.setStyle( "-fx-alignment: CENTER;");
        return adad;
    }

    private  TableColumn<BarCode, Double> getVaznColumn() {
        TableColumn<BarCode, Double>  vaznColumn = new TableColumn<>("Vazn");
        vaznColumn.setMinWidth(100);
        vaznColumn.setCellValueFactory(new PropertyValueFactory<>("vazn"));
        vaznColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setMinimumIntegerDigits (1);
                numberFormat.setMaximumIntegerDigits (8);

                numberFormat.setMinimumFractionDigits (1);
                numberFormat.setMaximumFractionDigits (5);
                return numberFormat.format(object);
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
        vaznColumn.setOnEditCommit(event -> {
            Double newValue = event.getNewValue();
            BarCode bc = event.getRowValue();
            if (newValue != null) {
                bc.setVazn(newValue);
                barCodeModels.update_data(connection, bc);
                barCodesTableView.refresh();
            }
        });
        vaznColumn.setStyle( "-fx-alignment: CENTER;");
        return vaznColumn;
    }

    private  TableColumn<BarCode, Double> getHajmColumn() {
        TableColumn<BarCode, Double>  hajmColumn = new TableColumn<>("Hajm");
        hajmColumn.setMinWidth(100);
        hajmColumn.setCellValueFactory(new PropertyValueFactory<>("hajm"));
        hajmColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setMinimumIntegerDigits (1);
                numberFormat.setMaximumIntegerDigits (8);

                numberFormat.setMinimumFractionDigits (1);
                numberFormat.setMaximumFractionDigits (5);
                return numberFormat.format(object);
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
        hajmColumn.setOnEditCommit(event -> {
            Double newValue = event.getNewValue();
            BarCode bc = event.getRowValue();
            if (newValue != null) {
                bc.setHajm(newValue);
                barCodeModels.update_data(connection, bc);
                barCodesTableView.refresh();
            }
        });
        hajmColumn.setStyle( "-fx-alignment: CENTER;");
        return hajmColumn;
    }

    private TableColumn<BarCode, Button> getBarCodeEditColumn() {
        TableColumn<BarCode, Button> editColumn = new TableColumn<>("O`zgartir");
        editColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<BarCode, Button>, ObservableValue<Button>>() {

            @Override
            public ObservableValue<Button> call(TableColumn.CellDataFeatures<BarCode, Button> param) {
                BarCode barCode = param.getValue();
                JFXButton b = new JFXButton("");
                b.setMaxWidth(2000);
                b.setPrefWidth(150);
                HBox.setHgrow(b, Priority.ALWAYS);
                InputStream inputStream = getClass().getResourceAsStream("/sample/images/Icons/edit.png");
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
                    TextInputDialog dialog = new TextInputDialog(barCode.getBarCode());
                    dialog.setTitle("O`zgartirish");
                    dialog.setHeaderText("Shtrix kodni o`zgartiring");
                    dialog.setContentText("Shtrix kod: ");

                    Optional<String> result = dialog.showAndWait();
                    result.ifPresent(name -> {
                        barCode.setBarCode(name);
                        barCodeModels.update_data(connection, barCode);
                        barCodesTableView.refresh();
                    });
                });
                return new SimpleObjectProperty<Button>(b);
            }
        });

        editColumn.setMinWidth(80);
        editColumn.setMaxWidth(80);
        editColumn.setStyle( "-fx-alignment: CENTER;");
        return editColumn;
    }

    private TableColumn<BarCode, Button> getBarCodeDeleteColumn() {
        TableColumn<BarCode, Button> deleteColumn = new TableColumn<>("O`chir");
        deleteColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<BarCode, Button>, ObservableValue<Button>>() {

            @Override
            public ObservableValue<Button> call(TableColumn.CellDataFeatures<BarCode, Button> param) {
                BarCode barCode = param.getValue();
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
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.getButtonTypes().removeAll(alert.getButtonTypes());
                    ButtonType okButton = new ButtonType("Ha");
                    ButtonType noButton = new ButtonType("Yo`q");
                    alert.getButtonTypes().addAll(okButton, noButton);
                    alert.setTitle("Diqqat !!!");
                    alert.setHeaderText("Shtrix kod: "  +  getBirlik(barCode.getBirlik()) + " " + barCode.getBarCode() + " " + barCode.getAdad());
                    alert.setContentText("Shtrix kod o`chiriladi. Davom etaymi ???");
                    Optional<ButtonType> option = alert.showAndWait();
                    ButtonType buttonType = option.get();
                    if (okButton.equals(buttonType)) {
                        barCodeObservableList.remove(barCode);
                        barCodeModels.delete_data(connection, barCode);
                    }
                });
                return new SimpleObjectProperty<Button>(b);
            }
        });

        deleteColumn.setMinWidth(40);
        deleteColumn.setMaxWidth(40);
        deleteColumn.setStyle( "-fx-alignment: CENTER;");
        return deleteColumn;
    }

    private void initCenterPane() {
        HBox.setHgrow(splitPane, Priority.ALWAYS);
        VBox.setVgrow(splitPane, Priority.ALWAYS);
        splitPane.getItems().addAll(leftGridPane, rightPane);
    }

    private void initRightPane() {
        rightPane.setPadding(new Insets(padding));
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        VBox.setVgrow(rightPane, Priority.ALWAYS);
        rightPane.getChildren().addAll(sarlawhaLabel, guruhLabel, rightButtons, barCodesTableView, barCodeImageViewButton, barCodeLabel);
    }

    private void initImagePane() {
        imagePane.setPadding(new Insets(padding));
        HBox.setHgrow(imagePane, Priority.ALWAYS);
        VBox.setVgrow(imagePane , Priority.ALWAYS);
    }

    private void initBorderPane() {
        borderpane.setCenter(splitPane);
    }

    private void initStage(Stage primaryStage) {
        stage = primaryStage;
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        stage.setX(bounds.getMinX() - 3);
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth() + 7);
        stage.setHeight(bounds.getHeight() + 6);
        stage.setResizable(false);
        stage.setTitle("Tovarlar");

        scene = new Scene(borderpane, 1000, 500);
        stage.setScene(scene);
        barCodeOn();
        stage.setOnCloseRequest(event -> {
            barCodeOff();
        });
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
                    String string = stringBuffer.toString().trim();
                    if (!string.isEmpty()) {
                        stringBuffer.delete(0, stringBuffer.length());
                        BarCode barCode = barCodeModels.getBarCode(connection, string);
                        if (barCode != null) {
                            Standart tovar = getTovar(barCode.getTovar());
                            barCodeTextField.setText("");
                            tovarTableView.getSelectionModel().select(tovar);
                            tovarTableView.scrollTo(tovar);
                            tovarTableView.requestFocus();
                            tovarTableView.refresh();
                            barCodesTableView.getSelectionModel().select(barCode);
                            barCodesTableView.scrollTo(barCode);
                            barCodesTableView.refresh();
                        } else {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Diqqat !!!");
                            alert.setHeaderText(string + "\n shtrix kodga muvofiq tovar topiilmadi");
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
                stringBuffer.delete(0, stringBuffer.length());
                String string = barCodeTextField.getText().trim();
                if (event.getCode()== KeyCode.ENTER) {
                    BarCode barCode = barCodeModels.getBarCode(connection, string);
                    if (barCode !=  null) {
                        Standart tovar = getTovar(barCode.getTovar());
                        barCodeTextField.setText("");
                        tovarTableView.getSelectionModel().select(tovar);
                        tovarTableView.scrollTo(tovar);
                        tovarTableView.refresh();
                        barCodesTableView.getSelectionModel().select(barCode);
                        barCodesTableView.scrollTo(barCode);
                        barCodesTableView.refresh();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Diqqat !!!");
                        alert.setHeaderText(string +  "\n shtrix kodga muvofiq tovar topiilmadi" );
                        alert.setContentText("");
                        alert.showAndWait();
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

    public BarCode getBarCode(String barCodeString) {
        BarCode barCode = barCodeModels.getBarCode(connection, barCodeString);

        return barCode;
    }

    private String getBirlik(int  id)  {
        String birlik = "";
        for (Standart b: birlikObservableList) {
            if (b.getId().equals(id)) {
                birlik = b.getText();
                break;
            }
        }
        return birlik;
    }

    public Boolean getDoubleClick() {
        return doubleClick;
    }

    public void setDoubleClick(Boolean doubleClick) {
        this.doubleClick = doubleClick;
    }

    public Standart getDoubleClickedRow() {
        return doubleClickedRow;
    }

    public void setDoubleClickedRow(Standart doubleClickedRow) {
        this.doubleClickedRow = doubleClickedRow;
    }

    private void initBarCodeGridPane () {
        barCodeGridPane.setVgap(padding);
        barCodeGridPane.setHgap(padding);
        HBox.setHgrow(buttonsHBox, Priority.ALWAYS);
        HBox.setHgrow(barCodeGridPane, Priority.ALWAYS);
        VBox.setVgrow(barCodeGridPane, Priority.ALWAYS);
        HBox.setHgrow(buttonsHBox, Priority.ALWAYS);
        HBox.setHgrow(barCodeHBox, Priority.ALWAYS);
        qaydEtButton.setMaxWidth(2000);
        qaydEtButton.setPrefWidth(150);
        HBox.setHgrow(qaydEtButton, Priority.ALWAYS);
        cancelButton.setMaxWidth(2000);
        cancelButton.setPrefWidth(150);
        HBox.setHgrow(cancelButton, Priority.ALWAYS);
        birlikComboBox.setMaxWidth(2000);
        birlikComboBox.setPrefWidth(150);
        birlikComboBox.setItems(birlikObservableList);
        barCodeComboBox.setMaxWidth(2000);
        barCodeComboBox.setPrefWidth(150);

        int rowIndex = 0;

        barCodeGridPane.add(new Label("Birlik: "), 0, rowIndex, 1, 1);
        barCodeGridPane.add(birlikComboBox, 1, rowIndex, 1, 1);
        GridPane.setHgrow(birlikComboBox, Priority.ALWAYS);

        rowIndex++;
        barCodeGridPane.add(new Label("Shtrix kod asosiy: "), 0, rowIndex, 1, 1);
        barCodeGridPane.add(barCodeHBox, 1, rowIndex, 1, 1);
        GridPane.setHgrow(barCodeHBox, Priority.ALWAYS);

        rowIndex++;
        barCodeGridPane.add(new Label("Adad: "), 0, rowIndex, 1, 1);
        barCodeGridPane.add(adadTextField, 1, rowIndex, 1, 1);
        GridPane.setHgrow(adadTextField, Priority.ALWAYS);

        rowIndex++;
        barCodeGridPane.add(new Label("Shtrix kod yordamchi: "), 0, rowIndex, 1, 1);
        barCodeGridPane.add(barCodeComboBox, 1, rowIndex, 1, 1);
        GridPane.setHgrow(barCodeComboBox, Priority.ALWAYS);

        rowIndex++;
        barCodeGridPane.add(new Label("Birlik yordamchi: "), 0, rowIndex, 1, 1);
        barCodeGridPane.add(birlik2Label, 1, rowIndex, 1, 1);
        GridPane.setHgrow(birlik2Label, Priority.ALWAYS);

        rowIndex++;
        barCodeGridPane.add(new Label("Vazn: "), 0, rowIndex, 1, 1);
        barCodeGridPane.add(vaznTextField, 1, rowIndex, 1, 1);
        GridPane.setHgrow(vaznTextField, Priority.ALWAYS);

        rowIndex++;
        barCodeGridPane.add(new Label("Hajm: "), 0, rowIndex, 1, 1);
        barCodeGridPane.add(hajmTextField, 1, rowIndex, 1, 1);
        GridPane.setHgrow(hajmTextField, Priority.ALWAYS);

        rowIndex++;
        buttonsHBox.getChildren().addAll(cancelButton, qaydEtButton);
        barCodeGridPane.add(buttonsHBox, 0, rowIndex, 2, 1);
        GridPane.setHgrow(buttonsHBox, Priority.ALWAYS);
    }

    public void gridToBarCode(BarCode barCode) {
        barCode.setBirlik(birlikComboBox.getValue().getId());
        barCode.setBarCode(barCodeTextField.getText());
        String string = adadTextField.getText();
        string = string.replaceAll(",", ".");
        barCode.setAdad(Double.valueOf(string));
        string = vaznTextField.getText();
        string = string.replaceAll(",", ".");
        barCode.setVazn(Double.valueOf(string));
        string = hajmTextField.getText();
        string = string.replaceAll(",", ".");
        barCode.setHajm(Double.valueOf(string));
        BarCode bc = barCodeComboBox.getValue();
        if (bc != null) {
            barCode.setTarkib(bc.getId());
        }
    }

    public void barCodeToGrid(BarCode barCode) {
        if (barCode != null) {
            barCodeComboBox.setItems(barCodeObservableList);
            Standart birlik = GetDbData.getBirlik(barCode.getBirlik());
            if (birlik !=null) {
                birlikComboBox.getSelectionModel().select(birlik);
            }
            barCodeTextField.setText(barCode.getBarCode());
            adadTextField.setText("" + barCode.getAdad());
            vaznTextField.setText(barCode.getVazn().toString());
            hajmTextField.setText(barCode.getHajm().toString());
            int tarkibInt = barCode.getTarkib();
            BarCode barCode1 = null;
            for (BarCode bc: barCodeObservableList) {
                if (bc.getId().equals(tarkibInt)) {
                    barCode1 = bc;
                    break;
                }
            }
            if (barCode1 != null) {
                barCodeComboBox.getSelectionModel().select(barCode1);
                Standart birlik2 = null;
                for (Standart s: birlikObservableList) {
                    if (s.getId().equals(barCode1.getBirlik())) {
                        birlik2 = s;
                        break;
                    }
                }
                if (birlik2 != null) {
                    birlik2Label.setText(birlik2.getText());
                }
            }
        }
    }

    private Standart getTovar(Integer tovarId) {
        Standart tovar = null;
        for (Standart t: tovarObservableList) {
            if (t.getId().equals(tovarId)) {
                tovar = t;
                break;
            }
        }
        return tovar;
    }

 /*   private void getBarCodeImageView(BarCode barCode) {
        Document document = new Document();
        PdfWriter pdfWriter = null;
        try {
            pdfWriter = PdfWriter.getInstance(document, new FileOutputStream("HelloWorld.pdf"));
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        PdfContentByte pdfContentByte = pdfWriter.getDirectContent();
        Barcode128 barcode128 = new Barcode128();
        barcode128.setCode("Bismillah. Abdumalik Abdujabbor");
        barcode128.setCodeType(Barcode128.CODE128);
        java.awt.Image awtImage = barcode128.createAwtImage(Color.BLACK, Color.WHITE);
        BufferedImage bImage= new BufferedImage(awtImage.getWidth(null), awtImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Image image = SwingFXUtils.toFXImage(bImage, null);
    }
   */

    // Right pad a string with the specified character
    public static String padRight(String s, int size,char pad) {
        StringBuilder builder = new StringBuilder(s);
        while(builder.length()<size) {
            builder.append(pad);
        }
        return builder.toString();
    }

    // Left pad a string with the specified character
    public static String padLeft(String s, int size,char pad) {
        StringBuilder builder = new StringBuilder(s);
        builder = builder.reverse(); // reverse initial string
        while(builder.length()<size) {
            builder.append(pad); // append at end
        }
        return builder.reverse().toString(); // reverse again!
    }

    /**
     * Converts a given Image into a BufferedImage
     *
     * @param img The Image to be converted
     * @return The converted BufferedImage
     */
    public static BufferedImage toBufferedImage(java.awt.Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    private ImageView getBarCodeImageView(String barCodeString) {
        Barcode128 barcode128 = new Barcode128();
        barcode128.setCode(barCodeString);
        barcode128.setGenerateChecksum(true);
        barcode128.setChecksumText(true);
        barcode128.setStartStopText(false);
        java.awt.Image img = barcode128.createAwtImage(Color.BLACK, Color.WHITE);
        BufferedImage bufferedImage = toBufferedImage(img);
        WritableImage writableImage = SwingFXUtils.toFXImage(bufferedImage, null);
        ImageView imageView = new ImageView(writableImage);
        return imageView;
    }

    public static BufferedImage getBarCodeToBufferedImage(BarCode barCode) {
        Barcode128 barcode128 = new Barcode128();
        barcode128.setCode(barCode.getBarCode());
        barcode128.setGenerateChecksum(true);
        barcode128.setChecksumText(true);
        barcode128.setStartStopText(false);
        java.awt.Image img = barcode128.createAwtImage(Color.BLACK, Color.WHITE);
        BufferedImage bufferedImage = toBufferedImage(img);
        return bufferedImage;
    }

    public static byte[] bufferedImageToByte(BufferedImage bufferedImage) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage,"pdf", byteArrayOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return bytes;
    }

    private void Taftish(String oldValue, String newValue) {
        ObservableList<Standart> subentries = FXCollections.observableArrayList();
        newValue = newValue.toLowerCase();

        if ( oldValue != null && (newValue.length() < oldValue.length()) ) {
            tovarTableView.setItems( tovarObservableList );
        }

        for ( Standart s: tovarObservableList ) {
            if (s.getText().toLowerCase().contains(newValue)) {
                subentries.add(s);
            }
        }
        tovarTableView.setItems(subentries);
    }

    private void printImage(BufferedImage image) {
        PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setPrintable(new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                // Get the upper left corner that it printable
                int x = (int) Math.ceil(pageFormat.getImageableX());
                int y = (int) Math.ceil(pageFormat.getImageableY());
                if (pageIndex != 0) {
                    return NO_SUCH_PAGE;
                }
                graphics.drawImage(image, x, y, image.getWidth(), image.getHeight(), null);
                return PAGE_EXISTS;
            }
        });
        try {
            printJob.print();
        } catch (PrinterException e1) {
            e1.printStackTrace();
        }
    }
}
