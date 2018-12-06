package org.lee.util;

import org.apache.poi.ss.usermodel.Row;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PoliceTableReader extends ExcelReader {

    private ConsoleReader consoleReader;

    public List<Map<String, String>> readPoliceTable() throws IOException, DataInvalidException {
        String newTable = consoleReader.readFileName("输入公安表：");
        return readBook(newTable);
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


    protected Map<String, String> read(Row row) throws DataInvalidException {
        Map<String, String> data = new HashMap<>();

        data.put(PoliceTableCloumnName.ADMIN_AREA, getStringCellValue(row, 0, PoliceTableCloumnName.ADMIN_AREA + "数据错误"));

        data.put(PoliceTableCloumnName.NAME, getStringCellValue(row, 1, PoliceTableCloumnName.NAME + "数据错误"));

        data.put(PoliceTableCloumnName.PHONE, getNullOrStringCellValue(row, 2, PoliceTableCloumnName.PHONE + "数据错误"));

        data.put(PoliceTableCloumnName.LON, getNullOrStringCellValue(row, 3, PoliceTableCloumnName.LON + "数据错误"));

        data.put(PoliceTableCloumnName.LAT, getNullOrStringCellValue(row, 4, PoliceTableCloumnName.LAT + "数据错误"));
        return data;
    }
}
