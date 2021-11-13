package sample.Temp;

import javafx.collections.ObservableList;
import sample.Config.MySqlDBGeneral;
import sample.Data.BarCode;
import sample.Data.HisobKitob;
import sample.Data.User;
import sample.Enums.ServerType;
import sample.Model.HisobKitobModels;
import sample.Tools.GetDbData;

import java.sql.Connection;
import java.util.Date;

public class temp2 {
    static Connection  connection;
    static User user = new User(1, "admin", "", "admin");
    public static void main(String[] args) {

        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        HisobKitob hisobKitob = new HisobKitob(
                2,
                1,
                1,
                5,
                13,
                3,
                1,
                569,
                1.0,
                "8993379284466",
                1.0,
                0.3,
                0,
                "",
                1,new Date()
        );
        Mayda mayda = new Mayda(connection, user);
        GetDbData.initData(connection);
        BarCode barCode = GetDbData.getBarCode(hisobKitob.getBarCode());
//        mayda.maydala(barCode, hisobKitob, 1.0);
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        Boolean maydalandi = false;
        ObservableList<HisobKitob> hkList = hisobKitobModels.getBarCodeQoldiq(connection, 13, barCode, hisobKitob.getDateTime());
        double hkQoldiq = .0;
        double talab = 2.0;
        Integer barCodeId = barCode.getId();
        for (HisobKitob hk: hkList) {
            System.out.println(hk.getDona());
            hkQoldiq += hk.getDona();
        }
        if (hkQoldiq == 0.0) {
            System.out.println("Tovar yo`q");
            ObservableList<BarCode> bcList = GetDbData.getBarCodeList(barCode.getTovar());
            if (bcList.size()>1) {
                bcList.removeIf(item -> item.getId().equals(barCodeId));
                for (BarCode bc: bcList) {
                    if (bc.getTarkib().equals(barCodeId)) {
                        System.out.println(bc.getTarkib() + " = " + barCodeId);
                        hkList = hisobKitobModels.getBarCodeQoldiq(connection, 13, bc, hisobKitob.getDateTime());
                        for (HisobKitob hk: hkList) {
                            System.out.println(bc.getBarCode());
                            hkQoldiq += hk.getDona();
                            System.out.println(hkQoldiq);
                            if (hkQoldiq>0) {
                                hk.setHisob1(hk.getHisob2());
                                hk.setHisob2(3);
                                mayda.maydala(bc, hk, 5.0);
                                maydalandi = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public static void fff() {}
}
