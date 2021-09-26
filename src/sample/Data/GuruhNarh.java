package sample.Data;

import java.util.Date;

public class GuruhNarh {
    private Integer id;
    private Date sana;
    private Integer guruhId;
    private Integer narhId;
    private Double narhDouble;
    private Integer userId;
    private Date dateTime;

    public GuruhNarh(Integer id, Date sana, Integer guruhId, Integer narhId, Double narhDouble, Integer userId, Date dateTime) {
        this.id = id;
        this.sana = sana;
        this.guruhId = guruhId;
        this.narhId = narhId;
        this.narhDouble = narhDouble;
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

    public Integer getGuruhId() {
        return guruhId;
    }

    public void setGuruhId(Integer guruhId) {
        this.guruhId = guruhId;
    }

    public Integer getNarhId() {
        return narhId;
    }

    public void setNarhId(Integer narhId) {
        this.narhId = narhId;
    }

    public Double getNarhDouble() {
        return narhDouble;
    }

    public void setNarhDouble(Double narhDouble) {
        this.narhDouble = narhDouble;
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
