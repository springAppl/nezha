package org.lee.util;

import java.io.File;
import java.util.Objects;
import java.util.Scanner;

public class ConsoleReader {

    public static String readFileName(String message){
        Scanner scan = new Scanner(System.in);
        String tableName;
        while (true) {
            System.out.println(message);
            if (scan.hasNext()) {
                tableName = scan.next();
                File file = new File(tableName);
                if (file.exists()) {
                    return tableName;
                }
            }
        }
    }

    public static Boolean readBoolean(String message){
        Scanner scan = new Scanner(System.in);
        while (true) {
            System.out.println(message);
            String judgement = null;
            if(scan.hasNext()) {
                judgement = scan.next();
            }
            if (Objects.equals("yes", judgement)) {
                return true;
            }
            if (Objects.equals("no", judgement)) {
                return false;
            }
        }
    }
}
