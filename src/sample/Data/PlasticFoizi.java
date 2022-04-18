package sample.Data;

import java.util.Date;

public class PlasticFoizi {
    private Integer id;
    private Double foiz;
    private Integer userId;
    private Date dateTime;

    public PlasticFoizi() {
    }

    public PlasticFoizi(Integer id, Double foiz, Integer userId, Date dateTime) {
        this.id = id;
        this.foiz = foiz;
        this.userId = userId;
        this.dateTime = dateTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getFoiz() {
        return foiz;
    }

    public void setFoiz(Double foiz) {
        this.foiz = foiz;
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
