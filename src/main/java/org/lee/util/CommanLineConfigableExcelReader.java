package org.lee.util;

import org.apache.poi.ss.usermodel.Row;

import java.util.Map;

public class CommanLineConfigableExcelReader extends CommandLineExcelReader {

    private ConfigableExcelReader configableExcelReader;

    public CommanLineConfigableExcelReader(String message) {
        super(message);

        this.configableExcelReader =
                new ConfigableExcelReader(ConsoleReader.readFileName("请输入配置文件:"));
    }

    @Override
    protected Map<String, String> read(Row row) throws DataInvalidException {
        return configableExcelReader.read(row);
    }
}
