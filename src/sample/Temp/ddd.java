package sample.Temp;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Config.MySqlDBGeneral;
import sample.Data.*;
import sample.Enums.ServerType;
import sample.Model.HisobKitobModels;
import sample.Model.Standart3Models;
import sample.Model.StandartModels;
import sample.Tools.GetDbData;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ddd {
    public static void main(String[] args) {
        Connection connection = new MySqlDBGeneral(ServerType.REMOTE).getDbConnection();
        GetDbData.initData(connection);
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        StandartModels standartModels = new StandartModels("Tovar");
        Standart3Models standart3Models = new Standart3Models("Dokonlar");
        ObservableList<Standart> tovarlar = standartModels.get_data(connection);
        ObservableList<Standart3> dokonlar = standart3Models.get_data(connection);
        ObservableList<HisobKitob> chiqimRoyxati = FXCollections.observableArrayList();
        ObservableList<HisobKitob> kirimRoyxati = FXCollections.observableArrayList();
        ObservableList<Hisob> tovarKirimChiqimi = FXCollections.observableArrayList();
        Map<String, Qoldiq> barCodesMap = new HashMap<>();
        ResultSet dokonlarRS = null;
        String dokonlarimiz = "";
        String select = "Select group_concat(ID3, '') FROM DOKONLAR;";
        ResultSet rs = hisobKitobModels.getResultSet(connection, select);

        try{
            while (rs.next()) {
                dokonlarimiz = rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        rs = null;
        select = "select hisob1, hisob2, tovar, barcode, sum(if(hisob2 in(\"+dokonlarimiz+\"), dona, 0)) as adad1, sum(if(hisob2 in("+dokonlarimiz+"), narh*dona/kurs, 0)) as i1, sum(if(hisob1 in(\"+dokonlarimiz+\"), dona, 0)) as adad2, sum(if(hisob1 in("+dokonlarimiz+"), narh*dona/kurs, 0)) as i2 from hisobkitob where tovar>0 and (hisob1 in("+dokonlarimiz+") or hisob2 in("+dokonlarimiz+")) group by barcode order by tovar;";
        rs = hisobKitobModels.getResultSet(connection, select);
        try{
            while (rs.next()) {
                HisobKitob hisobKitob = new HisobKitob();
                hisobKitob.setHisob1(rs.getInt(1));
                hisobKitob.setHisob2(rs.getInt(2));
                hisobKitob.setTovar(rs.getInt(3));
                hisobKitob.setBarCode(rs.getString(4));
                Hisob hisob1 = GetDbData.getHisob(rs.getInt(1));
                Hisob hisob2 = GetDbData.getHisob(rs.getInt(2));
                Standart tovar = GetDbData.getTovar(rs.getInt(3));
                String barcode = rs.getString(4);
                Double kirimDona = rs.getDouble(5);
                Double kirimNarh = rs.getDouble(6);
                Double chiqimDona = rs.getDouble(7);
                Double chiqimNarh = rs.getDouble(8);
                System.out.println(
                        hisob1.getText() + "|" +
                                hisob2.getText() + "|" +
                                tovar.getText() + "|" +
                                barcode + "|" +
                                kirimNarh + "|" +
                                chiqimNarh + "|" +
                                (kirimNarh - chiqimNarh) + "|"
                );
                Qoldiq qoldiq = null;
                if (barCodesMap.containsKey(barcode)) {
                    qoldiq = barCodesMap.get(1);
                } else {
                    qoldiq = new Qoldiq();
                    barCodesMap.put(barcode, qoldiq);
                }
                if (kirimNarh != 0) {
                    qoldiq.setKirimAdad(kirimDona);
                    qoldiq.setKirimNarh(kirimNarh);
                }
                if (chiqimNarh != 0) {
                    qoldiq.setChiqimAdad(chiqimDona);
                    qoldiq.setChiqimNarh(chiqimNarh);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

