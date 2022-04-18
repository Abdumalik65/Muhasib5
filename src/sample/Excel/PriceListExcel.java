package sample.Excel;

import javafx.collections.ObservableList;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sample.Data.HisobKitob;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;

public class PriceListExcel {
    CellStyle kirimCenterStyle = null;
    CellStyle chiqimCenterStyle = null;
    CellStyle kirimLeftStyle = null;
    CellStyle chiqimLeftStyle = null;
    CellStyle headerStyle = null;
    CellStyle headerLeftStyle = null;
    CellStyle hLinkStyle = null;
    CellStyle hLinkStyle2 = null;
    CellStyle wrapCellStyle = null;

    XSSFFont headerFont = null;
    XSSFFont kirimFont = null;
    XSSFFont chiqimFont = null;
    XSSFFont font3 = null;
    XSSFFont hLinkFont = null;
    XSSFFont hLinkFont2 = null;
    XSSFFont chiptaFont = null;

    XSSFWorkbook wb;
    Sheet sheet;
    Row row;
    int rowIndex = 0;
    Cell cell;
    int cellIndex = 0;

    File file;
    File directory = new File(System.getProperty("user.dir") + File.separator + "Hisobot");
    String pathString;
    String printerim;
    Desktop desktop = Desktop.getDesktop();

    public PriceListExcel() {
        ibtido();
    }

    public PriceListExcel(String printerim) {
        this.printerim = printerim;
        ibtido();
    }

    private void ibtido() {
        initDirectory();
        initWorkBook();
        initFonts();
        initStyles();
    }

    private void initDirectory() {
        if (!directory.exists()) {
            directory.mkdir();
        }
        pathString = directory.getAbsolutePath() + File.separator;
    }

    private void initWorkBook() {
        wb = new XSSFWorkbook();
    }

    private void initFonts() {
        Short fSize1 = 10;
        Short fSize2 = 11;
        headerFont = wb.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerFont.setFontHeightInPoints(fSize1);

        kirimFont = wb.createFont();
        kirimFont.setBold(false);
        kirimFont.setColor(IndexedColors.BLACK.getIndex());
        kirimFont.setFontHeightInPoints(fSize1);

        chiqimFont = wb.createFont();
        chiqimFont.setBold(false);
        chiqimFont.setColor(IndexedColors.BLACK.getIndex());
        chiqimFont.setFontHeightInPoints((fSize1));

        font3 = wb.createFont();
        font3.setBold(false);
        font3.setColor(IndexedColors.BLACK.getIndex());
        font3.setFontHeightInPoints(fSize1);

        hLinkFont = wb.createFont();
        hLinkFont.setBold(false);
        hLinkFont.setUnderline(Font.U_SINGLE);
        hLinkFont.setColor(IndexedColors.WHITE.getIndex());
        hLinkFont.setFontHeightInPoints(fSize1);

        hLinkFont2 = wb.createFont();
        hLinkFont2.setBold(false);
        hLinkFont2.setUnderline(Font.U_SINGLE);
        hLinkFont2.setColor(IndexedColors.BLUE.getIndex());
        hLinkFont2.setFontHeightInPoints(fSize1);

        chiptaFont = wb.createFont();
        chiptaFont.setBold(true);
        chiptaFont.setColor(IndexedColors.BLACK.getIndex());
        chiptaFont.setFontHeightInPoints(fSize2);

    }

    private void initStyles() {
        headerStyle = wb.createCellStyle();
        setBorderStyle(headerStyle, BorderStyle.MEDIUM, IndexedColors.GREY_40_PERCENT.getIndex(),
                FillPatternType.SOLID_FOREGROUND, headerFont, VerticalAlignment.CENTER, HorizontalAlignment.CENTER);

        headerLeftStyle = wb.createCellStyle();
        setBorderStyle(headerLeftStyle, BorderStyle.MEDIUM, IndexedColors.GREY_40_PERCENT.getIndex(),
                FillPatternType.SOLID_FOREGROUND, headerFont, VerticalAlignment.CENTER, HorizontalAlignment.LEFT);

        kirimCenterStyle = wb.createCellStyle();
        setBorderStyle(kirimCenterStyle, BorderStyle.THIN, IndexedColors.WHITE.getIndex(),
                FillPatternType.SOLID_FOREGROUND, kirimFont, VerticalAlignment.CENTER, HorizontalAlignment.CENTER);
        CreationHelper creationHelper = wb.getCreationHelper();
        kirimCenterStyle.setDataFormat(creationHelper.createDataFormat().getFormat("#,##0"));

        kirimLeftStyle = wb.createCellStyle();
        setBorderStyle(kirimLeftStyle, BorderStyle.THIN, IndexedColors.WHITE.getIndex(), FillPatternType.SOLID_FOREGROUND,
                kirimFont, VerticalAlignment.CENTER, HorizontalAlignment.LEFT);

        chiqimCenterStyle = wb.createCellStyle();
        setBorderStyle(chiqimCenterStyle, BorderStyle.THIN, IndexedColors.BRIGHT_GREEN.getIndex(), FillPatternType.SOLID_FOREGROUND,
                chiqimFont, VerticalAlignment.CENTER, HorizontalAlignment.CENTER);

        chiqimLeftStyle = wb.createCellStyle();
        setBorderStyle(chiqimLeftStyle, BorderStyle.THIN, IndexedColors.BRIGHT_GREEN.getIndex(), FillPatternType.SOLID_FOREGROUND,
                chiqimFont, VerticalAlignment.CENTER, HorizontalAlignment.LEFT);

        hLinkStyle = wb.createCellStyle();
        setBorderStyle(hLinkStyle, BorderStyle.THIN, IndexedColors.WHITE.getIndex(), FillPatternType.SOLID_FOREGROUND,
                hLinkFont, VerticalAlignment.CENTER, HorizontalAlignment.LEFT);

        hLinkStyle2 = wb.createCellStyle();
        setBorderStyle(hLinkStyle, BorderStyle.THIN, IndexedColors.WHITE.getIndex(), FillPatternType.SOLID_FOREGROUND,
                hLinkFont2, VerticalAlignment.CENTER, HorizontalAlignment.LEFT);

        wrapCellStyle = wb.createCellStyle();
        setBorderStyle(wrapCellStyle, BorderStyle.THIN, IndexedColors.WHITE.getIndex(), FillPatternType.SOLID_FOREGROUND,
                kirimFont, VerticalAlignment.CENTER, HorizontalAlignment.LEFT);
        wrapCellStyle.setWrapText(true);

    }

    private void setBorderStyle(CellStyle cellStyle, BorderStyle borderStyle, short foregroundColor,
                                FillPatternType fillPatternType,
                                XSSFFont font,
                                VerticalAlignment verticalAlignment,
                                HorizontalAlignment horizontalAlignment)
    {
        cellStyle.setBorderBottom(borderStyle);
        cellStyle.setBorderTop(borderStyle);
        cellStyle.setBorderRight(borderStyle);
        cellStyle.setBorderLeft(borderStyle);

        cellStyle.setFillForegroundColor(foregroundColor);
        cellStyle.setFillPattern(fillPatternType);
        cellStyle.setFont(font);
        cellStyle.setVerticalAlignment(verticalAlignment);
        cellStyle.setAlignment(horizontalAlignment);
    }

    private void showFile(String fileName) {
        file = new File(fileName);
        try {
            desktop.open(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void print(ObservableList<HisobKitob> observableList) {
        sheet = wb.createSheet("Price list");
        rowIndex = 0;
        row = sheet.createRow(rowIndex);
        cellIndex = 0;
        headerEslatmaCell(rowIndex, cellIndex);
        cellIndex ++;
        headerAdadCell(rowIndex, cellIndex);

        cellIndex ++;
        headerNarhCell(rowIndex, cellIndex);

        cellIndex ++;
        headerChakanaCell(rowIndex, cellIndex);

        cellIndex ++;
        headerUlgurjiCell(rowIndex, cellIndex);


        for (HisobKitob h: observableList) {
            if (h.getDona()>0) {
                rowIndex++;
                row = sheet.createRow(rowIndex);

                cellIndex = 0;
                dataIzohCell(h, cellIndex);

                cellIndex++;
                dataDonaCell(h, cellIndex);

                cellIndex++;
                dataNarhCell(h, cellIndex);

                cellIndex++;
                dataChakanaCell(h, cellIndex);

                cellIndex++;
                dataUlgurjiCell(h, cellIndex);
            }
        }
        for (int i = 0; i<9 ; i++) {
            sheet.autoSizeColumn(i);
        }

        OutputStream fileOut = null;
        String fileName= pathString + "Price_"+ LocalDate.now() +".xlsx";
        try {
            fileOut = new FileOutputStream(fileName);
            wb.write(fileOut);
            showFile(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void headerEslatmaCell(int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue("Mahsulot");
        cell.setCellStyle(headerStyle);
    }
    private void headerAdadCell(int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue("Adad");
        cell.setCellStyle(headerStyle);
    }
    private void headerNarhCell(int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue("Narh");
        cell.setCellStyle(headerStyle);
    }
    private void headerChakanaCell(int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue("Chakana");
        cell.setCellStyle(headerStyle);
    }
    private void headerUlgurjiCell(int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue("Ulgurji");
        cell.setCellStyle(headerStyle);
    }
    private void dataIzohCell(HisobKitob h, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellStyle(kirimLeftStyle);
        cell.setCellValue(h.getIzoh());
    }
    private void dataDonaCell(HisobKitob h, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.NUMERIC);
        cell.setCellStyle(kirimCenterStyle);
        cell.setCellValue(h.getDona());
    }
    private void dataNarhCell(HisobKitob h, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.NUMERIC);
        cell.setCellStyle(kirimCenterStyle);
        cell.setCellValue(h.getNarh() / h.getDona());
    }
    private void dataChakanaCell(HisobKitob h, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.NUMERIC);
        cell.setCellStyle(kirimCenterStyle);
        cell.setCellValue(h.getSummaColumn());
    }

    private void dataUlgurjiCell(HisobKitob h, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.NUMERIC);
        cell.setCellStyle(kirimCenterStyle);
        cell.setCellValue(h.getBalans());
    }

}
