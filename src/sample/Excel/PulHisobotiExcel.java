package sample.Excel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sample.Data.*;
import sample.Model.HisobKitobModels;
import sample.Model.HisobModels;
import sample.Model.KursModels;
import sample.Model.QaydnomaModel;
import sample.Tools.GetDbData;

import java.awt.*;
import java.io.*;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class PulHisobotiExcel {
    CellStyle headerTextStyle = null;
    CellStyle headerNumericStyle = null;
    CellStyle dataTextCenterStyle = null;
    CellStyle dataNumericCenterStyle = null;

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
    Desktop desktop = Desktop.getDesktop();

    public PulHisobotiExcel() {
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
        CreationHelper creationHelper = wb.getCreationHelper();

        headerTextStyle = wb.createCellStyle();
        setBorderStyle(headerTextStyle, BorderStyle.MEDIUM, IndexedColors.GREY_40_PERCENT.getIndex(),
                FillPatternType.SOLID_FOREGROUND, headerFont, VerticalAlignment.CENTER, HorizontalAlignment.CENTER);

        headerNumericStyle = wb.createCellStyle();
        setBorderStyle(headerNumericStyle, BorderStyle.MEDIUM, IndexedColors.GREY_40_PERCENT.getIndex(),
                FillPatternType.SOLID_FOREGROUND, headerFont, VerticalAlignment.CENTER, HorizontalAlignment.CENTER);
        headerNumericStyle.setDataFormat(creationHelper.createDataFormat().getFormat("#,##0.00"));

        dataTextCenterStyle = wb.createCellStyle();
        setBorderStyle(dataTextCenterStyle, BorderStyle.THIN, IndexedColors.WHITE.getIndex(),
                FillPatternType.SOLID_FOREGROUND, kirimFont, VerticalAlignment.CENTER, HorizontalAlignment.CENTER);

        dataNumericCenterStyle = wb.createCellStyle();
        setBorderStyle(dataNumericCenterStyle, BorderStyle.THIN, IndexedColors.WHITE.getIndex(), FillPatternType.SOLID_FOREGROUND,
                kirimFont, VerticalAlignment.CENTER, HorizontalAlignment.CENTER);
        dataNumericCenterStyle.setDataFormat(creationHelper.createDataFormat().getFormat("#,##0.00"));

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

    public void pulHisoboti(Integer hisobId, ObservableList<HisobKitob> hk) {
        Hisob hisob = GetDbData.getHisob(hisobId);
        sheet = wb.createSheet(hisob.getId().toString());
/*********** Birinchi sarlabha ****************/
        rowIndex = 0;
        row = sheet.createRow(rowIndex);
        cellIndex = 0;
        headerHisobTextCell(hisob, rowIndex, cellIndex, sheet);

        cellIndex = 2;
        headerSanaCell(rowIndex, cellIndex, sheet);

        cellIndex = 4;
        headerBalanceCell(rowIndex, cellIndex, sheet, hk);

/*********** Ikkinchi sarlabha ****************/
        rowIndex++;
        row = sheet.createRow(rowIndex);

        cellIndex = 0;
        header2ValutaCell(rowIndex, cellIndex);

        cellIndex++;
        header2KursCell(rowIndex, cellIndex);

        cellIndex++;
        header2NarhCell(rowIndex, cellIndex);

        cellIndex++;
        header2SummaCell(rowIndex, cellIndex);

        cellIndex++;
        header2BalanceCell(rowIndex, cellIndex);

/*********** Ma`lumotlar jadvali ****************/

        for (HisobKitob h: hk) {
            rowIndex++;
            row = sheet.createRow(rowIndex);

            cellIndex = 0;
            dataValutaCell(hisobId, h, rowIndex, cellIndex);

            cellIndex++;
            dataKursCell(hisobId,h, rowIndex, cellIndex);

            cellIndex++;
            dataNarhCell(hisobId, h, rowIndex, cellIndex);

            cellIndex++;
            dataSummaColCell(hisobId, h, rowIndex, cellIndex);

            cellIndex++;
            dataBalanceCell(hisobId, h, rowIndex, cellIndex);
        }
        for (int i = 0; i<7 ; i++) {
            sheet.autoSizeColumn(i);
        }

        OutputStream fileOut = null;
        String fileName= pathString + hisob.getText().trim() + "_" + LocalDate.now() + ".xlsx";
        try {
            fileOut = new FileOutputStream(fileName);
            wb.write(fileOut);
            showFile(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void headerHisobTextCell(Hisob hisob, int rowIndex, int cellIndex, Sheet sheet) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        CellRangeAddress region = new CellRangeAddress(rowIndex, rowIndex, 0,1);
        sheet.addMergedRegion(region);
        cell.setCellStyle(headerTextStyle);
        cell.setCellValue(hisob.getText());
        CreationHelper createHelper = wb.getCreationHelper();
    }

    private void  headerSanaCell(int rowIndex, int cellIndex, Sheet sheet) {
        Cell cell = row.createCell(cellIndex);
        CellRangeAddress region2 = new CellRangeAddress(rowIndex, rowIndex, cellIndex,3);
        sheet.addMergedRegion(region2);
        cell.setCellStyle(headerTextStyle);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        cell.setCellValue(sdf.format(new Date()));
    }

    private void  headerBalanceCell(int rowIndex, int cellIndex, Sheet sheet, ObservableList<HisobKitob> hk) {
        Cell cell = row.createCell(cellIndex, CellType.FORMULA);
        CreationHelper creationHelper = wb.getCreationHelper();
        cell.setCellStyle(headerNumericStyle);
        String formula = "SUM(D:D)";
        cell.setCellFormula(formula);
    }

    private void header2ValutaCell(int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue("Valyuta");
        cell.setCellStyle(headerTextStyle);
    }
    private void header2KursCell(int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue("Kurs");
        cell.setCellStyle(headerTextStyle);
    }
    private void header2NarhCell(int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue("Narh");
        cell.setCellStyle(headerTextStyle);
    }
    private void header2SummaCell(int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue("Summa");
        cell.setCellStyle(headerTextStyle);
    }
    private void header2BalanceCell(int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue("Balans");
        cell.setCellStyle(headerTextStyle);
    }

    private void dataValutaCell(int hisobId, HisobKitob h, int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellStyle(dataTextCenterStyle);
        Valuta valuta = GetDbData.getValuta(h.getValuta());
        if (valuta != null) {
            cell.setCellValue(valuta.getValuta());
        }
    }
    private void dataKursCell(int hisobId, HisobKitob h, int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.NUMERIC);
        cell.setCellStyle(dataNumericCenterStyle);
        cell.setCellValue(h.getKurs());
    }
    private void dataNarhCell(int hisobId, HisobKitob h, int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.NUMERIC);
        cell.setCellStyle(dataNumericCenterStyle);
        cell.setCellValue(h.getNarh());
    }
    private void dataSummaColCell(int hisobId, HisobKitob h, int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.NUMERIC);
        cell.setCellStyle(dataNumericCenterStyle);
        cell.setCellValue(h.getBalans());
    }
    private void dataBalanceCell(int hisobId, HisobKitob h, int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.NUMERIC);
        cell.setCellStyle(dataNumericCenterStyle);
        cell.setCellValue(h.getBalans());
    }
}
