package org.lee.util;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DataFormatException;

public class Boot {
    public static void main(String[] args) throws IOException, DataFormatException {
        InputStream configIn = Boot.class.getClassLoader().getResourceAsStream("org/lee/util/book.json");


        InputStream excelIn = Boot.class.getClassLoader().getResourceAsStream("output.xlsx");
        BookReader bookReader = new BookReader(excelIn, BookConfig.config(configIn));
        Map<Integer, Map<String, Object>> res = bookReader.readIndex(0);
        System.out.println(res);
    }

    @Test
    public void generate() throws IOException {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        Map<Integer, String> data = new HashMap<>();
        data.put(0, "腰果");
        data.put(1, "10");
        data.put(2, "5");
        data.put(3, "3");

        Map<Integer, Map<Integer, String>> headerData = new HashMap<>();
        Map<Integer, String> headerCellData = new HashMap<>();
        headerCellData.put(0, "1");
        headerCellData.put(1, "刘姐店");
        headerData.put(1, headerCellData);


        BookConfig bookConfig = BookConfig.config(this.getClass().getClassLoader().getResourceAsStream(
                "book.json"));

        SheetWriter sheetWriter = new SheetWriter(sheet, bookConfig.getIndex(0));



        sheetWriter.generate(headerData, Collections.singletonList(data));

        try(OutputStream outputStream = new FileOutputStream("/Users/shaofeiliu/IdeaProjects/nezha/src/test/java/org/lee/util/ex.xlsx")){
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}