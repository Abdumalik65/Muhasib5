package sample.Temp;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.Orientation;
import org.apache.pdfbox.printing.PDFPageable;
import sample.Tools.PrinterService;

import javax.print.*;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.Sides;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PrintingExample2 {
    final static String fileName = "PrintedBarCode.pdf";
    final static String printerName = "XP-80C";


    public static void main(String args[]) throws Exception {
        PrinterService printerService = new PrinterService();
        StringBuffer printStringBuffer = new StringBuffer();

        String chipta = printStringBuffer.toString().trim();
        printerService.printString("XP-80C", chipta);

    }

 }