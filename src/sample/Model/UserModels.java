package sample.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Config.MySqlStatus;
import sample.Tools.Alerts;
import sample.Tools.QueryHelper;
import sample.Data.User;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class UserModels {
    private final String TABLENAME = "Users";
    private final String ID_FIELD = "id";
    private final String ISM = "ism";
    private final String RASM = "rasm";
    private final String PAROL = "parol";
    private String EMAIL = "eMail";
    private String PHONE = "phone";
    private String STATUS = "status";
    private String JINS = "jins";
    private String ONLINE = "online";
    private String ONLINE_FIELD = "online";
    private final String USERID = "userId";
    private final String DATETIME = "dateTime";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    QueryHelper queryHelper;

    public Integer addUser(Connection connection, User user) {
        MySqlStatus.checkMyConnection(connection);
        Integer insertedID = -1;
        ResultSet rs = null;
        String insert = "INSERT INTO "+ TABLENAME + "(" +
                ISM + "," +
                RASM + "," +
                PAROL + "," +
                EMAIL + "," +
                PHONE + "," +
                STATUS + "," +
                JINS + "," +
                ONLINE + "," +
                USERID + ") " +
                "VALUES(?,?,?,?,?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            prSt.setString(1, user.getIsm());
            prSt.setString(2, user.getRasm());
            prSt.setString(3, user.getParol());
            prSt.setString(4, user.geteMail());
            prSt.setString(5, user.getPhone());
            prSt.setInt(6, user.getStatus());
            prSt.setString(7, user.getJins());
            prSt.setInt(8, user.getOnline());
            prSt.setInt(9, user.getUserId());
            prSt.executeUpdate();
            rs = prSt.getGeneratedKeys();
            if(rs.next()){
                insertedID = rs.getInt(1);
                user.setId(insertedID);
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();;
        }
        return insertedID;
    }
    public void copyDataBatch(Connection connection, ObservableList<User> userList) {
        MySqlStatus.checkMyConnection(connection);
        String insert = "INSERT INTO "+ TABLENAME + "(" +
                ID_FIELD + "," +
                ISM + "," +
                RASM + "," +
                PAROL + "," +
                EMAIL + "," +
                PHONE + "," +
                STATUS + "," +
                JINS + "," +
                ONLINE + "," +
                USERID + "," +
                DATETIME +
                ") VALUES(?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert);
            for (User user: userList) {
                prSt.setInt(1, user.getId());
                prSt.setString(2, user.getIsm());
                prSt.setString(3, user.getRasm());
                prSt.setString(4, user.getParol());
                prSt.setString(5, user.geteMail());
                prSt.setString(6, user.getPhone());
                prSt.setInt(7, user.getStatus());
                prSt.setString(8, user.getJins());
                prSt.setInt(9, user.getOnline());
                prSt.setInt(10, user.getUserId());
                prSt.setString(11, sdf.format(user.getDateTime()));
                prSt.addBatch();
            }
            prSt.executeBatch();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();;
//            e.printStackTrace();
        }
    }

    public ObservableList<User> getData(Connection connection) {
        MySqlStatus.checkMyConnection(connection);
        ObservableList<User> book = FXCollections.observableArrayList();
        ResultSet resSet = null;
        String select = "SELECT * FROM "+ TABLENAME;
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            resSet = prSt.executeQuery();
            while (resSet.next()) {
                book.add(new User(
                        resSet.getInt(1),
                        resSet.getString(2),
                        resSet.getString(3),
                        resSet.getString(4),
                        resSet.getString(5),
                        resSet.getString(6),
                        resSet.getInt(7),
                        resSet.getString(8),
                        resSet.getInt(9),
                        resSet.getInt(10),
                        sdf.parse(resSet.getString(11))
                ));
            }
            prSt.close();
            resSet.close();
        } catch (SQLException e) {
            Alerts.losted();
        } catch (ParseException e) {
            Alerts.parseError();
        }
        return book;
    }

    public ObservableList<User> getAnyData(Connection connection, String sqlWhere, String sqlOrderBy) {
        MySqlStatus.checkMyConnection(connection);
        ObservableList<User> book = FXCollections.observableArrayList();
        ResultSet resSet = null;
        queryHelper = new QueryHelper(sqlWhere, sqlOrderBy);
        String select = "SELECT * FROM "+ TABLENAME + queryHelper.getYakuniyJumla();
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            resSet = prSt.executeQuery();
            while (resSet.next()) {
                book.add(new User(
                        resSet.getInt(1),
                        resSet.getString(2),
                        resSet.getString(3),
                        resSet.getString(4),
                        resSet.getString(5),
                        resSet.getString(6),
                        resSet.getInt(7),
                        resSet.getString(8),
                        resSet.getInt(9),
                        resSet.getInt(10),
                        sdf.parse(resSet.getString(11))
                ));
            }
            prSt.close();
            resSet.close();
        } catch (SQLException e) {
            Alerts.losted();
        } catch (ParseException e) {
            Alerts.parseError();
        }
        return book;
    }

    public ObservableList<User> getData1(Connection connection) {
        MySqlStatus.checkMyConnection(connection);
        ObservableList<User> book = FXCollections.observableArrayList();
        ResultSet resSet = null;
        String select = "SELECT * FROM "+ TABLENAME;
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            resSet = prSt.executeQuery();
            while (resSet.next()) {
                book.add(new User(
                        resSet.getInt(1),
                        resSet.getString(2),
                        resSet.getString(3),
                        resSet.getString(4),
                        resSet.getString(5),
                        resSet.getString(6),
                        resSet.getInt(7),
                        resSet.getString(8),
                        resSet.getInt(9),
                        resSet.getInt(10),
                        sdf.parse(resSet.getString(11))
                ));
            }
            prSt.close();
            resSet.close();
        } catch (SQLException e) {
            Alerts.losted();
        } catch (ParseException e) {
            Alerts.parseError();
        }
        return book;
    }

    public User getUser(Connection connection, User user) {
        MySqlStatus.checkMyConnection(connection);
        User user1 = null;
        ResultSet resSet = null;
        String select = "SELECT * FROM "+ TABLENAME + " WHERE " + ISM + " = ? AND " + PAROL + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setString(1, user.getIsm().trim());
            prSt.setString(2, user.getParol().trim());
            resSet = prSt.executeQuery();
            while (resSet.next()) {
                System.out.println(resSet.getString(2));
                System.out.println(resSet.getString(4));
                user1 =  new User(
                        resSet.getInt(1),
                        resSet.getString(2),
                        resSet.getString(3),
                        resSet.getString(4),
                        resSet.getString(5),
                        resSet.getString(6),
                        resSet.getInt(7),
                        resSet.getString(8),
                        resSet.getInt(9),
                        resSet.getInt(10),
                        sdf.parse(resSet.getString(11))
                );
            }
            prSt.close();
            resSet.close();
        } catch (SQLException e) {
            Alerts.losted();
        } catch (ParseException e) {
            Alerts.parseError();
        }
        System.out.println(user1);
        return user1;
    }


    public void changeUser(Connection connection, User user) {
        MySqlStatus.checkMyConnection(connection);
        String replace = "UPDATE " + TABLENAME + " SET "  +
                ISM + " =?, " +
                RASM+ " =?, " +
                PAROL+ " =?, " +
                EMAIL+ " =?, " +
                PHONE+ " =?, " +
                STATUS+ " =?, " +
                JINS + " =?, " +
                ONLINE + " =? WHERE " +
                ID_FIELD + " =? ";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(replace);
            prSt.setString(1, user.getIsm());
            prSt.setString(2, user.getRasm());
            prSt.setString(3, user.getParol());
            prSt.setString(4, user.geteMail());
            prSt.setString(5, user.getPhone());
            prSt.setInt(6, user.getStatus());
            prSt.setString(7, user.getJins());
            prSt.setInt(8, user.getOnline());
            prSt.setInt(9, user.getId());
            prSt.executeUpdate();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();;
        }
    }

    public void delete_data(Connection connection, User user){
        MySqlStatus.checkMyConnection(connection);
        String delete = "DELETE FROM " + TABLENAME + " WHERE " + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(delete);
            prSt.setInt(1, user.getId());
            prSt.executeUpdate();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();;
        }
    }
}
