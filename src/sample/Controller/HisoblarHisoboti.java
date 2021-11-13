package sample.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import sample.Tools.GetDbData;
import sample.Tools.MoneyShow;
import sample.Data.*;
import sample.Model.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HisoblarHisoboti {
    Stage stage;
    Scene scene;
    HBox hBox = new HBox();

    VBox leftPane = new VBox();
    VBox rightPane = new VBox();

    TextField leftFinder = new TextField();
    TextField rightFinder = new TextField();

    Connection connection;
    User user;
    HisobKitobModels hisobKitobModels = new HisobKitobModels();
    HisobModels hisobModels = new HisobModels();

    ObservableList<HisobKitob> hisobKitobObservableList;
    ObservableList<Hisob> hisoblar = FXCollections.observableArrayList();
    ObservableList<Balans> balanslar = FXCollections.observableArrayList();

    TableView<Hisob> hisobTableView = new TableView<>();

    TableView<HisobKitob> hisobKitobTableView = new TableView<>();
    ObservableList<HisobKitob> yakkaHisobObservableList = FXCollections.observableArrayList();
    Button cancelButton = new Button("<<");

    StandartModels amalModels = new StandartModels();
    ObservableList<Standart> amallar = FXCollections.observableArrayList();

    ValutaModels valutaModels = new ValutaModels();
    ObservableList<Valuta> valutalar = FXCollections.observableArrayList();

    TovarModels tovarModels = new TovarModels();
    ObservableList<Standart> tovarlar = FXCollections.observableArrayList();

    Hisob hisob;
    HisobKitob hisobKitob;
    Boolean doubleClick = false;
    DecimalFormat decimalFormat = new MoneyShow();
    Double balansDouble = .0;
    Integer width = 285;
    Integer padding = 3;

    public HisoblarHisoboti(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
        initData();
        initHisobTableView();
        initHisoKitobTableView();
        initMethods();
    }

    private void initMethods() {
        leftFinder.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                TaftishHisob(oldValue, newValue);
            }
        });

        rightFinder.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                TaftishHisobKitob(oldValue, newValue);
            }
        });
    }

    public void start() {
        jumlaHisobot();
        hisobTableView.refresh();
        initStage();
    }

    public void initData() {
        hisobKitobObservableList = hisobKitobModels.get_data(connection);
        hisoblar = hisobModels.get_data(connection);
        hisob = hisobTableView.getSelectionModel().getSelectedItem();
        if (hisoblar.size()>0) {
            hisob = hisoblar.get(0);
            yakkaHisobObservableList = hisobKitobModels.getAnyData(connection, "hisob1 = " + hisob.getId() + " or hisob2 = " + hisob.getId(), "");
            balans2(hisob, yakkaHisobObservableList);
        }
        amalModels.setTABLENAME("Amal");
        amallar = amalModels.get_data(connection);
        valutalar = valutaModels.get_data(connection);
        tovarlar = GetDbData.getTovarObservableList();
    }

    private TableColumn<Hisob, Integer> idColumn() {
        TableColumn<Hisob, Integer> id = new TableColumn<>("N");
        id.setMinWidth(20);
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        return id;
    }

    private TableColumn<Hisob, String> hisobNomiColumn() {
        TableColumn<Hisob, String> hisobNomi = new TableColumn<>("Hisob nomi");
        hisobNomi.setMinWidth(160);
        hisobNomi.setCellValueFactory(new PropertyValueFactory<>("text"));
        return hisobNomi;
    }

    private  TableColumn<Hisob, Double> chiqimColumn() {
        TableColumn<Hisob, Double> chiqim = new TableColumn<>("Chiqim");
        chiqim.setMinWidth(80);
        chiqim.setCellValueFactory(new PropertyValueFactory<>("chiqim"));
        chiqim.setCellFactory(column -> {
            TableCell<Hisob, Double> cell = new TableCell<Hisob, Double>() {
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
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        return chiqim;
    }

    private TableColumn<Hisob, Double> kirimColumn() {
        TableColumn<Hisob, Double> kirim = new TableColumn<>("Kirim");
        kirim.setMinWidth(80);
        kirim.setCellValueFactory(new PropertyValueFactory<>("kirim"));
        kirim.setCellFactory(column -> {
            TableCell<Hisob, Double> cell = new TableCell<Hisob, Double>() {
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
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        return kirim;
    }

    private  TableColumn<Hisob, Double> balansColumn() {
        TableColumn<Hisob, Double> balans = new TableColumn<>("Balans");
        balans.setMinWidth(100);
        balans.setCellValueFactory(new PropertyValueFactory<>("balans"));
        balans.setCellFactory(column -> {
            TableCell<Hisob, Double> cell = new TableCell<Hisob, Double>() {
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
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        return balans;
    }

    public void initHisobTableView() {
//        hisobTableView.setPadding(new Insets(3));
        HBox.setHgrow(hisobTableView, Priority.ALWAYS);
        VBox.setVgrow(hisobTableView, Priority.ALWAYS);
        hisobTableView.setMaxWidth(width);
        hisobTableView.setMaxWidth(width);
        hisobTableView.getColumns().addAll(hisobNomiColumn(), balansColumn());
        hisobTableView.setItems(hisoblar);
        if (hisoblar.size()>0) {
            hisobTableView.getSelectionModel().selectFirst();
        }

        hisobTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                hisob = newValue;
                yakkaHisobObservableList.removeAll(yakkaHisobObservableList);
                yakkaHisobObservableList.addAll(hisobKitobModels.getAnyData(connection, "hisob1 = " + hisob.getId() + " or hisob2 = " + hisob.getId(), ""));
                balans2(hisob, yakkaHisobObservableList);
                hisobKitobTableView.setItems(yakkaHisobObservableList);
            }
        });

    }

    private void initHisoKitobTableView() {
        hisobKitobTableView.getColumns().addAll(dateTimeColumn(), hisob2Column(), hisob1Column(), amalColumn(), valutaColumn(),
                kursColumn(), tovarColumn(), donaColumn(), narhColumn(), summaColColumn(), balans2Column());

        HBox.setHgrow(hisobKitobTableView, Priority.ALWAYS);
        VBox.setVgrow(hisobKitobTableView, Priority.ALWAYS);
        GridPane.setHgrow(hisobKitobTableView, Priority.ALWAYS);
        GridPane.setVgrow(hisobKitobTableView, Priority.ALWAYS);

        hisobKitobTableView.setItems(yakkaHisobObservableList);
        hisobKitobTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                hisobKitob = newValue;
            }
        });

    }

    private TableColumn<HisobKitob, Integer> hisob1Column() {
        TableColumn<HisobKitob, Integer> hisob1 = new TableColumn<>("Hisob");
        hisob1.setMinWidth(150);
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
                        setText(getHisob(hisoblar, item).getText());
                    }
                }
            };
            return cell;
        });
        return hisob1;
    }

    private TableColumn<HisobKitob, Integer> hisob2Column() {
        TableColumn<HisobKitob, Integer> hisob2 = new TableColumn<>("Kirim/chiqim");
        hisob2.setMinWidth(60);
        hisob2.setCellValueFactory(new PropertyValueFactory<>("hisob2"));
        hisob2.setCellFactory(column -> {
            TableCell<HisobKitob, Integer> cell = new TableCell<HisobKitob, Integer>() {

                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        if (item == 1) {
                            setText("Chiqim");
                        } else {
                            setText("Kirim");
                        }
                    }
                }
            };
            return cell;
        });

        return hisob2;
    }

    private TableColumn<HisobKitob, Integer> amalColumn() {
        TableColumn<HisobKitob, Integer> amal = new TableColumn<>("Amal turi");
        amal.setMinWidth(100);
        amal.setCellValueFactory(new PropertyValueFactory<>("amal"));
        amal.setCellFactory(column -> {
            TableCell<HisobKitob, Integer> cell = new TableCell<HisobKitob, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        Standart amal = getAmal(amallar, item);
                        setText(amal.getText());
                    }
                }
            };
            return cell;
        });
        return amal;
    }

    private TableColumn<HisobKitob, Integer> valutaColumn() {
        TableColumn<HisobKitob, Integer> valuta = new TableColumn<>("Pul turi");
        valuta.setMinWidth(100);
        valuta.setCellValueFactory(new PropertyValueFactory<>("valuta"));
        valuta.setCellFactory(column -> {
            TableCell<HisobKitob, Integer> cell = new TableCell<HisobKitob, Integer>() {
                ValutaModels valutaModels = new ValutaModels();
                ObservableList<Valuta> valutas;
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        Valuta valuta = getValuta(valutalar, item);
                        setText("");
                        if (valuta != null) {
                            setText(valuta.getValuta());
                        }
                    }
                }
            };
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        return valuta;
    }

    private TableColumn<HisobKitob, Integer> tovarColumn() {
        TableColumn<HisobKitob, Integer> tovar = new TableColumn<>("Tovar");
        tovar.setMinWidth(100);
        tovar.setCellValueFactory(new PropertyValueFactory<>("tovar"));
        tovar.setCellFactory(column -> {
            TableCell<HisobKitob, Integer> cell = new TableCell<HisobKitob, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        Standart tovar = GetDbData.getTovar(item);
                        setText("");
                        if (tovar != null) {
                            setText(tovar.getText());
                        }
                    }
                }
            };
            return cell;
        });
        return tovar;
    }

    private TableColumn<HisobKitob, Double> kursColumn() {
        TableColumn<HisobKitob, Double> kurs = new TableColumn<>("Kurs");
        kurs.setMinWidth(80);
        kurs.setCellValueFactory(new PropertyValueFactory<>("kurs"));
        kurs.setCellFactory(column -> {
            TableCell<HisobKitob, Double> cell = new TableCell<HisobKitob, Double>() {
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
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        return kurs;
    }

    private TableColumn<HisobKitob, String> shaklColumn() {
        TableColumn<HisobKitob, String> shakl = new TableColumn<>("Tovar shakli");
        shakl.setMinWidth(100);
        shakl.setCellValueFactory(new PropertyValueFactory<>("shakl"));
        shakl.setCellFactory(column -> {
            TableCell<HisobKitob, String> cell = new TableCell<HisobKitob, String>() {

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    String[] combo;
                    if(empty) {
                        setText(null);
                    } else {
                        combo = item.split(":");
                        if (!combo[0].trim().equals("0")) {
                            setText(combo[2] + ": " + combo[3] + " " + combo[4]);
                        } else if (!combo[1].trim().equals("0")) {
                            setText(combo[4]);
                        }
                    }
                }
            };
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        return shakl;
    }

    private TableColumn<HisobKitob, Double> donaColumn() {
        TableColumn<HisobKitob, Double> dona = new TableColumn<>("Dona");
        dona.setMinWidth(80);
        dona.setCellValueFactory(new PropertyValueFactory<>("dona"));
        dona.setCellFactory(column -> {
            TableCell<HisobKitob, Double> cell = new TableCell<HisobKitob, Double>() {
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
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        return dona;
    }

    private TableColumn<HisobKitob, Double> narhColumn() {
        TableColumn<HisobKitob, Double> narh = new TableColumn<>("Narh");
        narh.setMinWidth(80);
        narh.setCellValueFactory(new PropertyValueFactory<>("narh"));
        narh.setCellFactory(column -> {
            TableCell<HisobKitob, Double> cell = new TableCell<HisobKitob, Double>() {
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
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        return narh;
    }

    private TableColumn<HisobKitob, Double> summaColColumn() {
        TableColumn<HisobKitob, Double> summaCol = new TableColumn<> ("Jami");
        summaCol.setMinWidth(80);
        summaCol.setStyle( "-fx-alignment: CENTER-RIGHT;");
        summaCol.setCellValueFactory(new PropertyValueFactory<>("summaCol"));
        return summaCol;
    }

    private TableColumn<HisobKitob, Double> balans2Column() {
        TableColumn<HisobKitob, Double> balans2 = new TableColumn<> ("Balans");
        balans2.setMinWidth(100);
        balans2.setCellValueFactory(new PropertyValueFactory<>("balans"));
        balans2.setCellFactory(column -> {
            TableCell<HisobKitob, Double> cell = new TableCell<HisobKitob, Double>() {
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
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        return balans2;
    }

    private TableColumn<HisobKitob, Date> dateTimeColumn() {
        TableColumn<HisobKitob, Date> dateTime = new TableColumn<>("Sana");
        dateTime.setMinWidth(80);
        dateTime.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        dateTime.setCellFactory(column -> {
            TableCell<HisobKitob, Date> cell = new TableCell<HisobKitob, Date>() {
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        setText(sdf.format(item));
                    }
                }
            };
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        return dateTime;
    }

    private TableColumn<HisobKitob, Integer> userIdColumn () {
        TableColumn<HisobKitob, Integer> userId = new TableColumn<>("userId");
        userId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        return userId;
    }

    public void initStage() {
        this.stage = new Stage();
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Hisoblar bo`yicha hisobot");

        leftPane.setMinWidth(width);
        leftPane.setMaxWidth(width);
        leftPane.setPadding(new Insets(padding));
        rightPane.setPadding(new Insets(padding));
        leftPane.getChildren().addAll(leftFinder, hisobTableView);
        rightPane.getChildren().addAll(rightFinder, hisobKitobTableView);
        hBox.getChildren().addAll(leftPane, rightPane);

        HBox.setHgrow(hBox, Priority.ALWAYS);
        VBox.setVgrow(hBox, Priority.ALWAYS);
        HBox.setHgrow(leftPane, Priority.ALWAYS);
        VBox.setVgrow(leftPane, Priority.ALWAYS);
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        VBox.setVgrow(rightPane, Priority.ALWAYS);
        HBox.setHgrow(leftFinder, Priority.ALWAYS);
        VBox.setVgrow(leftFinder, Priority.NEVER);
        HBox.setHgrow(rightFinder, Priority.ALWAYS);
        VBox.setVgrow(rightFinder, Priority.NEVER);
        scene = new Scene(hBox, 600, 400);
//        scene.getStylesheets().add("/sample/styles/caspian.css");
        stage.setScene(scene);
        stage.show();
    }

    public HisobKitob sumHisob(HisobKitob hisobKitob) {
        int sanoq = 0;
        double balans = .0;
        for (Hisob h: hisoblar) {
            if (h.getId().equals(hisobKitob.getHisob1())) {
                if (hisobKitob.getTovar() > 0) {
                    h.setChiqim(h.getChiqim() + (hisobKitob.getDona() * hisobKitob.getNarh() / hisobKitob.getKurs()));
                    sanoq++;
                } else {
                    h.setChiqim(h.getChiqim() + hisobKitob.getNarh() / hisobKitob.getKurs());
                    sanoq++;
                }
            }

            if (h.getId().equals(hisobKitob.getHisob2())) {
                if (hisobKitob.getTovar() > 0) {
                    h.setKirim(h.getKirim() + (hisobKitob.getDona() * hisobKitob.getNarh() / hisobKitob.getKurs()));
                    sanoq++;
                } else {
                    h.setKirim(h.getKirim() + hisobKitob.getNarh() / hisobKitob.getKurs());
                    sanoq++;
                }
            }
            if (sanoq == 2) {
                break;
            }
        }
        return hisobKitob;
    }

    public void balans() {
        balansDouble = .0;
        for (Hisob h: hisoblar) {
/*
            balans = balans + h.getKirim() - h. getChiqim();
            h.setBalans(balans);
*/
            h.setBalans(h.getKirim() - h. getChiqim());
        }
        balansDouble = .0;
    }

    public ObservableList<HisobKitob> balans2(Hisob hisob, ObservableList<HisobKitob> hisobKitobObservableList) {
        return hisobKitobObservableList;
    }

    public void jumlaHisobot() {
        sifr();
        for (HisobKitob h: hisobKitobObservableList) {
            sumHisob(h);
        }
        balans();
    }

    public void sifr() {
        balansDouble = .0;
        for (Hisob h: hisoblar) {
            h.setKirim(.0);
            h.setChiqim(.0);
            h.setBalans(.0);
        }
    }


    public Hisob getHisob(ObservableList<Hisob> hisobs, Integer id) {
        Hisob hisob = null;
        for (Hisob h: hisobs) {
            if (h.getId().equals(id)) {
                hisob = h;
                break;
            }
        }
        return hisob;
    }

    public Standart getAmal(ObservableList<Standart> amallar, Integer id) {
        Standart amal = null;
        for (Standart a: amallar) {
            if (a.getId().equals(id)) {
                amal = a;
                break;
            }
        }
        return amal;
    }

    public Valuta getValuta(ObservableList<Valuta> valutalar, Integer id) {
        Valuta valuta = null;
        for (Valuta v: valutalar) {
            if (v.getId().equals(id)) {
                valuta = v;
                break;
            }
        }
        return valuta;
    }

    public Tovar getTovar(ObservableList<Tovar> tovarlar, Integer id) {
        Tovar tovar = null;
        for (Tovar t: tovarlar) {
            if (t.getId().equals(id)) {
                tovar = t;
                break;
            }
        }
        return tovar;
    }

    public void TaftishHisob(String oldValue, String newValue) {
        ObservableList<Hisob> subentries = FXCollections.observableArrayList();
        newValue = newValue.toLowerCase();

        if ( oldValue != null && (newValue.length() < oldValue.length()) ) {
            hisobTableView.setItems( hisoblar );
        }

        for ( Hisob hisob: hisoblar ) {
            if (hisob.getText().toLowerCase().contains(newValue)) {
                subentries.add(hisob);
            }
        }
        hisobTableView.setItems(subentries);
    }

    public void TaftishHisobKitob(String oldValue, String newValue) {
        ObservableList<HisobKitob> subentries = FXCollections.observableArrayList();
        newValue = newValue.toLowerCase();

        if ( oldValue != null && (newValue.length() < oldValue.length()) ) {
            hisobKitobTableView.setItems( yakkaHisobObservableList );
        }

        for ( HisobKitob hisobKitob: yakkaHisobObservableList ) {
            if (getHisob(hisoblar, hisobKitob.getHisob1()).getText().toLowerCase().contains(newValue)) {
                subentries.add(hisobKitob);
            }
        }
        hisobKitobTableView.setItems(subentries);
    }
}
