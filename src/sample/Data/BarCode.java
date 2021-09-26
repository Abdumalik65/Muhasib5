package sample.Data;

import java.util.Date;

public class BarCode {
    private Integer id;
    private Integer tovar;
    private String barCode;
    private Integer birlik;
    private double adad;
    private Integer tarkib;
    private Double vazn;
    private Double hajm;
    private Integer userId;
    private Date dateTime;
    Boolean changed = false;

    public BarCode() {
    }

    public BarCode(Integer id, Integer tovar, String barCode, Integer birlik, double adad, Integer tarkib, Double vazn, Double hajm, Integer userId, Date dateTime) {
        this.id = id;
        this.tovar = tovar;
        this.barCode = barCode;
        this.birlik = birlik;
        this.adad = adad;
        this.tarkib = tarkib;
        this.vazn = vazn;
        this.hajm = hajm;
        this.userId = userId;
        this.dateTime = dateTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTovar() {
        return tovar;
    }

    public void setTovar(Integer tovar) {
        this.tovar = tovar;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public Integer getBirlik() {
        return birlik;
    }

    public void setBirlik(Integer birlik) {
        this.birlik = birlik;
    }

    public double getAdad() {
        return adad;
    }

    public void setAdad(double adad) {
        this.adad = adad;
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

    public Integer getTarkib() {
        return tarkib;
    }

    public void setTarkib(Integer tarkib) {
        this.tarkib = tarkib;
    }

    public Double getVazn() {
        return vazn;
    }

    public void setVazn(Double vazn) {
        this.vazn = vazn;
        changed = true;
    }

    public Double getHajm() {
        return hajm;
    }

    public void setHajm(Double hajm) {
        this.hajm = hajm;
        changed = true;
    }

    public Boolean getChanged() {
        return changed;
    }

    public void setChanged(Boolean changed) {
        this.changed = changed;
    }

    @Override
    public String toString() {
        return barCode;
    }
}
