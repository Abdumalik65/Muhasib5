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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class Hisobot2 {
    Connection connection;
    User user;
    HisobKitobModels hisobKitobModels = new HisobKitobModels();
    ObservableList<HisobKitob> hisobKitobObservableList;
    ObservableList<HisobKitob> pulHKList = FXCollections.observableArrayList();
    ObservableList<HisobKitob> mahsulotHKList = FXCollections.observableArrayList();
    ObservableList<HisobKitob> barCodeHKList = FXCollections.observableArrayList();
    DecimalFormat decimalFormat = new MoneyShow();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Hisob hisob;

    public Hisobot2() {
        connection = new MySqlDBGeneral(ServerType.LOCAL).getDbConnection();
        GetDbData.initData(connection);
        user = GetDbData.getUser(1);
    }

    public Hisobot2(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
    }

    public Hisobot2(Connection connection, User user, Hisob hisob) {
        this.connection = connection;
        this.user = user;
        this.hisob = hisob;
    }

    public Hisobot2(Connection connection, User user, LocalDate localDate) {
        this.connection = connection;
        this.user = user;
        this.hisob = hisob;
    }

    public void initHisobKitobLists(Integer hisobId) {
        hisobKitobObservableList = hisobKitobModels.getAllForHisob(connection, hisobId);
        for (HisobKitob hk: hisobKitobObservableList) {
            getPulHK(hisobId, hk);
            getMahsulotHK(hisobId, hk);
            getBarCodeHK(hisobId, hk);
        }
        Comparator<HisobKitob> comparator = Comparator.comparing(HisobKitob::getValuta);
        Collections.sort(pulHKList, comparator);
/*
        for (HisobKitob hk: pulHKList) {
            System.out.println("|" + hk.getValuta() + "|" + decimalFormat.format(hk.getNarh()) + "|");
        }

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
        Valuta valuta = GetDbData.getValuta(hisobKitob.getValuta());
        if (valuta == null) {return null;}
        if (hisobKitob.getTovar() == 0 && hisobKitob.getValuta() > 0) {
            Integer ishora = hisobKitob.getHisob2().equals(hisobId) ? 1 : -1;
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
                pulHK = new HisobKitob(pulHKList.size(), 0, 0, 0, hisobKitob.getHisob1(), hisobKitob.getHisob2(), hisobKitob.getValuta(),
                        0, 0d, "", 0d, mablag, 0, valuta.getValuta(), user.getId(), new Date());
                pulHKList.add(pulHK);
            }
        }
        return pulHK;
    }

    private HisobKitob getMahsulotHK(Integer hisobId, HisobKitob hisobKitob) {
        HisobKitob mahsulotHK = null;
        Standart tovar = GetDbData.getTovar(hisobKitob.getTovar());
        if (tovar == null) {return null;}
        BarCode bc = GetDbData.getBarCode(hisobKitob.getBarCode());
        if (bc == null) {return null;}
        if (hisobKitob.getTovar() != 0) {
            Integer ishora = hisobKitob.getHisob2().equals(hisobId) ? 1 : -1;
            Double adad = hisobKitob.getDona();
            Double mablag = adad * hisobKitob.getNarh()/hisobKitob.getKurs();
            if (!hisobKitob.getBarCode().isEmpty()) {
                Double tovarDonasi = tovarDonasi(bc);
                if (tovarDonasi > 1) {
                    adad = adad * tovarDonasi(bc);
                }
            }
            for (HisobKitob hk : mahsulotHKList) {
                if (hisobKitob.getTovar().equals(hk.getTovar()) && hk.getTovar() != 0) {
                    Double adad1 = hk.getDona();
                    Double mablag1 = hk.getNarh()/hk.getKurs();
                    hk.setDona(adad1 + adad*ishora);
                    hk.setNarh(mablag1 + mablag*ishora);
                    hk.setIzoh(tovar.getText());
                    mahsulotHK = hk;
                    break;
                }
            }

            if (mahsulotHK == null) {
                mahsulotHK = new HisobKitob(pulHKList.size(), 0, 0, 0, hisobKitob.getHisob1(), hisobKitob.getHisob2(), 1,
                        hisobKitob.getTovar(), 1d, "", adad*ishora, mablag*ishora, 0, tovar.getText(), user.getId(), new Date());
                mahsulotHKList.add(mahsulotHK);
            }
        }
        return mahsulotHK;
    }

    private HisobKitob getBarCodeHK(Integer hisobId, HisobKitob hisobKitob) {
        HisobKitob barCodeHK = null;
        Standart tovar = GetDbData.getTovar(hisobKitob.getTovar());
        if (tovar == null) {return null;}
        BarCode bc = GetDbData.getBarCode(hisobKitob.getBarCode());
        if (bc == null) {return null;}
        if (hisobKitob.getTovar() !=0 && !hisobKitob.getBarCode().isEmpty()) {
            String bcString = hisobKitob.getBarCode();
            Standart birlik = GetDbData.getBirlik(bc.getBirlik());
            String izoh = tovar.getText() + "\n" + bcString + "\n" + birlik.getText();
            Integer ishora = hisobKitob.getHisob2().equals(hisobId) ? 1 : -1;
            Double mablag = hisobKitob.getNarh()/hisobKitob.getKurs();
            Double adad = hisobKitob.getDona();
            for (HisobKitob hk : barCodeHKList) {
                if (hisobKitob.getTovar().equals(hk.getTovar()) && hisobKitob.getBarCode().equals(hk.getBarCode())) {
                    Double adad1 = hk.getDona();
                    Double mablag1 = hk.getNarh()/hk.getKurs();
                    hk.setDona(adad1 + adad*ishora);
                    hk.setNarh(mablag1 + adad*mablag*ishora);
                    hk.setIzoh(izoh);
                    hk.setKurs(1.0);
                    barCodeHK = hk;
                    break;
                }
            }

            if (barCodeHK == null) {
                barCodeHK = new HisobKitob(pulHKList.size(), 0, 0, 0, hisobKitob.getHisob1(), hisobKitob.getHisob2(), 0,
                        hisobKitob.getTovar(), 1d, hisobKitob.getBarCode(), adad*ishora, adad*mablag*ishora, 0, izoh, user.getId(), new Date());
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

    public ObservableList<HisobKitob> getPulMahsulot(int hisobId) {
        hisobKitobObservableList = hisobKitobModels.getAllForHisob(connection, hisobId);
        for (HisobKitob hk: hisobKitobObservableList) {
            getPulHK(hisobId, hk);
            getMahsulotHK(hisobId, hk);
        }
        ObservableList<HisobKitob> allList = FXCollections.observableArrayList();
        allList.addAll(pulHKList);
        allList.addAll(mahsulotHKList);
        Comparator<HisobKitob> comparator = Comparator.comparing(HisobKitob::getValuta);
        Collections.sort(allList, comparator);
        return allList;
    }
    public ObservableList<HisobKitob> getPulBarCodes(int hisobId) {
        hisobKitobObservableList = hisobKitobModels.getAllForHisob(connection, hisobId);
        for (HisobKitob hk: hisobKitobObservableList) {
            getPulHK(hisobId, hk);
            getBarCodeHK(hisobId, hk);
        }
        ObservableList<HisobKitob> allList = FXCollections.observableArrayList();
        allList.addAll(pulHKList);
        allList.addAll(barCodeHKList);
        Comparator<HisobKitob> comparator = Comparator.comparing(HisobKitob::getValuta);
        Collections.sort(allList, comparator);
        return allList;
    }
    public ObservableList<HisobKitob> getPul(int hisobId) {
        hisobKitobObservableList = hisobKitobModels.getAllForHisob(connection, hisobId);
        for (HisobKitob hk: hisobKitobObservableList) {
            getPulHK(hisobId, hk);
        }
        Comparator<HisobKitob> comparator = Comparator.comparing(HisobKitob::getValuta);
        Collections.sort(pulHKList, comparator);
        return pulHKList;
    }

    public ObservableList<HisobKitob> getPul(int hisobId, int userId) {
        hisobKitobObservableList = hisobKitobModels.getAllForHisob(connection, hisobId);
        for (HisobKitob hk: hisobKitobObservableList) {
            if (hk.getUserId().equals(userId)) {
                getPulHK(hisobId, hk);
            }
        }
        Comparator<HisobKitob> comparator = Comparator.comparing(HisobKitob::getValuta);
        Collections.sort(pulHKList, comparator);
        return pulHKList;
    }

    public ObservableList<HisobKitob> getPul(int hisobId, LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String localDateString = localDate.format(formatter);
        hisobKitobObservableList = hisobKitobModels.getAllForHisob(connection, hisobId, " and substr(datetime, 1, 10) <= '" + localDateString + "'");
        for (HisobKitob hk: hisobKitobObservableList) {
            getPulHK(hisobId, hk);
        }
        Comparator<HisobKitob> comparator = Comparator.comparing(HisobKitob::getValuta);
        Collections.sort(pulHKList, comparator);
        return pulHKList;
    }

    public ObservableList<HisobKitob> getBarCodes(int hisobId) {
        hisobKitobObservableList = hisobKitobModels.getAllForHisob(connection, hisobId);
        for (HisobKitob hk: hisobKitobObservableList) {
            getBarCodeHK(hisobId, hk);
        }
        Comparator<HisobKitob> comparator = Comparator.comparing(HisobKitob::getTovar);
        Collections.sort(barCodeHKList, comparator);
        return barCodeHKList;
    }

    public ObservableList<HisobKitob> getBarCodes(int hisobId, LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String localDateString = localDate.format(formatter);
        hisobKitobObservableList = hisobKitobModels.getAllForHisob(connection, hisobId, "and substr(datetime, 1, 10) <= '" + localDateString + "'");
        for (HisobKitob hk: hisobKitobObservableList) {
            getBarCodeHK(hisobId, hk);
        }
        Comparator<HisobKitob> comparator = Comparator.comparing(HisobKitob::getTovar);
        Collections.sort(barCodeHKList, comparator);
        return barCodeHKList;
    }

    public ObservableList<HisobKitob> getMahsulot(int hisobId) {
        hisobKitobObservableList = hisobKitobModels.getAllForHisob(connection, hisobId);
        for (HisobKitob hk: hisobKitobObservableList) {
            getMahsulotHK(hisobId, hk);
        }
        Comparator<HisobKitob> comparator = Comparator.comparing(HisobKitob::getTovar);
        Collections.sort(mahsulotHKList, comparator);
        return mahsulotHKList;
    }

    public ObservableList<HisobKitob> getMahsulot(int hisobId, LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String localDateString = localDate.format(formatter);
        hisobKitobObservableList = hisobKitobModels.getAllForHisob(connection, hisobId, "and substr(datetime, 1, 10) <= '" + localDateString + "'");
        for (HisobKitob hk: hisobKitobObservableList) {
            getMahsulotHK(hisobId, hk);
        }
        Comparator<HisobKitob> comparator = Comparator.comparing(HisobKitob::getTovar);
        Collections.sort(mahsulotHKList, comparator);
        return mahsulotHKList;
    }
}
