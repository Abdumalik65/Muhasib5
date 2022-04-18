package sample.Excel;

import javafx.collections.ObservableList;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sample.Data.Hisob;

import java.awt.*;
import java.io.*;
import java.time.LocalDate;

public class HaqdorQarzdorExcel {
    CellStyle kirimCenterStyle = null;
    CellStyle kirimNumericCenterStyle = null;
    CellStyle kirimLeftStyle = null;
    CellStyle headerStyle = null;
    CellStyle headerLeftStyle = null;

    XSSFFont headerFont = null;
    XSSFFont kirimFont = null;
    XSSFFont font3 = null;

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

    public HaqdorQarzdorExcel() {
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
        headerFont = wb.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerFont.setFontHeightInPoints(fSize1);

        kirimFont = wb.createFont();
        kirimFont.setBold(false);
        kirimFont.setColor(IndexedColors.BLACK.getIndex());
        kirimFont.setFontHeightInPoints(fSize1);

        font3 = wb.createFont();
        font3.setBold(false);
        font3.setColor(IndexedColors.BLACK.getIndex());
        font3.setFontHeightInPoints(fSize1);

    }

    private void initStyles() {
        CreationHelper creationHelper = wb.getCreationHelper();
        headerStyle = wb.createCellStyle();
        setBorderStyle(headerStyle, BorderStyle.MEDIUM, IndexedColors.GREY_40_PERCENT.getIndex(),
                FillPatternType.SOLID_FOREGROUND, headerFont, VerticalAlignment.CENTER, HorizontalAlignment.CENTER);

        headerLeftStyle = wb.createCellStyle();
        setBorderStyle(headerLeftStyle, BorderStyle.MEDIUM, IndexedColors.GREY_40_PERCENT.getIndex(),
                FillPatternType.SOLID_FOREGROUND, headerFont, VerticalAlignment.CENTER, HorizontalAlignment.LEFT);

        kirimCenterStyle = wb.createCellStyle();
        setBorderStyle(kirimCenterStyle, BorderStyle.THIN, IndexedColors.WHITE.getIndex(),
                FillPatternType.SOLID_FOREGROUND, kirimFont, VerticalAlignment.CENTER, HorizontalAlignment.CENTER);

        kirimLeftStyle = wb.createCellStyle();
        setBorderStyle(kirimLeftStyle, BorderStyle.THIN, IndexedColors.WHITE.getIndex(), FillPatternType.SOLID_FOREGROUND,
                kirimFont, VerticalAlignment.CENTER, HorizontalAlignment.LEFT);

        kirimNumericCenterStyle = wb.createCellStyle();
        setBorderStyle(kirimNumericCenterStyle, BorderStyle.THIN, IndexedColors.WHITE.getIndex(),
                FillPatternType.SOLID_FOREGROUND, kirimFont, VerticalAlignment.CENTER, HorizontalAlignment.CENTER);
        kirimNumericCenterStyle.setDataFormat(creationHelper.createDataFormat().getFormat("#,##0.00"));

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
    public void print(ObservableList<Hisob> hisoblar) {
        sheet = wb.createSheet("Hisoblar");
        rowIndex = 0;
        row = sheet.createRow(rowIndex);

        cellIndex = 0;
        headerHisobNomiCell();

        cellIndex++;
        headerHisobNarhCell();

        cellIndex++;
        headerHisobBalanceCell();

        for (Hisob h: hisoblar) {
            Double balance = h.getBalans();
            if (balance != 0d) {
                rowIndex++;
                row = sheet.createRow(rowIndex);

                cellIndex = 0;
                dataHisobNomiCell(h);

                cellIndex++;
                dataHisobNarhCell(h);

                cellIndex++;
                dataHisobBalanceCell(rowIndex, cellIndex);
            }
        }
        for (int i = 0; i<3 ; i++) {
            sheet.autoSizeColumn(i);
        }

        OutputStream fileOut = null;
        String fileName= pathString + "Haqdorlar_" + LocalDate.now() + ".xlsx";
        try {
            fileOut = new FileOutputStream(fileName);
            wb.write(fileOut);
            showFile(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void headerHisobNomiCell() {
        cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue("Hisob nomi");
        cell.setCellStyle(headerStyle);
    }
    private void headerHisobNarhCell() {
        cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue("Narh");
        cell.setCellStyle(headerStyle);
    }
    private void headerHisobBalanceCell() {
        cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue("Balans");
        cell.setCellStyle(headerStyle);
    }
    private void dataHisobNomiCell(Hisob h) {
        cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue(h.getText());
        cell.setCellStyle(kirimLeftStyle);
    }
    private void dataHisobNarhCell(Hisob h) {
        cell = row.createCell(cellIndex, CellType.NUMERIC);
        cell.setCellValue(h.getBalans());
        cell.setCellStyle(kirimNumericCenterStyle);
    }
    private void dataHisobBalanceCell(int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.FORMULA);
        cell.setCellStyle(kirimNumericCenterStyle);
        String formula = "";
        if (rowIndex < 2) {
            formula = "B2";
        } else {
            formula = "C" + rowIndex + " + B" + (rowIndex + 1);
        }
        cell.setCellFormula(formula);
    }

}
