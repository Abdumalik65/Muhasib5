package sample.Temp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Config.MySqlDB;
import sample.Config.MySqlDBGeneral;
import sample.Data.Hisob;
import sample.Data.Standart;
import sample.Data.User;
import sample.Enums.ServerType;
import sample.Model.HisobModels;
import sample.Model.StandartModels;
import sample.Model.UserModels;

import java.sql.Connection;
import java.util.Date;

public class InsertFirstData {
    public static void main(String[] args) {
        Connection connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();

        HisobModels hisobModels = new HisobModels();
        ObservableList<Hisob> hisobObservableList = FXCollections.observableArrayList();
        hisobObservableList.add(new Hisob(null, "SARMOYA", 0D, "", "", "", 1, null));
        hisobObservableList.add(new Hisob(null, "DO`KON", 0D, "", "", "", 1, null));
        hisobObservableList.add(new Hisob(null, "XARIDOR", 0D, "", "", "", 1, null));
        hisobObservableList.add(new Hisob(null, "FOYDA", 0D, "", "", "", 1, null));
        hisobObservableList.add(new Hisob(null, "ZARAR", 0D, "", "", "", 1, null));
        hisobObservableList.add(new Hisob(null, "CHEGIRMA", 0D, "", "", "", 1, null));
        hisobObservableList.add(new Hisob(null, "NDS", 0D, "", "", "", 1, null));
        hisobObservableList.add(new Hisob(null, "BOJXONA", 0D, "", "", "", 1, null));
        hisobObservableList.add(new Hisob(null, "BOJXONA SOLIG`I", 0D, "", "", "", 1, null));
        hisobObservableList.add(new Hisob(null, "BANK", 0D, "", "", "", 1, null));
        hisobObservableList.add(new Hisob(null, "BANK XIZMATI", 0D, "", "", "", 1, null));
        hisobObservableList.add(new Hisob(null, "KELDI-KETDI HISOBI", 0D, "", "", "", 1, null));
        hisobObservableList.add(new Hisob(null, "OYLIK", 0D, "", "", "", 1, null));
        for (Hisob h: hisobObservableList) {
            hisobModels.insert_data(connection,h);
        }

/*
        StandartModels standartModels = new StandartModels("Amal");
        ObservableList<Standart> standarts = FXCollections.observableArrayList();
        standarts.add(new Standart(null, "Pul harakatlari", 1, null));
        standarts.add(new Standart(null, "Tovar xaridi", 1, null));
        standarts.add(new Standart(null, "Tovar naqliyoti", 1, null));
        standarts.add(new Standart(null, "Tovar savdosi", 1, null));
        standarts.add(new Standart(null, "Pochkali tovarni donabayga aylantirish", 1, null));
        standarts.add(new Standart(null, "Donabayni pochkalash", 1, null));
        standarts.add(new Standart(null, "Tōlov", 1, null));
        standarts.add(new Standart(null, "Qaytim", 1, null));
        standarts.add(new Standart(null, "Foyda", 1, null));
        standarts.add(new Standart(null, "Tovar sanash", 1, null));
        standarts.add(new Standart(null, "NDS", 1, null));
        standarts.add(new Standart(null, "Zarar", 1, null));
        standarts.add(new Standart(null, "Chegirma", 1, null));
        standarts.add(new Standart(null, "Plastik kartadan pul yechish xarajati", 1, null));
        standarts.add(new Standart(null, "Bank hisobidan to`lov", 1, null));
        standarts.add(new Standart(null, "Konvertatsiya", 1, null));
        standarts.add(new Standart(null, "Kurs tafovuti", 1, null));
        standartModels.addBatch(connection, standarts);

        standarts.removeAll(standarts);
        standartModels.setTABLENAME("NarhTuri");
        standarts.add(new Standart(null, "Chakana", 1, null));
        standarts.add(new Standart(null, "Ulgurji", 1, null));
        for (Standart s: standarts) {
            standartModels.insert_data(connection, s);
        }
*/
/*
        UserModels userModels = new UserModels();
        User user = new User(1, "admin", "", "admin", "", "", 99, "", 1, new Date());
        userModels.addUser(connection, user);
*/
    }

}
