package sample.Data;

import java.util.Date;

public class Hisob {
    private Integer id;
    private String text;
    private String rasm;
    private String email;
    private String mobile;
    private Integer userId;
    private Date dateTime;
    private Double chiqim;
    private Double kirim;
    private Double balans;
    private Integer id1;

    public Hisob() {
        id = 0;
        text = "";
        balans = .0;
        rasm = "";
        email = "";
        mobile = "";
        userId = 0;
        dateTime = new Date();
        chiqim = .0;
        kirim = .0;
        id1 = 0;
    }

    public Hisob(Integer id, String text, Double balans, String rasm, String email, String mobile) {
        this.id = id;
        this.text = text;
        this.balans = balans;
        this.rasm = rasm;
        this.email = email;
        this.mobile = mobile;
    }

    public Hisob(Integer id, String text, Double chiqim, Double kirim, Double balans) {
        this.id = id;
        this.text = text;
        this.chiqim = chiqim;
        this.kirim = kirim;
        this.balans = balans;
    }

    public Hisob(Integer id, String text, Double balans, String rasm, String email, String mobile, Integer userId) {
        this.id = id;
        this.text = text;
        this.balans = balans;
        this.rasm = rasm;
        this.email = email;
        this.mobile = mobile;
        this.userId = userId;
    }

    public Hisob(Integer id, String text, Double balans, String rasm, String email, String mobile, Integer userId, Date dateTime) {
        this.id = id;
        this.text = text;
        this.balans = balans;
        this.rasm = rasm;
        this.email = email;
        this.mobile = mobile;
        this.userId = userId;
        this.dateTime = dateTime;
    }

    public Hisob(Integer id, String text, String rasm, String email, String mobile, Integer userId, Date dateTime, Double chiqim, Double kirim, Double balans) {
        this.id = id;
        this.text = text;
        this.rasm = rasm;
        this.email = email;
        this.mobile = mobile;
        this.userId = userId;
        this.dateTime = dateTime;
        this.chiqim = chiqim;
        this.kirim = kirim;
        this.balans = balans;
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

    public Double getBalans() {
        return balans;
    }

    public void setBalans(Double balans) {
        this.balans = balans;
    }

    public String getRasm() {
        return rasm;
    }

    public void setRasm(String rasm) {
        this.rasm = rasm;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
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

    public Double getChiqim() {
        return chiqim;
    }

    public void setChiqim(Double chiqim) {
        this.chiqim = chiqim;
    }

    public Double getKirim() {
        return kirim;
    }

    public void setKirim(Double kirim) {
        this.kirim = kirim;
    }

    public Integer getId1() {
        return id1;
    }

    public void setId1(Integer id1) {
        this.id1 = id1;
    }

    @Override
    public String toString() {
        return text;
    }
}
