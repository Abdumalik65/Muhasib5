package sample.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Data.Aloqa;
import sample.Tools.QueryHelper;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class AloqaModels {
    private String TABLENAME = "AdressComp";
    private final String ID_FIELD = "id";
    private final String TEXT = "text";
    private final String DBHOST = "dbHost";
    private final String DBPORT = "dbPort";
    private final String DBUSER = "dbUser";
    private final String DBPASS = "dbPass";
    private final String DBNAME = "dbName";
    private final String DBPREFIX = "dbPrefix";
    private final String USERID = "userId";
    private final String DATETIME = "dateTime";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String dbHost;
    private String dbPort;
    private String dbUser;
    private String dbPass;
    private String dbName;
    QueryHelper queryHelper;

    public AloqaModels() {}

    public AloqaModels(String tableName) {
        this.TABLENAME = tableName;
    }

    public ObservableList<Aloqa> get_data(Connection connection) {
        ObservableList<Aloqa> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME;
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new Aloqa(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(7),
                        rs.getString(8),
                        rs.getInt(9),
                        sdf.parse(rs.getString(10))
                ));
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return books;
    }

    public Aloqa getWithId(Connection connection, int id) {
        Aloqa aloqa = null;
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME + " WHERE " + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setInt(1, id);
            rs = prSt.executeQuery();
            while (rs.next()) {
                aloqa = new Aloqa(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(7),
                        rs.getString(8),
                        rs.getInt(9),
                        sdf.parse(rs.getString(10))
                );
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return aloqa;
    }

    public ObservableList<Aloqa> getAnyData(Connection connection, String sqlWhere, String sqlOrderBy) {
        ObservableList<Aloqa> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        queryHelper = new QueryHelper(sqlWhere, sqlOrderBy);
        String select = "SELECT * FROM " + TABLENAME + queryHelper.getYakuniyJumla();
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new Aloqa(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(7),
                        rs.getString(8),
                        rs.getInt(9),
                        sdf.parse(rs.getString(10))
                ));
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return books;
    }

    public Integer insert_data(Connection connection, Aloqa aloqa) {
        Integer insertedID = -1;
        ResultSet rs = null;
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + TEXT + ", "
                + DBHOST + ", "
                + DBPORT + ", "
                + DBUSER + ", "
                + DBPASS + ", "
                + DBNAME + ", "
                + DBPREFIX + ", "
                + USERID +
                ") VALUES(?,?,?,?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            prSt.setString(1, aloqa.getText());
            prSt.setString(2, aloqa.getDbHost());
            prSt.setString(3, aloqa.getDbPort());
            prSt.setString(4, aloqa.getDbUser());
            prSt.setString(5, aloqa.getDbPass());
            prSt.setString(6, aloqa.getDbName());
            prSt.setString(7, aloqa.getDbPrefix());
            prSt.setInt(8, aloqa.getUserId());
            prSt.executeUpdate();
            rs = prSt.getGeneratedKeys();
            if(rs.next()){
                insertedID = rs.getInt(1);
                aloqa.setId(insertedID);
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return insertedID;
    }


    public void addBatch(Connection connection, ObservableList<Aloqa> aloqaObservableList) {
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + TEXT + ", "
                + DBHOST + ", "
                + DBPORT + ", "
                + DBUSER + ", "
                + DBPASS + ", "
                + DBNAME + ", "
                + DBPREFIX + ", "
                + USERID +
                ") VALUES(?,?,?,?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert);
            for(Aloqa s6: aloqaObservableList) {
                prSt = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
                prSt.setString(1, s6.getText());
                prSt.setString(2, s6.getDbHost());
                prSt.setString(3, s6.getDbPort());
                prSt.setString(4, s6.getDbUser());
                prSt.setString(5, s6.getDbPass());
                prSt.setString(6, s6.getDbName());
                prSt.setString(7, s6.getDbPrefix());
                prSt.setInt(8, s6.getId());
                prSt.addBatch();
            }
            prSt.executeBatch();
            prSt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void copyDataBatch(Connection connection, ObservableList<Aloqa> aloqaObservableList) {
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + ID_FIELD + ", "
                + TEXT + ", "
                + DBHOST + ", "
                + DBPORT + ", "
                + DBUSER + ", "
                + DBPASS + ", "
                + DBNAME + ", "
                + DBPREFIX + ", "
                + USERID + ", "
                + DATETIME +
                ") VALUES(?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert);
            for(Aloqa s6: aloqaObservableList) {
                prSt.setInt(1, s6.getId());
                prSt.setString(2, s6.getText());
                prSt.setString(3, s6.getDbHost());
                prSt.setString(4, s6.getDbPort());
                prSt.setString(5, s6.getDbUser());
                prSt.setString(6, s6.getDbPass());
                prSt.setString(7, s6.getDbName());
                prSt.setString(8, s6.getDbPrefix());
                prSt.setInt(9, s6.getUserId());
                prSt.setString(10, sdf.format(s6.getDbPrefix()));
                prSt.addBatch();
            }
            prSt.executeBatch();
            prSt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete_data(Connection connection, Aloqa aloqa) {
        String delete = "DELETE FROM " + TABLENAME + " WHERE " + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(delete);
            prSt.setInt(1, aloqa.getId());
            prSt.executeUpdate();
            prSt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void update_data(Connection connection, Aloqa aloqa) {
        String replace = "UPDATE " + TABLENAME + " SET "
                + TEXT + " = ?, "
                + DBHOST + " = ?, "
                + DBPORT + " = ?, "
                + DBUSER + " = ?, "
                + DBPASS + " = ?, "
                + DBNAME + " = ?, "
                + DBPREFIX + " = ? WHERE "
                + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(replace);
            prSt.setString(1, aloqa.getText());
            prSt.setString(2, aloqa.getDbHost());
            prSt.setString(3, aloqa.getDbPort());
            prSt.setString(4, aloqa.getDbUser());
            prSt.setString(5, aloqa.getDbPass());
            prSt.setString(6, aloqa.getDbName());
            prSt.setString(7, aloqa.getDbPrefix());
            prSt.setInt(8, aloqa.getId());
            prSt.executeUpdate();
            prSt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getTABLENAME() {
        return TABLENAME;
    }

    public void setTABLENAME(String TABLENAME) {
        this.TABLENAME = TABLENAME;
    }
}
