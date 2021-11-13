package sample.Temp;

import javafx.collections.ObservableList;
import sample.Config.MySqlDB;
import sample.Config.MySqlDBGeneral;
import sample.Data.Hisob;
import sample.Data.HisobKitob;
import sample.Data.QaydnomaData;
import sample.Model.HisobKitobModels;
import sample.Model.QaydnomaModel;
import sample.Tools.ConnectionType;
import sample.Tools.MoneyShow;
import sample.Tools.PrinterService;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.bouncycastle.asn1.x500.style.RFC4519Style.c;

public class SavdoSummasi {
    public static void main(String[] args) throws Exception {
        DecimalFormat decimalFormat = new MoneyShow();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Integer day = 3;
        Integer month = 4;
        Integer year = 2021;
        String dateString = ""+ year + "-" + month + "-" + day;
        Date date = null;
        date = simpleDateFormat.parse(dateString);
        String s = simpleDateFormat.format(date);
        Double jamiDouble = 0d;
        Double mablagDouble = 0d;
        Double chegirmaDouble = 0d;
        int amalTuri = 4;
        Connection connection = new MySqlDB().getDbConnection();
        QaydnomaModel qaydnomaModel = new QaydnomaModel();
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        ObservableList<QaydnomaData> qaydnomaDataObservableList = qaydnomaModel.getAnyData(connection, "amalTuri = " + amalTuri + " and substr(sana, 1,10) = '" + s + "'", "sana desc");
        ObservableList<HisobKitob> hisobKitobObservableList = null;
        for (QaydnomaData q: qaydnomaDataObservableList) {
            hisobKitobObservableList = hisobKitobModels.getAnyData(connection, "qaydId = " + q.getId() + " AND hujjatId = " + q.getHujjat()   , "");
            for (HisobKitob hk : hisobKitobObservableList) {
                if (hk.getHisob2().equals(q.getKirimId())) {
                    if (hk.getTovar() > 0) {
                        mablagDouble = mablagDouble + hk.getDona() * hk.getNarh();
                    }
                }
                if (hk.getAmal() == 13) {
                    chegirmaDouble = hk.getNarh();
                }
            }
            jamiDouble = jamiDouble + mablagDouble - chegirmaDouble;
            mablagDouble = 0d;
            chegirmaDouble = 0d;
        }
        System.out.println(decimalFormat.format(jamiDouble));
    }
}
