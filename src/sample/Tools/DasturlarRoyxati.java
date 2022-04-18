package sample.Tools;

import javafx.collections.ObservableList;
import sample.Data.Standart;
import sample.Data.User;
import sample.Model.StandartModels;

import java.sql.Connection;
import java.util.Date;

public class DasturlarRoyxati {
    public static Standart dastur(Connection connection, User user, String dasturNomi) {
        Standart standart = null;
        StandartModels standartModels = new StandartModels("Dasturlar");
        ObservableList<Standart> observableList = standartModels.getAnyData(connection, "text like('"+ dasturNomi+"')", "");
        if (observableList.size()==0) {
            standart = new Standart(null, dasturNomi, user.getId(),new Date());
            standartModels.insert_data(connection, standart);
        }
        return standart;
    }
}
