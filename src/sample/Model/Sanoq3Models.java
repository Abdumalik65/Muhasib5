package sample.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Config.MySqlStatus;
import sample.Data.Sanoq3;
import sample.Tools.Alerts;
import sample.Tools.QueryHelper;
import sample.Data.HisobKitob;
import sample.Data.User;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Sanoq3Models {
    private String TABLENAME = "Sanoq3";
    private final String ID_FIELD = "id";
    private final String HISOB = "sanoqId";
    private final String TOVAR = "tovarId";
    private final String HISOBIYADAD = "hisobiyAdad";
    private final String SANALGANADAD = "sanalganAdad";
    private final String USERID = "userId";
    private final String DATETIME = "dateTime";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    QueryHelper queryHelper;

    public ObservableList<Sanoq3> get_data(Connection connection) {
        MySqlStatus.checkMyConnection(connection);
        ObservableList<Sanoq3> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME;
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new Sanoq3(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getDouble(4),
                        rs.getDouble(5),
                        rs.getInt(6),
                        sdf.parse(rs.getString(7))
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

    public ObservableList<Sanoq3> getAnyData(Connection connection, String sqlWhere, String sqlOrderBy){
        MySqlStatus.checkMyConnection(connection);
        ObservableList<Sanoq3> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        queryHelper = new QueryHelper(sqlWhere, sqlOrderBy);
        String select = "SELECT * FROM " + TABLENAME + queryHelper.getYakuniyJumla();
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new Sanoq3(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getDouble(4),
                        rs.getDouble(5),
                        rs.getInt(6),
                        sdf.parse(rs.getString(7))
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

    public Integer insert_data(Connection connection, Sanoq3 sanoq3) {
        MySqlStatus.checkMyConnection(connection);
        Integer insertedID = -1;
        ResultSet rs = null;
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + HISOB + ", "
                + TOVAR + ", "
                + HISOBIYADAD + ", "
                + SANALGANADAD + ", "
                + USERID +
                ") VALUES(?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            prSt.setInt(1, sanoq3.getSanoqId());
            prSt.setInt(2, sanoq3.getTovarId());
            prSt.setDouble(3, sanoq3.getHisobiyAdad());
            prSt.setDouble(4, sanoq3.getSanalganAdad());
            prSt.setInt(5, sanoq3.getUserId());
            prSt.executeUpdate();
            rs = prSt.getGeneratedKeys();
            if(rs.next()){
                insertedID = rs.getInt(1);
                sanoq3.setId(insertedID);
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return insertedID;
    }

    public void delete_data(Connection connection, Sanoq3 sanoq3) {
        MySqlStatus.checkMyConnection(connection);
        String delete = "DELETE FROM " + TABLENAME + " WHERE " + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(delete);
            prSt.setInt(1, sanoq3.getId());
            prSt.executeUpdate();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
    }
    public void update_data(Connection connection, Sanoq3 sanoq3){
        MySqlStatus.checkMyConnection(connection);
        String replace = "UPDATE " + TABLENAME + " SET "
                + HISOB + " = ?, "
                + TOVAR + " = ?, "
                + HISOBIYADAD + " = ?, "
                + SANALGANADAD + " = ? WHERE "
                + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(replace);
            prSt.setInt(1, sanoq3.getSanoqId());
            prSt.setInt(2, sanoq3.getTovarId());
            prSt.setDouble(3, sanoq3.getHisobiyAdad());
            prSt.setDouble(4, sanoq3.getSanalganAdad());
            prSt.setInt(5, sanoq3.getId());
            prSt.executeUpdate();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
    }

    public ObservableList<Sanoq3> getTovarAdad(Connection connection, Integer sanoqId, Integer hisobId, User user) {
        MySqlStatus.checkMyConnection(connection);
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        ObservableList<Sanoq3> sanoq3List = FXCollections.observableArrayList();
            ObservableList<HisobKitob> kirimList = hisobKitobModels.getAnyData(connection, "hisob2 = " + hisobId + " AND tovar > 0", "tovar asc");
            int idNum = 0;
            int tovarId = 0;
            Sanoq3 sanoq3 = new Sanoq3();
            if (kirimList.size()>0) {
                for (HisobKitob h: kirimList) {
                    if (tovarId != h.getTovar()) {
                        sanoq3 = new Sanoq3(null, sanoqId, h.getTovar(), .0, .0, user.getId(), null);
                        sanoq3List.add(sanoq3);
                    }
                }
            }

            ObservableList<HisobKitob> chiqimList = hisobKitobModels.getAnyData(connection, "hisob1 = " + hisobId + " AND tovar > 0", "tovar asc");
            if (chiqimList.size()>0) {
                for (HisobKitob h: chiqimList) {
                    if (sanoq3.getTovarId() != h.getTovar()) {
                        Boolean topdim = false;
                        for (Sanoq3 ta: sanoq3List) {
                            if (ta.getTovarId().equals(h.getTovar())) {
                                topdim = true;
                                sanoq3 = ta;
                                break;
                            }
                        }
                        if (!topdim) {
                            sanoq3 = new Sanoq3(null, sanoqId, h.getTovar(), .0, .0, user.getId(), null);
                            sanoq3List.add(sanoq3);
                        }
                    }
                }
            }

            if (sanoq3List.size()>0) {
                addBatch(connection, sanoq3List);
                sanoq3List = getAnyData(connection, "sanoqId = " + sanoqId, "");
            }
        return sanoq3List;
    }

    public void addBatch(Connection connection, ObservableList<Sanoq3> sanoq3List){
        MySqlStatus.checkMyConnection(connection);
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + HISOB + ", "
                + TOVAR + ", "
                + HISOBIYADAD + ", "
                + SANALGANADAD + ", "
                + USERID +
                ") VALUES(?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            for(Sanoq3 sanoq3 : sanoq3List) {
                prSt = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
                prSt.setInt(1, sanoq3.getSanoqId());
                prSt.setInt(2, sanoq3.getTovarId());
                prSt.setDouble(3, sanoq3.getHisobiyAdad());
                prSt.setDouble(4, sanoq3.getSanalganAdad());
                prSt.setInt(5, sanoq3.getUserId());
                prSt.addBatch();
            }
            prSt.executeBatch();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
    }
    public void copyDataBatch(Connection connection, ObservableList<Sanoq3> sanoq3List){
        MySqlStatus.checkMyConnection(connection);
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + ID_FIELD + ", "
                + HISOB + ", "
                + TOVAR + ", "
                + HISOBIYADAD + ", "
                + SANALGANADAD + ", "
                + USERID + ", "
                + DATETIME +
                ") VALUES(?,?,?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert);
            for(Sanoq3 sanoq3 : sanoq3List) {
                prSt.setInt(1, sanoq3.getId());
                prSt.setInt(2, sanoq3.getSanoqId());
                prSt.setInt(3, sanoq3.getTovarId());
                prSt.setDouble(4, sanoq3.getHisobiyAdad());
                prSt.setDouble(5, sanoq3.getSanalganAdad());
                prSt.setInt(6, sanoq3.getUserId());
                prSt.setString(7, sdf.format(sanoq3.getDateTime()));
                prSt.addBatch();
            }
            prSt.executeBatch();
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
}
