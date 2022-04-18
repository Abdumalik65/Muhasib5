package sample.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Config.MySqlStatus;
import sample.Data.Standart3;
import sample.Data.Standart6;
import sample.Tools.Alerts;
import sample.Tools.QueryHelper;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Standart6Models {
    private String TABLENAME = "Standart6";
    private final String ID_FIELD = "id";
    private final String TEXT = "text";
    private final String NARH = "narh";
    private final String ULGURJI = "ulgurji";
    private final String CHAKANA = "chakana";
    private final String NDS = "nds";
    private final String BOJ = "boj";
    private final String USERID = "userId";
    private final String DATETIME = "dateTime";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    QueryHelper queryHelper;

    public Standart6Models() {}

    public Standart6Models(String tableName) {
        this.TABLENAME = tableName;
    }

    public ObservableList<Standart6> get_data(Connection connection) {
        MySqlStatus.checkMyConnection(connection);
        ObservableList<Standart6> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME;
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new Standart6(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getDouble(3),
                        rs.getDouble(4),
                        rs.getDouble(5),
                        rs.getDouble(6),
                        rs.getDouble(7),
                        rs.getInt(8),
                        sdf.parse(rs.getString(9))
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

    public Standart6 getWithId(Connection connection, int id) {
        MySqlStatus.checkMyConnection(connection);
        Standart6 standart6 = null;
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME + " WHERE " + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setInt(1, id);
            rs = prSt.executeQuery();
            while (rs.next()) {
                standart6 = new Standart6(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getDouble(3),
                        rs.getDouble(4),
                        rs.getDouble(5),
                        rs.getDouble(6),
                        rs.getDouble(7),
                        rs.getInt(8),
                        sdf.parse(rs.getString(9))
                );
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        } catch (ParseException e) {
            Alerts.parseError();
        }
        return standart6;
    }

    public ObservableList<Standart6> getAnyData(Connection connection, String sqlWhere, String sqlOrderBy) {
        MySqlStatus.checkMyConnection(connection);
        ObservableList<Standart6> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        queryHelper = new QueryHelper(sqlWhere, sqlOrderBy);
        String select = "SELECT * FROM " + TABLENAME + queryHelper.getYakuniyJumla();
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new Standart6(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getDouble(3),
                        rs.getDouble(4),
                        rs.getDouble(5),
                        rs.getDouble(6),
                        rs.getDouble(7),
                        rs.getInt(8),
                        sdf.parse(rs.getString(9))
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

    public Integer insert_data(Connection connection, Standart6 standart6) {
        MySqlStatus.checkMyConnection(connection);
        Integer insertedID = -1;
        ResultSet rs = null;
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + TEXT + ", "
                + NARH + ", "
                + ULGURJI + ", "
                + CHAKANA + ", "
                + NDS + ", "
                + BOJ + ", "
                + USERID +
                ") VALUES(?,?,?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            prSt.setString(1, standart6.getText());
            prSt.setDouble(2, standart6.getNarh());
            prSt.setDouble(3, standart6.getUlgurji());
            prSt.setDouble(4, standart6.getChakana());
            prSt.setDouble(5, standart6.getNds());
            prSt.setDouble(6, standart6.getBoj());
            prSt.setInt(7, standart6.getUserId());
            prSt.executeUpdate();
            rs = prSt.getGeneratedKeys();
            if(rs.next()){
                insertedID = rs.getInt(1);
                standart6.setId(insertedID);
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();;
        }
        return insertedID;
    }


    public void addBatch(Connection connection, ObservableList<Standart6> standart6ObservableList) {
        MySqlStatus.checkMyConnection(connection);
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + TEXT + ", "
                + NARH + ", "
                + ULGURJI + ", "
                + CHAKANA + ", "
                + NDS + ", "
                + BOJ + ", "
                + USERID +
                ") VALUES(?,?,?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert);
            for(Standart6 s6: standart6ObservableList) {
                prSt = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
                prSt.setInt(1, s6.getId());
                prSt.setString(2, s6.getText());
                prSt.setDouble(3, s6.getNarh());
                prSt.setDouble(4, s6.getUlgurji());
                prSt.setDouble(5, s6.getChakana());
                prSt.setDouble(6, s6.getNds());
                prSt.setDouble(7, s6.getBoj());
                prSt.setInt(8, s6.getUserId());
                prSt.addBatch();
            }
            prSt.executeBatch();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();;
        }
    }
    public void copyDataBatch(Connection connection, ObservableList<Standart6> standart6ObservableList) {
        MySqlStatus.checkMyConnection(connection);
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + ID_FIELD + ", "
                + TEXT + ", "
                + NARH + ", "
                + ULGURJI + ", "
                + CHAKANA + ", "
                + NDS + ", "
                + BOJ + ", "
                + USERID + ", "
                + DATETIME +
                ") VALUES(?,?,?,?,?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert);
            for(Standart6 s6: standart6ObservableList) {
                prSt.setInt(1, s6.getId());
                prSt.setString(2, s6.getText());
                prSt.setDouble(3, s6.getNarh());
                prSt.setDouble(4, s6.getUlgurji());
                prSt.setDouble(5, s6.getChakana());
                prSt.setDouble(6, s6.getNds());
                prSt.setDouble(7, s6.getBoj());
                prSt.setInt(8, s6.getUserId());
                prSt.setString(9, sdf.format(s6.getDateTime()));
                prSt.addBatch();
            }
            prSt.executeBatch();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();;
        }
    }


    public void deleteBatch(Connection connection, ObservableList<Standart6> standart6List) {
        MySqlStatus.checkMyConnection(connection);
        String delete = "DELETE FROM " + TABLENAME + " WHERE " + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(delete);
            for (Standart6 s6: standart6List) {
                prSt.setInt(1, s6.getId());
                prSt.addBatch();
            }
            prSt.executeBatch();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();;
        }
    }
    public void delete_data(Connection connection, Standart6 standart6) {
        MySqlStatus.checkMyConnection(connection);
        String delete = "DELETE FROM " + TABLENAME + " WHERE " + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(delete);
            prSt.setInt(1, standart6.getId());
            prSt.executeUpdate();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();;
        }
    }
    public void update_data(Connection connection, Standart6 standart6) {
        MySqlStatus.checkMyConnection(connection);
        String replace = "UPDATE " + TABLENAME + " SET "
                + TEXT + " = ?, "
                + NARH + " = ?, "
                + ULGURJI + " = ?, "
                + CHAKANA + " = ?, "
                + NDS + " = ?, "
                + BOJ + " = ? WHERE "
                + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(replace);
            prSt.setString(1, standart6.getText());
            prSt.setDouble(2, standart6.getNarh());
            prSt.setDouble(3, standart6.getUlgurji());
            prSt.setDouble(4, standart6.getChakana());
            prSt.setDouble(5, standart6.getNds());
            prSt.setDouble(6, standart6.getBoj());
            prSt.setInt(7, standart6.getId());
            prSt.executeUpdate();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();;
        }
    }

    public Standart6 guruhliTovarNarhi(Connection connection, int tovarId) {
        MySqlStatus.checkMyConnection(connection);
        Standart6 s6 = null;
        Standart6Models standart6Models = new Standart6Models("TGuruh1");
        Standart3Models standart3Models = new Standart3Models();
        standart3Models.setTABLENAME("TGuruh2");
        ObservableList<Standart3> s3List = standart3Models.getAnyData(connection, "id3 = " + tovarId, "");
        if (s3List.size()>0) {
            Standart3 s3 = s3List.get(0);
            s6 = standart6Models.getWithId(connection, s3.getId2());
        }
        return s6;
    }
    public String getTABLENAME() {
        return TABLENAME;
    }

    public void setTABLENAME(String TABLENAME) {
        this.TABLENAME = TABLENAME;
    }
}
