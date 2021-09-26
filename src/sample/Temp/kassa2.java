package sample.Temp;

import sample.Config.MySqlDB;
import sample.Config.MySqlDBLocal;
import sample.Data.Hisob;
import sample.Data.Valuta;
import sample.Model.HisobKitobModels;
import sample.Tools.GetDbData;
import sample.Tools.MoneyShow;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class kassa2 {
    public static void main(String[] args) {
        MoneyShow moneyShow = new MoneyShow();
        Connection connection = new MySqlDB().getDbConnection();
        GetDbData.initData(connection);
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        List<Hisob> hisobList = new ArrayList<>();
        String select =
                "Select valuta, sum(if(hisob2=10,narh,0)) as kirim, sum(if(hisob1=10,narh,0)) as chiqim from HisobKitob where tovar=0 and valuta>0 group by valuta";
        ResultSet rs = hisobKitobModels.getResultSet(connection, select);
        while (true) {
            try {
                if (!rs.next()) {
                    break;
                } else {
                    Integer id = rs.getInt(1);
                    Double kirim = rs.getDouble(2);
                    Double chiqim = rs.getDouble(3);
                    Double jami = kirim - chiqim;
                    Valuta v = GetDbData.getValuta(id);
                    Hisob hisob= new Hisob(
                            v.getId(),
                            v.getValuta(),
                            chiqim,
                            kirim,
                            jami
                    );
                    System.out.println("Valuta: " + v.getValuta() + "\nKirim: " + moneyShow.format(kirim) + "\nChiqim: " + moneyShow.format(chiqim) + "\nJami: " + moneyShow.format(jami));
                };
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }
//       2|3.515935E8
//    1|2830.0
}
