package sample.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Config.MySqlDB;
import sample.Data.Standart;
import sample.Data.Standart6;
import sample.Tools.Alerts;
import sample.Tools.QueryHelper;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class StandartModels {
    private String TABLENAME = "Birlik";
    private final String ID_FIELD = "id";
    private final String TEXT = "text";
    private final String USERID = "userId";
    private final String DATETIME = "dateTime";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    QueryHelper queryHelper;

    public StandartModels() {
    }

    public StandartModels(String TABLENAME) {
        this.TABLENAME = TABLENAME;
    }


    public ObservableList<Standart> get_data(Connection connection) {
        try {
            if (!connection.isValid(100)) {
                MySqlDB.connectionIsValid(connection);
                System.out.println("qayta ulanamiz");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        ObservableList<Standart> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME;
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new Standart(rs.getInt(1),
                        rs.getString(2),
                        rs.getInt(3),
                        sdf.parse(rs.getString(4))
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

    public Standart getDataId(Connection connection, Integer tovarId) {
        ObservableList<Standart> books = FXCollections.observableArrayList();
        Standart tovar = null;
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME + " WHERE id = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setInt(1, tovarId);
            rs = prSt.executeQuery();
            while (rs.next()) {
                tovar =new Standart(rs.getInt(1),
                        rs.getString(2),
                        rs.getInt(3),
                        sdf.parse(rs.getString(4))
                );
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        } catch (ParseException e) {
            Alerts.parseError();
        }
        return tovar;
    }

    public ObservableList<Standart> getAnyData(Connection connection, String sqlWhere, String sqlOrderBy)  {
        ObservableList<Standart> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        queryHelper = new QueryHelper(sqlWhere, sqlOrderBy);
        String select = "SELECT * FROM " + TABLENAME + queryHelper.getYakuniyJumla();
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new Standart(rs.getInt(1),
                        rs.getString(2),
                        rs.getInt(3),
                        sdf.parse(rs.getString(4))
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

    public Integer insert_data(Connection connection, Standart standart) {
        Integer insertedID = -1;
        ResultSet rs = null;
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + TEXT + ", "
                + USERID +
                ") VALUES(?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            prSt.setString(1, standart.getText());
            prSt.setInt(2, standart.getUserId());
            prSt.executeUpdate();
            rs = prSt.getGeneratedKeys();
            if(rs.next()){
                insertedID = rs.getInt(1);
                standart.setId(insertedID);
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            e.printStackTrace();
//            Alerts.losted();;
        }
        return insertedID;
    }
    public Integer insert_data2(Connection connection, Standart standart) {
        Integer insertedID = -1;
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + ID_FIELD + ", "
                + TEXT + ", "
                + USERID +
                ") VALUES(?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert);
            prSt.setInt(1, standart.getId());
            prSt.setString(2, standart.getText());
            prSt.setInt(3, standart.getUserId());
            prSt.executeUpdate();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();;
        }
        return insertedID;
    }
    public void delete_data(Connection connection, Standart standart) {
        String delete = "DELETE FROM " + TABLENAME + " WHERE " + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(delete);
            prSt.setInt(1, standart.getId());
            prSt.executeUpdate();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();;
        }
    }
    public void update_data(Connection connection, Standart standart) {
        String replace = "UPDATE " + TABLENAME + " SET "
                + TEXT + " = ? WHERE "
                + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(replace);
            prSt.setString(1, standart.getText());
            prSt.setInt(2, standart.getId());
            prSt.executeUpdate();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();;
        }
    }

    public void addBatch(Connection connection, ObservableList<Standart> standartObservableList) {
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + TEXT + ", "
                + USERID +
                ") VALUES(?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert);
            for(Standart s: standartObservableList) {
                prSt.setString(1, s.getText());
                prSt.setInt(2, s.getUserId());
                prSt.addBatch();
            }
            prSt.executeBatch();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();;
        }
    }

    public void copyDataBatch(Connection connection, ObservableList<Standart> standartObservableList) {
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + ID_FIELD + ", "
                + TEXT + ", "
                + USERID + ", "
                + DATETIME +
                ") VALUES(?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert);
            for(Standart s: standartObservableList) {
                prSt.setInt(1, s.getId());
                prSt.setString(2, s.getText());
                prSt.setInt(3, s.getUserId());
                prSt.setString(4, sdf.format(s.getDateTime()));
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
