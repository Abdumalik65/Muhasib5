package sample.Temp;

public class FuncTemp {
    public static void main(String[] args) {
        int kunInt = 4;
        String kun = "";
        kun = Kunlar(kunInt);
        System.out.println(kun);
    }

    static String Kunlar(int kun) {
        String kunString = "";
        switch (kun) {
            case 1:
                kunString = "Dushanba";
                break;
            case 2:
                kunString = "Seshanba";
                break;
            case 3:
                kunString = "Chorshanba";
                break;
            case 4:
                kunString = "Payshanba";
                break;
            case 5:
                kunString = "Juma";
                break;
            case 6:
                kunString = "Shanba";
                break;
            case 7:
                kunString = "Yakshanba";
                break;
        }
        return kunString;
    }
}
