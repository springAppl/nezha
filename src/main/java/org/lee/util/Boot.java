package org.lee.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;

public class Boot {
    public static void main(String[] args) throws IOException, DataFormatException {
        InputStream in = null;
        BookConfig bookConfig = new BookConfig(in);
        InputStream exIn = Boot.class.getClassLoader().getResourceAsStream("ex.xlsx");
        BookReader bookReader = new BookReader(exIn, bookConfig);
        bookReader.readIndex(1);

    }
}