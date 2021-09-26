package sample.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Tools.Alerts;
import sample.Tools.QueryHelper;
import sample.Data.SanoqQaydi;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SanoqQaydiModels {
    private Integer id;
    private Date sana;
    private Integer hisob1;
    private Integer hisob2;
    private Double balance;
    private  Integer userId;
    private Date dateTime;

    private String TABLENAME = "SanoqQaydi";
    private final String ID_FIELD = "id";
    private final String SANA = "sana";
    private final String HISOB1 = "hisob1";
    private final String HISOB2 = "hisob2";
    private final String BALANCE = "balance";
    private final String USERID = "userId";
    private final String DATETIME = "dateTime";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    QueryHelper queryHelper;

    public ObservableList<SanoqQaydi> get_data(Connection connection) {
        ObservableList<SanoqQaydi> books = FXCollections.observableArrayList();
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

    public ObservableList<SanoqQaydi> getAnyData(Connection connection, String sqlWhere, String sqlOrderBy){
        ObservableList<SanoqQaydi> books = FXCollections.observableArrayList();
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

    public Integer insert_data(Connection connection, SanoqQaydi sanoqQaydi) {
        Integer insertedID = -1;
        ResultSet rs = null;
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + SANA + ", "
                + HISOB1 + ", "
                + HISOB2 + ", "
                + BALANCE + ", "
                + USERID +
                ") VALUES(?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            prSt.setString(1, sdf.format(sanoqQaydi.getSana()));
            prSt.setInt(2, sanoqQaydi.getHisob1());
            prSt.setInt(3, sanoqQaydi.getHisob2());
            prSt.setDouble(4, sanoqQaydi.getBalance());
            prSt.setInt(5, sanoqQaydi.getUserId());
            prSt.executeUpdate();
            rs = prSt.getGeneratedKeys();
            if(rs.next()){
                insertedID = rs.getInt(1);
                sanoqQaydi.setId(insertedID);
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();;
        }
        return insertedID;
    }
    public void copyDataBatch(Connection connection, ObservableList<SanoqQaydi> sanoqQaydiList) {
        Integer insertedID = -1;
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + ID_FIELD + ", "
                + SANA + ", "
                + HISOB1 + ", "
                + HISOB2 + ", "
                + BALANCE + ", "
                + USERID + ", "
                + DATETIME +
                ") VALUES(?,?,?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert);
            for (SanoqQaydi sanoqQaydi: sanoqQaydiList) {
                prSt.setInt(1, sanoqQaydi.getId());
                prSt.setString(2, sdf.format(sanoqQaydi.getSana()));
                prSt.setInt(3, sanoqQaydi.getHisob1());
                prSt.setInt(4, sanoqQaydi.getHisob2());
                prSt.setDouble(5, sanoqQaydi.getBalance());
                prSt.setInt(6, sanoqQaydi.getUserId());
                prSt.setString(7, sdf.format(sanoqQaydi.getDateTime()));
                prSt.addBatch();
            }
            prSt.executeBatch();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();;
        }
    }

    public void delete_data(Connection connection, SanoqQaydi sanoqQaydi) {
        String delete = "DELETE FROM " + TABLENAME + " WHERE " + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(delete);
            prSt.setInt(1, sanoqQaydi.getId());
            prSt.executeUpdate();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();;
        }
    }
    public void update_data(Connection connection, SanoqQaydi sanoqQaydi){
        String replace = "UPDATE " + TABLENAME + " SET "
                + SANA + " = ?, "
                + HISOB1 + " = ?, "
                + HISOB2 + " = ?, "
                + BALANCE + " = ? WHERE "
                + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(replace);
            prSt.setString(1, sdf.format(sanoqQaydi.getSana()));
            prSt.setInt(2, sanoqQaydi.getHisob1());
            prSt.setInt(3, sanoqQaydi.getHisob2());
            prSt.setDouble(4, sanoqQaydi.getBalance());
            prSt.setInt(5, sanoqQaydi.getId());
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

    private void saveResult(ObservableList<SanoqQaydi> books, ResultSet rs) {
        try {
            while (rs.next()) {
                books.add(new SanoqQaydi(
                        rs.getInt(1),
                        sdf.parse(rs.getString(2)),
                        rs.getInt(3),
                        rs.getInt(4),
                        rs.getDouble(5),
                        rs.getInt(6),
                        sdf.parse(rs.getString(7))
                ));
            }
        } catch (SQLException | ParseException e) {
            Alerts.losted();;
        }
    }}
