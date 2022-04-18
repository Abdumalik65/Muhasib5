package sample.Excel;

import javafx.collections.ObservableList;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sample.Data.Hisob;
import sample.Data.HisobKitob;
import sample.Data.Standart;
import sample.Tools.GetDbData;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;

public class Zaxira3Excel {
    CellStyle kirimCenterStyle = null;
    CellStyle chiqimCenterStyle = null;
    CellStyle kirimLeftStyle = null;
    CellStyle kirimDateStyle= null;
    CellStyle chiqimDateStyle= null;
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
    Hisob hisob;
    Standart tovar;
    ObservableList<HisobKitob> hisobKitobObservableList;

    File file;
    File directory = new File(System.getProperty("user.dir") + File.separator + "Hisobot");
    String pathString;
    String printerim;
    Desktop desktop = Desktop.getDesktop();
    CreationHelper creationHelper;

    public Zaxira3Excel() {
        ibtido();
    }

    public Zaxira3Excel(String printerim) {
        this.printerim = printerim;
        ibtido();
    }

    public Zaxira3Excel(Hisob hisob, Standart tovar, ObservableList<HisobKitob> hisobKitobObservableList) {
        this.hisob = hisob;
        this.tovar = tovar;
        this.hisobKitobObservableList = hisobKitobObservableList;
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
        creationHelper = wb.getCreationHelper();

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
        kirimCenterStyle.setDataFormat(creationHelper.createDataFormat().getFormat("#,##0.00"));

        kirimLeftStyle = wb.createCellStyle();
        setBorderStyle(kirimLeftStyle, BorderStyle.THIN, IndexedColors.WHITE.getIndex(), FillPatternType.SOLID_FOREGROUND,
                kirimFont, VerticalAlignment.CENTER, HorizontalAlignment.LEFT);

        kirimDateStyle = wb.createCellStyle();
        setBorderStyle(kirimDateStyle, BorderStyle.THIN, IndexedColors.WHITE.getIndex(), FillPatternType.SOLID_FOREGROUND,
                kirimFont, VerticalAlignment.CENTER, HorizontalAlignment.LEFT);
        kirimDateStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd.MM.yyyy"));

        chiqimDateStyle = wb.createCellStyle();
        setBorderStyle(chiqimDateStyle, BorderStyle.THIN, IndexedColors.BRIGHT_GREEN.getIndex(), FillPatternType.SOLID_FOREGROUND,
                kirimFont, VerticalAlignment.CENTER, HorizontalAlignment.LEFT);
        chiqimDateStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd.MM.yyyy"));

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

    public void print() {
        if (hisobKitobObservableList.size()>0) {
            HisobKitob hisobKitob = hisobKitobObservableList.get(0);
            sheet = wb.createSheet(hisob.getText());
            rowIndex = 0;
            row = sheet.createRow(rowIndex);
            cellIndex = 0;
            headerHisobCell(hisob, rowIndex, cellIndex);
            headerTovarCell(tovar, rowIndex, cellIndex);

            rowIndex++;
            row = sheet.createRow(rowIndex);
            cellIndex = 0;
            headerSanaCell(cellIndex);
            cellIndex ++;
            headerAdadCell(cellIndex);

            cellIndex++;
            headerNarhCell(cellIndex);

            cellIndex++;
            headerJamiCell(cellIndex);
            for (HisobKitob h : hisobKitobObservableList) {
                if (h.getDona() > 0) {
                    rowIndex++;
                    row = sheet.createRow(rowIndex);

                    cellIndex = 0;
                    dataSanaCell(h, cellIndex);

                    cellIndex++;
                    dataDonaCell(h, cellIndex);

                    cellIndex++;
                    dataNarhCell(h, cellIndex);

                    cellIndex++;
                    dataJamiCell(h, cellIndex);
                }
            }
            for (int i = 0; i < 9; i++) {
                sheet.autoSizeColumn(i);
            }

            OutputStream fileOut = null;
            String fileName = pathString + "Zaxira3_" + LocalDate.now() + ".xlsx";
            try {
                fileOut = new FileOutputStream(fileName);
                wb.write(fileOut);
                showFile(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void headerHisobCell(Hisob hisob, int rowIndex, int cellIndex) {
        Cell cell = row.createCell(0, CellType.STRING);
        CellRangeAddress region = new CellRangeAddress(rowIndex, rowIndex, 0,1);
        sheet.addMergedRegion(region);
        cell.setCellValue(hisob.getText());
        cell.setCellStyle(headerStyle);
    }
    private void headerTovarCell(Standart tovar, int rowIndex, int cellIndex) {
        Cell cell = row.createCell(2, CellType.STRING);
        CellRangeAddress region = new CellRangeAddress(rowIndex, rowIndex, 2,3);
        sheet.addMergedRegion(region);
        cell.setCellValue(tovar.getText());
        cell.setCellStyle(headerStyle);
    }
    private void  headerSanaCell(int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue("Sana");
        cell.setCellStyle(headerStyle);
    }
    private void headerAdadCell(int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue("Adad");
        cell.setCellStyle(headerStyle);
    }
    private void headerNarhCell(int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue("Narh");
        cell.setCellStyle(headerStyle);
    }
    private void headerJamiCell(int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue("Jami");
        cell.setCellStyle(headerStyle);
    }
    private void dataSanaCell(HisobKitob h, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        if (h.getHisob2().equals(hisob.getId())) {
            cell.setCellStyle(kirimDateStyle);
        } else {
            cell.setCellStyle(chiqimDateStyle);
        }
        cell.setCellValue(h.getDateTime());
    }
    private void dataDonaCell(HisobKitob h, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.NUMERIC);
        if (h.getHisob2().equals(hisob.getId())) {
            cell.setCellStyle(kirimCenterStyle);
        } else {
            cell.setCellStyle(chiqimCenterStyle);
        }
        cell.setCellValue(h.getDona());
    }
    private void dataNarhCell(HisobKitob h, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        if (h.getHisob2().equals(hisob.getId())) {
            cell.setCellStyle(kirimCenterStyle);
        } else {
            cell.setCellStyle(chiqimCenterStyle);
        }
        cell.setCellValue(h.getNarh() / h.getDona());
    }
    private void dataJamiCell(HisobKitob h, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.NUMERIC);
        if (h.getHisob2().equals(hisob.getId())) {
            cell.setCellStyle(kirimCenterStyle);
        } else {
            cell.setCellStyle(chiqimCenterStyle);
        }
        cell.setCellValue(h.getNarh());
    }
}
