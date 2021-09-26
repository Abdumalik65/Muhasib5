package sample.Temp;

import javafx.collections.ObservableList;
import sample.Config.MySqlDB;
import sample.Config.MySqlDBLocal;
import sample.Data.HisobKitob;
import sample.Data.QaydnomaData;
import sample.Data.Standart3;
import sample.Model.HisobKitobModels;
import sample.Model.HisobModels;
import sample.Model.QaydnomaModel;
import sample.Model.Standart3Models;
import sample.Tools.GetDbData;
import sample.Tools.MoneyShow;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FOYDA {
    public static void main(String[] args) throws Exception {
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        Connection connection = new MySqlDB().getDbConnection();
        GetDbData.initData(connection);
        ObservableList<HisobKitob> hisobKitobObservableList = hisobKitobModels.getAnyData(connection, "datetime like(\"%2021-06-26%\") and hisob2=10 and valuta=2", "");
        for (HisobKitob hk: hisobKitobObservableList) {
            if (hk.getTovar()==0)
            System.out.println(hk.getHisob1() + "|" + hk.getNarh() + "|" + hk.getIzoh());
        }
    }

}
