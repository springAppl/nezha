package org.lee.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public abstract class CommandLineExcelReader extends AbstractExcelReader {
    private String message;

    public CommandLineExcelReader(String message) {
        this.message = message;
    }

    protected List<Map<String, String>> read() throws IOException,
            DataInvalidException {
        String newTable = ConsoleReader.readFileName(message);
        return read(newTable);
    }
}
