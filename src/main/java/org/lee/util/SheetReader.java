package org.lee.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

public class SheetReader {

    private Sheet sheet;

    private SheetConfig sheetConfig;

    public SheetReader(Sheet sheet, SheetConfig sheetConfig){
        this.sheet = sheet;
        this.sheetConfig = sheetConfig;
    }



    public Map<Integer, Map<String, Object>> read() throws DataFormatException {
        Map<Integer, RowConfig> rowConfigMap = sheetConfig.getRowConfigs()
                .stream()
                .filter(rowConfig -> Objects.nonNull(rowConfig.getRowNum()))
                .collect(Collectors.toMap(RowConfig::getRowNum, rowConfig -> rowConfig));

        RowConfig mainConfig = sheetConfig.getRowConfigs()
                .stream().filter(rowConfig -> Objects.isNull(rowConfig.getRowNum()))
                .findFirst()
                .get();
        Map<Integer, Map<String, Object>> rows = new HashMap<>();
        for (int index = 0; true; index++){
            RowConfig rowConfig = rowConfigMap.get(index);
            if (Objects.nonNull(rowConfig)){
                if (rowConfig.isIgnore()){
                    continue;
                }
            } else {
                rowConfig = mainConfig;
            }
            if (empty(sheet.getRow(index), rowConfig)){
                break;
            }
            rows.put(index, read(sheet.getRow(index), rowConfig));
        }
        return rows;
    }



    public Map<String, Object> read(Row row, RowConfig rowConfig) throws DataFormatException {
        List<CellConfig> cellConfigs =
                rowConfig.getCellConfigs().stream().sorted(Comparator.comparing(CellConfig::getIndex)).collect(Collectors.toList());
        List<HashMap.Entry<String, Object>> entries = new ArrayList<>();
        for (CellConfig cellConfig : cellConfigs){
            entries.add(read(row.getCell(cellConfig.getIndex()), cellConfig));
        }

        return entries.stream()
                .filter(entry -> Objects.nonNull(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private HashMap.Entry<String, Object> read(Cell cell, CellConfig cellConfig) throws DataFormatException {

        if (Objects.isNull(cell)){
            return new HashMap.SimpleImmutableEntry<String, Object>(cellConfig.getKey(), null);
        }
        if (Objects.equals(CellConfig.TYPES.STR.getValue(), cellConfig.getType())){
            if (!cell.getCellType().equals(CellType.STRING)){
                throw new DataFormatException(String.format("%d 行  %d列 格式错误, 应当为字符串",
                        cell.getRowIndex() + 1, cell.getColumnIndex() + 1));
            }
            return new HashMap.SimpleImmutableEntry<String, Object>(cellConfig.getKey(),
                    cell.getStringCellValue());
        } else if (Objects.equals(CellConfig.TYPES.NUM.getValue(), cellConfig.getType())){

            if (!Objects.equals(cell.getCellType(), CellType.NUMERIC)){
                throw new DataFormatException(String.format("%d 行  %d列 格式错误, 应当为整数",
                        cell.getRowIndex() + 1, cell.getColumnIndex() + 1));
            }
            double numValue = cell.getNumericCellValue();
            return new HashMap.SimpleImmutableEntry<String, Object>(cellConfig.getKey(), (int) numValue);
        } else {
            throw new DataFormatException(String.format("%d 行  %d列 格式错误, 请仔细核对",
                    cell.getRowIndex() + 1, cell.getColumnIndex() + 1));
        }
    }

    private boolean empty(Row row, RowConfig rowConfig){

        return Objects.isNull(row) || rowConfig.getCellConfigs().stream().allMatch(cellConfig -> empty(row.getCell(cellConfig.getIndex())));
    }


    private boolean empty(Cell cell){
        return cell.getCellType().equals(CellType._NONE)
                || cell.getCellType().equals(CellType.BLANK)
                || cell.getCellType().equals(CellType.ERROR);
    }
}
