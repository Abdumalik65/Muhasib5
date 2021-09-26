package sample.Data;

import java.util.Date;

public class Standart2 {
    private Integer id;
    private Integer id2;
    private String text;
    private Integer userId;
    private Date dateTime;

    public Standart2() {}

    public Standart2(Integer id, Integer id2, String text, Integer userId, Date dateTime) {
        this.id = id;
        this.id2 = id2;
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

    public Integer getId2() {
        return id2;
    }

    public void setId2(Integer id2) {
        this.id2 = id2;
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
