package sample.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Config.MySqlStatus;
import sample.Data.HisobKitob;
import sample.Data.Kurs;
import sample.Data.Standart4;
import sample.Tools.Alerts;
import sample.Tools.QueryHelper;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Standart4Models {
    private String TABLENAME = "Tartib";
    private final String ID_FIELD = "id";
    private final String TOVAR = "tovar";
    private final String SANA = "sana";
    private final String MIQDOR = "miqdor";
    private final String USERID = "userId";
    private final String DATETIME = "dateTime";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    QueryHelper queryHelper;

    public ObservableList<Standart4> get_data(Connection connection) {
        MySqlStatus.checkMyConnection(connection);
        ObservableList<Standart4> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME;
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new Standart4(
                        rs.getInt(1),
                        rs.getInt(2),
                        sdf.parse(rs.getString(3)),
                        rs.getDouble(4),
                        rs.getInt(5),
                        sdf.parse(rs.getString(6))
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

    public ObservableList<Standart4> getAnyData(Connection connection, String sqlWhere, String sqlOrderBy) {
        MySqlStatus.checkMyConnection(connection);
        ObservableList<Standart4> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        queryHelper = new QueryHelper(sqlWhere, sqlOrderBy);
        String select = "SELECT * FROM " + TABLENAME + queryHelper.getYakuniyJumla();
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new Standart4(
                        rs.getInt(1),
                        rs.getInt(2),
                        sdf.parse(rs.getString(3)),
                        rs.getDouble(4),
                        rs.getInt(5),
                        sdf.parse(rs.getString(6))
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

    public Standart4 getTartibForDate(Connection connection, Integer tovar, Date date, String sqlOrderBy) {
        MySqlStatus.checkMyConnection(connection);
        ObservableList<Standart4> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME + " WHERE "+ TOVAR +" = ? AND sana <= ? ORDER BY " + DATETIME + " DESC";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setInt(1, tovar);
            prSt.setString(2, sdf.format(date));
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new Standart4(
                        rs.getInt(1),
                        rs.getInt(2),
                        sdf.parse(rs.getString(3)),
                        rs.getDouble(4),
                        rs.getInt(5),
                        sdf.parse(rs.getString(6))
                ));
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        } catch (ParseException e) {
            Alerts.parseError();
        }
        Standart4 standart4 = null;
        if (books.size()>0) {
            standart4 = books.get(0);
        }
        return standart4;
    }
    public Integer insert_data(Connection connection, Standart4 standart4) {
        MySqlStatus.checkMyConnection(connection);
        Integer insertedID = -1;
        ResultSet rs = null;
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + TOVAR + ", "
                + SANA + ", "
                + MIQDOR + ", "
                + USERID +
                ") VALUES(?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            prSt.setInt(1, standart4.getTovar());
            prSt.setString(2, sdf.format(standart4.getSana()));
            prSt.setDouble(3, standart4.getMiqdor());
            prSt.setInt(4, standart4.getUserId());
            prSt.executeUpdate();
            rs = prSt.getGeneratedKeys();
            if(rs.next()){
                insertedID = rs.getInt(1);
                standart4.setId(insertedID);
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();;
        }
        return insertedID;
    }

    public ObservableList<Standart4> getDate(Connection connection, Integer tovarId, Date date, String sqlOrderBy) {
        MySqlStatus.checkMyConnection(connection);
        ObservableList<Standart4> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME + " WHERE " + TOVAR + " = ? AND sana <= ? ORDER BY " + sqlOrderBy;
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setInt(1, tovarId);
            prSt.setString(2, sdf.format(date));
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new Standart4(
                        rs.getInt(1),
                        rs.getInt(2),
                        sdf.parse(rs.getString(3)),
                        rs.getDouble(4),
                        rs.getInt(5),
                        sdf.parse(rs.getString(6))
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

    public void addBatch(Connection connection, ObservableList<Standart4> standart4ObservableList) {
        MySqlStatus.checkMyConnection(connection);
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + TOVAR + ", "
                + SANA + ", "
                + MIQDOR + ", "
                + USERID +
                ") VALUES(?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert);
            for(Standart4 s4: standart4ObservableList) {
                prSt.setInt(1, s4.getTovar());
                prSt.setString(2, sdf.format(s4.getSana()));
                prSt.setDouble(3, s4.getMiqdor());
                prSt.setInt(4, s4.getUserId());
                prSt.addBatch();
            }
            prSt.executeBatch();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();;
        }
    }
    public void copyDataBatch(Connection connection, ObservableList<Standart4> standart4ObservableList) {
        MySqlStatus.checkMyConnection(connection);
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + ID_FIELD + ", "
                + TOVAR + ", "
                + SANA + ", "
                + MIQDOR + ", "
                + USERID + ", "
                + DATETIME +
                ") VALUES(?,?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert);
            for(Standart4 s4: standart4ObservableList) {
                prSt.setInt(1, s4.getId());
                prSt.setInt(2, s4.getTovar());
                prSt.setString(3, sdf.format(s4.getSana()));
                prSt.setDouble(4, s4.getMiqdor());
                prSt.setInt(5, s4.getUserId());
                prSt.setString(6, sdf.format(s4.getDateTime()));
                prSt.addBatch();
            }
            prSt.executeBatch();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();;
        }
    }


    public void delete_data(Connection connection, Standart4 standart4) {
        MySqlStatus.checkMyConnection(connection);
        String delete = "DELETE FROM " + TABLENAME + " WHERE " + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(delete);
            prSt.setInt(1, standart4.getId());
            prSt.executeUpdate();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();;
        }
    }
    public void update_data(Connection connection, Standart4 standart4) {
        MySqlStatus.checkMyConnection(connection);
        String replace = "UPDATE " + TABLENAME + " SET "
                + TOVAR + " = ?, "
                + SANA + " = ?, "
                + MIQDOR + " = ? WHERE "
                + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(replace);
            prSt.setInt(1, standart4.getTovar());
            prSt.setString(2, sdf.format(standart4.getSana()));
            prSt.setDouble(3, standart4.getMiqdor());
            prSt.setInt(4, standart4.getId());
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
