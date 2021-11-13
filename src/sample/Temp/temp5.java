package sample.Temp;

import com.mysql.cj.jdbc.DatabaseMetaData;
import com.sun.tools.javac.code.Scope;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Config.MySqlDB;
import sample.Config.MySqlDBGeneral;
import sample.Data.*;
import sample.Model.*;
import sample.Tools.Alerts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

public class temp5 {
    public static void main(String[] args) throws ParseException {
        Connection connection = new MySqlDB().getDbConnection();
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        ObservableList<HisobKitob> hisobKitobObservableList = hisobKitobModels.get_data(connection);
        for (HisobKitob h: hisobKitobObservableList) {
            if (h.getManba()>0) {
                ObservableList<HisobKitob> hkList = hisobKitobModels.getAnyData(connection, "id=" + h.getManba(), "");
                if (hkList.size() == 0) {
                    System.out.println(h.getId());
                }
            }
        }
    }
}
