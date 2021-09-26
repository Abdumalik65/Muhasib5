package sample.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Data.*;
import sample.Tools.Alerts;
import sample.Tools.GetDbData;
import sample.Tools.QueryHelper;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HisobKitobModels {
    private String TABLENAME = "HisobKitob";
    private final String ID_FIELD = "id";
    private final String QAYDID = "qaydId";
    private final String HUJJAT = "hujjatId";
    private final String AMAL = "amal";
    private final String HISOB1 = "hisob1";
    private final String HISOB2 = "hisob2";
    private final String VALUTA = "valuta";
    private final String TOVAR = "tovar";
    private final String KURS = "kurs";
    private final String BARCODE = "barCode";
    private final String DONA = "dona";
    private final String NARH = "narh";
    private final String MANBA = "manba";
    private final String IZOH = "izoh";
    private final String USERID = "userId";
    private final String DATETIME = "dateTime";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    QueryHelper queryHelper;

    public HisobKitobModels() {
    }

    public HisobKitobModels(String TABLENAME) {
        this.TABLENAME = TABLENAME;
    }

    public ResultSet getResultSet(Connection connection, String select) {
        ResultSet rs = null;
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public ObservableList<HisobKitob> get_data(Connection connection) {
        ObservableList<HisobKitob> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME;
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            saveResult(books, rs);
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
        return books;
    }

    public ObservableList<HisobKitob> getAnyData(Connection connection, String sqlWhere, String sqlOrderBy) {
        ObservableList<HisobKitob> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        queryHelper = new QueryHelper(sqlWhere, sqlOrderBy);
        String select = "SELECT * FROM " + TABLENAME + queryHelper.getYakuniyJumla();
            PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            saveResult(books, rs);
            rs.close();
            prSt.close();
        } catch (SQLException e) {
//           Alerts.losted();
            e.printStackTrace();
        }
        return books;
    }

    public ObservableList<HisobKitob> getAnyData(Connection connection, String sqlSelectItems, String sqlWhere, String sqlOrderBy) {
        ObservableList<HisobKitob> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        queryHelper = new QueryHelper(sqlWhere, sqlOrderBy);
        String select = "SELECT " + sqlSelectItems + " FROM " + TABLENAME + queryHelper.getYakuniyJumla();
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            saveResult(books, rs);
            rs.close();
            prSt.close();
        } catch (SQLException e) {
           Alerts.losted();
        }
        return books;
    }

    public ObservableList<QaydnomaData> getDistinct(Connection connection, int hisobId) {
        ObservableList<QaydnomaData> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT DISTINCT " + QAYDID + " FROM " + TABLENAME + " WHERE hisob1 = ? OR hisob2 = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setInt(1, hisobId);
            prSt.setInt(2, hisobId);
            rs = prSt.executeQuery();
            QaydnomaModel qaydnomaModel = new QaydnomaModel();
            while (rs.next()) {
                QaydnomaData q = qaydnomaModel.getDataById(connection, rs.getInt(1));
                books.add(q);
            }
            rs.close();
            prSt.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return books;
    }

    public ObservableList<QaydnomaData> getDistinct(Connection connection) {
        ObservableList<QaydnomaData> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT DISTINCT " + QAYDID + " FROM " + TABLENAME;
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            while (rs.next()) {
                QaydnomaData q = new QaydnomaData();
                q.setId(rs.getInt(1));
                books.add(q);
            }
            rs.close();
            prSt.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return books;
    }

    public ObservableList<QaydnomaData> getDistinctQaydId(Connection connection, int amal) {
        ObservableList<QaydnomaData> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT DISTINCT " + QAYDID + " FROM " + TABLENAME + " WHERE amal = ?" ;
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setInt(1, amal);
            rs = prSt.executeQuery();
            QaydnomaModel qaydnomaModel = new QaydnomaModel();
            while (rs.next()) {
                QaydnomaData q = qaydnomaModel.getDataById(connection, rs.getInt(1));
                books.add(q);
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
        return books;
    }

    public ObservableList<Valuta> getDistinctValuta(Connection connection, int hisobId, Date date) {
        ObservableList<Valuta> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT DISTINCT " + VALUTA + " FROM " + TABLENAME + " WHERE (hisob1 = ? OR hisob2 = ?)  AND " + TOVAR +" = 0 AND " + DATETIME + "  <= ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setInt(1, hisobId);
            prSt.setInt(2, hisobId);
            prSt.setString(3, sdf.format(date));
            rs = prSt.executeQuery();
            while (rs.next()) {
                Valuta v = GetDbData.getValuta(rs.getInt(1));
                books.add(v);
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
           Alerts.losted();
        }
        return books;
    }

    public ObservableList<HisobKitob> getDistinctValuta2(Connection connection, int hisobId, Date date) {
        ObservableList<HisobKitob> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT DISTINCT " + VALUTA + " FROM " + TABLENAME + " WHERE (hisob1 = ? OR hisob2 = ?)  AND " + TOVAR +" = 0 AND " + DATETIME + "  <= ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setInt(1, hisobId);
            prSt.setInt(2, hisobId);
            prSt.setString(3, sdf.format(date));
            rs = prSt.executeQuery();
            while (rs.next()) {
                HisobKitob hk1 = new HisobKitob(
                        0,
                        0,
                        0,
                        0,
                        hisobId,
                        0,
                        rs.getInt(1),
                        0,
                        0d,
                        " ",
                        0d,
                        0d,
                        0,
                        "",
                        0,
                        null
                );
                books.add(hk1);
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public ObservableList<HisobKitob> getDistinctTovar2(Connection connection, int hisobId, Date date) {
        ObservableList<HisobKitob> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT DISTINCT " + TOVAR + " FROM " + TABLENAME + " WHERE (hisob1 = ? OR hisob2 = ?)  AND " + TOVAR +" > 0 AND " + DATETIME + "  <= ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setInt(1, hisobId);
            prSt.setInt(2, hisobId);
            prSt.setString(3, sdf.format(date));
            rs = prSt.executeQuery();
            while (rs.next()) {
                HisobKitob hk1 = new HisobKitob(
                        0,
                        0,
                        0,
                        0,
                        hisobId,
                        0,
                        0,
                        rs.getInt(1),
                        0d,
                        " ",
                        0d,
                        0d,
                        0,
                        "",
                        0,
                        null
                );
                books.add(hk1);
                prSt.close();
                rs.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public ObservableList<Standart> getDistinctTovar(Connection connection, int hisobId, Date date) {
        ObservableList<Standart> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT DISTINCT " + TOVAR + " FROM " + TABLENAME + " WHERE (hisob1 = ? OR hisob2 = ?)  AND " + TOVAR +" > 0 AND " + DATETIME + "  <= ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setInt(1, hisobId);
            prSt.setInt(2, hisobId);
            prSt.setString(3, sdf.format(date));
            rs = prSt.executeQuery();
            while (rs.next()) {
                Standart v = GetDbData.getTovar(rs.getInt(1));
                books.add(v);
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
           Alerts.losted();
        }
        return books;
    }

    public ObservableList<BarCode> getDistinctBarCode(Connection connection, int hisobId) {
        ObservableList<BarCode> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT DISTINCT " + BARCODE + " FROM " + TABLENAME + " WHERE hisob1 = ? OR hisob2 = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setInt(1, hisobId);
            prSt.setInt(2, hisobId);
            rs = prSt.executeQuery();
            while (rs.next()) {
                BarCode bc = GetDbData.getBarCode(rs.getString(1));
                books.add(bc);
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
           Alerts.losted();
        }
        return books;
    }

    public ObservableList<BarCode> getDistinctBarCode(Connection connection, int hisobId, Date date) {
        ObservableList<BarCode> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT DISTINCT " + BARCODE + " FROM " + TABLENAME + " WHERE hisob1 = ? OR hisob2 = ? AND " + DATETIME + "  <= ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setInt(1, hisobId);
            prSt.setInt(2, hisobId);
            prSt.setString(3, sdf.format(date));
            rs = prSt.executeQuery();
            while (rs.next()) {
                BarCode bc = GetDbData.getBarCode(rs.getString(1));
                books.add(bc);
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
           Alerts.losted();
        }
        return books;
    }

    public Hisob getValutaBalans(Connection connection, Hisob hisob, Valuta valuta) {
        ResultSet rs = null;
        String select = "SELECT SUM("+NARH+") FROM " + TABLENAME + " WHERE " + HISOB1 + " = ? AND " + VALUTA + "=?" + " AND " + TOVAR + " = 0";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setInt(1,hisob.getId());
            prSt.setInt(2,valuta.getId());
            rs = prSt.executeQuery();
            hisob.setKirim(.00);
            hisob.setChiqim(.00);
            hisob.setBalans(.00);
            while (rs.next()) {
                hisob.setChiqim(rs.getDouble(1));
            }
            select = "SELECT SUM("+NARH+") FROM " + TABLENAME + " WHERE " + HISOB2 + " = ? AND " + VALUTA + "=?" + " AND " + TOVAR + " = 0";
            prSt = connection.prepareStatement(select);
            prSt.setInt(1,hisob.getId());
            prSt.setInt(2,valuta.getId());
            rs = prSt.executeQuery();
            while (rs.next()) {
                hisob.setKirim(rs.getDouble(1));
            }
            hisob.setBalans(hisob.getKirim()-hisob.getChiqim());
            rs.close();
            prSt.close();
        } catch (SQLException e) {
           Alerts.losted();
        }
        return hisob;
    }

    public HisobKitob getValutaBalans(Connection connection, Integer hisobId, Valuta valuta, Date date) {
        ResultSet rs = null;
        HisobKitob hisobKitob = new HisobKitob(
                null, 0,0,0,hisobId,0,valuta.getId(),0,
                0.0,"",.0,.0,0,valuta.getValuta(),1,null
        );
        String select = "SELECT SUM("+NARH+") FROM " + TABLENAME + " WHERE " + HISOB1 + " = ? AND " + VALUTA + "=?" + " AND " + TOVAR + " = 0 AND " + DATETIME + "<= ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setInt(1,hisobId);
            prSt.setInt(2,valuta.getId());
            prSt.setString(3,sdf.format(date));
            rs = prSt.executeQuery();
            while (rs.next()) {
                double eskiNarh = hisobKitob.getNarh();
                double narh = rs.getDouble(1);
                hisobKitob.setNarh(eskiNarh - narh);
            }
            rs.close();
            prSt.close();
            select = "SELECT SUM("+NARH+") FROM " + TABLENAME + " WHERE " + HISOB2 + " = ? AND " + VALUTA + "=?" + " AND " + TOVAR + " = 0 AND " + DATETIME + "<= ?";
            prSt = connection.prepareStatement(select);
            prSt.setInt(1,hisobId);
            prSt.setInt(2,valuta.getId());
            prSt.setString(3,sdf.format(date));
            rs = prSt.executeQuery();
            while (rs.next()) {
                double eskiNarh = hisobKitob.getNarh();
                double narh = rs.getDouble(1);
                hisobKitob.setNarh(narh + eskiNarh);
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
           Alerts.losted();
        }
        return hisobKitob;
    }

    public Double getValutaBalans(Connection connection, Integer hisob, Valuta valuta) {
        ResultSet rs = null;
        Double jami = .0;
        String select = "SELECT SUM("+NARH+") FROM " + TABLENAME + " WHERE " + HISOB1 + " = ? AND " + VALUTA + "=?" + " AND " + TOVAR + " = 0";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setInt(1,hisob);
            prSt.setInt(2,valuta.getId());
            rs = prSt.executeQuery();
            while (rs.next()) {
                jami -= rs.getDouble(1);
            }
            select = "SELECT SUM("+NARH+") FROM " + TABLENAME + " WHERE " + HISOB2 + " = ? AND " + VALUTA + "=?" + " AND " + TOVAR + " = 0";
            prSt = connection.prepareStatement(select);
            prSt.setInt(1,hisob);
            prSt.setInt(2,valuta.getId());
            rs = prSt.executeQuery();
            while (rs.next()) {
                jami += rs.getDouble(1);
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
           Alerts.losted();
        }
        return jami;
    }

    public HisobKitob getTovarBalans(Connection connection, Integer hisobId, Standart tovar, Date date) {
        ResultSet rs = null;
        ObservableList<HisobKitob> books = FXCollections.observableArrayList();
        HisobKitob hisobKitob = new HisobKitob(
                null, 0,0,0,hisobId,0,0,tovar.getId(),
                0.0,"",.0,.0,0,tovar.getText(),1,null
        );
        String select = "SELECT * FROM " + TABLENAME + " WHERE " + HISOB1 + " = ? AND " + TOVAR + "=? AND " + DATETIME + "<= ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setInt(1,hisobId);
            prSt.setInt(2,tovar.getId());
            prSt.setString(3,sdf.format(date));
            rs = prSt.executeQuery();
            saveResult(books, rs);
            for (HisobKitob hk: books) {
                BarCode barCode = GetDbData.getBarCode(hk.getBarCode());
                double tovarDonasi = barCode != null ? tovarDonasi(barCode) : 1.0;
                double kurs = hk.getKurs();
                double dona = hk.getDona() * tovarDonasi ;
                double narh = dona * hk.getNarh()/tovarDonasi;
                narh /= kurs;
                hisobKitob.setDona(hisobKitob.getDona() - dona);
                hisobKitob.setNarh(hisobKitob.getNarh() - narh);
            }

            books.removeAll(books);
            select = "SELECT * FROM " + TABLENAME + " WHERE " + HISOB2 + " = ? AND " + TOVAR + " = ? AND " + DATETIME + "<= ?";
            prSt = connection.prepareStatement(select);
            prSt.setInt(1,hisobId);
            prSt.setInt(2,tovar.getId());
            prSt.setString(3,sdf.format(date));
            rs = prSt.executeQuery();
            saveResult(books, rs);
            for (HisobKitob hk: books) {
                BarCode barCode = GetDbData.getBarCode(hk.getBarCode());
                double tovarDonasi = barCode != null ? tovarDonasi(barCode) : 1.0;
                double kurs =  hk.getKurs();
                double dona = hk.getDona() * tovarDonasi;
                double narh = dona * hk.getNarh()/tovarDonasi;
                narh /= kurs;
                hisobKitob.setDona(dona  + hisobKitob.getDona());
                hisobKitob.setNarh(narh + hisobKitob.getNarh());
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
        return hisobKitob;
    }

    public Double getTovarBalansDouble(Connection connection, Integer hisobId, Standart tovar, Date date) {
        ResultSet rs = null;
        ObservableList<HisobKitob> books = FXCollections.observableArrayList();
        HisobKitob hisobKitob = new HisobKitob(
                null, 0,0,0,hisobId,0,0,tovar.getId(),
                0.0,"",.0,.0,0,tovar.getText(),1,null
        );
        String select = "SELECT * FROM " + TABLENAME + " WHERE " + HISOB1 + " = ? AND " + TOVAR + "=? AND " + DATETIME + "<= ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setInt(1,hisobId);
            prSt.setInt(2,tovar.getId());
            prSt.setString(3,sdf.format(date));
            rs = prSt.executeQuery();
            saveResult(books, rs);
            rs.close();
            for (HisobKitob hk: books) {
                BarCode barCode = GetDbData.getBarCode(hk.getBarCode());
                double tovarDonasi = barCode != null ? tovarDonasi(barCode) : 1.0;
                double kurs = hk.getKurs();
                double dona = hk.getDona() * tovarDonasi ;
                double narh = dona * hk.getNarh()/tovarDonasi;
                narh /= kurs;
                hisobKitob.setDona(hisobKitob.getDona() - dona);
                hisobKitob.setNarh(hisobKitob.getNarh() - narh);
            }

            books.removeAll(books);
            select = "SELECT * FROM " + TABLENAME + " WHERE " + HISOB2 + " = ? AND " + TOVAR + " = ? AND " + DATETIME + "<= ?";
            prSt = connection.prepareStatement(select);
            prSt.setInt(1,hisobId);
            prSt.setInt(2,tovar.getId());
            prSt.setString(3,sdf.format(date));
            rs = prSt.executeQuery();
            saveResult(books, rs);
            for (HisobKitob hk: books) {
                BarCode barCode = GetDbData.getBarCode(hk.getBarCode());
                double tovarDonasi = barCode != null ? tovarDonasi(barCode) : 1.0;
                double kurs =  hk.getKurs();
                double dona = hk.getDona() * tovarDonasi;
                double narh = dona * hk.getNarh()/tovarDonasi;
                narh /= kurs;
                hisobKitob.setDona(dona  + hisobKitob.getDona());
                hisobKitob.setNarh(narh + hisobKitob.getNarh());
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
        return hisobKitob.getDona();
    }

    public Double getHisobBalance(Connection connection, Hisob hisob) {
        Double jami = 0d;
        ObservableList<HisobKitob> hisobKitobObservableList = getAnyData(connection, HISOB1 + " = " + hisob.getId() + " OR " + HISOB2 + " = " + hisob.getId(), "");
        for (HisobKitob hk: hisobKitobObservableList) {
            if (hk.getHisob1().equals(hisob.getId())) {
                jami -= hk.getSummaCol();
            }
            else if (hk.getHisob2().equals(hisob.getId())) {
                jami += hk.getSummaCol();
            }
        }
        return jami;
    }

    public ObservableList<HisobKitob> getBarCodeBalans(Connection connection, Integer hisobId, String barCode, Date date) {
        ObservableList<HisobKitob> kirimList = FXCollections.observableArrayList();
        ObservableList<HisobKitob> chiqimList = FXCollections.observableArrayList();
        ObservableList<HisobKitob> balanceList = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME + " WHERE " + HISOB2 + " = ? AND " + BARCODE + " = ? AND " + DATETIME + " <= ?" ;
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setInt(1, hisobId);
            prSt.setString(2, barCode);
            prSt.setString(3, sdf.format(date));
            rs = prSt.executeQuery();
            while (rs.next()) {
                String bcString = rs.getString(10);
                BarCode bc = GetDbData.getBarCode(bcString);
                Standart birlik = GetDbData.getBirlik(bc.getBirlik());
                Standart tovar = GetDbData.getTovar(rs.getInt(8));
                String izoh = tovar.getText() + "\n" + bcString + "\n" + birlik.getText();
                HisobKitob hk1 = new HisobKitob(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getInt(4),
                        rs.getInt(5),
                        rs.getInt(6),
                        rs.getInt(7),
                        rs.getInt(8),
                        rs.getDouble(9),
                        rs.getString(10),
                        rs.getDouble(11),
                        rs.getDouble(12),
                        rs.getInt(13),
                        izoh,
                        rs.getInt(15),
                        sdf.parse(rs.getString(16))
                );
                kirimList.add(hk1);
            }
            select = "SELECT " + MANBA + ", SUM(" + DONA + "), SUM(" + DONA + " * " + NARH + ") FROM " + TABLENAME + " WHERE " + HISOB1 + " = ? AND " + DATETIME + " <= ? GROUP BY " + MANBA;
            prSt = null;
            prSt = connection.prepareStatement(select);
            prSt.setInt(1, hisobId);
            prSt.setString(2, sdf.format(date));
            rs = prSt.executeQuery();
            while (rs.next()) {
                int manba = rs.getInt(1);
                double dona = rs.getDouble(2);
                double narh = rs.getDouble(3);
                HisobKitob hk2 = new HisobKitob();
                hk2.setId(manba);
                hk2.setDona(dona);
                hk2.setNarh(narh);
                chiqimList.add(hk2);
            }
            for (HisobKitob kirim: kirimList) {
                for (HisobKitob chiqim: chiqimList) {
                    if (kirim.getId().equals(chiqim.getId())) {
                        kirim.setDona(kirim.getDona() - chiqim.getDona());
                        kirim.setNarh(kirim.getNarh() - chiqim.getNarh());
                        break;
                    }
                }
            }
            for (HisobKitob h: kirimList) {
                if (!h.getDona().equals(0.0)) {
                    balanceList.add(h);
                }
            }

            rs.close();
            prSt.close();
        } catch (SQLException | ParseException e) {
           Alerts.losted();
        }
        return balanceList;
    }

    public HisobKitob getBarCodeBalans(Connection connection, Integer hisobId, BarCode barCode, Date date) {
        ObservableList<HisobKitob> books = FXCollections.observableArrayList();
        Standart tovar = GetDbData.getTovar(barCode.getTovar());
        Standart birlik = GetDbData.getBirlik(barCode.getBirlik());
        String izohString = "Tovar: "+ tovar.getText() + "\nShtrixkod: " + barCode.getBarCode() + "\nO`lchov birligi: " + birlik.getText();
        ResultSet rs = null;
        HisobKitob hisobKitob = new HisobKitob(
                null, 0,0,0,hisobId,0,0,barCode.getTovar(),
                0.0,barCode.getBarCode(),.0,.0,0,izohString,1,null
        );
        String select = "SELECT * FROM " + TABLENAME + " WHERE " + HISOB1 + " = ? AND " + BARCODE + "=? AND " + DATETIME + "<= ? AND  " + TOVAR + " > 0";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setInt(1,hisobId);
            prSt.setString(2,barCode.getBarCode());
            prSt.setString(3,sdf.format(date));
            rs = prSt.executeQuery();
            saveResult(books, rs);
            for (HisobKitob hk: books) {
                double kurs = hk.getKurs();
                double dona = hk.getDona();
                double narh = dona * hk.getNarh();
                narh /= kurs;
                hisobKitob.setDona(hisobKitob.getDona() - dona);
                hisobKitob.setNarh(hisobKitob.getNarh() - narh);
                hisobKitob.setKurs(1.0);
            }
            books.removeAll(books);
            select = "SELECT * FROM " + TABLENAME + " WHERE " + HISOB2 + " = ? AND " + BARCODE + " = ? AND " + DATETIME + "<= ? AND " + TOVAR + " > 0";
            prSt = connection.prepareStatement(select);
            prSt.setInt(1,hisobId);
            prSt.setString(2,barCode.getBarCode());
            prSt.setString(3,sdf.format(date));
            rs = prSt.executeQuery();
            saveResult(books, rs);
            for (HisobKitob hk: books) {
                double kurs =  hk.getKurs();
                double dona = hk.getDona();
                double narh = dona * hk.getNarh();
                narh /= kurs;
                hisobKitob.setDona(dona  + hisobKitob.getDona());
                hisobKitob.setNarh(narh + hisobKitob.getNarh());
                hisobKitob.setKurs(1.0);
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
           Alerts.losted();
        }
        return hisobKitob;
    }

    public ObservableList<HisobKitob> getBarCodeQoldiq(Connection connection, Integer hisobId, BarCode barCode, Date date) {
        ObservableList<HisobKitob> books = FXCollections.observableArrayList();
        Standart tovar = GetDbData.getTovar(barCode.getTovar());
        Standart birlik = GetDbData.getBirlik(barCode.getBirlik());
        String izohString = "Tovar: "+ tovar.getText() + "\nShtrixkod: " + barCode.getBarCode() + "\nO`lchov birligi: " + birlik.getText();
        ResultSet rs = null;
        String select = "";
        PreparedStatement prSt = null;
        try {
            select = "SELECT * FROM " + TABLENAME + " WHERE " + HISOB2 + " = ? AND " + BARCODE + " = ? AND " + DATETIME + "<= ?";
            prSt = connection.prepareStatement(select);
            prSt.setInt(1,hisobId);
            prSt.setString(2,barCode.getBarCode());
            prSt.setString(3,sdf.format(date));
            rs = prSt.executeQuery();
            saveResult(books, rs);
            for (HisobKitob hk: books) {
                hk.setIzoh(izohString);
                select = "SELECT SUM("+DONA+") FROM " + TABLENAME + " WHERE " + MANBA + " = ?";
                prSt = connection.prepareStatement(select);
                prSt.setInt(1,hk.getId());
                rs = prSt.executeQuery();
                double dona = .0;
                while (rs.next()) {
                    dona += rs.getDouble(1);
                }
                hk.setDona(hk.getDona() - dona);
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
           Alerts.losted();
        }
        books.removeIf(item -> item.getDona().equals(0.0));
        return books;
    }

    public Double getBarCodeCount(Connection connection, Integer hisobId, String barCode) {
        ResultSet rs = null;
        Double jami = .0;
        String select = "SELECT SUM(" + DONA + ") FROM " + TABLENAME + " WHERE " + HISOB1 + " = ? AND " +  BARCODE + " = ?" ;
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setInt(1,hisobId);
            prSt.setString(2, barCode);
            rs = prSt.executeQuery();
            while (rs.next()) {
                jami -= rs.getDouble(1);
            }
            select = "SELECT SUM(" + DONA + ") FROM " + TABLENAME + " WHERE " + HISOB2 + " = ? AND " +  BARCODE + " = ?" ;
            prSt = connection.prepareStatement(select);
            prSt.setInt(1,hisobId);
            prSt.setString(2, barCode);
            rs = prSt.executeQuery();
            while (rs.next()) {
                jami += rs.getDouble(1);
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
           Alerts.losted();
        }
        return jami;
    }

    public ObservableList<HisobKitob> getAllForHisob(Connection connection, int hisobId) {
        ObservableList<HisobKitob> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME + " WHERE " + HISOB1 + " = ? OR " + HISOB2 + " = ?" ;
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setInt(1,hisobId);
            prSt.setInt(2,hisobId);
            rs = prSt.executeQuery();
            saveResult(books, rs);
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
        return books;
    }

    public ObservableList<HisobKitob> getAllForHisob(Connection connection, int hisobId, String  sqlWhere) {
        ObservableList<HisobKitob> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME + " WHERE (" + HISOB1 + " = ? OR " + HISOB2 + " = ?)";
        if (!sqlWhere.isEmpty()) {
            select = select + " " + sqlWhere;
        }
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setInt(1,hisobId);
            prSt.setInt(2,hisobId);
            rs = prSt.executeQuery();
            saveResult(books, rs);
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public Integer insert_data(Connection connection, HisobKitob hisobKitob) {
        Integer insertedID = -1;
        ResultSet rs = null;
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + QAYDID + ", "
                + HUJJAT + ", "
                + AMAL + ", "
                + HISOB1 + ", "
                + HISOB2 + ", "
                + VALUTA + ", "
                + TOVAR + ", "
                + KURS + ", "
                + BARCODE + ", "
                + DONA + ", "
                + NARH + ", "
                + MANBA + ", "
                + IZOH + ", "
                + USERID + ", "
                + DATETIME +
                ") VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            prSt.setInt(1, hisobKitob.getQaydId());
            prSt.setInt(2, hisobKitob.getHujjatId());
            prSt.setInt(3, hisobKitob.getAmal());
            prSt.setInt(4, hisobKitob.getHisob1());
            prSt.setInt(5, hisobKitob.getHisob2());
            prSt.setInt(6, hisobKitob.getValuta());
            prSt.setInt(7, hisobKitob.getTovar());
            prSt.setDouble(8, hisobKitob.getKurs());
            prSt.setString(9, hisobKitob.getBarCode());
            prSt.setDouble(10, hisobKitob.getDona());
            prSt.setDouble(11, hisobKitob.getNarh());
            prSt.setDouble(12, hisobKitob.getManba());
            prSt.setString(13, hisobKitob.getIzoh());
            prSt.setDouble(14, hisobKitob.getUserId());
            prSt.setString(15, sdf.format(hisobKitob.getDateTime()));
            prSt.executeUpdate();
            rs = prSt.getGeneratedKeys();
            if(rs.next()){
                insertedID = rs.getInt(1);
                hisobKitob.setId(insertedID);
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
           Alerts.losted();
        }
        return insertedID;
    }

    public void delete_data(Connection connection, HisobKitob hisobKitob)  {
        String delete = "DELETE FROM " + TABLENAME + " WHERE " + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(delete);
            prSt.setInt(1, hisobKitob.getId());
            prSt.executeUpdate();
            prSt.close();
        } catch (SQLException e) {
           Alerts.losted();
        }
    }

    public void deleteWhere(Connection connection, String sqlWhere) {
        queryHelper = new QueryHelper(sqlWhere, "");
        String delete = "DELETE FROM " + TABLENAME +  queryHelper.getYakuniyJumla();
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(delete);
            prSt.executeUpdate();
            prSt.close();
        } catch (SQLException e) {
           Alerts.losted();
        }
    }

    public void update_data(Connection connection, HisobKitob hisobKitob){
        String replace = "UPDATE " + TABLENAME + " SET "
                + QAYDID + " = ?,"
                + HUJJAT + " = ?,"
                + AMAL + " = ?,"
                + HISOB1 + " = ?,"
                + HISOB2 + " = ?,"
                + VALUTA + " = ?,"
                + TOVAR + " = ?,"
                + KURS + " = ?,"
                + BARCODE + " = ?,"
                + DONA + " = ?,"
                + NARH + " = ?,"
                + MANBA + " = ?,"
                + IZOH + " = ?,"
                + USERID + " = ?,"
                + DATETIME + " = ? WHERE "
                + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(replace);
            prSt.setInt(1, hisobKitob.getQaydId());
            prSt.setInt(2, hisobKitob.getHujjatId());
            prSt.setInt(3, hisobKitob.getAmal());
            prSt.setInt(4, hisobKitob.getHisob1());
            prSt.setInt(5, hisobKitob.getHisob2());
            prSt.setInt(6, hisobKitob.getValuta());
            prSt.setInt(7, hisobKitob.getTovar());
            prSt.setDouble(8, hisobKitob.getKurs());
            prSt.setString(9, hisobKitob.getBarCode());
            prSt.setDouble(10, hisobKitob.getDona());
            prSt.setDouble(11, hisobKitob.getNarh());
            prSt.setDouble(12, hisobKitob.getManba());
            prSt.setString(13, hisobKitob.getIzoh());
            prSt.setInt(14, hisobKitob.getUserId());
            prSt.setString(15, sdf.format(hisobKitob.getDateTime()));
            prSt.setInt(16, hisobKitob.getId());
            prSt.executeUpdate();
            prSt.close();
        } catch (SQLException e) {
           Alerts.losted();
        }
    }

    public void addBatch(Connection connection, ObservableList<HisobKitob> hisobKitobObservableList) {
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + QAYDID + ", "
                + HUJJAT + ", "
                + AMAL + ", "
                + HISOB1 + ", "
                + HISOB2 + ", "
                + VALUTA + ", "
                + TOVAR + ", "
                + KURS + ", "
                + BARCODE + ", "
                + DONA + ", "
                + NARH + ", "
                + MANBA + ", "
                + IZOH + ", "
                + USERID + ", "
                + DATETIME +
                ") VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            for(HisobKitob hisobKitob: hisobKitobObservableList) {
                prSt.setInt(1, hisobKitob.getQaydId());
                prSt.setInt(2, hisobKitob.getHujjatId());
                prSt.setInt(3, hisobKitob.getAmal());
                prSt.setInt(4, hisobKitob.getHisob1());
                prSt.setInt(5, hisobKitob.getHisob2());
                prSt.setInt(6, hisobKitob.getValuta());
                prSt.setInt(7, hisobKitob.getTovar());
                prSt.setDouble(8, hisobKitob.getKurs());
                prSt.setString(9, hisobKitob.getBarCode());
                prSt.setDouble(10, hisobKitob.getDona());
                prSt.setDouble(11, hisobKitob.getNarh());
                prSt.setDouble(12, hisobKitob.getManba());
                prSt.setString(13, hisobKitob.getIzoh());
                prSt.setDouble(14, hisobKitob.getUserId());
                prSt.setString(15, sdf.format(hisobKitob.getDateTime()));
                prSt.addBatch();
            }
            prSt.executeBatch();
            prSt.close();
        } catch (SQLException e) {
           Alerts.losted();
        }
    }

    public void addBatchWithId(Connection connection, ObservableList<HisobKitob> hisobKitobObservableList) {
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + ID_FIELD + ", "
                + QAYDID + ", "
                + HUJJAT + ", "
                + AMAL + ", "
                + HISOB1 + ", "
                + HISOB2 + ", "
                + VALUTA + ", "
                + TOVAR + ", "
                + KURS + ", "
                + BARCODE + ", "
                + DONA + ", "
                + NARH + ", "
                + MANBA + ", "
                + IZOH + ", "
                + USERID + ", "
                + DATETIME +
                ") VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert);
            for(HisobKitob hisobKitob: hisobKitobObservableList) {
                prSt.setInt(1, hisobKitob.getId());
                prSt.setInt(2, hisobKitob.getQaydId());
                prSt.setInt(3, hisobKitob.getHujjatId());
                prSt.setInt(4, hisobKitob.getAmal());
                prSt.setInt(5, hisobKitob.getHisob1());
                prSt.setInt(6, hisobKitob.getHisob2());
                prSt.setInt(7, hisobKitob.getValuta());
                prSt.setInt(8, hisobKitob.getTovar());
                prSt.setDouble(9, hisobKitob.getKurs());
                prSt.setString(10, hisobKitob.getBarCode());
                prSt.setDouble(11, hisobKitob.getDona());
                prSt.setDouble(12, hisobKitob.getNarh());
                prSt.setDouble(13, hisobKitob.getManba());
                prSt.setString(14, hisobKitob.getIzoh());
                prSt.setDouble(15, hisobKitob.getUserId());
                prSt.setString(16, sdf.format(hisobKitob.getDateTime()));
                prSt.addBatch();
            }
            prSt.executeBatch();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
    }

    public void copyDataBatch(Connection connection, ObservableList<HisobKitob> hisobKitobObservableList) {
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + ID_FIELD + ", "
                + QAYDID + ", "
                + HUJJAT + ", "
                + AMAL + ", "
                + HISOB1 + ", "
                + HISOB2 + ", "
                + VALUTA + ", "
                + TOVAR + ", "
                + KURS + ", "
                + BARCODE + ", "
                + DONA + ", "
                + NARH + ", "
                + MANBA + ", "
                + IZOH + ", "
                + USERID + ", "
                + DATETIME +
                ") VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert);
            for(HisobKitob hisobKitob: hisobKitobObservableList) {
                prSt.setInt(1, hisobKitob.getId());
                prSt.setInt(2, hisobKitob.getQaydId());
                prSt.setInt(3, hisobKitob.getHujjatId());
                prSt.setInt(4, hisobKitob.getAmal());
                prSt.setInt(5, hisobKitob.getHisob1());
                prSt.setInt(6, hisobKitob.getHisob2());
                prSt.setInt(7, hisobKitob.getValuta());
                prSt.setInt(8, hisobKitob.getTovar());
                prSt.setDouble(9, hisobKitob.getKurs());
                prSt.setString(10, hisobKitob.getBarCode());
                prSt.setDouble(11, hisobKitob.getDona());
                prSt.setDouble(12, hisobKitob.getNarh());
                prSt.setDouble(13, hisobKitob.getManba());
                prSt.setString(14, hisobKitob.getIzoh());
                prSt.setDouble(15, hisobKitob.getUserId());
                prSt.setString(16, sdf.format(hisobKitob.getDateTime()));
                prSt.addBatch();
            }
            prSt.executeBatch();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
    }

    public HisobKitob cloneHisobKitob(HisobKitob hk) {
        HisobKitob clonedHisobKitob = new HisobKitob(
                hk.getId(),
                hk.getQaydId(),
                hk.getHujjatId(),
                hk.getAmal(),
                hk.getHisob1(),
                hk.getHisob2(),
                hk.getValuta(),
                hk.getTovar(),
                hk.getKurs(),
                hk.getBarCode(),
                hk.getDona(),
                hk.getNarh(),
                hk.getManba(),
                hk.getIzoh(),
                hk.getUserId(),
                hk.getDateTime()
        );
        return clonedHisobKitob;
    }

    public Integer yordamchiHisob(Connection connection, HisobKitob hisobKitob, String tableName) {
        Integer yordamchiHisob = 0;
        ObservableList<Standart3> standart3ObservableList = FXCollections.observableArrayList();
        Standart3Models standart3Models = new Standart3Models();
        standart3Models.setTABLENAME(tableName);
        standart3ObservableList = standart3Models.getAnyData(connection, "id3 = " + hisobKitob.getHisob1(),"");
        if (standart3ObservableList.size()>0) {
            yordamchiHisob = standart3ObservableList.get(0).getId2();
        }
        return yordamchiHisob;
    }

    public Integer yordamchiHisob(Connection connection, Integer hisobID, String tableName) {
        Integer yordamchiHisob = 0;
        ObservableList<Standart3> standart3ObservableList = FXCollections.observableArrayList();
        Standart3Models standart3Models = new Standart3Models();
        standart3Models.setTABLENAME(tableName);
        standart3ObservableList = standart3Models.getAnyData(connection, "id3 = " + hisobID,"");
        if (standart3ObservableList.size()>0) {
            yordamchiHisob = standart3ObservableList.get(0).getId2();
        }
        return yordamchiHisob;
    }

    public Integer yordamchiHisob(Connection connection, Integer hisobID, String tableName, String boshJadval) {
        Integer yordamchiHisob = 0;
        ObservableList<Standart3> standart3ObservableList = FXCollections.observableArrayList();
        Standart3Models standart3Models = new Standart3Models();
        standart3Models.setTABLENAME(tableName);
        standart3ObservableList = standart3Models.getAnyData(connection, "id3 = " + hisobID,"");
        if (standart3ObservableList.size()>0) {
            yordamchiHisob = standart3ObservableList.get(0).getId2();
        }
        if (yordamchiHisob == 0) {
            Standart2Models standart2Models = new Standart2Models();
            standart2Models.setTABLENAME(boshJadval);
            ObservableList<Standart2> s2List = standart2Models.get_data(connection);
            if (s2List.size()>0) {
                yordamchiHisob = s2List.get(0).getId2();
            }
        }
        return yordamchiHisob;
    }

    private double tovarDonasi(BarCode barCode) {
        double dona = 1.0;
        dona *= barCode.getAdad();
        int tarkibInt = barCode.getTarkib();
        while (tarkibInt>0) {
            BarCode barCode2 = GetDbData.getBarCode(tarkibInt);
            dona *= barCode2.getAdad();
            tarkibInt = barCode2.getTarkib();
        }
        return dona;
    }

    private void saveResult(ObservableList<HisobKitob> books, ResultSet rs) {
        try {
            while (rs.next()) {
                books.add(new HisobKitob(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getInt(4),
                        rs.getInt(5),
                        rs.getInt(6),
                        rs.getInt(7),
                        rs.getInt(8),
                        rs.getDouble(9),
                        rs.getString(10),
                        rs.getDouble(11),
                        rs.getDouble(12),
                        rs.getInt(13),
                        rs.getString(14),
                        rs.getInt(15),
                        sdf.parse(rs.getString(16))
                ));
            }
        } catch (SQLException | ParseException e) {
           Alerts.losted();
        }
   }
}