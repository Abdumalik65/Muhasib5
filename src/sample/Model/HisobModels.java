package sample.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Data.Standart3;
import sample.Tools.Alerts;
import sample.Tools.QueryHelper;
import sample.Data.Hisob;
import java.util.Date;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class HisobModels {
    private String TABLENAME = "Hisob";
    private final String ID_FIELD = "id";
    private final String HISOB = "text";
    private final String BALANS = "balans";
    private final String RASM = "rasm";
    private final String CHECK = "check";
    private final String EMAIL = "email";
    private final String MOBILE = "mobile";
    private final String USERID = "userId";
    private final String DATETIME = "dateTime";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    QueryHelper queryHelper;

    public HisobModels() {}

    public HisobModels(String TABLENAME) {
        this.TABLENAME = TABLENAME;
    }

    public ObservableList<Hisob> get_data(Connection connection) {
        ObservableList<Hisob> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME;
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new Hisob(rs.getInt(1),
                        rs.getString(2),
                        rs.getDouble(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getInt(7),
                        sdf.parse(rs.getString(8))
                ));
            }
        } catch (SQLException e) {
            Alerts.losted();
        } catch (ParseException e) {
            Alerts.parseError();
        }
        return books;
    }

    public ObservableList<Hisob> get_data1(Connection connection) {
        ObservableList<Hisob> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME;
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new Hisob(rs.getInt(1),
                        rs.getString(2),
                        0d,
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getInt(7),
                        sdf.parse(rs.getString(8))
                ));
            }

            HisobKitobModels hisobKitobModels = new HisobKitobModels();
            String select1 = "select hisob1, sum(if(tovar>0, dona*narh/kurs, narh/kurs)) as summa from Muhasib.HisobKitob group by hisob1";
            String select2 = "select hisob2, sum(if(tovar>0, dona*narh/kurs, narh/kurs)) as summa from Muhasib.HisobKitob group by hisob2";
            ResultSet resultSet1 = hisobKitobModels.getResultSet(connection, select1);
            ResultSet resultSet2 = hisobKitobModels.getResultSet(connection, select2);
            while (resultSet1.next()) {
                Integer id = resultSet1.getInt(1);
                for (Hisob h: books) {
                    if (id.equals(h.getId())) {
                        h.setChiqim(resultSet1.getDouble(2));
                        break;
                    }
                }
            }

            while (resultSet2.next()) {
                Integer id = resultSet2.getInt(1);
                for (Hisob h: books) {
                    if (id.equals(h.getId())) {
                        h.setKirim(resultSet2.getDouble(2));
                        break;
                    }
                }
            }
            for (Hisob h: books) {
                if (h.getKirim()==null) {
                    h.setKirim(0d);
                }
                if (h.getChiqim()==null) {
                    h.setChiqim(0d);
                }
                h.setBalans(h.getKirim() - h.getChiqim());
            }
            rs.close();
            resultSet1.close();
            resultSet2.close();
            prSt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            Alerts.parseError();
        }
        return books;
    }

    public ObservableList<Hisob> get_data1(Connection connection, Date date) {
        ObservableList<Hisob> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        if (date != null) {
            date = new Date();
        }
        String select = "SELECT * FROM " + TABLENAME;
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new Hisob(rs.getInt(1),
                        rs.getString(2),
                        0d,
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getInt(7),
                        sdf.parse(rs.getString(8))
                ));
            }

            HisobKitobModels hisobKitobModels = new HisobKitobModels();
            String select1 = "select hisob1, sum(if(tovar>0, dona*narh/kurs, narh/kurs)) as summa from Muhasib.HisobKitob WHERE  dateTime <= ? group by hisob1";
            String select2 = "select hisob2, sum(if(tovar>0, dona*narh/kurs, narh/kurs)) as summa from Muhasib.HisobKitob WHERE dateTime <= ? group by hisob2";
            ResultSet resultSet1 = hisobKitobModels.getResultSet(connection, select1);
            ResultSet resultSet2 = hisobKitobModels.getResultSet(connection, select2);
            prSt.setString(1, sdf.format(date));
            while (resultSet1.next()) {
                Integer id = resultSet1.getInt(1);
                for (Hisob h: books) {
                    if (id.equals(h.getId())) {
                        h.setChiqim(resultSet1.getDouble(2));
                        break;
                    }
                }
            }

            while (resultSet2.next()) {
                Integer id = resultSet2.getInt(1);
                for (Hisob h: books) {
                    if (id.equals(h.getId())) {
                        h.setKirim(resultSet2.getDouble(2));
                        break;
                    }
                }
            }
            for (Hisob h: books) {
                if (h.getKirim()==null) {
                    h.setKirim(0d);
                }
                if (h.getChiqim()==null) {
                    h.setChiqim(0d);
                }
                h.setBalans(h.getKirim() - h.getChiqim());
            }
            rs.close();
            resultSet1.close();
            resultSet2.close();
            prSt.close();
        } catch (SQLException e) {
            e.printStackTrace();
//            Alerts.losted();
        } catch (ParseException e) {
            e.printStackTrace();
//            Alerts.parseError();
        }
        return books;
    }

    public ObservableList<Hisob> getAnyData(Connection connection, String sqlWhere, String sqlOrderBy) {
        ObservableList<Hisob> books = FXCollections.observableArrayList();
        ResultSet rs = null;
        queryHelper = new QueryHelper(sqlWhere, sqlOrderBy);
        String select = "SELECT * FROM " + TABLENAME + queryHelper.getYakuniyJumla();
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new Hisob(rs.getInt(1),
                        rs.getString(2),
                        rs.getDouble(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getInt(7),
                        sdf.parse(rs.getString(8))
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

    public Hisob getHisob(Connection connection, Integer hisobId) {
        ObservableList<Hisob> books = FXCollections.observableArrayList();
        Hisob hisob = null;
        ResultSet rs = null;
        String select = "SELECT * FROM " + TABLENAME + " WHERE " + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(select);
            prSt.setInt(1, hisobId);
            rs = prSt.executeQuery();
            while (rs.next()) {
                books.add(new Hisob(rs.getInt(1),
                        rs.getString(2),
                        rs.getDouble(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getInt(7),
                        sdf.parse(rs.getString(8))
                ));
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
           Alerts.losted();
        } catch (ParseException e) {
            Alerts.parseError();
        }
        if (books.size()>0) {
            hisob = books.get(0);
        }
        return hisob;
    }

    public Integer insert_data(Connection connection, Hisob hisob) {
        Integer insertedID = -1;
        ResultSet rs = null;
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + HISOB + ", "
                + BALANS + ", "
                + RASM + ", "
                + EMAIL + ", "
                + MOBILE + ", "
                + USERID +
                ") VALUES(?,?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            prSt.setString(1, hisob.getText());
            prSt.setDouble(2, hisob.getBalans());
            prSt.setString(3, hisob.getRasm());
            prSt.setString(4, hisob.getEmail());
            prSt.setString(5, hisob.getMobile());
            prSt.setInt(6, hisob.getUserId());
            prSt.executeUpdate();
            rs = prSt.getGeneratedKeys();
            if(rs.next()) {
                insertedID = rs.getInt(1);
                hisob.setId(insertedID);
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
        return insertedID;
    }

    public Integer insert_dataID(Connection connection, Hisob hisob) {
        Integer insertedID = -1;
        ResultSet rs = null;
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + ID_FIELD + ", "
                + HISOB + ", "
                + BALANS + ", "
                + RASM + ", "
                + EMAIL + ", "
                + MOBILE + ", "
                + USERID +
                ") VALUES(?,?,?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            prSt.setInt(1, hisob.getId());
            prSt.setString(2, hisob.getText());
            prSt.setDouble(3, hisob.getBalans());
            prSt.setString(4, hisob.getRasm());
            prSt.setString(5, hisob.getEmail());
            prSt.setString(6, hisob.getMobile());
            prSt.setInt(7, hisob.getUserId());
            prSt.executeUpdate();
            rs = prSt.getGeneratedKeys();
            if(rs.next()) {
                insertedID = rs.getInt(1);
                hisob.setId(insertedID);
            }
            rs.close();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
        return insertedID;
    }

    public void copyDataBatch(Connection connection, ObservableList<Hisob> hisobList) {
        Integer insertedID = -1;
        String insert = "INSERT INTO "
                + TABLENAME + " ("
                + ID_FIELD + ", "
                + HISOB + ", "
                + BALANS + ", "
                + RASM + ", "
                + EMAIL + ", "
                + MOBILE + ", "
                + USERID + ", "
                + DATETIME +
                ") VALUES(?,?,?,?,?,?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(insert);
            for (Hisob hisob: hisobList) {
                prSt.setInt(1, hisob.getId());
                prSt.setString(2, hisob.getText());
                prSt.setDouble(3, hisob.getBalans());
                prSt.setString(4, hisob.getRasm());
                prSt.setString(5, hisob.getEmail());
                prSt.setString(6, hisob.getMobile());
                prSt.setInt(7, hisob.getUserId());
                prSt.setString(8, sdf.format(hisob.getDateTime()));
                prSt.addBatch();
            }
            prSt.executeBatch();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();
        }
    }

    public void delete_data(Connection connection, Hisob hisob) {
        String delete = "DELETE FROM " + TABLENAME + " WHERE " + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(delete);
            prSt.setInt(1, hisob.getId());
            prSt.executeUpdate();
            prSt.close();
        } catch (SQLException e) {
           Alerts.losted();
        }
    }

    public void deleteBatch(Connection connection, ObservableList<Hisob> hisobObservableList) {
        String delete = "DELETE FROM " + TABLENAME + " WHERE " + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(delete);
            for (Hisob h: hisobObservableList) {
                prSt.setInt(1, h.getId());
                prSt.addBatch();
            }
            prSt.executeBatch();
            prSt.close();
        } catch (SQLException e) {
            Alerts.losted();;
        }
    }

    public void update_data(Connection connection, Hisob hisob) {
        String replace = "UPDATE " + TABLENAME + " SET "
                + HISOB + " = ?,"
                + BALANS + " = ?,"
                + RASM + " = ?,"
                + EMAIL + " = ?,"
                + MOBILE + " = ? WHERE "
                + ID_FIELD + " = ?";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(replace);
            prSt.setString(1, hisob.getText());
            prSt.setDouble(2, hisob.getBalans());
            prSt.setString(3, hisob.getRasm());
            prSt.setString(4, hisob.getEmail());
            prSt.setString(5, hisob.getMobile());
            prSt.setInt(6, hisob.getId());
            prSt.executeUpdate();
            prSt.close();
        } catch (SQLException e) {
           Alerts.losted();
        }
    }

    public void setTABLENAME(String TABLENAME) {
        this.TABLENAME = TABLENAME;
    }
}
