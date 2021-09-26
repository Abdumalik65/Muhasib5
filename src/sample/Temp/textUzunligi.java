package sample.Temp;

import javafx.collections.ObservableList;
import sample.Config.MySqlDBLocal;
import sample.Data.BarCode;
import sample.Model.BarCodeModels;
import sample.Model.StandartModels;

import java.sql.Connection;

public class textUzunligi {
    public static void main(String[] args) {
        Connection connection = new MySqlDBLocal().getDbConnection();
        StandartModels standartModels = new StandartModels();
        standartModels.setTABLENAME("Amal");
        Integer uzunlik = 0;
        String s1 = "";
        String s2 = "";
        int cont = 0;
        BarCodeModels barCodeModels = new BarCodeModels();
        ObservableList<BarCode> observableList = barCodeModels.get_data(connection);
        for (BarCode bc: observableList) {
            cont++;
            s1 = bc.getBarCode().trim();
            if (s1.length() > uzunlik) {
                s2 = s1;
                uzunlik = s1.length();
            }
        }
        System.out.println(s2 + " | " + uzunlik+ " | " + cont);
    }
}
