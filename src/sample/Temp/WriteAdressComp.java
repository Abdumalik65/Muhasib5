package sample.Temp;

import sample.Config.SqliteDBLocal;
import sample.Data.Standart;
import sample.Model.StandartModels;
import sample.Tools.Encryptor;

import java.sql.Connection;

public class WriteAdressComp {
    public static void main(String[] args) {
        Connection connection = new SqliteDBLocal().getDbConnection();
        System.out.println(connection);
        StandartModels standartModels = new StandartModels();
        standartModels.setTABLENAME("AdressComp");
        String enCryptString = Encryptor.encrypt("MacOsComp01");
        System.out.println(enCryptString);
        Standart standart = new Standart(null, enCryptString, 1, null);
        standartModels.insert_data(connection, standart);
    }
}
