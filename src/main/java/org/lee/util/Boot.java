package org.lee.util;
import java.io.IOException;

public class Boot {
    public static void main(String[] args) throws IOException, DataInvalidException, DuplicateAdminAreaException {
        CommanLineConfigableExcelReader commanLineConfigableExcelReader =
                new CommanLineConfigableExcelReader("请输入EXCEL:");
        commanLineConfigableExcelReader.read().forEach(System.out::println);
    }

}