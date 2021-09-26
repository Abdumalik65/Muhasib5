package sample.Config;

import javafx.collections.ObservableList;
import sample.Data.Aloqa;
import sample.Model.AloqaModels;
import sample.Tools.ConnectionType;
import sample.Tools.LostConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySqlDBStatic {
    protected static String dbHost;
    protected static String dbPort;
    protected static String dbUser;
    protected static String dbPass;
    public static String dataBaseName;
    public static Connection connection;

    public MySqlDBStatic(String serverType) {
        Connection connectionSqlite = new SqliteDBRemote().getDbConnection();
        Aloqa aloqa = null;
        AloqaModels aloqaModels = new AloqaModels();
        ObservableList<Aloqa> aloqas = aloqaModels.get_data(connectionSqlite);
        if (aloqas.size()==2) {
            Boolean isRemoteConnection = false;
            if (serverType.equals("local")) {
                aloqa = aloqas.get(0);
            } else if (serverType.equals("remote")) {
                isRemoteConnection = true;
                aloqa = aloqas.get(1);
            }
            dbHost = aloqa.getDbHost();
            dbPort= aloqa.getDbPort();
            dbUser= aloqa.getDbUser();
            dbPass = aloqa.getDbPass();
            dataBaseName = aloqa.getDbName() + aloqa.getDbPrefix() +
                    "?autoReconnect=true&createDatabaseIfNotExist=true";
            System.out.println(dbHost + " | " + dbPort + " | " + dbUser + " | " + dbPass);
            ConnectionType.setIsRemoteConnection(isRemoteConnection);
            ConnectionType.setAloqa(aloqa);
        } else {
            System.out.println("Bu computerda server sozlanmagan");
        }
    }

    public static Connection getConnection() {
        String connectionString = "jdbc:mysql://" +
                dbHost + ":" +
                dbPort + "/" +
                dataBaseName;
        connection = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(connectionString, dbUser, dbPass);
            CreateTables createTables = new CreateTables(connection);
            createTables.start();
            if (ConnectionType.getIsRemoteConnection()) {
                System.out.println("Boshqa computerdagi MySql serverga ulandik");
            } else {
                System.out.println("Shu computerdagi MySql serverga ulandik");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
            LostConnection.losted();
        }
        return connection;
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
