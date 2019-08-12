package org.lee.util;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.zip.DataFormatException;

public class BookReader {

    private InputStream inputStream;

    private BookConfig bookConfig;

    public BookReader(InputStream inputStream, BookConfig bookConfig){
        this.inputStream = inputStream;
        this.bookConfig = bookConfig;
    }

    public Map<Integer, Map<String, Object>> readIndex(int index) throws IOException,
            DataFormatException {
        Workbook wb = new XSSFWorkbook(inputStream);
        Sheet sheet = wb.getSheetAt(index);
        SheetReader sheetReader = new SheetReader(sheet, bookConfig.getIndex(index));
        return sheetReader.read();
    }
}
