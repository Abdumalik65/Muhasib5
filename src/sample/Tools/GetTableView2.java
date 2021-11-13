package sample.Tools;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import sample.Data.*;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GetTableView2 {
    public TableView<Hisob> hisobTableView = new TableView();
    public TableView<QaydnomaData> qaydnomaTableView = new TableView();
    public TableView<HisobKitob> hisobKitobTableView = new TableView();
    public TableView<Standart2> standart2TableView = new TableView();

    public void initTableViews() {
        initHisobTableView();
        initQaydnomaTableView();
        initHisobKitobTableView();
        initStandart2TableView();
    }

    public void initHisobTableView() {
        hisobTableView.getColumns().addAll(getHisobIdColumn(), getHisobTextColumn(), getHisobBalansColumn());
    }

    public TableColumn<Hisob, Integer> getHisobIdColumn() {
        TableColumn<Hisob, Integer> hisobIdColumn = new TableColumn<>("N");
        hisobIdColumn.setMinWidth(20);
        hisobIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        return hisobIdColumn;
    }

    public TableColumn<Hisob, String> getHisobTextColumn() {
        TableColumn<Hisob, String> hisobTextColumn = new TableColumn<>("Hisob nomi");
        hisobTextColumn.setMinWidth(100);
        hisobTextColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
        hisobTextColumn.setCellFactory(tc -> {
            TableCell<Hisob, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(hisobTextColumn.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell ;
        });

        return hisobTextColumn;
    }

    public TableColumn<Hisob, Double> getHisobBalansColumn() {
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
                        String itemString = new MoneyShow().format(item);
                        if (itemString.trim().equals("-0")) {
                            itemString = "0";
                        }
                        setText(itemString);                    }
                }
            };
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        return hisobBalansColumn;
    }

    public TableView<Hisob> getHisobTableView() {
        return hisobTableView;
    }

    public void setHisobTableView(TableView<Hisob> hisobTableView) {
        this.hisobTableView = hisobTableView;
    }

    public TableView<QaydnomaData> initQaydnomaTableView() {
        qaydnomaTableView = new TableView<QaydnomaData>();
        qaydnomaTableView.getColumns().addAll(getIdColumn(), getAmalTuriColumn(), getHujjatColumn(), getSanaColumn(), getChiqimNomiColumn(), getKirimNomiColumn(), getJamiColumn(), getIzohColumn());
        return qaydnomaTableView;
    }

    public TableColumn<QaydnomaData, Integer> getIdColumn() {
        TableColumn<QaydnomaData, Integer> idColumn = new TableColumn("N");
        idColumn.setMinWidth(30);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        return idColumn;
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

    public TableColumn<QaydnomaData, Integer> getHujjatColumn() {
        TableColumn<QaydnomaData, Integer> hujjatColumn = new TableColumn("Hujjat N");
        hujjatColumn.setMinWidth(30);
        hujjatColumn.setCellValueFactory(new PropertyValueFactory<>("hujjat"));
        hujjatColumn.setStyle( "-fx-alignment: TOP_CENTER;");
        return hujjatColumn;
    }

    public TableColumn<QaydnomaData, Date> getSanaColumn() {
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
            cell.setAlignment(Pos.TOP_CENTER);

            return cell;
        });
        return sanaColumn;
    }

    public TableColumn<Hisob, Date> getHisobSanaColumn() {
        TableColumn<Hisob, Date> sanaColumn = new TableColumn("Sana");
        sanaColumn.setMinWidth(80);
        sanaColumn.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        sanaColumn.setCellFactory(column -> {
            TableCell<Hisob, Date> cell = new TableCell<Hisob, Date>() {
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
            cell.setAlignment(Pos.TOP_CENTER);

            return cell;
        });
        return sanaColumn;
    }

    public TableColumn<QaydnomaData, Integer> getChiqimIdColumn() {
        TableColumn<QaydnomaData, Integer> chiqimIdColumn = new TableColumn("Chiqim N");
        chiqimIdColumn.setMinWidth(30);
        chiqimIdColumn.setCellValueFactory(new PropertyValueFactory<>("chiqimId"));
        return chiqimIdColumn;
    }

    public TableColumn<QaydnomaData, String> getChiqimNomiColumn() {
        TableColumn<QaydnomaData, String> chiqimIdColumn = new TableColumn("Chiqim hisobi");
        chiqimIdColumn.setMinWidth(150);
        chiqimIdColumn.setCellValueFactory(new PropertyValueFactory<>("chiqimNomi"));
        return chiqimIdColumn;
    }

    public TableColumn<QaydnomaData, Integer> getKirimIdColumn() {
        TableColumn<QaydnomaData, Integer> kirimIdColumn = new TableColumn("Kirim N");
        kirimIdColumn.setMinWidth(30);
        kirimIdColumn.setCellValueFactory(new PropertyValueFactory<>("kirimId"));
        return kirimIdColumn;
    }

    public TableColumn<QaydnomaData, String> getKirimNomiColumn() {
        TableColumn<QaydnomaData, String> kirimNomiColumn = new TableColumn("Kirim hisobi");
        kirimNomiColumn.setMinWidth(150);
        kirimNomiColumn.setCellValueFactory(new PropertyValueFactory<>("kirimNomi"));
        return kirimNomiColumn;
    }

    public TableColumn<QaydnomaData, String> getIzohColumn() {
        TableColumn<QaydnomaData, String> izohColumn = new TableColumn("Izoh");
        izohColumn.setMinWidth(200);
        izohColumn.setCellValueFactory(new PropertyValueFactory<>("izoh"));
        izohColumn.setCellFactory(column -> {
            TableCell<QaydnomaData, String> cell = new TableCell<QaydnomaData, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
//                        setText(GetDbData.getTovar(item).getText());
                        Text text = new Text(item);
                        text.setStyle("-fx-text-alignment:justify;");
                        text.wrappingWidthProperty().bind(getTableColumn().widthProperty().subtract(2));
                        setGraphic(text);
                    }
                    setAlignment(Pos.CENTER_LEFT);
                }
            };
            return cell;
        });
        return izohColumn;
    }

    public TableColumn<QaydnomaData, Double> getJamiColumn() {
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
                        String itemString = new MoneyShow().format(item);
                        if (itemString.trim().equals("-0")) {
                            itemString = "0";
                        }
                        setText(itemString);                    }
                }
            };
            cell.setAlignment(Pos.TOP_CENTER);
            return cell;
        });
        return jamiColumn;
    }

    public TableView<QaydnomaData> getQaydnomaTableView() {
        return qaydnomaTableView;
    }

    public void setQaydnomaTableView(TableView<QaydnomaData> qaydnomaTableView) {
        this.qaydnomaTableView = qaydnomaTableView;
    }

    public TableView<HisobKitob> initHisobKitobTableView() {
        hisobKitobTableView = new TableView<HisobKitob>();
        hisobKitobTableView.getColumns().addAll(getCustomColumn(), getAmalColumn(), getValutaColumn(), getKursColumn(), getIzoh2Column(), getAdadColumn(), getNarhColumn(), getSummaColumn());
        return hisobKitobTableView;
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
                        Text text = new Text(amal.getText());
                        text.setStyle("-fx-text-alignment:justify;");
                        text.wrappingWidthProperty().bind(getTableColumn().widthProperty().subtract(2));
                        setGraphic(text);
                    }
                }
            };
            return cell;
        });
        return amalColumn;
    }

    public TableColumn<HisobKitob, Integer> getCustomColumn() {
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

    public TableColumn<HisobKitob, Integer> getValutaColumn() {
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
                    setAlignment(Pos.TOP_CENTER);
                }
            };
            return cell;
        });
        return valutaColumn;
    }

    public TableColumn<HisobKitob, Double> getKursColumn() {
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

    public TableColumn<HisobKitob, Integer> getTovarColumn() {
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

    public TableColumn<HisobKitob, String> getBarCodeColumn() {
        TableColumn<HisobKitob, String> hisob2 = new TableColumn<>("Shtrixkod");
        hisob2.setMinWidth(80);
        hisob2.setCellValueFactory(new PropertyValueFactory<>("barCode"));
        return hisob2;
    }

    public TableColumn<HisobKitob, String> getHujjatIdColumn() {
        TableColumn<HisobKitob, String> column = new TableColumn<>("Hujjat №");
        column.setMinWidth(80);
        column.setCellValueFactory(new PropertyValueFactory<>("hujjatId"));
        column.setStyle( "-fx-alignment: CENTER;");
        return column;
    }

    public TableColumn<HisobKitob, String> getHKIdColumn() {
        TableColumn<HisobKitob, String> column = new TableColumn<>("Id");
        column.setMinWidth(80);
        column.setCellValueFactory(new PropertyValueFactory<>("Id"));
        column.setStyle( "-fx-alignment: CENTER;");
        return column;
    }

    public TableColumn<HisobKitob, String> getBirlikColumn() {
        TableColumn<HisobKitob, String> birlikColumn = new TableColumn<>("Birlik");
        birlikColumn.setCellValueFactory(new PropertyValueFactory<>("barCode"));
        birlikColumn.setMinWidth(100);
        birlikColumn.setMaxWidth(100);
        birlikColumn.setCellFactory(column -> {
            TableCell<HisobKitob, String> cell = new TableCell<HisobKitob, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        BarCode bc = GetDbData.getBarCode(item);
                        Standart birlik = GetDbData.getBirlik(bc.getBirlik());
                        setText(bc.getBarCode() + "\n" + birlik.getText());
                    }
                    setAlignment(Pos.CENTER_LEFT);
                }
            };
            return cell;
        });
        return birlikColumn;
    }

    public TableColumn<HisobKitob, String> getIzoh2Column() {
        TableColumn<HisobKitob, String> izohColumn = new TableColumn<>("Eslatma");
        izohColumn.setMinWidth(150);
        izohColumn.setCellValueFactory(new PropertyValueFactory<>("izoh"));
        izohColumn.setCellFactory(column -> {
            TableCell<HisobKitob, String> cell = new TableCell<HisobKitob, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
//                        setText(GetDbData.getTovar(item).getText());
                        Text text = new Text(item);
                        text.setStyle("-fx-text-alignment:justify;");
                        text.wrappingWidthProperty().bind(getTableColumn().widthProperty().subtract(2));
                        setGraphic(text);
                    }
                    setAlignment(Pos.CENTER_LEFT);
                }
            };
            return cell;
        });
        return izohColumn;
    }

    public TableColumn<HisobKitob, Double> getAdadColumn() {
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

    public TableColumn<HisobKitob, Double> getNarhColumn() {
        TableColumn<HisobKitob, Double> narhColumn = new TableColumn<>("Narh");
        narhColumn.setMinWidth(150);
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

    public TableColumn<HisobKitob, Double> getSummaColumn() {
        TableColumn<HisobKitob, Double> summaColumn = new TableColumn<>("Summa");
        summaColumn.setMinWidth(150);
        summaColumn.setCellValueFactory(new PropertyValueFactory<>("summaCol"));
        summaColumn.setCellFactory(column -> {
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
        return summaColumn;
    }

    public TableColumn<HisobKitob, Double> getBalans2Column() {
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
                        String itemString = new MoneyShow().format(item);
                        if (itemString.trim().equals("-0")) {
                            itemString = "0";
                        }
                        setText(itemString);
                    }
                }
            };
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        return balansColumn;
    }

    public TableColumn<HisobKitob, Date> getDateTimeColumn() {
        TableColumn<HisobKitob, Date> sanaColumn = new TableColumn("Sana");
        sanaColumn.setMinWidth(80);
        sanaColumn.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        sanaColumn.setCellFactory(column -> {
            TableCell<HisobKitob, Date> cell = new TableCell<HisobKitob, Date>() {
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy\n HH.mm.ss");
                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    } else {
                        if (item != null) {
                            setText(format.format(item));
                        }
                    }
                }
            };
            cell.setAlignment(Pos.TOP_CENTER);

            return cell;
        });
        return sanaColumn;
    }

    public TableColumn<HisobKitob, Double> getVaznColumn() {
        TableColumn<HisobKitob, Double> vaznColumn = new TableColumn<>("Vazn");
        vaznColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, Double>, ObservableValue<Double>>() {

            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<HisobKitob, Double> param) {
                HisobKitob hisobKitob = param.getValue();
                BarCode b = GetDbData.getBarCode(hisobKitob.getBarCode());
                Double vaznDouble = .0;
                if (b != null) {
                    vaznDouble = b.getVazn() * hisobKitob.getDona();
                }
                return new SimpleObjectProperty<Double>(vaznDouble);
            }
        });

        vaznColumn.setMinWidth(50);
        vaznColumn.setStyle( "-fx-alignment: CENTER;");
        return vaznColumn;
    }

    public TableColumn<HisobKitob, Double> getHajmColumn() {
        TableColumn<HisobKitob, Double> hajmColumn = new TableColumn<>("Hajm");
        hajmColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HisobKitob, Double>, ObservableValue<Double>>() {

            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<HisobKitob, Double> param) {
                HisobKitob hisobKitob = param.getValue();
                BarCode b = GetDbData.getBarCode(hisobKitob.getBarCode());
                Double hajmDouble = .0;
                if (b != null) {
                    hajmDouble = b.getHajm() * hisobKitob.getDona();
                }
                return new SimpleObjectProperty<Double>(hajmDouble);
            }
        });

        hajmColumn.setMinWidth(50);
        hajmColumn.setStyle( "-fx-alignment: CENTER;");
        return hajmColumn;
    }

    public void initStandart2TableView() {
        HBox.setHgrow(standart2TableView, Priority.ALWAYS);
        VBox.setVgrow(standart2TableView, Priority.ALWAYS);
        standart2TableView.getColumns().addAll(getStandart2Id2Column(), getStandart2TextColumn());
    }

    public TableColumn<Standart2, Integer> getStandart2Id2Column() {
        TableColumn<Standart2, Integer> id2Column = new TableColumn<>("Tovar");
        id2Column.setMinWidth(100);
        id2Column.setCellValueFactory(new PropertyValueFactory<>("id2"));
        id2Column.setCellFactory(column -> {
            TableCell<Standart2, Integer> cell = new TableCell<Standart2, Integer>() {
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
        return id2Column;
    }

    public TableColumn<Standart2, Integer> getStandart2TextColumn() {
        TableColumn<Standart2, Integer> id2Column = new TableColumn<>("Shtrixkod");
        id2Column.setMinWidth(100);
        id2Column.setCellValueFactory(new PropertyValueFactory<>("text"));
        return id2Column;
    }

    public TableView<HisobKitob> getHisobKitobTableView() {
        return hisobKitobTableView;
    }

    public void setHisobKitobTableView(TableView<HisobKitob> hisobKitobTableView) {
        this.hisobKitobTableView = hisobKitobTableView;
    }

    public TableView<Standart2> getStandart2TableView() {
        return standart2TableView;
    }
}
