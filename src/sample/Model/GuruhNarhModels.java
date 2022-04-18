package sample.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Config.MySqlStatus;
import sample.Data.GuruhNarh;
import sample.Data.Narh;
import sample.Tools.Alerts;
import sample.Tools.QueryHelper;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GuruhNarhModels {
    private String TABLENAME = "GuruhNarhi";
    private final String ID_FIELD = "id";
    private final String SANA = "sana";
    private final String GURUHID = "guruhId";
    private final String NARHID = "narhId";
    private final String NARHDOUBLE = "narhDouble";
    private final String USERID = "userId";
    private final String DATETIME = "dateTime";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    QueryHelper queryHelper;

    public ObservableList<GuruhNarh> get_data(Connection connection) {
        MySqlStatus.checkMyConnection(connection);
        ObservableList<GuruhNarh> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME;
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new GuruhNarh(
                        rs.getInt(1),
                        sdf.parse(rs.getString(2)),
                        rs.getInt(3),
                        rs.getInt(4),
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

    public ObservableList<GuruhNarh> getAnyData(Connection connection, String sqlWhere, String sqlOrderBy) {
        MySqlStatus.checkMyConnection(connection);
        ObservableList<GuruhNarh> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        queryHelper = new QueryHelper(sqlWhere, sqlOrderBy);
        String select = "SELECT * FROM " + TABLENAME + queryHelper.getYakuniyJumla();
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new GuruhNarh(
                        rs.getInt(1),
                        sdf.parse(rs.getString(2)),
                        rs.getInt(3),
                        rs.getInt(4),
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

    public GuruhNarh getTartibForDate(Connection connection, Integer tovar, Date date, String sqlOrderBy) {
        MySqlStatus.checkMyConnection(connection);
        ObservableList<GuruhNarh> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME + " WHERE "+ GURUHID +" = ? AND sana <= ? ORDER BY " + DATETIME + " DESC";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setInt(1, tovar);
            prSt.setString(2, sdf.format(date));
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new GuruhNarh(
                                rs.getInt(1),
                                sdf.parse(rs.getString(2)),
                                rs.getInt(3),
                                rs.getInt(4),
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
        GuruhNarh guruhNarh = null;
        if (books.size()>0) {
            guruhNarh = books.get(0);
        }
        return guruhNarh;
    }
    public Integer insert_data(Connection connection, GuruhNarh guruhNarh) {
        MySqlStatus.checkMyConnection(connection);
        Integer insertedID = -1;
        ResultSet rs = null;
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + SANA + ", "
                + GURUHID + ", "
                + NARHID + ", "
                + NARHDOUBLE + ", "
                + USERID +
                ") VALUES(?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            prSt.setString(1, sdf.format(guruhNarh.getSana()));
            prSt.setInt(2, guruhNarh.getGuruhId());
            prSt.setInt(3, guruhNarh.getNarhId());
            prSt.setDouble(4, guruhNarh.getNarhDouble());
            prSt.setInt(5, guruhNarh.getUserId());
            prSt.executeUpdate();
            rs = prSt.getGeneratedKeys();
            if(rs.next()){
                insertedID = rs.getInt(1);
                guruhNarh.setId(insertedID);
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
        return insertedID;
    }

    public ObservableList<GuruhNarh> getDate(Connection connection, Integer tovarId, Date date, String sqlOrderBy) {
        MySqlStatus.checkMyConnection(connection);
        ObservableList<GuruhNarh> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME + " WHERE " + GURUHID + " = ? AND sana <= ? ORDER BY " + sqlOrderBy;
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setInt(1, tovarId);
            prSt.setString(2, sdf.format(date));
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new GuruhNarh(
                        rs.getInt(1),
                        sdf.parse(rs.getString(2)),
                        rs.getInt(3),
                        rs.getInt(4),
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

    public void addBatch(Connection connection, ObservableList<GuruhNarh> guruhNarhObservableList) {
        MySqlStatus.checkMyConnection(connection);
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + SANA + ", "
                + GURUHID + ", "
                + NARHID + ", "
                + NARHDOUBLE + ", "
                + USERID +
                ") VALUES(?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert);
            for(GuruhNarh s4: guruhNarhObservableList) {
                prSt.setString(1, sdf.format(s4.getSana()));
                prSt.setInt(2, s4.getGuruhId());
                prSt.setInt(3, s4.getNarhId());
                prSt.setDouble(4, s4.getNarhDouble());
                prSt.setInt(5, s4.getUserId());
                prSt.addBatch();
            }
            prSt.executeBatch();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
    }
    public void copyDataBatch(Connection connection, ObservableList<GuruhNarh> guruhNarhObservableList) {
        MySqlStatus.checkMyConnection(connection);
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + ID_FIELD + ", "
                + SANA + ", "
                + GURUHID + ", "
                + NARHID + ", "
                + NARHDOUBLE + ", "
                + USERID + ", "
                + DATETIME +
                ") VALUES(?,?,?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert);
            for(GuruhNarh s4: guruhNarhObservableList) {
                prSt.setInt(1, s4.getId());
                prSt.setString(2, sdf.format(s4.getSana()));
                prSt.setInt(3, s4.getGuruhId());
                prSt.setInt(4, s4.getNarhId());
                prSt.setDouble(5, s4.getNarhDouble());
                prSt.setInt(6, s4.getUserId());
                prSt.setString(7, sdf.format(s4.getSana()));
                prSt.addBatch();
            }
            prSt.executeBatch();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
    }

    public void delete_data(Connection connection, GuruhNarh guruhNarh) {
        MySqlStatus.checkMyConnection(connection);
        String delete = "DELETE FROM " + TABLENAME + " WHERE " + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(delete);
            prSt.setInt(1, guruhNarh.getId());
            prSt.executeUpdate();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
    }
    public void update_data(Connection connection, GuruhNarh guruhNarh) {
        MySqlStatus.checkMyConnection(connection);
        String replace = "UPDATE " + TABLENAME + " SET "
                + SANA + " = ?, "
                + GURUHID + " = ?, "
                + NARHID + " = ?, "
                + NARHDOUBLE + " = ? WHERE "
                + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(replace);
            prSt.setString(1, sdf.format(guruhNarh.getSana()));
            prSt.setInt(2, guruhNarh.getGuruhId());
            prSt.setInt(3, guruhNarh.getNarhId());
            prSt.setDouble(4, guruhNarh.getNarhDouble());
            prSt.setInt(5, guruhNarh.getId());
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
