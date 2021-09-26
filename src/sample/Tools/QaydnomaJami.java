package sample.Tools;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import sample.Data.HisobKitob;
import sample.Data.QaydnomaData;
import sample.Model.HisobKitobModels;
import sample.Model.QaydnomaModel;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;

public class QaydnomaJami extends GridPane {
    Connection connection;
    HisobKitobModels hisobKitobModels = new HisobKitobModels();
    QaydnomaModel qaydnomaModel = new QaydnomaModel();
    ObservableList<HisobKitob> hisobKitobs = FXCollections.observableArrayList();
    ObservableList<QaydnomaData> qaydnomaDatas = FXCollections.observableArrayList();
    Label jamiTextLabel = new Label("Jami");
    Label jamiDoubleLabel = new Label();
    DecimalFormat decimalFormat = new MoneyShow();

    public QaydnomaJami(Connection connection) {
        this.connection = connection;
        setPadding(new Insets(5));
        initJamiGridPane();
        jamiTextLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        jamiDoubleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        setHgap(25);
        setVgap(10);
    }

    public QaydnomaData getJami(QaydnomaData qaydnomaData, ObservableList<HisobKitob> hisobKitobs) throws SQLException, ParseException {
        Double sum = .00;
//        hisobKitobs = hisobKitobModels.getAnyData(connection, "hujjatId = " + qaydnomaData.getHujjat() + " AND amal = " + qaydnomaData.getAmalTuri(),"");
        for (HisobKitob h: hisobKitobs) {
            if (h.getTovar() == 0) {
                sum += h.getNarh()/h.getKurs();
            } else {
                sum += h.getNarh()/h.getKurs()*h.getDona();
            }
        }
        qaydnomaData.setJami(sum);
        getChildren().removeAll(getChildren());
        jamiDoubleLabel.setText(decimalFormat.format(sum));
        add(jamiTextLabel,0,0);
        add(jamiDoubleLabel,1,0);
        return qaydnomaData;
    }

    public ObservableList<QaydnomaData> refresh(ObservableList<QaydnomaData> qaydnomaDatas) {
        this.qaydnomaDatas = qaydnomaDatas;
        for (QaydnomaData q: qaydnomaDatas) {
            hisobKitobs = hisobKitobModels.getAnyData(connection, "qaydId="+q.getId()+" AND hujjatId = " + q.getHujjat() + " AND amal = " + q.getAmalTuri(),"");
            double sum = .00;
            for (HisobKitob h: hisobKitobs) {
                int ishora = q.getChiqimId().equals(h.getHisob1()) ? 1 : -1;

                if (h.getTovar() == 0) {
                    sum += ishora * h.getNarh()/h.getKurs();
                } else {
                    sum += ishora * h.getNarh()/h.getKurs()*h.getDona();
                }
            }
            q.setJami(sum);
            qaydnomaModel.update_data(connection, q);
        }
        return qaydnomaDatas;
    }

    public ObservableList<QaydnomaData> refresh(int amalId, ObservableList<QaydnomaData> qaydnomaDatas) {
        this.qaydnomaDatas = qaydnomaDatas;
        hisobKitobs = hisobKitobModels.getAnyData(connection, "amal = " + amalId,"");
        for (HisobKitob hk: hisobKitobs) {
            QaydnomaData qd = getQaydnomaData(qaydnomaDatas, hk.getQaydId());
            if (qd != null) {
                int ishora = qd.getChiqimId().equals(hk.getHisob1()) ? 1 : -1;
                double balance = qd.getJami();
                if (hk.getTovar() == 0) {
                    balance += ishora * hk.getNarh()/hk.getKurs();
                } else {
                    balance += ishora * hk.getNarh()/hk.getKurs()*hk.getDona();
                }
                qaydnomaModel.update_data(connection, qd);
            }
        }
        return qaydnomaDatas;
    }

    private QaydnomaData getQaydnomaData(ObservableList<QaydnomaData> qaydnomaDataObservableList, int qaydId) {
        QaydnomaData qaydData = null;
        for (QaydnomaData qd: qaydnomaDataObservableList) {
            if (qd.getId().equals(qaydId)) {
                qaydData = qd;
                break;
            }
        }
        return qaydData;
    }

    public void getJamiFromGrid(QaydnomaData qaydnomaData, ObservableList<HisobKitob> hisobKitobs) throws SQLException, ParseException {
        Double sum = .00;
        this. hisobKitobs = hisobKitobs;
        for (HisobKitob h: hisobKitobs) {
            if (h.getTovar() == 0) {
                sum += h.getNarh()/h.getKurs();
            } else {
                sum += h.getNarh()/h.getKurs()*h.getDona();
            }
        }
        jamiDoubleLabel.setText(decimalFormat.format(sum));
        qaydnomaData.setJami(sum);
    }

    public void initJamiGridPane() {
        add(jamiTextLabel, 0, 0);
        add(jamiDoubleLabel,2,0);
    }

    public Label getJamiTextLabel() {
        return jamiTextLabel;
    }

    public void setJamiTextLabel(Label jamiTextLabel) {
        this.jamiTextLabel = jamiTextLabel;
    }

    public Label getJamiDoubleLabel() {
        return jamiDoubleLabel;
    }

    public void setJamiDoubleLabel(Label jamiDoubleLabel) {
        this.jamiDoubleLabel = jamiDoubleLabel;
    }
}
