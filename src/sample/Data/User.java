package sample.Data;

import java.util.Date;

public class User {
    private Integer id;
    private String ism;
    private  String rasm;
    private String parol;
    private String eMail;
    private String phone;
    private Integer status;
    private String jins;
    private Integer online;
    private Integer pulHisobi;
    private Integer xaridorHisobi;
    private Integer tovarHisobi;
    private Integer valuta;
    private Integer savdoTuri;
    private Integer userId;
    private Date dateTime;

    public User() {
        this.id = 0;
        this.ism = "";
        this.rasm = "";
        this.parol = "";
    }

    public User(Integer id, String ism) {
        this.id = id;
        this.ism = ism;
    }

    public User(String ism, String parol) {
        this.ism = ism;
        this.parol = parol;
    }

    public User(Integer id, String ism, String rasm, String parol) {
        this.id = id;
        this.ism = ism;
        this.rasm = rasm;
        this.parol = parol;
    }

    public User(Integer id, String ism, String rasm, String parol, String eMail, String phone, Integer status, String jins, Integer userId, Date dateTime) {
        this.id = id;
        this.ism = ism;
        this.rasm = rasm;
        this.parol = parol;
        this.eMail = eMail;
        this.phone = phone;
        this.status = status;
        this.jins = jins;
        this.userId = userId;
        this.dateTime = dateTime;
    }

    public User(Integer id, String ism, String rasm, String parol, String eMail, String phone, Integer status, String jins, Integer online, Integer userId, Date dateTime) {
        this.id = id;
        this.ism = ism;
        this.rasm = rasm;
        this.parol = parol;
        this.eMail = eMail;
        this.phone = phone;
        this.status = status;
        this.jins = jins;
        this.online = online;
        this.userId = userId;
        this.dateTime = dateTime;
    }

    public User(Integer id, String ism, String rasm, String parol, String eMail, String phone, Integer status, String jins, Integer online, Integer pulHisobi, Integer xaridorHisobi, Integer tovarHisobi, Integer valuta, Integer savdoTuri, Integer userId, Date dateTime) {
        this.id = id;
        this.ism = ism;
        this.rasm = rasm;
        this.parol = parol;
        this.eMail = eMail;
        this.phone = phone;
        this.status = status;
        this.jins = jins;
        this.online = online;
        this.userId = userId;
        this.dateTime = dateTime;
        this.pulHisobi = pulHisobi;
        this.xaridorHisobi = xaridorHisobi;
        this.tovarHisobi = tovarHisobi;
        this.valuta = valuta;
        this.savdoTuri = savdoTuri;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIsm() {
        return ism;
    }

    public void setIsm(String ism) {
        this.ism = ism;
    }

    public String getRasm() {
        return rasm;
    }

    public void setRasm(String rasm) {
        this.rasm = rasm;
    }

    public String getParol() {
        return parol;
    }

    public void setParol(String parol) {
        this.parol = parol;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getJins() {
        return jins;
    }

    public void setJins(String jins) {
        this.jins = jins;
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

    public Integer getOnline() {
        return online;
    }

    public void setOnline(Integer online) {
        this.online = online;
    }

    public Integer getPulHisobi() {
        return pulHisobi;
    }

    public void setPulHisobi(Integer pulHisobi) {
        this.pulHisobi = pulHisobi;
    }

    public Integer getXaridorHisobi() {
        return xaridorHisobi;
    }

    public void setXaridorHisobi(Integer xaridorHisobi) {
        this.xaridorHisobi = xaridorHisobi;
    }

    public Integer getTovarHisobi() {
        return tovarHisobi;
    }

    public void setTovarHisobi(Integer tovarHisobi) {
        this.tovarHisobi = tovarHisobi;
    }

    public Integer getValuta() {
        return valuta;
    }

    public void setValuta(Integer valuta) {
        this.valuta = valuta;
    }

    public Integer getSavdoTuri() {
        return savdoTuri;
    }

    public void setSavdoTuri(Integer savdoTuri) {
        this.savdoTuri = savdoTuri;
    }
}
