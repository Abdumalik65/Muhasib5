package sample.Excel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sample.Data.*;
import sample.Model.HisobKitobModels;
import sample.Tools.GetDbData;

import java.awt.*;
import java.io.*;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

public class XaridChiptasiExcel {
    CreationHelper creationHelper;
    CellStyle kirimLeftStyle = null;
    CellStyle kirimCenterStyle = null;
    CellStyle kirimNotZeroStyle = null;
    CellStyle headerStyle = null;
    CellStyle headerLeftStyle = null;
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

    ObservableList<HisobKitob> jamiRoyxat;
    ObservableList<HisobKitob> savdoRoyxati = FXCollections.observableArrayList();
    ObservableList<HisobKitob> tolovRoyxati = FXCollections.observableArrayList();
    QaydnomaData qaydnomaData;
    Double balance;
    Connection connection;
    User user;
    Valuta valuta = null;
    Double kursDouble = 0d;

    public XaridChiptasiExcel() {
        ibtido();
    }

    public XaridChiptasiExcel(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
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
        creationHelper = wb.getCreationHelper();
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

        kirimNotZeroStyle = wb.createCellStyle();
        setBorderStyle(kirimNotZeroStyle, BorderStyle.THIN, IndexedColors.WHITE.getIndex(),
                FillPatternType.SOLID_FOREGROUND, kirimFont, VerticalAlignment.CENTER, HorizontalAlignment.CENTER);
        kirimNotZeroStyle.setDataFormat(creationHelper.createDataFormat().getFormat("#,##0"));

        kirimLeftStyle = wb.createCellStyle();
        setBorderStyle(kirimLeftStyle, BorderStyle.THIN, IndexedColors.WHITE.getIndex(), FillPatternType.SOLID_FOREGROUND,
                kirimFont, VerticalAlignment.CENTER, HorizontalAlignment.LEFT);

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

    public void savdoChiptasi(QaydnomaData qaydnomaData, User user) {
        this.qaydnomaData = qaydnomaData;
        this.user = user;
        Double jamiMablag = 0.0;
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        ObservableList<HisobKitob> jamiRoyxat = hisobKitobModels.getAnyData(connection, "qaydId = " + qaydnomaData.getId(),"");
        amallarniAjrat(jamiRoyxat);
        sheet = wb.createSheet("Chipta");

        rowIndex = 0;
        row = sheet.createRow(rowIndex);
        cellIndex = 0;
        headerShirkatCell(rowIndex, cellIndex, sheet, qaydnomaData, user);

        rowIndex++;
        header1Row(rowIndex, sheet, qaydnomaData, user);

        rowIndex++;
        sarlavhaRow(rowIndex);
        chopEt(savdoRoyxati);
        royxatniChopEt(tolovRoyxati);

        rowIndex += 2;
        row = sheet.createRow(rowIndex);
        cellIndex = 0;
        dataTextCell("Balans ", rowIndex, cellIndex);
        Cell cell = row.createCell(1, CellType.NUMERIC);
        CellRangeAddress region = new CellRangeAddress(rowIndex, rowIndex, 1,4);
        sheet.addMergedRegion(region);
        setBordersToMergedCells(sheet, region, BorderStyle.THIN);
        if (balance%1 != 0.00) {
            cell.setCellStyle(kirimCenterStyle);
        } else {
            cell.setCellStyle(kirimNotZeroStyle);
        }
        cell.setCellValue(balance);

        sheet.setColumnWidth(0, 40*256);
        sheet.setColumnWidth(1, 10*256);
        sheet.setColumnWidth(2, 10*256);
        sheet.setColumnWidth(3, 10*256);
        sheet.setColumnWidth(4, 10*256);

        OutputStream fileOut = null;
        String fileName= pathString + "Chipta_" + LocalDate.now() + ".xlsx";
        try {
            fileOut = new FileOutputStream(fileName);
            wb.write(fileOut);
            showFile(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void royxatniChopEt(ObservableList<HisobKitob> hisobKitobObservableList) {
        if (hisobKitobObservableList.size() > 0) {
            rowIndex++;
            for (HisobKitob hisobKitob: hisobKitobObservableList) {
                rowIndex++;
                row = sheet.createRow(rowIndex);

                cellIndex = 0;
                Valuta valuta = GetDbData.getValuta(hisobKitob.getValuta());
                Standart amal = GetDbData.getAmal(hisobKitob.getAmal());
                String amalString = amal.getText().trim() +": ";
                String izoh = amalString + valuta.getValuta().trim().toUpperCase();
                dataTextCell(izoh, rowIndex, cellIndex);

                cellIndex++;
                dataNumberCell(hisobKitob.getKurs(), rowIndex, cellIndex);

                cellIndex++;
                dataNumberCell(hisobKitob.getNarh(), rowIndex, cellIndex);

                cellIndex++;
                dataNumberCell(ishora(hisobKitob) * hisobKitob.getNarh() * kursDouble / hisobKitob.getKurs(), rowIndex, cellIndex);

                cellIndex++;
                balance += balansYoz(hisobKitob);
                hisobKitob.setBalans(balance);
                dataNumberCell(hisobKitob.getBalans(), rowIndex, cellIndex);
            }

        }
    }

    private void headerShirkatCell(int rowIndex, int cellIndex, Sheet sheet, QaydnomaData qaydnomaData, User user) {
        CellStyle headerStyle = wb.createCellStyle();
        setBorderStyle(headerStyle, BorderStyle.THIN, IndexedColors.WHITE.getIndex(),
                FillPatternType.SOLID_FOREGROUND, chiptaFont, VerticalAlignment.CENTER, HorizontalAlignment.CENTER);
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellStyle(headerStyle);
        cell.setCellValue(GetDbData.getHisob(user.getTovarHisobi()).getText());
        Cell cell2 = row.createCell(1, CellType.STRING);
        cell2.setCellValue(qaydnomaData.getKirimNomi());
        cell2.setCellStyle(headerStyle);
        CellRangeAddress region1 = new CellRangeAddress(rowIndex, rowIndex, 1,4);
        sheet.addMergedRegion(region1);
        setBordersToMergedCells(sheet, region1, BorderStyle.THIN);
    }
    private void dataTextCell(String string, int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue(string);
        cell.setCellStyle(wrapCellStyle);
    }
    private void header1Row(int rowIndex, Sheet sheet, QaydnomaData qaydnomaData, User user) {
        SimpleDateFormat sanaFormat = new SimpleDateFormat("dd.MM.yyyy  HH:mm:ss");
        row = sheet.createRow(rowIndex);
        CellStyle leftCellStyle = wb.createCellStyle();
        setBorderStyle(leftCellStyle, BorderStyle.THIN, IndexedColors.WHITE.getIndex(),
                FillPatternType.SOLID_FOREGROUND, chiptaFont, VerticalAlignment.CENTER, HorizontalAlignment.CENTER);
        Cell cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("Telefon: " +user.getPhone()+ " Sotuvchi: " + user.getIsm().trim() + " Sana: " + sanaFormat.format(qaydnomaData.getSana()).trim());
        cell.setCellStyle(leftCellStyle);

        CellRangeAddress region1 = new CellRangeAddress(rowIndex, rowIndex, 0,3);
        sheet.addMergedRegion(region1);
        setBordersToMergedCells(sheet, region1, BorderStyle.THIN);
        Cell cell1 = row.createCell(4, CellType.STRING);
        cell1.setCellValue("№: " + qaydnomaData.getHujjat());
        cell1.setCellStyle(leftCellStyle);
    }

    private void sarlavhaRow(int rowIndex) {
        row = sheet.createRow(rowIndex);
        CellStyle cellStyle = wb.createCellStyle();
        setBorderStyle(cellStyle, BorderStyle.THIN, IndexedColors.WHITE.getIndex(),
                FillPatternType.SOLID_FOREGROUND, chiptaFont, VerticalAlignment.CENTER, HorizontalAlignment.CENTER);
        Cell cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("Tovar");
        cell.setCellStyle(cellStyle);
        Cell cell1 = row.createCell(1, CellType.STRING);
        cell1.setCellValue("Soni");
        cell1.setCellStyle(cellStyle);
        Cell cell2 = row.createCell(2, CellType.STRING);
        cell2.setCellValue("Narh");
        cell2.setCellStyle(cellStyle);
        Cell cell3 = row.createCell(3, CellType.STRING);
        cell3.setCellValue("Jami");
        cell3.setCellStyle(cellStyle);
        Cell cell4 = row.createCell(4, CellType.STRING);
        cell4.setCellValue("Balans");
        cell4.setCellStyle(cellStyle);
    }
    private void dataNumberCell(Double aDouble, int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue(aDouble);
        if (aDouble%1 != 0.00) {
            cell.setCellStyle(kirimCenterStyle);
        } else {
            cell.setCellStyle(kirimNotZeroStyle);
        }
    }
    protected void setBordersToMergedCells(Sheet sheet, CellRangeAddress rangeAddress, BorderStyle borderStyle) {
        RegionUtil.setBorderTop(borderStyle, rangeAddress, sheet);
        RegionUtil.setBorderLeft(borderStyle, rangeAddress, sheet);
        RegionUtil.setBorderRight(borderStyle, rangeAddress, sheet);
        RegionUtil.setBorderBottom(borderStyle, rangeAddress, sheet);
    }
    private void amallarniAjrat(ObservableList<HisobKitob> observableList) {
        Double balance = 0d;
        if (observableList.size()>0) {
            for (HisobKitob hisobKitob: observableList) {
                switch (hisobKitob.getAmal()) {
                    case 2:
                        savdoRoyxati.add(hisobKitob);
                        break;
                    case 4:
                        if (hisobKitob.getNarh() + hisobKitob.getDona() !=0d) {
                            if (hisobKitob.getHisob2().equals(qaydnomaData.getKirimId())) {
                                valuta = GetDbData.getValuta(hisobKitob.getValuta());
                                kursDouble = hisobKitob.getKurs();
                                savdoRoyxati.add(hisobKitob);
                            }
                        }
                        break;
                    case 7: //tolov
                        tolovRoyxati.add(hisobKitob);
                        break;
                    case 8: //qaytim
                        tolovRoyxati.add(hisobKitob);
                        break;
                    case 13: // chegirma
                        tolovRoyxati.add(hisobKitob);
                        break;
                    case 15: //bank tolovi
                        tolovRoyxati.add(hisobKitob);
                        break;
                    case 18: //qoshimcha daromad
                        tolovRoyxati.add(hisobKitob);
                        break;
                }
            }
        }
    }
    private Double balansYoz(HisobKitob hisobKitob) {
        Double balance = 0d;
        if (hisobKitob !=null) {
            switch (hisobKitob.getAmal()) {
                case 2:
                    balance -= hisobKitob.getSummaCol() * kursDouble;
                    break;
                case 4:
                    if (hisobKitob.getNarh() + hisobKitob.getDona() !=0d) {
                        if (hisobKitob.getHisob2().equals(qaydnomaData.getKirimId())) {
                            balance += hisobKitob.getSummaCol() * kursDouble;
                        }
                    }
                    break;
                case 7: //tolov
                    balance -= hisobKitob.getSummaCol() * kursDouble;
                    break;
                case 8: //qaytim
                    balance += hisobKitob.getSummaCol() * kursDouble;
                    break;
                case 13: // chegirma
                    balance -= hisobKitob.getSummaCol() * kursDouble;
                    break;
                case 15: //bank tolovi
                    balance -= hisobKitob.getSummaCol() * kursDouble;
                    break;
                case 18: //qoshimcha daromad
                    balance += hisobKitob.getSummaCol() * kursDouble;
                    break;
            }
        }
        return balance;
    }
    private Double ishora(HisobKitob hisobKitob) {
        Double ishora = -1d;
        if (hisobKitob !=null) {
            switch (hisobKitob.getAmal()) {
                case 2:
                    ishora = -1d;
                    break;
                case 4:
                    if (hisobKitob.getNarh() + hisobKitob.getDona() !=0d) {
                        if (hisobKitob.getHisob2().equals(qaydnomaData.getKirimId())) {
                            ishora = 1d;
                        }
                    }
                    break;
                case 7: //tolov
                    ishora = -1d;
                    break;
                case 8: //qaytim
                    ishora = 1d;
                    break;
                case 13: // chegirma
                    ishora = -1d;
                    break;
                case 15: //bank tolovi
                    ishora = -1d;
                    break;
                case 18: //qoshimcha daromad
                    ishora = 1d;
                    break;
            }
        }
        return ishora;
    }
    private void chopEt(ObservableList<HisobKitob> observableList) {
        balance = 0d;
        Double jami = 0d;
        for (HisobKitob hk: observableList) {
            rowIndex++;
            row = sheet.createRow(rowIndex);

            cellIndex = 0;
            dataTextCell(hk.getIzoh(), rowIndex, cellIndex);

            cellIndex++;
            dataNumberCell(hk.getDona(), rowIndex, cellIndex);

            cellIndex++;
            dataNumberCell(hk.getNarh(), rowIndex, cellIndex);

            cellIndex++;
            dataNumberCell(hk.getDona() * hk.getNarh() , rowIndex, cellIndex);
            jami += hk.getDona() * hk.getNarh();

            cellIndex++;
            balance += balansYoz(hk);
            hk.setBalans(balance);
            dataNumberCell(hk.getBalans(), rowIndex, cellIndex);
        }
    }
}
