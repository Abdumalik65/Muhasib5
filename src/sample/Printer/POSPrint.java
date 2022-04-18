package sample.Printer;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintJobAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Sides;
import javax.print.event.PrintJobAttributeListener;
import javax.print.event.PrintJobListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;


public class POSPrint {
    public static void main(String[] args) throws IOException {
        DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PAGEABLE;
        PrintRequestAttributeSet patts = new HashPrintRequestAttributeSet();
        patts.add(Sides.DUPLEX);
        PrintService[] ps = PrintServiceLookup.lookupPrintServices(flavor, patts);
        if (ps.length == 0) {
            throw new IllegalStateException("No printer found");
        }

        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        PrintService myService = null;
        for (PrintService printService: printServices) {
            System.out.println(printService.getName());
            if (printService.getName().equals("XP-80C")) {
                myService = printService;
                break;
            }
        }

        if (myService == null) {
            throw new IllegalStateException("Printer nt found");
        }

        ByteArrayOutputStream expected = new ByteArrayOutputStream();

        expected.write("SHOP TITLE".getBytes());
        expected.write("Address line1".getBytes());
        expected.write("Address line2".getBytes());
        expected.write("Address line3".getBytes());
        expected.write("Phone N: 99999999".getBytes());
        expected.write(new String(new char[42]).replace("\0", "-").getBytes());

        DocPrintJob job = myService.createPrintJob();
        Doc doc = new SimpleDoc(expected.toByteArray(), DocFlavor.BYTE_ARRAY.AUTOSENSE, null);
        try {
            job.print(doc, new HashPrintRequestAttributeSet());
        } catch (PrintException e) {
            e.printStackTrace();
        }

    }
}
