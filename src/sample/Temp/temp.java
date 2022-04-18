package sample.Temp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Config.MySqlDBGeneral;
import sample.Data.*;
import sample.Enums.ServerType;
import sample.Model.HisobKitobModels;
import sample.Tools.GetDbData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class temp {
    public static void main(String[] args){
        double d= Double.valueOf("5.00");
        System.out.println(d%1);
    }

    public Map<String, HisobKitob> qoldiqBugunga()  throws SQLException {
        ObservableList<Dona> list = FXCollections.observableArrayList();
        Map<String, HisobKitob> map = new HashMap<>();
        Connection connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        GetDbData.initData(connection);
        LocalDate localDate = LocalDate.now();
        LocalDate localDate1  = localDate.minusMonths(2);
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        Integer hisobId=13;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String localDateString = localDate.format(formatter);
        String select0 = "Select sum(if(hisob2="+hisobId+",dona,-dona)) from hisobkitob where (hisob1="+hisobId+" or hisob2="+hisobId+")  and dateTime<='" + localDateString + " 23:59:59' group by barcode order by tovar;";
        String select = "Select tovar, barcode, sum(if(hisob2="+hisobId+",dona,-dona)) as balance from hisobkitob where tovar>0 and (hisob1="+hisobId+" or hisob2="+hisobId+")  and dateTime<='" + localDateString + " 23:59:59' group by barcode order by tovar;";
        ResultSet rs = hisobKitobModels.getResultSet(connection, select);
        while (rs.next()) {
            HisobKitob hisobKitob = new HisobKitob();
            hisobKitob.setTovar(rs.getInt(1));
            hisobKitob.setBarCode(rs.getString(2));
            hisobKitob.setDona(rs.getDouble(3));
            map.put(hisobKitob.getBarCode(), hisobKitob);
            Dona donaBugun = new Dona(
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getDouble(3)
            );
        }
        ResultSet rs1 = hisobKitobModels.getResultSet(connection, select0);
        while (rs1.next()) {
            HisobKitob hisobKitob = new HisobKitob();
            hisobKitob.setTovar(rs1.getInt(1));
            hisobKitob.setBarCode(rs1.getString(2));
            hisobKitob.setDona(rs1.getDouble(3));
            if (map.containsKey(hisobKitob.getBarCode())) {
            }
            map.put(hisobKitob.getBarCode(), hisobKitob);
        }
        map.forEach((key, hk)->{
            Standart tovar = GetDbData.getTovar(hk.getTovar());
            System.out.println(tovar.getText());
        });
        return map;
    }

    public class Dona {
        Integer id;
        Integer tovar;
        String bcString;
        Double bugun;
        double savdoBugungacha;
        double savdoBuOy;
        double savdoOldingiOy;

        public Dona(Integer tovar, String bcString, Double bugun) {
            this.tovar = tovar;
            this.bcString = bcString;
            this.bugun = bugun;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getTovar() {
            return tovar;
        }

        public void setTovar(Integer tovar) {
            this.tovar = tovar;
        }

        public String getBcString() {
            return bcString;
        }

        public void setBcString(String bcString) {
            this.bcString = bcString;
        }

        public Double getBugun() {
            return bugun;
        }

        public void setBugun(Double bugun) {
            this.bugun = bugun;
        }

        public double getSavdoBugungacha() {
            return savdoBugungacha;
        }

        public void setSavdoBugungacha(double savdoBugungacha) {
            this.savdoBugungacha = savdoBugungacha;
        }

        public double getSavdoBuOy() {
            return savdoBuOy;
        }

        public void setSavdoBuOy(double savdoBuOy) {
            this.savdoBuOy = savdoBuOy;
        }

        public double getSavdoOldingiOy() {
            return savdoOldingiOy;
        }

        public void setSavdoOldingiOy(double savdoOldingiOy) {
            this.savdoOldingiOy = savdoOldingiOy;
        }
    }
}
