package org.lee.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ExcelReader {
    List<Map<String, String>> read(String file) throws IOException, DataInvalidException;
}
