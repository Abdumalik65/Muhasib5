package sample.Temp;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.qrcode.EncodeHintType;
import com.itextpdf.text.pdf.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class QrCodePrint {
    public static void main(String[] args) {
        Document pdfDocument = new Document(PageSize.A4);
        File pdfFile = new File("qrCode.pdf");
        try {
            PdfWriter.getInstance(pdfDocument, new FileOutputStream(pdfFile));
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        pdfDocument.open();

        PdfPTable qrCodeTable = new PdfPTable(new float[] { 1.0f });

        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "ISO-8859-1");
        BarcodeQRCode qrCode = new BarcodeQRCode("Allohumma sallim", 200, 200, hints);
        Image qrCodeImage = null;
        try {
            qrCodeImage = qrCode.getImage();
        } catch (BadElementException e) {
            e.printStackTrace();
        }

        qrCodeTable.addCell(qrCodeImage);
        try {
            pdfDocument.add(qrCodeTable);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        pdfDocument.close();    }
}
