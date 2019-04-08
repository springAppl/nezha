package org.lee.util;
import java.io.IOException;

public class Boot {
    public static void main(String[] args) throws IOException{
        try {
            CommandLineConfigableExcelReader commandLineConfigableExcelReader =
                    new CommandLineConfigableExcelReader("请输入EXCEL:");
            commandLineConfigableExcelReader.read().forEach(System.out::println);
        } catch (DataInvalidException e) {
            System.out.println("亲：你这里出错了,改正就好了");
            System.out.println(e.getMessage());
        }
    }

}