package sample.Tools;

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
import sample.Model.QaydnomaModel;

import java.awt.*;
import java.io.*;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class ExportToExcel {
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

    public ExportToExcel() {
        ibtido();
    }

    public ExportToExcel(String printerim) {
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
        headerFont = wb.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerFont.setFontHeightInPoints((short)8);

        kirimFont = wb.createFont();
        kirimFont.setBold(false);
        kirimFont.setColor(IndexedColors.BLACK.getIndex());
        kirimFont.setFontHeightInPoints((short)8);

        chiqimFont = wb.createFont();
        chiqimFont.setBold(false);
        chiqimFont.setColor(IndexedColors.BLACK.getIndex());
        chiqimFont.setFontHeightInPoints((short)8);

        font3 = wb.createFont();
        font3.setBold(false);
        font3.setColor(IndexedColors.BLACK.getIndex());
        font3.setFontHeightInPoints((short)8);

        hLinkFont = wb.createFont();
        hLinkFont.setBold(false);
        hLinkFont.setUnderline(Font.U_SINGLE);
        hLinkFont.setColor(IndexedColors.WHITE.getIndex());
        hLinkFont.setFontHeightInPoints((short)8);

        hLinkFont2 = wb.createFont();
        hLinkFont2.setBold(false);
        hLinkFont2.setUnderline(Font.U_SINGLE);
        hLinkFont2.setColor(IndexedColors.BLUE.getIndex());
        hLinkFont2.setFontHeightInPoints((short)8);

        chiptaFont = wb.createFont();
        chiptaFont.setBold(true);
        chiptaFont.setColor(IndexedColors.BLACK.getIndex());
        chiptaFont.setFontHeightInPoints((short)10);

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

    public void ValutTable(String tableName, ObservableList<Valuta> valutaList) {
        sheet = wb.createSheet(tableName);
        rowIndex = 0;
        row = sheet.createRow(rowIndex);

        cellIndex = 0;
        headerTovarIdCell(rowIndex, cellIndex);

        cellIndex++;
        headerTovarNomiCell(rowIndex, cellIndex);

        for (Valuta t: valutaList) {
            rowIndex++;
            row = sheet.createRow(rowIndex);

            cellIndex = 0;
            dataValutaIdCell(t, rowIndex, cellIndex);

            cellIndex ++;
            dataValutaNomiCell(t, rowIndex, cellIndex);
        }
        sheet.setColumnWidth(0, 8*256);
        sheet.autoSizeColumn(1);

        OutputStream fileOut = null;
        String fileName= pathString + tableName+ ".xlsx";
        try {
            fileOut = new FileOutputStream(fileName);
            wb.write(fileOut);
            showFile(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void headerValutaIdCell(int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue("Id");
        cell.setCellStyle(headerStyle);
    }
    private void headerValutaNomiCell() {
        cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue("Nomi");
        cell.setCellStyle(headerStyle);
    }
    private void dataValutaIdCell(Valuta t, int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.NUMERIC);
        cell.setCellValue(t.getId());
        cell.setCellStyle(kirimCenterStyle);
    }
    private void dataValutaNomiCell(Valuta t, int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue(t.getValuta());
        cell.setCellStyle(kirimLeftStyle);
    }

    public void standartTable(String tableName, ObservableList<Standart> standartList) {
        sheet = wb.createSheet(tableName);
        rowIndex = 0;
        row = sheet.createRow(rowIndex);

        cellIndex = 0;
        headerTovarIdCell(rowIndex, cellIndex);

        cellIndex++;
        headerTovarNomiCell(rowIndex, cellIndex);

        for (Standart t: standartList) {
            rowIndex++;
            row = sheet.createRow(rowIndex);

            cellIndex = 0;
            dataTovarIdCell(t, rowIndex, cellIndex);

            cellIndex ++;
            dataTovarNomiCell(t, rowIndex, cellIndex);
        }
        sheet.setColumnWidth(0, 8*256);
        sheet.autoSizeColumn(1);

        OutputStream fileOut = null;
        String fileName= pathString + tableName+ ".xlsx";
        try {
            fileOut = new FileOutputStream(fileName);
            wb.write(fileOut);
            showFile(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void headerTovarIdCell(int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue("Id");
        cell.setCellStyle(headerStyle);
    }
    private void headerTovarNomiCell(int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue("Nomi");
        cell.setCellStyle(headerStyle);
    }
    private void dataTovarIdCell(Standart t, int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.NUMERIC);
        cell.setCellValue(t.getId());
        cell.setCellStyle(kirimCenterStyle);
    }
    private void dataTovarNomiCell(Standart t, int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue(t.getText());
        cell.setCellStyle(kirimLeftStyle);
    }

    public void hisoblar(ObservableList<Hisob> hisoblar) {
        sheet = wb.createSheet("Hisoblar");
        rowIndex = 0;
        row = sheet.createRow(rowIndex);

        cellIndex = 0;
        headerHisobIdCell();

        cellIndex++;
        headerHisobNomiCell();

        cellIndex++;
        headerHisobBalanceCell();

        for (Hisob h: hisoblar) {
            Double balance = doubleRound(h.getBalans());
            if (balance != 0d) {
                rowIndex++;
                row = sheet.createRow(rowIndex);

                cellIndex = 0;
                dataHisobIdCell(h);

                cellIndex++;
                dataHisobNomiCell(h);

                cellIndex++;
                dataHisobBalanceCell(h);
            }
        }
        sheet.setColumnWidth(0, 8*256);
        sheet.autoSizeColumn(1);

        try (OutputStream fileOut = new FileOutputStream("hisoblar.xlsx")) {
            wb.write(fileOut);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        showFile("hisoblar.xlsx");
    }
    private void headerHisobIdCell() {
        cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue("Id");
        cell.setCellStyle(headerStyle);
    }
    private void headerHisobNomiCell() {
        cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue("Hisob nomi");
        cell.setCellStyle(headerStyle);
    }
    private void headerHisobBalanceCell() {
        cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue("Balans");
        cell.setCellStyle(headerStyle);
    }
    private void dataHisobIdCell(Hisob h) {
        cell = row.createCell(cellIndex, CellType.NUMERIC);
        cell.setCellValue(h.getId());
        cell.setCellStyle(kirimCenterStyle);
    }
    private void dataHisobNomiCell(Hisob h) {
        cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue(h.getText());
        cell.setCellStyle(kirimLeftStyle);
    }
    private void dataHisobNomiCellWithHyperlink(Hisob h) {
        cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue(h.getText());
        CreationHelper createHelper = wb.getCreationHelper();
        Hyperlink hyperlink = createHelper.createHyperlink(HyperlinkType.DOCUMENT);
        hyperlink.setAddress(" "+h.getId() + "!A1");
        cell.setHyperlink(hyperlink);
        cell.setCellStyle(hLinkStyle);
    }
    private void dataHisobBalanceCell(Hisob h) {
        cell = row.createCell(cellIndex, CellType.NUMERIC);
        cell.setCellValue(doubleRound(h.getBalans()));
        cell.setCellStyle(kirimCenterStyle);
    }
    private void dataHisobBalanceCell(Hisob h, Connection connection) {
        cell = row.createCell(cellIndex, CellType.NUMERIC);
        cell.setCellValue(doubleRound(hisobBalans(h.getId(), connection)));
        cell.setCellStyle(kirimCenterStyle);
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
        String fileName= pathString + "hisob"+ hisobId +".xlsx";
        try {
            fileOut = new FileOutputStream(fileName);
            wb.write(fileOut);
            showFile(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void hisobMufassal(Connection connection) {
        HisobModels hisobModels = new HisobModels();
        QaydnomaModel qaydnomaModel = new QaydnomaModel();
        ObservableList<Hisob> hisoblar = hisobModels.get_data(connection);
        ObservableList<QaydnomaData> qList = qaydnomaModel.get_data(connection);
        Comparator<Hisob> comparator = Comparator.comparing(Hisob::getText);
        FXCollections.sort(hisoblar, comparator);

        sheet = wb.createSheet("Hisoblar");
        rowIndex = 0;
        row = sheet.createRow(rowIndex);

        cellIndex = 0;
        headerHisobNomiCell();

        cellIndex++;
        headerHisobBalanceCell();
         for (Hisob h: hisoblar) {
             Double balance = doubleRound(hisobBalans(h.getId(), connection));
             if (balance != 0d) {

                 rowIndex++;
                 row = sheet.createRow(rowIndex);

                 cellIndex = 0;
                 dataHisobNomiCellWithHyperlink(h);

                 cellIndex++;
                 dataHisobBalanceCell(h, connection);

                 hisobToExcel(connection, h, qList, true);
             }
        }
        sheet.setColumnWidth(0, 30*256);
        sheet.autoSizeColumn(1);

        try (OutputStream fileOut = new FileOutputStream("Mufassal.xlsx")) {
            wb.write(fileOut);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        showFile("Mufassal.xlsx");
    }

    private void hisobToExcel(Connection connection, Hisob hisob, ObservableList<QaydnomaData> qList, Boolean withHyperlink) {
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        ObservableList<HisobKitob> hkList = hisobKitobModels.getAnyData(connection, "hisob1 = " + hisob.getId() + " OR hisob2 = " + hisob.getId(), "");
        setDateTime(hkList, qList, hisob);
        Sheet sheet = wb.createSheet(hisob.getId().toString());
        Integer rowIndex = 0;
        row = sheet.createRow(rowIndex);

        Integer cellIndex = 0;
        headerHisobTextCell(hisob, rowIndex, cellIndex, sheet, withHyperlink);

        cellIndex = 2;
        headerHisobotSanasiCell(rowIndex, cellIndex, sheet);

        cellIndex = 5;
        headerSanaCell(rowIndex, cellIndex, sheet);

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

        for (HisobKitob h: hkList) {
            rowIndex++;
            row = sheet.createRow(rowIndex);

            cellIndex = 0;
            dataSanaCell(hisob.getId(), h, rowIndex, cellIndex);

            cellIndex++;
            dataIzohCell(hisob.getId(), h, rowIndex, cellIndex);

            cellIndex++;
            dataValutaCell(hisob.getId(), h, rowIndex, cellIndex);

            cellIndex++;
            dataKursCell(hisob.getId(),h, rowIndex, cellIndex);

            cellIndex++;
            dataDonaCell(hisob.getId(), h, rowIndex, cellIndex);

            cellIndex++;
            dataNarhCell(hisob.getId(), h, rowIndex, cellIndex);

            cellIndex++;
            dataSummaColCell(hisob.getId(), h, rowIndex, cellIndex);

            cellIndex++;
            dataBalanceCell(hisob.getId(), h, rowIndex, cellIndex);
        }
        for (int i = 0; i<9 ; i++) {
            sheet.autoSizeColumn(i);
        }

    }

    private void setDateTime(ObservableList<HisobKitob> hkList, ObservableList<QaydnomaData> qList, Hisob hisob) {
        for (HisobKitob hk: hkList) {
            hk.setDateTime(getQaydDate(hk.getQaydId(), qList));
        }
        Collections.sort(hkList, Comparator.comparing(HisobKitob::getDateTime));
        Double yigindi = .0;
        for (HisobKitob hk: hkList) {
            if (hk.getHisob1().equals(hisob.getId())) {
                hk.setId(1);
            } else {
                hk.setId(2);
            }

            if (hk.getId() == 1) {
                yigindi -= hk.getSummaCol();
            } else {
                yigindi += hk.getSummaCol();
            }
            hk.setBalans(yigindi);
        }
    }

    private Date getQaydDate(Integer qaydId, ObservableList<QaydnomaData> qList) {
        Date qaydDate = null;
        for (QaydnomaData q: qList) {
            if (q.getId().equals(qaydId)) {
                qaydDate = q.getSana();
                break;
            }
        }
        return qaydDate;
    }

    private double hisobBalans(int hisobId, Connection connection) {
        double kirim = 0.0;
        double chiqim = 0.0;
        double balans = 0.0;
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        ObservableList<HisobKitob> kirimObservableList;
        ObservableList<HisobKitob> chiqimObservableList;
        kirimObservableList = hisobKitobModels.getAnyData(connection, "hisob2 = " + hisobId, "");
        for (HisobKitob k: kirimObservableList) {
            double jami = (k.getTovar() == 0 ? 1: k.getDona()) * k.getNarh()/k.getKurs();
            kirim += jami;
        }
        chiqimObservableList = hisobKitobModels.getAnyData(connection, "hisob1 = " + hisobId, "");
        for (HisobKitob ch: chiqimObservableList) {
            double jami = (ch.getTovar() == 0 ? 1: ch.getDona()) * ch.getNarh()/ch.getKurs();
            chiqim +=  jami;
        }
        balans = kirim - chiqim;
        return balans;
    }
    private QaydnomaData getQaydnomaData(ObservableList<QaydnomaData> qList, HisobKitob hk) {
        QaydnomaData qaydnomaData = null;
        for (QaydnomaData q: qList) {
            if (q.getId().equals(hk.getQaydId())) {
                qaydnomaData = q;
                break;
            }
        }
        return qaydnomaData;
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

    private void headerHisobotSanasiCell(int rowIndex, int cellIndex, Sheet sheet) {
        Cell cell = row.createCell(cellIndex);
        CellRangeAddress region1 = new CellRangeAddress(rowIndex, rowIndex, cellIndex,4);
        sheet.addMergedRegion(region1);
        cell.setCellStyle(headerStyle);
        cell.setCellValue("Hisobot sanasi");
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
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellStyle(headerStyle);
        Double balance = 0d;
        if (hk.size()>0) {
            balance = hk.get(hk.size()-1).getBalans();
        }
        cell.setCellValue(new MoneyShow().format(balance));
    }

    private void header2SanaCell(int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellStyle(headerStyle);
        cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue("Sana");
        cell.setCellStyle(headerStyle);
    }

    private void header2HisobNomiCell(int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellStyle(headerStyle);
        cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue("Hisob nomi");
        cell.setCellStyle(headerStyle);
    }
    private void header2AmalCell(int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue("Amal");
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

    private void dataHisobCell(int hisobId, HisobKitob h, int rowIndex, int cellIndex) {
        CellStyle rowStyle = null;
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        Hisob hisob1 = null;
        if (h.getHisob1().equals(hisobId)){
            hisob1 = GetDbData.getHisob(h.getHisob2());
            rowStyle = chiqimLeftStyle;
        } else {
            hisob1 = GetDbData.getHisob(h.getHisob1());
            rowStyle = kirimLeftStyle;
        }
        if (hisob1 != null) {
            cell.setCellValue(hisob1.getText());
        }
        cell.setCellStyle(rowStyle);
    }
    private void dataAmalCell(int hisobId, HisobKitob h, int rowIndex, int cellIndex) {
        CellStyle rowStyle = h.getHisob1().equals(hisobId) ? chiqimLeftStyle : kirimLeftStyle;
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        Standart amal = GetDbData.getAmal(h.getAmal());
        cell.setCellStyle(rowStyle);
        if (amal != null) {
            cell.setCellValue(amal.getText());
        }
    }
    private void dataIzohCell(int hisobId, HisobKitob h, int rowIndex, int cellIndex) {
        CellStyle cellStyle = h.getHisob1().equals(hisobId) ? chiqimLeftStyle : kirimLeftStyle;
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellStyle(cellStyle);
        cell.setCellValue(h.getIzoh());
    }
    private void dataValutaCell(int hisobId, HisobKitob h, int rowIndex, int cellIndex) {
        CellStyle rowStyle = h.getHisob1().equals(hisobId) ? chiqimCenterStyle : kirimCenterStyle;
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellStyle(rowStyle);
        Valuta valuta = GetDbData.getValuta(h.getValuta());
        if (valuta != null) {
            cell.setCellValue(valuta.getValuta());
        }
    }
    private void dataKursCell(int hisobId, HisobKitob h, int rowIndex, int cellIndex) {
        CellStyle rowStyle = h.getHisob1().equals(hisobId) ? chiqimCenterStyle : kirimCenterStyle;
        Cell cell = row.createCell(cellIndex, CellType.NUMERIC);
        cell.setCellStyle(rowStyle);
        cell.setCellValue(doubleRound(h.getKurs()));
    }
    private void dataDonaCell(int hisobId, HisobKitob h, int rowIndex, int cellIndex) {
        CellStyle rowStyle = h.getHisob1().equals(hisobId) ? chiqimCenterStyle : kirimCenterStyle;
        Cell cell = row.createCell(cellIndex, CellType.NUMERIC);
        cell.setCellStyle(rowStyle);
        cell.setCellValue(h.getDona());
    }
    private void dataNarhCell(int hisobId, HisobKitob h, int rowIndex, int cellIndex) {
        CellStyle rowStyle = h.getHisob1().equals(hisobId) ? chiqimCenterStyle : kirimCenterStyle;
        Cell cell = row.createCell(cellIndex, CellType.NUMERIC);
        cell.setCellStyle(rowStyle);
        cell.setCellValue(doubleRound(h.getNarh()));
    }
    private void dataSummaColCell(int hisobId, HisobKitob h, int rowIndex, int cellIndex) {
        CellStyle rowStyle = h.getHisob1().equals(hisobId) ? chiqimCenterStyle : kirimCenterStyle;
        Cell cell = row.createCell(cellIndex, CellType.NUMERIC);
        cell.setCellStyle(rowStyle);
        cell.setCellValue(doubleRound(h.getSummaCol()));
    }
    private void dataBalanceCell(int hisobId, HisobKitob h, int rowIndex, int cellIndex) {
        CellStyle rowStyle = h.getHisob1().equals(hisobId) ? chiqimCenterStyle : kirimCenterStyle;
        Cell cell = row.createCell(cellIndex, CellType.NUMERIC);
        cell.setCellStyle(rowStyle);
        cell.setCellValue(doubleRound(h.getBalans()));
    }

    private double doubleRound(double d) {
        double d1 = d * 100;
        double d2 = Math.round(d1);
        double d3 = d2/100;
        return d3;
    }

    public void savdoChiptasi(QaydnomaData qaydnomaData, Connection connection, User user) {
        Double jamiMablag = 0.0;
        Double kassagaDouble = 0.0;
        Double chegirmaDouble = 0.0;
        Double naqdDouble = 0.0;
        Double plasticDouble = 0.0;
        Double balansDouble = 0.0;
        Double qaytimDouble = 0.0;
        HisobKitobModels hisobKitobModels = new HisobKitobModels();
        ObservableList<HisobKitob> hisobKitobObservableList = hisobKitobModels.getAnyData(
                connection, "qaydId = " + qaydnomaData.getId()/* + " AND " + "hujjatId = " + qaydnomaData.getHujjat()*/, "");
        sheet = wb.createSheet("Chipta");

        rowIndex = 0;
        row = sheet.createRow(rowIndex);
        cellIndex = 0;
        headerShirkatCell(rowIndex, cellIndex, sheet, qaydnomaData, user);

        rowIndex++;
        header1Row(rowIndex, sheet, qaydnomaData, user);

        rowIndex++;
        sarlavhaRow(rowIndex);

        for (HisobKitob hk: hisobKitobObservableList) {
            switch (hk.getAmal()) {
                case 4: //Tovar
                    if (hk.getHisob2().equals(qaydnomaData.getKirimId())) {
                        rowIndex++;
                        row = sheet.createRow(rowIndex);

                        cellIndex = 0;
                        dataChiptaTovarNomiCell(hk, rowIndex, cellIndex);

                        cellIndex ++;
                        dataChiptaTovarDonaCell(hk, rowIndex, cellIndex);

                        cellIndex ++;
                        dataChiptaTovarNarhCell(hk, rowIndex, cellIndex);

                        cellIndex ++;
                        dataChiptaTovarJamiCell(hk, rowIndex, cellIndex);
                        jamiMablag += hk.getNarh() * hk.getDona();
                    }
                    break;
                case 7: //To`lov naqd
                    naqdDouble = hk.getNarh();
                    break;
                case 8: //Qaytim
                    qaytimDouble = hk.getNarh();
                    break;
                case 13:    //Chegirma
                    chegirmaDouble = hk.getNarh();
                    break;
                case 15:    //To`lov plastic
                    plasticDouble = hk.getNarh();
                    break;
            }
        }

        kassagaDouble = jamiMablag - chegirmaDouble;
        balansDouble = (naqdDouble + plasticDouble) - kassagaDouble - qaytimDouble;


        if (jamiMablag > 0) {
            rowIndex++;
            jamiCell("Jami xarid", jamiMablag, rowIndex);
        }
/*
        if (chegirmaDouble > 0) {
            rowIndex++;
            jamiCell("Chegirma", chegirmaDouble, rowIndex);
        }

        if (kassagaDouble>0) {
            rowIndex++;
            jamiCell("Kassaga", kassagaDouble, rowIndex);
        }

        if (naqdDouble > 0) {
            rowIndex++;
            jamiCell("Naqd", naqdDouble, rowIndex);
        }
        if (plasticDouble > 0) {
            rowIndex++;
            jamiCell("Plastik", plasticDouble, rowIndex);
        }
        if (qaytimDouble > 0) {
            rowIndex++;
            jamiCell("Qaytim", qaytimDouble, rowIndex);
        }

        rowIndex++;
        jamiCell("Balans", balansDouble, rowIndex);
*/

        sheet.setColumnWidth(0, 50*256);
        sheet.setColumnWidth(1, 10*256);
        sheet.setColumnWidth(2, 10*256);
        sheet.setColumnWidth(3, 10*256);

        OutputStream fileOut = null;
        String fileName= pathString + "Chipta.xlsx";
        try {
            fileOut = new FileOutputStream(fileName);
            wb.write(fileOut);
            showFile(fileName);
        } catch (IOException e) {
            e.printStackTrace();
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
        CellRangeAddress region1 = new CellRangeAddress(rowIndex, rowIndex, 1,2);
        sheet.addMergedRegion(region1);
        setBordersToMergedCells(sheet, region1, BorderStyle.THIN);
        Cell cell3 = row.createCell(3, CellType.NUMERIC);
        cell3.setCellValue(qaydnomaData.getHujjat());
        cell3.setCellStyle(headerStyle);
    }

    private void dataChiptaTovarNomiCell(HisobKitob hisobKitob, int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue(hisobKitob.getIzoh());
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
    }

    private void dataChiptaTovarDonaCell(HisobKitob hisobKitob, int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue(hisobKitob.getDona());
        cell.setCellStyle(kirimCenterStyle);
    }

    private void dataChiptaTovarNarhCell(HisobKitob hisobKitob, int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue(hisobKitob.getNarh());
        cell.setCellStyle(kirimCenterStyle);
    }
    private void dataChiptaTovarJamiCell(HisobKitob hisobKitob, int rowIndex, int cellIndex) {
        Cell cell = row.createCell(cellIndex, CellType.STRING);
        cell.setCellValue(hisobKitob.getDona()*hisobKitob.getNarh());
        cell.setCellStyle(kirimCenterStyle);
    }

    private void jamiCell(String text, Double summa, int rowIndex) {
        CellStyle leftStyle = wb.createCellStyle();
        CellStyle centerStyle = wb.createCellStyle();
        setBorderStyle(leftStyle, BorderStyle.THIN, IndexedColors.WHITE.getIndex(),
                FillPatternType.SOLID_FOREGROUND, chiptaFont, VerticalAlignment.CENTER, HorizontalAlignment.LEFT);
        setBorderStyle(centerStyle, BorderStyle.THIN, IndexedColors.WHITE.getIndex(),
                FillPatternType.SOLID_FOREGROUND, chiptaFont, VerticalAlignment.CENTER, HorizontalAlignment.CENTER);
        row = sheet.createRow(rowIndex);


        Cell cell = row.createCell(0, CellType.STRING);
        cell.setCellStyle(leftStyle);
        cell.setCellValue(text);

        Cell cell2 = row.createCell(1, CellType.NUMERIC);
        cell2.setCellStyle(centerStyle);
        cell2.setCellValue(summa);
        CellRangeAddress region1 = new CellRangeAddress(rowIndex, rowIndex, 1,3);
        sheet.addMergedRegion(region1);
        setBordersToMergedCells(sheet, region1, BorderStyle.THIN);
    }
    protected void setBordersToMergedCells(Sheet sheet, CellRangeAddress rangeAddress, BorderStyle borderStyle) {
        RegionUtil.setBorderTop(borderStyle, rangeAddress, sheet);
        RegionUtil.setBorderLeft(borderStyle, rangeAddress, sheet);
        RegionUtil.setBorderRight(borderStyle, rangeAddress, sheet);
        RegionUtil.setBorderBottom(borderStyle, rangeAddress, sheet);
    }

    public void priceList(ObservableList<Standart6> observableList, User user) {
        sheet = wb.createSheet("Price");
        rowIndex = 0;
        row = sheet.createRow(rowIndex);

        cellIndex = 0;
        headerRow1(rowIndex, cellIndex, user);

        rowIndex++;
        headerRow2(rowIndex, cellIndex);

        for (Standart6 s6: observableList) {
            rowIndex++;
            priceDataRow(rowIndex, s6);
        }

        sheet.setColumnWidth(0, 50*256);
        sheet.setColumnWidth(1, 10*256);

        OutputStream fileOut = null;
        String fileName= pathString + "Price.xlsx";
        try {
            fileOut = new FileOutputStream(fileName);
            wb.write(fileOut);
            showFile(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void headerRow1(int rowIndex, int cellIndex, User user) {
        row = sheet.createRow(rowIndex);
        CellStyle cellStyle = wb.createCellStyle();
        setBorderStyle(cellStyle, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT.getIndex(),
                FillPatternType.SOLID_FOREGROUND, headerFont, VerticalAlignment.CENTER, HorizontalAlignment.CENTER);
        Cell cell = row.createCell(0, CellType.STRING);
        String shirkatNomi = GetDbData.getHisob(user.getTovarHisobi()).getText();
        cell.setCellValue(shirkatNomi);
        cell.setCellStyle(cellStyle);
        SimpleDateFormat sanaFormat = new SimpleDateFormat("dd.MM.yyyy  HH:mm:ss");
        Date sana = new Date();
        CellStyle wrapCellStyle = wb.createCellStyle();
        setBorderStyle(wrapCellStyle, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT.getIndex(), FillPatternType.SOLID_FOREGROUND,
                headerFont, VerticalAlignment.CENTER, HorizontalAlignment.CENTER);
        wrapCellStyle.setWrapText(true);
        Cell cell2 = row.createCell(1, CellType.STRING);
        cell2.setCellValue(sanaFormat.format(sana));
        cell2.setCellStyle(wrapCellStyle);
    }

    private void headerRow2(int rowIndex, int cellIndex) {
        row = sheet.createRow(rowIndex);
        CellStyle cellStyle = wb.createCellStyle();
        setBorderStyle(cellStyle, BorderStyle.THIN, IndexedColors.WHITE.getIndex(),
                FillPatternType.SOLID_FOREGROUND, kirimFont, VerticalAlignment.CENTER, HorizontalAlignment.CENTER);
        Cell cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("Tovar");
        cell.setCellStyle(cellStyle);
        CellStyle wrapCellStyle = wb.createCellStyle();
        setBorderStyle(wrapCellStyle, BorderStyle.THIN, IndexedColors.WHITE.getIndex(), FillPatternType.SOLID_FOREGROUND,
                kirimFont, VerticalAlignment.CENTER, HorizontalAlignment.CENTER);
        wrapCellStyle.setWrapText(true);
        Cell cell2 = row.createCell(1, CellType.STRING);
        cell2.setCellValue("Narh");
        cell2.setCellStyle(wrapCellStyle);
    }

    private void priceDataRow(int rowIndex, Standart6 s6) {
        row = sheet.createRow(rowIndex);
        CellStyle cellStyle = wb.createCellStyle();
        setBorderStyle(cellStyle, BorderStyle.THIN, IndexedColors.WHITE.getIndex(),
                FillPatternType.SOLID_FOREGROUND, kirimFont, VerticalAlignment.CENTER, HorizontalAlignment.LEFT);
        Cell cell = row.createCell(0, CellType.STRING);
        cell.setCellValue(s6.getText());
        cell.setCellStyle(cellStyle);
        CellStyle wrapCellStyle = wb.createCellStyle();
        setBorderStyle(wrapCellStyle, BorderStyle.THIN, IndexedColors.WHITE.getIndex(), FillPatternType.SOLID_FOREGROUND,
                kirimFont, VerticalAlignment.CENTER, HorizontalAlignment.CENTER);
        wrapCellStyle.setWrapText(true);
        Cell cell2 = row.createCell(1, CellType.NUMERIC);
        cell2.setCellValue(s6.getNarh());
        cell2.setCellStyle(wrapCellStyle);
    }
}
