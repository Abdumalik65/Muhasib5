package sample.Data;

import java.util.Date;

public class Kurs {
    private Integer id;
    private Date sana;
    private Integer valuta;
    private Double kurs;
    private Integer userId;
    private Date dateTime;

    public Kurs(Integer id, Date sana, Integer valuta, Double kurs, Integer userId, Date dateTime) {
        this.id = id;
        this.sana = sana;
        this.valuta = valuta;
        this.kurs = kurs;
        this.userId = userId;
        this.dateTime = dateTime;
    }

    public Kurs() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getSana() {
        return sana;
    }

    public void setSana(Date sana) {
        this.sana = sana;
    }

    public Integer getValuta() {
        return valuta;
    }

    public void setValuta(Integer valuta) {
        this.valuta = valuta;
    }

    public Double getKurs() {
        return kurs;
    }

    public void setKurs(Double kurs) {
        this.kurs = kurs;
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
