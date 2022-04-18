package sample.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Config.MySqlStatus;
import sample.Data.Sanoq;
import sample.Tools.Alerts;
import sample.Tools.QueryHelper;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SanoqModels {
    private String TABLENAME = "Sanoq";
    private final String ID_FIELD = "id";
    private final String QAYDID = "qaydId";
    private final String BARCODE = "barcode";
    private final String ADAD = "adad";
    private final String NARH = "narh";
    private final String USERID = "userId";
    private final String DATETIME = "dateTime";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    QueryHelper queryHelper;

    public ObservableList<Sanoq> get_data(Connection connection) {
        MySqlStatus.checkMyConnection(connection);
        ObservableList<Sanoq> books = FXCollections.observableArrayList();
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

    public ObservableList<Sanoq> getAnyData(Connection connection, String sqlWhere, String sqlOrderBy){
        MySqlStatus.checkMyConnection(connection);
        ObservableList<Sanoq> books = FXCollections.observableArrayList();
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
            Alerts.losted();
        }
        return books;
    }

    public Integer insert_data(Connection connection, Sanoq sanoq) {
        MySqlStatus.checkMyConnection(connection);
        Integer insertedID = -1;
        ResultSet rs = null;
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + QAYDID + ", "
                + BARCODE + ", "
                + ADAD + ", "
                + NARH + ", "
                + USERID +
                ") VALUES(?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            prSt.setInt(1, sanoq.getQaydId());
            prSt.setString(2, sanoq.getBarCode());
            prSt.setDouble(3, sanoq.getAdad());
            prSt.setDouble(4, sanoq.getNarh());
            prSt.setInt(5, sanoq.getUserId());
            prSt.executeUpdate();
            rs = prSt.getGeneratedKeys();
            if(rs.next()){
                insertedID = rs.getInt(1);
                sanoq.setId(insertedID);
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
        return insertedID;
    }
    public void copyDataBatch(Connection connection, ObservableList<Sanoq> sanoqList) {
        MySqlStatus.checkMyConnection(connection);
        Integer insertedID = -1;
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + ID_FIELD + ", "
                + QAYDID + ", "
                + BARCODE + ", "
                + ADAD + ", "
                + NARH + ", "
                + USERID + ", "
                + DATETIME +
                ") VALUES(?,?,?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert);
            for (Sanoq sanoq: sanoqList) {
                prSt.setInt(1, sanoq.getId());
                prSt.setInt(2, sanoq.getQaydId());
                prSt.setString(3, sanoq.getBarCode());
                prSt.setDouble(4, sanoq.getAdad());
                prSt.setDouble(5, sanoq.getNarh());
                prSt.setInt(6, sanoq.getUserId());
                prSt.setString(3, sdf.format(sanoq.getDateTime()));
                prSt.addBatch();
            }
            prSt.executeBatch();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
    }

    public void delete_data(Connection connection, Sanoq sanoq) {
        MySqlStatus.checkMyConnection(connection);
        String delete = "DELETE FROM " + TABLENAME + " WHERE " + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(delete);
            prSt.setInt(1, sanoq.getId());
            prSt.executeUpdate();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
    }
    public void update_data(Connection connection, Sanoq sanoq){
        MySqlStatus.checkMyConnection(connection);
        String replace = "UPDATE " + TABLENAME + " SET "
                + QAYDID + " = ?, "
                + BARCODE + " = ?, "
                + ADAD + " = ?, "
                + NARH + " = ? WHERE "
                + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(replace);
            prSt.setString(1, sdf.format(sanoq.getQaydId()));
            prSt.setString(2, sanoq.getBarCode());
            prSt.setDouble(3, sanoq.getAdad());
            prSt.setDouble(4, sanoq.getNarh());
            prSt.setInt(5, sanoq.getId());
            prSt.executeUpdate();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
    }

    public String getTABLENAME() {
        return TABLENAME;
    }

    public void setTABLENAME(String TABLENAME) {
        this.TABLENAME = TABLENAME;
    }

    private void saveResult(ObservableList<Sanoq> books, ResultSet rs) {
        try {
            while (rs.next()) {
                books.add(new Sanoq(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getDouble(4),
                        rs.getDouble(5),
                        rs.getInt(6),
                        sdf.parse(rs.getString(7))
                ));
            }
        } catch (SQLException | ParseException e) {
            Alerts.losted();
        }
    }}
