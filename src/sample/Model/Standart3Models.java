package sample.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Config.MySqlStatus;
import sample.Data.Standart3;
import sample.Tools.Alerts;
import sample.Tools.QueryHelper;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Standart3Models {
    private String TABLENAME = "Birlik";
    private final String ID_FIELD = "id";
    private final String ID_FIELD2 = "id2";
    private final String ID_FIELD3 = "id3";
    private final String TEXT = "text";
    private final String USERID = "userId";
    private final String DATETIME = "dateTime";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    QueryHelper queryHelper;

    public Standart3Models() {
    }

    public Standart3Models(String TABLENAME) {
        this.TABLENAME = TABLENAME;
    }

    public ObservableList<Standart3> get_data(Connection connection)  {
        MySqlStatus.checkMyConnection(connection);
        ObservableList<Standart3> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME;
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new Standart3(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getString(4),
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

    public ObservableList<Standart3> get_data1(Connection connection, java.util.Date date)  {
        MySqlStatus.checkMyConnection(connection);
        ObservableList<Standart3> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        if (date != null) {
            date = new java.util.Date();
        }
        String select = "SELECT * FROM " + TABLENAME + " WHERE DATETIME<=?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setString(1, sdf.format(date));
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new Standart3(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getString(4),
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

    public ObservableList<Standart3> getAnyData(Connection connection, String sqlWhere, String sqlOrderBy){
        MySqlStatus.checkMyConnection(connection);
        ObservableList<Standart3> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        queryHelper = new QueryHelper(sqlWhere, sqlOrderBy);
        String select = "SELECT * FROM " + TABLENAME + queryHelper.getYakuniyJumla();
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new Standart3(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getString(4),
                        rs.getInt(5),
                        sdf.parse(rs.getString(6))
                ));
            }
            rs.close();
            prSt.close();
        } catch (ParseException e) {
            Alerts.parseError();
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public Integer insert_data(Connection connection, Standart3 standart3) {
        MySqlStatus.checkMyConnection(connection);
        Integer insertedID = -1;
        ResultSet rs = null;
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + ID_FIELD2 + ", "
                + ID_FIELD3 + ", "
                + TEXT + ", "
                + USERID +
                ") VALUES(?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            prSt.setInt(1, standart3.getId2());
            prSt.setInt(2, standart3.getId3());
            prSt.setString(3, standart3.getText());
            prSt.setInt(4, standart3.getUserId());
            prSt.executeUpdate();
            rs = prSt.getGeneratedKeys();
            if(rs.next()){
                insertedID = rs.getInt(1);
                standart3.setId(rs.getInt(1));
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
            e.printStackTrace();
        }
        return insertedID;
    }
    public void delete_data(Connection connection, Standart3 standart3) {
        MySqlStatus.checkMyConnection(connection);
        String delete = "DELETE FROM " + TABLENAME + " WHERE " + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(delete);
            prSt.setInt(1, standart3.getId());
            prSt.executeUpdate();
            prSt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            Alerts.losted();;
        }
    }

    public void deleteBatch(Connection connection, ObservableList<Standart3> standart3ObservableList) {
        MySqlStatus.checkMyConnection(connection);
        String delete = "DELETE FROM " + TABLENAME + " WHERE " + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(delete);
            for (Standart3 standart3: standart3ObservableList) {
                prSt.setInt(1, standart3.getId());
                prSt.addBatch();
            }
            prSt.executeBatch();
            prSt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            Alerts.losted();;
        }
    }

    public void update_data(Connection connection, Standart3 standart3) {
        MySqlStatus.checkMyConnection(connection);
        String replace = "UPDATE " + TABLENAME + " SET "
                + ID_FIELD2 + " = ?, "
                + ID_FIELD3 + " = ?, "
                + TEXT + " = ? WHERE "
                + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(replace);
            prSt.setInt(1, standart3.getId2());
            prSt.setInt(2, standart3.getId3());
            prSt.setString(3, standart3.getText());
            prSt.setInt(4, standart3.getId());
            prSt.executeUpdate();
            prSt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            Alerts.losted();;
        }
    }

    public void addBatch(Connection connection, ObservableList<Standart3> standart3ObservableList) {
        MySqlStatus.checkMyConnection(connection);
        ResultSet rs = null;
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + ID_FIELD2 + ", "
                + ID_FIELD3 + ", "
                + TEXT + ", "
                + USERID +
                ") VALUES(?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            for(Standart3 standart3: standart3ObservableList) {
                prSt.setInt(1, standart3.getId2());
                prSt.setInt(2, standart3.getId3());
                prSt.setString(3, standart3.getText());
                prSt.setInt(4, standart3.getUserId());
                prSt.addBatch();
            }
            prSt.executeBatch();
            rs = prSt.getGeneratedKeys();
            for (Standart3 s3: standart3ObservableList) {
                if (rs.next()) {
                    s3.setId(rs.getInt(1));
                }
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            Alerts.losted();;
        }
    }
    public void copyDataBatch(Connection connection, ObservableList<Standart3> standart3ObservableList) {
        MySqlStatus.checkMyConnection(connection);
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + ID_FIELD + ", "
                + ID_FIELD2 + ", "
                + ID_FIELD3 + ", "
                + TEXT + ", "
                + USERID + ", "
                + DATETIME +
                ") VALUES(?,?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert);
            for(Standart3 standart3: standart3ObservableList) {
                prSt.setInt(1, standart3.getId());
                prSt.setInt(2, standart3.getId2());
                prSt.setInt(3, standart3.getId3());
                prSt.setString(4, standart3.getText());
                prSt.setInt(5, standart3.getUserId());
                prSt.setString(6, sdf.format(standart3.getDateTime()));
                prSt.addBatch();
            }
            prSt.executeBatch();
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
