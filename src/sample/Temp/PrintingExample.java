package sample.Temp;

import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.print.*;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.Sides;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.Orientation;
import org.apache.pdfbox.printing.PDFPageable;

public class PrintingExample {
    final static String fileName = "PrintedBarCode.pdf";
    final static String printerName = "XP-370B";


    public static void main(String args[]) throws Exception {
        print("PrintedBarCode1.pdf");
    }

    public static void print(String fileName) {
        PDDocument document = null;
        try {
            document = PDDocument.load(new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintRequestAttributeSet attr_set = new HashPrintRequestAttributeSet();
        PrinterJob job = PrinterJob.getPrinterJob();
        PDFPageable pdfPageable = new PDFPageable(document, Orientation.PORTRAIT);
        PageFormat pageFormat = pdfPageable.getPageFormat(0);
        Paper paper = new Paper();
        paper.setSize(156,85);
        pageFormat.setPaper(paper);
        job.setPageable(pdfPageable);
        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException e) {
                e.printStackTrace();
            }
        }
    }

    public static void print2() {
        FileInputStream fis = null;
        DocFlavor docFlavor =  DocFlavor.SERVICE_FORMATTED.PAGEABLE;
        try {
            fis = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Doc pdfDoc = new SimpleDoc(fis, docFlavor, null);
        PrintService printService = findPrintService(printerName);
        DocPrintJob printJob = printService.createPrintJob();
        try {
            printJob.print(pdfDoc, new HashPrintRequestAttributeSet());
        } catch (PrintException e) {
            e.printStackTrace();
        }
        try {
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void print3() {
        Path pdfPath = Paths.get(fileName);
        byte[] bytes = null;
        try {
            bytes = Files.readAllBytes(pdfPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayInputStream bais = new ByteArrayInputStream( bytes );

// First identify the default print service on the system
        PrintService printService = findPrintService(printerName);

// prepare the flvaor you are intended to print
        DocFlavor docFlavor = DocFlavor.BYTE_ARRAY.PDF;

// prepare the print job
        DocPrintJob printJob = printService.createPrintJob();

// prepare the document, with default attributes, ready to print
        Doc docToPrint = new SimpleDoc( bais, docFlavor, new HashDocAttributeSet());

// now send the doc to print job, with no attributes to print
        try {
            printJob.print( docToPrint, null );
        } catch (PrintException e) {
            e.printStackTrace();
        }
    }

    public static void print4() {
        File file = new File(fileName);
        DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;

        PrintService printService = findPrintService(printerName);
        PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
        attr.add(MediaSizeName.ISO_A4);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Doc doc = new SimpleDoc(fis, flavor, null);
        DocPrintJob job = printService.createPrintJob();
        try {
            job.print(doc, attr);
        } catch (PrintException e) {
            e.printStackTrace();
        }
        try {
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static PrintService findPrintService(String printerName) {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printService : printServices) {
            if (printService.getName().trim().equals(printerName)) {
                return printService;
            }
        }
        return null;
    }

    public static void print5() {
        DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PAGEABLE;
        PrintRequestAttributeSet patts = new HashPrintRequestAttributeSet();
        patts.add(Sides.DUPLEX);

        PrintService myService = findPrintService(printerName);


        if (myService == null) {
            throw new IllegalStateException("Printer not found");
        }

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Doc pdfDoc = new SimpleDoc(fis, DocFlavor.INPUT_STREAM.AUTOSENSE, null);
        DocPrintJob printJob = myService.createPrintJob();
        try {
            printJob.print(pdfDoc, new HashPrintRequestAttributeSet());
        } catch (PrintException e) {
            e.printStackTrace();
        }
        try {
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void print6(String dest) throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(dest));
        Barcode128 barcode128 = new Barcode128();
        barcode128.setCode(printerName);
        barcode128.setCodeType(Barcode128.CODE128);
        com.itextpdf.text.Rectangle one = new com.itextpdf.text.Rectangle(156,85);
        one.rotate();
        document.setPageSize(one);
        document.setMargins(10, 0, 5, 0);
        document.open();
        PdfContentByte pdfContentByte = pdfWriter.getDirectContent();
        com.itextpdf.text.Image code128Image = barcode128.createImageWithBarcode(pdfContentByte, BaseColor.BLACK, BaseColor.BLACK);
        float[] columnWidths = {1};
        PdfPTable table = new PdfPTable(columnWidths);

        table.getDefaultCell().setUseAscender(true);
        table.getDefaultCell().setUseDescender(true);
        Font f = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, GrayColor.GRAYWHITE);
        Paragraph paragraph = new Paragraph("This is a header", f);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        PdfPCell textCell = new PdfPCell(paragraph);
        PdfPCell imageCell = new PdfPCell();
        imageCell.setImage(code128Image);
        imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        textCell.setBackgroundColor(GrayColor.GRAYBLACK);
        textCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        textCell.setColspan(1);
//        table.addCell(textCell);
        table.addCell(imageCell);
        document.add(table);
        document.close();
    }

}