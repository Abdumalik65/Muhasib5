package sample.Data;

import java.util.Date;

public class TovarNarhi {
    private Integer id;
    private Date sana;
    private Integer tovar;
    private Integer narhTuri;
    private Integer valuta;
    private Double kurs;
    private Double narh;
    private Integer userId;
    private Date dateTime;

    public TovarNarhi() {
        id = 0;
        sana = new Date();
        tovar = 0;
        narhTuri = 0;
        valuta = 1;
        kurs = 1.0;
        narh = .0;
        userId = 0;
        dateTime  = new Date();
    }

    public TovarNarhi(Integer id, Date sana, Integer tovar, Integer narhTuri, Integer valuta, Double kurs, Double narh, Integer userId, Date dateTime) {
        this.id = id;
        this.sana = sana;
        this.tovar = tovar;
        this.narhTuri = narhTuri;
        this.valuta = valuta;
        this.kurs = kurs;
        this.narh = narh;
        this.userId = userId;
        this.dateTime = dateTime;
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

    public Integer getTovar() {
        return tovar;
    }

    public void setTovar(Integer tovar) {
        this.tovar = tovar;
    }

    public Integer getNarhTuri() {
        return narhTuri;
    }

    public void setNarhTuri(Integer narhTuri) {
        this.narhTuri = narhTuri;
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

    public Double getNarh() {
        return narh;
    }

    public void setNarh(Double narh) {
        this.narh = narh;
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
