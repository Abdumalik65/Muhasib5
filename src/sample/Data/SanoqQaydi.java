package sample.Data;

import java.util.Date;

public class SanoqQaydi {
    private Integer id;
    private Date sana;
    private Integer hisob1;
    private Integer hisob2;
    private Double balance;
    private  Integer userId;
    private Date dateTime;

    public SanoqQaydi(Integer id, Date sana, Integer hisob1, Integer hisob2, Double balance, Integer userId, Date dateTime) {
        this.id = id;
        this.sana = sana;
        this.hisob1 = hisob1;
        this.hisob2 = hisob2;
        this.balance = balance;
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

    public Integer getHisob1() {
        return hisob1;
    }

    public void setHisob1(Integer hisob1) {
        this.hisob1 = hisob1;
    }

    public Integer getHisob2() {
        return hisob2;
    }

    public void setHisob2(Integer hisob2) {
        this.hisob2 = hisob2;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
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
