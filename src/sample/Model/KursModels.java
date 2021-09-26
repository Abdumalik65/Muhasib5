package sample.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Data.Kurs;
import sample.Data.Valuta;
import sample.Tools.Alerts;
import sample.Tools.GetDbData;
import sample.Tools.QueryHelper;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class KursModels {
    private final String TABLENAME = "Kurs";
    private final String ID_FIELD = "id";
    private final String SANA = "sana";
    private final String VALUTA = "valuta";
    private final String KURS = "kurs";
    private final String USERID = "userId";
    private final String DATETIME = "dateTime";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    QueryHelper queryHelper;

    public ObservableList<Kurs> get_data(Connection connection) {
        ObservableList<Kurs> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME;
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new Kurs(rs.getInt(1),
                        sdf.parse(rs.getString(2)),
                        rs.getInt(3),
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

    public ObservableList<Kurs> getAnyData(Connection connection, String sqlWhere, String sqlOrderBy) {
        ObservableList<Kurs> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        queryHelper = new QueryHelper(sqlWhere, sqlOrderBy);
        String select = "SELECT * FROM " + TABLENAME + queryHelper.getYakuniyJumla();
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new Kurs(rs.getInt(1),
                        sdf.parse(rs.getString(2)),
                        rs.getInt(3),
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

    public ObservableList<Kurs> getDate(Connection connection, Integer valuta, Date date, String sqlOrderBy) {
        ObservableList<Kurs> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME + " WHERE valuta = ? AND sana <= ? ORDER BY " + sqlOrderBy;
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setInt(1, valuta);
            prSt.setString(2, sdf.format(date));
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new Kurs(rs.getInt(1),
                        sdf.parse(rs.getString(2)),
                        rs.getInt(3),
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

    public Kurs getKurs(Connection connection, Integer valuta, Date date, String sqlOrderBy) {
        ObservableList<Kurs> books = FXCollections.observableArrayList();
        Kurs kurs = null;
        Valuta v1 = GetDbData.getValuta(valuta);

        if (v1.getStatus().equals(1)) {
            kurs = new Kurs(1, new Date(), v1.getId(), 1d, 1, new Date());
            return kurs;
        }
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME + " WHERE valuta = ? AND sana <= ? ORDER BY " + sqlOrderBy;
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setInt(1, valuta);
            prSt.setString(2, sdf.format(date));
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new Kurs(rs.getInt(1),
                        sdf.parse(rs.getString(2)),
                        rs.getInt(3),
                        rs.getDouble(4),
                        rs.getInt(5),
                        sdf.parse(rs.getString(6))
                ));
            }
            if (books.size()>0) {
                kurs = books.get(0);
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        } catch (ParseException e) {
            Alerts.parseError();
        }
        return kurs;
    }

    public Integer insert_data(Connection connection, Kurs kurs){
        Integer insertedID = -1;
        ResultSet rs = null;
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + SANA + ", "
                + VALUTA + ", "
                + KURS + ", "
                + USERID +
                ") VALUES(?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            prSt.setString(1, sdf.format(kurs.getSana()));
            prSt.setInt(2, kurs.getValuta());
            prSt.setDouble(3, kurs.getKurs());
            prSt.setInt(4, kurs.getUserId());
            prSt.executeUpdate();
            rs = prSt.getGeneratedKeys();
            if(rs.next()){
                insertedID = rs.getInt(1);
                kurs.setId(insertedID);
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
        return insertedID;
    }
    public void copyDataBatch(Connection connection, ObservableList<Kurs> kurslar){
        Integer insertedID = -1;
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + ID_FIELD + ", "
                + SANA + ", "
                + VALUTA + ", "
                + KURS + ", "
                + USERID + ", "
                + DATETIME +
                ") VALUES(?,?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert);
            for (Kurs kurs: kurslar) {
                prSt.setInt(1, kurs.getId());
                prSt.setString(2, sdf.format(kurs.getSana()));
                prSt.setInt(3, kurs.getValuta());
                prSt.setDouble(4, kurs.getKurs());
                prSt.setInt(5, kurs.getUserId());
                prSt.setString(6, sdf.format(kurs.getDateTime()));
                prSt.addBatch();
            }
            prSt.executeBatch();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
    }
    public void delete_data(Connection connection, Kurs kurs) {
        String delete = "DELETE FROM " + TABLENAME + " WHERE " + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(delete);
            prSt.setInt(1, kurs.getId());
            prSt.executeUpdate();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
    }
    public void update_data(Connection connection, Kurs kurs) {
        String replace = "UPDATE " + TABLENAME + " SET "
                + SANA + " = ?, "
                + VALUTA + " = ?, "
                + KURS + " = ? WHERE "
                + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(replace);
            prSt.setString(1, sdf.format(kurs.getSana()));
            prSt.setInt(2, kurs.getValuta());
            prSt.setDouble(3, kurs.getKurs());
            prSt.setInt(4, kurs.getId());
            prSt.executeUpdate();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
    }
}
