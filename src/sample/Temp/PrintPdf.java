package sample.Temp;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import java.awt.print.PrinterJob;
import java.io.*;

public class PrintPdf {
    public static void main(String[] args) throws IOException, PrintException {
        File file = new File("test.pdf");

        byte[] pdfByte = getBytesFromFile(file);
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        HashPrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
        PrintService service = null;
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        if (printerJob.printDialog()) {
            service = printerJob.getPrintService();
            DocPrintJob job = service.createPrintJob();
            Doc doc = new SimpleDoc(pdfByte, flavor, null);
            job.print(doc, attributes);
        }
    }
    // Returns the contents of the file in a byte array.

    public static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        // Get the size of the file
        long length = file.length();

        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }

        // Close the input stream and return bytes
        is.close();
        return bytes;
    }
}
