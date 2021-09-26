package sample.Tools;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import sample.Data.*;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GetTableView {
    public static TableView<Hisob> hisobTableView = new TableView();
    public static TableView<QaydnomaData> qaydnomaTableView = new TableView();
    public static TableView<HisobKitob> hisobKitobTableView = new TableView();

    public static void initTableViews() {
        initHisobTableView();
        initQaydnomaTableView();
        initHisobKitobTableView();
    }

    public static void initHisobTableView() {
        hisobTableView.getColumns().addAll(getHisobIdColumn(), getHisobTextColumn(), getHisobBalansColumn());
    }

    public static TableColumn<Hisob, Integer> getHisobIdColumn() {
        TableColumn<Hisob, Integer> hisobIdColumn = new TableColumn<>("N");
        hisobIdColumn.setMinWidth(20);
        hisobIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        return hisobIdColumn;
    }

    public static TableColumn<Hisob, String> getHisobTextColumn() {
        TableColumn<Hisob, String> hisobTextColumn = new TableColumn<>("Hisob nomi");
        hisobTextColumn.setMinWidth(20);
        hisobTextColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
        return hisobTextColumn;
    }

    public static TableColumn<Hisob, Double> getHisobBalansColumn() {
        TableColumn<Hisob, Double> hisobBalansColumn = new TableColumn<>("Balans");
        hisobBalansColumn.setMinWidth(90);
        hisobBalansColumn.setCellValueFactory(new PropertyValueFactory<>("balans"));
        hisobBalansColumn.setCellFactory(column -> {
            TableCell<Hisob, Double> cell = new TableCell<Hisob, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        setText(new MoneyShow().format(item));
                    }
                }
            };
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        return hisobBalansColumn;
    }

    public static TableView<Hisob> getHisobTableView() {
        return hisobTableView;
    }

    public static void setHisobTableView(TableView<Hisob> hisobTableView) {
        GetTableView.hisobTableView = hisobTableView;
    }

    public static TableView<QaydnomaData> initQaydnomaTableView() {
        qaydnomaTableView = new TableView<QaydnomaData>();
        qaydnomaTableView.getColumns().addAll(getIdColumn(), getAmalTuriColumn(), getHujjatColumn(), getSanaColumn(), getChiqimNomiColumn(), getKirimNomiColumn(), getJamiColumn());
        return qaydnomaTableView;
    }

    public static TableColumn<QaydnomaData, Integer> getIdColumn() {
        TableColumn<QaydnomaData, Integer> idColumn = new TableColumn("N");
        idColumn.setMinWidth(30);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        return idColumn;
    }

    public static TableColumn<QaydnomaData, Integer> getAmalTuriColumn() {
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
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        return amalTuriColumn;
    }

    public static TableColumn<QaydnomaData, Integer> getHujjatColumn() {
        TableColumn<QaydnomaData, Integer> hujjatColumn = new TableColumn("Hujjat N");
        hujjatColumn.setMinWidth(30);
        hujjatColumn.setCellValueFactory(new PropertyValueFactory<>("hujjat"));
        hujjatColumn.setStyle( "-fx-alignment: CENTER;");
        return hujjatColumn;
    }

    public static TableColumn<QaydnomaData, Date> getSanaColumn() {
        TableColumn<QaydnomaData, Date> sanaColumn = new TableColumn("Sana");
        sanaColumn.setMinWidth(80);
        sanaColumn.setCellValueFactory(new PropertyValueFactory<>("sana"));
        sanaColumn.setCellFactory(column -> {
            TableCell<QaydnomaData, Date> cell = new TableCell<QaydnomaData, Date>() {
                private SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy\n  HH:mm:ss");
                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        setText(format.format(item));
                    }
                }
            };
            cell.setAlignment(Pos.CENTER);

            return cell;
        });
        return sanaColumn;
    }

    public static TableColumn<QaydnomaData, Integer> getChiqimIdColumn() {
        TableColumn<QaydnomaData, Integer> chiqimIdColumn = new TableColumn("Chiqim N");
        chiqimIdColumn.setMinWidth(30);
        chiqimIdColumn.setCellValueFactory(new PropertyValueFactory<>("chiqimId"));
        return chiqimIdColumn;
    }

    public static TableColumn<QaydnomaData, String> getChiqimNomiColumn() {
        TableColumn<QaydnomaData, String> chiqimIdColumn = new TableColumn("Chiqim hisobi");
        chiqimIdColumn.setMinWidth(150);
        chiqimIdColumn.setCellValueFactory(new PropertyValueFactory<>("chiqimNomi"));
        return chiqimIdColumn;
    }

    public static TableColumn<QaydnomaData, Integer> getKirimIdColumn() {
        TableColumn<QaydnomaData, Integer> kirimIdColumn = new TableColumn("Kirim N");
        kirimIdColumn.setMinWidth(30);
        kirimIdColumn.setCellValueFactory(new PropertyValueFactory<>("kirimId"));
        return kirimIdColumn;
    }

    public static TableColumn<QaydnomaData, String> getKirimNomiColumn() {
        TableColumn<QaydnomaData, String> kirimNomiColumn = new TableColumn("Kirim hisobi");
        kirimNomiColumn.setMinWidth(150);
        kirimNomiColumn.setCellValueFactory(new PropertyValueFactory<>("kirimNomi"));
        return kirimNomiColumn;
    }

    public static TableColumn<QaydnomaData, String> getIzohColumn() {
        TableColumn<QaydnomaData, String> izohColumn = new TableColumn("Izoh");
        izohColumn.setMinWidth(150);
        izohColumn.setCellValueFactory(new PropertyValueFactory<>("izoh"));
        return izohColumn;
    }

    public static TableColumn<QaydnomaData, Double> getJamiColumn() {
        TableColumn<QaydnomaData, Double> jamiColumn = new TableColumn("Jami");
        jamiColumn.setMinWidth(150);
        jamiColumn.setCellValueFactory(new PropertyValueFactory<>("jami"));
        jamiColumn.setCellFactory(column -> {
            TableCell<QaydnomaData, Double> cell = new TableCell<QaydnomaData, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        setText(new MoneyShow().format(item));
                    }
                }
            };
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        return jamiColumn;
    }

    public static TableView<QaydnomaData> getQaydnomaTableView() {
        return qaydnomaTableView;
    }

    public static void setQaydnomaTableView(TableView<QaydnomaData> qaydnomaTableView) {
        GetTableView.qaydnomaTableView = qaydnomaTableView;
    }

    public static TableView<HisobKitob> initHisobKitobTableView() {
        hisobKitobTableView = new TableView<HisobKitob>();
        hisobKitobTableView.getColumns().addAll(getCustomColumn(), getAmalColumn(), getValutaColumn(), getKursColumn(), getTovarColumn(), getAdadColumn(), getNarhColumn(), getSummaColumn());
        return hisobKitobTableView;
    }

    public static TableColumn<HisobKitob, Integer> getHisob1Column() {
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

    public static TableColumn<HisobKitob, Integer> getHisob2Column() {
        TableColumn<HisobKitob, Integer> hisob2 = new TableColumn<>("Hisob2");
        hisob2.setMinWidth(80);
        hisob2.setCellValueFactory(new PropertyValueFactory<>("hisob2"));
        return hisob2;
    }

    public static TableColumn<HisobKitob, Integer> getAmalColumn() {
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
                            setText(amal.getText());
                        } else {
                            setText("");
                        }
                    }
                }
            };
            return cell;
        });
        return amalColumn;
    }

    public static TableColumn<HisobKitob, Integer> getCustomColumn() {
        TableColumn<HisobKitob, Integer> integerTableColumn = new TableColumn<>("Kirim/\nChiqim");
        integerTableColumn.setMinWidth(100);
        integerTableColumn.setCellValueFactory(new PropertyValueFactory<>("hisob2"));
        integerTableColumn.setCellFactory(column -> {
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
        return integerTableColumn;
    }

    public static TableColumn<HisobKitob, Integer> getValutaColumn() {
        TableColumn<HisobKitob, Integer> valutaColumn = new TableColumn<>("Pul turi");
        valutaColumn.setMinWidth(100);
        valutaColumn.setCellValueFactory(new PropertyValueFactory<>("valuta"));
        valutaColumn.setCellFactory(column -> {
            TableCell<HisobKitob, Integer> cell = new TableCell<HisobKitob, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        Valuta valuta = GetDbData.getValuta(item);
                        if (valuta != null) {
                            setText(valuta.getValuta());
                        } else {
                            setText("");
                        }
                    }
                }
            };
            return cell;
        });
        return valutaColumn;
    }

    public static TableColumn<HisobKitob, Double> getKursColumn() {
        TableColumn<HisobKitob, Double> kursColumn = new TableColumn<>("Kurs");
        kursColumn.setMinWidth(100);
        kursColumn.setCellValueFactory(new PropertyValueFactory<>("kurs"));
        kursColumn.setCellFactory(column -> {
            TableCell<HisobKitob, Double> cell = new TableCell<HisobKitob, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        setText(new MoneyShow().format(item));
                    }
                }
            };
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        return kursColumn;
    }

    public static TableColumn<HisobKitob, Integer> getTovarColumn() {
        TableColumn<HisobKitob, Integer> tovarColumn = new TableColumn<>("Tovar");
        tovarColumn.setMinWidth(200);
        tovarColumn.setCellValueFactory(new PropertyValueFactory<>("tovar"));
        tovarColumn.setCellFactory(column -> {
            TableCell<HisobKitob, Integer> cell = new TableCell<HisobKitob, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        Standart tovar = GetDbData.getTovar(item);
                        if (tovar != null) {
                            setText(tovar.getText());
                        } else {
                            setText("");
                        }
                    }
                }
            };
            return cell;
        });
        return tovarColumn;
    }

    public static TableColumn<HisobKitob, String> getShaklColumn() {
        TableColumn<HisobKitob, String> shaklColumn = new TableColumn<>("Shakl");
        shaklColumn.setMinWidth(200);
        shaklColumn.setCellValueFactory(new PropertyValueFactory<>("shakl"));
        shaklColumn.setCellFactory(column -> {
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
        return shaklColumn;
    }

    public static TableColumn<HisobKitob, Double> getAdadColumn() {
        TableColumn<HisobKitob, Double> adadColumn = new TableColumn<>("Dona");
        adadColumn.setMinWidth(100);
        adadColumn.setCellValueFactory(new PropertyValueFactory<>("dona"));
        adadColumn.setCellFactory(column -> {
            TableCell<HisobKitob, Double> cell = new TableCell<HisobKitob, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        setText(new MoneyShow().format(item));
                    }
                }
            };
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        adadColumn.setStyle( "-fx-alignment: CENTER;");
        return adadColumn;
    }

    public static TableColumn<HisobKitob, Double> getNarhColumn() {
        TableColumn<HisobKitob, Double> narhColumn = new TableColumn<>("Narh");
        narhColumn.setMinWidth(100);
        narhColumn.setCellValueFactory(new PropertyValueFactory<>("narh"));
        narhColumn.setCellFactory(column -> {
            TableCell<HisobKitob, Double> cell = new TableCell<HisobKitob, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        setText(new MoneyShow().format(item));
                    }
                }
            };
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        return narhColumn;
    }

    public static TableColumn<HisobKitob, String> getSummaColumn() {
        TableColumn<HisobKitob, String> summaColumn = new TableColumn<>("Summa");
        summaColumn.setMinWidth(200);
        summaColumn.setCellValueFactory(new PropertyValueFactory<>("summaCol"));
        summaColumn.setStyle( "-fx-alignment: CENTER;");
        return summaColumn;
    }

    public static TableColumn<HisobKitob, Double> getBalans2Column() {
        TableColumn<HisobKitob, Double> balansColumn = new TableColumn<>("Balans");
        balansColumn.setMinWidth(100);
        balansColumn.setCellValueFactory(new PropertyValueFactory<>("balans"));
        balansColumn.setCellFactory(column -> {
            TableCell<HisobKitob, Double> cell = new TableCell<HisobKitob, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        setText(new MoneyShow().format(item));
                    }
                }
            };
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        return balansColumn;
    }

    public static TableView<HisobKitob> getHisobKitobTableView() {
        return hisobKitobTableView;
    }

    public static void setHisobKitobTableView(TableView<HisobKitob> hisobKitobTableView) {
        GetTableView.hisobKitobTableView = hisobKitobTableView;
    }
}
