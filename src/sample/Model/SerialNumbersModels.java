package sample.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Data.BarCode;
import sample.Data.Standart3;
import sample.Data.Standart6;
import sample.Data.SerialNumber;
import sample.Tools.Alerts;
import sample.Tools.QueryHelper;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SerialNumbersModels {
    private final String TABLENAME = "SerialNumbers";
    private final String ID_FIELD = "id";
    private final String SANA = "sana";
    private final String HISOB = "hisob";
    private final String INVOICE = "invoice";
    private final String TOVAR = "tovar";
    private final String SERIAL_NUMBER = "serialNumber";
    private final String USERID = "userId";
    private final String DATETIME = "dateTime";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    QueryHelper queryHelper;

    public ObservableList<SerialNumber> get(Connection connection) {
        ObservableList<SerialNumber> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME;
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new SerialNumber(
                        rs.getInt(1),
                        sdf.parse(rs.getString(2)),
                        rs.getInt(3),
                        rs.getString(4),
                        rs.getInt(5),
                        rs.getString(6),
                        rs.getInt(7),
                        sdf.parse(rs.getString(8))
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

    public Boolean getSerialNumber(Connection connection, String serialNumberString) {
        Boolean isExist = false;
        SerialNumber serialNumber;
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME + " WHERE " + SERIAL_NUMBER + "=?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setString(1, serialNumberString);
            rs = prSt.executeQuery();
            while (rs.next()) {
                isExist = true;
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
        return isExist;
    }

    public ObservableList<SerialNumber> getAnyData(Connection connection, String sqlWhere, String sqlOrderBy) {
        ObservableList<SerialNumber> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        queryHelper = new QueryHelper(sqlWhere, sqlOrderBy);
        String select = "SELECT * FROM " + TABLENAME + queryHelper.getYakuniyJumla();
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new SerialNumber(
                        rs.getInt(1),
                        sdf.parse(rs.getString(2)),
                        rs.getInt(3),
                        rs.getString(4),
                        rs.getInt(5),
                        rs.getString(6),
                        rs.getInt(7),
                        sdf.parse(rs.getString(8))
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

    public Integer insert(Connection connection, SerialNumber serialNumber) {
        Integer insertedID = -1;
        ResultSet rs = null;
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + SANA + ", "
                + HISOB + ", "
                + INVOICE + ", "
                + TOVAR + ", "
                + SERIAL_NUMBER + ", "
                + USERID +
                ") VALUES(?,?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            prSt.setString(1, sdf.format(serialNumber.getSana()));
            prSt.setInt(2, serialNumber.getHisob());
            prSt.setString(3, serialNumber.getInvoice());
            prSt.setInt(4, serialNumber.getTovar());
            prSt.setString(5, serialNumber.getSerialNumber());
            prSt.setInt(6, serialNumber.getUserId());
            prSt.executeUpdate();
            rs = prSt.getGeneratedKeys();
            if(rs.next()){
                insertedID = rs.getInt(1);
                serialNumber.setId(insertedID);
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();;
        }
        return insertedID;
    }

    public Integer addBath(Connection connection, ObservableList<SerialNumber> serialNumbers) {
        Integer insertedID = -1;
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + SANA + ", "
                + HISOB + ", "
                + INVOICE + ", "
                + TOVAR + ", "
                + SERIAL_NUMBER + ", "
                + USERID +
                ") VALUES(?,?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert);
            for (SerialNumber sn: serialNumbers) {
                prSt.setString(1, sdf.format(sn.getSana()));
                prSt.setInt(2, sn.getHisob());
                prSt.setString(3, sn.getInvoice());
                prSt.setInt(4, sn.getTovar());
                prSt.setString(5, sn.getSerialNumber());
                prSt.setInt(6, sn.getUserId());
                prSt.addBatch();
            }
            prSt.executeBatch();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();;
        }
        return insertedID;
    }

    public void copyDataBatch(Connection connection, ObservableList<SerialNumber> tovarNarhiObservableList) {
        Integer insertedID = -1;
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + SANA + ", "
                + HISOB + ", "
                + INVOICE + ", "
                + TOVAR + ", "
                + SERIAL_NUMBER + ", "
                + USERID +
                ") VALUES(?,?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert);
            for (SerialNumber sn: tovarNarhiObservableList) {
                prSt.setString(1, sdf.format(sn.getSana()));
                prSt.setInt(2, sn.getHisob());
                prSt.setString(3, sn.getInvoice());
                prSt.setInt(4, sn.getTovar());
                prSt.setString(5, sn.getSerialNumber());
                prSt.setInt(6, sn.getUserId());
                prSt.setString(7, sdf.format(sn.getDateTime()));
                prSt.addBatch();
            }
            prSt.executeBatch();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();;
        }
    }

    public void delete(Connection connection, SerialNumber serialNumber) {
        String delete = "DELETE FROM " + TABLENAME + " WHERE " + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(delete);
            prSt.setInt(1, serialNumber.getId());
            prSt.executeUpdate();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();;
        }
    }
    public void update(Connection connection, SerialNumber serialNumber) {
        String replace = "UPDATE " + TABLENAME + " SET "
                + SANA + " = ?, "
                + HISOB + " = ?, "
                + INVOICE + " = ?, "
                + TOVAR + " = ?, "
                + SERIAL_NUMBER + "  = ? WHERE "
                + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(replace);
            prSt.setString(1, sdf.format(serialNumber.getSana()));
            prSt.setInt(2, serialNumber.getHisob());
            prSt.setString(3, serialNumber.getInvoice());
            prSt.setInt(4, serialNumber.getTovar());
            prSt.setString(5, serialNumber.getSerialNumber());
            prSt.setInt(6, serialNumber.getId());
            prSt.executeUpdate();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();;
        }
    }
}