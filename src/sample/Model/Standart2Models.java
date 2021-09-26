package sample.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Data.Standart2;
import sample.Tools.Alerts;
import sample.Tools.QueryHelper;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Standart2Models {
    private String TABLENAME = "Birlik";
    private final String ID_FIELD = "id";
    private final String ID_FIELD2 = "id2";
    private final String TEXT = "text";
    private final String USERID = "userId";
    private final String DATETIME = "dateTime";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    QueryHelper queryHelper;

    public ObservableList<Standart2> get_data(Connection connection) {
        ObservableList<Standart2> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME;
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new Standart2(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getInt(4),
                        sdf.parse(rs.getString(5))
                ));
            }
            rs.close();
            prSt.close();
        } catch (SQLException | ParseException e) {
            Alerts.losted();;
        }
        return books;
    }

    public ObservableList<Standart2> getAnyData(Connection connection, String sqlWhere, String sqlOrderBy) {
        ObservableList<Standart2> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        queryHelper = new QueryHelper(sqlWhere, sqlOrderBy);
        String select = "SELECT * FROM " + TABLENAME + queryHelper.getYakuniyJumla();
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new Standart2(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getInt(4),
                        sdf.parse(rs.getString(5))
                ));
            }
            rs.close();
            prSt.close();
        } catch (SQLException | ParseException e) {
            Alerts.losted();;
        }
        return books;
    }

    public Integer insert_data(Connection connection, Standart2 standart2) {
        Integer insertedID = -1;
        ResultSet rs = null;
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + ID_FIELD2 + ", "
                + TEXT + ", "
                + USERID +
                ") VALUES(?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            prSt.setInt(1, standart2.getId2());
            prSt.setString(2, standart2.getText());
            prSt.setInt(3, standart2.getUserId());
            prSt.executeUpdate();
            rs = prSt.getGeneratedKeys();
            if(rs.next()){
                insertedID = rs.getInt(1);
                standart2.setId(insertedID);
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();;
        }
        return insertedID;
    }
    public void copyDataBatch(Connection connection, ObservableList<Standart2> standart2List) {
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + ID_FIELD + ", "
                + ID_FIELD2 + ", "
                + TEXT + ", "
                + USERID + ", "
                + DATETIME +
                ") VALUES(?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert);
            for (Standart2 standart2: standart2List) {
                prSt.setInt(1, standart2.getId());
                prSt.setInt(2, standart2.getId2());
                prSt.setString(3, standart2.getText());
                prSt.setInt(4, standart2.getUserId());
                prSt.setString(5, sdf.format(standart2.getDateTime()));
                prSt.addBatch();
            }
            prSt.executeBatch();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();;
        }
    }
    public void delete_data(Connection connection, Standart2 standart2) {
        String delete = "DELETE FROM " + TABLENAME + " WHERE " + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(delete);
            prSt.setInt(1, standart2.getId());
            prSt.executeUpdate();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();;
        }
    }
    public void update_data(Connection connection, Standart2 standart2) {
        String replace = "UPDATE " + TABLENAME + " SET "
                + ID_FIELD2 + " = ?, "
                + TEXT + " = ? WHERE "
                + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(replace);
            prSt.setInt(1, standart2.getId2());
            prSt.setString(2, standart2.getText());
            prSt.setInt(3, standart2.getId());
            prSt.executeUpdate();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();;
        }
    }

    public String getTABLENAME() {
        return TABLENAME;
    }

    public void setTABLENAME(String TABLENAME) {
        this.TABLENAME = TABLENAME;
    }
}
