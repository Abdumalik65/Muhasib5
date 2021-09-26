package sample.Report;

import javafx.collections.ObservableList;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.xssf.usermodel.*;
import sample.Data.Standart;

public class TovarReport {
    public XSSFWorkbook generateReport(ObservableList<Standart> tovarList, boolean isRes, boolean isRes1) {


        XSSFWorkbook workbook = null;
        XSSFSheet sheet = null;
        XSSFRow row = null;
        XSSFCell cell = null;
        String[] headers = null;
        int rowNum = 0;
        int colNum = 0;
        CellStyle cellStyle = null;
        CellStyle headerStyle = null;
        XSSFFont font = null;
        CellStyle datecellStyle = null;
        /* set the weight of the font */
        try {
            workbook = new XSSFWorkbook();

            headers = new String[]{
                    "Id", "Tovar nomi"
            };
            row = sheet.createRow(rowNum);
            font = workbook.createFont();
            font.setBold(true);

            headerStyle = workbook.createCellStyle();
            headerStyle.setBorderBottom(BorderStyle.MEDIUM);
            headerStyle.setBorderTop(BorderStyle.MEDIUM);
            headerStyle.setBorderRight(BorderStyle.MEDIUM);
            headerStyle.setBorderLeft(BorderStyle.MEDIUM);
            headerStyle.setFillForegroundColor((short) 200);
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setFont(font);

            cellStyle = workbook.createCellStyle();
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);



            datecellStyle = workbook.createCellStyle();
            datecellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("dd-MMM-yyyy"));
            datecellStyle.setBorderBottom(BorderStyle.THIN);
            datecellStyle.setBorderTop(BorderStyle.THIN);
            datecellStyle.setBorderRight(BorderStyle.THIN);
            datecellStyle.setBorderLeft(BorderStyle.THIN);



            /**
             * Writing Headers
             */
            for (String header : headers) {
                cell = row.createCell(colNum);
                cell.setCellValue(header);
                cell.setCellStyle(headerStyle);
                ++colNum;
            }

            /**
             * Writing Other Rows
             */
            for (Standart t : tovarList) {
                ++rowNum;
                colNum = 0;
                row = sheet.createRow(rowNum);
                cell = row.createCell(colNum);
                //sheet.setColumnWidth(0, 4000);
                cell.setCellValue(t.getId());

                cell.setCellStyle(cellStyle);

                ++colNum;
                cell = row.createCell(colNum);
                cell.setCellValue(t.getText());
                cell.setCellStyle(cellStyle);

            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            sheet.createFreezePane(1, 1);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return workbook;
    }
}

