package sample.Temp;

import sample.Config.MySqlDBGeneral;
import sample.Config.RemoteToLocal;
import sample.Enums.ServerType;

import java.sql.Connection;

public class TestMySqlConnection {
    public static void main(String[] args) {
        Connection connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        RemoteToLocal.TruncateTables(connection);
    }
}
