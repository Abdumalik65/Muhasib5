package sample.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Data.Valuta;
import sample.Tools.Alerts;
import sample.Tools.QueryHelper;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ValutaModels {
    private final String TABLENAME = "Valuta";
    private final String ID_FIELD = "id";
    private final String VALUTA = "valuta";
    private final String STATUS = "status";
    private final String USERID = "userId";
    private final String DATETIME = "dateTime";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    QueryHelper queryHelper;

    public ObservableList<Valuta> get_data(Connection connection) {
        ObservableList<Valuta> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME;
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new Valuta(rs.getInt(1),
                        rs.getString(2),
                        rs.getInt(3),
                        rs.getInt(4),
                        sdf.parse(rs.getString(5))
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

    public ObservableList<Valuta> getAnyData(Connection connection, String whereString, String orderByString) {
        ObservableList<Valuta> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        queryHelper = new QueryHelper(whereString, orderByString);
        String select = "SELECT * FROM " + TABLENAME + queryHelper.getYakuniyJumla();
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new Valuta(rs.getInt(1),
                        rs.getString(2),
                        rs.getInt(3),
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

    public Integer insert_data(Connection connection, Valuta valuta) {
        Integer insertedID = -1;
        ResultSet rs = null;
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + VALUTA + ", "
                + STATUS + ", "
                + USERID +
                ") VALUES(?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            prSt.setString(1, valuta.getValuta());
            prSt.setInt(2, valuta.getStatus());
            prSt.setInt(3, valuta.getUserId());
            prSt.executeUpdate();
            rs = prSt.getGeneratedKeys();
            if (rs.next()) {
                insertedID = rs.getInt(1);
                valuta.setId(insertedID);
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();;
        }
        return insertedID;
    }
    public void copyDataBatch(Connection connection, ObservableList<Valuta> valutaList) {
        Integer insertedID = -1;
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + ID_FIELD + ", "
                + VALUTA + ", "
                + STATUS + ", "
                + USERID + ", "
                + DATETIME +
                ") VALUES(?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert);
            for (Valuta valuta: valutaList) {
                prSt.setInt(1, valuta.getId());
                prSt.setString(2, valuta.getValuta());
                prSt.setInt(3, valuta.getStatus());
                prSt.setInt(4, valuta.getUserId());
                prSt.setString(5, sdf.format(valuta.getDateTime()));
                prSt.addBatch();
            }
            prSt.executeBatch();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();;
        }
    }
    public void delete_data(Connection connection, Valuta valuta) {
        String delete = "DELETE FROM " + TABLENAME + " WHERE " + ID_FIELD + " = ?";
            PreparedStatement prSt = null;
            try {
                prSt = connection.prepareStatement(delete);
                prSt.setInt(1, valuta.getId());
                prSt.executeUpdate();
                prSt.close();
            } catch (SQLException e) {
                Alerts.losted();;
            }
    }
    public void update_data(Connection connection, Valuta valuta) {
        String replace = "UPDATE " + TABLENAME + " SET "
                + VALUTA + " = ?,"
                + STATUS + " = ? WHERE "
                + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(replace);
            prSt.setString(1, valuta.getValuta());
            prSt.setInt(2, valuta.getStatus());
            prSt.setInt(3, valuta.getId());
            prSt.executeUpdate();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();;
        }
    }
}
