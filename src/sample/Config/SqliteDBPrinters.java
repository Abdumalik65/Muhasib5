package sample.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SqliteDBPrinters {
    private String dataBaseName = "Printers.db";
    Connection dbConnection;

    public SqliteDBPrinters() {
    }

    public SqliteDBPrinters(String dataBaseName) {
        this.dataBaseName = dataBaseName;
    }

    public Connection getDbConnection() {
        String connectionString = "jdbc:sqlite:" + dataBaseName;
        try {
            Class.forName("org.sqlite.JDBC");
            dbConnection = DriverManager.getConnection(connectionString);
            PrintersTable(dbConnection, "Printers");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }


        return dbConnection;
    }

    public String getDataBaseName(String db_name) {
        return dataBaseName;
    }

    public void setDataBaseName(String dataBaseName) {
        this.dataBaseName = dataBaseName;
    }

    private void PrintersTable(Connection connection, String tableName) throws SQLException {
        String structure = "CREATE TABLE IF NOT EXISTS " +  tableName + " (id integer PRIMARY KEY, text text(150), userId integer, dateTime datetime DEFAULT (datetime('now','localtime')))";
        PreparedStatement prSt = connection.prepareStatement(structure);
        prSt.executeUpdate();
    }
}

