package sample.Data;

public class Balans {
    private Double kirim;
    private Double chiqim;
    private Double jami;
    private Double balans;
    private Integer id;

    public Balans() {
        kirim = .0;
        chiqim = .0;
        jami = .0;
        balans = .0;
        id = 0;
    }

    public Balans(Integer id) {
        this.id = id;
        kirim = .0;
        chiqim = .0;
        jami = .0;
        balans = .0;
        id = 0;
    }

    public Balans(Integer id, Double kirim, Double chiqim, Double jami, Double balans) {
        this.kirim = kirim;
        this.chiqim = chiqim;
        this.jami = jami;
        this.balans = balans;
        this.id = id;
    }

    public Double getKirim() {
        return kirim;
    }

    public void setKirim(Double kirim) {
        this.kirim = kirim;
    }

    public Double getChiqim() {
        return chiqim;
    }

    public void setChiqim(Double chiqim) {
        this.chiqim = chiqim;
    }

    public Double getJami() {
        return getKirim() - getChiqim();
    }

    public void setJami(Double jami) {
        this.jami = jami;
    }

    public Double getBalans() {
        return balans;
    }

    public void setBalans(Double balans) {
        this.balans = balans;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
