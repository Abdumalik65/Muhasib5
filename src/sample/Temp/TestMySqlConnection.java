package sample.Temp;

import sample.Config.MySqlDBLocal;
import sample.Config.RemoteToLocal;

import java.sql.Connection;

public class TestMySqlConnection {
    public static void main(String[] args) {
        Connection connection = new MySqlDBLocal().getDbConnection();
        RemoteToLocal.TruncateTables(connection);
    }
}
