package sample.Config;

import javafx.collections.ObservableList;
import sample.Data.*;
import sample.Model.*;

import java.sql.Connection;

public class LocalToRemote {
    public static void main(String[] args) {
        Connection sourceCconnection = new MySqlDBLocal().getDbConnection();
        Connection targetConnection = new MySqlDB().getDbConnection();
        copyStandartData(sourceCconnection, targetConnection, "Amal");
        copyStandartData(sourceCconnection, targetConnection, "Tovar");
        copyStandartData(sourceCconnection, targetConnection, "Birlik");
        copyStandartData(sourceCconnection, targetConnection, "NarhTuri");

        copyStandartData(sourceCconnection, targetConnection, "AmalGuruhlari");
        copyStandartData(sourceCconnection, targetConnection, "HisobGuruhlarNomi");

        copyStandartData(sourceCconnection, targetConnection,"ChiqimShakli");
        copyStandartData(sourceCconnection, targetConnection,"TolovShakli");
        copyStandartData(sourceCconnection, targetConnection,"MijozTuri");
        copyStandartData(sourceCconnection, targetConnection,"SavdoTuri");
        copyStandartData(sourceCconnection, targetConnection,"QarzAmallari");
        System.out.println("Standart");


        copyStandartData2(sourceCconnection, targetConnection, "FoydaHisobi");
        copyStandartData2(sourceCconnection, targetConnection, "Zarar");
        copyStandartData2(sourceCconnection, targetConnection, "TranzitHisob");
        copyStandartData2(sourceCconnection, targetConnection, "NDS1");
        copyStandartData2(sourceCconnection, targetConnection, "Chegirma");
        copyStandartData2(sourceCconnection, targetConnection, "Bank");
        copyStandartData2(sourceCconnection, targetConnection, "Mahsulot");
        copyStandartData2(sourceCconnection, targetConnection, "BankXizmati");

        System.out.println("Standart2");

        copyStandartData3(sourceCconnection, targetConnection, "Bank1");
        copyStandartData3(sourceCconnection, targetConnection, "BankXizmati1");
        copyStandartData3(sourceCconnection, targetConnection, "FoydaHisobiGuruhi");
        copyStandartData3(sourceCconnection, targetConnection, "TranzitHisobGuruhi");
        copyStandartData3(sourceCconnection, targetConnection, "ZararGuruhi");
        copyStandartData3(sourceCconnection, targetConnection, "ChegirmaGuruhi");
        copyStandartData3(sourceCconnection, targetConnection, "NDS2");
        copyStandartData3(sourceCconnection, targetConnection, "HisobGuruhTarkibi");

        System.out.println("Standart3");

        copyStandartData4(sourceCconnection, targetConnection, "Tartib");
        copyStandartData4(sourceCconnection, targetConnection, "Nds");
        copyNarhlar(sourceCconnection, targetConnection);
        System.out.println("Standart4");

        BarCodesTable(sourceCconnection, targetConnection);
        System.out.println("BarCodes");
        TovarNarhi(sourceCconnection, targetConnection);
        System.out.println("Tovar narhlari");
        TovarSana(sourceCconnection, targetConnection);
        System.out.println("Narh sanalari");
        ValutaTable(sourceCconnection, targetConnection);
        System.out.println("Valuta");
        HisobTable(sourceCconnection, targetConnection);
        System.out.println("Hisoblar");
//        HisobKitobTable(sourceCconnection, targetConnection);
//        System.out.println("HisobKitob");
        UserTable(sourceCconnection, targetConnection);
        System.out.println("Users");
        KassaTable(sourceCconnection, targetConnection);
        System.out.println("Kassa");
//        QaydnomaTable(sourceCconnection, targetConnection);
//        System.out.println("Qaydnoma");
        KursTable(sourceCconnection, targetConnection);
//        System.out.println("Kurslar");
//        SanoqQaydiTable(sourceCconnection, targetConnection);
//        System.out.println("Sanoq qaydi");
    }

    private static void copyStandartData(Connection connectionSqlite, Connection connectionMySql, String fileName) {
        StandartModels standartModels = new StandartModels();
        standartModels.setTABLENAME(fileName);
        ObservableList<Standart> observableList = standartModels.get_data(connectionSqlite);
        for (Standart s: observableList) {
            String string1 = s.getText().trim();
            if (string1.length()>80) {
                string1 = string1.substring(0,79);
            }
            s.setText(string1);
            standartModels.insert_data2(connectionMySql, s);
        }
    }

    private static void copyStandartData2(Connection connectionSqlite, Connection connectionMySql, String fileName) {
        Standart2Models standart2Models = new Standart2Models();
        standart2Models.setTABLENAME(fileName);
        ObservableList<Standart2> observableList = standart2Models.get_data(connectionSqlite);
        for (Standart2 s: observableList) {
            String string1 = s.getText().trim();
            if (string1.length()>80) {
                string1 = string1.substring(0,79);
            }
            s.setText(string1);
            standart2Models.insert_data(connectionMySql, s);
        }
    }

    private static void copyStandartData3(Connection connectionSqlite, Connection connectionMySql, String fileName) {
        Standart3Models standart3Models = new Standart3Models();
        standart3Models.setTABLENAME(fileName);
        ObservableList<Standart3> observableList = standart3Models.get_data(connectionSqlite);
        for (Standart3 s: observableList) {
            String string1 = s.getText().trim();
            if (string1.length()>80) {
                string1 = string1.substring(0,79);
            }
            s.setText(string1);
            standart3Models.insert_data(connectionMySql, s);
        }
    }

    private static void copyStandartData4(Connection connectionSqlite, Connection connectionMySql, String fileName) {
        Standart4Models standart4Models = new Standart4Models();
        standart4Models.setTABLENAME(fileName);
        ObservableList<Standart4> observableList = standart4Models.get_data(connectionSqlite);
        for (Standart4 s: observableList) {
            standart4Models.insert_data(connectionMySql, s);
        }
    }

    private static void copyNarhlar(Connection connectionSqlite, Connection connectionMySql) {
        NarhModels narhModels = new NarhModels();
        ObservableList<Narh> observableList = narhModels.get_data(connectionSqlite);
        for (Narh n: observableList) {
            narhModels.insert_data(connectionMySql, n);
        }
    }

    private static void BarCodesTable(Connection connectionSqlite, Connection connectionMySql) {
        BarCodeModels barCodeModels = new BarCodeModels();
        ObservableList<BarCode> observableList = barCodeModels.get_data(connectionSqlite);
        for (BarCode bc: observableList) {
            barCodeModels.insert_data2(connectionMySql, bc);
        }
    }

    private static void TovarNarhi(Connection connectionSqlite, Connection connectionMySql) {
        TovarNarhiModels tovarNarhiModels = new TovarNarhiModels();
        ObservableList<TovarNarhi> observableList = tovarNarhiModels.get_data(connectionSqlite);
        for (TovarNarhi tn: observableList) {
            tovarNarhiModels.insert_data(connectionMySql, tn);
        }
    }

    private static void TovarSana(Connection connectionSqlite, Connection connectionMySql) {
        TovarSanaModels tovarSanaModels = new TovarSanaModels();
        ObservableList<TovarSana> observableList = tovarSanaModels.get_data(connectionSqlite);
        for (TovarSana ts: observableList) {
            tovarSanaModels.insert_data(connectionMySql, ts);
        }
    }

    private static void ValutaTable(Connection connectionSqlite, Connection connectionMySql) {
        ValutaModels valutaModels = new ValutaModels();
        ObservableList<Valuta> observableList = valutaModels.get_data(connectionSqlite);
        for (Valuta o: observableList) {
            valutaModels.insert_data(connectionMySql, o);
        }
    }

    private static void HisobTable(Connection connectionSqlite, Connection connectionMySql) {
        HisobModels hisobModels = new HisobModels();
        ObservableList<Hisob> observableList = hisobModels.get_data(connectionSqlite);
        for (Hisob o: observableList) {
            hisobModels.insert_data(connectionMySql, o);
        }
    }

    private static void HisobKitobTable(Connection connectionSqlite, Connection connectionMySql) {
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        ObservableList<HisobKitob> observableList = hisobKitobModels.get_data(connectionSqlite);
        for (HisobKitob o: observableList) {
            hisobKitobModels.insert_data(connectionMySql, o);
        }
    }

    private static void UserTable(Connection connectionSqlite, Connection connectionMySql) {
        UserModels userModels = new UserModels();
        ObservableList<User> observableList = userModels.getData(connectionSqlite);
        for (User o: observableList) {
            userModels.addUser(connectionMySql, o);
        }
    }

    private static void KassaTable(Connection connectionSqlite, Connection connectionMySql) {
        KassaModels kassaModels = new KassaModels();
        ObservableList<Kassa> observableList = kassaModels.get_data(connectionSqlite);
        for (Kassa o: observableList) {
            kassaModels.insert_data(connectionMySql, o);
        }
    }

    private static void QaydnomaTable(Connection connectionSqlite, Connection connectionMySql) {
        QaydnomaModel qaydnomaModel = new QaydnomaModel();
        ObservableList<QaydnomaData> observableList = qaydnomaModel.get_data(connectionSqlite);
        for (QaydnomaData o: observableList) {
            qaydnomaModel.insert_data(connectionMySql, o);
        }
    }

    private static void KursTable(Connection connectionSqlite, Connection connectionMySql) {
        KursModels kursModels = new KursModels();
        ObservableList<Kurs> observableList = kursModels.get_data(connectionSqlite);
        for (Kurs o: observableList) {
            kursModels.insert_data(connectionMySql, o);
        }
    }

    private static void SanoqQaydiTable(Connection connectionSqlite, Connection connectionMySql) {
        SanoqQaydiModels sanoqQaydiModels = new SanoqQaydiModels();
        ObservableList<SanoqQaydi> observableList = sanoqQaydiModels.get_data(connectionSqlite);
        for (SanoqQaydi o: observableList) {
            sanoqQaydiModels.insert_data(connectionMySql, o);
        }
    }
}
