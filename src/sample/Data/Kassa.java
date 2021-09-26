package sample.Data;

import java.util.Date;

public class Kassa {
    private Integer id;
    private String kassaNomi;
    private Integer pulHisobi;
    private Integer xaridorHisobi;
    private Integer tovarHisobi;
    private Integer valuta;
    private Integer savdoTuri;
    private String serialNumber;
    private Integer online;
    private Integer isLocked;
    private Integer userId;
    private Date dateTime;

    public Kassa(String kassaNomi) {
        this.kassaNomi = kassaNomi;
    }

    public Kassa(Integer id, String kassaNomi, Integer pulHisobi, Integer xaridorHisobi, Integer tovarHisobi, Integer valuta, Integer savdoTuri, String serialNumber, Integer online, Integer isLocked, Integer userId, Date dateTime) {
        this.id = id;
        this.kassaNomi = kassaNomi;
        this.pulHisobi = pulHisobi;
        this.xaridorHisobi = xaridorHisobi;
        this.tovarHisobi = tovarHisobi;
        this.valuta = valuta;
        this.savdoTuri = savdoTuri;
        this.serialNumber = serialNumber;
        this.online = online;
        this.isLocked = isLocked;
        this.userId = userId;
        this.dateTime = dateTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKassaNomi() {
        return kassaNomi;
    }

    public void setKassaNomi(String kassaNomi) {
        this.kassaNomi = kassaNomi;
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

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Integer getOnline() {
        return online;
    }

    public void setOnline(Integer online) {
        this.online = online;
    }

    public Integer getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(Integer isLocked) {
        this.isLocked = isLocked;
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
}
