package sample.Temp;

import javafx.collections.ObservableList;
import sample.Config.MySqlDBGeneral;
import sample.Data.BarCode;
import sample.Data.HisobKitob;
import sample.Enums.ServerType;
import sample.Model.HisobKitobModels;
import sample.Tools.GetDbData;

import java.sql.Connection;
import java.util.Date;

public class tempExcel {

    public static void main(String[] args) {
        Connection connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        ObservableList<HisobKitob> data = hisobKitobModels.get_data(connection);
        GetDbData.initData(connection);
        BarCode barCode = GetDbData.getBarCode(1354);
        data = hisobKitobModels.getBarCodeQoldiq(connection, 10, barCode, new Date());
        for (HisobKitob hk: data) {
            System.out.println(hk.getId() + " | " + hk.getIzoh() + " | " + hk.getDona() + " | " + hk.getNarh());
        }
    }
}
