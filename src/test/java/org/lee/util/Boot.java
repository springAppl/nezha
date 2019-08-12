package org.lee.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.zip.DataFormatException;

public class Boot {
    public static void main(String[] args) throws IOException, DataFormatException {
        InputStream configIn = Boot.class.getClassLoader().getResourceAsStream("book.json");


        InputStream excelIn = Boot.class.getClassLoader().getResourceAsStream("output.xlsx");
        BookReader bookReader = new BookReader(excelIn, BookConfig.config(configIn));
        Map<Integer, Map<String, Object>> res = bookReader.readIndex(0);
        System.out.println(res);
    }
}