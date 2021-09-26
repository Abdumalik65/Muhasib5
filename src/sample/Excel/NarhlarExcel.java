package sample.Excel;

import javafx.collections.ObservableList;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sample.Data.*;
import sample.Tools.Alerts;
import sample.Tools.GetDbData;
import sample.Tools.MoneyShow;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NarhlarExcel {
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
    int fontSize = 10;

    File file;
    Desktop desktop = Desktop.getDesktop();
    ObservableList<Standart3> s3List;
    ObservableList<BarCode>  barCodeList;
    public NarhlarExcel() {
        ibtido();
    }

    private void ibtido() {
        initWorkBook();
        initFonts();
        initStyles();
    }

    private void initWorkBook() {
        wb = new XSSFWorkbook();
    }

    private void initFonts() {
        headerFont = wb.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerFont.setFontHeightInPoints((short)fontSize);

        kirimFont = wb.createFont();
        kirimFont.setBold(false);
        kirimFont.setColor(IndexedColors.BLACK.getIndex());
        kirimFont.setFontHeightInPoints((short)fontSize);

        chiqimFont = wb.createFont();
        chiqimFont.setBold(true);
        chiqimFont.setColor(IndexedColors.BLACK.getIndex());
        chiqimFont.setFontHeightInPoints((short)14);

        font3 = wb.createFont();
        font3.setBold(false);
        font3.setColor(IndexedColors.BLACK.getIndex());
        font3.setFontHeightInPoints((short)fontSize);

        hLinkFont = wb.createFont();
        hLinkFont.setBold(false);
        hLinkFont.setUnderline(Font.U_SINGLE);
        hLinkFont.setColor(IndexedColors.WHITE.getIndex());
        hLinkFont.setFontHeightInPoints((short)fontSize);

        hLinkFont2 = wb.createFont();
        hLinkFont2.setBold(false);
        hLinkFont2.setUnderline(Font.U_SINGLE);
        hLinkFont2.setColor(IndexedColors.BLUE.getIndex());
        hLinkFont2.setFontHeightInPoints((short)fontSize);

        chiptaFont = wb.createFont();
        chiptaFont.setBold(true);
        chiptaFont.setColor(IndexedColors.BLACK.getIndex());
        chiptaFont.setFontHeightInPoints((short)fontSize);

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

        kirimLeftStyle = wb.createCellStyle();
        setBorderStyle(kirimLeftStyle, BorderStyle.THIN, IndexedColors.WHITE.getIndex(), FillPatternType.SOLID_FOREGROUND,
                kirimFont, VerticalAlignment.CENTER, HorizontalAlignment.LEFT);

        chiqimCenterStyle = wb.createCellStyle();
        setBorderStyle(chiqimCenterStyle, BorderStyle.THIN, IndexedColors.WHITE.getIndex(), FillPatternType.SOLID_FOREGROUND,
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

    public void narh(ObservableList<Tovar>  tovarList) {
        if (tovarList.size()>0) {
            sheet = wb.createSheet("Narhlar");
            rowIndex = 0;
            row = sheet.createRow(rowIndex);

            cellIndex = 0;
            header1(rowIndex, cellIndex, sheet, false);

            for (Tovar t : tovarList) {
                rowIndex++;
                row = sheet.createRow(rowIndex);

                cellIndex = 0;
                border(t, rowIndex, cellIndex);

            }

            for (int i = 0; i < 2; i++) {
                sheet.autoSizeColumn(i);
            }

            rowIndex++;
            cellIndex = 0;


            OutputStream fileOut = null;
            String fileName = "narhlar.xlsx";
            try {
                fileOut = new FileOutputStream(fileName);
                wb.write(fileOut);
                showFile(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void header1(int rowIndex, int cellIndex, Sheet sheet, Boolean withHyperlink) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        CellRangeAddress region = new CellRangeAddress(rowIndex, rowIndex, 0,1);
        sheet.addMergedRegion(region);
        cell.setCellStyle(headerStyle);
        cell.setCellValue("Narhlar");

        cellIndex = 2;
        Cell cell2 = row.createCell(cellIndex);
        CellRangeAddress region1 = new CellRangeAddress(rowIndex, rowIndex, cellIndex,4);
        sheet.addMergedRegion(region1);
        cell2.setCellStyle(headerStyle);
        cell2.setCellValue("Hisobot sanasi");

        cellIndex = 5;
        Cell cell3 = row.createCell(cellIndex);
        CellRangeAddress region2 = new CellRangeAddress(rowIndex, rowIndex, cellIndex,9);
        sheet.addMergedRegion(region2);
        cell3.setCellStyle(headerStyle);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        cell3.setCellValue(sdf.format(new Date()));
    }
    private void header2(int rowIndex, int cellIndex) {
        Cell cell1 = row.createCell(cellIndex, CellType.STRING);
        cell1.setCellValue("Sana");
        cell1.setCellStyle(headerStyle);

        cellIndex++;
        Cell cell2 = row.createCell(cellIndex, CellType.STRING);
        cell2.setCellValue("Eslatma");
        cell2.setCellStyle(headerStyle);

        cellIndex++;
        Cell cell3 = row.createCell(cellIndex, CellType.STRING);
        cell3.setCellValue("Valyuta");
        cell3.setCellStyle(headerStyle);

        cellIndex++;
        Cell cell4 = row.createCell(cellIndex, CellType.STRING);
        cell4.setCellValue("Kurs");
        cell4.setCellStyle(headerStyle);

        cellIndex++;
        Cell cell5 = row.createCell(cellIndex, CellType.STRING);
        cell5.setCellValue("Adad");
        cell5.setCellStyle(headerStyle);

        cellIndex++;
        Cell cell6 = row.createCell(cellIndex, CellType.STRING);
        cell6.setCellValue("Narh");
        cell6.setCellStyle(headerStyle);

        cellIndex++;
        Cell cell7 = row.createCell(cellIndex, CellType.STRING);
        cell7.setCellValue("Summa");
        cell7.setCellStyle(headerStyle);

        cellIndex++;
        Cell cell8 = row.createCell(cellIndex, CellType.STRING);
        cell8.setCellValue("Parfumeriya");
        cell8.setCellStyle(headerStyle);

        cellIndex++;
        Cell cell9 = row.createCell(cellIndex, CellType.STRING);
        cell9.setCellValue("Mayda-chuyda");
        cell9.setCellStyle(headerStyle);

        cellIndex++;
        Cell cell10 = row.createCell(cellIndex, CellType.STRING);
        cell10.setCellValue("Naqd foyda");
        cell10.setCellStyle(headerStyle);
    }

    private void border(Tovar tovar, int rowIndex, int cellIndex) {
        Cell cell2 = row.createCell(cellIndex, CellType.STRING);
        cell2.setCellStyle(kirimLeftStyle);
        cell2.setCellValue(tovar.getText());

        cellIndex++;
        Cell cell3 = row.createCell(cellIndex, CellType.NUMERIC);
        cell3.setCellStyle(kirimCenterStyle);
        cell3.setCellValue(tovar.getNds());
    }

    private void footer(int rowIndex, double jamiSavdo) {
        row = sheet.createRow(rowIndex);
        cellIndex = 3;
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellStyle(chiqimCenterStyle);
        cell.setCellValue("Jami foyda");
        CellRangeAddress region1 = new CellRangeAddress(rowIndex, rowIndex, 3,5);
        sheet.addMergedRegion(region1);

        cellIndex = 6;
        Cell cell1 = row.createCell(cellIndex, CellType.FORMULA);
        cell1.setCellStyle(chiqimCenterStyle);
        String strFormula = "SUM(G3:G" + rowIndex + ")";
        cell1.setCellFormula(strFormula);

        cellIndex++;
        Cell cell2 = row.createCell(cellIndex, CellType.FORMULA);
        cell2.setCellStyle(chiqimCenterStyle);
        strFormula = "SUM(H3:H" + rowIndex + ")";
        cell2.setCellFormula(strFormula);

        cellIndex++;
        Cell cell3 = row.createCell(cellIndex, CellType.FORMULA);
        cell3.setCellStyle(chiqimCenterStyle);
        strFormula = "SUM(I3:I" + rowIndex + ")";
        cell3.setCellFormula(strFormula);

        cellIndex++;
        Cell cell4 = row.createCell(cellIndex, CellType.FORMULA);
        cell4.setCellStyle(chiqimCenterStyle);
        strFormula = "SUM(J3:J" + rowIndex + ")";
        cell4.setCellFormula(strFormula);

        rowIndex++;
        row = sheet.createRow(rowIndex);
        cellIndex = 3;
        Cell cell5 = row.createCell(cellIndex, CellType.STRING);
        cell5.setCellStyle(chiqimCenterStyle);
        cell5.setCellValue("Jami savdo");
        CellRangeAddress region2 = new CellRangeAddress(rowIndex, rowIndex, 3,5);
        sheet.addMergedRegion(region2);
        CellRangeAddress region3 = new CellRangeAddress(rowIndex, rowIndex, 6,9);
        sheet.addMergedRegion(region3);
        cellIndex = 6;
        Cell cell6 = row.createCell(cellIndex, CellType.STRING);
        cell6.setCellStyle(chiqimCenterStyle);
        cell6.setCellValue(new MoneyShow().format(jamiSavdo));
    }

    private double doubleRound(double d) {
        double d1 = d * 100;
        double d2 = Math.round(d1);
        double d3 = d2/100;
        return d3;
    }

    private int qaysiBolim(HisobKitob hk) {
        int bolim = 0;
        BarCode barCode = GetDbData.getBarCode(hk.getBarCode());
        if (!hk.getBarCode().isEmpty()) {
            if (barCode != null) {
                Integer tovarId = barCode.getTovar();
                for (Standart3 s3 : s3List) {
                    if (s3.getId3().equals(tovarId)) {
                        bolim = s3.getId2();
                        break;
                    }
                }
            }
        }
        return bolim;
    }
}
