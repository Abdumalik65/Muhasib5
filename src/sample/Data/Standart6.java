package sample.Data;

import java.util.Date;

public class Standart6 {
    private Integer id;
    private String text;
    private Double narh;
    private Double ulgurji;
    private Double chakana;
    private Double nds;
    private Double boj;
    private Integer userId;
    private Date dateTime;


    public Standart6() {}

    public Standart6(String text, Double narh, Double ulgurji, Double chakana, Double nds, Double boj, Integer userId, Date dateTime) {
        this.text = text;
        this.narh = narh;
        this.ulgurji = ulgurji;
        this.chakana = chakana;
        this.nds = nds;
        this.boj = boj;
        this.userId = userId;
        this.dateTime = dateTime;
    }

    public Standart6(Integer id, String text, Double narh, Double ulgurji, Double chakana, Double nds, Double boj, Integer userId, Date dateTime) {
        this.id = id;
        this.text = text;
        this.narh = narh;
        this.ulgurji = ulgurji;
        this.chakana = chakana;
        this.nds = nds;
        this.boj = boj;
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

    public Double getNarh() {
        return narh;
    }

    public void setNarh(Double narh) {
        this.narh = narh;
    }

    public Double getUlgurji() {
        return ulgurji;
    }

    public void setUlgurji(Double ulgurji) {
        this.ulgurji = ulgurji;
    }

    public Double getChakana() {
        return chakana;
    }

    public void setChakana(Double chakana) {
        this.chakana = chakana;
    }

    public Double getNds() {
        return nds;
    }

    public void setNds(Double nds) {
        this.nds = nds;
    }

    public Double getBoj() {
        return boj;
    }

    public void setBoj(Double boj) {
        this.boj = boj;
    }

    @Override
    public String toString() {
        return text;
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
