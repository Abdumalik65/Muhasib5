package sample.Excel;

import javafx.collections.ObservableList;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
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
import java.time.LocalDate;
import java.util.Date;

public class FoydaExcel {
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
    int fontSize = 19;

    File file;
    File directory = new File(System.getProperty("user.dir") + File.separator + "Hisobot");
    String pathString;
    String printerim;
    Desktop desktop = Desktop.getDesktop();

    ObservableList<Standart3> s3List;
    ObservableList<BarCode>  barCodeList;
    public FoydaExcel() {
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
        int i =8;
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
        hLinkFont.setUnderline(org.apache.poi.ss.usermodel.Font.U_SINGLE);
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

    public void hisob(Integer hisobId, ObservableList<HisobKitob> hk, ObservableList<Standart3>  s3List, Double jamiSavdo) {
        if (hk.size()>0) {
            this.s3List = s3List;
            Hisob hisob = GetDbData.getHisob(hisobId);
            sheet = wb.createSheet(hisob.getId().toString());
            rowIndex = 0;
            row = sheet.createRow(rowIndex);

            cellIndex = 0;
            header1(hisob, rowIndex, cellIndex, sheet, false);

            rowIndex++;
            row = sheet.createRow(rowIndex);

            cellIndex = 0;
            header2(rowIndex, cellIndex);

            for (HisobKitob h : hk) {
                rowIndex++;
                row = sheet.createRow(rowIndex);

                cellIndex = 0;
                border(hisobId, h, rowIndex, cellIndex);

            }

            for (int i = 0; i < 13; i++) {
                sheet.autoSizeColumn(i);
            }

            rowIndex++;
            cellIndex = 0;
            footer(rowIndex, jamiSavdo);


            OutputStream fileOut = null;
            String fileName= pathString + hisob.getText().trim() + "_" + LocalDate.now() + ".xlsx";
            try {
                fileOut = new FileOutputStream(fileName);
                wb.write(fileOut);
                showFile(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Alerts.AlertString("Bu kun savdo bo`lmadi");
        }
    }

    public void hisob2(Integer hisobId, ObservableList<HisobKitob> hk, ObservableList<Standart3>  s3List, ObservableList<Standart5> jamiSavdo) {
        if (hk.size()>0) {
            this.s3List = s3List;
            Hisob hisob = GetDbData.getHisob(hisobId);
            sheet = wb.createSheet(hisob.getId().toString());
            rowIndex = 0;
            row = sheet.createRow(rowIndex);

            cellIndex = 0;
            header1(hisob, rowIndex, cellIndex, sheet, false);

            rowIndex++;
            row = sheet.createRow(rowIndex);

            cellIndex = 0;
            header2(rowIndex, cellIndex);

            for (HisobKitob h : hk) {
                rowIndex++;
                row = sheet.createRow(rowIndex);

                cellIndex = 0;
                border(hisobId, h, rowIndex, cellIndex);

            }

            for (int i = 0; i < 13; i++) {
                sheet.autoSizeColumn(i);
            }

            rowIndex++;
            cellIndex = 0;
            footer2(rowIndex, jamiSavdo);


            OutputStream fileOut = null;
            String fileName= pathString + hisob.getText().trim() + "_" + LocalDate.now() + ".xlsx";
            try {
                fileOut = new FileOutputStream(fileName);
                wb.write(fileOut);
                showFile(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Alerts.AlertString("Bu kun savdo bo`lmadi");
        }
    }

    private void header1(Hisob hisob, int rowIndex, int cellIndex, Sheet sheet, Boolean withHyperlink) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        CellRangeAddress region = new CellRangeAddress(rowIndex, rowIndex, 0,1);
        sheet.addMergedRegion(region);
        cell.setCellStyle(headerStyle);
        cell.setCellValue(hisob.getText());

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

    private void border(int hisobId, HisobKitob hk, int rowIndex, int cellIndex) {
        Cell cell1 = row.createCell(cellIndex, CellType.STRING);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        cell1.setCellValue(sdf.format(hk.getDateTime()));
        cell1.setCellStyle(kirimLeftStyle);

        cellIndex++;
        Cell cell2 = row.createCell(cellIndex, CellType.STRING);
        cell2.setCellStyle(kirimLeftStyle);
        cell2.setCellValue(hk.getIzoh());

        cellIndex++;
        Cell cell3 = row.createCell(cellIndex, CellType.NUMERIC);
        cell3.setCellStyle(kirimCenterStyle);
        Valuta valuta = GetDbData.getValuta(hk.getValuta());
        if (valuta != null) {
            cell3.setCellValue(valuta.getValuta());
        }

        cellIndex++;
        Cell cell4 = row.createCell(cellIndex, CellType.NUMERIC);
        cell4.setCellStyle(kirimCenterStyle);
        cell4.setCellValue(hk.getKurs());

        cellIndex++;
        Cell cell5 = row.createCell(cellIndex, CellType.NUMERIC);
        cell5.setCellStyle(kirimCenterStyle);
        cell5.setCellValue(hk.getDona());

        cellIndex++;
        Cell cell6 = row.createCell(cellIndex, CellType.NUMERIC);
        cell6.setCellStyle(kirimCenterStyle);
        cell6.setCellValue(doubleRound(hk.getNarh()));

        cellIndex++;
        Cell cell7 = row.createCell(cellIndex, CellType.NUMERIC);
        cell7.setCellStyle(kirimCenterStyle);
        cell7.setCellValue(doubleRound(hk.getSummaCol()));

        cellIndex++;
        Cell cell8 = row.createCell(cellIndex, CellType.NUMERIC);
        cell8.setCellStyle(kirimCenterStyle);
        if (qaysiBolim(hk)==1) {
            cell8.setCellValue(doubleRound(hk.getSummaCol()));
        }

        cellIndex++;
        Cell cell9 = row.createCell(cellIndex, CellType.NUMERIC);
        cell9.setCellStyle(kirimCenterStyle);
        if (qaysiBolim(hk)==2) {
            cell9.setCellValue(doubleRound(hk.getSummaCol()));
        }

        cellIndex++;
        Cell cell10 = row.createCell(cellIndex, CellType.NUMERIC);
        cell10.setCellStyle(kirimCenterStyle);
        if (qaysiBolim(hk)==0) {
            cell10.setCellValue(doubleRound(hk.getSummaCol()));
        }
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

    private void footer2(int rowIndex, ObservableList<Standart5> jamiSavdo) {
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

        for (Standart5 s5: jamiSavdo) {
            rowIndex++;
            row = sheet.createRow(rowIndex);
            cellIndex = 3;
            Cell cell5 = row.createCell(cellIndex, CellType.STRING);
            cell5.setCellStyle(chiqimCenterStyle);
            cell5.setCellValue(s5.getText());
            CellRangeAddress region2 = new CellRangeAddress(rowIndex, rowIndex, 3, 5);
            sheet.addMergedRegion(region2);
            CellRangeAddress region3 = new CellRangeAddress(rowIndex, rowIndex, 6, 9);
            sheet.addMergedRegion(region3);
            cellIndex = 6;
            Cell cell6 = row.createCell(cellIndex, CellType.STRING);
            cell6.setCellStyle(chiqimCenterStyle);
            cell6.setCellValue(new MoneyShow().format(s5.getDona()));
        }
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
