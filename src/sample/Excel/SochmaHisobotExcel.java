package sample.Excel;

import javafx.collections.ObservableList;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sample.Data.Hisob;
import sample.Data.HisobKitob;
import sample.Data.Valuta;
import sample.Tools.GetDbData;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class SochmaHisobotExcel {
    CellStyle kirimCenterStyle = null;
    CellStyle chiqimNumericCenterStyle = null;
    CellStyle kirimNumericCenterStyle = null;
    CellStyle chiqimCenterStyle = null;
    CellStyle kirimLeftStyle = null;
    CellStyle chiqimLeftStyle = null;
    CellStyle headerStyle = null;
    CellStyle headerNumericStyle = null;
    CellStyle headerLeftStyle = null;
    CellStyle hLinkStyle = null;
    CellStyle hLinkStyle2 = null;
    CellStyle wrapCellStyle = null;
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
    String printerim;
    Desktop desktop = Desktop.getDesktop();

    public SochmaHisobotExcel() {
        ibtido();
    }

    public SochmaHisobotExcel(String printerim) {
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
        CreationHelper creationHelper = wb.getCreationHelper();
        headerStyle = wb.createCellStyle();
        setBorderStyle(headerStyle, BorderStyle.MEDIUM, IndexedColors.GREY_40_PERCENT.getIndex(),
                FillPatternType.SOLID_FOREGROUND, headerFont, VerticalAlignment.CENTER, HorizontalAlignment.CENTER);

        headerLeftStyle = wb.createCellStyle();
        setBorderStyle(headerLeftStyle, BorderStyle.MEDIUM, IndexedColors.GREY_40_PERCENT.getIndex(),
                FillPatternType.SOLID_FOREGROUND, headerFont, VerticalAlignment.CENTER, HorizontalAlignment.LEFT);

        headerNumericStyle = wb.createCellStyle();
        setBorderStyle(headerNumericStyle, BorderStyle.MEDIUM, IndexedColors.GREY_40_PERCENT.getIndex(),
                FillPatternType.SOLID_FOREGROUND, headerFont, VerticalAlignment.CENTER, HorizontalAlignment.CENTER);
        headerNumericStyle.setDataFormat(creationHelper.createDataFormat().getFormat("#,##0.00"));

        kirimCenterStyle = wb.createCellStyle();
        setBorderStyle(kirimCenterStyle, BorderStyle.THIN, IndexedColors.WHITE.getIndex(),
                FillPatternType.SOLID_FOREGROUND, kirimFont, VerticalAlignment.CENTER, HorizontalAlignment.CENTER);

        kirimNumericCenterStyle = wb.createCellStyle();
        setBorderStyle(kirimNumericCenterStyle, BorderStyle.THIN, IndexedColors.WHITE.getIndex(),
                FillPatternType.SOLID_FOREGROUND, kirimFont, VerticalAlignment.CENTER, HorizontalAlignment.CENTER);
        kirimNumericCenterStyle.setDataFormat(creationHelper.createDataFormat().getFormat("#,##0.00"));

        kirimLeftStyle = wb.createCellStyle();
        setBorderStyle(kirimLeftStyle, BorderStyle.THIN, IndexedColors.WHITE.getIndex(), FillPatternType.SOLID_FOREGROUND,
                kirimFont, VerticalAlignment.CENTER, HorizontalAlignment.LEFT);

        chiqimCenterStyle = wb.createCellStyle();
        setBorderStyle(chiqimCenterStyle, BorderStyle.THIN, IndexedColors.BRIGHT_GREEN.getIndex(), FillPatternType.SOLID_FOREGROUND,
                chiqimFont, VerticalAlignment.CENTER, HorizontalAlignment.CENTER);

        chiqimNumericCenterStyle = wb.createCellStyle();
        setBorderStyle(chiqimNumericCenterStyle, BorderStyle.THIN, IndexedColors.BRIGHT_GREEN.getIndex(), FillPatternType.SOLID_FOREGROUND,
                chiqimFont, VerticalAlignment.CENTER, HorizontalAlignment.CENTER);
        chiqimNumericCenterStyle.setDataFormat(creationHelper.createDataFormat().getFormat("#,##0.00"));

        chiqimLeftStyle = wb.createCellStyle();
        setBorderStyle(chiqimLeftStyle, BorderStyle.THIN, IndexedColors.BRIGHT_GREEN.getIndex(), FillPatternType.SOLID_FOREGROUND,
                chiqimFont, VerticalAlignment.CENTER, HorizontalAlignment.LEFT);

        dataNumericCenterStyle = wb.createCellStyle();
        setBorderStyle(dataNumericCenterStyle, BorderStyle.THIN, IndexedColors.WHITE.getIndex(), FillPatternType.SOLID_FOREGROUND,
                kirimFont, VerticalAlignment.CENTER, HorizontalAlignment.CENTER);
        dataNumericCenterStyle.setDataFormat(creationHelper.createDataFormat().getFormat("#,##0.00"));

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

    private double doubleRound(double d) {
        double d1 = d * 100;
        double d2 = Math.round(d1);
        double d3 = d2/100;
        return d3;
    }

    public void hisob(Integer hisobId, ObservableList<HisobKitob> hk) {
        Hisob hisob = GetDbData.getHisob(hisobId);
        sheet = wb.createSheet(hisob.getId().toString());
        rowIndex = 0;
        row = sheet.createRow(rowIndex);

        cellIndex = 0;
        headerHisobTextCell(hisob, rowIndex, cellIndex, sheet, false);

        cellIndex = 2;
        headerSanaCell(rowIndex, cellIndex, sheet);

        cellIndex = 7;
        headerBalanceCell(rowIndex, cellIndex, sheet, hk);

        rowIndex++;
        row = sheet.createRow(rowIndex);

        cellIndex = 0;
        header2SanaCell(rowIndex, cellIndex);

        cellIndex++;
        header2EslatmaCell(rowIndex, cellIndex);

        cellIndex++;
        header2ValutaCell(rowIndex, cellIndex);

        cellIndex++;
        header2KursCell(rowIndex, cellIndex);

        cellIndex++;
        header2AdadCell(rowIndex, cellIndex);

        cellIndex++;
        header2NarhCell(rowIndex, cellIndex);

        cellIndex++;
        header2SummaCell(rowIndex, cellIndex);

        cellIndex++;
        header2BalanceCell(rowIndex, cellIndex);

        for (HisobKitob h: hk) {
            rowIndex++;
            row = sheet.createRow(rowIndex);

            cellIndex = 0;
            dataSanaCell(hisobId, h, rowIndex, cellIndex);

            cellIndex++;
            dataIzohCell(hisobId, h, rowIndex, cellIndex);

            cellIndex++;
            dataValutaCell(hisobId, h, rowIndex, cellIndex);

            cellIndex++;
            dataKursCell(hisobId,h, rowIndex, cellIndex);

            cellIndex++;
            dataDonaCell(hisobId, h, rowIndex, cellIndex);

            cellIndex++;
            dataNarhCell(hisobId, h, rowIndex, cellIndex);

            cellIndex++;
            dataSummaColCell(hisobId, h, rowIndex, cellIndex);

            cellIndex++;
            dataBalanceCell(hisobId, h, rowIndex, cellIndex);
        }
        for (int i = 0; i<9 ; i++) {
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

    private void balanceSetter(Integer hisobId, ObservableList<HisobKitob> hisobKitobList) {
        Double balance = 0d;
        for (HisobKitob hisobKitob: hisobKitobList) {
            balance += hisobKitob.getSummaColumn();
            hisobKitob.setBalans(balance);
        }
    }

    private void headerHisobTextCell(Hisob hisob, int rowIndex, int cellIndex, Sheet sheet, Boolean withHyperlink) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        CellRangeAddress region = new CellRangeAddress(rowIndex, rowIndex, 0,1);
        sheet.addMergedRegion(region);
        if (!withHyperlink) {
            cell.setCellStyle(headerStyle);
        } else {
            cell.setCellStyle(hLinkStyle2);
        }
        cell.setCellValue(hisob.getText());
        CreationHelper createHelper = wb.getCreationHelper();
        Hyperlink hyperlink = createHelper.createHyperlink(HyperlinkType.DOCUMENT);
        String adress ="B" + (hisob.getId()+1);
        hyperlink.setAddress("Hisoblar!" + adress);
        cell.setHyperlink(hyperlink);
    }
    private void  headerSanaCell(int rowIndex, int cellIndex, Sheet sheet) {
        Cell cell = row.createCell(cellIndex);
        CellRangeAddress region2 = new CellRangeAddress(rowIndex, rowIndex, cellIndex,6);
        sheet.addMergedRegion(region2);
        cell.setCellStyle(headerStyle);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        cell.setCellValue(sdf.format(new Date()));
    }
    private void  headerBalanceCell(int rowIndex, int cellIndex, Sheet sheet, ObservableList<HisobKitob> hk) {
        Cell cell = row.createCell(cellIndex, CellType.NUMERIC);
        cell.setCellStyle(headerNumericStyle);
        String formula = "SUM(G:G)";
        Double balance = 0d;
        if (hk.size()>0) {
            Integer oxirgi = hk.size()-1;
            balance = hk.get(oxirgi).getBalans();
        }
        cell.setCellValue(balance);
    }
    private void header2SanaCell(int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellStyle(headerStyle);
        cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue("Sana");
        cell.setCellStyle(headerStyle);
    }
    private void header2EslatmaCell(int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue("Eslatma");
        cell.setCellStyle(headerStyle);
    }
    private void header2ValutaCell(int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue("Valyuta");
        cell.setCellStyle(headerStyle);
    }
    private void header2KursCell(int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue("Kurs");
        cell.setCellStyle(headerStyle);
    }
    private void header2AdadCell(int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue("Adad");
        cell.setCellStyle(headerStyle);
    }
    private void header2NarhCell(int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue("Narh");
        cell.setCellStyle(headerStyle);
    }
    private void header2SummaCell(int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue("Summa");
        cell.setCellStyle(headerStyle);
    }
    private void header2BalanceCell(int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue("Balans");
        cell.setCellStyle(headerStyle);
    }
    private void dataSanaCell(int hisobId, HisobKitob hk, int rowIndex, int cellIndex) {
        if (hk==null) {
            System.out.println("Null");
        }
        CellStyle rowStyle = hk.getHisob1().equals(hisobId) ? chiqimCenterStyle : kirimCenterStyle;
        cell = row.createCell(cellIndex, CellType.STRING);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        cell.setCellValue(sdf.format(hk.getDateTime()));
        cell.setCellStyle(rowStyle);
    }
    private void dataIzohCell(int hisobId, HisobKitob h, int rowIndex, int cellIndex) {
        CellStyle cellStyle = h.getHisob1().equals(hisobId) ? chiqimLeftStyle : kirimLeftStyle;
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellStyle(cellStyle);
        cell.setCellValue(h.getIzoh());
    }
    private void dataValutaCell(int hisobId, HisobKitob h, int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        if (h.getId() == 1) {
            cell.setCellStyle(chiqimNumericCenterStyle);
        } else {
            cell.setCellStyle(kirimNumericCenterStyle);
        }
        Valuta valuta = GetDbData.getValuta(h.getValuta());
        if (valuta != null) {
            cell.setCellValue(valuta.getValuta());
        }
    }
    private void dataKursCell(int hisobId, HisobKitob h, int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.NUMERIC);
        if (h.getId() == 1) {
            cell.setCellStyle(chiqimNumericCenterStyle);
        } else {
            cell.setCellStyle(kirimNumericCenterStyle);
        }
        cell.setCellValue(h.getKurs());
    }
    private void dataDonaCell(int hisobId, HisobKitob h, int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.NUMERIC);
        if (h.getDona()==0) {
            h.setDona(1d);
        }
        if (h.getId() == 1) {
            cell.setCellStyle(chiqimNumericCenterStyle);
        } else {
            cell.setCellStyle(kirimNumericCenterStyle);
        }
        cell.setCellValue(h.getDona());
    }
    private void dataNarhCell(int hisobId, HisobKitob h, int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.NUMERIC);
        if (h.getHisob1().equals(hisobId)) {
            cell.setCellStyle(chiqimNumericCenterStyle);
        } else {
            cell.setCellStyle(kirimNumericCenterStyle);
        }
        cell.setCellValue(h.getNarh());
    }
    private void dataSummaColCell(int hisobId, HisobKitob h, int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.NUMERIC);
        Double summaColumn = 0d;
        if (h.getHisob1().equals(hisobId)) {
            cell.setCellStyle(chiqimNumericCenterStyle);
            summaColumn = h.getTovar()>0 ? -h.getDona()*h.getNarh() : -h.getNarh();
        } else {
            summaColumn = h.getTovar()>0 ? h.getDona()*h.getNarh() : h.getNarh();
            cell.setCellStyle(kirimNumericCenterStyle);
        }
        cell.setCellValue(summaColumn / h.getKurs());
    }
    private void dataBalanceCell(int hisobId, HisobKitob h, int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.NUMERIC);
        if (h.getHisob1().equals(hisobId)) {
            cell.setCellStyle(chiqimNumericCenterStyle);
        } else {
            cell.setCellStyle(kirimNumericCenterStyle);
        }

        cell.setCellValue(h.getBalans());
    }
}
