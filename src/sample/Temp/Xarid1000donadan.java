package sample.Temp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Config.MySqlDB;
import sample.Data.*;
import sample.Model.HisobKitobModels;
import sample.Model.NarhModels;
import sample.Model.QaydnomaModel;
import sample.Tools.GetDbData;

import java.sql.Connection;
import java.util.Date;

public class Xarid1000donadan {

    static User user = new User(1, "admin", "", "admin");
    static Connection connection;
    static QaydnomaModel qaydnomaModel = new QaydnomaModel();
    static Integer amalTuri = 2;

    public static void main(String[] args) {
        User user = new User(1, "admin", "", "admin");
        connection = new MySqlDB().getDbConnection();
        GetDbData.initData(connection);
        ObservableList<HisobKitob> hisobKitobObservableList = FXCollections.observableArrayList();
        QaydnomaData qaydnomaData = qaydnomaSaqlash();
        ObservableList<BarCode> barCodes;
        NarhModels narhModels = new NarhModels();
        ObservableList<Narh> narhObservableList;
        barCodes = GetDbData.getBarCodeObservableList();
        for (BarCode bc: barCodes) {
            narhObservableList = narhModels.getDate(connection, bc.getTovar(), qaydnomaData.getSana(), "sana desc");
            if (narhObservableList.size()>0) {
                Narh narh = narhObservableList.get(0);
                Double xaridNarhi = narh.getXaridDouble() * tovarDonasi(bc.getBarCode());
                HisobKitob hisobKitob = new HisobKitob(
                        null,
                        qaydnomaData.getId(),
                        qaydnomaData.getHujjat(),
                        amalTuri,
                        1,
                        13,
                        1,
                        bc.getTovar(),
                        1.0,
                        bc.getBarCode(),
                        1000.0,
                        xaridNarhi,
                        0,
                        "",
                        user.getId(),
                        null
                );
                hisobKitobObservableList.add(hisobKitob);
            }
        }
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        hisobKitobModels.addBatch(connection, hisobKitobObservableList);
    }

    private static double tovarDonasi(String barCodeString) {
        double adadBarCode2 = 0;
        BarCode barCode = GetDbData.getBarCode(barCodeString);
        double dona = barCode.getAdad();
        int tarkibInt = barCode.getTarkib();
        if (tarkibInt>0) {
            while (true) {
                BarCode barCode2 = GetDbData.getBarCode(tarkibInt);
                adadBarCode2 = barCode2.getAdad();
                dona *= adadBarCode2;
                tarkibInt = barCode2.getTarkib();
                if (tarkibInt == 0) {
                    break;
                }
            }
        }
        return dona;
    }
    private static QaydnomaData qaydnomaSaqlash() {
        Hisob hisob1 = GetDbData.getHisob(1);
        Hisob hisob2 = GetDbData.getHisob(13);
        int hujjatInt = getQaydnomaNumber();
        String izohString = "";
//        Double jamiDouble = getJami(hisobKitobObservableList);
        Date date = new Date();
        QaydnomaData qaydnomaData = new QaydnomaData(null, 2, hujjatInt, date,
                hisob1.getId(), hisob1.getText(), hisob2.getId(), hisob2.getText(),
                izohString, 0.0, 0, user.getId(), new Date());
        qaydnomaModel.insert_data(connection, qaydnomaData);
        return qaydnomaData;
    }

    private static int getQaydnomaNumber() {
        int qaydnomaInt = 1;
        ObservableList<QaydnomaData> qaydList = qaydnomaModel.getAnyData(connection, "amalTuri = " + amalTuri, "hujjat desc");
        if (qaydList.size()>0) {
            qaydnomaInt = qaydList.get(0).getHujjat() + 1;
        }
        return qaydnomaInt;
    }

}

