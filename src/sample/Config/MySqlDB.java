package sample.Config;

import javafx.collections.ObservableList;
import sample.Data.Aloqa;
import sample.Enums.ServerType;
import sample.Model.AloqaModels;
import sample.Tools.ConnectionType;
import sample.Tools.LostConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySqlDB {
    protected String dbHost;
    protected String dbPort;
    protected String dbUser;
    protected String dbPass;
    private String dataBaseName;
    private String setting = "?useUnicode=true" +
            "&" +
            "sessionVariables=sql_mode=(SELECT REPLACE(@@sql_mode,'ONLY_FULL_GROUP_BY',''))" +
            "&" +
            "useJDBCCompliantTimezoneShift=true" +
            "&" +
            "useLegacyDatetimeCode=false" +
            "&" +
            "serverTimezone=UTC" +
            "&" +
            "autoReconnect=true" +
            "&" +
            "createDatabaseIfNotExist=true";

    Connection dbConnection;

    public MySqlDB() {
        Connection connectionSqlite = new SqliteDBRemote().getDbConnection();
        AloqaModels aloqaModels = new AloqaModels();
        ObservableList<Aloqa> aloqas = aloqaModels.get_data(connectionSqlite);
        if (aloqas.size()>0) {
            Aloqa aloqa = aloqas.get(0);
            dbHost = aloqa.getDbHost();
//            dbHost = "192.168.1.108";
            dbPort= aloqa.getDbPort();
            dbUser= aloqa.getDbUser();
            dbPass = aloqa.getDbPass();
            dataBaseName = aloqa.getDbName() ;
//            dataBaseName = "BestParfumery" ;
            System.out.println(dataBaseName + " | " + dbHost + " | " + dbPort + " | " + dbUser + " | " + dbPass);
            ConnectionType.setIsRemoteConnection(false);
            ConnectionType.setAloqa(aloqa);
        }
    }

    public Connection getDbConnection() {
        String connectionString = "jdbc:mysql://" +
                dbHost + ":" +
                dbPort + "/" +
                dataBaseName + setting;
        dbConnection = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            dbConnection = DriverManager.getConnection(connectionString, dbUser, dbPass);
            CreateTables createTables = new CreateTables(dbConnection);
            createTables.start();
            System.out.println("Boshqa computerdagi MySql serverga ulandik");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("MySql serverga ulana olmadik");
        } catch (SQLException e) {
            e.printStackTrace();
            LostConnection.losted();
            System.out.println("MySql serverga ulana olmadik");
        }
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

    public static Boolean connectionIsValid(Connection connection) {
        Boolean valid = false;
        try {
            if (connection.isValid(100)) {
                System.out.println("Valid");
            }else{
                System.out.println("Not valid");
                connection = new MySqlDB().getDbConnection();
                valid = connection.isValid(100);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return valid;
    }

    public void setDbConnection(Connection dbConnection) {
        this.dbConnection = dbConnection;
    }
}
