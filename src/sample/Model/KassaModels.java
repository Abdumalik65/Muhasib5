package sample.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Data.Kassa;
import sample.Tools.Alerts;
import sample.Tools.QueryHelper;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class KassaModels {
    private String TABLENAME = "Kassa";
    private final String ID_FIELD = "id";
    private final String KASSANOMI = "kassaNomi";
    private final String PULHISOBI = "pulHisobi";
    private final String XARIDORHISOBI = "xaridorHisobi";
    private final String TOVARHISOBI = "tovarHisobi";
    private final String VALUTA = "valuta";
    private final String SAVDOTURI = "savdoTuri";
    private final String SERIALNUMBER = "serialNumber";
    private final String ONLINE = "online";
    private final String LOCK = "isLocked";
    private final String USERID = "userId";
    private final String DATETIME = "dateTime";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    QueryHelper queryHelper;

    public ObservableList<Kassa> get_data(Connection connection) {
        ObservableList<Kassa> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME;
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new Kassa(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getInt(3),
                        rs.getInt(4),
                        rs.getInt(5),
                        rs.getInt(6),
                        rs.getInt(7),
                        rs.getString(8),
                        rs.getInt(9),
                        rs.getInt(10),
                        rs.getInt(11),
                        sdf.parse(rs.getString(12))
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

    public ObservableList<Kassa> getAnyData(Connection connection, String sqlWhere, String sqlOrderBy) {
        ObservableList<Kassa> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        queryHelper = new QueryHelper(sqlWhere, sqlOrderBy);
        String select = "SELECT * FROM " + TABLENAME + queryHelper.getYakuniyJumla();
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new Kassa(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getInt(3),
                        rs.getInt(4),
                        rs.getInt(5),
                        rs.getInt(6),
                        rs.getInt(7),
                        rs.getString(8),
                        rs.getInt(9),
                        rs.getInt(10),
                        rs.getInt(11),
                        sdf.parse(rs.getString(12))
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

    public Integer insert_data(Connection connection, Kassa kassa) {
        Integer insertedID = -1;
        ResultSet rs = null;
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + KASSANOMI + ", "
                + PULHISOBI + ", "
                + XARIDORHISOBI + ", "
                + TOVARHISOBI + ", "
                + VALUTA + ", "
                + SAVDOTURI + ", "
                + SERIALNUMBER + ", "
                + ONLINE + ", "
                + LOCK + ", "
                + USERID +
                ") VALUES(?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            prSt.setString(1, kassa.getKassaNomi());
            prSt.setDouble(2, kassa.getPulHisobi());
            prSt.setDouble(3, kassa.getXaridorHisobi());
            prSt.setInt(4, kassa.getTovarHisobi());
            prSt.setInt(5, kassa.getValuta());
            prSt.setInt(6, kassa.getSavdoTuri());
            prSt.setString(7, kassa.getSerialNumber());
            prSt.setInt(8, kassa.getOnline());
            prSt.setInt(9, kassa.getIsLocked());
            prSt.setInt(10, kassa.getUserId());
            prSt.executeUpdate();
            rs = prSt.getGeneratedKeys();
            if(rs.next()){
                insertedID = rs.getInt(1);
            }
            kassa.setId(insertedID);
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
        return insertedID;
    }
    public void copyDataBatch(Connection connection, ObservableList<Kassa> kassaList) {
        Integer insertedID = -1;
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + ID_FIELD + ", "
                + KASSANOMI + ", "
                + PULHISOBI + ", "
                + XARIDORHISOBI + ", "
                + TOVARHISOBI + ", "
                + VALUTA + ", "
                + SAVDOTURI + ", "
                + SERIALNUMBER + ", "
                + ONLINE + ", "
                + LOCK + ", "
                + USERID + ", "
                + DATETIME +
                ") VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert);
            for (Kassa kassa: kassaList) {
                prSt.setInt(1, kassa.getId());
                prSt.setString(2, kassa.getKassaNomi());
                prSt.setDouble(3, kassa.getPulHisobi());
                prSt.setDouble(4, kassa.getXaridorHisobi());
                prSt.setInt(5, kassa.getTovarHisobi());
                prSt.setInt(6, kassa.getValuta());
                prSt.setInt(7, kassa.getSavdoTuri());
                prSt.setString(8, kassa.getSerialNumber());
                prSt.setInt(9, kassa.getOnline());
                prSt.setInt(10, kassa.getIsLocked());
                prSt.setInt(11, kassa.getUserId());
                prSt.setString(12, sdf.format(kassa.getDateTime()));
                prSt.addBatch();
            }
            prSt.executeBatch();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
    }

    public void delete_data(Connection connection, Kassa kassa){
        String delete = "DELETE FROM " + TABLENAME + " WHERE " + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(delete);
            prSt.setInt(1, kassa.getId());
            prSt.executeUpdate();
            prSt.close();
        } catch (SQLException e) {
           Alerts.losted();
        }
    }
    public void update_data(Connection connection, Kassa kassa) {
        String replace = "UPDATE " + TABLENAME + " SET "
                + KASSANOMI + " = ?,"
                + PULHISOBI + " = ?,"
                + XARIDORHISOBI + " = ?,"
                + TOVARHISOBI + " = ?,"
                + VALUTA + " = ?,"
                + SAVDOTURI + " = ?,"
                + SERIALNUMBER + " = ?,"
                + ONLINE + " = ?,"
                + LOCK + " = ?,"
                + USERID + " = ? WHERE "
                + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(replace);
            prSt.setString(1, kassa.getKassaNomi());
            prSt.setInt(2, kassa.getPulHisobi());
            prSt.setInt(3, kassa.getXaridorHisobi());
            prSt.setInt(4, kassa.getTovarHisobi());
            prSt.setInt(5, kassa.getValuta());
            prSt.setInt(6, kassa.getSavdoTuri());
            prSt.setString(7, kassa.getSerialNumber());
            prSt.setInt(8, kassa.getOnline());
            prSt.setInt(9, kassa.getIsLocked());
            prSt.setInt(10, kassa.getUserId());
            prSt.setInt(11, kassa.getId());
            prSt.executeUpdate();
            prSt.close();
        } catch (SQLException e) {
           Alerts.losted();
        }
    }
}
