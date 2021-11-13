package sample.Temp;

import com.itextpdf.awt.geom.misc.HashCode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Config.MySqlDB;
import sample.Config.MySqlDBGeneral;
import sample.Data.*;
import sample.Model.HisobKitobModels;
import sample.Model.HisobModels;
import sample.Model.QaydnomaModel;
import sample.Model.Standart3Models;
import sample.Tools.GetDbData;
import sample.Tools.MoneyShow;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;

public class FOYDA {
    public static void main(String[] args) throws Exception {
        DecimalFormat decimalFormat = new MoneyShow();
        Connection connection = new MySqlDB().getDbConnection();
        GetDbData.initData(connection);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.now();
        String localDateString = localDate.format(formatter);
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        ObservableList<HisobKitob> hisobKitobObservableList = FXCollections.observableArrayList();
        ObservableList<Hisob> hisobObservableList = FXCollections.observableArrayList();
        String kirimHisoblari =
                "Select hisob2, sum(narh/kurs) from HisobKitob where tovar=0 and valuta>0 and dateTime<='" + localDateString + " 23:59:59' group by hisob2 order by hisob2";
        ResultSet rs1 = hisobKitobModels.getResultSet(connection, kirimHisoblari);
        try {
            while (rs1.next()) {
                Integer id = rs1.getInt(1);
                Double balance = rs1.getDouble(2);
                Hisob hisob = GetDbData.getHisob(id);
                hisobObservableList.add(new Hisob(id, hisob.getText(), 0d, 0d, balance));
            }
            rs1.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String chiqimHisoblari =
                "Select hisob1, sum(narh/kurs) from HisobKitob where tovar=0 and valuta>0 and dateTime<='" + localDateString + " 23:59:59' group by valuta order by hisob1";
        ResultSet rs2 = hisobKitobModels.getResultSet(connection, chiqimHisoblari);
        try {
            while (rs2.next()) {
                Integer id = rs2.getInt(1);
                Double balance = rs2.getDouble(2);
                Hisob hisob = GetDbData.hisobniTop(id, hisobObservableList);
                if (hisob != null) {
                    hisob.setBalans(hisob.getBalans() - balance);
                } else {
                    Hisob h = GetDbData.getHisob(id);
                    hisobObservableList.add(new Hisob(id, h.getText(), 0d, 0d, balance));
                }
            }
            rs2.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (hisobObservableList.size()>0) {
            Comparator<Hisob> comparator = Comparator.comparing(Hisob::getId);
            Collections.sort(hisobObservableList, comparator);
        }
        hisobObservableList.removeIf(hisob -> decimalFormat.format(hisob.getBalans()).equals(0));
        for (Hisob hisob: hisobObservableList) {
            System.out.println("|" + hisob.getText() + "|" + decimalFormat.format(hisob.getBalans()));
        }
    }
}
