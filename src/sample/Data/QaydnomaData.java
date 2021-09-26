package sample.Data;

import java.util.Date;

public class QaydnomaData {
    private Integer id;
    private  Integer amalTuri;
    private Integer hujjat;
    private Date sana;
    private Integer chiqimId;
    private String chiqimNomi;
    private Integer kirimId;
    private String kirimNomi;
    private String izoh;
    private Double jami;
    private Integer status = 0;
    private Integer userId;
    private Date dateTime;

    public QaydnomaData() {
        hujjat = 0;
        sana = new Date();
        chiqimNomi = "";
        kirimNomi = "";
        izoh = "";
        jami = .00;
    }

    public QaydnomaData(Integer id, Integer amalTuri, Integer hujjat, Date sana, Integer chiqimId, String chiqimNomi, Integer kirimId, String kirimNomi, String izoh, Double jami, Integer status, Integer userId, Date dateTime) {
        this.id = id;
        this.amalTuri = amalTuri;
        this.hujjat = hujjat;
        this.sana = sana;
        this.chiqimId = chiqimId;
        this.chiqimNomi = chiqimNomi;
        this.kirimId = kirimId;
        this.kirimNomi = kirimNomi;
        this.izoh = izoh;
        this.jami = jami;
        this.status = status;
        this.userId = userId;
        this.dateTime = dateTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAmalTuri() {
        return amalTuri;
    }

    public void setAmalTuri(Integer amalTuri) {
        this.amalTuri = amalTuri;
    }

    public Integer getHujjat() {
        return hujjat;
    }

    public void setHujjat(Integer hujjat) {
        this.hujjat = hujjat;
    }

    public Date getSana() {
        return sana;
    }

    public void setSana(Date sana) {
        this.sana = sana;
    }

    public Integer getChiqimId() {
        return chiqimId;
    }

    public void setChiqimId(Integer chiqimId) {
        this.chiqimId = chiqimId;
    }

    public String getChiqimNomi() {
        return chiqimNomi;
    }

    public void setChiqimNomi(String chiqimNomi) {
        this.chiqimNomi = chiqimNomi;
    }

    public Integer getKirimId() {
        return kirimId;
    }

    public void setKirimId(Integer kirimId) {
        this.kirimId = kirimId;
    }

    public String getKirimNomi() {
        return kirimNomi;
    }

    public void setKirimNomi(String kirimNomi) {
        this.kirimNomi = kirimNomi;
    }

    public String getIzoh() {
        return izoh;
    }

    public void setIzoh(String izoh) {
        this.izoh = izoh;
    }

    public Double getJami() {
        return jami;
    }

    public void setJami(Double jami) {
        this.jami = jami;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
