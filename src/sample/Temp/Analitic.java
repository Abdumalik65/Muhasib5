package sample.Temp;

import sample.Data.HisobKitob;
import sample.Data.Standart;
import sample.Model.HisobKitobModels;
import sample.Tools.GetDbData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Analitic {
    Connection connection;

    public Analitic(Connection connection) {
        this.connection = connection;
        GetDbData.initData(connection);
    }

    public Map<String, HisobKitob> getQoldiq(LocalDate localDate, Integer hisobId) {
        Map<String, HisobKitob> map = new HashMap<>();
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String localDateString = localDate.format(formatter);
        String select = "Select tovar, barcode, sum(if(hisob2="+hisobId+",dona,-dona)) as balance from hisobkitob where tovar>0 and (hisob1="+hisobId+" or hisob2="+hisobId+")  and dateTime<='" + localDateString + " 23:59:59' group by barcode order by tovar;";
        ResultSet rs = hisobKitobModels.getResultSet(connection, select);
        try {
            while (rs.next()) {
                HisobKitob hisobKitob = new HisobKitob();
                hisobKitob.setTovar(rs.getInt(1));
                hisobKitob.setBarCode(rs.getString(2));
                hisobKitob.setDona(rs.getDouble(3));
                map.put(hisobKitob.getBarCode(), hisobKitob);
            }
            map.forEach((key, hk)->{
                Standart tovar = GetDbData.getTovar(hk.getTovar());
                System.out.println(tovar.getText() + "|" + hk.getDona() + "|");
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    public Map<String, HisobKitob> getSotilgan(LocalDate localDate, LocalDate localDate1, Integer hisobId) {
        Map<String, HisobKitob> map = new HashMap<>();
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String localDateString = localDate.format(formatter) + " 23:59:59";
        String localDateString2 = localDate1.format(formatter) + " 00:00:00";
        String select = "Select barcode, sum(dona) from muhasib.hisobkitob where manba>0 and hisob1=" + hisobId + " and dateTime BETWEEN '" + localDateString2 + "' and '"+localDateString+"' group by barcode order by barcode;";
        ResultSet rs = hisobKitobModels.getResultSet(connection, select);
        try {
            while (rs.next()) {
                HisobKitob hisobKitob = new HisobKitob();
                hisobKitob.setBarCode(rs.getString(1));
                hisobKitob.setDona(rs.getDouble(2));
                System.out.println(hisobKitob.getBarCode() + "|" + hisobKitob.getDona() + "|");
                map.put(hisobKitob.getBarCode(), hisobKitob);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    public void getAnalitic(LocalDate localDate, Integer hisobId) {}
}
