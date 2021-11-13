package sample.Temp;

import javafx.collections.ObservableList;
import sample.Config.MySqlDB;
import sample.Config.MySqlDBGeneral;
import sample.Data.Hisob;
import sample.Data.HisobKitob;
import sample.Data.QaydnomaData;
import sample.Model.HisobKitobModels;
import sample.Model.HisobModels;
import sample.Model.QaydnomaModel;
import sample.Tools.MoneyShow;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class temp {
    public static void main(String[] args) throws SQLException {
        DecimalFormat decimalFormat = new MoneyShow();
        Connection connection = new MySqlDB().getDbConnection();
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        HisobModels hisobModels = new HisobModels();
        ObservableList<Hisob> hisobObservableList = hisobModels.get_data(connection);
        for (Hisob h: hisobObservableList) {
            h.setKirim(0d);
            h.setChiqim(0d);
            h.setBalans(0d);
            hisobModels.update_data(connection, h);
        }
        String select1 = "select hisob1, sum(if(tovar>0, dona*narh/kurs, narh/kurs)) as summa from Muhasib.HisobKitob group by hisob1";
        String select2 = "select hisob2, sum(if(tovar>0, dona*narh/kurs, narh/kurs)) as summa from Muhasib.HisobKitob group by hisob2";
        ResultSet resultSet1 = hisobKitobModels.getResultSet(connection, select1);
        ResultSet resultSet2 = hisobKitobModels.getResultSet(connection, select2);
        while (resultSet1.next()) {
            Integer id = resultSet1.getInt(1);
            for (Hisob h: hisobObservableList) {
                if (id.equals(h.getId())) {
                    h.setChiqim(resultSet1.getDouble(2));
                    break;
                }
            }
        }

        while (resultSet2.next()) {
            Integer id = resultSet2.getInt(1);
            for (Hisob h: hisobObservableList) {
                if (id.equals(h.getId())) {
                    h.setKirim(resultSet2.getDouble(2));
                    break;
                }
            }
        }

        for (Hisob h: hisobObservableList) {
            if (h.getKirim()==null) {
                h.setKirim(0d);
            }
            if (h.getChiqim()==null) {
                h.setChiqim(0d);
            }
            h.setBalans(h.getKirim() - h.getChiqim());
            String line = String.format("%.3s %10s %20s %20s %20s\n", h.getId().toString(), h.getText(), decimalFormat.format(h.getKirim()), decimalFormat.format(h.getChiqim()), decimalFormat.format(h.getBalans()));
            System.out.println(line);
        }

/*
        ObservableList<HisobKitob> hisobKitobObservableList = hisobKitobModels.getAnyData(connection, "tovar=0 and barcode = '' and narh=0 and dona=0", "");
        for (HisobKitob hk: hisobKitobObservableList) {
            System.out.println(hk.getId() + "|Tovar| " + hk.getTovar() + "|barCode| " + hk.getBarCode() + "|narh|" + hk.getNarh() + "|dona|" + hk.getDona() + hk.getIzoh());
            hisobKitobModels.delete_data(connection, hk);
        }
*/
    }
}
