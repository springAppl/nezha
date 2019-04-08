package org.lee.util;

import org.apache.poi.ss.usermodel.Row;

import java.util.Map;

public class CommandLineConfigableExcelReader extends CommandLineExcelReader {

    private ConfigableExcelReader configableExcelReader;

    public CommandLineConfigableExcelReader(String message) {
        this(message, "请输入配置文件:");
    }

    public CommandLineConfigableExcelReader(String message, String configMessage) {
        super(message);

        this.configableExcelReader =
                new ConfigableExcelReader(ConsoleReader.readFileName(configMessage));
    }

    @Override
    protected Map<String, String> read(Row row) throws DataInvalidException {
        return configableExcelReader.read(row);
    }
}
