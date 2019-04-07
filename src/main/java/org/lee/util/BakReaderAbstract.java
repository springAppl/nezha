package org.lee.util;

import org.apache.poi.ss.usermodel.Row;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class BakReaderAbstract extends AbstractExcelReader {
    private ConsoleReader consoleReader;

    public Optional<ResultColl> compareBakTable(List<Map<String, String>> leftData) throws IOException, DataInvalidException {
        Boolean hasBak = consoleReader.readBoolean("是否有备份表（yes/no）:");
        if (hasBak) {
            String bakTable = consoleReader.readFileName("输入备份表：");
            return Optional.of(compareNewAndBakTable(leftData, bakTable));
        }
        return Optional.empty();
    }

    // 新表与旧表过滤
    public ResultColl compareOldTable() throws IOException, DataInvalidException {
        String newTable = consoleReader.readFileName("输入新表：");
        String oldTable = consoleReader.readFileName("输入旧表：");

        return compareNewAndOldTable(
                newTable,
                oldTable
        );
    }


    private ResultColl compareNewAndBakTable(List<Map<String, String>> leftData,
                                       String bakTable) throws IOException, DataInvalidException {
        List<Map<String, String>> bakData = read(bakTable);
        Map<String, Map<String, String>> bakMap = null;
        try {
            bakMap = toMap(bakData);
        } catch (DuplicateAdminAreaException e) {
            throw new DataInvalidException("备份表 " + e.getMessage());
        }
        return compare(leftData, bakMap);
    }

    private ResultColl compareNewAndOldTable(String newTable, String oldTable) throws IOException, DataInvalidException {
        List<Map<String, String>> newData;

        newData = read(newTable);

        List<Map<String, String>> oldData;

        oldData = read(oldTable);

        Map<String, Map<String, String>> oldDataMap;
        try {
            oldDataMap = toMap(oldData);
        } catch (DuplicateAdminAreaException e) {
            throw new DataInvalidException("旧表 " + e.getMessage());
        }
        return compare(newData, oldDataMap);
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


    private ResultColl compare(List<Map<String, String>> newData, Map<String,Map<String,
            String>> compareMap) {
        // [1,2,3],[4,5,6]
        List<Map<String, String>> resultData = newData.stream().filter(obj -> {
            Map<String, String> oldDataTemp = compareMap.get(obj.get(ExcelCloumnName.ADMIN_AREA) + obj.get(ExcelCloumnName.DEVICE_NAME));
            if (Objects.nonNull(oldDataTemp)) {
                // 覆盖值
                coverNewTableData(obj, oldDataTemp);
            }
            return Objects.nonNull(oldDataTemp);
        }).collect(Collectors.toList());

        // [7,8,9]
        List<Map<String, String>> leftData = newData.stream().filter(obj -> {
            Map<String, String> oldDataTemp = compareMap.get(obj.get(ExcelCloumnName.ADMIN_AREA) + obj.get(ExcelCloumnName.DEVICE_NAME));
            return Objects.isNull(oldDataTemp);
        }).collect(Collectors.toList());
        return new ResultColl(leftData, resultData);
    }

    private void coverNewTableData(Map<String, String> newTableData, Map<String, String> compareTableData){
        for (Map.Entry<Integer, String> ele : ExcelCloumnName.EXCEL_CLOUMN.entrySet()) {
            String value = ele.getValue();
            newTableData.replace(value, compareTableData.get(value));
        }
    }

    private Map<String, Map<String, String>> toMap(List<Map<String, String>> dataList) throws DuplicateAdminAreaException {
        Map<String, Map<String, String>> dataMap;
        dataMap = dataList.stream().collect(Collectors.toMap(data -> data.get(ExcelCloumnName.ADMIN_AREA) + data.get(ExcelCloumnName.DEVICE_NAME), data -> data, (oldValue, newValue) -> null));
        List<String> duplicatKeies = dataMap.entrySet().stream().filter(obj -> Objects.isNull(obj.getValue())).map(Map.Entry::getKey).collect(Collectors.toList());
        if (!duplicatKeies.isEmpty()) {
            throw new DuplicateAdminAreaException(" 行政区域设备名称重复：" + duplicatKeies);
        }
        return dataMap;
    }
}
