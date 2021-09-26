package sample.Temp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Config.MySqlDBLocal;
import sample.Data.BarCode;
import sample.Model.BarCodeModels;

import java.sql.Connection;

public class CheckBarCodeTable {
    public static void main(String[] args) {
        Connection connection = new MySqlDBLocal().getDbConnection();
        BarCodeModels barCodeModels = new BarCodeModels();
        ObservableList<BarCode> barCodes = barCodeModels.getDistinct(connection);
        ObservableList<BarCode> barCodes1 = FXCollections.observableArrayList();
        ObservableList<BarCode> barCodesResult = FXCollections.observableArrayList();
        for (BarCode bc: barCodes) {
            barCodes1 = barCodeModels.getAnyData(connection, "barCode = '" + bc.getBarCode() + "'", "");
            if (barCodes1.size()>1) {
                barCodesResult.addAll(barCodes1);
            }
        }
        if (barCodesResult.size()>0) {
            for (BarCode bc: barCodesResult) {
                System.out.println("|" + bc.getTovar() + "|" + bc.getBarCode() + "|");
            }
        }
    }
}
