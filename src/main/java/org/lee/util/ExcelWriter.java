package org.lee.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class ExcelWriter {

    private static final Integer INCREMENT = 10000;


    public void writeResult(String book, List<Map<String, String>> data){
        writeBook(book, data, ExcelCloumnName.FULL_EXCEL_CLOUMN);
    }

    public void writeBook(String book, List<Map<String, String>> data, Map<Integer, String> tableHead){
        Workbook workbook = new HSSFWorkbook();
        for (int toIndex = INCREMENT;true;toIndex = toIndex + INCREMENT) {
            if (toIndex > data.size()) {
                writeSheet(workbook, data.subList(toIndex - INCREMENT, data.size()), tableHead);
                break;
            } else {
                writeSheet(workbook, data.subList(toIndex - INCREMENT, toIndex), tableHead);
            }
        }
        try (OutputStream fileOut = new FileOutputStream(book + ".xls")) {
            workbook.write(fileOut);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeSheet(Workbook workbook, List<Map<String, String>> data, Map<Integer, String> tableHead ){
        Sheet sheet = workbook.createSheet();
        writeTableHead(sheet, tableHead);
        for (int index = 0; index < data.size(); index++) {
            writeRow(index + 1, sheet, data.get(index));
        }
    }

    public void writeRow(int index, Sheet sheet, Map<String, String> data){
        Row row = sheet.createRow(index);
        ExcelCloumnName.FULL_EXCEL_CLOUMN.forEach((key, value) -> {
            int cellIndex = key;
            writeStringCell(row, cellIndex, data.get(value));
        });
    }

    public void writeTableHead(Sheet sheet, Map<Integer, String> tableHead) {
        Row row = sheet.createRow(0);
        tableHead.forEach((key, value) -> {
            int cellIndex = key;
            writeStringCell(row, cellIndex, value);
        });
    }

    public void writeStringCell(Row row, int index, String value) {
        Cell cell = row.createCell(index, CellType.STRING);
        cell.setCellValue(value);
    }
}
