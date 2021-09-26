package sample.Temp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Config.MySqlDBLocal;
import sample.Data.BarCode;
import sample.Data.HisobKitob;
import sample.Data.QaydnomaData;
import sample.Data.Standart;
import sample.Model.BarCodeModels;
import sample.Model.HisobKitobModels;
import sample.Model.QaydnomaModel;
import sample.Model.StandartModels;
import sample.Tools.GetDbData;

import java.sql.Connection;

public class TestData {
    public static void main(String[] args) {
        Connection connection = new MySqlDBLocal().getDbConnection();
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        ObservableList<HisobKitob> hisobKitobObservableList = hisobKitobModels.get_data(connection);
        QaydnomaModel qaydnomaModel = new QaydnomaModel();
        ObservableList<QaydnomaData> qaydnomaDataObservableList = qaydnomaModel.get_data(connection);
        Integer i=0;
        for (HisobKitob hk: hisobKitobObservableList) {
            QaydnomaData qaydnomaData = qaydnomaTop(qaydnomaDataObservableList, hk.getQaydId());
            if (qaydnomaData == null) {
                System.out.println(hk.getQaydId());
            }
            i++;
        }
        System.out.println(i);
    }
    private static QaydnomaData qaydnomaTop(ObservableList<QaydnomaData> qaydnomaDataObservableList, Integer id) {
        QaydnomaData qaydnomaData = null;
        for (QaydnomaData q: qaydnomaDataObservableList) {
            if (q.getId().equals(id)) {
                qaydnomaData = q;
                break;
            }
        }
        return qaydnomaData;
    }
}
