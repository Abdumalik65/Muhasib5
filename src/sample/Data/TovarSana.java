package sample.Data;

import java.util.Date;

public class TovarSana {
    private Integer id;
    private Date sana;
    private Integer tovar;
    private Integer userId;
    private Date dateTime;

    public TovarSana() {
        id = 0;
        tovar = 0;
        sana = new Date();
        userId = 0;
        dateTime = new Date();
    }

    public TovarSana(Integer id, Date sana, Integer tovar, Integer userId, Date dateTime) {
        this.id = id;
        this.sana = sana;
        this.tovar = tovar;
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
