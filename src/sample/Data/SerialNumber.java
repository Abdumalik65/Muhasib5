package sample.Data;

import java.util.Date;

public class SerialNumber {
    private Integer id;
    private Date sana;
    private Integer hisob;
    private String invoice;
    private Integer tovar;
    private String serialNumber;
    private Integer userId;
    private Date dateTime;

    public SerialNumber() {
        id = 0;
        sana = new Date();
        hisob = 0;
        invoice = "";
        tovar = 0;
        serialNumber = "";
        userId = 1;
        dateTime = new Date();
    }

    public SerialNumber(Integer id, Date sana, Integer hisob, String invoice, Integer tovar, String serialNumber, Integer userId, Date dateTime) {
        this.id = id;
        this.sana = sana;
        this.hisob = hisob;
        this.invoice = invoice;
        this.tovar = tovar;
        this.serialNumber = serialNumber;
        this.userId = userId;
        this.dateTime = dateTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getHisob() {
        return hisob;
    }

    public void setHisob(Integer hisob) {
        this.hisob = hisob;
    }

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public Integer getTovar() {
        return tovar;
    }

    public void setTovar(Integer tovar) {
        this.tovar = tovar;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
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

    public Date getSana() {
        return sana;
    }

    public void setSana(Date sana) {
        this.sana = sana;
    }
}
