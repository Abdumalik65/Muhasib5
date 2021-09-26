package sample.Temp;

import javafx.collections.ObservableList;
import sample.Config.MySqlDBLocal;
import sample.Data.BarCode;
import sample.Data.HisobKitob;
import sample.Data.Standart;
import sample.Model.BarCodeModels;
import sample.Model.HisobKitobModels;

import java.sql.Connection;


public class barcodeqidir {
    static ObservableList<BarCode> barCodeList;

    public static void main(String[] args) {
        Connection connection = new MySqlDBLocal().getDbConnection();
        BarCodeModels barCodeModels = new BarCodeModels();

        barCodeList = barCodeModels.get_data(connection);
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        ObservableList<HisobKitob> hisobKitobObservableList = hisobKitobModels.getAnyData(connection, "tovar>0", "");
        for (HisobKitob hk: hisobKitobObservableList) {
            if (!barCodeTop(hk.getBarCode())) {
                System.out.println(hk.getBarCode());
            }
        }

    }

    private static Boolean barCodeTop(String barCode) {
        Boolean topdim = false;
        for (BarCode bc: barCodeList) {
            if (bc.getBarCode().equals(barCode)) {
                topdim = true;
                break;
            }
        }
        return topdim;
    }
}
