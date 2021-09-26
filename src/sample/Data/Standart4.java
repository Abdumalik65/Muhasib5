package sample.Data;

import java.util.Date;

public class Standart4 {
    private Integer id;
    private Integer tovar;
    private Date sana;
    private Double miqdor;
    private Integer userId;
    private Date dateTime;
    private Boolean changed = false;

    public Standart4() {}

    public Standart4(Integer id, Integer tovar, Date sana, Double miqdor, Integer userId, Date dateTime) {
        this.id = id;
        this.tovar = tovar;
        this.sana = sana;
        this.miqdor = miqdor;
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

    public Date getSana() {
        return sana;
    }

    public void setSana(Date sana) {
        this.sana = sana;
    }

    public Double getMiqdor() {
        return miqdor;
    }

    public void setMiqdor(Double miqdor) {
        this.miqdor = miqdor;
        changed = true;
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

    public Boolean getChanged() {
        return changed;
    }

    public void setChanged(Boolean changed) {
        this.changed = changed;
    }
}
