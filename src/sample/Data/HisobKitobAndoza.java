package sample.Data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Model.*;
import sample.Tools.ConnectionType;
import sample.Tools.GetDbData;

import java.sql.Connection;
import java.util.Date;

public class HisobKitobAndoza {
    Connection connection;
    User user;
    Hisob hisob1;
    Hisob hisob2;
    Valuta valuta;
    QaydnomaData qaydnomaData;
    HisobKitob savdoXossalari;
    Kassa kassa;

    ObservableList<HisobKitob> jamiSavdoRoyxati = FXCollections.observableArrayList();
    ObservableList<HisobKitob> tanNarhRoyxati = FXCollections.observableArrayList();
    ObservableList<HisobKitob> sotishNarhRoyxati = FXCollections.observableArrayList();
    ObservableList<HisobKitob> tolovRoyxati = FXCollections.observableArrayList();
    ObservableList<HisobKitob> bankToloviRoyxati = FXCollections.observableArrayList();
    ObservableList<HisobKitob> qaytimRoyxati = FXCollections.observableArrayList();
    ObservableList<HisobKitob> chegirmaRoyxati = FXCollections.observableArrayList();
    ObservableList<HisobKitob> qoshimchaDaromadRoyxati = FXCollections.observableArrayList();
    ObservableList<Valuta> valutaObservableList = FXCollections.observableArrayList();
    ObservableList<Kurs> kursObservableList = FXCollections.observableArrayList();

    HisobKitob kursTafovuti;

    HisobModels hisobModels = new HisobModels();
    HisobKitobModels hisobKitobModels = new HisobKitobModels();
    ValutaModels valutaModels = new ValutaModels();
    KursModels kursModels = new KursModels();

    public HisobKitobAndoza(Connection connection, User user, Hisob hisob1, Hisob hisob2, ObservableList<HisobKitob> sotishNarhRoyxati) {
        this.connection = connection;
        this.user = user;
        this.hisob1 = hisob1;
        this.hisob2 = hisob2;
        this.sotishNarhRoyxati = sotishNarhRoyxati;
        initData();
        kassa = yangiKassa();
    }

    private void initData() {
        valutaObservableList = valutaModels.getAnyData(connection, "status < 3", "");
        for (Valuta v: valutaObservableList) {
            Kurs kurs = kursModels.getKurs(connection, v.getId(), new Date(), "sana desc");
            kursObservableList.add(kurs);
        }
        yangiRoyxatlar();
    }

    private void yangiRoyxatlar() {
        tolovRoyxati = pulRoyxati(7);
        tolovMalumotlariniYangila(hisob1, hisob2, tolovRoyxati);

        qaytimRoyxati = pulRoyxati(8);
        tolovMalumotlariniYangila(hisob1, hisob2, qaytimRoyxati);

        chegirmaRoyxati = pulRoyxati(13);
        tolovMalumotlariniYangila(hisob1, hisob2, chegirmaRoyxati);

        bankToloviRoyxati = pulRoyxati(15);
        tolovMalumotlariniYangila(hisob1, hisob2, bankToloviRoyxati);

        qoshimchaDaromadRoyxati = pulRoyxati(18);
        tolovMalumotlariniYangila(hisob1, hisob2, qoshimchaDaromadRoyxati);

    }

    private void tolovMalumotlariniYangila(Hisob hisob1, Hisob hisob2, ObservableList<HisobKitob> observableList) {
        for (HisobKitob hisobKitob: observableList) {
            Hisob h1 = null;
            Hisob h2 = null;
            switch (hisobKitob.getAmal()) {
                case 4: // xossalar
                    h1 = hisob1;
                    h2 = hisob2;
                    hisobKitob.setValuta(valuta.getId());
                    Kurs kurs = kursOl(valuta);
                    if (kurs == null)
                        hisobKitob.setKurs(1d);
                    else
                        hisobKitob.setKurs(kursOl(valuta).getKurs());
                    break;
                case 7: // naqd to`lov
                    h1 = hisob2;
                    h2 = hisobModels.pulHisobi(connection, user, hisob1);
                    break;
                case 8: //qaytim
                    h1 = hisobModels.pulHisobi(connection, user, hisob1);
                    h2 = hisob2;
                    break;
                case 13: // chegirma
                    h1 = hisob2;
                    h2 = hisobModels.chegirmaHisobi(connection, hisob1);
                    break;
                case 15: // bankdan to`lov
                    h1 = hisob2;
                    h2 = hisobModels.bankHisobi(connection, hisob1);
                    break;
                case 18: //qo`shimcha daromad
                    h1 = hisobModels.qoshimchaDaromadHisobi(connection, hisob1);
                    h2 = hisob2;
                    break;
            }
            hisobKitob.setHisob1(h1.getId());
            hisobKitob.setHisob2(h2.getId());
        }
    }
    private ObservableList<HisobKitob> jamiSavdoRoyxati() {
        ObservableList<HisobKitob> observableList = FXCollections.observableArrayList();
        observableList = hisobKitobModels.getAnyData(connection, "qaydId = " + qaydnomaData.getId(), "" );
        return observableList;
    }

    private ObservableList<HisobKitob> pulRoyxati(Integer amal) {
        ObservableList<HisobKitob> observableList = FXCollections.observableArrayList();
        for (Valuta v: valutaObservableList) {
            HisobKitob hisobKitob = new HisobKitob();
            hisobKitob.setAmal(amal);
            hisobKitob.setValuta(v.getId());
            Double kursDouble = 1d;
            Kurs kurs = kursOl(v);
            if (kurs != null) {
                kursDouble = kurs.getKurs();
            }
            hisobKitob.setKurs(kursDouble);
            observableList.add(hisobKitob);
        }
        return observableList;
    }

    private Kurs kursOl(Valuta valuta) {
        Kurs kurs = null;
        for (Kurs k: kursObservableList) {
            if (k.getValuta().equals(valuta.getId())) {
                kurs = k;
                break;
            }
        }
        return kurs;
    }

    public Hisob getHisob1() {
        return hisob1;
    }

    public void setHisob1(Hisob hisob1) {
        this.hisob1 = hisob1;
    }

    public Hisob getHisob2() {
        return hisob2;
    }

    public void setHisob2(Hisob hisob2) {
        this.hisob2 = hisob2;
    }

    public ObservableList<HisobKitob> getJamiSavdoRoyxati() {
        return jamiSavdoRoyxati;
    }

    public void setJamiSavdoRoyxati(ObservableList<HisobKitob> jamiSavdoRoyxati) {
        this.jamiSavdoRoyxati = jamiSavdoRoyxati;
    }

    public ObservableList<HisobKitob> getTanNarhRoyxati() {
        return tanNarhRoyxati;
    }

    public void setTanNarhRoyxati(ObservableList<HisobKitob> tanNarhRoyxati) {
        this.tanNarhRoyxati = tanNarhRoyxati;
    }

    public ObservableList<HisobKitob> getSotishNarhRoyxati() {
        return sotishNarhRoyxati;
    }

    public void setSotishNarhRoyxati(ObservableList<HisobKitob> sotishNarhRoyxati) {
        this.sotishNarhRoyxati = sotishNarhRoyxati;
    }

    public ObservableList<HisobKitob> getTolovRoyxati() {
        return tolovRoyxati;
    }

    public void setTolovRoyxati(ObservableList<HisobKitob> tolovRoyxati) {
        this.tolovRoyxati = tolovRoyxati;
    }

    public ObservableList<HisobKitob> getBankToloviRoyxati() {
        return bankToloviRoyxati;
    }

    public void setBankToloviRoyxati(ObservableList<HisobKitob> bankToloviRoyxati) {
        this.bankToloviRoyxati = bankToloviRoyxati;
    }

    public ObservableList<HisobKitob> getQaytimRoyxati() {
        return qaytimRoyxati;
    }

    public void setQaytimRoyxati(ObservableList<HisobKitob> qaytimRoyxati) {
        this.qaytimRoyxati = qaytimRoyxati;
    }

    public ObservableList<HisobKitob> getChegirmaRoyxati() {
        return chegirmaRoyxati;
    }

    public void setChegirmaRoyxati(ObservableList<HisobKitob> chegirmaRoyxati) {
        this.chegirmaRoyxati = chegirmaRoyxati;
    }

    public ObservableList<HisobKitob> getQoshimchaDaromadRoyxati() {
        return qoshimchaDaromadRoyxati;
    }

    public void setQoshimchaDaromadRoyxati(ObservableList<HisobKitob> qoshimchaDaromadRoyxati) {
        this.qoshimchaDaromadRoyxati = qoshimchaDaromadRoyxati;
    }

    public HisobKitob getKursTafovuti() {
        return kursTafovuti;
    }

    public void setKursTafovuti(HisobKitob kursTafovuti) {
        this.kursTafovuti = kursTafovuti;
    }

    public HisobKitobAndoza(Valuta valuta) {
        this.valuta = valuta;
    }

    public Valuta getValuta() {
        return valuta;
    }

    public void setValuta(Valuta valuta) {
        this.valuta = valuta;
    }

    public Kassa getKassa() {
        return kassa;
    }

    public void setKassa(Kassa kassa) {
        this.kassa = kassa;
    }

    private Kassa yangiKassa() {
        Kassa kassa = null;
        KassaModels kassaModels = new KassaModels();
        String serialNumber = ConnectionType.getAloqa().getText().trim();
        kassa = kassaModels.getKassa(connection, serialNumber);
        return kassa;
    }

    public HisobKitob savdoXossalari(ObservableList<HisobKitob> sotishNarhiRoyxati) {
        HisobKitob hisobKitob = null;
        ValutaModels valutaModels = new ValutaModels();
        for (HisobKitob hk: sotishNarhiRoyxati) {
            if (hk.getIzoh().contains(" Savdo №: ")) {
                hisobKitob = hk;
                break;
            }
        }
        if (hisobKitob == null) {
            hisobKitob = yangiSavdoXossalari(qaydnomaData, sotishNarhiRoyxati);
        }
        return hisobKitob;
    }

    private HisobKitob yangiSavdoXossalari(QaydnomaData qaydnomaData, ObservableList<HisobKitob> sotishNarhRoyxati) {
        Integer valutaId = 0;
        if (sotishNarhRoyxati.size() > 0) {
            HisobKitob hisobKitob = sotishNarhRoyxati.get(0);
            valutaId = hisobKitob.getValuta();
        } else {
            valutaId = 1;
        }
        HisobKitob hisobKitob = new HisobKitob();
        hisobKitob.setQaydId(qaydnomaData.getId());
        hisobKitob.setHujjatId(qaydnomaData.getHujjat());
        hisobKitob.setAmal(4);
        hisobKitob.setHisob1(qaydnomaData.getChiqimId());
        hisobKitob.setHisob2(qaydnomaData.getKirimId());
        hisobKitob.setValuta(valutaId);
        hisobKitob.setTovar(0);
        hisobKitob.setKurs(1d);
        hisobKitob.setBarCode("");
        hisobKitob.setDona(0d);
        hisobKitob.setNarh(0d);
        hisobKitob.setManba(0);
        hisobKitob.setIzoh(" Savdo №: " + qaydnomaData.getHujjat());
        hisobKitob.setUserId(user.getId());
        hisobKitob.setDateTime(qaydnomaData.getSana());
        return hisobKitob;
    }
    public HisobKitob yangiSavdoXossalari(QaydnomaData qaydnomaData, Valuta valuta) {
        Integer valutaId = valuta.getId();
        HisobKitob hisobKitob = new HisobKitob();
        hisobKitob.setQaydId(qaydnomaData.getId());
        hisobKitob.setHujjatId(qaydnomaData.getHujjat());
        hisobKitob.setAmal(4);
        hisobKitob.setHisob1(qaydnomaData.getChiqimId());
        hisobKitob.setHisob2(qaydnomaData.getKirimId());
        hisobKitob.setValuta(valutaId);
        hisobKitob.setTovar(0);
        hisobKitob.setKurs(1d);
        hisobKitob.setBarCode("");
        hisobKitob.setDona(0d);
        hisobKitob.setNarh(0d);
        hisobKitob.setManba(0);
        hisobKitob.setIzoh(" Savdo №: " + qaydnomaData.getHujjat());
        hisobKitob.setUserId(user.getId());
        hisobKitob.setDateTime(qaydnomaData.getSana());
        return hisobKitob;
    }
}
