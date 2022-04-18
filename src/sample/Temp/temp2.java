package sample.Temp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Config.MySqlDBGeneral;
import sample.Data.HisobKitob;
import sample.Data.Standart;
import sample.Enums.ServerType;
import sample.Model.HisobKitobModels;
import sample.Tools.Alerts;
import sample.Tools.GetDbData;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class temp2 {
    public static void main(String[] args) throws ParseException {
        Connection connection =
                new MySqlDBGeneral(ServerType.REMOTE).getDbConnection();
        Map<Integer, Integer> yillar = new HashMap<>();
        Map<Integer, Integer> oylar = new HashMap<>();
        Map<Integer, Integer> kunlar = new HashMap<>();
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String select = "select distinct date(datetime) from hisobkitob group by (datetime) order by datetime ;";
        ResultSet rs = hisobKitobModels.getResultSet(connection, select);
        ObservableList<Timestamp> timestampObservableList = FXCollections.observableArrayList();
        try {
            while (rs.next()) {
                Timestamp timestamp = rs.getTimestamp(1);
                timestampObservableList.add(timestamp);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void printHisobKitob(HisobKitob hk) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        System.out.println(
                "|" +
                        hk.getId() + "|" +
                        GetDbData.getHisob(hk.getHisob1()) + "|" +
                        GetDbData.getHisob(hk.getHisob2()) + "|" +
                        GetDbData.getTovar(hk.getTovar()) + "|" +
                        hk.getBarCode() + "|" +
                        hk.getDona() + "|" +
                        hk.getNarh() + "|" +
                        hk.getManba() + "|  " +
                        sdf.format(hk.getDateTime()) + "  |"
        );
    }

}
