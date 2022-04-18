package sample.Config;

import com.mysql.cj.jdbc.DatabaseMetaData;
import javafx.collections.ObservableList;
import sample.Data.*;
import sample.Enums.ServerType;
import sample.Model.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RemoteToLocalYangi {
    public static void main(String[] args) {
        Connection sourceCconnection = new MySqlDBGeneral(ServerType.REMOTE).getDbConnection();
        Connection targetConnection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        TruncateTables(targetConnection);
        copyStandartData(sourceCconnection, targetConnection, "Amal");
        copyStandartData(sourceCconnection, targetConnection, "Tovar");
        copyStandartData(sourceCconnection, targetConnection, "Birlik");
        copyStandartData(sourceCconnection, targetConnection, "NarhTuri");

        copyStandartData(sourceCconnection, targetConnection, "AmalGuruhlari");
        copyStandartData(sourceCconnection, targetConnection, "HisobGuruhlarNomi");

        copyStandartData(sourceCconnection, targetConnection, "ChiqimShakli");
        copyStandartData(sourceCconnection, targetConnection, "TolovShakli");
        copyStandartData(sourceCconnection, targetConnection, "MijozTuri");
        copyStandartData(sourceCconnection, targetConnection, "SavdoTuri");
        copyStandartData(sourceCconnection, targetConnection, "QarzAmallari");
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
        copyStandartData3(sourceCconnection, targetConnection, "TGuruh2");

        System.out.println("Standart3");

        copyStandartData4(sourceCconnection, targetConnection, "Tartib");
        copyStandartData4(sourceCconnection, targetConnection, "Nds");
        copyNarhlar(sourceCconnection, targetConnection);
        System.out.println("Standart4");

        copyStandartData6(sourceCconnection, targetConnection, "TGuruh1");
        System.out.println("Standart6");

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
        HisobKitobTable(sourceCconnection, targetConnection);
        System.out.println("HisobKitob");
        UserTable(sourceCconnection, targetConnection);
        System.out.println("Users");
        KassaTable(sourceCconnection, targetConnection);
        System.out.println("Kassa");
        QaydnomaTable(sourceCconnection, targetConnection);
        System.out.println("Qaydnoma");
        KursTable(sourceCconnection, targetConnection);
        System.out.println("Kurslar");
//        SanoqQaydiTable(sourceCconnection, targetConnection);
//        System.out.println("Sanoq qaydi");
    }

    private static void copyStandartData(Connection sourceConnection, Connection targetConnection, String fileName) {
        StandartModels standartModels = new StandartModels();
        standartModels.setTABLENAME(fileName);
        ObservableList<Standart> observableList = standartModels.get_data(sourceConnection);
        for (Standart s : observableList) {
            String string1 = s.getText().trim();
            if (string1.length() > 80) {
                string1 = string1.substring(0, 79);
            }
            s.setText(string1);
            standartModels.insert_data2(targetConnection, s);
        }
    }

    private static void copyStandartData2(Connection sourceConnection, Connection targetConnection, String fileName) {
        Standart2Models standart2Models = new Standart2Models();
        standart2Models.setTABLENAME(fileName);
        ObservableList<Standart2> observableList = standart2Models.get_data(sourceConnection);
        for (Standart2 s : observableList) {
            String string1 = s.getText().trim();
            if (string1.length() > 80) {
                string1 = string1.substring(0, 79);
            }
            s.setText(string1);
            standart2Models.insert_data(targetConnection, s);
        }
    }

    private static void copyStandartData3(Connection sourceConnection, Connection targetConnection, String fileName) {
        Standart3Models standart3Models = new Standart3Models();
        standart3Models.setTABLENAME(fileName);
        ObservableList<Standart3> observableList = standart3Models.get_data(sourceConnection);
        for (Standart3 s : observableList) {
            String string1 = s.getText().trim();
            if (string1.length() > 80) {
                string1 = string1.substring(0, 79);
            }
            s.setText(string1);
            standart3Models.insert_data(targetConnection, s);
        }
    }

    private static void copyStandartData4(Connection sourceConnection, Connection targetConnection, String fileName) {
        Standart4Models standart4Models = new Standart4Models();
        standart4Models.setTABLENAME(fileName);
        ObservableList<Standart4> observableList = standart4Models.get_data(sourceConnection);
        for (Standart4 s : observableList) {
            standart4Models.insert_data(targetConnection, s);
        }
    }

    private static void copyStandartData6(Connection sourceConnection, Connection targetConnection, String fileName) {
        Standart6Models standart6Models = new Standart6Models();
        standart6Models.setTABLENAME(fileName);
        ObservableList<Standart6> observableList = standart6Models.get_data(sourceConnection);
        for (Standart6 s : observableList) {
            standart6Models.insert_data(targetConnection, s);
        }
    }

    private static void copyNarhlar(Connection sourceConnection, Connection targetConnection) {
        NarhModels narhModels = new NarhModels();
        ObservableList<Narh> observableList = narhModels.get_data(sourceConnection);
        for (Narh n : observableList) {
            narhModels.insert_data(targetConnection, n);
        }
    }

    private static void BarCodesTable(Connection sourceConnection, Connection targetConnection) {
        BarCodeModels barCodeModels = new BarCodeModels();
        ObservableList<BarCode> observableList = barCodeModels.get_data(sourceConnection);
        for (BarCode bc : observableList) {
            barCodeModels.insert_data2(targetConnection, bc);
        }
    }

    private static void TovarNarhi(Connection sourceConnection, Connection targetConnection) {
        TovarNarhiModels tovarNarhiModels = new TovarNarhiModels();
        ObservableList<TovarNarhi> observableList = tovarNarhiModels.get_data(sourceConnection);
        for (TovarNarhi tn : observableList) {
            tovarNarhiModels.insert_data(targetConnection, tn);
        }
    }

    private static void TovarSana(Connection sourceConnection, Connection targetConnection) {
        TovarSanaModels tovarSanaModels = new TovarSanaModels();
        ObservableList<TovarSana> observableList = tovarSanaModels.get_data(sourceConnection);
        for (TovarSana ts : observableList) {
            tovarSanaModels.insert_data(targetConnection, ts);
        }
    }

    private static void ValutaTable(Connection sourceConnection, Connection targetConnection) {
        ValutaModels valutaModels = new ValutaModels();
        ObservableList<Valuta> observableList = valutaModels.get_data(sourceConnection);
        for (Valuta o : observableList) {
            valutaModels.insert_data(targetConnection, o);
        }
    }

    private static void HisobTable(Connection sourceConnection, Connection targetConnection) {
        HisobModels hisobModels = new HisobModels();
        ObservableList<Hisob> observableList = hisobModels.get_data(sourceConnection);
        for (Hisob o : observableList) {
            hisobModels.insert_data(targetConnection, o);
        }
    }

    private static void HisobKitobTable(Connection sourceConnection, Connection targetConnection) {
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        ObservableList<HisobKitob> observableList = hisobKitobModels.get_data(sourceConnection);
        hisobKitobModels.addBatchWithId(targetConnection, observableList);
    }

    private static void UserTable(Connection sourceConnection, Connection targetConnection) {
        UserModels userModels = new UserModels();
        ObservableList<User> observableList = userModels.getData(sourceConnection);
        for (User o : observableList) {
            userModels.addUser(targetConnection, o);
        }
    }

    private static void KassaTable(Connection sourceConnection, Connection targetConnection) {
        KassaModels kassaModels = new KassaModels();
        ObservableList<Kassa> observableList = kassaModels.get_data(sourceConnection);
        for (Kassa o : observableList) {
            kassaModels.insert_data(targetConnection, o);
        }
    }

    private static void QaydnomaTable(Connection sourceConnection, Connection targetConnection) {
        QaydnomaModel qaydnomaModel = new QaydnomaModel();
        ObservableList<QaydnomaData> observableList = qaydnomaModel.get_data(sourceConnection);
        qaydnomaModel.addBatchWithId(targetConnection, observableList);
    }

    private static void KursTable(Connection sourceConnection, Connection targetConnection) {
        KursModels kursModels = new KursModels();
        ObservableList<Kurs> observableList = kursModels.get_data(sourceConnection);
        for (Kurs o : observableList) {
            kursModels.insert_data(targetConnection, o);
        }
    }

    private static void SanoqQaydiTable(Connection sourceConnection, Connection targetConnection) {
        SanoqQaydiModels sanoqQaydiModels = new SanoqQaydiModels();
        ObservableList<SanoqQaydi> observableList = sanoqQaydiModels.get_data(sourceConnection);
        for (SanoqQaydi o : observableList) {
            sanoqQaydiModels.insert_data(targetConnection, o);
        }
    }

    public static void TruncateTables(Connection connection) {
        String tableName;
        Statement statement = null;
        DatabaseMetaData md = null;
        try {
            md = (DatabaseMetaData) connection.getMetaData();
            String databaseName = connection.getCatalog();
            ResultSet rs = md.getTables(databaseName, databaseName, "%", null);

            while (rs.next()) {
                tableName = rs.getString(3);
/*
                statement = connection.createStatement();
                int result = statement.executeUpdate("TRUNCATE " + tableName);
*/
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
