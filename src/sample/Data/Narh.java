package sample.Data;

import java.util.Date;

public class Narh {
    private Integer id;
    private Integer tovar;
    private Date sana;
    private Double xaridDouble;
    private Integer userId;
    private Date dateTime;

    public Narh() {}

    public Narh(Integer id, Integer tovar, Date sana, Double xaridDouble, Integer userId, Date dateTime) {
        this.id = id;
        this.tovar = tovar;
        this.sana = sana;
        this.xaridDouble = xaridDouble;
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

    public Double getXaridDouble() {
        return xaridDouble;
    }

    public void setXaridDouble(Double xaridDouble) {
        this.xaridDouble = xaridDouble;
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
