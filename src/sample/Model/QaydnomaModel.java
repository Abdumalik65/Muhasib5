package sample.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Data.Hisob;
import sample.Data.HisobKitob;
import sample.Data.QaydnomaData;
import sample.Tools.Alerts;
import sample.Tools.GetDbData;
import sample.Tools.QueryHelper;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class QaydnomaModel {
    private final String TABLENAME = "Qaydnoma";
    private final String ID_FIELD = "id";
    private final String AMALTURI = "amalTuri";
    private final String HUJJAT = "hujjat";
    private final String SANA = "sana";
    private final String CHIQIMID = "chiqimId";
    private final String CHIQIMNOMI = "chiqimNomi";
    private final String KIRIMID = "kirimId";
    private final String KIRIMNOMI = "kirimNomi";
    private final String IZOH = "izoh";
    private final String JAMI = "jami";
    private final String STATUS = "status";
    private final String USERID = "userId";
    private final String DATETIME = "dateTime";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    QueryHelper queryHelper;

    public ObservableList<QaydnomaData> get_data(Connection connection){
        ObservableList<QaydnomaData> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME;
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new QaydnomaData(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        sdf.parse(rs.getString(4)),
                        rs.getInt(5),
                        rs.getString(6),
                        rs.getInt(7),
                        rs.getString(8),
                        rs.getString(9),
                        rs.getDouble(10),
                        rs.getInt(11),
                        rs.getInt(12),
                        sdf.parse(rs.getString(13))
                ));
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        } catch (ParseException e) {
            Alerts.parseError();
        }
        return books;
    }

    public QaydnomaData get_data(Connection connection, Integer qaydId){
        QaydnomaData qaydnomaData = null;
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME + " WHERE " + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            while (rs.next()) {
                qaydnomaData = new QaydnomaData(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        sdf.parse(rs.getString(4)),
                        rs.getInt(5),
                        rs.getString(6),
                        rs.getInt(7),
                        rs.getString(8),
                        rs.getString(9),
                        rs.getDouble(10),
                        rs.getInt(11),
                        rs.getInt(12),
                        sdf.parse(rs.getString(13))
                );
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        } catch (ParseException e) {
            Alerts.parseError();
        }
        return qaydnomaData;
    }

    public ObservableList<Hisob> get_data(Connection connection, Integer hisobId, Integer amalId){
        ObservableList<Hisob> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT DISTINCT " + CHIQIMID + " FROM " + TABLENAME + " WHERE " + KIRIMID + " = ? and amalTuri = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setInt(1, hisobId);
            prSt.setInt(2, amalId);
            rs = prSt.executeQuery();
            while (rs.next()) {
                Hisob h = GetDbData.getHisob(rs.getInt(1));
                books.add(h);
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
        return books;
    }

    public ObservableList<QaydnomaData> getAnyData(Connection connection, String sqlWhere, String sqlOrderBy) {
        ObservableList<QaydnomaData> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        queryHelper = new QueryHelper(sqlWhere, sqlOrderBy);
        String select = "SELECT * FROM " + TABLENAME + queryHelper.getYakuniyJumla();
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new QaydnomaData(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        sdf.parse(rs.getString(4)),
                        rs.getInt(5),
                        rs.getString(6),
                        rs.getInt(7),
                        rs.getString(8),
                        rs.getString(9),
                        rs.getDouble(10),
                        rs.getInt(11),
                        rs.getInt(12),
                        sdf.parse(rs.getString(13))
                ));
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
//            Alerts.losted();
            e.printStackTrace();
        } catch (ParseException e) {
//            Alerts.parseError();
            e.printStackTrace();
        }
        return books;
    }

    public QaydnomaData getDataById(Connection connection, int qaydId) {
        QaydnomaData qaydnomaData = null;
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME + " WHERE " + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setInt(1, qaydId);
            rs = prSt.executeQuery();
            while (rs.next()) {
                qaydnomaData = new QaydnomaData(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        sdf.parse(rs.getString(4)),
                        rs.getInt(5),
                        rs.getString(6),
                        rs.getInt(7),
                        rs.getString(8),
                        rs.getString(9),
                        rs.getDouble(10),
                        rs.getInt(11),
                        rs.getInt(12),
                        sdf.parse(rs.getString(13))
                );
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            Alerts.losted();
        } catch (ParseException e) {
            e.printStackTrace();
            Alerts.parseError();
        }
        return qaydnomaData;
    }
    public ObservableList<QaydnomaData> getDataByDate(Connection connection, Date date) {
        ObservableList<QaydnomaData> qaydnomaDataList = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME + " WHERE " + SANA + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setString(1, sdf.format(date));
            rs = prSt.executeQuery();
            while (rs.next()) {
                qaydnomaDataList.add( new QaydnomaData(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        sdf.parse(rs.getString(4)),
                        rs.getInt(5),
                        rs.getString(6),
                        rs.getInt(7),
                        rs.getString(8),
                        rs.getString(9),
                        rs.getDouble(10),
                        rs.getInt(11),
                        rs.getInt(12),
                        sdf.parse(rs.getString(13))
                ));
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        } catch (ParseException e) {
            Alerts.parseError();
        }
        return qaydnomaDataList;
    }

    public Integer insert_data(Connection connection, QaydnomaData qaydnomaData) {
        Integer insertedID = -1;
        ResultSet rs = null;
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + AMALTURI + ", "
                + HUJJAT + ", "
                + SANA + ", "
                + CHIQIMID + ", "
                + CHIQIMNOMI + ", "
                + KIRIMID + ", "
                + KIRIMNOMI + ", "
                + IZOH + ", "
                + JAMI + ", "
                + STATUS + ", "
                + USERID +
                ") VALUES(?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            prSt.setInt(1, qaydnomaData.getAmalTuri());
            prSt.setInt(2, qaydnomaData.getHujjat());
            prSt.setString(3, sdf.format(qaydnomaData.getSana()));
            prSt.setInt(4, qaydnomaData.getChiqimId());
            prSt.setString(5, qaydnomaData.getChiqimNomi());
            prSt.setInt(6, qaydnomaData.getKirimId());
            prSt.setString(7, qaydnomaData.getKirimNomi());
            prSt.setString(8, qaydnomaData.getIzoh());
            prSt.setDouble(9, qaydnomaData.getJami());
            prSt.setInt(10, qaydnomaData.getStatus());
            prSt.setInt(11, qaydnomaData.getUserId());
            prSt.executeUpdate();
            rs = prSt.getGeneratedKeys();
            if(rs.next()){
                insertedID = rs.getInt(1);
                qaydnomaData.setId(insertedID);
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
        return insertedID;
    }
    public void addBatchWithId(Connection connection, ObservableList<QaydnomaData> qaydnomaDataObservableList) {
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + ID_FIELD + ", "
                + AMALTURI + ", "
                + HUJJAT + ", "
                + SANA + ", "
                + CHIQIMID + ", "
                + CHIQIMNOMI + ", "
                + KIRIMID + ", "
                + KIRIMNOMI + ", "
                + IZOH + ", "
                + JAMI + ", "
                + STATUS + ", "
                + USERID +
                ") VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert);
            for(QaydnomaData qaydnomaData: qaydnomaDataObservableList) {
                prSt.setInt(1, qaydnomaData.getId());
                prSt.setInt(2, qaydnomaData.getAmalTuri());
                prSt.setInt(3, qaydnomaData.getHujjat());
                prSt.setString(4, sdf.format(qaydnomaData.getSana()));
                prSt.setInt(5, qaydnomaData.getChiqimId());
                prSt.setString(6, qaydnomaData.getChiqimNomi());
                prSt.setInt(7, qaydnomaData.getKirimId());
                prSt.setString(8, qaydnomaData.getKirimNomi());
                prSt.setString(9, qaydnomaData.getIzoh());
                prSt.setDouble(10, qaydnomaData.getJami());
                prSt.setInt(11, qaydnomaData.getStatus());
                prSt.setInt(12, qaydnomaData.getUserId());
                prSt.addBatch();
            }
            prSt.executeBatch();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
    }
    public void copyDataBatch(Connection connection, ObservableList<QaydnomaData> qaydnomaDataObservableList) {
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + ID_FIELD + ", "
                + AMALTURI + ", "
                + HUJJAT + ", "
                + SANA + ", "
                + CHIQIMID + ", "
                + CHIQIMNOMI + ", "
                + KIRIMID + ", "
                + KIRIMNOMI + ", "
                + IZOH + ", "
                + JAMI + ", "
                + STATUS + ", "
                + USERID + ", "
                + DATETIME +
                ") VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert);
            for(QaydnomaData qaydnomaData: qaydnomaDataObservableList) {
                prSt.setInt(1, qaydnomaData.getId());
                prSt.setInt(2, qaydnomaData.getAmalTuri());
                prSt.setInt(3, qaydnomaData.getHujjat());
                prSt.setString(4, sdf.format(qaydnomaData.getSana()));
                prSt.setInt(5, qaydnomaData.getChiqimId());
                prSt.setString(6, qaydnomaData.getChiqimNomi());
                prSt.setInt(7, qaydnomaData.getKirimId());
                prSt.setString(8, qaydnomaData.getKirimNomi());
                prSt.setString(9, qaydnomaData.getIzoh());
                prSt.setDouble(10, qaydnomaData.getJami());
                prSt.setInt(11, qaydnomaData.getStatus());
                prSt.setInt(12, qaydnomaData.getUserId());
                prSt.setString(13, sdf.format(qaydnomaData.getDateTime()));
                prSt.addBatch();
            }
            prSt.executeBatch();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
    }

    public void delete_data(Connection connection, QaydnomaData qaydnomaData)  {
        String delete = "DELETE FROM " + TABLENAME + " WHERE " + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(delete);
            prSt.setInt(1, qaydnomaData.getId());
            prSt.executeUpdate();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
    }
    public void update_data(Connection connection, QaydnomaData qaydnomaData)  {
        String replace = "UPDATE " + TABLENAME + " SET "
                + AMALTURI + "= ?,"
                + HUJJAT + "= ?,"
                + SANA + "= ?,"
                + CHIQIMID + "= ?,"
                + CHIQIMNOMI + "= ?,"
                + KIRIMID + "= ?,"
                + KIRIMNOMI + "= ?,"
                + IZOH + "= ?,"
                + JAMI + "= ?,"
                + STATUS + "= ?,"
                + USERID + "= ?,"
                + DATETIME + " = ? WHERE "
                + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(replace);
            prSt.setInt(1, qaydnomaData.getAmalTuri());
            prSt.setInt(2, qaydnomaData.getHujjat());
            prSt.setString(3, sdf.format(qaydnomaData.getSana()));
            prSt.setInt(4, qaydnomaData.getChiqimId());
            prSt.setString(5, qaydnomaData.getChiqimNomi());
            prSt.setInt(6, qaydnomaData.getKirimId());
            prSt.setString(7, qaydnomaData.getKirimNomi());
            prSt.setString(8, qaydnomaData.getIzoh());
            prSt.setDouble(9, qaydnomaData.getJami());
            prSt.setInt(10, qaydnomaData.getStatus());
            prSt.setInt(11, qaydnomaData.getUserId());
            prSt.setString(12, sdf.format(qaydnomaData.getDateTime()));
            prSt.setInt(13, qaydnomaData.getId());
            prSt.executeUpdate();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
    }

    public Hisob getBalans(Connection connection, Hisob hisob) {
        ResultSet rs = null;

        String select = "SELECT SUM("+JAMI+") FROM " + TABLENAME + " WHERE " + CHIQIMID + "=?" ;
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setInt(1,hisob.getId());
            rs = prSt.executeQuery();
            while (rs.next()) {
                hisob.setChiqim(rs.getDouble(1));
            }
            select = "SELECT SUM("+JAMI+") FROM " + TABLENAME + " WHERE " + KIRIMID + "=?" ;
            prSt = connection.prepareStatement(select);
            prSt.setInt(1,hisob.getId());
            rs = prSt.executeQuery();
            while (rs.next()) {
                hisob.setKirim(rs.getDouble(1));
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
        hisob.setBalans(hisob.getKirim()-hisob.getChiqim());
        return hisob;
    }

    public Integer getCount(Connection connection, String sqlWhere) {
        Integer count = 0;
        ObservableList<QaydnomaData> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        queryHelper = new QueryHelper(sqlWhere, "");
        String select = "SELECT COUNT(*) FROM " + TABLENAME  + queryHelper.getYakuniyJumla();
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            while (rs.next()) {
                count = rs.getInt(1);
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
        return count;
    }
}
