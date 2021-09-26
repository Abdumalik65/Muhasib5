package sample.Data;

import java.util.Date;

public class Sanoq {
    private Integer id;
    private Integer qaydId;
    private String barCode;
    private double adad;
    private double narh;
    private Integer userId;
    private Date dateTime;

    public Sanoq(Integer id, Integer qaydId, String barCode, double adad, double narh, Integer userId, Date dateTime) {
        this.id = id;
        this.qaydId = qaydId;
        this.barCode = barCode;
        this.adad = adad;
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

    public Integer getQaydId() {
        return qaydId;
    }

    public void setQaydId(Integer qaydId) {
        this.qaydId = qaydId;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public double getAdad() {
        return adad;
    }

    public void setAdad(double adad) {
        this.adad = adad;
    }

    public double getNarh() {
        return narh;
    }

    public void setNarh(double narh) {
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
