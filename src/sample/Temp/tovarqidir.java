package sample.Temp;

import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.collections.ObservableList;
import sample.Config.MySqlDBLocal;
import sample.Data.HisobKitob;
import sample.Data.Standart;
import sample.Model.HisobKitobModels;
import sample.Model.StandartModels;

import java.sql.Connection;



public class tovarqidir {
    static ObservableList<Standart> tovarList;

    public static void main(String[] args) {
        Connection connection = new MySqlDBLocal().getDbConnection();
        StandartModels standartModels = new StandartModels("Tovar");
        tovarList = standartModels.get_data(connection);
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        ObservableList<HisobKitob> hisobKitobObservableList = hisobKitobModels.getAnyData(connection, "tovar>0", "");
        for (HisobKitob hk: hisobKitobObservableList) {
            if (!tovarTop(hk.getTovar())) {
                System.out.println(hk.getTovar());
            }
        }

    }

    private static Boolean tovarTop(Integer tovarId) {
        Boolean topdim = false;
        for (Standart s: tovarList) {
            if (s.getId().equals(tovarId)) {
                topdim = true;
                break;
            }
        }
        return topdim;
    }
}
