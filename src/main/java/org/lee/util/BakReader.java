package org.lee.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BakReader {
    private ConsoleReader consoleReader;

    public Optional<ResultColl> compareBakTable(List<Map<String, String>> leftData) throws IOException, DataInvalidException {
        Boolean hasBak = consoleReader.readBoolean("是否有备份表（yes/no）:");
        if (hasBak) {
            ExcelReader excelReader = new ExcelReader();
            String bakTable = consoleReader.readFileName("输入备份表：");
            return Optional.of(excelReader.compareNewAndBakTable(leftData, bakTable));
        }
        return Optional.empty();
    }

    // 新表与旧表过滤
    public ResultColl compareOldTable() throws IOException, DataInvalidException {
        String newTable = consoleReader.readFileName("输入新表：");
        String oldTable = consoleReader.readFileName("输入旧表：");

        ExcelReader excelReader = new ExcelReader();
        return excelReader.compareNewAndOldTable(
                newTable,
                oldTable
        );
    }

}
