package sample.Temp;

import javafx.collections.ObservableList;
import sample.Config.MySqlDBLocal;
import sample.Data.Hisob;
import sample.Data.HisobKitob;
import sample.Model.HisobKitobModels;
import sample.Tools.GetDbData;

import java.sql.Connection;

public class Diagramma {
    public static void main(String[] args) {
        Connection connection = new MySqlDBLocal().getDbConnection();
        GetDbData.initData(connection);
        ObservableList<Hisob> hisobObservableList = GetDbData.getHisobObservableList();
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        ObservableList<HisobKitob> hisobKitobObservableList = hisobKitobModels.getAnyData(connection, "", "dateTime");
        System.out.println(hisobKitobObservableList.size());
    }
}
