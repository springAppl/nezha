package org.lee.util;

import org.apache.poi.ss.usermodel.Row;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class LastResultReader extends ExcelReader {
    private ConsoleReader consoleReader;

    public Optional<List<Map<String, String>>> compareTable(List<Map<String, String>> leftData) throws IOException, DataInvalidException {
        Boolean hasBak = consoleReader.readBoolean("是否需要比对增量（yes/no）:");
        if (hasBak) {
            String bakTable = consoleReader.readFileName("输入要比对的表：");
            return Optional.of(compareIncrement(leftData, bakTable));
        }
        return Optional.empty();
    }


    private List<Map<String, String>> compareIncrement(List<Map<String, String>> leftData, String lastResultTable) throws IOException, DataInvalidException {
        List<Map<String, String>> bakData = readBook(lastResultTable);
        Map<String, Map<String, String>> bakMap = null;
        try {
            bakMap = toMap(bakData);
        } catch (DuplicateAdminAreaException e) {
            throw new DataInvalidException("比对表 " + e.getMessage());
        }
        return increment(leftData, bakMap);
    }

    public Map<String, Map<String, String>> toMap(List<Map<String, String>> dataList) throws DuplicateAdminAreaException {
        Map<String, Map<String, String>> dataMap;
        dataMap = dataList.stream().collect(Collectors.toMap(data -> data.get(ExcelCloumnName.DEVICE_CODE) + data.get(ExcelCloumnName.DEVICE_NAME), data -> data, (oldValue, newValue) -> null));
        List<String> duplicatKeies = dataMap.entrySet().stream().filter(obj -> Objects.isNull(obj.getValue())).map(Map.Entry::getKey).collect(Collectors.toList());
        if (!duplicatKeies.isEmpty()) {
            throw new DuplicateAdminAreaException("设备编码行政区域重复：" + duplicatKeies);
        }
        return dataMap;
    }

    private List<Map<String, String>> increment(List<Map<String, String>> newData, Map<String,Map<String, String>> compareMap) {
        return newData.stream().filter(ele -> !compareMap.containsKey(ele.get(ExcelCloumnName.DEVICE_CODE) + ele.get(ExcelCloumnName.DEVICE_NAME))).collect(Collectors.toList());
    }

    protected Map<String, String> read(Row row) throws DataInvalidException {
        Map<String, String> data = new HashMap<>();

        data.put(ExcelCloumnName.DEVICE_CODE, getStringCellValue(row, 0, "设备编码数据错误"));

        data.put(ExcelCloumnName.DEVICE_NAME, getStringCellValue(row, 1, "设备名称数据错误"));

        data.put(ExcelCloumnName.ADMIN_AREA, getStringCellValue(row, 3, "行政区域数据错误"));

        if (data.get(ExcelCloumnName.ADMIN_AREA).length() < 6) {
            throw new DataInvalidException(String.format("第%d行，第%d列, 行政区域数据错误", row.getRowNum() + 1, 4));
        }

        for (Map.Entry<Integer, String> ele : ExcelCloumnName.EXCEL_CLOUMN.entrySet()){
            Integer key = ele.getKey();
            String value = ele.getValue();
            data.put(value, getNullOrStringCellValue(row, key, value + "数据错误"));
        }
        data.put(ExcelCloumnName.DEVICE_LONGITUDE, getNullOrNumStringCellValue(row, 6, "经度数据错误"));

        data.put(ExcelCloumnName.DEVICE_LATITUDE, getNullOrNumStringCellValue(row, 7, "纬度数据错误"));
        return data;
    }
}
