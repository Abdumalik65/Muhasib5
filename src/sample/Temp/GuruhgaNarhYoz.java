package sample.Temp;

import javafx.collections.ObservableList;
import sample.Config.MySqlDB;
import sample.Config.MySqlDBLocal;
import sample.Data.*;
import sample.Model.*;

import java.sql.Connection;
import java.util.Date;

public class GuruhgaNarhYoz {

    Connection connection = new MySqlDB().getDbConnection();
    Standart6Models standart6Models = new Standart6Models("TGuruh1");
    Standart3Models standart3Models = new Standart3Models();
    Standart4Models standart4Models = new Standart4Models();
    TovarNarhiModels tovarNarhiModels = new TovarNarhiModels();
    ObservableList<Standart6> s6List;
    ObservableList<Standart3> s3List;

    public void initData() {
        standart3Models.setTABLENAME("TGuruh2");
        s6List = standart6Models.get_data(connection);
        s3List = standart3Models.get_data(connection);
        for (Standart6 s6: s6List) {
            s6.setNarh(tanNarh(s6));
            s6.setChakana(chakanNarh(s6));
            s6.setUlgurji(ulgurjiNarh(s6));
            s6.setBoj(bojNarh(s6));
            s6.setNds(ndsNarh(s6));
            standart6Models.update_data(connection, s6);
        }
    }

    private Double tanNarh(Standart6 s6) {
        NarhModels narhModels = new NarhModels();
        Double narhDouble = 0d;
        Narh xaridNarhi = null;
        Integer tovarId = getTovar(s6);
        ObservableList<Narh> list = narhModels.getDate(connection, tovarId, new Date(), "sana desc");
        if (list.size()>0) {
            xaridNarhi = list.get(0);
            narhDouble = xaridNarhi.getXaridDouble();
        }
        return  narhDouble;
    }

    private Double chakanNarh(Standart6 s6) {
        Double narhDouble = 0d;
        TovarNarhi narh = null;
        Integer tovarId = getTovar(s6);
        ObservableList<TovarNarhi> list = tovarNarhiModels.getDate3(connection, tovarId, 1, new Date(), "sana desc");
        if (list.size()>0) {
            narh = list.get(0);
            narhDouble = narh.getNarh();
        }
        return  narhDouble;
    }

    private Double ulgurjiNarh(Standart6 s6) {
        Double narhDouble = 0d;
        TovarNarhi narh = null;
        Integer tovarId = getTovar(s6);
        ObservableList<TovarNarhi> list = tovarNarhiModels.getDate3(connection, tovarId, 2, new Date(), "sana desc");
        if (list.size()>0) {
            narh = list.get(0);
            narhDouble = narh.getNarh();
        }
        return  narhDouble;
    }

    private Double bojNarh(Standart6 s6) {
        Double narhDouble = 0d;
        standart4Models.setTABLENAME("BojxonaSoligi");
        Integer tovarId = getTovar(s6);
        ObservableList<Standart4> s4List = standart4Models.getAnyData(connection,"tovar = " + tovarId,"sana desc");
        if (s4List.size()>0) {
            Standart4 s4 = s4List.get(0);
            narhDouble = s4.getMiqdor();
        }
        return  narhDouble;
    }

    private Double ndsNarh(Standart6 s6) {
        Double narhDouble = 0d;
        standart4Models.setTABLENAME("Nds");
        Integer tovarId = getTovar(s6);
        ObservableList<Standart4> s4List = standart4Models.getAnyData(connection,"tovar = " + tovarId,"sana desc");
        if (s4List.size()>0) {
            Standart4 s4 = s4List.get(0);
            narhDouble = s4.getMiqdor();
        }
        return  narhDouble;
    }

    private Integer getTovar(Standart6 s6) {
        Integer tovarId= null;
        if (s3List.size()>0) {
            for (Standart3 s3: s3List) {
                if (s3.getId2().equals(s6.getId())) {
                    tovarId = s3.getId3();
                    break;
                }
            }
        }
        return  tovarId;
    }
}
