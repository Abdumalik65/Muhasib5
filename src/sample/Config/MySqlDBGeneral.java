package sample.Config;

import jdk.nashorn.internal.ir.GetSplitState;
import sample.Controller.SetServer;
import sample.Data.Aloqa;
import sample.Model.AloqaModels;
import sample.Tools.ConnectionType;
import sample.Tools.LostConnection;
import sample.Enums.ServerType;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySqlDBGeneral {
    protected String dbHost;
    protected String dbPort;
    protected String dbUser;
    protected String dbPass;
    private String dataBaseName;
    private String setting = "?useUnicode=true" +
            "&sessionVariables=sql_mode=(SELECT REPLACE(@@sql_mode,'ONLY_FULL_GROUP_BY',''))" +
            "&useJDBCCompliantTimezoneShift=true" +
            "&useLegacyDatetimeCode=false" +
            "&serverTimezone=UTC" +
            "&autoReconnect=true" +
            "&createDatabaseIfNotExist=true";

    Connection dbConnection;
    private static Integer hostId;
    private ServerType serverType;

    public MySqlDBGeneral() {
        this.serverType = ServerType.LOCAL;
        this.hostId = 1;
        setValues();
    }

    public MySqlDBGeneral(ServerType serverType) {
        this.serverType = serverType;
        if (serverType.equals(ServerType.LOCAL)) {
            this.hostId = 1;
        } else if(serverType.equals(ServerType.REMOTE)) {
            this.hostId = 2;
        }
        MyServer.setId(hostId);
        setValues();
    }

    private Aloqa setValues() {
        Connection connectionSqlite = new SqliteDB().getDbConnection();
        AloqaModels aloqaModels = new AloqaModels();
        Aloqa aloqa = aloqaModels.getWithId(connectionSqlite, hostId);
        if (aloqa!=null) {
            dbHost = aloqa.getDbHost();
            dbPort= aloqa.getDbPort();
            dbUser= aloqa.getDbUser();
            dbPass = aloqa.getDbPass();
            dataBaseName = aloqa.getDbName() ;
            System.out.println(dataBaseName + " | " + dbHost + " | " + dbPort + " | " + dbUser + " | " + dbPass);
            ConnectionType.setIsRemoteConnection(false);
            ConnectionType.setAloqa(aloqa);
        }
        return aloqa;
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
            if (serverType.equals(ServerType.LOCAL)) {
                System.out.println("Shu computerdagi MySql serverga ulandik");
            } else if(serverType.equals(ServerType.REMOTE)) {
                System.out.println("Boshqa computerdagi MySql serverga ulandik");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("MySql serverga ulana olmadik");
        } catch (SQLException e) {
            e.printStackTrace();
//            LostConnection.losted();
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

    public void setDbConnection(Connection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public static Integer getHostId() {
        return hostId;
    }

    public static void setHostId(Integer hostId) {
        MySqlDBGeneral.hostId = hostId;
    }
}
