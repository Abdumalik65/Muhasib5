package sample.Data;

import sample.Tools.MoneyShow;

import java.text.DecimalFormat;
import java.util.Date;

public class HisobKitob {
    private Integer id;
    private Integer qaydId;
    private Integer hujjatId;
    private Integer amal = 0;
    private Integer hisob1;
    private Integer hisob2;
    private Integer valuta = 0;
    private Integer tovar =0;
    private Double kurs = .0;
    private String barCode  = "";
    private Double dona = .0;
    private Double narh = .0;
    private Integer manba = 0;
    private String izoh = "";
    private Integer userId;
    private Date dateTime;
    private Double  summaCol;
    private Double balans = 0.0;

    public HisobKitob() {
    }

    public HisobKitob(Integer id, Integer qaydId, Integer hujjatId, Integer amal, Integer hisob1, Integer hisob2, Integer valuta, Integer tovar, Double kurs, String barCode, Double dona, Double narh, Integer manba, String izoh, Integer userId, Date dateTime) {
        this.id = id;
        this.qaydId = qaydId;
        this.hujjatId = hujjatId;
        this.amal = amal;
        this.hisob1 = hisob1;
        this.hisob2 = hisob2;
        this.valuta = valuta;
        this.tovar = tovar;
        this.kurs = kurs;
        this.barCode = barCode;
        this.dona = dona;
        this.narh = narh;
        this.manba = manba;
        this.izoh = izoh;
        this.userId = userId;
        this.dateTime = dateTime;
    }

    public HisobKitob(Integer qaydId, Integer hujjatId, Integer amal, Integer hisob1, Integer hisob2) {
        this.qaydId = qaydId;
        this.hujjatId = hujjatId;
        this.amal = amal;
        this.hisob1 = hisob1;
        this.hisob2 = hisob2;
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

    public Integer getHujjatId() {
        return hujjatId;
    }

    public void setHujjatId(Integer hujjatId) {
        this.hujjatId = hujjatId;
    }

    public Integer getAmal() {
        return amal;
    }

    public void setAmal(Integer amal) {
        this.amal = amal;
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

    public Integer getValuta() {
        return valuta;
    }

    public void setValuta(Integer valuta) {
        this.valuta = valuta;
    }

    public Integer getTovar() {
        return tovar;
    }

    public void setTovar(Integer tovar) {
        this.tovar = tovar;
    }

    public Double getKurs() {
        return kurs;
    }

    public void setKurs(Double kurs) {
        this.kurs = kurs;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public Double getDona() {
        return dona;
    }

    public void setDona(Double dona) {
        this.dona = dona;
    }

    public Double getNarh() {
        return narh;
    }

    public void setNarh(Double narh) {
        this.narh = narh;
    }

    public Integer getManba() {
        return manba;
    }

    public void setManba(Integer manba) {
        this.manba = manba;
    }

    public String getIzoh() {
        return izoh;
    }

    public void setIzoh(String izoh) {
        this.izoh = izoh;
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

    public Double getSummaCol() {
        summaCol = tovar > 0 ? dona*narh/kurs : narh/kurs;
        return summaCol;
    }

    public void setSummaCol(Double summaCol) {
        this.summaCol = summaCol;
    }

    public Double getBalans() {
        return balans;
    }

    public void setBalans(Double balans) {
        this.balans = balans;
    }
}
