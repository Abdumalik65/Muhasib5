package sample.Temp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Config.MySqlDBGeneral;
import sample.Data.*;
import sample.Enums.ServerType;
import sample.Model.HisobKitobModels;
import sample.Model.HisobModels;
import sample.Tools.GetDbData;
import sample.Tools.StringNumberUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class copyTemp {
    public static void main(String[] args) {
        Connection connection = new MySqlDBGeneral(ServerType.REMOTE).getDbConnection();
        GetDbData.initData(connection);
        ObservableList<HisobKitob> pullar = pulRoyxati(connection, 196);
        ObservableList<HisobKitob> tovarlar = tovarRoyxati(connection, 196);
    }

    private static ObservableList<Balance> zaxiraniOl(Connection connection) {
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        Map<String, Hisob> barCodeMap = new HashMap<>();
        ObservableList<Balance> books=FXCollections.observableArrayList();
        String dokonlar = "";
        List<Integer> dokonlarRoyxati = new ArrayList<>();
        Qoldiq qoldiq = new Qoldiq();
        String select = "Select group_concat(ID3, '') FROM DOKONLAR;";
        ResultSet rs = hisobKitobModels.getResultSet(connection, select);

        try{
            if (rs.next()) {
                dokonlar = rs.getString(1);
                dokonlarRoyxati = dokonlarRoyxati(dokonlar);
                System.out.println(dokonlar);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        select = "select * from HisobKitob where (hisob1 in("+ dokonlar +") and hisob2 in("+ dokonlar +")) and tovar>0 group by tovar";
        rs = hisobKitobModels.getResultSet(connection, select);

        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Map<Integer, Balance> tovarlarBalansi = new HashMap<>();
            while (rs.next()) {
                Integer id =  rs.getInt(1);
                Integer qaydId =  rs.getInt(2);
                Integer hujjatId =  rs.getInt(3);
                Integer amalId =  rs.getInt(4);
                Integer hisob1Id =  rs.getInt(5);
                Integer hisob2Id =  rs.getInt(6);
                Integer valutaId =  rs.getInt(7);
                Integer tovarId =  rs.getInt(8);
                Double kurs = rs.getDouble(9);
                String barCode =  rs.getString(10);
                Double dona = rs.getDouble(11);
                Double narh = rs.getDouble(12);
                Integer manba =  rs.getInt(13);
                String izoh =  rs.getString(14);
                Integer userId =  rs.getInt(15);
                Date dateTime =  sdf.parse(rs.getString(16));
                BarCode bc = GetDbData.getBarCode(barCode);
                Standart tovar = GetDbData.getTovar(tovarId);
                if (hisobBiznikimi(dokonlarRoyxati, hisob1Id)) {
                    Balance balance = null;
                    if (tovarlarBalansi.containsKey(hisob1Id)) {
                        balance = tovarlarBalansi.get(hisob1Id);
                        Double adadBalance = balance.getAdadChiqim();
                        Double narhBalance = balance.getNarhChiqim();
                        balance.setAdadChiqim(adadBalance + dona * tovarDonasi(bc));
                        balance.setNarhChiqim(narhBalance + narh);
                    } else {
                        balance = new Balance();
                        balance.setId(tovarId);
                        balance.setText(tovar.getText());
                        balance.setAdadChiqim(dona * tovarDonasi(bc));
                        balance.setNarhChiqim(narh);
                        tovarlarBalansi.put(tovarId, balance);
                    }
                    if (tovarlarBalansi.containsKey(hisob2Id)) {
                        balance = tovarlarBalansi.get(hisob2Id);
                        Double adadBalance = balance.getAdadKirim();
                        Double narhBalance = balance.getNarhKirim();
                        balance.setAdadKirim(adadBalance + dona * tovarDonasi(bc));
                        balance.setNarhKirim(narhBalance + narh);
                    } else {
                        balance = new Balance();
                        balance.setId(tovarId);
                        balance.setText(tovar.getText());
                        balance.setAdadKirim(dona * tovarDonasi(bc));
                        balance.setNarhKirim(narh);
                        tovarlarBalansi.put(tovarId, balance);
                    }
                    books.add(balance);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        for (Balance b: books) {
            b.setAdadJami(b.getAdadKirim() - b.getAdadChiqim());
            b.setNarhJami(b.getNarhKirim() - b.getNarhChiqim());
        }
        books.removeIf(balance -> balance.getNarhJami() == 0d);
        for (Balance b: books) {
            System.out.println(b.getText() + "|" + b.getAdadJami() + "|" + b.getNarhJami());
        }
        return books;
    }

    private static List<Integer> dokonlarRoyxati(String dokonlar) {
        String[] dokonlarRoyxati = dokonlar.split(",");
        List<Integer> dokonlarInteger = new ArrayList<>();
        for (String s: dokonlarRoyxati) {
            dokonlarInteger.add(Integer.valueOf(s));
        }
        return dokonlarInteger;
    }

    private static Boolean hisobBiznikimi(List<Integer> dokonlar, Integer hisobId) {
        Boolean bizniki = false;
        for (Integer dokon: dokonlar) {
            if (hisobId.equals(dokon)) {
                bizniki = true;
                break;
            }
        }
        return bizniki;
    }
    private static double tovarDonasi(BarCode barCode) {
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
    private static ObservableList<Balance> dokonlarZaxirasiniOl(Connection connection, Integer tovar1Id) {
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        ObservableList<Balance> books=FXCollections.observableArrayList();
        String dokonlar = "";
        List<Integer> dokonlarRoyxati = new ArrayList<>();
        Map<Integer, Balance> hisoblarBalansi = new HashMap<>();
        Qoldiq qoldiq = new Qoldiq();
        String select = "Select group_concat(ID3, '') FROM DOKONLAR;";
        ResultSet rs = hisobKitobModels.getResultSet(connection, select);

        try{
            if (rs.next()) {
                dokonlar = rs.getString(1);
                System.out.println(dokonlar);
                dokonlarRoyxati = dokonlarRoyxati(dokonlar);
                dokonlarRoyxati.forEach(dokon -> {
                    Balance balance = new Balance();
                    Hisob hisob = GetDbData.getHisob(dokon);
                    balance.setId(hisob.getId());
                    balance.setText(hisob.getText());
                    books.add(balance);
                    hisoblarBalansi.put(hisob.getId(), balance);
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        select = "select * from HisobKitob where (hisob1 in("+ dokonlar +") or hisob2 in("+ dokonlar +")) and tovar = " + tovar1Id + " order by tovar";
        rs = hisobKitobModels.getResultSet(connection, select);
        int i=0;
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            while (rs.next()) {
                i++;
                Integer id =  rs.getInt(1);
                Integer qaydId =  rs.getInt(2);
                Integer hujjatId =  rs.getInt(3);
                Integer amalId =  rs.getInt(4);
                Integer hisob1Id =  rs.getInt(5);
                Integer hisob2Id =  rs.getInt(6);
                Integer valutaId =  rs.getInt(7);
                Integer tovarId =  rs.getInt(8);
                Double kurs = rs.getDouble(9);
                String barCode =  rs.getString(10);
                Double dona = rs.getDouble(11);
                Double narh = rs.getDouble(12);
                Integer manba =  rs.getInt(13);
                String izoh =  rs.getString(14);
                Integer userId =  rs.getInt(15);
                Date dateTime =  sdf.parse(rs.getString(16));
                BarCode bc = GetDbData.getBarCode(barCode);
                Standart tovar = GetDbData.getTovar(tovarId);
                Balance balance = null;
                dona *= tovarDonasi(bc);
                narh /= kurs;
                if (hisobBiznikimi(dokonlarRoyxati, hisob1Id)) {
                    balance = hisoblarBalansi.get(hisob1Id);
                    Double adadBalance = balance.getAdadChiqim();
                    Double narhBalance = balance.getNarhChiqim();
                    balance.setAdadChiqim(adadBalance + dona);
                    balance.setNarhChiqim(narhBalance + narh * dona);
                }
                if (hisobBiznikimi(dokonlarRoyxati, hisob2Id)) {
                    balance = hisoblarBalansi.get(hisob2Id);
                    Double adadBalance = balance.getAdadKirim();
                    Double narhBalance = balance.getNarhKirim();
                    balance.setAdadKirim(adadBalance + dona);
                    balance.setNarhKirim(narhBalance + narh * dona);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        for (Balance b: books) {
            b.setAdadJami(b.getAdadKirim() - b.getAdadChiqim());
            b.setNarhJami(b.getNarhKirim() - b.getNarhChiqim());
        }
        books.removeIf(balance -> balance.getAdadJami() == 0d);
        System.out.println("Jami :" + i);
        for (Balance b: books) {
            System.out.println(b.getText() + "|" + b.getAdadJami() + "|" + b.getNarhJami());
        }
        return books;
    }

    private static ObservableList<Balance> containerZaxirasiniOl(Connection connection, Integer hisobId) {
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        HisobModels hisobModels = new HisobModels();
        Hisob hisob = GetDbData.getHisob(hisobId);
        Hisob keldiKetdiHisobi = hisobModels.keldiKetdiHisobi(connection, hisob);
        ObservableList<Balance> books=FXCollections.observableArrayList();
        ObservableList<HisobKitob> pulLRoyxati = pulRoyxati(connection, hisobId);
        ObservableList<HisobKitob> tovarRoyxati = FXCollections.observableArrayList();
        String dokonlar = "";
        Map<Integer, Balance> pullarBalansi = new HashMap<>();
        Map<Integer, HisobKitob> hkMap = new HashMap<>();
        Qoldiq qoldiq = new Qoldiq();
        String select = "select valuta, sum(if(hisob1 = " + hisobId + ", -narh, narh)) from HisobKitob where (hisob1 ="+ hisobId +" or hisob2 ="+ hisobId +") and tovar = 0 group by valuta order by valuta";
        ResultSet rs  = hisobKitobModels.getResultSet(connection, select);
        int i=0;
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            while (rs.next()) {
                Integer valutaId = rs.getInt(1);
                Double narh = rs.getDouble(2);
                if (StringNumberUtils.yaxlitla(narh, -2) != 0d) {
                    HisobKitob hisobKitob = new HisobKitob(
                            0,
                            0,
                            0,
                            1,
                            hisobId,
                            keldiKetdiHisobi.getId(),
                            valutaId,
                            0,
                            0d,
                            "",
                            0d,
                            narh,
                            0,
                            "",
                            1,
                            null
                    );
                    pulLRoyxati.add(hisobKitob);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    private static ObservableList<HisobKitob> pulRoyxati(Connection connection, Integer hisobId) {
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        HisobModels hisobModels = new HisobModels();
        Hisob hisob = GetDbData.getHisob(hisobId);
        Hisob keldiKetdiHisobi = hisobModels.keldiKetdiHisobi(connection, hisob);
        ObservableList<HisobKitob> pulLRoyxati = FXCollections.observableArrayList();
        String select = "select valuta, sum(if(hisob1 = " + hisobId + ", -narh, narh)) from HisobKitob where (hisob1 ="+ hisobId +" or hisob2 ="+ hisobId +") and tovar = 0 group by valuta order by valuta";
        ResultSet rs  = hisobKitobModels.getResultSet(connection, select);
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            while (rs.next()) {
                Integer valutaId = rs.getInt(1);
                Double narh = rs.getDouble(2);
                if (StringNumberUtils.yaxlitla(narh, -2) != 0d) {
                    HisobKitob hisobKitob = new HisobKitob(
                            0,
                            0,
                            0,
                            1,
                            hisobId,
                            keldiKetdiHisobi.getId(),
                            valutaId,
                            0,
                            0d,
                            "",
                            0d,
                            narh,
                            0,
                            "",
                            1,
                            null
                    );
                    pulLRoyxati.add(hisobKitob);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pulLRoyxati;
    }

    private static ObservableList<HisobKitob> tovarRoyxati(Connection connection, Integer hisobId) {
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        HisobModels hisobModels = new HisobModels();
        Hisob hisob = GetDbData.getHisob(hisobId);
        Hisob keldiKetdiHisobi = hisobModels.keldiKetdiHisobi(connection, hisob);
        ObservableList<HisobKitob> tovarRoyxati = FXCollections.observableArrayList();
        String select = "select barcode, sum(if(hisob1 = "+hisobId+", -dona, dona)) as dona,sum(if(hisob1 = "+hisobId+", -narh*dona/kurs, narh*dona/kurs)) as narh from HisobKitob where (hisob1 ="+ hisobId +" or hisob2 ="+ hisobId +") and tovar > 0 group by barcode order by tovar";
        ResultSet rs  = hisobKitobModels.getResultSet(connection, select);
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            while (rs.next()) {
                String bc = rs.getString(1);
                Double adad = rs.getDouble(2);
                Double narh = rs.getDouble(3);
                if (StringNumberUtils.yaxlitla(narh, -2) != 0d) {
                    HisobKitob hisobKitob = new HisobKitob(
                            0,
                            0,
                            0,
                            1,
                            hisobId,
                            keldiKetdiHisobi.getId(),
                            1,
                            0,
                            1d,
                            bc,
                            adad,
                            narh,
                            0,
                            "",
                            1,
                            null
                    );
                    tovarRoyxati.add(hisobKitob);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tovarRoyxati;

    }
}
