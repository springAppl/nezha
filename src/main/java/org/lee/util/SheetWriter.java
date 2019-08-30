package org.lee.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.*;
import java.util.stream.Collectors;

public class SheetWriter {

    private Sheet sheet;

    private SheetConfig sheetConfig;

    public SheetWriter(Sheet sheet, SheetConfig sheetConfig) {
        this.sheet = sheet;
        this.sheetConfig = sheetConfig;
    }

    public void generate(Map<Integer, Map<Integer, String>> headerData,
                         List<Map<Integer, String>> values) {

        Map<Integer, List<CellData>> data = write(headerData, values);

        data.forEach((key, value) -> {
            Row row = sheet.createRow(key);
            value.forEach(clo -> {
                Cell cell = row.createCell(clo.getIndex());
                if (Objects.equals(CellType.STRING, clo.getCellType())) {
                    cell.setCellValue(clo.getValue());
                } else if (Objects.equals(CellType.NUMERIC, clo.getCellType())) {
                    cell.setCellValue(Integer.parseInt(clo.getValue()));
                } else {
                    throw new DataFormatxception("");
                }
            });
        });
    }

    private Map<Integer, List<CellData>> write(Map<Integer, Map<Integer, String>> headerData, List<Map<Integer,
            String>> values) {
        Map<Integer, List<CellData>> data = sheetConfig.getRowConfigs()
                .stream()
                .filter(ele -> Objects.nonNull(ele.getRowNum()))
                .filter(RowConfig::isIgnore)
                .map(ele -> {
                    int rowIndex = ele.getRowNum();

                    List<CellData> cellDatas =
                            ele.getCellConfigs().stream().map(cube -> {
                                CellData cellData = new CellData();
                                cellData.setIndex(cube.getIndex());
                                if (Objects.isNull(cube.getType())) {
                                    cellData.setCellType(CellType.STRING);
                                } else if (Objects.equals(cube.getType(),
                                        CellConfig.TYPES.NUM.getValue())) {
                                    cellData.setCellType(CellType.NUMERIC);
                                } else if (Objects.equals(cube.getType(),
                                        CellConfig.TYPES.STR.getValue())) {
                                    cellData.setCellType(CellType.STRING);
                                } else {
                                    throw new DataFormatxception("格式错误");
                                }
                                cellData.setValue(cube.getKey());
                                return cellData;
                            }).collect(Collectors.toList());
                    return new HashMap.SimpleImmutableEntry<>(rowIndex, cellDatas);
                }).collect(Collectors.toMap(AbstractMap.SimpleImmutableEntry::getKey, AbstractMap.SimpleImmutableEntry::getValue));

        List<RowConfig> rowConfigs = sheetConfig.getRowConfigs()
                .stream()
                .filter(ele -> Objects.nonNull(ele.getRowNum()))
                .filter(ele -> !ele.isIgnore())
                .collect(Collectors.toList());

        for (RowConfig rowConfig : rowConfigs){
            int rowNum = rowConfig.getRowNum();
            Map<Integer, String> cellValues = headerData.get(rowNum);
            List<CellData> cellDatas =
                    rowConfig.getCellConfigs().stream().map(cellConfig -> {
                CellData cellData = new CellData();
                if (Objects.isNull(cellConfig.getType())) {
                    cellData.setCellType(CellType.STRING);
                } else if (Objects.equals(cellConfig.getType(),
                        CellConfig.TYPES.NUM.getValue())) {
                    cellData.setCellType(CellType.NUMERIC);
                } else if (Objects.equals(cellConfig.getType(),
                        CellConfig.TYPES.STR.getValue())) {
                    cellData.setCellType(CellType.STRING);
                } else {
                    throw new DataFormatxception("格式错误");
                }
                cellData.setIndex(cellConfig.getIndex());
                cellData.setValue(cellValues.get(cellConfig.getIndex()));
                return cellData;
            }).collect(Collectors.toList());

            data.put(rowNum, cellDatas);
        }


        RowConfig rowConfig =
                sheetConfig.getRowConfigs().stream().filter(ele -> Objects.isNull(ele.getRowNum())).findAny().get();

        int startIndex = sheetConfig.getRowConfigs().size() - 1;

        for (Map<Integer, String> pin : values) {
            List<CellData> cellDatas =
                    rowConfig.getCellConfigs().stream().map(cellConfig -> {
                        CellData cellData = new CellData();
                        cellData.setIndex(cellConfig.getIndex());
                        if (Objects.equals(cellConfig.getType(),
                                CellConfig.TYPES.STR.getValue())) {
                            cellData.setCellType(CellType.STRING);
                        } else if (Objects.equals(cellConfig.getType(),
                                CellConfig.TYPES.NUM.getValue())) {
                            cellData.setCellType(CellType.NUMERIC);
                        } else {
                            throw new DataFormatxception("格式错误");
                        }
                        cellData.setValue(pin.get(cellConfig.getIndex()));
                        return cellData;
                    }).collect(Collectors.toList());

            data.put(startIndex++, cellDatas);
        }
        return data;
    }
}
