package sample.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Data.BarCode;
import sample.Tools.Alerts;
import sample.Tools.QueryHelper;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Observable;

public class BarCodeModels {
    private String TABLENAME = "BarCodes";
    private final String ID_FIELD = "id";
    private final String TOVAR = "tovar";
    private final String BARCODE = "barCode";
    private final String BIRLIK = "birlik";
    private final String ADAD = "adad";
    private final String TARKIB = "tarkib";
    private final String VAZN = "vazn";
    private final String HAJM = "hajm";
    private final String USERID = "userId";
    private final String DATETIME = "dateTime";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    QueryHelper queryHelper;

    public ObservableList<BarCode> get_data(Connection connection) {
        ObservableList<BarCode> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME;
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new BarCode(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getInt(4),
                        rs.getDouble(5),
                        rs.getInt(6),
                        rs.getDouble(7),
                        rs.getDouble(8),
                        rs.getInt(9),
                        sdf.parse(rs.getString(10))
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

    public ObservableList<BarCode> getDistinct(Connection connection) {
        ObservableList<BarCode> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT DISTINCT "+BARCODE+" FROM " + TABLENAME;
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            int colIndex = 0;
            while (rs.next()) {
                colIndex++;
                books.add(new BarCode(
                        colIndex,
                        0,
                        rs.getString(1),
                        0,
                        .0,
                        0,
                        .0,
                        .0,
                        1,
                        null)
                );
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
        return books;
    }

    public ObservableList<BarCode> getAnyData(Connection connection, String sqlWhere, String sqlOrderBy){
        ObservableList<BarCode> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        queryHelper = new QueryHelper(sqlWhere, sqlOrderBy);
        String select = "SELECT * FROM " + TABLENAME + queryHelper.getYakuniyJumla();
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new BarCode(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getInt(4),
                        rs.getDouble(5),
                        rs.getInt(6),
                        rs.getDouble(7),
                        rs.getDouble(8),
                        rs.getInt(9),
                        sdf.parse(rs.getString(10))
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

    public BarCode getBarCode(Connection connection, String barCodeString){
        BarCode barCode = null;
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME + " WHERE UPPER(" + BARCODE + ") = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setString(1, barCodeString.toUpperCase());
            rs = prSt.executeQuery();
            while (rs.next()) {
                barCode = new BarCode(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getInt(4),
                        rs.getDouble(5),
                        rs.getInt(6),
                        rs.getDouble(7),
                        rs.getDouble(8),
                        rs.getInt(9),
                        sdf.parse(rs.getString(10))
                );
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        } catch (ParseException e) {
            Alerts.parseError();
        }
        return barCode;
    }

    public BarCode getBarCode(Connection connection, Integer barCodeId){
        BarCode barCode = null;
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME + " WHERE UPPER(" + BARCODE + ") = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setInt(1, barCodeId);
            rs = prSt.executeQuery();
            while (rs.next()) {
                barCode = new BarCode(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getInt(4),
                        rs.getDouble(5),
                        rs.getInt(6),
                        rs.getDouble(7),
                        rs.getDouble(8),
                        rs.getInt(9),
                        sdf.parse(rs.getString(10))
                );
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        } catch (ParseException e) {
            Alerts.parseError();
        }
        return barCode;
    }

    public Integer getRecordsCount(Connection connection) {
        Integer recordsCount = 0;
        ResultSet rs = null;
        String select = "SELECT COUNT(*) FROM " + TABLENAME;
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            while (rs.next()) {
                recordsCount = rs.getInt(1);
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
        return recordsCount;
    }


    public Integer insert_data(Connection connection, BarCode barCode) {
        Integer insertedID = -1;
        ResultSet rs = null;
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + TOVAR + ", "
                + BARCODE + ", "
                + BIRLIK + ", "
                + ADAD + ", "
                + TARKIB + ", "
                + VAZN + ", "
                + HAJM + ", "
                + USERID +
                ") VALUES(?,?,?,?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            prSt.setInt(1, barCode.getTovar());
            prSt.setString(2, barCode.getBarCode());
            prSt.setInt(3, barCode.getBirlik());
            prSt.setDouble(4, barCode.getAdad());
            prSt.setDouble(5, barCode.getTarkib());
            prSt.setDouble(6, barCode.getVazn());
            prSt.setDouble(7, barCode.getHajm());
            prSt.setInt(8, barCode.getUserId());
            prSt.executeUpdate();
            rs = prSt.getGeneratedKeys();
            if(rs.next()){
                insertedID = rs.getInt(1);
                barCode.setId(insertedID);
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
        return insertedID;
    }

    public Integer insert_data2(Connection connection, BarCode barCode) {
        Integer insertedID = -1;
        ResultSet rs = null;
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + ID_FIELD + ", "
                + TOVAR + ", "
                + BARCODE + ", "
                + BIRLIK + ", "
                + ADAD + ", "
                + TARKIB + ", "
                + VAZN + ", "
                + HAJM + ", "
                + USERID +
                ") VALUES(?,?,?,?,?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            prSt.setInt(1, barCode.getId());
            prSt.setInt(2, barCode.getTovar());
            prSt.setString(3, barCode.getBarCode());
            prSt.setInt(4, barCode.getBirlik());
            prSt.setDouble(5, barCode.getAdad());
            prSt.setDouble(6, barCode.getTarkib());
            prSt.setDouble(7, barCode.getVazn());
            prSt.setDouble(8, barCode.getHajm());
            prSt.setInt(9, barCode.getUserId());
            prSt.executeUpdate();
            rs = prSt.getGeneratedKeys();
            if(rs.next()){
                insertedID = rs.getInt(1);
                barCode.setId(insertedID);
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
        return insertedID;
    }

    public void copyDataBatch(Connection connection, ObservableList<BarCode> barcodeList) {
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + ID_FIELD + ", "
                + TOVAR + ", "
                + BARCODE + ", "
                + BIRLIK + ", "
                + ADAD + ", "
                + TARKIB + ", "
                + VAZN + ", "
                + HAJM + ", "
                + USERID + ", "
                + DATETIME +
                ") VALUES(?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert);
            for (BarCode barCode: barcodeList) {
                prSt.setInt(1, barCode.getId());
                prSt.setInt(2, barCode.getTovar());
                prSt.setString(3, barCode.getBarCode());
                prSt.setInt(4, barCode.getBirlik());
                prSt.setDouble(5, barCode.getAdad());
                prSt.setDouble(6, barCode.getTarkib());
                prSt.setDouble(7, barCode.getVazn());
                prSt.setDouble(8, barCode.getHajm());
                prSt.setInt(9, barCode.getUserId());
                prSt.setString(10, sdf.format(barCode.getDateTime()));
                prSt.addBatch();
            }
            prSt.executeBatch();
            prSt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void delete_data(Connection connection, BarCode barCode) {
        String delete = "DELETE FROM " + TABLENAME + " WHERE " + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(delete);
            prSt.setInt(1, barCode.getId());
            prSt.executeUpdate();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
    }
    public void update_data(Connection connection, BarCode barCode){
        String replace = "UPDATE " + TABLENAME + " SET "
                + TOVAR + " = ?, "
                + BARCODE + " = ?, "
                + BIRLIK + " = ?, "
                + ADAD + " = ?, "
                + TARKIB + " = ?, "
                + VAZN + " = ?, "
                + HAJM + " = ? WHERE "
                + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(replace);
            prSt.setInt(1, barCode.getTovar());
            prSt.setString(2, barCode.getBarCode());
            prSt.setInt(3, barCode.getBirlik());
            prSt.setDouble(4, barCode.getAdad());
            prSt.setDouble(5, barCode.getTarkib());
            prSt.setDouble(6, barCode.getVazn());
            prSt.setDouble(7, barCode.getHajm());
            prSt.setInt(8, barCode.getId());
            prSt.executeUpdate();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
    }
}
