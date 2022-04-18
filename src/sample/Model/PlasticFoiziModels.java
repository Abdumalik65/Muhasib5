package sample.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Config.MySqlStatus;
import sample.Data.PlasticFoizi;
import sample.Data.User;
import sample.Data.Valuta;
import sample.Tools.Alerts;
import sample.Tools.GetDbData;
import sample.Tools.QueryHelper;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class PlasticFoiziModels {
    private final String TABLENAME = "PlasticFoiz";
    private final String ID_FIELD = "id";
    private final String FOIZ = "foiz";
    private final String USERID = "userId";
    private final String DATETIME = "dateTime";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    QueryHelper queryHelper;

    public ObservableList<PlasticFoizi> get_data(Connection connection) {
        MySqlStatus.checkMyConnection(connection);
        ObservableList<PlasticFoizi> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME;
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new PlasticFoizi(rs.getInt(1),
                        rs.getDouble(2),
                        rs.getInt(3),
                        sdf.parse(rs.getString(4))
                ));
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            Alerts.losted();
        } catch (ParseException e) {
            Alerts.parseError();
        }
        return books;
    }

    public void update_data(Connection connection, PlasticFoizi plasticFoizi) {
        MySqlStatus.checkMyConnection(connection);
        String replace = "UPDATE " + TABLENAME + " SET "
                + FOIZ + " = ? WHERE "
                + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(replace);
            prSt.setDouble(1, plasticFoizi.getFoiz());
            prSt.setInt(2, plasticFoizi.getId());
            prSt.executeUpdate();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
    }
    public Integer insert(Connection connection, PlasticFoizi plasticFoizi) {
        MySqlStatus.checkMyConnection(connection);
        Integer insertedID = -1;
        ResultSet rs = null;
        String insert = "INSERT INTO "+ TABLENAME + "(" +
                FOIZ + "," +
                USERID + ") " +
                "VALUES(?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            prSt.setDouble(1, plasticFoizi.getFoiz());
            prSt.setInt(2, plasticFoizi.getUserId());
            prSt.executeUpdate();
            rs = prSt.getGeneratedKeys();
            if(rs.next()){
                insertedID = rs.getInt(1);
                plasticFoizi.setId(insertedID);
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();;
        }
        return insertedID;
    }

}