package sample.Temp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Config.MySqlDBGeneral;
import sample.Data.*;
import sample.Enums.ServerType;
import sample.Model.HisobKitobModels;
import sample.Tools.GetDbData;
import sample.Tools.MoneyShow;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class Hisobot {
    Connection connection;
    User user;
    HisobKitobModels hisobKitobModels = new HisobKitobModels();
    ObservableList<HisobKitob> hisobKitobObservableList;
    ObservableList<HisobKitob> pulHKList = FXCollections.observableArrayList();
    ObservableList<HisobKitob> mahsulotHKList = FXCollections.observableArrayList();
    ObservableList<HisobKitob> barCodeHKList = FXCollections.observableArrayList();
    DecimalFormat decimalFormat = new MoneyShow();

    public Hisobot() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        GetDbData.initData(connection);
    }

    public Hisobot(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
    }

    public void initHisobKitobLists(Integer hisobId) {
        user = GetDbData.getUser(1);
        hisobKitobObservableList = hisobKitobModels.getAllForHisob(connection, hisobId);
        for (HisobKitob hk: hisobKitobObservableList) {
            getPulHK(hisobId, hk);
            getMahsulotHK(hisobId, hk);
            getBarCodeHK(hisobId, hk);
        }
        Comparator<HisobKitob> comparator = Comparator.comparing(HisobKitob::getValuta);
        Collections.sort(pulHKList, comparator);
/*
        System.out.println("Pul...");
        for (HisobKitob hk: pulHKList) {
            System.out.println("|" + hk.getValuta() + "|" + decimalFormat.format(hk.getNarh()) + "|");
        }

        System.out.println("Mahsulot...");
*/
        comparator = Comparator.comparing(HisobKitob::getTovar);
        Collections.sort(mahsulotHKList, comparator);
/*
        for (HisobKitob hk: mahsulotHKList) {
            System.out.println("|" + hk.getTovar() + "|" + hk.getDona() + "|" + decimalFormat.format(hk.getNarh()) + "|");
        }
*/
        comparator = Comparator.comparing(HisobKitob::getBarCode);
        Collections.sort(barCodeHKList, comparator);
/*
        System.out.println("BarCode...");
        for (HisobKitob hk: barCodeHKList) {
            System.out.println("|" + hk.getTovar() + "|" + hk.getBarCode() + "|" + hk.getDona() + "|" + decimalFormat.format(hk.getNarh()) + "|");
        }
*/
    }

    private HisobKitob getPulHK(Integer hisobId, HisobKitob hisobKitob) {
        HisobKitob pulHK = null;
        if (hisobKitob.getTovar() == 0 && hisobKitob.getValuta() != 0) {
            Valuta valuta = GetDbData.getValuta(hisobKitob.getValuta());
            Integer ishora = hisobKitob.getHisob1().equals(hisobId) ? -1 : 1;
            Double mablag = hisobKitob.getNarh() * ishora;
            for (HisobKitob hk : pulHKList) {
                if (hisobKitob.getValuta().equals(hk.getValuta())) {
                    Double mablag1 = hk.getNarh();
                    hk.setNarh(mablag + mablag1);
                    hk.setIzoh(valuta.getValuta());
                    pulHK = hk;
                    break;
                }
            }

            if (pulHK == null) {
                pulHK = new HisobKitob(pulHKList.size(), 0, 0, 0, hisobId, 0, hisobKitob.getValuta(),
                        0, 0d, "", 0d, mablag, 0, valuta.getValuta(), user.getId(), new Date());
                pulHKList.add(pulHK);
            }
        }
        return pulHK;
    }

    private HisobKitob getMahsulotHK(Integer hisobId, HisobKitob hisobKitob) {
        HisobKitob mahsulotHK = null;
        if (hisobKitob.getTovar() != 0) {
            Standart tovar = GetDbData.getTovar(hisobKitob.getTovar());
            Integer ishora = hisobKitob.getHisob1().equals(hisobId) ? -1 : 1;
            Double mablag = hisobKitob.getNarh();
            Double adad = hisobKitob.getDona();
            Double jamiAdad = adad;
            if (!hisobKitob.getBarCode().isEmpty()) {
                BarCode bc = GetDbData.getBarCode(hisobKitob.getBarCode());
                jamiAdad = adad * tovarDonasi(bc);
            }
            Double jamiNarh = mablag * jamiAdad * ishora;
            for (HisobKitob hk : mahsulotHKList) {
                if (hisobKitob.getTovar().equals(hk.getTovar())) {
                    Double adad1 = hk.getDona();
                    Double mablag1 = hk.getNarh();
                    hk.setDona(adad1 + jamiAdad*ishora);
                    hk.setNarh(mablag1 + jamiNarh);
                    hk.setIzoh(tovar.getText());
                    mahsulotHK = hk;
                    break;
                }
            }

            if (mahsulotHK == null) {
                mahsulotHK = new HisobKitob(pulHKList.size(), 0, 0, 0, hisobId, 0, 0,
                        hisobKitob.getTovar(), 0d, "", jamiAdad, jamiNarh, 0, tovar.getText(), user.getId(), new Date());
                mahsulotHKList.add(mahsulotHK);
            }
        }
        return mahsulotHK;
    }

    private HisobKitob getBarCodeHK(Integer hisobId, HisobKitob hisobKitob) {
        HisobKitob barCodeHK = null;
        if (hisobKitob.getTovar() !=0 && !hisobKitob.getBarCode().isEmpty()) {
            Standart tovar = GetDbData.getTovar(hisobKitob.getTovar());
            String bcString = hisobKitob.getBarCode();
            BarCode bc = GetDbData.getBarCode(bcString);
            Standart birlik = GetDbData.getBirlik(bc.getBirlik());
            String izoh = tovar.getText() + "\n" + bcString + "\n" + birlik.getText();
            Integer ishora = hisobKitob.getHisob1().equals(hisobId) ? -1 : 1;
            Double mablag = hisobKitob.getNarh();
            Double adad = hisobKitob.getDona();
            for (HisobKitob hk : barCodeHKList) {
                if (hisobKitob.getTovar().equals(hk.getTovar()) && hisobKitob.getBarCode().equals(hk.getBarCode())) {
                    Double adad1 = hk.getDona();
                    Double mablag1 = hk.getNarh();
                    hk.setDona(adad1 + adad*ishora);
                    hk.setNarh(mablag1 + adad*mablag*ishora);
                    hk.setIzoh(izoh);
                    barCodeHK = hk;
                    break;
                }
            }

            if (barCodeHK == null) {
                barCodeHK = new HisobKitob(pulHKList.size(), 0, 0, 0, hisobId, 0, 0,
                        hisobKitob.getTovar(), 0d, hisobKitob.getBarCode(), adad*ishora, adad*mablag*ishora, 0, izoh, user.getId(), new Date());
                barCodeHKList.add(barCodeHK);
            }
        }
        return barCodeHK;
    }

    private double tovarDonasi(BarCode barCode) {
        double dona = 1.0;
        double adadBarCode2 = 0;
        dona *= barCode.getAdad();
        int tarkibInt = barCode.getTarkib();
        if (tarkibInt>0) {
            while (true) {
                BarCode barCode2 = GetDbData.getBarCode(tarkibInt);
                adadBarCode2 = barCode2.getAdad();
                dona *= adadBarCode2;
                tarkibInt = barCode2.getTarkib();
                if (adadBarCode2 == 1.0) {
                    break;
                }
            }
        }
        return dona;
    }

    public ObservableList<HisobKitob> getAllMahsulot() {
        ObservableList<HisobKitob> allList = FXCollections.observableArrayList();
        allList.addAll(pulHKList);
        allList.addAll(mahsulotHKList);
        return allList;
    }
    public ObservableList<HisobKitob> getAllBarCodes() {
        ObservableList<HisobKitob> allList = FXCollections.observableArrayList();
        allList.addAll(pulHKList);
        allList.addAll(barCodeHKList);
        return allList;
    }
    public ObservableList<HisobKitob> getPul() {
        ObservableList<HisobKitob> allList = FXCollections.observableArrayList();
        allList.addAll(pulHKList);
        return allList;
    }
    public ObservableList<HisobKitob> getBarCodes() {
        ObservableList<HisobKitob> allList = FXCollections.observableArrayList();
        allList.addAll(barCodeHKList);
        return allList;
    }
    public ObservableList<HisobKitob> getMahsulot() {
        ObservableList<HisobKitob> allList = FXCollections.observableArrayList();
        allList.addAll(mahsulotHKList);
        return allList;
    }
}
