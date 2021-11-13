package sample.Temp;

import javafx.collections.ObservableList;
import sample.Data.*;
import sample.Model.HisobKitobModels;
import sample.Model.QaydnomaModel;
import sample.Tools.GetDbData;

import java.sql.Connection;
import java.util.Date;

public class Mayda {
    Connection connection;
    HisobKitobModels hisobKitobModels = new HisobKitobModels();
    HisobKitob hisobKitob;
    BarCode barCode;
    BarCode barCode1;
    Standart tovar;
    QaydnomaData qaydnomaData;
    QaydnomaModel qaydnomaModel = new QaydnomaModel();
    User user = new User(1, "admin", "", "admin");
    int keldiKetdiHisobi = 0;
    Integer amalTuri = 5;
    Double  adad;
    String izohString;

    public Mayda(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
    }

    public Boolean maydala(BarCode barCode, HisobKitob hisobKitob, Double adad) {
        ObservableList<HisobKitob> qoldiqList;
        Boolean maydaladim = false;
        if (barCode.getTarkib() > 0) {
            this.hisobKitob = hisobKitob;
            this.barCode = barCode;
            this.hisobKitob = hisobKitob;
            this.adad = adad;
            HisobKitob chiqimHisobi = initChiqimHisobi(hisobKitob);
            HisobKitob kirimHisobi = initKirimHisobi(hisobKitob);
            getQaydNoma();
            chiqimHisobi.setQaydId(qaydnomaData.getId());
            kirimHisobi.setQaydId(qaydnomaData.getId());

            qoldiqList = hisobKitobModels.getBarCodeQoldiq(connection,hisobKitob.getHisob1(), barCode, qaydnomaData.getSana());
            for (HisobKitob q: qoldiqList) {
                HisobKitob clonedHK = hisobKitobModels.cloneHisobKitob(q);
                clonedHK.setQaydId(qaydnomaData.getId());
                clonedHK.setHujjatId(qaydnomaData.getHujjat());
                clonedHK.setAmal(amalTuri);
                clonedHK.setHisob1(hisobKitob.getHisob1());
                clonedHK.setHisob2(hisobKitob.getHisob2());
                clonedHK.setManba(q.getId());
                if (q.getDona()>=adad) {
                    clonedHK.setDona(adad);
                    chiqimHisobi = initChiqimHisobi(clonedHK);
                    kirimHisobi = initKirimHisobi(clonedHK);
                    adad = 0.0;
                    hisobKitobModels.insert_data(connection, chiqimHisobi);
                    hisobKitobModels.insert_data(connection, kirimHisobi);
                    maydaladim = true;
                    break;
                } else {
                    clonedHK.setDona(q.getDona());
                    chiqimHisobi = initChiqimHisobi(clonedHK);
                    kirimHisobi = initKirimHisobi(clonedHK);
                    hisobKitobModels.insert_data(connection, chiqimHisobi);
                    hisobKitobModels.insert_data(connection, kirimHisobi);
                    maydaladim = true;
                    adad -= q.getDona();
                }
            }


/*
            HisobKitob hisobKitob1 = hisobKitobModels.getBarCodeBalans(connection, hisobKitob.getHisob1(), barCode, hisobKitob.getDateTime());
            if (hisobKitob1.getDona() >= adad) {
                barCode1 = GetDbData.getBarCode(barCode.getTarkib());
                izohString = GetDbData.getBirlikFromBarCodeString(barCode) + " => " + GetDbData.getBirlikFromBarCodeString(barCode1);
                keldiKetdiHisobi = hisobKitobModels.yordamchiHisob(connection, hisobKitob, "TranzitHisobGuruhi");
                HisobKitob chiqimHisobi = initChiqimHisobi(hisobKitob);
                HisobKitob kirimHisobi = initKirimHisobi(hisobKitob);
                getQaydNoma();
                chiqimHisobi.setQaydId(qaydnomaData.getId());
                kirimHisobi.setQaydId(qaydnomaData.getId());
                hisobKitobModels.insert_data(connection, chiqimHisobi);
                hisobKitobModels.insert_data(connection, kirimHisobi);
                maydaladim = true;
            }
*/
        } else {
            System.out.println("O`zi mayda");
        }
        return maydaladim;
    }

    private HisobKitob initChiqimHisobi(HisobKitob hisobKitob) {
        barCode1 = GetDbData.getBarCode(barCode.getTarkib());
        izohString = GetDbData.getBirlikFromBarCodeString(barCode) + " => " + GetDbData.getBirlikFromBarCodeString(barCode1);
        tovar = GetDbData.getTovar(hisobKitob.getTovar());
        HisobKitob chiqimHisobi = hisobKitobModels.cloneHisobKitob(hisobKitob);
        chiqimHisobi.setHisob2(keldiKetdiHisobi);
        chiqimHisobi.setDona(adad);
        chiqimHisobi.setManba(hisobKitob.getId());
        chiqimHisobi.setIzoh(tovar.getText() + "\n"+ izohString);
        return chiqimHisobi;
    }

    private HisobKitob initKirimHisobi(HisobKitob hisobKitob) {
        barCode1 = GetDbData.getBarCode(barCode.getTarkib());
        izohString = GetDbData.getBirlikFromBarCodeString(barCode) + " => " + GetDbData.getBirlikFromBarCodeString(barCode1);
        HisobKitob kirimHisobi = hisobKitobModels.cloneHisobKitob(hisobKitob);
        kirimHisobi.setHisob1(keldiKetdiHisobi);
        kirimHisobi.setHisob2(hisobKitob.getHisob1());
        kirimHisobi.setManba(0);
        kirimHisobi.setBarCode(barCode1.getBarCode());
        kirimHisobi.setDona(adad*barCode.getAdad());
        kirimHisobi.setNarh(hisobKitob.getNarh()/barCode.getAdad());
        kirimHisobi.setIzoh(tovar.getText() + "\n"+ izohString);
        return kirimHisobi;
    }

    private Integer getQaydNoma() {
        Hisob hisob1 = GetDbData.getHisob(hisobKitob.getHisob1());
        Hisob hisob2 = GetDbData.getHisob(hisobKitob.getHisob2());
        Integer qaydId = 0;
        ObservableList<QaydnomaData> qaydList = qaydnomaModel.getAnyData(connection, "amalTuri = " + amalTuri, "sana desc");
        if (qaydList.size()>0) {
            qaydId = qaydList.get(0).getHujjat() + 1;
        } else {
            qaydId = 1;
        }
        qaydnomaData = new QaydnomaData(
                null, amalTuri, qaydId, new Date(), hisob1.getId(), hisob1.getText(),
                hisob2.getId(), hisob2.getText(),"", .0, 0, user.getId(), null);
        qaydnomaModel.insert_data(connection, qaydnomaData);
        return qaydId;
    }
}
