package sample.Temp;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Config.MySqlDB;
import sample.Config.MySqlDBLocal;
import sample.Config.MySqlDBTemp01;
import sample.Config.MySqlDBTemp02;
import sample.Data.*;
import sample.Data.Tovar;
import sample.Excel.NarhlarExcel;
import sample.Model.*;
import sample.Tools.GetDbData;
import sample.Tools.MoneyShow;
import sample.Tools.PrinterService;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.List;

public class Abdurahim {
    static Connection connection = new MySqlDB().getDbConnection();
    static Standart3Models standart3Models = new Standart3Models();
    static ObservableList<Standart3> standart3ObservableList;
    static ObservableList<Tovar> tovarObservableList = FXCollections.observableArrayList();
    public static void main(String[] args) throws IOException {
        standart3Models.setTABLENAME("BGuruh2");
        standart3ObservableList = standart3Models.getAnyData(connection, "id2 = " + 2,"");
        for (Standart3 s3: standart3ObservableList) {
            Double xaridNarhiDouble = 0.0;
            Standart6 xaridNarhi = narhOl(s3.getId3());
            if (xaridNarhi != null) {
                xaridNarhiDouble = xaridNarhi.getNarh();
            }
            else {
                TovarNarhi tn = yakkaTovarNarhi(s3.getId3(), 0);
                if (tn != null) {
                    xaridNarhiDouble = tn.getNarh();
                }
            }
            tovarObservableList.add(new Tovar(s3.getId3(), s3.getText(), xaridNarhiDouble, 1, null));
        }

/*
        for (Tovar t: tovarObservableList) {
            System.out.println("| " + t.getText() + " | " + t.getNds());
        }
*/
        NarhlarExcel narhlarExcel = new NarhlarExcel();
        narhlarExcel.narh(tovarObservableList);
    }

    public static Standart6 narhOl(int tovarId) {
        Standart6 s6 = null;
        Standart6Models standart6Models = new Standart6Models("TGuruh1");
        Standart3Models standart3Models = new Standart3Models();
        standart3Models.setTABLENAME("TGuruh2");
        ObservableList<Standart3> s3List = standart3Models.getAnyData(connection, "id3 = " + tovarId, "");
        if (s3List.size()>0) {
            Standart3 s3 = s3List.get(0);
            s6 = standart6Models.getWithId(connection, s3.getId2());
        }
        return s6;
    }
    private static TovarNarhi yakkaTovarNarhi(int tovarId, int narhTuri) {
        TovarNarhi tovarNarhi = null;
        TovarNarhiModels tovarNarhiModels = new TovarNarhiModels();
        ObservableList<TovarNarhi> observableList = tovarNarhiModels.getAnyData(
                connection, "tovar = " + tovarId + " AND narhTuri = " + narhTuri, "sana desc"
        );
        if (observableList.size()>0) {
            tovarNarhi = observableList.get(0);
        }
        return tovarNarhi;
    }
}
