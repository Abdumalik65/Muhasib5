package sample.Temp;

import java.util.Date;

public class Tovar {
    private Integer id;
    private String text;
    private Double nds;
    private Integer jumla;
    private Integer dona;
    private Integer birlik;
    private String barCode;
    private Integer userId;
    private Date dateTime;

    public Tovar() {
    }

    public Tovar(Integer id, String text, Double nds, Integer jumla, Integer dona, Integer birlik, String barCode, Integer userId, Date dateTime) {
        this.id = id;
        this.text = text;
        this.nds = nds;
        this.jumla = jumla;
        this.dona = dona;
        this.birlik = birlik;
        this.barCode = barCode;
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

    public Integer getJumla() {
        return jumla;
    }

    public void setJumla(Integer jumla) {
        this.jumla = jumla;
    }

    public Integer getDona() {
        return dona;
    }

    public void setDona(Integer dona) {
        this.dona = dona;
    }

    public Integer getBirlik() {
        return birlik;
    }

    public void setBirlik(Integer birlik) {
        this.birlik = birlik;
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

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public Double getNds() {
        return nds;
    }

    public void setNds(Double nds) {
        this.nds = nds;
    }

    @Override
    public String toString() {
        return text;
    }
}
