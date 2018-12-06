package org.lee.util;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class ExcelReader {

    public Map<String, Map<String, String>> toMap(List<Map<String, String>> dataList) throws DuplicateAdminAreaException {
        Map<String, Map<String, String>> dataMap;
        dataMap = dataList.stream().collect(Collectors.toMap(data -> data.get(ExcelCloumnName.ADMIN_AREA) + data.get(ExcelCloumnName.DEVICE_NAME), data -> data, (oldValue, newValue) -> null));
        List<String> duplicatKeies = dataMap.entrySet().stream().filter(obj -> Objects.isNull(obj.getValue())).map(Map.Entry::getKey).collect(Collectors.toList());
        if (!duplicatKeies.isEmpty()) {
            throw new DuplicateAdminAreaException(" 行政区域设备名称重复：" + duplicatKeies);
        }
        return dataMap;
    }

    public ResultColl compare(List<Map<String, String>> newData, Map<String,Map<String, String>> compareMap) {
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

    protected List<Map<String, String>> readBook(String file) throws IOException, DataInvalidException {
        Workbook wb = WorkbookFactory.create(new File(file));
        List<Map<String, String>> data = new ArrayList<>();
        for (int index = 0, len = wb.getNumberOfSheets(); index < len; index++) {
            List<Map<String, String>> temp;
            try {
                temp = readSheet(wb.getSheetAt(index));
            } catch (DataInvalidException e) {
                String message = e.getMessage();
                message = file + "  " + message;
                throw new DataInvalidException(message);
            }
            data.addAll(temp);
        }
        return data;
    }

    public List<Map<String, String>> readSheet(Sheet sheet) throws IOException, DataInvalidException {
        List<Map<String, String>> data = new ArrayList<>();
        for (int index = 0, len = sheet.getLastRowNum(); index <= len; index++) {
            Row row = sheet.getRow(index);
            Map<String, String> temp;
            try {
                if (isBlank(row)) {
                    break;
                }
                temp = read(row);
            } catch (DataInvalidException e) {
                String message = e.getMessage();
                message = sheet.getSheetName() + "    第" + (index + 1) + "行    " + message;
                throw new DataInvalidException(message);
            }
            data.add(temp);
        }
        return data;
    }


    private boolean isBlank(Row row){
        if (Objects.isNull(row)) {
            return true;
        }
        Cell deviceCode = row.getCell(0);
        if (Objects.nonNull(deviceCode) &&  !Objects.equals(deviceCode.getCellType(), CellType.BLANK)) {
            return false;
        }
        Cell deviceName = row.getCell(1);
        if (Objects.nonNull(deviceName) && !Objects.equals(deviceName.getCellType(), CellType.BLANK)) {
            return false;
        }
        Cell adminArea = row.getCell(3);
        if (Objects.nonNull(adminArea) && !Objects.equals(adminArea.getCellType(), CellType.BLANK)) {
            return false;
        }
        return true;
    }


    public String getStringCellValue(Row row, int cloumn, String errorMessage) throws DataInvalidException {
        Cell cell = row.getCell(cloumn);
        try {
            if (!isStringCell(cell)) {
                throw new DataInvalidException(String.format("第%d列 ", cloumn + 1) + errorMessage);
            }
        } catch (DataFormatException e) {
            throw new DataInvalidException(String.format("第%d列 ", cloumn + 1) + e.getMessage());
        }
        return cell.getStringCellValue().trim();
    }

    public String getNullOrStringCellValue(Row row, int cloumn, String errorMessage) throws DataInvalidException {
        Cell cell = row.getCell(cloumn);

        if (Objects.isNull(cell) || Objects.equals(cell.getCellType(), CellType.BLANK)) {
            return null;
        }

        if (Objects.equals(cell.getCellType(), CellType.STRING)) {
            return cell.getStringCellValue().trim();
        }
        throw new DataInvalidException(String.format("第%d列 ", cloumn + 1) + errorMessage);
    }

    public boolean isStringCell(Cell cell) throws DataFormatException {
        if (Objects.isNull(cell)){
            return false;
        }
        if (!Objects.equals(cell.getCellType(), CellType.STRING)) {
            throw new DataFormatException("数据类型错误，非文本，读取异常, 请将数据类型改为文本，并检查其他");
        }
        String value = cell.getStringCellValue();
        if (Objects.isNull(value)) {
            return false;
        }
        value = value.trim();
        if (value.length() == 0) {
            return false;
        }
        return true;
    }

    abstract protected Map<String, String> read(Row row) throws DataInvalidException;
}
