package sample.Data;

import java.util.Date;

public class Tovar {
    private Integer id;
    private String text;
    private Double nds;
    private Integer userId;
    private Date dateTime;

    public Tovar() {
    }

    public Tovar(Integer id, String text, Double nds, Integer userId, Date dateTime) {
        this.id = id;
        this.text = text;
        this.nds = nds;
        this.userId = userId;
        this.dateTime = dateTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Double getNds() {
        return nds;
    }

    public void setNds(Double nds) {
        this.nds = nds;
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
        return text;
    }
}
