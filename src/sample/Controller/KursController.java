package sample.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import sample.Data.Kurs;
import sample.Data.User;
import sample.Data.Valuta;
import sample.Model.KursModels;
import sample.Model.ValutaModels;
import sample.Tools.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;

public class KursController {
    Tugmachalar tugmachalar = new Tugmachalar();
    KursModels kursModels = new KursModels();
    CustomWindow customWindow = new CustomWindow(false, 430, 400);
    ObservableList<Kurs> kurslar;
    ObservableList<Valuta> valutalar;
    TableView<Kurs> tableView = new TableView();
    Connection connection;
    User user;
    DecimalFormat decimalFormat = new DecimalFormat("###,###,###,###,###.##");
    DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance();
    ValutaModels valutaModels = new ValutaModels();

    public KursController(Connection connection, User user) throws SQLException, ParseException {
        decimalFormatSymbols.setGroupingSeparator(' ');
        decimalFormatSymbols.setDecimalSeparator('.');
        decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
        this.connection = connection;
        this.user = user;
        String classSimpleName = getClass().getSimpleName();
        DasturlarRoyxati.dastur(connection, user, classSimpleName);
        valutalar = valutaModels.get_data(connection);
        customWindow.setStageTitle("Kurslar");
        tugmachalar.getAdd().setOnAction(event -> {
            RecordHandler recordHandler = null;
                recordHandler = new RecordHandler(connection, user);
            try {
                recordHandler.add();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        tugmachalar.getEdit().setOnAction(event -> {
            Kurs kurs = tableView.getSelectionModel().getSelectedItem();
            if (kurs != null) {
                RecordHandler recordHandler = null;
                    recordHandler = new RecordHandler(connection, user, kurs);
                try {
                    recordHandler.edit();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        tugmachalar.getDelete().setOnAction(event -> {
            Kurs kurs = tableView.getSelectionModel().getSelectedItem();
            if (kurs != null) {
                RecordHandler recordHandler = null;
                    recordHandler = new RecordHandler(connection, user, kurs);
                try {
                    recordHandler.delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void showAndWait() throws SQLException, ParseException, IOException {
        customWindow.getStage().getIcons().add(new Image("/sample/images/Icons/WindowsTable.png"));
        kurslar = kursModels.get_data(connection);
        TableColumn<Kurs, Integer> id = new TableColumn<>("N");
        id.setMinWidth(15);
        id.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Kurs, Date> sana = new TableColumn<>("Sana");
        sana.setMinWidth(80);
        sana.setCellValueFactory(new PropertyValueFactory<>("sana"));
        sana.setCellFactory(column -> {
            TableCell<Kurs, Date> cell = new TableCell<Kurs, Date>() {
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
            return cell;
        });

        TableColumn<Kurs, Integer> valuta = new TableColumn<>("Pul turi");
        valuta.setMinWidth(150);
        valuta.setCellValueFactory(new PropertyValueFactory<>("valuta"));
        valuta.setCellFactory(column -> {
            TableCell<Kurs, Integer> cell = null;
            try {
                cell = new TableCell<Kurs, Integer>() {

                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            Valuta valuta = getValuta(item);
                            if (valuta != null) {
                                setText(valuta.getValuta().trim());
                            } else {
                                setText("");
                            }
                        }
                    }
                };
            } catch (Exception e) {
                e.printStackTrace();
            }
            return cell;
        });

        TableColumn<Kurs, Double> kurs = new TableColumn<>("Kurs");
        kurs.setMinWidth(100);
        kurs.setCellValueFactory(new PropertyValueFactory<>("kurs"));
        kurs.setCellFactory(column -> {
            TableCell<Kurs, Double> cell = null;
            String pattern = "###,###,###,###,###.##";
            DecimalFormat decimalFormat = new DecimalFormat(pattern);
            try {
                cell = new TableCell<Kurs, Double>() {

                    @Override
                    protected void updateItem(Double item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty) {
                            setText(null);
                        }
                        else {
                            setText(decimalFormat.format(item));
                        }
                    }
                };
            } catch (Exception e) {
                e.printStackTrace();
            }
            return cell;
        });

        customWindow.getRightVBox().getChildren().addAll(tugmachalar, tableView);
        tableView.getColumns().addAll(id, sana, valuta, kurs);
        tableView.setItems(kurslar);
        tableView.setPadding(new Insets(5));
        tugmachalar.setPadding(new Insets(5));
        customWindow.showAndWait();
    }

    class RecordHandler {
        Kurs kurs;
        CustomWindow customWindow = new CustomWindow(false, 430, 120);
        Connection connection;
        User user;
        GridPane gridPane = new GridPane();
        Button qaydEtButton = new Button("Qayd et");
        Label sanaLabel = new Label("Sana");
        DatePicker sanaDatePicker = new DatePicker();
        TextField vaqtTextField = new TextField();
        HBox valutaHBox = new HBox();
        Label valutaLabel = new Label("Kurs");
        ComboBox<Valuta> valutaComboBox = new ComboBox<>();
        Button valutaButton = new Button("");
        Label kursLabel = new Label("Kurs");
        TextField kursTextField = new TextField();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        ValutaModels valutaModels = new ValutaModels();
        ObservableList<Valuta> valutas;

        public RecordHandler(Connection connection, User user){
            valutas = valutaModels.get_data(connection);
            valutaComboBox.setItems(valutas);
            this.connection = connection;
            this.user = user;
            kursTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    newValue = newValue.trim().replaceAll(" ", "");
                    newValue = newValue.trim().replaceAll(",", ".");
                    if (!Alerts.isNumericAlert(newValue)) {
                        newValue = oldValue;
                    }
                }
            });
        }

        public RecordHandler(Connection connection, User user, Kurs kurs) {
            valutas = valutaModels.get_data(connection);
            valutaComboBox.setItems(valutas);
            this.connection = connection;
            this.user = user;
            this.kurs = kurs;
            kursTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    newValue = newValue.trim().replaceAll(" ", "");
                    newValue = newValue.trim().replaceAll(",", ".");
                    if (!Alerts.isNumericAlert(newValue)) {
                        newValue = oldValue;
                    }
                }
            });
        }

        public void add() throws IOException {
            valutaComboBox.getSelectionModel().selectFirst();
            customWindow.setStageTitle("Yangi kurs");
            LocalDateTime localDateTime = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            Integer year = localDateTime.getYear();
            Integer month = localDateTime.getMonthValue();
            Integer day = localDateTime.getDayOfMonth();
            vaqtTextField.setText(sdf.format(new Date()));
            sanaDatePicker = new DatePicker(LocalDate.of(year, month, day));
            valutaHBox.getChildren().addAll(valutaComboBox, valutaButton);
            qaydEtButton.setOnAction(event -> {
                kurs = new Kurs();
                LocalDateTime localDateTime1 = LocalDateTime.of(sanaDatePicker.getValue(), LocalTime.parse(vaqtTextField.getText()));
                Instant instant = Instant.from(localDateTime1.atZone(ZoneId.systemDefault()));
                kurs.setSana(Date.from(instant));
                kurs.setValuta(valutaComboBox.getValue().getId());
                kurs.setKurs(Double.valueOf(spaceDelete(kursTextField.getText())));
                kurs.setUserId(user.getId());
                kurs.setId(kursModels.insert_data(connection, kurs));
                kurslar.add(kurs);
                tableView.refresh();
                customWindow.getStage().close();
            });
            display();
        }

        public void edit() throws SQLException, ParseException, IOException {
            customWindow.setStageTitle("O`zgartirish amali");
            LocalDateTime localDateTime = kurs.getSana().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            Integer year = localDateTime.getYear();
            Integer month = localDateTime.getMonthValue();
            Integer day = localDateTime.getDayOfMonth();
            vaqtTextField.setText(sdf.format(kurs.getSana()));
            sanaDatePicker = new DatePicker(LocalDate.of(year, month, day));
            kursTextField.setText(decimalFormat.format(kurs.getKurs()));
            valutaComboBox.getSelectionModel().select(kurs.getValuta() - 1);
            valutaHBox.getChildren().addAll(valutaComboBox, valutaButton);
            qaydEtButton.setOnAction(event -> {
                LocalDateTime localDateTime1 = LocalDateTime.of(sanaDatePicker.getValue(), LocalTime.parse(vaqtTextField.getText()));
                Instant instant = Instant.from(localDateTime1.atZone(ZoneId.systemDefault()));
                kurs.setSana(Date.from(instant));
                kurs.setValuta(valutaComboBox.getValue().getId());
                kurs.setKurs(Double.valueOf(spaceDelete(kursTextField.getText())));
                kurs.setUserId(user.getId());
                kursModels.update_data(connection, kurs);
                tableView.refresh();
                customWindow.getStage().close();
            });
            display();
        }

        public void delete() throws IOException {
            customWindow.setStageTitle("O`chirish amali");
            LocalDateTime localDateTime = kurs.getSana().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            Integer year = localDateTime.getYear();
            Integer month = localDateTime.getMonthValue();
            Integer day = localDateTime.getDayOfMonth();
            vaqtTextField.setText(sdf.format(kurs.getSana()));
            sanaDatePicker = new DatePicker(LocalDate.of(year, month, day));
            kursTextField.setText(decimalFormat.format(kurs.getKurs()));
            valutaComboBox.getSelectionModel().select(kurs.getValuta() - 1);
            valutaHBox.getChildren().addAll(valutaComboBox, valutaButton);
            sanaDatePicker.setDisable(true);
            vaqtTextField.setDisable(true);
            valutaComboBox.setDisable(true);
            valutaButton.setDisable(true);
            kursTextField.setDisable(true);
            qaydEtButton.setOnAction(event -> {
                kursModels.delete_data(connection, kurs);
                kurslar.remove(kurs);
                tableView.refresh();
                customWindow.getStage().close();
            });
            display();
        }

        public void display() throws IOException {
            valutaButton.setGraphic( new PathToImageView("/sample/images/Icons/add.png").getImageView());
            valutaComboBox.setMaxWidth(1.7976931348623157E308);
            valutaComboBox.setPrefWidth(150);
            Integer rowIndex = 0;
            gridPane.setVgap(5);
            gridPane.setHgap(5);
            gridPane.setPadding(new Insets(5));
            gridPane.add(sanaLabel, 0, rowIndex, 1, 1);
            gridPane.add(sanaDatePicker, 1, rowIndex, 1, 1);
            gridPane.add(vaqtTextField, 2, rowIndex, 1, 1);
            rowIndex++;
            gridPane.add(valutaLabel, 0, rowIndex, 1, 1);
            gridPane.add(valutaHBox, 1, rowIndex, 1, 1);
            gridPane.add(kursTextField, 2, rowIndex, 2, 1);
            rowIndex++;
            gridPane.add(qaydEtButton, 2, rowIndex, 1, 1);
            GridPane.setHalignment(qaydEtButton, HPos.RIGHT);
            customWindow.getRightVBox().getChildren().add(gridPane);
            GridPane.setHgrow(sanaDatePicker, Priority.ALWAYS);
            GridPane.setHgrow(vaqtTextField, Priority.ALWAYS);
            GridPane.setHgrow(valutaHBox, Priority.ALWAYS);
            GridPane.setHgrow(kursTextField, Priority.ALWAYS);
            customWindow.showAndWait();
        }
    }

    public String spaceDelete(String string) {
        string = string.replaceAll("\\s+", "");
        string = string.replaceAll(",", ".");
        return string;
    }

    public void Taftish(String oldValue, String newValue) {
        ObservableList<Kurs> subentries = FXCollections.observableArrayList();
        newValue = newValue.toLowerCase();

        if ( oldValue != null && (newValue.length() < oldValue.length()) ) {
            tableView.setItems( kurslar );
        }

        for ( Kurs kurs: kurslar ) {
            if (getValuta(kurs.getId()).getValuta().contains(newValue)) {
                subentries.add(kurs);
            }
        }
        tableView.setItems(subentries);
    }

    public Valuta getValuta(Integer valutaId) {
        Valuta valuta = null;
        for (Valuta v: valutalar) {
            if (v.getId().equals(valutaId)) {
                valuta = v;
                break;
            }
        }
        return valuta;
    }
}
