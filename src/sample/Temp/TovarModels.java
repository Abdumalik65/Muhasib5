package sample.Temp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Tools.QueryHelper;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TovarModels {
    private final String TABLENAME = "Tovar";
    private final String ID_FIELD = "id";
    private final String TOVAR = "text";
    private final String NDS = "nds";
    private final String JUMLA = "jumla";
    private final String DONA = "dona";
    private final String BIRLIK = "birlik";
    private final String BARCODE = "barCode";
    private final String USERID = "userId";
    private final String DATETIME = "dateTime";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    QueryHelper queryHelper;

    public ObservableList<Tovar> get_data(Connection connection) throws SQLException, ParseException {
        ObservableList<Tovar> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME;
        PreparedStatement prSt = connection.prepareStatement(select);
        rs = prSt.executeQuery();
        while (rs.next()) {
            books.add(new Tovar(
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getDouble(3),
                    rs.getInt(4),
                    rs.getInt(5),
                    rs.getInt(6),
                    rs.getString(7),
                    rs.getInt(8),
                    sdf.parse(rs.getString(9))
            ));
        }
        rs.close();
        return books;
    }

    public ObservableList<Tovar> getAnyData(Connection connection, String sqlWhere, String sqlOrderBy) throws SQLException, ParseException {
        ObservableList<Tovar> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        queryHelper = new QueryHelper(sqlWhere, sqlOrderBy);
        String select = "SELECT * FROM " + TABLENAME + queryHelper.getYakuniyJumla();
        PreparedStatement prSt = connection.prepareStatement(select);
        rs = prSt.executeQuery();
        while (rs.next()) {
            books.add(new Tovar(
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getDouble(3),
                    rs.getInt(4),
                    rs.getInt(5),
                    rs.getInt(6),
                    rs.getString(7),
                    rs.getInt(8),
                    sdf.parse(rs.getString(9))
            ));
        }
        rs.close();
        return books;
    }

    public Integer insert_data(Connection connection, Tovar tovar) throws SQLException {
        Integer insertedID = -1;
        ResultSet rs = null;
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + TOVAR + ", "
                + NDS + ", "
                + JUMLA + ", "
                + DONA + ", "
                + BIRLIK + ", "
                + BARCODE + ", "
                + USERID +
                ") VALUES(?,?,?,?,?,?,?)";
        PreparedStatement prSt = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
        prSt.setString(1, tovar.getText());
        prSt.setDouble(2, tovar.getNds());
        prSt.setInt(3, tovar.getJumla());
        prSt.setInt(4, tovar.getDona());
        prSt.setInt(5, tovar.getBirlik());
        prSt.setString(6, tovar.getBarCode());
        prSt.setInt(7, tovar.getUserId());
        prSt.executeUpdate();
        rs = prSt.getGeneratedKeys();
        if(rs.next()){
            insertedID = rs.getInt(1);
        }
        rs.close();
        return insertedID;
    }

    public void delete_data(Connection connection, Tovar tovar) throws SQLException {
        String delete = "DELETE FROM " + TABLENAME + " WHERE " + ID_FIELD + " = ?";
        PreparedStatement prSt = connection.prepareStatement(delete);
        prSt.setInt(1, tovar.getId());
        prSt.executeUpdate();
    }

    public void deleteAll(Connection connection) throws SQLException {
        String delete = "DELETE FROM " + TABLENAME;
        PreparedStatement prSt = connection.prepareStatement(delete);
        prSt.executeUpdate();
    }

    public void update_data(Connection connection, Tovar tovar) throws SQLException {
        String replace = "UPDATE " + TABLENAME + " SET "
                + TOVAR + " = ?," +
                NDS + " = ?," +
                JUMLA + " =?," +
                DONA + " =?," +
                BIRLIK + " =?," +
                BARCODE + " =?" +
                " WHERE "
                + ID_FIELD + " = ?";
        PreparedStatement prSt = connection.prepareStatement(replace);
        prSt.setString(1, tovar.getText());
        prSt.setDouble(2, tovar.getNds());
        prSt.setInt(3, tovar.getJumla());
        prSt.setInt(4, tovar.getDona());
        prSt.setInt(5, tovar.getBirlik());
        prSt.setString(6, tovar.getBarCode());
        prSt.setInt(7, tovar.getId());
        prSt.executeUpdate();
    }
}
