package sample.Tools;

import javafx.collections.ObservableList;
import sample.Config.SqliteDBPrinters;
import sample.Data.Standart;
import sample.Model.StandartModels;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class PrinterService implements Printable {
    public static List<String> getPrinters(){

        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();

        PrintService printServices[] = PrintServiceLookup.lookupPrintServices(flavor, pras);

        List<String> printerList = new ArrayList<String>();
        for(PrintService printerService: printServices){
            printerList.add( printerService.getName());
        }
        return printerList;
    }

    @Override
    public int print(Graphics g, PageFormat pf, int page)
            throws PrinterException {
        if (page > 0) { /* We have only one page, and 'page' is zero-based */
            return NO_SUCH_PAGE;
        }

        /*
         * User (0,0) is typically outside the imageable area, so we must
         * translate by the X and Y values in the PageFormat to avoid clipping
         */
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());
        /* Now we perform our rendering */

        g.setFont(new Font("Roman", 0, 8));
        g.drawString("Hello world !", 0, 10);

        return PAGE_EXISTS;
    }

    public void printString(String printerName, String text) {

        // find the printService of name printerName
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();

        PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, pras);
        PrintService service = findPrintService(printerName, printService);

        if (service != null) {

            DocPrintJob job = service.createPrintJob();

            try {

                byte[] bytes;

                // important for umlaut chars
                bytes = text.getBytes("CP437");

                Doc doc = new SimpleDoc(bytes, flavor, null);


                job.print(doc, null);

            } catch (Exception e) {
                // TODO Auto-generated catch block
            e.printStackTrace();
            }
        } else {
            Alerts.printerNotReadyAlert();
        }

    }

    public void printBytes(String printerName, byte[] bytes) {

        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();

        PrintService printService[] = PrintServiceLookup.lookupPrintServices(
                flavor, pras);
        PrintService service = findPrintService(printerName, printService);

        if (service != null) {
            DocPrintJob job = service.createPrintJob();
            try {

                Doc doc = new SimpleDoc(bytes, flavor, null);
                job.print(doc, null);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Alerts.printerNotReadyAlert();
        }
    }

    private PrintService findPrintService(String printerName, PrintService[] services) {
        for (PrintService service : services) {
            String serviceName = service.getName();
            if (serviceName.equalsIgnoreCase(printerName)) {
                System.out.println("ULANDIK " + serviceName);
                return service;
            }
        }
        return null;
    }
}