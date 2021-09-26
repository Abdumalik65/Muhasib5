package sample.Config;

import javafx.collections.ObservableList;
import sample.Data.Aloqa;
import sample.Model.AloqaModels;
import sample.Tools.ConnectionType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySqlDBLocal {
    protected String dbHost;
    protected String dbPort;
    protected String dbUser;
    protected String dbPass;
    private String dataBaseName;
    Connection dbConnection;

    public MySqlDBLocal() {
        Connection connectionSqlite = new SqliteDBLocal().getDbConnection();
        AloqaModels aloqaModels = new AloqaModels();
        ObservableList<Aloqa> aloqas = aloqaModels.get_data(connectionSqlite);
        if (aloqas.size()>0) {
            Aloqa aloqa = aloqas.get(0);
            dbHost = aloqa.getDbHost();
            dbPort= aloqa.getDbPort();
            dbUser= aloqa.getDbUser();
            dbPass = aloqa.getDbPass();
            dataBaseName = aloqa.getDbName();
            System.out.println(dbHost + " | " + dbPort + " | " + dbUser + " | " + dbPass);
            ConnectionType.setIsRemoteConnection(false);
            ConnectionType.setAloqa(aloqa);
        }
    }

    public Connection getDbConnection() {
        String connectionString = "jdbc:mysql://" +
                dbHost + ":" +
                dbPort + "/" +
                dataBaseName +
                "?autoReconnect=true&createDatabaseIfNotExist=true";
        dbConnection = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            dbConnection = DriverManager.getConnection(connectionString, dbUser, dbPass);

            CreateTables createTables = new CreateTables(dbConnection);
            createTables.start();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
 //           LostConnection.losted();
        }
        System.out.println("Shu computerdagi MySql serverga ulandik");
        return dbConnection;
    }
    public String getDbHost() {
        return dbHost;
    }

    public void setDbHost(String dbHost) {
        this.dbHost = dbHost;
    }

    public String getDbPort() {
        return dbPort;
    }

    public void setDbPort(String dbPort) {
        this.dbPort = dbPort;
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public String getDbPass() {
        return dbPass;
    }

    public void setDbPass(String dbPass) {
        this.dbPass = dbPass;
    }

    public String getDataBaseName(String db_name) {
        return dataBaseName;
    }

    public void setDataBaseName(String dataBaseName) {
        this.dataBaseName = dataBaseName;
    }

}
