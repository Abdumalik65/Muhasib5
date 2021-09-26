package sample.Temp;

import javafx.collections.ObservableList;
import sample.Config.MySqlDB;
import sample.Config.MySqlDBLocal;
import sample.Data.HisobKitob;
import sample.Data.QaydnomaData;
import sample.Model.HisobKitobModels;
import sample.Model.QaydnomaModel;

import java.sql.Connection;

public class tempHHH {
    public static void main(String[] args) {
        Connection connection = new MySqlDB().getDbConnection();
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        QaydnomaModel qaydnomaModel = new QaydnomaModel();
        Double jami = 0d;
        ObservableList<HisobKitob> hisobKitobObservableList = hisobKitobModels.get_data(connection);
        ObservableList<QaydnomaData> qaydObservableList = qaydnomaModel.get_data(connection);
        for (HisobKitob hk: hisobKitobObservableList) {
            Boolean topdim = false;
            for (QaydnomaData q: qaydObservableList) {
                if (hk.getHujjatId().equals(q.getHujjat())) {
                    hk.setDateTime(q.getSana());
//                    hisobKitobModels.update_data(connection, hk);
                    topdim = true;
                    break;
                }
            }
            if (!topdim) {
                System.out.println(hk.getHujjatId());
            }
        }
    }
}
