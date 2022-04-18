package sample.Config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CreateTables {
    Connection connection;

    public CreateTables(Connection connection) {
        this.connection = connection;
    }

    public void start() {
        try {
            AndozaTable(connection, "Amal");
            AndozaTable(connection, "Tovar");
            AndozaTable(connection, "Birlik");
            AndozaTable(connection, "NarhTuri");
            AndozaTable(connection, "AmalGuruhlari");
            AndozaTable(connection, "HisobGuruhlarNomi");
            AndozaTable(connection, "ValutaGuruhlari");
            AndozaTable(connection, "ChiqimShakli");
            AndozaTable(connection, "TolovShakli");
            AndozaTable(connection, "NarhTuri");
            AndozaTable(connection, "MijozTuri");
            AndozaTable(connection, "SavdoTuri");
            AndozaTable(connection, "QarzAmallari");
            AndozaTable(connection, "Tizimlar");
            AndozaTable(connection, "Dasturlar");
            Andoza6Table(connection, "BGuruh1");
            Andoza6Table(connection, "TGuruh1");
            AndozaTable(connection, "XodimMavqesi");
            AndozaTable(connection, "PulMavqesi");

            AndozaTable2(connection, "FoydaHisobi");
            AndozaTable2(connection, "Zarar");
            AndozaTable2(connection, "TranzitHisob");
            AndozaTable2(connection, "Xaridor");
            AndozaTable2(connection, "PulHisobi");
            AndozaTable2(connection, "NDS1");
            AndozaTable2(connection, "Chegirma");
            AndozaTable2(connection, "Bank");
            AndozaTable2(connection, "Bojxona");
            AndozaTable2(connection, "Tasdiq");
            AndozaTable2(connection, "QoshimchaDaromad");
            AndozaTable2(connection, "Mahsulot");
            AndozaTable2(connection, "BankXizmati");
            AndozaTable2(connection, "YordamchiHisobRoyxati");

            AndozaTable3(connection, "Bank1");
            AndozaTable3(connection, "BankXizmati1");
            AndozaTable3(connection, "FoydaHisobiGuruhi");
            AndozaTable3(connection, "TranzitHisobGuruhi");
            AndozaTable3(connection, "ZararGuruhi");
            AndozaTable3(connection, "Xaridor1");
            AndozaTable3(connection, "PulHisobi1");
            AndozaTable3(connection, "ChegirmaGuruhi");
            AndozaTable3(connection, "NDS2");
            AndozaTable3(connection, "BGuruh2");
            AndozaTable3(connection, "TGuruh2");
            AndozaTable3(connection, "HisobGuruhTarkibi");
            AndozaTable3(connection, "CheklanganHisobTarkibi");
            AndozaTable3(connection, "CheklanganDasturTarkibi");
            AndozaTable3(connection, "Bojxona2");
            AndozaTable3(connection, "Tasdiq2");
            AndozaTable3(connection, "QoshimchaDaromad2");
            AndozaTable3(connection, "Dokonlar");

            Andoza4Table(connection, "Tartib");
            Andoza4Table(connection, "Nds");
            Andoza4Table(connection, "BojxonaSoligi");

            BarCodesTable(connection);
            TovarNarhi(connection);
            TovarSana(connection);
            ValutaTable(connection);
            HisobTable(connection, "Hisob");
            HisobTable(connection, "Hisob1");
            HisobTable(connection, "Hisob2");
            HisobKitob(connection, "HisobKitob");
            HisobKitob(connection, "XomAshyo");
            HisobKitob(connection, "TayyorMaxsulot");
            HisobKitob(connection, "TempHisobKitob");
            UserTable(connection);
            KassaTable(connection);
            Qaydnoma(connection);
            Kurs(connection);
            SanoqQaydiTable(connection);
            HisobKitob(connection, "Sanoq");
            NarhTable(connection);
            GuruhNarhiTable(connection);
            SerialNumbersTable(connection, "SerialNumbers");
            PlasticFoizTable(connection, "PlasticFoiz");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    private void AndozaTable(Connection connection, String fileName) throws SQLException {
        String structure = "CREATE TABLE IF NOT EXISTS " +  fileName + " (id INT  PRIMARY KEY AUTO_INCREMENT, text varchar(200), userId INT, dateTime datetime NOT NULL DEFAULT CURRENT_TIMESTAMP)";
        PreparedStatement prSt = connection.prepareStatement(structure);
        prSt.executeUpdate();

    }
    private void AndozaTable2(Connection connection, String fileName) throws SQLException {
        String structure = "CREATE TABLE IF NOT EXISTS " +  fileName + " (id INT  PRIMARY KEY AUTO_INCREMENT, id2 INT, text varchar(200), userId INT, dateTime datetime NOT NULL DEFAULT CURRENT_TIMESTAMP)";
        PreparedStatement prSt = connection.prepareStatement(structure);
        prSt.executeUpdate();
    }
    private void AndozaTable3(Connection connection, String fileName) throws SQLException {
        String structure = "CREATE TABLE IF NOT EXISTS " +  fileName + " (id INT  PRIMARY KEY AUTO_INCREMENT, id2 INT, id3 INT, text varchar(200), userId INT, dateTime datetime NOT NULL DEFAULT CURRENT_TIMESTAMP)";
        PreparedStatement prSt = connection.prepareStatement(structure);
        prSt.executeUpdate();
    }
    private void AndozaTable7(Connection connection, String fileName) throws SQLException {
        String structure = "CREATE TABLE IF NOT EXISTS " +  fileName + " (id INT  PRIMARY KEY AUTO_INCREMENT, text varchar(200), aDouble double NOT NULL DEFAULT 1, userId INT, dateTime datetime NOT NULL DEFAULT CURRENT_TIMESTAMP)";
        PreparedStatement prSt = connection.prepareStatement(structure);
        prSt.executeUpdate();
    }

    private void BarCodesTable(Connection connection) throws SQLException {
        String structure = "CREATE TABLE IF NOT EXISTS BarCodes (id INT PRIMARY KEY AUTO_INCREMENT, tovar INT, barCode varchar(30), birlik INT, adad double, tarkib double, vazn double, hajm double, userId INT, dateTime datetime NOT NULL DEFAULT CURRENT_TIMESTAMP)";
        PreparedStatement prSt = connection.prepareStatement(structure);
        prSt.executeUpdate();
    }
    private void TovarNarhi(Connection connection) throws SQLException {
        String structure = "CREATE TABLE IF NOT EXISTS TovarNarhi (id INT  PRIMARY KEY AUTO_INCREMENT, sana datetime, tovar INT, narhTuri INT, valuta INT, kurs double, narh double, userId INT, dateTime datetime NOT NULL DEFAULT CURRENT_TIMESTAMP)";
        PreparedStatement prSt = connection.prepareStatement(structure);
        prSt.executeUpdate();
    }
    private void TovarSana(Connection connection) throws SQLException {
        String structure = "CREATE TABLE IF NOT EXISTS TovarSana (id INT  PRIMARY KEY AUTO_INCREMENT, sana datetime, tovar INT, userId INT, dateTime datetime NOT NULL DEFAULT CURRENT_TIMESTAMP)";
        PreparedStatement prSt = connection.prepareStatement(structure);
        prSt.executeUpdate();
    }
    private void ValutaTable(Connection connection) throws SQLException {
        String structure = "CREATE TABLE IF NOT EXISTS Valuta (id INT  PRIMARY KEY AUTO_INCREMENT, valuta varchar(100), status INT NOT NULL default 0, userId INT, dateTime datetime NOT NULL DEFAULT CURRENT_TIMESTAMP)";
        PreparedStatement prSt = connection.prepareStatement(structure);
        prSt.executeUpdate();
    }

    private void HisobTable(Connection connection, String fileName) throws SQLException {
        String structure = "CREATE TABLE IF NOT EXISTS " + fileName + " (id INT  PRIMARY KEY AUTO_INCREMENT, text varchar(100), balans double, rasm varchar(100), email varchar(100), mobile varchar(15), userId INT, dateTime datetime NOT NULL DEFAULT CURRENT_TIMESTAMP)";
        PreparedStatement prSt = connection.prepareStatement(structure);
        prSt.executeUpdate();
    }
    private void HisobKitob(Connection connection, String fileName) throws SQLException {
        String structure = "CREATE TABLE IF NOT EXISTS " + fileName + " (id INT  PRIMARY KEY AUTO_INCREMENT, qaydId INT, hujjatId INT, amal INT, hisob1 INT, hisob2 INT, valuta INT, tovar INT, kurs double, barCode varchar(30)  NOT NULL DEFAULT '', dona double, narh double, manba INT, izoh varchar(255)  NOT NULL DEFAULT '', userId INT, dateTime datetime NOT NULL DEFAULT CURRENT_TIMESTAMP)";
        PreparedStatement prSt = connection.prepareStatement(structure);
        prSt.executeUpdate();
    }
    private  void UserTable(Connection connection) {
        String structure = "CREATE TABLE IF NOT EXISTS Users (id INT  PRIMARY KEY AUTO_INCREMENT, ism varchar(100), rasm varchar(100), parol varchar(20), eMail varchar(100), phone varchar(100), status INT, jins varchar(20), online INT, userId INT, dateTime datetime NOT NULL DEFAULT CURRENT_TIMESTAMP)";
        PreparedStatement prSt = null;
        try {
            prSt = connection.prepareStatement(structure);
            prSt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void KassaTable(Connection connection) throws SQLException {
        String structure = "CREATE TABLE IF NOT EXISTS Kassa (id INT  PRIMARY KEY AUTO_INCREMENT, kassaNomi varchar(100), pulHisobi INT, xaridorHisobi INT, tovarHisobi INT, valuta INT, savdoTuri INT, serialNumber varchar(100)  NOT NULL DEFAULT '', online INT  NOT NULL DEFAULT 0, isLocked INT  NOT NULL DEFAULT 0, userId INT, dateTime datetime NOT NULL DEFAULT CURRENT_TIMESTAMP)";
        PreparedStatement prSt = connection.prepareStatement(structure);
        prSt.executeUpdate();
    }
    private void Qaydnoma(Connection connection) throws SQLException {
        String structure = "CREATE TABLE IF NOT EXISTS Qaydnoma (id INT  PRIMARY KEY AUTO_INCREMENT, amalTuri INT, hujjat INT, sana datetime, chiqimId INT, chiqimNomi varchar(100), kirimId INT, kirimNomi varchar(100), izoh varchar(255), jami double, status INT  NOT NULL DEFAULT 0, userId INT, dateTime datetime NOT NULL DEFAULT CURRENT_TIMESTAMP)";
        PreparedStatement prSt = connection.prepareStatement(structure);
        prSt.executeUpdate();
    }
    private void Kurs(Connection connection) throws SQLException {
        String structure = "CREATE TABLE IF NOT EXISTS Kurs (id INT  PRIMARY KEY AUTO_INCREMENT, sana datetime, valuta INT, kurs double, userId INT, dateTime datetime NOT NULL DEFAULT CURRENT_TIMESTAMP)";
        PreparedStatement prSt = connection.prepareStatement(structure);
        prSt.executeUpdate();
    }

    private void Andoza4Table(Connection connection, String fileName) throws SQLException {
        String structure = "CREATE TABLE IF NOT EXISTS " + fileName + " (id INT  PRIMARY KEY AUTO_INCREMENT, tovar INT, sana datetime, miqdor double, userId INT, dateTime datetime NOT NULL DEFAULT CURRENT_TIMESTAMP)";
        PreparedStatement prSt = connection.prepareStatement(structure);
        prSt.executeUpdate();
    }

    private void SanoqQaydiTable(Connection connection) throws SQLException {
        String structure = "CREATE TABLE IF NOT EXISTS SanoqQaydi (id INT  PRIMARY KEY AUTO_INCREMENT, sana datetime, hisob1 INT, hisob2 INT, balance double, izoh varchar(255)  NOT NULL DEFAULT '', userId INT, dateTime datetime NOT NULL DEFAULT CURRENT_TIMESTAMP)";
        PreparedStatement prSt = connection.prepareStatement(structure);
        prSt.executeUpdate();
    }

    private void NarhTable(Connection connection) throws SQLException {
        String structure = "CREATE TABLE IF NOT EXISTS XaridNarhi (id INT  PRIMARY KEY AUTO_INCREMENT, tovar INT, sana datetime, xarid double, userId INT, dateTime datetime NOT NULL DEFAULT CURRENT_TIMESTAMP)";
        PreparedStatement prSt = connection.prepareStatement(structure);
        prSt.executeUpdate();
    }

    private void Andoza6Table(Connection connection, String fileName) throws SQLException {
        String structure = "CREATE TABLE IF NOT EXISTS " + fileName + " (id INT  PRIMARY KEY AUTO_INCREMENT, text varchar(100), narh double, ulgurji double, chakana double, nds double, boj double, userId INT, dateTime datetime NOT NULL DEFAULT CURRENT_TIMESTAMP)";
        PreparedStatement prSt = connection.prepareStatement(structure);
        prSt.executeUpdate();
    }
    private void GuruhNarhiTable(Connection connection) throws SQLException {
        String structure = "CREATE TABLE IF NOT EXISTS GuruhNarhi (id INT  PRIMARY KEY AUTO_INCREMENT, sana datetime, guruhId INT, narhId INT, narhDouble double, userId INT, dateTime datetime NOT NULL DEFAULT CURRENT_TIMESTAMP)";
        PreparedStatement prSt = connection.prepareStatement(structure);
        prSt.executeUpdate();
    }

    private void SerialNumbersTable(Connection connection, String fileName) throws SQLException {
        String structure = "CREATE TABLE IF NOT EXISTS " + fileName + " (id INT  PRIMARY KEY AUTO_INCREMENT, sana datetime, hisob INT, invoice varchar(40), tovar int, serialNumber varchar(60), userId INT, dateTime datetime NOT NULL DEFAULT CURRENT_TIMESTAMP)";
        PreparedStatement prSt = connection.prepareStatement(structure);
        prSt.executeUpdate();
    }

    private void PlasticFoizTable(Connection connection, String fileName) throws SQLException {
        String structure = "CREATE TABLE IF NOT EXISTS " + fileName + " (id INT  PRIMARY KEY AUTO_INCREMENT, foiz double NOT NULL DEFAULT 0.02, userId INT, dateTime datetime NOT NULL DEFAULT CURRENT_TIMESTAMP)";
        PreparedStatement prSt = connection.prepareStatement(structure);
        prSt.executeUpdate();
    }
}
