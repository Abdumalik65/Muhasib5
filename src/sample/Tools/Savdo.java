package sample.Tools;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Data.*;
import sample.Model.*;

import java.sql.Connection;
import java.util.Collections;
import java.util.Comparator;

public class Savdo {
    Connection connection;
    User user = GetDbData.getUser();

    Standart tovar;
    HisobKitobModels hisobKitobModels = new HisobKitobModels();
    HisobKitob hisobKitob;
    QaydnomaData qaydnomaData;
    HisobKitob sotuvchiKeldiKetdi;
    HisobKitob keldiKetdiMijoz;
    HisobKitob foydaKeldiKetdi;
    HisobKitob ndsKeldiKetdi;
    HisobKitob keldiKetdiBank;
    HisobKitob keldiKetdiZarar;
    HisobKitob mijozChegirma;
    Standart4 tartibStandart4;
    Standart4 ndsStandart4;

    StandartModels standartModels = new StandartModels();
    Standart4Models standart4Models = new Standart4Models();

    ObservableList<Valuta> valutaObservableList = GetDbData.getValutaObservableList();
    ObservableList<Standart> standartObservableList = FXCollections.observableArrayList();
    ObservableList<HisobKitob> qoldiqObservableList = FXCollections.observableArrayList();
    ObservableList<HisobKitob> tannarhObservableList = FXCollections.observableArrayList();

    double talab = .00;
    double jami = .00;

    int keldiKetdiHisobi = 0;
    int foydaHisobi = 0;
    int zararHisobi = 0;
    int ndsHisobi = 0;
    int bankHisobi = 0;
    int chegirmaHisobi = 0;

    public Savdo(Connection connection) {
        this.connection = connection;
        initData();
    }

    public void initHisobKitob(HisobKitob hisobKitob) {
        this.hisobKitob = hisobKitob;
        tovar = GetDbData.getTovar(hisobKitob.getTovar());
        talab = hisobKitob.getDona();
        keldiKetdiHisobi = hisobKitobModels.yordamchiHisob(connection, hisobKitob.getHisob1(), "TranzitHisobGuruhi", "TranzitHisob");
        ndsHisobi = hisobKitobModels.yordamchiHisob(connection, hisobKitob.getHisob1(), "NDS2","NDS1");
        foydaHisobi = hisobKitobModels.yordamchiHisob(connection, hisobKitob.getHisob1(), "FoydaHisobiGuruhi", "FoydaHisobi");
        zararHisobi = hisobKitobModels.yordamchiHisob(connection, hisobKitob.getHisob1(), "ZararGuruhi", "Zarar");
        bankHisobi = hisobKitobModels.yordamchiHisob(connection, hisobKitob.getHisob1(), "Bank1", "Bank");
        chegirmaHisobi = hisobKitobModels.yordamchiHisob(connection, hisobKitob.getHisob1(), "ChegirmaGuruhi", "Chegirma");

        tannarhObservableList.removeAll(tannarhObservableList);
        initKeldiKetdi();
        initMijoz();
        initFoyda();
        initZarar();
        initNds();
        initBank();
        initChegirma();
        standart4Models.setTABLENAME("Tartib");
        tartibStandart4 = standart4Models.getTartibForDate(connection, hisobKitob.getTovar(), qaydnomaData.getSana(), "dateTime desc");
        standart4Models.setTABLENAME("Nds");
        ndsStandart4 = standart4Models.getTartibForDate(connection, hisobKitob.getTovar(), qaydnomaData.getSana(), "dateTime desc");
        if (ndsStandart4 == null) {
            ndsStandart4 = new Standart4(null, hisobKitob.getTovar(), hisobKitob.getDateTime(), .0, hisobKitob.getUserId(), null);
        }
    }

    private void initData() {
        standartModels.setTABLENAME("ChiqimShakli");
        standartObservableList = standartModels.get_data(connection);
    }

    public HisobKitob initKeldiKetdi() {
        sotuvchiKeldiKetdi = cloneHisobKitob(hisobKitob);
        sotuvchiKeldiKetdi.setHisob2(keldiKetdiHisobi);
        return sotuvchiKeldiKetdi;
    }

    public HisobKitob initMijoz() {
        keldiKetdiMijoz = cloneHisobKitob(hisobKitob);
        keldiKetdiMijoz.setHisob1(keldiKetdiHisobi);
        keldiKetdiMijoz.setHisob2(hisobKitob.getHisob2());
        return keldiKetdiMijoz;
    }

    public HisobKitob initFoyda() {
        foydaKeldiKetdi = cloneHisobKitob(hisobKitob);
        foydaKeldiKetdi.setHisob1(foydaHisobi);
        foydaKeldiKetdi.setHisob2(keldiKetdiHisobi);
        foydaKeldiKetdi.setNarh(0.0);
        foydaKeldiKetdi.setDona(0.0);
        foydaKeldiKetdi.setManba(0);
        foydaKeldiKetdi.setTovar(0);
        foydaKeldiKetdi.setAmal(9);
        foydaKeldiKetdi.setValuta(getBirlikValuta());
        foydaKeldiKetdi.setKurs(1.00);
        return foydaKeldiKetdi;
    }

    public HisobKitob initNds() {
        ndsKeldiKetdi = cloneHisobKitob(hisobKitob);
        ndsKeldiKetdi.setHisob1(ndsHisobi);
        ndsKeldiKetdi.setHisob2(keldiKetdiHisobi);
        ndsKeldiKetdi.setNarh(0.0);
        ndsKeldiKetdi.setDona(0.0);
        ndsKeldiKetdi.setManba(0);
        ndsKeldiKetdi.setTovar(0);
        ndsKeldiKetdi.setAmal(11);
        ndsKeldiKetdi.setValuta(getBirlikValuta());
        ndsKeldiKetdi.setKurs(1.00);
        return ndsKeldiKetdi;
    }

    public HisobKitob initBank() {
        keldiKetdiBank = cloneHisobKitob(hisobKitob);
        keldiKetdiBank.setHisob1(keldiKetdiHisobi);
        keldiKetdiBank.setHisob2(bankHisobi);
        keldiKetdiBank.setNarh(0.0);
        keldiKetdiBank.setDona(0.0);
        keldiKetdiBank.setManba(0);
        keldiKetdiBank.setTovar(0);
        keldiKetdiBank.setAmal(14);
        keldiKetdiBank.setValuta(getBirlikValuta());
        keldiKetdiBank.setKurs(1.00);
        return keldiKetdiBank;
    }

    public HisobKitob initZarar() {
        keldiKetdiZarar = cloneHisobKitob(hisobKitob);
        keldiKetdiZarar.setHisob1(keldiKetdiHisobi);
        keldiKetdiZarar.setHisob2(zararHisobi);
        keldiKetdiZarar.setNarh(0.0);
        keldiKetdiZarar.setDona(0.0);
        keldiKetdiZarar.setManba(0);
        keldiKetdiZarar.setTovar(0);
        keldiKetdiZarar.setAmal(12);
        keldiKetdiZarar.setValuta(getBirlikValuta());
        keldiKetdiZarar.setKurs(1.00);
        return keldiKetdiZarar;

    }
    public HisobKitob initChegirma() {
        mijozChegirma = cloneHisobKitob(hisobKitob);
        mijozChegirma.setHisob1(hisobKitob.getHisob2());
        mijozChegirma.setHisob2(chegirmaHisobi);
        mijozChegirma.setDona(0.0);
        mijozChegirma.setManba(0);
        mijozChegirma.setTovar(0);
        mijozChegirma.setAmal(12);
        mijozChegirma.setIzoh("");
        return mijozChegirma;

    }

    private int getBirlikValuta() {
        int valutaId = 0;
        for (Valuta v: valutaObservableList) {
            if (v.getStatus() == 1) {
                valutaId = v.getId();
            }
        }
        return valutaId;
    }

    public boolean tovarChiqar() {
        boolean yetarliAdad = false;
        if (talab > 0) {
            if (talab > jami) {
                System.out.println("Talab etilgan adad yo`q");
            } else {
                yetarliAdad = true;
                sotuvchiKeldiKetdi.setDona(talab);
                tannarhObservableList.addAll(bazagaYoz(sotuvchiKeldiKetdi));
            }
        }
        return yetarliAdad;
    }

    public boolean sot() {
        boolean yetarliAdad = false;
//          Sotuvchidan  tovarning tannarhi ayirilib keldiKetdi hisobiga chiqadi
        tovarQoldiq(sotuvchiKeldiKetdi);
        if (qoldiqObservableList.size()>0) {
            if (tovarChiqar()) {
                yetarliAdad = true;
//              Keldiketdi hisobidan sotish narhida mijozga o`tadi
                keldiKetdiMijoz.setManba(sotuvchiKeldiKetdi.getId());
                hisobKitobModels.insert_data(connection, keldiKetdiMijoz);
//              Foyda hisobidan keldiKetdi hisobi yopildi
                for (HisobKitob h : tannarhObservableList) {
                    Standart t = GetDbData.getTovar(h.getTovar());
                    double ndsDouble = h.getDona() * h.getNarh()/h.getKurs() * ndsStandart4.getMiqdor()/100;
                    double natijaDouble = h.getDona() * (keldiKetdiMijoz.getNarh()/ keldiKetdiMijoz.getKurs() - h.getNarh()/h.getKurs()) - ndsDouble;
                    if (natijaDouble>0) {
                        foydaKeldiKetdi.setNarh(natijaDouble);
                        foydaKeldiKetdi.setDona(h.getDona());
                        hisobKitobModels.insert_data(connection, foydaKeldiKetdi);
                    } else {
                        keldiKetdiZarar.setNarh(-natijaDouble);
                        if (keldiKetdiZarar.getNarh() != 0) {
                            hisobKitobModels.insert_data(connection, keldiKetdiZarar);
                        }
                    }
                    ndsKeldiKetdi.setNarh(ndsDouble);
                    if (ndsKeldiKetdi.getNarh() != 0) {
                        hisobKitobModels.insert_data(connection, ndsKeldiKetdi);
                    }
                }
            }
        }
        return yetarliAdad;
    }

    public ObservableList<HisobKitob> tovarQoldiq(HisobKitob hisobKitob) {
        this.hisobKitob = hisobKitob;
        ObservableList<HisobKitob> kirimObservableList = FXCollections.observableArrayList();
        qoldiqObservableList.removeAll(qoldiqObservableList);
        HisobKitob qoldiq;
        jami = .00;
        kirimObservableList = hisobKitobModels.getAnyData(
                connection,
                "hisob2 = " + hisobKitob.getHisob1() + " and barCode = '" + hisobKitob.getBarCode() + "'",
                ""
        );
        tovarKirimTartibi(hisobKitob, kirimObservableList);
        for (HisobKitob hk : kirimObservableList) {
            if (!hk.getDateTime().after(hisobKitob.getDateTime())) {
                qoldiq = cloneHisobKitob(hk);
                ObservableList<HisobKitob> chiqim = null;
                chiqim = hisobKitobModels.getAnyData(connection, "manba = " + hk.getId(), "");
                for (HisobKitob ch : chiqim) {
                    qoldiq.setDona(qoldiq.getDona() - ch.getDona());
                }
                jami = jami + qoldiq.getDona();
                if (qoldiq.getDona() > 0) {
                    qoldiqObservableList.add(qoldiq);
                }
            }
        }
        return qoldiqObservableList;
    }

    public ObservableList<HisobKitob> bazagaYoz (HisobKitob hk) {
        ObservableList<HisobKitob> tovarKirimChiqimiObservableList = FXCollections.observableArrayList();
        for (HisobKitob q : qoldiqObservableList) {
            if (q.getDona() > 0) {
                if (q.getDona() >= hk.getDona()) {
                    HisobKitob buyurtma = cloneHisobKitob(hk);
                    buyurtma.setValuta(q.getValuta());
                    buyurtma.setDona(hk.getDona());
                    buyurtma.setKurs(q.getKurs());
                    buyurtma.setNarh(q.getNarh());
                    buyurtma.setManba(q.getId());
                    q.setDona(q.getDona() - hk.getDona());
                    hk.setDona(0.00);
                    buyurtma.setId(hisobKitobModels.insert_data(connection, buyurtma));
                    tovarKirimChiqimiObservableList.add(buyurtma);
                    break;
                } else {
                    HisobKitob buyurtma = cloneHisobKitob(hk);
                    buyurtma.setValuta(q.getValuta());
                    buyurtma.setKurs(q.getKurs());
                    buyurtma.setDona(q.getDona());
                    buyurtma.setNarh(q.getNarh());
                    hk.setDona(hk.getDona() - q.getDona());
                    q.setDona(0.00);
                    buyurtma.setManba(q.getId());
                    buyurtma.setId(hisobKitobModels.insert_data(connection, buyurtma));
                    tovarKirimChiqimiObservableList.add(buyurtma);
                }
            }
        }
        return tovarKirimChiqimiObservableList;
    }

    public ObservableList<HisobKitob> tovarKirimTartibi(HisobKitob hisobKitob, ObservableList<HisobKitob> kirimObservableList) {
        Integer chiqimShakliId = null;
        if (tartibStandart4 == null) {
            chiqimShakliId = 1;
        } else {
            chiqimShakliId = tartibStandart4.getId();
        }

        switch (chiqimShakliId) {
            case 1: /*Avval keldi, avval ketdi.*/
                Collections.sort(kirimObservableList, Comparator.comparing(HisobKitob::getDateTime));
                break;
            case 2: /*Avval keldi, oxir ketdi*/
                Collections.sort(kirimObservableList, Comparator.comparing(HisobKitob::getDateTime).reversed());
                break;
            case 3: /*Eng arzoni eng avval.*/
                Collections.sort(kirimObservableList, Comparator.comparingDouble(HisobKitob::getNarh));
                break;
            case 4: /*Eng qimmati eng avval*/
                Collections.sort(kirimObservableList, Comparator.comparingDouble(HisobKitob::getNarh).reversed());
                break;
            case 5: /*O`rtacha narh*/
                break;
        }
        return kirimObservableList;
    }

    public void printClass(ObservableList<HisobKitob> hisobKitobObservableList) {
        for (HisobKitob hk: hisobKitobObservableList) {
            if (!hk.getDateTime().after(hisobKitob.getDateTime())) {
                System.out.println(
                        "|" +
                                hk.getId() + "|" +
                                GetDbData.getHisob(hk.getHisob1()) + "|" +
                                GetDbData.getHisob(hk.getHisob2()) + "|" +
                                GetDbData.getTovar(hk.getTovar()) + "|" +
//                                getValuta(hk.getValuta()) + "|" +
                                hk.getBarCode() + "|" +
                                hk.getDona() + "|" +
                                hk.getNarh() + "|" +
                                hk.getManba() + "|"
                );
            }
        }
    }

    public void printHisobKitob(HisobKitob hk) {
        System.out.println(
                "|" +
                        hk.getId() + "|" +
                        GetDbData.getHisob(hk.getHisob1()) + "|" +
                        GetDbData.getHisob(hk.getHisob2()) + "|" +
                        GetDbData.getTovar(hk.getTovar()) + "|" +
                        hk.getBarCode() + "|" +
                        hk.getDona() + "|" +
                        hk.getNarh() + "|" +
                        hk.getManba() + "|"
        );
    }

    public HisobKitob cloneHisobKitob(HisobKitob hk) {
        HisobKitob clonedHisobKitob = new HisobKitob(
                hk.getId(),
                hk.getQaydId(),
                hk.getHujjatId(),
                hk.getAmal(),
                hk.getHisob1(),
                hk.getHisob2(),
                hk.getValuta(),
                hk.getTovar(),
                hk.getKurs(),
                hk.getBarCode(),
                hk.getDona(),
                hk.getNarh(),
                hk.getManba(),
                hk.getIzoh(),
                hk.getUserId(),
                hk.getDateTime()
        );
        return clonedHisobKitob;
    }

    public QaydnomaData getQaydnomaData() {
        return qaydnomaData;
    }

    public void setQaydnomaData(QaydnomaData qaydnomaData) {
        this.qaydnomaData = qaydnomaData;
    }
}
