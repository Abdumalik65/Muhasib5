package sample.Temp;

import javafx.collections.ObservableList;
import sample.Config.SqliteDB;
import sample.Data.Aloqa;
import sample.Model.AloqaModels;
import java.sql.Connection;

public class sqliteConnectionTemp {
    public static void main(String[] args) {
        AloqaModels aloqaModels = new AloqaModels();
        Connection connection = new SqliteDB().getDbConnection();
        ObservableList<Aloqa> aloqas = aloqaModels.get_data(connection);
        for (Aloqa a: aloqas) {
            System.out.println(a.getDbHost());
        }
    }
}
