package sample.Temp;

import java.awt.print.PrinterJob;
import java.io.File;
import javax.print.PrintService;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;

public class PrintPdf {
private static Logger log = Logger.getLogger(PrintPdf.class);

    public void print(String printer, String pdfRoute) {
        log.info("Printing document: "+pdfRoute);
        try{
         PDDocument document = null;
                document = PDDocument.load(new File(pdfRoute));
                PrinterJob printJob = PrinterJob.getPrinterJob();   
                    PrintService[] printService = PrinterJob.lookupPrintServices();
                     boolean printerFound = false;
                     for(int i = 0; !printerFound && i < printService.length; i++)
                     {
                         if(printService[i].getName().indexOf(printer) != -1)
                         {
                             printJob.setPrintService(printService[i]);
                             printerFound = true;
                             break;
                         }
                     }
                     if(printerFound) {}
                     else
                        log.info("The printer: "+printer + "was NOT found");
        }catch(Exception e){
            e.printStackTrace();
            log.error("Error when printing pdf",e);
        }

    }




}
