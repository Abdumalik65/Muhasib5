package sample.Data;

import java.util.Date;

public class Aloqa {
    private Integer id;
    private String text;
    private String dbHost;
    private String dbPort;
    private String dbUser;
    private String dbPass;
    private String dbName;
    private String dbPrefix;
    private Integer userId;
    private Date dateTime;

    public Aloqa() {
        id = 0;
        text = "";
        dbHost = "";
        dbPort = "";
        dbUser = "";
        dbPass = "";
        dbName = "";
        dbPrefix = "";
        userId = 0;
        dateTime = new Date();
    }

    public Aloqa(Integer id, String text, String dbHost, String dbPort, String dbUser, String dbPass, String dbName, String dbPrefix, Integer userId, Date dateTime) {
        this.id = id;
        this.text = text;
        this.dbHost = dbHost;
        this.dbPort = dbPort;
        this.dbUser = dbUser;
        this.dbPass = dbPass;
        this.dbName = dbName;
        this.dbPrefix = dbPrefix;
        this.userId = userId;
        this.dateTime = dateTime;
    }

    public Aloqa(String dbHost, String dbPort, String dbUser, String dbPass, String dbName, String dbPrefix) {
        this.dbHost = dbHost;
        this.dbPort = dbPort;
        this.dbUser = dbUser;
        this.dbPass = dbPass;
        this.dbName = dbName;
        this.dbPrefix = dbPrefix;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getDbPrefix() {
        return dbPrefix;
    }

    public void setDbPrefix(String dbPrefix) {
        this.dbPrefix = dbPrefix;
    }
}
