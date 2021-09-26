package sample.Data;

import java.util.Date;

public class Valuta {
    private Integer id;
    private String valuta;
    private Integer status;
    private Integer userId;
    private Date dateTime;

    public Valuta() {
        id = 0;
        valuta = "";
        status = 0;
    }

    public Valuta(Integer id, String valuta, Integer status, Integer userId, Date dateTime) {
        this.id = id;
        this.valuta = valuta;
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

    public String getValuta() {
        return valuta;
    }

    public void setValuta(String valuta) {
        this.valuta = valuta;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    @Override
    public String toString() {
        return valuta;
    }
}
