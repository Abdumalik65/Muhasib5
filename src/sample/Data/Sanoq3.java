package sample.Data;

import java.util.Date;

public class Sanoq3 {
    private Integer id;
    private Integer sanoqId;
    private Integer tovarId;
    private Double hisobiyAdad;
    private Double sanalganAdad;
    private  Integer userId;
    private Date dateTime;
    private double balans;
    private Integer valuta;
    private Double kurs;
    private double narh;

    public Sanoq3() {
    }

    public Sanoq3(Integer id, Integer sanoqId, Integer tovarId, Double hisobiyAdad, Double sanalganAdad, Integer userId, Date dateTime) {
        this.id = id;
        this.sanoqId = sanoqId;
        this.tovarId = tovarId;
        this.hisobiyAdad = hisobiyAdad;
        this.sanalganAdad = sanalganAdad;
        this.userId = userId;
        this.dateTime = dateTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSanoqId() {
        return sanoqId;
    }

    public void setSanoqId(Integer sanoqId) {
        this.sanoqId = sanoqId;
    }

    public Integer getTovarId() {
        return tovarId;
    }

    public void setTovarId(Integer tovarId) {
        this.tovarId = tovarId;
    }

    public Double getHisobiyAdad() {
        return hisobiyAdad;
    }

    public void setHisobiyAdad(Double hisobiyAdad) {
        this.hisobiyAdad = hisobiyAdad;
    }

    public Double getSanalganAdad() {
        return sanalganAdad;
    }

    public void setSanalganAdad(Double sanalganAdad) {
        this.sanalganAdad = sanalganAdad;
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

    public double getBalans() {
        return sanalganAdad-hisobiyAdad;
    }

    public void setBalans(double balans) {
        this.balans = balans;
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

    public double getNarh() {
        return narh;
    }

    public void setNarh(double narh) {
        this.narh = narh;
    }
}
