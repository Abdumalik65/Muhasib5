package sample.Temp;

import javafx.collections.ObservableList;
import sample.Config.MySqlDBLocal;
import sample.Data.BarCode;
import sample.Data.Standart;
import sample.Tools.ConnectionType;
import sample.Tools.GetDbData;

import java.sql.Connection;

public class tempBarCodes {
    public static void main(String[] args) {
        Connection connection = new MySqlDBLocal().getDbConnection();
        GetDbData.initData(connection);
        ObservableList<BarCode> barCodes = GetDbData.getBarCodeObservableList();
        ObservableList<Standart> tovarlar = GetDbData.getTovarObservableList();
        ObservableList<Standart> birliklar = GetDbData.getBiirlikObservableList();
        for (BarCode bc: barCodes) {
            String string = bc.getBarCode();
            String tovarNomi = GetDbData.getTovar(bc.getTovar()).getText();
            String birlikNomi = GetDbData.getBirlik(bc.getBirlik()).getText();
            if (string.substring(0, 2).equalsIgnoreCase(ConnectionType.getAloqa().getDbPrefix())) {
                System.out.println("|"+ tovarNomi + "|" + bc.getBarCode() + "|" + birlikNomi + "|");
            }
        }
    }
}
