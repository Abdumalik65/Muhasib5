package sample.Config;

import com.mysql.cj.jdbc.DatabaseMetaData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Data.*;
import sample.Model.*;
import sample.Tools.Alerts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

public class CopyDataFromServer {
    public static void main(String[] args) throws ParseException {
        Connection sourceConnection = new MySqlDB().getDbConnection();
        Connection targetConnection = new MySqlDBLocal().getDbConnection();
        ObservableList<String> list = getTableList(targetConnection);
        for (String s : list) {
            dropTable(targetConnection, s);
            System.out.println(s + " o`chdi");
        }
        targetConnection = new MySqlDBLocal().getDbConnection();
        copyStandartData(sourceConnection, targetConnection, "Amal");
        copyStandartData(sourceConnection, targetConnection, "Tovar");
        copyStandartData(sourceConnection, targetConnection, "Birlik");
        copyStandartData(sourceConnection, targetConnection, "NarhTuri");

        copyStandartData(sourceConnection, targetConnection, "AmalGuruhlari");
        copyStandartData(sourceConnection, targetConnection, "HisobGuruhlarNomi");

        copyStandartData(sourceConnection, targetConnection, "ChiqimShakli");
        copyStandartData(sourceConnection, targetConnection, "TolovShakli");
        copyStandartData(sourceConnection, targetConnection, "MijozTuri");
        copyStandartData(sourceConnection, targetConnection, "SavdoTuri");
        copyStandartData(sourceConnection, targetConnection, "QarzAmallari");
        copyStandartData(sourceConnection, targetConnection, "ValutaGuruhlari");
        System.out.println("Standart");

        copyStandartData2(sourceConnection, targetConnection, "FoydaHisobi");
        copyStandartData2(sourceConnection, targetConnection, "Zarar");
        copyStandartData2(sourceConnection, targetConnection, "TranzitHisob");
        copyStandartData2(sourceConnection, targetConnection, "NDS1");
        copyStandartData2(sourceConnection, targetConnection, "Chegirma");
        copyStandartData2(sourceConnection, targetConnection, "Bank");
        copyStandartData2(sourceConnection, targetConnection, "Mahsulot");
        copyStandartData2(sourceConnection, targetConnection, "BankXizmati");
        copyStandartData2(sourceConnection, targetConnection, "Bojxona");
        System.out.println("Standart2");

        copyStandartData3(sourceConnection, targetConnection, "Bank1");
        copyStandartData3(sourceConnection, targetConnection, "BankXizmati1");
        copyStandartData3(sourceConnection, targetConnection, "FoydaHisobiGuruhi");
        copyStandartData3(sourceConnection, targetConnection, "TranzitHisobGuruhi");
        copyStandartData3(sourceConnection, targetConnection, "ZararGuruhi");
        copyStandartData3(sourceConnection, targetConnection, "ChegirmaGuruhi");
        copyStandartData3(sourceConnection, targetConnection, "NDS2");
        copyStandartData3(sourceConnection, targetConnection, "HisobGuruhTarkibi");
        copyStandartData3(sourceConnection, targetConnection, "TGuruh2");
        copyStandartData3(sourceConnection, targetConnection, "BGuruh2");
        copyStandartData3(sourceConnection, targetConnection, "Bojxona2");

        System.out.println("Standart3");

        copyStandartData4(sourceConnection, targetConnection, "Tartib");
        copyStandartData4(sourceConnection, targetConnection, "Nds");
        copyStandartData4(sourceConnection, targetConnection, "BojxonaSoligi");
        System.out.println("Standart4");

        copyStandartData6(sourceConnection, targetConnection, "TGuruh1");
        copyStandartData6(sourceConnection, targetConnection, "BGuruh1");
        System.out.println("Standart6");

        BarCodesTable(sourceConnection, targetConnection);
        System.out.println("BarCodes");
        TovarNarhi(sourceConnection, targetConnection);
        System.out.println("Tovar narhlari");
        TovarSana(sourceConnection, targetConnection);
        System.out.println("Narh sanalari");
        ValutaTable(sourceConnection, targetConnection);
        System.out.println("Valuta");
        copyNarhlar(sourceConnection, targetConnection);
        HisobTable(sourceConnection, targetConnection, "Hisob");
        HisobTable(sourceConnection, targetConnection, "Hisob1");
        HisobTable(sourceConnection, targetConnection, "Hisob2");
        System.out.println("Hisoblar");
        HisobKitobTable(sourceConnection, targetConnection);
        System.out.println("HisobKitob");
        UserTable(sourceConnection, targetConnection);
        System.out.println("Users");
        KassaTable(sourceConnection, targetConnection);
        System.out.println("Kassa");
        QaydnomaTable(sourceConnection, targetConnection);
        System.out.println("Qaydnoma");
        KursTable(sourceConnection, targetConnection);
        System.out.println("Kurslar");
        GuruhNarhiTable(sourceConnection, targetConnection);
        System.out.println("GuruhNarhiTable");
        SanoqQaydiTable(sourceConnection, targetConnection);
        System.out.println("SanoqQaydiTable");
    }


    private static void dropTable(Connection connection, String tableName) {
        String dropTable = "DROP TABLE IF EXISTS " + tableName;
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(dropTable);
            prSt.executeUpdate();
        } catch (SQLException e) {
            Alerts.losted();;
        }

    }

    private static ObservableList<String> getTableList(Connection connection) {
        ObservableList<String> list = FXCollections.observableArrayList();
        DatabaseMetaData md = null;
        try {
            md = (DatabaseMetaData) connection.getMetaData();
            ResultSet rs = md.getTables("Muhasib", "Muhasib", "%", null);
            while (rs.next()) {
                list.add(rs.getString(3));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return list;
    }

    private static void copyStandartData(Connection sourceConnection, Connection targetConnection, String fileName) {
        StandartModels standartModels = new StandartModels();
        standartModels.setTABLENAME(fileName);
        ObservableList<Standart> observableList = standartModels.get_data(sourceConnection);
        if (observableList.size()>0) {
            standartModels.copyDataBatch(targetConnection, observableList);
        }
    }
    private static void copyStandartData2(Connection sourceConnection, Connection targetConnection, String fileName) {
        Standart2Models standart2Models = new Standart2Models();
        standart2Models.setTABLENAME(fileName);
        ObservableList<Standart2> observableList = standart2Models.get_data(sourceConnection);
        if (observableList.size()>0) {
            standart2Models.copyDataBatch(targetConnection, observableList);
        }
    }

    private static void copyStandartData3(Connection sourceConnection, Connection targetConnection, String fileName) {
        Standart3Models standart3Models = new Standart3Models();
        standart3Models.setTABLENAME(fileName);
        ObservableList<Standart3> observableList = standart3Models.get_data(sourceConnection);
        if (observableList.size()>0) {
            standart3Models.copyDataBatch(targetConnection, observableList);
        }
    }

    private static void copyStandartData4(Connection sourceConnection, Connection targetConnection, String fileName) {
        Standart4Models standart4Models = new Standart4Models();
        standart4Models.setTABLENAME(fileName);
        ObservableList<Standart4> observableList = standart4Models.get_data(sourceConnection);
        if (observableList.size()>0) {
            standart4Models.copyDataBatch(targetConnection, observableList);
        }
    }

    private static void copyStandartData6(Connection sourceConnection, Connection targetConnection, String fileName) {
        Standart6Models standart6Models = new Standart6Models();
        standart6Models.setTABLENAME(fileName);
        ObservableList<Standart6> observableList = standart6Models.get_data(sourceConnection);
        if (observableList.size()>0) {
            standart6Models.copyDataBatch(targetConnection, observableList);
        }
    }
    private static void BarCodesTable(Connection sourceConnection, Connection targetConnection) {
        BarCodeModels barCodeModels = new BarCodeModels();
        ObservableList<BarCode> observableList = barCodeModels.get_data(sourceConnection);
        if (observableList.size()>0) {
            barCodeModels.copyDataBatch(targetConnection, observableList);
        }
    }

    private static void TovarNarhi(Connection sourceConnection, Connection targetConnection) {
        TovarNarhiModels tovarNarhiModels = new TovarNarhiModels();
        ObservableList<TovarNarhi> observableList = tovarNarhiModels.get_data(sourceConnection);
        if (observableList.size()>0) {
            tovarNarhiModels.copyDataBatch(targetConnection, observableList);
        }
    }
    private static void TovarSana(Connection sourceConnection, Connection targetConnection) {
        TovarSanaModels tovarSanaModels = new TovarSanaModels();
        ObservableList<TovarSana> observableList = tovarSanaModels.get_data(sourceConnection);
        if (observableList.size()>0) {
            tovarSanaModels.copyDataBatch(targetConnection, observableList);
        }
    }
    private static void ValutaTable(Connection sourceConnection, Connection targetConnection) {
        ValutaModels valutaModels = new ValutaModels();
        ObservableList<Valuta> observableList = valutaModels.get_data(sourceConnection);
        if (observableList.size()>0) {
            valutaModels.copyDataBatch(targetConnection, observableList);
        }
    }
    private static void copyNarhlar(Connection sourceConnection, Connection targetConnection) {
        NarhModels narhModels = new NarhModels();
        ObservableList<Narh> observableList = narhModels.get_data(sourceConnection);
        if (observableList.size()>0) {
            narhModels.copyDataBatch(targetConnection, observableList);
        }
    }
    private static void HisobTable(Connection sourceConnection, Connection targetConnection, String fileName) {
        HisobModels hisobModels = new HisobModels();
        hisobModels.setTABLENAME(fileName);
        ObservableList<Hisob> observableList = hisobModels.get_data(sourceConnection);
        if (observableList.size()>0) {
            hisobModels.copyDataBatch(targetConnection, observableList);
        }
    }
    private static void HisobKitobTable(Connection sourceConnection, Connection targetConnection) {
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        ObservableList<HisobKitob> observableList = hisobKitobModels.get_data(sourceConnection);
        if (observableList.size()>0) {
            hisobKitobModels.copyDataBatch(targetConnection, observableList);
        }
    }

    private static void UserTable(Connection sourceConnection, Connection targetConnection) {
        UserModels userModels = new UserModels();
        ObservableList<User> observableList = userModels.getData(sourceConnection);
        if (observableList.size()>0) {
            userModels.copyDataBatch(targetConnection, observableList);
        }
    }
    private static void KassaTable(Connection sourceConnection, Connection targetConnection) {
        KassaModels kassaModels = new KassaModels();
        ObservableList<Kassa> observableList = kassaModels.get_data(sourceConnection);
        if (observableList.size()>0) {
            kassaModels.copyDataBatch(targetConnection, observableList);
        }
    }
    private static void QaydnomaTable(Connection sourceConnection, Connection targetConnection) {
        QaydnomaModel qaydnomaModel = new QaydnomaModel();
        ObservableList<QaydnomaData> observableList = qaydnomaModel.get_data(sourceConnection);
        if (observableList.size()>0) {
            qaydnomaModel.copyDataBatch(targetConnection, observableList);
        }
    }
    private static void KursTable(Connection sourceConnection, Connection targetConnection) {
        KursModels kursModels = new KursModels();
        ObservableList<Kurs> observableList = kursModels.get_data(sourceConnection);
        if (observableList.size()>0) {
            kursModels.copyDataBatch(targetConnection, observableList);
        }
    }
    private static void GuruhNarhiTable(Connection sourceConnection, Connection targetConnection) {
        GuruhNarhModels guruhNarhModels = new GuruhNarhModels();
        ObservableList<GuruhNarh> observableList = guruhNarhModels.get_data(sourceConnection);
        if (observableList.size()>0) {
            guruhNarhModels.copyDataBatch(targetConnection, observableList);
        }
    }
    private static void SanoqQaydiTable(Connection sourceConnection, Connection targetConnection) {
        SanoqQaydiModels sanoqQaydiModels = new SanoqQaydiModels();
        ObservableList<SanoqQaydi> observableList = sanoqQaydiModels.get_data(sourceConnection);
        if (observableList.size()>0) {
            sanoqQaydiModels.copyDataBatch(targetConnection, observableList);
        }
    }
}
