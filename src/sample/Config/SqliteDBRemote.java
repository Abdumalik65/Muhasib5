package sample.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SqliteDBRemote {
    private String dataBaseName = "MuhasibRemote.db";
    Connection dbConnection;

    public SqliteDBRemote() {
    }

    public SqliteDBRemote(String dataBaseName) {
        this.dataBaseName = dataBaseName;
    }

    public Connection getDbConnection() {
        String connectionString = "jdbc:sqlite:" + dataBaseName;
        try {
            Class.forName("org.sqlite.JDBC");
            dbConnection = DriverManager.getConnection(connectionString);
            AndozaTable(dbConnection, "AdressComp");
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

    private void AndozaTable(Connection connection, String tableName) throws SQLException {
        String structure = "CREATE TABLE IF NOT EXISTS " +  tableName + " (id integer PRIMARY KEY, text text(50), dbHost text(15), dbPort text(10), dbUser text(30), dbPass text(10), databeName text(20), userId integer, dateTime datetime DEFAULT (datetime('now','localtime')))";
        PreparedStatement prSt = connection.prepareStatement(structure);
        prSt.executeUpdate();
    }
}

