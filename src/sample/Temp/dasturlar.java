package sample.Temp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Config.MySqlDBGeneral;
import sample.Data.Standart;
import sample.Enums.ServerType;
import sample.Model.StandartModels;

import java.sql.Connection;
import java.util.Date;

public class dasturlar {
    public static void main(String[] args) {
        Connection connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        StandartModels standartModels = new StandartModels("Dasturlar");
        ObservableList<Standart> dasturList = FXCollections.observableArrayList(
                new Standart(null, "Dastur", 1, new Date()),
                new Standart(null, "Server sozlash", 1, new Date()),
                new Standart(null, "Dastur yurituvchilar", 1, new Date()),
                new Standart(null, "Dastur yurituvchini alishtir", 1, new Date()),
                new Standart(null, "Sozlovlar", 1, new Date()),
                new Standart(null, "Kirish cheklangan hisoblar", 1, new Date()),
                new Standart(null, "Kirish cheklangan dasturlar", 1, new Date()),
                new Standart(null, "Printerlar", 1, new Date())
        );
        ObservableList<Standart> serverList = FXCollections.observableArrayList(
                new Standart(null, "Local server", 1, new Date()),
                new Standart(null, "Remote server", 1, new Date())
                );
        ObservableList<Standart> hisobList = FXCollections.observableArrayList(
                new Standart(null, "Hisoblar", 1, new Date()),
                new Standart(null, "Murakkab hisob", 1, new Date()),
                new Standart(null, "Hisob guruhlari", 1, new Date())
        );
        ObservableList<Standart> hisobGuruhlariList = FXCollections.observableArrayList(
                new Standart(null, "Yordamchi hisoblar", 1, new Date()),
                new Standart(null, "Keldi-ketdi hisobi", 1, new Date()),
                new Standart(null, "Foyda hisobi", 1, new Date()),
                new Standart(null, "Zarar hisobi", 1, new Date()),
                new Standart(null, "Xaridor hisobi", 1, new Date()),
                new Standart(null, "NDS hisobi", 1, new Date()),
                new Standart(null, "Chegirma hisobi", 1, new Date()),
                new Standart(null, "Bank hisobi", 1, new Date()),
                new Standart(null, "Bank xizmati hisobi", 1, new Date()),
                new Standart(null, "Bojxona hisobi", 1, new Date()),
                new Standart(null, "Tasdiq hisobi", 1, new Date())
        );
        ObservableList<Standart> pulList = FXCollections.observableArrayList(
                new Standart(null, "Pullar", 1, new Date()),
                new Standart(null, "Kurslar", 1, new Date()),
                new Standart(null, "Pul harakatlari", 1, new Date()),
                new Standart(null, "Pul guruhlari", 1, new Date()),
                new Standart(null, "Konvertatsiya", 1, new Date())
        );
        ObservableList<Standart> tovarList = FXCollections.observableArrayList(
                new Standart(null, "Tovarlar", 1, new Date()),
                new Standart(null, "Yangi tovar", 1, new Date()),
                new Standart(null, "Tovar sotish tartibi", 1, new Date()),
                new Standart(null, "Tovar NDS miqdori", 1, new Date()),
                new Standart(null, "O`lchov birliklari", 1, new Date()),
                new Standart(null, "Tovar narhlari", 1, new Date()),
                new Standart(null, "Tovar xaridi", 1, new Date()),
                new Standart(null, "Tovar harakatlari", 1, new Date()),
                new Standart(null, "Tovar guruhlari", 1, new Date()),
                new Standart(null, "Seriya raqami", 1, new Date())
        );
        ObservableList<Standart> amalList = FXCollections.observableArrayList(
                new Standart(null, "Amallar", 1, new Date()),
                new Standart(null, "Qarz amallari", 1, new Date()),
                new Standart(null, "Amal guruhlari", 1, new Date())
        );
        ObservableList<Standart> savdoList = FXCollections.observableArrayList(
                new Standart(null, "Narh turlari", 1, new Date()),
                new Standart(null, "Chiqim shakli", 1, new Date()),
                new Standart(null, "To`lov shakli", 1, new Date()),
                new Standart(null, "Savdo nuqtalari", 1, new Date()),
                new Standart(null, "Savdo bo`limlari", 1, new Date()),
                new Standart(null, "Xaridlar jadvali", 1, new Date())
        );
        ObservableList<Standart> hisobotList = FXCollections.observableArrayList(
                new Standart(null, "Pul hisoboti", 1, new Date()),
                new Standart(null, "Tovar hisoboti", 1, new Date()),
                new Standart(null, "Pul hisoboti", 1, new Date()),
                new Standart(null, "Shtrixkod hisoboti", 1, new Date()),
                new Standart(null, "Sochma hisobot", 1, new Date()),
                new Standart(null, "Foyda hisoboti", 1, new Date()),
                new Standart(null, "Taminotchilar hisoboti", 1, new Date())
        );

    }
}
