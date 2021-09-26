package sample.Tools;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import sample.Data.*;
import sample.Model.HisobModels;
import sample.Model.StandartModels;
import sample.Model.UserModels;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TableViewMaster {
    Connection connection;
    User user;

    TableView<HisobKitob> tableView = new TableView();
    TableColumn<HisobKitob, Integer> id = new TableColumn<>("id");
    TableColumn<HisobKitob, Integer> hujjatId = new TableColumn<>("Hujjat N");
    TableColumn<HisobKitob, Date> sana = new TableColumn<>("Sana");
    TableColumn<HisobKitob, Integer> hisob1 = new TableColumn<>("Beruvchi");
    TableColumn<HisobKitob, Integer> hisob2 = new TableColumn<>("Oluvchi");
    TableColumn<HisobKitob, Integer> amal = new TableColumn<>("Amal turi");
    TableColumn<HisobKitob, Integer> valuta = new TableColumn<>("Pul turi");
    TableColumn<HisobKitob, Integer> tovar = new TableColumn<>("Tovar");
    TableColumn<HisobKitob, Double> kurs = new TableColumn<>("Kurs");
    TableColumn<HisobKitob, String> barCode = new TableColumn<>("Tovar shakli");
    TableColumn<HisobKitob, Double> dona = new TableColumn<>("Dona");
    TableColumn<HisobKitob, Double> narh = new TableColumn<>("Narh");
    TableColumn<HisobKitob, Double> summaCol = new TableColumn<> ("Jami");
    DecimalFormat decimalFormat = new MoneyShow();
    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    QaydnomaJami qaydnomaJami;
    TableView<QaydnomaData> qaydnomaTableView;
    Integer amalTuri;
    StandartModels amalModels = new StandartModels();

    public TableViewMaster() {
        initColumns();
    }

    public TableViewMaster(Connection connection, User user, Integer amalTuri, QaydnomaJami qaydnomaJami, TableView<QaydnomaData> qaydnomaTableView) {
        this.connection = connection;
        this.user = user;
        this.amalTuri = amalTuri;
        amalModels.setTABLENAME("Amal");
        this.qaydnomaJami = qaydnomaJami;
        this.qaydnomaTableView = qaydnomaTableView;sana.setMinWidth(20);
        initColumns();
        setTableColumns();
    }

    private void initColumns() {
        sana.setCellValueFactory(new PropertyValueFactory<>("sana"));
        sana.setCellFactory(column -> {
            TableCell<HisobKitob, Date> cell = new TableCell<HisobKitob, Date>() {
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
                        if (hisob  != null) {
                            setText(hisob.getText());
                        } else {
                            setText("");
                        }
                    }
                }
            };
            return cell;
        });

        hisob2.setMinWidth(200);
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
                        Hisob hisob = GetDbData.getHisob(item);
                        if (hisob  != null) {
                            setText(hisob.getText());
                        } else {
                            setText("");
                        }
                    }
                }
            };
            return cell;
        });

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

        valuta.setMinWidth(100);
        valuta.setCellValueFactory(new PropertyValueFactory<>("valuta"));
        valuta.setCellFactory(column -> {
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
            cell.setAlignment(Pos.CENTER);
            return cell;
        });

        tovar.setMinWidth(250);
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

        barCode.setMinWidth(100);
        barCode.setCellValueFactory(new PropertyValueFactory<>("shakl"));
        barCode.setCellFactory(TextFieldTableCell.forTableColumn());
        barCode.setStyle( "-fx-alignment: CENTER-RIGHT;");

        dona.setMinWidth(50);
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

        narh.setMinWidth(100);
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

        kurs.setMinWidth(50);
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

        summaCol.setMinWidth(50);
        summaCol.setStyle( "-fx-alignment: CENTER-RIGHT;");
        summaCol.setCellValueFactory(new PropertyValueFactory<>("summaCol"));
    }

    /***********************************************************************************************************************/
    public TableView<HisobKitob> getTableView() {
        return tableView;
    }
    public void setTableView(TableView<HisobKitob> tableView) {
        this.tableView = tableView;
    }

    public void setTableColumns() {
        switch (amalTuri) {
            case 1:
                tableView.getColumns().addAll(valuta, kurs, narh);
                break;
            case 2:
                tableView.getColumns().addAll(tovar, valuta, kurs, barCode, dona, narh, summaCol);
                break;
            case 3:
                tableView.getColumns().addAll(tovar, barCode, kurs, dona, narh, summaCol);
                break;
            case 15:
                tableView.getColumns().addAll(valuta, kurs, narh);
                break;
        }
    }

    public TableColumn<HisobKitob, Integer> getId() {
        return id;
    }

    public void setId(TableColumn<HisobKitob, Integer> id) {
        this.id = id;
    }

    public TableColumn<HisobKitob, Integer> getHujjatId() {
        return hujjatId;
    }

    public void setHujjatId(TableColumn<HisobKitob, Integer> hujjatId) {
        this.hujjatId = hujjatId;
    }

    public TableColumn<HisobKitob, Date> getSana() {
        return sana;
    }

    public void setSana(TableColumn<HisobKitob, Date> sana) {
        this.sana = sana;
    }

    public TableColumn<HisobKitob, Integer> getHisob1() {
        return hisob1;
    }

    public void setHisob1(TableColumn<HisobKitob, Integer> hisob1) {
        this.hisob1 = hisob1;
    }

    public TableColumn<HisobKitob, Integer> getHisob2() {
        return hisob2;
    }

    public void setHisob2(TableColumn<HisobKitob, Integer> hisob2) {
        this.hisob2 = hisob2;
    }

    public TableColumn<HisobKitob, Integer> getAmal() {
        return amal;
    }

    public void setAmal(TableColumn<HisobKitob, Integer> amal) {
        this.amal = amal;
    }

    public TableColumn<HisobKitob, Integer> getValuta() {
        return valuta;
    }

    public void setValuta(TableColumn<HisobKitob, Integer> valuta) {
        this.valuta = valuta;
    }

    public TableColumn<HisobKitob, Integer> getTovar() {
        return tovar;
    }

    public void setTovar(TableColumn<HisobKitob, Integer> tovar) {
        this.tovar = tovar;
    }

    public TableColumn<HisobKitob, Double> getKurs() {
        return kurs;
    }

    public void setKurs(TableColumn<HisobKitob, Double> kurs) {
        this.kurs = kurs;
    }

    public TableColumn<HisobKitob, String> getBarCode() {
        return barCode;
    }

    public void setBarCode(TableColumn<HisobKitob, String> barCode) {
        this.barCode = barCode;
    }

    public TableColumn<HisobKitob, Double> getDona() {
        return dona;
    }

    public void setDona(TableColumn<HisobKitob, Double> dona) {
        this.dona = dona;
    }

    public TableColumn<HisobKitob, Double> getNarh() {
        return narh;
    }

    public void setNarh(TableColumn<HisobKitob, Double> narh) {
        this.narh = narh;
    }

    public TableColumn<HisobKitob, Double> getSummaCol() {
        return summaCol;
    }

    public void setSummaCol(TableColumn<HisobKitob, Double> summaCol) {
        this.summaCol = summaCol;
    }

}
