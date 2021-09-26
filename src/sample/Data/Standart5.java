package sample.Data;

import java.util.Date;

public class Standart5 {
    private Integer id;
    private Integer id2;
    private Integer id3;
    private String text;
    private Double dona;
    private Integer userId;
    private Date dateTime;

    public Standart5() {
    }

    public Standart5(Integer id, Integer id2, Integer id3, String text, Double dona, Integer userId, Date dateTime) {
        this.id = id;
        this.id2 = id2;
        this.id3 = id3;
        this.text = text;
        this.dona = dona;
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

    public Integer getId3() {
        return id3;
    }

    public void setId3(Integer id3) {
        this.id3 = id3;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Double getDona() {
        return dona;
    }

    public void setDona(Double dona) {
        this.dona = dona;
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
