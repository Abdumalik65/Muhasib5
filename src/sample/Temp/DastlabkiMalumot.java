package sample.Temp;

import javafx.collections.ObservableList;
import sample.Config.MySqlDBLocal;
import sample.Data.Standart;
import sample.Model.StandartModels;

import java.sql.Connection;

public class DastlabkiMalumot {
    public static void main(String[] args) {
        Connection connection = new MySqlDBLocal().getDbConnection();
        StandartModels standartModels = new StandartModels();
        standartModels.setTABLENAME("NarhTuri");
        ObservableList<Standart> standarts = standartModels.get_data(connection);
        for (Standart s: standarts) {
            System.out.println(s.getText());
        }
    }
}
