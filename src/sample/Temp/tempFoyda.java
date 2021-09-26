package sample.Temp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Config.MySqlDBLocal;
import sample.Data.BarCode;
import sample.Data.HisobKitob;
import sample.Data.QaydnomaData;
import sample.Data.Standart3;
import sample.Excel.FoydaExcel;
import sample.Model.HisobKitobModels;
import sample.Model.QaydnomaModel;
import sample.Model.Standart3Models;
import sample.Tools.GetDbData;
import sample.Tools.MoneyShow;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class tempFoyda {
    public static void main(String[] args) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Integer day = 12;
        Integer month = 3;
        Integer year = 2021;
        String dateString = "" + day + "." + month + "." + year;
        Date date = null;
        date = simpleDateFormat.parse(dateString);
        String s = simpleDateFormat.format(date);
        Double jamiDouble = 0d;
        Double mablagDouble = 0d;
        Double chegirmaDouble = 0d;
        int amalTuri = 4;
        Date sana = null;
        Connection connection = new MySqlDBLocal().getDbConnection();
        GetDbData.initData(connection);
        QaydnomaModel qaydnomaModel = new QaydnomaModel();
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        ObservableList<HisobKitob> hisobKitobObservableList = FXCollections.observableArrayList();
        ObservableList<QaydnomaData> qaydnomaDataObservableList = qaydnomaModel.getAnyData(connection, "substr(SANA,1,10)='2021-03-25'", "");
        for (QaydnomaData q: qaydnomaDataObservableList) {
//            System.out.println(q.getSana() + " | " + q.getId()  + " | " + q.getAmalTuri() + " | " + q.getHujjat() + " | " + q.getChiqimNomi() + " | " + q.getKirimNomi() + "|");
            ObservableList<HisobKitob> hkList = hisobKitobModels.getAnyData(connection, "qaydId =" + q.getId() + " and hujjatId = " + q.getHujjat() + " and hisob1 = 3", "");
            if (hkList.size()>0) {
                hisobKitobObservableList.addAll(hkList);
            }
        }
        ObservableList<Standart3>  s3List = initBGuruh(connection);
        FoydaExcel foydaExcel = new FoydaExcel();
        foydaExcel.hisob(3, hisobKitobObservableList, s3List, 0d);
        System.out.println("Ish bitdi");
    }

    public static ObservableList<Standart3> initBGuruh(Connection connection) {
        ObservableList<Standart3> s3List;
        Standart3Models standart3Models = new Standart3Models();
        standart3Models.setTABLENAME("BGuruh2");
        s3List = standart3Models.get_data(connection);
        return s3List;
    }

}