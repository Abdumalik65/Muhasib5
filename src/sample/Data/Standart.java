package sample.Data;

import java.util.Date;

public class Standart {
    private Integer id;
    private String text;
    private Integer userId;
    private Date dateTime;

    public Standart() {}

    public Standart(Integer id, String text, Integer userId, Date dateTime) {
        this.id = id;
        this.text = text;
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
