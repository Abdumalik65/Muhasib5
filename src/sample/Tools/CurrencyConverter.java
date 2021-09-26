package sample.Tools;

import javafx.collections.ObservableList;
import sample.Config.MySqlDBLocal;
import sample.Data.Kurs;
import sample.Data.User;
import sample.Data.Valuta;
import sample.Model.KursModels;

import java.sql.Connection;
import java.util.Date;

public class CurrencyConverter {
    Valuta sourceValuta = new Valuta();
    Valuta targetValuta = new Valuta();
    Double summaDouble = 0.0;
    Kurs kurs = null;
    KursModels kursModels = new KursModels();
    Connection connection;
    User user = new User(1, "admin", "", "admin");

    public CurrencyConverter() {
        connection = new MySqlDBLocal().getDbConnection();
    }

    public CurrencyConverter(Connection connection, User user, Valuta sourceValuta, Valuta targetValuta, Double summaDouble) {
        this.connection = connection;
        this.user = user;
        this.sourceValuta = sourceValuta;
        this.targetValuta = targetValuta;
        this.summaDouble = summaDouble;
    }

    public Double convert() {
        Double convertedDouble = null;
        Kurs sourceValutaKurs = getKurs(sourceValuta);
        Kurs targetValutaKurs = getKurs(targetValuta);
        convertedDouble = summaDouble*targetValutaKurs.getKurs()/sourceValutaKurs.getKurs();
        return convertedDouble;
    }

    private Kurs getKurs(Valuta valuta) {
        Kurs kurs = null;
        if (valuta.getStatus() != 1) {
            ObservableList<Kurs> kursObservableList = kursModels.getDate(connection, valuta.getId(), new Date(), "dateTime desc");
            if (kursObservableList.size() > 0) {
                kurs = kursObservableList.get(0);
            }
        } else {
            kurs = new Kurs(1, new Date(), valuta.getId(), 1.0, user.getId(), new Date());
        }
        return kurs;
    }
}
