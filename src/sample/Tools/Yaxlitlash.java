package sample.Tools;

public class Yaxlitlash {
    static public double yahlitla(double son, int daraja) {
        double darajalanganSon = Math.pow(10, daraja);
        double natija = son/darajalanganSon;
        double roundNatija = Math.round(natija)*darajalanganSon;
        System.out.println(new MoneyShow().format(roundNatija));
        return roundNatija;
    }
}
