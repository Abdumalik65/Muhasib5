package sample.Printer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Config.SqliteDB;
import sample.Data.*;
import sample.Model.QaydnomaModel;
import sample.Model.StandartModels;
import sample.Tools.GetDbData;
import sample.Tools.MoneyShow;
import sample.Tools.PrinterService;
import sample.Tools.StringNumberUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SavdoChiptasiniChopEtish {
    User user;
    QaydnomaData qaydnomaData;
    PrinterService printerService = new PrinterService();
    ObservableList<HisobKitob> jamiRoyxat;
    ObservableList<HisobKitob> sotilganTovarlarRoyxati = FXCollections.observableArrayList();
    ObservableList<HisobKitob> sotibOlinganTovarlarRoyxati = FXCollections.observableArrayList();
    ObservableList<HisobKitob> naqdTolovRoyxati = FXCollections.observableArrayList();
    ObservableList<HisobKitob> bankToloviRoyxati = FXCollections.observableArrayList();
    ObservableList<HisobKitob> qaytimRoyxati = FXCollections.observableArrayList();
    ObservableList<HisobKitob> chegirmaRoyxati = FXCollections.observableArrayList();
    ObservableList<HisobKitob> qoshimchaDaromadRoyxati = FXCollections.observableArrayList();
    QaydnomaModel qaydnomaModel = new QaydnomaModel();
    DecimalFormat decimalFormat = new MoneyShow();
    Double balance;
    Integer joriyValuta;
    Double joriyValutaKursi;


    public SavdoChiptasiniChopEtish(User user, QaydnomaData qaydnomaData, ObservableList<HisobKitob> hisobKitobObservableList) {
        this.user = user;
        this.qaydnomaData = qaydnomaData;
        this.jamiRoyxat = hisobKitobObservableList;
        balance = amallarniAjrat(jamiRoyxat);
    }

    public void tolovChiptasiniBerPos58(String printerNomi) {
        if (sotilganTovarlarRoyxati.size()>0) {
            StringBuffer printStringBuffer = new StringBuffer();
            String lineB = String.format("%.50s\n", "--------------------------------");
            SimpleDateFormat sana = new SimpleDateFormat("dd.MM.yyyy");
            SimpleDateFormat vaqt = new SimpleDateFormat("HH:mm:ss");
            Date date = new Date();
            String sanaString = sana.format(date);
            String vaqtString = vaqt.format(date);
            printStringBuffer.append(lineB);
            String shirkatNomi = GetDbData.getHisob(user.getTovarHisobi()).getText();
            printStringBuffer.append(String.format("%23s\n", shirkatNomi));
            printStringBuffer.append(lineB);
            printStringBuffer.append(String.format("%-15s %16s\n", "Telefon", user.getPhone()));
            String lineT = String.format("%32s\n", user.getPhone());
            printStringBuffer.append(lineT);
            printStringBuffer.append(String.format("%-11s %20s\n", "Telegram", "t.me/best_perfumery"));
            printStringBuffer.append(String.format("%-15s %16s\n", "Sana", sanaString));
            printStringBuffer.append(String.format("%-15s %16s\n", "Vaqt", vaqtString));
            printStringBuffer.append(String.format("%-15s %16s\n", "Sotuvchi", user.getIsm()));
            printStringBuffer.append(String.format("%-15s %16s\n", "Oluvchi", qaydnomaData.getKirimNomi()));
            printStringBuffer.append(String.format("%-15s %16s\n", "Chipta N", qaydnomaData.getHujjat()));
            printStringBuffer.append(lineB);
            printStringBuffer.append(String.format("%-15s %5s %10s\n", "Mahsulot", "Dona", "Narh"));
            printStringBuffer.append(lineB);

            String space = "                    ";
            Double jamiMablag = 0d;
            for (HisobKitob hk : sotilganTovarlarRoyxati) {
                Double dona = hk.getDona();
                Double narh = hk.getDona() * hk.getNarh();
                String line = "";
                String lineS = String.format("%32s\n", decimalFormat.format(narh));
                line = String.format("%.15s %5s %10s\n", hk.getIzoh() + space, decimalFormat.format(dona), decimalFormat.format(hk.getNarh()));
                lineS = String.format("%32s\n", decimalFormat.format(narh));
                printStringBuffer.append(line);
                printStringBuffer.append(lineS);
                printStringBuffer.append(lineB);
                jamiMablag += narh;
            }

            for (HisobKitob hk : sotibOlinganTovarlarRoyxati) {
                Double dona = hk.getDona();
                Double narh = hk.getDona() * hk.getNarh();
                String line = "";
                String lineS = String.format("%32s\n", decimalFormat.format(narh));
                line = String.format("%.15s %5s %10s\n", hk.getIzoh() + space, decimalFormat.format(dona), decimalFormat.format(-hk.getNarh()));
                lineS = String.format("%32s\n", decimalFormat.format(-narh));
                printStringBuffer.append(line);
                printStringBuffer.append(lineS);
                printStringBuffer.append(lineB);
                jamiMablag -= narh;
            }

            if (jamiMablag > 0) {
                String line = String.format("%-15s %5s %10s\n", "Xarid jami", " ", decimalFormat.format(jamiMablag));
                printStringBuffer.append(line);
                printStringBuffer.append(lineB);
            }

            royxatniChopEtPos58(naqdTolovRoyxati, printStringBuffer, lineB);
            royxatniChopEtPos58(bankToloviRoyxati, printStringBuffer, lineB);
            royxatniChopEtPos58(qaytimRoyxati, printStringBuffer, lineB);
            royxatniChopEtPos58(chegirmaRoyxati, printStringBuffer, lineB);
            royxatniChopEtPos58(qoshimchaDaromadRoyxati, printStringBuffer, lineB);
            balance = StringNumberUtils.yaxlitla(balance, -2);
            String line = String.format("%-15s %5s %10s\n", "Balans", " ", decimalFormat.format(balance));
            printStringBuffer.append(line);
            printStringBuffer.append(lineB);

            printStringBuffer.append(String.format("%29" +
                    "s\n", "XARIDINGIZ UCHUN TASHAKKUR"));
            printStringBuffer.append(lineB);
            printStringBuffer.append(String.format("%s\n\n\n\n\n\n\n\n", ""));

            String chipta = printStringBuffer.toString().trim();
            System.out.println(chipta);

            printerService.printString(printerNomi, chipta);
        }
    }
    private void royxatniChopEtPos58(ObservableList<HisobKitob> hisobKitobObservableList, StringBuffer printStringBuffer, String lineB) {
        String space = "                    ";
        if (hisobKitobObservableList.size() > 0) {
            for (HisobKitob hisobKitob: hisobKitobObservableList) {
                Valuta valuta = GetDbData.getValuta(hisobKitob.getValuta());
                Double valutaKursi = hisobKitob.getKurs();
                Standart amal = GetDbData.getAmal(hisobKitob.getAmal());
                String amalString = amal.getText().trim() +": ";
                String izoh = amalString + valuta.getValuta().trim().toUpperCase();
                String line = String.format("%.14s %5s %10s\n", izoh + space, decimalFormat.format(hisobKitob.getKurs()).trim(), decimalFormat.format(hisobKitob.getNarh()).trim());
                String lineS = String.format("%32s\n", decimalFormat.format(hisobKitob.getNarh() * joriyValutaKursi / valutaKursi));
                printStringBuffer.append(line);
                printStringBuffer.append(lineS);
            }
            printStringBuffer.append(lineB);
        }
    }

    public void tolovChiptasiniBerXP80(String printerNomi) {
        if (sotilganTovarlarRoyxati.size()>0) {
            StringBuffer printStringBuffer = new StringBuffer();
            String lineB = String.format("%.63s\n", "---------------------------------------------");
            SimpleDateFormat sana = new SimpleDateFormat("dd.MM.yyyy");
            SimpleDateFormat vaqt = new SimpleDateFormat("HH:mm:ss");
            Date date = new Date();
            String sanaString = sana.format(date);
            String vaqtString = vaqt.format(date);
            printStringBuffer.append(lineB);
            String shirkatNomi = GetDbData.getHisob(user.getTovarHisobi()).getText();
            printStringBuffer.append(String.format("%29s\n", shirkatNomi));
            printStringBuffer.append(lineB);
            printStringBuffer.append(String.format("%-15s %29s\n", "Telefon", user.getPhone()));
            printStringBuffer.append(String.format("%-15s %29s\n", "Sana", sanaString));
            printStringBuffer.append(String.format("%-15s %29s\n", "Vaqt", vaqtString));
            printStringBuffer.append(String.format("%-15s %29s\n", "Sotuvchi", user.getIsm()));
            printStringBuffer.append(String.format("%-15s %29s\n", "Oluvchi", qaydnomaData.getKirimNomi()));
            printStringBuffer.append(String.format("%-15s %29s\n", "Chipta N", qaydnomaData.getHujjat()));
            printStringBuffer.append("\n" + lineB);
            printStringBuffer.append(String.format("%-15s %5s %10s %12s\n", "Mahsulot", "Dona", "Narh", "Jami"));
            printStringBuffer.append(lineB);

            String space = "                    ";
            Double jamiMablag = 0d;
            for (HisobKitob hk : sotilganTovarlarRoyxati) {
                Double dona = hk.getDona();
                Double narh = hk.getDona() * hk.getNarh();
                jamiMablag += narh;
                String line = String.format("%.15s %5s %10s %12s\n", hk.getIzoh() + space, decimalFormat.format(dona), decimalFormat.format(hk.getNarh()), decimalFormat.format(narh));
                printStringBuffer.append(line);
            }

            for (HisobKitob hk : sotibOlinganTovarlarRoyxati) {
                Double dona = hk.getDona();
                Double narh = hk.getDona() * hk.getNarh();
                jamiMablag -= narh;
                String line = String.format("%.15s %5s %10s %12s\n", hk.getIzoh() + space, decimalFormat.format(dona), decimalFormat.format(-hk.getNarh()), decimalFormat.format(-narh));
                printStringBuffer.append(line);
            }
            printStringBuffer.append(lineB);

            if (jamiMablag > 0) {
                String line = String.format("%-15s %5s %23s\n", "Xarid jami", " ", decimalFormat.format(jamiMablag));
                printStringBuffer.append(line);
                printStringBuffer.append(lineB);
            }

            royxatniChopEtXP80(naqdTolovRoyxati, printStringBuffer, lineB);
            royxatniChopEtXP80(bankToloviRoyxati, printStringBuffer, lineB);
            royxatniChopEtXP80(qaytimRoyxati, printStringBuffer, lineB);
            royxatniChopEtXP80(chegirmaRoyxati, printStringBuffer, lineB);
            royxatniChopEtXP80(qoshimchaDaromadRoyxati, printStringBuffer, lineB);
            balance = StringNumberUtils.yaxlitla(balance, -2);
            String line = String.format("%-15s %5s %10s\n", "Balans", " ", decimalFormat.format(balance));
            printStringBuffer.append(line);
            printStringBuffer.append(lineB);

            printStringBuffer.append(String.format("%32" +
                    "s\n", "XARIDINGIZ UCHUN TASHAKKUR"));
            printStringBuffer.append(lineB);
            printStringBuffer.append(String.format("%s\n\n\n\n\n\n\n\n", ""));

            String chipta = printStringBuffer.toString().trim();
            System.out.println(chipta);

//            printerService.printString(printerNomi, chipta);
        }
    }
    private void royxatniChopEtXP80(ObservableList<HisobKitob> hisobKitobObservableList, StringBuffer printStringBuffer, String lineB) {
        String space = "                    ";
        if (hisobKitobObservableList.size() > 0) {
            String line = "";
            for (HisobKitob hisobKitob: hisobKitobObservableList) {
                Valuta valuta = GetDbData.getValuta(hisobKitob.getValuta());
                Double valutaKursi = hisobKitob.getKurs();
                Standart amal = GetDbData.getAmal(hisobKitob.getAmal());
                String amalString = amal.getText().trim() +": ";
                String izoh = amalString + valuta.getValuta().trim().toUpperCase();
                line = String.format("%.14s %5s %10s %12s\n", izoh + space, decimalFormat.format(hisobKitob.getKurs()), decimalFormat.format(hisobKitob.getNarh()), decimalFormat.format(hisobKitob.getNarh() * joriyValutaKursi / valutaKursi));
                printStringBuffer.append(line);
            }
            printStringBuffer.append(lineB);
        }
    }

    public void tolovChiptasiniBerXP80Bytes(String printerNomi) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (sotilganTovarlarRoyxati.size()>0) {
            StringBuffer printStringBuffer = new StringBuffer();
            String lineB = String.format("%.63s\n", "---------------------------------------------");
            SimpleDateFormat sana = new SimpleDateFormat("dd.MM.yyyy");
            SimpleDateFormat vaqt = new SimpleDateFormat("HH:mm:ss");
            Date date = new Date();
            String sanaString = sana.format(date);
            String vaqtString = vaqt.format(date);
            printStringBuffer.append(lineB);
            String shirkatNomi = GetDbData.getHisob(user.getTovarHisobi()).getText();
            try {
                byteArrayOutputStream.write(String.format("%29s\n", shirkatNomi).getBytes());
                byteArrayOutputStream.write(lineB.getBytes());
                byteArrayOutputStream.write(String.format("%-15s %29s\n", "Telefon", user.getPhone()).getBytes());
                byteArrayOutputStream.write(String.format("%-15s %29s\n", "Sana", sanaString).getBytes());
                byteArrayOutputStream.write(String.format("%-15s %29s\n", "Vaqt", vaqtString).getBytes());
                byteArrayOutputStream.write(String.format("%-15s %29s\n", "Sotuvchi", user.getIsm()).getBytes());
                byteArrayOutputStream.write(String.format("%-15s %29s\n", "Oluvchi", qaydnomaData.getKirimNomi()).getBytes());
                byteArrayOutputStream.write(String.format("%-15s %29s\n", "Chipta N", qaydnomaData.getHujjat()).getBytes());
                byteArrayOutputStream.write(("\n" + lineB).getBytes());
                byteArrayOutputStream.write(String.format("%-15s %5s %10s %12s\n", "Mahsulot", "Dona", "Narh", "Jami").getBytes());
                byteArrayOutputStream.write(lineB.getBytes());
                String space = "                    ";
                Double jamiMablag = 0d;
                for (HisobKitob hk : sotilganTovarlarRoyxati) {
                    Double dona = hk.getDona();
                    Double narh = hk.getDona() * hk.getNarh();
                    jamiMablag += narh;
                    String line = String.format("%.15s %5s %10s %12s\n", hk.getIzoh() + space, decimalFormat.format(dona), decimalFormat.format(hk.getNarh()), decimalFormat.format(narh));
                    byteArrayOutputStream.write(line.getBytes());
                }

                for (HisobKitob hk : sotibOlinganTovarlarRoyxati) {
                    Double dona = hk.getDona();
                    Double narh = hk.getDona() * hk.getNarh();
                    jamiMablag -= narh;
                    String line = String.format("%.15s %5s %10s %12s\n", hk.getIzoh() + space, decimalFormat.format(dona), decimalFormat.format(-hk.getNarh()), decimalFormat.format(-narh));
                    byteArrayOutputStream.write(line.getBytes());
                }
                printStringBuffer.append(lineB);

                if (jamiMablag > 0) {
                    String line = String.format("%-15s %5s %23s\n", "Xarid jami", " ", decimalFormat.format(jamiMablag));
                    byteArrayOutputStream.write(line.getBytes());
                    byteArrayOutputStream.write(lineB.getBytes());
                }

                royxatniChopEtXP80Bytes(naqdTolovRoyxati, printStringBuffer, lineB);
                royxatniChopEtXP80Bytes(bankToloviRoyxati, printStringBuffer, lineB);
                royxatniChopEtXP80Bytes(qaytimRoyxati, printStringBuffer, lineB);
                royxatniChopEtXP80Bytes(chegirmaRoyxati, printStringBuffer, lineB);
                royxatniChopEtXP80Bytes(qoshimchaDaromadRoyxati, printStringBuffer, lineB);
                balance = StringNumberUtils.yaxlitla(balance, -2);
                String line = String.format("%-15s %5s %10s\n", "Balans", " ", decimalFormat.format(balance));
                printStringBuffer.append(line);
                printStringBuffer.append(lineB);

                printStringBuffer.append(String.format("%32" +
                        "s\n", "XARIDINGIZ UCHUN TASHAKKUR"));
                printStringBuffer.append(lineB);
                printStringBuffer.append(String.format("%s\n\n\n\n\n\n\n\n", ""));

                String chipta = printStringBuffer.toString().trim();
                System.out.println(chipta);

//            printerService.printString(printerNomi, chipta);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
    private void royxatniChopEtXP80Bytes(ObservableList<HisobKitob> hisobKitobObservableList, StringBuffer printStringBuffer, String lineB) {
        String space = "                    ";
        if (hisobKitobObservableList.size() > 0) {
            String line = "";
            for (HisobKitob hisobKitob: hisobKitobObservableList) {
                Valuta valuta = GetDbData.getValuta(hisobKitob.getValuta());
                Double valutaKursi = hisobKitob.getKurs();
                Standart amal = GetDbData.getAmal(hisobKitob.getAmal());
                String amalString = amal.getText().trim() +": ";
                String izoh = amalString + valuta.getValuta().trim().toUpperCase();
                line = String.format("%.14s %5s %10s %12s\n", izoh + space, decimalFormat.format(hisobKitob.getKurs()), decimalFormat.format(hisobKitob.getNarh()), decimalFormat.format(hisobKitob.getNarh() * joriyValutaKursi / valutaKursi));
                printStringBuffer.append(line);
            }
            printStringBuffer.append(lineB);
        }
    }

    private Double amallarniAjrat(ObservableList<HisobKitob> observableList) {
        Double balance = 0d;
        if (observableList.size()>0) {
            for (HisobKitob hisobKitob: observableList) {
                switch (hisobKitob.getAmal()) {
                    case 2:
                        sotibOlinganTovarlarRoyxati.add(hisobKitob);
                        balance += hisobKitob.getSummaCol();
                        break;
                    case 4:
                        if (hisobKitob.getNarh() + hisobKitob.getDona() !=0d) {
                            if (hisobKitob.getHisob2().equals(qaydnomaData.getKirimId())) {
                                sotilganTovarlarRoyxati.add(hisobKitob);
                                balance -= hisobKitob.getSummaCol();
                                joriyValuta = sotilganTovarlarRoyxati.get(0).getValuta();
                                joriyValutaKursi = sotilganTovarlarRoyxati.get(0).getKurs();
                            }
                        }
                        break;
                    case 7: //tolov
                        naqdTolovRoyxati.add(hisobKitob);
                        balance += hisobKitob.getSummaCol();
                        break;
                    case 8: //qaytim
                        balance -= hisobKitob.getSummaCol();
                        hisobKitob.setNarh(-hisobKitob.getNarh());
                        qaytimRoyxati.add(hisobKitob);
                        break;
                    case 13: // chegirma
                        chegirmaRoyxati.add(hisobKitob);
                        balance += hisobKitob.getSummaCol();
                        break;
                    case 15: //bank tolovi
                        bankToloviRoyxati.add(hisobKitob);
                        balance += hisobKitob.getSummaCol();
                        break;
                    case 18: //qoshimcha daromad
                        balance -= hisobKitob.getSummaCol();
                        hisobKitob.setNarh(-hisobKitob.getNarh());
                        qoshimchaDaromadRoyxati.add(hisobKitob);
                        break;
                }
            }
        }
        return balance;
    }

    public String printerim() {
        Connection printersConnection = new SqliteDB().getDbConnection();
        StandartModels printerModels = new StandartModels("Printers");
        ObservableList<Standart> printers = printerModels.get_data(printersConnection);
        String printerNomi = "Topmadim";
        Standart myPrinter = null;
        if (printers.size()>0) {
            myPrinter = printers.get(0);
            printerNomi = myPrinter.getText();
        }
        return printerNomi;
    }

}
