package sample.Tools;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Data.*;
import sample.Model.*;

import java.sql.Connection;

public class GetDbData {
    public static ObservableList<Hisob> hisobObservableList;
    public static ObservableList<Valuta> valutaObservableList;
    public static ObservableList<Standart> tovarObservableList;
    public static ObservableList<Standart> amalObservableList;
    public static ObservableList<User> userObservableList;
    public static ObservableList<BarCode> barCodeObservableList;
    public static ObservableList<Standart> birlikObservableList;

    public static HisobModels hisobModels = new HisobModels();
    public static ValutaModels valutaModels = new ValutaModels();
    public static StandartModels standartModels = new StandartModels();
    public static UserModels userModels = new UserModels();
    public static BarCodeModels barCodeModels = new BarCodeModels();

    public static User user;

    public static Connection connection;


    public static void initData(Connection connection) {
        GetDbData.connection = connection;
        GetDbData.hisobObservableList = hisobModels.get_data(connection);
        GetDbData.valutaObservableList = valutaModels.get_data(connection);
        GetDbData.standartModels.setTABLENAME("Tovar");
        GetDbData.tovarObservableList = standartModels.get_data(connection);
        GetDbData.standartModels.setTABLENAME("Birlik");
        GetDbData.birlikObservableList = standartModels.get_data(connection);
        GetDbData.standartModels.setTABLENAME("Amal");
        GetDbData.amalObservableList = standartModels.get_data(connection);
        GetDbData.userObservableList = userModels.getData(connection);
        GetDbData.barCodeObservableList = barCodeModels.get_data(connection);
    }

    public static ObservableList<Hisob> getHisobObservableList() {
        return hisobObservableList;
    }

    public static void setHisobObservableList(ObservableList<Hisob> hisobObservableList) {
        GetDbData.hisobObservableList = hisobObservableList;
    }

    public static Hisob getHisob(int id) {
        Hisob hisob = null;
        for (Hisob h: hisobObservableList) {
            if (h.getId().equals(id)) {
                hisob = h;
                break;
            }
        }

        if (hisob == null) {
            ObservableList<Hisob> hisobs = hisobModels.getAnyData(connection, "id = " + id, "");
            if (hisobs.size()>0) {
                hisob = hisobs.get(0);
                GetDbData.hisobObservableList.add(hisob);
            }
        }
        return hisob;
    }

    public static Hisob hisobniTop(int id, ObservableList<Hisob> hisobObservableList) {
        Hisob hisob = null;
        for (Hisob h: hisobObservableList) {
            if (h.getId().equals(id)) {
                hisob = h;
                break;
            }
        }
        return hisob;
    }

    public static ObservableList<Valuta> getValutaObservableList() {
        return valutaObservableList;
    }

    public static void setValutaObservableList(ObservableList<Valuta> valutaObservableList) {
        GetDbData.valutaObservableList = valutaObservableList;
    }

    public static Standart getTovar(int id) {
        Standart tovar = null;
        for (Standart o: tovarObservableList) {
            if (o.getId().equals(id)) {
                tovar = o;
                break;
            }
        }

        if (tovar == null) {
            standartModels.setTABLENAME("Tovar");
            ObservableList<Standart> tovars = standartModels.getAnyData(connection, "id = " + id, "");
            if (tovars.size()>0) {
                tovar = tovars.get(0);
                GetDbData.tovarObservableList.add(tovar);
            }
        }
        return tovar;
    }

    public static ObservableList<BarCode> getBarCodeObservableList() {
        return barCodeObservableList;
    }

    public static void setBarCodeObservableList(ObservableList<BarCode> barCodeObservableList) {
        GetDbData.barCodeObservableList = barCodeObservableList;
    }

    public static BarCode getBarCode(String string) {
        BarCode barCode = null;
        for (BarCode bc : barCodeObservableList) {
            if (bc.getBarCode().trim().equalsIgnoreCase(string.trim())) {
                barCode = bc;
                break;
            }
        }
        if (barCode == null) {
            BarCode barCode1 = barCodeModels.getBarCode(connection, string);
            if (barCode1 != null) {
                barCode = barCode1;
                GetDbData.barCodeObservableList.add(barCode);
            }
        }
        return barCode;
    }

    public static BarCode getBarCode(Integer id, ObservableList<BarCode> barCodeObservableList) {
        BarCode barCode = null;
        for (BarCode bc : barCodeObservableList) {
            if (bc.getId().equals(id)) {
                barCode = bc;
                break;
            }
        }
        return barCode;
    }

    public static BarCode getBarCode(Integer id) {
        BarCode barCode = null;
        for (BarCode bc : barCodeObservableList) {
            if (bc.getId().equals(id)) {
                barCode = bc;
                break;
            }
        }
        if (barCode == null) {
            BarCode barCode1 = barCodeModels.getBarCode(connection, id);
            barCode = barCode1;
            GetDbData.barCodeObservableList.add(barCode);
        }
        return barCode;
    }

    public static ObservableList<BarCode> getBarCodeList(Integer tovarId) {
        ObservableList<BarCode> barCodeList = FXCollections.observableArrayList();
        for (BarCode bc: barCodeObservableList) {
            if (bc.getTovar().equals(tovarId)) {
                barCodeList.add(bc);
            }
        }
        return barCodeList;
    }

    public static String getBirlikFromBarCodeString(BarCode barCode) {
        String birlikString = "";
        Standart birlik = getBirlik(barCode.getBirlik());
        if (birlik != null) {
            birlikString = birlik.getText();
        }
        return birlikString;
    }

    public static ObservableList<Standart> getTovarObservableList() {
        return tovarObservableList;
    }

    public static void setTovarObservableList(ObservableList<Standart> tovarObservableList) {
        GetDbData.tovarObservableList = tovarObservableList;
    }

    public static Valuta getValuta(int id) {
        Valuta valuta = null;
        for (Valuta o: valutaObservableList) {
            if (o.getId().equals(id)) {
                valuta = o;
                break;
            }
        }

        if (valuta == null) {
            ObservableList<Valuta> valutas = valutaModels.getAnyData(connection, "id = " + id, "");
            if (valutas.size()>0) {
                valuta = valutas.get(0);
                GetDbData.valutaObservableList.add(valuta);
            }
        }
        return valuta;
    }

    public static ObservableList<Standart> getAmalObservableList() {
        return amalObservableList;
    }

    public static void setAmalObservableList(ObservableList<Standart> amalObservableList) {
        GetDbData.amalObservableList = amalObservableList;
    }

    public static Standart getAmal(int id) {
        Standart amal = null;
        for (Standart o: amalObservableList) {
            if (o.getId().equals(id)) {
                amal = o;
                break;
            }
        }

        if (amal == null) {
            standartModels.setTABLENAME("Amal");
            ObservableList<Standart> amals = standartModels.getAnyData(connection, "id = " + id, "");
            if (amals.size()>0) {
                amal = amals.get(0);
                GetDbData.amalObservableList.add(amal);
            }
        }
        return amal;
    }

    public static ObservableList<Standart> getBiirlikObservableList() {
        return birlikObservableList;
    }

    public static void setBirllikObservableList(ObservableList<Standart> amalObservableList) {
        GetDbData.birlikObservableList = birlikObservableList;
    }

    public static Standart getBirlik(int id) {
        Standart birlik = null;
        for (Standart o: birlikObservableList) {
            if (o.getId().equals(id)) {
                birlik = o;
                break;
            }
        }

        if (birlik == null) {
            standartModels.setTABLENAME("Birlik");
            ObservableList<Standart> birliklar = standartModels.getAnyData(connection, "id = " + id, "");
            if (birliklar.size()>0) {
                birlik = birliklar.get(0);
                GetDbData.birlikObservableList.add(birlik);
            }
        }
        return birlik;
    }

    public static Standart getBirlik(int id, ObservableList<Standart> birlikObservableList) {
        Standart birlik = null;
        for (Standart o: birlikObservableList) {
            if (o.getId().equals(id)) {
                birlik = o;
                break;
            }
        }
        return birlik;
    }

    public static ObservableList<User> getUserObservableList() {
        return userObservableList;
    }

    public static void setUserObservableList(ObservableList<User> userObservableList) {
        GetDbData.userObservableList = userObservableList;
    }

    public static User getUser(int id) {
        User user = null;
        for (User o: userObservableList) {
            if (o.getId().equals(id)) {
                user = o;
                break;
            }
        }

        if (user == null) {
            ObservableList<User> users = userModels.getAnyData(connection, "id = " + id, "");
            if (users.size()>0) {
                user = users.get(0);
                GetDbData.userObservableList.add(user);
            }
        }
        return user;
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void setConnection(Connection connection) {
        GetDbData.connection = connection;
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        GetDbData.user = user;
    }
}
