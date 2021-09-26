package sample.Temp;

import javafx.collections.ObservableList;
import sample.Data.BarCode;
import sample.Data.User;

import java.sql.Connection;

public class Aylantir {
    Connection connection;
    User user = new User(1, "admin", "", "admin");
    BarCode barCode;
    BarCode barCode1;
    ObservableList<BarCode> barCodeList;


    private BarCode qidir(BarCode talabEtilganBarCode) {
        BarCode topilganBarCode = null;
        for (BarCode bc: barCodeList) {
            if (bc.getId().equals(talabEtilganBarCode.getId())) {
                topilganBarCode = bc;
                break;
            }
        }
        return topilganBarCode;
    }
}
