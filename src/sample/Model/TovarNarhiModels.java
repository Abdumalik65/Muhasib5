package sample.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Data.*;
import sample.Tools.Alerts;
import sample.Tools.GetDbData;
import sample.Tools.QueryHelper;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TovarNarhiModels {
    private final String TABLENAME = "TovarNarhi";
    private final String ID_FIELD = "id";
    private final String SANA = "sana";
    private final String TOVAR = "tovar";
    private final String NARHTURI = "narhTuri";
    private final String VALUTA = "valuta";
    private final String KURS = "kurs";
    private final String NARH = "narh";
    private final String USERID = "userId";
    private final String DATETIME = "dateTime";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    QueryHelper queryHelper;

    public ObservableList<TovarNarhi> get_data(Connection connection) {
        ObservableList<TovarNarhi> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME;
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new TovarNarhi(
                        rs.getInt(1),
                        sdf.parse(rs.getString(2)),
                        rs.getInt(3),
                        rs.getInt(4),
                        rs.getInt(5),
                        rs.getDouble(6),
                        rs.getDouble(7),
                        rs.getInt(8),
                        sdf.parse(rs.getString(9))
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

    public ObservableList<TovarNarhi> getAnyData(Connection connection, String sqlWhere, String sqlOrderBy) {
        ObservableList<TovarNarhi> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        queryHelper = new QueryHelper(sqlWhere, sqlOrderBy);
        String select = "SELECT * FROM " + TABLENAME + queryHelper.getYakuniyJumla();
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new TovarNarhi(
                        rs.getInt(1),
                        sdf.parse(rs.getString(2)),
                        rs.getInt(3),
                        rs.getInt(4),
                        rs.getInt(5),
                        rs.getDouble(6),
                        rs.getDouble(7),
                        rs.getInt(8),
                        sdf.parse(rs.getString(9))
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

    public ObservableList<TovarNarhi> getDate(Connection connection, Integer tovarId, Date date) {
        ObservableList<TovarNarhi> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME + " WHERE tovar = ? AND sana = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setInt(1, tovarId);
            prSt.setString(2, sdf.format(date));
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new TovarNarhi(
                        rs.getInt(1),
                        sdf.parse(rs.getString(2)),
                        rs.getInt(3),
                        rs.getInt(4),
                        rs.getInt(5),
                        rs.getDouble(6),
                        rs.getDouble(7),
                        rs.getInt(8),
                        sdf.parse(rs.getString(9))
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

    public ObservableList<TovarNarhi> getDate2(Connection connection, Integer tovarId, Date date, String sqlOrderBy) {
        ObservableList<TovarNarhi> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME + " WHERE tovar = ? AND sana <= ?  ORDER BY " + sqlOrderBy;
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setInt(1, tovarId);
            prSt.setString(2, sdf.format(date));
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new TovarNarhi(
                        rs.getInt(1),
                        sdf.parse(rs.getString(2)),
                        rs.getInt(3),
                        rs.getInt(4),
                        rs.getInt(5),
                        rs.getDouble(6),
                        rs.getDouble(7),
                        rs.getInt(8),
                        sdf.parse(rs.getString(9))
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

    public ObservableList<TovarNarhi> getDate3(Connection connection, Integer tovarId, Integer narhTuri, Date date, String sqlOrderBy) {
        ObservableList<TovarNarhi> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME + " WHERE tovar = ? AND  narhTuri = ? AND sana <= ?  ORDER BY " + sqlOrderBy;
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setInt(1, tovarId);
            prSt.setInt(2, narhTuri);
            prSt.setString(3, sdf.format(date));
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new TovarNarhi(
                        rs.getInt(1),
                        sdf.parse(rs.getString(2)),
                        rs.getInt(3),
                        rs.getInt(4),
                        rs.getInt(5),
                        rs.getDouble(6),
                        rs.getDouble(7),
                        rs.getInt(8),
                        sdf.parse(rs.getString(9))
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
    public Integer insert_data(Connection connection, TovarNarhi tovarNarhi) {
        Integer insertedID = -1;
        ResultSet rs = null;
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + SANA + ", "
                + TOVAR + ", "
                + NARHTURI + ", "
                + VALUTA + ", "
                + KURS + ", "
                + NARH + ", "
                + USERID +
                ") VALUES(?,?,?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            prSt.setString(1, sdf.format(tovarNarhi.getSana()));
            prSt.setInt(2, tovarNarhi.getTovar());
            prSt.setInt(3, tovarNarhi.getNarhTuri());
            prSt.setInt(4, tovarNarhi.getValuta());
            prSt.setDouble(5, tovarNarhi.getKurs());
            prSt.setDouble(6, tovarNarhi.getNarh());
            prSt.setInt(7, tovarNarhi.getUserId());
            prSt.executeUpdate();
            rs = prSt.getGeneratedKeys();
            if(rs.next()){
                insertedID = rs.getInt(1);
                tovarNarhi.setId(insertedID);
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();;
        }
        return insertedID;
    }

    public Integer addBath(Connection connection, ObservableList<TovarNarhi> tovarNarhiObservableList) {
        Integer insertedID = -1;
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + SANA + ", "
                + TOVAR + ", "
                + NARHTURI + ", "
                + VALUTA + ", "
                + KURS + ", "
                + NARH + ", "
                + USERID +
                ") VALUES(?,?,?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert);
            for (TovarNarhi tn: tovarNarhiObservableList) {
                prSt.setString(1, sdf.format(tn.getSana()));
                prSt.setInt(2, tn.getTovar());
                prSt.setInt(3, tn.getNarhTuri());
                prSt.setInt(4, tn.getValuta());
                prSt.setDouble(5, tn.getKurs());
                prSt.setDouble(6, tn.getNarh());
                prSt.setInt(7, tn.getUserId());
                prSt.addBatch();
            }
            prSt.executeBatch();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();;
        }
        return insertedID;
    }
    public void copyDataBatch(Connection connection, ObservableList<TovarNarhi> tovarNarhiObservableList) {
        Integer insertedID = -1;
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + ID_FIELD + ", "
                + SANA + ", "
                + TOVAR + ", "
                + NARHTURI + ", "
                + VALUTA + ", "
                + KURS + ", "
                + NARH + ", "
                + USERID + ", "
                + DATETIME +
                ") VALUES(?,?,?,?,?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert);
            for (TovarNarhi tn: tovarNarhiObservableList) {
                prSt.setInt(1, tn.getId());
                prSt.setString(2, sdf.format(tn.getSana()));
                prSt.setInt(3, tn.getTovar());
                prSt.setInt(4, tn.getNarhTuri());
                prSt.setInt(5, tn.getValuta());
                prSt.setDouble(6, tn.getKurs());
                prSt.setDouble(7, tn.getNarh());
                prSt.setInt(8, tn.getUserId());
                prSt.setString(9, sdf.format(tn.getDateTime()));
                prSt.addBatch();
            }
            prSt.executeBatch();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();;
        }
    }

    public void delete_data(Connection connection, TovarNarhi tovarNarhi) {
        String delete = "DELETE FROM " + TABLENAME + " WHERE " + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(delete);
            prSt.setInt(1, tovarNarhi.getId());
            prSt.executeUpdate();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();;
        }
    }
    public void update_data(Connection connection, TovarNarhi tovarNarhi) {
        String replace = "UPDATE " + TABLENAME + " SET "
                + SANA + " = ?, "
                + TOVAR + " = ?, "
                + NARHTURI + " = ?, "
                + VALUTA + " = ?, "
                + KURS + " = ?, "
                + NARH + " = ? WHERE "
                + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(replace);
            prSt.setString(1, sdf.format(tovarNarhi.getSana()));
            prSt.setInt(2, tovarNarhi.getTovar());
            prSt.setInt(3, tovarNarhi.getNarhTuri());
            prSt.setInt(4, tovarNarhi.getValuta());
            prSt.setDouble(5, tovarNarhi.getKurs());
            prSt.setDouble(6, tovarNarhi.getNarh());
            prSt.setInt(7, tovarNarhi.getId());
            prSt.executeUpdate();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();;
        }
    }

    public Double tovarNarhiniOl(Connection connection, BarCode barCode, Integer narhTuri) {
        Double tovarNarhiDouble = null;
        if (barCode != null) {
            Standart3Models standart3Models = new Standart3Models();
            standart3Models.setTABLENAME("TGuruh2");
            ObservableList<Standart3> s3List = standart3Models.getAnyData(connection, "id3 = " + barCode.getTovar(), "");
            if (s3List.size() > 0) {
                Standart3 s3 = s3List.get(0);
                if (s3.getId2() != null) {
                    Standart6Models standart6Models = new Standart6Models("TGuruh1");
                    ObservableList<Standart6> s6List = standart6Models.getAnyData(connection, "id = " + s3.getId2(), "");
                    if (s6List.size() > 0) {
                        Standart6 s6 = s6List.get(0);
                        switch (narhTuri) {
                            case 0:
                                tovarNarhiDouble = s6.getNarh();
                                break;
                            case 1:
                                tovarNarhiDouble = s6.getChakana();
                                break;
                            case 2:
                                tovarNarhiDouble = s6.getUlgurji();
                                break;
                            case 3:
                                tovarNarhiDouble = s6.getBoj();
                                break;
                            case 4:
                                tovarNarhiDouble = s6.getNds();
                                break;
                        }
                    }
                }
            }
        }
        if (tovarNarhiDouble == null) {
            TovarNarhi tovarNarhi = null;
            Integer tovarId = barCode.getTovar();
            ObservableList<TovarNarhi> observableList = getAnyData(
                    connection, "tovar = " + tovarId + " AND narhTuri = " + narhTuri, "sana desc"
            );
            if (observableList.size()>0) {
                tovarNarhi = observableList.get(0);
            }
        }
        return tovarNarhiDouble;
    }

    private TovarNarhi yakkaTovarNarhi(Connection connection, int tovarId, int narhTuri) {
        TovarNarhi tovarNarhi = null;
        ObservableList<TovarNarhi> observableList = getAnyData(
                connection, "tovar = " + tovarId + " AND narhTuri = " + narhTuri, "sana desc"
        );
        if (observableList.size()>0) {
            tovarNarhi = observableList.get(0);
        }
        return tovarNarhi;
    }
}