package sample.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Data.Narh;
import sample.Data.Standart4;
import sample.Tools.Alerts;
import sample.Tools.QueryHelper;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;

public class NarhModels {
    private String TABLENAME = "XaridNarhi";
    private final String ID_FIELD = "id";
    private final String TOVAR = "tovar";
    private final String SANA = "sana";
    private final String XARID = "xarid";
    private final String USERID = "userId";
    private final String DATETIME = "dateTime";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    QueryHelper queryHelper;

    public ObservableList<Narh> get_data(Connection connection) {
        ObservableList<Narh> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME;
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new Narh(
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

    public ObservableList<Narh> getAnyData(Connection connection, String sqlWhere, String sqlOrderBy) {
        ObservableList<Narh> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        queryHelper = new QueryHelper(sqlWhere, sqlOrderBy);
        String select = "SELECT * FROM " + TABLENAME + queryHelper.getYakuniyJumla();
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new Narh(
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

    public Narh getTartibForDate(Connection connection, Integer tovar, Date date, String sqlOrderBy) {
        ObservableList<Narh> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME + " WHERE "+ TOVAR +" = ? AND sana <= ? ORDER BY " + DATETIME + " DESC";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setInt(1, tovar);
            prSt.setString(2, sdf.format(date));
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new Narh(
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
        Narh narh = null;
        if (books.size()>0) {
            narh = books.get(0);
        }
        return narh;
    }
    public Integer insert_data(Connection connection, Narh narh) {
        Integer insertedID = -1;
        ResultSet rs = null;
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + TOVAR + ", "
                + SANA + ", "
                + XARID + ", "
                + USERID +
                ") VALUES(?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            prSt.setInt(1, narh.getTovar());
            prSt.setString(2, sdf.format(narh.getSana()));
            prSt.setDouble(3, narh.getXaridDouble());
            prSt.setInt(4, narh.getUserId());
            prSt.executeUpdate();
            rs = prSt.getGeneratedKeys();
            if(rs.next()){
                insertedID = rs.getInt(1);
                narh.setId(insertedID);
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
        return insertedID;
    }

    public ObservableList<Narh> getDate(Connection connection, Integer tovarId, Date date, String sqlOrderBy) {
        ObservableList<Narh> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME + " WHERE " + TOVAR + " = ? AND sana <= ? ORDER BY " + sqlOrderBy;
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setInt(1, tovarId);
            prSt.setString(2, sdf.format(date));
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new Narh(
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

    public void addBatch(Connection connection, ObservableList<Narh> narhObservableList) {
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + TOVAR + ", "
                + SANA + ", "
                + XARID + ", "
                + USERID +
                ") VALUES(?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert);
            for(Narh s4: narhObservableList) {
                prSt.setInt(1, s4.getTovar());
                prSt.setString(2, sdf.format(s4.getSana()));
                prSt.setDouble(3, s4.getXaridDouble());
                prSt.setInt(4, s4.getUserId());
                prSt.addBatch();
            }
            prSt.executeBatch();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
    }
    public void copyDataBatch(Connection connection, ObservableList<Narh> narhObservableList) {
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + ID_FIELD + ", "
                + TOVAR + ", "
                + SANA + ", "
                + XARID + ", "
                + USERID + ", "
                + DATETIME +
                ") VALUES(?,?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert);
            for(Narh s4: narhObservableList) {
                prSt.setInt(1, s4.getId());
                prSt.setInt(2, s4.getTovar());
                prSt.setString(3, sdf.format(s4.getSana()));
                prSt.setDouble(4, s4.getXaridDouble());
                prSt.setInt(5, s4.getUserId());
                prSt.setString(6, sdf.format(s4.getDateTime()));
                prSt.addBatch();
            }
            prSt.executeBatch();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
    }

    public void delete_data(Connection connection, Narh narh) {
        String delete = "DELETE FROM " + TABLENAME + " WHERE " + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(delete);
            prSt.setInt(1, narh.getId());
            prSt.executeUpdate();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
    }
    public void update_data(Connection connection, Narh narh) {
        String replace = "UPDATE " + TABLENAME + " SET "
                + TOVAR + " = ?, "
                + SANA + " = ?, "
                + XARID + " = ? WHERE "
                + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(replace);
            prSt.setInt(1, narh.getTovar());
            prSt.setString(2, sdf.format(narh.getSana()));
            prSt.setDouble(3, narh.getXaridDouble());
            prSt.setInt(4, narh.getId());
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

}
