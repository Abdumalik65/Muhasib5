package sample.Data;

public class Balance {
    Integer id = 0;
    String text = "";
    Double adadKirim = 0d;
    Double adadChiqim = 0d;
    Double adadJami = 0d;
    Double narhKirim = 0d;
    Double narhChiqim = 0d;
    Double narhJami = 0d;

    public Balance() {
    }

    public Balance(Integer id, String text, Double adadKirim, Double adadChiqim, Double adadJami, Double narhKirim, Double narhChiqim, Double narhJami) {
        this.id = id;
        this.text = text;
        this.adadKirim = adadKirim;
        this.adadChiqim = adadChiqim;
        this.adadJami = adadJami;
        this.narhKirim = narhKirim;
        this.narhChiqim = narhChiqim;
        this.narhJami = narhJami;
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

    public Double getAdadKirim() {
        return adadKirim;
    }

    public void setAdadKirim(Double adadKirim) {
        this.adadKirim = adadKirim;
    }

    public Double getAdadChiqim() {
        return adadChiqim;
    }

    public void setAdadChiqim(Double adadChiqim) {
        this.adadChiqim = adadChiqim;
    }

    public Double getAdadJami() {
        return adadJami;
    }

    public void setAdadJami(Double adadJami) {
        this.adadJami = adadJami;
    }

    public Double getNarhKirim() {
        return narhKirim;
    }

    public void setNarhKirim(Double narhKirim) {
        this.narhKirim = narhKirim;
    }

    public Double getNarhChiqim() {
        return narhChiqim;
    }

    public void setNarhChiqim(Double narhChiqim) {
        this.narhChiqim = narhChiqim;
    }

    public Double getNarhJami() {
        return narhJami;
    }

    public void setNarhJami(Double narhJami) {
        this.narhJami = narhJami;
    }
}
