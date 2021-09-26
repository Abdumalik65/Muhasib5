package sample.Temp;

import sample.Config.MySqlDBLocal;
import sample.Data.User;
import sample.Data.Valuta;
import sample.Tools.CurrencyConverter;
import sample.Tools.GetDbData;
import sample.Tools.MoneyShow;

import java.sql.Connection;
import java.text.DecimalFormat;

public class TestConverter {
    public static void main(String[] args) {
        Connection connection = new MySqlDBLocal().getDbConnection();
        User user = new User(1, "admin", "", "admin");
        GetDbData.initData(connection);
        Valuta sourceValuta = GetDbData.getValuta(2);
        Valuta targetValuta = GetDbData.getValuta(3);
        CurrencyConverter currencyConverter = new CurrencyConverter(connection, user, sourceValuta, targetValuta, 1037000.0);
        DecimalFormat decimalFormat = new MoneyShow();
        System.out.println(decimalFormat.format(currencyConverter.convert()));
    }
}
