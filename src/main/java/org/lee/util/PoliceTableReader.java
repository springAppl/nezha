package org.lee.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PoliceTableReader {

    private ExcelReader excelReader;

    private ConsoleReader consoleReader;

    public List<Map<String, String>> readPoliceTable() throws IOException, DataInvalidException {
        String newTable = consoleReader.readFileName("输入公安表：");
        ExcelReader excelReader = new ExcelReader();
        return excelReader.readPoliceTable(newTable);
    }

    public Map<String, Map<String, String>> policeTable() throws IOException, DataInvalidException, DuplicateAdminAreaException {
        List<Map<String, String>> policeData = readPoliceTable();
        // 校验重复
        Map<String, Integer> alrm = policeData.stream().collect(Collectors.groupingBy(ele -> ele.get(PoliceTableCloumnName.ADMIN_AREA).trim(), Collectors.toList()))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, ele -> ele.getValue().size()));
        boolean flag = false;
        if (!alrm.entrySet().isEmpty()) {
            for (Map.Entry<String, Integer> ele : alrm.entrySet()) {
                if (ele.getValue() > 1) {
                    flag = true;
                    System.out.println(ele.getKey() + ": " + ele.getValue());
                }
            }
            if (flag) {
                throw new DuplicateAdminAreaException("");
            }
        }
        return policeData.stream().collect(Collectors.toMap(ele -> ele.get(PoliceTableCloumnName.ADMIN_AREA).trim(), ele -> ele));
    }
}
